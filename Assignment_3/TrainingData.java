// Dominique da Silva

/*
 * Summary of what the data looks like:
 *     We have 286 instances in the  text file
 *     Each line or rather instance has the following format
 *          Class,age,menopause,tumor-size,inv-nodes,node-caps,deg-malig,breast,breast-quad,irradiat
 *     Missing Attribute Values: (denoted by "?")
 *      
 * This data set includes 201 instances of one class and 85 instances of another class.  
 * The instances are described by 9 attributes, some of which are linear and some are nominal.
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

public class TrainingData{
    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static void main(String[] args){
        String data = "breast-cancer-data.data";

        int numInstances = countInstances(data);
        int numAttributes = 10; //actually the number of columns

        String[][] instances = new String[numInstances][numAttributes];

        try (BufferedReader reader = new BufferedReader(new FileReader(data))) {
            ///////////////////////////////////////////////
            String line;
            int instanceIndex = 0;
            while ((line = reader.readLine()) != null) {
                //System.out.println("Instance "+ (instanceIndex+1) +": ");
                String[] attributes = line.split(",");
                for (int attrIndex = 0; attrIndex < numAttributes; attrIndex++) {
                    //System.out.println(attributes[attrIndex]);
                    instances[instanceIndex][attrIndex] = attributes[attrIndex];
                }
                instanceIndex++;
            }

            ///////////////////////////////////////////////
            // The file has now been read
            // Data wrangling
            // int[][] processed = DataPreprocessing.preprocessData(instances);


            ///////////////////////////////////////////////
            //We need to split it into a testing and training set
            // Create an array to hold indices of all instances
            int[] allIndices = new int[numInstances];
            for (int i = 0; i < numInstances; i++) {
                allIndices[i] = i;
            }

            // Shuffle the array of indices
            Random random = new Random(1234);
            for (int i = numInstances - 1; i > 0; i--) {
                int j = random.nextInt(i + 1);
                int temp = allIndices[i];
                allIndices[i] = allIndices[j];
                allIndices[j] = temp;
            }

            // Create a training set array
            int trainingSetSize = 229;
            String[][] trainingSet = new String[trainingSetSize][numAttributes];
            for (int i = 0; i < trainingSetSize; i++) {
                int instanceIndex1 = allIndices[i];
                trainingSet[i] = instances[instanceIndex1];
            }

            // Create a testing set array
            int testingSetSize = 57;
            String[][] testingSet = new String[testingSetSize][numAttributes];
            for (int i = 0; i < testingSetSize; i++) {
                int instanceIndex2 = allIndices[trainingSetSize + i];
                testingSet[i] = instances[instanceIndex2];
            }

            //The testingSet and the trainingSet still contain their classes

            // Printing the training set
            /*System.out.println("");
            System.out.println("Training Set:");
            for (String[] instance : trainingSet) {
                System.out.println(java.util.Arrays.toString(instance));
            }*/
            int[][] TrainingpreprocessedWithClass = DataPreprocessing.preprocessData(trainingSet);

            // Printing the testing set
            /*System.out.println("");
            System.out.println("Testing Set:");
            for (String[] instance : testingSet) {
                System.out.println(java.util.Arrays.toString(instance));
            }*/
            int[][] TestingpreprocessedWithClass = DataPreprocessing.preprocessData(testingSet);


            // now we want to remove the class that has been given from both the data testing and the training
            int[][] TrainingProcessedWithoutClass = new int[trainingSetSize][numAttributes-1];
            int[] TrainingClass = new int[trainingSetSize];
            int[][] TestingProcessedWithoutClass = new int[testingSetSize][numAttributes-1];
            int[] TestingClass = new int[testingSetSize];

            
            for(int i=0; i<trainingSetSize;i++){
                int counterTraining =0;
                for(int j=0; j<numAttributes;j++){
                    if(j!=0){
                        TrainingProcessedWithoutClass[i][counterTraining] = TrainingpreprocessedWithClass[i][j];
                        counterTraining++;
                    }
                    else {
                        TrainingClass[i] = TrainingpreprocessedWithClass[i][j];
                    }
                }
            }

            for(int i=0; i<testingSetSize;i++){
                int counterTesting =0;
                for(int j=0; j<numAttributes;j++){
                    if(j!=0){
                        TestingProcessedWithoutClass[i][counterTesting] = TestingpreprocessedWithClass[i][j];
                        counterTesting++;
                    }
                    else TestingClass[i] = TestingpreprocessedWithClass[i][j];
                }
            }


            ///////////////////////////////////////////////
            // Call the 3 algorithms

            // Artificial Neural Network
            System.out.println("");
            System.out.println("==========================================================================");
            System.out.println("ARTIFICIAL NEURAL NETWORK");
            System.out.println("Testing the ANN on training instances set.");
            ANN trainingANN = new ANN(TrainingProcessedWithoutClass);
            trainingANN.train(TrainingProcessedWithoutClass, TrainingClass, 10);
            
            System.out.println();
            System.out.println("==========================================================================");

            System.out.println("Testing the ANN on testing instances set.");
            System.out.println("Prediction value \t Actual Classification from data set");
            for(int p=0; p<57; p++){
                String classString ="";
                String predictString ="";
                if(TestingpreprocessedWithClass[p][0]==0) classString="no-recurrence-events";
                else classString="recurrence-events";
                if(trainingANN.predict(TestingProcessedWithoutClass[p])==0)
                    predictString = "0 no-recurrence-events";
                else predictString = "1 recurrence-events";
                System.out.println(predictString+ " \t "+ classString);
            }
            

            //Genetic Programming
            System.out.println(" ");
            System.out.println("==========================================================================");
            System.out.println("GENETIC PROGRAMMING CLASSIFICATION ALGORITHM");
            System.out.println("Testing the GP on training instances set.");
            GP gpTraining = new GP();
            gpTraining.train(TestingProcessedWithoutClass, TestingClass);
            System.out.println("==========================================================================");
            System.out.println("Testing the GP on testing instances set.");
            System.out.println("Prediction value \t Actual Classification from data set");
            for(int p=0; p<57; p++){
                String classString ="";
                String predictString ="";
                if(TestingpreprocessedWithClass[p][0]==0) classString="no-recurrence-events";
                else classString="recurrence-events";
                if(gpTraining.predictClassNew(TestingProcessedWithoutClass[p])==0)
                    predictString = "0 no-recurrence-events";
                else predictString = "1 recurrence-events";
                System.out.println(predictString+ " \t "+ classString);
            }


            //Decision Tree
            System.out.println(" ");
            System.out.println("C4.5 DECISION TREE");
            System.out.println("This is visible in the report, and was done via the Weka tool.");
    

        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Helper function in order to count the number of lines in the text file
    public static int countInstances(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            int numInstances = 0;
            while (reader.readLine() != null) {
                numInstances++;
            }
            return numInstances;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////

}