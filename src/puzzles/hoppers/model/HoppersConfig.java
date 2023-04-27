package puzzles.hoppers.model;
import puzzles.common.solver.Configuration;
import java.util.*;
import java.io.*;

/**
 * Representation of a configuration of the Hoppers puzzle.
 * This class holds the representation of the puzzle while the model
 * serves as the main class to interact with the controller and view.
 *
 * @author Tiffany Lee
 */
public class HoppersConfig implements Configuration{
    /** grid of cells containing lily-pad(.), water(*), green frog(G), or red frog(R) */
    private char[][] grid;
    /** total number of rows in the hopper board */
    private int rows;
    /** total number of columns in the hopper board */
    private int cols;
    /** red frog character */
    public static final char RED_FROG = 'R';
    /** green frog character */
    public static final char GREEN_FROG = 'G';
    /** lily-pad character */
    public static final char LILY_PAD = '.';
    /** water character */
    public static final char WATER = '*';

    /**
     * Constructs the initial configuration from an input file whose contents
     * are, for example hoppers-4.txt:
     * <pre>
     *     5 5          # rows and columns in the board
     *     G * G * R    # row 1 - G=green frog, R=red frog, *=invalid space/water
     *     * G * . *    # row 2 - .=valid empty space/lily-pad
     *     . * G * G    # row 3
     *     * . * G *    # row 4
     *     . * . * G    # row 5
     * </pre>
     * @param filename the name of the file to read from
     * @throws IOException if the file is not found or there are errors reading
     */
    public HoppersConfig(String filename) throws IOException {
        try(BufferedReader file = new BufferedReader(new FileReader(filename))){
            String[] dimensions = file.readLine().split("\\s+");
            rows = Integer.parseInt(dimensions[0]);
            cols = Integer.parseInt(dimensions[1]);
            this.grid = new char[rows][cols];
            for(int r = 0; r < rows; r++){
                char[] row = file.readLine().replaceAll("\\s+", "").toCharArray();
                for(int c = 0; c < cols; c++){
                    char cell = row[c];
                    this.grid[r][c] = cell;
                }
            }
        } catch (FileNotFoundException e){
            System.err.println("Could not find file.");
            System.exit(1);
        }
    }

    /**
     * Copy constructor. Takes a config other, and makes a full "deep" copy
     * of its instance data.
     *
     * @param other the config to copy
     */
    private HoppersConfig(HoppersConfig other){
        this.grid = new char[other.rows][other.cols];
        this.rows = other.rows;
        this.cols = other.cols;
        for(int r = 0; r < other.rows; r++){
            System.arraycopy(other.grid[r], 0, this.grid[r], 0, cols);
        }
    }

    /**
     * Gets the contents at a cell.
     *
     * @param r the row
     * @param c the column
     * @return the contents
     */
    public char getCell(int r, int c){ return grid[r][c]; }

    /**
     * Gets the total amount of rows on the board.
     *
     * @return total amount of rows on the board
     */
    public int getRows() { return rows; }

    /**
     * Gets the total amount of columns on the board.
     *
     * @return total amount of columns on the board
     */
    public int getCols() { return cols; }

    /**
     * Moves a frog from one space to another on the board
     *
     * @param startRow start row
     * @param startCol start column
     * @param endRow end row
     * @param endCol end column
     */
    public void move(int startRow, int startCol, int endRow, int endCol){
        int midRow = (startRow+endRow)/2;
        int midCol = (startCol+endCol)/2;
        grid[endRow][endCol] = grid[startRow][startCol];
        grid[midRow][midCol] = LILY_PAD;
        grid[startRow][startCol] = LILY_PAD;
    }

    /**
     * Is the row and column inside the board's boundaries?
     *
     * @param row the row
     * @param col the column
     * @return true if the position is valid, false otherwise
     */
    public boolean isValidCoordinate(int row, int col){
        return (row>=0 && row<rows) && (col>=0 && col<cols);
    }

    /**
     * Will this move jump over a green frog and land on an empty lily-pad?
     *
     * @param move 2-D integer array that gives the cell the frog hops over
     *             and the cell that the frog lands on
     * @return true if valid, false otherwise
     */
    public boolean isValidMove(Integer[][] move){
        if(isValidCoordinate(move[0][0], move[0][1]) && isValidCoordinate(move[1][0], move[1][1])){
            return (grid[move[0][0]][move[0][1]] == GREEN_FROG) && (grid[move[1][0]][move[1][1]] == LILY_PAD);
        }
        return false;
    }

