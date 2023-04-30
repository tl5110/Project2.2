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
import javafx.stage.Stage;
import puzzles.common.Observer;
import puzzles.chess.model.ChessModel;
import puzzles.hoppers.model.HoppersModel;

import java.io.IOException;
import java.util.Objects;

/**
 * The graphical user interface for the Chess game
 * @author jolin qiu
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

    /** the 6 types of chess pieces that we have */
    private enum ChessPieces {
        BISHOP,
        KING,
        KNIGHT,
        PAWN,
        QUEEN,
        ROOK
    }
    private Image bishop = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"bishop.png"));
    private Image king = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"king.png"));
    private Image knight = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"knight.png"));
    private Image pawn = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"pawn.png"));
    private Image queen = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"queen.png"));
    private Image rook = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"rook.png"));

    private Image BLUE = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"blue.png"));


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
     *
     *
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
        HBox messages = new HBox();
        messages.getChildren().add(messages);
        messages.setAlignment(Pos.CENTER);

        // the chessboard
        chessGame.setTop(messages);
        chessGame.setCenter(makeChessBoard());


        Button button = new Button();
        button.setGraphic(new ImageView(bishop));
        button.setBackground(LIGHT);
        button.setMinSize(ICON_SIZE, ICON_SIZE);
        button.setMaxSize(ICON_SIZE, ICON_SIZE);
        Scene scene = new Scene(button);
        stage.setScene(scene);
        stage.show();
    }

    private void makeChessBoard(){
        GridPane grid = new GridPane();
        this.chessBoard = new Button[model.getRows()][model.getCols()];

        for(int row=0; row < model.getRows(); row++){
            for (int col=0; col < model.getCols(); col++){

                Button chessPieces = new Button();
                chessPieces.setMinSize(ICON_SIZE, ICON_SIZE);
                chessPieces.setMaxSize(ICON_SIZE, ICON_SIZE);


            }
        }
    }


    @Override
    public void update(ChessModel chessModel, String msg) {

        this.stage.sizeToScene();  // when a different sized puzzle is loaded
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
