package project1;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

import project1.Item.ItemType;

public class App {
    public static void main(String[] args) {
        String path = "src/main/java/project1/input/";
        String itemFileName = path + "items.txt";
        String bookingFileName = path + "bookings.txt";
        String discountFileName = path + "discounts.txt";

        List<Item> items = loadItems(itemFileName);
        if (items == null) {
            System.out.println("Failed to load items.");
            return;
        }

        for (Item item : items) {
            if (item.getCode().startsWith("R")) {
                System.out.printf("%s, %-19s    rate (per day) = %,9.2f    rate++ = %,9.2f\n", item.getCode(), item.getName(), item.getUnitPrice(), item.getPriceWithRate());
            }
        }

        System.out.println("");
        for (Item item : items) {
            if (item.getCode().startsWith("M")) {
                System.out.printf("%s, %-12s    rate (per person per day) = %,6.2f \n", item.getCode(), item.getName(), item.getUnitPrice());
            }
        }

        System.out.println("");
        List<Discount> discounts = loadDiscount(discountFileName);
        if (discounts == null) {
            System.out.println("Failed to load discounts.");
            return;
        }

        Collections.sort(discounts, new Comparator<Discount>() {
            public int compare(Discount d1, Discount d2) {
                return Double.compare(d2.getDiscountPercent(), d1.getDiscountPercent());
            }
        });

        for (Discount discount : discounts) {
            System.out.printf("If total bill >= %,10.0f   discount = %4.1f%% \n", discount.getMinSubTotal(), discount.getDiscountPercent());
        }

        Collections.sort(discounts, new Comparator<Discount>() {
            public int compare(Discount d1, Discount d2) {
                return Double.compare(d1.getDiscountPercent(), d2.getDiscountPercent());
            }
        });

        System.out.println("");
        List<Booking> booking = loadBooking(bookingFileName, items, discounts);
        if (booking == null) {
            System.out.println("Failed to load booking.");
            return;
        }

        System.out.println("\n===== Booking Processing =====");
        for (Booking b : booking) {
            int[] roomPerDay = b.getRoomPerDay();
            int[] mealPerPersonPerDay = b.getMealPerPersonPerDay();
            System.out.printf(
                "Booking %3s, customer %s  >>  days = %2d, persons = %d, rooms = [%d, %d, %d], meals = [%d, %d, %d]\n",
                b.getBookingId(), b.getCustomerId(), b.getDay(), b.getPerson(), roomPerDay[0], roomPerDay[1],
                roomPerDay[2], mealPerPersonPerDay[0], mealPerPersonPerDay[1], mealPerPersonPerDay[2]
            );
            // System.out.println("Booking ID: " + b.getBookingId());
            // System.out.println("Customer ID: " + b.getCustomerId());
            // System.out.println("Day: " + b.getDay());
            // System.out.println("Room per day: " + roomPerDay[0] + ", " + roomPerDay[1] +
            // ", " + roomPerDay[2]);
            // System.out.println("Person: " + b.getPerson());
            // System.out.println("Meal per person per day: " + mealPerPersonPerDay[0] + ",
            // " + mealPerPersonPerDay[1]
            // + ", " + mealPerPersonPerDay[2]);

            b.calculateTotalAmount(items, discounts);
            System.out.println("");

            // System.out.println("Total Amount: " + b.getTotalAmount());
        }

        List<Customer> customers = loadCustomer(booking);
        if (customers == null) {
            System.out.println("Failed to load customer.");
            return;
        }

        System.out.println("===== Customer Summary =====");

        Collections.sort(customers, new Comparator<Customer>() {
            public int compare(Customer c1, Customer c2) {
                return Double.compare(c2.getTotalAmount(), c1.getTotalAmount());
            }
        });

        for (Customer c : customers) {
            System.out.printf("%-5s>>  total amount = %,13.2f", c.getId(), c.getTotalAmount());
            // System.out.println("Customer ID: " + c.getId());
            // System.out.println("Total Amount: " + c.getTotalAmount());
            // System.out.println("Bookings:");
            String out = "    bookings = [";
            int counter = 0;
            for (Booking b : c.getBookings()) {
                // System.out.println(" - Booking ID: " + b.getBookingId() + ", Total Amount: "
                // + b.getTotalAmount());
                out += b.getBookingId();
                counter++;
                if (counter < c.getBookings().size())
                    out += ", ";
            }
            out += "]";
            System.out.println(out);
        }
        // System.out.println("===================================\n\n\n");

    }

