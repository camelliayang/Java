/**
 * The Think Tank (Team 7)
 * Latest edited: April 8, 2014
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KNN {

	private final int k;
	private final String classType;
	private List<ArrayList<String>> trainingSet;
	private List<ArrayList<String>> testingSet;
	private List<Double> maxAttribute;
	private List<Double> minAttribute;
	private List<Neighbor> neighbor;
	private List<Double> attributeWeight;
	private Map<String, Double> similarityMatrix;

	// Constructor: Variables Initialization
	public KNN(List<ArrayList<String>> trainingSet,
			List<ArrayList<String>> testingSet,
			Map<String, Double> similarityMatrix, List<Double> attributeWeight,
			int k, String classType) {
		this.k = k;
		this.classType = classType;
		this.trainingSet = trainingSet;
		this.testingSet = testingSet;
		this.similarityMatrix = similarityMatrix;
		this.attributeWeight = attributeWeight;
		this.neighbor = new ArrayList<Neighbor>();
		this.maxAttribute = new ArrayList<Double>();
		this.minAttribute= new ArrayList<Double>();
		findMaxAndMin();
	}

	// Find the max and Min values for each attribute
	private void findMaxAndMin() {
		double max;
		double min;
		boolean error;
		
		// k denotes the kth attribute in the the vector
		for (int k = 0; k < trainingSet.get(0).size() - 1; k++) {			
			max = Double.MIN_VALUE;
			min = Double.MAX_VALUE;
			error = false;
			
			try {
				for (int i = 0; i < trainingSet.size(); i++) {
					double curr = Double.parseDouble(trainingSet.get(i).get(k));
					if (curr > max)
						max = curr;
					if (curr < min)
						min = curr;
				}
				for (int j = 0; j < testingSet.size(); j++) {
					double curr = Double.parseDouble(testingSet.get(j).get(k));
					if (curr > max)
						max = curr;
					if (curr < min)
						min = curr;
				}
			} catch (Exception e) {
				maxAttribute.add(-1.0);
				minAttribute.add(-1.0);
				error = true;
			}
			if (!error){
				maxAttribute.add(max);
				minAttribute.add(min);
			}
		}
	}

	// Normalize the kth  attributes in the vectors
	private double normalizeVectorAttributes(double value, int kthAttribute) {
		
		// Find  max & min values for kth attribute
		double min = minAttribute.get(kthAttribute);
		double max = maxAttribute.get(kthAttribute);
		double norm = (value - min) / (max - min ) * attributeWeight.get(kthAttribute);
		
		return norm;
	}


	// Compute the similarity between testing data set and training data set
	public List<ArrayList<String>> computeSimilarity() {
		double similarityScore;

		for (int testIndex = 0; testIndex < testingSet.size(); testIndex++) {
			for (int trainIndex = 0; trainIndex < trainingSet.size(); trainIndex++) {
				similarityScore = 0;
				for (int i = 0; i < trainingSet.get(0).size() - 1; i++) {
					
					String trainingStr = trainingSet.get(trainIndex).get(i);
					String testingStr = testingSet.get(testIndex).get(i);
					//Compute the distance between testingSet Vector and trainingSet Vector with Euclidean distance
					try { 
						double trainingVal = normalizeVectorAttributes(Double.parseDouble(trainingStr),i) * attributeWeight.get(i); 
						double testingVal = normalizeVectorAttributes(Double.parseDouble(testingStr),i) * attributeWeight.get(i);
						similarityScore = similarityScore + Math.pow((trainingVal - testingVal), 2);
					} catch (Exception e) {
						//The distance between symbolic attributes is defined by its similarity matrix
						StringBuilder key = new StringBuilder();
						key.append(testingStr + "-" + trainingStr);
						double distance = similarityMatrix.get(key.toString().toLowerCase());
						similarityScore = similarityScore + Math.pow( ((1 - distance ) * attributeWeight.get(i)), 2);
					}
				}
				similarityScore = 1 / Math.sqrt(similarityScore);
				String type = trainingSet.get(trainIndex).get(trainingSet.get(trainIndex).size() - 1);
				neighbor.add(new Neighbor(similarityScore, type));
			}
			Collections.sort(neighbor);
			if (classType.equals("Discrete")) {
				kNeighborsVote(testIndex);
			} else if (classType.equals("Continuous")) {
				clasificationAverageSimilarityScore(testIndex);
			}
			neighbor.clear();
		}
		return testingSet;
	}


	// K neighbors vote for objective attribute
	public void kNeighborsVote(int testIndex) {
		Map<String, Integer> classMap = new HashMap<String, Integer>();
		int commonClass = 0;
		int commonClassMax = 0;
		String similarityClass = "";
		
		for (int numNeighbor = 0; numNeighbor < k; numNeighbor++) {
			if (classMap.get(neighbor.get(numNeighbor).getClassType()) != null) {
				commonClass = classMap.get(neighbor.get(numNeighbor).getClassType()) + 1;
				classMap.put(neighbor.get(numNeighbor).getClassType(),commonClass);
			} else {
				commonClass = 1;
				classMap.put(neighbor.get(numNeighbor).getClassType(),commonClass);
			}
			if (commonClass > commonClassMax) {
				commonClassMax = commonClass;
				similarityClass = neighbor.get(numNeighbor).getClassType();
			}
		}
		testingSet.get(testIndex).set((testingSet.get(testIndex).size() - 1),similarityClass);
	}

	
	// Classify the object attribute in the testing data set
	public void clasificationAverageSimilarityScore(int testIndex) {
		double similarityScore = 0;
		for (int numNeighbor = 0; numNeighbor < k; numNeighbor++) {
			similarityScore = similarityScore + Double.parseDouble(neighbor.get(numNeighbor).getClassType());
		}
		testingSet.get(testIndex).set((testingSet.get(testIndex).size() - 1),String.valueOf(similarityScore / k));
	}
	
	
	
	//Inner class - Neighbor: store K-neighbor information
	class Neighbor implements Comparable<Neighbor> {
		private double similarityScore;
		private String classType;

		public Neighbor(double similarityScore, String classType) {
			this.similarityScore = similarityScore;
			this.classType = classType;
		}

		public double getSimilarityScore() {
			return similarityScore;
		}

		public String getClassType() {
			return classType;
		}

		// Implements compareTo method
		public int compareTo(Neighbor other) {
			if (this.getSimilarityScore() < other.getSimilarityScore())
				return 1;
			if (this.getSimilarityScore() > other.getSimilarityScore())
				return -1;
			if (this.getClassType().compareTo(other.getClassType()) != 0)
				return this.getClassType().compareTo(other.getClassType());
			return 0;
		}
	}
}