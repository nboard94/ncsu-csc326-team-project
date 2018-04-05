/**
 *
 */
package edu.ncsu.csc.itrust2.forms.admin;

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
