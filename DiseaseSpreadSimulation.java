package disease;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;  
import java.io.IOException;
import java.util.Scanner;
import java.util.Random;

public class DiseaseSpreadSimulation {
	static String csvName = "epidemicData";
    static String folderName = "Epidemic";
	static String directoryPath = "C:/";
	static Random rand = new Random();
    public static void main(String[] args) {
        // inputs needed:
        // Number of individuals (N) => (*N NEEDS TO BE PERFECT SQUARE)
        // Number of time steps (T) => (program exits after completing steps)
        // Infection rate (α) => You should accept a value 0 ≤ 𝛼 ≤ 1. Also note that
        // the cumulative probability that and individual can get infected should not be
        // more than 1
        // Recover rate (β) => You should accept a value 0 ≤ β ≤ 1.
        Scanner sc = new Scanner(System.in);

        // taking in 'number of individuals' input
        System.out.println("Enter the number of individuals (this should be a perfect square): ");
        int N = sc.nextInt(); // N = number of individuals
        while (N <= 0 || !(Math.sqrt(N) % 1 == 0)) {
            System.out.println("Please input a perfect square: ");
            N = sc.nextInt();
        }

        // taking in 'number of time steps' input
        System.out.println("Enter the number of time steps: ");
        int T = sc.nextInt(); // T = number of time steps
        while (T < 0) {
            System.out.println("Please enter a non-negative integer: ");
            T = sc.nextInt();
        }

        // taking in 'infection rate' input
        System.out.println("Enter the infection rate (between 0 and 1 inclusive): ");
        double α = sc.nextDouble(); // α = infection rate
        while (!(0 <= α && α <= 1)) {
            System.out.println("Please enter a number between 0 and 1 (inclusive): ");
            α = sc.nextDouble();
        }

        // taking in 'recovery rate' input
        System.out.println("Enter the recovery rate (between 0 and 1 inclusive): ");
        double β = sc.nextDouble(); // β = recovery rate
        while (!(0 <= β && β <= 1)) {
            System.out.println("Please enter a number between 0 and 1 (inclusive): ");
            β = sc.nextDouble();
        }
        sc.close();
        // creating the grid (2D array)
        int size = (int) Math.sqrt(N); // Size of the square array.
        char[][] epidemicGrid = new char[size][size];// Creates char grid with 'Math.sqrt(N)' rows and 'Math.sqrt(N)'
                                                     // columns

        char[][] previousGrid = new char[size][size];//Char grid to hold previous values
        
        for (int i = 0; i < size; i++) { // Instantiates every element in the epidemicGrid with 'S'
            for (int j = 0; j < size; j++) {
                epidemicGrid[i][j] = 'S'; // S stands for susceptible
            }
        }

        int randRow = rand.nextInt(size); // Chooses random number from 0 to Math.sqrt(N)
        int randCol = rand.nextInt(size); // Chooses random number from 0 to Math.sqrt(N)

        epidemicGrid[randRow][randCol] = 'I'; // instantiates the randomly chosen row and randomly chosen column with I,
                                              // A.K.A. 'Patient Zero'
		
		// Initializing the CSV file for data collection
		String filePath = directoryPath + folderName +"/" + csvName + N + ".csv" ;
		String[] header = {"Time Step", "Infection Rate", "Recovery Rate", "Number Infected", "Number Recovered", "Number Susceptible", 
				"Percent Infected", "Percent Recovered", "Percent Susceptible"
				};
		try {
            // Create FileWriter in append mode (true parameter)
            FileWriter fileWriter = new FileWriter(filePath, true);

            // Append each element of rowData followed by a comma to the file
            for (String data : header) {
                fileWriter.append(data).append(",");
            }

            // Add a new line character at the end of the row
            fileWriter.append("\n");
            // Close the FileWriter
            fileWriter.close();
        } catch (IOException e) {
            System.err.println("Error occurred while adding row to CSV file: " + e.getMessage());
        }
		
        System.out.println("\n\n\n");

        System.out.println("Patient Zero is (" + randRow + ", " + randCol + ") Meaning row with index " + randRow
                + " and column with index " + randCol + ".");
        System.out.println("Here is the initial grid.");
        outputGridInfo(epidemicGrid, 0, α, β);
        //Run thorugh the grid to see who recovers or gets infected
        for (int time = 1; time <= T; ++time) { //Starts the infection and recovery protocols for each timestep 
    		previousGrid = preGrid(previousGrid, time); // gets the previous grid
    		for (int i = 0; i < previousGrid.length; i++) { // nested loop to iterate through indiviudal of the previous grid
                for (int j = 0; j < previousGrid[i].length; j++) {
                	if (previousGrid[i][j] == 'S') { //checks and changes thet status for each individual  
                		epidemicGrid[i][j] = infectionProtocol(previousGrid, i, j, α);
                	}
                	if (previousGrid[i][j] == 'I') {
                		epidemicGrid[i][j] = recoveryProtocol(previousGrid, i, j, β);
                	}
                }
            }
    		outputGridInfo(epidemicGrid, time, α, β); // outputs the time step's changes
        }
    }
    public static char recoveryProtocol(char[][] grid, int row, int col, double recRate) {
		double k = rand.nextDouble();
		if (recRate >= k) {
			return 'R';
		} else {
			return 'I';
		}
	}
    
