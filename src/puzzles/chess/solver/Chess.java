package puzzles.chess.solver;
import puzzles.chess.model.ChessConfig;
import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;
import java.io.IOException;
import java.util.Collection;

/**
 * The CLI chess program, run with one command line argument: the name of the puzzle file.
 * If the puzzle file is present it is assumed to be valid,
 * otherwise display an error message and exit.
 * chess-4.txt example:
 *     4 4
 *     B . P K
 *     N . . P
 *     . . P Q
 *     R . . P
 * The first line represents the number of rows and number of columns in the puzzle.
 * The puzzle does not always have to be square!
 * Each row of the puzzle follows with space separated characters to denote:
 *   B - A bishop
 *   K - A king
 *   N - A knight
 *   P - A pawn
 *   Q - A queen
 *   R - A rook
 *   . - An empty space on the board
 * Unlike real chess, you can have many duplicate pieces of the same kind.
 * When the solution is found there will be one piece remaining on the board
 * .
 * @author jolin qiu
 */
public class Chess {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Chess filename");
        } else {
            try{
                // construct and display initial configuration
                ChessConfig initialConfig = new ChessConfig(args[0]);
                System.out.print("File: " + args[0]);
                System.out.print(initialConfig);

                // solve the puzzle
                Solver solver = new Solver(initialConfig);

                // display the solution
                int step = 0;
                Collection<Configuration> solved = solver.solve();
                System.out.println("Total configs: " + solver.getTotalConfigs());
                System.out.println("Unique configs: " + solver.getUniqueConfigs());
                if(solved != null) {
                    for (Configuration board : solved) {
                        System.out.println("Step " + step + ": " + board);
                        step += 1;
                    }
                }
            } catch (IOException ioe){
                System.out.println(ioe.getMessage());
            }
        }
    }
}
