package edu.ncsu.csc.itrust2.controllers.er;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller class responsible for managing the behavior for the ER Landing
 * Screen
 *
 * @author Cody Roberts (jdrobe10)
 */
@Controller
public class ERController {

    /**
     * Returns the Landing screen for the Emergency Responder
     *
     * @param model
     *            Data form the front end
     * @return The page to display
     */
    @RequestMapping ( value = "er/index" )
    @PreAuthorize ( "hasRole('ROLE_ER')" )
    public String index ( final Model model ) {
        return edu.ncsu.csc.itrust2.models.enums.Role.ROLE_ER.getLanding();
    }

    /**
     * Returns the page allowing ERs to edit patient demographics
     *
     * @return The page to display
     */
    @GetMapping ( "/er/editPatientDemographics" )
    @PreAuthorize ( "hasRole('ROLE_ER')" )
    public String editPatientDemographics () {
        return "/er/editPatientDemographics";
    }

    /**
     * Returns the page allowing ERs to access emergency health records
     *
     * @return The page to display
     */
    @GetMapping ( "er/emergencyHealthRecords" )
    @PreAuthorize ( "hasRole('ROLE_ER')" )
    public String emergencyHealthRecords () {
        return "/er/emergencyHealthRecords";
    }

}
