import java.util.List;
import java.util.ArrayList;
import java.util.Random;

/*
 * ILS can get stuck at local minimum
 * 
 * Iterated Local Search:
 *      1. Generate and initial solution using constructive heuristic or random method.
 *          Most likely will use first fit in order go generate our initial solution
 *      2. Apply local search algorithm to improve the solution
 *      3. Obtain local optimum
 *      4. Perturb the local optimum to obtain a new solution
 *          Methods like shaking, randomization and mutation.
 *      5. Apply the local search algorithm to the new solution
 *      6. Iff new solution better than curr, update curr 
 * 
 * First fit bin packing:
 *      Keeps list of open bins that are initially empty
 *      When an item arrives, find the FIRST bin into which the item can fit, if there is any
 *      Otherwise a new bin is opened and the coming item is placed inside it
 * 
 * Best fit bin packaging:
 *      Keeps a list of bins that are initially empty
 *      When the item arrives, find the bin with the maximum load into which the item can fit, if any
 *          If such a bin is found, place the new item inside it
 *          Otherwise, new bin is opened and the coming item is placed inside it
 */



public class IteratedLocalSearch{


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private int binCapacity; //this is our bincapacity
    private List<Integer> itemList; //the list of entries of the textfile
    private Random random = new Random();


    //variables for our chosen heuristic
    private List<Integer> binsSizes; //we don't know how many bins we'll be using this use a list
    private List<List<Integer>> bins; //we have a list, but each entry in the list, i.e. a singular bin is going to contain another list of items in the bin that we allocated
    //this is essentially what we are going to calculate our optimal solution
    //new singular bin: List<Integer> newBin = new ArrayList<>();

    //we want a maximum number of iterations as our stopping condition
    //since we have no idea how to determine when we reach an optimal solution
    //unless we use the excel spreadsheet, but assume that wasn't given

