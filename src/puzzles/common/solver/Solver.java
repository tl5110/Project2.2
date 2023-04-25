package puzzles.common.solver;

import java.util.*;

/**
 * This class represents the common breadth first search algorithm.
 * It takes the start of a puzzle and has a solver that returns a
 * solution, if one exists.
 *
 * @author Tiffany Lee
 */
public class Solver{
    /** Stores the predecessors of each configuration */
    private final HashMap<Configuration, Configuration> predecessors = new HashMap<>();
    /** Stores the order of the visitation of each configuration */
    private final Queue<Configuration> queue = new LinkedList<>();
    /** The start configuration */
    private final Configuration start;
    /** Total number of configurations it took to get to a solution */
    private int totalConfigs = 0;
    /** Number of unique configurations it took to get to a solution */
    private int uniqueConfigs = 0;

    /**
     * Initialize a new solver
     *
     * @param start The start configuration of a puzzle
     */
    public Solver(Configuration start){
        this.start = start;
    }

    /**
     * Tries to find a solution, if one exists, for a given configuration
     *
     * @return A solution, or null if no solution
     */
    public Collection<Configuration> solve(){
        queue.add(start);
        predecessors.put(start, null);
        uniqueConfigs += queue.size();
        totalConfigs += predecessors.size();
        while(!queue.isEmpty()){
            Configuration current = queue.remove();
            if (current.isSolution()) {
                return constructPath(predecessors, current);
            } else {
                Collection<Configuration> successors = current.getNeighbors();
                totalConfigs += successors.size();
                for (Configuration nbr : successors) {
                    if (!predecessors.containsKey(nbr)) {
                        uniqueConfigs += 1;
                        predecessors.put(nbr, current);
                        queue.add(nbr);
                    }
                }
            }
        }
        return null;
    }

    /**
     * Constructs a path from the predecessor map and returns the sequence
     * from start to finish configurations
     *
     * @param predecessors Map used to reconstruct the path
     * @param end finishing configuration
     * @return a list containing the sequence of configurations comprising
     * the path, an empty list if no path exists
     */
    public Collection<Configuration> constructPath(HashMap<Configuration, Configuration> predecessors,
                                                   Configuration end){
        List<Configuration> path = new ArrayList<>();
        if(predecessors.containsKey(end)) {
            while(end != start){
                path.add(0, end);
                end = predecessors.get(end);
            }
            path.add(0, start);
        }
        return path;
    }

    /**
     * Gets the total number of configurations it took to get a solution
     * @return total number of configurations
     */
    public int getTotalConfigs() { return totalConfigs; }

    /**
     * Gets the number of unique configurations it took to get a solution
     * @return number of unique configurations
     */
    public int getUniqueConfigs() { return uniqueConfigs; }
}
