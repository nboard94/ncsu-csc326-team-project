package edu.ncsu.csc.itrust2.apitest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

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
import com.google.gson.reflect.TypeToken;

import edu.ncsu.csc.itrust2.config.RootConfiguration;
import edu.ncsu.csc.itrust2.forms.admin.UserForm;
import edu.ncsu.csc.itrust2.forms.admin.VaccinationForm;
import edu.ncsu.csc.itrust2.forms.hcp.VacRecordForm;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.persistent.VacRecord;
import edu.ncsu.csc.itrust2.models.persistent.Vaccination;
import edu.ncsu.csc.itrust2.mvc.config.WebMvcConfiguration;

/**
 * Class for testing vacRecord API.
 *
 * @author Nick Board
 */
@RunWith ( SpringJUnit4ClassRunner.class )
@ContextConfiguration ( classes = { RootConfiguration.class, WebMvcConfiguration.class } )
@WebAppConfiguration
public class APIVacRecordTest {
    private MockMvc               mvc;

    private Gson                  gson;
    VaccinationForm               vaccinationForm1;
    VaccinationForm               vaccinationForm2;

    @Autowired
    private WebApplicationContext context;

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
        final UserForm patientForm = new UserForm( "api_test_patient", "123456", Role.ROLE_PATIENT, 1 );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( patientForm ) ) );

        // Create vaccinations for testing
        vaccinationForm1 = new VaccinationForm();
        vaccinationForm1.setCode( "91191" );
        vaccinationForm1.setName( "TEST" );
        vaccinationForm1.setDescription( "DESC" );
        mvc.perform( post( "/api/v1/vaccinations" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( vaccinationForm1 ) ) );

        vaccinationForm2 = new VaccinationForm();
        vaccinationForm2.setCode( "00000" );
        vaccinationForm2.setName( "TEST" );
        vaccinationForm2.setDescription( "DESC" );
        mvc.perform( post( "/api/v1/vaccinations" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( vaccinationForm2 ) ) );
    }

    /**
     * Tests basic vacRecord APIs.
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "hcp", roles = { "USER", "HCP", "ADMIN" } )
    public void testVacRecordAPI () throws Exception {

        // Clear existing conflicting vacRecords
        final List<VacRecord> allRecs = VacRecord.getAll();
        for ( int i = 0; i < allRecs.size(); i++ ) {
            final VacRecord conflicting = allRecs.get( i );
            if ( conflicting.getId() != null ) {
                mvc.perform( delete( "/api/v1/vacrecords/" + conflicting.getId() ) ).andExpect( status().isOk() )
                        .andExpect( content().string( conflicting.getId().toString() ) );
            }
        }

        // Create two vacRecord forms for testing
        final VacRecordForm form1 = new VacRecordForm();
        form1.setId( null );
        form1.setVaccination( vaccinationForm1.getCode() );
        form1.setPatient( "api_test_patient" );
        form1.setAdministrationDate( "02/19/2018" );

        final VacRecordForm form2 = new VacRecordForm();
        form2.setVaccination( vaccinationForm1.getCode() );
        form2.setPatient( "api_test_patient" );
        form2.setAdministrationDate( "02/19/2018" );

        // Add first vacRecord to system
        final String content1 = mvc
                .perform( post( "/api/v1/vacrecords" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( form1 ) ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();

        // Parse response as VacRecord
        final VacRecord v1 = gson.fromJson( content1, VacRecord.class );
        final VacRecordForm v1Form = new VacRecordForm( v1 );
        assertEquals( form1.getVaccination(), v1Form.getVaccination() );
        assertEquals( form1.getPatient(), v1Form.getPatient() );
        assertEquals( form1.getAdministrationDate(), v1Form.getAdministrationDate() );

        // Add second vacrecord to system
        final String content2 = mvc

                .perform( post( "/api/v1/vacrecords" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( form2 ) ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();
        final VacRecord v2 = gson.fromJson( content2, VacRecord.class );
        final VacRecordForm v2Form = new VacRecordForm( v2 );
        assertEquals( form2.getVaccination(), v2Form.getVaccination() );
        assertEquals( form2.getPatient(), v2Form.getPatient() );
        assertEquals( form2.getAdministrationDate(), v2Form.getAdministrationDate() );

        // Verify vacrecord have been added
        final String allVacRecordContent = mvc.perform( get( "/api/v1/vacrecords" ) ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();
        final List<VacRecord> allVacRecords = gson.fromJson( allVacRecordContent, new TypeToken<List<VacRecord>>() {
        }.getType() );
        assertTrue( allVacRecords.size() >= 2 );

        // Edit first vaccination
        v1.setVaccination( new Vaccination( vaccinationForm2 ) );
        final String editContent = mvc
                .perform( put( "/api/v1/vacrecords" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( new VacRecordForm( v1 ) ) ) )
                .andReturn().getResponse().getContentAsString();
        final VacRecord edited = gson.fromJson( editContent, VacRecord.class );
        assertEquals( v1.getId(), edited.getId() );
        assertEquals( v1.getVaccination().getCode(), edited.getVaccination().getCode() );

        // Get single vac record
        final String getContent = mvc
                .perform( put( "/api/v1/vacrecords" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( new VacRecordForm( v1 ) ) ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();
        final VacRecord fetched = gson.fromJson( getContent, VacRecord.class );
        assertEquals( v1.getId(), fetched.getId() );

        // Attempt invalid edit
        // v2.setVaccination( null );
        // mvc.perform( put( "api/v1/vacrecords" ).contentType(
        // MediaType.APPLICATION_JSON )
        // .content( TestUtils.asJsonString( new VacRecordForm( v2 ) ) )
        // ).andExpect( status().isBadRequest() );

        // Delete test objects
        mvc.perform( delete( "/api/v1/vacrecords/" + v1.getId() ) ).andExpect( status().isOk() )
                .andExpect( content().string( v1.getId().toString() ) );
        mvc.perform( delete( "/api/v1/vacrecords/" + v2.getId() ) ).andExpect( status().isOk() )
                .andExpect( content().string( v2.getId().toString() ) );
    }

}