	public static char infectionProtocol(char[][] grid, int row, int col, double infRate) {
		
		//Looking at the chars of the point's niehgbors
		char above = (row > 0) ? grid[row - 1][col] : 'n'; // 'n' indicates no neighbor above
        char below = (row < grid.length - 1) ? grid[row + 1][col] : 'n'; // 'n' indicates no neighbor below
        char left = (col > 0) ? grid[row][col - 1] : 'n'; // 'n' indicates no neighbor to the left
        char right = (col < grid[0].length - 1) ? grid[row][col + 1] : 'n'; // 'n' indicates no neighbor to the right
        
        //Seeing how many neighbors are infected
        int count = 0;
        count += (above == 'I') ? 1 : 0;
        count += (below == 'I') ? 1 : 0;
        count += (left == 'I') ? 1 : 0;
        count += (right == 'I') ? 1 : 0;
        
    	//testing if the given point will become infected
        for (int i = 0; i < count; ++i) {
        	double k = rand.nextDouble();
    		if (infRate >= k) {
    			return 'I';
    		} 
        }
        return 'S';	
	}
    
	//preGrid will return the grid of the previous timestep
	public static char[][] preGrid(char[][] grid, int timeStep) {
		//Defining FilePaths
        String fileName = "TimeStep "+(timeStep-1)+".txt";
        String filePath = directoryPath + folderName +"/" + fileName;
        //Initialising scnaner and preGrid
		Scanner scnr;
		char[][] prevGrid = new char[grid.length][grid[0].length];
		
		try {
			//Read previous step file
			scnr = new Scanner(new File(filePath));
			scnr.nextLine();
			
			//Fill in prevGrid with previous step
			for (int i = 0; i < prevGrid.length; i++) {
				String[] yes = scnr.nextLine().split(" ",0);
				String no = String.join("",yes);
				char[] replace = no.toCharArray();
	            for (int j = 0; j < prevGrid[i].length && j < replace.length; j++) {
	            	prevGrid[i][j] = replace[j];
	            }
	        }	
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return prevGrid;
	}
    // outputGridInfo method will output information on the grid
    public static void outputGridInfo(char[][] grid, int timeStep, double infRate, double recRate) {
        // Tracking Numbers
        int numInfected = 0;
        int numRecovered = 0;
        int numSus = 0;
        int total = 0;
        // Names for the file path
        String fileName = "TimeStep " + timeStep + ".txt";

        // Create the folder
        File directory = new File(directoryPath, folderName);
        directory.mkdirs();

        // Combine the directory path and file name to create the complete file path
        String filePath = directoryPath + folderName + "/" + fileName;
        try {
            // Create a FileWriter object to write to the file
            FileWriter writer = new FileWriter(filePath);

            // Write and print the current time step
            writer.write("/////Step " + timeStep + "/////");
            System.out.println("/////Step " + timeStep + "/////");
            writer.write(System.lineSeparator());

            // Writes the array to the file and print to console
            for (char[] row : grid) {
                for (char c : row) {
                    writer.write(c + " ");
                    System.out.print(c + " ");
                    // Count the numbers
                    switch (c) {
                        case 'I':
                            ++numInfected;
                            break;
                        case 'R':
                            ++numRecovered;
                            break;
                        default:
                            ++numSus;
                            break;
                    }
                }
                // Add a newline after each row
                System.out.println();
                writer.write(System.lineSeparator());
            }

            total = numSus + numRecovered + numInfected;
            double percentInf = (100.0)*((double)numInfected/(double)total);
            double percentRec = (100.0)*((double)numRecovered/(double)total);
            double percentSus = (100.0)*((double)numSus/(double)total);
            
            // Prints some values
            System.out.println("Number Infected: " + numInfected);
            System.out.println("Number Recovered: " + numRecovered);
            System.out.println("Number Susceptible: " + numSus);
            System.out.printf("Percent Infected: %,.2f%%\n", percentInf);
            System.out.printf("Percent Recovered: %,.2f%%\n", percentRec);
            System.out.printf("Percent Susceptible: %,.2f%%\n", percentSus);
            System.out.println();
            
            // create a String array of the data
            String[] data = {Integer.toString(timeStep) ,Double.toString(infRate), Double.toString(recRate), 
            		Integer.toString(numInfected), Integer.toString(numRecovered), Integer.toString(numSus), 
            		Double.toString(percentInf), Double.toString(percentRec), Double.toString(percentSus)
            };
            
            //Write data to CSV file
            writeToCSV(data, total);
            
            // Close the FileWriter to release system resources
            writer.close();
        } catch (IOException e) {
            // Handle IO exception
            e.printStackTrace();
        }
    }
	public static void writeToCSV(String[] rowData, int N) {
		String filePath = directoryPath + folderName +"/" + csvName + N + ".csv";
		try {
            // Create FileWriter in append mode (true parameter)
            FileWriter fileWriter = new FileWriter(filePath, true);

            // Append each element of rowData followed by a comma to the file
            for (String data : rowData) {
                fileWriter.append(data).append(",");
            }

            // Add a new line character at the end of the row
            fileWriter.append("\n");

            // Close the FileWriter
            fileWriter.close();
        } catch (IOException e) {
            System.err.println("Error occurred while adding row to CSV file: " + e.getMessage());
        }
	}
}
