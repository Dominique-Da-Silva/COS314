Knapsack Problem - Genetic Algorithm and Ant Colony Optimization
	This project solves the knapsack problem using two metaheuristic algorithms: Genetic Algorithm and Ant Colony Optimization.

Problem Description
	The knapsack problem is a combinatorial optimization problem that asks: Given a set of items, each with a weight and a value, determine the number of each item to include in a collection so that the total weight is less than or equal to a given limit and the total value is maximized.

Solution Approach
	We implement two metaheuristic algorithms to solve this problem: Genetic Algorithm (GA) and Ant Colony Optimization (ACO). 
	Both algorithms are implemented in Java.

	Genetic Algorithm
		In the GA, the solution is represented by a chromosome, which is a binary string of length n, where n is the number of items. 
		Each gene of the chromosome represents whether an item is included in the knapsack or not. 
		The algorithm starts by randomly generating an initial population of chromosomes. 
		It then evaluates the fitness of each chromosome, which is defined as the total value of the items in the knapsack. 
		The fittest chromosomes are then selected for crossover and mutation to generate new offspring chromosomes. 
		The process is repeated for a fixed number of generations, with the hope that the population will converge towards a solution that is close to optimal.

	Ant Colony Optimization
		In the ACO, we create a graph where each item is a node and the edges represent the probability of choosing one item over another. 
		The ants then traverse the graph and choose items to include in the knapsack based on a probability distribution determined by the pheromone levels on the edges. 
		The pheromone levels are updated based on the quality of the solutions found by the ants.

How to Use the Code
	Download the folder (u2162994_COS314_Assignment2.zip) from the repository/ ClickUp.
	Knapsack instances should be contained within the repository, so there is no need to download again.
	Unzip the implementation of the problem, the folder named u21629944_COS314_Assignment2.
	The following documents and files should be contained within the folder:
		Excelsheets folder
			Knows Optimums.xlsx
			Tests.xlsx
		Knapsack Instances
			f1_l-d_kp_10_269
			f2_l-d_kp_20_878
			f3_l-d_kp_4_20
			f4_l-d_kp_4_11
			f5_l-d_kp_15_375
			f6_l-d_kp_10_60
			f7_l-d_kp_7_50
			knapPI_1_100_1000_1
			f8_l-d_kp_23_10000
			f9_l-d_kp_5_80
			f10_l-d_kp_20_879
		21629944_report.docx
		21629944_report.pdf
		ACO.java
		GA.java
		knapsack.java
		ReadMe.txt
	Compile and run the the java file named knapsack.java
	The program will iterate through all the instances in the "Knapsack Instances" folder and run both the GA and ACO algorithms on each instance. 
	The results will be printed to the console, with appropriate print statements for readability.