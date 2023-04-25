package puzzles.hoppers.model;

import puzzles.common.Observer;
import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

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
    /** the initial row that was selected */
    private int startRow;
    /** the initial column that was selected */
    private int startCol;
    /** helps keep track if select has been called once or twice */
    private boolean isFirstSelect = true;

    /**
     * Constructs the hopper model from the string of a filename
     *
     * @param filename the name of the file to read from
     * @throws IOException if the file is not found or there are errors reading
     */
    public HoppersModel(String filename) throws IOException {
        this.currentConfig = new HoppersConfig(filename);
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
     * Takes provided name of a hopper puzzle file for the game to load
     *
     * @param filename the name of a file to read from
     * @throws IOException if the file is not found or there are errors reading
     */
    public void load(String filename) throws IOException {
        // How to do this without FileReader
        try(FileReader file = new FileReader("data/hoppers/"+filename)){
            currentConfig = new HoppersConfig("data/hoppers/"+filename);
            alertObservers("Loaded: " + filename);
        } catch (FileNotFoundException e){
            alertObservers("Failed to load: " + filename);
        }
    }

    /**
     * Previously loaded file is reloaded, causing the puzzle to return to
     * its initial state
     *
     * @param filename filename of previously loaded file
     * @throws IOException if the file is not found or there are errors reading
     */
    public void reset(String filename) throws IOException {
        currentConfig = new HoppersConfig(filename);
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
        if (hints != null) {
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
     * @param row the row
     * @param col the column
     */
    public void select(int row, int col){
        if(isFirstSelect){
            firstSelect(row,col);
        } else {
            secondSelect(row, col);
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
        if(currentConfig.getCell(r, c) == 'G' || currentConfig.getCell(r, c) == 'R'){
            startRow = r;
            startCol = c;
            isFirstSelect = false;
            alertObservers("Selected (" + r + ", " + c + ")");
        } else {
            alertObservers("No frog");
        }
    }

    /**
     * Determines if the second selected cell on the board is a valid move
     *
     * @param r the row
     * @param c the column
     */
    public void secondSelect(int r, int c){
        if(validJump(startRow, startCol, r, c)){
            currentConfig.move(startRow, startCol, r, c);
            alertObservers("Jumped from (" + startRow + ", " + startCol +
                    ") to " + "(" + r + ", " + c + ")");
        } else {
            alertObservers("Can't jump from (" + startRow + ", " + startCol +
                    ") to " + "(" + r + ", " + c + ")");
        }
        isFirstSelect = true;
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
        int midRow = (startRow+endRow)/2;
        int midCol = (startCol+endCol)/2;
        if((endRow>=0 && endRow<currentConfig.getRows()) && (endCol>=0 && endCol< currentConfig.getCols())){
            return currentConfig.getCell(midRow, midCol) == 'G' && currentConfig.getCell(endRow, endCol) == '.';
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
        gridString.append("""
                   0 1 2 3 4
                  ----------
                """);
        for(int r = 0; r < currentConfig.getRows(); r++){
            gridString.append(r).append("| ");
            for(int c = 0; c < currentConfig.getCols(); c++){
                gridString.append(currentConfig.getCell(r, c)).append(" ");
            }
            gridString.append("\n");
        }
        return gridString.toString();
    }
}
