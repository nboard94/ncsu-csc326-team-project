package edu.ncsu.csc.itrust2.models.persistent;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.criterion.Criterion;

import edu.ncsu.csc.itrust2.forms.hcp.LabRequestForm;
import edu.ncsu.csc.itrust2.models.enums.Priority;
import edu.ncsu.csc.itrust2.models.enums.Role;

/**
 * Backing object for the Lab request system. This is the object that is
 * actually stored in the database and reflects the persistent information we
 * have on the appointment request.
 *
 * @author Kai Presler-Marshall
 */

@Entity
@Table ( name = "LabRequests" )
public class LabRequest extends DomainObject<LabRequest> {

    /**
     * Retrieve an LabRequest by its numerical ID.
     *
     * @param id
     *            The ID (as assigned by the DB) of the LabRequest
     * @return The LabRequestS, if found, or null if not found.
     */
    public static LabRequest getById ( final Long id ) {
        try {
            return getWhere( createCriterionAsList( ID, id ) ).get( 0 );
        }
        catch ( final Exception e ) {
            return null;
        }
    }

    /**
     * Retrieve a List of all LabRequests from the database. Can be filtered
     * further once retrieved. Will return the LabRequests sorted by date.
     *
     * @return A List of all LabRequests saved in the database
     */
    @SuppressWarnings ( "unchecked" )
    public static List<LabRequest> getLabRequests () {
        final List<LabRequest> requests = (List<LabRequest>) getAll( LabRequest.class );
        requests.sort( new Comparator<LabRequest>() {
            @Override
            public int compare ( final LabRequest o1, final LabRequest o2 ) {
                return compareTo( o1, o2 );
            }
        } );
        return requests;
    }

    /**
     * Compares two lab requests based on their priority
     *
     * @param o1
     *            the first lab request to compare
     * @param o2
     *            the second lab request to compare
     * @return 1 if the first one is higher priority 0 if it is equal priority
     *         -1 if the first one is lower priority
     */
    private static int compareTo ( final LabRequest o1, final LabRequest o2 ) {
        if ( o1.getPriority().getPriority() < o2.getPriority().getPriority() ) {
            return -1;
        }
        else if ( o1.getPriority().getPriority() > o2.getPriority().getPriority() ) {
            return 1;
        }
        return 0;
    }

    /**
     * Used so that Hibernate can construct and load objects
     */
    public LabRequest () {
    }

    /**
     * Retrieve a List of Lab requests that meets the given where clause. Clause
     * is expected to be valid SQL.
     *
     * @param where
     *            List of Criterion to and together and search for records by
     * @return The matching list
     */
    @SuppressWarnings ( "unchecked" )
    private static List<LabRequest> getWhere ( final List<Criterion> where ) {
        return (List<LabRequest>) getWhere( LabRequest.class, where );
    }

    /**
     * Retrieves all LabRequests for the Patient provided.
     *
     * @param techName
     *            Name of the patient
     * @return All of their LabRequests
     */
    public static List<LabRequest> getLabRequestsForLabTech ( final String techName ) {
        return getWhere( createCriterionAsList( "labtech", User.getByNameAndRole( techName, Role.ROLE_LABTECH ) ) );
    }

    /**
     * Retrieves all lab requests for the HCP provided
     *
     * @param hcpName
     *            Name of the HCP
     * @return All LabRequests involving this HCP
     */
    public static List<LabRequest> getLabRequestsForHCP ( final String hcpName ) {
        return getWhere( createCriterionAsList( "hcp", User.getByNameAndRole( hcpName, Role.ROLE_HCP ) ) );
    }

    /**
     * Retrieves all LabRequests for the HCP _and_ Patient provided. This is the
     * intersection of the requests -- namely, only the ones where both the HCP
     * _and_ Patient are on the request.
     *
     * @param hcpName
     *            Name of the HCP
     * @param patientName
     *            Name of the Patient
     * @return The list of matching LabRequests
     */
    public static List<LabRequest> getLabRequestsForHCPAndPatient ( final String hcpName, final String patientName ) {
        final Vector<Criterion> criteria = new Vector<Criterion>();
        criteria.add( createCriterion( "hcp", User.getByNameAndRole( hcpName, Role.ROLE_HCP ) ) );
        criteria.add( createCriterion( "patient", User.getByNameAndRole( patientName, Role.ROLE_PATIENT ) ) );
        return getWhere( criteria );
    }

