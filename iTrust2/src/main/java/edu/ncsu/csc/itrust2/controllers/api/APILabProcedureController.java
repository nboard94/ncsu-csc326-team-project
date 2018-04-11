package edu.ncsu.csc.itrust2.controllers.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.itrust2.forms.admin.LabProcedureForm;
import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import edu.ncsu.csc.itrust2.models.persistent.LabProcedure;
import edu.ncsu.csc.itrust2.utils.LoggerUtil;

/**
 * Provides REST endpoints that deal with LabProcedures. Exposes functionality
 * to add, edit, fetch, and delete LabProcedure.
 *
 * @author Jacob Struckmeyer
 */
@SuppressWarnings ( { "unchecked", "rawtypes" } )
@RestController
public class APILabProcedureController extends APIController {

    /**
     * Adds a new LabProcedure to the system. Requires admin permissions.
     * Returns an error message if something goes wrong.
     *
     * @param form
     *            the LabProcedure form
     * @return the created LabProcedure
     */
    @PreAuthorize ( "hasRole('ROLE_ADMIN')" )
    @PostMapping ( BASE_PATH + "/labprocedures" )
    public ResponseEntity addLabProcedure ( @RequestBody final LabProcedureForm form ) {
        try {
            final LabProcedure lp = new LabProcedure( form );

            // Make sure code does not conflict with existing lab procedures
            if ( LabProcedure.getByCode( lp.getCode() ) != null ) {
                LoggerUtil.log( TransactionType.DRUG_CREATE, LoggerUtil.currentUser(),
                        "Conflict: Lab Procedure with code " + lp.getCode() + " already exists" );
                return new ResponseEntity(
                        errorResponse( "Lab Procedure with code " + lp.getCode() + " already exists" ),
                        HttpStatus.CONFLICT );
            }

            lp.save();
            LoggerUtil.log( TransactionType.DRUG_CREATE, LoggerUtil.currentUser(),
                    "Lab Procedure " + lp.getCode() + " created" );
            return new ResponseEntity( lp, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            LoggerUtil.log( TransactionType.DRUG_CREATE, LoggerUtil.currentUser(), "Failed to create Lab Procedure" );
            return new ResponseEntity( errorResponse( "Could not add Lab Procedure: " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Edits a LabProcedure in the system. The id stored in the form must match
     * an existing LabProcedure, and changes to NDCs cannot conflict with
     * existing NDCs. Requires admin permissions.
     *
     * @param form
     *            the edited LabProcedure form
     * @return the edited LabProcedure or an error message
     */
    @PreAuthorize ( "hasRole('ROLE_ADMIN')" )
    @PutMapping ( BASE_PATH + "/labprocedures" )
    public ResponseEntity editLabProcedure ( @RequestBody final LabProcedureForm form ) {
        try {
            final LabProcedure lp = new LabProcedure( form );

            // Check for existing LabProcedure in database

            // TODO fix this to make sure that ID type is long for lab
            // procedures
            final LabProcedure savedLabProcedure = null; // LabProcedure.getById(
                                                         // lp.getId() );
            if ( savedLabProcedure == null ) {
                return new ResponseEntity( errorResponse( "No Lab Procedure found with code " + lp.getCode() ),
                        HttpStatus.NOT_FOUND );
            }

            // If the code was changed, make sure it is unique
            final LabProcedure sameCode = LabProcedure.getByCode( lp.getCode() );
            if ( sameCode != null && !sameCode.getId().equals( savedLabProcedure.getId() ) ) {
                return new ResponseEntity(
                        errorResponse( "Lab Procedure with code " + lp.getCode() + " already exists" ),
                        HttpStatus.CONFLICT );
            }

            lp.save(); /* Overwrite existing LabProcedure */

            LoggerUtil.log( TransactionType.DRUG_EDIT, LoggerUtil.currentUser(),
                    "Lab Procedure with id " + lp.getId() + " edited" );
            return new ResponseEntity( lp, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            LoggerUtil.log( TransactionType.DRUG_EDIT, LoggerUtil.currentUser(), "Failed to edit LabProcedure" );
            return new ResponseEntity( errorResponse( "Could not update LabProcedure: " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Deletes the LabProcedure with the id matching the given id. Requires
     * admin permissions.
     *
     * @param id
     *            the id of the LabProcedure to delete
     * @return the id of the deleted LabProcedure
     */
    @PreAuthorize ( "hasRole('ROLE_ADMIN')" )
    @DeleteMapping ( BASE_PATH + "/labprocedures/{id}" )
    public ResponseEntity deleteLabProcedure ( @PathVariable final String id ) {
        try {
            final LabProcedure lp = LabProcedure.getById( Long.parseLong( id ) );
            if ( lp == null ) {
                LoggerUtil.log( TransactionType.DRUG_DELETE, LoggerUtil.currentUser(),
                        "Could not find Lab Procedure with id " + id );
                return new ResponseEntity( errorResponse( "No drug found with id " + id ), HttpStatus.NOT_FOUND );
            }
            lp.delete();
            LoggerUtil.log( TransactionType.DRUG_DELETE, LoggerUtil.currentUser(),
                    "Deleted Lab Procedure with id " + lp.getId() );
            return new ResponseEntity( id, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            LoggerUtil.log( TransactionType.DRUG_DELETE, LoggerUtil.currentUser(), "Failed to delete drug" );
            return new ResponseEntity( errorResponse( "Could not delete Lab Procedure: " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Gets a list of all the LabProcedures in the system.
     *
     * @return a list of LabProcedures
     */
    @GetMapping ( BASE_PATH + "/labprocedures" )
    public List<LabProcedure> getLabProcedures () {
        LoggerUtil.log( TransactionType.DRUG_VIEW, LoggerUtil.currentUser(), "Fetched list of Lab Procedures" );
        return LabProcedure.getAll();
    }

}
