package puzzles.hoppers.gui;
import puzzles.common.Observer;
import puzzles.hoppers.model.*;
import java.nio.file.*;
import java.util.*;
import java.io.*;
import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.Scene;
import javafx.geometry.*;
import javafx.stage.*;
import javafx.scene.image.*;

/**
 * The graphical user interface to the Hoppers Puzzle game model in
 * {@link HoppersModel}.
 *
 * @author Tiffany Lee
 */
public class HoppersGUI extends Application implements Observer<HoppersModel, String> {
    /** View/controller access to model */
    private HoppersModel model;
    /** the primary stage for this application */
    private Stage stage;
    /** initializes an empty GUI */
    private final BorderPane hoppers = new BorderPane();
    /** grid of all the pieces on the board */
    private Button[][] piecesGrid;
    /** the status area, or interaction messages from the model */
    private final Label status = new Label();
    /** the size of all icons, in square dimension */
    private final static int ICON_SIZE = 75;
    /** the font size for labels and buttons */
    private final static int FONT_SIZE = 25;
    /** the resources directory is located directly underneath the gui package */
    private final static String RESOURCES_DIR = "resources/";
    /** red frog */
    private final Image redFrog = new Image(Objects.requireNonNull
            (getClass().getResourceAsStream(RESOURCES_DIR + "red_frog.png")));
    /** green frog */
    private final Image greenFrog = new Image(Objects.requireNonNull
            (getClass().getResourceAsStream(RESOURCES_DIR + "green_frog.png")));
    /** lily pad */
    private final Image lilyPad = new Image(Objects.requireNonNull
            (getClass().getResourceAsStream(RESOURCES_DIR + "lily_pad.png")));
    /** water */
    private final Image water = new Image(Objects.requireNonNull
            (getClass().getResourceAsStream(RESOURCES_DIR + "water.png")));

    /**
     * Creates the Hoppers Puzzle model and registers this object as an
     * observer of it. Sets the status/interaction message
     *
     * @throws IOException if the file is not found or there are errors reading
     */
    @Override
    public void init() throws IOException {
        String filename = getParameters().getRaw().get(0);
        String[] file = filename.split("/");
        this.model = new HoppersModel(filename);
        this.model.addObserver(this);
        this.status.setText("Loaded: " + file[file.length-1]);
        status.setFont(new Font("Serif", FONT_SIZE));
    }

    /**
     * Sets up the GUI.
     * Window displays the status/interaction message at the top,
     * the Hoppers Puzzle board at the center, and the buttons
     * (load, reset, hint) at the bottom.
     *
     * @param stage the primary stage for this application, onto which
     * the application scene can be set.
     * Applications may create other stages, if needed, but they will not be
     * primary stages.
     * @throws Exception if the file is not found or there are errors reading
     */
    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        // STATUS H-BOX
        HBox stats = new HBox();
        stats.getChildren().add(status);
        stats.setAlignment(Pos.CENTER);
        // SETS UP GUI
        hoppers.setTop(stats);
        hoppers.setCenter(makeCenter());
        hoppers.setBottom(makeBottom());
        // SETS UP SCENE & STAGE
        Scene scene = new Scene(hoppers);
        stage.setTitle("Hoppers GUI");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Sets up and creates the grid of the Hoppers Puzzle Board with
     * a red frog, green frogs, lily pads, and spots of water on it.
     *
     * @return GridPane containing the pieces mentioned above
     */
    public GridPane makeCenter(){
        GridPane board = new GridPane();
        this.piecesGrid = new Button[model.getRows()][model.getCols()];
        for(int r = 0; r < model.getRows(); r++) {
            for (int c = 0; c < model.getCols(); c++) {
                int locR = r;
                int locC = c;
                Button pieces = new Button();
                pieces.setMinSize(ICON_SIZE, ICON_SIZE);
                pieces.setMaxSize(ICON_SIZE, ICON_SIZE);
                if(model.getCell(r, c) == HoppersConfig.LILY_PAD) {
                    pieces.setGraphic(new ImageView(lilyPad));
                    pieces.setOnAction(event -> model.select(locR, locC));
                } else if(model.getCell(r, c) == HoppersConfig.WATER){
                    pieces.setGraphic(new ImageView(water));
                    pieces.setOnAction(event -> model.select(locR, locC));
                } else if(model.getCell(r, c) == HoppersConfig.GREEN_FROG){
                    pieces.setGraphic(new ImageView(greenFrog));
                    pieces.setOnAction(event -> model.select(locR, locC));

                } else if(model.getCell(r, c) == HoppersConfig.RED_FROG) {
                    pieces.setGraphic(new ImageView(redFrog));
                    pieces.setOnAction(event -> model.select(locR, locC));
                }
                piecesGrid[r][c] = pieces;
                board.add(pieces, c, r);
            }
        }
        return board;
    }

    /**
     * Sets up and creates the load, reset, and hint buttons
     *
     * @return HBox containing the buttons mentioned above
     */
    public HBox makeBottom(){
        HBox buttons = new HBox();
        // LOAD
        Button load = new Button("Load");
        load.setFont(new Font("Serif", FONT_SIZE));
        load.setOnAction(event -> {
            FileChooser chooser = new FileChooser();
            String currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
            currentPath += File.separator + "data" + File.separator + "hoppers";
            chooser.setInitialDirectory(new File(currentPath));
            File file = chooser.showOpenDialog(stage);
            if(file != null){
                try {
                    model.load(file.getAbsolutePath());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                update(this.model, "No File Chosen!");
            }
        });
        // RESET
        Button reset = new Button("Reset");
        reset.setFont(new Font("Serif", FONT_SIZE));
        reset.setOnAction(event -> {
            try {
                model.reset();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        // HINT
        Button hint = new Button("Hint");
        hint.setFont(new Font("Serif", FONT_SIZE));
        hint.setOnAction(event -> model.hint());

        buttons.getChildren().addAll(load, reset, hint);
        buttons.setAlignment(Pos.CENTER);
        return buttons;
    }

    /**
     * The model -- the subject -- has some changes.
     * Displays the current state of the board and prints the provided
     * message.
     *
     * @param hoppersModel the object that wishes to inform this object
     *                about something that has happened.
     * @param msg optional data the server.model can send to the observer
     *
     */
    @Override
    public void update(HoppersModel hoppersModel, String msg) {
        this.status.setText(msg);
        hoppers.setCenter(makeCenter());
        this.stage.sizeToScene();
        for(int r = 0; r < model.getRows(); r++) {
            for (int c = 0; c < model.getCols(); c++) {
                if(model.getCell(r, c) == HoppersConfig.LILY_PAD) {
                    piecesGrid[r][c].setGraphic(new ImageView(lilyPad));
                } else if(model.getCell(r, c) == HoppersConfig.WATER){
                    piecesGrid[r][c].setGraphic(new ImageView(water));
                } else if(model.getCell(r, c) == HoppersConfig.GREEN_FROG){
                    piecesGrid[r][c].setGraphic(new ImageView(greenFrog));
                } else if(model.getCell(r, c) == HoppersConfig.RED_FROG) {
                    piecesGrid[r][c].setGraphic(new ImageView(redFrog));
                }
            }
        }
    }

    /**
     * Launches the application.
     *
     * @param args the name of the initial Hoppers Puzzle file to load
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java HoppersPTUI filename");
        } else {
            Application.launch(args);
        }
    }
}
