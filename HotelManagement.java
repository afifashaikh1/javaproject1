import java.util.Scanner;

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

    public void checkIn(int roomNumber, Customer customer) {
        if (rooms[roomNumber - 1].isAvailable()) {
            rooms[roomNumber - 1].checkIn(customer);
            System.out.println("Customer checked in successfully to room " + roomNumber);
        } else {
            System.out.println("Room " + roomNumber + " is already occupied.");
        }
    }

    public void checkOut(int roomNumber) {
        if (!rooms[roomNumber - 1].isAvailable()) {
            rooms[roomNumber - 1].checkOut();
            System.out.println("Customer checked out successfully from room " + roomNumber);
        } else {
            System.out.println("Room " + roomNumber + " is already empty.");
        }
    }

    public void displayRooms() {
        for (Room room : rooms) {
            System.out.println(room.toString());
        }
    }
}

public class HotelManagement {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Hotel hotel = new Hotel(100); // 100 rooms in the hotel
        boolean running = true;

        while (running) {
            System.out.println("\nHotel Management System");
            System.out.println("1. Check In");
            System.out.println("2. Check Out");
            System.out.println("3. Display Rooms");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.print("Enter room number (1-100): ");
                    int roomNumber = scanner.nextInt();
                    scanner.nextLine(); // Consume newline
                    if (roomNumber < 1 || roomNumber > 100) {
                        System.out.println("Invalid room number. Please choose between 1 and 100.");
                        break;
                    }
                    System.out.print("Enter customer name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter customer contact: ");
                    String contact = scanner.nextLine();
                    Customer customer = new Customer(name, contact);
                    hotel.checkIn(roomNumber, customer);
                    break;
                case 2:
                    System.out.print("Enter room number (1-100) to check out: ");
                    roomNumber = scanner.nextInt();
                    if (roomNumber < 1 || roomNumber > 100) {
                        System.out.println("Invalid room number. Please choose between 1 and 100.");
                        break;
                    }
                    hotel.checkOut(roomNumber);
                    break;
                case 3:
                    hotel.displayRooms();
                    break;
                case 4:
                    running = false;
                    System.out.println("Exiting Hotel Management System.");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }

        scanner.close();
    }
}