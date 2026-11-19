# CodeAlpha_HotelReservationSystem

**Java Programming Internship — Task 4: Hotel Reservation System**
CodeAlpha | Student: Piyush Vishnupant Divase | ID: CA/DF1/433397

---

## Task Requirements Covered

| Requirement | Implementation |
|---|---|
| Design a system to search, book and manage hotel rooms | Console application with search → book → view → cancel flow |
| Room categorisation (Standard, Deluxe, Suite) | `RoomCategory` enum with tariff, features and 12 rooms over 3 floors |
| Allow users to make and cancel reservations | `Hotel.createReservation()` / `cancelReservation()` with auto booking IDs |
| Payment simulation and booking details view | `Payment` class (UPI / Card / Net banking / Cash) + receipt and booking detail screens |
| OOP + database/File I/O for bookings and availability | OOP model + CSV persistence in `data/reservations.csv`; availability is derived from stored date ranges |

## Project Structure

```
CodeAlpha_HotelReservationSystem/
├── src/
│   ├── RoomCategory.java          # Standard / Deluxe / Suite + tariff + features
│   ├── Room.java                  # Room number, category, floor
│   ├── Reservation.java           # Booking record, nights, totals, date-overlap logic
│   ├── Payment.java               # Payment gateway simulation (no real money)
│   ├── Hotel.java                 # Inventory, availability, booking, cancel, CSV I/O
│   └── HotelReservationSystem.java# Main class + console menu (entry point)
├── data/
│   └── reservations.csv           # Auto-created booking storage
├── run.sh
└── README.md
```

## Room Inventory & Tariff

| Category | Tariff / night | Rooms | Features |
|---|---|---|---|
| Standard | Rs. 2,000 | 101-104 | 1 double bed, AC, TV, free Wi-Fi, attached bathroom |
| Deluxe | Rs. 3,500 | 201-204 | 1 king bed, AC, 42" TV, free Wi-Fi, mini bar, city view |
| Suite | Rs. 6,000 | 301-304 | Bedroom + living area, 55" TV, mini bar, bathtub, balcony |

GST of **12%** is applied on the room total.

## How to Compile and Run

```bash
javac -encoding UTF-8 -d out src/*.java
java -cp out HotelReservationSystem
```

or `./run.sh`

## Menu Options

```
1. View Room Categories & Inventory  -> tariff and every room
2. Search Available Rooms            -> date range + category, shows only free rooms
3. Make a Reservation                -> summary, payment simulation, receipt, booking ID
4. View Booking Details              -> full detail of one booking ID
5. Cancel a Reservation              -> cancellation with 90% refund (10% charge)
6. View All Bookings                 -> all confirmed / cancelled bookings
7. Hotel Statistics                  -> occupancy, revenue, booking count
8. Save & Exit                       -> writes data/reservations.csv
```

## Sample Flow

```
 Check-in date (dd-MM-yyyy): 24-11-2026
 Check-out date (dd-MM-yyyy): 27-11-2026
 Category: 1) Standard  2) Deluxe  3) Suite  4) Any
 Choose category: 2

 Available rooms for 24-11-2026 to 27-11-2026  (3 night(s))
 201    Deluxe     Floor 2      3500.00 / night
 ...

----------------- BOOKING SUMMARY -----------------
 Guest      : Piyush Divase (9876543210)
 Room       : 201 - Deluxe (Floor 2)
 Nights     : 3
 Room total : Rs. 10,500.00
 GST (12%)  : Rs.  1,260.00
 TOTAL      : Rs. 11,760.00
---------------------------------------------------

================ PAYMENT RECEIPT ================
 Payment status : SUCCESS
 Amount paid    : Rs.11,760.00
 Method         : UPI
 Reference no   : TXN482913
================================================

 RESERVATION CONFIRMED! Your booking ID is BK1001
```

## Design Notes

- **Availability** is not stored as a separate flag; it is computed by checking the
  requested date range against every active reservation of a room (half-open interval
  overlap: `existing.checkIn < requested.checkOut && requested.checkIn < existing.checkOut`).
  This is why cancelled bookings automatically free the room.
- **Payment** is simulated only — it validates the format of the UPI ID / card number,
  prints gateway log lines and returns a transaction reference. No network call is made.
- **Persistence** is a pipe-separated CSV so guest names can safely contain commas.
  Bookings reload on start-up, so availability survives restarts.
- Every money value is rounded to 2 decimals through `Hotel.round2()`.
