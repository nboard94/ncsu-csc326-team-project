package edu.ncsu.csc.itrust2.controllers.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.itrust2.models.persistent.Patient;
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
            return new ResponseEntity( errorResponse( "User with username: " + username + " could not be found" ),
                    HttpStatus.NOT_FOUND );
        }
        patient.addRepresentative( rep );
        patient.save();

        return new ResponseEntity( rep, HttpStatus.OK );
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
            return new ResponseEntity( errorResponse( "Input Patient not found" ), HttpStatus.NOT_FOUND );
        }
        else if ( rep == null ) {
            return new ResponseEntity( errorResponse( "Input Representative not found" ), HttpStatus.NOT_FOUND );
        }
        patient.addRepresentative( rep );
        patient.save();

        return new ResponseEntity( rep, HttpStatus.OK );
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
        final Patient patient1 = Patient.getByName( LoggerUtil.currentUser() );
        final Patient patient2 = Patient.getByName( fromFront[1] );

        if ( patient1 == null ) {
            return new ResponseEntity( errorResponse( "Input Patient not found" ), HttpStatus.NOT_FOUND );
        }
        else if ( patient2 == null ) {
            return new ResponseEntity( errorResponse( "Input Representative not found" ), HttpStatus.NOT_FOUND );
        }

        Patient sendToFront = null;
        // 0 == remove representative
        if ( mode.equals( "0" ) ) {
            patient1.removeRepresentative( patient2 );
            patient1.save();
            sendToFront = patient2;
        }
        // 1 == remove self as representative
        else if ( mode.equals( "1" ) ) {
            patient2.removeRepresentative( patient1 );
            patient2.save();
            sendToFront = patient1;

        }
        return new ResponseEntity( sendToFront, HttpStatus.OK );
    }

}
