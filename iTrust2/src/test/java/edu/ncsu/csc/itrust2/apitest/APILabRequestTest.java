package edu.ncsu.csc.itrust2.apitest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.ncsu.csc.itrust2.config.RootConfiguration;
import edu.ncsu.csc.itrust2.forms.hcp.LabRequestForm;
import edu.ncsu.csc.itrust2.models.enums.Priority;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.enums.Status;
import edu.ncsu.csc.itrust2.models.persistent.LabProcedure;
import edu.ncsu.csc.itrust2.models.persistent.LabRequest;
import edu.ncsu.csc.itrust2.models.persistent.User;
import edu.ncsu.csc.itrust2.mvc.config.WebMvcConfiguration;

/**
 * Class for testing LabRequest API.
 *
 * @author Jacob Struckmeyer
 */
@RunWith ( SpringJUnit4ClassRunner.class )
@ContextConfiguration ( classes = { RootConfiguration.class, WebMvcConfiguration.class } )
@WebAppConfiguration
public class APILabRequestTest {
    private MockMvc               mvc;

    private Gson                  gson;
    LabRequestForm                requestForm;

    @Autowired
    private WebApplicationContext context;

    /**
     * Creates users that will be
     */
    @BeforeClass
    public static void onlyOnce () {
        User user = User.getByName( "hcp" );
        if ( user == null ) {
            user = new User( "hcp", "123456", Role.ROLE_HCP, 1 );
            user.save();
        }
        user = User.getByName( "test_labtech" );
        if ( user == null ) {
            user = new User( "test_labtech", "123456", Role.ROLE_LABTECH, 1 );
            user.save();
        }
        user = User.getByName( "patient" );
        if ( user == null ) {
            user = new User( "patient", "123456", Role.ROLE_PATIENT, 1 );
            user.save();
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
     * Performs setup operations for the tests.
     *
     * @throws Exception
     */
    @Before
    @WithMockUser ( username = "admin", roles = { "USER", "ADMIN" } )
    public void setup () throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();
        gson = new GsonBuilder().create();
        mvc.perform( delete( "/api/v1/labrequests" ) );
    }

    /**
     * Tests basic prescription APIs.
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "labtech", roles = { "LABTECH" } )
    public void testLabRequestAPI () throws Exception {
        final LabRequest lr = new LabRequest();
        lr.setComments( "comments" );
        lr.setHcp( User.getByName( "hcp" ) );
        lr.setLabTech( User.getByName( "test_labtech" ) );
        lr.setPatient( User.getByName( "patient" ) );
        lr.setLabProcedure( LabProcedure.getByCode( "111111-11" ) );
        lr.setPriority( Priority.PRIORITY_HIGH );
        lr.setStatus( Status.PENDING );
        lr.save();

        // final String content1 = mvc.perform( get( "/api/v1/labrequests" )
        // ).andExpect( status().isOk() ).andReturn()
        // .getResponse().getContentAsString();
    }

}
