/* 		Team Think Tank
 * 		Decision Tree(C4.5)
 * 
 * Class: 	CategoryAttribute
 * Date: 	2014.04.05
 */

import java.util.ArrayList;
import java.util.List;

public class CategoryAttribute extends Attribute {
	public final int num_of_categories;
	public ArrayList<String> categories;
	
	// Initialize the name and possible values of the attribute.
	public CategoryAttribute(String _name, ArrayList<String> _categories) {
		super(Attribute.ATTR_CATEGORY, _name);
		categories = new ArrayList<String>();
		
		for (String c : _categories)
			categories.add(c);
		num_of_categories = categories.size();
	}

	// Calculate the info gain ratio based on the attribute using C4.5 algorithm.
	@Override
	public CalculateResult calculateInfo(List<TrainingDataContainer> data, double curinfo) {
		if (data.isEmpty())
			return new CalculateResult(0, -1);
		
		AttributeContainer attrs = DataContainer.attrs;
		int index = attrs.getAttributeIndex(attr_name);
		if (index < 0) {
			System.out.println("The attribute " + attr_name + " does not exist.");
		}
		
		int[][] count = new int[num_of_categories][attrs.result.num_of_categories];
		int[] catecount = new int[num_of_categories];
		
		for (int i = 0; i < num_of_categories; i++) {
			for (int j = 0; j < attrs.result.num_of_categories; j++) {
				count[i][j] = 0;
			}
			catecount[i] = 0;
		}
		
		for (TrainingDataContainer d : data) {
			count[(Integer) d.data.get(index)][d.result]++;
			catecount[(Integer) d.data.get(index)]++;
		}
		
		double info = 0.0;
		
		for (int i = 0; i < num_of_categories; i++) {
			double subinfo = 0.0;
			if (catecount[i] > 0) {
				for (int j = 0; j < attrs.result.num_of_categories; j++) {
					if (count[i][j] > 0) {
						double p = count[i][j] * 1.0 / catecount[i];
						subinfo -= p * Math.log(p);
					}
				}
			}
			info += catecount[i] * subinfo / data.size();
		}
		
		double splitinfo = 0.0;
		
		for (int i = 0; i < num_of_categories; i++) {
			double p = catecount[i] * 1.0 / data.size();
			if (catecount[i] > 0)
				splitinfo -= p * Math.log(p);
		}
		
		double ret = splitinfo < ZERO_THRESHOLD ? Double.MIN_VALUE : ((curinfo - info) / splitinfo);
		
		return new CalculateResult(ret, -1);
	}
	
	// Split the data set into multiple data sets based on the value of the current attribute.
	public ArrayList<ArrayList<TrainingDataContainer>> split(List<TrainingDataContainer> data) {
		ArrayList<ArrayList<TrainingDataContainer>> ret = new ArrayList<ArrayList<TrainingDataContainer>>();
		for (int i = 0; i < num_of_categories; i++)
			ret.add(new ArrayList<TrainingDataContainer>());
		
		AttributeContainer attrs = DataContainer.attrs;
		int index = attrs.getAttributeIndex(attr_name);
		if (index < 0) {
			System.out.println("The attribute " + attr_name + " does not exist.");
		}
		
		for (TrainingDataContainer d : data) {
			ret.get((Integer) d.data.get(index)).add(d);
		}
		
		return ret;
	}
	
	// Get the integer representation of the given type in the attribute(i.e. index in array list storing possible values 
	// of the attribute).
	public int getTypeNumber(String type) {
		for (int i = 0; i < num_of_categories; i++)
			if (categories.get(i).equals(type))
				return i;
		return -1;
	}
}
