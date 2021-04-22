package asg.concert.service.services;

import asg.concert.common.dto.ConcertDTO;
import asg.concert.service.domain.Concert;
import asg.concert.service.mapper.ConcertMapper;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/concert-service")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ConcertResource {

    @GET
    @Path("concerts/{id}")
    public Response retrieveConcert(@PathParam("id") long id) {
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
            dtoConcert = ConcertMapper.toDto(concert);
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
}
