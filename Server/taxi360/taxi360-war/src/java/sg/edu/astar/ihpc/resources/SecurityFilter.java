/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sg.edu.astar.ihpc.resources;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Priority;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import sg.edu.astar.taxi360.ejb.CommonEJB;

/**
 *
 * @author mido
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
public class SecurityFilter implements ContainerRequestFilter {

    private static final String AUTH_HEADER = "AUTH_KEY";
    @Override
    public void filter(ContainerRequestContext requestContext) {
        try {
            String path = requestContext.getUriInfo().getPath(true);
            if ("auth".equalsIgnoreCase(path.split("/")[1]) || "common".equalsIgnoreCase(path.split("/")[1]) ) {
                return;
            }
            
            String accessKey = requestContext.getHeaderString(AUTH_HEADER);
            if (accessKey == null || "".equals(accessKey)) {
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("unauthorized!").build());
                return;
            }

            if (TokenKeeper.getInstance().contains(accessKey)) {
                return;
            }

            InitialContext ic = new InitialContext();
            CommonEJB commonEJB = (CommonEJB) ic.lookup("java:app/taxi360-ejb/CommonEJB!sg.edu.astar.taxi360.ejb.CommonEJB");
            if (commonEJB.validateAccessKey(accessKey)) {
                TokenKeeper.getInstance().put(accessKey);
            } else {
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("unauthorized!").build());
            }
        } catch (NamingException ex) {
            requestContext.abortWith(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("INTERNAL_SERVER_ERROR!").build());
            Logger.getLogger(SecurityFilter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
