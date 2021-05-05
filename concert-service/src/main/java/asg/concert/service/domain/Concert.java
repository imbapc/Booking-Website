package asg.concert.service.domain;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;

@Entity
@Table(name = "CONCERTS")
public class Concert {

    @Id
    @GeneratedValue
    private long id;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "IMAGE_NAME")
    private String imageName;

    @Column(name = "BLURB", length = 1000)
    private String blurb;

    @ElementCollection
    @CollectionTable(
            name = "CONCERT_DATES",
            joinColumns = @JoinColumn(name = "CONCERT_ID")
    )
    @org.hibernate.annotations.Fetch(
            org.hibernate.annotations.FetchMode.SUBSELECT)
    @Column(name = "DATE")
    private List<LocalDateTime> dates;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST})
    @org.hibernate.annotations.Fetch(
            org.hibernate.annotations.FetchMode.SUBSELECT)
    @JoinTable(name = "CONCERT_PERFORMER",
            joinColumns = @JoinColumn(name = "CONCERT_ID"),
            inverseJoinColumns = @JoinColumn(name = "PERFORMER_ID"))
    private List<Performer> performers;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private List<Booking> bookings;

    public Concert() {
    }

    public Concert(String title, String imageName, String blurb,
                   List<LocalDateTime> dates, List<Performer> performers,
                   List<Booking> bookings) {
        this.title = title;
        this.imageName = imageName;
        this.blurb = blurb;
        this.performers = performers;
        this.dates = dates;
        this.bookings = bookings;
    }

    public long getId() {
        return id;
    }
    
    public void setId(long id) {
    	this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getBlurb() {
        return blurb;
    }

    public void setBlurb(String blurb) {
        this.blurb = blurb;
    }

    public List<LocalDateTime> getDates() {
        return dates;
    }

    public void setDates(List<LocalDateTime> dates) {
        this.dates = dates;
    }

    public List<Performer> getPerformers() {
        return performers;
    }

    public void setPerformers(List<Performer> performers) {
        this.performers = performers;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }
}