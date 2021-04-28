package asg.concert.service.services;

import asg.concert.common.dto.*;
import asg.concert.service.domain.*;
import asg.concert.service.mapper.BookingMapper;
import asg.concert.service.mapper.ConcertMapper;
import asg.concert.service.mapper.PerformerMapper;
import asg.concert.service.mapper.SeatMapper;
import asg.concert.service.util.TheatreLayout;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Path("/concert-service")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ConcertResource {

    @Context
    private UriInfo uriInfo;

    @GET
    @Path("/concerts")
    public Response retrieveConcerts() {
        EntityManager em = PersistenceManager.instance().createEntityManager();
        List<Concert> allConcerts;
        List<ConcertDTO> result = new ArrayList<>();
        try {
            allConcerts = em.createQuery("SELECT c FROM Concert c").getResultList();
            if (allConcerts == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            for (Concert concert : allConcerts) {
                result.add(ConcertMapper.toConcertDto(concert));
            }
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return Response.ok().entity(result).build();
    }

    @GET
    @Path("concerts/{id}")
    public Response retrieveConcertById(@PathParam("id") long id) {
        EntityManager em = PersistenceManager.instance().createEntityManager();
        EntityTransaction tx = null;
        Concert concert;
        ConcertDTO dtoConcert;
        try {
            tx = em.getTransaction();
            tx.begin();
            concert = em.find(Concert.class, id);
            if (concert == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            dtoConcert = ConcertMapper.toConcertDto(concert);
            tx.setRollbackOnly();
            tx.commit();
        } finally {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return Response.ok().entity(dtoConcert).build();
    }

    @GET
    @Path("/concerts/summaries")
    public Response retrieveConcertSummaries() {
        EntityManager em = PersistenceManager.instance().createEntityManager();
        List<Concert> allConcerts;
        List<ConcertSummaryDTO> result = new ArrayList<>();
        try {
            allConcerts = em.createQuery("SELECT c FROM Concert c").getResultList();
            if (allConcerts == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            for (Concert concert : allConcerts) {
                result.add(ConcertMapper.toConcertSummaryDto(concert));
            }
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return Response.ok().entity(result).build();
    }

    @GET
    @Path("/performers")
    public Response retrievePerformers() {
        EntityManager em = PersistenceManager.instance().createEntityManager();
        List<Performer> allPerformers;
        List<PerformerDTO> result = new ArrayList<>();
        try {
            allPerformers = em.createQuery("SELECT p FROM Performer p").getResultList();
            if (allPerformers == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            for (Performer performer : allPerformers) {
                result.add(PerformerMapper.toPerformerDto(performer));
            }
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return Response.ok().entity(result).build();
    }

    @GET
    @Path("/performers/{id}")
    public Response retrievePerformerById(@PathParam("id") long id) {
        EntityManager em = PersistenceManager.instance().createEntityManager();
        EntityTransaction tx = null;
        Performer performer;
        PerformerDTO dtoPerformer;
        try {
            tx = em.getTransaction();
            tx.begin();
            performer = em.find(Performer.class, id);
            if (performer == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            dtoPerformer = PerformerMapper.toPerformerDto(performer);
            tx.setRollbackOnly();
            tx.commit();
        } finally {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return Response.ok().entity(dtoPerformer).build();
    }

    @POST
    @Path("/login")
    public Response login(UserDTO userDTO) {
        EntityManager em = PersistenceManager.instance().createEntityManager();
        EntityTransaction tx = null;
        List<User> userResults;
        User matchedUser;

        // Test if user exist
        try {
            tx = em.getTransaction();
            tx.begin();
            TypedQuery<User> userQuery = em
                    .createQuery(
                            "select u from User u where u.username = :username",
                            User.class)
                    .setParameter("username", userDTO.getUsername());
            userResults = userQuery.getResultList();
            if (userResults.isEmpty()) {
                return Response.status(Response.Status.UNAUTHORIZED).build();
            } else {
                matchedUser = userResults.get(0);
            }
            tx.setRollbackOnly();
            tx.commit();
        } finally {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            if (em != null && em.isOpen()) {
                em.close();
            }
        }

        // Test user password
        if (!userDTO.getPassword().equals(matchedUser.getPassword())) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        // Generate cookie and return Ok
        NewCookie cookie = new NewCookie("auth", userDTO.getUsername());
        return Response.ok().cookie(cookie).build();
    }

    @GET
    @Path("/bookings")
    public Response retrieveBookings(@CookieParam("auth") Cookie auth) {
        if (auth == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        EntityManager em = PersistenceManager.instance().createEntityManager();
        List<Booking> bookings;
        List<BookingDTO> results = new ArrayList<>();
        try {
            TypedQuery<Booking> bookingQuery = em
                    .createQuery(
                            "SELECT b FROM Booking b " +
                                    "WHERE b.bookingUser = :username",
                            Booking.class)
                    .setParameter("username", auth.getValue());
            bookings = bookingQuery.getResultList();
            for (Booking booking : bookings) {
                results.add(BookingMapper.toBookingDto(booking));
            }
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return Response.ok().entity(results).build();
    }

    @GET
    @Path("/bookings/{id}")
    public Response retrieveBookingById(@PathParam("id") long bookingId,
                                        @CookieParam("auth") Cookie auth) {
        EntityManager em = PersistenceManager.instance().createEntityManager();
        List<Booking> bookings;
        BookingDTO result;
        try {
            TypedQuery<Booking> bookingQuery = em
                    .createQuery(
                            "SELECT b FROM Booking b " +
                                    "WHERE b.id = :bookingId",
                            Booking.class)
                    .setParameter("bookingId", bookingId);
            bookings = bookingQuery.getResultList();
            if (bookings.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            Booking booking = bookings.get(0);
            if (!booking.getBookingUser().equals(auth.getValue())) {
                return Response.status(Response.Status.FORBIDDEN).build();
            }
            result = BookingMapper.toBookingDto(booking);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return Response.ok().entity(result).build();
    }

    @POST
    @Path("/bookings")
    public Response bookConcert(BookingRequestDTO bookingRequestDTO,
                                @CookieParam("auth") Cookie auth) {
        if (auth == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        // Check concert exists
        if (!checkConcertExists(bookingRequestDTO)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }


        // Check seats available
        if (!checkSeatAvailable(bookingRequestDTO)) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        // Create seats
        List<Seat> seats = new ArrayList<>();
        for (String seatLabel : bookingRequestDTO.getSeatLabels()) {
            char rowLabel = seatLabel.charAt(0);
            int rowNum = rowLabel - 'A' + 1;
            for (TheatreLayout.PriceBand priceBand : TheatreLayout.PRICE_BANDS) {
                if (rowNum <= priceBand.numRows) {
                    seats.add(new Seat(seatLabel, true, bookingRequestDTO.getDate(), priceBand.price));
                    break;
                } else {
                    rowNum = rowNum - priceBand.numRows;
                }
            }
        }

        // Create booking
        Booking booking = new Booking(
                bookingRequestDTO.getConcertId(),
                bookingRequestDTO.getDate(),
                auth.getValue(), seats);

        // Persist booking
        EntityManager em = PersistenceManager.instance().createEntityManager();
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();
            em.persist(booking);
            tx.commit();
        } finally {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            if (em != null && em.isOpen()) {
                em.close();
            }
        }

        URI u = URI.create(uriInfo.getRequestUri() + "/" + booking.getId());
        return Response.created(u).build();
    }

    @GET
    @Path("/seats/{date}")
    public Response retrieveBookedSeats(@PathParam("date") String dateTime,
                                        @QueryParam("status") String status) {
        LocalDateTime dateToQuery = LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        Boolean isBooked = status.equals("Booked");

        // retrieve seats in a given date
        EntityManager em = PersistenceManager.instance().createEntityManager();
        EntityTransaction tx = null;
        List<Seat> allSeats;
        List<SeatDTO> result = new ArrayList<>();
        try {
            tx = em.getTransaction();
            tx.begin();
            TypedQuery<Seat> seatQuery = em
                    .createQuery(
                            "SELECT s FROM Seat s " +
                                    "WHERE s.date = :date " +
                                    "AND s.isBooked = :isBooked",
                            Seat.class)
                    .setParameter("date", dateToQuery)
                    .setParameter("isBooked", isBooked);
            allSeats = seatQuery.getResultList();
            for (Seat seat : allSeats) {
                result.add(SeatMapper.toSeatDto(seat));
            }
            tx.setRollbackOnly();
            tx.commit();
        } finally {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return Response.ok().entity(result).build();
    }

    private boolean checkConcertExists(BookingRequestDTO bookingRequestDTO) {
        EntityManager em = PersistenceManager.instance().createEntityManager();
        List<Concert> concerts;
        try {
            TypedQuery<Concert> concertsQuery = em.createQuery(
                    "SELECT c FROM Concert c WHERE c.id = :concertId AND :concertDate member of c.dates",
                    Concert.class)
                    .setParameter("concertId", bookingRequestDTO.getConcertId())
                    .setParameter("concertDate", bookingRequestDTO.getDate());
            concerts = concertsQuery.getResultList();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return !concerts.isEmpty();
    }

    private boolean checkSeatAvailable(BookingRequestDTO bookingRequestDTO) {
        EntityManager em = PersistenceManager.instance().createEntityManager();
        List<String> seatLables = bookingRequestDTO.getSeatLabels();
        List<Booking> bookings = new ArrayList<>();
        try {
            TypedQuery<Booking> bookingQuery = em.createQuery(
                    "SELECT b FROM Booking b JOIN b.seats s WHERE b.concertId = :concertId AND s.label in :seatLables",
                    Booking.class)
                    .setParameter("concertId", bookingRequestDTO.getConcertId())
                    .setParameter("seatLables", seatLables);
            bookings = bookingQuery.getResultList();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return bookings.isEmpty();
    }
}
