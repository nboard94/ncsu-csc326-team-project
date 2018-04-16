/**
 *
 */
package edu.ncsu.csc.itrust2.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.text.ParseException;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.ncsu.csc.itrust2.forms.hcp.LabRequestForm;
import edu.ncsu.csc.itrust2.models.enums.Priority;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.enums.Status;
import edu.ncsu.csc.itrust2.models.persistent.DomainObject;
import edu.ncsu.csc.itrust2.models.persistent.LabProcedure;
import edu.ncsu.csc.itrust2.models.persistent.LabRequest;
import edu.ncsu.csc.itrust2.models.persistent.User;

/**
 * Tests the LabRequest Object class
 *
 * @author Jacob Struckmeyer
 *
 */
public class LabRequestTest {

    /**
     * Creates all necessary users for testing
     */
    @BeforeClass
    public static void createUsers () {
        User labTech = User.getByName( "lr_test_labtech" );
        if ( labTech == null ) {
            labTech = new User( "lr_test_labtech", "123456", Role.ROLE_LABTECH, 1 );
            labTech.save();
        }

        User patient = User.getByName( "lr_test_patient" );
        if ( patient == null ) {
            patient = new User( "lr_test_patient", "123456", Role.ROLE_PATIENT, 1 );
            patient.save();
        }

        User hcp = User.getByName( "lr_test_hcp" );
        if ( hcp == null ) {
            hcp = new User( "lr_test_hcp", "123456", Role.ROLE_HCP, 1 );
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
     * Tests creating a lab request
     *
     * @throws ParseException
     */
    @Test
    public void testMakeLabRequest () throws ParseException {

        // Create LabRequest via empyt constructor
        final LabRequest lr1 = new LabRequest();
        lr1.setComments( "comment" );
        lr1.setHcp( User.getByName( "lr_test_hcp" ) );
        lr1.setLabTech( User.getByName( "lr_test_labtech" ) );
        lr1.setPatient( User.getByName( "lr_test_patient" ) );
        lr1.setPriority( Priority.PRIORITY_LOW );
        lr1.setStatus( Status.PENDING );
        lr1.setLabProcedure( LabProcedure.getByCode( "111111-11" ) );
        lr1.save();

        LabRequest savedLr = LabRequest.getById( lr1.getId() );
        assertEquals( "comment", savedLr.getComments() );
        assertEquals( "lr_test_hcp", savedLr.getHcp().getUsername() );
        assertEquals( "lr_test_labtech", savedLr.getLabTech().getUsername() );
        assertEquals( "lr_test_patient", savedLr.getPatient().getUsername() );
        assertEquals( "PRIORITY_LOW", savedLr.getPriority().name() );
        assertEquals( "PENDING", savedLr.getStatus().name() );
        assertEquals( "111111-11", savedLr.getLabProcedure().getCode() );
        assertNotNull( savedLr.getId() );

        // Create LabRequest via LabRequestForm
        final LabRequestForm lqr1 = new LabRequestForm();
        lqr1.setComments( "comment1" );
        lqr1.setHcp( "lr_test_hcp" );
        lqr1.setLabTech( "lr_test_labtech" );
        lqr1.setPatient( "lr_test_patient" );
        lqr1.setPriority( "PRIORITY_HIGH" );
        lqr1.setStatus( "PENDING" );
        lqr1.setLabProcedure( "111111-11" );
        final LabRequest lr2 = new LabRequest( lqr1 );
        lr2.save();

        savedLr = LabRequest.getById( lr2.getId() );
        assertEquals( "comment1", savedLr.getComments() );
        assertEquals( "lr_test_hcp", savedLr.getHcp().getUsername() );
        assertEquals( "lr_test_labtech", savedLr.getLabTech().getUsername() );
        assertEquals( "lr_test_patient", savedLr.getPatient().getUsername() );
        assertEquals( "PRIORITY_HIGH", savedLr.getPriority().name() );
        assertEquals( "PENDING", savedLr.getStatus().name() );
        assertEquals( "111111-11", savedLr.getLabProcedure().getCode() );
        assertNotNull( savedLr.getId() );

        // Test creating a LabRequestform from a LabRequest
        final LabRequestForm form = new LabRequestForm( savedLr );
        assertEquals( "comment1", form.getComments() );
        assertEquals( "lr_test_hcp", form.getHcp() );
        assertEquals( "lr_test_labtech", form.getLabTech() );
        assertEquals( "lr_test_patient", form.getPatient() );
        assertEquals( "PRIORITY_HIGH", form.getPriority() );
        assertEquals( "PENDING", form.getStatus() );
        assertEquals( "111111-11", form.getLabProcedure() );
        assertNotNull( form.getId() );

        DomainObject.deleteAll( LabRequest.class );

    }

}
