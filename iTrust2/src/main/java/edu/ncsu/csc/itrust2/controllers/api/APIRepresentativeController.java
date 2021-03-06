package edu.ncsu.csc.itrust2.controllers.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.itrust2.models.enums.TransactionType;
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
    @PreAuthorize ( "hasRole('ROLE_PATIENT')" )
    @GetMapping ( BASE_PATH + "/reps" )
    public ResponseEntity getRepresentatives () {
        final User self = User.getByName( LoggerUtil.currentUser() );
        final Patient patient = Patient.getPatient( self );
        LoggerUtil.log( TransactionType.VIEW_REPS, self );
        return new ResponseEntity( wrapRepresentatives( patient ), HttpStatus.OK );
    }

    /**
     * Retrieves the total list of representatives of a passed in username
     *
     * @param username
     *            the patient's username
     * @return the complete list of the patient's representatives
     */
    @PreAuthorize ( "hasRole('ROLE_HCP')" )
    @GetMapping ( BASE_PATH + "/reps/{username}" )
    public ResponseEntity getRepresentativesHCP ( @PathVariable final String username ) {
        final Patient patient = Patient.getByName( username );
        final User self = User.getByName( LoggerUtil.currentUser() );
        if ( patient == null ) {
            return new ResponseEntity( errorResponse( "Patient with username: " + username + " could not be found" ),
                    HttpStatus.NOT_FOUND );
        }
        LoggerUtil.log( TransactionType.VIEW_REPS, self, patient.getSelf() );
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
    @PreAuthorize ( "hasRole('ROLE_PATIENT')" )
    @PostMapping ( BASE_PATH + "/declareRep/{username}" )
    public ResponseEntity declareRepresentative ( @PathVariable final String username ) {
        final Patient patient = Patient.getByName( LoggerUtil.currentUser() );
        final Patient rep = Patient.getByName( username );
        if ( rep == null ) {
            return new ResponseEntity( errorResponse( "Patient with username: " + username + " could not be found" ),
                    HttpStatus.NOT_FOUND );
        }
        if ( patient.equals( rep ) ) {
            return new ResponseEntity( errorResponse( "Cannot declare self as representative" ),
                    HttpStatus.NOT_ACCEPTABLE );
        }
        final Set<Patient> patientReps = patient.getMyRepresentatives();
        if ( patientReps.contains( rep ) ) {
            return new ResponseEntity( errorResponse( "You are already represented by " + rep.getSelf().getUsername() ),
                    HttpStatus.NOT_ACCEPTABLE );
        }
        patient.addRepresentative( rep );
        patient.save();

        LoggerUtil.log( TransactionType.DECLARE_REP, patient.getSelf(), rep.getSelf() );
        return new ResponseEntity( new RepView( rep ), HttpStatus.OK );
    }

    /**
     * Adds a representative to a patient's list of representatives as a HCP
     *
     * @param patientName
     *            the user name of the patient
     * @param repName
     *            the representatives user name
     * @return response
     */
    @PreAuthorize ( "hasRole('ROLE_HCP')" )
    @PostMapping ( BASE_PATH + "/declareRepHCP/{patientName}/{repName}" )
    public ResponseEntity declareRepresentativeAsHCP ( @PathVariable final String patientName,
            @PathVariable final String repName ) {
        final Patient patient = Patient.getByName( patientName );
        final Patient rep = Patient.getByName( repName );
        if ( patient == null ) {
            return new ResponseEntity( errorResponse( "Patient with username: " + patientName + " could not be found" ),
                    HttpStatus.NOT_FOUND );
        }
        else if ( rep == null ) {
            return new ResponseEntity( errorResponse( "Patient with username: " + repName + " could not be found" ),
                    HttpStatus.NOT_FOUND );
        }

        if ( patient.equals( rep ) ) {
            return new ResponseEntity( errorResponse( "Cannot add patient as its own representative" ),
                    HttpStatus.NOT_ACCEPTABLE );
        }

        final Set<Patient> patientReps = patient.getMyRepresentatives();
        if ( patientReps.contains( rep ) ) {
            return new ResponseEntity( errorResponse(
                    patient.getSelf().getUsername() + " is already represented by " + rep.getSelf().getUsername() ),
                    HttpStatus.NOT_ACCEPTABLE );
        }
        patient.addRepresentative( rep );
        patient.save();

        LoggerUtil.log( TransactionType.DECLARE_REP, patient.getSelf(), rep.getSelf() );
        return new ResponseEntity( new RepView( rep ), HttpStatus.OK );
    }

    /**
     * Undeclares a representative from the logged in user's list of reps
     *
     * @param username
     *            the username of the rep to be removed
     * @return reponse
     */
    @PreAuthorize ( "hasRole('ROLE_PATIENT')" )
    @PostMapping ( BASE_PATH + "/undeclare/{username}" )
    public ResponseEntity undeclareRepresentative ( @PathVariable final String username ) {
        final Patient patient = Patient.getByName( LoggerUtil.currentUser() );
        final Patient rep = Patient.getByName( username );
        if ( rep == null ) {
            return new ResponseEntity( errorResponse( "Patient with username: " + username + " could not be found" ),
                    HttpStatus.NOT_FOUND );
        }
        if ( !patient.removeRepresentative( rep ) ) {
            return new ResponseEntity(
                    errorResponse(
                            "Patient with username: " + username + " is not in patient list of representatives" ),
                    HttpStatus.NOT_FOUND );
        }
        patient.save();
        LoggerUtil.log( TransactionType.UNDECLARE_REP, patient.getSelf(), rep.getSelf() );
        return new ResponseEntity( new RepView( rep ), HttpStatus.OK );
    }

    /**
     * Undeclares the logged in user as a representative to another patient
     *
     * @param username
     *            the username of the rep to be removed
     * @return reponse
     */
    @PreAuthorize ( "hasRole('ROLE_PATIENT')" )
    @PostMapping ( BASE_PATH + "/undeclareSelf/{username}" )
    public ResponseEntity undeclareSelfAsRepresentative ( @PathVariable final String username ) {
        final Patient patient = Patient.getByName( LoggerUtil.currentUser() );
        final Patient otherUser = Patient.getByName( username );
        if ( otherUser == null ) {
            return new ResponseEntity( errorResponse( "Patient with username: " + username + " could not be found" ),
                    HttpStatus.NOT_FOUND );
        }

        if ( !otherUser.removeRepresentative( patient ) ) {
            return new ResponseEntity(
                    errorResponse(
                            "Patient with username: " + username + " is not in patient list of representatives" ),
                    HttpStatus.NOT_FOUND );

        }
        otherUser.save();
        LoggerUtil.log( TransactionType.UNDECLARE_SELF_AS_REP, patient.getSelf(), otherUser.getSelf() );

        return new ResponseEntity( new RepView( otherUser ), HttpStatus.OK );
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
