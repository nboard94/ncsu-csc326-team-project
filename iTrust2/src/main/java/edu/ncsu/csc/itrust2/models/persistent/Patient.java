package edu.ncsu.csc.itrust2.models.persistent;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.criterion.Criterion;
import org.hibernate.validator.constraints.Length;

import edu.ncsu.csc.itrust2.forms.hcp_patient.PatientForm;
import edu.ncsu.csc.itrust2.models.enums.BloodType;
import edu.ncsu.csc.itrust2.models.enums.Ethnicity;
import edu.ncsu.csc.itrust2.models.enums.Gender;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.enums.State;

/**
 * Class representing a Patient object. This goes beyond the basic information
 * stored as part of a User and contains relevant information for a patient in
 * our medical records system.
 *
 * @author Kai Presler-Marshall
 *
 */
@Entity
@Table ( name = "Patients" )
public class Patient extends DomainObject<Patient> implements Serializable {

    /**
     * Randomly generated ID.
     */
    private static final long serialVersionUID = 4617248041239679701L;

    @ManyToMany ( cascade = { CascadeType.MERGE }, fetch = FetchType.EAGER )
    @JoinTable ( name = "PERSONAL_REPRESENTATIVES", joinColumns = { @JoinColumn ( name = "WHOIREPRESENT" ) },
            inverseJoinColumns = { @JoinColumn ( name = "IAMREPRESENTEDBY" ) } )
    private Set<Patient>      representsMe     = new HashSet<Patient>();

    @ManyToMany ( mappedBy = "representsMe", fetch = FetchType.EAGER )
    private Set<Patient>      whoIRepresent    = new HashSet<Patient>();

    /**
     * Get all patients in the database
     *
     * @SuppressWarnings for Unchecked cast from List<capture#1-of ? extends
     *                   DomainObject> to List<Patient> Because get all just
     *                   returns a list of DomainObjects, the cast is okay.
     *
     * @return all patients in the database
     */
    @SuppressWarnings ( "unchecked" )
    public static List<Patient> getPatients () {
        return (List<Patient>) getAll( Patient.class );
    }

    /**
     * Get a specific patient by username
     *
     * @param username
     *            the username of the patient to get
     * @return the patient with the queried username
     */
    public static Patient getByName ( final String username ) {
        try {
            return getWhere( createCriterionAsList( "self", User.getByNameAndRole( username, Role.ROLE_PATIENT ) ) )
                    .get( 0 );
        }
        catch ( final Exception e ) {
            return null;
        }
    }

    /**
     * Helper method to pass to the DomainObject class that performs a specific
     * query on the database.
     *
     * @param where
     *            List of Criterion to and together and search for records by
     * @return The list of all patients found matching the Criterion provided
     */
    @SuppressWarnings ( "unchecked" )
    private static List<Patient> getWhere ( final List<Criterion> where ) {
        return (List<Patient>) getWhere( Patient.class, where );
    }

    /**
     * Get the patient representation of the specified user (not all users are
     * patients, aka an HCP is a user of the system, but not a patient)
     *
     * @param user
     *            the user to get the patient representation of
     * @return the corresponding patient record
     */
    public static Patient getPatient ( final User user ) {
        return getByName( user.getUsername() );
    }

    /**
     * Empty constructor necessary for Hibernate.
     */
    public Patient () {
    }

    /**
     * Create a new patient based off of a user record
     *
     * @param self
     *            the user record
     */
    public Patient ( final User self ) {
        setSelf( self );
    }

    /**
     * Create a new patient based of a user record (found via the username)
     *
     * @param self
     *            the username
     */
    public Patient ( final String self ) {
        this( User.getByNameAndRole( self, Role.ROLE_PATIENT ) );
    }

