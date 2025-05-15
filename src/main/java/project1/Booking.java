package project1;

import java.util.List;

public class Booking {
    private String bookingId;
    private String customerId;
    private int day;
    private int[] roomPerDay;
    private int person;
    private int[] mealPerPersonPerDay;
    private double totalAmount;

    public Booking(String bookingId, String customerId, int day, int[] roomPerDay, int person, int[] mealPerPersonPerDay) {
        this.bookingId = bookingId;
        this.customerId = customerId;
        this.day = day;
        this.roomPerDay = roomPerDay;
        this.person = person;
        this.mealPerPersonPerDay = mealPerPersonPerDay;
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public int getDay() {
        return day;
    }

    public int[] getRoomPerDay() {
        return roomPerDay;
    }

    public int getPerson() {
        return person;
    }

    public int[] getMealPerPersonPerDay() {
        return mealPerPersonPerDay;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void calculateTotalAmount(List<Item> items, List<Discount> discounts) {
        final String[] roomCodes = {"R1", "R2", "R3"};
        final String[] mealCodes = {"M1", "M2", "M3"};

        // double total = 0;

        // for (int i = 0; i < roomPerDay.length; i++) {
        //     String roomCode = roomCodes[i];
        //     int roomCount = roomPerDay[i];

        //     for (Item item : items) {
        //             System.out.println("Room code: " + roomCode);
        //             System.out.println("Room count: " + roomCount);
        //             System.out.println("Unit price: " + item.getUnitPrice());
        //             System.out.println("Day: " + day);
        //         if (item.getCode().equals(roomCode)) {
        //             total += item.getUnitPrice() * roomCount * day;
        //             break;
        //         }
        //     }
        // }

        // total = (total * 1.1) * 1.07;

        // System.out.println("Total room price++: " + total);

        // for (int i = 0; i < mealPerPersonPerDay.length; i++) {
        //     String mealCode = mealCodes[i];
        //     int mealCount = mealPerPersonPerDay[i];

        //     for (Item item : items) {
        //         if (item.getCode().equals(mealCode)) {
        //             total += item.getUnitPrice() * mealCount * person * day;
        //             break;
        //         }
        //     }
        // }

        // System.out.println("Total meal price: " + total);

        // this.totalAmount = total;

        double roomTotal = 0;
        double mealTotal = 0;

        for (int i = 0; i < roomPerDay.length; i++) {
            String roomCode = roomCodes[i];
            int roomCount = roomPerDay[i];

            for (Item item : items) {
                if (item.getCode().equals(roomCode)) {
                    roomTotal += item.getUnitPrice() * roomCount * day;
                    break;
                }
            }
        }

        roomTotal = (roomTotal * 1.1) * 1.07;

        System.out.println("Total room price++: " + roomTotal);

        for (int i = 0; i < mealPerPersonPerDay.length; i++) {
            String mealCode = mealCodes[i];
            int mealCount = mealPerPersonPerDay[i];

            for (Item item : items) {
                if (item.getCode().equals(mealCode)) {
                    mealTotal += item.getUnitPrice() * mealCount * person * day;
                    break;
                }
            }
        }

        System.out.println("Total meal price: " + mealTotal);

        double subTotal = roomTotal + mealTotal;
        double discountPercent = 0;
        double discountAmount = 0;

        System.out.println("Sub total: " + subTotal);

        for (Discount discount : discounts) {
            if (subTotal >= discount.getMinSubTotal()) {
                discountPercent = discount.getDiscountPercent();
                discountAmount = (subTotal * discountPercent) / 100;
            }
        }

        System.out.println("Discount percent: " + discountPercent);
        System.out.println("Discount amount: " + discountAmount);
        System.out.println("Total after discount: " + (subTotal - discountAmount));

        this.totalAmount = (subTotal - discountAmount);
    }
}
