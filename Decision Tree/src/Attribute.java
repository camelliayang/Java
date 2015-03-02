/* 		Team Think Tank
 * 		Decision Tree(C4.5)
 * 
 * Class: 	Attribute
 * Date: 	2014.04.05
 */

import java.util.List;

public abstract class Attribute {
	
	// Define possible types of the attribute.
	public static final int ATTR_CATEGORY = 1001;
	public static final int ATTR_REAL = 1002;
	
	static final double ZERO_THRESHOLD = 1e-5;
	
	// Storing the name and type of the attribute.
	public int attr_type;
	public String attr_name;
	
	public Attribute(int _type, String _name) {
		attr_type = _type;
		attr_name = _name;
	}
	
	// Abstract method. Calculate the info gain ratio based on C4.5 algorithm. 
	// If the attribute is real attribute, the split pivot will also be returned.
	public abstract CalculateResult calculateInfo(List<TrainingDataContainer> data, double curinfo);
	
	class CalculateResult {
		double info;
		double pivot;
		
		public CalculateResult(double _info, double _pivot) {
			info = _info;
			pivot = _pivot;
		}
	}
}
