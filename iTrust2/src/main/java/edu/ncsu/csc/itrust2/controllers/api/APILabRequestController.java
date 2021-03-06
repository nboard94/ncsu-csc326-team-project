package edu.ncsu.csc.itrust2.controllers.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.itrust2.forms.hcp.LabRequestForm;
import edu.ncsu.csc.itrust2.models.enums.Status;
import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import edu.ncsu.csc.itrust2.models.persistent.LabRequest;
import edu.ncsu.csc.itrust2.utils.LoggerUtil;

/**
 * Provides REST endpoints that deal with LabRequests. Exposes functionality to
 * add, edit, fetch, and delete LabProcedure.
 *
 * @author Jacob Struckmeyer
 */
@SuppressWarnings ( { "unchecked", "rawtypes" } )
@RestController
public class APILabRequestController extends APIController {

    /**
     * Gets a list of all the LabRequests in the system.
     *
     * @return a list of LabProcedures
     */
    @GetMapping ( BASE_PATH + "/labrequests/all" )
    public List<LabRequestForm> getAllLabRequests () {
        return wrapLabRequests( LabRequest.getLabRequests() );
    }

    /**
     * Gets all of the lab requests for a logged in lab tech
     *
     * @return the list a technician's lab requests
     */
    @PreAuthorize ( "hasRole('ROLE_LABTECH')" )
    @GetMapping ( BASE_PATH + "/labrequests/labtech" )
    public ResponseEntity getLabRequestsForLabTech () {
        try {
            final String currentUser = LoggerUtil.currentUser();
            final List<LabRequest> list = LabRequest.getLabRequestsForLabTech( currentUser );
            LoggerUtil.log( TransactionType.LAB_REQUEST_VIEW, currentUser );
            return new ResponseEntity( wrapLabRequests( list ), HttpStatus.OK );
        }
        catch ( final Exception e ) {
            LoggerUtil.log( TransactionType.LAB_REQUEST_VIEW, LoggerUtil.currentUser(),
                    "Failed to retrieve list of lab requests" );
            return new ResponseEntity( errorResponse( "Could not get lab requests:" + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Takes a list of lab requests and transforms them into forms so that they
     * can be read by the front end
     *
     * @param list
     *            the list of lab requests
     * @return the for versions of the lab requests
     */
    private List<LabRequestForm> wrapLabRequests ( final List<LabRequest> list ) {
        final List<LabRequestForm> toFront = new ArrayList<LabRequestForm>();
        for ( final LabRequest lr : list ) {
            toFront.add( new LabRequestForm( lr ) );
        }
        return toFront;
    }

    /**
     * Gets all of the lab requests for a logged in lab tech
     *
     * @return the list a technician's lab requests
     */
    @PreAuthorize ( "hasRole('ROLE_HCP')" )
    @GetMapping ( BASE_PATH + "/labrequests/hcp" )
    public ResponseEntity getLabRequestsForHCP () {
        try {
            final String currentUser = LoggerUtil.currentUser();
            final List<LabRequest> list = LabRequest.getLabRequestsForHCP( currentUser );
            LoggerUtil.log( TransactionType.LAB_REQUEST_VIEW, currentUser );
            return new ResponseEntity( wrapLabRequests( list ), HttpStatus.OK );
        }
        catch ( final Exception e ) {
            LoggerUtil.log( TransactionType.LAB_REQUEST_VIEW, LoggerUtil.currentUser(),
                    "Failed to retrieve list of lab requests" );
            return new ResponseEntity( errorResponse( "Could not get lab requests:" + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Gets all of the labrequests for that are associated with a certain
     * patient
     *
     * @param patientName
     *            the username of the searched patient
     * @return the list of all of the patient's lab requests
     */
    @PreAuthorize ( "hasRole('ROLE_HCP') or hasRole('ROLE_LABTECH')" )
    @GetMapping ( BASE_PATH + "/labrequests/{patientName}" )
    public ResponseEntity getLabRequestsForPatient ( @PathVariable final String patientName ) {
        try {
            final List<LabRequest> list = LabRequest.getLabRequestsForPatient( patientName );
            LoggerUtil.log( TransactionType.LAB_REQUEST_VIEW, LoggerUtil.currentUser(), patientName );
            return new ResponseEntity( wrapLabRequests( list ), HttpStatus.OK );
        }
        catch ( final Exception e ) {
            LoggerUtil.log( TransactionType.LAB_REQUEST_VIEW, LoggerUtil.currentUser(),
                    "Failed to retrieve list of lab requests" );
            return new ResponseEntity( errorResponse( "Could not get lab requests:" + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Deletes all LabRequests in the system. This cannot be reverse; exercise
     * caution before calling it
     */
    @DeleteMapping ( BASE_PATH + "/labrequests" )
    public void deleteAllLabRequests () {
        LoggerUtil.log( TransactionType.LAB_REQUEST_DELETE, LoggerUtil.currentUser() );
        LabRequest.deleteAll( LabRequest.class );
    }

    /**
     * Edits an existing lab request in the system. Matches lab requests by ids.
     * Requires LabTech permissions.
     *
     * @param form
     *            the form containing the details of the new prescription
     * @return the edited prescription
     */
    @PreAuthorize ( "hasRole('ROLE_LABTECH')" )
    @PutMapping ( BASE_PATH + "/labrequests" )
    public ResponseEntity editLabRequest ( @RequestBody final LabRequestForm form ) {
        try {
            final LabRequest lr = new LabRequest( form );
            final LabRequest saved = LabRequest.getById( lr.getId() );
            if ( saved == null ) {
                LoggerUtil.log( TransactionType.LAB_REQUEST_EDIT, LoggerUtil.currentUser(),
                        "No lab request found with id " + lr.getId() );
                return new ResponseEntity( errorResponse( "No lab request found with id " + lr.getId() ),
                        HttpStatus.NOT_FOUND );
            }
            if ( saved.getStatus().getCode() > lr.getStatus().getCode() ) {
                LoggerUtil.log( TransactionType.LAB_REQUEST_EDIT, LoggerUtil.currentUser(),
                        "Invalid status change for lab request with id " + lr.getId() );
                return new ResponseEntity( errorResponse( "Cannot change status to an earlier value" ),
                        HttpStatus.NOT_ACCEPTABLE );
            }
            if ( lr.getComments().length() > 128 ) {
                LoggerUtil.log( TransactionType.LAB_REQUEST_EDIT, LoggerUtil.currentUser(),
                        "Invalid comment length for lab request with id " + lr.getId() );
                return new ResponseEntity( errorResponse( "Comment length has to be less than 128 characters" ),
                        HttpStatus.NOT_ACCEPTABLE );
            }
            lr.save(); /* Overwrite existing */
            LoggerUtil.log( TransactionType.LAB_REQUEST_EDIT, LoggerUtil.currentUser(), lr.getHcp().getUsername(),
                    "Edited lab procedure with id " + lr.getId() );
            return new ResponseEntity( new LabRequestForm( lr ), HttpStatus.OK );
        }
        catch ( final Exception e ) {
            LoggerUtil.log( TransactionType.LAB_REQUEST_EDIT, LoggerUtil.currentUser(), "Failed to edit lab request" );
            return new ResponseEntity( errorResponse( "Failed to update lab request: " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Gets the list of supported statuses levels
     *
     * @return a list of all of the priority levels
     */
    @GetMapping ( BASE_PATH + "/labrequests/statuses" )
    public Status[] getStatusList () {
        return Status.values();
    }

}
