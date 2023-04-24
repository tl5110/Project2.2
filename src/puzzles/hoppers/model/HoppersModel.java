package puzzles.hoppers.model;

import puzzles.common.Observer;
import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class HoppersModel {
    /** the collection of observers of this model */
    private final List<Observer<HoppersModel, String>> observers = new LinkedList<>();
    /** the current configuration */
    private HoppersConfig currentConfig;


    public void hint(){
        Solver solver = new Solver(currentConfig);
        ArrayList<Configuration> hints = new ArrayList<>(solver.solve());
        currentConfig = (HoppersConfig) hints.get(1);
    }

    public boolean checkFrog(int r, int c){
        return currentConfig.getCell(r, c) == 'G' || currentConfig.getCell(r, c) == 'R';
    }

    public boolean isValid(int r, int c){
        int midRow = r - 2;
        int midCol = c -2;
        if((r>=0 && r<currentConfig.getRows()) && (c>=0 && c< currentConfig.getCols())){
            return currentConfig.getCell(midRow, midCol) == 'G' && currentConfig.getCell(r, c) == '.';
        }
        return false;
    }

    public void move(int r, int c){
        currentConfig.move(r, c);
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

    public HoppersModel(String filename) throws IOException {
        this.currentConfig = new HoppersConfig(filename);
    }
}
