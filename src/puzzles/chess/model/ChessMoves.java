package puzzles.chess.model;
import puzzles.common.Coordinates;
import puzzles.common.solver.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static puzzles.chess.model.ChessConfig.*;

/**
 * Helper class that provides methods to retrieve
 * the valid moves that can be made from various directions, and by each chess piece:
 * Diagonally, anti-diagonal, vertical, horizontal, multiple directions, etc.
 *
 * @author jolin qiu
 */


public class ChessMoves {

    /** whether the current program is a PTUI/GUI or Solver determines how coordinates will be
     * returned */
    private static boolean isPTUI;

    /** stack of the valid moves a chess piece can make for a given configuration */
    private static Stack<Coordinates> validMoves;


    /**
     * gets the valid moves
     * @param chessPiece the current chess piece
     * @param currentConfig the current chess board configuration
     * @param currCoord the current coordinate to check if spot is valid
     * @return the stack of the valid coordinates for the given piece configuration
     */
    public static Stack<Coordinates> getValidMoves(char chessPiece,
                                                       ChessConfig currentConfig,
                                                       Coordinates currCoord) {
        validMoves = new Stack<>();
//        return validMoves;
        ArrayList neighbors = new ArrayList<>();
        switch (chessPiece) {
            // makes new [chessPiece] configuration(s) at next valid capture coordinates
            case BISHOP -> ChessMoves.makeBishopConfigs(neighbors, currentConfig, currCoord);
            case KING -> ChessMoves.makeKingConfigs(neighbors, currentConfig, currCoord);
            case KNIGHT -> ChessMoves.makeKnightConfigs(neighbors, currentConfig, currCoord);
            case PAWN -> ChessMoves.makePawnConfigs(neighbors, currentConfig, currCoord);
            case QUEEN -> ChessMoves.makeQueenConfigs(neighbors, currentConfig, currCoord);
            case ROOK -> ChessMoves.makeRookConfigs(neighbors, currentConfig, currCoord);
        }
        return validMoves;
    }

    /**
     * Creates the configurations for the specified chess piece
     *
     * @param neighbors  the list of successors to return
     * @param child      the child configuration
     * @param validMoves the valid moves, if any, that should have their own configurations
     * @param chessPiece the specified chess piece to move on the board
     */
    public static void getConfigurations(List<Configuration> neighbors,
                                         ChessConfig child,
                                         Stack<Coordinates> validMoves,
                                         char chessPiece,
                                         Coordinates current) {
        while (!validMoves.isEmpty()){
            ChessConfig newChild = new ChessConfig(child);
            ChessConfig.setCell(current.row(), current.col(), newChild, ChessConfig.EMPTY);
            Coordinates newCoord = validMoves.pop();
            ChessConfig.setCell(newCoord.row(),newCoord.col(), newChild, chessPiece);
            neighbors.add(newChild);
        }
    }

    /** retrieves the coordinates for use in making neighbors for a BISHOP configuration
     * @param neighbors the neighbors to update
     * @param child the configuration
     * @param current the current coordinates
     */
    public static void makeBishopConfigs(List<Configuration> neighbors, ChessConfig child, Coordinates current){
        // new bishop configuration(s) at the next valid capture coordinate
//        Stack<Coordinates> validMoves = ChessMoves.getValidDiagonals(child, current);
        validMoves = ChessMoves.getValidDiagonals(child, current);
        if (!isPTUI){
            getConfigurations(neighbors, child, validMoves, BISHOP, current);
        }
    }

    /** retrieves the coordinates for use in making neighbors for a ROOK configuration */
    public static void makeRookConfigs(List<Configuration> neighbors, ChessConfig child, Coordinates current) {
//        Stack<Coordinates> validMoves = ChessMoves.getValidHorizontals(child, current);
        validMoves = ChessMoves.getValidHorizontals(child, current);
        validMoves.addAll(ChessMoves.getValidVerticals(child, current));
        if (!isPTUI){
            getConfigurations(neighbors, child, validMoves, ChessConfig.ROOK, current);
        }
    }

