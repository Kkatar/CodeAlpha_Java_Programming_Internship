import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Core of the hotel: room inventory, availability search, booking,
 * cancellation and file I/O persistence.
 */
public class Hotel {

    private static final double TAX_RATE = 0.12;          // 12% GST
    private static final String RESERVATIONS_FILE = "data/reservations.csv";

    private final Map<Integer, Room> rooms = new LinkedHashMap<>();
    private final List<Reservation> reservations = new ArrayList<>();

    public Hotel() {
        buildInventory();
    }

    /** 4 rooms per category on 3 floors. */
    private void buildInventory() {
        int[] numbers = {101, 102, 103, 104, 201, 202, 203, 204, 301, 302, 303, 304};
        for (int n : numbers) {
            RoomCategory cat;
            if (n % 100 <= 4 && n / 100 == 1) {
                cat = RoomCategory.STANDARD;
            } else if (n / 100 == 2) {
                cat = RoomCategory.DELUXE;
            } else {
                cat = RoomCategory.SUITE;
            }
            rooms.put(n, new Room(n, cat, n / 100));
        }
    }

    public List<Room> getRooms() {
        return new ArrayList<>(rooms.values());
    }

    public Room getRoom(int number) {
        return rooms.get(number);
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    // ------------------------------------------------------------------
    //  Availability search
    // ------------------------------------------------------------------
    public List<Room> findAvailableRooms(LocalDate checkIn, LocalDate checkOut, RoomCategory category) {
        List<Room> available = new ArrayList<>();
        for (Room room : rooms.values()) {
            if (category != null && room.getCategory() != category) {
                continue;
            }
            if (isRoomFree(room.getRoomNumber(), checkIn, checkOut)) {
                available.add(room);
            }
        }
        available.sort(Comparator.comparingInt(Room::getRoomNumber));
        return available;
    }

    public boolean isRoomFree(int roomNumber, LocalDate checkIn, LocalDate checkOut) {
        for (Reservation r : reservations) {
            if (r.getRoomNumber() == roomNumber && r.overlaps(checkIn, checkOut)) {
                return false;
            }
        }
        return true;
    }

    // ------------------------------------------------------------------
    //  Booking
    // ------------------------------------------------------------------
    public Reservation createReservation(String guestName, String phone, int roomNumber,
                                        LocalDate checkIn, LocalDate checkOut) {
        Room room = rooms.get(roomNumber);
        if (room == null) {
            return null;
        }
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        double roomTotal = round2(nights * room.getCategory().getPricePerNight());
        double tax = round2(roomTotal * TAX_RATE);
        double total = round2(roomTotal + tax);

        Reservation r = new Reservation(nextBookingId(), guestName, phone, roomNumber,
                room.getCategory(), checkIn, checkOut, roomTotal, tax, total,
                Reservation.Status.CONFIRMED);
        reservations.add(r);
        return r;
    }

    public Reservation findReservation(String bookingId) {
        for (Reservation r : reservations) {
            if (r.getBookingId().equalsIgnoreCase(bookingId)) {
                return r;
            }
        }
        return null;
    }

    /** Cancels a booking. Returns the refund amount (0 if not applicable). */
    public double cancelReservation(Reservation r) {
        if (!r.isActive()) {
            return 0.0;
        }
        r.setStatus(Reservation.Status.CANCELLED);
        // 90% refund (10% cancellation charge)
        return round2(r.getTotalAmount() * 0.90);
    }

    private String nextBookingId() {
        int max = 1000;
        for (Reservation r : reservations) {
            try {
                int n = Integer.parseInt(r.getBookingId().replaceAll("\\D", ""));
                max = Math.max(max, n);
            } catch (NumberFormatException ignored) {
            }
        }
        return "BK" + (max + 1);
    }

    // ------------------------------------------------------------------
    //  Reporting helpers
    // ------------------------------------------------------------------
    public long countOccupiedRooms(LocalDate onDate) {
        LocalDate next = onDate.plusDays(1);
        return rooms.keySet().stream()
                .filter(n -> !isRoomFree(n, onDate, next))
                .count();
    }

    public double getTotalRevenue() {
        return round2(reservations.stream()
                .filter(Reservation::isActive)
                .mapToDouble(Reservation::getTotalAmount).sum());
    }

    public static double getTaxRate() {
        return TAX_RATE;
    }

    public static double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    public String getInventoryTable() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n------------------------------------------------------------\n");
        sb.append("                    ROOM CATEGORIES\n");
        sb.append("------------------------------------------------------------\n");
        for (RoomCategory c : RoomCategory.values()) {
            sb.append(String.format(" %-10s Rs.%,9.2f / night%n", c.getDisplayName(), c.getPricePerNight()));
            sb.append("            ").append(c.getFeatures()).append("\n");
        }
        sb.append("------------------------------------------------------------\n");
        sb.append(String.format(" %-6s %-10s %-9s %s%n", "ROOM", "CATEGORY", "FLOOR", "TARIFF"));
        sb.append("------------------------------------------------------------\n");
        for (Room r : rooms.values()) {
            sb.append(" ").append(r).append("\n");
        }
        sb.append("------------------------------------------------------------\n");
        sb.append(" Total rooms: ").append(rooms.size())
          .append("   |   Tax (GST): ").append((int) (TAX_RATE * 100)).append("%\n");
        sb.append("------------------------------------------------------------\n");
        return sb.toString();
    }

    // ------------------------------------------------------------------
    //  File I/O
    // ------------------------------------------------------------------
    public void save() throws IOException {
        Path file = Paths.get(RESERVATIONS_FILE);
        if (file.getParent() != null) {
            Files.createDirectories(file.getParent());
        }
        try (BufferedWriter w = Files.newBufferedWriter(file)) {
            w.write("bookingId|guestName|phone|roomNumber|category|checkIn|checkOut"
                    + "|roomTotal|tax|totalAmount|status");
            w.newLine();
            for (Reservation r : reservations) {
                w.write(r.toCsv());
                w.newLine();
            }
        }
    }

    public void load() throws IOException {
        reservations.clear();
        Path file = Paths.get(RESERVATIONS_FILE);
        if (!Files.exists(file)) {
            return;
        }
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line = reader.readLine(); // header
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                Reservation r = Reservation.fromCsv(line);
                if (r != null) {
                    reservations.add(r);
                }
            }
        }
    }
}
