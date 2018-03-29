package edu.ncsu.csc.itrust2.apitest;

import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import edu.ncsu.csc.itrust2.config.RootConfiguration;
import edu.ncsu.csc.itrust2.mvc.config.WebMvcConfiguration;

@RunWith ( SpringJUnit4ClassRunner.class )
@ContextConfiguration ( classes = { RootConfiguration.class, WebMvcConfiguration.class } )
@WebAppConfiguration
@FixMethodOrder ( MethodSorters.NAME_ASCENDING )
public class RepresentativeControllerTest {

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
     * Tests that getting a representative that doesn't exist returns the proper
     * status.
     *
     * @throws Exception
     */
    @Test
    public void testNonExistentRepresentatives () throws Exception {
        mvc.perform( get( "/api/v1/reps/-1" ) ).andExpect( status().isNotFound() );
    }

    /**
     * Tests that getting a valid patient representative returns the proper
     * status.
     *
     * @throws Exception
     */
    @Test
    public void testGetValidRepresentative () throws Exception {
        mvc.perform( get( "/api/v1/reps/patient" ) ).andExpect( status().isOk() );
    }

    @Test
    public void testGetRepresentatives () throws Exception {
        fail( "Not yet implemented" );
        // mvc.perform( get( "/api/v1/reps/" ) ).andExpect( status().isOk() );
    }

    @Test
    public void testDeclareRepresentative () {
        fail( "Not yet implemented" );
    }

    @Test
    public void testDeclareRepresentativeAsHCP () {
        fail( "Not yet implemented" );
    }

    @Test
    public void testUndeclareRepresentative () {
        fail( "Not yet implemented" );
    }

    @Test
    public void testToJsonObject () {
        fail( "Not yet implemented" );
    }

    @Test
    public void testToJsonObjectClassOfJSONResponse () {
        fail( "Not yet implemented" );
    }

    @Test
    public void testResponseMessage () {
        fail( "Not yet implemented" );
    }

    @Test
    public void testErrorResponse () {
        fail( "Not yet implemented" );
    }

    @Test
    public void testSuccessResponse () {
        fail( "Not yet implemented" );
    }

}
