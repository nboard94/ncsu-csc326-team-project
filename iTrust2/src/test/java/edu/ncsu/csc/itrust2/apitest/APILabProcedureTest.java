/**
 *
 */
package edu.ncsu.csc.itrust2.apitest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import edu.ncsu.csc.itrust2.config.RootConfiguration;
import edu.ncsu.csc.itrust2.forms.admin.LabProcedureForm;
import edu.ncsu.csc.itrust2.forms.personnel.PersonnelForm;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.persistent.DomainObject;
import edu.ncsu.csc.itrust2.models.persistent.LabProcedure;
import edu.ncsu.csc.itrust2.models.persistent.Personnel;
import edu.ncsu.csc.itrust2.models.persistent.User;
import edu.ncsu.csc.itrust2.mvc.config.WebMvcConfiguration;

/**
 * Tests the lab procedure controller
 *
 * @author Jacob Struckmeyer
 *
 */
@RunWith ( SpringJUnit4ClassRunner.class )
@ContextConfiguration ( classes = { RootConfiguration.class, WebMvcConfiguration.class } )
@WebAppConfiguration
@FixMethodOrder ( MethodSorters.NAME_ASCENDING )
public class APILabProcedureTest {

    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    /**
     * Sets up test
     *
     * @throws Exception
     */
    @Before
    public void setup () throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();
        User adminUser = User.getByName( "admin" );
        if ( adminUser == null ) {
            adminUser = new User( "admin", "123456", Role.ROLE_ADMIN, 1 );
            adminUser.save();
        }
        Personnel admin = Personnel.getByName( "admin" );
        if ( admin == null ) {
            final PersonnelForm adminForm = new PersonnelForm( adminUser );
            adminForm.setState( "AL" );
            admin = new Personnel( adminForm );
            admin.save();
        }
    }

    /**
     * Tests the APILabProcedureController
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "admin", roles = { "USER", "ADMIN" } )
    public void testLabProcedureAPI () throws Exception {
        // Delete all lab procedure before testing

        DomainObject.deleteAll( LabProcedure.class );

        final LabProcedureForm lpf1 = new LabProcedureForm();
        lpf1.setCode( "876543-21" );
        lpf1.setCommonName( "commonName" );
        lpf1.setComponent( "compnonent" );
        lpf1.setProperty( "property" );

        /** Test adding */
        // Add lab procedure
        mvc.perform( post( "/api/v1/labprocedures" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( lpf1 ) ) ).andExpect( status().isOk() );

        // Add again and it should fail
        mvc.perform( post( "/api/v1/labprocedures" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( lpf1 ) ) ).andExpect( status().isConflict() );

        /** Test editing */
        // Edit the lab procedure
        LabProcedure lp1 = LabProcedure.getByCode( "876543-21" );
        final LabProcedureForm lpf2 = new LabProcedureForm( lp1 );
        lpf2.setCommonName( "newCommonName" );

        mvc.perform( put( "/api/v1/labprocedures" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( lpf2 ) ) ).andExpect( status().isOk() );

        // Edit lab procedure that doesn't exist
        lpf1.setCode( "222222-22" );

        mvc.perform( put( "/api/v1/labprocedures" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( lpf1 ) ) ).andExpect( status().isNotFound() );

        // Attempt to change code of lab to a code that already exists
        lpf1.setCode( "123456-78" );
        mvc.perform( post( "/api/v1/labprocedures" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( lpf1 ) ) ).andExpect( status().isOk() );

        lp1 = LabProcedure.getByCode( "876543-21" );
        final LabProcedureForm lpf3 = new LabProcedureForm( lp1 );
        lpf3.setCode( "123456-78" );

        mvc.perform( put( "/api/v1/labprocedures" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( lpf3 ) ) ).andExpect( status().isConflict() );

        /** Test delete */
        // Delete existing lab
        String id = Long.toString( LabProcedure.getByCode( "876543-21" ).getId() );

        mvc.perform( delete( "/api/v1/labprocedures/" + id ) ).andExpect( status().isOk() );

        // Attempt to delete lab that doesn't exist
        mvc.perform( delete( "/api/v1/labprocedures/0000" ) ).andExpect( status().isNotFound() );

        /** Test get */
        mvc.perform( get( "/api/v1/labprocedures" ) ).andExpect( status().isOk() )
                .andExpect( content().string( Matchers.containsString( lpf1.getCode() ) ) );

        /** Delete any lab procedures that were added */
        id = Long.toString( LabProcedure.getByCode( "123456-78" ).getId() );
        mvc.perform( delete( "/api/v1/labprocedures/" + id ) ).andExpect( status().isOk() );

    }

}
