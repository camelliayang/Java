/* 		Team Think Tank
 * 		Decision Tree(C4.5)
 * 
 * Class: 	DataContainer
 * Date: 	2014.04.05
 */

import java.util.ArrayList;

public abstract class DataContainer {
	static AttributeContainer attrs = null;
	
	// Storing values of categories on the data node.
	ArrayList<Object> data = null;
	
	public DataContainer(AttributeContainer _attrs) {
		if (attrs == null)
			attrs = _attrs;
	}
}
