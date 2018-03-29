package edu.ncsu.csc.itrust2.controllers.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.itrust2.forms.hcp.VacRecordForm;
import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import edu.ncsu.csc.itrust2.models.persistent.VacRecord;
import edu.ncsu.csc.itrust2.utils.LoggerUtil;

/**
 * Provides REST endpoints that deal with VacRecords. Exposes functionality to
 * add, edit, fetch, and delete VacRecords.
 *
 * @author Nick Board
 */
@RestController
@SuppressWarnings ( { "rawtypes", "unchecked" } )
public class APIVacRecordController extends APIController {

    /**
     * Adds a new VacRecord to the system. Requires HCP permissions.
     *
     * @param form
     *            details of the new VacRecord
     * @return the created VacRecord
     */
    @PreAuthorize ( "hasRole('ROLE_HCP')" )
    @PostMapping ( BASE_PATH + "/vacRecords" )
    public ResponseEntity addVacRecord ( @RequestBody final VacRecordForm form ) {
        try {
            final VacRecord p = new VacRecord( form );
            p.save();
            LoggerUtil.log( TransactionType.VACRECORD_CREATE, LoggerUtil.currentUser(), p.getPatient().getUsername(),
                    "Created vaccination record with id " + p.getId() );
            return new ResponseEntity( p, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            LoggerUtil.log( TransactionType.VACRECORD_CREATE, LoggerUtil.currentUser(),
                    "Failed to create vaccination record" );
            return new ResponseEntity( errorResponse( "Could not save the vaccination record: " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Edits an existing vac record in the system. Matches vac records by ids.
     * Requires HCP permissions.
     *
     * @param form
     *            the form containing the details of the new vac record
     * @return the edited vac record
     */
    @PreAuthorize ( "hasRole('ROLE_HCP')" )
    @PutMapping ( BASE_PATH + "/vacRecords" )
    public ResponseEntity editVacRecord ( @RequestBody final VacRecordForm form ) {
        try {
            final VacRecord p = new VacRecord( form );
            final VacRecord saved = VacRecord.getById( p.getId() );
            if ( saved == null ) {
                LoggerUtil.log( TransactionType.VACRECORD_EDIT, LoggerUtil.currentUser(),
                        "No vaccination record found with id " + p.getId() );
                return new ResponseEntity( errorResponse( "No vaccination record found with id " + p.getId() ),
                        HttpStatus.NOT_FOUND );
            }
            p.save();
            LoggerUtil.log( TransactionType.VACCINATION_EDIT, LoggerUtil.currentUser(), p.getPatient().getUsername(),
                    "Edited vaccination record with id " + p.getId() );
            return new ResponseEntity( p, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            LoggerUtil.log( TransactionType.VACRECORD_EDIT, LoggerUtil.currentUser(),
                    "Failed to edit vaccination record" );
            return new ResponseEntity( errorResponse( "Failed to update vaccination records: " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

}
