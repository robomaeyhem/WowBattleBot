package TPPDekuBot;

import java.io.Serializable;
import java.security.SecureRandom;

public interface MoveEffect extends Serializable {

    String run(Pokemon user, Pokemon opponent, int damage, Move move);
}

class MoveEffects {

    public static MoveEffect FLINCH = (Pokemon user, Pokemon opponent, int damage, Move move) -> {
        opponent.setFlinch(true);
        return "";
    };

    public static MoveEffect HEAL_HALF = (Pokemon user, Pokemon opponent, int damage, Move move) -> {
        int amt = damage / 2;
        if (amt > user.getMaxHP() || (user.getStat(Stats.HP) + amt) > user.getMaxHP()) {
            amt = (user.getMaxHP() - user.getStat(Stats.HP));
            user.setHP(user.getMaxHP());
        } else {
            user.setHP(user.getStat(Stats.HP) + amt);
        }
        return amt == 0 ? user.getName() + "'s HP is already full!" : (user.getName() + " gained " + amt + " HP!");
    };
    public static MoveEffect EXPLOSION = (Pokemon user, Pokemon opponent, int damage, Move move) -> {
        user.setHP(0);
        return (user.getName() + " fainted! KAPOW ");
    };
    public static MoveEffect RECOIL_25 = (Pokemon user, Pokemon opponent, int damage, Move move) -> {
        int amt = damage / 4;
        if (amt > user.getStat(Stats.HP)) {
            amt = user.getStat(Stats.HP);
        }
        user.damage(amt);
        return user.getName() + " lost " + amt + "hp due to Recoil!";
    };
    public static MoveEffect RECOIL_33 = (Pokemon user, Pokemon opponent, int damage, Move move) -> {
        int amt = damage / 3;
        if (amt > user.getStat(Stats.HP)) {
            amt = user.getStat(Stats.HP);
        }
        user.damage(amt);
        return user.getName() + " lost " + amt + "hp due to Recoil!";
    };
    public static MoveEffect RECOIL_50 = (Pokemon user, Pokemon opponent, int damage, Move move) -> {
        int amt = damage / 2;
        if (amt > user.getStat(Stats.HP)) {
            amt = user.getStat(Stats.HP);
        }
        user.damage(amt);
        return user.getName() + " lost " + amt + "hp due to Recoil!";
    };
    public static MoveEffect STRUGGLE = (Pokemon user, Pokemon opponent, int damage, Move move) -> {
        int amt = user.getMaxHP() / 4;
        if (amt > user.getStat(Stats.HP)) {
            amt = user.getStat(Stats.HP);
        }
        user.damage(amt);
        return user.getName() + " lost " + amt + "hp due to Recoil!";
    };
    public static MoveEffect RECHARGE = (Pokemon user, Pokemon opponent, int damage, Move move) -> {
        user.setMoveStatus(Status.NO_MOVE_THIS_TURN);
        return "";
    };
    public static MoveEffect SLEEP = (Pokemon user, Pokemon opponent, int damage, Move move) -> {
        if (opponent.getStatus() == Status.SLEEP) {
            return opponent.getName() + " is already asleep!";
        }
        if (opponent.getStatus() != Status.NORMAL) {
            opponent.goToSleep();
            return opponent.getName() + " fell asleep!";
        }
        return "But it failed!";
    };
    public static MoveEffect TOXIC = (Pokemon user, Pokemon opponent, int damage, Move move) -> {
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
    public static MoveEffect BURN = (Pokemon user, Pokemon opponent, int damage, Move move) -> {
        if (opponent.getStatus() == Status.NORMAL && (opponent.getType1() != Type.FIRE || opponent.getType2() != Type.FIRE)) {
            opponent.setStatus(Status.BURN);
            return opponent.getName() + " was burned!";
        } else if (move.getCategory() == MoveCategory.STATUS) {
            return "But it failed!";
        } else {
            return "";
        }
    };
    public static MoveEffect PARALYZE = (Pokemon user, Pokemon opponent, int damage, Move move) -> {
        if (opponent.getStatus() == Status.NORMAL) {
            opponent.setStatus(Status.PARALYSIS);
            return opponent.getName() + " was paralyzed!";
        } else if (move.getCategory() == MoveCategory.STATUS) {
            return "But it failed!";
        } else {
            return "";
        }
    };
    public static MoveEffect FREEZE = (Pokemon user, Pokemon opponent, int damage, Move move) -> {
        if (opponent.getStatus() == Status.NORMAL) {
            opponent.setStatus(Status.FREEZE);
            return opponent.getName() + " was frozen solid!";
        } else if (move.getCategory() == MoveCategory.STATUS) {
            return "But it failed!";
        } else {
            return "";
        }
    };
    public static MoveEffect CONFUSE_TARGET = (Pokemon user, Pokemon opponent, int damage, Move move) -> {
        if (opponent.isConfused()) {
            return opponent.getName() + " is already confused!";
        } else {
            opponent.setConfused(true);
            return opponent.getName() + " was confused!";
        }
    };
    public static MoveEffect POISON = (Pokemon user, Pokemon opponent, int damage, Move move) -> {
        if (opponent.getType1() == Type.STEEL || opponent.getType2() == Type.STEEL || opponent.getType1() == Type.POISON || opponent.getType2() == Type.POISON) {
            if (move.getCategory() == MoveCategory.STATUS) {
                return "But it failed!";
            } else {
                return "";
            }
        }
        if (opponent.getStatus() != Status.NORMAL) {
            opponent.setStatus(Status.POISON);
            return opponent.getName() + " was poisioned!";
        } else if (move.getCategory() == MoveCategory.STATUS) {
            return "But it failed!";
        } else {
            return "";
        }
    };
    public static MoveEffect TRI_ATTACK = (Pokemon user, Pokemon opponent, int damage, Move move) -> {
        if (opponent.getStatus() != Status.NORMAL) {
            int rand = new SecureRandom().nextInt(2);
            Status status = null;
            if (opponent.getType1() == Type.FIRE || opponent.getType2() == Type.FIRE) {
                rand = new SecureRandom().nextInt(1);
                switch (rand) {
                    case 0:
                        status = Status.FREEZE;
                        break;
                    case 1:
                        status = Status.PARALYSIS;
                        break;
                }
            } else {
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
}
