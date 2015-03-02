/* 		Team Think Tank
 * 		Decision Tree(C4.5)
 * 
 * Class: 	TrainingDataContainer
 * Date: 	2014.04.05
 */

import java.io.IOException;
import java.util.ArrayList;

public class TrainingDataContainer extends DataContainer {
	public Integer result = null;
	
	// Initialize the TrainingDataContainer based on given attribute set and an input line.
	// For training data, the result field will be filled based on the input line.
	public TrainingDataContainer(AttributeContainer _attrs, String line) throws IOException {
		super(_attrs);
		
		data = new ArrayList<Object>();
		
		String[] parts = line.split(",");
		if (parts.length != attrs.getSize() + 1)
			throw new IOException("The line is not formatted correctly.");
		for (int i = 0; i < attrs.getSize(); i++) {
			int type = attrs.getAttributeType(i);
			switch (type) {
			case Attribute.ATTR_CATEGORY:
				Integer type_number = attrs.getCategoryType(i, parts[i].trim());
				if (type_number < 0)
					throw new IOException("[Line]" + line +": [Field]" + parts[i] + " out of range.");
				data.add(type_number);
				break;
			case Attribute.ATTR_REAL:
				data.add(Double.valueOf(parts[i]));
				break;
			default:
				throw new IOException("Uninitialized attribute.");
			}
		}
		
		Integer result_number = attrs.getResultType(parts[parts.length - 1]);
		if (result_number < 0)
			throw new IOException("[Line]" + line +": [Result]" + parts[parts.length - 1] + " out of range.");
		result = result_number;
	}
	
	// Convert the record to a string, displaying values of all attributes and result of the current record.
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < attrs.getSize(); i++) {
			sb.append(attrs.getAttributeName(i) + ":");
			if (attrs.getAttributeType(i) == Attribute.ATTR_CATEGORY) {
				sb.append(attrs.getAttributeValue(i, (Integer) data.get(i)));
			} else {
				sb.append((Double) data.get(i));
			}
			sb.append("\t");
		}
		sb.append(attrs.result.categories.get(result));
		return sb.toString();
	}
}
