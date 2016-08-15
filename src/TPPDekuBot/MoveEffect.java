package TPPDekuBot;

import java.io.Serializable;
import java.security.SecureRandom;

public interface MoveEffect extends Serializable {

    /**
     * Defines a Move Effect for a particular move.
     *
     * @param user User who used the move.
     * @param opponent Opponent who received the move.
     * @param damage Damage done to the opponent in terms of HP lost.
     * @param move Move used.
     * @param battle Battle Object (for getting Weather, user teams, etc)
     * @return Text for the BattleBot to return to the user.
     */
    String run(Pokemon user, Pokemon opponent, int damage, Move move, Battle battle);
}

class MoveEffects {

    /**
     * Calculates and returns the amount of damage for a specific move.
     *
     * @param user User Pokemon who is using the move.
     * @param opponent Pokemon who is receiving the move.
     * @param move Move the User is using.
     * @param power Power of the move. Specify this in case of moves that vary
     * with power due to certain conditions.
     * @param category Move category.
     * @param crit Critical Hit or not
     * @return Damage move will do.
     */
    private static int calcDamage(Pokemon user, Pokemon opponent, Move move, int power, MoveCategory category, boolean crit) {
        if (category == MoveCategory.STATUS) {
            return 0;
        }
        SecureRandom rand = new SecureRandom();
        double effectiveness = getEffectiveness(move, opponent);
        double stab = 1.0;
        if (user.getType1() == move.getType() || user.getType2() == move.getType()) {
            stab = 1.5;
        }
        double critical = crit ? 1.5 : 1;
        rand = new SecureRandom();
        double randModifier = 0.85 + (1.0 - 0.85) * rand.nextDouble();
        double modifier = stab * effectiveness * critical * randModifier;
        double damageBuf = 0.0;
        damageBuf = (2.0 * (double) user.getLevel() + 10.0) / 250.0;
        damageBuf = damageBuf * ((double) user.getStat((category == MoveCategory.PHYSICAL) ? Stats.ATTACK : Stats.SP_ATTACK) / (double) user.getStat((category == MoveCategory.PHYSICAL) ? Stats.DEFENSE : Stats.SP_DEFENSE));
        damageBuf = damageBuf * (double) power + 2.0;
        damageBuf = (damageBuf * modifier);
        int damage = (int) damageBuf;
        int damageBuffer = damage;
        if (damageBuffer > opponent.getStat(Stats.HP)) {
            damageBuffer = opponent.getStat(Stats.HP);
        }
        return damage;
    }

    /**
     * Gets the effectiveness of the move against the opponent.
     *
     * @param move Move being used
     * @param opponent Pokemon to apply the move to
     * @return >=2 is Super Effective, >0 and &lt;1 is Not Very Effective, 0 is
     * No effect, 1 is normal effectiveness.
     */
    private static double getEffectiveness(Move move, Pokemon opponent) {
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
        return effectiveness;
    }

    private static String getEffectivenessText(double effectiveness) {
        if (effectiveness >= 2.0) {
            return "\nIt's Super Effective!";
        }
        if (effectiveness > 0 && effectiveness < 1) {
            return "\nIt's not very effective...";
        }
        if (effectiveness == 0) {
            return "\nIt doesn't affect the opponent!";
        }
        return "";
    }
    public static MoveEffect FLINCH = (Pokemon user, Pokemon opponent, int damage, Move move, Battle battle) -> {
        opponent.setFlinch(true);
        return "";
    };

