package asg.concert.service.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "SEATS")
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

    public boolean getIsBooked() {
        return isBooked;
    }

    public void setIsBooked(boolean booked) {
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

    @Override
    public String toString() {
        return label;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Seat seat = (Seat) o;

        return new EqualsBuilder()
                .append(label, seat.label)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(label)
                .toHashCode();
    }
}