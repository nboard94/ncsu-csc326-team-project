package edu.ncsu.csc.itrust2.models.enums;

/**
 * Keeping track of priority ranging from low to very high
 * 
 * @author Cody Roberts (jdrobe10)
 *
 */
public enum Priority {

    /**
     * Low Priority
     */
    PRIORITY_LOW ( 4 ),

    /**
     * Medium Priority
     */
    PRIORITY_MEDIUM ( 3 ),

    /**
     * High Priority
     */

    PRIORITY_HIGH ( 2 ),

    /**
     * Very High Priority
     */
    PRIORITY_VERY_HIGH ( 1 );

    /**
     * Integer value for the priority
     */
    private int priority;

    /**
     * Create a priority with a numeric value
     */
    private Priority ( final int pri ) {
        this.priority = pri;
    }

    /**
     * Get the priority
     */
    public int getPriority () {
        return priority;
    }

}
