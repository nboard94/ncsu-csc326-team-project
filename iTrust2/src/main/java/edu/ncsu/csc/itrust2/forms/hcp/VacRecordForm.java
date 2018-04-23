package edu.ncsu.csc.itrust2.forms.hcp;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Locale;

import edu.ncsu.csc.itrust2.models.persistent.VacRecord;

/**
 * A form for REST API communication. Contains files for constructing VacRecord
 * objects.
 *
 * @author Nick Board
 */
public class VacRecordForm implements Serializable {

    private static final long serialVersionUID = 1L;

    private String            vaccination;
    private String            administrationDate;
    private String            patient;
    private Long              id;

    /**
     * Empty constructor for filling in fields without a VacRecord object.
     */
    public VacRecordForm () {
    }

    /**
     * Constructs a new form with information from the given VacRecord.
     *
     * @param vacRecord
     *            the VacRecord object
     */
    public VacRecordForm ( final VacRecord vacRecord ) {
        setId( vacRecord.getId() );
        setVaccination( vacRecord.getVaccination().getCode() );
        setPatient( vacRecord.getPatient().getUsername() );
        final SimpleDateFormat tempDate = new SimpleDateFormat( "MM/dd/yyyy", Locale.ENGLISH );
        setAdministrationDate( tempDate.format( vacRecord.getAdministrationDate().getTime() ) );
    }

    /**
     * Returns the vaccination associated with the VacRecord
     *
     * @return the VacRecord's vaccination
     */
    public String getVaccination () {
        return vaccination;
    }

    /**
     * Associates this VacRecord with the given vaccination
     *
     * @param vaccination
     *            the vaccination
     */
    public void setVaccination ( final String vaccination ) {
        this.vaccination = vaccination;
    }

    /**
     * Returns the administrationDate associated with the VacRecord
     *
     * @return the VacRecord's adminiStrationDate
     */
    public String getAdministrationDate () {
        return administrationDate;
    }

    /**
     * Associates this VacRecord with the given administrationDate
     *
     * @param administrationDate
     *            the administrationDaten
     */
    public void setAdministrationDate ( final String administrationDate ) {
        this.administrationDate = administrationDate;
    }

    /**
     * Returns the patient associated with the VacRecord
     *
     * @return the VacRecord's patient
     */
    public String getPatient () {
        return patient;
    }

    /**
     * Associates this VacRecord with the given patient
     *
     * @param patient
     *            the patietn
     */
    public void setPatient ( final String patient ) {
        this.patient = patient;
    }

    /**
     * Returns the id associated with the VacRecord
     *
     * @return the VacRecord's id
     */
    public Long getId () {
        return id;
    }

    /**
     * Associates this VacRecord with the given id
     *
     * @param id
     *            the id
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

}
