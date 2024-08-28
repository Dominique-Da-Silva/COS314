import java.util.List;
import java.util.ArrayList;
import java.util.Random;

/*
 * Tabus Search Algorithm:
 *      Highly parameritised.
 *      Stopping criteria should be carefully selected.
 * 
 *      One is allowed to get out of local optima by choosing poorer solutions, but solutions visited earlier are not allowed to be visited again
 * 
 *      Set x=x0 (initial candidate solution)
 *      Set length(L) = z (maximum tabu list length)
 *      Set L={} (initialise the tabu list)
 *      repeat
 *      generate a random neighbor x'
 *      if x' not contained within L
 *          if length(L)>z then
 *              remove oldest solution from L
 *              set x' as an element of L
 *          end;
 *          end;
 *          if x' < x then
 *              x = x'
 *              end;
 *              until stopping criteria is met
 *              return x;
 * 
 * A greedy algorithm is an algorithmic strategy that makes the best optimal choice at each small stage with the goal of this eventually leading to a globally optimum solution. 
 * This means that the algorithm picks the best solution at the moment without regard for consequences
 */

public class TabuSearchAlgorithm {

    private int binCapacity;
    private List<Integer> itemList;
    private int ListSize;
    private List<List<Integer>> bins;
    private int tabuListLength;
    private static final int maxIterations = 1000;
    private List<List<Integer>> tabuList;


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    public TabuSearchAlgorithm (int binCapacity, List<Integer> itemList) {
        this.binCapacity = binCapacity;
        this.itemList = itemList;
        this.bins = new ArrayList<>();
        this.ListSize = itemList.size();
        this.tabuListLength = (int) Math.floor(ListSize*0.15); //said that the value should be between 10 and 20%
        this.tabuList = new ArrayList<>();
        //we don't want the algorithm to converge to quickly but we also don't want it to get stuck on a local optima
        //the tabuListLength is to help prevent revisiting previous solutions in aid in the exploration & exploitation process
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public int TABUoptimal() {
        this.bins = initial();
        //System.out.println("Called the initialiser for the bins.");
        //if(bins!=null) System.out.println("And it would seem that the bins 2D List is not empty.");
        int currentOptimal = bins.size();
        //System.out.println("This is the current optimal: "+currentOptimal);
        //System.out.println("\t"+"\t"+"Initial Optimal Value: "+currentOptimal);
        int iterations = 0;
        while (iterations < maxIterations) {
            //System.out.println("Entered the while loop.");
            List<List<Integer>> neighbor = neighborSolution(bins);
            if (!tabuList.contains(neighbor)) {
                if (tabuList.size() > tabuListLength) {
                    tabuList.remove(0);
                }
                tabuList.addAll(neighbor);
                bins = neighbor;
                int binsUsed = bins.size();
                if (binsUsed < currentOptimal) {
                    currentOptimal = binsUsed;
                }
            }
            iterations++;
        }
        return currentOptimal;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private List<List<Integer>> initial() {
        List<List<Integer>> bins = new ArrayList<>();
        int binIndex = 0;
        for (int i = 0; i < itemList.size(); i++) {
            int item = itemList.get(i);
            boolean itemPlaced = false;
            while (!itemPlaced) {
                if (binIndex == bins.size()) {
                    bins.add(new ArrayList<>());
                }
                List<Integer> currentBin = bins.get(binIndex);
                if (currentBin.stream().mapToInt(Integer::intValue).sum() + item <= binCapacity) {
                    currentBin.add(item);
                    itemPlaced = true;
                } else {
                    binIndex++;
                }
            }
        }
        return bins;
    }
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    
    private int currentBinCapacity(List<Integer> list) {
        int sum = 0;
        for (int item : list) {
            sum += item;
        }
        return sum;
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static List<List<Integer>> neighborSolution(List<List<Integer>> currentSolution) {
        // Create a new solution by swapping two items in two different bins
        List<List<Integer>> neighbor = new ArrayList<>(currentSolution);
    
        // Get the index of a random bin
        Random random = new Random();
        int binIndex1 = random.nextInt(neighbor.size());
    
        // Get the index of a random item in the first bin
        List<Integer> selectedBin = neighbor.get(binIndex1);
        if (selectedBin.isEmpty()) {
            // The selected bin is empty, cannot select an item from it
            return currentSolution;
        }
        int itemIndex1 = random.nextInt(selectedBin.size());
    
        // Get the index of a random bin that is different from the first bin
        int binIndex2 = random.nextInt(neighbor.size() - 1);
        if (binIndex2 >= binIndex1) {
            // Adjust the second bin index if it's equal to or greater than the first bin index
            binIndex2++;
        }
    
        // Get the index of a random item in the second bin
        selectedBin = neighbor.get(binIndex2);
        if (selectedBin.isEmpty()) {
            // The selected bin is empty, cannot select an item from it
            return currentSolution;
        }
        int itemIndex2 = random.nextInt(selectedBin.size());
    
        // Swap the selected items between the two bins
        int temp = neighbor.get(binIndex1).get(itemIndex1);
        neighbor.get(binIndex1).set(itemIndex1, neighbor.get(binIndex2).get(itemIndex2));
        neighbor.get(binIndex2).set(itemIndex2, temp);
    
        return neighbor;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /*private List<List<Integer>> neighborSolution(List<List<Integer>> currentSolution) {
        List<List<Integer>> neighbor = new ArrayList<>(currentSolution);
    
        // Choose two non-empty bins randomly
        Random random = new Random();
        int binIndex1 = -1, binIndex2 = -1;
        while (binIndex1 == binIndex2) {
            binIndex1 = random.nextInt(neighbor.size());
            binIndex2 = random.nextInt(neighbor.size());
        }
        List<Integer> bin1 = neighbor.get(binIndex1);
        List<Integer> bin2 = neighbor.get(binIndex2);
        if (bin1.isEmpty() || bin2.isEmpty()) {
            // At least one of the chosen bins is empty, cannot swap items
            return currentSolution;
        }
    
        // Choose two random items from each bin
        int itemIndex1 = random.nextInt(bin1.size());
        int itemIndex2 = random.nextInt(bin2.size());
        int item1 = bin1.get(itemIndex1);
        int item2 = bin2.get(itemIndex2);
    
        // Check if swapping the items will lead to a feasible solution
        int capacity1 = binCapacity - bin1.stream().mapToInt(Integer::intValue).sum() + item1;
        int capacity2 = binCapacity - bin2.stream().mapToInt(Integer::intValue).sum() + item2;
        if (capacity1 < item2 || capacity2 < item1) {
            // Swapping the items will result in at least one bin exceeding its capacity
            return currentSolution;
        }
    
        // Swap the items between the two bins
        bin1.set(itemIndex1, item2);
        bin2.set(itemIndex2, item1);
        return neighbor;
    }*/
}
