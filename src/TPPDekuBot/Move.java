/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TPPDekuBot;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Objects;

/**
 *
 * @author Michael
 */
public /*abstract*/ class Move implements Serializable {

    private String name;
    private MoveCategory category;
    private Type type;
    private MoveEffect effect;
    private int power;
    private int accuracy;
    private int pp;
    private int effectChance;
    private static final long serialVersionUID = 8484288705595459692L;

    public Move(String name, Type type, MoveCategory category, int power, int accuracy, int pp, MoveEffect effect, int effectChance) {
        // Set effect to null for moves without effects
        this.name = name;
        this.type = type;
        this.category = category;
        this.power = power;
        this.accuracy = accuracy;
        this.pp = pp;
        this.effect = effect;
        this.effectChance = effectChance;
    }

    public static boolean isValidMove(String input) {
        try {
            input = input.toLowerCase();
            input = input.substring(0, 6);
        } catch (Exception ex) {
            return false;
        }
        return (input.startsWith("!run") || input.equalsIgnoreCase("!move1") || input.equalsIgnoreCase("!move2") || input.equalsIgnoreCase("!move3") || input.equalsIgnoreCase("!move4"));
    }

    public String getName() {
        if (name.equalsIgnoreCase("Hidden-Power")) {
            return name + " (" + type + ")";
        }
        return name;
    }

    public int getPP() {
        return pp;
    }

    public void setPP(int pp) {
        this.pp = pp;
    }

    public MoveCategory getCategory() {
        return category;
    }

    public Type getType() {
        return type;
    }

    public int getPower() {
        return power;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public int getEffectChance() {
        return effectChance;
    }

    public MoveEffect getEffect() {
        return effect;
    }

//    public void doMove(Pokemon user, Pokemon opponent) {
//        preMove(user, opponent);
//        switch (this.category) {
//            case PHYSICAL:
//                break;
//            case SPECIAL:
//                break;
//            case STATUS:
//                break;
//        }
//        postMove(user, opponent);
//
//    }
//
//    abstract int preMove(Pokemon user, Pokemon opponent);
//
//    abstract String postMove(Pokemon user, Pokemon opponent);
    public static Move getMove(Pokemon pokemon, int id) {
        Move toReturn = null;
        switch (id) {
            case 1:
                toReturn = pokemon.getMove1();
                break;
            case 2:
                toReturn = pokemon.getMove2();
                break;
            case 3:
                toReturn = pokemon.getMove3();
                break;
            case 4:
                toReturn = pokemon.getMove4();
                break;
        }
        return toReturn;

    }

    public boolean hasMoveEffect() {
        return effect != null;
    }

    public static Move selectBestMove(Pokemon user, Pokemon opponent) {
        HashMap<Integer, Integer> movesSet = new HashMap<>();
        movesSet.put(1, Pokemon.effectiveness(user.getMove1().getType(), opponent.getType1()));
        movesSet.put(2, Pokemon.effectiveness(user.getMove2().getType(), opponent.getType1()));
        movesSet.put(3, Pokemon.effectiveness(user.getMove3().getType(), opponent.getType1()));
        movesSet.put(4, Pokemon.effectiveness(user.getMove4().getType(), opponent.getType1()));
        if (opponent.getType2() != Type.NONE) {
            int eff1 = movesSet.get(1), eff2 = movesSet.get(2), eff3 = movesSet.get(3), eff4 = movesSet.get(4);
            if (eff1 != 0) {
                eff1 += Pokemon.effectiveness(user.getMove1().getType(), opponent.getType2());
                movesSet.replace(1, eff1);
            }
            if (eff2 != 0) {
                eff2 += Pokemon.effectiveness(user.getMove2().getType(), opponent.getType2());
                movesSet.replace(2, eff2);
            }
            if (eff3 != 0) {
                eff3 += Pokemon.effectiveness(user.getMove3().getType(), opponent.getType2());
                movesSet.replace(3, eff3);
            }
            if (eff4 != 0) {
                eff4 += Pokemon.effectiveness(user.getMove4().getType(), opponent.getType2());
                movesSet.replace(4, eff4);
            }
        }
        if (movesSet.get(1) == 0) {
            movesSet.remove(1);
        }
        if (movesSet.get(2) == 0) {
            movesSet.remove(2);
        }
        if (movesSet.get(3) == 0) {
            movesSet.remove(3);
        }
        if (movesSet.get(4) == 0) {
            movesSet.remove(4);
        }
        int mostPowerful = -7;
        int power = -7;
        for (int el : movesSet.keySet()) {
            int movePower = Move.getMove(user, el).getPower() * movesSet.get(el);
            if (movePower > power) {
                mostPowerful = el;
                power = movePower;
            }
        }
        return Move.getMove(user, mostPowerful);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + Objects.hashCode(this.name);
        hash = 71 * hash + Objects.hashCode(this.category);
        hash = 71 * hash + Objects.hashCode(this.type);
        hash = 71 * hash + Objects.hashCode(this.effect);
        hash = 71 * hash + this.power;
        hash = 71 * hash + this.accuracy;
        hash = 71 * hash + this.pp;
        hash = 71 * hash + this.effectChance;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Move other = (Move) obj;
        if (this.power != other.power) {
            return false;
        }
        if (this.accuracy != other.accuracy) {
            return false;
        }
        if (this.pp != other.pp) {
            return false;
        }
        if (this.effectChance != other.effectChance) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (this.category != other.category) {
            return false;
        }
        if (this.type != other.type) {
            return false;
        }
        return Objects.equals(this.effect, other.effect);
    }

}