    /**
     * Create a new patient based off of the PatientForm
     *
     * @param form
     *            the filled-in patient form with patient information
     * @throws ParseException
     *             if there is an issue in parsing the date
     */
    public Patient ( final PatientForm form ) throws ParseException {
        this( form.getSelf() );
        setMother( User.getByNameAndRole( form.getMother(), Role.ROLE_PATIENT ) );
        setFather( User.getByNameAndRole( form.getFather(), Role.ROLE_PATIENT ) );
        setFirstName( form.getFirstName() );
        setPreferredName( form.getPreferredName() );
        setLastName( form.getLastName() );
        setEmail( form.getEmail() );
        setAddress1( form.getAddress1() );
        setAddress2( form.getAddress2() );
        setCity( form.getCity() );
        setState( State.parse( form.getState() ) );
        setZip( form.getZip() );
        setPhone( form.getPhone() );

        final SimpleDateFormat sdf = new SimpleDateFormat( "MM/dd/yyyy", Locale.ENGLISH );
        try {
            final Date parsedDate = sdf.parse( form.getDateOfBirth() );
            final Calendar c = Calendar.getInstance();
            c.setTime( parsedDate );
            setDateOfBirth( c );

        }
        catch ( final Exception e ) {
            throw new IllegalArgumentException( "Date of birth must be of the form mm/dd/yyyy" );
        }
        if ( null != form
                .getDateOfDeath() ) { /*
                                       * Patient can't set their date of death
                                       */
            try {
                final Date parsedDeathDate = sdf.parse( form.getDateOfDeath() );
                final Calendar deathC = Calendar.getInstance();
                deathC.setTime( parsedDeathDate );
                setDateOfDeath( deathC );
                setCauseOfDeath( form.getCauseOfDeath() );
            }
            catch ( final Exception e ) {
                throw new IllegalArgumentException( "Date of death must be of the form mm/dd/yyyy" );
            }
        }

        setBloodType( BloodType.parse( form.getBloodType() ) );

        setEthnicity( Ethnicity.parse( form.getEthnicity() ) );

        setGender( Gender.parse( form.getGender() ) );

        setId( form.getId() );
    }

    /**
     * This stores a reference to the User object that this patient is.
     * Mandatory.
     */
    @OneToOne
    @JoinColumn ( name = "self_id", columnDefinition = "varchar(100)" )
    @Id
    private User      self;

    /**
     * For keeping track of the User who is the mother of this patient.
     * Optional.
     */
    @ManyToOne
    @JoinColumn ( name = "mother_id", columnDefinition = "varchar(100)" )
    private User      mother;

    /**
     * For keeping track of the User who is the father of this patient.
     * Optional.
     */
    @ManyToOne
    @JoinColumn ( name = "father_id", columnDefinition = "varchar(100)" )
    private User      father;

    /**
     * The first name of this patient
     */
    @Length ( max = 20 )
    private String    firstName;

    /**
     * The preferred name of this patient
     */
    @Length ( max = 20 )
    private String    preferredName;

    /**
     * The last name of this patient
     */
    @Length ( max = 30 )
    private String    lastName;

    /**
     * The email address of this patient
     */
    @Length ( max = 30 )
    private String    email;

    /**
     * The address line 1 of this patient
     */
    @Length ( max = 50 )
    private String    address1;

    /**
     * The address line 2 of this patient
     */
    @Length ( max = 50 )
    private String    address2;

    /**
     * The city of residence of this patient
     */
    @Length ( max = 15 )
    private String    city;

    /**
     * The state of residence of this patient
     */
    @Enumerated ( EnumType.STRING )
    private State     state;

    /**
     * The zip code of this patient
     */
    @Length ( min = 5, max = 10 )
    private String    zip;

    /**
     * The phone number of this patient
     */
    @Length ( min = 12, max = 12 )
    private String    phone;

    /**
     * The birthday of this patient
     */
    private Calendar  dateOfBirth;

    /**
     * The date of death of this patient
     */
    private Calendar  dateOfDeath;

    /**
     * The cause of death of this patient
     */
    private String    causeOfDeath;

    /**
     * The blood type of this patient
     */
    @Enumerated ( EnumType.STRING )
    private BloodType bloodType;

