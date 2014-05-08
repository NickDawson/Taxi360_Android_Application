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
import sg.edu.astar.taxi360.ejb.PassengerEJB;
import sg.edu.astar.taxi360.entity.PassengerDestination;

/**
 * REST Web Service
 *
 * @author mido
 */
@Path("passengerDestinations")
@RequestScoped
public class PassengerDestinationResource {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of DriverResource
     */
    public PassengerDestinationResource() {
    }

    @EJB
    private PassengerEJB passengerEJB;

    @POST
    public void addDestination(PassengerDestination driverDestination) {
        passengerEJB.addDestination(driverDestination);
    }


    @DELETE
    @Path("{id}")
    public void deletetDestination(Long id) {
        passengerEJB.deletetDestination(id);
    }
    
    @GET
    @Path("{id}")
    public ArrayList<PassengerDestination> getDriver(@PathParam("id") Long id) {
        ArrayList<PassengerDestination> a=  passengerEJB.getPassengerDestinations(id);
        if(a!=null && a.size()>0){
            for(PassengerDestination d:a)
                d.setPassengerid(null);
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

