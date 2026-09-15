import java.util.List;
import java.util.Set;

public class Reservation {
    private static int counter = 1000;
    private final String reservationId;
    private final Flight flight;
    private final User user;
    private final List<String> passengers;
    private final String seatClass;
    private final int baggageCount;
    private final double totalPrice;
    private String status; // Confirmed, Cancelled
    private final Set<Integer> bookedSeats;

    public Reservation(Flight flight, User user, List<String> passengers,
                       String seatClass, int baggageCount, double totalPrice, Set<Integer> bookedSeats) {
        this.reservationId = "RES" + (++counter);
        this.flight = flight;
        this.user = user;
        this.passengers = passengers;
        this.seatClass = seatClass;
        this.baggageCount = baggageCount;
        this.totalPrice = totalPrice;
        this.status = "Confirmed";
        this.bookedSeats = bookedSeats;
    }

    public String getReservationId() { return reservationId; }
    public Flight getFlight() { return flight; }
    public User getUser() { return user; }
    public List<String> getPassengers() { return passengers; }
    public String getSeatClass() { return seatClass; }
    public int getBaggageCount() { return baggageCount; }
    public double getTotalPrice() { return totalPrice; }
    public String getStatus() { return status; }
    public Set<Integer> getBookedSeats() { return bookedSeats; }

    public void cancel() {
        this.status = "Cancelled";
        this.flight.freeSeats(this.bookedSeats);
    }
}