    /** retrieves the coordinates for use in making neighbors for a QUEEN configuration */
    public static void makeQueenConfigs(List<Configuration> neighbors, ChessConfig child, Coordinates current) {
//        Stack<Coordinates> validMoves = ChessMoves.getValidHorizontals(child, current);
        validMoves = ChessMoves.getValidHorizontals(child, current);
        validMoves.addAll(ChessMoves.getValidVerticals(child, current));
        validMoves.addAll(ChessMoves.getValidDiagonals(child, current));
        if (!isPTUI){
            getConfigurations(neighbors, child, validMoves, ChessConfig.QUEEN, current);
        }
    }

    /** retrieves the coordinates for use in making neighbors for a PAWN configuration */
    public static void makePawnConfigs(List<Configuration> neighbors, ChessConfig child, Coordinates current) {
        validMoves = new Stack<>();
        // fwd spots will always be pawn's current row -1
        int row = current.row();
        int col = current.col();
        // left diagonal = cur[col] -1
        if (ChessMoves.isValidBounds( row-1, col-1, ChessConfig.LENGTH, ChessConfig.WIDTH)) {
            if (ChessConfig.isCaptureTarget(ChessConfig.getCell(row-1, col-1, child))){
                validMoves.push(new Coordinates(row-1, col-1));
            }
        }
        // right diagonal = cur[Col] + 1
        if (ChessMoves.isValidBounds(row-1, col+1, ChessConfig.LENGTH, ChessConfig.WIDTH)) {
            if (ChessConfig.isCaptureTarget(ChessConfig.getCell(row-1, col+1, child))){
                validMoves.push(new Coordinates(row-1, col+1));
            }
        }
        if (!isPTUI){
            getConfigurations(neighbors, child, validMoves, ChessConfig.PAWN, current);

        }
    }

    /** retrieves the coordinates for use in making neighbors for a KNIGHT configuration */
    public static void makeKnightConfigs(List<Configuration> neighbors, ChessConfig child, Coordinates current) {
        validMoves = new Stack<>();

        int row = current.row();
        int col = current.col();

        if (isValidBounds(row - 1, col - 2, ChessConfig.LENGTH, ChessConfig.WIDTH)){
            if (ChessConfig.isCaptureTarget(ChessConfig.getCell(row-1, col-2, child))){
                validMoves.push(new Coordinates(row-1, col-2));
            }
        }
        if (isValidBounds(row + 1, col - 2, ChessConfig.LENGTH, ChessConfig.WIDTH)){
            if (ChessConfig.isCaptureTarget(ChessConfig.getCell(row+1, col-2, child))){
                validMoves.push(new Coordinates(row+1, col-2));
            }
        }
        if (isValidBounds(row + 1, col + 2, ChessConfig.LENGTH, ChessConfig.WIDTH)){
            if (ChessConfig.isCaptureTarget(ChessConfig.getCell(row+1, col+2, child))){
                validMoves.push(new Coordinates(row+1, col+2));
            }
        }
        if (isValidBounds(row - 1,col + 2, ChessConfig.LENGTH, ChessConfig.WIDTH)){
            if (ChessConfig.isCaptureTarget(ChessConfig.getCell(row-1, col+2, child))){
                validMoves.push(new Coordinates(row-1, col+2));
            }
        }
        if (isValidBounds(row - 2, col + 1, ChessConfig.LENGTH, ChessConfig.WIDTH)){
            if (ChessConfig.isCaptureTarget(ChessConfig.getCell(row-2, col+1, child))){
                validMoves.push(new Coordinates(row-2, col+1));
            }
        }
        if (isValidBounds(row + 2, col + 1, ChessConfig.LENGTH, ChessConfig.WIDTH)){
            if (ChessConfig.isCaptureTarget(ChessConfig.getCell(row+2, col+1, child))){
                validMoves.push(new Coordinates(row+2, col+1));
            }
        }
        if (isValidBounds(row + 2, col - 1, ChessConfig.LENGTH, ChessConfig.WIDTH)){
            if (ChessConfig.isCaptureTarget(ChessConfig.getCell(row+2, col-1, child))){
                validMoves.push(new Coordinates(row+2, col-1));
            }
        }
        if (isValidBounds(row - 2, col - 1, ChessConfig.LENGTH, ChessConfig.WIDTH)){
            if (ChessConfig.isCaptureTarget(ChessConfig.getCell(row-2, col-1, child))){
                validMoves.push(new Coordinates(row-2, col-1));
            }
        }
        if (!isPTUI){
            getConfigurations(neighbors, child, validMoves, KNIGHT, current);
        }
    }


