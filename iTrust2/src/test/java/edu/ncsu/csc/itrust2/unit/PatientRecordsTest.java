package edu.ncsu.csc.itrust2.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import edu.ncsu.csc.itrust2.models.enums.BloodType;
import edu.ncsu.csc.itrust2.models.enums.Gender;
import edu.ncsu.csc.itrust2.models.persistent.Diagnosis;
import edu.ncsu.csc.itrust2.models.persistent.Drug;
import edu.ncsu.csc.itrust2.models.persistent.ICDCode;
import edu.ncsu.csc.itrust2.models.persistent.Patient;
import edu.ncsu.csc.itrust2.models.persistent.PatientRecords;
import edu.ncsu.csc.itrust2.models.persistent.Prescription;

/**
 * Tests the functionality of the patient records class
 *
 * @author Will Duke
 *
 */
public class PatientRecordsTest {

    /** A patient to use for testing purposes */
    Patient        tempPatient = new Patient();

    /** A patient record to use for testing purposes */
    PatientRecords record      = new PatientRecords();

    /**
     * Ensures names are set and returned properly
     */
    @Test
    public void testSetName () {
        assertNull( record.getName() );
        record.setName( "name" );
        assertEquals( "name", record.getName() );
    }

    /**
     * Ensures ages are set and returned properly
     */
    @Test
    public void testSetAge () {
        assertEquals( 0, record.getAge() );
        record.setAge( 22 );
        assertEquals( 22, record.getAge() );
    }

    /**
     * Ensures date of birth is set and returned properly
     */
    @Test
    public void testSetDateOfBirth () {
        assertNull( record.getDateOfBirth() );
        record.setDateOfBirth( "03/05/1996" );
        assertEquals( "03/05/1996", record.getDateOfBirth() );
    }

    /**
     * Ensures patient gender is set and returned properly
     */
    @Test
    public void testSetPatientGender () {
        assertNull( record.getPatientGender() );
        record.setPatientGender( Gender.Male );
        assertEquals( Gender.Male, record.getPatientGender() );
    }

    /**
     * Ensures patient blood type is set and returned properly
     */
    @Test
    public void testSetBloodType () {
        assertNull( record.getBloodType() );
        record.setBloodType( BloodType.ABPos );
        assertEquals( BloodType.ABPos, record.getBloodType() );
    }

    /**
     * Ensures diagnosis list is set and returned properly
     */
    @Test
    public void testSetDiagnosisList () {
        final List<Diagnosis> tempList = new ArrayList<Diagnosis>();
        assertNull( record.getDiagnosisList() );
        record.setDiagnosisList( tempList );
        assertEquals( tempList, record.getDiagnosisList() );
    }

    /**
     * Ensures the prescriptions list is set and returned properly
     */
    @Test
    public void testSetPrescriptionsList () {
        final ArrayList<Prescription> tempList = new ArrayList<Prescription>();
        assertNull( record.getPrescriptionsList() );
        record.setPrescriptionsList( tempList );
        assertEquals( tempList, record.getPrescriptionsList() );
    }

    /**
     * Ensures the drugs list is set and returned properly
     */
    @Test
    public void testSetDrugsList () {
        final ArrayList<Drug> tempList = new ArrayList<Drug>();
        assertNull( record.getDrugsLIst() );
        record.setDrugsList( tempList );
        assertEquals( tempList, record.getDrugsLIst() );
    }

    /**
     * Ensures ICD Codes list is set and returned properly
     */
    @Test
    public void testSetICDCodesList () {
        final ArrayList<ICDCode> tempList = new ArrayList<ICDCode>();
        assertNull( record.getICDCodesList() );
        record.setICDCodesList( tempList );
        assertEquals( tempList, record.getICDCodesList() );
    }

    /**
     * Ensures the record id is set and returned properly
     */
    @Test
    public void testSetRecordID () {
        assertEquals( 0, record.getRecrodID() );
        record.setRecordID( (long) 123456 );
        assertEquals( 123456, record.getRecrodID() );
    }

}
