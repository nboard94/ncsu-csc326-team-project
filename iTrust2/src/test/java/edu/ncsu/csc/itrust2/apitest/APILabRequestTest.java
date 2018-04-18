package edu.ncsu.csc.itrust2.apitest;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

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

    private final Type            listType = new TypeToken<ArrayList<LabRequest>>() {
                                           }.getType();

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
        user = User.getByName( "test_labtech2" );
        if ( user == null ) {
            user = new User( "test_labtech2", "123456", Role.ROLE_LABTECH, 1 );
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
    @WithMockUser ( username = "test_labtech", roles = { "LABTECH" } )
    public void testLabRequestAPI () throws Exception {

        // TEST GET LAB REQUESTS

        LabRequest lr = new LabRequest();
        lr.setComments( "comments" );
        lr.setHcp( User.getByName( "hcp" ) );
        lr.setLabTech( User.getByName( "test_labtech" ) );
        lr.setPatient( User.getByName( "patient" ) );
        lr.setLabProcedure( LabProcedure.getByCode( "111111-11" ) );
        lr.setPriority( Priority.PRIORITY_HIGH );
        lr.setStatus( Status.PENDING );
        lr.save();

        String content = mvc.perform( get( "/api/v1/labrequests/labtech" ) ).andExpect( status().isOk() ).andReturn()
                .getResponse().getContentAsString();
        ArrayList<LabRequest> lrl = gson.fromJson( content, listType );
        assertEquals( "comments", lrl.get( 0 ).getComments() );
        assertEquals( "hcp", lrl.get( 0 ).getHcp().getUsername() );
        assertEquals( "test_labtech", lrl.get( 0 ).getLabTech().getUsername() );
        assertEquals( "patient", lrl.get( 0 ).getPatient().getUsername() );
        assertEquals( "111111-11", lrl.get( 0 ).getLabProcedure().getCode() );
        assertEquals( Priority.PRIORITY_HIGH, lrl.get( 0 ).getPriority() );

        lr = new LabRequest();
        lr.setComments( "comments1" );
        lr.setHcp( User.getByName( "hcp" ) );
        lr.setLabTech( User.getByName( "test_labtech" ) );
        lr.setPatient( User.getByName( "patient" ) );
        lr.setLabProcedure( LabProcedure.getByCode( "111111-11" ) );
        lr.setPriority( Priority.PRIORITY_LOW );
        lr.setStatus( Status.PENDING );
        lr.save();

        content = mvc.perform( get( "/api/v1/labrequests/labtech" ) ).andExpect( status().isOk() ).andReturn()
                .getResponse().getContentAsString();
        lrl = gson.fromJson( content, listType );
        assertEquals( Priority.PRIORITY_HIGH, lrl.get( 0 ).getPriority() );
        assertEquals( Priority.PRIORITY_LOW, lrl.get( 1 ).getPriority() );

        lr = new LabRequest();
        lr.setComments( "comments" );
        lr.setHcp( User.getByName( "hcp" ) );
        lr.setLabTech( User.getByName( "test_labtech" ) );
        lr.setPatient( User.getByName( "patient" ) );
        lr.setLabProcedure( LabProcedure.getByCode( "111111-11" ) );
        lr.setPriority( Priority.PRIORITY_VERY_HIGH );
        lr.setStatus( Status.PENDING );
        lr.save();

        content = mvc.perform( get( "/api/v1/labrequests/labtech" ) ).andExpect( status().isOk() ).andReturn()
                .getResponse().getContentAsString();
        lrl = gson.fromJson( content, listType );
        assertEquals( Priority.PRIORITY_VERY_HIGH, lrl.get( 0 ).getPriority() );
        assertEquals( Priority.PRIORITY_HIGH, lrl.get( 1 ).getPriority() );
        assertEquals( Priority.PRIORITY_LOW, lrl.get( 2 ).getPriority() );

        // TEST EDIT LAB REQUEST

        LabRequestForm form = new LabRequestForm( lr );
        form.setPriority( "PRIORITY_LOW" );
        content = mvc
                .perform( put( "/api/v1/labrequests" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( form ) ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();
        lr = gson.fromJson( content, LabRequest.class );
        assertEquals( "comments", lr.getComments() );
        assertEquals( "hcp", lr.getHcp().getUsername() );
        assertEquals( "test_labtech", lr.getLabTech().getUsername() );
        assertEquals( "patient", lr.getPatient().getUsername() );
        assertEquals( "111111-11", lr.getLabProcedure().getCode() );
        assertEquals( Priority.PRIORITY_LOW, lr.getPriority() );

        // TEST EDIT NON EXISTENT LAB

        form.setId( null );

        mvc.perform( put( "/api/v1/labrequests" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( form ) ) ).andExpect( status().isNotFound() );

        // TEST CHANGE LAB TECH

        content = mvc.perform( get( "/api/v1/labrequests/labtech" ) ).andExpect( status().isOk() ).andReturn()
                .getResponse().getContentAsString();
        lrl = gson.fromJson( content, listType );
        assertEquals( 3, lrl.size() );

        form = new LabRequestForm( lr );
        form.setLabTech( "test_labtech2" );

        final String content5 = mvc
                .perform( put( "/api/v1/labrequests" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( form ) ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();
        lr = gson.fromJson( content5, LabRequest.class );
        assertEquals( "test_labtech2", lr.getLabTech().getUsername() );

        content = mvc.perform( get( "/api/v1/labrequests/labtech" ) ).andExpect( status().isOk() ).andReturn()
                .getResponse().getContentAsString();
        lrl = gson.fromJson( content, listType );
        assertEquals( 2, lrl.size() );

        // TEST GET ALL

        content = mvc.perform( get( "/api/v1/labrequests/all" ) ).andExpect( status().isOk() ).andReturn().getResponse()
                .getContentAsString();

        lrl = gson.fromJson( content, listType );
        assertEquals( 3, lrl.size() );
        assertEquals( Priority.PRIORITY_HIGH, lrl.get( 0 ).getPriority() );
        assertEquals( Priority.PRIORITY_LOW, lrl.get( 1 ).getPriority() );
        assertEquals( Priority.PRIORITY_LOW, lrl.get( 2 ).getPriority() );

        // TEST GET FOR PAITENT

        content = mvc.perform( get( "/api/v1/labrequests/patient" ) ).andExpect( status().isOk() ).andReturn()
                .getResponse().getContentAsString();
        lrl = gson.fromJson( content, listType );
        assertEquals( 3, lrl.size() );
    }

    /**
     * Tests the getting api route for retrieving lab requests for the hcp
     *
     * @throws Exception
     * @throws UnsupportedEncodingException
     */
    @Test
    @WithMockUser ( username = "hcp", roles = { "HCP" } )
    public void testGetLabRequestsHCP () throws Exception {
        // Create a lab request
        LabRequest lr = new LabRequest();
        lr.setComments( "comments" );
        lr.setHcp( User.getByName( "hcp" ) );
        lr.setLabTech( User.getByName( "test_labtech" ) );
        lr.setPatient( User.getByName( "patient" ) );
        lr.setLabProcedure( LabProcedure.getByCode( "111111-11" ) );
        lr.setPriority( Priority.PRIORITY_HIGH );
        lr.setStatus( Status.PENDING );
        lr.save();

        // Retrieve list of lab requests
        String content = mvc.perform( get( "/api/v1/labrequests/hcp" ) ).andExpect( status().isOk() ).andReturn()
                .getResponse().getContentAsString();
        ArrayList<LabRequest> lrl = gson.fromJson( content, listType );

        assertEquals( 1, lrl.size() );
        assertEquals( "comments", lrl.get( 0 ).getComments() );
        assertEquals( "hcp", lrl.get( 0 ).getHcp().getUsername() );
        assertEquals( "test_labtech", lrl.get( 0 ).getLabTech().getUsername() );
        assertEquals( "patient", lrl.get( 0 ).getPatient().getUsername() );
        assertEquals( "111111-11", lrl.get( 0 ).getLabProcedure().getCode() );
        assertEquals( Priority.PRIORITY_HIGH, lrl.get( 0 ).getPriority() );

        // Create a another lab request
        lr = new LabRequest();
        lr.setComments( "comments" );
        lr.setHcp( User.getByName( "hcp" ) );
        lr.setLabTech( User.getByName( "test_labtech" ) );
        lr.setPatient( User.getByName( "patient" ) );
        lr.setLabProcedure( LabProcedure.getByCode( "111111-11" ) );
        lr.setPriority( Priority.PRIORITY_VERY_HIGH );
        lr.setStatus( Status.PENDING );
        lr.save();

        // Retrieve list of lab requests again
        content = mvc.perform( get( "/api/v1/labrequests/hcp" ) ).andExpect( status().isOk() ).andReturn().getResponse()
                .getContentAsString();
        lrl = gson.fromJson( content, listType );

        assertEquals( 2, lrl.size() );
        assertEquals( Priority.PRIORITY_VERY_HIGH, lrl.get( 0 ).getPriority() );
        assertEquals( Priority.PRIORITY_HIGH, lrl.get( 1 ).getPriority() );

        // TEST GET FOR PATIENT

        content = mvc.perform( get( "/api/v1/labrequests/patient" ) ).andExpect( status().isOk() ).andReturn()
                .getResponse().getContentAsString();
        lrl = gson.fromJson( content, listType );
        assertEquals( 2, lrl.size() );

    }

}
