/*
 * Project: University Data Warehouse
 * Author:  Andres Zubizarreta
 */

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Reads the personal information of students, teachers and TAs from the console
 * and stores it in out.csv, one person per row.
 *
 * Input format:
 *   n
 *   Position Name StudentID TeacherID Phone   (n lines)
 *
 * Any line entered in the wrong format is reported with the reason, and the user
 * is asked to re-enter just that line.
 */
public class Main {

    private static final String OUTPUT_FILE = "out.csv";

    // A word is letters only, optionally joined by a hyphen or apostrophe (e.g. Smith-Jones, O'Neil).
    private static final String WORD = "[A-Za-z]+(?:['-][A-Za-z]+)*";
    private static final Pattern NAME = Pattern.compile(WORD + "," + WORD);
    private static final Pattern ID = Pattern.compile("0|[0-9]{5}");
    private static final Pattern PHONE = Pattern.compile("[0-9]{10}");

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        System.out.println("Enter the number of people, followed by one line per person:");
        System.out.println("  Position Name StudentID TeacherID Phone");
        System.out.println("  (example: Teacher Alex,Martinez 0 98765 3053489999)");

        List<CSVPrintable> people = new ArrayList<CSVPrintable>();
        try {
            int n = readCount(in);
            for (int i = 1; i <= n; i++) {
                people.add(readPerson(in, i, n));
            }
        } catch (NoSuchElementException e) {
            System.out.println("Error: the input ended before all of the information was entered. "
                    + OUTPUT_FILE + " was not written.");
            System.exit(1);
        }

