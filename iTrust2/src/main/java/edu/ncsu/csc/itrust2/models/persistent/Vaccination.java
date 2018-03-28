package edu.ncsu.csc.itrust2.models.persistent;

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
import org.hibernate.validator.constraints.NotEmpty;

import edu.ncsu.csc.itrust2.forms.admin.VaccinationForm;

/**
 * Represents a vaccination in the NDC format.
 *
 * @author Nick Board
 */
@Entity
@Table ( name = "Vaccinations" )
public class Vaccination extends DomainObject<Vaccination> {

    /** For Hibernate/Thymeleaf _must_ be an empty constructor */
    public Vaccination () {
    }

    /**
     * Constructs a new form from the details in the given form
     *
     * @param form
     *            the form to base the new vaccination on
     */
    public Vaccination ( final VaccinationForm form ) {
        setId( form.getId() );
        setCode( form.getCode() );
        setName( form.getName() );
        setDescription( form.getDescription() );
    }

    @Id
    @GeneratedValue ( strategy = GenerationType.AUTO )
    private Long   id;

    @Pattern ( regexp = "^\\d{5}$" )
    private String code;

    @NotEmpty
    @Length ( max = 64 )
    private String name;

    @NotNull
    @Length ( max = 1024 )
    private String description;

    /**
     * Sets the vaccination's id to the given value. All saved vaccinations must
     * have unique ids.
     *
     * @param id
     *            the new id
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Returns the id associated with this vaccination.
     *
     * @return the id
     */
    @Override
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
     * Sets the code to the given string. Must be in the format "####-####-##".
     *
     * @param code
     *            the code
     */
    public void setCode ( final String code ) {
        this.code = code;
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
     * Sets the vaccination name.
     *
     * @param name
     *            the name of the vaccination
     */
    public void setName ( final String name ) {
        this.name = name;
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
     * Sets this vaccination's description to the given value.
     *
     * @param description
     *            the description
     */
    public void setDescription ( final String description ) {
        this.description = description;
    }

    /**
     * Gets a list of vaccinations that match the given query.
     *
     * @param where
     *            List of Criterion to and together and search for records by
     * @return the collection of matching vaccinations
     */
    @SuppressWarnings ( "unchecked" )
    private static List<Vaccination> getWhere ( final List<Criterion> where ) {
        return (List<Vaccination>) getWhere( Vaccination.class, where );
    }

    /**
     * Returns the vaccination whose id matches the given value.
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
     * Gets the vaccination with the code matching the given value. Returns null
     * if none found.
     *
     * @param code
     *            the code to search for
     * @return the matching vaccination
     */
    public static Vaccination getByCode ( final String code ) {
        try {
            return getWhere( createCriterionAsList( "code", code ) ).get( 0 );
        }
        catch ( final Exception e ) {
            return null;
        }
    }

    /**
     * Collects and returns all vaccinations in the system
     *
     * @return all saved vaccinations
     */
    @SuppressWarnings ( "unchecked" )
    public static List<Vaccination> getAll () {
        return (List<Vaccination>) DomainObject.getAll( Vaccination.class );
    }

}
