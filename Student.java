/*
 * Project: University Data Warehouse
 * Author:  Andres Zubizarreta
 */

import java.io.PrintWriter;

/**
 * A student. Stores the StudentID and the full 10-digit phone number.
 * The TeacherID given in the input is ignored.
 */
public class Student implements CSVPrintable {

    private final String firstName;
    private final String lastName;
    private final int studentID;
    private final long phone; // full 10-digit phone number

    public Student(String firstName, String lastName, int studentID, long phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.studentID = studentID;
        this.phone = phone;
    }

    @Override
    public String getName() {
        return firstName + " " + lastName;
    }

    /** Returns the StudentID (the TeacherID is ignored). */
    @Override
    public int getID() {
        return studentID;
    }

    public long getPhone() {
        return phone;
    }

    /**
     * Writes getName() + "," + getID() + "," + phone. The ID and phone are
     * zero-padded so leading zeros (e.g. ID 01234) are not lost.
     */
    @Override
    public void csvPrintln(PrintWriter out) {
        out.println(getName() + "," + String.format("%05d", getID()) + "," + String.format("%010d", phone));
    }
}
