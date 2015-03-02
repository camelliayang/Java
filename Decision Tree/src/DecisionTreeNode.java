/* 		Team Think Tank
 * 		Decision Tree(C4.5)
 * 
 * Class: 	DecisionTreeNode
 * Date: 	2014.04.05
 */

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class DecisionTreeNode {
	public static AttributeContainer attr = null;
	
	// Define the termination condition:
	// If the size of current data set is less than TERMINATION_SIZE, stop splitting.
	public static final int TERMINATION_SIZE = 4;
	
	// If the size of current data set is less than TOOSMALL_SIZE, stop splitting and use the major result of parent node.
	public static final int TOOSMALL_SIZE = 0;
	
	// If the info gain ratio of current node is less than PRE_PRUNE, stop splitting.
	static final double PRE_PRUNE = 0.2;
	
	// On which category will the current node branch on.
	int attribute_number = -1;
	
	// Pivot of splitting real attribute.
	double pivot = 0.0;
	
	// The info gain ratio of current node in DT building process.
	double info = 0.0;
	
	// The number of data nodes under the node in DT building process.
	int size = 0;
	
	// Sub-trees of the current node.
	ArrayList<DecisionTreeNode> subTrees;
	
	// Whether the node is a termination node (Leaf node)
	boolean terminate = false;
	
	// The label the node belongs to if the node is a termination node.
	int result = -1;
	
	DecisionTreeNode() {
		subTrees = new ArrayList<DecisionTreeNode>();
	}
	
	// Initialize the DecisionTreeNode class with given AttributeContainer.
	public static void initialize(AttributeContainer _attr) {
		attr = _attr;
	}
	
	// Print the decision tree with root in current node, with @level tabs before the root node.
	public void printTree(int level) {
		StringBuilder pad = new StringBuilder();
		for (int i = 0; i < level; i++)
			pad.append("\t");
		String pads = pad.toString();
		
		// Print the current node.
		if (terminate) {
			System.out.println(pads + "== [TERMINATE NODE] Label: " + attr.result.categories.get(result) + ", size:" + size);
		} else {
			DecimalFormat snf = new DecimalFormat("#.####");
			if (attr.getAttributeType(attribute_number) == Attribute.ATTR_CATEGORY) {
				System.out.println(pads + "== [BRANCH ON " + attr.getAttributeName(attribute_number) + "], info: " + snf.format(info) + ", size: " + size);
				
				// Print subtrees recursively.
				for (int i = 0; i < attr.getAttributeSize(attribute_number); i++) {
					System.out.println(pads + "== " + attr.getAttributeName(attribute_number) + " = " + attr.getAttributeValue(attribute_number, i));
					subTrees.get(i).printTree(level + 1);
				}
			} else {
				System.out.println(pads + "[BRANCH ON " + attr.getAttributeName(attribute_number) + "], info: " + snf.format(info) + ", size: " + size);
				
				// Print subtrees recursively.
				System.out.println(pads + "== " + attr.getAttributeName(attribute_number) + " < " + pivot);
				subTrees.get(0).printTree(level + 1);
				System.out.println(pads + "== " + attr.getAttributeName(attribute_number) + " >= " + pivot);
				subTrees.get(1).printTree(level + 1);
			}
		}
	}
	
	// Simple post-prune process on decision tree with root in current node.
	// If all sub-nodes are termination nodes and all sub-nodes have same labels, combine them into a single node.
	public void simplePrune() {
		if (terminate)
			return;
		
		for (DecisionTreeNode sub : subTrees)
			sub.simplePrune();
		
		boolean allEqual = true;
		for (int i = 1; i < subTrees.size(); i++) {
			if (!subTrees.get(i).terminate || !subTrees.get(i - 1).terminate) {
				allEqual = false;
				break;
			}
			if (subTrees.get(i).result != subTrees.get(i - 1).result) {
				allEqual = false;
				break;
			}
		}
		
		if (allEqual) {
			terminate = true;
			result = subTrees.get(0).result;
			subTrees = new ArrayList<DecisionTreeNode>();
		}
	}
	
	// Build a decision tree based on current data set @data and remaining attributes @attrList
	public static DecisionTreeNode buildTree(List<TrainingDataContainer> data, ArrayList<Attribute> attrList) {
		DecisionTreeNode ret = new DecisionTreeNode();
		ret.size = data.size();
		
		// If termination condition is met, terminate splitting
		if (data.size() <= TOOSMALL_SIZE) {
			ret.result = -1;
			ret.terminate = true;
			return ret;
		} else if (data.size() <= TERMINATION_SIZE || attrList.isEmpty() || checkAllEqual(data)) {
			ret.terminate = true;
			ret.result = findMajority(data);
			return ret;
		}
		
		// Find the attribute with maximum info gain ratio.
		Attribute.CalculateResult maxr = null;
		int max_cate = -1;
		
		double curinfo = calculateSetInfo(data);
		
		for (Attribute a : attrList) {
			Attribute.CalculateResult r = a.calculateInfo(data, curinfo);
			if (maxr == null || r.info > maxr.info) {
				maxr = r;
				max_cate = attr.getAttributeIndex(a.attr_name);
			}
		}
		
		// Generate subtrees with the attribute.
		int type = attr.getAttributeType(max_cate);		
		Attribute divideAttr = attr.getAttribute(max_cate);
		ArrayList<ArrayList<TrainingDataContainer>> parts = null;
		ret.attribute_number = max_cate;
		ret.pivot = maxr.pivot;
		ret.info = maxr.info;
		
		int majority = findMajority(data);
		
		// If the maximum info gain is less than PRE_PRUNE, also stop splitting.
		if (maxr.info < PRE_PRUNE) {
			ret.terminate = true;
			ret.result = majority;
			return ret;
		}
		
		// Update the list of remaining attributes. 
		ArrayList<Attribute> remain = new ArrayList<Attribute>();
		for (Attribute a : attrList) {
			if (a.attr_type == Attribute.ATTR_REAL || !a.equals(divideAttr))
				remain.add(a);
		}
		
		// Divide the data set into subsets based on the attribute to split on, and build decision tree recursively
		// on these sets and attach the subtrees to the current node.
		if (type == Attribute.ATTR_CATEGORY) {
			CategoryAttribute cateAttr = (CategoryAttribute) divideAttr;
			parts = cateAttr.split(data);			
			for (ArrayList<TrainingDataContainer> part : parts) {
				DecisionTreeNode node = buildTree(part, remain);
				if (node.result == -1)
					node.result = majority;
				ret.subTrees.add(node);
			}
		} else {
			RealAttribute realAttr = (RealAttribute) divideAttr;
			parts = realAttr.split(data, maxr.pivot);
			for (ArrayList<TrainingDataContainer> part : parts) {
				DecisionTreeNode node = buildTree(part, remain);
				if (node.result == -1)
					node.result = majority;
				ret.subTrees.add(node);
			}
		}
		return ret;
	}
	
	// Calculate the split info on labels of given data set @data. The value is used in
	// C4.5 algorithm to calculate the info gain ratio.
	static double calculateSetInfo(List<TrainingDataContainer> data) {
		int[] count = new int[attr.result.num_of_categories];
		for (int i = 0; i < attr.result.num_of_categories; i++)
			count[i] = 0;
		
		for (TrainingDataContainer d : data)
			count[d.result]++;
		
		double info = 0.0;
		for (int i = 0; i < attr.result.num_of_categories; i++) {
			double p = count[i] * 1.0 / data.size();
			if (count[i] > 0)
				info -= p * Math.log(p);
		}
		
		return info;
	}
	
	// Check whether the labels of all data nodes in the data set @data are equal
	static boolean checkAllEqual(List<TrainingDataContainer> data) {
		if (data.size() <= 1)
			return true;
		int len = data.size();
		for (int i = 1; i < len; i++) {
			if (data.get(i).result != data.get(i - 1).result)
				return false;
		}
		return true;
	}
	
	// Find the label with maximum count in data set @data
	static int findMajority(List<TrainingDataContainer> data) {
		int[] count = new int[attr.result.num_of_categories];
		for (int i = 0; i < attr.result.num_of_categories; i++) {
			count[i] = 0;
		}
		
		for (TrainingDataContainer d : data)
			count[d.result]++;
		
		int max_cate = 0;
		for (int i = 0; i < attr.result.num_of_categories; i++) {
			if (count[i] > count[max_cate])
				max_cate = i;
		}

		return max_cate;
	}
	
	// Get the accuracy rate of applying TrainingDataSet @test to the current decision tree
	// in cross-validation process.
	public double testNodes(List<TrainingDataContainer> test) {
		int allCount = 0;
		int trueCount = 0;
		int falseCount = 0;
		
		for (TrainingDataContainer t : test) {
			boolean res = getResult(t);
			if (res)
				trueCount++;
			else {
				falseCount++;
			}
			allCount++;
		}	
		
		System.out.println(allCount + " cases tested. True: " + trueCount + ", False: " + falseCount + ". Accuracy: " + trueCount * 1.0 / allCount);
		return trueCount * 1.0 / allCount;
	}
	
	// Check whether the predicted result is consistent with the given result in training data node @test
	// in cross-validation process.
	boolean getResult(TrainingDataContainer test) {
		if (terminate)
			return test.result == result;
		
		int type = attr.getAttributeType(attribute_number);
		if (type == Attribute.ATTR_CATEGORY) {
			int value = (Integer) test.data.get(attribute_number);
			return subTrees.get(value).getResult(test);
		} else {
			double value = (Double) test.data.get(attribute_number);
			if (value < pivot)
				return subTrees.get(0).getResult(test);
			else
				return subTrees.get(1).getResult(test);
		}
	}
	
	// Predict the labels of testing data set @test with the current decision tree.
	public void predictNodes(List<TestingDataContainer> test) {
		for (TestingDataContainer t : test) {
			t.result = predictResult(t);
			System.out.println(t);
		}
	}
	
	// Predict the label of testing data node @test with the current decision tree.
	int predictResult(TestingDataContainer test) {
		if (terminate)
			return result;
		
		int type = attr.getAttributeType(attribute_number);
		if (type == Attribute.ATTR_CATEGORY) {
			int value = (Integer) test.data.get(attribute_number);
			return subTrees.get(value).predictResult(test);
		} else {
			double value = (Double) test.data.get(attribute_number);
			if (value < pivot) {
				return subTrees.get(0).predictResult(test);
			} else {
				return subTrees.get(1).predictResult(test);
			}
		}
	}
}