    public static MoveEffect HEAL_HALF = (Pokemon user, Pokemon opponent, int damage, Move move, Battle battle) -> {
        int amt = damage / 2;
        if (amt > user.getMaxHP() || (user.getStat(Stats.HP) + amt) > user.getMaxHP()) {
            amt = (user.getMaxHP() - user.getStat(Stats.HP));
            user.setHP(user.getMaxHP());
        } else {
            user.setHP(user.getStat(Stats.HP) + amt);
        }
        return amt == 0 ? user.getName() + "'s HP is already full!" : (user.getName() + " gained " + amt + " HP!");
    };
    public static MoveEffect EXPLOSION = (Pokemon user, Pokemon opponent, int damage, Move move, Battle battle) -> {
        user.setHP(0);
        return (user.getName() + " fainted! KAPOW ");
    };
    public static MoveEffect RECOIL_25 = (Pokemon user, Pokemon opponent, int damage, Move move, Battle battle) -> {
        int amt = damage / 4;
        if (amt > user.getStat(Stats.HP)) {
            amt = user.getStat(Stats.HP);
        }
        user.damage(amt);
        return user.getName() + " lost " + amt + "hp due to Recoil!";
    };
    public static MoveEffect RECOIL_33 = (Pokemon user, Pokemon opponent, int damage, Move move, Battle battle) -> {
        int amt = damage / 3;
        if (amt > user.getStat(Stats.HP)) {
            amt = user.getStat(Stats.HP);
        }
        user.damage(amt);
        return user.getName() + " lost " + amt + "hp due to Recoil!";
    };
    public static MoveEffect RECOIL_50 = (Pokemon user, Pokemon opponent, int damage, Move move, Battle battle) -> {
        int amt = damage / 2;
        if (amt > user.getStat(Stats.HP)) {
            amt = user.getStat(Stats.HP);
        }
        user.damage(amt);
        return user.getName() + " lost " + amt + "hp due to Recoil!";
    };
    public static MoveEffect STRUGGLE = (Pokemon user, Pokemon opponent, int damage, Move move, Battle battle) -> {
        int amt = user.getMaxHP() / 4;
        if (amt > user.getStat(Stats.HP)) {
            amt = user.getStat(Stats.HP);
        }
        user.damage(amt);
        return user.getName() + " lost " + amt + "hp due to Recoil!";
    };
    public static MoveEffect RECHARGE = (Pokemon user, Pokemon opponent, int damage, Move move, Battle battle) -> {
        user.setMoveStatus(Status.NO_MOVE_THIS_TURN);
        return "";
    };
    public static MoveEffect SLEEP = (Pokemon user, Pokemon opponent, int damage, Move move, Battle battle) -> {
        if (opponent.getStatus() == Status.SLEEP) {
            return opponent.getName() + " is already asleep!";
        }
        if (opponent.getStatus() == Status.NORMAL) {
            opponent.goToSleep();
            return opponent.getName() + " fell asleep!";
        }
        return "But it failed!";
    };
    public static MoveEffect TOXIC = (Pokemon user, Pokemon opponent, int damage, Move move, Battle battle) -> {
        if (opponent.getType1() == Type.STEEL || opponent.getType2() == Type.STEEL || opponent.getType1() == Type.POISON || opponent.getType2() == Type.POISON) {
            return "But it failed!";
        }
        if (opponent.getStatus() == Status.NORMAL) {
            opponent.setStatus(Status.TOXIC);
            return opponent.getName() + " was badly poisioned!";
        } else {
            return "But it failed!";
        }
    };
    public static MoveEffect BURN = (Pokemon user, Pokemon opponent, int damage, Move move, Battle battle) -> {
        if (opponent.getStatus() == Status.NORMAL && (opponent.getType1() != Type.FIRE || opponent.getType2() != Type.FIRE) && !opponent.isFainted()) {
            opponent.setStatus(Status.BURN);
            return opponent.getName() + " was burned!";
        } else if (move.getCategory() == MoveCategory.STATUS) {
            return "But it failed!";
        } else {
            return "";
        }
    };
    public static MoveEffect PARALYZE = (Pokemon user, Pokemon opponent, int damage, Move move, Battle battle) -> {
        if (opponent.getStatus() == Status.NORMAL && (opponent.getType1() != Type.ELECTRIC || opponent.getType2() != Type.ELECTRIC) && !opponent.isFainted()) {
            opponent.setStatus(Status.PARALYSIS);
            return opponent.getName() + " was paralyzed!";
        } else if (move.getCategory() == MoveCategory.STATUS) {
            return "But it failed!";
        } else {
            return "";
        }
    };
    public static MoveEffect FREEZE = (Pokemon user, Pokemon opponent, int damage, Move move, Battle battle) -> {
        if (opponent.getStatus() == Status.NORMAL && (opponent.getType1() != Type.ICE || opponent.getType2() != Type.ICE) && !opponent.isFainted()) {
            opponent.setStatus(Status.FREEZE);
            return opponent.getName() + " was frozen solid!";
        } else if (move.getCategory() == MoveCategory.STATUS) {
            return "But it failed!";
        } else {
            return "";
        }
    };
    public static MoveEffect CONFUSE_TARGET = (Pokemon user, Pokemon opponent, int damage, Move move, Battle battle) -> {
        if (opponent.isConfused()) {
            return opponent.getName() + " is already confused!";
        } else {
            opponent.setConfused(true);
            return opponent.getName() + " was confused!";
        }
    };
    public static MoveEffect POISON = (Pokemon user, Pokemon opponent, int damage, Move move, Battle battle) -> {
        if (opponent.getType1() == Type.STEEL || opponent.getType2() == Type.STEEL || opponent.getType1() == Type.POISON || opponent.getType2() == Type.POISON) {
            if (move.getCategory() == MoveCategory.STATUS) {
                return "But it failed!";
            } else {
                return "";
            }
        }
        if (opponent.getStatus() != Status.NORMAL && !opponent.isFainted()) {
            opponent.setStatus(Status.POISON);
            return opponent.getName() + " was poisioned!";
        } else if (move.getCategory() == MoveCategory.STATUS) {
            return "But it failed!";
        } else {
            return "";
        }
    };
    public static MoveEffect TRI_ATTACK = (Pokemon user, Pokemon opponent, int damage, Move move, Battle battle) -> {
        if (opponent.getStatus() != Status.NORMAL) {
            int rand = new SecureRandom().nextInt(2);
            Status status = null;
            switch (opponent.getType1()) {
                case FIRE:
                    switch (opponent.getType2()) {
                        case ICE: //interesting combo, regardless:
                            status = Status.PARALYSIS;
                            break;
                        case ELECTRIC:
                            status = Status.FREEZE;
                            break;
                        default:
                            rand = new SecureRandom().nextInt(1);
                            switch (rand) {
                                case 0:
                                    status = Status.PARALYSIS;
                                    break;
                                case 1:
                                    status = Status.FREEZE;
                                    break;
                            }
                            break;
                    }
                    break;
                case ICE:
                    switch (opponent.getType2()) {
                        case FIRE: //interesting combo, regardless:
                            status = Status.PARALYSIS;
                            break;
                        case ELECTRIC:
                            status = Status.BURN;
                            break;
                        default:
                            rand = new SecureRandom().nextInt(1);
                            switch (rand) {
                                case 0:
                                    status = Status.PARALYSIS;
                                    break;
                                case 1:
                                    status = Status.BURN;
                                    break;
                            }
                            break;
                    }
                    break;
                case ELECTRIC:
                    switch (opponent.getType2()) {
                        case FIRE:
                            status = Status.FREEZE;
                            break;
                        case ICE:
                            status = Status.BURN;
                            break;
                        default:
                            rand = new SecureRandom().nextInt(1);
                            switch (rand) {
                                case 0:
                                    status = Status.BURN;
                                    break;
                                case 1:
                                    status = Status.FREEZE;
                                    break;
                            }
                            break;
                    }
                    break;
                default:
                    switch (rand) {
                        case 0:
                            status = Status.BURN;
                            break;
                        case 1:
                            status = Status.FREEZE;
                            break;
                        case 2:
                            status = Status.PARALYSIS;
                            break;
                    }
                    break;
            }
            opponent.setStatus(status);
            String toReturn = "";
            //fuck the null pointer warnings I have money
            switch (status) {
                case BURN:
                    toReturn = " was burned!";
                    break;
                case FREEZE:
                    toReturn = " was frozen solid!";
                    break;
                case PARALYSIS:
                    toReturn = " was paralyzed!";
                    break;
            }
            return opponent.getName() + toReturn;
        } else {
            return "";
        }
    };
    public static MoveEffect OHKO = (Pokemon user, Pokemon opponent, int damage, Move move, Battle battle) -> {
        if (opponent.getLevel() <= user.getLevel()) {
            int diff = user.getLevel() - opponent.getLevel();
            diff = diff + 30;
            if (diff > 100) {
                diff = 100;
            }
            int hit = new SecureRandom().nextInt(100) + 1;
            if (diff == 100 || hit > diff) {
                opponent.setHP(0);
                return "PogChamp IT'S A ONE HIT KO!! PogChamp " + opponent.getName() + " has 0hp left!";
            } else {
                return "The attack missed!";
            }
        } else {
            return "But it failed!";
        }
    };
    public static MoveEffect SPLASH = (Pokemon user, Pokemon opponent, int damage, Move move, Battle battle) -> {
        return "ThunBeast But nothing happened...";
    };
    public static MoveEffect HURRICANE = (Pokemon user, Pokemon opponent, int damage, Move move, Battle battle) -> {
        String toReturn = "";
        int hitChance = new SecureRandom().nextInt(100);
        int target = 0;
        switch (battle.getWeather()) {
            case NORMAL:
            default:
                target = 70;
                break;
            case RAIN:
            case RAIN_HEAVY:
                target = -1;
                break;
            case SUN_EXTREME:
                target = 50;
                break;
        }
        if (hitChance >= target && target != -1) {
            return "The attack missed!";
        }
        int power = 110;
        if (opponent.getMoveStatus() == Status.ATTACK_NEXT_TURN) {
            power = 220;
        }
        SecureRandom rand = new SecureRandom();
        double effectiveness = getEffectiveness(move, opponent);
        toReturn += getEffectivenessText(effectiveness);
        int randomNum = rand.nextInt((16 - 1) + 1) + 1;
        boolean crit = false;
        if (randomNum == 1) {
            crit = true;
            toReturn += "\nCritical Hit!!";
        }
        damage = calcDamage(user, opponent, move, power, MoveCategory.SPECIAL, crit);
        opponent.damage(damage);
        toReturn += " " + opponent.getName() + " lost " + damage + "hp! ";
        toReturn += opponent.getName() + " has " + opponent.getStat(Stats.HP) + "hp left!";
        if (!opponent.isConfused()) {
            int confChance = new SecureRandom().nextInt(100);
            if (confChance <= 30) {
                opponent.setConfused(true);
                toReturn += opponent.getName() + " was confused! ";
            }
        }
        return toReturn;
    };
    public static MoveEffect MH_15P_85A = (Pokemon user, Pokemon opponent, int damage, Move move, Battle battle) -> {
        int power = 15;
        int amt = new SecureRandom().nextInt(3) + 2;
        int accuracy = 85;
        int hit = new SecureRandom().nextInt(100) + 1;
        if (hit >= accuracy) {
            return "The attack missed!";
        }
        return multiHit(user, opponent, move, power, amt);
    };
    public static MoveEffect MH_15P_100A = (Pokemon user, Pokemon opponent, int damage, Move move, Battle battle) -> {
        int power = 15;
        int amt = new SecureRandom().nextInt(3) + 2;
        return multiHit(user, opponent, move, power, amt);
    };
    public static MoveEffect MH_25P_90A = (Pokemon user, Pokemon opponent, int damage, Move move, Battle battle) -> {
        int power = 25;
        int amt = new SecureRandom().nextInt(3) + 2;
        int accuracy = 90;
        int hit = new SecureRandom().nextInt(100) + 1;
        if (hit >= accuracy) {
            return "The attack missed!";
        }
        return multiHit(user, opponent, move, power, amt);
    };
    public static MoveEffect MH_25P_100A = (Pokemon user, Pokemon opponent, int damage, Move move, Battle battle) -> {
        int power = 15;
        int amt = new SecureRandom().nextInt(3) + 2;
        return multiHit(user, opponent, move, power, amt);
    };
    public static MoveEffect MH_18P_85A = (Pokemon user, Pokemon opponent, int damage, Move move, Battle battle) -> {
        int power = 18;
        int amt = new SecureRandom().nextInt(3) + 2;
        int accuracy = 85;
        int hit = new SecureRandom().nextInt(100) + 1;
        if (hit >= accuracy) {
            return "The attack missed!";
        }
        return multiHit(user, opponent, move, power, amt);
    };
    public static MoveEffect MH_18P_80A = (Pokemon user, Pokemon opponent, int damage, Move move, Battle battle) -> {
        int power = 15;
        int amt = new SecureRandom().nextInt(3) + 2;
        int accuracy = 80;
        int hit = new SecureRandom().nextInt(100) + 1;
        if (hit >= accuracy) {
            return "The attack missed!";
        }
        return multiHit(user, opponent, move, power, amt);
    };
    public static MoveEffect MH_25P_95A = (Pokemon user, Pokemon opponent, int damage, Move move, Battle battle) -> {
        int power = 25;
        int amt = new SecureRandom().nextInt(3) + 2;
        int accuracy = 95;
        int hit = new SecureRandom().nextInt(100) + 1;
        if (hit >= accuracy) {
            return "The attack missed!";
        }
        return multiHit(user, opponent, move, power, amt);
    };
    public static MoveEffect MH_20P_100A = (Pokemon user, Pokemon opponent, int damage, Move move, Battle battle) -> {
        int power = 20;
        int amt = new SecureRandom().nextInt(3) + 2;
        return multiHit(user, opponent, move, power, amt);
    };
    public static MoveEffect MH_25P_85A = (Pokemon user, Pokemon opponent, int damage, Move move, Battle battle) -> {
        int power = 25;
        int amt = new SecureRandom().nextInt(3) + 2;
        int accuracy = 85;
        int hit = new SecureRandom().nextInt(100) + 1;
        if (hit >= accuracy) {
            return "The attack missed!";
        }
        return multiHit(user, opponent, move, power, amt);
    };
    public static MoveEffect BOOMERANG = (Pokemon user, Pokemon opponent, int damage, Move move, Battle battle) -> {
        int power = 50;
        int amt = 2;
        int accuracy = 90;
        int hit = new SecureRandom().nextInt(100) + 1;
        if (hit >= accuracy) {
            return "The attack missed!";
        }
        return multiHit(user, opponent, move, power, amt);
    };
    public static MoveEffect DOUBLE_HIT = (Pokemon user, Pokemon opponent, int damage, Move move, Battle battle) -> {
        int power = 35;
        int amt = 2;
        int accuracy = 90;
        int hit = new SecureRandom().nextInt(100) + 1;
        if (hit >= accuracy) {
            return "The attack missed!";
        }
        return multiHit(user, opponent, move, power, amt);
    };
    public static MoveEffect DOUBLE_KICK = (Pokemon user, Pokemon opponent, int damage, Move move, Battle battle) -> {
        int power = 30;
        int amt = 2;
        return multiHit(user, opponent, move, power, amt);
    };
    public static MoveEffect DUAL_CHOP = (Pokemon user, Pokemon opponent, int damage, Move move, Battle battle) -> {
        int power = 40;
        int amt = 2;
        int accuracy = 90;
        int hit = new SecureRandom().nextInt(100) + 1;
        if (hit >= accuracy) {
            return "The attack missed!";
        }
        return multiHit(user, opponent, move, power, amt);
    };
    public static MoveEffect GEAR_GRIND = (Pokemon user, Pokemon opponent, int damage, Move move, Battle battle) -> {
        int power = 50;
        int amt = 2;
        int accuracy = 85;
        int hit = new SecureRandom().nextInt(100) + 1;
        if (hit >= accuracy) {
            return "The attack missed!";
        }
        return multiHit(user, opponent, move, power, amt);
    };
    public static MoveEffect TWINEEDLE = (Pokemon user, Pokemon opponent, int damage, Move move, Battle battle) -> {
        int power = 25;
        int amt = 2;
        int chance = 20;
        int hit = new SecureRandom().nextInt(100) + 1;
        String toReturn = multiHit(user, opponent, move, power, amt);
        if (hit < chance) {
            if (opponent.getType1() == Type.STEEL || opponent.getType2() == Type.STEEL || opponent.getType1() == Type.POISON || opponent.getType2() == Type.POISON) {
            } else if (opponent.getStatus() == Status.NORMAL && !opponent.isFainted()) {
                opponent.setStatus(Status.POISON);
                toReturn += " " + opponent.getName() + " was poisioned!";
            }
        }
        return toReturn;
    };
    public static MoveEffect TRIPLE_KICK = (Pokemon user, Pokemon opponent, int damage, Move move, Battle battle) -> {
        int power = 10;
        int amt = 3;
        int accuracy = 90;
        int i = amt;
        amt = 0;
        String toReturn = "";
        double effect = getEffectiveness(move, opponent);
        if (effect == 0) {
            return "It doesn't affect the opponent!";
        }
        String effectiveness = getEffectivenessText(effect);
        while (i > 0) {
            int hit = new SecureRandom().nextInt(100) + 1;
            if (hit >= accuracy) {
                toReturn += "The attack missed!";
                amt++;
                break;
            }
            boolean crit = new SecureRandom().nextBoolean();
            if (crit) {
                toReturn += " Critical Hit!! ";
            }
            damage = calcDamage(user, opponent, move, power, move.getCategory(), crit);
            opponent.damage(damage);
            power += 10;
            toReturn += opponent.getName() + " lost " + damage + "hp! ";
            i--;
            amt++;
        }
        toReturn += effectiveness + " Hit " + amt + " time" + (amt == 1 ? "! " : "s! ") + opponent.getName() + " has " + opponent.getStat(Stats.HP) + "hp left!";
        return toReturn;
    };

    private static String multiHit(Pokemon user, Pokemon opponent, Move move, int power, int amt) {
        StringBuilder toReturn = new StringBuilder("");
        int i = amt;
        double effect = getEffectiveness(move, opponent);
        if (effect == 0) {
            return "It doesn't affect the opponent!";
        }
        String effectiveness = getEffectivenessText(effect);
        while (i > 0) {
            boolean crit = new SecureRandom().nextBoolean();
            int damage = calcDamage(user, opponent, move, power, move.getCategory(), crit);
            opponent.damage(damage);
            toReturn.append(crit ? " Critical Hit!! " : " ").append(opponent.getName()).append(" lost ").append(damage).append("hp! ");
            i--;
        }
        toReturn.append(effectiveness).append(" Hit ").append(amt).append(" time").append(amt > 1 ? "s! " : "! ").append(opponent.getName()).append(" has ").append(opponent.getStat(Stats.HP)).append("hp left!");
        return toReturn.toString();
    }
}