        writeCsv(people);
    }

    /** Returns the next line of input that is not blank. Throws NoSuchElementException at end of input. */
    private static String nextNonBlankLine(Scanner in) {
        String line;
        do {
            line = in.nextLine().trim();
        } while (line.isEmpty());
        return line;
    }

    /** Reads the first line (the number of people), asking again until it is a positive whole number. */
    private static int readCount(Scanner in) {
        while (true) {
            String line = nextNonBlankLine(in);
            try {
                int n = Integer.parseInt(line);
                if (n > 0) {
                    return n;
                }
                System.out.println("Error: the number of people must be at least 1, but was " + n + ".");
            } catch (NumberFormatException e) {
                System.out.println("Error: the first line must contain only a whole number (the number of people), "
                        + "but was \"" + line + "\".");
            }
            System.out.println("Please re-enter the number of people:");
        }
    }

    /** Reads person number lineNumber, asking the user to re-enter the line until it is valid. */
    private static CSVPrintable readPerson(Scanner in, int lineNumber, int total) {
        while (true) {
            String line = nextNonBlankLine(in);
            List<String> errors = new ArrayList<String>();
            CSVPrintable person = parsePerson(line, errors);
            if (person != null) {
                return person;
            }
            System.out.println("Error in person " + lineNumber + " of " + total + ": \"" + line + "\"");
            for (String error : errors) {
                System.out.println("  - " + error);
            }
            System.out.println("Please re-enter person " + lineNumber
                    + " as: Position Name StudentID TeacherID Phone");
        }
    }

    /**
     * Validates one input line and builds the matching Student, Teacher or TA.
     * Returns null and adds every problem found to errors if the line is invalid.
     */
    static CSVPrintable parsePerson(String line, List<String> errors) {
        String[] fields = line.trim().split("\\s+");
        if (fields.length != 5) {
            errors.add("Expected 5 values separated by spaces (Position Name StudentID TeacherID Phone), "
                    + "but found " + fields.length + ".");
            if (line.contains(", ") || line.contains(" ,")) {
                errors.add("The name must not contain spaces; write it as First,Last (e.g. Alex,Martinez).");
            }
            return null;
        }

        String position = fields[0];
        String name = fields[1];
        String studentIdText = fields[2];
        String teacherIdText = fields[3];
        String phoneText = fields[4];

        boolean isStudent = position.equalsIgnoreCase("student");
        boolean isTeacher = position.equalsIgnoreCase("teacher");
        boolean isTA = position.equalsIgnoreCase("TA");
        if (!isStudent && !isTeacher && !isTA) {
            errors.add("Position must be \"student\", \"teacher\", or \"TA\", but was \"" + position + "\".");
        }

        checkName(name, errors);

        boolean studentIdValid = checkId("StudentID", studentIdText, errors);
        boolean teacherIdValid = checkId("TeacherID", teacherIdText, errors);

        if (!PHONE.matcher(phoneText).matches()) {
            errors.add("Phone must be exactly 10 digits, but was \"" + phoneText + "\".");
        }

        // The zero / non-zero rules can only be checked once both IDs are well formed.
        int studentId = studentIdValid ? Integer.parseInt(studentIdText) : -1;
        int teacherId = teacherIdValid ? Integer.parseInt(teacherIdText) : -1;
        if (studentIdValid && teacherIdValid) {
            if (isTeacher) {
                if (studentId != 0) {
                    errors.add("A teacher's StudentID must be 0, but was " + studentIdText + ".");
                }
                if (teacherId == 0) {
                    errors.add("A teacher's TeacherID must be a 5-digit number other than 0.");
                }
            } else if (isStudent) {
                if (teacherId != 0) {
                    errors.add("A student's TeacherID must be 0, but was " + teacherIdText + ".");
                }
                if (studentId == 0) {
                    errors.add("A student's StudentID must be a 5-digit number other than 0.");
                }
            } else if (isTA) {
                if (studentId == 0) {
                    errors.add("A TA's StudentID must be a 5-digit number other than 0.");
                }
                if (teacherId == 0) {
                    errors.add("A TA's TeacherID must be a 5-digit number other than 0.");
                }
            }
        }

        if (!errors.isEmpty()) {
            return null;
        }

        String[] names = name.split(",");
        long phone = Long.parseLong(phoneText);
        if (isTeacher) {
            return new Teacher(names[0], names[1], teacherId, phone);
        } else if (isTA) {
            return new TA(names[0], names[1], studentId, teacherId, phone);
        } else {
            return new Student(names[0], names[1], studentId, phone);
        }
    }

    /** Adds an error if name is not two words separated by a single comma. */
    private static void checkName(String name, List<String> errors) {
        int commas = name.length() - name.replace(",", "").length();
        if (commas != 1) {
            errors.add("Name must be two words separated by a single comma (e.g. Alex,Martinez), "
                    + "but \"" + name + "\" has " + commas + " commas.");
        } else if (!NAME.matcher(name).matches()) {
            errors.add("Name must have one word of letters on each side of the comma (e.g. Alex,Martinez), "
                    + "but was \"" + name + "\".");
        }
    }

    /** Adds an error and returns false if the ID is not "0" or exactly 5 digits. */
    private static boolean checkId(String label, String id, List<String> errors) {
        if (ID.matcher(id).matches()) {
            return true;
        }
        errors.add(label + " must be 0 or exactly 5 digits, but was \"" + id + "\".");
        return false;
    }

    /** Writes every person to out.csv by calling their csvPrintln method. */
    private static void writeCsv(List<CSVPrintable> people) {
        File file = new File(OUTPUT_FILE);
        try (PrintWriter out = new PrintWriter(new FileWriter(file))) {
            for (CSVPrintable person : people) {
                person.csvPrintln(out);
            }
            if (out.checkError()) {
                throw new IOException("an error occurred while writing to the file");
            }
        } catch (IOException e) {
            System.out.println("Error: could not write " + OUTPUT_FILE + " (" + e.getMessage() + ").");
            System.out.println("If the file is open in another program such as Excel, close it and run the program again.");
            System.exit(1);
        }
        System.out.println("Saved " + people.size() + " row(s) to " + file.getAbsolutePath());
    }
}
