package project1;
/* 
   Made with ♥ by,

   Yoswaris Lawpaiboon,  6681170
   Pasin Piyavej,        6681187
   Praepilai Phetsamsri, 6681374
   
*/

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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
        boolean[] usableVar = { false, false, false }; // item, discount, booking
        Scanner keyboardIn = new Scanner(System.in);

        /* ===================Item section=================== */

        // Buffer data from items.txt into list

        List<Item> items = loadItems(itemFileName);
        while (items == null) {
            System.out.printf("Determined item list file path cannot be read (Current path: %s)\n", itemFileName);
            System.out.println("Caution: Customer's booking will not been charged!\n");
            System.out.println("Do you want to enter new items list file path?");
            System.out.println("(Enter new file name or 0 to exit program)");
            String temp = keyboardIn.next();
            if (!temp.equals("0")) {
                itemFileName = path + temp;
                items = loadItems(itemFileName);
            } else {
                System.exit(0);
            }
        }
        // Print everything in buffer
        // Room type section
        for (Item item : items) {
            if (item.getItemType() == ItemType.ROOM) {
                System.out.printf("%s, %-25s%2s Rate (per day) = %,9.2f%2s Rate++ = %,9.2f\n",
                        item.getCode(),
                        item.getName(), " ",
                        item.getUnitPrice(), " ",
                        item.getPriceWithRate());
            }
        }

        // Meal menu section
        System.out.println("");
        for (Item item : items) {
            if (item.getItemType() == ItemType.MEAL) {
                System.out.printf("%s, %-12s    rate (per person per day) = %,6.2f \n", item.getCode(), item.getName(),
                        item.getUnitPrice());
            }
        }
        usableVar[0] = true;

        /* ===================Discount Section=================== */

        // Buffer data from discounts.txt
        System.out.println("");
        List<Discount> discounts = loadDiscount(discountFileName);
        if (discounts == null) {
            System.out.println("Failed to load discounts.");
            while (discounts == null) {
                System.out.printf("\nDetermined discount data file path cannot be read (Current path: %s)\n",
                        discountFileName);
                System.out.println("No discount will not be apply\n");
                System.out.println("Do you want to enter new items list file path?");
                System.out.println("(Enter new file name or 0 to ignore discount program)");
                String temp = keyboardIn.next();
                if (!temp.equals("0")) {
                    discountFileName = path + temp;
                    discounts = loadDiscount(discountFileName);
                } else {
                    break;
                }
            }
            // return;
        } else {
            // Sort discount list first
            List<Discount> discountsCopy = new ArrayList<>(discounts);
            Collections.sort(discountsCopy, new Comparator<Discount>() {
                public int compare(Discount d1, Discount d2) {
                    return Double.compare(d2.getDiscountPercent(), d1.getDiscountPercent());
                }
            });

            // Then, print here
            for (Discount discount : discountsCopy) {
                System.out.printf("If total bill >= %,10.0f   discount = %4.1f%% \n",
                        discount.getMinSubTotal(),
                        discount.getDiscountPercent());
            }
            usableVar[1] = true;
        }

        /* ===================Booking Section=================== */

        // Load booking file
        System.out.println("");

        List<Booking> booking = null;
        while (booking == null) {
            // Go through each variable, making sure everythings work
            List<Item> itemsToLoad = new ArrayList<>();
            if (usableVar[0] == false) {
                itemsToLoad.add(new Item("?", "?", 0, ItemType.ROOM));
            } else {
                itemsToLoad.addAll(items);
            }

            List<Discount> discountsToLoad = new ArrayList<>();
            if (usableVar[1] == false) {
                discountsToLoad.add(new Discount(0, 0));
            } else {
                discountsToLoad.addAll(discounts);
            }

            // Try read booking file, with each variable expressed
            booking = loadBooking(bookingFileName, itemsToLoad, discountsToLoad, keyboardIn);

            // Failsafe: Booking file Path invalid
            if (booking == null) {
                System.out.println("Failed to load booking.\n");
                System.out.printf(
                        "Determined booking list file path cannot be read (Current path: %s)%n",
                        bookingFileName);
                System.out.println("Caution: Failing to read booking file will exit program!");
                System.out.print("Enter new file name or 0 to exit program: ");

                String temp = keyboardIn.next();
                if (temp.equals("0")) {
                    System.exit(0);
                } else {
                    bookingFileName = path + temp;
                }
            }

        }

        // Print each booking summarize
        System.out.println("\n===== Booking Processing =====");
        for (Booking b : booking) {
            int[] roomPerDay = b.getRoomPerDay();
            int[] mealPerPersonPerDay = b.getMealPerPersonPerDay();
            System.out.printf("Booking %3s, customer %s  >>  days = %2d, persons = %d, rooms = %s, meals = %s\n",
                    b.getBookingId(),
                    b.getCustomerId(),
                    b.getDay(),
                    b.getPerson(),
                    Arrays.toString(roomPerDay),
                    Arrays.toString(mealPerPersonPerDay));
            // System.out.println("Booking ID: " + b.getBookingId());
            // System.out.println("Customer ID: " + b.getCustomerId());
            // System.out.println("Day: " + b.getDay());
            // System.out.println("Room per day: " + roomPerDay[0] + ", " + roomPerDay[1] +
            // ", " + roomPerDay[2]);
            // System.out.println("Person: " + b.getPerson());
            // System.out.println("Meal per person per day: " + mealPerPersonPerDay[0] + ",
            // " + mealPerPersonPerDay[1]
            // + ", " + mealPerPersonPerDay[2]);

            // Invoice section
            if (usableVar[0] == false || usableVar[1] == false) { // FailSafe: Discount file can't read
                List<Discount> nonCertainDiscount = new ArrayList<>();
                List<Item> nonCertainItem = new ArrayList<>();
                if (usableVar[0] == false) {
                    nonCertainItem.add(new Item("?", "?", 0, ItemType.ROOM));
                } else
                    nonCertainItem = items;
                if (usableVar[1] == false) {
                    nonCertainDiscount.add(new Discount(0, 0));
                } else
                    nonCertainDiscount = discounts;

                b.calculateTotalAmount(nonCertainItem, nonCertainDiscount);
            } else {
                b.calculateTotalAmount(items, discounts);
            }
            System.out.println("");

            // System.out.println("Total Amount: " + b.getTotalAmount());
        }

        // Summarize each customer's booking
        List<Customer> customers = loadCustomer(booking);
        if (customers == null) {
            System.out.println("Failed to load customer.");
            // return;
        } else {
            System.out.println("===== Customer Summary =====");

            Collections.sort(customers, new Comparator<Customer>() {
                public int compare(Customer c1, Customer c2) {
                    return Double.compare(c2.getTotalAmount(), c1.getTotalAmount()) != 0
                            ? Double.compare(c2.getTotalAmount(), c1.getTotalAmount())
                            : c1.getId().compareTo(c2.getId());
                }
            });

            for (Customer c : customers) {
                System.out.printf("%-5s>>  total amount = %,13.2f", c.getId(), c.getSubTotalAmount());
                // System.out.println("Customer ID: " + c.getId());
                // System.out.println("Total Amount: " + c.getTotalAmount());
                // System.out.println("Bookings:");
                ArrayList<String> bookingIds = new ArrayList<String>();
                for (Booking b : c.getBookings()) {
                    bookingIds.add(b.getBookingId());
                }

                System.out.printf("    bookings = [%s]\n", String.join(", ", bookingIds));
            }
            usableVar[2] = true;
        }

        // End of Program
        // System.out.println("===================================\n\n\n");
        keyboardIn.close();
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

    public static List<Booking> loadBooking(String fileName, List<Item> items, List<Discount> discounts,
            Scanner keyboardIn) {
        try {
            int roomCount = 0;
            int mealCount = 0;

            // Buffer numbers of room and meal types(from items.txt)
            for (Item item : items) {
                if (item.getItemType() == ItemType.ROOM) {
                    roomCount++;
                } else if (item.getItemType() == ItemType.MEAL) {
                    mealCount++;
                }
            }

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

                try {
                    String[] parts = line.split(",");

                    String bookingId = parts[0].trim(); // Set bookingID
                    String customerId = parts[1].trim(); // Set customerID

                    // Handle non-int bookingID
                    if (!Character.isDigit(bookingId.charAt(1))) {
                        System.out.printf(
                                "\nBooking: %s format is inappropriate, do you want to continue?\n(Acceptable input i.e. B1, B2,...)",
                                bookingId);
                        System.out.println("\n(Enter y to continue OR other key to skip this Booking)");
                        String input = keyboardIn.next().toLowerCase();
                        if (!input.equals("y")) {
                            continue;
                        }
                    }

                    // Handle non-int customerID
                    if (!Character.isDigit(customerId.charAt(1))) {
                        System.out.printf(
                                "\nBooking: %s customer code format is inappropriate, do you want to continue?\n(Acceptable input i.e. C1, C2,... but the code read as %s)\n",
                                bookingId,
                                customerId);
                        System.out.println("(Enter y to continue OR other key to skip this Booking)");
                        String input = keyboardIn.next().toLowerCase();
                        if (!input.equals("y")) {
                            continue;
                        }
                    }

                    int day = 0;

                    // Missing data such as 4.5,0 <- need another ,0
                    // if (parts.length != (roomCount + mealCount - 2)) {
                    // System.out.printf(
                    // "\nBooking: %s contain invalid room type and day format, which unsolvable,
                    // skipping booking %s\n",
                    // bookingId, bookingId);
                    // continue;
                    // }

                    // Failsafe: Invalid day reservation
                    try {
                        day = Integer.parseInt(parts[2].trim()); // Set reservation duration
                        if (day < 1) {
                            throw new ArithmeticException("Negative day reservation");
                        }
                    } catch (Exception e) { // Ask user to enter again
                        System.out.printf(
                                "\nBooking: %s contain invalid numbers of reservation day input\n(Acceptable input i.e. 1, 2, 3,... but you enter %s days)\n",
                                bookingId, parts[2].trim());
                        System.out.println("(Enter new numbers of reservation day or input 0 to skip this booking)");
                        try {
                            day = keyboardIn.nextInt();
                        } catch (Exception f) {
                            System.out.printf("Input still contain inappropriate format, skip %s booking\n", bookingId);
                        }
                        if (day == 0) {
                            continue;
                        }
                    }

                    int[] roomPerDay = new int[roomCount]; // Declare array storing R1:R2:R3, day reservered
                    String[] roomPerDayRaw = parts[3].trim().split(":"); // Organized format
                    boolean potentialInvalidRoomDay = false;
                    if (roomPerDayRaw.length > roomCount) { // If input more than room type
                        potentialInvalidRoomDay = true;
                    }

                    // Failsafe: Invalid rooms and days format
                    // if (roomPerDayRaw.length != roomCount) {
                    //     System.out.printf(
                    //             "\nBooking: %s contain invalid room type, which unsolvable, skipping booking %s\n",
                    //             bookingId, bookingId);
                    //     continue;
                    // }

                    try {
                        for (int i = 0; i < roomPerDayRaw.length; i++) {
                            roomPerDay[i] = Integer.parseInt(roomPerDayRaw[i].trim()); // Convert to int
                        }
                    } catch (Exception e) { // Try read data from bad seperator
                        int[] bufferPosInput = new int[roomCount];
                        try {
                            for (int i = 0; i < roomCount; i++) {
                                if (roomPerDayRaw[0].charAt(i + 1) != ',') {
                                    if (Character.isDigit(roomPerDayRaw[0].charAt(i)))
                                        bufferPosInput[i] = Integer
                                                .parseInt(String.valueOf(roomPerDayRaw[0].charAt(i)));
                                } else {
                                    continue;
                                }
                            }
                            potentialInvalidRoomDay = false;
                        } catch (Exception f) { // Exit when available too less compare to all room option
                            System.out.printf(
                                    "\nBooking: %s contain invalid room type and day format, which unsolvable, skipping booking %s\n",
                                    bookingId, bookingId);
                            continue;
                        }
                        System.out.printf(
                                "\nBooking: %s contain invalid room type and day format (Proper input i.e. 1:2:3)\n",
                                bookingId);
                        for (int i = 0; i < roomCount; i++) {
                            System.out.printf("Program read as %s: %d days\n", items.get(i).getName(),
                                    bufferPosInput[i]);
                        }
                        System.out.println("(Enter y to accpet, OR other key to skip)");
                        String input = keyboardIn.next().toLowerCase();
                        if (input.equals("y")) {
                            for (int i = 0; i < roomCount; i++) {
                                roomPerDay[i] = bufferPosInput[i];
                            }
                            potentialInvalidRoomDay = false;
                        } else {
                            continue;
                        }
                    }

                    // Failsafe: Input person non-Int
                    int person = 0;
                    try {
                        person = Integer.parseInt(parts[4].trim());
                    } catch (Exception e) { // Ask user to input again
                        if (parts.length == (roomCount + mealCount - 2)) {
                            System.out.printf(
                                    "\nBooking: %s contain invalid numbers of guest\n(Accepetable input i.e. 1, 2, 3,... but you enter: %s)\n",
                                    bookingId, parts[4].trim());
                            System.out.println("(Enter new numbers of guest or input 0 to skip this booking)");
                            try {
                                person = keyboardIn.nextInt();
                                if (person == 0) {
                                    continue;
                                }
                                potentialInvalidRoomDay = false;
                            } catch (Exception f) {
                                System.out.printf("Input still contain inappropriate format, skip %s booking\n",
                                        bookingId);
                            }
                        }
                    }

                    int[] mealPerPersonPerDay = new int[mealCount]; // Array storing M1:M2:M3, day reservered
                    String[] mealPerPersonPerDayRaw = parts[5].trim().split(":"); // Organized format

                    // Failsafe: Meals and person and day format
                    // if (mealPerPersonPerDayRaw.length != mealCount) {
                    //     System.out.printf(
                    //             "\nBooking: %s contain invalid meal type and day format, which unsolvable, skipping booking %s\n",
                    //             bookingId, bookingId);
                    //     continue;
                    // }

                    try {
                        for (int i = 0; i < mealPerPersonPerDayRaw.length; i++) {
                            mealPerPersonPerDay[i] = Integer.parseInt(mealPerPersonPerDayRaw[i].trim()); // Convert to
                                                                                                         // int
                        }
                    } catch (Exception e) { // Try read data from bad seperator
                        int[] bufferPosInput = new int[mealCount];
                        for (int i = 0, j = 0; i < mealCount; i++) {
                            if (mealPerPersonPerDayRaw[0].charAt(i + 1) != ',') {
                                if (Character.isDigit(mealPerPersonPerDayRaw[0].charAt(i))) {
                                    bufferPosInput[j] = Integer
                                            .parseInt(String.valueOf(mealPerPersonPerDayRaw[0].charAt(i)));
                                    j++;
                                }
                            } else {
                                continue;
                            }
                        }
                        System.out.printf(
                                "\nBooking: %s contain invalid menu type and day format (Proper input i.e. 1:2:3)\n",
                                bookingId);
                        for (int i = 0; i < mealCount; i++) {
                            System.out.printf("Program read as %s: %d ea/person/day\n",
                                    items.get(roomCount + i).getName(), bufferPosInput[i]);
                        }
                        System.out.println("(Enter y to accept, OR other key to skip)");
                        String input = keyboardIn.next().toLowerCase();
                        if (input.equals("y")) {
                            for (int i = 0; i < mealCount; i++) {
                                mealPerPersonPerDay[i] = bufferPosInput[i];
                            }
                            potentialInvalidRoomDay = false;
                        } else {
                            continue;
                        }
                    }

                    // Final inspection
                    // Failsafe: Person != 0
                    if (potentialInvalidRoomDay == true) {
                        System.out.printf("\nBooking: %s has invalid room and day reservation format, unsolvable\n",
                                bookingId);
                        System.out.println("Do want to enter data manually?");
                        System.out.println("(Enter y to accept OR other key to skip this booking)");
                        String temp = keyboardIn.next();
                        if (temp.toLowerCase().equals("y")) {
                            for (int i = 0; i < roomCount; i++) {
                                System.out.printf("Room R%d: ", i);
                                try {

                                    roomPerDay[i] = keyboardIn.nextInt();
                                    System.out.println();
                                } catch (Exception e) {
                                    System.out.printf("Fatal: An input is non-natual number, skip booking %s\n",
                                            bookingId);
                                }
                            }
                        } else {
                            continue;
                        }
                    }

                    if (person <= 0) {
                        System.out.printf(
                                "\nBooking: %s has invalid input guest, booking with non-natural number of guest are not allowed\n",
                                bookingId);
                        System.out.printf("(Acceptable format (i.e. 1,2,3,..) but you entered %d)\n", person);
                        System.out.println("(Enter number of guest, enter 0 to skip this booking)");
                        try {
                            person = keyboardIn.nextInt();
                        } catch (Exception e) {
                            System.out.printf("Invalid input type, skipping %s\n", bookingId);
                            continue;
                        }
                    }

                    // Failsafe: Must reserve at least 1 room or dine-in
                    Boolean recordAble = false;
                    for (int i = 0; i < roomCount; i++) {
                        if (roomPerDay[i] > 0 || mealPerPersonPerDay[i] > 0) {
                            recordAble = true;
                        }
                        if (roomPerDay[i] < 0 || mealPerPersonPerDay[i] < 0) {
                            System.out.printf("\nBooking: %s contain negative data value, not solvable, skipping %s\n",
                                    bookingId,
                                    bookingId);
                            recordAble = false;
                            break;
                        }
                    }
                    if (!recordAble) {
                        System.out.printf(
                                "\nBooking: %s has no room reserved, booking with 0 room reserved are not allowed, skipping booking %s\n",
                                bookingId,
                                bookingId);
                        continue;
                    }

                    // Pack value into variable
                    Booking booking = new Booking(bookingId, customerId, day, roomPerDay, person, mealPerPersonPerDay);

                    // booking.calculateTotalAmount(items, discounts);

                    bookings.add(booking); // Add to packed variable into array
                } catch (Exception e) {
                    System.err.println(e);
                    System.err.println(line);
                    continue;
                }
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

    public static boolean failCheck(String inPath) {
        try {
            File fileData = new File(inPath);
            Scanner fileScan = new Scanner(fileData);
            fileScan.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
