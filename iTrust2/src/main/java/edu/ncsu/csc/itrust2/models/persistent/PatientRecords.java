package edu.ncsu.csc.itrust2.models.persistent;

import java.util.ArrayList;
import java.util.List;

import edu.ncsu.csc.itrust2.models.enums.BloodType;
import edu.ncsu.csc.itrust2.models.enums.Gender;

/**
 * A patient records object to create the emergency health records as they are
 * needed.
 *
 * @author Will Duke
 *
 */
public class PatientRecords {

    /** A string representation of the name */
    private String                  name;

    /** The age represented as an integer */
    private int                     age;

    /** A string representation of the patient's date of birth */
    private String                  dateOfBirth;

    /** A Gender type representing the gender of the patient */
    private Gender                  patientGender;

    /** A BloodType type representing the blood type of the patient */
    private BloodType               bloodType;

    /** A list of diagnosis for the patient */
    private List<Diagnosis>         diagnosisList;

    /** An array list of prescriptions for the patient */
    private ArrayList<Prescription> prescriptionsList;

    /** An array list of drugs for the patient */
    private ArrayList<Drug>         drugsList;

    /** An array list of ICD codes */
    private ArrayList<ICDCode>      icdCodesList;

    /** An id for the record objects */
    private long                    recordID;

    /**
     * Sets the name to the passed name.
     *
     * @param name
     *            the name to set to
     */
    public void setName ( final String name ) {
        this.name = name;
    }

    /**
     * Returns the name as a string.
     *
     * @return String name the name
     */
    public String getName () {
        return name;
    }

    /**
     * Sets the age to be the passed value.
     *
     * @param age
     *            the age to set to
     */
    public void setAge ( final int age ) {
        this.age = age;
    }

    /**
     * Returns the age as an integer.
     *
     * @return age the age
     */
    public int getAge () {
        return age;
    }

    /**
     * Sets the date of birth of the patient to the passed value.
     *
     * @param dateOfBirth
     *            the date of birth to set to.
     */
    public void setDateOfBirth ( final String dateOfBirth ) {
        this.dateOfBirth = dateOfBirth;
    }

    /**
     * Returns the patient's date of birth as a string
     *
     * @return dateOfBirth of the patient
     */
    public String getDateOfBirth () {
        return dateOfBirth;
    }

    /**
     * Sets the gender of the patient to be the passed Gender
     *
     * @param gender
     *            of the patient to set to
     */
    public void setPatientGender ( final Gender gender ) {
        this.patientGender = gender;
    }

    /**
     * Returns the gender of the patient as a Gender type
     *
     * @return Gender of the patient
     */
    public Gender getPatientGender () {
        return patientGender;
    }

    /**
     * Sets the blood type of the patient to the passed blood type
     *
     * @param bloodType
     *            the blood type to set the patient to
     */
    public void setBloodType ( final BloodType bloodType ) {
        this.bloodType = bloodType;
    }

    /**
     * Returns the blood type of the patient
     *
     * @return BloodType of the patient
     */
    public BloodType getBloodType () {
        return bloodType;
    }

    /**
     * Sets the diagnosis list of the patient
     *
     * @param diagnosisList
     *            the list of diagnosis
     */
    public void setDiagnosisList ( final List<Diagnosis> diagnosisList ) {
        this.diagnosisList = diagnosisList;
    }

    /**
     * Returns the list of diagnosis of the patient
     *
     * @return diagnosis the list of diagnosis
     */
    public List<Diagnosis> getDiagnosisList () {
        return diagnosisList;
    }

    /**
     * Sets the prescription list of
     *
     * @param prescriptionsList
     *            to set to
     */
    public void setPrescriptionsList ( final ArrayList<Prescription> prescriptionsList ) {
        this.prescriptionsList = prescriptionsList;
    }

    /**
     * Returns the prescription list of the patient
     *
     * @return prescriptionsList of the patient
     */
    public List<Prescription> getPrescriptionsList () {
        return prescriptionsList;
    }

    /**
     * Sets the drugs list of the patient to the passed value
     *
     * @param drugsList
     *            to set to
     */
    public void setDrugsList ( final ArrayList<Drug> drugsList ) {
        this.drugsList = drugsList;
    }

    /**
     * Returns the drug list of the patient
     *
     * @return drugsList of the patient
     */
    public ArrayList<Drug> getDrugsLIst () {
        return drugsList;
    }

    /**
     * Sets the ICD Codes list of the patient
     *
     * @param icdCodesList
     *            to set to
     */
    public void setICDCodesList ( final ArrayList<ICDCode> icdCodesList ) {
        this.icdCodesList = icdCodesList;
    }

    /**
     * Returns the ICD Codes list of the patient
     *
     * @return icdCodesList of the patient
     */
    public ArrayList<ICDCode> getICDCodesList () {
        return icdCodesList;
    }

    /**
     * Sets the id of the record as a long
     *
     * @param recordID
     *            the record id to set to
     */
    public void setRecordID ( final Long recordID ) {
        this.recordID = recordID;
    }

    /**
     * Returns the records id as a long
     *
     * @return recordID the id of the record
     */
    public long getRecrodID () {
        return recordID;
    }

}
