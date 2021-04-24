package asg.concert.service.services;

import asg.concert.common.dto.ConcertDTO;
import asg.concert.common.dto.ConcertSummaryDTO;
import asg.concert.common.dto.PerformerDTO;
import asg.concert.service.domain.Concert;
import asg.concert.service.domain.Performer;
import asg.concert.service.mapper.ConcertMapper;
import asg.concert.service.mapper.PerformerMapper;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/concert-service")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ConcertResource {

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

}
