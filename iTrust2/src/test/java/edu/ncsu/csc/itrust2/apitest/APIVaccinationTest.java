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
import edu.ncsu.csc.itrust2.forms.admin.VaccinationForm;
import edu.ncsu.csc.itrust2.models.persistent.Vaccination;
import edu.ncsu.csc.itrust2.mvc.config.WebMvcConfiguration;

/**
 * Class for testing vaccination API.
 *
 * @author Nick Board
 *
 */
@RunWith ( SpringJUnit4ClassRunner.class )
@ContextConfiguration ( classes = { RootConfiguration.class, WebMvcConfiguration.class } )
@WebAppConfiguration
public class APIVaccinationTest {
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
     * Tests basic vaccination API functionality.
     *
     * @throws UnsupportedEncodingException
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "admin", roles = { "USER", "ADMIN" } )
    public void testVaccinationAPI () throws UnsupportedEncodingException, Exception {

        // Destroy conflicting test vaccinations
        // final List<Vaccination> allVacs = Vaccination.getAll();
        // for ( int i = 0; i < allVacs.size(); i++ ) {
        // final Vaccination conflicting = allVacs.get( i );
        //
        // if ( conflicting != null && conflicting.getName().equals( "TEST" ) )
        // {
        // mvc.perform( delete( "/api/v1/vaccinations/" + conflicting.getId() )
        // ).andExpect( status().isOk() )
        // .andExpect( content().string( conflicting.getId().toString() ) );
        // }
        // }

        // Create vaccinations for testing
        final VaccinationForm form1 = new VaccinationForm();
        form1.setCode( "00000" );
        form1.setName( "TEST" );
        form1.setDescription( "DESC1" );

        final VaccinationForm form2 = new VaccinationForm();
        form2.setCode( "00001" );
        form2.setName( "TEST" );
        form2.setDescription( "Desc2" );

        // Add vaccination1 to system
        final String content1 = mvc
                .perform( post( "/api/v1/vaccinations" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( form1 ) ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();

        // Parse response as Vaccination object
        final Gson gson = new GsonBuilder().create();
        final Vaccination vaccination1 = gson.fromJson( content1, Vaccination.class );
        assertEquals( form1.getCode(), vaccination1.getCode() );
        assertEquals( form1.getName(), vaccination1.getName() );
        assertEquals( form1.getDescription(), vaccination1.getDescription() );

        // Attempt to add same vaccination twice
        mvc.perform( post( "/api/v1/vaccinations" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( form1 ) ) ).andExpect( status().isConflict() );

        // Add vaccination2 to system
        final String content2 = mvc
                .perform( post( "/api/v1/vaccinations" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( form2 ) ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();
        final Vaccination vaccination2 = gson.fromJson( content2, Vaccination.class );
        assertEquals( form2.getCode(), vaccination2.getCode() );
        assertEquals( form2.getName(), vaccination2.getName() );
        assertEquals( form2.getDescription(), vaccination2.getDescription() );

        // Verify vaccinations have been added
        mvc.perform( get( "/api/v1/vaccinations" ) ).andExpect( status().isOk() )
                .andExpect( content().string( Matchers.containsString( form1.getCode() ) ) )
                .andExpect( content().string( Matchers.containsString( form2.getCode() ) ) );

        // Edit first vaccination's description
        vaccination1.setDescription( "This is a better description" );
        final String editContent = mvc
                .perform( put( "/api/v1/vaccinations" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( vaccination1 ) ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();
        final Vaccination editedVaccination = gson.fromJson( editContent, Vaccination.class );
        assertEquals( vaccination1.getId(), editedVaccination.getId() );
        assertEquals( vaccination1.getCode(), editedVaccination.getCode() );
        assertEquals( vaccination1.getName(), editedVaccination.getName() );
        assertEquals( "This is a better description", editedVaccination.getDescription() );

        // Attempt invalid edit
        vaccination2.setCode( "00000" );
        mvc.perform( put( "/api/v1/vaccinations" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( vaccination2 ) ) ).andExpect( status().isConflict() );

        // Follow up with valid edit
        vaccination2.setCode( "00003" );
        mvc.perform( put( "/api/v1/vaccinations" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( vaccination2 ) ) ).andExpect( status().isOk() );

        // Delete test vaccinations
        mvc.perform( delete( "/api/v1/vaccinations/" + vaccination1.getId() ) ).andExpect( status().isOk() )
                .andExpect( content().string( vaccination1.getId().toString() ) );
        mvc.perform( delete( "/api/v1/vaccinations/" + vaccination2.getId() ) ).andExpect( status().isOk() )
                .andExpect( content().string( vaccination2.getId().toString() ) );
    }

}
