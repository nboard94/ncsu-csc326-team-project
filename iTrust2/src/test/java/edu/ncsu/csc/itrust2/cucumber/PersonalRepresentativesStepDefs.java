package edu.ncsu.csc.itrust2.cucumber;

import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import edu.ncsu.csc.itrust2.models.persistent.Patient;
import edu.ncsu.csc.itrust2.models.persistent.User;

/**
 * Step definitions for adding a user feature
 */
public class PersonalRepresentativesStepDefs {

    private final WebDriver driver  = new HtmlUnitDriver( true );
    private final String    baseUrl = "http://localhost:8080/iTrust2";

    public void makeReps () {
        final Set<Patient> rep = new HashSet<Patient>();
        rep.add( Patient.getByName( "patient3" ) );
        Patient.getByName( "patient1" ).setRepresentedByMe( rep );
    }

    @Before
    public void setup () {
        try {
            final User u1 = new User(), u2 = new User(), u3 = new User();
            u1.setPassword( "password" );
            u2.setPassword( "password" );
            u3.setPassword( "password" );
            u1.setUsername( "patient1" );
            u2.setUsername( "patient2" );
            u3.setUsername( "patient3" );
            final Patient p1 = new Patient( u1 );
            final Patient p2 = new Patient( u2 );
            final Patient p3 = new Patient( u3 );
            p1.save();
            p2.save();
            p3.save();
        }
        catch ( final Exception e ) {
            // Do nothing, users already exist
        }
    }

    /**
     * Check for patients
     */
    @Given ( "three patients exist" )
    public void threePatients () {
        final List<Patient> pats = Patient.getPatients();
        assertTrue( pats.size() >= 3 );
    }

    /**
     * Check for representative
     */
    @Given ( "I am a patient and have a personal representative" )
    public void checkForRep () {
        assertTrue(
                Patient.getByName( "patient1" ).getMyRepresentatives().contains( Patient.getByName( "patient2" ) ) );
    }

    /**
     * Check I am representative
     */
    @Given ( "I am assigned as a personal representative" )
    public void checkIAmRep () {
        makeReps();
        assertTrue(
                Patient.getByName( "patient1" ).getMyRepresentatives().contains( Patient.getByName( "patient3" ) ) );
    }

    /**
     * Login as a patient
     */
    @When ( "I login as a patient" )
    public void loginPatient () {
        driver.get( baseUrl );
        final WebElement username = driver.findElement( By.name( "username" ) );
        username.clear();
        username.sendKeys( "patient1" );
        final WebElement password = driver.findElement( By.name( "password" ) );
        password.clear();
        password.sendKeys( "password" );
        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();
    }

    /**
     * Navigate to add user page
     */
    @When ( "I navigate to the personal representatives page" )
    public void addRepsPage () {
        // ( (JavascriptExecutor) driver ).executeScript(
        // "document.getElementById('personalRepresentatives').click();" );
    }

    /**
     * Add a personal representative
     */
    @When ( "I choose to add a personal representative" )
    public void addRep () {
        final WebElement id = driver.findElement( By.name( "declareUsername" ) );
        id.clear();
        id.sendKeys( "patient2" );
        final WebElement submit = driver.findElement( ( By.className( "btn" ) ) );
        submit.click();
    }

    /**
     * Remove rep
     */
    @When ( "I choose to remove a personal representative" )
    public void removeRep () {
        final WebElement delete = driver.findElement( By.name( "deleteUser" ) );
        delete.click();
    }

    /**
     * Remove me as rep
     */
    @When ( "I select the patient I do not want to represent" )
    public void removeMeAsRep () {
        final WebElement delete = driver.findElement( By.name( "deleteSelfUser" ) );
        delete.click();
    }

    /**
     * Add representative
     */
    @Then ( "the patient is added as my representative" )
    public void addedSuccessfully () {
        assertTrue( "patient2" == driver.findElement( By.name( "patientMIDCell" ) ).getText() );
    }

    /**
     * Representative is removed
     */
    @Then ( "the representative is removed from my list of personal representatives" )
    public void removedSuccessfully () {
        assertTrue(
                !Patient.getByName( "patient1" ).getMyRepresentatives().contains( Patient.getByName( "patient2" ) ) );
    }

    /**
     * I am removed as representative
     */
    @Then ( "I am removed as their personal representative" )
    public void removedMeSuccessfully () {
        assertTrue( !Patient.getByName( "patient1" ).getRepresentedByMe().contains( Patient.getByName( "patient3" ) ) );
    }

}
