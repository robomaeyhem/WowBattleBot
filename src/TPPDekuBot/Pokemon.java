/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TPPDekuBot;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Michael
 */
public class Pokemon implements Serializable {

    private int id;
    private int level;
    private int hp, attack, defense, spAttack, spDefense, speed;
    private int sleepLeft;
    private int attackStage = 0, defenseStage = 0, spAttackStage = 0, spDefenseStage = 0, speedStage = 0, evasion = 0, accuracy = 0;
    private int experience;
    private int maxHP;
    private String name;
    private Move move1, move2, move3, move4;
    private Type type1, type2;
    private boolean fainted, confused, attracted;
    private boolean flinched;
    private Status status;
    private Status moveStatus;
    private Move moveBuffer;
    private int toxicCount;
    private static final long serialVersionUID = -8670060699743627504L;
    private final Pokemon baseStatPokemon;
    private final ArrayList<PokemonBaseStat> baseStat;

    /**
     * Returns a level 1 Pokemon with ID.
     *
     * @param id ID of the Pokemon to generate.
     */
    public Pokemon(int id) {
        this.id = id;
        baseStatPokemon = Pokemon.getPokemon(id);
        this.level = 1;
        this.name = baseStatPokemon.name;
        this.attack = baseStatPokemon.attack;
        this.defense = baseStatPokemon.defense;
        this.spAttack = baseStatPokemon.spAttack;
        this.spDefense = baseStatPokemon.spDefense;
        this.speed = baseStatPokemon.speed;
        if (this.id != 292) {
            this.hp = baseStatPokemon.hp;
        } else {
            this.hp = 1;
        }
        this.type1 = baseStatPokemon.type1;
        this.type2 = baseStatPokemon.type2;
        this.maxHP = baseStatPokemon.maxHP;
        this.fainted = false;
        this.status = Status.NORMAL;
        sleepLeft = 0;
        this.experience = 1;
        this.moveBuffer = null;
        this.toxicCount = 0;
        this.moveStatus = Status.NORMAL;
        ArrayList<PokemonBaseStat> toLoad = null;
        while (toLoad == null) {
            try (FileInputStream f = new FileInputStream(BattleBot.BASE_PATH + "pokemonBaseStats.dat"); ObjectInputStream o = new ObjectInputStream(f)) {
                toLoad = (ArrayList<PokemonBaseStat>) o.readObject();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        baseStat = toLoad;
    }

    public Pokemon(int id, int level) {
        this(id);
        this.level = level;
        this.attack = calculateStats(level, Stats.ATTACK);
        this.defense = calculateStats(level, Stats.DEFENSE);
        this.spAttack = calculateStats(level, Stats.SP_ATTACK);
        this.spDefense = calculateStats(level, Stats.SP_DEFENSE);
        this.speed = calculateStats(level, Stats.SPEED);
        this.hp = calculateStats(level, Stats.HP);
        this.experience = ExpLevel.getExp(level);
        this.maxHP = this.hp;
        this.moveBuffer = null;
        this.toxicCount = 0;
        this.moveStatus = Status.NORMAL;
    }

    public Pokemon(int id, String name, int hp, int attack, int defense, int spAttack, int spDefense, int speed, Type type1, Type type2) {
        this.id = id;
        this.name = name;
        this.hp = hp;
        this.attack = attack;
        this.defense = defense;
        this.spAttack = spAttack;
        this.spDefense = spDefense;
        this.speed = speed;
        this.type1 = type1;
        this.type2 = type2;
        this.level = 1;
        this.fainted = false;
        this.status = Status.NORMAL;
        sleepLeft = 0;
        this.experience = 1;
        this.maxHP = hp;
        this.moveBuffer = null;
        this.toxicCount = 0;
        this.moveStatus = Status.NORMAL;
        baseStatPokemon = this;
        ArrayList<PokemonBaseStat> toLoad = null;
        while (toLoad == null) {
            try (FileInputStream f = new FileInputStream(BattleBot.BASE_PATH + "pokemonBaseStats.dat"); ObjectInputStream o = new ObjectInputStream(f)) {
                toLoad = (ArrayList<PokemonBaseStat>) o.readObject();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        baseStat = toLoad;
    }

    public void goToSleep() {
        this.status = Status.SLEEP;
        SecureRandom r = new SecureRandom();
        sleepLeft = r.nextInt((5 - 1) + 1) + 1;
    }

    public boolean isStillSleeping() {
        if (sleepLeft > 0) {
            sleepLeft--;
            return true;
        } else {
            return false;
        }
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getMoveStatus() {
        return this.moveStatus;
    }

    public void setMoveStatus(Status status) {
        this.moveStatus = status;
    }

    public void setConfused(boolean confused) {
        this.confused = confused;
    }

    public void setAttracted(boolean attracted) {
        this.attracted = attracted;
    }

    public boolean isConfused() {
        return confused;
    }

    public boolean isAttracted() {
        return attracted;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public void setFlinch(boolean flinch) {
        this.flinched = flinch;
    }

    public boolean isFlinched() {
        if (this.isFainted()) {
            this.flinched = false;
        }
        return this.flinched;
    }

    public void addExperience(int exp) {
        if (this.level == 100) {
            return;
        }
        this.experience += exp;
        int oldLevel = this.level;
        this.level = ExpLevel.getLevel(experience);
        int newLevel = this.level;
        if (oldLevel != newLevel) {
            this.attack = calculateStats(level, Stats.ATTACK);
            this.defense = calculateStats(level, Stats.DEFENSE);
            this.spAttack = calculateStats(level, Stats.SP_ATTACK);
            this.spDefense = calculateStats(level, Stats.SP_DEFENSE);
            this.speed = calculateStats(level, Stats.SPEED);
            int oldHP = calculateStats(oldLevel, Stats.HP);
            int newHP = calculateStats(newLevel, Stats.HP);
            this.hp += (newHP - oldHP);
            this.maxHP = newHP;
        }
    }

    public static int calculateExperience(boolean trainer, Pokemon user, Pokemon fainted) {
        double exp = -1;
        double a = (trainer) ? 1.5 : 1.0;
        int t = 1;
        int b = 0;
        try {
            b = ExpLevel.getBaseExp(user.getName());
        } catch (Exception ex) {
            b = 250;
        }
        int e = 1;
        int l = fainted.getLevel();
        int p = 1;
        int f = 1;
        int v = 1;
        //double v = (user.canEvolve()) ? 1.2 ; 1;
        int s = 1;
        exp = (a * t * b * e * l * p * f * v) / 7 * s;
        return (int) exp;
    }

    public int getMaxHP() {
        return this.maxHP;
    }

    public String attack(Pokemon opponent, Move move) {
        return attack(opponent, move, false);
    }

    public String attack(Pokemon opponent, Move move, boolean confused) {

        int attack = this.attack;
        int defense = opponent.getStat(Stats.DEFENSE);
        int spAttack = this.spAttack;
        int spDefense = opponent.getStat(Stats.SP_DEFENSE);
        SecureRandom rand = new SecureRandom();
        String toReturn = "";
        try {
            switch (this.moveStatus) {
                default:
                case NORMAL:
                    break;
                case ATTACK_NEXT_TURN:
                    if (moveBuffer == null) {
                        moveBuffer = move;
                        switch (move.getName()) {//todo
                            case "Fly":
                                break;
                            case "Bounce":
                                break;
                        }
                    } else {
                        moveBuffer = null;
                        this.moveStatus = Status.NORMAL;
                    }
                    break;
                case NO_MOVE_THIS_TURN:
                    this.setMoveStatus(Status.NORMAL);
                    toReturn = this.getName() + " must recharge!";
                    return toReturn;
            }
            switch (this.status) {
                default:
                case NORMAL:
                    break;
                case BURN:
                    attack = attack / 2;
                    break;
                case FREEZE:
                    int cure = rand.nextInt((100 - 1) + 1) + 1;
                    if (cure <= 20) {
                        this.status = Status.NORMAL;
                        toReturn = this.getName() + " thawed out! ";
                        break;
                    } else {
                        toReturn = this.getName() + " is frozen solid!";
                        return toReturn;
                    }
                case PARALYSIS:
                    int hit = rand.nextInt((100 - 1) + 1) + 1;
                    if (hit <= 25) {
                        toReturn = this.getName() + " is fully paralyzed!";
                        return toReturn;
                    }
                    break;
                case SLEEP:
                    boolean sleep = this.isStillSleeping();
                    if (sleep) {
                        return this.getName() + " is fast asleep!";
                    } else {
                        toReturn += this.getName() + " woke up! ";
                        this.setStatus(Status.NORMAL);
                    }
                    break;
            }
            if (this.isConfused() && !confused) {
                rand = new SecureRandom();
                int hit = rand.nextInt((100 - 1) + 1) + 1;
                if (hit > 50) {
                    double randModifier = 0.85 + (1.0 - 0.85) * rand.nextDouble();
                    double damageBuf = 0;
                    damageBuf = (2.0 * (double) this.level + 10.0) / 250.0;
                    damageBuf = damageBuf * ((double) attack / (double) defense);
                    damageBuf = damageBuf * (double) 40 + 2.0;
                    damageBuf = (damageBuf * randModifier);
                    int damage = (int) damageBuf;
                    this.damage(damage);
                    toReturn = this.getName() + " is confused! It hit itself in confusion! " + this.getName() + " has " + this.getStat(Stats.HP) + "HP left!";
                    return toReturn;
                }
            }
            if (this.isAttracted()) {
                rand = new SecureRandom();
                int hit = rand.nextInt((100 - 1) + 1) + 1;
                if (hit > 50) {
                    return this.getName() + " is immobilized by it's attraction! MVGame";
                }
            }
            toReturn += this.getName() + " used " + move.getName() + "!";
            if (opponent.isFainted()) {
                toReturn += " But there was no target...";
                return toReturn;
            }
            if (move.getAccuracy() != 100 && move.getAccuracy() != 0 && !confused) {
                rand = new SecureRandom();
                int hit = rand.nextInt((100 - 1) + 1) + 1;
                if (hit > move.getAccuracy()) {
                    toReturn += "\nThe attack missed!";
                    return toReturn;
                }
            }
            int effective1 = Pokemon.effectiveness(move.getType(), opponent.getType1());
            int effective2 = Pokemon.effectiveness(move.getType(), opponent.getType2());
            double effectiveness = 1.0;
            if (effective2 == -5) {
                switch (effective1) {
                    case 0:
                        effectiveness = 0.0;
                        break;
                    case -1:
                        effectiveness = 0.5;
                        break;
                    case 1:
                        effectiveness = 1.0;
                        break;
                    case 2:
                        effectiveness = 2.0;
                        break;
                    default:
                        effectiveness = 1.0;
                        break;
                }
            } else {
                switch (effective1) {
                    case 0:
                        effectiveness = 0.0;
                        break;
                    case -1:
                        switch (effective2) {
                            case 0:
                                effectiveness = 0.0;
                                break;
                            case -1:
                                effectiveness = 0.25;
                                break;
                            case 1:
                                effectiveness = 0.5;
                                break;
                            case 2:
                                effectiveness = 1.0;
                                break;
                        }
                        break;
                    case 1:
                        switch (effective2) {
                            case 0:
                                effectiveness = 0.0;
                                break;
                            case -1:
                                effectiveness = 0.5;
                                break;
                            case 1:
                                effectiveness = 1.0;
                                break;
                            case 2:
                                effectiveness = 2.0;
                                break;
                        }
                        break;
                    case 2:
                        switch (effective2) {
                            case 0:
                                effectiveness = 0.0;
                                break;
                            case -1:
                                effectiveness = 1.0;
                                break;
                            case 1:
                                effectiveness = 2.0;
                                break;
                            case 2:
                                effectiveness = 4.0;
                                break;
                        }
                        break;
                }
            }
            if (move.getCategory() == MoveCategory.STATUS) {
                if (effectiveness == 0) {
                    toReturn += "\nIt doesn't affect the opponent!";
                    return toReturn;
                }
                String effect = "";
                if (move.getEffectChance() != -1 && move.getEffect() != null) {
                    int chance = rand.nextInt(100) + 1;
                    if (chance <= move.getEffectChance() || move.getEffectChance() == 100) {
                        effect = move.getEffect().run(this, opponent, 0, move);
                    }
                }
                return toReturn += " " + effect;

            }
            if (effectiveness >= 2.0) {
                toReturn += "\nIt's Super Effective!";
            }
            if (effectiveness > 0 && effectiveness < 1) {
                toReturn += "\nIt's not very effective...";
            }
            if (effectiveness == 0) {
                toReturn += "\nIt doesn't affect the opponent!";
                return toReturn;
            }
            double stab = 1.0;
            if (this.type1 == move.getType() || this.type2 == move.getType()) {
                stab = 1.5;
            }
            double critical = 1.0;
            int randomNum = rand.nextInt((16 - 1) + 1) + 1;
            if (randomNum == 1) {
                critical = 1.5;
                toReturn += "\nCritical Hit!!";
            }
            rand = new SecureRandom();
            double randModifier = 0.85 + (1.0 - 0.85) * rand.nextDouble();
            double modifier = stab * effectiveness * critical * randModifier;
            double damageBuf = 0.0;
            if (move.getCategory() == MoveCategory.PHYSICAL) {
                damageBuf = (2.0 * (double) this.level + 10.0) / 250.0;
                damageBuf = damageBuf * ((double) attack / (double) defense);
                damageBuf = damageBuf * (double) move.getPower() + 2.0;
                damageBuf = (damageBuf * modifier);
            } else if (move.getCategory() == MoveCategory.SPECIAL) {
                damageBuf = (2.0 * (double) level + 10.0) / 250.0;
                damageBuf = damageBuf * ((double) spAttack / (double) spDefense);
                damageBuf = damageBuf * (double) move.getPower() + 2.0;
                damageBuf = (damageBuf * modifier);
            }
            int damage = (int) damageBuf;
            int damageBuffer = damage;
            if (damageBuffer > opponent.getStat(Stats.HP)) {
                damageBuffer = opponent.getStat(Stats.HP);
            }

            String effect = "";
            if (move.getEffectChance() != -1 && move.getEffect() != null) {
                int chance = rand.nextInt(100) + 1;
                if (chance <= move.getEffectChance() || move.getEffectChance() == 100) {
                    effect = move.getEffect().run(this, opponent, damageBuffer, move);
                }
            }

            opponent.damage(damage);
            toReturn += "\n" + opponent.getName() + " lost " + damageBuffer + "hp! " + opponent.getName() + " has " + opponent.getStat(Stats.HP) + "hp left!";
            if (effect != null && !effect.isEmpty()) {
                toReturn += " " + effect;
            }
            return toReturn;
        } finally {
            if (null != this.getStatus()) {
                switch (this.getStatus()) {
                    case TOXIC: {
                        this.toxicCount++;
                        int dmg = (int) ((double) this.getMaxHP() / ((double) this.toxicCount / (double) 16));
                        toReturn += " " + this.getName() + " lost " + dmg + "hp due to poison!";
                        break;
                    }
                    case POISON: {
                        int dmg = (int) ((double) this.getMaxHP() / (double) 8);
                        toReturn += " " + this.getName() + " lost " + dmg + "hp due to poison!";
                        break;
                    }
                    case BURN:
                        int dmg = (int) ((double) this.getMaxHP() / (double) 8);
                        toReturn += " " + this.getName() + " lost " + dmg + "hp due to it's burn!";
                        break;
                    default:
                        break;
                }
            }
            return toReturn;
        }
    }

    //-1 = 1/2 dmg, 0 = no effect, 1 = normal, 2 = super effective
    /**
     * Determines type effectiveness.
     *
     * @param attack Attacking type.
     * @param defense Defending type.
     * @return Integer dependent on how effective the type is. -1 is Not Very
     * effective, 0 is No Effect, 1 is Normal, 2 is Super Effective.
     */
    public static int effectiveness(Type attack, Type defense) {
        int toReturn = 1;
        switch (attack) {
            default:
                break;
            case NONE:
                toReturn = -5;
                break;
            case NORMAL:
                switch (defense) {
                    default:
                        toReturn = 1;
                        break;
                    case ROCK:
                    case STEEL:
                        toReturn = -1;
                        break;
                    case GHOST:
                        toReturn = 0;
                        break;
                }
                break;
            case FIRE:
                switch (defense) {
                    default:
                        toReturn = 1;
                        break;
                    case FIRE:
                    case WATER:
                    case ROCK:
                    case DRAGON:
                        toReturn = -1;
                        break;
                    case GRASS:
                    case ICE:
                    case BUG:
                    case STEEL:
                        toReturn = 2;
                        break;
                }
                break;
            case WATER:
                switch (defense) {
                    default:
                        toReturn = 1;
                        break;
                    case WATER:
                    case GRASS:
                    case DRAGON:
                        toReturn = -1;
                        break;
                    case FIRE:
                    case GROUND:
                    case ROCK:
                        toReturn = 2;
                        break;
                }
                break;
            case ELECTRIC:
                switch (defense) {
                    default:
                        toReturn = 1;
                        break;
                    case ELECTRIC:
                    case GRASS:
                    case DRAGON:
                        toReturn = -1;
                        break;
                    case GROUND:
                        toReturn = 0;
                        break;
                    case WATER:
                    case FLYING:
                        toReturn = 2;
                        break;
                }
                break;
            case GRASS:
                switch (defense) {
                    default:
                        toReturn = 1;
                        break;
                    case FIRE:
                    case GRASS:
                    case POISON:
                    case FLYING:
                    case BUG:
                    case DRAGON:
                    case STEEL:
                        toReturn = -1;
                        break;
                    case WATER:
                    case GROUND:
                    case ROCK:
                        toReturn = 2;
                        break;
                }
                break;
            case ICE:
                switch (defense) {
                    default:
                        toReturn = 1;
                        break;
                    case FIRE:
                    case WATER:
                    case ICE:
                    case STEEL:
                        toReturn = -1;
                        break;
                    case GRASS:
                    case GROUND:
                    case FLYING:
                    case DRAGON:
                        toReturn = 2;
                        break;
                }
                break;
            case FIGHTING:
                switch (defense) {
                    default:
                        toReturn = 1;
                        break;
                    case POISON:
                    case FLYING:
                    case PSYCHIC:
                    case BUG:
                    case FAIRY:
                        toReturn = -1;
                        break;
                    case GHOST:
                        toReturn = 0;
                        break;
                    case NORMAL:
                    case ICE:
                    case ROCK:
                    case DARK:
                    case STEEL:
                        toReturn = 2;
                        break;
                }
                break;
            case POISON:
                switch (defense) {
                    default:
                        toReturn = 1;
                        break;
                    case POISON:
                    case GROUND:
                    case ROCK:
                    case GHOST:
                        toReturn = -1;
                        break;
                    case STEEL:
                        toReturn = 0;
                        break;
                    case GRASS:
                    case FAIRY:
                        toReturn = 2;
                        break;
                }
                break;
            case GROUND:
                switch (defense) {
                    default:
                        toReturn = 1;
                        break;
                    case GRASS:
                    case BUG:
                        toReturn = -1;
                        break;
                    case FLYING:
                        toReturn = 0;
                        break;
                    case FIRE:
                    case ELECTRIC:
                    case POISON:
                    case ROCK:
                    case STEEL:
                        toReturn = 2;
                        break;
                }
                break;
            case FLYING:
                switch (defense) {
                    default:
                        toReturn = 1;
                        break;
                    case ELECTRIC:
                    case ROCK:
                    case STEEL:
                        toReturn = -1;
                        break;
                    case GRASS:
                    case FIGHTING:
                    case BUG:
                        toReturn = 2;
                        break;
                }
                break;
            case PSYCHIC:
                switch (defense) {
                    default:
                        toReturn = 1;
                        break;
                    case PSYCHIC:
                    case STEEL:
                        toReturn = -1;
                        break;
                    case DARK:
                        toReturn = 0;
                        break;
                    case FIGHTING:
                    case POISON:
                        toReturn = 2;
                        break;
                }
                break;
            case BUG:
                switch (defense) {
                    default:
                        toReturn = 1;
                        break;
                    case FIRE:
                    case FIGHTING:
                    case POISON:
                    case FLYING:
                    case GHOST:
                    case STEEL:
                    case FAIRY:
                        toReturn = -1;
                        break;
                    case GRASS:
                    case PSYCHIC:
                    case DARK:
                        toReturn = 2;
                        break;
                }
                break;
            case ROCK:
                switch (defense) {
                    default:
                        toReturn = 1;
                        break;
                    case FIGHTING:
                    case GROUND:
                    case STEEL:
                        toReturn = -1;
                        break;
                    case FIRE:
                    case ICE:
                    case FLYING:
                    case BUG:
                        toReturn = 2;
                        break;
                }
                break;
            case GHOST:
                switch (defense) {
                    default:
                        toReturn = 1;
                        break;
                    case DARK:
                        toReturn = -1;
                        break;
                    case NORMAL:
                        toReturn = 0;
                        break;
                    case PSYCHIC:
                    case GHOST:
                        toReturn = 2;
                        break;
                }
                break;
            case DRAGON:
                switch (defense) {
                    default:
                        toReturn = 1;
                        break;
                    case STEEL:
                        toReturn = -1;
                        break;
                    case FAIRY:
                        toReturn = 0;
                        break;
                    case DRAGON:
                        toReturn = 2;
                        break;
                }
                break;
            case DARK:
                switch (defense) {
                    default:
                        toReturn = 1;
                        break;
                    case FIGHTING:
                    case DARK:
                    case FAIRY:
                        toReturn = -1;
                        break;
                    case PSYCHIC:
                    case GHOST:
                        toReturn = 2;
                        break;
                }
                break;
            case STEEL:
                switch (defense) {
                    default:
                        toReturn = 1;
                        break;
                    case FIRE:
                    case WATER:
                    case ELECTRIC:
                    case STEEL:
                        toReturn = -1;
                        break;
                    case ICE:
                    case ROCK:
                    case FAIRY:
                        toReturn = 2;
                        break;
                }
                break;
            case FAIRY:
                switch (defense) {
                    default:
                        toReturn = 1;
                        break;
                    case FIRE:
                    case POISON:
                    case STEEL:
                        toReturn = -1;
                        break;
                    case FIGHTING:
                    case DRAGON:
                    case DARK:
                        toReturn = 2;
                        break;
                }
                break;
        }
        return toReturn;
    }

    public Type getType1() {
        return type1;
    }

    public Type getType2() {
        return type2;
    }

    public Move getMove1() {
        return move1;
    }

    public void setMove1(Move move1) {
        this.move1 = move1;
    }

    public Move getMove2() {
        return move2;
    }

    public void setMove2(Move move2) {
        this.move2 = move2;
    }

    public Move getMove3() {
        return move3;
    }

    public void setMove3(Move move3) {
        this.move3 = move3;
    }

    public Move getMove4() {
        return move4;
    }

    public void setMove4(Move move4) {
        this.move4 = move4;
    }

    public Move getMoveByNumber(int move) {
        switch (move) {
            case 1:
                return move1;
            case 2:
                return move2;
            case 3:
                return move3;
            case 4:
                return move4;
        }
        return null;
    }

    public int getId() {
        return id;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setHP(int hp) {
        this.hp = hp;
    }

    public void resetHP() {
        this.hp = maxHP;
    }

    public void heal() {
        resetHP();
    }

    public void damage(int hp) {
        this.hp = this.hp - hp;
        if (this.hp <= 0) {
            fainted = true;
            this.hp = 0;
        }
    }

    public boolean isFainted() {
        if (this.hp <= 0) {
            this.fainted = true;
        }
        return fainted;
    }

    public String setStage(Stats stat, int amt) {
        String result = this.getName() + "\'s ";
        boolean max = false;
        switch (stat) {
            case HP:
                break;
            case ATTACK:
                if (this.attackStage <= -6) {
                    max = true;
                    result += "Attack won\'t go any lower!";
                    break;
                }
                if (this.attackStage >= 6) {
                    max = true;
                    result += "Attack won\'t go any higher!";
                    break;
                }
                attackStage += amt;
                break;
            case DEFENSE:
                if (this.defenseStage <= -6) {
                    max = true;
                    result += "Defense won't go any lower!";
                    break;
                }
                if (this.defenseStage >= 6) {
                    max = true;
                    result += "Defense won't go any higher!";
                    break;
                }
                defenseStage += amt;
                break;
            case SP_ATTACK:
                if (this.spAttackStage <= -6) {
                    max = true;
                    result += "Special Attack won't go any lower!";
                    break;
                }
                if (this.spAttackStage >= 6) {
                    max = true;
                    result += "Special Attack won't go any higher!";
                    break;
                }
                spAttackStage += amt;
                break;
            case SP_DEFENSE:
                if (this.spDefenseStage <= -6) {
                    max = true;
                    result += "Special Defense won't go any lower!";
                    break;
                }
                if (this.spDefenseStage >= 6) {
                    max = true;
                    result += "Special Defense won't go any higher!";
                    break;
                }
                spDefenseStage += amt;
                break;
            case SPEED:
                if (this.speedStage <= -6) {
                    max = true;
                    result += "Speed won't go any lower!";
                    break;
                }
                if (this.speedStage >= 6) {
                    max = true;
                    result += "Speed won't go any higher!";
                    break;
                }
                speedStage += amt;
                break;
            case EVASION:
                if (this.evasion <= -6) {
                    max = true;
                    result += "Evasion won't go any lower!";
                    break;
                }
                if (this.evasion >= 6) {
                    max = true;
                    result += "Evasion won't go any higher!";
                    break;
                }
                evasion += amt;
                break;
            case ACCURACY:
                if (this.accuracy <= -6) {
                    max = true;
                    result += "Accuracy won't go any lower!";
                    break;
                }
                if (this.accuracy >= 6) {
                    max = true;
                    result += "Accuracy won't go any higher!";
                    break;
                }
                accuracy += amt;
                break;
        }
        return result;
    }

    public int getStage(Stats stat) {
        int toReturn = 0;
        switch (stat) {
            case HP:
                break;
            case ATTACK:
                toReturn = this.attackStage;
                break;
            case DEFENSE:
                toReturn = this.defenseStage;
                break;
            case SP_ATTACK:
                toReturn = this.spAttackStage;
                break;
            case SP_DEFENSE:
                toReturn = this.spDefenseStage;
                break;
            case SPEED:
                toReturn = this.speedStage;
                break;
            case EVASION:
                toReturn = this.evasion;
                break;
            case ACCURACY:
                toReturn = this.accuracy;
        }
        return toReturn;
    }

    public int getStat(Stats stat) {
        int toReturn = 0;
        switch (stat) {
            case HP:
                toReturn = this.hp;
                break;
            case ATTACK:
                toReturn = this.attack;
                break;
            case DEFENSE:
                toReturn = this.defense;
                break;
            case SP_ATTACK:
                toReturn = this.spAttack;
                break;
            case SP_DEFENSE:
                toReturn = this.spDefense;
                break;
            case SPEED:
                toReturn = this.speed;
                break;
            case EVASION:
            case ACCURACY:
                break;
        }
        return toReturn;
    }

    public String getName() {
        return name;
    }

    private int calculateStats(int level, Stats stat) {
        int iv = 31;
        int ev = 85;
        int base = baseStat.get(this.id).getStat(stat);
        if (stat == Stats.HP) {
            int result = (((iv + (2 * base) + (ev / 4)) * level) / 100) + level + 10;
            return result;
        } else {
            int result = (((iv + (2 * base) + (ev / 4)) * level) / 100) + 5;
            return result;
        }
    }

    private static HashMap<Integer, Pokemon> reloadPokemonList() {
        try (FileInputStream fileIn = new FileInputStream(BattleBot.BASE_PATH + "pokemonData.dat"); ObjectInputStream in = new ObjectInputStream(fileIn)) {
            HashMap<Integer, Pokemon> pokemon = (HashMap<Integer, Pokemon>) in.readObject();
            return pokemon;
        } catch (Exception ex) {
            System.err.println("[WARNING] Failed to read the Pokemon list!! " + ex);
            return null;
        }
    }

    private static HashMap<Integer, ArrayList<String>> reloadPokemonMoveList() {
        try (FileInputStream fileIn = new FileInputStream(BattleBot.BASE_PATH + "pokemonMovesList.dat"); ObjectInputStream in = new ObjectInputStream(fileIn)) {
            HashMap<Integer, ArrayList<String>> pokemon = (HashMap<Integer, ArrayList<String>>) in.readObject();
            return pokemon;
        } catch (Exception ex) {
            System.err.println("[WARNING] Failed to read the Pokemon Moves list!! " + ex);
            return null;
        }
    }

    public static HashMap<String, Move> reloadMoves() {
        try (FileInputStream fileIn = new FileInputStream(BattleBot.BASE_PATH + "pokemonMoves.dat"); ObjectInputStream in = new ObjectInputStream(fileIn)) {
            HashMap<String, Move> pokemon = (HashMap<String, Move>) in.readObject();
            return pokemon;
        } catch (Exception ex) {
            System.err.println("[WARNING] Failed to read the Pokemon Moves list!! " + ex);
            return null;
        }
    }

    public static Pokemon getPokemon(int id) {
        HashMap<Integer, Pokemon> pokemon = reloadPokemonList();
        while (pokemon == null) {
            pokemon = reloadPokemonList();
        }
        return pokemon.get(id);
    }

    public static Type typeConverter(String type) {
        type = type.toLowerCase();
        Type toReturn = Type.NONE;
        switch (type) {
            default:
            case "none":
                break;
            case "normal":
                toReturn = Type.NORMAL;
                break;
            case "fire":
                toReturn = Type.FIRE;
                break;
            case "fighting":
                toReturn = Type.FIGHTING;
                break;
            case "water":
                toReturn = Type.WATER;
                break;
            case "flying":
                toReturn = Type.FLYING;
                break;
            case "grass":
                toReturn = Type.GRASS;
                break;
            case "poison":
                toReturn = Type.POISON;
                break;
            case "electric":
                toReturn = Type.ELECTRIC;
                break;
            case "ground":
                toReturn = Type.GROUND;
                break;
            case "psychic":
                toReturn = Type.PSYCHIC;
                break;
            case "rock":
                toReturn = Type.ROCK;
                break;
            case "ice":
                toReturn = Type.ICE;
                break;
            case "bug":
                toReturn = Type.BUG;
                break;
            case "dragon":
                toReturn = Type.DRAGON;
                break;
            case "ghost":
                toReturn = Type.GHOST;
                break;
            case "dark":
                toReturn = Type.DARK;
                break;
            case "steel":
                toReturn = Type.STEEL;
                break;
            case "fairy":
                toReturn = Type.FAIRY;
                break;
        }
        return toReturn;
    }

    public static MoveCategory moveConverter(String type) {
        type = type.toLowerCase();
        MoveCategory toReturn = MoveCategory.PHYSICAL;
        switch (type) {
            case "physical":
            default:
                toReturn = MoveCategory.PHYSICAL;
                break;
            case "special":
                toReturn = MoveCategory.SPECIAL;
                break;
            case "status":
                toReturn = MoveCategory.STATUS;
                break;
        }
        return toReturn;
    }

    public static ArrayList<String> getCompatableMoves(int id) {
        HashMap<Integer, ArrayList<String>> pokemonMoves = reloadPokemonMoveList();
        while (pokemonMoves == null) {
            pokemonMoves = reloadPokemonMoveList();
        }
        return pokemonMoves.get(id);
    }

    public static Move getMove(String name) {
        HashMap<String, Move> moves = reloadMoves();
        while (moves == null) {
            moves = reloadMoves();
        }
        return moves.get(name);
    }

    public void setMove(int id, Move move) {
        switch (id) {
            case 1:
                this.move1 = move;
                break;
            case 2:
                this.move2 = move;
                break;
            case 3:
                this.move3 = move;
                break;
            case 4:
                this.move4 = move;
                break;
        }
    }

    public void assignMoves() {
        ArrayList<String> compatableMoves = getCompatableMoves(this.id);
        if (compatableMoves.size() < 4) {
            int size = 0;
            for (int i = 1; i <= 4; i++) {
                if (size > compatableMoves.size()) {
                    size = 0;
                }
                Move move = getMove(compatableMoves.get(size));
                while ((move.getCategory() == MoveCategory.STATUS && !move.hasMoveEffect()) || (move.getPower() == 0 || move.getAccuracy() == 0 || (move.getCategory() == MoveCategory.STATUS && !move.hasMoveEffect()))) {
                    size++;
                    if (size > compatableMoves.size()) {
                        size = 0;
                    }
                    move = getMove(compatableMoves.get(size));
                }
                this.setMove(i, getMove(compatableMoves.get(size)));
            }
            return;
        }
        SecureRandom rand = new SecureRandom();
        while (this.getMove1() == null || (this.getMove1().getCategory() == MoveCategory.STATUS && !this.getMove1().hasMoveEffect()) || (this.getMove1().getPower() == 0 || this.getMove1().getAccuracy() == 0 || this.getMove1().getCategory() == MoveCategory.STATUS)) {
            int index = rand.nextInt(compatableMoves.size());
            this.setMove1(getMove(compatableMoves.get(index)));
        }
        while (this.getMove2() == null || (this.getMove2().getCategory() == MoveCategory.STATUS && !this.getMove2().hasMoveEffect()) || (this.getMove2().getPower() == 0 || this.getMove2().getAccuracy() == 0 || this.getMove2().getCategory() == MoveCategory.STATUS || this.getMove2().equals(this.getMove1()))) {
            int index = rand.nextInt(compatableMoves.size());
            this.setMove2(getMove(compatableMoves.get(index)));
        }
        while (this.getMove3() == null || (this.getMove3().getCategory() == MoveCategory.STATUS && !this.getMove3().hasMoveEffect()) || (this.getMove3().getPower() == 0 || this.getMove3().getAccuracy() == 0 || this.getMove3().getCategory() == MoveCategory.STATUS || this.getMove3().equals(this.getMove1()) || this.getMove3().equals(this.getMove2()))) {
            int index = rand.nextInt(compatableMoves.size());
            this.setMove3(getMove(compatableMoves.get(index)));
        }
        while (this.getMove4() == null || (this.getMove4().getCategory() == MoveCategory.STATUS && !this.getMove4().hasMoveEffect()) || (this.getMove4().getPower() == 0 || this.getMove4().getAccuracy() == 0 || this.getMove4().getCategory() == MoveCategory.STATUS || this.getMove4().equals(this.getMove1()) || this.getMove4().equals(this.getMove2()) || this.getMove4().equals(this.getMove3()))) {
            int index = rand.nextInt(compatableMoves.size());
            this.setMove4(getMove(compatableMoves.get(index)));
        }

    }

    public void setStat(Stats stat, int level) {
        switch (stat) {
            case HP:
                this.hp = level;
                return;
            case ATTACK:
                this.attack = level;
                return;
            case DEFENSE:
                this.defense = level;
                return;
            case SP_ATTACK:
                this.spAttack = level;
                return;
            case SP_DEFENSE:
                this.spDefense = level;
                return;
            case SPEED:
                this.speed = level;
                return;
        }
    }

    public static final boolean isBannedPokemon(int poke) {
        int[] banned = {0, 132, 201, 202, 235, 292, 360, 665, 606};
        for (int el : banned) {
            if (poke == el) {
                return true;
            }
        }
        return false;
    }

    public static final int getCatchRate(String poke) {
        int toReturn = -1;
        try (FileInputStream f = new FileInputStream(BattleBot.BASE_PATH + "/pokemoncatchrate.wdu"); ObjectInputStream o = new ObjectInputStream(f)) {
            HashMap<String, Integer> c = (HashMap<String, Integer>) o.readObject();
            toReturn = c.get(poke);
        } catch (Exception ex) {
        }
        return toReturn;
    }
}
