/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sg.edu.astar.taxi360.bo;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import sg.edu.astar.taxi360.entity.Ride;

/**
 *
 * @author mido
 */
@Stateless
public class RideFacade extends AbstractFacade<Ride> {

    @PersistenceContext(unitName = "taxi360-ejbPU")
    private EntityManager em;

    /**
     *
     * @return
     */
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     *
     */
    public RideFacade() {
        super(Ride.class);
    }
    
    public void flush(){
    
        getEntityManager().flush();
    }

    @Override
    public void create(Ride ride) {
        super.create(ride);
        getEntityManager().flush();
    }
    
    public boolean driverInRange(Ride ride, Double distance){
//        String sql = "Select r.* from Ride r where ST_DWithin(ST_GeographyFromText('SRID=4326;POINT(r.passenger_start_longitude r.passenger_start_latitude )'),"
//                + " ST_GeographyFromText('SRID=4326;POINT( "+ride.getPassengerEndLocation().getLongitude()+" "+ride.getPassengerEndLocation().getLatitude()+")'),"+distance+") and r.rideid='"+ride.getId()+"'";
        String sql = "Select r.* from Ride r where  extract(epoch from AGE(current_timestamp,r.createtime))>31   AND  ST_DWithin(r.passenger_start_location,"
                + " ST_GeographyFromText('SRID=4326;POINT( "+ride.getPassengerEndLocation().getLongitude()+" "+ride.getPassengerEndLocation().getLatitude()+")'),"+distance+") and r.rideid='"+ride.getId()+"'";
        System.out.println("sql stmt= "+sql);
        Query query = em.createNativeQuery(sql, Ride.class);
        return query.getResultList().size()>0;
    }

}
