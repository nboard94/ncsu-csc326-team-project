package edu.ncsu.csc.itrust2.controllers.er;

import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.ncsu.csc.itrust2.forms.personnel.PersonnelForm;
import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import edu.ncsu.csc.itrust2.models.persistent.Personnel;
import edu.ncsu.csc.itrust2.models.persistent.User;
import edu.ncsu.csc.itrust2.utils.LoggerUtil;

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
     * Provides the page for a User to view and edit their demographics
     *
     * @param model
     *            The data for the front end
     * @return The page to show the user so they can edit demographics
     */
    @GetMapping ( value = "er/editDemographics" )
    @PreAuthorize ( "hasRole('ROLE_ER')" )
    public String viewDemographics ( final Model model ) {
        final User self = User.getByName( SecurityContextHolder.getContext().getAuthentication().getName() );
        final PersonnelForm form = new PersonnelForm( self );
        model.addAttribute( "PersonnelForm", form );
        LoggerUtil.log( TransactionType.VIEW_DEMOGRAPHICS, self );
        return "/er/editDemographics";
    }

    /**
     * Processes the Edit Demographics form for a Patient
     *
     * @param form
     *            Form from the user to parse and validate
     * @param result
     *            The validation result on the firm
     * @param model
     *            Data from the front end
     * @return Page to show to the user
     */
    @PostMapping ( "/er/editDemographics" )
    @PreAuthorize ( "hasRole('ROLE_ER')" )
    public String demographicsSubmit ( @Valid @ModelAttribute ( "PersonnelForm" ) final PersonnelForm form,
            final BindingResult result, final Model model ) {
        Personnel p = null;
        try {
            p = new Personnel( form );
            p.setSelf( User.getByName( SecurityContextHolder.getContext().getAuthentication().getName() ) );
        }
        catch ( final Exception e ) {
            e.printStackTrace( System.out );
            result.rejectValue( "dateOfBirth", "dateOfBirth.notvalid", "Expected format: MM/DD/YYYY" );
        }

        if ( result.hasErrors() ) {
            model.addAttribute( "PatientForm", form );
            return "/er/editDemographics";
        }
        else {
            // Delete the patient so that the cache has to refresh.
            final Personnel oldPersonnel = Personnel.getByName( p.getSelf().getUsername() );
            if ( oldPersonnel != null ) {
                oldPersonnel.delete();
            }
            p.save();
            LoggerUtil.log( TransactionType.EDIT_DEMOGRAPHICS,
                    SecurityContextHolder.getContext().getAuthentication().getName() );
            return "er/editDemographicsResult";
        }
    }

    /**
     * Returns the page allowing ERs to access emergency health records
     *
     * @return The page to display
     */
    /*
     * @GetMapping ( "er/emergencyHealthRecords" )
     * @PreAuthorize ( "hasRole('ROLE_ER')" ) public String
     * emergencyHealthRecords () { return "/er/emergencyHealthRecords"; }
     */

}
