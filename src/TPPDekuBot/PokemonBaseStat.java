package TPPDekuBot;

import java.util.*;
import java.io.*;

/**
 * Holds the Pokemon base stats for easy getting.
 *
 * @author Michael
 */
public class PokemonBaseStat implements Serializable{

    private String name;
    private int id;
    private int attack, defense, spAttack, spDefense, speed, HP;

    public PokemonBaseStat(int id, String name, int attack, int defense, int spAttack, int spDefense, int speed, int HP) {
        this.name = name;
        this.id = id;
        this.attack = attack;
        this.defense = defense;
        this.spAttack = spAttack;
        this.spDefense = spDefense;
        this.speed = speed;
        this.HP = HP;
    }

    public int getStat(Stats stat) {
        int toReturn = -1;
        switch (stat) {
            case HP:
                toReturn = HP;
                break;
            case ATTACK:
                toReturn = attack;
                break;
            case DEFENSE:
                toReturn = defense;
                break;
            case SP_ATTACK:
                toReturn = spAttack;
                break;
            case SP_DEFENSE:
                toReturn = spDefense;
                break;
            case SPEED:
                toReturn = speed;
                break;
        }
        return toReturn;
    }

    public static int getStat(int id, Stats stat) {
        PokemonBaseStat base = null;
        while (base == null) {
            try (FileInputStream f = new FileInputStream(BattleBot.BASE_PATH + "pokemonBaseStats.dat"); ObjectInputStream o = new ObjectInputStream(f)) {
                ArrayList<PokemonBaseStat> list = (ArrayList<PokemonBaseStat>) o.readObject();
                base = list.get(id);
            } catch (Exception ex) {
                System.err.println("Failed to get the Pokemon Base Stat List! " + ex);
            }
        }
        return base.getStat(stat);
    }

}
