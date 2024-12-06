import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class HotelManagementGUI {
    private static final String DB_URL = "jdbc:mysql://localhost:3308/hotel_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    private static Connection connection;

    public static void main(String[] args) {
        // Establish database connection
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Database connection established successfully.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Failed to connect to the database. Exiting...", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            System.exit(1);
        }

        // Create the main application window
        JFrame frame = new JFrame("Hotel Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        // Create buttons for operations
        JButton checkInButton = new JButton("Check In");
        JButton checkOutButton = new JButton("Check Out");
        JButton displayRoomsButton = new JButton("Display Rooms");
        JButton exitButton = new JButton("Exit");

        // Add action listeners to buttons
        checkInButton.addActionListener(e -> checkInDialog());
        checkOutButton.addActionListener(e -> checkOutDialog());
        displayRoomsButton.addActionListener(e -> displayRoomsDialog());
        exitButton.addActionListener(e -> {
            closeConnection();
            frame.dispose();
        });

        // Layout for buttons
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1, 10, 10));
        panel.add(checkInButton);
        panel.add(checkOutButton);
        panel.add(displayRoomsButton);
        panel.add(exitButton);

        frame.add(panel);
        frame.setVisible(true);
    }

    private static void checkInDialog() {
        JTextField roomField = new JTextField(5);
        JTextField nameField = new JTextField(20);
        JTextField contactField = new JTextField(15);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));
        panel.add(new JLabel("Room Number (1-100):"));
        panel.add(roomField);
        panel.add(new JLabel("Customer Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Customer Contact:"));
        panel.add(contactField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Check In", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                int roomNumber = Integer.parseInt(roomField.getText());
                String name = nameField.getText();
                String contact = contactField.getText();
                if (roomNumber < 1 || roomNumber > 100) {
                    JOptionPane.showMessageDialog(null, "Invalid room number. Please choose between 1 and 100.", "Error", JOptionPane.ERROR_MESSAGE);
                } else if (isRoomAvailable(roomNumber)) {
                    checkIn(roomNumber, name, contact);
                } else {
                    JOptionPane.showMessageDialog(null, "Room " + roomNumber + " is already occupied.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Invalid room number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void checkOutDialog() {
        String roomInput = JOptionPane.showInputDialog(null, "Enter Room Number (1-100):", "Check Out", JOptionPane.PLAIN_MESSAGE);
        try {
            int roomNumber = Integer.parseInt(roomInput);
            if (roomNumber < 1 || roomNumber > 100) {
                JOptionPane.showMessageDialog(null, "Invalid room number. Please choose between 1 and 100.", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (!isRoomAvailable(roomNumber)) {
                checkOut(roomNumber);
            } else {
                JOptionPane.showMessageDialog(null, "Room " + roomNumber + " is already empty.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Invalid room number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void displayRoomsDialog() {
        StringBuilder roomsInfo = new StringBuilder();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM rooms")) {
            while (rs.next()) {
                int roomNumber = rs.getInt("room_number");
                String customerName = rs.getString("customer_name");
                String customerContact = rs.getString("customer_contact");

                if (customerName == null) {
                    roomsInfo.append("Room ").append(roomNumber).append(": Available\n");
                } else {
                    roomsInfo.append("Room ").append(roomNumber).append(": Occupied by ")
                            .append("Name: ").append(customerName)
                            .append(", Contact: ").append(customerContact).append("\n");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JTextArea textArea = new JTextArea(roomsInfo.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        JOptionPane.showMessageDialog(null, scrollPane, "Room Status", JOptionPane.INFORMATION_MESSAGE);
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
        try (PreparedStatement stmt = connection.prepareStatement(
                "UPDATE rooms SET customer_name = ?, customer_contact = ? WHERE room_number = ?")) {
            stmt.setString(1, name);
            stmt.setString(2, contact);
            stmt.setInt(3, roomNumber);
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(null, "Customer checked in successfully to room " + roomNumber, "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void checkOut(int roomNumber) {
        try (PreparedStatement stmt = connection.prepareStatement(
                "UPDATE rooms SET customer_name = NULL, customer_contact = NULL WHERE room_number = ?")) {
            stmt.setInt(1, roomNumber);
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(null, "Customer checked out successfully from room " + roomNumber, "Success", JOptionPane.INFORMATION_MESSAGE);
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
