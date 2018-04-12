package edu.ncsu.csc.itrust2.unit;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.ncsu.csc.itrust2.forms.admin.LabProcedureForm;
import edu.ncsu.csc.itrust2.models.persistent.LabProcedure;

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

}
