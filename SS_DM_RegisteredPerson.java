import java.io.Serializable;

public class SS_DM_RegisteredPerson extends SS_DM_Person implements Serializable {
    private static final long serialVersionUID = 1L;

    private String governmentID;

    public SS_DM_RegisteredPerson(String firstName, String lastName, String governmentID) {
        super(firstName, lastName);
        this.governmentID = governmentID;
    }

    public String getGovernmentID() { return governmentID; }
    public void   setGovernmentID(String governmentID) { this.governmentID = governmentID; }

    @Override
    public String getTypeLabel() { return "RegisteredPerson"; }

    @Override
    public String toString() {
        return super.toString() + " (ID: " + governmentID + ")";
    }

    @Override
    public String toDetailString() {
        return "[RegisteredPerson]  " + getFirstName() + " " + getLastName()
             + "   |   Gov't ID: " + governmentID;
    }
}
