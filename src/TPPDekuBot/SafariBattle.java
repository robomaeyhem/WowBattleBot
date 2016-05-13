package TPPDekuBot;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.SecureRandom;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class SafariBattle {

    public Trainer user;
    private Pokemon wild;
    private int angry = 0, eat = 0;
    public LinkedBlockingQueue<String> msg = new LinkedBlockingQueue<>();
    private double hpMax;
    private double hpCurrent;
    private double rate;
    private double catchChance;
    private double shakeProbability;

    public SafariBattle(String user, Pokemon wild) {
        this.user = new Trainer(user);
        this.wild = wild;
    }

    public SafariBattle(Trainer user, Pokemon wild) {
        this.user = user;
        this.wild = wild;
    }

    private void recalcCatch() {
        hpMax = wild.getMaxHP();
        hpCurrent = wild.getStat(Stats.HP);
        rate = Pokemon.getCatchRate(wild.getName());
        catchChance = ((((3 * hpMax) - (2 * hpCurrent)) * rate * 1.5) / (3 * hpMax));
        if (catchChance > 255) {
            catchChance = 255;
        }
        shakeProbability = 1048560.0 / (Math.sqrt(Math.sqrt(16711680.0 / catchChance)));
    }

    public void doBattle(BattleBot b, String channel) {
        b.music.play(b.determineMusic(wild));
        b.sendMessage(channel, (BattleBot.isLegendary(wild.getId()) ? "Woah! " : "") + "A Wild " + wild.getName() + " (Level " + wild.getLevel() + ") Appeared!");
        recalcCatch();
        boolean caught = false;
        boolean end = false;
        String lastTurn = "";
        while (true) {
            msg = new LinkedBlockingQueue<>();
            b.sendMessage(channel, "What will " + user + " do? (!bait) Throw Bait, (!rock) Throw Rock, (!ball) Throw Pokeball, (!run) Run");
            try {
                String move = msg.poll(60, TimeUnit.SECONDS);
                if (move == null) {
                    b.sendMessage(channel, user.getTrainerName() + " did not select an action in time, the Pokemon was stolen by Team Flare WutFace");
                    b.music.clear();
                    end = true;
                    return;
                }
                if (move.startsWith("!bait")) {
                    lastTurn = "bait";
                    angry = 0;
                    eat += new SecureRandom().nextInt(5 - 1 + 1) + 1;

                } else if (move.startsWith("!rock")) {
                    lastTurn = "rock";
                    angry += new SecureRandom().nextInt(5 - 1 + 1) + 1;
                    eat = 0;
                } else if (move.startsWith("!ball")) {
                    lastTurn = "ball";
                    b.sendMessage(channel, user.getTrainerName() + " threw a Pokeball!");
                    System.err.println("catchChance = " + catchChance + "\nshakeProb = " + shakeProbability);
                    int shake1 = new SecureRandom().nextInt(65536);
                    if (shake1 >= shakeProbability) {
                        b.sendMessage(channel, "You missed the Pokemon! RuleFive Good Job, Dumbass. FUNgineer");
                        continue;
                    }
                    b.sendMessage(channel, "Shake...");
                    int shake2 = new SecureRandom().nextInt(65536);
                    if (shake2 >= shakeProbability) {
                        b.sendMessage(channel, "Aww! It appeared to be caught! BibleThump");
                        continue;
                    }
                    b.sendMessage(channel, "Shake.... ThunBeast");
                    int shake3 = new SecureRandom().nextInt(65536);
                    if (shake3 >= shakeProbability) {
                        b.sendMessage(channel, "Aargh! Almost had it caught! DansGame");
                        continue;
                    }
                    b.sendMessage(channel, "Shake..... PogChamp");
                    int shake4 = new SecureRandom().nextInt(65536);
                    if (shake4 >= shakeProbability) {
                        b.sendMessage(channel, "Gah! It was so close, too! SwiftRage");
                        continue;
                    }
                    b.sendMessage(channel, "Awright! " + wild.getName() + " was caught! Kreygasm");
                    b.music.clear();
                    caught = true;
                    return;

                } else if (move.startsWith("!run")) {
                    b.sendMessage(channel, "You got away safely!");
                    b.music.clear();
                    lastTurn = "run";
                    return;
                }
            } catch (Exception ex) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                ex.printStackTrace(pw);
                b.music.sendMessage(b.music.getChannel(), b.music.CHEF.mention() + " ```" + sw.toString() + "Pokemon ID: " + this.wild.getId() + "```");
            } finally {
                if (caught || lastTurn.equalsIgnoreCase("run") || end) {
                    return;
                }
                if (eat > angry) {
                    b.sendMessage(channel, wild.getName() + " is eating!");
                    int random = new SecureRandom().nextInt(256);
                    if (random < (wild.getStat(Stats.SPEED) / 2)) {
                        b.sendMessage(channel, "The wild " + wild.getName() + " ran away!");
                        b.music.clear();
                        return;
                    }
                } else if (angry > eat) {
                    b.sendMessage(channel, wild.getName() + " is pissed off!");
                    int random = new SecureRandom().nextInt(256);
                    if (random < (wild.getStat(Stats.SPEED) * 4)) {
                        b.sendMessage(channel, "The wild " + wild.getName() + " ran away!");
                        b.music.clear();
                        return;
                    }
                } else {
                    b.sendMessage(channel, wild.getName() + " is watching carefully...");
                    int random = new SecureRandom().nextInt(256);
                    if (random < (wild.getStat(Stats.SPEED) * 2)) {
                        b.sendMessage(channel, "The wild " + wild.getName() + " ran away!");
                        b.music.clear();
                        return;
                    }
                }
                if (angry > 0) {
                    angry--;
                }
                if (eat > 0) {
                    eat--;
                }
                if (angry == 0) {
                    recalcCatch();
                }
                switch (lastTurn) {
                    case "bait":
                        catchChance = catchChance / 2;
                        shakeProbability = 1048560.0 / (Math.sqrt(Math.sqrt(16711680.0 / catchChance)));
                        continue;
                    case "rock":
                        catchChance = catchChance * 2;
                        shakeProbability = 1048560.0 / (Math.sqrt(Math.sqrt(16711680.0 / catchChance)));
                        if (catchChance > 255) {
                            catchChance = 255;
                        }
                        continue;
                    default:
                        continue;
                }

            }
        }
    }
}