    /** retrieves the coordinates for use in making neighbors for a KING configuration */
    public static void makeKingConfigs(List<Configuration> neighbors, ChessConfig child, Coordinates current) {
        validMoves = new Stack<>();
        int i = current.row();
        int j = current.col();

        // Checking for all the possible adjacent positions
        if (ChessMoves.isValidBounds(i - 1, j - 1, ChessConfig.LENGTH, ChessConfig.WIDTH)) {
            if (ChessConfig.isCaptureTarget(ChessConfig.getCell(i-1, j-1, child))){
                validMoves.push(new Coordinates(i-1, j-1));
            }
        }
        if (ChessMoves.isValidBounds(i - 1, j, ChessConfig.LENGTH, ChessConfig.WIDTH)) {
            if (ChessConfig.isCaptureTarget(ChessConfig.getCell(i-1, j, child))){
                validMoves.push(new Coordinates(i-1, j));
            }
        }
        if (ChessMoves.isValidBounds(i - 1, j + 1, ChessConfig.LENGTH, ChessConfig.WIDTH)) {
            if (ChessConfig.isCaptureTarget(ChessConfig.getCell(i-1, j+1, child))){
                validMoves.push(new Coordinates(i-1, j+1));
            }
        }
        if (ChessMoves.isValidBounds(i, j - 1, ChessConfig.LENGTH, ChessConfig.WIDTH)) {
            if (ChessConfig.isCaptureTarget(ChessConfig.getCell(i, j-1, child))){
                validMoves.push(new Coordinates(i, j-1));
            }
        }
        if (ChessMoves.isValidBounds(i, j + 1, ChessConfig.LENGTH, ChessConfig.WIDTH)) {
            if (ChessConfig.isCaptureTarget(ChessConfig.getCell(i, j+1, child))){
                validMoves.push(new Coordinates(i, j+1));
            }
        }
        if (ChessMoves.isValidBounds(i + 1, j - 1, ChessConfig.LENGTH, ChessConfig.WIDTH)) {
            if (ChessConfig.isCaptureTarget(ChessConfig.getCell(i+1, j-1, child))){
                validMoves.push(new Coordinates(i+1, j-1));
            }
        }
        if (ChessMoves.isValidBounds(i + 1, j, ChessConfig.LENGTH, ChessConfig.WIDTH)) {
            if (ChessConfig.isCaptureTarget(ChessConfig.getCell(i+1, j, child))){
                validMoves.push(new Coordinates(i+1, j));
            }
        }
        if (ChessMoves.isValidBounds(i + 1, j + 1, ChessConfig.LENGTH, ChessConfig.WIDTH)) {
            if (ChessConfig.isCaptureTarget(ChessConfig.getCell(i+1, j+1, child))){
                validMoves.push(new Coordinates(i+1, j+1));
            }
        }
        // returning the neighbors
        if (!isPTUI){
            getConfigurations(neighbors, child, validMoves, KING, current);
        }
    }

    /**
     * helper function to check if the position may be out of bounds
     * @param i number of rows
     * @param j number of cols
     * @param n length of the board
     * @param m width of the board
     * @return  true if the position is valid, false otherwise
     */
    public static boolean isValidBounds(int i, int j, int n, int m){
        if (i < 0 || j < 0 || i > n - 1 || j > m - 1) {
            return false;
        }
        return true;
    }

