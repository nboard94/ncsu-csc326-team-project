package edu.ncsu.csc.itrust2.controllers.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    @PostMapping ( BASE_PATH + "/vacrecords" )
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
    @PutMapping ( BASE_PATH + "/vacrecords" )
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

    /**
     * Deletes the vaccination record with the given id.
     *
     * @param id
     *            the id
     * @return the id of the deleted vaccination record
     */
    @PreAuthorize ( "hasRole('ROLE_HCP')" )
    @DeleteMapping ( BASE_PATH + "/vacrecords/{id}" )
    public ResponseEntity deleteVacRecord ( @PathVariable final Long id ) {
        final VacRecord p = VacRecord.getById( id );
        if ( p == null ) {
            return new ResponseEntity( errorResponse( "No vaccination record found with id " + id ),
                    HttpStatus.NOT_FOUND );
        }
        try {
            p.delete();
            LoggerUtil.log( TransactionType.VACRECORD_DELETE, LoggerUtil.currentUser(), p.getPatient().getUsername(),
                    "Deleted vaccination record with id " + p.getId() );
            return new ResponseEntity( p.getId(), HttpStatus.OK );
        }
        catch ( final Exception e ) {
            LoggerUtil.log( TransactionType.VACRECORD_DELETE, LoggerUtil.currentUser(), p.getPatient().getUsername(),
                    "Failed to delete vaccination record" );
            return new ResponseEntity( errorResponse( "Failed to delete vaccination record: " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Returns a collection of all vaccination records in the system
     *
     * @return all saved vaccination records
     */
    @PreAuthorize ( "hasAnyRole('ROLE_HCP', 'ROLE_PATIENT')" )
    @GetMapping ( BASE_PATH + "/vacrecords" )
    public List<VacRecord> getVacRecords () {
        final boolean isHCP = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .contains( new SimpleGrantedAuthority( "ROLE_HCP" ) );
        if ( isHCP ) {
            LoggerUtil.log( TransactionType.VACRECORD_VIEW, LoggerUtil.currentUser(),
                    "HCP viewed a list of all prescriptions" );
            return VacRecord.getVacRecords();
        }
        else {
            return VacRecord.getForPatient( LoggerUtil.currentUser() );
        }
    }

    /**
     * Returns a single vaccination record using the given id.
     *
     * @param id
     *            the id of the desired prescription
     * @return the requested prescription
     */
    @PreAuthorize ( "hasRole('ROLE_HCP')" )
    @GetMapping ( BASE_PATH + "vacrecords/{id}" )
    public ResponseEntity getVacRecord ( @PathVariable final Long id ) {
        final VacRecord p = VacRecord.getById( id );
        if ( p == null ) {
            LoggerUtil.log( TransactionType.VACRECORD_VIEW, LoggerUtil.currentUser(),
                    "Failedf to find vaccination record with id " + id );
            return new ResponseEntity( errorResponse( "No vaccination record found for " + id ), HttpStatus.NOT_FOUND );
        }
        else {
            LoggerUtil.log( TransactionType.VACRECORD_VIEW, LoggerUtil.currentUser(),
                    "Viewed vaccination record " + id );
            return new ResponseEntity( p, HttpStatus.OK );
        }
    }
}
