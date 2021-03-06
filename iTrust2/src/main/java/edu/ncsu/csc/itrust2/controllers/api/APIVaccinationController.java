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

import edu.ncsu.csc.itrust2.forms.admin.VaccinationForm;
import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import edu.ncsu.csc.itrust2.models.persistent.Vaccination;
import edu.ncsu.csc.itrust2.utils.LoggerUtil;

/**
 * Provides REST endpoints that deal with vaccinations. Exposes functionality to
 * add, edit, fetch, and delete vaccination.
 *
 * @author Nick Board
 */
@SuppressWarnings ( { "unchecked", "rawtypes" } )
@RestController
public class APIVaccinationController extends APIController {

    /**
     * Adds a new vaccination to the system. Requires admin permissions. Returns
     * an error message if something goes wrong.
     *
     * @param form
     *            the vaccination form
     * @return the created vaccination
     */
    @PreAuthorize ( "hasRole('ROLE_ADMIN')" )
    @PostMapping ( BASE_PATH + "/vaccinations" )
    public ResponseEntity addVaccination ( @RequestBody final VaccinationForm form ) {
        try {
            final Vaccination vaccination = new Vaccination( form );

            // Make sure code does not conflict with existing vaccinations
            if ( Vaccination.getByCode( vaccination.getCode() ) != null ) {
                LoggerUtil.log( TransactionType.VACCINATION_CREATE, LoggerUtil.currentUser(),
                        "Conflict: vaccination with code " + vaccination.getCode() + " already exists" );
                return new ResponseEntity(
                        errorResponse( "Vaccination with code " + vaccination.getCode() + " already exists" ),
                        HttpStatus.CONFLICT );
            }

            vaccination.save();
            LoggerUtil.log( TransactionType.VACCINATION_CREATE, LoggerUtil.currentUser(),
                    "Vaccination " + vaccination.getCode() + " created" );
            return new ResponseEntity( vaccination, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            LoggerUtil.log( TransactionType.VACCINATION_CREATE, LoggerUtil.currentUser(),
                    "Failed to create vaccination" );
            return new ResponseEntity( errorResponse( "Could not add vaccination: " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Edits a vaccination in the system. The id stored in the form must match
     * an existing vaccination, and changes to codes cannot conflict with
     * existing codes. Requires admin permissions.
     *
     * @param form
     *            the edited vaccination form
     * @return the edited vaccination or an error message
     */
    @PreAuthorize ( "hasRole('ROLE_ADMIN')" )
    @PutMapping ( BASE_PATH + "/vaccinations" )
    public ResponseEntity editVaccination ( @RequestBody final VaccinationForm form ) {
        try {
            final Vaccination vaccination = new Vaccination( form );

            // Check for existing vaccination in database
            final Vaccination savedVaccination = Vaccination.getById( vaccination.getId() );
            if ( savedVaccination == null ) {
                return new ResponseEntity( errorResponse( "No vaccination found with code " + vaccination.getCode() ),
                        HttpStatus.NOT_FOUND );
            }

            // If the code was changed, make sure it is unique
            final Vaccination sameCode = Vaccination.getByCode( vaccination.getCode() );
            if ( sameCode != null && !sameCode.getId().equals( savedVaccination.getId() ) ) {
                return new ResponseEntity(
                        errorResponse( "Vaccination with code " + vaccination.getCode() + " already exists" ),
                        HttpStatus.CONFLICT );
            }

            vaccination.save(); /* Overwrite existing vaccination */

            LoggerUtil.log( TransactionType.VACCINATION_EDIT, LoggerUtil.currentUser(),
                    "Vaccination with id " + vaccination.getId() + " edited" );
            return new ResponseEntity( vaccination, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            LoggerUtil.log( TransactionType.VACCINATION_EDIT, LoggerUtil.currentUser(), "Failed to edit vaccination" );
            return new ResponseEntity( errorResponse( "Could not update vaccination: " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Deletes the vaccination with the id matching the given id. Requires admin
     * permissions.
     *
     * @param id
     *            the id of the vaccination to delete
     * @return the id of the deleted vaccination
     */
    @PreAuthorize ( "hasRole('ROLE_ADMIN')" )
    @DeleteMapping ( BASE_PATH + "/vaccinations/{id}" )
    public ResponseEntity deleteVaccination ( @PathVariable final String id ) {
        try {
            final Vaccination vaccination = Vaccination.getById( Long.parseLong( id ) );
            if ( vaccination == null ) {
                LoggerUtil.log( TransactionType.VACCINATION_DELETE, LoggerUtil.currentUser(),
                        "Could not find vaccination with id " + id );
                return new ResponseEntity( errorResponse( "No vaccination found with id " + id ),
                        HttpStatus.NOT_FOUND );
            }
            vaccination.delete();
            LoggerUtil.log( TransactionType.VACCINATION_DELETE, LoggerUtil.currentUser(),
                    "Deleted vaccination with id " + vaccination.getId() );
            return new ResponseEntity( id, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            LoggerUtil.log( TransactionType.VACCINATION_DELETE, LoggerUtil.currentUser(),
                    "Failed to delete vaccination" );
            return new ResponseEntity( errorResponse( "Could not delete vaccination: " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Gets a list of all the vaccinations in the system.
     *
     * @return a list of vaccinations
     */
    @GetMapping ( BASE_PATH + "/vaccinations" )
    public List<Vaccination> getVaccinations () {
        LoggerUtil.log( TransactionType.VACCINATION_VIEW, LoggerUtil.currentUser(), "Fetched list of vaccinations" );
        return Vaccination.getAll();
    }

}
