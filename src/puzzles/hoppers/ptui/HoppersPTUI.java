package puzzles.hoppers.ptui;
import puzzles.common.Observer;
import puzzles.hoppers.model.HoppersModel;
import java.io.IOException;
import java.util.Scanner;

/**
 * A Plain-Text user interface for the hopper program
 *
 * @author Tiffany Lee
 */
public class HoppersPTUI implements Observer<HoppersModel, String> {
    /** View/Controller access to model */
    private HoppersModel model;
    /** the initial state of the given hopper's board */
    private static String ogBoard;

    /**
     * Creates the Hopper model with the given filename and registers
     * this object as an observer of it.
     *
     * @param filename the name of the file to read from
     * @throws IOException if the file is not found or there are errors reading
     */
    public void init(String filename) throws IOException {
        this.model = new HoppersModel(filename);
        this.model.addObserver(this);
        model.load(filename);
        displayHelp();
    }

    /**
     * The model -- the subject -- has some changes.
     * Displays the current state of the board and prints the provided
     * message
     *
     * @param model the object that wishes to inform this object
     *                about something that has happened.
     * @param data optional data the server.model can send to the observer
     *
     */
    @Override
    public void update(HoppersModel model, String data) {
        System.out.println(data);
        System.out.println(model);
    }

    /**
     * Displays all the instructions on how to play the hopper's puzzle
     */
    private void displayHelp() {
        System.out.println( "h(int)              -- hint next move" );
        System.out.println( "l(oad) filename     -- load new puzzle file" );
        System.out.println( "s(elect) r c        -- select cell at r, c" );
        System.out.println( "q(uit)              -- quit the game" );
        System.out.println( "r(eset)             -- reset the current game" );
    }

    /**
     * Takes user input to call the necessary methods from the model and
     * updates the display of the hopper puzzle board based on said input
     *
     * @throws IOException if the file is not found or there are errors reading
     */
    public void run() throws IOException {
        Scanner in = new Scanner( System.in );
        for ( ; ; ) {
            System.out.print( "> " );
            String line = in.nextLine();
            String[] words = line.split( "\\s+" );
            if (words.length > 0) {
                if (words[0].startsWith("h")) {
                    model.hint();
                } else if(words[0].startsWith("l")){
                    if(words.length < 2){
                        update(this.model, "No File Chosen!");
                    } else {
                        String[] filename = words[1].split("/");
                        if(filename.length > 1){
                            ogBoard = words[1];
                            model.load(ogBoard);
                        } else {
                            ogBoard = "data/hoppers/" + words[1];
                            model.load(ogBoard);
                        }
                    }
                } else if(words[0].startsWith("s")){
                    if(words.length < 3){
                        update(this.model, "Incomplete Selection!");
                    } else {
                        int row = Integer.parseInt(words[1]);
                        int col = Integer.parseInt(words[2]);
                        model.select(row, col);
                    }
                } else if(words[0].startsWith("q")){
                    break;
                } else if(words[0].startsWith("r")){
                    model.reset();
                } else {
                    displayHelp();
                }
            }
        }
    }

    /**
     * Starts up the plain-text user interface of the hopper puzzle
     * @param args the filename of the puzzle
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java HoppersPTUI filename");
        } else {
            try {
                HoppersPTUI ptui = new HoppersPTUI();
                ogBoard = args[0];
                ptui.init(args[0]);
                ptui.run();
            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());
            }
        }
    }
}
