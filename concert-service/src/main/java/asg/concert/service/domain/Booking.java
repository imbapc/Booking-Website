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
    @OneToMany(targetEntity = Seat.class, mappedBy = "BOOKINGS", fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    private List<Seat> seats = new ArrayList<>();
    

    public Booking() {
    }
    
    public Booking(long id, long concertId, LocalDateTime date, List<Seat> seats) {
    	this.id = id;
    	this.concertId = concertId;
    	this.date = date;
    	this.seats = seats;
    }
    
    public long getId() {
    	return this.id;
    }
    
    public void setId(long id) {
    	this.id = id;
    }
    
    public long getConcertId(){
    	return this.concertId;
    }
    
    public void setConcertId(long concertId) {
    	this.concertId = concertId;
    }
    
    public LocalDateTime getDate() {
    	return this.date;
    }
    
    public void setDate(LocalDateTime date) {
    	this.date = date;
    }
    
    public List<Seat> getSeats(){
    	return this.seats;
    }
    
    public void setSeats(List<Seat> seats) {
    	this.seats = seats;
    }
}