    public static List<Item> loadItems(String fileName) {
        try {
            File fileData = new File(fileName);

            Scanner fileScan = new Scanner(fileData);
            System.out.println("Read from " + fileData.getPath());
            List<Item> items = new ArrayList<>();

            int currentLine = 0;

            while (fileScan.hasNext()) {
                currentLine++;
                if (currentLine == 1) {
                    fileScan.nextLine();
                    continue;
                }

                String line = fileScan.nextLine();
                String[] parts = line.split(",");

                String code = parts[0].trim();
                String name = parts[1].trim();
                double unitPrice = Double.parseDouble(parts[2].trim());
                String type = code.substring(0, 1).toUpperCase();

                ItemType itemType = type.equals("R") ? ItemType.ROOM : ItemType.MEAL;
                items.add(new Item(code, name, unitPrice, itemType));
            }

            fileScan.close();

            return items;
        } catch (Exception e) {
            System.err.println("Error");
            System.err.println(e);

            return null;
        }
    }

    public static List<Discount> loadDiscount(String fileName) {
        try {
            File fileData = new File(fileName);

            Scanner fileScan = new Scanner(fileData);
            System.out.println("Read from " + fileData.getPath());
            List<Discount> discounts = new ArrayList<>();

            int currentLine = 0;

            while (fileScan.hasNext()) {
                currentLine++;
                if (currentLine == 1) {
                    fileScan.nextLine();
                    continue;
                }

                String line = fileScan.nextLine();
                String[] parts = line.split(",");

                double minSubTotal = Double.parseDouble(parts[0].trim());
                double discountPercent = Double.parseDouble(parts[1].trim());

                discounts.add(new Discount(minSubTotal, discountPercent));
            }

            fileScan.close();

            return discounts;
        } catch (Exception e) {
            System.err.println("Error");
            System.err.println(e);

            return null;
        }
    }

    public static List<Booking> loadBooking(String fileName, List<Item> items, List<Discount> discounts) {
        try {
            File fileData = new File(fileName);

            Scanner fileScan = new Scanner(fileData);
            System.out.println("Read from " + fileData.getPath());
            List<Booking> bookings = new ArrayList<>();

            int currentLine = 0;

            while (fileScan.hasNext()) {
                currentLine++;
                if (currentLine == 1) {
                    fileScan.nextLine();
                    continue;
                }

                String line = fileScan.nextLine();
                String[] parts = line.split(",");

                String bookingId = parts[0].trim();
                String customerId = parts[1].trim();
                int day = Integer.parseInt(parts[2].trim());

                int[] roomPerDay = new int[3];
                String[] roomPerDayRaw = parts[3].trim().split(":");
                for (int i = 0; i < roomPerDayRaw.length; i++) {
                    roomPerDay[i] = Integer.parseInt(roomPerDayRaw[i].trim());
                }

                int person = Integer.parseInt(parts[4].trim());

                int[] mealPerPersonPerDay = new int[3];
                String[] mealPerPersonPerDayRaw = parts[5].trim().split(":");
                for (int i = 0; i < mealPerPersonPerDayRaw.length; i++) {
                    mealPerPersonPerDay[i] = Integer.parseInt(mealPerPersonPerDayRaw[i].trim());
                }

                Booking booking = new Booking(bookingId, customerId, day, roomPerDay, person, mealPerPersonPerDay);

                // booking.calculateTotalAmount(items, discounts);

                bookings.add(booking);
            }

            fileScan.close();

            return bookings;
        } catch (Exception e) {
            System.err.println("Error");
            System.err.println(e);

            return null;
        }
    }

    public static List<Customer> loadCustomer(List<Booking> booking) {
        try {
            List<Customer> customers = new ArrayList<Customer>();

            for (Booking b : booking) {
                String customerId = b.getCustomerId();
                boolean found = false;

                for (Customer c : customers) {
                    if (c.getId().equals(customerId)) {
                        c.getBookings().add(b);
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    Customer newCustomer = new Customer(customerId);
                    newCustomer.getBookings().add(b);
                    customers.add(newCustomer);
                }
            }

            return customers;
        } catch (Exception e) {
            System.err.println("Error");
            System.err.println(e);

            return null;
        }
    }
}
