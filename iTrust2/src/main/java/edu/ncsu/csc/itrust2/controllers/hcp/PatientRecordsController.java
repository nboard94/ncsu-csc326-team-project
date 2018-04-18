package edu.ncsu.csc.itrust2.controllers.hcp;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.ncsu.csc.itrust2.models.persistent.User;

/**
 * Controls the mapping and handling of patient records.
 *
 * @author Will Duke
 *
 */
@Controller
public class PatientRecordsController {

    /**
     * Retrieves the page for the patient lookup
     *
     * @param model
     *            Data for front end
     * @return The page to display
     */
    @RequestMapping ( value = "hcp/viewPatientRecords" )
    @PreAuthorize ( "hasRole('ROLE_HCP')" )
    public String drugs ( final Model model ) {
        model.addAttribute( "users", User.getUsers() );
        return "hcp/viewPatientRecords";
    }

}
