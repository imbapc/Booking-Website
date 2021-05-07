package asg.concert.service.services;

import asg.concert.common.types.BookingStatus;
import asg.concert.service.domain.*;
import asg.concert.common.dto.*;
import org.hibernate.Hibernate;
import org.hibernate.criterion.NotNullExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.persistence.*;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.CookieParam;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Path("/concert-service")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ConcertResource {

    private final static Logger LOGGER = LoggerFactory.getLogger(ConcertResource.class);

    @GET
    @Path("concerts/{id}")
    public Response retrieveConcert(@PathParam("id") Long id) {
        LOGGER.info("Retrieving Concert with id " + id);
        EntityManager em = PersistenceManager.instance().createEntityManager();

        Concert concert;
        ConcertDTO concertDTO;
        try {
            em.getTransaction().begin();

            concert = em.find(Concert.class, id);
            Hibernate.initialize(concert.getDates());
            if (concert == null) {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }
            concertDTO = ConcertMapper.toDTO(concert);
            em.getTransaction().commit();
        }
        catch(NullPointerException e){
            return Response.status(404).build();
        }
        finally {
            if(em!= null && em.isOpen()) {
                em.close();
            }
        }

        return Response.ok().entity(concertDTO).build();
    }

    @GET
    @Path("concerts")
    public Response retrieveAllConcerts(){
        LOGGER.info("Retrieve ALl concerts");
        EntityManager em = PersistenceManager.instance().createEntityManager();
        Query query = em.createQuery("select concert from Concert concert", Concert.class);

        List<Concert> concertList;
        List<ConcertDTO> concertDTOList = new ArrayList<>();

        try{
            em.getTransaction().begin();

            concertList = (List<Concert>) query.getResultList();
            if (concertList == null){
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }
            for (Concert concert: concertList){
                ConcertDTO concertDTO = ConcertMapper.toDTO(concert);
                concertDTOList.add(concertDTO);
            }
            em.getTransaction().commit();
        }
        catch(NullPointerException e){
            return Response.status(404).build();
        }
        finally {
            em.close();
        }

        return Response.ok().entity(concertDTOList).build();
    }

    @GET
    @Path("concerts/summaries")
    public Response retrieveConcertSummaries() {
    	LOGGER.info("Retrieving All Concert Summaries");
    	EntityManager em = PersistenceManager.instance().createEntityManager();
    	Query query = em.createQuery("select concert from Concert concert", Concert.class);
    	List<Concert> concertList;
    	List<ConcertSummaryDTO> concertSummaryDTOList = new ArrayList<>();
    	try {
    		em.getTransaction().begin();

            concertList = (List<Concert>)query.getResultList();
    		if (concertList == null) {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }
            for (Concert concert: concertList){
                ConcertSummaryDTO concertSummaryDTO = ConcertMapper.toSummaryDTO(concert);
                concertSummaryDTOList.add(concertSummaryDTO);
            }
    		em.getTransaction().commit();
    	}
        catch(NullPointerException e){
            return Response.status(404).build();
        }
    	finally{
    		em.close();
    	}
    	
    	return Response.ok().entity(concertSummaryDTOList).build();
    }

    @GET
    @Path("performers/{id}")
    public Response retrievePerformer(@PathParam("id") Long id){
        LOGGER.info("Retrieving Performer with id" + id);
        EntityManager em = PersistenceManager.instance().createEntityManager();
        Performer performer;
        PerformerDTO performerDTO;
        try{
            em.getTransaction().begin();

            performer = em.find(Performer.class, id);
            if (performer == null){
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }
            performerDTO = PerformerMapper.toDTO(performer);
            em.getTransaction().commit();
        }
        catch(NullPointerException e){
            return Response.status(404).build();
        }
        finally {
            em.close();
        }
        return Response.ok().entity(performerDTO).build();
    }

    @GET
    @Path("performers")
    public Response retrieveAllPerformers(){
        LOGGER.info("Retrieving all performers");
        EntityManager em = PersistenceManager.instance().createEntityManager();
        Query query = em.createQuery("select performer from Performer performer",Performer.class);
        List<Performer> performerList;
        List<PerformerDTO> performerDTOList = new ArrayList<>();
        try{
            em.getTransaction().begin();

            performerList = (List<Performer>) query.getResultList();
            if (performerList == null){
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }

            for (Performer performer: performerList){
                PerformerDTO performerDTO = PerformerMapper.toDTO(performer);
                performerDTOList.add(performerDTO);
            }
            em.getTransaction().commit();
        }
        catch(NullPointerException e){
            return Response.status(404).build();
        }
        finally {
            em.close();
        }
        return Response.ok().entity(performerDTOList).build();
    }

    @POST
    @Path("login")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(UserDTO userDTO){
        LOGGER.info("user " + userDTO.getUsername() + " tries to login with password " + userDTO.getPassword());
        EntityManager em = PersistenceManager.instance().createEntityManager();
        User user = UserMapper.toDomainModel(userDTO);
        String userPassword = user.getPassword();
        Response.ResponseBuilder builder = Response.ok();
        Query query = em.createQuery("select user from User user where user.username = :username", User.class).setParameter("username", user.getUsername());
        User userInDB;
        try{
            em.getTransaction().begin();

            userInDB = (User) query.getSingleResult();
            String passwordToCheck = userInDB.getPassword();
            if(!passwordToCheck.equals(userPassword)){
                throw new WebApplicationException(Response.Status.UNAUTHORIZED);
            }
            em.getTransaction().commit();
        }
        catch (NoResultException e){
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }
        finally{
            em.close();
        }
        builder.cookie(new NewCookie("auth", user.toString()));
        return builder.status(200).build();
    }
    @GET
    @Path("seat/{date}")
    public Response retrieveSeats(@QueryParam("status") BookingStatus bookingStatus, @PathParam("date")LocalDateTime date) {
        EntityManager em = PersistenceManager.instance().createEntityManager();
        Query query;
        List<SeatDTO> seatDTOList = new ArrayList<>();
        List<Seat> seatList;
        if (bookingStatus == BookingStatus.Any) {
            query = em.createQuery("select seat from Seat seat where seat.date = :date", Seat.class).setParameter("date", date);
        }
        else if (bookingStatus == BookingStatus.Booked){
            query = em.createQuery("select seat from Seat seat where seat.date = :date and seats.isBooked=true", Booking.class).setParameter("date", date);
        }
        else {
            query = em.createQuery("select seat from Seat seat where seat.date = :date and seats.isBooked=false", Booking.class).setParameter("date", date);
        }
        try{
            em.getTransaction().begin();
            seatList = (List<Seat>) query.getResultList();
            for (Seat seat: seatList){
                SeatDTO seatDTO = SeatMapper.toDTO(seat);
                seatDTOList.add(seatDTO);
            }
            em.getTransaction().commit();
        }
        finally{
            em.close();
        }
        return Response.ok().entity(seatDTOList).build();
    }
    
    @POST
    @Path("bookings")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response booking(BookingRequestDTO bookingRequestDTO, @CookieParam("auth") String username) {
        if (username == null) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }
        EntityManager em = PersistenceManager.instance().createEntityManager();
        Query query;
        List<Seat> seatList;
        query = em.createQuery("select booking from Booking booking" +  "inner join booking.seats seat" +
                "where booking.concertId= :concertId and booking.date= :date and seat.label IN (?1)", Booking.class)
                .setParameter("concertId", bookingRequestDTO.getConcertId()).setParameter("date", bookingRequestDTO.getDate())
                .setParameter("1", bookingRequestDTO.getSeatLabels());
        query.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT);

        try{
            em.getTransaction().begin();
            seatList = (List<Seat>) query.getResultList();
            for (Seat seat: seatList) {
                if(seat.getIsBooked()){
                    throw new WebApplicationException(Response.Status.FORBIDDEN);
                }
                seat.setIsBooked(true);
            }
            Booking booking = new Booking();
            booking.setConcertId(bookingRequestDTO.getConcertId());
            booking.setDate(bookingRequestDTO.getDate());
            booking.setSeats(seatList);
            em.persist(booking);
            em.getTransaction().setRollbackOnly();
            em.getTransaction().commit();
        }
        finally {
            em.close();
        }


        return Response.seeOther(URI.create(String.format("seats/%s?status=Booked", bookingRequestDTO.getDate().toString()))).build();
    }
}

