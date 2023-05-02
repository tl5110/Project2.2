package puzzles.chess.gui;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import puzzles.common.Observer;
import puzzles.chess.model.ChessModel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import static puzzles.chess.model.ChessConfig.*;

/**
 * The graphical user interface for the Chess game
 * @author jolin qiu
 *
 * NOTES:
 *      I wasn't able to source the cause of this issue but for some reason after
 *      the first "initial click" all other actions/clicks interacting with the GUI
 *      are processed as SELECTing-actions. For example, even when I press the
 *      LOAD button, it may allow you to pick a file to load initially, however
 *      afterwards: the action events are all processed as SELECT.
 *
 *      In my code (see after each setOnAction methods in lines 143,
 *      204, 230, 247) I put System.out.println("[name of button type] clicked")
 *      And indeed each click on the interface after the first one is all
 *      processed as a Chess Piece button being clicked / SELECT.
 *
 *      An example of the console output in the terminal after clicking the
 *          1) load button,
 *          2) the load button again,
 *          3) the reset button,
 *          and 4) the hint button,
 *          etc.
 *          would look like:
 *             " load button clicked
 *               chess piece clicked
 *               chess piece clicked
 *               chess piece clicked
 *               ..."
 */
public class ChessGUI extends Application implements Observer<ChessModel, String> {
    /** The size of all icons, in square dimension */
    private final static int ICON_SIZE = 75;
    /** the font size for labels and buttons */
    private final static int FONT_SIZE = 12;
    /** The resources directory is located directly underneath the gui package */
    private final static String RESOURCES_DIR = "resources/";

    /** the model to manage data, logic and rules */
    private ChessModel model;
    /** the primary stage for the application */
    private Stage stage;

    private final Label message = new Label();

