// Student: Boniface Chikomo | Student Number: 202510809
import java.io.*;
import java.util.*;
import java.util.logging.*;

public class MarksImporter {
    private static final Logger LOGGER = Logger.getLogger(MarksImporter.class.getName());

    // Custom unchecked exception for duplicate student numbers.
    static class DuplicateStudentException extends RuntimeException {
        public DuplicateStudentException(String studentId) {
            super("Duplicate student number: " + studentId);
        }
    }

    static void importMarks(String filename) throws IOException {
        Set<String> seen = new HashSet<>();

        // try-with-resources automatically closes the reader, even if an exception occurs.
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            int lineNumber = 0;

            while ((line = br.readLine()) != null) {
                lineNumber++;
                try {
                    String[] parts = line.split(",", -1);
                    if (parts.length != 2) {
                        throw new IllegalArgumentException("Line " + lineNumber +
                                " must contain exactly 2 fields (student ID, mark).");
                    }

                    String studentId = parts[0].trim();
                    if (studentId.isEmpty()) {
                        throw new IllegalArgumentException("Line " + lineNumber +
                                " has an empty student ID.");
                    }

                    int mark;
                    try {
                        mark = Integer.parseInt(parts[1].trim());
                    } catch (NumberFormatException ex) {
                        LOGGER.log(Level.WARNING, "Line " + lineNumber +
                                ": non-numeric mark. Technical cause: " + ex, ex);
                        System.out.println("Line " + lineNumber +
                                ": Invalid mark. Enter a whole number from 0 to 100. Skipping record.");
                        continue; // One bad record does not stop the entire import.
                    }

                    if (mark < 0 || mark > 100) {
                        throw new IllegalArgumentException("Line " + lineNumber +
                                ": mark must be between 0 and 100, but was " + mark);
                    }

                    if (!seen.add(studentId)) {
                        throw new DuplicateStudentException(studentId);
                    }

                    System.out.println("Accepted: " + studentId + " -> " + mark);

                } catch (DuplicateStudentException | IllegalArgumentException ex) {
                    LOGGER.log(Level.WARNING, "Line " + lineNumber +
                            ": record rejected. Technical cause: " + ex, ex);
                    System.out.println("Line " + lineNumber + ": " +
                            ex.getMessage() + ". Skipping record.");
                }
            }
        }
    }

    public static void main(String[] args) {
        String filename = args.length > 0 ? args[0] : "marks.txt";
        try {
            importMarks(filename);
        } catch (FileNotFoundException ex) {
            LOGGER.log(Level.SEVERE, "File not found: " + filename, ex);
            System.out.println("Marks file not found; check the file name: " + filename);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "I/O problem while importing marks.", ex);
            System.out.println("Sorry, the marks file could not be read. Check it and try again.");
        } finally {
            System.out.println("Import attempt finished");
        }
    }
}
