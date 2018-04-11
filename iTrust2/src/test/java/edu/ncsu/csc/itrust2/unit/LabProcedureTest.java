package edu.ncsu.csc.itrust2.unit;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;

import org.junit.Test;

import edu.ncsu.csc.itrust2.forms.admin.LabProcedureForm;
import edu.ncsu.csc.itrust2.forms.hcp.LabRequestForm;
import edu.ncsu.csc.itrust2.models.enums.Priority;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.persistent.LabProcedure;
import edu.ncsu.csc.itrust2.models.persistent.LabRequest;
import edu.ncsu.csc.itrust2.models.persistent.User;

/**
 * Tests the lab procedure functionality
 *
 * @author Cody Roberts (jdrobe10)
 */
public class LabProcedureTest {

    /**
     * Tests the lab procedure object and form
     */
    @Test
    public void testLabProcedure () {
        // Create two lab procedures
        final LabProcedure lp = new LabProcedure();
        LabProcedure lp2 = new LabProcedure();

        // Create two lab procedure forms
        LabProcedureForm lpf = new LabProcedureForm();
        final LabProcedureForm lpf2 = new LabProcedureForm();

        // Fill the fields of lp
        lp.setCode( "1111-11" );
        lp.setCommonName( "one" );
        lp.setComponent( "comp" );
        lp.setProperty( "prop" );

        // Test that these values are set
        assertEquals( "1111-11", lp.getCode() );
        assertEquals( "one", lp.getCommonName() );
        assertEquals( "comp", lp.getComponent() );
        assertEquals( "prop", lp.getProperty() );

        // Fill the fields of lpf2
        lpf2.setCode( "1111-11" );
        lpf2.setCommonName( "one" );
        lpf2.setComponent( "comp" );
        lpf2.setId( lp.getId() );
        lpf2.setProperty( "prop" );

        // Test that these values are set
        assertEquals( "1111-11", lpf2.getCode() );
        assertEquals( "one", lpf2.getCommonName() );
        assertEquals( "comp", lpf2.getComponent() );
        assertEquals( lp.getId(), lpf2.getId() );
        assertEquals( "prop", lpf2.getProperty() );

        // Set lp2 through the form
        lp2 = new LabProcedure( lpf2 );

        // Set lpf through the object
        lpf = new LabProcedureForm( lp );

        // Test that these values are equal
        assertEquals( lp.getCode(), lp2.getCode() );
        assertEquals( lp.getCommonName(), lp2.getCommonName() );
        assertEquals( lp.getComponent(), lp2.getComponent() );
        assertEquals( lp.getProperty(), lp2.getProperty() );
        assertEquals( lpf.getCode(), lpf2.getCode() );
        assertEquals( lpf.getCommonName(), lpf2.getCommonName() );
        assertEquals( lpf.getComponent(), lpf2.getComponent() );
        assertEquals( lpf.getId(), lpf2.getId() );
        assertEquals( lpf.getProperty(), lpf2.getProperty() );

    }

    /**
     * Tests the LabRequest Object and Form
     *
     * @throws ParseException
     */
    @Test
    public void testLabRequest () throws ParseException {
        // Create lab tech user
        User lt = User.getByName( "labtech" );
        if ( lt == null ) {
            lt = new User();
            lt.setRole( Role.ROLE_LABTECH );
            lt.setUsername( "labtech" );
            lt.setPassword( "passwo1" );
            lt.save();
        }

        // Create hcp user
        User hcp = User.getByName( "hcp" );
        if ( hcp == null ) {
            hcp = new User();
            hcp.setRole( Role.ROLE_HCP );
            hcp.setUsername( "hcp" );
            hcp.setPassword( "passwo2" );
            hcp.save();
        }

        // Create patient user
        User pat = User.getByName( "patient" );
        if ( pat == null ) {
            pat = new User();
            pat.setRole( Role.ROLE_PATIENT );
            pat.setUsername( "patient" );
            pat.setPassword( "passwo3" );
            pat.save();
        }

        // Create a lab procedures
        LabProcedure lp = LabProcedure.getByCode( "1111-11" );
        if ( lp == null ) {
            lp = new LabProcedure();
            lp.setCode( "111111-11" );
            lp.setCommonName( "one" );
            lp.setComponent( "comp" );
            lp.setProperty( "prop" );
            lp.save();
        }

        // Create two LabRequests
        final LabRequest lr = new LabRequest();
        LabRequest lr2 = new LabRequest();

        // Create two LabRequestForms
        LabRequestForm lrf = new LabRequestForm();
        final LabRequestForm lrf2 = new LabRequestForm();

        // Fill the fields of lr
        lr.setComments( "comments" );
        lr.setHcp( hcp );
        lr.setLabTech( lt );
        lr.setPatient( pat );
        lr.setLabProcedure( lp );
        lr.setPriority( Priority.PRIORITY_HIGH );

        // Test that these values are set
        assertEquals( "comments", lr.getComments() );
        assertEquals( "hcp", lr.getHcp().getUsername() );
        assertEquals( "labtech", lr.getLabTech().getUsername() );
        assertEquals( "patient", lr.getPatient().getUsername() );
        assertEquals( lp, lr.getLabProcedure() );
        assertEquals( Priority.PRIORITY_HIGH, lr.getPriority() );

        // Fill the fields of lrf2
        lrf2.setComments( "comments" );
        lrf2.setHcp( "hcp" );
        lrf2.setLabTech( "labtech" );
        lrf2.setPatient( "patient" );
        lrf2.setPriority( "PRIORITY_HIGH" );
        lrf2.setProc( lp.getCode() );

        // Test that these values are set
        assertEquals( "comments", lrf2.getComments() );
        assertEquals( "hcp", lrf2.getHcp() );
        assertEquals( "labtech", lrf2.getLabTech() );
        assertEquals( "patient", lrf2.getPatient() );
        assertEquals( lp.getId(), LabProcedure.getByCode( lrf2.getProc() ).getId(), 3 );
        assertEquals( "PRIORITY_HIGH", lrf2.getPriority() );

        // Set lp2 through form
        lr2 = new LabRequest( lrf2 );

        // Set lrf through object
        lrf = new LabRequestForm( lr );

        // Check that these values are equal
        assertEquals( lr.getComments(), lr2.getComments() );
        assertEquals( lr.getHcp(), lr2.getHcp() );
        assertEquals( lr.getLabProcedure().getCode(), lr2.getLabProcedure().getCode() );
        assertEquals( lr.getLabTech(), lr2.getLabTech() );
        assertEquals( lr.getPatient(), lr2.getPatient() );
        assertEquals( lr.getPriority(), lr2.getPriority() );
        assertEquals( lrf.getComments(), lrf2.getComments() );
        assertEquals( lrf.getHcp(), lrf2.getHcp() );
        assertEquals( lrf.getLabTech(), lrf2.getLabTech() );
        assertEquals( lrf.getPatient(), lrf2.getPatient() );
        assertEquals( lrf.getPriority(), lrf2.getPriority() );
        assertEquals( lrf.getProc(), lrf2.getProc() );
    }

}
