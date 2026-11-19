import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * Payment simulation: mimics a payment gateway that accepts a method and a
 * UPI ID / card number, and returns a transaction reference on success.
 * No real money or network call is involved.
 */
public class Payment {

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    private static final Random RANDOM = new Random();

    public enum Method {
        UPI, CARD, NET_BANKING, CASH
    }

    public static class Result {
        public final boolean success;
        public final String reference;
        public final String message;
        public final String paidAt;

        Result(boolean success, String reference, String message, String paidAt) {
            this.success = success;
            this.reference = reference;
            this.message = message;
            this.paidAt = paidAt;
        }
    }

    /**
     * Simulates processing a payment. Payment fails only when the method is UPI
     * and the supplied UPI ID is obviously invalid, or when the amount is not positive.
     * Results are logged to the console to imitate a gateway. (No real processing.)
     */
    public static Result process(double amount, Method method, String detail) {
        String now = LocalDateTime.now().format(FMT);

        if (amount <= 0) {
            return new Result(false, "-", "Invalid amount.", now);
        }
        if (method == Method.UPI) {
            if (detail == null || !detail.contains("@") || detail.length() < 5) {
                return new Result(false, "-", "Invalid UPI ID. Payment declined.", now);
            }
        }
        if (method == Method.CARD) {
            String digits = detail == null ? "" : detail.replaceAll("\\D", "");
            if (digits.length() < 12) {
                return new Result(false, "-", "Invalid card number. Payment declined.", now);
            }
        }

        System.out.println("   [gateway] Connecting to payment server ...");
        System.out.println("   [gateway] Authorising " + money(amount) + " via " + method + " ...");

        String reference = "TXN" + (100000 + RANDOM.nextInt(900000));
        return new Result(true, reference, "Payment successful.", now);
    }

    private static String money(double v) {
        return String.format("Rs.%,.2f", v);
    }
}
