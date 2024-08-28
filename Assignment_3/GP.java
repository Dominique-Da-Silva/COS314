// Dominique da Silva

import java.util.Random;
import java.util.ArrayList;

public class GP {

    private int population_size = 100; // this parameter was given in the spec
    private int generations_num = 50; // this parameter was given in the spec
    private double mutation_rate = 0.02; // probability of mutation
    private double crossover_rate = 0.6; // probability of cross over 
    private Random random;
    public DecisionTree bestTree; // Best decision tree found during evolution

    String[][] categoricalMappings = {
        //{"no-recurrence-events", "recurrence-events"},  // Class attribute
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

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public GP(){
        random = new Random(1234); 
    }



    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public class DecisionTree {
        private String condition; // e.g., "x1 <= 0.5"
        private DecisionTree left; // branch for condition being true
        private DecisionTree right; // branch for condition being false
        private boolean isLeaf; // flag to indicate that it is a leaf node
        private int classification; // only relevant if this is a leaf node. classification value, since it is only at the end of the tree that we get the class
    
        // create a non-leaf node
        public DecisionTree(String condition, DecisionTree left, DecisionTree right) {
            this.condition = condition;
            this.left = left;
            this.right = right;
            this.isLeaf = false;
        }
    
        // create a leaf node
        public DecisionTree(int classification) {
            this.isLeaf = true;
            this.classification = classification;
        }

        // convert the decision tree to a string representation such that we'd be able to read it
        public String toString() {
            return toStringHelper(this, 0);
        }
        
        private String toStringHelper(DecisionTree node, int indentLevel) {
            StringBuilder sb = new StringBuilder();
            String indent = "\t".repeat(indentLevel);
        
            if (node.isLeaf) {
                sb.append(indent).append("Class: ").append(node.classification).append("\n");
            } else {
                sb.append(indent).append("Condition: ").append(node.condition).append("\n");
                sb.append(indent).append("True Branch:\n").append(toStringHelper(node.left, indentLevel + 1));
                sb.append(indent).append("False Branch:\n").append(toStringHelper(node.right, indentLevel + 1));
            }
        
            return sb.toString();
        }
    }



    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Method to train the genetic programming model
    public DecisionTree train(int[][] data, int[] classes){
        int numAttributes = data[0].length;
        int bestIndex = 0;

        // 1. Initialization - generate initial population
        // System.out.println("Generating random trees to initialise.");
        int maxDepth = 5;
        DecisionTree[] population = new DecisionTree[population_size];
        for (int i = 0; i < population_size; i++) {
            population[i] = generateRandomTree(numAttributes,0,maxDepth);
            // System.out.println("Initial population tree generation number "+i+" : " + population[i] .toString());
        }

        // System.out.println("Starting the Evolutionary process.");
        // 2. Evolution - generations of selection, crossover, and mutation
        for (int gen = 0; gen < generations_num; gen++) {
            // System.out.println("Applying Tournament Selection.");
            // 2.1. Selection - tournament selection is used here
            DecisionTree[] selected = new DecisionTree[population_size];
            for (int i = 0; i < population_size; i++) {
                selected[i] = tournamentSelection(population, data, classes);
                // System.out.println("Selected tree generation number after tournament selection "+ i +" : " + selected[i] .toString());
            }

            // System.out.println("Applying Crossover genetic operator.");
            // 2.2. Crossover - randomly select pairs for crossover
            for (int i = 0; i < population_size - 1; i += 2) {
                if (random.nextDouble() < crossover_rate) {
                    crossover(selected[i], selected[i + 1]);
                }
            }

            // System.out.println("Applying Mutation genetic operator.");
            // 2.3. Mutation - apply mutation with a certain probability
            for (int i = 0; i < population_size; i++) {
                if (random.nextDouble() < mutation_rate) {
                    mutate(selected[i], data);
                }
            }

            population = selected;

            // Evaluate the fitness of each individual in the population
            double[] fitness = new double[population_size];
            double totalAccuracy = 0.0;
            double bestFitness = -1.0;

            for (int i = 0; i < population_size; i++) {
                double accuracy = evaluateAccuracy(population[i], data, classes);
                fitness[i] = accuracy;
                totalAccuracy += accuracy;

                if (accuracy >= bestFitness) {
                    bestFitness = accuracy;
                    bestIndex = i;
                    bestTree = population[bestIndex];
                }
            }

            // Print the best decision tree and average accuracy
            System.out.println("Generation " + (gen + 1) + ":");
            System.out.println("Best Decision Tree: " + population[bestIndex].toString());
            System.out.println("Average Accuracy: " + (totalAccuracy / population_size));
            System.out.println("---------------------------------------------"+"\n");

        }

        // Return the best decision tree
        System.out.println("The best tree: \n"+bestTree.toString());
        System.out.println("---------------------------------------------"+"\n");
        return population[bestIndex];
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /*private DecisionTree generateRandomTree(int numAttributes, int currentDepth, int maxDepth) {
        double leafProbability = 0.3; // Probability of a node being a leaf node
        
        if (currentDepth >= maxDepth || random.nextDouble() < leafProbability) {
            // Generate a leaf node
            int classification = random.nextInt(2); // Assuming binary classification (0 or 1)
            return new DecisionTree(classification);
        } else {
            // Generate a non-leaf node
            int attributeIndex = random.nextInt(numAttributes);
            double threshold = random.nextDouble();
            // hence, x1 maps to age attribute and so forth, down the list
            // x1==>age
            // x2==>menopause
            // x3==>tumor-size
            // x4==>inv-nodes
            // x5==>node-caps
            // x6==>deg-malig
            // x7==>breast
            // x8==>breast-quad
            // x9==>irradiat
            String condition = "x" + (attributeIndex + 1) + " <= " + threshold;
            
            DecisionTree left = generateRandomTree(numAttributes, currentDepth + 1, maxDepth);
            DecisionTree right = generateRandomTree(numAttributes, currentDepth + 1, maxDepth);
            
            return new DecisionTree(condition, left, right);
        }

    }*/

    private DecisionTree generateRandomTree(int numAttributes, int currentDepth, int maxDepth) {
        double leafProbability = 0.3; // Probability of a node being a leaf node
    
        if (currentDepth >= maxDepth || random.nextDouble() < leafProbability) {
            // Generate a leaf node
            int classification = random.nextInt(2); // Assuming binary classification (0 or 1)
            return new DecisionTree(classification);
        } else {
            // Generate a non-leaf node
            int attributeIndex = random.nextInt(numAttributes);
            int numCategories = categoricalMappings[attributeIndex].length;
            int categoryIndex = random.nextInt(numCategories);
            String condition = "x" + (attributeIndex + 1) + " == \"" + categoricalMappings[attributeIndex][categoryIndex] + "\"";
    
            DecisionTree left = generateRandomTree(numAttributes, currentDepth + 1, maxDepth);
            DecisionTree right = generateRandomTree(numAttributes, currentDepth + 1, maxDepth);
    
            return new DecisionTree(condition, left, right);
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private DecisionTree tournamentSelection(DecisionTree[] population, int[][] data, int[] classes) {
        // we want to select individuals with a higher fitness to be used in mutation and crossover
        // we want to ensure better solutions and such that we have a higher chance of being preserved and propagated in subsequent generations
        int tournamentSize = 4; // Set the tournament size

        DecisionTree winner = null;
        double bestFitness = -1.0;

        for (int i = 0; i < tournamentSize; i++) {
            int randomIndex = random.nextInt(population_size);
            DecisionTree individual = population[randomIndex];
            double fitness = evaluateAccuracy(individual, data, classes);

            if (fitness > bestFitness) {
                bestFitness = fitness;
                winner = copyTree(individual); // Create a copy of the selected individual
            }
        }

        return winner;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void crossover(DecisionTree tree1, DecisionTree tree2) {
        // Perform subtree crossover
        DecisionTree offspring1 = crossoverSubtree(tree1);
        DecisionTree offspring2 = crossoverSubtree(tree2);

        // Replace the original trees with the offspring trees
        tree1.condition = offspring1.condition;
        tree1.left = offspring1.left;
        tree1.right = offspring1.right;
        tree1.isLeaf = offspring1.isLeaf;
        tree1.classification = offspring1.classification;

        tree2.condition = offspring2.condition;
        tree2.left = offspring2.left;
        tree2.right = offspring2.right;
        tree2.isLeaf = offspring2.isLeaf;
        tree2.classification = offspring2.classification;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private DecisionTree crossoverSubtree(DecisionTree tree) {
        // Create a copy of the tree
        DecisionTree copiedTree = copyTree(tree);
    
        // Randomly select a subtree from the copied tree
        DecisionTree subtree = getRandomSubtree(copiedTree);
    
        // Create a deep copy of the selected subtree
        DecisionTree copiedSubtree = copyTree(subtree);
    
        return copiedSubtree;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private DecisionTree copyTree(DecisionTree tree) {
        if (tree.isLeaf) {
            return new DecisionTree(tree.classification);
        } else {
            DecisionTree left = (tree.left != null) ? copyTree(tree.left) : null;
            DecisionTree right = (tree.right != null) ? copyTree(tree.right) : null;
            return new DecisionTree(tree.condition, left, right);
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private DecisionTree getRandomSubtree(DecisionTree tree) {
        // Perform depth-first search traversal to find a random subtree
        ArrayList<DecisionTree> nodes = new ArrayList<>();
        depthFirstSearch(tree, nodes);
    
        // Randomly select a subtree from the list of nodes
        int randomIndex = random.nextInt(nodes.size());
        return nodes.get(randomIndex);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void depthFirstSearch(DecisionTree node, ArrayList<DecisionTree> nodes) {
        if (node == null) {
            return;
        }
        nodes.add(node);
        depthFirstSearch(node.left, nodes);
        depthFirstSearch(node.right, nodes);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void mutate(DecisionTree tree, int[][] data) {
        mutateSubtree(tree, data);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void mutateSubtree(DecisionTree node, int[][] data) {
        if (node == null) {
            return;
        }
    
        if (random.nextDouble() < mutation_rate) {
            if (node.isLeaf) {
                // Generate a new leaf node with a random classification
                int newClassification = random.nextInt(2); // Assuming binary classification, modify as needed
                node.classification = newClassification;
            } else {
                // Generate a new non-leaf node with a random condition and subtrees
                int numAttributes = data[0].length;
                int currentDepth = 0;
                int maxDepth = 5; // Adjust the maximum depth as needed
                DecisionTree newSubtree = generateRandomTree(numAttributes, currentDepth, maxDepth);
                node.condition = newSubtree.condition;
                node.left = newSubtree.left;
                node.right = newSubtree.right;
                node.isLeaf = newSubtree.isLeaf;
                node.classification = newSubtree.classification;
            }
        } else {
            mutateSubtree(node.left, data);
            mutateSubtree(node.right, data);
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public double evaluateAccuracy(DecisionTree tree, int[][] data, int[] classes) {
        int correctPredictions = 0;
    
        for (int i = 0; i < data.length; i++) {
            int predictedClass = predictClass(tree, data[i]);
            if (predictedClass == classes[i]) {
                correctPredictions++;
            }
        }
    
        return (double) correctPredictions / data.length;
    }
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // This is a prediction function when training the data to see whether we could evaluate correctly
    /*public int predictClass(DecisionTree tree, int[] instance) {
        if (tree.isLeaf) {
            return tree.classification;
        } else {
            // Extract the attribute index from the condition string
            int attributeIndex = Integer.parseInt(tree.condition.split("x")[1].split(" ")[0]) - 1;
    
            if (instance[attributeIndex] <= Double.parseDouble(tree.condition.split("<= ")[1])) {
                return predictClass(tree.left, instance);
            } else {
                return predictClass(tree.right, instance);
            }
        }
    }*/

    public int predictClass(DecisionTree tree, int[] instance) {
        if (tree.isLeaf) {
            return tree.classification;
        } else {
            // Extract the attribute index from the condition string
            int attributeIndex = Integer.parseInt(tree.condition.split("x")[1].split(" ")[0]) - 1;
    
            // Get the categorical value for the attribute index
            String categoricalValue = categoricalMappings[attributeIndex][instance[attributeIndex]];
    
            if (categoricalValue.equals(tree.condition.split(" == ")[1].replace("\"", ""))) {
                return predictClass(tree.left, instance);
            } else {
                return predictClass(tree.right, instance);
            }
        }
    }
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /*public int predictClassNew(int[] instance) {
        // This function does the prediction for new unseen instances
        if (this.bestTree.isLeaf) {
            return this.bestTree.classification;
        } else {
            // Extract the attribute index from the condition string
            int attributeIndex = Integer.parseInt(this.bestTree.condition.split("x")[1].split(" ")[0]) - 1;
    
            if (instance[attributeIndex] <= Double.parseDouble(this.bestTree.condition.split("<= ")[1])) {
                return predictClass(this.bestTree.left, instance);
            } else {
                return predictClass(this.bestTree.right, instance);
            }
        }
    }*/

    public int predictClassNew(int[] instance) {
        if (this.bestTree.isLeaf) {
            return this.bestTree.classification;
        } else {
            // Extract the attribute index from the condition string
            int attributeIndex = Integer.parseInt(this.bestTree.condition.split("x")[1].split(" ")[0]) - 1;
    
            // Get the categorical value for the attribute index
            String categoricalValue = categoricalMappings[attributeIndex][instance[attributeIndex]];
    
            if (categoricalValue.equals(this.bestTree.condition.split(" == ")[1].replace("\"", ""))) {
                return predictClass(this.bestTree.left, instance);
            } else {
                return predictClass(this.bestTree.right, instance);
            }
        }
    }

}
