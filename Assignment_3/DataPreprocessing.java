// Dominique da Silva

public class DataPreprocessing {
    public static int[][] preprocessData(String[][] instances){
        int numInstances = instances.length;
        int numAttributes = instances[0].length;

        //creating a matrix in order to hold the preprocessed data
        int[][] preprocessedDataMatrix = new int[numInstances][numAttributes];

        //Want to create mappings for each catagorical attribute
        //the position indicates the value that will be assigned
       String[][] categoricalMappings = {
        {"no-recurrence-events", "recurrence-events"},  // Class attribute
        {"10-19", "20-29", "30-39", "40-49", "50-59", "60-69", "70-79", "80-89", "90-99"},  // Age attribute
        {"lt40", "ge40", "premeno"},  // Menopause attribute
        {"0-4", "5-9", "10-14", "15-19", "20-24", "25-29", "30-34", "35-39", "40-44", "45-49", "50-54", "55-59"},  // Tumor-size attribute
        {"0-2", "3-5", "6-8", "9-11", "12-14", "15-17", "18-20", "21-23", "24-26","27-29", "30-32", "33-35", "36-39"},  // inv-nodes attribute
        {"no" , "yes"},  // node-caps attribute
        {"1" , "2" , "3"},  // deg-malig attribute
        {"left" , "right"},  // breast attribute
        {"left_up" , "left_low" , "right_up" , "right_low" , "central"},  // breast-quad attribute
        {"no" , "yes"},  // irradiat attribute
        };

        // Create arrays to hold the sum and count for each attribute
        double[] attributeSums = new double[numAttributes];
        int[] attributeCounts = new int[numAttributes];

        //now we want to iterate over every instance in the input data and want to process the data
        //we want to convert the data into number in order to make classification easier
        for (int i = 0; i < numInstances; i++) {
            String[] instance = instances[i];

            // Iterate over each attribute in the instance
            for (int j = 0; j < numAttributes; j++) {
                String value = instance[j];

                // Convert the categorical value to a numeric value using the mapping
                int numericValue;
                if (value.equals("?")) {
                    // Handle missing value by adding the value to the attribute sum and incrementing the count
                    numericValue = -1; // Assuming a default value of -1 for missing values
                } else {
                    numericValue = convertToNumericValue(value, categoricalMappings[j]);
                }

                attributeSums[j] += numericValue;
                attributeCounts[j]++;

                // Assign the numeric value to the preprocessed data
                preprocessedDataMatrix[i][j] = numericValue;
            }
        }

        // Calculate the average for each attribute
        for (int j = 0; j < numAttributes; j++) {
            if (attributeCounts[j] > 0) {
                attributeSums[j] /= attributeCounts[j];
            }
        }

        // Iterate over the preprocessed data again to replace missing values with attribute averages
        for (int i = 0; i < numInstances; i++) {
            for (int j = 0; j < numAttributes; j++) {
                if (preprocessedDataMatrix[i][j] == -1) { // Check if the value is a missing value
                    preprocessedDataMatrix[i][j] = (int) attributeSums[j];
                }
            }
        }

        return preprocessedDataMatrix;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static int convertToNumericValue(String value, String[] mapping) {
        // Find the index of the value in the mapping
        for (int i = 0; i < mapping.length; i++) {
            if (mapping[i].equals(value)) {
                return i;
            }
        }

        // If the value is not found, return a default value (e.g., -1)
        // We rectify this with the average as well
        return -1;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////

}
