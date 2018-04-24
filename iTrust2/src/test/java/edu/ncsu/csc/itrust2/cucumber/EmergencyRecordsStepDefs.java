package edu.ncsu.csc.itrust2.cucumber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.persistent.Patient;
import edu.ncsu.csc.itrust2.models.persistent.User;

/**
 * Step definitions for accessing emergency records
 *
 * @author Cody Roberts (jdrobe10)
 */
public class EmergencyRecordsStepDefs {

    private final WebDriver driver  = new HtmlUnitDriver( true );
    private final String    baseUrl = "http://localhost:8080/iTrust2";

    /**
     * Check that a patient and hcp exist
     */
    @Given ( "the patient in question exists" )
    public void checkForPatient () {
        // check for patient
        User u = User.getByNameAndRole( "patient1", Role.ROLE_PATIENT );
        if ( u == null ) {
            u = new User( "patient1", "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.", Role.ROLE_PATIENT,
                    1 );
            u.save();
        }
        Patient patient1 = Patient.getByName( "patient1" );
        if ( patient1 != null ) {
            patient1.delete();
        }
        patient1 = new Patient( u );
        patient1.save();

        // check for hcp
        User hcp = User.getByNameAndRole( "hcp1", Role.ROLE_HCP );
        if ( hcp == null ) {
            hcp = new User( "hcp1", "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.", Role.ROLE_HCP, 1 );
            hcp.save();
        }

        assertEquals( patient1, Patient.getByName( "patient1" ) );
        assertEquals( hcp, User.getByName( "hcp1" ) );
    }

    /**
     * Login as the hcp
     */
    @When ( "I login as an HCP" )
    public void hcpLogin () {
        driver.get( baseUrl );
        final WebElement username = driver.findElement( By.name( "username" ) );
        username.clear();
        username.sendKeys( "hcp1" );
        final WebElement password = driver.findElement( By.name( "password" ) );
        password.clear();
        password.sendKeys( "123456" );
        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();
        assertTrue( driver.getPageSource().contains( "Welcome to iTrust2" ) );
    }

    /**
     * Navigate to Emergency Records Page
     */
    @When ( "I navigate to the Emergency Health Records page" )
    public void recordsPage () {
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('emergencyHealthRecords').click();" );
        assertTrue( driver.getPageSource().contains( "Lookup Patient" ) );
    }

    /**
     * Look for the patient
     *
     * @throws InterruptedException
     */
    @When ( "I enter the patients MID" )
    public void enterPatientMID () throws InterruptedException {
        final WebElement patientMID = driver.findElement( By.id( "lookupMID" ) );
        patientMID.clear();
        patientMID.sendKeys( "patient1" );
        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();
        patientMID.clear();
    }

    /**
     * An emergency health record is displayed
     */
    @Then ( "I am given an emergency health record for the patient" )
    public void checkForRecord () throws InterruptedException {
        // assertEquals( "patient1", driver.findElement( By.name(
        // "patientNameCell" ) ).getText() );
        // final String tableText = driver.findElement( By.tagName( "tr" )
        // ).getText();
        final String source = driver.getPageSource();
        assertTrue( source.contains( "Successfully retrieved patient records" ) );
    }
}
