package edu.ncsu.csc.itrust2.apitest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import edu.ncsu.csc.itrust2.config.RootConfiguration;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.persistent.Patient;
import edu.ncsu.csc.itrust2.models.persistent.User;
import edu.ncsu.csc.itrust2.mvc.config.WebMvcConfiguration;

/**
 * Testing class for testing APIRepresentativesController
 *
 * @author Jacob Struckmeyer, Will Duke
 *
 */
@RunWith ( SpringJUnit4ClassRunner.class )
@ContextConfiguration ( classes = { RootConfiguration.class, WebMvcConfiguration.class } )
@WebAppConfiguration
@FixMethodOrder ( MethodSorters.NAME_ASCENDING )
public class APIRepresentativeTest {

    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    /**
     * Sets up test
     */
    @Before
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();

        // Attempt to add first patient
        Patient p1 = Patient.getByName( "josh" );
        if ( p1 == null ) {
            final User p1User = new User( "josh", "123456", Role.ROLE_PATIENT, 1 );
            p1User.save();
            p1 = new Patient( p1User );
            p1.save();
        }

        // Attempt to add second patient
        Patient p2 = Patient.getByName( "kathy" );
        if ( p2 == null ) {
            final User p2User = new User( "kathy", "123456", Role.ROLE_PATIENT, 1 );
            p2User.save();
            p2 = new Patient( p2User );
            p2.save();
        }

        // Attempt to add second patient
        Patient p3 = Patient.getByName( "thomas" );
        if ( p3 == null ) {
            final User p3User = new User( "thomas", "123456", Role.ROLE_PATIENT, 1 );
            p3User.save();
            p3 = new Patient( p3User );
            p3.save();
        }

    }

    /**
     * Private helper method that adds a patient to another patient's list of
     * representatives
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "kathy", roles = { "PATIENT" } )
    public void addRepAsOtherUser () throws Exception {
        // Add valid user
        mvc.perform( post( "/api/v1/declareRep/josh" ) ).andExpect( status().isOk() );
    }

    /**
     * Tests that getting a patient that doesn't exist returns the proper
     * status.
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "hcp", roles = { "HCP" } )
    public void testGetNonExistentPatient () throws Exception {
        mvc.perform( get( "/api/v1/reps/-1" ) ).andExpect( status().isNotFound() );
    }

    /**
     * Tests that getting a valid patient representative returns the proper
     * status.
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "hcp", roles = { "HCP" } )
    public void testGetValidRepresentative () throws Exception {

        mvc.perform( get( "/api/v1/reps/josh" ) ).andExpect( status().isOk() );

    }

    /**
     * Test getting reps as logged in user
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "josh", roles = { "PATIENT" } )
    public void testGetRepresentativesAsPatient () throws Exception {

        mvc.perform( get( "/api/v1/reps" ) ).andExpect( status().isOk() );

    }

    /**
     * Tests declaring a personal representative as a patient
     */
    @Test
    @WithMockUser ( username = "josh", roles = { "PATIENT" } )
    public void testDeclareRepresentative () throws Exception {

        // Add valid user
        mvc.perform( post( "/api/v1/declareRep/kathy" ) ).andExpect( status().isOk() );

        // Attempt to add user that doesn't exist
        mvc.perform( post( "/api/v1/declareRep/nonexistent" ) ).andExpect( status().isNotFound() );

        // Attempt to add rep as self
        mvc.perform( post( "/api/v1/declareRep/josh" ) ).andExpect( status().isNotAcceptable() );

    }

    /**
     * Tests declaring a personal representative as an HCP
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "hcp", roles = { "HCP" } )
    public void testDeclareRepresentativeAsHCP () throws Exception {
        // Add valid rep to patient
        mvc.perform( post( "/api/v1/declareRepHCP/josh/thomas" ) ).andExpect( status().isOk() );

        // Attempt to add invalid rep to patient
        mvc.perform( post( "/api/v1/declareRepHCP/josh/nonexistent" ) ).andExpect( status().isNotFound() );

        // Attempt to add rep to invalid patient
        mvc.perform( post( "/api/v1/declareRepHCP/nonexistent/josh" ) ).andExpect( status().isNotFound() );

        // Attempt to add patient as own rep
        mvc.perform( post( "/api/v1/declareRepHCP/josh/josh" ) ).andExpect( status().isNotAcceptable() );
    }

    /**
     * Tests undeclaring a personal representative as a patient
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "josh", roles = { "PATIENT" } )
    public void testUndeclareRepresentative () throws Exception {

        // Remove valid patient
        mvc.perform( post( "/api/v1/undeclare/kathy" ) ).andExpect( status().isOk() );

        // Attempt to remove patient that does not exist
        mvc.perform( post( "/api/v1/undeclare/nonexistent" ) ).andExpect( status().isNotFound() );

        // Attempt to remove patient that exists but is not in the list of reps
        mvc.perform( post( "/api/v1/undeclare/kathy" ) ).andExpect( status().isNotFound() );

    }

    /**
     * Tests undeclaring a personal representative as a patient
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "josh", roles = { "PATIENT" } )
    public void testUndeclareSelfAsRepresentative () throws Exception {

        // Remove patient as rep
        mvc.perform( post( "/api/v1/undeclareSelf/kathy" ) ).andExpect( status().isOk() );

        // Attempt to remove nonexistant rep
        mvc.perform( post( "/api/v1/undeclareSelf/nonexistent" ) ).andExpect( status().isNotFound() );

        // Attempt to remove valid user that's not in list of reps
        mvc.perform( post( "/api/v1/undeclareSelf/thomas" ) ).andExpect( status().isNotFound() );

    }
}
