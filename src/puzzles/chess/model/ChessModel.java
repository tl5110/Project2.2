package puzzles.chess.model;

import puzzles.common.Coordinates;
import puzzles.common.Observer;
import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * The representation, logic and rules of the game.
 */

public class ChessModel {
    /** the collection of observers of this model */
    private final List<Observer<ChessModel, String>> observers = new LinkedList<>();

    /** the current configuration */
    private ChessConfig currentConfig;
    private char chessPiece;
    private Coordinates currCoordinates;
    private Coordinates newCoordinates;

    /** the name of the previous file */
    private String prevFile;

    /**
     * tracks whether the user is selecting a cell on the board to move, or
     * then moving a cell to another
     */
    private boolean isFirstSelection;

    /**
     * After the model is informed by the controller to
     * load a file with a given name. It asks the configuration
     * to load it
     *
     * @param filename the name of the file to load
     * @throws IOException can't find input/output file
     */
    public ChessModel(String filename) throws IOException {
        this.currentConfig = new ChessConfig(filename);
        this.isFirstSelection = true;
        this.prevFile = filename;
    }


    /**
     * The view calls this to add itself as an observer.
     *
     * @param observer the view
     */
    public void addObserver(Observer<ChessModel, String> observer) {
        this.observers.add(observer);
    }

    /**
     * The model's state has changed (the counter), so inform the view via
     * the update method
     */
    private void alertObservers(String data) {
        for (var observer : observers) {
            observer.update(this, data);
        }
    }

    /**
     * Controller tells model to load a new puzzle file: When loading,
     * the user will provide the path and name of a puzzle file for the
     * game to load. If the file is readable it is guaranteed to be a
     * valid puzzle file and the new puzzle file should be loaded and
     * displayed, along with an indication of success. If the file cannot
     * be read, an error message should be displayed and the
     * previous puzzle file should remain loaded.
     * @throws IOException the input/output exception
     */
    public void load(String filename) throws IOException {
        // data/chess/chess-7.txt
        String[] file = filename.split("/");
        // [data, chess, chess-7.txt]
        try (FileReader ignored = new FileReader(filename)){
            currentConfig = new ChessConfig(filename);
            alertObservers("Loaded: " + file[file.length-1]);
            prevFile = filename;
        } catch (FileNotFoundException e) {
            alertObservers("Failed to load: " + file[file.length - 1]);
        }
    }

    /**
     * When hinting, if the current state of the puzzle is solvable,
     * the puzzle should advance to the next step in the solution
     * with an indication that it was successful.
     * Otherwise, the puzzle should remain in the same state and indicate
     * there is no solution.
     */
    public void hint(){
        ChessMoves.setPTUI(false);
        Solver solver = new Solver(currentConfig);
        ArrayList<Configuration> hints = (ArrayList<Configuration>) solver.solve();
        if (hints != null && hints.size() >= 2){
            currentConfig = (ChessConfig) hints.get(1);
            alertObservers("Next step!");
        } else if (currentConfig.isSolution()) {
            alertObservers("Solved");
        } else {
            alertObservers("No Solution");
        }
    }

    /**
     * Selection works in two parts.
     *      For the first selection, the user should be able to select a cell
     *   on the board with the intention of selecting the piece at that
     *   location.
     *      For the second selection, the user should be able to select another
     *  cell on the board with the intention of moving the previously
     *  selected piece to this location.
     */
    public void select(int row, int col){
        if (isFirstSelection){
            firstSelection(row, col);
        } else {
            secondSelection(row, col);
        }
    }

    /**
     * Determines if the contents of the first selected cell of the board
     * contains anything
     */
    public void firstSelection(int row, int col){
        currCoordinates = new Coordinates(row, col);
        chessPiece = ChessConfig.getCell(row, col, currentConfig);
        if (ChessConfig.isCaptureTarget(chessPiece)) {
            // if there is a piece there, there should be an indication + selection
            // should advance to the second part
            alertObservers("Selected (" + row + ", " + col + ")");
            isFirstSelection = false;
        } else {
            // Otherwise display an error message that there's nothing there
            alertObservers("Invalid selection (" + row + ", " + col + ")");
        }
    }

    /**
     * Determines if the second selected cell on the board is a valid move
     */
    public void secondSelection(int row, int col){
        // for the second selection, the user should be able to select another cell on the board
        // if the selection place is valid
        switch (chessPiece){
            // move the previously selected piece to this location
            case ChessConfig.BISHOP -> chessMoves(row, col, ChessConfig.BISHOP);
            case ChessConfig.KING -> chessMoves(row, col, ChessConfig.KING);
            case ChessConfig.KNIGHT -> chessMoves(row, col, ChessConfig.KNIGHT);
            case ChessConfig.PAWN -> chessMoves(row, col, ChessConfig.PAWN);
            case ChessConfig.QUEEN -> chessMoves(row, col, ChessConfig.QUEEN);
            case ChessConfig.ROOK -> chessMoves(row, col, ChessConfig.ROOK);
        }
        isFirstSelection = true;
    }

    /**
     * helper function for the second selection process
     * @param newRow the new row index to attempt to capture
     * @param newCol the new column index to attempt to capture
     * @param chessPiece the chess we've currently selected
     *                   that has a specific / limited number of moves
     */
    private void chessMoves(int newRow, int newCol, char chessPiece){
        ChessMoves.setPTUI(true);
        ArrayList<Object> moves = new ArrayList<>();
        Collections.addAll(moves, ChessMoves.getValidMoves(chessPiece, currentConfig, currCoordinates).toArray());

        newCoordinates = new Coordinates(newRow,  newCol);
        // if the coordinates the user selected is a valid capture to make
        if (moves.contains(newCoordinates)){
            // the board should be updated with an appropriate indication
            ChessConfig.moveTo(currCoordinates.row(), currCoordinates.col(), newRow, newCol, currentConfig);
            alertObservers("Captured from (" + currCoordinates.row() + ", " + currCoordinates.col() +
                    ") to " + "(" + newRow + ", " + newCol + ")");
        } else {
            // if the move is invalid, an error message should be displayed
            alertObservers("Can't capture from (" + currCoordinates.row() + ", " + currCoordinates.col() +
                    ") to " + "(" + newRow + ", " + newCol + ")");
        }
    }

    /**
     * The previously loaded file should be reloaded, causing the puzzle
     * to return to its initial state. An indication of the reset should
     * be informed to the user.
     */
    public void reset() throws IOException {
        load(prevFile);
        alertObservers("Puzzle reset!");
    }

    /**
     * returns the string representation of the Chess Model with
     * numbered rows and columns
     */
    @Override
    public String toString(){
        StringBuilder gridString = new StringBuilder();
        gridString.append("   ");
        for(int c = 0; c < currentConfig.WIDTH; c++){
            gridString.append(c).append(" ");
        }
        gridString.append("\n  ");
        gridString.append("-".repeat(Math.max(0, 2 * currentConfig.WIDTH)));
        gridString.append("\n");
        for(int r = 0; r < currentConfig.LENGTH; r++){
            gridString.append(r).append("| ");
            for(int c = 0; c < currentConfig.LENGTH; c++){
                gridString.append(ChessConfig.getCell(r, c, currentConfig)).append(" ");
            }
            gridString.append("\n");
        }
        return gridString.toString();
    }

    public int getRows(){
        return currentConfig.LENGTH;
    }

    public int getCols(){
        return currentConfig.WIDTH;
    }


}
