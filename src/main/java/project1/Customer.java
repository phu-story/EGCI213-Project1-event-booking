package project1;

import java.util.ArrayList;

public class Customer {
    private String id;
    private ArrayList<Booking> bookings;

    public Customer(String id) {
        this.id = id;
        this.bookings = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public ArrayList<Booking> getBookings() {
        return bookings;
    }

    public double getTotalAmount() {
        double total = 0;
        for (Booking booking : bookings) {
            total += booking.getTotalAmount();
        }
        return total;
    }
}