    /**
     * Handles conversion between an labrequestForm (the form with user-provided
     * data) and an LabRequests object that contains validated information These
     * two classes are closely intertwined to handle validated persistent
     * information and text-based information that is then displayed back to the
     * user.
     *
     * @param laf
     *            labrequestForm
     * @throws ParseException
     */
    public LabRequest ( final LabRequestForm laf ) throws ParseException {

        // Set users
        setPatient( User.getByNameAndRole( laf.getPatient(), Role.ROLE_PATIENT ) );
        setHcp( User.getByNameAndRole( laf.getHcp(), Role.ROLE_HCP ) );
        setLabTech( User.getByNameAndRole( laf.getLabTech(), Role.ROLE_LABTECH ) );
        setComments( laf.getComments() );

        // final SimpleDateFormat sdf = new SimpleDateFormat( "MM/dd/yyyy hh:mm
        // aaa", Locale.ENGLISH );
        // final Date parsedDate = sdf.parse( laf.getDate() + " " +
        // laf.getTime() );
        // final Calendar c = Calendar.getInstance();
        // c.setTime( parsedDate );
        // if ( c.before( Calendar.getInstance() ) ) {
        // throw new IllegalArgumentException( "Cannot request an appointment
        // before the current time" );
        // }
        // setDate( c );

        final Priority p = Priority.valueOf( laf.getPriority() );
        // try {
        // s = Priority.valueOf( laf.getPriority() );
        // }
        // catch ( final NullPointerException npe ) {
        // s = Priority.PRIORITY_LOW; /*
        // * Incoming LabRequests will come in from the
        // * form with no status. Set status to Pending
        // * until it is adjusted further
        // */
        // }
        setPriority( p );
        setLabProcedure( LabProcedure.getByCode( laf.getProc() ) );

    }

    /**
     * Sets the lab technician of the lab request
     *
     * @param labTech
     *            the lab tech
     */
    public void setLabTech ( final User labTech ) {
        this.labTech = labTech;
    }

    /**
     * ID of the labrequest
     */
    @Id
    @GeneratedValue ( strategy = GenerationType.AUTO )
    private Long id;

    /**
     * Retrieves the ID of the labrequest
     */
    @Override
    public Long getId () {
        return id;
    }

    /**
     * Sets the ID of the labrequest
     *
     * @param id
     *            The new ID of the labrequest. For Hibernate.
     */
    @SuppressWarnings ( "unused" )
    private void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * The Patient who is associated with this labrequest
     */
    @NotNull
    @ManyToOne
    @JoinColumn ( name = "patient_id", columnDefinition = "varchar(100)" )
    private User         patient;

    /**
     * The HCP who is associated with this labrequest
     */
    @NotNull
    @ManyToOne
    @JoinColumn ( name = "hcp_id", columnDefinition = "varchar(100)" )
    private User         hcp;

    /**
     * The lab Technician who is associated with this labrequest
     */
    @NotNull
    @ManyToOne
    @JoinColumn ( name = "labtech", columnDefinition = "varchar(100)" )
    private User         labTech;

    /**
     * When this labrequest has been scheduled to take place
     */
    @NotNull
    private Calendar     date;

    /**
     * Store the Enum in the DB as a string as it then makes the DB info more
     * legible if it needs to be read manually.
     */
    @NotNull
    @Enumerated ( EnumType.STRING )
    private LabProcedure proc;

    /**
     * Retrieve the priority of this LabRequest
     *
     * @return The priority of this LabRequest
     */
    public Priority getPriority () {
        return pri;
    }

    /**
     * Set the priority of this LabRequest
     *
     * @param pri
     *            New Status
     */
    public void setPriority ( final Priority pri ) {
        this.pri = pri;
    }

    /**
     * Any (optional) comments on the LabRequest
     */
    private String   comments;

    /**
     * The priority of the LabRequest
     */
    @NotNull
    @Enumerated ( EnumType.STRING )
    private Priority pri;

    /**
     * Retrieves the User object for the Patient for the LabRequest
     *
     * @return The associated Patient
     */
    public User getPatient () {
        return patient;
    }

    /**
     * Sets the Patient for the LabRequest
     *
     * @param patient
     *            The User object for the Patient on the Request
     */
    public void setPatient ( final User patient ) {
        this.patient = patient;
    }

    /**
     * Gets the User object for the HCP on the request
     *
     * @return The User object for the HCP on the request
     */
    public User getHcp () {
        return hcp;
    }

    /**
     * Sets the User object for the HCP on the LabRequest
     *
     * @param hcp
     *            User object for the HCP on the Request
     */
    public void setHcp ( final User hcp ) {
        this.hcp = hcp;
    }

    // /**
    // * Retrieves the date & time of the LabRequest
    // *
    // * @return Calendar for when the Request takes place
    // */
    // public Calendar getDate () {
    // return date;
    // }
    //
    // /**
    // * Sets the date & time of the LabRequest
    // *
    // * @param date
    // * Calendar object for the Date & Time of the request
    // */
    // public void setDate ( final Calendar date ) {
    // this.date = date;
    // }

    /**
     * Retrieves the comments on the request
     *
     * @return Comments on the LabRequest
     */
    public String getComments () {
        return comments;
    }

    /**
     * Sets the Comments on the LabRequest
     *
     * @param comments
     *            New comments for the LabRequest
     */
    public void setComments ( final String comments ) {
        this.comments = comments;
    }

    /**
     * Retrieves the LabProcedure of this LabRequest.
     *
     * @return the lab procedure.
     */
    public LabProcedure getLabProcedure () {
        return proc;
    }

    /**
     * Sets the LabProcedure of this LabRequest
     *
     * @param proc
     *            The new type for the LabRequest
     */
    public void setLabProcedure ( final LabProcedure proc ) {
        this.proc = proc;
    }

}