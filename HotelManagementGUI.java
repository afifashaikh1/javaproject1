import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
        this.customer = null; // Empty room
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
    Room[] rooms;

    public Hotel(int numberOfRooms) {
        rooms = new Room[numberOfRooms];
        for (int i = 0; i < numberOfRooms; i++) {
            rooms[i] = new Room(i + 1);
        }
    }

    public String checkIn(int roomNumber, Customer customer) {
        if (rooms[roomNumber - 1].isAvailable()) {
            rooms[roomNumber - 1].checkIn(customer);
            return "Customer checked in successfully to room " + roomNumber;
        } else {
            return "Room " + roomNumber + " is already occupied.";
        }
    }

    public String checkOut(int roomNumber) {
        if (!rooms[roomNumber - 1].isAvailable()) {
            rooms[roomNumber - 1].checkOut();
            return "Customer checked out successfully from room " + roomNumber;
        } else {
            return "Room " + roomNumber + " is already empty.";
        }
    }

    public String displayRooms() {
        StringBuilder roomList = new StringBuilder();
        for (Room room : rooms) {
            roomList.append(room.toString()).append("\n");
        }
        return roomList.toString();
    }
}

public class HotelManagementGUI {
    private static Hotel hotel;
    private static JTextArea roomDisplayArea;

    public static void main(String[] args) {
        // Initialize the hotel with 100 rooms
        hotel = new Hotel(100);

        // Create the main window
        JFrame frame = new JFrame("Hotel Management System");
        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Panel for user input (room number, customer name, and contact)
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(4, 2));

        JLabel roomNumberLabel = new JLabel("Room Number:");
        JTextField roomNumberField = new JTextField();
        JLabel nameLabel = new JLabel("Customer Name:");
        JTextField nameField = new JTextField();
        JLabel contactLabel = new JLabel("Customer Contact:");
        JTextField contactField = new JTextField();

        inputPanel.add(roomNumberLabel);
        inputPanel.add(roomNumberField);
        inputPanel.add(nameLabel);
        inputPanel.add(nameField);
        inputPanel.add(contactLabel);
        inputPanel.add(contactField);

        // Panel for buttons (Check In, Check Out, Display Rooms)
        JPanel buttonPanel = new JPanel();
        JButton checkInButton = new JButton("Check In");
        JButton checkOutButton = new JButton("Check Out");
        JButton displayButton = new JButton("Display Rooms");

        buttonPanel.add(checkInButton);
        buttonPanel.add(checkOutButton);
        buttonPanel.add(displayButton);

        // Area to display room status
        roomDisplayArea = new JTextArea();
        roomDisplayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(roomDisplayArea);

        // Add the panels to the frame
        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(buttonPanel, BorderLayout.CENTER);
        frame.add(scrollPane, BorderLayout.SOUTH);

        // Action listener for Check In
        checkInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int roomNumber = Integer.parseInt(roomNumberField.getText());
                    if (roomNumber < 1 || roomNumber > 100) {
                        JOptionPane.showMessageDialog(frame, "Invalid room number. Please choose between 1 and 100.");
                        return;
                    }
                    String name = nameField.getText().trim();
                    String contact = contactField.getText().trim();

                    if (name.isEmpty() || contact.isEmpty()) {
                        JOptionPane.showMessageDialog(frame, "Please enter both name and contact.");
                        return;
                    }

                    Customer customer = new Customer(name, contact);
                    String message = hotel.checkIn(roomNumber, customer);
                    roomDisplayArea.setText(message);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Please enter a valid number for the room.");
                }
            }
        });

        // Action listener for Check Out
        checkOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int roomNumber = Integer.parseInt(roomNumberField.getText());
                    if (roomNumber < 1 || roomNumber > 100) {
                        JOptionPane.showMessageDialog(frame, "Invalid room number. Please choose between 1 and 100.");
                        return;
                    }

                    String message = hotel.checkOut(roomNumber);
                    roomDisplayArea.setText(message);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Please enter a valid number for the room.");
                }
            }
        });

        // Action listener for Display Rooms
        displayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String rooms = hotel.displayRooms();
                roomDisplayArea.setText(rooms);
            }
        });

        // Show the window
        frame.setVisible(true);
    }
}