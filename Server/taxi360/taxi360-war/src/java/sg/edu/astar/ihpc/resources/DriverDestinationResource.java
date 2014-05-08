/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sg.edu.astar.ihpc.resources;

import java.util.ArrayList;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import sg.edu.astar.taxi360.ejb.DriverEJB;
import sg.edu.astar.taxi360.entity.Driver;
import sg.edu.astar.taxi360.entity.DriverDestination;

/**
 * REST Web Service
 *
 * @author mido
 */
@Path("driverDestinations")
@RequestScoped
public class DriverDestinationResource {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of DriverResource
     */
    public DriverDestinationResource() {
    }

    @EJB
    private DriverEJB driverEJB;

    @POST
    public void addDestination(DriverDestination driverDestination) {
        driverEJB.addDestination(driverDestination);
    }


    @DELETE
    @Path("{id}")
    public void deletetDestination(@PathParam("id") Long id) {
        driverEJB.deletetDestination(id);
    }

    
    @GET
    @Path("{id}")
    public ArrayList<DriverDestination> getDriver(@PathParam("id") Long id) {
        ArrayList<DriverDestination> a=  driverEJB.getDriverDestinations(id);
        if(a!=null && a.size()>0){
            for(DriverDestination d:a)
                d.setDriverid(null);
        }
        return a;
//       DriverDestination d=  driverEJB.getDriverDestination(id);
//       if(d==null)
//           d= new DriverDestination();
//       return d;
    }
   
    
//    @GET
//    @Path("driver")
//    public ArrayList<DriverDestination> getDriver(Driver d) {
//        return driverEJB.getDriverDestinations(d);
//    }
    
}

