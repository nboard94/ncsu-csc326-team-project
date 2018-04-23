/**
 *
 */
package edu.ncsu.csc.itrust2.forms.admin;

import edu.ncsu.csc.itrust2.models.persistent.LabProcedure;

/**
 * Form object that is used to retrieve information from the front end
 *
 * @author Jacob Struckmeyer
 *
 */
public class LabProcedureForm {

    private Long   id;
    private String code;
    private String property;
    private String component;
    private String commonName;

    /**
     * Empty constructor for filling in fields without a LabProcedure object
     */
    public LabProcedureForm () {
    }

    /**
     * Constructs a new form with information from the given LabProcedure
     *
     * @param procedure
     *            the lab procedure object
     */
    public LabProcedureForm ( final LabProcedure procedure ) {
        setId( procedure.getId() );
        setCode( procedure.getCode() );
        setProperty( procedure.getProperty() );
        setComponent( procedure.getComponent() );
        setCommonName( procedure.getCommonName() );
    }

    /**
     * Sets the id
     *
     * @param id
     *            the id to set
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Sets the code
     *
     * @param code
     *            the code to set
     */
    public void setCode ( final String code ) {
        this.code = code;
    }

    /**
     * Sets the property
     *
     * @param property
     *            the property to set
     */
    public void setProperty ( final String property ) {
        this.property = property;
    }

    /**
     * @param component
     *            the component to set
     */
    public void setComponent ( final String component ) {
        this.component = component;
    }

    /**
     * Sets the common name
     *
     * @param commonName
     *            the commonName to set
     */
    public void setCommonName ( final String commonName ) {
        this.commonName = commonName;
    }

    /**
     * Gets the id of the lab procedure
     *
     * @return the id
     */
    public Long getId () {
        return id;
    }

    /**
     * Gets the code of the lab procedure
     *
     * @return the code
     */
    public String getCode () {
        return code;
    }

    /**
     * Gets the property of the lab procedure
     *
     * @return the property
     */
    public String getProperty () {
        return property;
    }

    /**
     * Gets the component of the lab procedure
     *
     * @return the component
     */
    public String getComponent () {
        return component;
    }

    /**
     * Gets the common name of the lab procedure
     *
     * @return the common name
     */
    public String getCommonName () {
        return commonName;
    }

}
