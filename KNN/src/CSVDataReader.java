/*
 * Team Cinco
 * Task 11
 */

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import au.com.bytecode.opencsv.CSVReader;

public class CSVDataReader {

	private String inputFile;

	public void setInputFile(String inputFile) {
		this.inputFile = inputFile;
	}

	// Method to read the training and test dataset from a external files
	public ArrayList<ArrayList<String>> readData() throws IOException {
		String csvFilename = inputFile;
		CSVReader csvReader = new CSVReader(new FileReader(csvFilename));
		ArrayList<ArrayList<String>> dataList = new ArrayList<ArrayList<String>>();
		ArrayList<String> attributes;
		String[] row = csvReader.readNext();
		try {
			while ((row = csvReader.readNext()) != null) {
				attributes = new ArrayList<String>();
				for (int i = 0; i < row.length; i++) {
					attributes.add(row[i]);
				}
				dataList.add(attributes);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return dataList;
	}

	// Method to read the similarity matrix from a external files
	public Map<String, Double> readSimilarityMatrix() throws IOException {
		String csvFilename = inputFile;
		CSVReader csvReader = new CSVReader(new FileReader(csvFilename));
		Map<String, Double> dataMap = new HashMap<String, Double>();
		String[] row = csvReader.readNext();
		try {
			while ((row = csvReader.readNext()) != null) {
				dataMap.put(row[0], Double.parseDouble(row[1]));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return dataMap;
	}
}
