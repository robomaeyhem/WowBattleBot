/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TPPDekuBot;

import java.io.Serializable;

/**
 *
 * @author Michael
 */
public enum Type implements Serializable {

    NORMAL, FIRE, WATER, ELECTRIC, GRASS, ICE, FIGHTING, POISON, GROUND, FLYING, PSYCHIC, BUG, ROCK, GHOST, DRAGON, DARK, STEEL, FAIRY, NONE;

    @Override
    public String toString() {
        String toReturn = this.name().toLowerCase().replace("type.", "");
        char first = toReturn.charAt(0);
        first = Character.toUpperCase(first);
        toReturn = first + toReturn.substring(1);
        return toReturn;
    }
}
