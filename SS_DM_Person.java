import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SS_DM_Person implements Serializable {
    private static final long serialVersionUID = 1L;

    private String firstName;
    private String lastName;
    private List<SS_DM_Person> children;

    public SS_DM_Person(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName  = lastName;
        this.children  = new ArrayList<>();
    }

    public String getFirstName() { return firstName; }
    public void   setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName()  { return lastName; }
    public void   setLastName(String lastName) { this.lastName = lastName; }

    public List<SS_DM_Person> getChildren() { return children; }
    public void addChild(SS_DM_Person child) { children.add(child); }
    public void removeChild(SS_DM_Person child) { children.remove(child); }

    /** Human-readable type label used in the GUI list. */
    public String getTypeLabel() { return "Person"; }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }

    /** Full detail string shown in the info panel. */
    public String toDetailString() {
        return "[Person]  " + firstName + " " + lastName;
    }
}
