package TPPDekuBot;

import java.util.ArrayList;

public class GymLeader extends Trainer {

    private int prizeMoney;
    private GymBadge badge;

    public GymLeader(String name, ArrayList<Pokemon> pokemon, ArrayList<Item> items, int prizeMoney, GymBadge badge) {
        super(name, pokemon, items);
        this.prizeMoney = prizeMoney;
        this.badge = badge;
    }

}
