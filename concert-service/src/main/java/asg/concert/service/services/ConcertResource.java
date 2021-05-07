package asg.concert.service.services;

import asg.concert.common.dto.ConcertDTO;
import asg.concert.common.dto.ConcertSummaryDTO;
import asg.concert.common.dto.PerformerDTO;
import asg.concert.service.domain.Concert;
import asg.concert.service.domain.Performer;
import asg.concert.service.mapper.ConcertMapper;
import asg.concert.service.mapper.PerformerMapper;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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
            concertDTO = ConcertMapper.toConcertDto(concert);
            em.getTransaction().commit();
        } catch (NullPointerException e) {
            return Response.status(404).build();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }

        return Response.ok().entity(concertDTO).build();
    }

    @GET
    @Path("concerts")
    public Response retrieveAllConcerts() {
        LOGGER.info("Retrieve ALl concerts");
        EntityManager em = PersistenceManager.instance().createEntityManager();
        Query query = em.createQuery("select concert from Concert concert");

        List<Concert> concertList;
        List<ConcertDTO> concertDTOList = new ArrayList<>();

        try {
            em.getTransaction().begin();

            concertList = (List<Concert>) query.getResultList();
            if (concertList == null) {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }
            for (Concert concert : concertList) {
                ConcertDTO concertDTO = ConcertMapper.toConcertDto(concert);
                concertDTOList.add(concertDTO);
            }
        } catch (NullPointerException e) {
            return Response.status(404).build();
        } finally {
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

            concertList = (List<Concert>) query.getResultList();
            if (concertList == null) {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }
            for (Concert concert : concertList) {
                ConcertSummaryDTO concertSummaryDTO = ConcertMapper.toConcertSummaryDto(concert);
                concertSummaryDTOList.add(concertSummaryDTO);
            }
            em.getTransaction().commit();
        } catch (NullPointerException e) {
            return Response.status(404).build();
        } finally {
            em.close();
        }

        return Response.ok().entity(concertSummaryDTOList).build();
    }

    @GET
    @Path("performers/{id}")
    public Response retrievePerformer(@PathParam("id") Long id) {
        LOGGER.info("Retrieving Performer with id" + id);
        EntityManager em = PersistenceManager.instance().createEntityManager();
        Performer performer;
        PerformerDTO performerDTO;
        try {
            em.getTransaction().begin();

            performer = em.find(Performer.class, id);
            if (performer == null) {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }
            performerDTO = PerformerMapper.toPerformerDto(performer);
            em.getTransaction().commit();
        } catch (NullPointerException e) {
            return Response.status(404).build();
        } finally {
            em.close();
        }
        return Response.ok().entity(performerDTO).build();
    }

    @GET
    @Path("performers")
    public Response retrieveAllPerformers() {
        LOGGER.info("Retrieving all performers");
        EntityManager em = PersistenceManager.instance().createEntityManager();
        Query query = em.createQuery("select performer from Performer performer");
        List<Performer> performerList;
        List<PerformerDTO> performerDTOList = new ArrayList<>();
        try {
            em.getTransaction().begin();

            performerList = (List<Performer>) query.getResultList();
            if (performerList == null) {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }

            for (Performer performer : performerList) {
                PerformerDTO performerDTO = PerformerMapper.toPerformerDto(performer);
                performerDTOList.add(performerDTO);
            }
            em.getTransaction().commit();
        } catch (NullPointerException e) {
            return Response.status(404).build();
        } finally {
            em.close();
        }
        return Response.ok().entity(performerDTOList).build();
    }


}
