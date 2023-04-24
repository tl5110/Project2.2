package puzzles.hoppers.solver;

import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;
import puzzles.hoppers.model.HoppersConfig;
import java.io.IOException;
import java.util.Collection;

/**
 * The main hopper program that is run with one command line argument,
 * the name of the puzzle file. If the puzzle file is present it is
 * assumed to be valid, otherwise display an error message and exit.
 * hoppers-4.txt example:
 * <pre>
 *     5 5          # rows and columns in the board
 *     G * G * R    # row 1 - G=green frog, R=red frog, *=invalid space/water
 *     * G * . *    # row 2 - .=valid empty space/lily-pad
 *     . * G * G    # row 3
 *     * . * G *    # row 4
 *     . * . * G    # row 5
 * </pre>
 * When the solution is found there should only be the red frog on
 * the board
 *
 * @author Tiffany Lee
 */
public class Hoppers {
    /**
     * The main method.
     * @param args the command line arguments (name of input file)
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Hoppers filename");
        } else {
            try{
                HoppersConfig hopper = new HoppersConfig(args[0]);
                Solver solve = new Solver(hopper);
                Collection<Configuration> solved =  solve.solve();

                System.out.println("File: " + args[0]);
                System.out.print(hopper);
                System.out.println("Total configs: " + solve.getTotalConfigs());
                System.out.println("Unique configs: " + solve.getUniqueConfigs());

                int step = 0;
                if(solved != null){
                    for(Configuration hop : solved){
                        System.out.println("Step " + step + ":");
                        System.out.println(hop);
                        step += 1;
                    }
                } else {
                    System.out.println("No solution!");
                }
            } catch(IOException ioe){
                System.out.println(ioe.getMessage());
            }
        }
    }
}