    /**
     * The ethnicity of this patient
     */
    @Enumerated ( EnumType.STRING )
    private Ethnicity ethnicity;

    /**
     * The gender of this patient
     */
    @Enumerated ( EnumType.STRING )
    private Gender    gender;

    /**
     * The id of this patient
     */
    @GeneratedValue ( strategy = GenerationType.AUTO )
    private Long      id;

    /**
     * Set the id of this patient
     *
     * @param id
     *            the id to set this patient to
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Get the id of this patient
     *
     * @return the id of this patient
     */
    @Override
    public Long getId () {
        return this.id;
    }

    /**
     * Get the user representation of this patient
     *
     * @return the user representation of this patient
     */
    public User getSelf () {
        return self;
    }

    /**
     * Set the user representation of this patient
     *
     * @param self
     *            representation the user representation to set this patient to
     */
    public void setSelf ( final User self ) {
        this.self = self;
    }

    /**
     * Get the mother of this patient
     *
     * @return the mother of this patient
     */
    public User getMother () {
        return mother;
    }

    /**
     * Set the mother of this patient
     *
     * @param mother
     *            the mother to set this patient to
     */
    public void setMother ( final User mother ) {
        this.mother = mother;
    }

    /**
     * Get the father of this patient
     *
     * @return the father of this patient
     */
    public User getFather () {
        return father;
    }

    /**
     * Set the father of this patient
     *
     * @param father
     *            the father to set this patient to
     */
    public void setFather ( final User father ) {
        this.father = father;
    }

    /**
     * Get the first name of this patient
     *
     * @return the first name of this patient
     */
    public String getFirstName () {
        return firstName;
    }

    /**
     * Set the first name of this patient
     *
     * @param firstName
     *            the first name to set this patient to
     */
    public void setFirstName ( final String firstName ) {
        if ( firstName == null || firstName.length() > 20 || !firstName.matches( "[a-zA-Z\\d' -]+" ) ) {
            throw new IllegalArgumentException(
                    "First name must contain 1-20 characters (alphanumeric, -, ', or space)" );
        }
        this.firstName = firstName;
    }

    /**
     * Get the preferred name of this patient
     *
     * @return the preferred name of this patient
     */
    public String getPreferredName () {
        return preferredName;
    }

    /**
     * Set the preferred name of this patient
     *
     * @param preferredName
     *            the preferred name to set this patient to
     */
    public void setPreferredName ( final String preferredName ) {
        // preferred name is optional
        if ( preferredName != null && ( preferredName.length() > 30 || !preferredName.matches( "[a-zA-Z\\d' -]*" ) ) ) {
            throw new IllegalArgumentException(
                    "Preferred name can contain no more than 30 characters (alphanumeric, -, ', or space)" );
        }
        this.preferredName = preferredName;
    }

    /**
     * Get the last name of this patient
     *
     * @return the last name of this patient
     */
    public String getLastName () {
        return lastName;
    }

    /**
     * Set the last name of this patient
     *
     * @param lastName
     *            the last name to set this patient to
     */
    public void setLastName ( final String lastName ) {
        if ( lastName == null || lastName.length() > 30 || !lastName.matches( "[a-zA-Z\\d' -]+" ) ) {
            throw new IllegalArgumentException(
                    "Last name must contain 1-30 characters (alphanumeric, -, ', or space)" );
        }
        this.lastName = lastName;
    }

    /**
     * Get the email of this patient
     *
     * @return the email of this patient
     */
    public String getEmail () {
        return email;
    }

    /**
     * Set the email of this patient
     *
     * @param email
     *            the email to set this patient to
     */
    public void setEmail ( final String email ) {
        if ( lastName == null || lastName.length() > 30 || !lastName.matches( "[\\w\\.@]+" ) ) {
            throw new IllegalArgumentException( "Email must contain 1-30 characters (alphanumeric, ., _, or @)" );
        }
        this.email = email;
    }

    /**
     * Get the address line 1 of this patient
     *
     * @return the address line 1 of this patient
     */
    public String getAddress1 () {
        return address1;
    }

