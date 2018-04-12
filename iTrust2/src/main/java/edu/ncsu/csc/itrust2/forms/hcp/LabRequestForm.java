/**
 *
 */
package edu.ncsu.csc.itrust2.forms.hcp;

import edu.ncsu.csc.itrust2.models.enums.Priority;
import edu.ncsu.csc.itrust2.models.persistent.LabRequest;

/**
 * Form that takes information from the front-end and parses it into a
 * LabRequestObject
 *
 * @author Jacob Struckmeyer
 *
 */
public class LabRequestForm {

    private Long   id;
    private String labProcedure;
    // private String patient;
    private String hcp;
    private String labTech;
    private String priority;
    private String comments;
    private Long   officeVisit;

    /**
     * Empty constructor for filling in fields without a LabRequest object
     */
    public LabRequestForm () {
    }

    /**
     * Constructs a LebRequestForm from a given LabRequest object
     *
     * @param request
     *            the LabRequest
     */
    public LabRequestForm ( final LabRequest request ) {
        setId( request.getId() );
        setLabProcedure( request.getLabProcedure().getCode() );
        // setPatient( request.getPatient().getId() );
        setHcp( request.getHcp().getId() );
        setLabTech( request.getLabTech().getId() );
        setPriority( Priority.toString( request.getPriority() ) );
        setComments( request.getComments() );
        setOfficeVisit( request.getVisit().getId() );
    }

    // /**
    // * Sets the patient
    // *
    // * @param patient
    // * the patient to set
    // */
    // public void setPatient ( final String patient ) {
    // this.patient = patient;
    // }

    /**
     * Sets the HCP
     *
     * @param hcp
     *            the hcp to set
     */
    public void setHcp ( final String hcp ) {
        this.hcp = hcp;
    }

    /**
     * Sets the lab tech
     *
     * @param labTech
     *            the labTech to set
     */
    public void setLabTech ( final String labTech ) {
        this.labTech = labTech;
    }

    /**
     * Sets the priority
     *
     * @param priority
     *            the priority to set
     */
    public void setPriority ( final String priority ) {
        this.priority = priority;
    }

    /**
     * Sets the comments
     *
     * @param comments
     *            the comments to set
     */
    public void setComments ( final String comments ) {
        this.comments = comments;
    }

    /**
     * Gets the procedure
     *
     * @return the lab procedure
     */
    public String getLabProcedure () {
        return labProcedure;
    }

    /**
     * Sets the lab procedure of for the lab request
     *
     * @param labProcedure
     *            the lab procedure to set
     */
    public void setLabProcedure ( final String labProcedure ) {
        this.labProcedure = labProcedure;
    }

    // /**
    // * Gets the patient
    // *
    // * @return the patient
    // */
    // public String getPatient () {
    // return patient;
    // }

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

    /**
     * Gets the office visit id that this lab request is associated with
     *
     * @return the office visit id
     */
    public Long getOfficeVisit () {
        return officeVisit;
    }

    /**
     * Sets the office visit of this lab
     *
     * @param officeVisit
     *            the id of the office visit
     */
    public void setOfficeVisit ( final Long officeVisit ) {
        this.officeVisit = officeVisit;
    }

    /**
     * Gets the id of the lab request
     *
     * @return the id
     */
    public Long getId () {
        return id;
    }

    /**
     * Sets the id of the lab request
     *
     * @param id
     *            the id to set
     */
    public void setId ( final Long id ) {
        this.id = id;
    }
}
