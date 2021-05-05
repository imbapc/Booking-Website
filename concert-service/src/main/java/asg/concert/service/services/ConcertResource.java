package asg.concert.service.services;

import asg.concert.service.domain.*;
import asg.concert.common.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

@Path("/concert-service/concerts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ConcertResource {

    private final static Logger LOGGER = LoggerFactory.getLogger(ConcertResource.class);

    @GET
    @Path("{id}")
    public Response retrieveConcert(@PathParam("id") Long id) {
        LOGGER.info("Retrieving Concert with id " + id);
        EntityManager em = PersistenceManager.instance().createEntityManager();

        Concert concert;
        ConcertDTO concertDTO;
        try {
            em.getTransaction().begin();

            concert = em.find(Concert.class, id);
            if (concert == null) {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }
            concertDTO = ConcertMapper.toDTO(concert);
            em.getTransaction().commit();
        } finally {
            if(em!= null && em.isOpen()) {
                em.close();
            }
        }

        return Response.ok().entity(concertDTO).build();
    }
    
    @GET
    @Path("summaries/{id}")
    public Response retrieveConcertSummaries(@PathParam("id") Long id) {
    	LOGGER.info("Retrieveing Concert Summary with id" + id);
    	EntityManager em = PersistenceManager.instance().createEntityManager();
    	Concert concert;
    	ConcertSummaryDTO concertSummaryDTO;
    	try {
    		em.getTransaction().begin();
    		
    		concert = em.find(Concert.class, id);
    		if (concert == null) {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }
    		concertSummaryDTO = ConcertMapper.toSummaryDTO(concert);
    		em.getTransaction().commit();
    	}finally {
    		em.close();
    	}
    	
    	return Response.ok().entity(concertSummaryDTO).build();
    }
}