    /**
     * Gives all possible moves from one cell.
     *
     * @param r the row
     * @param c the column
     * @return list of all the possible moves from one cell
     */
    public ArrayList<Integer[][]> getMoves(int r, int c){
        ArrayList<Integer[][]> neighborCoordinates = new ArrayList<>();
        if((r%2 != 0) && (c%2 != 0)){
            //Diagonals
            neighborCoordinates.add(new Integer[][]{{r-1, c-1}, {r-2, c-2}});
            neighborCoordinates.add(new Integer[][]{{r-1, c+1}, {r-2, c+2}});
            neighborCoordinates.add(new Integer[][]{{r+1, c+1}, {r+2, c+2}});
            neighborCoordinates.add(new Integer[][]{{r+1, c-1}, {r+2, c-2}});
        } else {
            // Diagonals
            neighborCoordinates.add(new Integer[][]{{r-1, c-1}, {r-2, c-2}});
            neighborCoordinates.add(new Integer[][]{{r-1, c+1}, {r-2, c+2}});
            neighborCoordinates.add(new Integer[][]{{r+1, c+1}, {r+2, c+2}});
            neighborCoordinates.add(new Integer[][]{{r+1, c-1}, {r+2, c-2}});
            // Horizontals
            neighborCoordinates.add(new Integer[][]{{r-2, c}, {r-4, c}});
            neighborCoordinates.add(new Integer[][]{{r+2, c}, {r+4, c}});
            // Verticals
            neighborCoordinates.add(new Integer[][]{{r, c+2}, {r, c+4}});
            neighborCoordinates.add(new Integer[][]{{r, c-2}, {r, c-4}});
        }
        return neighborCoordinates;
    }

    /**
     * Gets the collection of neighbors from the current cell.
     *
     * @return valid neighbors
     */
    @Override
    public Collection<Configuration> getNeighbors() {
        Collection<Configuration> neighborsList = new ArrayList<>();
        for(int r = 0; r < rows; r++){
            for(int c = 0; c < cols; c++){
                if(grid[r][c] == RED_FROG || grid[r][c] == GREEN_FROG){
                    ArrayList<Integer[][]> neighborCoordinates = getMoves(r, c);
                    for (Integer[][] move: neighborCoordinates) {
                        if (isValidMove(move)){
                            int midRow = move[0][0];
                            int midCol = move[0][1];
                            int endRow = move[1][0];
                            int endCol = move[1][1];
                            HoppersConfig neighbor = new HoppersConfig(this);
                            neighbor.grid[r][c] = LILY_PAD;
                            neighbor.grid[midRow][midCol] = LILY_PAD;
                            neighbor.grid[endRow][endCol] = this.grid[r][c];
                            neighborsList.add(neighbor);
                        }
                    }
                }
            }
        }
        return neighborsList;
    }

    /**
     * Is the current configuration the solution?
     *
     * @return true if solution, false otherwise
     */
    @Override
    public boolean isSolution() {
        for(int r = 0; r < rows; r++){
            for(int c = 0; c < cols; c++){
                if(grid[r][c] == GREEN_FROG){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Checks if a hopper board configuration is equal to another.
     *
     * @param other the other board config
     * @return true if boards are equal, false otherwise
     */
    @Override
    public boolean equals(Object other){
        if(other instanceof HoppersConfig otherHopper){
            return Arrays.deepEquals(this.grid, otherHopper.grid);
        }
        return false;
    }

    /**
     * @return the hashcode of the current configuration
     */
    @Override
    public int hashCode() {
        return Arrays.deepHashCode(this.grid);
    }

    /**
     * Get string representation of the configuration.
     *
     * @return the complete string
     */
    @Override
    public String toString(){
        StringBuilder gridString = new StringBuilder();
        for(int r = 0; r < rows; r++){
            for(int c = 0; c < cols; c++){
                gridString.append(grid[r][c]).append(" ");
            }
            gridString.append("\n");
        }
        return gridString.toString();
    }
}