/**
 * The Think Tank (Team 7)
 * Latest edited: April 8, 2014
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//Evaluate data mining accuracy on training data
public class CrossValidation {

	private final int k;
	private final String classType;
	private final int numOfFolds;
	private int indexEndSection;
	private int indexStartSection;
	private final List<ArrayList<String>> trainingSet;
	private final List<Double> attributeWeight;
	private final Map<String, Double> similarityMatrix;

	// Constructor: Variables Initialization
	public CrossValidation(List<ArrayList<String>> trainData,
			List<Double> attributeWeight, Map<String, Double> similarityMatrix,
			int numOfFolds, int k, String classType) {
		this.k = k;
		this.classType = classType;
		this.numOfFolds = numOfFolds;
		this.trainingSet = trainData;
		this.attributeWeight = attributeWeight;
		this.similarityMatrix = similarityMatrix;
	}

	// Divide the training data into k equal pieces
	private void divideTrainingSet(int indexSection) {
		indexSection++;
		int sectionItemNum = trainingSet.size() / numOfFolds;
		if (indexSection == numOfFolds) {
			indexEndSection = trainingSet.size();
			indexStartSection = (indexSection - 1) * sectionItemNum;
		} else {
			indexEndSection = indexSection * sectionItemNum;
			indexStartSection = indexEndSection - sectionItemNum;
		}
	}

	//k-fold cross validation process
	public double crossValidation() {
		Double accuracy = 0.0;
		List<ArrayList<String>> knnResult;
		List<ArrayList<String>> tempTrainingSet = new ArrayList<ArrayList<String>>();
		List<ArrayList<String>> tempTestingSet = new ArrayList<ArrayList<String>>();
		List<ArrayList<String>> testingSet = new ArrayList<ArrayList<String>>();		
		
		for (int indexSection = 0; indexSection < numOfFolds; indexSection++) {
			//Divide the training data into k equal pieces (each piece is called a "fold")
			divideTrainingSet(indexSection);
	
			//Train the classifier using all but kth fold
			for (int indexRow = 0; indexRow < trainingSet.size(); indexRow++) {
				if ((indexRow >= indexStartSection) && (indexRow < indexEndSection)) {
					testingSet.add(new ArrayList<String>(trainingSet.get(indexRow)));
					tempTestingSet.add(new ArrayList<String>(trainingSet.get(indexRow)));
				} else {
					tempTrainingSet.add(new ArrayList<String>(trainingSet.get(indexRow)));
				}
			}
			
			KNN knn = new KNN(tempTrainingSet, clearObjectAttribute(tempTestingSet),
					similarityMatrix, attributeWeight, k, classType);
			knnResult = knn.computeSimilarity();
			
			//Test for accuracy on kth fold
			int rightValues = 0;
			for (int index = 0; index < testingSet.size(); index++) {
				if (testingSet.get(index).get(testingSet.get(index).size() - 1)
						.equals(knnResult.get(index).get(testingSet.get(index).size() - 1)))
					rightValues++;
			}
			
			accuracy = accuracy + ((double) ((rightValues * 100) / testingSet.size()));
			tempTrainingSet.clear();
			tempTestingSet.clear();
			knnResult.clear();
			testingSet.clear();
			//Repeat with kth-1 fold held out for testing, 
			//then with kth-2 fold for testing, till tested on all folds
		}
		//Report the average accuracy across folds
		return accuracy / numOfFolds;
	}

	// clear the data in object attribute
	private List<ArrayList<String>> clearObjectAttribute(
			List<ArrayList<String>> arrayTempTest) {
		for (int index = 0; index < arrayTempTest.size(); index++) {
			arrayTempTest.get(index).set((arrayTempTest.get(index).size() - 1),"");
		}
		return arrayTempTest;
	}
}
