package edu.ncsu.csc.itrust2.controllers.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.itrust2.forms.hcp.LabRequestForm;
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
    public List<LabRequest> getAllLabRequests () {
        return LabRequest.getLabRequests();
    }

    /**
     * Gets all of the lab requests for a logged in lab tech
     *
     * @return the list a technician's lab requests
     */
    @PreAuthorize ( "hasRole('ROLE_LABTECH')" )
    @GetMapping ( BASE_PATH + "/labrequests" )
    public ResponseEntity getLabRequests () {
        try {
            final String currentUser = LoggerUtil.currentUser();
            final List<LabRequest> list = LabRequest.getLabRequestsForLabTech( currentUser );
            LoggerUtil.log( TransactionType.LAB_REQUEST_VIEW, currentUser );
            return new ResponseEntity( list, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            LoggerUtil.log( TransactionType.LAB_REQUEST_VIEW, LoggerUtil.currentUser(),
                    "Failed to retrieve list of lab requests" );
            return new ResponseEntity( errorResponse( "Could not get lab procedures:" + e.getMessage() ),
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
     * Edits an existing prescription in the system. Matches prescriptions by
     * ids. Requires HCP permissions.
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
            lr.save(); /* Overwrite existing */
            LoggerUtil.log( TransactionType.LAB_REQUEST_EDIT, LoggerUtil.currentUser(), lr.getHcp().getUsername(),
                    "Edited lab procedure with id " + lr.getId() );
            return new ResponseEntity( lr, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            LoggerUtil.log( TransactionType.LAB_REQUEST_EDIT, LoggerUtil.currentUser(), "Failed to edit lab request" );
            return new ResponseEntity( errorResponse( "Failed to update lab request: " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

}
