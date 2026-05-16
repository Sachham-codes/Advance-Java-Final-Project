import java.io.Serializable;

public class SS_DM_OCCCPerson extends SS_DM_Person implements Serializable {
    private static final long serialVersionUID = 1L;

    private SS_DM_OCCCDate dateOfBirth;

    public SS_DM_OCCCPerson(String firstName, String lastName, SS_DM_OCCCDate dateOfBirth) {
        super(firstName, lastName);
        this.dateOfBirth = dateOfBirth;
    }

    public SS_DM_OCCCDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(SS_DM_OCCCDate dob) { this.dateOfBirth = dob; }

    @Override
    public String getTypeLabel() { return "OCCCPerson"; }

    @Override
    public String toString() {
        return super.toString() + " (DOB: " + dateOfBirth + ")";
    }

    @Override
    public String toDetailString() {
        return "[OCCCPerson]  " + getFirstName() + " " + getLastName()
             + "   |   DOB: " + dateOfBirth;
    }
}
