package asg.concert.service.domain;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "BOOKINGS")
public class Booking {

    @Id
    @GeneratedValue
    private long id;

    private long concertId;
    private LocalDateTime date;
    private String bookingUser;

    @OneToMany(cascade = {CascadeType.MERGE})
    @JoinTable(name = "BOOKINGS_SEAT", joinColumns = @JoinColumn(name = "BOOKINGS_ID"), inverseJoinColumns = @JoinColumn(name = "SEATS_ID"))
    private List<Seat> seats = new ArrayList<>();

    public Booking() {
    }

    public Booking(long concertId, LocalDateTime date, String bookingUser, List<Seat> seats) {
        this.concertId = concertId;
        this.date = date;
        this.bookingUser = bookingUser;
        this.seats = seats;
    }

    public long getId() {
        return id;
    }

    public long getConcertId() {
        return concertId;
    }

    public void setConcertId(long concertId) {
        this.concertId = concertId;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }

    public String getBookingUser() {
        return bookingUser;
    }

    public void setBookingUser(String bookingUser) {
        this.bookingUser = bookingUser;
    }
}
