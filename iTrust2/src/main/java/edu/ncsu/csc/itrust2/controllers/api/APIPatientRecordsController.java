package edu.ncsu.csc.itrust2.controllers.api;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.itrust2.models.persistent.Diagnosis;
import edu.ncsu.csc.itrust2.models.persistent.Drug;
import edu.ncsu.csc.itrust2.models.persistent.ICDCode;
import edu.ncsu.csc.itrust2.models.persistent.Patient;
import edu.ncsu.csc.itrust2.models.persistent.PatientRecords;
import edu.ncsu.csc.itrust2.models.persistent.Prescription;

/**
 * Controller class responsible for managing the behavior for the Patient
 * Records landing page
 *
 * @author Will Duke
 *
 */
@RestController
public class APIPatientRecordsController extends APIController {

    /**
     * An API method to call so that you can pass an MID of a user, and have
     * their record returned.
     *
     * @param mid
     *            the MID of the patient to get the record for
     * @return an array list of the patient records
     */
    @GetMapping ( BASE_PATH + "/viewPatientRecords/{mid}" )
    public ArrayList<PatientRecords> getAndMakePatientRecord ( @PathVariable ( "mid" ) final String mid ) {

        final Patient tempPatient = Patient.getByName( mid );

        final PatientRecords records = new PatientRecords();

        final ArrayList<PatientRecords> recordsArrayList = new ArrayList<PatientRecords>();

        if ( mid == null && tempPatient == null ) {
            return null;
        }

        records.setName( tempPatient.getFirstName() );

        final int age = getAgeCalculation( tempPatient );
        records.setAge( age );

        final SimpleDateFormat ft = new SimpleDateFormat( "MM.dd.yyyy" );
        records.setDateOfBirth( ft.format( tempPatient.getDateOfBirth().getTime() ) );

        records.setPatientGender( tempPatient.getGender() );

        records.setBloodType( tempPatient.getBloodType() );

        records.setPrescriptionsList( getPrescriptionsList( tempPatient ) );

        records.setDrugsList( getDrugsList( tempPatient, getPrescriptionsList( tempPatient ) ) );

        records.setDiagnosisList( getDiagnosisList( tempPatient ) );

        records.setICDCodesList( getICDCodesList( tempPatient, getDiagnosisList( tempPatient ) ) );

        recordsArrayList.add( records );

        return recordsArrayList;
    }

    /**
     * Returns the age of the patient by subtracting the year of their birth
     * from the current year.
     *
     * @param patient
     *            the patient who's age to calculate
     * @return age of the patient
     */
    private int getAgeCalculation ( final Patient patient ) {
        final Calendar calendar = Calendar.getInstance();
        return calendar.get( Calendar.YEAR ) - patient.getDateOfBirth().get( Calendar.YEAR );
    }

    /**
     * Returns the list of prescriptions for the patient
     *
     * @param tempPatient
     *            the patient to get the prescriptions for
     * @return prescriptions as an array list
     */
    private ArrayList<Prescription> getPrescriptionsList ( final Patient tempPatient ) {
        return (ArrayList<Prescription>) Prescription.getForPatient( tempPatient.getSelf().getId() );
    }

    /**
     * Returns the list of drugs for the patient from the prescriptions list of
     * that patient
     *
     * @param tempPatient
     *            the patient to get the list of drugs for
     * @param prescriptionsList
     *            the patient's current prescriptions
     * @return drugs as an array list
     */
    private ArrayList<Drug> getDrugsList ( final Patient tempPatient,
            final ArrayList<Prescription> prescriptionsList ) {
        final ArrayList<Drug> drugsList = new ArrayList<Drug>();
        final Drug tempDrug = new Drug();
        Drug drugFromPrescription;
        final int size = prescriptionsList.size();
        for ( int i = 0; i < size; i++ ) {
            drugFromPrescription = prescriptionsList.get( i ).getDrug();
            tempDrug.setName( drugFromPrescription.getName() );
            tempDrug.setCode( drugFromPrescription.getCode() );
            drugsList.add( tempDrug );
        }
        return drugsList;
    }

    /**
     * Returns the list of diagnosis for the patient
     *
     * @param tempPatient
     *            the patient to get the diagnosis for
     * @return Diagnosis array list
     */
    private List<Diagnosis> getDiagnosisList ( final Patient tempPatient ) {
        return Diagnosis.getForPatient( tempPatient.getSelf() );
    }

    /**
     * Returns the list of ICD Codes for the patient from the diagnosis list of
     * that patient
     *
     * @param tempPatient
     *            the patient to get the list for
     * @param list
     *            the patient's current diagnosis
     * @return icdcode as an array list
     */
    private ArrayList<ICDCode> getICDCodesList ( final Patient tempPatient, final List<Diagnosis> list ) {
        final ArrayList<ICDCode> icdCodesList = new ArrayList<ICDCode>();
        final ICDCode tempCode = new ICDCode();
        ICDCode icdCodeFromDiagnosis;
        final int size = list.size();
        for ( int i = 0; i < size; i++ ) {
            icdCodeFromDiagnosis = list.get( i ).getCode();
            tempCode.setCode( icdCodeFromDiagnosis.getCode() );
            tempCode.setDescription( icdCodeFromDiagnosis.getDescription() );
            icdCodesList.add( tempCode );
        }
        return icdCodesList;
    }

}