    /**
     * Set the address line 1 of this patient
     *
     * @param address1
     *            the address line 1 to set this patient to
     */
    public void setAddress1 ( final String address1 ) {
        if ( address1 == null || address1.length() > 50 || !address1.matches( "[a-zA-Z\\d\\. ]+" ) ) {
            throw new IllegalArgumentException(
                    "Address line 1 must contain 1-50 characters (alphanumeric, ., or space)" );
        }
        this.address1 = address1;
    }

    /**
     * Get the address line 2 of this patient
     *
     * @return the address line 2 of this patient
     */
    public String getAddress2 () {
        return address2;
    }

    /**
     * Set the address line 2 of this patient
     *
     * @param address2
     *            the address line 2 to set this patient to
     */
    public void setAddress2 ( final String address2 ) {
        // optional
        if ( address2 != null && ( address2.length() > 50 || !address2.matches( "[a-zA-Z\\d\\. ]*" ) ) ) {
            throw new IllegalArgumentException(
                    "Address line 2 can contain no more than 50 characters (alphanumeric, ., or space)" );
        }
        this.address2 = address2;
    }

    /**
     * Get the city of residence of this patient
     *
     * @return the city of residence of this patient
     */
    public String getCity () {
        return city;
    }

    /**
     * Set the city of residence of this patient
     *
     * @param city
     *            the city of residence to set this patient to
     */
    public void setCity ( final String city ) {
        if ( city == null || city.length() > 15 || !city.matches( "[a-zA-Z]+" ) ) {
            throw new IllegalArgumentException( "City must contain 1-15 alpha characters" );
        }
        this.city = city;
    }

    /**
     * Get the state of residence of this patient
     *
     * @return the state of residence of this patient
     */
    public State getState () {
        return state;
    }

    /**
     * Set the state of residence of this patient
     *
     * @param state
     *            the state of residence to set this patient to
     */
    public void setState ( final State state ) {
        this.state = state;
    }

    /**
     * Get the zipcode of this patient
     *
     * @return the zipcode of this patient
     */
    public String getZip () {
        return zip;
    }

    /**
     * Set the zipcode of this patient
     *
     * @param zip
     *            the zipcode to set this patient to
     */
    public void setZip ( final String zip ) {
        if ( zip == null || !zip.matches( "\\d\\d\\d\\d\\d(-\\d\\d\\d\\d)?" ) ) {
            throw new IllegalArgumentException( "Only 5 or 9 digit zipcode allowed" );
        }
        this.zip = zip;
    }

    /**
     * Get the phone number of this patient
     *
     * @return the phone number of this patient
     */
    public String getPhone () {
        return phone;
    }

    /**
     * Set the phone number of this patient
     *
     * @param phone
     *            the phone number to set this patient to
     */
    public void setPhone ( final String phone ) {
        if ( phone == null || !phone.matches( "\\d\\d\\d-\\d\\d\\d-\\d\\d\\d\\d" ) ) {
            throw new IllegalArgumentException( "Phone number must be of the form XXX-XXX-XXXX (digits only)" );
        }
        this.phone = phone;
    }

    /**
     * Get the date of birth of this patient
     *
     * @return the date of birth of this patient
     */
    public Calendar getDateOfBirth () {
        return dateOfBirth;
    }

    /**
     * Set the date of birth of this patient
     *
     * @param dateOfBirth
     *            the date of birth to set this patient to
     */
    public void setDateOfBirth ( final Calendar dateOfBirth ) {
        this.dateOfBirth = dateOfBirth;
    }

    /**
     * Get the date of death of this patient
     *
     * @return the date of death of this patient
     */
    public Calendar getDateOfDeath () {
        return dateOfDeath;
    }

    /**
     * Set the date of death of this patient
     *
     * @param dateOfDeath
     *            the date of death to set this patient to
     */
    public void setDateOfDeath ( final Calendar dateOfDeath ) {
        this.dateOfDeath = dateOfDeath;
    }

