package asg.concert.service.services;

import asg.concert.common.dto.*;
import asg.concert.service.domain.*;
import asg.concert.service.mapper.BookingMapper;
import asg.concert.service.mapper.ConcertMapper;
import asg.concert.service.mapper.PerformerMapper;
import asg.concert.service.mapper.SeatMapper;
import asg.concert.service.util.TheatreLayout;
import org.apache.commons.lang3.tuple.Pair;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.*;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Path("/concert-service")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ConcertResource {
    private static final List<Pair<AsyncResponse, ConcertInfoSubscriptionDTO>> subs = new Vector<>();
    private static final List<Pair<AsyncResponse, ConcertInfoSubscriptionDTO>> subsToRemove = new Vector<>();
    private final Map<Long, Map<LocalDateTime, Integer>> seatsCount = new HashMap<>();
    ExecutorService threadPool = Executors.newSingleThreadExecutor();


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
                                @CookieParam("auth") Cookie auth,
                                @Context UriInfo uriInfo) {
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

        long concertId = bookingRequestDTO.getConcertId();
        LocalDateTime concertDate = bookingRequestDTO.getDate();
        List<String> seatLabels = bookingRequestDTO.getSeatLabels();

        EntityManager em = PersistenceManager.instance().createEntityManager();
        EntityTransaction tx = null;

        // Find seats
        TypedQuery<Seat> seatQuery = em
                .createQuery(
                        "SELECT s FROM Seat s " +
                                "WHERE s.date = :date " +
                                "AND s.label in :seatLabels",
                        Seat.class)
                .setParameter("date", concertDate)
                .setParameter("seatLabels", seatLabels);
        List<Seat> seats = seatQuery.getResultList();

        // Set seats to be booked
        for (Seat seat : seats) {
            seat.setBooked(true);
        }

        // Create booking and link to seats
        Booking booking = new Booking(concertId, concertDate, auth.getValue(), seats);

        // Persist booking
        try {
            tx = em.getTransaction();
            tx.begin();
            em.persist(booking);
            tx.commit();
        } finally {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            if (em.isOpen()) {
                em.close();
            }
        }

        // Notify subs if there is any
        processSubscriber(concertId, concertDate);

        URI bookingUri = URI.create(uriInfo.getRequestUri() + "/" + booking.getId());
        return Response.created(bookingUri).build();
    }

    @GET
    @Path("/seats/{date}")
    public Response retrieveBookedSeats(@PathParam("date") String dateTime,
                                        @QueryParam("status") String status) {
        EntityManager em = PersistenceManager.instance().createEntityManager();

        // Set query
        LocalDateTime dateToQuery = LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        TypedQuery<Seat> seatQuery;

        if (status.equals("Any")) {
            seatQuery = em
                    .createQuery("SELECT s FROM Seat s WHERE s.date = :date", Seat.class)
                    .setParameter("date", dateToQuery);
        } else {
            seatQuery = em
                    .createQuery(
                            "SELECT s FROM Seat s WHERE s.date = :date AND s.isBooked = :isBooked", Seat.class)
                    .setParameter("date", dateToQuery)
                    .setParameter("isBooked", status.equals("Booked"));
        }

        // retrieve seats in a given date
        List<Seat> seatsResult;
        EntityTransaction tx = null;
        List<SeatDTO> result = new ArrayList<>();
        try {
            tx = em.getTransaction();
            tx.begin();
            seatsResult = seatQuery.getResultList();
            for (Seat seat : seatsResult) {
                result.add(SeatMapper.toSeatDto(seat));
            }
            tx.setRollbackOnly();
            tx.commit();
        } finally {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            if (em.isOpen()) {
                em.close();
            }
        }
        return Response.ok().entity(result).build();
    }

    @POST
    @Path("/subscribe/concertInfo")
    public void subscribeConcertInfo(ConcertInfoSubscriptionDTO concertInfoSubscriptionDTO,
                                     @CookieParam("auth") Cookie auth,
                                     @Suspended AsyncResponse sub) {
        // Check login status
        if (auth == null) {
            sub.resume(Response.status(Response.Status.UNAUTHORIZED).build());
        }

        long concertId = concertInfoSubscriptionDTO.getConcertId();
        LocalDateTime targetDate = concertInfoSubscriptionDTO.getDate();
        EntityManager em = PersistenceManager.instance().createEntityManager();
        Concert concert;
        boolean concertExist = true;
        boolean legalDate = true;
        try {
            concert = em.find(Concert.class, concertId);
            if (concert == null) {
                // Concert doesn't exist
                concertExist = false;
            } else if (!concert.getDates().contains(targetDate)) {
                // Concert date is illegal
                legalDate = false;
            }
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }

        if (concertExist && legalDate) {
            // Subscribe
            synchronized (subs) {
                subs.add(Pair.of(sub, concertInfoSubscriptionDTO));
            }
        } else {
            // Resume with BAD_REQUEST status
            sub.resume(Response.status(Response.Status.BAD_REQUEST).build());
        }
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
        List<Booking> bookings;
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

    private int countBookedSeats(LocalDateTime concertDate) {
        EntityManager em = PersistenceManager.instance().createEntityManager();
        List<Seat> seats;

        try {
            TypedQuery<Seat> seatQuery = em
                    .createQuery(
                            "SELECT s FROM Seat s " +
                                    "WHERE s.date = :date " +
                                    "AND s.isBooked = true",
                            Seat.class)
                    .setParameter("date", concertDate);
            seats = seatQuery.getResultList();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }

        return seats.size();
    }

    private void processSubscriber(long concertId, LocalDateTime concertDate) {
        threadPool.submit(() -> {
            if (subs.size() == 0) {
                return;
            }

            synchronized (subs) {
                int seatsBooked = countBookedSeats(concertDate);
                int totalSeats = TheatreLayout.NUM_SEATS_IN_THEATRE;

                for (Pair<AsyncResponse, ConcertInfoSubscriptionDTO> pair : subs) {
                    ConcertInfoSubscriptionDTO concertInfoSubscriptionDTO = pair.getRight();
                    AsyncResponse sub = pair.getLeft();

                    // Concert match
                    boolean concertMatch = concertInfoSubscriptionDTO.getConcertId() == concertId;

                    // Notification threshold match
                    double bookedPercentage = (double) seatsBooked * 100 / (double) totalSeats;
                    int notifyPercentage = concertInfoSubscriptionDTO.getPercentageBooked();
                    boolean thresholdMatch = bookedPercentage >= notifyPercentage;

                    if (concertMatch && thresholdMatch) {
                        int seatsRemaining = totalSeats - seatsBooked;
                        ConcertInfoNotificationDTO concertInfoNotificationDTO = new ConcertInfoNotificationDTO(seatsRemaining);
                        sub.resume(concertInfoNotificationDTO);
                        subsToRemove.add(pair);
                    }
                }
                subs.removeAll(subsToRemove);
                subsToRemove.clear();
            }
        });
    }
}
