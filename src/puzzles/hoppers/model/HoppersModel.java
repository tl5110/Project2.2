package puzzles.hoppers.model;

import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;
import puzzles.common.Observer;
import java.util.*;
import java.io.*;

/**
 * The model for the hopper game
 *
 * @author Tiffany Lee
 */

public class HoppersModel {
    /** the collection of observers of this model */
    private final List<Observer<HoppersModel, String>> observers = new LinkedList<>();
    /** the current configuration */
    private HoppersConfig currentConfig;
    /** total number of rows in the hopper board */
    private int rows;
    /** total number of columns in the hopper board */
    private int cols;
    /** the initial row that was selected */
    private int startRow;
    /** the initial column that was selected */
    private int startCol;
    /** helps keep track if select has been called once or twice */
    private boolean isFirstSelect = true;
    /** the name of the previous file */
    private String prevFile;

    /**
     * Constructs the hopper model from the string of a filename
     *
     * @param filename the name of the file to read from
     * @throws IOException if the file is not found or there are errors reading
     */
    public HoppersModel(String filename) throws IOException {
        this.currentConfig = new HoppersConfig(filename);
        this.rows = currentConfig.getRows();
        this.cols = currentConfig.getCols();
        this.prevFile = filename;
    }

    /**
     * The view calls this to add itself as an observer.
     *
     * @param observer the view
     */
    public void addObserver(Observer<HoppersModel, String> observer) {
        this.observers.add(observer);
    }

    /**
     * The model's state has changed (the counter), so inform the view via
     * the update method
     */
    private void alertObservers(String msg) {
        for (var observer : observers) {
            observer.update(this, msg);
        }
    }

    /**
     * Gets the total amount of rows on the board.
     * @return total amount of rows on the board
     */
    public int getRows() { return rows; }

    /**
     * Gets the total amount of columns on the board.
     * @return total amount of columns on the board
     */
    public int getCols() { return cols; }

    /**
     * Gets the contents at a cell.
     * @param r the row
     * @param c the column
     * @return the contents
     */
    public char getCell(int r, int c){ return currentConfig.getCell(r, c); }

    /**
     * Takes provided name of a hopper puzzle file for the game to load
     *
     * @param filename the name of a file to read from
     * @throws IOException if the file is not found or there are errors reading
     */
    public void load(String filename) throws IOException {
        String[] file = filename.split("/");
        try(FileReader ignored = new FileReader(filename)){
            currentConfig = new HoppersConfig(filename);
            this.rows = currentConfig.getRows();
            this.cols = currentConfig.getCols();
            prevFile = filename;
            alertObservers("Loaded: " + file[file.length-1]);
        } catch (FileNotFoundException e){
            alertObservers("Failed to load: " + file[file.length-1]);
        }
    }

    /**
     * Previously loaded file is reloaded, causing the puzzle to return to
     * its initial state
     *
     * @throws IOException if the file is not found or there are errors reading
     */
    public void reset() throws IOException {
        load(prevFile);
        alertObservers("Puzzle reset!");
    }

    /**
     * If the current state of the hopper puzzle is solvable, the puzzle
     * advances to the next step in the solution with an indication that was
     * successful. Other-wise the puzzle remains in the same state and
     * indicates there is no solution.
     */
    public void hint(){
        Solver solver = new Solver(currentConfig);
        ArrayList<Configuration> hints = (ArrayList<Configuration>) solver.solve();
        if (hints != null && hints.size() >= 2) {
            currentConfig = (HoppersConfig) hints.get(1);
            alertObservers("Next step!");
        } else {
            alertObservers("No Solution");
        }
    }

    /**
     * In the first selection, the user selects a cell with a piece at that
     * location on the board. If there is a piece, there should be an
     * indication and selection should advance to the second part. Other-wise
     * an error message should be displayed and the selection ends.
     * In the second selection, the user selects another cell with the intention
     * of moving the previously selected piece to this location. If the move is
     * valid, it should be made and the board should be updated along with an
     * appropriate indication. Other-wise an error message should be displayed
     *
     * @param r the row
     * @param c the column
     */
    public void select(int r, int c){
        if(isFirstSelect){
            firstSelect(r,c);
        } else {
            secondSelect(r, c);
        }
    }

    /**
     * Determines if the contents of the first selected cell on the board
     * is a frog
     *
     * @param r the row
     * @param c the column
     */
    public void firstSelect(int r, int c){
        if((r >= 0 && r < rows) && (c >= 0 && c < cols) &&
                (currentConfig.getCell(r, c) == HoppersConfig.GREEN_FROG
                        || currentConfig.getCell(r, c) == HoppersConfig.RED_FROG)){
            startRow = r;
            startCol = c;
            isFirstSelect = false;
            alertObservers("Selected (" + r + ", " + c + ")");
        } else {
            alertObservers("No frog at (" + r + ", " + c + ")");
        }
    }

    /**
     * Determines if the second selected cell on the board is a valid move
     *
     * @param r the row
     * @param c the column
     */
    public void secondSelect(int r, int c){
        isFirstSelect = true;
        if(validJump(startRow, startCol, r, c)){
            currentConfig.move(startRow, startCol, r, c);
            alertObservers("Jumped from (" + startRow + ", " + startCol +
                    ") to " + "(" + r + ", " + c + ")");
        } else {
            alertObservers("Can't jump from (" + startRow + ", " + startCol +
                    ") to " + "(" + r + ", " + c + ")");
        }
    }

    /**
     * Determines if the jump/move from the starting cell to the end cell
     * is valid
     *
     * @param startRow the starting row
     * @param startCol the starting column
     * @param endRow the end row
     * @param endCol the end column
     * @return true if the jump is valid, false otherwise
     */
    public boolean validJump(int startRow, int startCol, int endRow, int endCol){
        int rowDif = Math.abs(startRow-endRow);
        int colDif = Math.abs(startCol-endCol);
        if((rowDif==colDif && rowDif==2) || (rowDif==4 && colDif==0) || (rowDif==0 && colDif==4)){
            int midRow = (startRow+endRow)/2;
            int midCol = (startCol+endCol)/2;
            if((endRow>=0 && endRow<rows) && (endCol>=0 && endCol<cols)){
                return currentConfig.getCell(midRow, midCol) == HoppersConfig.GREEN_FROG
                        && currentConfig.getCell(endRow, endCol) == HoppersConfig.LILY_PAD;
            }
        }
        return false;
    }

    /**
     * Get string representation of the hopper model
     *
     * @return the complete string
     */
    @Override
    public String toString(){
        StringBuilder gridString = new StringBuilder();
        gridString.append("   ");
        for(int c = 0; c < cols; c++){
            gridString.append(c).append(" ");
        }
        gridString.append("\n  ");
        gridString.append("-".repeat(Math.max(0, 2 * cols)));
        gridString.append("\n");
        for(int r = 0; r < rows; r++){
            gridString.append(r).append("| ");
            for(int c = 0; c < cols; c++){
                gridString.append(currentConfig.getCell(r, c)).append(" ");
            }
            gridString.append("\n");
        }
        return gridString.toString();
    }
}
