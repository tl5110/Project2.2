package puzzles.strings;

import puzzles.common.solver.Configuration;
import java.util.ArrayList;
import java.util.Collection;

public class StringsConfig implements Configuration{
    /** starting string to begin transforming with */
    private final String start;
    /** finished string */
    private final String finish;

    /**
     * Constructs the initial configuration from start and finish
     *
     * @param start starting string
     * @param finish finished string
     */
    public StringsConfig(String start, String finish){
        this.start = start;
        this.finish = finish;
    }

    /**
     * Is the current configuration a solution or not?
     *
     * @return true if it's a solution, false otherwise
     */
    @Override
    public boolean isSolution() { return start.equals(finish); }

    /**
     * Get the collection of neighbors from the current one
     *
     * @return all neighbors
     */
    @Override
    public Collection<Configuration> getNeighbors() {
        Collection<Configuration> neighborsList = new ArrayList<>();
        for(int i = 0; i < start.length(); i++){
            char up = (char) (start.charAt(i)+1);
            char down = (char) (start.charAt(i)-1);
            if(up > 'Z'){
                up = 'A';
            } else if(down < 'A'){
                down = 'Z';
            }
            StringBuilder upStr = new StringBuilder(start);
            StringBuilder downStr = new StringBuilder(start);
            upStr.setCharAt(i, up);
            downStr.setCharAt(i, down);
            StringsConfig forward = new StringsConfig(upStr.toString(), finish);
            StringsConfig backward = new StringsConfig(downStr.toString(), finish);
            neighborsList.add(backward);
            neighborsList.add(forward);
        }
        return neighborsList;
    }

    /**
     * Two strings are equal iff they have the same start and finish
     *
     * @param other the other object
     * @return whether they are equal or not
     */
    @Override
    public boolean equals(Object other) {
        if(other instanceof StringsConfig otherString){
            return this.start.equals(otherString.start)
                    && this.finish.equals(otherString.finish);
        }
        return false;
    }

    /**
     * Uses all the fields of the Strings and hash them together
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return this.start.hashCode() + this.finish.hashCode();
    }

    /**
     * Displays a string representation of the Strings to standard output.
     *
     * @return string representation of the Strings to standard output
     */
    @Override
    public String toString() {
        return start;
    }
}
