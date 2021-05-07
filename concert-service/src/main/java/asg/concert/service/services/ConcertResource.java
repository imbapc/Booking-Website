package asg.concert.service.services;

import asg.concert.common.dto.*;
import asg.concert.common.types.BookingStatus;
import asg.concert.service.domain.*;
import asg.concert.service.jaxrs.LocalDateTimeParam;
import asg.concert.service.mapper.BookingMapper;
import asg.concert.service.mapper.ConcertMapper;
import asg.concert.service.mapper.PerformerMapper;
import asg.concert.service.mapper.SeatMapper;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
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
    private final static Logger LOGGER = LoggerFactory.getLogger(ConcertResource.class);

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
		try {
			em.getTransaction().begin();
			User user;
			try {
				user = em.createQuery("SELECT u FROM User u where u.username = :username AND u.password = :password", User.class)
						.setParameter("username", userDTO.getUsername())
						.setParameter("password", userDTO.getPassword())
						.getSingleResult();
			} catch (NoResultException e) { // No username-password match
				return Response.status(Response.Status.UNAUTHORIZED).build();
			} finally {
				em.getTransaction().commit();
			}

	        NewCookie cookie = new NewCookie("auth", userDTO.getUsername());
	        return Response.ok().cookie(cookie).build();
		} finally {
			em.close();
		}
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
    @Path("bookings")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response booking(@CookieParam("auth") Cookie auth, BookingRequestDTO bookingRequestDTO) {
        if (auth == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        EntityManager em = PersistenceManager.instance().createEntityManager();
        TypedQuery<Seat> query;
        List<Seat> seatList;
        Booking booking = new Booking();
        query = em.createQuery("select seat from Seat seat where seat.date = :date and seat.label IN (:labels)", Seat.class);
        query.setParameter("date", bookingRequestDTO.getDate()).setParameter("labels", bookingRequestDTO.getSeatLabels());
        query.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT);

        try{
            em.getTransaction().begin();
            seatList = (List<Seat>) query.getResultList();
            if (seatList.isEmpty()){return Response.status(Response.Status.NOT_FOUND).build();}
            else {
                for (Seat seat : seatList) {
                    if (seat.getIsBooked()) {
                        return Response.status(Response.Status.FORBIDDEN).build();
                    } else {
                        em.persist(seat);
                        seat.setIsBooked(true);

                    }
                }

            }
            em.persist(booking);
            booking.setConcertId(bookingRequestDTO.getConcertId());
            booking.setDate(bookingRequestDTO.getDate());
            booking.setSeats(seatList);
            em.getTransaction().setRollbackOnly();
            em.getTransaction().commit();
        }
        finally {
            em.close();
        }
        LOGGER.info("Length of seats", booking.getSeats().size());
        return Response.created(URI.create(String.format("seats/%s?status=Booked", bookingRequestDTO.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))).build();
    }

    @GET
    @Path("seats/{date}")
    public Response retrieveSeats(@PathParam("date") String inputDate, @QueryParam("status") BookingStatus bookingStatus){
        EntityManager em = PersistenceManager.instance().createEntityManager();
        LocalDateTime date = new LocalDateTimeParam(inputDate).getLocalDateTime();
        List<Seat> seatList;
        List<SeatDTO> seatDTOList = new ArrayList<>();
        TypedQuery<Seat> query;
        LOGGER.info("BookingStatus" + bookingStatus);
        if (bookingStatus.equals(BookingStatus.Any)){
            query = (TypedQuery<Seat>) em.createQuery("select seat from Seat seat where seat.date = :date");
            query.setParameter("date", date);
        }
        else if(bookingStatus.equals(BookingStatus.Booked)){
            query = (TypedQuery<Seat>) em.createQuery("select seat from Seat seat where seat.date = :date and seat.isBooked=true");
            query.setParameter("date", date);
        }
        else{
            query = (TypedQuery<Seat>) em.createQuery("select seat from Seat seat where seat.date = :date and seat.isBooked=false");
            query.setParameter("date", date);
        }
        seatList = query.getResultList();
        for (Seat seat: seatList){
            SeatDTO seatDTO = SeatMapper.toSeatDto(seat);
            seatDTOList.add(seatDTO);
        }
        return Response.ok().entity(seatDTOList).build();
    }

}
