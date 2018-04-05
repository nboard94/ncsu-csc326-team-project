package edu.ncsu.csc.itrust2.cucumber;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Random;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.Select;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import edu.ncsu.csc.itrust2.models.persistent.User;

/**
 * Step definitions for adding a user feature
 */
public class AddUserStepDefs {

    private final WebDriver driver       = new HtmlUnitDriver( true );
    private final String    baseUrl      = "http://localhost:8080/iTrust2";
    private final String    jenkinsUname = "jenkins" + ( new Random() ).nextInt();
    private final String    labTech      = "labtech" + ( new Random() ).nextInt();
    private final String    er           = "er" + ( new Random() ).nextInt();

    /**
     * Check for no user
     */
    @Given ( "The user does not already exist in the database" )
    public void noUser () {
        final List<User> users = User.getUsers();
        for ( final User user : users ) {
            if ( user.getUsername().equals( jenkinsUname ) || user.getUsername().equals( labTech )
                    || user.getUsername().equals( er ) ) {
                try {
                    user.delete();
                }
                catch ( final Exception e ) {
                    Assert.fail();
                }
            }
        }
    }

    /**
     * Admin log in
     */
    @When ( "I log in as admin" )
    public void loginAdmin () {
        driver.get( baseUrl );
        final WebElement username = driver.findElement( By.name( "username" ) );
        username.clear();
        username.sendKeys( "admin" );
        final WebElement password = driver.findElement( By.name( "password" ) );
        password.clear();
        password.sendKeys( "123456" );
        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();
    }

    /**
     * Navigate to add user page
     */
    @When ( "I navigate to the Add User page" )
    public void addUserPage () {
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('addnewuser').click();" );
    }

    /**
     * Fill in add user values
     */
    @When ( "I fill in the values in the Add User form" )
    public void fillFields () {
        final WebElement username = driver.findElement( By.id( "username" ) );
        username.clear();
        username.sendKeys( jenkinsUname );

        final WebElement password = driver.findElement( By.id( "password" ) );
        password.clear();
        password.sendKeys( "123456" );

        final WebElement password2 = driver.findElement( By.id( "password2" ) );
        password2.clear();
        password2.sendKeys( "123456" );

        final Select role = new Select( driver.findElement( By.id( "role" ) ) );
        role.selectByVisibleText( "ROLE_HCP" );

        final WebElement enabled = driver.findElement( By.className( "checkbox" ) );
        enabled.click();

        driver.findElement( By.className( "btn" ) ).click();

    }

    /**
     * Fill in add user values
     */
    @When ( "I fill in the values for a lab tech" )
    public void fillFieldsLabTech () {
        final WebElement username = driver.findElement( By.id( "username" ) );
        username.clear();
        username.sendKeys( labTech );

        final WebElement password = driver.findElement( By.id( "password" ) );
        password.clear();
        password.sendKeys( "123456" );

        final WebElement password2 = driver.findElement( By.id( "password2" ) );
        password2.clear();
        password2.sendKeys( "123456" );

        final Select role = new Select( driver.findElement( By.id( "role" ) ) );
        role.selectByVisibleText( "ROLE_LABTECH" );

        final WebElement enabled = driver.findElement( By.className( "checkbox" ) );
        enabled.click();

        driver.findElement( By.className( "btn" ) ).click();

    }

    /**
     * Fill in add user values
     */
    @When ( "I fill in the values for an ER" )
    public void fillFieldsER () {
        final WebElement username = driver.findElement( By.id( "username" ) );
        username.clear();
        username.sendKeys( er );

        final WebElement password = driver.findElement( By.id( "password" ) );
        password.clear();
        password.sendKeys( "123456" );

        final WebElement password2 = driver.findElement( By.id( "password2" ) );
        password2.clear();
        password2.sendKeys( "123456" );

        final Select role = new Select( driver.findElement( By.id( "role" ) ) );
        role.selectByVisibleText( "ROLE_ER" );

        final WebElement enabled = driver.findElement( By.className( "checkbox" ) );
        enabled.click();

        driver.findElement( By.className( "btn" ) ).click();

    }

    /**
     * Create user
     */
    @Then ( "The user is created successfully" )
    public void createdSuccessfully () {
        assertTrue( driver.getPageSource().contains( "User added successfully" ) );
    }

    /**
     * User login
     */
    @Then ( "The new user can login" )
    public void tryLogin () {
        driver.findElement( By.id( "logout" ) ).click();

        final WebElement username = driver.findElement( By.name( "username" ) );
        username.clear();
        username.sendKeys( jenkinsUname );
        final WebElement password = driver.findElement( By.name( "password" ) );
        password.clear();
        password.sendKeys( "123456" );
        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();

        /**
         * Not an assert statement in the typical sense, but we know that we can
         * log in if we can find the "iTrust" button in the top-left after
         * attempting to do so.
         */
        try {
            driver.findElement( By.linkText( "iTrust2" ) );
        }
        catch ( final Exception e ) {
            Assert.assertNull( e );
        }
    }

    /**
     * User login
     */
    @Then ( "The new lab tech can login" )
    public void tryLoginLabTech () {
        driver.findElement( By.id( "logout" ) ).click();

        final WebElement username = driver.findElement( By.name( "username" ) );
        username.clear();
        username.sendKeys( labTech );
        final WebElement password = driver.findElement( By.name( "password" ) );
        password.clear();
        password.sendKeys( "123456" );
        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();

        /**
         * Not an assert statement in the typical sense, but we know that we can
         * log in if we can find the "iTrust" button in the top-left after
         * attempting to do so.
         */
        try {
            driver.findElement( By.linkText( "iTrust2" ) );
        }
        catch ( final Exception e ) {
            Assert.assertNull( e );
        }
    }

    /**
     * User login
     */
    @Then ( "The new ER can login" )
    public void tryLoginER () {
        driver.findElement( By.id( "logout" ) ).click();

        final WebElement username = driver.findElement( By.name( "username" ) );
        username.clear();
        username.sendKeys( er );
        final WebElement password = driver.findElement( By.name( "password" ) );
        password.clear();
        password.sendKeys( "123456" );
        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();

        /**
         * Not an assert statement in the typical sense, but we know that we can
         * log in if we can find the "iTrust" button in the top-left after
         * attempting to do so.
         */
        try {
            driver.findElement( By.linkText( "iTrust2" ) );
        }
        catch ( final Exception e ) {
            Assert.assertNull( e );
        }
    }
}
