import java.io.Serializable;
import java.util.GregorianCalendar;

public class SS_DM_OCCCDate implements Serializable {
    private static final long serialVersionUID = 1L;

    private int dayOfMonth;
    private int monthOfYear;
    private int year;

    public SS_DM_OCCCDate(int day, int month, int year) throws SS_DM_OCCCDateException {
        // Use non-lenient calendar to detect invalid dates
        GregorianCalendar cal = new GregorianCalendar();
        cal.setLenient(false);
        try {
            cal.set(year, month - 1, day);
            // Force computation to trigger any exception
            cal.getTime();
        } catch (Exception e) {
            throw new SS_DM_OCCCDateException("Invalid date: " + month + "/" + day + "/" + year);
        }

        // Extra check: make sure what we get back matches what we put in
        if (cal.get(GregorianCalendar.DAY_OF_MONTH) != day ||
            cal.get(GregorianCalendar.MONTH) != month - 1 ||
            cal.get(GregorianCalendar.YEAR) != year) {
            throw new SS_DM_OCCCDateException("Invalid date: " + month + "/" + day + "/" + year);
        }

        this.dayOfMonth  = day;
        this.monthOfYear = month;
        this.year        = year;
    }

    public int getDayOfMonth()  { return dayOfMonth; }
    public int getMonthOfYear() { return monthOfYear; }
    public int getYear()        { return year; }

    /** Returns the maximum valid day for a given month/year (handles leap years). */
    public static int daysInMonth(int month, int year) {
        GregorianCalendar cal = new GregorianCalendar(year, month - 1, 1);
        return cal.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
    }

    @Override
    public String toString() {
        return String.format("%02d/%02d/%04d", monthOfYear, dayOfMonth, year);
    }
}
