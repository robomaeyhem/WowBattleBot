package TPPDekuBot;

public interface MoveEffect {
    void run(Pokemon user, Pokemon opponent);
}

class MoveEffects {
    // only flinch for now, just as an example
    public static MoveEffect FLINCH = (Pokemon user, Pokemon opponent) -> {
        opponent.setFlinch(true);
    };
}