    // directional helpers
    /**
     * return coordinates of valid captures along the diagonals
     */
    public static Stack<Coordinates> getValidDiagonals(ChessConfig child, Coordinates current){
        Stack<Coordinates> validDiagonalMoves = new Stack<>();
        // Check main-diagonal (\) based on the current position
        // upper diagonal
        int row = current.row();
        int col = current.col();
        while(isValidBounds(--row, --col, ChessConfig.LENGTH, ChessConfig.WIDTH)){
            if (ChessConfig.isCaptureTarget(ChessConfig.getCell(row, col, child))) {
                validDiagonalMoves.push(new Coordinates(row, col));
                // as soon as first capture target is found don't continue since you cannot skip pieces
                break;
            }
        }
        // lower diagonal
        row = current.row();
        col = current.col();
        while(isValidBounds(++row, ++col, ChessConfig.LENGTH, ChessConfig.WIDTH)){
            if (ChessConfig.isCaptureTarget(ChessConfig.getCell(row, col, child))) {
                validDiagonalMoves.push(new Coordinates(row, col));
                break;
            }
        }
        // Check anti-diagonal (/) based on the current position
        // upper diagonal
        row = current.row();
        col = current.col();
        while(isValidBounds(--row, ++col, ChessConfig.LENGTH, ChessConfig.WIDTH)){
            if (ChessConfig.isCaptureTarget(ChessConfig.getCell(row, col, child))) {
                validDiagonalMoves.push(new Coordinates(row, col));
                break;
            }
        }
        // lower diagonal
        row = current.row();
        col = current.col();
        while(isValidBounds(++row, --col, ChessConfig.LENGTH, ChessConfig.WIDTH)){
            if (ChessConfig.isCaptureTarget(ChessConfig.getCell(row, col, child))) {
                validDiagonalMoves.push(new Coordinates(row, col));
                break;
            }
        }
        return validDiagonalMoves;
    }

    /**
     * return coordinates of valid captures along the Horizontals
     */
    public static Stack<Coordinates> getValidHorizontals(ChessConfig child, Coordinates current){
        Stack<Coordinates> validHorizontals = new Stack<>();
        int row = current.row();
        int col = current.col();
        while(isValidBounds(row, ++col, ChessConfig.LENGTH, ChessConfig.WIDTH)){
            if (ChessConfig.isCaptureTarget(ChessConfig.getCell(row, col, child))){
                validHorizontals.push(new Coordinates(row, col));
                break;
            }
        }
        col = current.col();
        while(isValidBounds(row, --col, ChessConfig.LENGTH, ChessConfig.WIDTH)){
            if (ChessConfig.isCaptureTarget(ChessConfig.getCell(row, col, child))){
                validHorizontals.push(new Coordinates(row, col));
                break;
            }
        }
        return validHorizontals;
    }

    /**
     * return coordinates of valid captures along the verticals
     */
    public static Stack<Coordinates> getValidVerticals(ChessConfig child, Coordinates current) {
        Stack<Coordinates> validVerticals = new Stack<>();
        int row = current.row();
        int col = current.col();
        // check the upper verticals : constant columns, incrementing rows
        while (isValidBounds(++row, col, ChessConfig.LENGTH, ChessConfig.WIDTH)) {
            if (ChessConfig.isCaptureTarget(ChessConfig.getCell(row, col, child))) {
                validVerticals.push(new Coordinates(row, col));
                break;
            }
        }
        // check the lower verticals : constant columns, decrementing rows
        row = current.row();
        while (isValidBounds(--row, col, ChessConfig.LENGTH, ChessConfig.WIDTH)) {
            if (ChessConfig.isCaptureTarget(ChessConfig.getCell(row, col, child))) {
                validVerticals.push(new Coordinates(row, col));
                break;
            }
        }
        return validVerticals;
    }

    /**
     * is the program running a PTUI?
     * @param PTUI boolean
     */
    public static void setPTUI(boolean PTUI) {
        isPTUI = PTUI;
    }
}
