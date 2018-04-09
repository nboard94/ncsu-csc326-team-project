/**
 *
 */
package edu.ncsu.csc.itrust2.models.persistent;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.criterion.Criterion;
import org.hibernate.validator.constraints.Length;

import edu.ncsu.csc.itrust2.forms.admin.LabProcedureForm;

/**
 * Persistent object that holds information about a certain lab procedure
 *
 * @author Jacob Struckmeyer
 *
 */
@Entity
@Table ( name = "Lab Procedures" )
public class LabProcedure extends DomainObject<LabProcedure> {

    /** Empty Constructor for hibernate/thymeleaf */
    public LabProcedure () {
    }

    /**
     * Constructor that creates a lab procedure from a form
     *
     * @param form
     *            the form that was recevied from the fron end
     */
    public LabProcedure ( final LabProcedureForm form ) {
        setCode( form.getCode() );
        setProperty( form.getProperty() );
        setComponent( form.getComponent() );
        setCommonName( form.getCommonName() );
    }

    /**
     * Id of the lab procedure
     */
    @Id
    @GeneratedValue ( strategy = GenerationType.AUTO )
    private Long   id;

    /**
     * LOINC code for the procedure
     */
    @Pattern ( regexp = "^\\d{6}-\\d{2}$" )
    private String code;

    /**
     * Property of the procedure
     */
    @NotNull
    @Length ( max = 16 )
    private String property;

    /**
     * Component of the procedure
     */
    @NotNull
    @Length ( max = 16 )
    private String component;

    /**
     * The common name that describes the procedure
     */
    @NotNull
    @Length ( max = 128 )
    private String commonName;

    /**
     * Gets a list of lab procedures that match the given query.
     *
     * @param where
     *            List of Criterion to and together and search for records by
     * @return the collection of matching lab procedures
     */
    @SuppressWarnings ( "unchecked" )
    private static List<LabProcedure> getWhere ( final List<Criterion> where ) {
        return (List<LabProcedure>) getWhere( LabProcedure.class, where );
    }

    /**
     * Returns the lab procedure whose id matches the given value.
     *
     * @param id
     *            the id to search for
     * @return the matching lab procedure or null if none is found
     */
    public static LabProcedure getById ( final Long id ) {
        try {
            return getWhere( createCriterionAsList( ID, id ) ).get( 0 );
        }
        catch ( final Exception e ) {
            return null;
        }
    }

    /**
     * Gets the lab procedure with the code matching the given value. Returns
     * null if none found.
     *
     * @param code
     *            the code to search for
     * @return the matching lab procedure
     */
    public static LabProcedure getByCode ( final String code ) {
        try {
            return getWhere( createCriterionAsList( "code", code ) ).get( 0 );
        }
        catch ( final Exception e ) {
            return null;
        }
    }

    /**
     * Collects and returns all lab procedures in the system
     *
     * @return all saved drugs
     */
    @SuppressWarnings ( "unchecked" )
    public static List<LabProcedure> getAll () {
        return (List<LabProcedure>) DomainObject.getAll( Drug.class );
    }

    /**
     * Gets the code of the lab
     *
     * @return the code
     */
    public String getCode () {
        return code;
    }

    /**
     * Sets the code of the lab
     *
     * @param code
     *            the code to set
     */
    public void setCode ( final String code ) {
        this.code = code;
    }

    /**
     * Gets the property of the lab
     *
     * @return the property
     */
    public String getProperty () {
        return property;
    }

    /**
     * Sets the property of the lab
     *
     * @param property
     *            the property to set
     */
    public void setProperty ( final String property ) {
        this.property = property;
    }

    /**
     * Gets the component of the lab
     *
     * @return the component
     */
    public String getComponent () {
        return component;
    }

    /**
     * Sets the component of the lab
     *
     * @param component
     *            the component to set
     */
    public void setComponent ( final String component ) {
        this.component = component;
    }

    /**
     * Gets the common name of the lab
     *
     * @return the commonName
     */
    public String getCommonName () {
        return commonName;
    }

    /**
     * Sets the common name of the lab
     *
     * @param commonName
     *            the commonName to set
     */
    public void setCommonName ( final String commonName ) {
        this.commonName = commonName;
    }

    @Override
    public Serializable getId () {
        return id;
    }

}
