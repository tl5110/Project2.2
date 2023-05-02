package puzzles.chess.ptui;

import puzzles.chess.model.ChessConfig;
import puzzles.common.Observer;
import puzzles.chess.model.ChessModel;

import java.io.IOException;
import java.util.Scanner;

/**
 * The logical view and handler of user interactions (view and controller)
 * The view creates the model as part of its startup.
 * The view calls the model's addObserver and add itself to the model.
 *
 * The model stores the view in its list of observers.
 *
 * When the user makes an interaction the controller informs the model of it.
 * When the model changes, as a result of the controller calling it, it calls
 * its own alertObservers.
 *      This causes the model to call the view's update method.
 *      When the view's update method is called, it can use whatever calls it
 *      needs into the model to change the UI that is displayed to the user.
 */
public class ChessPTUI implements Observer<ChessModel, String> {
    private ChessModel model;

    private static String initialBoard;

    public void init(String filename) throws IOException {
        this.model = new ChessModel(filename);
        this.model.addObserver(this);
        model.load(filename);
        displayHelp();
    }

    @Override
    public void update(ChessModel model, String data) {
        // for demonstration purposes
        System.out.println(data);
        System.out.println(model);
    }

    private void displayHelp() {
        System.out.println( "h(int)              -- hint next move" );
        System.out.println( "l(oad) filename     -- load new puzzle file" );
        System.out.println( "s(elect) r c        -- select cell at r, c" );
        System.out.println( "q(uit)              -- quit the game" );
        System.out.println( "r(eset)             -- reset the current game" );
    }

    /**
     *
     * @throws IOException
     */
    public void run() throws IOException {
        Scanner in = new Scanner( System.in );
        for ( ; ; ) {
            System.out.print( "> " );
            String line = in.nextLine();
            String[] words = line.split( "\\s+" );
            // words = curr user input line, i.e. "l chess-5.txt"
            if (words.length > 0) {
                if (words[0].startsWith("h")) {
                    model.hint();
                } else if (words[0].startsWith("l")) {
                    if (words.length < 2) {
                        update(this.model, "No File Chosen!");
                    } else {
                        String[] filename = words[1].split("/");
                        if(filename.length > 1){
                            // data/chess-4.txt
                            initialBoard = words[1];
                            model.load(initialBoard);
                        } else {
                            // chess-4.txt
                            initialBoard = "data/chess/" + words[1];
                            model.load(initialBoard);
                        }
                    }
                } else if (words[0].startsWith("s")) {
                    // valid entry needs 3 elements [s, 0, 0 ]
                    if(words.length != 3){
                        update(this.model, "Incomplete Selection!");
                    } else {
                        int row = Integer.parseInt(words[1]);
                        int col = Integer.parseInt(words[2]);
                        model.select(row, col);
                    }
                } else if (words[0].startsWith("r")) {
                    model.reset();
                } else if (words[0].startsWith("q")) {
                        break;
                } else {
                    displayHelp();
                }
            }
        }
    }

    /**
     * The model is informed by the controller to
     * load a file with a given name.
     * @param args
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java ChessPTUI filename");
        } else {
            try {
                ChessPTUI ptui = new ChessPTUI();
                // The model asks the configuration to load it
                initialBoard = args[0];
                ptui.init(initialBoard);
                ptui.run();
            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());
            }
        }
    }
}

