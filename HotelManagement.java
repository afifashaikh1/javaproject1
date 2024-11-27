import java.util.Scanner;
import java.util.Vector;

class Customer {
    int id;
    String name;
    String roomType;
    int days;

    Customer(int id, String name, String roomType, int days) {
        this.id = id;
        this.name = name;
        this.roomType = roomType;
        this.days = days;
    }

    @Override
    public String toString() {
        return "ID: " + id + ", Name: " + name + ", Room Type: " + roomType + ", Days: " + days;
    }
}

public class HotelManagement {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Vector<Customer> customers = new Vector<>();
        int maxCapacity = 100;

        while (true) {
            System.out.println("\n--- Hotel Management System ---");
            System.out.println("1. Add Customer");
            System.out.println("2. View All Customers");
            System.out.println("3. Search Customer by ID");
            System.out.println("4. Remove Customer");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    if (customers.size() < maxCapacity) {
                        System.out.print("Enter Customer ID: ");
                        int id = sc.nextInt();
                        sc.nextLine(); // Consume newline
                        System.out.print("Enter Customer Name: ");
                        String name = sc.nextLine();
                        System.out.print("Enter Room Type (Single/Double/Suite): ");
                        String roomType = sc.nextLine();
                        System.out.print("Enter Number of Days: ");
                        int days = sc.nextInt();

                        customers.add(new Customer(id, name, roomType, days));
                        System.out.println("Customer added successfully!");
                    } else {
                        System.out.println("Hotel is at full capacity!");
                    }
                    break;

                case 2:
                    if (customers.isEmpty()) {
                        System.out.println("No customers found.");
                    } else {
                        System.out.println("\n--- Customer List ---");
                        for (Customer customer : customers) {
                            System.out.println(customer);
                        }
                    }
                    break;

                case 3:
                    System.out.print("Enter Customer ID to search: ");
                    int searchId = sc.nextInt();
                    boolean found = false;
                    for (Customer customer : customers) {
                        if (customer.id == searchId) {
                            System.out.println("Customer Found: " + customer);
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        System.out.println("Customer not found.");
                    }
                    break;

                case 4:
                    System.out.print("Enter Customer ID to remove: ");
                    int removeId = sc.nextInt();
                    boolean removed = false;
                    for (Customer customer : customers) {
                        if (customer.id == removeId) {
                            customers.remove(customer);
                            System.out.println("Customer removed successfully!");
                            removed = true;
                            break;
                        }
                    }
                    if (!removed) {
                        System.out.println("Customer not found.");
                    }
                    break;

                case 5:
                    System.out.println("Exiting Hotel Management System. Goodbye!");
                    sc.close();
                    return;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}