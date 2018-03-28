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

import edu.ncsu.csc.itrust2.forms.admin.ImmunizationForm;
import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import edu.ncsu.csc.itrust2.models.persistent.Immunization;
import edu.ncsu.csc.itrust2.utils.LoggerUtil;

/**
 * Provides REST endpoints that deal with immunizations. Exposes functionality
 * to add, edit, fetch, and delete immunization.
 *
 * @author Nick Board
 */
@SuppressWarnings ( { "unchecked", "rawtypes" } )
@RestController
public class APIImmunizationController extends APIController {

    /**
     * Adds a new immunization to the system. Requires admin permissions.
     * Returns an error message if something goes wrong.
     *
     * @param form
     *            the immunization form
     * @return the created immunization
     */
    @PreAuthorize ( "hasRole('ROLE_ADMIN')" )
    @PostMapping ( BASE_PATH + "/immunizations" )
    public ResponseEntity addImmunization ( @RequestBody final ImmunizationForm form ) {
        try {
            final Immunization immunization = new Immunization( form );

            // Make sure code does not conflict with existing immunizations
            if ( Immunization.getByCode( immunization.getCode() ) != null ) {
                LoggerUtil.log( TransactionType.VACCINATION_CREATE, LoggerUtil.currentUser(),
                        "Conflict: immunization with code " + immunization.getCode() + " already exists" );
                return new ResponseEntity(
                        errorResponse( "Immunization with code " + immunization.getCode() + " already exists" ),
                        HttpStatus.CONFLICT );
            }

            immunization.save();
            LoggerUtil.log( TransactionType.VACCINATION_CREATE, LoggerUtil.currentUser(),
                    "Immunization " + immunization.getCode() + " created" );
            return new ResponseEntity( immunization, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            LoggerUtil.log( TransactionType.VACCINATION_CREATE, LoggerUtil.currentUser(),
                    "Failed to create immunization" );
            return new ResponseEntity( errorResponse( "Could not add immunization: " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Edits a immunization in the system. The id stored in the form must match
     * an existing immunization, and changes to codes cannot conflict with
     * existing codes. Requires admin permissions.
     *
     * @param form
     *            the edited immunization form
     * @return the edited immunization or an error message
     */
    @PreAuthorize ( "hasRole('ROLE_ADMIN')" )
    @PutMapping ( BASE_PATH + "/immunizations" )
    public ResponseEntity editImmunization ( @RequestBody final ImmunizationForm form ) {
        try {
            final Immunization immunization = new Immunization( form );

            // Check for existing immunization in database
            final Immunization savedImmunization = Immunization.getById( immunization.getId() );
            if ( savedImmunization == null ) {
                return new ResponseEntity( errorResponse( "No immunization found with code " + immunization.getCode() ),
                        HttpStatus.NOT_FOUND );
            }

            // If the code was changed, make sure it is unique
            final Immunization sameCode = Immunization.getByCode( immunization.getCode() );
            if ( sameCode != null && !sameCode.getId().equals( savedImmunization.getId() ) ) {
                return new ResponseEntity(
                        errorResponse( "Immunization with code " + immunization.getCode() + " already exists" ),
                        HttpStatus.CONFLICT );
            }

            immunization.save(); /* Overwrite existing immunization */

            LoggerUtil.log( TransactionType.VACCINATION_EDIT, LoggerUtil.currentUser(),
                    "Immunization with id " + immunization.getId() + " edited" );
            return new ResponseEntity( immunization, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            LoggerUtil.log( TransactionType.VACCINATION_EDIT, LoggerUtil.currentUser(), "Failed to edit immunization" );
            return new ResponseEntity( errorResponse( "Could not update immunization: " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Deletes the immunization with the id matching the given id. Requires
     * admin permissions.
     *
     * @param id
     *            the id of the immunization to delete
     * @return the id of the deleted immunization
     */
    @PreAuthorize ( "hasRole('ROLE_ADMIN')" )
    @DeleteMapping ( BASE_PATH + "/immunizations/{id}" )
    public ResponseEntity deleteImmunization ( @PathVariable final String id ) {
        try {
            final Immunization immunization = Immunization.getById( Long.parseLong( id ) );
            if ( immunization == null ) {
                LoggerUtil.log( TransactionType.VACCINATION_DELETE, LoggerUtil.currentUser(),
                        "Could not find immunization with id " + id );
                return new ResponseEntity( errorResponse( "No immunization found with id " + id ),
                        HttpStatus.NOT_FOUND );
            }
            immunization.delete();
            LoggerUtil.log( TransactionType.VACCINATION_DELETE, LoggerUtil.currentUser(),
                    "Deleted immunization with id " + immunization.getId() );
            return new ResponseEntity( id, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            LoggerUtil.log( TransactionType.VACCINATION_DELETE, LoggerUtil.currentUser(),
                    "Failed to delete immunization" );
            return new ResponseEntity( errorResponse( "Could not delete immunization: " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Gets a list of all the immunizations in the system.
     *
     * @return a list of immunizations
     */
    @GetMapping ( BASE_PATH + "/immunizations" )
    public List<Immunization> getImmunizations () {
        LoggerUtil.log( TransactionType.VACCINATION_VIEW, LoggerUtil.currentUser(), "Fetched list of immunizations" );
        return Immunization.getAll();
    }

}
