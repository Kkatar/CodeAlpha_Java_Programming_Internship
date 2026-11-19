import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * A booking made by a guest for one room between two dates.
 */
public class Reservation {

    public enum Status { CONFIRMED, CANCELLED }

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private final String bookingId;
    private final String guestName;
    private final String phone;
    private final int roomNumber;
    private final RoomCategory category;
    private final LocalDate checkIn;
    private final LocalDate checkOut;
    private final double roomTotal;
    private final double tax;
    private final double totalAmount;
    private Status status;

    public Reservation(String bookingId, String guestName, String phone, int roomNumber,
                       RoomCategory category, LocalDate checkIn, LocalDate checkOut,
                       double roomTotal, double tax, double totalAmount, Status status) {
        this.bookingId = bookingId;
        this.guestName = guestName;
        this.phone = phone;
        this.roomNumber = roomNumber;
        this.category = category;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.roomTotal = roomTotal;
        this.tax = tax;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getPhone() {
        return phone;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public RoomCategory getCategory() {
        return category;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public long getNights() {
        return ChronoUnit.DAYS.between(checkIn, checkOut);
    }

    public double getRoomTotal() {
        return roomTotal;
    }

    public double getTax() {
        return tax;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isActive() {
        return status == Status.CONFIRMED;
    }

    /** True when the given date range overlaps this reservation. */
    public boolean overlaps(LocalDate from, LocalDate to) {
        return isActive() && checkIn.isBefore(to) && from.isBefore(checkOut);
    }

    // ------------------------------------------------------------------
    //  CSV  (one line per reservation, '|' separated to avoid name issues)
    // ------------------------------------------------------------------
    public String toCsv() {
        return String.join("|", bookingId, guestName.replace("|", " "), phone,
                String.valueOf(roomNumber), category.name(),
                checkIn.format(FMT), checkOut.format(FMT),
                String.valueOf(roomTotal), String.valueOf(tax),
                String.valueOf(totalAmount), status.name());
    }

    public static Reservation fromCsv(String line) {
        String[] p = line.split("\\|", -1);
        if (p.length < 11) return null;
        return new Reservation(p[0], p[1], p[2], Integer.parseInt(p[3]),
                RoomCategory.valueOf(p[4]),
                LocalDate.parse(p[5], FMT), LocalDate.parse(p[6], FMT),
                Double.parseDouble(p[7]), Double.parseDouble(p[8]), Double.parseDouble(p[9]),
                Status.valueOf(p[10]));
    }

    public String toDisplayString() {
        return String.format("%-9s %-18s %-6d %-10s %-12s %-12s %2d  %11.2f  %-9s",
                bookingId, guestName.length() > 17 ? guestName.substring(0, 17) : guestName,
                roomNumber, category.getDisplayName(),
                checkIn.format(FMT), checkOut.format(FMT), getNights(), totalAmount, status);
    }
}
