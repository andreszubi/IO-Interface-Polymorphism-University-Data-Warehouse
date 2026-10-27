/*
 * Project: University Data Warehouse
 * Author:  Andres Zubizarreta
 */

import java.io.PrintWriter;

/**
 * Anything that can be written as one row of a comma-separated values (.csv) file.
 */
public interface CSVPrintable {

    /** Returns the person's name as "First Last". */
    String getName();

    /** Returns the ID that is written to the .csv file. */
    int getID();

    /** Writes getName() + "," + getID() + "," + phone followed by a new line. */
    void csvPrintln(PrintWriter out);
}
