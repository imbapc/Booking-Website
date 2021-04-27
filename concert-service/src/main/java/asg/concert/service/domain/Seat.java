package asg.concert.service.domain;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Seat {

    // TODO Implement this class.
	private String label;
	
	private boolean isBooked;
	
	private LocalDateTime date;
	
	private BigDecimal price;
	
	

	public Seat() {}

	public Seat(String label, boolean isBooked, LocalDateTime date, BigDecimal price) {
		this.label = label;
		this.isBooked = isBooked;
		this.date = date;
		this.price = price;
	}

}
