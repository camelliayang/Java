/* 		Team Think Tank
 * 		Decision Tree(C4.5)
 * 
 * Class: 	RealAttribute
 * Date: 	2014.04.05
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RealAttribute extends Attribute {	
	static final int N_PARTS = 10;
	
	// Initialize the name of the attribute.
	public RealAttribute(String _name) {
		super(Attribute.ATTR_REAL, _name);
	}

	// Calculate the info gain based on the attribute using C4.5 algorithm.
	// The split pivot is the n/nparts * data.size-th smallest value of the current attribute in the data set 
	public CalculateResult calculatePartInfo(List<TrainingDataContainer> data, double curinfo, int nparts, int n) {		
		AttributeContainer attrs = DataContainer.attrs;
		final int index = attrs.getAttributeIndex(attr_name);
		if (index < 0) {
			System.out.println("The attribute " + attr_name + " does not exist.");
		}
		
		double pivot = (double) data.get((data.size() - 1) * n / nparts).data.get(index);
		
		int[][] count = new int[2][attrs.result.num_of_categories];
		int[] catecount = new int[2];
		
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < attrs.result.num_of_categories; j++) {
				count[i][j] = 0;
			}
			catecount[i] = 0;
		}
		
		for (TrainingDataContainer d : data) {
			double value = (double) d.data.get(index);
			if (value < pivot) {
				count[0][d.result]++;
				catecount[0]++;
			} else {
				count[1][d.result]++;
				catecount[1]++;
			}
		}
		
		double info = 0.0;
		
		for (int i = 0; i < 2; i++) {
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
		
		for (int i = 0; i < 2; i++) {
			double p = catecount[i] * 1.0 / data.size();
			if (catecount[i] > 0)
				splitinfo -= p * Math.log(p);
		}
		
		double ret = splitinfo < ZERO_THRESHOLD ? Double.MIN_VALUE : ((curinfo - info) / splitinfo);
		
		return new CalculateResult(ret, pivot);
	}
	
	// Calculate the info gain ratio based on the attribute using C4.5 algorithm.
	@Override
	public CalculateResult calculateInfo(List<TrainingDataContainer> data, double curinfo) {
		if (data.isEmpty())
			return new CalculateResult(0, -1);
		
		AttributeContainer attrs = DataContainer.attrs;
		final int index = attrs.getAttributeIndex(attr_name);
		if (index < 0) {
			System.out.println("The attribute " + attr_name + " does not exist.");
		}
		
		Collections.sort(data, new Comparator<TrainingDataContainer>() {
			public int compare(TrainingDataContainer t1, TrainingDataContainer t2) {
				if ((Double) t1.data.get(index) < (Double) t2.data.get(index))
					return -1;
				else if ((Double) t1.data.get(index) > (Double) t2.data.get(index))
					return 1;
				return 0;
			}
		});
		
		CalculateResult maxr = null;
		for (int i = 2; i < N_PARTS - 1; i++) {
			CalculateResult cur = calculatePartInfo(data, curinfo, N_PARTS, i);
			if (maxr == null || cur.info > maxr.info)
				maxr = cur;
		}
		
		return maxr;
	}

	// Split the data set into multiple data sets based on the value of the current attribute and given pivot.
	public ArrayList<ArrayList<TrainingDataContainer>> split(List<TrainingDataContainer> data, double pivot) {
		ArrayList<ArrayList<TrainingDataContainer>> ret = new ArrayList<ArrayList<TrainingDataContainer>>();
		for (int i = 0; i < 2; i++)
			ret.add(new ArrayList<TrainingDataContainer>());
		
		AttributeContainer attrs = DataContainer.attrs;
		int index = attrs.getAttributeIndex(attr_name);
		if (index < 0) {
			System.out.println("The attribute " + attr_name + " does not exist.");
		}
		
		for (TrainingDataContainer d : data) {
			if ((double) d.data.get(index) < pivot)
				ret.get(0).add(d);
			else
				ret.get(1).add(d);
		}
		
		return ret;
	}
}
