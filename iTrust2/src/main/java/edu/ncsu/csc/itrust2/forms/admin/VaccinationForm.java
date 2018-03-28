
package edu.ncsu.csc.itrust2.forms.admin;

import edu.ncsu.csc.itrust2.models.persistent.Vaccination;

/**
 * A form for REST API communication. Contains fields for constructing
 * vaccination objects.
 *
 * @author Nick
 */
public class VaccinationForm {

    private Long   id;
    private String name;
    private String code;
    private String description;

    /**
     * Empty constructor for filling in fields without a vaccination object.
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
        setName( vaccination.getName() );
        setCode( vaccination.getCode() );
        setDescription( vaccination.getDescription() );
    }

    /**
     * Sets the vaccination's id to the given value. All saved vaccinations must
     * have unique ids.
     *
     * @return the vaccination id
     */
    public Long getId () {
        return id;
    }

    /**
     * Returns the vaccination's code
     *
     * @return the code
     */
    public String getCode () {
        return code;
    }

    /**
     * The name of the vaccination.
     *
     * @return the vaccination's name
     */
    public String getName () {
        return name;
    }

    /**
     * Gets this vaccination's description.
     *
     * @return this description
     */
    public String getDescription () {
        return description;
    }

    /**
     * Sets the id associated with this vaccination.
     *
     * @param id
     *            the vaccination's id
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
     * Sets the vaccination name.
     *
     * @param name
     *            the name of the vaccination
     */
    public void setName ( final String name ) {
        this.name = name;
    }

    /**
     * Sets this vaccination's description to the given value.
     *
     * @param description
     *            the description
     */
    public void setDescription ( final String description ) {
        this.description = description;
    }
}
