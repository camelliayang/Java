/**
 * The Think Tank (Team 7)
 * Latest edited: April 8, 2014
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AttributeWeighting {
	
    private double accuracy;
    private final double expectedAccuracy;
    private List<Double> attributeWeights;

    
    // Constructor: Variables Initialization
    public AttributeWeighting(double accuracy, double expectedAccuracy, List<Double> attributeWeights) {
		setAccuracy(accuracy);
		this.expectedAccuracy = expectedAccuracy;
		setAttributeWeights(attributeWeights);
    }

    // Update weights
    public double[] updateWeights(List<ArrayList<String>> trainData,
    		List<Double> attributeWeightFromMain,
		    Map<String, Double> similarityMatrix, 
		    int numSection, 
		    int k,
		    double increment, 
		    int numOfAttributes, 
		    double expacc,
		    String predictionClassType) {
    	
		CrossValidation accuracyCalculator = new CrossValidation(trainData,
				attributeWeightFromMain, similarityMatrix, numSection, k, predictionClassType);
		
		int count = 0;
		int threshold = 10;
		double currAccuracy = 0;
		double[] weights = new double[numOfAttributes];
		int breakTolerance = 3;		
			
		//Repeat till acceptable level of accuracy is reached
		while (getAccuracy() < getExpectedAccuracy()) {
		    for (int j = breakTolerance; j >= 1; j--) {
				for (int attr = 0; attr < numOfAttributes; attr++) {				
				    for (int i = 0; i < threshold; i++) {
						if (currAccuracy >= 90) {
						    attributeWeightFromMain.set(attr, attributeWeightFromMain.get(attr) + increment / 2);
						}else {
						    attributeWeightFromMain.set(attr, attributeWeightFromMain.get(attr) + increment);
						}
					
						//Calculate the classification error
						accuracyCalculator = new CrossValidation(trainData, attributeWeightFromMain, similarityMatrix, numSection, k, predictionClassType);
						currAccuracy = accuracyCalculator.crossValidation();
						count++;
					
						if (getAccuracy() - currAccuracy >= j) {
						    break;
						}
						if (currAccuracy > getAccuracy()) {
						    setAccuracy(currAccuracy);
						    setAttributeWeights(attributeWeightFromMain);
						    for (int x = 0; x < this.getAttributeWeights().size(); x++) {
						    	weights[x] = this.getAttributeWeights().get(x);
						    }
						}
				    }
		
				}
				//Adjust the weights according to the error
				for (int num = 0; num < numOfAttributes; num++) {
				    for (int i = 0; i < threshold; i++) {
						if (attributeWeightFromMain.get(num) - increment > 0) {
						    if (currAccuracy >= 90)
						    	attributeWeightFromMain.set(num, attributeWeightFromMain.get(num) + increment / 2);
						    else
						    	attributeWeightFromMain.set(num, attributeWeightFromMain.get(num) - increment);
						    accuracyCalculator = new CrossValidation(trainData, attributeWeightFromMain, similarityMatrix, numSection, k, predictionClassType);
						    currAccuracy = accuracyCalculator.crossValidation();
						}
						count++;
						if (getAccuracy() - currAccuracy >= j) {
						    break;
						}
						if (currAccuracy > getAccuracy()) {
						    setAccuracy(currAccuracy);
						    setAttributeWeights(attributeWeightFromMain);
						    for (int x = 0; x < this.getAttributeWeights().size(); x++) {
						    	weights[x] = this.getAttributeWeights().get(x);
						    }
						}
				    }
				}
		    }
		    if (count > threshold * numOfAttributes * 20)
			return weights; 	
		}
		return weights; 
    }

    public List<Double> getAttributeWeights() {
    	return attributeWeights;
    }

    public void setAttributeWeights(List<Double> attributeWeights) {
    	this.attributeWeights = attributeWeights;
    }

    public double getAccuracy() {
    	return accuracy;
    }

    public void setAccuracy(double accuracy) {
    	this.accuracy = accuracy;
    }

    public double getExpectedAccuracy() {
    	return expectedAccuracy;
    }

}
