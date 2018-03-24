package edu.ncsu.csc.itrust2.models.persistent;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.criterion.Criterion;

import edu.ncsu.csc.itrust2.forms.admin.VaccinationForm;

/**
 * Represents a vaccination in the system.
 *
 * @author Nick Board
 */
@Entity
@Table ( name = "Vaccinations" )
public class Vaccination extends DomainObject<Vaccination> {

    @Id
    @GeneratedValue ( strategy = GenerationType.AUTO )
    private Long   id;

    @NotNull
    @JoinColumn ( name = "cpt_code" )
    private int    cptCode;

    @NotNull
    @JoinColumn ( name = "cpt_description" )
    private String cptDescription;

    @NotNull
    @JoinColumn ( name = "cvx_code" )
    private int    cvxCode;

    @NotNull
    @JoinColumn ( name = "vaccine_name" )
    private String vaccineName;

    @JoinColumn ( name = "comments" )
    private String comments;

    /**
     * Empty constructor for Hibernate.
     */
    public Vaccination () {
    }

    /**
     * Construct a new Vaccination using the details in the given form.
     *
     * @param form
     *            the vaccination form
     */
    public Vaccination ( final VaccinationForm form ) {

    }

    /**
     * Returns the id associated with the Vaccination.
     *
     * @return the vaccination id
     */
    @Override
    public Long getId () {
        return this.id;
    }

    /**
     * Sets the Vaccination's unique id.
     *
     * @param id
     *            the vaccination id
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Returns the cptCode associated with the vaccination.
     *
     * @return the vaccination cptCode
     */
    public int getCptCode () {
        return this.cptCode;
    }

    /**
     * Sets the Vaccination's unique cptCode.
     *
     * @param cptCode
     *            the vaccination cptCode
     */
    public void setCptCode ( final int cptCode ) {
        this.cptCode = cptCode;
    }

    /**
     * Returns the cptDescription associated with the vaccination.
     *
     * @return the vaccination cptDescription
     */
    public String getCptDescription () {
        return this.cptDescription;
    }

    /**
     * Sets the Vaccination's unique cptDescription.
     *
     * @param cptDescription
     *            the vaccination cptDescription
     */
    public void setCptDescription ( final String cptDescription ) {
        this.cptDescription = cptDescription;
    }

    /**
     * Returns the cvxCode associated with the vaccination.
     *
     * @return the vaccination cvxCode
     */
    public int getCvxCode () {
        return this.cvxCode;
    }

    /**
     * Sets the Vaccination's unique cvxCode.
     *
     * @param cvxCode
     *            the vaccination cvxCode
     */
    public void setCvxCode ( final int cvxCode ) {
        this.cvxCode = cvxCode;
    }

    /**
     * Returns the vaccineName associated with the vaccination.
     *
     * @return the vaccination vaccineName
     */
    public String getVaccineName () {
        return this.vaccineName;
    }

    /**
     * Sets the vaccination's unique vaccineName.
     *
     * @param vaccineName
     *            the vaccination vaccineName
     */
    public void setVaccineName ( final String vaccineName ) {
        this.vaccineName = vaccineName;
    }

    /**
     * Returns the comments associated with the vaccination.
     *
     * @return the vaccination comments
     */
    public String getComments () {
        return this.comments;
    }

    /**
     * Sets the vaccination's unique comments.
     *
     * @param comments
     *            the vaccination comments
     */
    public void setComments ( final String comments ) {
        this.comments = comments;
    }

    /**
     * Gets a list of vaccinations that match the given query
     *
     * @param where
     *            List of Criterion to and together and search records by
     * @return the collection of matching drugs
     */
    @SuppressWarnings ( "unchecked" )
    private static List<Vaccination> getWhere ( final List<Criterion> where ) {
        return (List<Vaccination>) getWhere( Vaccination.class, where );
    }

    /**
     * Returns the vaccination whose id matches the given value
     *
     * @param id
     *            the id to search for
     * @return the matching vaccination or null if none is found
     */
    public static Vaccination getById ( final Long id ) {
        try {
            return getWhere( createCriterionAsList( ID, id ) ).get( 0 );
        }
        catch ( final Exception e ) {
            return null;
        }
    }

    /**
     * Gets the vaccination wit hthe cptCode matching the given value. Returns
     * null if none found.
     *
     * @param code
     *            the code to search for
     * @return the matching vaccination
     */
    public static Vaccination getByCode ( final int code ) {
        try {
            return getWhere( createCriterionAsList( "cptCode", code ) ).get( 0 );
        }
        catch ( final Exception e ) {
            return null;
        }
    }

    /**
     * Collects and returns all drugs in the system
     *
     * @return all saved vaccinations
     */
    @SuppressWarnings ( "unchecked" )
    public static List<Vaccination> getAll () {
        return (List<Vaccination>) DomainObject.getAll( Vaccination.class );
    }
}
