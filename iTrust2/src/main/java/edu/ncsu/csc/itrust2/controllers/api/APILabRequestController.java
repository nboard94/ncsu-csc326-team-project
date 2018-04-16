package edu.ncsu.csc.itrust2.controllers.api;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import edu.ncsu.csc.itrust2.models.persistent.LabRequest;
import edu.ncsu.csc.itrust2.utils.LoggerUtil;

/**
 * Provides REST endpoints that deal with LabRequests. Exposes functionality to
 * add, edit, fetch, and delete LabProcedure.
 *
 * @author Jacob Struckmeyer
 */
// @SuppressWarnings ( { "unchecked", "rawtypes" } )
@RestController
public class APILabRequestController extends APIController {

    /**
     * Gets a list of all the LabRequests in the system.
     *
     * @return a list of LabProcedures
     */
    @GetMapping ( BASE_PATH + "/labrequests" )
    public List<LabRequest> getLabRequests () {
        // TODO change the transaction type to lab requests
        LoggerUtil.log( TransactionType.DRUG_VIEW, LoggerUtil.currentUser(), "Fetched list of Lab Procedures" );
        return LabRequest.getLabRequests();
    }

    /**
     * Deletes all LabRequests in the system. This cannot be reverse; exercise
     * caution before calling it
     */
    @DeleteMapping ( BASE_PATH + "/labrequests" )
    public void deleteOfficeVisits () {
        // TODO change the transaction type to lab requests
        LoggerUtil.log( TransactionType.OFFICE_VISIT_DELETE, LoggerUtil.currentUser() );
        LabRequest.deleteAll( LabRequest.class );
    }

}
