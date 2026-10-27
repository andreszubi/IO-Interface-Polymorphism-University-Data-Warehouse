/*
 * Project: University Data Warehouse
 * Author:  Andres Zubizarreta
 */

import java.io.PrintWriter;

/**
 * A teaching assistant: a student who also has a TeacherID.
 */
public class TA extends Student {

    private final int teacherID;

    public TA(String firstName, String lastName, int studentID, int teacherID, long phone) {
        super(firstName, lastName, studentID, phone);
        this.teacherID = teacherID;
    }

    /** Returns the larger of the StudentID and the TeacherID. */
    @Override
    public int getID() {
        return Math.max(super.getID(), teacherID);
    }

    /** Writes getName() + "," + getID() + "," + phone, using the TA's ID. */
    @Override
    public void csvPrintln(PrintWriter out) {
        out.println(getName() + "," + String.format("%05d", getID()) + "," + String.format("%010d", getPhone()));
    }
}
