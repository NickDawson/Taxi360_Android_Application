/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sg.edu.astar.ihpc.resources;

import javax.ejb.EJB;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Path;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import sg.edu.astar.taxi360.ejb.CommonEJB;
import sg.edu.astar.taxi360.ejb.PassengerEJB;
import sg.edu.astar.taxi360.entity.Credential;
import sg.edu.astar.taxi360.entity.Driver;
import sg.edu.astar.taxi360.entity.Passenger;

/**
 * REST Web Service
 *
 * @author Thilak
 */
@Path("auth")
@RequestScoped
public class LoginResource {

    @Context
    private UriInfo context;

    @EJB
    PassengerEJB passEJB;

    @EJB
    private CommonEJB commonEJB;

    /**
     * Creates a new instance of LoginResource
     */
    public LoginResource() {
    }

    /**
     * This method is called when a passenger tries to log-in via any social
     * network credential.
     *
     * @param pass The passenger to be logged in.
     * @return passenger id, if the passenger is already registered otherwise it
     * returns 0. If 0 is returned, the client must try to register the
     * passenger.
     */
//    @PermitAll
    @Path("socialMedia")
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Passenger loginViaSocialMedia(Passenger pass) {
        System.out.println("pass media="+pass.getEmailid());
                
        return passEJB.loginPassenger(pass);
    }

    /**
     * This method is called when the passenger log out of the system. This
     * method de-activates the access key.
     *
     * @param pass passenger to be logged out.
     * @return
     */
    @Path("plogout")
    @PUT
    @Consumes("application/json")
    @Produces("application/json")
    public void plogout(Passenger pass) {
        if ("normal".equalsIgnoreCase(pass.getRegistrationtype())) {
            commonEJB.logoutPassenger(pass);
        }
        TokenKeeper.getInstance().remove(pass.getAccesskey());
    }

    @Path("dlogout")
    @PUT
    @Consumes("application/json")
    @Produces("application/json")
    public void dlogout(Driver d) {
        commonEJB.logoutDriver(d);
        TokenKeeper.getInstance().remove(d.getAccesskey());
    }

    /**
     * This method is used to validate the driver.
     *
     * @param driver the driver being verified.
     * @return Driver value object.
     */
//    @PermitAll
    @POST
    @Produces("application/json")
    @Path("driver")
    public Driver validateDriver(Credential credential) {
        return commonEJB.validateDriver(credential);
    }

//    @PermitAll
    @POST
    @Produces("application/json")
    @Path("passenger")
    public Passenger validatePassenger(Credential credential) {
        return commonEJB.validatePassenger(credential);
    }

}
