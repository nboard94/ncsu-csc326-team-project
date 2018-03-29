package edu.ncsu.csc.itrust2.models.persistent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.ObjectNotFoundException;
import org.hibernate.criterion.Criterion;

import edu.ncsu.csc.itrust2.forms.hcp.VacRecordForm;
import edu.ncsu.csc.itrust2.models.enums.Role;

/**
 * Represents a vaccination record in the system. Each vaccination record is
 * associated with a patient and a vaccination.
 *
 * @author Nick Board
 */
@Entity
@Table ( name = "VacRecords" )
public class VacRecord extends DomainObject<VacRecord> {

    @Id
    @GeneratedValue ( strategy = GenerationType.AUTO )
    private Long        id;

    @NotNull
    @ManyToOne
    @JoinColumn ( name = "vac_id" )
    private Vaccination vaccination;

    @NotNull
    @JoinColumn ( name = "administration_id" )
    private Calendar    administrationDate;

    @NotNull
    @ManyToOne
    @JoinColumn ( name = "patient_id", columnDefinition = "varchar(100)" )
    private User        patient;

    /**
     * Empty constructor for Hibernate.
     */
    public VacRecord () {

    }

    /**
     * Construct a new VacRecord using the details in the given form.
     *
     * @param form
     *            the prescription form
     */
    public VacRecord ( final VacRecordForm form ) {

        if ( form.getId() != null ) {
            setId( form.getId() );
        }

        final SimpleDateFormat sdf = new SimpleDateFormat( "MM/dd/yyyy", Locale.ENGLISH );
        Date parsedDate = null;
        try {
            parsedDate = sdf.parse( form.getAdministrationDate() );
        }
        catch ( final ParseException e ) {
            // Ignore, Hibernate will catch the null date
        }
        final Calendar c = Calendar.getInstance();
        c.setTime( parsedDate );
        setAdministrationDate( c );

        setVaccination( Vaccination.getByCode( form.getVaccination() ) );
        setPatient( User.getByName( form.getPatient() ) );
    }

    @Override
    public Long getId () {
        return id;
    }

    /**
     * Sets the VacRecord's unique id.
     *
     * @param id
     *            the VacRecord id
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Returns the id associated with the VacRecord.
     *
     * @return the VacRecord's vaccination
     */
    public Vaccination getVaccination () {
        return vaccination;
    }

    /**
     * Sets the VacRecord's associated vaccination.
     *
     * @param vaccination
     *            the VacRecord vaccination
     */
    public void setVaccination ( final Vaccination vaccination ) {
        this.vaccination = vaccination;
    }

    /**
     * Returns the administration date associated with the VacRecord.
     *
     * @return the Vacrecord's administration date
     */
    public Calendar getAdministrationDate () {
        return administrationDate;
    }

    /**
     * Sets the VacRecord's unique administration date.
     *
     * @param administrationDate
     *            the VacRecord administration date
     */
    public void setAdministrationDate ( final Calendar administrationDate ) {
        this.administrationDate = administrationDate;
    }

    /**
     * Returns the patient associated with the VacRecord.
     *
     * @return the Vacrecord's patient
     */
    public User getPatient () {
        return patient;
    }

    /**
     * Sets the VacRecord's associated patient.
     *
     * @param patient
     *            the VacRecord patient
     */
    public void setPatient ( final User patient ) {
        this.patient = patient;
    }

    /**
     * Retrieve all VacRecords for the patient provided.
     *
     * @param patient
     *            The patient to find VacRecords for
     * @return The List of records that was found
     */
    public static List<VacRecord> getForPatient ( final String patient ) {
        return getWhere( createCriterionAsList( "patient", User.getByNameAndRole( patient, Role.ROLE_PATIENT ) ) );
    }

    /**
     * Returns a collection of VacRecords that meet the "where" query
     *
     * @param where
     *            List of Criterion to and together and search for records by
     * @return a collection of matching prescriptions
     */
    @SuppressWarnings ( "unchecked" )
    private static List<VacRecord> getWhere ( final List<Criterion> where ) {
        return (List<VacRecord>) getWhere( Prescription.class, where );
    }

    /**
     * Gets the VacRecord with the given id, or null if none exists.
     *
     * @param id
     *            the id to query for
     * @return the matching prescription
     */
    public static VacRecord getById ( final Long id ) {
        try {
            return getWhere( createCriterionAsList( ID, id ) ).get( 0 );
        }
        catch ( final ObjectNotFoundException e ) {
            return null;
        }
    }

    /**
     * Gets a collection of all the VacRecords in the system.
     *
     * @return the system's VacRecords.
     */
    @SuppressWarnings ( "unchecked" )
    public static List<VacRecord> getVacRecords () {
        return (List<VacRecord>) DomainObject.getAll( VacRecord.class );
    }
}
