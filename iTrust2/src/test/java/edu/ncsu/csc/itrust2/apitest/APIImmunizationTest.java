package edu.ncsu.csc.itrust2.apitest;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;

import org.hamcrest.Matchers;
import org.junit.Before;
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

import edu.ncsu.csc.itrust2.config.RootConfiguration;
import edu.ncsu.csc.itrust2.forms.admin.ImmunizationForm;
import edu.ncsu.csc.itrust2.models.persistent.Immunization;
import edu.ncsu.csc.itrust2.mvc.config.WebMvcConfiguration;

/**
 * Class for testing immunization API.
 *
 * @author Nick Board
 *
 */
@RunWith ( SpringJUnit4ClassRunner.class )
@ContextConfiguration ( classes = { RootConfiguration.class, WebMvcConfiguration.class } )
@WebAppConfiguration
public class APIImmunizationTest {
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
     * Tests basic immunization API functionality.
     *
     * @throws UnsupportedEncodingException
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "admin", roles = { "USER", "ADMIN" } )
    public void testImmunizationAPI () throws UnsupportedEncodingException, Exception {
        // Create immunizations for testing
        final ImmunizationForm form1 = new ImmunizationForm();
        form1.setCode( "0000-0000-00" );
        form1.setName( "TEST1" );
        form1.setDescription( "DESC1" );

        final ImmunizationForm form2 = new ImmunizationForm();
        form2.setCode( "0000-0000-01" );
        form2.setName( "TEST2" );
        form2.setDescription( "Desc2" );

        // Add immunization1 to system
        final String content1 = mvc
                .perform( post( "/api/v1/immunizations" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( form1 ) ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();

        // Parse response as Immunization object
        final Gson gson = new GsonBuilder().create();
        final Immunization immunization1 = gson.fromJson( content1, Immunization.class );
        assertEquals( form1.getCode(), immunization1.getCode() );
        assertEquals( form1.getName(), immunization1.getName() );
        assertEquals( form1.getDescription(), immunization1.getDescription() );

        // Attempt to add same immunization twice
        mvc.perform( post( "/api/v1/immunizations" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( form1 ) ) ).andExpect( status().isConflict() );

        // Add immunization2 to system
        final String content2 = mvc
                .perform( post( "/api/v1/immunizations" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( form2 ) ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();
        final Immunization immunization2 = gson.fromJson( content2, Immunization.class );
        assertEquals( form2.getCode(), immunization2.getCode() );
        assertEquals( form2.getName(), immunization2.getName() );
        assertEquals( form2.getDescription(), immunization2.getDescription() );

        // Verify immunizations have been added
        mvc.perform( get( "/api/v1/immunizations" ) ).andExpect( status().isOk() )
                .andExpect( content().string( Matchers.containsString( form1.getCode() ) ) )
                .andExpect( content().string( Matchers.containsString( form2.getCode() ) ) );

        // Edit first immunization's description
        immunization1.setDescription( "This is a better description" );
        final String editContent = mvc
                .perform( put( "/api/v1/immunizations" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( immunization1 ) ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();
        final Immunization editedImmunization = gson.fromJson( editContent, Immunization.class );
        assertEquals( immunization1.getId(), editedImmunization.getId() );
        assertEquals( immunization1.getCode(), editedImmunization.getCode() );
        assertEquals( immunization1.getName(), editedImmunization.getName() );
        assertEquals( "This is a better description", editedImmunization.getDescription() );

        // Attempt invalid edit
        immunization2.setCode( "0000-0000-00" );
        mvc.perform( put( "/api/v1/immunizations" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( immunization2 ) ) ).andExpect( status().isConflict() );

        // Follow up with valid edit
        immunization2.setCode( "0000-0000-03" );
        mvc.perform( put( "/api/v1/immunizations" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( immunization2 ) ) ).andExpect( status().isOk() );

        // Delete test immunizations
        mvc.perform( delete( "/api/v1/immunizations/" + immunization1.getId() ) ).andExpect( status().isOk() )
                .andExpect( content().string( immunization1.getId().toString() ) );
        mvc.perform( delete( "/api/v1/immunizations/" + immunization2.getId() ) ).andExpect( status().isOk() )
                .andExpect( content().string( immunization2.getId().toString() ) );
    }

}
