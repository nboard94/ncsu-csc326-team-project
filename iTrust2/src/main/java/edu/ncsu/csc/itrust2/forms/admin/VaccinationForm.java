package edu.ncsu.csc.itrust2.forms.admin;

import java.io.Serializable;

import edu.ncsu.csc.itrust2.models.persistent.Vaccination;

/**
 * A form for REST API communication. Contains fields for constructing
 * Vaccination objects.
 *
 * @author Nick Board
 */
public class VaccinationForm implements Serializable {

    private static final long serialVersionUID = 1L;

    private int               cptCode;
    private String            cptDescription;
    private int               cvxCode;
    private String            vaccineName;
    private String            comments;
    private Long              id;

    /**
     * Empty constructor for filling in fields without a Vaccination object.
     */
    public VaccinationForm () {
    }

    /**
     * Constructs a new form with information from the given vaccination.
     *
     * @param vaccination
     *            the vaccination object
     */
    public VaccinationForm ( final Vaccination vaccination ) {
        setId( vaccination.getId() );
        setCptCode( vaccination.getCptCode() );
        setCptDescription( vaccination.getCptDescription() );
        setCvxCode( vaccination.getCvxCode() );
        setVaccineName( vaccination.getVaccineName() );
        setComments( vaccination.getComments() );
    }

    /**
     * Returns the cptCode associated with this Vaccination.
     *
     * @return the vaccination's cptCode
     */
    public int getCptCode () {
        return cptCode;
    }

    /**
     * Associates this vaccination with the given cptCode.
     *
     * @param cptCode
     *            the cptCode
     */
    public void setCptCode ( final int cptCode ) {
        this.cptCode = cptCode;
    }

    /**
     * Returns the cptDescription associated with this Vaccination.
     *
     * @return the vaccination's cptDescription
     */
    public String getCptDescription () {
        return cptDescription;
    }

    /**
     * Associates this vaccination with the given cptDescription.
     *
     * @param cptDescription
     *            the cptDescription
     */
    public void setCptDescription ( final String cptDescription ) {
        this.cptDescription = cptDescription;
    }

    /**
     * Returns the cvxCode associated with this Vaccination.
     *
     * @return the vaccination's cvxCode
     */
    public int getCvxCode () {
        return cvxCode;
    }

    /**
     * Associates this vaccination with the given cvxCode.
     *
     * @param cvxCode
     *            the cvxCode
     */
    public void setCvxCode ( final int cvxCode ) {
        this.cvxCode = cvxCode;
    }

    /**
     * Returns the vaccineName associated with this Vaccination.
     *
     * @return the vaccination's vaccineName
     */
    public String getVaccineName () {
        return vaccineName;
    }

    /**
     * Associates this vaccination with the given vaccineName.
     *
     * @param vaccineName
     *            the vaccineName
     */
    public void setVaccineName ( final String vaccineName ) {
        this.vaccineName = vaccineName;
    }

    /**
     * Returns the comments associated with this Vaccination.
     *
     * @return the vaccination's comments
     */
    public String getComments () {
        return comments;
    }

    /**
     * Associates this vaccination with the given comments.
     *
     * @param comments
     *            the comments
     */
    public void setComments ( final String comments ) {
        this.comments = comments;
    }

    /**
     * Returns the id associated with this Vaccination.
     *
     * @return the vaccination's id
     */
    public Long getId () {
        return id;
    }

    /**
     * Associates this vaccination with the given id.
     *
     * @param id
     *            the id
     */
    public void setId ( final Long id ) {
        this.id = id;
    }
}
