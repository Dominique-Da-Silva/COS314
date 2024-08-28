import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class knapsack{

    /*
     * Knapsack
     * Items have a weight and a value.
     * Want to fill the sack with a collection of items keeping 
     *      total weight in given limit
     *      maximizing the total value of the selected items
     * Selecting a subset n from set N
     *      n items have weight (w) and value (v)
     *      they have to fit into sack capacity of W
     */



    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static void main(String[] args) { 

        //going to the folder of the problem instances
        File folder = new File("Knapsack Instances");
        //reading all 10 instances of the knapsack problem
        File[] ProblemInstances = folder.listFiles();

        //iterating through the list of problem instances
        for(int i=0; i<ProblemInstances.length;i++){

            System.out.println("File name: " + ProblemInstances[i].getName() );

            //each item in the file will have a weight and a value
            //an item's attribute will correspond in the array on the index
            double[] weights = null;
            double[] values = null;

            try (BufferedReader br = new BufferedReader(new FileReader(ProblemInstances[i]))) {
                String line; //this is to capture the value and the weight of an item, i.e the line with data
                int lineNumber = 1; //this is just to be able to get the number of items in the list and max Weight of sack
                int numItems = 0;
                double maxWeight = 0; 
                while ((line = br.readLine()) != null) {
                    //keep reading from the textfile while there is something to read
                    if (lineNumber == 1) {
                        //we are on the first line of the text file
                        String[] firstLine = line.split(" ");
                        //first element of the created array in the number of items in the textfile for the sack
                        numItems = Integer.parseInt(firstLine[0]);
                        maxWeight  = Double.parseDouble(firstLine[1]);
                        //now we know the size of the two arrays from the first line
                        weights = new double[numItems];
                        values = new double[numItems];
                    } else {
                        String[] itemLine = line.split(" ");
                        values[lineNumber - 2] = Double.parseDouble(itemLine[0]);
                        weights[lineNumber - 2] = Double.parseDouble(itemLine[1]);
                    }
                    lineNumber++;
                }


                if(weights.length!=numItems  || values.length!=numItems){
                    System.out.println("There is a problem with reading the text file since the item number don't match up.");
                }


                // Run Genetic Algorithm on the problem instance
                runGeneticAlgorithm(weights, values, maxWeight );

                // Run Ant Colony Optimization on the problem instance
                runAntColonyOptimization(weights, values, maxWeight );

            } catch (IOException e) {
                System.err.println("Error reading file: " + ProblemInstances[i].getName());
                e.printStackTrace();
            }

        }

 
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static void runGeneticAlgorithm(double[] weights, double[] values, double maxWeight ) {
        long startRuntime = System.currentTimeMillis();
        
        //calling the Genetic Algorithm
        GA geneticAlgo = new GA(weights, values, maxWeight);
        double GAoptimal = geneticAlgo.runGA();
        System.out.println("GA optimal: "+ GAoptimal);

        long endRuntime = System.currentTimeMillis(); 
        long Runtime = (endRuntime-startRuntime)/1000;
        System.out.println("GA Runtime: "+Runtime);
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////


    public static void runAntColonyOptimization(double[] weights, double[] values, double maxWeight) {
        long startRuntime = System.currentTimeMillis();
        
        //calling the Ant Colony Optimization Algorithm
        ACO antcoloptAlgo = new ACO(weights, values, maxWeight);
        double ACOoptimal = antcoloptAlgo.runACO();
        System.out.println("ACO optimal: "+ ACOoptimal);


        long endRuntime = System.currentTimeMillis(); 
        long Runtime = (endRuntime-startRuntime)/1000;
        System.out.println("ACO Runtime: "+Runtime);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static double[] resizeArray(double[] arr, int newSize) {
        double[] newArr = new double[newSize];
        System.arraycopy(arr, 0, newArr, 0, Math.min(arr.length, newSize));
        return newArr;
    }
}