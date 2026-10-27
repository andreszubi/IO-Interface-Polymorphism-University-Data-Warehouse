# University Data Warehouse

A Java command-line tool that collects personal records for a university's students, teachers and teaching assistants, validates every field, and exports them to a CSV file (`out.csv`) that opens as a table in Excel or any spreadsheet app.

It is built around file I/O, interfaces and polymorphism. Every record type implements a common `CSVPrintable` interface, and the exporter writes each row by calling `csvPrintln` through that interface, without needing to know whether the record is a student, a teacher or a TA.

## Design

| Type | Role |
|---|---|
| `CSVPrintable` | Interface with `getName()`, `getID()` and `csvPrintln(PrintWriter)` |
| `Student` | Stores the StudentID and the full 10-digit phone number (`long`) |
| `Teacher` | Stores the TeacherID and only the last 4 digits of the phone number (`int`) |
| `TA` | Extends `Student`; its ID is the larger of its StudentID and TeacherID |
| `Main` | Reads and validates console input, then writes `out.csv` |

## Build and run

Requires Java 8 or newer.

```bash
javac *.java
java Main
```

Records can also be piped in from a file:

```bash
java Main < records.txt
```

## Input format

The first line is the number of records, followed by one line per record:

```
Position Name StudentID TeacherID Phone
```

- **Position**: `student`, `teacher` or `TA` (case-insensitive)
- **Name**: two words joined by a single comma, no spaces (e.g. `Alex,Martinez`)
- **StudentID / TeacherID**: `0` or exactly 5 digits
  - teacher: StudentID is 0, TeacherID is not
  - student: TeacherID is 0, StudentID is not
  - TA: neither is 0
- **Phone**: exactly 10 digits

Any invalid line is rejected with the reason it failed, and only that line has to be re-entered.

## Example

Input:

```
3
Teacher Alex,Martinez 0 98765 3053489999
Student Rose,Gonzales 56789 0 9876543210
TA John,Cruz 88888 99999 1234567890
```

`out.csv`:

```
Alex Martinez,98765,9999
Rose Gonzales,56789,9876543210
John Cruz,99999,1234567890
```

## Author

Andres Zubizarreta
