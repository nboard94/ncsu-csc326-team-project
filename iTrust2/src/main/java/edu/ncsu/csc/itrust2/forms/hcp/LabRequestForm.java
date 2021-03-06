/**
 *
 */
package edu.ncsu.csc.itrust2.forms.hcp;

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
    private String patient;
    private String hcp;
    private String labTech;
    private String priority;
    private String status;
    private String comments;

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
        setPatient( request.getPatient().getId() );
        setHcp( request.getHcp().getId() );
        setLabTech( request.getLabTech().getId() );
        setPriority( request.getPriority().toString() );
        setStatus( request.getStatus().toString() );
        setComments( request.getComments() );
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

    /**
     * Gets the patient
     *
     * @return the patient
     */
    public String getPatient () {
        return patient;
    }

    /**
     * Sets the patient
     *
     * @param patient
     *            the patient to set
     */
    public void setPatient ( final String patient ) {
        this.patient = patient;
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
     * Sets the HCP
     *
     * @param hcp
     *            the hcp to set
     */
    public void setHcp ( final String hcp ) {
        this.hcp = hcp;
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
     * Sets the lab tech
     *
     * @param labTech
     *            the labTech to set
     */
    public void setLabTech ( final String labTech ) {
        this.labTech = labTech;
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
     * Sets the priority
     *
     * @param priority
     *            the priority to set
     */
    public void setPriority ( final String priority ) {
        this.priority = priority;
    }

    /**
     * Gets the status of the form
     *
     * @return the status
     */
    public String getStatus () {
        return status;
    }

    /**
     * Sets the status of the form
     *
     * @param status
     *            the status to set
     */
    public void setStatus ( final String status ) {
        this.status = status;
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
     * Sets the comments
     *
     * @param comments
     *            the comments to set
     */
    public void setComments ( final String comments ) {
        this.comments = comments;
    }
}