    /**
     * Get the cause of death of this patient
     *
     * @return the cause of death of this patient
     */
    public String getCauseOfDeath () {
        return causeOfDeath;
    }

    /**
     * Set the cause of death of this patient
     *
     * @param causeOfDeath
     *            the cause of death to set this patient to
     */
    public void setCauseOfDeath ( final String causeOfDeath ) {
        this.causeOfDeath = causeOfDeath;
    }

    /**
     * Get the blood type of this patient
     *
     * @return the blood type of this patient
     */
    public BloodType getBloodType () {
        return bloodType;
    }

    /**
     * Set the blood type of this patient
     *
     * @param bloodType
     *            the blood type to set this patient to
     */
    public void setBloodType ( final BloodType bloodType ) {
        this.bloodType = bloodType;
    }

    /**
     * Get the ethnicity of this patient
     *
     * @return the ethnicity of this patient
     */
    public Ethnicity getEthnicity () {
        return ethnicity;
    }

    /**
     * Set the ethnicity of this patient
     *
     * @param ethnicity
     *            the ethnicity to set this patient to
     */
    public void setEthnicity ( final Ethnicity ethnicity ) {
        this.ethnicity = ethnicity;
    }

    /**
     * Get the gender of this patient
     *
     * @return the gender of this patient
     */
    public Gender getGender () {
        return gender;
    }

    /**
     * Set the gender of this patient
     *
     * @param gender
     *            the gender to set this patient to
     */
    public void setGender ( final Gender gender ) {
        this.gender = gender;
    }

    /**
     * Get the set of people who represent the patient.
     *
     * @return the set of patients.
     */
    public Set<Patient> getMyRepresentatives () {
        return representsMe;
    }

    /**
     * Set the set of patients who are my representatives
     *
     * @param representsMe
     *            the set of patients
     */
    public void setMyRepresentatives ( final Set<Patient> representsMe ) {
        this.representsMe = representsMe;
    }

    /**
     * Get the set of people the patient represents
     *
     * @return the set of patients
     */
    public Set<Patient> getRepresentedByMe () {
        return whoIRepresent;
    }

    /**
     * Set the set of patient who I represent
     *
     * @param representedByMe
     *            the set of patients
     */
    public void setRepresentedByMe ( final Set<Patient> representedByMe ) {
        this.whoIRepresent = representedByMe;
    }

    /**
     * Adds a representative to the patients list of reps
     *
     * @param rep
     *            the patient that is to be added to the patients list of reps
     */
    public void addRepresentative ( final Patient rep ) {
        this.representsMe.add( rep );
    }

