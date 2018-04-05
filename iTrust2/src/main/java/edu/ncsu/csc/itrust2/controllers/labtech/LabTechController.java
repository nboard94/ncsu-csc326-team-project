package edu.ncsu.csc.itrust2.controllers.labtech;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller class responsible for managing the behavior for the Lab Tech
 * Landing Screen
 *
 * @author Cody Roberts (jdrobe10)
 *
 */
@Controller
public class LabTechController {

    /**
     * Returns the Landing screen for the Lab Tech
     *
     * @param model
     *            Data from the front end
     * @return The page to display
     */
    @RequestMapping ( value = "labtech/index" )
    @PreAuthorize ( "hasRole('ROLE_LABTECH')" )
    public String index ( final Model model ) {
        return edu.ncsu.csc.itrust2.models.enums.Role.ROLE_LABTECH.getLanding();
    }

    /**
     * Returns the edit demographics screen for Lab Techs
     *
     * @return The page to display
     */
    @GetMapping ( "/labtech/editPatientDemographics" )
    @PreAuthorize ( "hasRole('ROLE_LABTECH')" )
    public String editDemographics () {
        return "/labtech/editPatientDemographics";
    }

    // Code for Lab Procedures

}