    /** The full chess game with headers and labels */
    private final BorderPane chessGame = new BorderPane();
    /** the chessboard grid */
    private Button[][] chessBoard;
    private final Image bishop = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"bishop.png"));
    private final Image king = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"king.png"));
    private final Image knight = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"knight.png"));
    private final Image pawn = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"pawn.png"));
    private final Image queen = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"queen.png"));
    private final Image rook = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"rook.png"));
    private final Image empty = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"empty.png"));


    /** a definition of light and dark and for the button backgrounds */
    private static final Background LIGHT =
            new Background( new BackgroundFill(Color.WHITE, null, null));
    private static final Background DARK =
            new Background( new BackgroundFill(Color.MIDNIGHTBLUE, null, null));

    /**
     * initializes the Chess puzzle
     *
     * @throws IOException
     */
    @Override
    public void init() throws IOException {
        // get the file name from the command line
        String filename = getParameters().getRaw().get(0);
        String[] file = filename.split("/");
        this.model = new ChessModel(filename);
        this.model.addObserver(this);
        this.message.setText("Loaded: " + file[file.length-1]);
        message.setFont(new Font("Serif", FONT_SIZE));
    }

    /**
     * @param stage the primary stage for this application, onto which
     * the application scene can be set.
     * Applications may create other stages, if needed, but they will not be
     * primary stages.
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {
        // header messages for the game : loaded file, hints, invalid/valid moves, etc.
        this.stage = stage;
        HBox upperGameConsole = new HBox();
        upperGameConsole.getChildren().add(message);
        upperGameConsole.setAlignment(Pos.CENTER);

        // the console with in game messages
        chessGame.setTop(upperGameConsole);
        // the chessboard puzzle the user interacts with
        chessGame.setCenter(makeChessBoard());
        // the buttons to load, reset, or hint
        chessGame.setBottom(makeBase());


        Scene scene = new Scene(chessGame);
        stage.setTitle("Chess GUI");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @return the chessboard populated with pieces based on the file
     */
    public GridPane makeChessBoard(){
        GridPane grid = new GridPane();
        this.chessBoard = new Button[model.getRows()][model.getCols()];
        for(int row=0; row < model.getRows(); row++){
            for (int col=0; col < model.getCols(); col++){
                int currRow = row;
                int currCol = col;
                // populate the board with pieces
                Button chessPiece = new Button();
                chessPiece.setOnAction(event -> {
                    System.out.println("chess piece clicked");
                    model.select(currRow, currCol);
                });

                chessPiece.setMinSize(ICON_SIZE, ICON_SIZE);
                chessPiece.setMaxSize(ICON_SIZE, ICON_SIZE);
                ImageView image;
                char piece = model.getCell(row, col);
                switch (piece){
                    case BISHOP ->  image = new ImageView(bishop);
                    case KING ->    image = new ImageView(king);
                    case KNIGHT ->  image = new ImageView(knight);
                    case PAWN ->    image = new ImageView(pawn);
                    case QUEEN ->   image = new ImageView(queen);
                    case ROOK ->    image = new ImageView(rook);
                    default ->      image = new ImageView(empty);
                }
                // set the "button" to be the appropriate piece/image constructed from the model
                chessPiece.setGraphic(image);
                // populate the background of the piece/image
                // to be an alternating checkerboard pattern
                if ((row+col) % 2 == 0){
//                    chessPiece.setBackground(TRANSPARENT);
                    chessPiece.setBackground(LIGHT);
                } else {
                    chessPiece.setBackground(DARK);
                }
                // set the board at the appropriate place to be the chess piece or graphic
                chessBoard[row][col] = chessPiece;
                grid.add(chessPiece, col, row);

            }
        }
        return grid;
    }


    /**
     * makes the baseline of the game screen window that gives the user options to
     * load a new puzzle, reset the puzzle, or receive hints
     * @return
     */
    public HBox makeBase(){
        HBox buttons = new HBox();
        Button load = loadButton();
        Button reset = resetButton();
        Button hint = hintButton();
        buttons.getChildren().addAll(load, reset, hint);

        buttons.setAlignment(Pos.CENTER);
        return buttons;
    }

    /**
     * makes the load button
     */
    private Button loadButton(){
        Button load = new Button("Load");
        load.setFont(new Font("Serif", FONT_SIZE));

        load.setOnAction(event -> {
            System.out.println("load button clicked");
            FileChooser chooser = new FileChooser();
            String currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
            currentPath += File.separator + "data" + File.separator + "chess";  // or "hoppers"
            chooser.setInitialDirectory(new File(currentPath));
            File file = chooser.showOpenDialog(stage);
            if (file != null){
                try {
                    model.load(file.getAbsolutePath());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                update(this.model, "No File Chosen!");
            }
        });
        return load;
    }

    /**
     * makes the reset button
     */
    private Button resetButton(){
        Button reset = new Button("Reset");
        reset.setFont(new Font("Serif", FONT_SIZE));
        reset.setOnAction(event -> {
            System.out.println("reset button pushed");
            try {
                model.reset();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        return reset;
    }

    /**
     * makes the hint button
     */
    private Button hintButton(){
        Button hint = new Button("Hint");
        hint.setFont(new Font("Serif", FONT_SIZE));
        hint.setOnAction(event -> {
            System.out.println("hint button pushed");
            model.hint();
        });
        return hint;
    }

    /**
     * updates the view
     * @param chessModel the object that wishes to inform this object
     *                about something that has happened.
     * @param msg optional data the server.model can send to the observer
     *
     */
    @Override
    public void update(ChessModel chessModel, String msg) {
        this.message.setText(msg);
        chessGame.setCenter(makeChessBoard());

        for(int row = 0; row < model.getRows(); row++) {
            for (int col = 0; col < model.getCols(); col++) {
                ImageView image;
                char piece = model.getCell(row, col);
                switch (piece){
                    case BISHOP ->  image = new ImageView(bishop);
                    case KING ->    image = new ImageView(king);
                    case KNIGHT ->  image = new ImageView(knight);
                    case PAWN ->    image = new ImageView(pawn);
                    case QUEEN ->   image = new ImageView(queen);
                    case ROOK ->    image = new ImageView(rook);
                    default ->      image = new ImageView(empty);
                }
                // set the board at the appropriate place to be the chesspiece or graphic
                chessBoard[row][col].setGraphic(image);
            }
        }
        this.stage.sizeToScene();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
