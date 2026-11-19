/**
 * Room categories offered by the hotel, with their tariff and features.
 */
public enum RoomCategory {

    STANDARD("Standard", 2000.00,
            "1 double bed, AC, TV, free Wi-Fi, attached bathroom"),
    DELUXE("Deluxe", 3500.00,
            "1 king bed, AC, 42\" TV, free Wi-Fi, mini bar, city view"),
    SUITE("Suite", 6000.00,
            "Bedroom + living area, AC, 55\" TV, free Wi-Fi, mini bar, bathtub, balcony");

    private final String displayName;
    private final double pricePerNight;
    private final String features;

    RoomCategory(String displayName, double pricePerNight, String features) {
        this.displayName = displayName;
        this.pricePerNight = pricePerNight;
        this.features = features;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public String getFeatures() {
        return features;
    }

    public static RoomCategory fromString(String value) {
        for (RoomCategory c : values()) {
            if (c.name().equalsIgnoreCase(value) || c.displayName.equalsIgnoreCase(value)) {
                return c;
            }
        }
        return null;
    }
}
