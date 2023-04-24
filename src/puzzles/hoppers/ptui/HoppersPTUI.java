package puzzles.hoppers.ptui;

import puzzles.common.Observer;
import puzzles.hoppers.model.HoppersModel;
import puzzles.chess.ptui.ChessPTUI;
import java.io.IOException;
import java.util.Scanner;

public class HoppersPTUI implements Observer<HoppersModel, String> {
    private HoppersModel model;
    private static String ogBoard;

    public void init(String filename) throws IOException {
        this.model = new HoppersModel(filename);
        this.model.addObserver(this);
        displayHelp();
    }

    @Override
    public void update(HoppersModel model, String data) {
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
                    model = new HoppersModel(words[1]);
                    System.out.println("Loaded: " + words[1]);
                } else if(words[0].startsWith("s")){
                    int startRow = Integer.parseInt(words[1]);
                    int startCol = Integer.parseInt(words[2]);
                    if(model.checkFrog(startRow, startCol)){
                        String select2 = in.nextLine();
                        String[] words2 = select2.split( "\\s+" );
                        int endRow = Integer.parseInt(words2[1]);
                        int endCol = Integer.parseInt(words2[2]);
                        if(model.isValid(endRow, endCol)){
                            model.move(endRow, endCol);
                        }
                    } else {
                        System.out.println("Invalid selection");
                    }

                } else if(words[0].startsWith("q")){
                    break;
                } else if(words[0].startsWith("r")){
                    model = new HoppersModel(ogBoard);
                } else {
                    displayHelp();
                }
            }
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java HoppersPTUI filename");
        } else {
            try {
                ChessPTUI ptui = new ChessPTUI();
                ogBoard = args[0];
                ptui.init(args[0]);
                ptui.run();
            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());
            }
        }
    }
}
