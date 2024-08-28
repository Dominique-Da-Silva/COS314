import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.List;
import java.util.ArrayList;


public class FoldersTextFilesReader{

    public static String CSV_FILE_NAME = "output.csv";
    //where we want to write our search bin values to


    //static array declaration of all the folders we are looking for
    private static final String[] FOLDERS = {
        "Falkenauer_T",
        "Falkenauer_U",
        "Hard28",
        "Scholl_1",
        "Scholl_2",
        "Scholl_3",
        "Schwerin_1",
        "Schwerin_2",
        "Waescher"
    };
    


    public static void main(String[] args) throws IOException {

        List<Integer> itemList = new ArrayList<>(); 
        //using a list since there isn't a fixed number of entries for every textfile

        //FileWriter writer = new FileWriter(CSV_FILE_NAME);
        //initialize the csv writer
        //writer.write("TextFile name, ILS, Tabu\n");
        // write the header row to the CSV file

        for (String folder : FOLDERS) {
            long startFolderRuntime = System.currentTimeMillis(); 
            //iterate through our list of folders

            System.out.println("Current folder directory: "+folder);

            File directory = new File(folder);
            //created a File object called directory for the current folder

            File[] files = directory.listFiles();
            //gets an array of ALL files within the folder

            if (files != null) {
                //looping through all the files in the list

                for (File file : files) {
                    //check each file of the folder

                    if (file.isFile() && file.getName().endsWith(".txt")) {

                        //System.out.println("Name of the current file: "+ file.getName());
                        //note that the file does include the extension

                        String textfileNameWithExtenstion = file.getName();
                        String textfileName = textfileNameWithExtenstion.substring(0,textfileNameWithExtenstion.lastIndexOf('.'));
                        //System.out.println("\t"+"Name of the current file: "+ textfileName);

                        itemList.clear();
                        //clearing the list for the next textfile to read

                        BufferedReader reader = new BufferedReader(new FileReader(file));
                        String line;
                        int lineNumber = 1;
                        int binCapacity = 0;
                        int totalNumberItems =0; //just used in order to check that the linked list and totalNumber of items from the text file match up

                        while ((line = reader.readLine()) != null) {
                            //System.out.println(line);
                            if(lineNumber==1) totalNumberItems=Integer.parseInt(line.trim());
                            else{
                                if(lineNumber==2) binCapacity= Integer.parseInt(line.trim());
                                else itemList.add(Integer.parseInt(line.trim()));
                            }
                            lineNumber++;
                        }

                        //System.out.println("Total number of items from textfile: "+totalNumberItems);
                        //System.out.println("Bin Capacity: "+binCapacity);
                        //System.out.println("Total items from linked list: "+itemList.size());

                        /*
                         * This is where you need to call all the search algorithms with the given data and thereafter write to your excel file
                         */


                        //IteratedLocalSearch ILS = new IteratedLocalSearch(binCapacity, itemList);
                        //int ILSoptimalValue = ILS.ILSoptimal();
                        //System.out.println("\t"+"\t"+"ILS Optimal Value: "+ILSoptimalValue);
                        //System.out.println(ILSoptimalValue);

                        //true indicates that we will append to the file instead of overwriting the content
                        //writer.write(folder + "\t" + textfileName + "\t");
                        //writer.write(Integer.toString(ILSoptimalValue) + "\n");
                        //writer.flush();

                        TabuSearchAlgorithm TABU = new TabuSearchAlgorithm(binCapacity, itemList);
                        int TabuOptimalValue = TABU.TABUoptimal();
                        System.out.println(TabuOptimalValue );

                        
                        reader.close();

                    }

                }

            }
            long endFolderRuntime = System.currentTimeMillis();
            long Runtime = endFolderRuntime-startFolderRuntime;
            System.out.println("Folder Runtime in milliseconds: "+Runtime);
            Runtime = Runtime/1000;
            System.out.println("Folder Runtime in seconds: "+Runtime);
        }
        //writer.close();
    }



}