package edu.ncsu.csc.itrust2.controllers.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.itrust2.models.persistent.Patient;
import edu.ncsu.csc.itrust2.models.persistent.User;
import edu.ncsu.csc.itrust2.utils.LoggerUtil;

/**
 * Controller responsible for providing various REST API endpoints for the
 * representatives.
 *
 * @author Kai Presler-Marshall
 *
 */
@RestController
@SuppressWarnings ( { "rawtypes", "unchecked" } )
public class APIRepresentativeController extends APIController {

    /**
     * Convenient method that gets the list of representatives of the logged in
     * user
     *
     * @return response
     */
    @GetMapping ( BASE_PATH + "/reps" )
    public ResponseEntity getRepresentatives () {
        final User self = User.getByName( LoggerUtil.currentUser() );
        final Patient patient = Patient.getPatient( self );
        if ( patient == null ) {
            return new ResponseEntity( errorResponse( "Could not find a patient entry for you, " + self.getUsername() ),
                    HttpStatus.NOT_FOUND );
        }

        return new ResponseEntity( wrapRepresentatives( patient ), HttpStatus.OK );
    }

    /**
     * Retrieves the total list of representatives of a passed in username
     *
     * @param username
     *            the patient's username
     * @return the complete list of the patient's representatives
     */
    @GetMapping ( BASE_PATH + "/reps/{username}" )
    public ResponseEntity getRepresentativesHCP ( @PathVariable final String username ) {
        final Patient patient = Patient.getByName( username );
        if ( patient == null ) {
            return new ResponseEntity( errorResponse( "Patient with username: " + username + " could not be found" ),
                    HttpStatus.NOT_FOUND );
        }
        return new ResponseEntity( wrapRepresentatives( patient ), HttpStatus.OK );
    }

    /**
     * Private helper method that wraps up a patient's representatives and list
     * of people they represent into a list that can be sent to the frontend
     *
     * @param patient
     *            the patient who's representatives are being wrapped
     * @return a list of all of the patient's representatives
     */
    private List<List<RepView>> wrapRepresentatives ( final Patient patient ) {
        final List<RepView> representer = new ArrayList<RepView>();
        for ( final Patient p : patient.getMyRepresentatives() ) {
            representer.add( new RepView( p ) );
        }
        final List<RepView> represented = new ArrayList<RepView>();
        for ( final Patient p : patient.getRepresentedByMe() ) {
            represented.add( new RepView( p ) );
        }

        final List<List<RepView>> allReps = new ArrayList<List<RepView>>();
        allReps.add( representer );
        allReps.add( represented );
        return allReps;
    }

    /**
     * Adds a representative to the current user's list of representatives
     *
     * @param username
     *            the username the patient that is to be added to the list of
     *            representatives
     * @return response
     */
    @PostMapping ( BASE_PATH + "/declareRep/{username}" )
    public ResponseEntity declareRepresentative ( @PathVariable final String username ) {
        final String currentUser = LoggerUtil.currentUser();
        final Patient patient = Patient.getByName( currentUser );
        final Patient rep = Patient.getByName( username );
        if ( patient == null ) {
            return new ResponseEntity( errorResponse( "Current patient could not be found" ), HttpStatus.NOT_FOUND );
        }
        if ( rep == null ) {
            return new ResponseEntity( errorResponse( "Patient with username: " + username + " could not be found" ),
                    HttpStatus.NOT_FOUND );
        }
        patient.addRepresentative( rep );
        patient.save();

        return new ResponseEntity( new RepView( rep ), HttpStatus.OK );
    }

    /**
     * Adds a representative to a patient's list of representatives as a HCP
     *
     * @param usernames
     *            the usernames of the representative that is being added to the
     *            list and the patient that the representative is being added to
     * @return response
     */
    @PreAuthorize ( "hasRole('ROLE_HCP')" )
    @PostMapping ( BASE_PATH + "/declareRepHCP/{usernames}" )
    public ResponseEntity declareRepresentativeAsHCP ( @PathVariable final String usernames ) {
        final String[] names = usernames.split( "-" );
        final Patient patient = Patient.getByName( names[0] );
        final Patient rep = Patient.getByName( names[1] );
        if ( patient == null ) {
            return new ResponseEntity( errorResponse( "Patient with username: " + names[0] + " could not be found" ),
                    HttpStatus.NOT_FOUND );
        }
        else if ( rep == null ) {
            return new ResponseEntity( errorResponse( "Patient with username: " + names[1] + " could not be found" ),
                    HttpStatus.NOT_FOUND );
        }
        patient.addRepresentative( rep );
        patient.save();

        return new ResponseEntity( new RepView( rep ), HttpStatus.OK );
    }

    /**
     * Removes a representative to the current user's list of representatives
     *
     * @param format
     *            the username of the representative and the mode whether the
     *            patient is removing the rep or removing self as rep
     * @return response
     */
    @PostMapping ( BASE_PATH + "/undeclareRep/{format}" )
    public ResponseEntity undeclareRepresentative ( @PathVariable final String format ) {
        final String[] fromFront = format.split( "-" );
        final String mode = fromFront[0];
        final String username = fromFront[1];

        Patient patient;
        Patient rep;

        // Current user is undeclaring rep
        if ( mode.equals( "0" ) ) {
            patient = Patient.getByName( LoggerUtil.currentUser() );
            rep = Patient.getByName( username );
        }
        // Current user is undeclaring themselves as rep to another patient
        else if ( mode.equals( "1" ) ) {
            patient = Patient.getByName( username );
            rep = Patient.getByName( LoggerUtil.currentUser() );
        }
        else {
            return new ResponseEntity( errorResponse( "Mode for undeclaring representative not correct" ),
                    HttpStatus.NOT_ACCEPTABLE );
        }

        // Error checking to see if input user exists
        if ( patient == null || rep == null ) {
            return new ResponseEntity( errorResponse( "Could not find Patient with username: " + username ),
                    HttpStatus.NOT_FOUND );
        }
        if ( !patient.removeRepresentative( rep ) ) {
            return new ResponseEntity( errorResponse( "Could not undeclare Patient with username: " + username ),
                    HttpStatus.EXPECTATION_FAILED );
        }
        patient.save();

        final RepView toFront = mode.equals( "0" ) ? new RepView( rep ) : new RepView( patient );

        return new ResponseEntity( toFront, HttpStatus.OK );
    }

    /**
     * Private Inner class that is used for sending a patient's information to
     * the front end without giving up all of the patients info
     *
     * @author Jacob Struckmeyer
     *
     */
    @SuppressWarnings ( "unused" )
    private class RepView {
        /** Username of the patient */
        private final String username;
        /** First name of the patient */
        private final String firstName;
        /** Last name of the patient */
        private final String lastName;

        /**
         * Constructor that makes a representative view item from a patient's
         * username, first name, and last name
         *
         * @param patient
         *            that patient that is to be converted into a view object
         */
        public RepView ( final Patient patient ) {
            username = patient.getSelf().getId();
            firstName = patient.getFirstName();
            lastName = patient.getLastName();
        }

    }
}
