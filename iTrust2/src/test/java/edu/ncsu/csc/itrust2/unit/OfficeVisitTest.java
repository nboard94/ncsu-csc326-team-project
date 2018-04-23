package edu.ncsu.csc.itrust2.unit;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.junit.Before;
import org.junit.Test;

import edu.ncsu.csc.itrust2.models.enums.AppointmentType;
import edu.ncsu.csc.itrust2.models.enums.HouseholdSmokingStatus;
import edu.ncsu.csc.itrust2.models.enums.Priority;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.enums.Status;
import edu.ncsu.csc.itrust2.models.persistent.BasicHealthMetrics;
import edu.ncsu.csc.itrust2.models.persistent.Diagnosis;
import edu.ncsu.csc.itrust2.models.persistent.Drug;
import edu.ncsu.csc.itrust2.models.persistent.Hospital;
import edu.ncsu.csc.itrust2.models.persistent.ICDCode;
import edu.ncsu.csc.itrust2.models.persistent.LabProcedure;
import edu.ncsu.csc.itrust2.models.persistent.LabRequest;
import edu.ncsu.csc.itrust2.models.persistent.OfficeVisit;
import edu.ncsu.csc.itrust2.models.persistent.Prescription;
import edu.ncsu.csc.itrust2.models.persistent.User;
import edu.ncsu.csc.itrust2.models.persistent.VacRecord;
import edu.ncsu.csc.itrust2.models.persistent.Vaccination;

/**
 * Tests the functionality of creating, editing, and saving an office visit
 *
 * @author someone else
 * @author Nick Board
 */
public class OfficeVisitTest {

    /**
     * Creates necessary objects before testing
     */
    @Before
    public void setUp () {
        User labTech = User.getByName( "ov_test_labtech" );
        if ( labTech == null ) {
            labTech = new User( "ov_test_labtech", "123456", Role.ROLE_LABTECH, 1 );
            labTech.save();
        }

        User patient = User.getByName( "ov_test_patient" );
        if ( patient == null ) {
            patient = new User( "ov_test_patient", "123456", Role.ROLE_PATIENT, 1 );
            patient.save();
        }

        User hcp = User.getByName( "ov_test_hcp" );
        if ( hcp == null ) {
            hcp = new User( "ov_test_hcp", "123456", Role.ROLE_HCP, 1 );
            hcp.save();
        }

        LabProcedure proc = LabProcedure.getByCode( "111111-11" );
        if ( proc == null ) {
            proc = new LabProcedure();
            proc.setCode( "111111-11" );
            proc.setCommonName( "commonName" );
            proc.setComponent( "component" );
            proc.setProperty( "property" );
            proc.save();
        }
    }

