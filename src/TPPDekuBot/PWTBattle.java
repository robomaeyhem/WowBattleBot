package TPPDekuBot;

import java.io.File;
import java.security.SecureRandom;
import java.util.concurrent.LinkedBlockingQueue;

public class PWTBattle extends Battle {

    private PWTType type;
    private PWTClass pwtclass;
    private Trainer player1;
    private Trainer player2;
    private PWTRound round;
    public LinkedBlockingQueue<String> p1msg;
    public LinkedBlockingQueue<String> p2msg;

    public PWTBattle(BattleBot b, Trainer player1, Trainer player2, PWTType type, PWTClass pwtclass, PWTRound round) {
        super(b);
        this.type = type;
        this.pwtclass = pwtclass;
        this.player1 = player1;
        this.player2 = player2;
        this.round = round;
        p1msg = new LinkedBlockingQueue<>();
        p2msg = new LinkedBlockingQueue<>();
    }

    public PWTType getType() {
        return type;
    }

    public PWTClass getPwtclass() {
        return pwtclass;
    }

    public Trainer getPlayer1() {
        return player1;
    }

    public Trainer getPlayer2() {
        return player2;
    }

    public PWTRound getRound() {
        return round;
    }

    /**
     * Does the PWT Battle.
     *
     * @return Trainer that was victorious
     */
    public Trainer doBattle() {
        //todo
    }

    public static File determineMusic(PWTBattle battle) {
        if (battle.getRound() == PWTRound.FINALS) {
            return new File(BattleBot.ROOT_PATH + "pwt\\pwt-final.mp3");
        }
        File toReturn = new File(BattleBot.ROOT_PATH + "pwt\\pwt-trainer.mp3");
        Trainer ai = null;
        if (battle.player1.isAI()) {
            ai = battle.player1;
        } else if (battle.player2.isAI()) {
            ai = battle.player2;
        }
        if (ai == null) {
            if (battle.player2.getTrnClass().contains("Champion") || battle.player2.getTrnClass().contains("Gym Leader")) {
                ai = battle.player2;
            } else {
                ai = battle.player1;
            }
        }
        String trnClass = ai.getTrnClass();
        if (trnClass.contains("Champion")) {
            switch (ai.getRegion()) {
                case KANTO:
                    toReturn = new File(BattleBot.ROOT_PATH + "pwt\\pwt-kanto-champion.mp3");
                    break;
                case JOHTO:
                    toReturn = new File(BattleBot.ROOT_PATH + "pwt\\pwt-johto-champion.mp3");
                    break;
                case HOENN:
                    toReturn = new File(BattleBot.ROOT_PATH + "pwt\\pwt-hoenn-champion.mp3");
                    break;
                case SINNOH:
                    toReturn = new File(BattleBot.ROOT_PATH + "pwt\\pwt-sinnoh-champion.mp3");
                    break;
                case UNOVA:
                    toReturn = new File(BattleBot.ROOT_PATH + "pwt\\pwt-unova-champion.mp3");
                    break;
                default:
                    File[] files = {new File(BattleBot.ROOT_PATH + "pwt\\pwt-unova-champion.mp3"), new File(BattleBot.ROOT_PATH + "pwt\\pwt-sinnoh-champion.mp3"), new File(BattleBot.ROOT_PATH + "pwt\\pwt-hoenn-champion.mp3"), toReturn = new File(BattleBot.ROOT_PATH + "pwt\\pwt-johto-champion.mp3"), toReturn = new File(BattleBot.ROOT_PATH + "pwt\\pwt-kanto-champion.mp3")};
                    toReturn = files[new SecureRandom().nextInt(files.length)];
                    break;
            }
        } else if (trnClass.contains("Gym Leader")) {
            switch (ai.getRegion()) {
                case KANTO:
                    toReturn = new File(BattleBot.ROOT_PATH + "pwt\\pwt-kanto-gym.mp3");
                    break;
                case JOHTO:
                    toReturn = new File(BattleBot.ROOT_PATH + "pwt\\pwt-johto-gym.mp3");
                    break;
                case HOENN:
                    toReturn = new File(BattleBot.ROOT_PATH + "pwt\\pwt-hoenn-gym.mp3");
                    break;
                case SINNOH:
                    toReturn = new File(BattleBot.ROOT_PATH + "pwt\\pwt-sinnoh-gym.mp3");
                    break;
                case UNOVA:
                    toReturn = new File(BattleBot.ROOT_PATH + "pwt\\pwt-unova-gym.mp3");
                    break;
                default:
                    File[] files = {new File(BattleBot.ROOT_PATH + "pwt\\pwt-unova-gym.mp3"), new File(BattleBot.ROOT_PATH + "pwt\\pwt-sinnoh-gym.mp3"), new File(BattleBot.ROOT_PATH + "pwt\\pwt-hoenn-gym.mp3"), toReturn = new File(BattleBot.ROOT_PATH + "pwt\\pwt-johto-gym.mp3"), toReturn = new File(BattleBot.ROOT_PATH + "pwt\\pwt-kanto-gym.mp3")};
                    toReturn = files[new SecureRandom().nextInt(files.length)];
                    break;
            }
        }
        return toReturn;
    }
}
