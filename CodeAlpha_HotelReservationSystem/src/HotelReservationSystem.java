import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Scanner;

/**
 * Task 4 - Hotel Reservation System
 *
 * Search, book and manage hotel rooms:
 *   - room categorisation (Standard, Deluxe, Suite) with tariff and features
 *   - availability search for a date range
 *   - reservation and cancellation with booking ID
 *   - payment simulation (UPI / Card / Net banking / Cash) and payment receipt
 *   - OOP design + file I/O for storing bookings and availability
 */
public class HotelReservationSystem {

    private static final Scanner sc = new Scanner(System.in);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final Hotel hotel = new Hotel();

    public static void main(String[] args) {
        loadData();
        System.out.println("=========================================================");
        System.out.println("        HOTEL RESERVATION SYSTEM  (CodeAlpha)           ");
        System.out.println("=========================================================");
        System.out.println(" Welcome to Grand Stay Residency. Today is " + LocalDate.now().format(FMT));

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt(" Enter your choice: ");
            switch (choice) {
                case 1: System.out.println(hotel.getInventoryTable());  break;
                case 2: searchRooms();                                 break;
                case 3: makeReservation();                             break;
                case 4: viewBooking();                                 break;
                case 5: cancelBooking();                               break;
                case 6: viewAllBookings();                             break;
                case 7: showStatistics();                              break;
                case 8:
                    saveData();
                    running = false;
                    System.out.println("\n Thank you for using the Hotel Reservation System. Goodbye!");
                    break;
                default:
                    System.out.println(" Invalid choice. Please select 1-8.");
            }
        }
        sc.close();
    }

    private static void printMenu() {
        System.out.println("\n---------------------- MENU ----------------------");
        System.out.println(" 1. View Room Categories & Inventory");
        System.out.println(" 2. Search Available Rooms");
        System.out.println(" 3. Make a Reservation (with payment)");
        System.out.println(" 4. View Booking Details");
        System.out.println(" 5. Cancel a Reservation");
        System.out.println(" 6. View All Bookings");
        System.out.println(" 7. Hotel Statistics");
        System.out.println(" 8. Save & Exit");
        System.out.println("--------------------------------------------------");
    }

    // ---------------------------------------------------------------
    //  Search
    // ---------------------------------------------------------------
    private static void searchRooms() {
        System.out.println("\n--- SEARCH AVAILABLE ROOMS ---");
        LocalDate in = readDate(" Check-in date (dd-MM-yyyy): ");
        if (in == null) return;
        LocalDate out = readDate(" Check-out date (dd-MM-yyyy): ");
        if (out == null) return;
        if (!out.isAfter(in)) {
            System.out.println(" Check-out date must be after the check-in date.");
            return;
        }

        System.out.println(" Category: 1) Standard  2) Deluxe  3) Suite  4) Any");
        int c = readInt(" Choose category: ");
        RoomCategory category = categoryFromChoice(c);

        List<Room> available = hotel.findAvailableRooms(in, out, category);
        long nights = ChronoUnit.DAYS.between(in, out);

        System.out.println("\n------------------------------------------------------------");
        System.out.println(" Available rooms for " + in.format(FMT) + " to " + out.format(FMT)
                + "  (" + nights + " night(s))");
        System.out.println("------------------------------------------------------------");
        if (available.isEmpty()) {
            System.out.println(" Sorry, no rooms available for the selected criteria.");
            System.out.println("------------------------------------------------------------");
            return;
        }
        System.out.printf(" %-6s %-10s %-9s %s%n", "ROOM", "CATEGORY", "FLOOR", "TARIFF");
        System.out.println("------------------------------------------------------------");
        for (Room r : available) {
            System.out.println(" " + r);
        }
        System.out.println("------------------------------------------------------------");
        System.out.println(" " + available.size() + " room(s) available.");
        System.out.println("------------------------------------------------------------");
    }

    // ---------------------------------------------------------------
    //  Booking + payment
    // ---------------------------------------------------------------
    private static void makeReservation() {
        System.out.println("\n--- MAKE A RESERVATION ---");
        LocalDate in = readDate(" Check-in date (dd-MM-yyyy): ");
        if (in == null) return;
        LocalDate out = readDate(" Check-out date (dd-MM-yyyy): ");
        if (out == null) return;
        if (!out.isAfter(in)) {
            System.out.println(" Check-out date must be after the check-in date.");
            return;
        }

        System.out.println("\n Choose room category:");
        System.out.println("  1) Standard  - Rs.2,000 / night");
        System.out.println("  2) Deluxe    - Rs.3,500 / night");
        System.out.println("  3) Suite     - Rs.6,000 / night");
        RoomCategory category = categoryFromChoice(readInt(" Category: "));
        if (category == null) {
            System.out.println(" Invalid category.");
            return;
        }

        List<Room> available = hotel.findAvailableRooms(in, out, category);
        if (available.isEmpty()) {
            System.out.println(" No " + category.getDisplayName() + " rooms are available for these dates.");
            return;
        }
        System.out.println("\n Available " + category.getDisplayName() + " rooms:");
        for (Room r : available) {
            System.out.println("   " + r);
        }

        int roomNo = readInt(" Enter room number to book: ");
        Room room = hotel.getRoom(roomNo);
        if (room == null || room.getCategory() != category) {
            System.out.println(" Invalid room number for this category.");
            return;
        }
        if (!hotel.isRoomFree(roomNo, in, out)) {
            System.out.println(" Sorry, that room is already booked for the selected dates.");
            return;
        }

        String name = readLine(" Guest name : ");
        if (name.isEmpty()) {
            System.out.println(" Guest name cannot be empty.");
            return;
        }
        String phone = readLine(" Phone number: ");
        if (!phone.matches("\\d{10}")) {
            System.out.println(" Please enter a valid 10 digit phone number.");
            return;
        }

        long nights = ChronoUnit.DAYS.between(in, out);
        double roomTotal = Hotel.round2(nights * category.getPricePerNight());
        double tax = Hotel.round2(roomTotal * Hotel.getTaxRate());
        double total = Hotel.round2(roomTotal + tax);

        System.out.println("\n----------------- BOOKING SUMMARY -----------------");
        System.out.printf(" Guest      : %s (%s)%n", name, phone);
        System.out.printf(" Room       : %d - %s (Floor %d)%n", roomNo, category.getDisplayName(), room.getFloor());
        System.out.printf(" Check-in   : %s%n", in.format(FMT));
        System.out.printf(" Check-out  : %s%n", out.format(FMT));
        System.out.printf(" Nights     : %d%n", nights);
        System.out.printf(" Room total : Rs.%,10.2f%n", roomTotal);
        System.out.printf(" GST (%d%%)  : Rs.%,10.2f%n", (int) (Hotel.getTaxRate() * 100), tax);
        System.out.printf(" TOTAL      : Rs.%,10.2f%n", total);
        System.out.println("---------------------------------------------------");

        if (!readLine(" Proceed to payment? (y/n): ").equalsIgnoreCase("y")) {
            System.out.println(" Booking cancelled before payment.");
            return;
        }

        System.out.println("\n Payment method: 1) UPI  2) Card  3) Net Banking  4) Cash at hotel");
        Payment.Method method = paymentMethodFromChoice(readInt(" Choose method: "));
        if (method == null) {
            System.out.println(" Invalid payment method. Booking cancelled.");
            return;
        }

        String detail = "";
        if (method == Payment.Method.UPI) {
            detail = readLine(" Enter UPI ID (e.g. guest@bank): ");
        } else if (method == Payment.Method.CARD) {
            detail = readLine(" Enter 16 digit card number: ");
        } else if (method == Payment.Method.NET_BANKING) {
            detail = readLine(" Enter your bank name: ");
        } else {
            detail = "cash";
        }

        Payment.Result pay = Payment.process(total, method, detail);
        if (!pay.success) {
            System.out.println(" PAYMENT FAILED: " + pay.message);
            System.out.println(" Booking not created.");
            return;
        }

        Reservation r = hotel.createReservation(name, phone, roomNo, in, out);
        System.out.println("\n================ PAYMENT RECEIPT ================");
        System.out.println(" Payment status : SUCCESS");
        System.out.println(" Amount paid    : Rs." + String.format("%,.2f", r.getTotalAmount()));
        System.out.println(" Method         : " + method);
        System.out.println(" Reference no   : " + pay.reference);
        System.out.println(" Paid at        : " + pay.paidAt);
        System.out.println("================================================");
        System.out.println("\n RESERVATION CONFIRMED! Your booking ID is " + r.getBookingId());
        System.out.println(" Please keep this ID to view or cancel the booking.");
        saveData();
    }

    // ---------------------------------------------------------------
    //  View / cancel
    // ---------------------------------------------------------------
    private static void viewBooking() {
        System.out.println("\n--- VIEW BOOKING DETAILS ---");
        String id = readLine(" Enter booking ID (e.g. BK1001): ");
        Reservation r = hotel.findReservation(id);
        if (r == null) {
            System.out.println(" No booking found with ID " + id);
            return;
        }
        printBooking(r);
    }

    private static void cancelBooking() {
        System.out.println("\n--- CANCEL RESERVATION ---");
        String id = readLine(" Enter booking ID: ");
        Reservation r = hotel.findReservation(id);
        if (r == null) {
            System.out.println(" No booking found with ID " + id);
            return;
        }
        printBooking(r);
        if (!r.isActive()) {
            System.out.println(" This booking is already cancelled.");
            return;
        }
        if (!readLine(" Are you sure you want to cancel? (y/n): ").equalsIgnoreCase("y")) {
            System.out.println(" Cancellation aborted.");
            return;
        }
        double refund = hotel.cancelReservation(r);
        System.out.println("\n Booking " + r.getBookingId() + " has been CANCELLED.");
        System.out.printf(" Refund amount (90%%): Rs.%,.2f%n", refund);
        System.out.printf(" Cancellation charge: Rs.%,.2f%n", Hotel.round2(r.getTotalAmount() - refund));
        saveData();
    }

    private static void viewAllBookings() {
        System.out.println("\n-------------------------------------------------------------------------------");
        System.out.println("                             ALL BOOKINGS");
        System.out.println("-------------------------------------------------------------------------------");
        if (hotel.getReservations().isEmpty()) {
            System.out.println(" No bookings yet.");
            System.out.println("-------------------------------------------------------------------------------");
            return;
        }
        System.out.printf("%-9s %-18s %-6s %-10s %-12s %-12s %2s  %11s  %-9s%n",
                "ID", "GUEST", "ROOM", "CATEGORY", "CHECK-IN", "CHECK-OUT", "N", "AMOUNT", "STATUS");
        System.out.println("-------------------------------------------------------------------------------");
        for (Reservation r : hotel.getReservations()) {
            System.out.println(" " + r.toDisplayString());
        }
        System.out.println("-------------------------------------------------------------------------------");
        long active = hotel.getReservations().stream().filter(Reservation::isActive).count();
        System.out.println(" Total bookings: " + hotel.getReservations().size()
                + "   |   Confirmed: " + active
                + "   |   Cancelled: " + (hotel.getReservations().size() - active));
        System.out.println("-------------------------------------------------------------------------------");
    }

    private static void printBooking(Reservation r) {
        System.out.println("\n----------------- BOOKING DETAILS -----------------");
        System.out.printf(" Booking ID : %s%n", r.getBookingId());
        System.out.printf(" Guest      : %s (%s)%n", r.getGuestName(), r.getPhone());
        System.out.printf(" Room       : %d - %s%n", r.getRoomNumber(), r.getCategory().getDisplayName());
        System.out.printf(" Check-in   : %s%n", r.getCheckIn().format(FMT));
        System.out.printf(" Check-out  : %s%n", r.getCheckOut().format(FMT));
        System.out.printf(" Nights     : %d%n", r.getNights());
        System.out.printf(" Room total : Rs.%,10.2f%n", r.getRoomTotal());
        System.out.printf(" GST        : Rs.%,10.2f%n", r.getTax());
        System.out.printf(" Total paid : Rs.%,10.2f%n", r.getTotalAmount());
        System.out.printf(" Status     : %s%n", r.getStatus());
        System.out.println("---------------------------------------------------");
    }

    private static void showStatistics() {
        System.out.println("\n--- HOTEL STATISTICS ---");
        LocalDate today = LocalDate.now();
        long occupied = hotel.countOccupiedRooms(today);
        int totalRooms = hotel.getRooms().size();
        System.out.println(" Total rooms        : " + totalRooms);
        System.out.println(" Occupied today     : " + occupied);
        System.out.println(" Available today    : " + (totalRooms - occupied));
        System.out.printf(" Occupancy rate     : %.1f%%%n", (occupied * 100.0) / totalRooms);
        System.out.printf(" Confirmed revenue  : Rs.%,.2f%n", hotel.getTotalRevenue());
        System.out.println(" Total bookings     : " + hotel.getReservations().size());
    }

    // ---------------------------------------------------------------
    //  Helpers
    // ---------------------------------------------------------------
    /** Maps the menu number to a room category (null when the choice is invalid). */
    private static RoomCategory categoryFromChoice(int choice) {
        switch (choice) {
            case 1: return RoomCategory.STANDARD;
            case 2: return RoomCategory.DELUXE;
            case 3: return RoomCategory.SUITE;
            default: return null;
        }
    }

    /** Maps the menu number to a payment method (null when the choice is invalid). */
    private static Payment.Method paymentMethodFromChoice(int choice) {
        switch (choice) {
            case 1: return Payment.Method.UPI;
            case 2: return Payment.Method.CARD;
            case 3: return Payment.Method.NET_BANKING;
            case 4: return Payment.Method.CASH;
            default: return null;
        }
    }

    private static String readLine(String prompt) {
        System.out.print(prompt);
        return sc.hasNextLine() ? sc.nextLine().trim() : "";
    }

    private static int readInt(String prompt) {
        while (true) {
            String v = readLine(prompt);
            try {
                return Integer.parseInt(v);
            } catch (NumberFormatException e) {
                System.out.print(" Please enter a valid number: ");
            }
        }
    }

    private static LocalDate readDate(String prompt) {
        String v = readLine(prompt);
        try {
            LocalDate d = LocalDate.parse(v, FMT);
            if (d.isBefore(LocalDate.now())) {
                System.out.println(" Date cannot be in the past.");
                return null;
            }
            return d;
        } catch (DateTimeParseException e) {
            System.out.println(" Invalid date format. Use dd-MM-yyyy (e.g. 25-12-2026).");
            return null;
        }
    }

    private static void loadData() {
        try {
            hotel.load();
            if (!hotel.getReservations().isEmpty()) {
                System.out.println(" Loaded " + hotel.getReservations().size()
                        + " saved booking(s) from data/reservations.csv");
            }
        } catch (IOException e) {
            System.out.println(" Could not load saved bookings: " + e.getMessage());
        }
    }

    private static void saveData() {
        try {
            hotel.save();
        } catch (IOException e) {
            System.out.println(" Could not save bookings: " + e.getMessage());
        }
    }
}
