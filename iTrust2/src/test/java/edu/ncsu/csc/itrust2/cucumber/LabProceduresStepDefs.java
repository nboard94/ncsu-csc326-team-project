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
import edu.ncsu.csc.itrust2.models.persistent.LabProcedure;

/**
 * Step definitions for accessing lab procedures
 *
 * @author Cody Roberts (jdrobe10)
 */

public class LabProceduresStepDefs {

    private final WebDriver driver  = new HtmlUnitDriver( true );
    private final String    baseUrl = "http://localhost:8080/iTrust2";
    private final String    code    = "111111-12";
    private LabProcedure    l       = null;

    /**
     * Check that the lab procedure doesn't exist
     */
    @Given ( "that the lab procedure doesn't exist" )
    public void checkForProcedure () {
        l = LabProcedure.getByCode( code );
        if ( l != null ) {
            l.delete();
        }
    }

    /**
     * Login as an admin
     */
    @When ( "I login as an admin" )
    public void adminLogin () {
        driver.get( baseUrl );
        final WebElement username = driver.findElement( By.name( "username" ) );
        username.clear();
        username.sendKeys( "admin" );
        final WebElement password = driver.findElement( By.name( "password" ) );
        password.clear();
        password.sendKeys( "123456" );
        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();
        assertTrue( driver.getPageSource().contains( "Welcome to iTrust2" ) );
    }

    /**
     * Navigate to the lab procedures page
     */
    @When ( "I navigate to the lab procedures page" )
    public void proceduresPage () {
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('manageProcedures').click();" );
        assertTrue( driver.getPageSource().contains( "Admin Manage Lab Procedures" ) );
    }

    /**
     * Add the lab procedure
     */
    @When ( "I select to add a lab procedure with new information" )
    public void addProcedure () {
        final WebElement code = driver.findElement( By.name( "code" ) );
        code.clear();
        code.sendKeys( "111111-12" );
        final WebElement commonName = driver.findElement( By.name( "commonName" ) );
        commonName.clear();
        commonName.sendKeys( "Procedure 1" );
        final WebElement component = driver.findElement( By.name( "component" ) );
        component.clear();
        component.sendKeys( "comp" );
        final WebElement property = driver.findElement( By.name( "property" ) );
        property.clear();
        property.sendKeys( "prop" );
        final WebElement submit = driver.findElement( By.name( "submit" ) );
        submit.click();
        try {
            Thread.sleep( 500 );
        }
        catch ( final InterruptedException e ) {
            e.printStackTrace();
        }
    }

    /**
     * Verify the lab procedure was added
     */
    @Then ( "the lab procedure is created" )
    public void verifyProcedure () {
        assertEquals( "Procedure 1", LabProcedure.getByCode( code ).getCommonName() );
    }
}
