import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class HotelManagementGUI {
    private static Connection connectToDatabase() {
        try {
            // Database connection details
            String url = "jdbc:mysql://sql12.freesqldatabase.com:3306/sql12753264";
            String user = "sql12753264"; // Provided MySQL username
            String password = "qSpxLHssLm"; // Provided MySQL password

            // Establish connection
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database connection failed!", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
            return null;
        }
    }

    private static boolean isRoomAvailable(Connection conn, int roomNumber) throws SQLException {
        String query = "SELECT customer_id FROM rooms WHERE room_number = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, roomNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("customer_id") == 0; // Room is available if customer_id is NULL
            }
        }
        return false;
    }

    private static void checkIn(Connection conn, int roomNumber, String name, String contact) throws SQLException {
        try (PreparedStatement insertCustomer = conn.prepareStatement(
                "INSERT INTO customers (name, contact) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
             PreparedStatement updateRoom = conn.prepareStatement(
                     "UPDATE rooms SET customer_id = ? WHERE room_number = ?")) {

            // Insert customer into the customers table
            insertCustomer.setString(1, name);
            insertCustomer.setString(2, contact);
            insertCustomer.executeUpdate();

            // Get generated customer ID
            ResultSet rs = insertCustomer.getGeneratedKeys();
            if (rs.next()) {
                int customerId = rs.getInt(1);

                // Update room with the customer's ID
                updateRoom.setInt(1, customerId);
                updateRoom.setInt(2, roomNumber);
                updateRoom.executeUpdate();
                JOptionPane.showMessageDialog(null, "Check-in successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private static void checkOut(Connection conn, int roomNumber) throws SQLException {
        try (PreparedStatement updateRoom = conn.prepareStatement(
                "UPDATE rooms SET customer_id = NULL WHERE room_number = ?")) {

            updateRoom.setInt(1, roomNumber);
            updateRoom.executeUpdate();
            JOptionPane.showMessageDialog(null, "Check-out successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private static void displayRooms(Connection conn) throws SQLException {
        String query = "SELECT r.room_number, c.name, c.contact FROM rooms r LEFT JOIN customers c ON r.customer_id = c.customer_id";
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            StringBuilder roomsInfo = new StringBuilder("Room Status:\n\n");
            while (rs.next()) {
                int roomNumber = rs.getInt("room_number");
                String customerName = rs.getString("name");
                String customerContact = rs.getString("contact");

                if (customerName == null) {
                    roomsInfo.append("Room ").append(roomNumber).append(" is available.\n");
                } else {
                    roomsInfo.append("Room ").append(roomNumber)
                            .append(" is occupied by ").append(customerName)
                            .append(" (Contact: ").append(customerContact).append(").\n");
                }
            }

            JOptionPane.showMessageDialog(null, roomsInfo.toString(), "Room Status", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void main(String[] args) {
        Connection conn = connectToDatabase();

        // Create the main frame
        JFrame frame = new JFrame("Hotel Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new GridLayout(4, 1));

        // Check-in button
        JButton checkInButton = new JButton("Check In");
        checkInButton.addActionListener(e -> {
            try {
                int roomNumber = Integer.parseInt(JOptionPane.showInputDialog("Enter Room Number:"));
                if (!isRoomAvailable(conn, roomNumber)) {
                    JOptionPane.showMessageDialog(null, "Room is already occupied!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String name = JOptionPane.showInputDialog("Enter Customer Name:");
                String contact = JOptionPane.showInputDialog("Enter Customer Contact:");
                checkIn(conn, roomNumber, name, contact);
            } catch (SQLException | NumberFormatException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error during check-in!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Check-out button
        JButton checkOutButton = new JButton("Check Out");
        checkOutButton.addActionListener(e -> {
            try {
                int roomNumber = Integer.parseInt(JOptionPane.showInputDialog("Enter Room Number:"));
                checkOut(conn, roomNumber);
            } catch (SQLException | NumberFormatException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error during check-out!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Display rooms button
        JButton displayRoomsButton = new JButton("Display Rooms");
        displayRoomsButton.addActionListener(e -> {
            try {
                displayRooms(conn);
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error fetching room status!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Exit button
        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> {
            try {
                conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.exit(0);
        });

        // Add buttons to the frame
        frame.add(checkInButton);
        frame.add(checkOutButton);
        frame.add(displayRoomsButton);
        frame.add(exitButton);

        // Make the frame visible
        frame.setVisible(true);
    }
}