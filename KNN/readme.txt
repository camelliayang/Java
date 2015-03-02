Before using the kNN program, you have to assign values to the constant variables as followed:
1) Number of K in KNN
	static final int K = 3; 
2) Training file path: File format should be .csv
	static final String TRAIN_FILE_PATH = "trainProdSelection.csv"; 
3) Testing file path: File format should be .csv
	static final String TEST_FILE_PATH = "testProdSelection.csv"; 
4) Similarity Matrix path: File format should be .csv
	static final String SIMILARITY_MATRIX_FILE_PATH = "similaritymatrixA.csv"; 

5) Number of folds in the cross validation
	static final int NUM_OF_FOLDS = 8; 
	
6) Predictor Attribute type: Discrete/Continuous. Use "Discrete" for categorical prediction and "Continuous" for numerical prediction.
	static final String predictionClassType = "Discrete";

The meaning of output is as followed:
1) line 1: Accuracy before weight optimization
2) line 2: attribute weights before optimization
3) line 3: Accuracy after weight optimization
4) line 4: attribute weights after optimization
5) line 5-end: kNN prediction for the object attribute.