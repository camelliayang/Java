/**
 * The Think Tank (Team 7)
 * Latest edited: April 8, 2014
 */
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
	
	
	/* Constants*/
	// Number of K in KNN
	static final int K = 3; 
	// Training file path
	static final String TRAIN_FILE_PATH = "trainProdSelection.csv"; 
	//a: trainProdSelection.csv
	//b: trainProdIntroBinary.csv
	//b: trainProdIntroReal.csv
	
	// Testing file path
	static final String TEST_FILE_PATH = "testProdSelection.csv"; 
	//a: testProdSelection.csv
	//b: testProdIntroBinary.csv
	//b: testProdIntroReal.csv
	
	// Similarity Matrix path
	static final String SIMILARITY_MATRIX_FILE_PATH = "similaritymatrixA.csv"; 
	//a: similaritymatrixA.csv
	//b: similaritymatrixB.csv
	
	// Number of folds in the cross validation
	static final int NUM_OF_FOLDS = 8; 
	
	// Predictor Attribute type: Discrete/Continuous
	static final String predictionClassType = "Discrete";
	

	/* Class Variables */
	private static List<ArrayList<String>> trainingSet;
	private static List<ArrayList<String>> testingSet;

	
	public static void main(String[] args) {

		//1. Read in the training data set. 
		CSVDataReader csvDataReader = new CSVDataReader();
		csvDataReader.setInputFile(TRAIN_FILE_PATH);
		trainingSet = new ArrayList<ArrayList<String>>();
		try {
			trainingSet = csvDataReader.readData();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//2. Read in the test data set 		
		csvDataReader.setInputFile(TEST_FILE_PATH);
		testingSet = new ArrayList<ArrayList<String>>();
		try {
			testingSet = csvDataReader.readData();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//3. Read in the similarity matrix
		csvDataReader.setInputFile(SIMILARITY_MATRIX_FILE_PATH);
		Map<String, Double> similarityMatrix = new HashMap<String, Double>();
		try {
			similarityMatrix = csvDataReader.readSimilarityMatrix();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//4. Assign weights to attributes in training set
		List<Double> attributeWeight = new ArrayList<Double>();
		if (predictionClassType.equals("Discrete")) {
			attributeWeight = new ArrayList<Double>();
			
			//Assign certain weights
			for (int index = 0; index < trainingSet.get(index).size() - 1; index++)
				attributeWeight.add(1.0);
		} else { 
			attributeWeight = new ArrayList<Double>(Arrays.asList(
					2.9000000000000017, 2.000000000000001, 0.10000000000000014,
					0.10000000000000014, 2.100000000000001, 2.4000000000000012,
					2.4000000000000012, 3.100000000000002));
		}
		
		//5. Cross Validation
		if (predictionClassType.equals("Discrete")) {
			// Accuracy before weight optimization
			CrossValidation accuracyBeforeOptimization = new CrossValidation(
					trainingSet, attributeWeight, similarityMatrix, NUM_OF_FOLDS,K, predictionClassType);
			System.out.println("Accuracy before weight optimization: "
					+ accuracyBeforeOptimization.crossValidation());
			System.out.println(attributeWeight);

			// Weight Calculations
			AttributeWeighting attributeWeighting = new AttributeWeighting(0, 99, attributeWeight);
			double[] attributeWeightArray = new double[attributeWeight.size()];
			attributeWeightArray = attributeWeighting.updateWeights(trainingSet, attributeWeight,
							similarityMatrix, NUM_OF_FOLDS, K, 0.1,attributeWeight.size(),
							attributeWeighting.getExpectedAccuracy(),predictionClassType);
			for (int i = 0; i < attributeWeightArray.length; i++) {
				attributeWeight.set(i, attributeWeightArray[i]);
			}
			System.out.println(attributeWeight);
			
			// Accuracy after weight optimization
			CrossValidation accuracyAfterOptimization = new CrossValidation(
					trainingSet, attributeWeight, similarityMatrix, NUM_OF_FOLDS,
					K, predictionClassType);
			System.out.println("Accuracy after weight optimization: "
					+ accuracyAfterOptimization.crossValidation());
		}
		//6. Run KNN Algorithm
				KNN knn = new KNN(trainingSet, testingSet, similarityMatrix,
						attributeWeight, K, predictionClassType);
				System.out.println("kNN Predictions for Object Attributes:");
				for (ArrayList<String> data : knn.computeSimilarity())
					System.out.println(data);
				
	}
}
