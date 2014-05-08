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
import sg.edu.astar.taxi360.entity.Passenger;
import sg.edu.astar.taxi360.entity.PassengerDestination;

/**
 *
 * @author mido
 */
@Stateless
public class PassengerDestinationFacade extends AbstractFacade<PassengerDestination> {
    @PersistenceContext(unitName = "taxi360-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PassengerDestinationFacade() {
        super(PassengerDestination.class);
    }

    public ArrayList<PassengerDestination> getPassengerDestinations(Passenger d) {
        try {
            TypedQuery<PassengerDestination> tq = em.createNamedQuery("PassengerDestination.findByPassengerId", PassengerDestination.class);
            tq.setParameter("passengerid", d);
            return new ArrayList<PassengerDestination>(tq.getResultList());
        } catch (NoResultException e) {
            return  new ArrayList<PassengerDestination>();
        }
    }
    
}
