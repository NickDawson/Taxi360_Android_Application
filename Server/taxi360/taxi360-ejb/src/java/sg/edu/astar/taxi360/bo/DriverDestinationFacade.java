/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sg.edu.astar.taxi360.bo;

import java.util.ArrayList;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import sg.edu.astar.taxi360.entity.Driver;
import sg.edu.astar.taxi360.entity.DriverDestination;

/**
 *
 * @author mido
 */
@Stateless
public class DriverDestinationFacade extends AbstractFacade<DriverDestination> {

    @PersistenceContext(unitName = "taxi360-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DriverDestinationFacade() {
        super(DriverDestination.class);
    }

    public ArrayList<DriverDestination> getDriverDestinations(Driver d) {
        try {
            TypedQuery<DriverDestination> tq = em.createNamedQuery("DriverDestination.findByDriverId", DriverDestination.class);
            tq.setParameter("driverid", d);
            return new ArrayList<DriverDestination>(tq.getResultList());
        } catch (NoResultException e) {
            return  new ArrayList<DriverDestination>();
        }
    }

}
