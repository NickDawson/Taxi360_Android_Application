package sg.edu.astar.ihpc.taxidriver.utils;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.Serializable;

/**
 *
 * @author Thilak
 */
public class RequestDensity implements Serializable, Comparable {
    private Integer quarter;
    private Integer count;
    
    public RequestDensity(){}

    public RequestDensity(Integer q) {
        quarter = q;
        count = 0;
    }

    public Integer getQuarter() {
        return quarter;
    }

    public void setQuarter(Integer quarter) {
        this.quarter = quarter;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
    
    public void incrementCount() {
        this.count+=1;
    }

    @Override
    public int compareTo(Object o) {
        return count.compareTo(((RequestDensity)(o)).getCount());
    } 
}

