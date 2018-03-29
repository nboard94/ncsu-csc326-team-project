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
    }

    /**
     * Tests that getting a patient that doesn't exist returns the proper
     * status.
     *
     * @throws Exception
     */
    @Test
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

        mvc.perform( get( "/api/v1/reps/patient" ) ).andExpect( status().isOk() );

    }

    /**
     * Test getting reps as logged in user
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "patient", roles = { "PATIENT" } )
    public void testGetRepresentativesAsPatient () throws Exception {

        mvc.perform( get( "/api/v1/reps" ) ).andExpect( status().isOk() );

    }

    /**
     * Test getting reps as logged in user
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "hcp", roles = { "HCP" } )
    public void testGetRepresentativesAsHCP () throws Exception {

        mvc.perform( get( "/api/v1/reps" ) ).andExpect( status().isNotFound() );

    }

    /**
     * Tests declaring a personal representative as a patient
     */
    @Test
    @WithMockUser ( username = "patient", roles = { "PATIENT" } )
    public void testDeclareRepresentative () throws Exception {

        // Add valid user
        mvc.perform( post( "/api/v1/declareRep/AliceThirteen" ) ).andExpect( status().isOk() );

        // Attempt to add user that doesn't exist
        mvc.perform( post( "/api/v1/declareRep/nonexistent" ) ).andExpect( status().isNotFound() );

        // Attempt to add rep as self
        mvc.perform( post( "/api/v1/declareRep/patient" ) ).andExpect( status().isNotAcceptable() );

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
        mvc.perform( post( "/api/v1/declareRepHCP/patient,AliceThirteen" ) ).andExpect( status().isOk() );

        // Attempt to add invalid rep to patient
        mvc.perform( post( "/api/v1/declareRepHCP/patient,nonexistent" ) ).andExpect( status().isNotFound() );

        // Attempt to add rep to invalid patient
        mvc.perform( post( "/api/v1/declareRepHCP/nonexistent,AliceThirteen" ) ).andExpect( status().isNotFound() );

        // Attempt to add patient as own rep
        mvc.perform( post( "/api/v1/declareRepHCP/AliceThirteen,AliceThirteen" ) )
                .andExpect( status().isNotAcceptable() );
    }

    /**
     * Tests undeclaring a personal representative as a patient
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "patient", roles = { "PATIENT" } )
    public void testUndeclareRepresentative () throws Exception {
        // first add valid patient
        mvc.perform( post( "/api/v1/declareRep/AliceThirteen" ) ).andExpect( status().isOk() );

        // Then remove them
        mvc.perform( post( "/api/v1/undeclareRep/0,AliceThirteen" ) ).andExpect( status().isOk() );

        // Attempt to remove patient that doesn't exist
        mvc.perform( post( "/api/v1/undeclareRep/0,nonexistent" ) ).andExpect( status().isNotFound() );

        // Attempt to remove patient with invalid mode
        mvc.perform( post( "/api/v1/undeclareRep/3,nonexistent" ) ).andExpect( status().isNotAcceptable() );

        // Attempt to remove valid patient that isn't a representative of user
        mvc.perform( post( "/api/v1/undeclareRep/1,AliceThirteen" ) ).andExpect( status().isExpectationFailed() );
    }
}
