/*
 * Project: University Data Warehouse
 * Author:  Andres Zubizarreta
 */

import java.io.PrintWriter;

/**
 * A teacher. Stores the TeacherID and only the last 4 digits of the phone number.
 * The StudentID given in the input is ignored.
 */
public class Teacher implements CSVPrintable {

    private final String firstName;
    private final String lastName;
    private final int teacherID;
    private final int phone; // 4-digit postfix of the phone number

    /**
     * @param fullPhone the full 10-digit phone number; only its last 4 digits are kept
     */
    public Teacher(String firstName, String lastName, int teacherID, long fullPhone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.teacherID = teacherID;
        this.phone = (int) (fullPhone % 10000);
    }

    @Override
    public String getName() {
        return firstName + " " + lastName;
    }

    /** Returns the TeacherID (the StudentID is ignored). */
    @Override
    public int getID() {
        return teacherID;
    }

    public int getPhone() {
        return phone;
    }

    /**
     * Writes getName() + "," + getID() + "," + phone. The ID and phone are
     * zero-padded so leading zeros (e.g. a postfix of 0042) are not lost.
     */
    @Override
    public void csvPrintln(PrintWriter out) {
        out.println(getName() + "," + String.format("%05d", getID()) + "," + String.format("%04d", phone));
    }
}
