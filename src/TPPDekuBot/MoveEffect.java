package TPPDekuBot;

import java.io.Serializable;
import java.security.SecureRandom;

public interface MoveEffect extends Serializable {

    String run(Pokemon user, Pokemon opponent, int damage, Move move, Battle battle);
}

class MoveEffects {

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
        if (opponent.getStatus() != Status.NORMAL) {
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
        if (user.getType1() == move.getType() || user.getType2() == move.getType()) {
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
        damageBuf = (2.0 * (double) user.getLevel() + 10.0) / 250.0;
        damageBuf = damageBuf * ((double) user.getStat(Stats.SP_ATTACK) / (double) user.getStat(Stats.SP_DEFENSE));
        damageBuf = damageBuf * (double) power + 2.0;
        damageBuf = (damageBuf * modifier);
        damage = (int) damageBuf;
        int damageBuffer = damage;
        if (damageBuffer > opponent.getStat(Stats.HP)) {
            damageBuffer = opponent.getStat(Stats.HP);
        }
        opponent.damage(damage);
        toReturn += " "+opponent.getName() + " lost " + damage + "hp! ";
        if (!opponent.isConfused()) {
            int confChance = new SecureRandom().nextInt(100);
            if (confChance <= 30) {
                opponent.setConfused(true);
                toReturn += opponent.getName() + " was confused! ";
            }
        }
        toReturn += opponent.getName() + " has " + opponent.getStat(Stats.HP) + "hp left!";
        return toReturn;
    };
}
