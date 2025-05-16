package project1;

import java.util.ArrayList;
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
        ArrayList<String> roomCodes = new ArrayList<String>();
        ArrayList<String> mealCodes = new ArrayList<String>();

        for (Item item : items) {
            if (item.getItemType() == Item.ItemType.ROOM) {
                roomCodes.add(item.getCode());
            } else if (item.getItemType() == Item.ItemType.MEAL) {
                mealCodes.add(item.getCode());
            }
        }

        if(roomCodes.size() != roomPerDay.length) {
            System.out.println("Room codes and room per day length do not match.");
            return;
        }

        if(mealCodes.size() != mealPerPersonPerDay.length) {
            System.out.println("Meal codes and meal per person per day length do not match.");
            return;
        }

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
            String roomCode = roomCodes.get(i);
            int roomCount = roomPerDay[i];

            for (Item item : items) {
                if (item.getCode().equals(roomCode)) {
                    roomTotal += item.getUnitPrice() * roomCount * day;
                    break;
                }
            }
        }

        roomTotal = (roomTotal * 1.1) * 1.07;

        //System.out.println("Total room price++: " + roomTotal);
        String stringRoomTotal =  "total room price++";
        System.out.printf("             %-20s  =   %,10.2f \n", stringRoomTotal, roomTotal);

        for (int i = 0; i < mealPerPersonPerDay.length; i++) {
            String mealCode = mealCodes.get(i);
            int mealCount = mealPerPersonPerDay[i];

            for (Item item : items) {
                if (item.getCode().equals(mealCode)) {
                    mealTotal += item.getUnitPrice() * mealCount * person * day;
                    break;
                }
            }
        }

       // System.out.println("Total meal price: " + mealTotal);
        String stringMealTotal =  "total meal price";
        System.out.printf("             %-20s  =   %,10.2f \n", stringMealTotal, mealTotal);

        double subTotal = roomTotal + mealTotal;
        double discountPercent = 0;
        double discountAmount = 0;

        //System.out.println("Sub total: " + subTotal);
        String stringSubTotal =  "sub-total";
        System.out.printf("             %-20s  =   %,10.2f \n", stringSubTotal, subTotal);

        for (Discount discount : discounts) {
            if (subTotal >= discount.getMinSubTotal()) {
                discountPercent = discount.getDiscountPercent();
                discountAmount = (subTotal * discountPercent) / 100;
            }
        }

        //System.out.println("Discount percent: " + discountPercent);
        //System.out.println("Discount amount: " + discountAmount);
        //System.out.println("Total after discount: " + (subTotal - discountAmount));

        if(discountPercent == 0) {
            System.out.printf("             %-20s  =   %,10.2f \n", "discount", discountAmount);
        } else {
            System.out.printf("             %-10s%-10s  =   %,10.2f \n", "discount", String.format("%.1f%%", discountPercent), discountAmount);
        }

        double total = subTotal - discountAmount;

        System.out.printf("             %-20s  =   %,10.2f \n", "total", total);

        this.totalAmount = total;
    }
}
