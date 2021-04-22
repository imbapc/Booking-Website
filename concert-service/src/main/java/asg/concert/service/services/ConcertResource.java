package asg.concert.service.services;

import asg.concert.common.dto.ConcertDTO;
import asg.concert.service.domain.Concert;
import asg.concert.service.mapper.ConcertMapper;

import javax.persistence.EntityManager;
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
        Concert concert;
        ConcertDTO dtoConcert;
        try {
            em.getTransaction().begin();

            concert = em.find(Concert.class, id);
            if (concert == null) {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }

            em.getTransaction().commit();
            dtoConcert = ConcertMapper.toDto(concert);
        } finally {
            em.close();
        }
        return Response.ok().entity(dtoConcert).build();
    }
}
