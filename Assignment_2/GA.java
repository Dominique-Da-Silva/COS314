import java.util.Random;

public class GA {

    /*
     * OUTLINE OF STEPS FOR THE GENETIC ALGORITHM
     */
    // 1 Create Initial Population
    // 2 Calculate the fitness of all the individuals
    // 3 While the termination condition is not met
    // 4        Select fitter individuals for reproduction
    // 5        Recombine individuals
    // 6        Mutate individuals
    // 7        Evaluate fitness of all individuals
    // 8        Generate a new population
    // 9 End while
    // 10 Return the best individual



    private static final int MAX_GENERATIONS = 10000;
    private static final double CROSSOVER_RATE = 0.6;
    private static final double MUTATION_RATE = 0.01;
    private static final int TOURNAMENT_SIZE = 4;
    private Random random = new Random();
    public double[] weights = null;
    public double[] values = null;
    public double maxWeight = 0;
    public int numItems = 0;
    public int populationSize = 200;

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    public GA(double[] weights, double[] values, double maxWeight) {
        this.maxWeight = maxWeight;
        this.values = values;
        this.weights = weights;
        this.numItems = weights.length;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    public double runGA() {
        //GENERATE AN INITIAL POPULATION
        int[][] population = generateInitialPopulation();
        //CALCULATE THE FITNESS OF ALL THE INDIVIDUALS
        //TERMINATION CONDITION
        for (int generation = 0; generation < MAX_GENERATIONS; generation++) {
            int[][] newPopulation = new int[populationSize][numItems];

            for (int i = 0; i < populationSize; i++) {
                //SELECT FITTER INDIVIDUALS FOR REPRODUCTION
                int[] parent1 = selectParent(population);
                int[] parent2 = selectParent(population);
                // RECOMBINE INDIVIDUALS (CROSSOVER)
                int[] child = crossover(parent1, parent2);
                // MUTATE INDIVIDUALS
                mutate(child);
                //CALCULATE THE FITNESS OF ALL THE INDIVIDUALS
                // GENERATE A NEW POPULATION
                newPopulation[i] = child;
            }

            population = newPopulation;
        }
        // RETURN THE BEST INDIVIDUA
        return getBestFitness(population);
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    private int[][] generateInitialPopulation() {
        //General solution is created in order to start us off
        int[][] population = new int[populationSize][numItems];

        for (int i = 0; i < populationSize; i++) {
            int[] individual = new int[numItems];

            for (int j = 0; j < numItems; j++) {
                //Randomly select which items are included and which are not
                //1 ==> in the knapsack
                //0 ==> not contained within the knapsack
                individual[j] = Math.random() < 0.5 ? 1 : 0;
            }

            //Seperate function to ensure that we do not exceed the max capacity
            maxWeight(individual); 

            population[i] = individual;
        }

        return population;
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //Calculating the maximum weight of the current chromosome, should not exceed the instance's
    // maximum value that has been specified, otherwise it's an invalid solution
    private void maxWeight(int[] individual) {
        double totalWeight = 0;

        for (int i = 0; i < numItems; i++) {
            if (individual[i] == 1) {
                totalWeight += weights[i];
            }
        }

        //if the total weight exeeds our given limit, we need to remove some items, we'll just do this in order
        // work from the last inserted item, i.e. from the back of the array with a binary value of 1
        // remove, i.e. binary value of 0 for that specific item
        // remove the item's  weight from the total
        // if the weight is within limit, then we break otherwise we just repeat the process
        if (totalWeight > maxWeight) {
            for (int i = numItems - 1; i >= 0; i--) {
                if (individual[i] == 1) {
                    individual[i] = 0;
                    totalWeight -= weights[i];
                    if (totalWeight <= maxWeight) {
                        break;
                    }
                }
            }
        }
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    private int[] selectParent(int[][] population) {
        int[] best = null;

        for (int i = 0; i < TOURNAMENT_SIZE; i++) {
            int[] candidate = population[random.nextInt(populationSize)];
            if (best == null || fitness(candidate) > fitness(best)) {
                best = candidate;
            }
        }

        return best;
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    private int[] crossover(int[] parent1, int[] parent2) {

        //We have a 80% thus increasing the likelyhood of applying crossover and producing more diverse offspring
        //Helps introduce exploration
        //Exploitation is done through selection and mutation

        int[] child = new int[numItems];

        if (Math.random() < CROSSOVER_RATE) {
            int crossoverPoint = random.nextInt(numItems);

            for (int i = 0; i < numItems; i++) {
                child[i] = i < crossoverPoint ? parent1[i] : parent2[i];
            }
        }
        else {
            System.arraycopy(parent1, 0, child, 0, numItems);
        }

        maxWeight(child);
        return child;
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    private void mutate(int[] individual) {
        //This introduces diversity within the population
        //Bit flip in order to create a new individual
        //Prevents the search from pre-maturely converging

        for (int i = 0; i < numItems; i++) {
            if (Math.random() < MUTATION_RATE) {
                individual[i] = individual[i] == 0 ? 1 : 0;
            }
        }

        maxWeight(individual);
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    private double fitness(int[] individual) {
        double totalWeight = 0;
        double totalValue = 0;

        for (int i = 0; i < numItems; i++) {
            if (individual[i] == 1) {
                totalWeight += weights[i];
                totalValue += values[i];
            }
        }

        return totalWeight <= maxWeight ? totalValue : 0;
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    private double getBestFitness(int[][] population) {
        double bestFitness = 0;

        for (int i = 0; i < populationSize; i++) {
            double fitness = fitness(population[i]);
            if (fitness > bestFitness) {
                bestFitness = fitness;
            }
        }

        return bestFitness;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
