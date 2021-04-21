package asg.concert.service.domain;

import javax.persistence.*;

@Entity
@Table(name = "BOOKINGS")
public class Booking {

    @Id
    @GeneratedValue
    private long id;

    public Booking() {
    }
}
