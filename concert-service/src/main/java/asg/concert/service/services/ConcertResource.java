package asg.concert.service.services;

import asg.concert.service.domain.*;
import asg.concert.common.dto.*;
import org.hibernate.Hibernate;
import org.hibernate.criterion.NotNullExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
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
        Query query = em.createQuery("select concert from Concert concert");

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
    	Query query = em.createQuery("select concert from Concert concert");
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
        Query query = em.createQuery("select performer from Performer performer");
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
        LOGGER.info("user " + userDTO.getUsername() + "tries to login with password" + userDTO.getPassword());
        EntityManager em = PersistenceManager.instance().createEntityManager();
        User user = UserMapper.toDomainModel(userDTO);
        Query query = em.createQuery("select user from User user where user.username = :username").setParameter("username", user.getUsername());
        User userInDB;
        try{
            em.getTransaction().begin();

            userInDB = (User) query.getSingleResult();
            if (userInDB == null){
                throw new WebApplicationException(Response.Status.UNAUTHORIZED);
            }
            else if(userInDB != user){
                throw new WebApplicationException(Response.Status.UNAUTHORIZED);
            }
        }
        finally{
            em.close();
        }
        return Response.seeOther(URI.create("/")).entity(userInDB).build();
    }

}

