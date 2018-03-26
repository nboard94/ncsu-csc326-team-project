package edu.ncsu.csc.itrust2.apitest;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;

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
        // Create vaccinations for testing
        final VaccinationForm form1 = new VaccinationForm();
        form1.setCptCode( 00001 );
        form1.setCptDescription( "fake vaccine 1" );
        form1.setCvxCode( 001 );
        form1.setVaccineName( "vaccine1" );

        final VaccinationForm form2 = new VaccinationForm();
        form2.setCptCode( 00002 );
        form2.setCptDescription( "fake vaccine 2" );
        form2.setCvxCode( 002 );
        form2.setVaccineName( "vaccine2" );
        form2.setComments( "commented on this one" );

        // Add vaccine1 to system
        final String content1 = mvc
                .perform( post( "/api/v1/vaccinations" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( form1 ) ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();

        // Parse response as Vaccination object
        final Gson gson = new GsonBuilder().create();
        final Vaccination vac1 = gson.fromJson( content1, Vaccination.class );
        assertEquals( form1.getCptCode(), vac1.getCptCode() );
        assertEquals( form1.getCptDescription(), vac1.getCptDescription() );
        assertEquals( form1.getCvxCode(), vac1.getCvxCode() );
        assertEquals( form1.getVaccineName(), vac1.getVaccineName() );

        // Attempt to add same vaccination twice
        mvc.perform( post( "/api/v1/vaccinations" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( form1 ) ) ).andExpect( status().isConflict() );
    }

}
