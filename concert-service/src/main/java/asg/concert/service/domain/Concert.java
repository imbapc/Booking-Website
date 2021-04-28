package asg.concert.service.domain;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Entity
public class Concert implements Comparable<Concert> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private LocalDateTime date;

    @ManyToOne(fetch = FetchType.LAZY, cascade=CascadeType.ALL)
    protected Performer performer;

    public Concert(Long id, String title, LocalDateTime date, Performer performer) {
    	this.id = id;
        this.title = title;
        this.date = date;
        this.performer = performer;
    }

    @JsonCreator
    public Concert(@JsonProperty("title") String title,
                   @JsonProperty("date") LocalDateTime date,
                   @JsonProperty("performer") Performer performer) {
    	this(null,title, date, performer);
    }

    public Concert() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    public LocalDateTime getDate() {
        return date;
    }

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    public void setDate(LocalDateTime date) {
        this.date = date;
    }
    
    @JsonSerialize(using = PerformerSerializer.class)
    public Performer getPerformer() {
        return performer;
    }
    
    @JsonDeserialize(using = PerformerDeserializer.class)
    public void setPerformer(Performer performer) {
    	this.performer = performer;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Concert, id: ");
        buffer.append(id);
        buffer.append(", title: ");
        buffer.append(title);
        buffer.append(", date: ");
        buffer.append(date.toString());
        buffer.append(", featuring: ");
        buffer.append(performer.getName());

        return buffer.toString();
    }

    @Override
    public boolean equals(Object obj) {
        // Implement value-equality based on a Concert's title alone. ID isn't
        // included in the equality check because two Concert objects could
        // represent the same real-world Concert, where one is stored in the
        // database (and therefore has an ID - a primary key) and the other
        // doesn't (it exists only in memory).
        if (!(obj instanceof Concert))
            return false;
        if (obj == this)
            return true;

        Concert rhs = (Concert) obj;
        return new EqualsBuilder().
                append(title, rhs.title).
                isEquals();
    }

    @Override
    public int hashCode() {
        // Hash-code value is derived from the value of the title field. It's
        // good practice for the hash code to be generated based on a value
        // that doesn't change.
        return new HashCodeBuilder(17, 31).
                append(title).hashCode();
    }

    @Override
    public int compareTo(Concert concert) {
        return title.compareTo(concert.getTitle());
    }
}