    /**
     * Tests office visit class
     * Test class for testing office visits
     */
    @Test
    public void testOfficeVisit () {

        final Hospital hosp = new Hospital( "Dr. Jenkins' Insane Asylum", "123 Main St", "12345", "NC" );
        hosp.save();

        final OfficeVisit visit = new OfficeVisit();

        final BasicHealthMetrics bhm = new BasicHealthMetrics();

        bhm.setDiastolic( 150 );
        bhm.setDiastolic( 100 );
        bhm.setHcp( User.getByName( "hcp" ) );
        bhm.setPatient( User.getByName( "AliceThirteen" ) );
        bhm.setHdl( 75 );
        bhm.setHeight( 75f );
        bhm.setHouseSmokingStatus( HouseholdSmokingStatus.NONSMOKING );

        bhm.save();

        visit.setBasicHealthMetrics( bhm );
        visit.setType( AppointmentType.GENERAL_CHECKUP );
        visit.setHospital( hosp );
        visit.setPatient( User.getByName( "AliceThirteen" ) );
        visit.setHcp( User.getByName( "AliceThirteen" ) );
        visit.setDate( Calendar.getInstance() );
        visit.save();

        final List<Diagnosis> diagnoses = new Vector<Diagnosis>();

        final ICDCode code = new ICDCode();
        code.setCode( "A21" );
        code.setDescription( "Top Quality" );

        code.save();

        final Diagnosis diagnosis = new Diagnosis();

        diagnosis.setCode( code );
        diagnosis.setNote( "This is bad" );
        diagnosis.setVisit( visit );

        diagnoses.add( diagnosis );

        visit.setDiagnoses( diagnoses );

        visit.save();

        final Drug drug = new Drug();
        drug.setCode( "1234-4321-89" );
        drug.setDescription( "Lithium Compounds" );
        drug.setName( "Li2O8" );
        drug.save();

        final Prescription pres = new Prescription();
        pres.setDosage( 3 );
        pres.setDrug( drug );

        final Calendar end = Calendar.getInstance();
        end.add( Calendar.DAY_OF_WEEK, 10 );
        pres.setEndDate( end );
        pres.setPatient( User.getByName( "AliceThirteen" ) );
        pres.setStartDate( Calendar.getInstance() );
        pres.setRenewals( 5 );

        pres.save();

        visit.setPrescriptions( Collections.singletonList( pres ) );

        visit.save();

        visit.setPrescriptions( Collections.emptyList() );

        visit.save();

        // Test adding/getting lab procedures
        assertEquals( 0, visit.getLabRequests().size() );

        final LabRequest lr = new LabRequest();
        lr.setHcp( User.getByName( "ov_test_hcp" ) );
        lr.setPatient( User.getByName( "ov_test_patient" ) );
        lr.setLabTech( User.getByName( "ov_test_labtech" ) );
        lr.setPriority( Priority.PRIORITY_HIGH );
        lr.setStatus( Status.PENDING );
        lr.setLabProcedure( LabProcedure.getByCode( "111111-11" ) );

        final LabRequest lr2 = new LabRequest();
        lr2.setHcp( User.getByName( "ov_test_hcp" ) );
        lr2.setPatient( User.getByName( "ov_test_patient" ) );
        lr2.setLabTech( User.getByName( "ov_test_labtech" ) );
        lr2.setPriority( Priority.PRIORITY_LOW );
        lr2.setStatus( Status.WORKING );
        lr2.setLabProcedure( LabProcedure.getByCode( "111111-11" ) );

        Set<LabRequest> set = new HashSet<LabRequest>();
        set.add( lr );
        set.add( lr2 );
        visit.setLabRequests( set );

        visit.save();

        assertEquals( 2, OfficeVisit.getById( visit.getId() ).getLabRequests().size() );

        // Try to add a 3rd lab request
        final LabRequest lr3 = new LabRequest();
        lr3.setHcp( User.getByName( "ov_test_hcp" ) );
        lr3.setPatient( User.getByName( "ov_test_patient" ) );
        lr3.setLabTech( User.getByName( "ov_test_labtech" ) );
        lr3.setPriority( Priority.PRIORITY_VERY_HIGH );
        lr3.setStatus( Status.COMPLETED );
        lr3.setLabProcedure( LabProcedure.getByCode( "111111-11" ) );

        set = OfficeVisit.getById( visit.getId() ).getLabRequests();
        set.add( lr3 );

        visit.setLabRequests( set );

        visit.save();

        assertEquals( 3, OfficeVisit.getById( visit.getId() ).getLabRequests().size() );

        // Try to remove one of the lab requests
        set = new HashSet<LabRequest>();
        set.add( LabRequest.getById( lr.getId() ) );
        set.add( LabRequest.getById( lr2.getId() ) );
        visit.setLabRequests( set );
        visit.save();

        assertEquals( 2, OfficeVisit.getById( visit.getId() ).getLabRequests().size() );

        final Vaccination vaccination = new Vaccination();
        vaccination.setCode( "01234" );
        vaccination.setName( "TEST" );
        vaccination.setDescription( "DESC" );
        vaccination.save();

        final VacRecord vacrec = new VacRecord();
        vacrec.setVaccination( vaccination );
        vacrec.setPatient( User.getByName( "AliceThirteen" ) );
        vacrec.setAdministrationDate( Calendar.getInstance() );

        vacrec.save();

        visit.setVacRecords( Collections.singletonList( vacrec ) );

        visit.save();

        visit.setVacRecords( Collections.emptyList() );

        visit.save();
        visit.delete();
    }

}
