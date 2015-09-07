package TPPDekuBot;

import java.util.ArrayList;

public class GymLeader extends Trainer {

    private int prizeMoney;
    private GymBadge badge;
    private int number;
    private boolean isEliteFour;
    private boolean isChampion;

    public GymLeader(String name, ArrayList<Pokemon> pokemon, ArrayList<Item> items, int prizeMoney, GymBadge badge, int number, boolean eliteFour, boolean champion) {
        super(name, pokemon, items);
        this.prizeMoney = prizeMoney;
        this.badge = badge;
        this.number = number;
        this.isEliteFour = eliteFour;
        this.isChampion = champion;
        if(isEliteFour){
            this.trnClass = "Elite Four";
        }
        if(isChampion){
            this.trnClass = "Champion";
        }
    }

    public boolean canChallenge(int numberOfBadges, int eliteFourNumber) {
        if (isEliteFour) {
            return eliteFourNumber == (number - 1);
        } else if (isChampion) {
            return (numberOfBadges == 8) && (eliteFourNumber == 4);
        } else {
            return numberOfBadges == (number - 1);
        }
    }

}