    /**
     * Removes a representative to the patients list of reps
     *
     * @param rep
     *            the patient that is to be removed to the patients list of reps
     * @return whether the remove was successful or not
     */
    public boolean removeRepresentative ( final Patient rep ) {
        return representsMe.remove( rep );
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode () {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( address1 == null ) ? 0 : address1.hashCode() );
        result = prime * result + ( ( address2 == null ) ? 0 : address2.hashCode() );
        result = prime * result + ( ( bloodType == null ) ? 0 : bloodType.hashCode() );
        result = prime * result + ( ( causeOfDeath == null ) ? 0 : causeOfDeath.hashCode() );
        result = prime * result + ( ( city == null ) ? 0 : city.hashCode() );
        result = prime * result + ( ( dateOfBirth == null ) ? 0 : dateOfBirth.hashCode() );
        result = prime * result + ( ( dateOfDeath == null ) ? 0 : dateOfDeath.hashCode() );
        result = prime * result + ( ( email == null ) ? 0 : email.hashCode() );
        result = prime * result + ( ( ethnicity == null ) ? 0 : ethnicity.hashCode() );
        result = prime * result + ( ( father == null ) ? 0 : father.hashCode() );
        result = prime * result + ( ( firstName == null ) ? 0 : firstName.hashCode() );
        result = prime * result + ( ( gender == null ) ? 0 : gender.hashCode() );
        result = prime * result + ( ( id == null ) ? 0 : id.hashCode() );
        result = prime * result + ( ( lastName == null ) ? 0 : lastName.hashCode() );
        result = prime * result + ( ( mother == null ) ? 0 : mother.hashCode() );
        result = prime * result + ( ( phone == null ) ? 0 : phone.hashCode() );
        result = prime * result + ( ( preferredName == null ) ? 0 : preferredName.hashCode() );
        result = prime * result + ( ( self == null ) ? 0 : self.hashCode() );
        result = prime * result + ( ( state == null ) ? 0 : state.hashCode() );
        result = prime * result + ( ( zip == null ) ? 0 : zip.hashCode() );
        return result;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals ( final Object obj ) {
        if ( this == obj ) {
            return true;
        }
        if ( obj == null ) {
            return false;
        }
        if ( getClass() != obj.getClass() ) {
            return false;
        }
        final Patient other = (Patient) obj;
        if ( address1 == null ) {
            if ( other.address1 != null ) {
                return false;
            }
        }
        else if ( !address1.equals( other.address1 ) ) {
            return false;
        }
        if ( address2 == null ) {
            if ( other.address2 != null ) {
                return false;
            }
        }
        else if ( !address2.equals( other.address2 ) ) {
            return false;
        }
        if ( bloodType != other.bloodType ) {
            return false;
        }
        if ( causeOfDeath == null ) {
            if ( other.causeOfDeath != null ) {
                return false;
            }
        }
        else if ( !causeOfDeath.equals( other.causeOfDeath ) ) {
            return false;
        }
        if ( city == null ) {
            if ( other.city != null ) {
                return false;
            }
        }
        else if ( !city.equals( other.city ) ) {
            return false;
        }
        if ( dateOfBirth == null ) {
            if ( other.dateOfBirth != null ) {
                return false;
            }
        }
        else if ( !dateOfBirth.equals( other.dateOfBirth ) ) {
            return false;
        }
        if ( dateOfDeath == null ) {
            if ( other.dateOfDeath != null ) {
                return false;
            }
        }
        else if ( !dateOfDeath.equals( other.dateOfDeath ) ) {
            return false;
        }
        if ( email == null ) {
            if ( other.email != null ) {
                return false;
            }
        }
        else if ( !email.equals( other.email ) ) {
            return false;
        }
        if ( ethnicity != other.ethnicity ) {
            return false;
        }
        if ( father == null ) {
            if ( other.father != null ) {
                return false;
            }
        }
        else if ( !father.equals( other.father ) ) {
            return false;
        }
        if ( firstName == null ) {
            if ( other.firstName != null ) {
                return false;
            }
        }
        else if ( !firstName.equals( other.firstName ) ) {
            return false;
        }
        if ( gender != other.gender ) {
            return false;
        }
        if ( id == null ) {
            if ( other.id != null ) {
                return false;
            }
        }
        else if ( !id.equals( other.id ) ) {
            return false;
        }
        if ( lastName == null ) {
            if ( other.lastName != null ) {
                return false;
            }
        }
        else if ( !lastName.equals( other.lastName ) ) {
            return false;
        }
        if ( mother == null ) {
            if ( other.mother != null ) {
                return false;
            }
        }
        else if ( !mother.equals( other.mother ) ) {
            return false;
        }
        if ( phone == null ) {
            if ( other.phone != null ) {
                return false;
            }
        }
        else if ( !phone.equals( other.phone ) ) {
            return false;
        }
        if ( preferredName == null ) {
            if ( other.preferredName != null ) {
                return false;
            }
        }
        else if ( !preferredName.equals( other.preferredName ) ) {
            return false;
        }
        if ( self == null ) {
            if ( other.self != null ) {
                return false;
            }
        }
        else if ( !self.equals( other.self ) ) {
            return false;
        }
        if ( state != other.state ) {
            return false;
        }
        if ( zip == null ) {
            if ( other.zip != null ) {
                return false;
            }
        }
        else if ( !zip.equals( other.zip ) ) {
            return false;
        }
        return true;
    }

}
