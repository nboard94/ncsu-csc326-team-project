package edu.ncsu.csc.itrust2.controllers.hcp;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller class responsible for managing the behavior for the HCP Landing
 * Screen
 *
 * @author Kai Presler-Marshall
 *
 */
@Controller
public class HCPController {

    /**
     * Returns the Landing screen for the HCP
     *
     * @param model
     *            Data from the front end
     * @return The page to display
     */
    @RequestMapping ( value = "hcp/index" )
    @PreAuthorize ( "hasRole('ROLE_HCP')" )
    public String index ( final Model model ) {
        return edu.ncsu.csc.itrust2.models.enums.Role.ROLE_HCP.getLanding();
    }

    /**
     * Returns the page that allows HCPs to view personal representatives
     *
     * @param model
     *            the date for the front end
     * @return page to display the personal representatives
     */
    @GetMapping ( "/hcp/viewPatientPersonalRepresentatives" )
    @PreAuthorize ( "hasRole('ROLE_HCP')" )
    public String viewPersonalRepresentatives ( final Model model ) {
        return "/hcp/viewPatientPersonalRepresentatives";
    }

    /**
     * Returns the page allowing HCPs to edit patient demographics
     *
     * @return The page to display
     */
    @GetMapping ( "/hcp/editPatientDemographics" )
    @PreAuthorize ( "hasRole('ROLE_HCP')" )
    public String editPatientDemographics () {
        return "/hcp/editPatientDemographics";
    }

    /**
     * Returns the page allowing HCPs to edit prescriptions
     *
     * @return The page to display
     */
    @GetMapping ( "/hcp/editPrescriptions" )
    @PreAuthorize ( "hasRole('ROLE_HCP')" )
    public String editPrescriptions () {
        return "/hcp/editPrescriptions";
    }

    /**
     * Returns the page allowing HCPs to edit vaccinations
     *
     * @return The page to display
     */
    @GetMapping ( "/hcp/editVaccinations" )
    @PreAuthorize ( "hasRole('ROLE_HCP')" )
    public String editVaccinations () {
        return "/hcp/editVaccinations";
    }

}
