

// !!!!!!!!!!!!!!!!!!!!!!!!!!WORKING IN PROGRESS!!!!!!!!!!!!!!!!!!!!!!!!!!

package jdbc.demo;

import jdbc.demo.model.QueryResult;
import jdbc.demo.model.UserSession;

import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    private static final Database db = new Database();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        UserSession user = login(scanner);

        if (user == null) {
            System.out.println("Invalid credentials.");
            return;
        }
        System.out.println("Welcome, " + user.getName() + " (" + user.getRole() + ")");

        switch (user.getRole()) {
            case "admin" -> adminMenu(scanner, user);
            case "student" -> studentMenu(scanner, user);
            case "custodian" -> custodianMenu(scanner, user);
        }

        scanner.close();
    }

    // LOGIN PAGE
    private static String login(Scanner scanner) {
        try {
            System.out.println("\n===== Login =====");

            System.out.println("Enter Username: ");
            String id = scanner.nextLine();

            System.out.println("Enter Password: ");
            String pass = scanner.nextLine();

            return db.login(id, pass);

        } catch (Exception e) {
            return null;
        }
    }

    // ADMIN SIDE
    private static void adminMenu(Scanner scanner) {
        while (true) {
            System.out.println("\n===== Admin Menu =====");
            System.out.println("1 - Manage Users");
            System.out.println("2 - Reports");
            System.out.println("0 - Logout");

            switch (scanner.nextLine()) {
                case "1" -> manageUsers(scanner);
                case "2" -> showReports();
                case "0" -> { return; }
            }
        }
    }

    // STUDENT SIDE
    private static void studentMenu(Scanner scanner) {
        while (true) {
            System.out.println("\n===== Student Menu =====");
            System.out.println("1 - View Items");
            System.out.println("2 - Borrow Items");
            System.out.println("3 - Logout");

            try {
                switch (scanner.nextLine()) {
                    case "1" -> print(db.getItems());
                    case "2" -> {
                        System.out.println("Item ID: ");
                        String item = scanner.nextLine();

                        db.borrowItem(user.getId(), item);
                        System.out.println("Item borrowed successfully.");
                    }
                    case "0" -> { return; }
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    // CUSTODIAN SIDE
    private static void custodianMenu(Scanner scanner) {
        while (true) {
            System.out.println("\n===== Custodian Menu =====");
            System.out.println("1 - View Borrow Records");
            System.out.println("2 - Return Item");
            System.out.println("0 - Logout");

            try {
                switch (scanner.nextLine()) {
                    case "1" -> print(db.getBorrow());
                    case "2" -> {
                        System.out.println("Borrow ID: ");
                        int id = Integer.parseInt(scanner.nextLine());
                        db.returnItem(id);
                        System.out.println("Item returned.");
                    }
                    case "0" -> {
                        return;
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    // USER MANAGEMENT
    private static void manageUsers(Scanner sc) {
        try {
            while (true) {
                System.out.println("\n--- MANAGE USERS ---");
                System.out.println("1 - Create");
                System.out.println("2 - View");
                System.out.println("3 - Delete");
                System.out.println("0 - Back");

                switch (sc.nextLine()) {
                    case "1" -> {
                        System.out.print("ID: "); String id = sc.nextLine();
                        System.out.print("Name: "); String name = sc.nextLine();
                        System.out.print("Password: "); String pass = sc.nextLine();
                        System.out.print("Role: "); String role = sc.nextLine();
                        db.insertUser(id, name, pass, role);
                    }
                    case "2" -> print(db.getUsers());
                    case "3" -> {
                        System.out.print("ID: ");
                        db.deleteUser(sc.nextLine());
                    }
                    case "0" -> { return; }
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // ================= REPORTS =================
    private static void showReports() {
        try {
            print(db.borrowedItemsReport());
            print(db.itemsNeverBorrowed());
            print(db.usersNoBorrow());
            print(db.mostBorrowedItem());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // ================= PRINT =================
    private static void print(QueryResult r) {
        for (String c : r.getColumns()) System.out.print(c + "\t");
        System.out.println();
        for (var row : r.getRows()) {
            for (String v : row) System.out.print(v + "\t");
            System.out.println();
        }
    }
}





