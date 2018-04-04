package edu.ncsu.csc.itrust2.cucumber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.persistent.Patient;
import edu.ncsu.csc.itrust2.models.persistent.User;

/**
 * Step definitions for adding a user feature
 *
 * @author Cody Roberts (jdrobe10), Jacob Struckmeyer (jrstruck)
 */
public class PersonalRepresentativesStepDefs {

    private final WebDriver driver  = new HtmlUnitDriver( true );
    private final String    baseUrl = "http://localhost:8080/iTrust2";

    /**
     * Creates all of the users for testing
     */
    @Before
    public static void createUsers () {

        // Create Patient user
        User cUser1 = User.getByName( "cuPatient1" );
        if ( cUser1 == null ) {
            cUser1 = new User( "cuPatient1", "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.",
                    Role.ROLE_PATIENT, 1 );
            cUser1.save();
        }
        Patient cuPatient1 = Patient.getByName( "cuPatient1" );
        if ( cuPatient1 != null ) {
            cuPatient1.delete();
        }
        cuPatient1 = new Patient( cUser1 );
        cuPatient1.save();

        // Create Second Patient user
        User cUser2 = User.getByName( "cuPatient2" );
        if ( cUser2 == null ) {
            cUser2 = new User( "cuPatient2", "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.",
                    Role.ROLE_PATIENT, 1 );
            cUser2.save();
        }
        Patient cuPatient2 = Patient.getByName( "cuPatient2" );
        if ( cuPatient2 != null ) {
            cuPatient2.delete();
        }
        cuPatient2 = new Patient( cUser2 );
        cuPatient2.save();

        // Create third Patient user
        User cUser3 = User.getByName( "cuPatient3" );
        if ( cUser3 == null ) {
            cUser3 = new User( "cuPatient3", "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.",
                    Role.ROLE_PATIENT, 1 );
            cUser3.save();
        }
        Patient cuPatient3 = Patient.getByName( "cuPatient3" );
        if ( cuPatient3 != null ) {
            cuPatient3.delete();
        }
        cuPatient3 = new Patient( cUser3 );
        cuPatient3.save();

    }

    /**
     * Check for patients
     */
    @Given ( "three patients exist" )
    public void threePatients () {
        assertNotNull( Patient.getByName( "cuPatient1" ) );
        assertNotNull( Patient.getByName( "cuPatient2" ) );
        assertNotNull( Patient.getByName( "cuPatient3" ) );

    }

    /**
     * Check for representative
     *
     * @throws InterruptedException
     */
    @Given ( "I am a patient and have two representatives" )
    public void checkForRep () {
        // assertTrue(
        // Patient.getByName( "patient1" ).getMyRepresentatives().contains(
        // Patient.getByName( "patient2" ) ) );
        final Patient p1 = Patient.getByName( "cuPatient1" );
        final Patient p2 = Patient.getByName( "cuPatient2" );

        p1.addRepresentative( p2 );
        p1.save();

        assertEquals( 1, p1.getMyRepresentatives().size() );
    }

    /**
     * Check I am representative
     */
    @Given ( "I am assigned as a personal representative" )
    public void checkIAmRep () {
        final Patient p3 = Patient.getByName( "cuPatient3" );
        p3.addRepresentative( Patient.getByName( "cuPatient1" ) );
        p3.save();
        assertEquals( 1, Patient.getByName( "cuPatient1" ).getRepresentedByMe().size() );
    }

    /**
     * Login as a patient
     */
    @When ( "I login as a patient" )
    public void loginPatient () {
        driver.get( baseUrl );
        final WebElement username = driver.findElement( By.name( "username" ) );
        username.clear();
        username.sendKeys( "cuPatient1" );
        final WebElement password = driver.findElement( By.name( "password" ) );
        password.clear();
        password.sendKeys( "123456" );
        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();
    }

    /**
     * Navigate to add user page
     */
    @When ( "I navigate to the personal representatives page" )
    public void addRepsPage () {
        ( (JavascriptExecutor) driver )
                .executeScript( "document.getElementById('personalRepresentatives-patient').click();" );
    }

    /**
     * Add a personal representative
     */
    @When ( "I choose to add a personal representative" )
    public void addRep () {
        final WebElement id = driver.findElement( By.name( "declareUsername" ) );
        id.clear();
        id.sendKeys( "cuPatient2" );
        final WebElement submit = driver.findElement( ( By.className( "btn" ) ) );
        submit.click();
    }

    /**
     * Remove rep
     *
     * @throws InterruptedException
     */
    @When ( "I choose to remove a personal representative" )
    public void removeRep () throws InterruptedException {
        Thread.sleep( 3000 );
        assertTrue( tryButtonClick( By.name( "deleteUser" ), 4 ) );
        Thread.sleep( 3000 );

    }

    /**
     * Helper private method that attempts to click on an object
     *
     * @param by
     *            the element that is to be clicked
     * @param attempts
     *            the number of attempts to click the object
     * @return the success of the click attempts
     */
    private boolean tryButtonClick ( final By by, final int attempts ) {
        for ( int i = 0; i < attempts; i++ ) {
            try {
                driver.findElement( by ).click();
                return true;
            }
            catch ( final StaleElementReferenceException e ) {
                // try again
            }
        }

        return false;

    }

    /**
     * Remove me as rep
     *
     * @throws InterruptedException
     */
    @When ( "I select the patient I do not want to represent" )
    public void removeMeAsRep () throws InterruptedException {
        Thread.sleep( 3000 );
        assertTrue( tryButtonClick( By.name( "deleteSelfUser" ), 4 ) );
        Thread.sleep( 3000 );
    }

    /**
     * Add representative
     *
     * @throws IOException
     * @throws InterruptedException
     */
    @Then ( "the patient is added as my representative" )
    public void addedSuccessfully () throws InterruptedException {
        Thread.sleep( 1000 );
        final Patient p = Patient.getByName( "cuPatient1" );
        final Set<Patient> reps = p.getMyRepresentatives();
        assertEquals( 1, reps.size() );
    }

    /**
     * Representative is removed
     *
     * @throws InterruptedException
     */
    @Then ( "the representative is removed from my list of personal representatives" )
    public void removedSuccessfully () throws InterruptedException {
        Thread.sleep( 3000 );
        final Patient p = Patient.getByName( "cuPatient1" );
        final Set<Patient> reps = p.getMyRepresentatives();
        assertEquals( 0, reps.size() );
    }

    /**
     * I am removed as representative
     *
     * @throws InterruptedException
     */
    @Then ( "I am removed as their personal representative" )
    public void removedMeSuccessfully () throws InterruptedException {
        Thread.sleep( 3000 );
        final Patient p = Patient.getByName( "cuPatient1" );
        final Set<Patient> reps = p.getMyRepresentatives();
        assertEquals( 0, reps.size() );
    }

}
