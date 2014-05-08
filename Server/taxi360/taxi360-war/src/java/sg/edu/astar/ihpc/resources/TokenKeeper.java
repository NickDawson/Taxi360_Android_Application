/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sg.edu.astar.ihpc.resources;

import java.util.HashSet;

/**
 *
 * @author mido
 */
public class TokenKeeper {

    private HashSet<String> hs;

    private static TokenKeeper instance = null;

    public boolean contains(String s) {
        return hs.contains(s);
    }

    public void put(String s) {
        synchronized (this) {
            hs.add(s);
        }
    }

    public void remove(String s) {
        synchronized (this) {
            hs.remove(s);
        }
    }

    private TokenKeeper() {
        hs = new HashSet<String>();
    }

    public static TokenKeeper getInstance() {
        if (instance == null) {
            instance = new TokenKeeper();
        }
        return instance;
    }

}
