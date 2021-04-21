package asg.concert.service.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Seat {

    @Id
    @GeneratedValue
    private long id;

    @Column(name = "LABEL")
    private String label;

    @Column(name = "IS_BOOKED")
    private boolean isBooked;

    @Column(name = "DATE")
    private LocalDateTime date;

    @Column(name = "PRICE")
    private BigDecimal price;

    public Seat() {
    }

    public Seat(String label, boolean isBooked, LocalDateTime date, BigDecimal price) {
        this.label = label;
        this.isBooked = isBooked;
        this.date = date;
        this.price = price;
    }

    public long getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isBooked() {
        return isBooked;
    }

    public void setBooked(boolean booked) {
        isBooked = booked;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
