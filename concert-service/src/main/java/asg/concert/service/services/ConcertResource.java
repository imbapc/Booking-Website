package asg.concert.service.services;

import asg.concert.service.domain.Concert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

@Path("/concerts")
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
        try {
            em.getTransaction().begin();

            concert = em.find(Concert.class, id);
            if (concert == null) {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }

            em.getTransaction().commit();
        } finally {
            em.close();
        }


        return Response.ok().entity(concert).build();
    }

    @POST
    public Response createConcert(Concert concert) {
        EntityManager em = PersistenceManager.instance().createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(concert);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        LOGGER.debug("Created concert with id: " + concert.getId());
        return Response.created(URI.create("/concerts/" + concert.getId())).build();
    }

    @PUT
    public Response updateConcert(Concert concert) {
        LOGGER.info("Updating Concert with id " + concert.getId());
        EntityManager em = PersistenceManager.instance().createEntityManager();

        try {
            em.getTransaction().begin();

            Concert found = em.find(Concert.class, concert.getId());
            if (found == null) {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }
            em.merge(concert);

            em.getTransaction().commit();
        } finally {
            em.close();
        }

        return Response.noContent().build();
    }

    @DELETE
    @Path("{id}")
    public Response deleteConcert(@PathParam("id") Long id) {
        LOGGER.info("Deleting Concert with id " + id);
        EntityManager em = PersistenceManager.instance().createEntityManager();

        try {
            em.getTransaction().begin();

            Concert concert = em.find(Concert.class, id);
            if (concert == null) {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }
            em.remove(concert);

            em.getTransaction().commit();
        } finally {
            em.close();
        }

        return Response.noContent().build();
    }

    @DELETE
    public Response deleteConcerts() {
        EntityManager em = PersistenceManager.instance().createEntityManager();
        TypedQuery<Concert> concertQuery = em.createQuery("select c from Concert c", Concert.class);
        List<Concert> concerts = concertQuery.getResultList();
        try {
            em.getTransaction().begin();

            for (Concert concert : concerts) {
                em.remove(concert);
            }

            em.getTransaction().commit();
        } finally {
            em.close();
        }

        return Response.noContent().build();
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
				return Response.status(Status.UNAUTHORIZED).build();
			} finally {
				em.getTransaction().commit();
			}

			return Response.ok().cookie(newSession(user, em)).build();
		} finally {
			em.close();
		}
	}
}

