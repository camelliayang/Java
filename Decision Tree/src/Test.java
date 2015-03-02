/* 		Team Think Tank
 * 		Decision Tree(C4.5)
 * 
 * Class: 	Test(Main class)
 * Date: 	2014.04.05
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Test {
	public static ArrayList<Attribute> attrList = null;
	
	// Define the number of folds in cross validation.
	public static final int CROSS_PARTS = 8;
	
	// Define the input file name.
	public static final String templateName = "attributeinfo";
	public static final String fileName = "trainset";
	public static final String testName = "testset";

	// Main method. Load the attribute information, training data and testing data from files, build 
	// decision trees based on training data, calculate the accuracy based on cross validation process,
	// and give prediction on testing data.
	public static void main(String[] args) throws IOException {
		attrList = new ArrayList<Attribute>();
		CategoryAttribute result = null;
		
		// Read the attribute information from the attribute file.
		FileReader trd = new FileReader(templateName);
		BufferedReader treader = new BufferedReader(trd);
		
		String tline = null;
		while ((tline = treader.readLine()) != null) {
			String[] parts = tline.split(",");
			if (parts.length < 2) {
				treader.close();
				throw new IOException("Category file is not formatted correctly: " + tline);
			}
			
			String name = parts[0].trim();
			String type = parts[1].trim();
			
			if (type.equalsIgnoreCase("category")) {
				ArrayList<String> types = new ArrayList<String>();
				for (int i = 2; i < parts.length; i++)
					types.add(parts[i].trim());
				attrList.add(new CategoryAttribute(name, types));
			} else if (type.equalsIgnoreCase("real")) {
				attrList.add(new RealAttribute(name));
			} else if (type.equalsIgnoreCase("result")) {
				if (treader.readLine() != null) {
					treader.close();
					throw new IOException("Result should be on the last line");
				}
				ArrayList<String> types = new ArrayList<String>();
				for (int i = 2; i < parts.length; i++)
					types.add(parts[i].trim());
				result = new CategoryAttribute("label", types);
			} else {
				treader.close();
				throw new IOException("Attribute type incorrect: " + type + ", should be category or real");
			}
		}
		
		treader.close();
		
		if (attrList.size() == 0 || result == null)
			throw new IOException("Category initialization error.");

		// Initialize the DecisionTreeNode class based on the attributes.
		AttributeContainer attrs = new AttributeContainer(attrList, result);
		DecisionTreeNode.initialize(attrs);

		// Read the training data from file into an array of DataContainers.
		ArrayList<TrainingDataContainer> data = new ArrayList<TrainingDataContainer>();
		
		FileReader rd = new FileReader(fileName);
		BufferedReader reader = new BufferedReader(rd);
		String line = null;
		while ((line = reader.readLine()) != null)
			data.add(new TrainingDataContainer(attrs, line));

		reader.close();
		rd.close();

		// Read the testing data from file into an array of DataContainers.
		ArrayList<TestingDataContainer> predictSet = new ArrayList<TestingDataContainer>();

		FileReader rdt = new FileReader(testName);
		BufferedReader readert = new BufferedReader(rdt);
		line = null;
		while ((line = readert.readLine()) != null)
			predictSet.add(new TestingDataContainer(attrs, line));

		readert.close();
		rdt.close();

		// Start cross validation process, and calculate the average accuracy.
		System.out.println("====== CROSS VALIDATION PROCESS ======");
		double accuracy = 0.0;

		for (int i = 0; i < CROSS_PARTS; i++) {
			accuracy += crossValidation(CROSS_PARTS, i, data);
		}
		
		accuracy /= CROSS_PARTS;
		
		System.out.println("\nAverage accuracy for cross validation: " + accuracy);
		
		// Builder a decision tree using given training data.
		DecisionTreeNode finalTree = DecisionTreeNode.buildTree(data, attrList);
		finalTree.simplePrune();
		
		System.out.println("\n====== DECISION TREE ======");
		finalTree.printTree(0);
		
		// Give prediction of test cases.
		System.out.println("\n====== PREDICTION ON TEST CASES ======");
		finalTree.predictNodes(predictSet);
	}

	// Cross validation process for @n-th fold of all @parts folds
	public static double crossValidation(int parts, int n, ArrayList<TrainingDataContainer> data) {
		if (parts <= 0 || n >= parts)
			return -1.0;

		System.out.println("\nCross-validation Process " + (n + 1) + "/" + parts);

		int size = data.size();
		ArrayList<TrainingDataContainer> trainSet = new ArrayList<TrainingDataContainer>();
		ArrayList<TrainingDataContainer> testSet = new ArrayList<TrainingDataContainer>();

		for (int i = 0; i < size; i++) {
			if (i % parts != n)
				trainSet.add(data.get(i));
			else
				testSet.add(data.get(i));
		}

		DecisionTreeNode tree = DecisionTreeNode.buildTree(trainSet, attrList);		

		tree.simplePrune();

		tree.testNodes(trainSet);
		return tree.testNodes(testSet);
	}
}
