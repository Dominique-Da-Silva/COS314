public class ACO {


    /*
     * ANT COLONY OPTIMIZATION
     * 
     * The parameters:
     *      Number of Ants
     *      Pheromone Evaporation Rate
     *      Pheromone Intensity
     *      Heuristic Information
     *      Ant Decision rule
     *      Local Search Strategy
     *      Termination Criteria
     */


    
    private static final int MAX_ITERATIONS = 10000;
    public double[] weights = null;
    public double[] values = null;
    public double W = 0;
    public int numItems;
    public int numAnts=0;
    private double[][] pheromone;
    private double alpha = 1.0; //Pheromone trails will have equal importance to the heuristic information in the decision making process
    private double beta = 2.0; //The heuristic information will have twice the importance compared to pheromone trails in the decision making process
    private double evaporationRate = 0.1; //10% of the pheromone trails will evaporate over each iteration
    private double Q = 100.0;
    

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //Initialisation
    public ACO(double[] weights, double[] values, double maxWeight){
        this.W = maxWeight;
        this.values = values;
        this.weights = weights;
        this.numItems = weights.length;
        this.numAnts= 500;
        this.pheromone = new double [numItems][2]; //the item's heuristic value and pheromone trail is stored
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public double runACO(){
        
        int[] bestSolution = null;
        double bestFitness =0;


        //INTIALISE THE PHEROMONE TRAILS AND PARAMETERS
        initialisePheromones();

        
        //WHILE TERMINATION CONDITION HAS NOT BEEN MET
        int generationNum =0;
        while(generationNum<MAX_ITERATIONS){

            int[][] antSolutions = new int[numAnts][numItems];

            //FOR EACH ANT DO
            for(int antNum=0; antNum<numAnts;antNum++){
                //CONSTRUCT A SOLUTION USING PHEROMONE TRAILS AND HEURISTIC INFORMATION
                antSolutions[antNum] = constructSolution();

                //EVALUATE THE INFORMATION 
                double fitness = evaluateSolution(antSolutions[antNum]);
                //System.out.println("Fitness value: " + fitness);
                if (fitness > bestFitness) {
                    bestFitness = fitness;
                    bestSolution = antSolutions[antNum];
                }

                //UPDATE THE PHEROMONE TRAILS BASED ON THE QUALITY OF THE SOLUTION
                updateLocalPheromone(antNum, antSolutions[antNum], fitness);

                //System.out.println("Solution fitness: "+evaluateSolution(antSolutions[antNum]));

            }

            //UPDATE THE PHEROMONES GLOBALLY
            updateGlobalPheromone(bestSolution, bestFitness);

            generationNum++;
        }

        //RETURN THE BEST SOLUTION FOUND
        bestFitness = evaluateSolution(bestSolution);
        return bestFitness;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public double[] HeuristicInformationItems(){
        //Value to weight ratio of each item
        //Higher ratio indicates a better choice as it gives more value for less weight
        //This will help guide ants to select items that are likely to result in higher value solutions
        double[] HeuristicInfo = new double[numItems];
        for(int i=0; i<numItems;i++){
            double ratio = values[i]/weights[i];
            HeuristicInfo[i] = ratio;
        }
        return HeuristicInfo;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void initialisePheromones(){
        //All pheramone trails are initialised with the same intensity
        for(int i=0; i<numItems;i++){
            pheromone[i][1]=alpha;
            pheromone[i][0] = values[i]/weights[i];
        }
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public int[] constructSolution(){
        //Constructs a solution for a single ant
        //Calculates leave and take probabilities for each item based on the pheromone trails and heuristic information, and then selects the item with higher probability.
        //As always, weight limit must not be exceeded

        int[] solution = new int[numItems];
        double totalWeight = 0;

        // Calculate the sum of pheromone levels for each type of item
        double[] pheromoneSums = new double[numItems];
        for (int item = 0; item < numItems; item++) {
            pheromoneSums[item] = pheromone[item][0] + pheromone[item][1];
        }

        // Iterate over the items and select them based on their probabilities
        for (int item = 0; item < numItems; item++) {
            double rand = Math.random();
            double takeProbability = pheromone[item][1] / pheromoneSums[item];
            //double leaveProbability = pheromone[item][0] / pheromoneSums[item];

            if (rand < takeProbability && (totalWeight + weights[item]) <= W) {
                solution[item] = 1;
                totalWeight += weights[item];
            } else {
                solution[item] = 0;
            }
        }

        return solution;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public double evaluateSolution(int[] solution) {
        //Fitness of given solution is evaluated
        //Returns 0 if the max weight of the knapsack has been exceeded

        double totalValue = 0;
        double totalWeight = 0;

        for (int i = 0; i < numItems; i++) {
            if (solution[i] == 1) {
                totalValue += values[i];
                totalWeight += weights[i];
            }
        }

        if(totalWeight<=W) return totalValue;
        else return 0;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void updateLocalPheromone(int ant, int[] solution, double fitness){
        //Update the pheromone trail of each item based on the quality of the solution found by a single ant
        //If the item is selected, the pheromone trail is updated
        //If the item has not been selected then fitness value is updated

        for(int item=0; item<numItems;item++){
            double pheromoneChange = 0.0;
            if(solution[item]==1){
                pheromoneChange = fitness / Q;
            }
            else{
                pheromoneChange = -fitness / Q;
            }
            pheromone[item][1] = (1-evaporationRate) * pheromone[item][1] + evaporationRate * pheromoneChange;
            pheromone[item][0] = (1-evaporationRate) * pheromone[item][0] + evaporationRate * pheromoneChange;
        }

    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void updateGlobalPheromone(int[] bestsolution, double fitness){

        // Introduce an elitist strategy by depositing a higher amount of pheromone on the items selected in the best solution
        for (int item = 0; item < numItems; item++) {
            double pheromoneUpdate = 0.0;
            
            if (bestsolution[item] == 1) {
                // Deposit a higher amount of pheromone on the items selected in the best solution
                pheromoneUpdate = beta * (fitness / Q);
            } else {
                pheromoneUpdate = (fitness / Q);
            }
            
            // Update pheromone level for the current item
            pheromone[item][1] = (1 - evaporationRate) * pheromone[item][1] + evaporationRate * pheromoneUpdate;
            pheromone[item][0] = (1 - evaporationRate) * pheromone[item][0] + evaporationRate * pheromoneUpdate;
            }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
