package TPPDekuBot;

import java.io.Serializable;

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
}
