package puzzles.chess.model;

import puzzles.common.Coordinates;
import puzzles.common.solver.Configuration;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Represents the current config for the chess board. Thus,
 * the configuration class holds the representation of the puzzle,
 * while the model serves as the main class to interact with the
 * controller and view.
 *
 * @author jolin qiu
 *
 */
public class ChessConfig implements Configuration {
    /** dimensions of the chess board's length (number of rows) */
    public static int LENGTH;
    /** dimensions of the chess board's width (number of columns) */
    public static int WIDTH;
    /** An empty cell */
    public final static char EMPTY = '.';
    /** A cell occupied with a Bishop */
    public final static char BISHOP = 'B';
    /** A cell occupied with a King */
    public final static char KING = 'K';
    /** A cell occupied with a Knight */
    public final static char KNIGHT = 'N';
    /** A cell occupied with a Pawn */
    public final static char PAWN = 'P';
    /** A cell occupied with a Queen */
    public final static char QUEEN = 'Q';
    /** A cell occupied with a Rook */
    public final static char ROOK = 'R';

    /** the grid of cells that can contain the various chess pieces */
    private char[][] chessBoard;


    /**
     * Constructor that builds the Chess board.
     * The first line represents the dimensions of the board.
     * It won't always be a square
     * Using chess-4.txt as an example
     * 4 4
     * B . P K
     * N . . P
     * . . P Q
     * R . . P
     *
     * @param filename      the name of the file to process
     * @throws IOException  the filename wasn't valid
     */
    public ChessConfig(String filename) throws IOException {
        try (BufferedReader in = new BufferedReader(new FileReader(filename))) {
            // get the field dimensions
            String[] dimensions = in.readLine().split(" ");
            // [4, 4] 0 = number of rows, 1 = number of columns
            LENGTH = Integer.parseInt(dimensions[0]);
            WIDTH = Integer.parseInt(dimensions[1]);
            // board configuration / populating the board with chars
            this.chessBoard = new char[LENGTH][WIDTH];
            // String[] line = in.readLine().split(" ");
            char[] line = in.readLine().replaceAll(" ", "").toCharArray();

            // next line = [B . P K]
            for (int cursorRow = 0; cursorRow < LENGTH; cursorRow++) {
                for (int cursorCol = 0; cursorCol < WIDTH; cursorCol++) {
                    char chessPiece = line[cursorCol];
                    chessBoard[cursorRow][cursorCol] = chessPiece;
                }
                String nextLine = in.readLine();
                if (nextLine != null) {
                    line = nextLine.replaceAll(" ", "").toCharArray();
                }
            }
            // set the cursor off the board

        } catch (FileNotFoundException e) {
            System.err.println(e);
            // handle exception
        }
    }

    /**
     * Copy constructor.
     * Takes a config, other, and makes a full "deep" copy
     * of its instance data.
     * @param other the config to copy
     */
    public ChessConfig(ChessConfig other){
        // create copy of the board
        this.chessBoard = new char[LENGTH][WIDTH];
        for (int row = 0; row < LENGTH; row++){
            System.arraycopy(other.chessBoard[row], 0,
                    // width or length?
                    this.chessBoard[row], 0, WIDTH);
            }
        }


    /**
     * When the solution is found there will be one piece remaining on the board.
     * @return true if one piece was remaining, false otherwise
     */
    @Override
    public boolean isSolution() {
        Stack<Object> piecesLeft = new Stack<>();
        for (int row = 0; row < LENGTH; row++) {
            for (int col = 0; col < WIDTH; col++) {
                if (isCaptureTarget(chessBoard[row][col])) {
                    piecesLeft.push(chessBoard[row][col]);
                }
            }
        }
        Object testPiece = piecesLeft.pop();
        if (piecesLeft.isEmpty()) {
            return true;
        } else {
            // put back the piece lol
            piecesLeft.push(testPiece);
            return false;
        }
    }

    /**
     * Gets the possible configurations of neighbors
     * each move has to be a valid CAPTURE
     * @return the list of neighbors
     */
    @Override
    public Collection<Configuration> getNeighbors() {
        List<Configuration> neighbors = new ArrayList<>();

        // for each cell, there is a configuration able to be made
        for (int row=0; row < LENGTH; row++){
            for(int col=0; col < WIDTH; col++){
                Coordinates current = new Coordinates(row, col);
                // create a copy config and add it to the successor
                ChessConfig child = new ChessConfig(this);
                char chessPiece = child.chessBoard[row][col];
                switch (chessPiece) {
                    // makes new [chessPiece] configuration(s) at next valid capture coordinates
                    case BISHOP -> ChessMoves.makeBishopConfigs(neighbors, child, current);
                    case KING -> ChessMoves.makeKingConfigs(neighbors, child, current);
                    case KNIGHT -> ChessMoves.makeKnightConfigs(neighbors, child, current);
                    case PAWN -> ChessMoves.makePawnConfigs(neighbors, child, current);
                    case QUEEN -> ChessMoves.makeQueenConfigs(neighbors, child, current);
                    case ROOK -> ChessMoves.makeRookConfigs(neighbors, child, current);
                    // skip to the next cell where you can find neighbors
                    default -> {
                        // the current cell is an EMPTY and doesn't need neighbor configurations made
                    }
                }
            }
        }
        return neighbors;
    }


    /**
     * Checks if a chessboard configuration is equal to another
     * @param other the other board config
     * @return      true if the two boards are equal, false otherwise
     */
    @Override
    public boolean equals(Object other){
        // type casting obj to ChessConfig
        ChessConfig o = (ChessConfig) other;
        return Arrays.deepEquals(this.chessBoard, o.chessBoard);
    }

    /**
     * @return The hashcode of the current string.
     */
    @Override
    public int hashCode() {
        return Arrays.deepHashCode(this.chessBoard);
    }

    /**
     * Get the string representation of the configuration.
     * @return the complete string
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("\n");
        for (char[] chars : chessBoard) {
            for (char aChar : chars) {
                result.append(aChar).append(" ");
            }
            result.append("\n");
        }
        return result.toString();
    }

    /**
     * Get the contents at a cell.
     * @param row the row
     * @param col the column
     * @return the contents
     */
    public static char getCell(int row, int col, ChessConfig child) {
        return child.chessBoard[row][col];
    }

    /**
     * Set new cell contents
     * @param row   the desired row
     * @param col   the desired col
     * @param child the configuration
     * @param piece the chessPiece the cell contents should be set to
     */
    public static void setCell(int row, int col, ChessConfig child, char piece){
        child.chessBoard[row][col] = piece;
    }

    /**
     * if the cell is a capture target, it has any piece. Otherwise, the cell is empty
     * @param cell  the space on the chessBoard
     * @return      true if the cell is not empty
     */
    public static boolean isCaptureTarget(char cell){
        return cell != EMPTY;
    }
}
