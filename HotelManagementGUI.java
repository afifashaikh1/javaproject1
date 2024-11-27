import javax.swing.*;
import java.awt.*;
import java.util.Vector;

class Customer {
    String name;
    String contact;

    public Customer(String name, String contact) {
        this.name = name;
        this.contact = contact;
    }

    @Override
    public String toString() {
        return "Name: " + name + ", Contact: " + contact;
    }
}

class Room {
    int roomNumber;
    Customer customer;

    public Room(int roomNumber) {
        this.roomNumber = roomNumber;
        this.customer = null;
    }

    public boolean isAvailable() {
        return customer == null;
    }

    public void checkIn(Customer customer) {
        this.customer = customer;
    }

    public void checkOut() {
        this.customer = null;
    }

    @Override
    public String toString() {
        if (customer == null) {
            return "Room " + roomNumber + " is available.";
        } else {
            return "Room " + roomNumber + " is occupied by " + customer.toString();
        }
    }
}

class Hotel {
    Vector<Room> rooms;

    public Hotel(int numberOfRooms) {
        rooms = new Vector<>(numberOfRooms);
        for (int i = 0; i < numberOfRooms; i++) {
            rooms.add(new Room(i + 1));
        }
    }

    public void checkIn(int roomNumber, Customer customer) {
        Room room = rooms.get(roomNumber - 1);
        if (room.isAvailable()) {
            room.checkIn(customer);
        } else {
            throw new IllegalStateException("Room " + roomNumber + " is already occupied.");
        }
    }

    public void checkOut(int roomNumber) {
        Room room = rooms.get(roomNumber - 1);
        if (!room.isAvailable()) {
            room.checkOut();
        } else {
            throw new IllegalStateException("Room " + roomNumber + " is already empty.");
        }
    }

    public Vector<String> getRoomStatuses() {
        Vector<String> statuses = new Vector<>();
        for (Room room : rooms) {
            statuses.add(room.toString());
        }
        return statuses;
    }
}

public class HotelManagementGUI extends JFrame {
    private Hotel hotel;
    private JTextArea displayArea;

    public HotelManagementGUI() {
        hotel = new Hotel(100);
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Hotel Management System");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout());
        JButton checkInButton = new JButton("Check In");
        JButton checkOutButton = new JButton("Check Out");
        JButton displayRoomsButton = new JButton("Display Rooms");
        topPanel.add(checkInButton);
        topPanel.add(checkOutButton);
        topPanel.add(displayRoomsButton);

        displayArea = new JTextArea();
        displayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(displayArea);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        checkInButton.addActionListener(e -> showCheckInDialog());
        checkOutButton.addActionListener(e -> showCheckOutDialog());
        displayRoomsButton.addActionListener(e -> displayRoomStatuses());
    }

    private void showCheckInDialog() {
        JTextField roomField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField contactField = new JTextField();
        Object[] message = {
            "Room Number (1-100):", roomField,
            "Customer Name:", nameField,
            "Customer Contact:", contactField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Check In", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                int roomNumber = Integer.parseInt(roomField.getText());
                String name = nameField.getText();
                String contact = contactField.getText();
                if (roomNumber < 1 || roomNumber > 100) {
                    throw new IllegalArgumentException("Invalid room number.");
                }
                Customer customer = new Customer(name, contact);
                hotel.checkIn(roomNumber, customer);
                JOptionPane.showMessageDialog(this, "Customer checked in successfully to room " + roomNumber);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showCheckOutDialog() {
        JTextField roomField = new JTextField();
        Object[] message = {
            "Room Number (1-100):", roomField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Check Out", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                int roomNumber = Integer.parseInt(roomField.getText());
                if (roomNumber < 1 || roomNumber > 100) {
                    throw new IllegalArgumentException("Invalid room number.");
                }
                hotel.checkOut(roomNumber);
                JOptionPane.showMessageDialog(this, "Customer checked out successfully from room " + roomNumber);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void displayRoomStatuses() {
        Vector<String> statuses = hotel.getRoomStatuses();
        displayArea.setText("");
        for (String status : statuses) {
            displayArea.append(status + "\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            HotelManagementGUI app = new HotelManagementGUI();
            app.setVisible(true);
        });
    }
}
