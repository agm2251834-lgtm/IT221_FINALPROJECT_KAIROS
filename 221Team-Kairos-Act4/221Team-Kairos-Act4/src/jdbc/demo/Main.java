package jdbc.demo;

import jdbc.demo.model.QueryResult;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    // This connects the menu to the database functions.
    private static final Database database = new Database();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            // Keep showing the menu until user exits.
            showMenu();
            String option = scanner.nextLine().trim();

            try {
                switch (option) {
                    case "0" -> running = false;
                    default -> handleQueryOption(option, scanner);
                }
            } catch (SQLException ex) {
                System.out.println("Database error: " + ex.getMessage());
            }

            System.out.println();
        }

        scanner.close();
        System.out.println("Program ended.");
    }

    private static void showMenu() {
        System.out.println("=======================================================================");
        System.out.println("                         KAIROS BORROWING SYSTEM                       ");
        System.out.println("=======================================================================");
        System.out.println("Basic System Lookups");
        System.out.println("  1  - S1: List all registered users");
        System.out.println("  2  - S1: View all items in the inventory");
        System.out.println("  3  - S1: List all available facility rooms");
        System.out.println("  4  - S1: Find equipment by availability status");
        System.out.println("  5  - S1: Search for specific userId");
        System.out.println("  6  - S1: Find items by condition status");
        System.out.println("  7  - S1: Activities scheduled on a date");
        System.out.println("  8  - S1: Filter items by type");
        System.out.println();
        System.out.println("Equipment Accountability");
        System.out.println("  9  - S2: Borrowed items and borrower names");
        System.out.println("  10 - S2: Items never borrowed");
        System.out.println("  11 - S2: Last person to handle a specific item");
        System.out.println("  12 - S2: Borrower history on a specific date");
        System.out.println();
        System.out.println("Facility & Scheduling Management");
        System.out.println("  13 - S3: Activities and assigned labs");
        System.out.println("  14 - S3: Facilities used for a specific activity type");
        System.out.println("  15 - S3: Facility hosting a specific activity");
        System.out.println("  16 - S3: Facilities with no activities yet");
        System.out.println();
        System.out.println("Request Approval & Workflow");
        System.out.println("  17 - S4: Activities and requesters");
        System.out.println("  18 - S4: Activities and approver");
        System.out.println("  19 - S4: Activities requested by specific user type");
        System.out.println("  20 - S4: Borrow transactions and custodian");
        System.out.println();
        System.out.println("Incident Reporting & Analytics");
        System.out.println("  21 - S5: Borrowers with incident remarks");
        System.out.println("  22 - S5: Count items held per student");
        System.out.println("  23 - S5: Most frequently borrowed item");
        System.out.println("  24 - S5: Users with no borrow transactions");
        System.out.println("  25 - S5: Items out for a specific activity type");
        System.out.println();
        System.out.println("Transactional Summary");
        System.out.println("  26 - S6: Borrow log (filter by date range/borrower)");
        System.out.println("  27 - S6: Unreturned items and borrower contact");
        System.out.println("  28 - S6: Items used in a specific facility");
        System.out.println("  29 - S6: Count activities approved per custodian");
        System.out.println("  30 - S6: Item count per itemType");
        System.out.println("  0  - Exit");
        System.out.println("=======================================================================");
        System.out.print("Choose option: ");
    }

    private static void handleQueryOption(String option, Scanner scanner) throws SQLException {
        // Each option runs a read-only query.
        try {
            int reportNumber = Integer.parseInt(option);
            List<String> params = collectReportParams(reportNumber, scanner);
            printQueryResult(database.executeReport(reportNumber, params));
        } catch (NumberFormatException ex) {
            System.out.println("Invalid option.");
        }
    }

    private static List<String> collectReportParams(int reportNumber, Scanner scanner) {
        List<String> params = new ArrayList<>();
        switch (reportNumber) {
            case 4 -> {
                System.out.print("Enter availabilityStatus: ");
                params.add(scanner.nextLine().trim());
            }
            case 5 -> {
                System.out.print("Enter userId: ");
                params.add(scanner.nextLine().trim());
            }
            case 6 -> {
                System.out.print("Enter conditionStatus: ");
                params.add(scanner.nextLine().trim());
            }
            case 7 -> {
                System.out.print("Enter activityDate (YYYY-MM-DD): ");
                params.add(scanner.nextLine().trim());
            }
            case 8 -> {
                System.out.print("Enter itemType: ");
                params.add(scanner.nextLine().trim());
            }
            case 11 -> {
                System.out.print("Enter itemId: ");
                params.add(scanner.nextLine().trim());
            }
            case 12 -> {
                System.out.print("Enter dateBorrowed (YYYY-MM-DD): ");
                params.add(scanner.nextLine().trim());
            }
            case 14 -> {
                System.out.print("Enter activityType: ");
                params.add(scanner.nextLine().trim());
            }
            case 15 -> {
                System.out.print("Enter activityId: ");
                params.add(scanner.nextLine().trim());
            }
            case 19 -> {
                System.out.print("Enter user type: ");
                params.add(scanner.nextLine().trim());
            }
            case 25 -> {
                System.out.print("Enter activityType: ");
                params.add(scanner.nextLine().trim());
            }
            case 26 -> {
                System.out.print("Enter borrower lastName (optional): ");
                params.add(scanner.nextLine().trim());
                System.out.print("Enter start date (YYYY-MM-DD, optional): ");
                params.add(scanner.nextLine().trim());
                System.out.print("Enter end date (YYYY-MM-DD, optional): ");
                params.add(scanner.nextLine().trim());
            }
            case 28 -> {
                System.out.print("Enter facilityName: ");
                params.add(scanner.nextLine().trim());
            }
            default -> {
                // This option does not need extra input.
            }
        }
        return params;
    }

    private static void printQueryResult(QueryResult result) {
        if (result.getRows().isEmpty()) {
            System.out.println("No records found.");
            return;
        }

        List<String> columns = result.getColumns();
        List<List<String>> rows = result.getRows();
        List<Integer> widths = new ArrayList<>();

        // Check the longest text in each column for neat printing.
        for (int i = 0; i < columns.size(); i++) {
            int max = columns.get(i).length();
            for (List<String> row : rows) {
                if (row.get(i).length() > max) {
                    max = row.get(i).length();
                }
            }
            widths.add(max);
        }

        // Print column names, separator, then the rows.
        printRow(columns, widths);
        printSeparator(widths);
        for (List<String> row : rows) {
            printRow(row, widths);
        }
    }

    private static void printRow(List<String> values, List<Integer> widths) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.size(); i++) {
            sb.append(String.format("%-" + widths.get(i) + "s", values.get(i)));
            if (i < values.size() - 1) {
                sb.append(" | ");
            }
        }
        System.out.println(sb);
    }

    private static void printSeparator(List<Integer> widths) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < widths.size(); i++) {
            sb.append("-".repeat(widths.get(i)));
            if (i < widths.size() - 1) {
                sb.append("-+-");
            }
        }
        System.out.println(sb);
    }
}
