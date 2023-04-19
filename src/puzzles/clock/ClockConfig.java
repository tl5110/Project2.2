package puzzles.clock;

import puzzles.common.solver.Configuration;
import java.util.ArrayList;
import java.util.Collection;

public class ClockConfig implements Configuration{
    /** number of hours the clock has */
    private final int hours;
    /** starting hour */
    private final int start;
    /** ending hour */
    private final int end;

    /**
     * Constructs the initial configuration from the hours, start, and end
     *
     * @param hours number of hours the clock has
     * @param start starting hour
     * @param end ending hour
     */
    public ClockConfig(int hours, int start, int end){
        this.hours = hours;
        this.start = start;
        this.end = end;
    }

    /**
     * Is the current configuration a solution or not?
     *
     * @return true if it's a solution, false otherwise
     */
    @Override
    public boolean isSolution() {
        return start == end;
    }

    /**
     * Get the collection of neighbors from the current one
     *
     * @return all neighbors
     */
    @Override
    public Collection<Configuration> getNeighbors() {
        Collection<Configuration> neighborsList = new ArrayList<>();
        int up = start + 1;
        int down = start - 1;
        if(up > hours){
            up = 1;
        } else if(down <= 0){
            down = hours;
        }
        ClockConfig forward = new ClockConfig(hours, up, end);
        ClockConfig backward = new ClockConfig(hours, down, end);
        neighborsList.add(backward);
        neighborsList.add(forward);
        return neighborsList;
    }

    /**
     * Two clocks are equal iff they have the same hours, start, and end
     *
     * @param other the other object
     * @return whether they are equal or not
     */
    @Override
    public boolean equals(Object other) {
        if(other instanceof ClockConfig otherClock){
            return this.hours == otherClock.hours
                    && this.start == otherClock.start
                    && this.end == otherClock.end;
        }
        return false;
    }

    /**
     * Uses all the fields of the clock and hash them together
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return this.hours + this.start + this.end;
    }

    /**
     * Displays a string representation of the Clock to standard output.
     *
     * @return string representation of the Clock to standard output
     */
    @Override
    public String toString() {
        return String.valueOf(start);
    }
}
