
package edu.ncsu.csc.itrust2.forms.admin;

import edu.ncsu.csc.itrust2.models.persistent.Immunization;

/**
 * A form for REST API communication. Contains fields for constructing
 * immunization objects.
 *
 * @author Nick
 */
public class ImmunizationForm {

    private Long   id;
    private String name;
    private String code;
    private String description;

    /**
     * Empty constructor for filling in fields without a immunization object.
     */
    public ImmunizationForm () {
    }

    /**
     * Constructs a new form with information from the given immunization.
     *
     * @param immunization
     *            the immunization object
     */
    public ImmunizationForm ( final Immunization immunization ) {
        setId( immunization.getId() );
        setName( immunization.getName() );
        setCode( immunization.getCode() );
        setDescription( immunization.getDescription() );
    }

    /**
     * Sets the immunization's id to the given value. All saved immunizations
     * must have unique ids.
     *
     * @return the immunization id
     */
    public Long getId () {
        return id;
    }

    /**
     * Returns the immunization's code
     *
     * @return the code
     */
    public String getCode () {
        return code;
    }

    /**
     * The name of the immunization.
     *
     * @return the immunization's name
     */
    public String getName () {
        return name;
    }

    /**
     * Gets this immunization's description.
     *
     * @return this description
     */
    public String getDescription () {
        return description;
    }

    /**
     * Sets the id associated with this immunization.
     *
     * @param id
     *            the immunization's id
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Sets the code to the given string. Must be in the format "####-####-##".
     *
     * @param code
     *            the code
     */
    public void setCode ( final String code ) {
        this.code = code;
    }

    /**
     * Sets the immunization name.
     *
     * @param name
     *            the name of the immunization
     */
    public void setName ( final String name ) {
        this.name = name;
    }

    /**
     * Sets this immunization's description to the given value.
     *
     * @param description
     *            the description
     */
    public void setDescription ( final String description ) {
        this.description = description;
    }
}
