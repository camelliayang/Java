/* 		Team Think Tank
 * 		Decision Tree(C4.5)
 * 
 * Class: 	AttributeContainer
 * Date: 	2014.04.05
 */

import java.util.ArrayList;
import java.util.List;


public class AttributeContainer {
	public static AttributeContainer instance = null;
	
	// Storing information of all attributes.
	ArrayList<Attribute> attributes;
	
	// Storing information of the result attribute.
	CategoryAttribute result;
	
	public AttributeContainer(List<Attribute> _attributes, CategoryAttribute _result) {
		attributes = new ArrayList<Attribute>();
		attributes.addAll(_attributes);
		result = _result;
	}
	
	// Get number of attributes.
	public int getSize() {
		return attributes.size();
	}
	
	// Get the type of an attribute with index @index.
	public int getAttributeType(int index) {
		if (index < 0 || index > attributes.size())
			return -1;
		return attributes.get(index).attr_type;
	}
	
	// Get the index of type @type in the category attribute with index @index
	public int getCategoryType(int index, String type) {
		if (index < 0 || index > attributes.size())
			return -1;
		if (attributes.get(index).attr_type == Attribute.ATTR_REAL)
			return -2;
		return ((CategoryAttribute) attributes.get(index)).getTypeNumber(type);
	}
	
	// Get the index of type @type in result(label)
	public int getResultType(String type) {
		if (result == null)
			return -1;
		return result.getTypeNumber(type);
	}
	
	// Get the index of the attribute with name @name
	public int getAttributeIndex(String name) {
		for (int i = 0; i < attributes.size(); i++)
			if (attributes.get(i).attr_name.equals(name))
				return i;
				
		return -1;
	}
	
	// Get the name of the attribute with index @index
	public String getAttributeName(int index) {
		if (index < 0 || index > attributes.size())
			return "Invalid index " + index;
		return attributes.get(index).attr_name;
	}
	
	// Get the number of types of an attribute with index @index
	public int getAttributeSize(int index) {
		if (index < 0 || index > attributes.size())
			return -1;
		if (attributes.get(index).attr_type == Attribute.ATTR_REAL)
			return -2;
		return ((CategoryAttribute) attributes.get(index)).num_of_categories;
	}
	
	// Get the value of type with index @val in the category attribute with index @index
	public String getAttributeValue(int index, int val) {
		if (index < 0 || index > attributes.size())
			return "Invalid index " + index;
		if (attributes.get(index).attr_type == Attribute.ATTR_REAL)
			return "Real attribute";
		return ((CategoryAttribute) attributes.get(index)).categories.get(val);
	}
	
	// Get the Attribute Object with index @index.
	public Attribute getAttribute(int index) {
		if (index < 0 || index > attributes.size())
			return null;
		return attributes.get(index);
	}
}

