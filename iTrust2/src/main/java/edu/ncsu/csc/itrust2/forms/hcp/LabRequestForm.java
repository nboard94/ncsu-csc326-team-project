/**
 *
 */
package edu.ncsu.csc.itrust2.forms.hcp;

/**
 * Form that takes information from the front-end and parses it into a
 * LabRequestObject
 *
 * @author Jacob Struckmeyer
 *
 */
public class LabRequestForm {

    private String proc;
    private String patient;
    private String hcp;
    private String labTech;
    private String priority;
    private String comments;

    /**
     * Gets the procedure
     *
     * @return the proc
     */
    public String getProc () {
        return proc;
    }

    /**
     * Gets the patient
     *
     * @return the patient
     */
    public String getPatient () {
        return patient;
    }

    /**
     * Gets the hcp
     *
     * @return the hcp
     */
    public String getHcp () {
        return hcp;
    }

    /**
     * Gets the lab techician
     *
     * @return the labTech
     */
    public String getLabTech () {
        return labTech;
    }

    /**
     * Gets the priority
     *
     * @return the priority
     */
    public String getPriority () {
        return priority;
    }

    /**
     * Gets the comment
     *
     * @return the comment
     */
    public String getComments () {
        return comments;
    }
}