    private static final int maxIterations = 1000;
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    public IteratedLocalSearch(int binCapacity, List<Integer> itemList) {
        this.binCapacity = binCapacity;
        this.itemList = itemList;
        this.bins = new ArrayList<>();
        this.binsSizes = new ArrayList<>();
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public int ILSoptimal(){

        /*
        * STEP 1
        */
        //in order to return the optimal solution
        bins = firstFitHeuristic(itemList, binCapacity);
        //after our initial solution has been calculated, we want the bin sizes
        binsSizes = BinSizes(bins);


        int InitialOptimal = bins.size();
        //System.out.println("\t"+"\t"+"Initial Optimal Value: "+InitialOptimal);

        /*
        * STEP 2
        */
        //using a fixed number of iterations as the stopping citeria for the local search
        //could change  to specific level of improvement or a timeout
        for(int iteration=0; iteration<maxIterations;iteration++){
            /*
            * STEP 3
            * Obtaining the local optimum
            * The improved solution
            * First do local search to improve solution
            */
            //System.out.println("Starting the Local Optima function.");
            List<List<Integer>> LocalOptimum = localOptima(bins, binsSizes);

            this.binsSizes.clear();
            this.binsSizes = BinSizes(LocalOptimum);

            /*
            * STEP 4
            * Perturb to obtain new solution
            */

            //System.out.println("Starting the perturbution process.");
            //List<List<Integer>> perturbedCandidate = Perturb(LocalOptimum, binsSizes);
            //this.binsSizes.clear();
            //this.binsSizes = BinSizes(perturbedCandidate);

            List<List<Integer>> bestNeighbor = LocalOptimum;
            int bestCost = InitialOptimal;
            for(int i = 0; i < bins.size(); i++) {
                for(int j = 0; j < bins.size(); j++) {
                    if(i == j) {
                        continue;
                    }
                    List<List<Integer>> neighbor = swapItems(LocalOptimum, i, j);
                    int neighborCost = neighbor.size();
                    if(neighborCost < bestCost) {
                        bestNeighbor = neighbor;
                        bestCost = neighborCost;
                    }
                }
            }

            /*
            * STEP 5
            * Local Search to new solution
            */

            //List<List<Integer>> LocalSearchedSolution = LocalSearch(perturbedCandidate, binsSizes);
            //this.binsSizes.clear();
            //this.binsSizes = BinSizes(LocalSearchedSolution);

           if(bestCost < InitialOptimal) {
                bins = bestNeighbor;
                binsSizes = BinSizes(bins);
                InitialOptimal = bestCost;
            }

            /*
            * STEP 6
            * Compare and update
            */

            //int UpdatedOptimal = LocalSearchedSolution.size();
            //if(UpdatedOptimal<InitialOptimal){
                //bins = LocalSearchedSolution;
                //InitialOptimal = UpdatedOptimal;
            //}
        }
        return InitialOptimal;
    }


//FUNCTION/HEURISTIC TO GENERATE INITIAL SOLUTION
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private List<List<Integer>> firstFitHeuristic(List<Integer> itemList, int binCapacity) {
        //using the first fit heuristic to generate an initial condition
        //chose first fit instead of best fit, since it's big O complexity is smaller
        //o(nlogn) instead of a O(n^2) since we don't need to compare all the items in the bin and move items in the bins
        //this leads to faster iterations and possibly faster convergence

        List<List<Integer>> bins = new ArrayList<>();
        boolean inserted;

        for(int i=0; i<itemList.size();i++) {
            //loop through every item in the list
            int item = itemList.get(i);
            inserted = false;
            //the item is yet to be inserted into a bin within the list
            List<Integer> currentBin;
            //create variable to check the current bins valuses
            for (int b=0; b< bins.size();b++) {
                currentBin = bins.get(b);
                //get the current items in the bin and calculate the current capacity of the bin
                int currentBinCapacity=0;
                for(int c=0; c<currentBin.size();c++){
                    currentBinCapacity=currentBinCapacity+currentBin.get(c);
                }
                if(currentBinCapacity+item<=binCapacity){
                    currentBin.add(item);
                    inserted = true;
                    break;
                }
            }
            if (inserted==false) {
                //there was no existing bin that we could insert into, so we should create a new bin
                List<Integer> newBin = new ArrayList<>();
                newBin.add(item);
                bins.add(newBin);
            }
        }
        return bins;
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private List<Integer> BinSizes(List<List<Integer>> bins) {
        List<Integer> BinsSizes = new ArrayList<>();
        List<Integer> currentBin;
        for(int i=0; i<bins.size();i++){
            currentBin = bins.get(i);
            int binCapacity =0;
            for(int j=0; j<currentBin.size(); j++){
                binCapacity = binCapacity + currentBin.get(j);
            }
            BinsSizes.add(binCapacity);
        }
        return BinsSizes;
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private List<List<Integer>> swapItems(List<List<Integer>> solution, int bin1, int bin2) {
        List<List<Integer>> newSolution = new ArrayList<>(solution);
        List<Integer> bin1Items = newSolution.get(bin1);
        List<Integer> bin2Items = newSolution.get(bin2);
        if(bin1Items.isEmpty() || bin2Items.isEmpty()) {
            return newSolution;
        }
        int index1 = random.nextInt(bin1Items.size());
        int index2 = random.nextInt(bin2Items.size());
        int item1 = bin1Items.get(index1);
        int item2 = bin2Items.get(index2);
        bin1Items.set(index1, item2);
        bin2Items.set(index2, item1);
        return newSolution;
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private List<List<Integer>> Perturb(List<List<Integer>> solution,List<Integer> binsSizes) {
        /*
            * we are searching for other candidate solutions in the solution space
            * iteratively extending partial candidate solutions
            * we want to introduce diversity in order to avoid a local optima
            * we are going to remove items from bins and reintroduce them in a different order
            * if the bin only has one item, we leave as is and just add to the new solution
            * however, if that is not the case we just swap a random number of items 
            * we do this, because is we should only swap or remove and replace one item from one bin into another we are likely to end up with similar solutions
            * this would also make it more difficult to escape from a local optima
            * the random number of perturbutations ensures a unique solution
        */

        List<List<Integer>> newSolution = new ArrayList<>();

        List<Integer> currentbin;
        for(int b=0; b<solution.size();b++){
            currentbin = solution.get(b);
            if(currentbin.size()>1){
                int removeNumber = random.nextInt(currentbin.size()-1)+1; // (bo [size-1] - onder [0]) +1
                List<Integer> removeList = new ArrayList<>();

                for(int r=0; r<removeNumber;r++){
                    int removeIndex = random.nextInt(currentbin.size());
                    removeList.add(currentbin.get(removeIndex));
                    currentbin.remove(removeIndex);
                }

                List<Integer> newBin = new ArrayList<>(removeList);
                newBin.addAll(currentbin);
                newSolution.add(newBin);
            }
            else{
                newSolution.add(currentbin);
            }
        }

        return newSolution;


    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private List<List<Integer>> localOptima(List<List<Integer>> solution, List<Integer> binsSizes){
        /*
         * Moving items between bins until we cannot move anymore, or any better moves, we get stuck
         * Although a solution might still exist on an alternative path
         * 
         * Iterate until an optima is reached
         * Select two random bins
         * Check that they are indeed two seperate bins 
         */

        boolean optimaReached = false;
        int iterations=0; //just added the counter in order to avoid an infinite loop
        while(optimaReached==false && iterations<100){
            iterations++;
            //System.out.println("Entering the while loop.");

            int randFirstIndex = random.nextInt(bins.size());
            int randSecondIndex = random.nextInt(bins.size());
            
            if(randFirstIndex!=randSecondIndex){
                //System.out.println("The two bins differ.");
                int OneBinCapacity=0;
                int TwoBinCapacity=0;

                List<Integer> FirstBin = bins.get(randFirstIndex);
                List<Integer> SecondBin = bins.get(randSecondIndex);

                OneBinCapacity = binsSizes.get(randFirstIndex);
                TwoBinCapacity = binsSizes.get(randSecondIndex);

                for(int b=0; b<FirstBin.size();b++){
                    int item = FirstBin.get(b);
                    int SecondBinWaste = binCapacity-TwoBinCapacity;
                    if(item<=SecondBinWaste){
                        //thus the item can fit in the second bin
                        //remove from the first bin
                        //place in the second bin
                        //update the bin sizes
                        FirstBin.remove(b);
                        SecondBin.add(item);

                        binsSizes.set(randFirstIndex,binsSizes.get(randFirstIndex)-item);
                        binsSizes.set(randSecondIndex,binsSizes.get(randSecondIndex)+item);

                        optimaReached=true;
                        break;
                    }
                }

                //assuming that the item could not be shifted from the first bin into the second bin
                    //iterate through the second bin
                    if(optimaReached==false){
                        for(int b2=0; b2< SecondBin.size();b2++){
                            int item2 =SecondBin.get(b2);
                            int FirstBinWaste = binCapacity-OneBinCapacity;
                            if(item2<=FirstBinWaste) {
                                SecondBin.remove(b2);
                                FirstBin.add(item2);
                                
                                binsSizes.set(randSecondIndex, binsSizes.get(randSecondIndex)-item2);
                                binsSizes.set(randFirstIndex, binsSizes.get(randFirstIndex)+item2);
                                
                                optimaReached=true;
                                break;
                            }
                        }
                    }
                
            }
        }
        return bins;
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private List<List<Integer>> LocalSearch(List<List<Integer>> bins, List<Integer> binsSizes){
        /*
         * Similar to searching for the local optima
         * Now we are just using the perturbed solution
         * We continue until no further improvements can be found or max number of iterations
         */


        boolean improvementFound = true;
        int iterations = 0;
        while (improvementFound && iterations < 100) {
            iterations++;
            improvementFound = false;

            for (int randFirstIndex = 0; randFirstIndex < bins.size(); randFirstIndex++) {
                for (int randSecondIndex = 0; randSecondIndex < bins.size(); randSecondIndex++) {

                    if (randFirstIndex != randSecondIndex) {
                        int oneBinCapacity = binsSizes.get(randFirstIndex);
                        int twoBinCapacity = binsSizes.get(randSecondIndex);

                        List<Integer> firstBin = bins.get(randFirstIndex);
                        List<Integer> secondBin = bins.get(randSecondIndex);

                        for (int b = 0; b < firstBin.size(); b++) {
                            int item = firstBin.get(b);
                            int secondBinWaste = binCapacity - twoBinCapacity;
                            if (item <= secondBinWaste) {
                                firstBin.remove(b);
                                secondBin.add(item);

                                binsSizes.set(randFirstIndex, binsSizes.get(randFirstIndex) - item);
                                binsSizes.set(randSecondIndex, binsSizes.get(randSecondIndex) + item);

                                improvementFound = true;
                                break;
                            }
                        }

                        if (!improvementFound) {
                            for (int b2 = 0; b2 < secondBin.size(); b2++) {
                                int item2 = secondBin.get(b2);
                                int firstBinWaste = binCapacity - oneBinCapacity;
                                if (item2 <= firstBinWaste) {
                                    secondBin.remove(b2);
                                    firstBin.add(item2);

                                    binsSizes.set(randSecondIndex, binsSizes.get(randSecondIndex) - item2);
                                    binsSizes.set(randFirstIndex, binsSizes.get(randFirstIndex) + item2);

                                    improvementFound = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        return bins;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}