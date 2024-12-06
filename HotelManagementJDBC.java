import java.sql.*;
import java.util.Scanner;

public class HotelManagementJDBC {
    // Database connection details
    private static final String DB_URL = "jdbc:mysql://localhost:3308/hotel_db?serverTimezone=UTC";
    private static final String DB_USER = "root"; // Replace with your MySQL username
    private static final String DB_PASSWORD = ""; // Replace with your MySQL password

    private static Connection connection;

    public static void main(String[] args) {
        // Establish database connection
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Database connection established successfully.");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found.");
            e.printStackTrace();
            System.exit(1);
        } catch (SQLException e) {
            System.err.println("Failed to connect to the database.");
            e.printStackTrace();
            System.exit(1);
        }

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\nHotel Management System");
            System.out.println("1. Check In");
            System.out.println("2. Check Out");
            System.out.println("3. Display Rooms");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline

            switch (choice) {
                case 1:
                    System.out.print("Enter room number (1-100): ");
                    int roomNumber = scanner.nextInt();
                    scanner.nextLine();
                    if (roomNumber < 1 || roomNumber > 100) {
                        System.out.println("Invalid room number. Please choose between 1 and 100.");
                        break;
                    }
                    System.out.print("Enter customer name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter customer contact: ");
                    String contact = scanner.nextLine();
                    checkIn(roomNumber, name, contact);
                    break;
                case 2:
                    System.out.print("Enter room number (1-100) to check out: ");
                    roomNumber = scanner.nextInt();
                    if (roomNumber < 1 || roomNumber > 100) {
                        System.out.println("Invalid room number. Please choose between 1 and 100.");
                        break;
                    }
                    checkOut(roomNumber);
                    break;
                case 3:
                    displayRooms();
                    break;
                case 4:
                    running = false;
                    closeConnection();
                    System.out.println("Exiting Hotel Management System.");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }

        scanner.close();
    }

    private static boolean isRoomAvailable(int roomNumber) {
        try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT customer_name FROM rooms WHERE room_number = ?")) {
            stmt.setInt(1, roomNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("customer_name") == null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void checkIn(int roomNumber, String name, String contact) {
        try {
            if (isRoomAvailable(roomNumber)) {
                try (PreparedStatement stmt = connection.prepareStatement(
                        "UPDATE rooms SET customer_name = ?, customer_contact = ? WHERE room_number = ?")) {
                    stmt.setString(1, name);
                    stmt.setString(2, contact);
                    stmt.setInt(3, roomNumber);
                    int rowsUpdated = stmt.executeUpdate();
                    if (rowsUpdated > 0) {
                        System.out.println("Customer checked in successfully to room " + roomNumber);
                    }
                }
            } else {
                System.out.println("Room " + roomNumber + " is already occupied.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void checkOut(int roomNumber) {
        try {
            if (!isRoomAvailable(roomNumber)) {
                try (PreparedStatement stmt = connection.prepareStatement(
                        "UPDATE rooms SET customer_name = NULL, customer_contact = NULL WHERE room_number = ?")) {
                    stmt.setInt(1, roomNumber);
                    int rowsUpdated = stmt.executeUpdate();
                    if (rowsUpdated > 0) {
                        System.out.println("Customer checked out successfully from room " + roomNumber);
                    }
                }
            } else {
                System.out.println("Room " + roomNumber + " is already empty.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void displayRooms() {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM rooms")) {
            while (rs.next()) {
                int roomNumber = rs.getInt("room_number");
                String customerName = rs.getString("customer_name");
                String customerContact = rs.getString("customer_contact");

                if (customerName == null) {
                    System.out.println("Room " + roomNumber + " is available.");
                } else {
                    System.out.println("Room " + roomNumber + " is occupied by Name: " + customerName + ", Contact: " + customerContact);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
