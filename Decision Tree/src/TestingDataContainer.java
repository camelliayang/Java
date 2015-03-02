/* 		Team Think Tank
 * 		Decision Tree(C4.5)
 * 
 * Class: 	TestingDataContainer
 * Date: 	2014.04.05
 */

import java.io.IOException;
import java.util.ArrayList;

public class TestingDataContainer extends DataContainer {
	public Integer result = null;
	
	// Initialize the TestingDataContainer based on given attribute set and an input line.
	// For testing data, the result field will be null before making prediction with decision tree.
	public TestingDataContainer(AttributeContainer _attrs, String line) throws IOException {
		super(_attrs);
		
		data = new ArrayList<Object>();
		
		String[] parts = line.split(",");
		if (parts.length < attrs.getSize())
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
		if (result != null)
			sb.append(attrs.result.categories.get(result));
		else
			sb.append("NEED TO BE CALCULATED");
		return sb.toString();
	}
}
