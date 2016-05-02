package TPPDekuBot;

import java.io.Serializable;

public interface MoveEffect extends Serializable {
    String run(Pokemon user, Pokemon opponent, int damage, Move move);
}

class MoveEffects {
    public static MoveEffect FLINCH = (Pokemon user, Pokemon opponent, int damage, Move move) -> {
        opponent.setFlinch(true);
        return opponent.getName()+" flinched!";
    };

    public static MoveEffect HEAL_HALF = (Pokemon user, Pokemon opponent, int damage, Move move) -> {
        int amt = damage/2;
        user.setHP(user.getStat(Stats.HP)+amt);
        return (user.getName()+" gained "+amt+" HP!");
    };
}
