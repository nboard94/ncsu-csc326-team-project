package edu.ncsu.csc.itrust2.controllers.admin;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.ncsu.csc.itrust2.forms.admin.VaccinationForm;

/**
 * Class that enables an Admin to add a Vaccination to the system.
 *
 * @author Nick Board
 *
 */
@Controller
public class VaccinationContoller {

    /**
     * Creates the form page for the Vaccinations page
     *
     * @param model
     *            Data for the front end
     * @return Page to show to the user
     */
    @RequestMapping ( value = "admin/vaccinations" )
    @PreAuthorize ( "hasRole('ROLE_ADMIN')" )
    public String vaccinations ( final Model model ) {
        model.addAttribute( "VaccinationForm", new VaccinationForm() );
        return "admin/vaccinations";
    }

}
