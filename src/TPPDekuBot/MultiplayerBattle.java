package TPPDekuBot;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.SecureRandom;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class MultiplayerBattle {

    public Trainer player1;
    public Trainer player2;
    private Pokemon pokemon1;
    private Pokemon pokemon2;
    public LinkedBlockingQueue<String> p1msg = new LinkedBlockingQueue<>();
    public LinkedBlockingQueue<String> p2msg = new LinkedBlockingQueue<>();
    private boolean p1switch = false;
    private boolean p2switch = false;
    private boolean endBattle = false;
    private int numberOfMon = 1;
    private boolean randomBattle;
    private boolean musicChanged = false;

    private MultiplayerBattle() {
    }

    public final boolean isBannedPokemon(int poke) {
        int[] banned = {0, 132, 202, 235, 292, 360, 665};
        for (int el : banned) {
            if (poke == el) {
                return true;
            }
        }
        if (poke > 720 || poke < 0) {
            return true;
        }
        return false;
    }

    public MultiplayerBattle(String player1, String player2, int level, int pokemonNumber) {
        this.player1 = new Trainer(player1, pokemonNumber, level);
        this.player2 = new Trainer(player2, pokemonNumber, level);
        this.pokemon1 = this.player1.getPokemon(0);
        this.pokemon2 = this.player2.getPokemon(0);
        this.player1.removePokemon(0);
        this.player2.removePokemon(0);
        this.numberOfMon = pokemonNumber;
        randomBattle = true;
    }

    public MultiplayerBattle(String player1, String player2, int level) {
        this.player1 = new Trainer(player1, 1, level);
        this.player2 = new Trainer(player2, 1, level);
        this.pokemon1 = this.player1.getPokemon(0);
        this.pokemon2 = this.player2.getPokemon(0);
        this.player1.removePokemon(0);
        this.player2.removePokemon(0);
        randomBattle = true;
    }

    public MultiplayerBattle(Trainer player1, Trainer player2) {
        randomBattle = false;
        this.player1 = player1;
        this.player2 = player2;
        this.pokemon1 = this.player1.getPokemon(0);
        this.pokemon2 = this.player2.getPokemon(0);
        this.player1.removePokemon(0);
        this.player2.removePokemon(0);
        numberOfMon = (this.player1.getPokemon().size() >= this.player2.getPokemon().size()) ? this.player1.getPokemon().size() : this.player2.getPokemon().size();
    }

    public String getPlayer1() {
        return player1.getTrainerName();
    }

    public String getPlayer2() {
        return player2.getTrainerName();
    }

//    private void doPlayer1Move(BattleBot b, String channel, String p1move) {
//        switch (p1move) {
//            case "1":
//                b.sendMessage(channel, pokemon1.attack(pokemon2, pokemon1.getMove1()).replace("\n", " "));
//                break;
//            case "2":
//                b.sendMessage(channel, pokemon1.attack(pokemon2, pokemon1.getMove2()).replace("\n", " "));
//                break;
//            case "3":
//                b.sendMessage(channel, pokemon1.attack(pokemon2, pokemon1.getMove3()).replace("\n", " "));
//                break;
//            case "4":
//                b.sendMessage(channel, pokemon1.attack(pokemon2, pokemon1.getMove4()).replace("\n", " "));
//                break;
//        }
//    }
//
//    private void doPlayer2Move(BattleBot b, String channel, String p2move) {
//        switch (p2move) {
//            case "1":
//                b.sendMessage(channel, pokemon2.attack(pokemon1, pokemon2.getMove1()).replace("\n", " "));
//                break;
//            case "2":
//                b.sendMessage(channel, pokemon2.attack(pokemon1, pokemon2.getMove2()).replace("\n", " "));
//                break;
//            case "3":
//                b.sendMessage(channel, pokemon2.attack(pokemon1, pokemon2.getMove3()).replace("\n", " "));
//                break;
//            case "4":
//                b.sendMessage(channel, pokemon2.attack(pokemon1, pokemon2.getMove4()).replace("\n", " "));
//                break;
//        }
//    }
    private void doMove(BattleBot b, String channel, String move, Pokemon user, Pokemon opponent) {
        int m = Integer.parseInt(move);
        b.sendMessage(channel, user.attack(opponent, user.getMoveByNumber(m)).replace("\n", " "));
    }

    private void switchPlayer1(BattleBot b, String channel) {
        if (pokemon2.getLevel() == 100) {
            b.sendMessage(channel, pokemon1.getName() + " fainted! What Pokemon will " + player1.getTrainerName() + " switch to?");
        } else {
            int levelBefore = pokemon2.getLevel();
            int exp = Pokemon.calculateExperience(true, pokemon2, pokemon1);
            pokemon2.addExperience(exp);
            int levelAfter = pokemon2.getLevel();
            b.sendMessage(channel, pokemon1.getName() + " fainted! " + pokemon2.getName() + " gained " + exp + " Exp. Points!");
            if (levelBefore < levelAfter) {
                b.sendMessage(channel, pokemon2.getName() + " grew to Level " + levelAfter + "! PogChamp");
            }
            b.sendMessage(channel, "What Pokemon will " + player1.getTrainerName() + " switch to?");
        }
        b.sendWhisper(player1.getTrainerName(), "Type !list to get a list of your Pokemon. Type !switch<number> to switch to that Pokemon (for example, the if you want to switch to the first Pokemon, type !switch0 )");
        String p1move = "";
        int p1switchto = -1;
        try {
            p1move = p1msg.poll(60, TimeUnit.SECONDS);
            if (p1move == null) {
                b.sendMessage(channel, player1.getTrainerName() + " did not select a new Pokemon in time. " + player2.getTrainerName() + " wins!");
                endBattle = true;
                return;
            }
            if (p1move.startsWith("!switch") && p1move.length() >= 8) {
                if (!Character.isDigit(p1move.charAt(7))) {
                    p1move = "";
                    b.sendMessage(channel, "/w " + player1.getTrainerName() + " Invalid Pokemon Position FUNgineer");
                } else {
                    p1switchto = Integer.parseInt(p1move.charAt(7) + "");
                    try {
                        if (player1.getPokemon(p1switchto).isFainted()) {
                            b.sendMessage(channel, "/w " + player1.getTrainerName() + " You cannot switch to a fainted Pokemon FUNgineer");
                            p1move = "";
                        }
                    } catch (Exception ex) {
                        b.sendMessage(channel, "/w " + player1.getTrainerName() + " Invalid Pokemon Position FUNgineer");
                        p1move = "";
                    }
                }
            } else {
                p1move = "";
            }
            while (!p1move.startsWith("!switch")) {
                p1move = p1msg.take();
                if (p1move.startsWith("!switch") && p1move.length() >= 8) {
                    if (!Character.isDigit(p1move.charAt(7))) {
                        p1move = "";
                        b.sendMessage(channel, "/w " + player1.getTrainerName() + " Invalid Pokemon Position FUNgineer");
                        continue;
                    }
                    p1switchto = Integer.parseInt(p1move.charAt(7) + "");
                    try {
                        if (player1.getPokemon(p1switchto).isFainted()) {
                            b.sendMessage(channel, "/w " + player1.getTrainerName() + " You cannot switch to a fainted Pokemon FUNgineer");
                            p1move = "";
                            continue;
                        }
                    } catch (Exception ex) {
                        b.sendMessage(channel, "/w " + player1.getTrainerName() + " Invalid Pokemon Position FUNgineer");
                        p1move = "";
                        continue;
                    }
                } else {
                    p1move = "";
                }
            }
            p1msg = new LinkedBlockingQueue<>();
            this.pokemon1 = player1.getPokemon(p1switchto);
            player1.removePokemon(p1switchto);
            b.sendMessage(channel, this.player1 + " sends out " + this.pokemon1.getName() + " (level " + this.pokemon1.getLevel() + ")!");

        } catch (Exception ex) {
            System.err.println("[WARNING] Failed to Switch pokemon! " + ex);
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            b.music.sendMessage(b.music.getChannel(), b.music.CHEF.mention() + " ```" + sw.toString() + "\n```");
            endBattle = true;
        }
    }

    private void switchPlayer2(BattleBot b, String channel) {
        if (pokemon1.getLevel() == 100) {
            b.sendMessage(channel, pokemon2.getName() + " fainted! What Pokemon will " + player2.getTrainerName() + " switch to?");
        } else {
            int levelBefore = pokemon1.getLevel();
            int exp = Pokemon.calculateExperience(true, pokemon1, pokemon2);
            pokemon1.addExperience(exp);
            int levelAfter = pokemon1.getLevel();
            b.sendMessage(channel, pokemon2.getName() + " fainted! " + pokemon1.getName() + " gained " + exp + " Exp. Points!");
            if (levelBefore < levelAfter) {
                b.sendMessage(channel, pokemon1.getName() + " grew to Level " + levelAfter + "! PogChamp");
            }
            b.sendMessage(channel, "What Pokemon will " + player2.getTrainerName() + " switch to?");
        }
        b.sendWhisper(player2.getTrainerName(), "Type !list to get a list of your Pokemon. Type !switch<number> to switch to that Pokemon (for example, the if you want to switch to the first Pokemon, type !switch0 )");
        String p2move = "";
        int p2switchto = -1;
        try {
            p2move = p2msg.poll(60, TimeUnit.SECONDS);
            if (p2move == null) {
                b.sendMessage(channel, player2.getTrainerName() + " did not select a new Pokemon in time. " + player1.getTrainerName() + " wins!");
                endBattle = true;
                return;
            }
            if (p2move.startsWith("!switch") && p2move.length() >= 8) {
                if (!Character.isDigit(p2move.charAt(7))) {
                    p2move = "";
                    b.sendMessage(channel, "/w " + player2.getTrainerName() + " Invalid Pokemon Position FUNgineer");
                } else {
                    p2switchto = Integer.parseInt(p2move.charAt(7) + "");
                    try {
                        if (player2.getPokemon(p2switchto).isFainted()) {
                            b.sendMessage(channel, "/w " + player2.getTrainerName() + " You cannot switch to a fainted Pokemon FUNgineer");
                            p2move = "";
                        }
                    } catch (Exception ex) {
                        b.sendMessage(channel, "/w " + player2.getTrainerName() + " Invalid Pokemon Position FUNgineer");
                        p2move = "";
                    }
                }
            } else {
                p2move = "";
            }
            while (!p2move.startsWith("!switch")) {
                p2move = p2msg.take();
                if (p2move.startsWith("!switch") && p2move.length() >= 8) {
                    if (!Character.isDigit(p2move.charAt(7))) {
                        p2move = "";
                        b.sendMessage(channel, "/w " + player2.getTrainerName() + " Invalid Pokemon Position FUNgineer");
                        continue;
                    }
                    p2switchto = Integer.parseInt(p2move.charAt(7) + "");
                    try {
                        if (player2.getPokemon(p2switchto).isFainted()) {
                            b.sendMessage(channel, "/w " + player2.getTrainerName() + " You cannot switch to a fainted Pokemon FUNgineer");
                            p2move = "";
                            continue;
                        }
                    } catch (Exception ex) {
                        b.sendMessage(channel, "/w " + player2.getTrainerName() + " Invalid Pokemon Position FUNgineer");
                        p2move = "";
                        continue;
                    }
                } else {
                    p2move = "";
                }
            }
            p2msg = new LinkedBlockingQueue<>();
            this.pokemon2 = player2.getPokemon(p2switchto);
            player2.removePokemon(p2switchto);
            b.sendMessage(channel, this.player2 + " sends out " + this.pokemon2.getName() + " (level " + this.pokemon2.getLevel() + ")!");
        } catch (Exception ex) {
            System.err.println("[WARNING] Failed to Switch pokemon! " + ex);
            endBattle = true;
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            b.music.sendMessage(b.music.getChannel(), b.music.CHEF.mention() + " ```" + sw.toString() + "```");
        }

    }

    public void doBattle(BattleBot b, String channel) {
        boolean hasSent = false;
        b.music.play(b.determineMusic(player1.getTrnClass(), player2.getTrnClass(), player1.getTrainerName(), player2.getTrainerName()));
        b.sendMessage(channel, player1 + " is issuing a challenge against " + player2 + "!");
        b.sendMessage(channel, player1.getTrainerName() + " sends out " + pokemon1.getName() + " (Level " + pokemon1.getLevel() + ")! " + player2.getTrainerName() + " sends out " + pokemon2.getName() + " (Level " + pokemon2.getLevel() + ")!");
        do {
            singlebattle:
            while (!pokemon1.isFainted() && !pokemon2.isFainted()) {
                if (player1.getPokemon().isEmpty() && player2.getPokemon().isEmpty() && !hasSent && numberOfMon > 1) {
                    hasSent = true;
                    String toSend = (new SecureRandom().nextBoolean()) ? "PRChase The battle has reached it's final stage! And the tension is peaking ThunBeast" : "PRChase The Last Pokemon from Each Team takes the field. Will the Outcome of the battle be decided in the next turn? ThunBeast";
                    b.sendMessage(channel, toSend);
                }
                if (numberOfMon > 1) {
                    if (!musicChanged) {
                        if ((player1.getTrnClass().equalsIgnoreCase("Gym Leader") && player1.getPokemon().isEmpty()) || (player2.getTrnClass().equalsIgnoreCase("Gym Leader") && player2.getPokemon().isEmpty())) {
                            String nowPlaying = b.music.getNowPlaying();
                            if (nowPlaying.contains("gen5-bw-gym")) {
                                b.music.clear();
                                b.music.play(new File(b.ROOT_PATH + "gen5-bw-gym-final.mp3"));
                                musicChanged = true;
                            }
                            if (nowPlaying.contains("gen5-b2w2-gym")) {
                                b.music.clear();
                                b.music.play(new File(b.ROOT_PATH + "gen5-b2w2-gym-final.mp3"));
                                musicChanged = true;
                            }
                        }
                    }
                }
                b.sendMessage(channel, "Waiting on communication...");
                b.sendWhisper(player1.getTrainerName(), "What will " + pokemon1.getName() + " do? (!move1)" + pokemon1.getMove1().getName() + ", (!move2)" + pokemon1.getMove2().getName() + ", (!move3)" + pokemon1.getMove3().getName() + ", (!move4)" + pokemon1.getMove4().getName() + " (!help)Additional Commands (reply in Battle Dungeon)");
                b.sendWhisper(player2.getTrainerName(), "What will " + pokemon2.getName() + " do? (!move1)" + pokemon2.getMove1().getName() + ", (!move2)" + pokemon2.getMove2().getName() + ", (!move3)" + pokemon2.getMove3().getName() + ", (!move4)" + pokemon2.getMove4().getName() + " (!help)Additional Commands (reply in Battle Dungeon)");
                pokemon1.setFlinch(false);
                pokemon2.setFlinch(false);
                p1switch = false;
                p2switch = false;
                String p1move = "", p2move = "";
                int p1switchto = -1;
                int p2switchto = -1;
                try {
                    p1move = p1msg.poll(60, TimeUnit.SECONDS);
                    if (p1move == null) {
                        b.sendMessage(channel, player1.getTrainerName() + " did not select a move in time. " + player2.getTrainerName() + " wins!");
                        b.music.clear();
                        return;
                    }
                    if (p1move.startsWith("!switch") && p1move.length() >= 8) {
                        if (!Character.isDigit(p1move.charAt(7))) {
                            p1move = "";
                            b.sendMessage(channel, "/w " + player1.getTrainerName() + " Invalid Pokemon Position FUNgineer");
                        } else {
                            p1switchto = Integer.parseInt(p1move.charAt(7) + "");
                            try {
                                if (player1.getPokemon(p1switchto).isFainted()) {
                                    b.sendMessage(channel, "/w " + player1.getTrainerName() + " You cannot switch to a fainted Pokemon FUNgineer");
                                    p1move = "";
                                }
                            } catch (Exception ex) {
                                b.sendMessage(channel, "/w " + player1.getTrainerName() + " Invalid Pokemon Position FUNgineer");
                                p1move = "";
                            }
                            p1switch = true;
                        }
                    }
                    while (!p1move.startsWith("!run") && !p1move.startsWith("!move") && !p1move.startsWith("!switch")) {
                        p1move = p1msg.take();
                        if (p1move.startsWith("!switch") && p1move.length() >= 8) {
                            if (!Character.isDigit(p1move.charAt(7))) {
                                b.sendMessage(channel, "/w " + player1.getTrainerName() + " Invalid Pokemon Position FUNgineer");
                                p1move = "";
                            } else {
                                p1switchto = Integer.parseInt(p1move.charAt(7) + "");
                                try {
                                    if (player1.getPokemon(p1switchto).isFainted()) {
                                        b.sendMessage(channel, "/w " + player1.getTrainerName() + " You cannot switch to a fainted Pokemon FUNgineer");
                                        p1move = "";
                                        continue;
                                    }
                                } catch (Exception ex) {
                                    b.sendMessage(channel, "/w " + player1.getTrainerName() + " Invalid Pokemon Position FUNgineer");
                                    p1move = "";
                                    continue;
                                }
                                p1switch = true;
                                break;
                            }
                        } else if (!p1move.startsWith("!move") && !p1move.startsWith("!run")) {
                            p1move = "";
                        }
                    }
                    p1msg = new LinkedBlockingQueue<>();
                    p2move = p2msg.poll(60, TimeUnit.SECONDS);
                    if (p2move == null) {
                        b.sendMessage(channel, player2.getTrainerName() + " did not select a move in time. " + player1.getTrainerName() + " wins!");
                        b.music.clear();
                        return;
                    }
                    if (p2move.startsWith("!switch") && p2move.length() >= 8) {
                        if (!Character.isDigit(p2move.charAt(7))) {
                            b.sendMessage(channel, "/w " + player2.getTrainerName() + " Invalid Pokemon Position FUNgineer");
                            p2move = "";
                        } else {
                            p2switchto = Integer.parseInt(p2move.charAt(7) + "");
                            try {
                                if (player2.getPokemon(p2switchto).isFainted()) {
                                    b.sendMessage(channel, "/w " + player2.getTrainerName() + " You cannot switch to a fainted Pokemon FUNgineer");
                                    p2move = "";
                                }
                            } catch (Exception ex) {
                                b.sendMessage(channel, "/w " + player2.getTrainerName() + " Invalid Pokemon Position FUNgineer");
                                p2move = "";
                            }
                            p2switch = true;
                        }
                    }
                    while (!p2move.equalsIgnoreCase("!run") && !p2move.startsWith("!move") && !p2move.startsWith("!switch")) {
                        p2move = p2msg.take();
                        if (p2move.startsWith("!switch") && p2move.length() >= 8) {
                            if (!Character.isDigit(p2move.charAt(7))) {
                                b.sendMessage(channel, "/w " + player2.getTrainerName() + " Invalid Pokemon Position FUNgineer");
                                p2move = "";
                            } else {
                                p2switchto = Integer.parseInt(p2move.charAt(7) + "");
                                try {
                                    if (player2.getPokemon(p2switchto).isFainted()) {
                                        b.sendMessage(channel, "/w " + player2.getTrainerName() + " You cannot switch to a fainted Pokemon FUNgineer");
                                        p2move = "";
                                        continue;
                                    }
                                } catch (Exception ex) {
                                    b.sendMessage(channel, "/w " + player2.getTrainerName() + " Invalid Pokemon Position FUNgineer");
                                    p2move = "";
                                    continue;
                                }
                                p2switch = true;
                                break;
                            }
                        } else if (!p2move.startsWith("!move") && !p2move.startsWith("!run")) {
                            p2move = "";
                            continue;
                        }
                    }
                    p2msg = new LinkedBlockingQueue<>();
                } catch (Exception ex) {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    ex.printStackTrace(pw);
                    b.music.sendMessage(b.music.getChannel(), b.music.CHEF.mention() + " ```" + sw.toString() + "```");
                }
                if (p1move.startsWith("!run") && !p2move.startsWith("!run")) {
                    b.sendMessage(channel, player1.getTrainerName() + " forfeits! " + player2.getTrainerName() + " wins!");
                    b.music.clear();
                    return;
                }
                if (p2move.startsWith("!run") && !p1move.startsWith("!run")) {
                    b.sendMessage(channel, player2.getTrainerName() + " forfeits! " + player1.getTrainerName() + " wins!");
                    b.music.clear();
                    return;
                }
                if (p1move.startsWith("!run") && p2move.startsWith("!run")) {
                    b.sendMessage(channel, player1.getTrainerName() + " forfeits! " + player2.getTrainerName() + " forfeits as well! The result of the Battle is a Draw! PipeHype");
                    b.music.clear();
                    return;
                }
                if (Character.isDigit(p1move.charAt(5))) {
                    p1move = "" + p1move.charAt(5);
                }
                if (Character.isDigit(p2move.charAt(5))) {
                    p2move = "" + p2move.charAt(5);
                }
                if (p1switch && !p2switch) {
                    String oldName = pokemon1.getName();
                    String newName = "";
                    player1.addPokemon(pokemon1);
                    pokemon1 = player1.getPokemon(p1switchto);
                    player1.removePokemon(p1switchto);
                    newName = pokemon1.getName();
                    b.sendMessage(channel, player1.getTrainerName() + " calls back " + oldName + " and sent out " + newName + "!");
                    //doPlayer2Move(b, channel, p2move);
                    doMove(b, channel, p2move, pokemon2, pokemon1);
                    if (pokemon1.isFainted()) {
                        break singlebattle;
                    }
                    continue;
                }
                if (p2switch && !p1switch) {
                    String oldName = pokemon2.getName();
                    String newName = "";
                    player2.addPokemon(pokemon2);
                    pokemon2 = player2.getPokemon(p2switchto);
                    player2.removePokemon(p2switchto);
                    newName = pokemon2.getName();
                    b.sendMessage(channel, player2.getTrainerName() + " calls back " + oldName + " and sent out " + newName + "!");
                    //doPlayer1Move(b, channel, p1move);
                    doMove(b, channel, p1move, pokemon1, pokemon2);
                    if (pokemon2.isFainted()) {
                        break singlebattle;
                    }
                    continue;
                }
                if (p1switch && p2switch) {
                    String oldName = pokemon1.getName();
                    String newName = "";
                    player1.addPokemon(pokemon1);
                    pokemon1 = player1.getPokemon(p1switchto);
                    player1.removePokemon(p1switchto);
                    newName = pokemon1.getName();
                    b.sendMessage(channel, player1.getTrainerName() + " calls back " + oldName + " and sent out " + newName + "!");
                    oldName = pokemon2.getName();
                    newName = "";
                    player2.addPokemon(pokemon2);
                    pokemon2 = player2.getPokemon(p2switchto);
                    player2.removePokemon(p2switchto);
                    newName = pokemon2.getName();
                    b.sendMessage(channel, player2.getTrainerName() + " calls back " + oldName + " and sent out " + newName + "!");
                    continue;
                }
                //main battle
                Pokemon first, second;
                String m1, m2;
                if (pokemon1.getStat(Stats.SPEED) > pokemon2.getStat(Stats.SPEED)) {
//                    doPlayer1Move(b, channel, p1move);
//                    if (!pokemon2.isFainted()) {
//                        doPlayer2Move(b, channel, p2move);
//                    } else {
//                        break singlebattle;
//                    }
                    first = pokemon1;
                    m1 = p1move;
                    second = pokemon2;
                    m2 = p2move;
                } else if (pokemon1.getStat(Stats.SPEED) < pokemon2.getStat(Stats.SPEED)) {
//                    doPlayer2Move(b, channel, p2move);
//                    if (!pokemon1.isFainted()) {
//                        doPlayer1Move(b, channel, p1move);
//                    } else {
//                        break singlebattle;
//                    }
                    first = pokemon2;
                    m1 = p2move;
                    second = pokemon1;
                    m2 = p1move;
                } else if (new SecureRandom().nextBoolean()) {
                    first = pokemon1;
                    m1 = p1move;
                    second = pokemon2;
                    m2 = p2move;
                } else {
                    first = pokemon2;
                    m1 = p2move;
                    second = pokemon1;
                    m2 = p1move;
                } //                    if (p1First) {
                //                        doPlayer1Move(b, channel, p1move);
                //                        if (!pokemon2.isFainted()) {
                //                            doPlayer2Move(b, channel, p2move);
                //                        } else {
                //                            break singlebattle;
                //                        }
                //                    } else {
                //                        doPlayer2Move(b, channel, p2move);
                //                        if (!pokemon1.isFainted()) {
                //                            doPlayer1Move(b, channel, p1move);
                //                        } else {
                //                            break singlebattle;
                //                        }
                //                    }
                boolean fainted = mainBattle(first, second, m1, m2, b, channel);
                if (fainted) {
                    break singlebattle;
                }
            }
            if (pokemon1.isFainted()) {
                if (player1.getPokemon().isEmpty()) {
                    break;
                } else {
                    switchPlayer1(b, channel);
                    if (endBattle) {
                        b.sendMessage(channel, "Something went wrong this battle is now over all the Pokemon got stolen by Team Rocket RuleFive");
                        b.music.clear();
                        return;
                    }
                    continue;
                }
            } else if (pokemon2.isFainted()) {
                if (player2.getPokemon().isEmpty()) {
                    break;
                } else {
                    switchPlayer2(b, channel);
                    if (endBattle) {
                        b.sendMessage(channel, "Something went wrong this battle is now over all the Pokemon got stolen by Team Rocket RuleFive");
                        b.music.clear();
                        return;
                    }
                    continue;
                }
            }
            if (endBattle) {
                b.sendMessage(channel, "Something went wrong this battle is now over all the Pokemon got stolen by Team Rocket RuleFive");
                b.music.clear();
                return;
            }
            //} while ((!player1.getPokemon().isEmpty() && !player2.getPokemon().isEmpty()) && (!pokemon1.isFainted() && !pokemon2.isFainted()));
        } while (continueBattle());
        if (pokemon1.isFainted() && pokemon2.isFainted()) {
            b.sendMessage(channel, pokemon1.getName() + " fainted! But " + pokemon2.getName() + " fainted too! The Battle ends in a Draw! NotLikeThis");
        } else if (pokemon1.isFainted() && !pokemon2.isFainted()) {
            if (pokemon2.getLevel() == 100) {
                b.sendMessage(channel, pokemon1.getName() + " fainted! " + player1.getTrainerName() + " is out of usable Pokemon! " + player2 + " wins! PogChamp");
            } else {
                int levelBefore = pokemon2.getLevel();
                int exp = Pokemon.calculateExperience(true, pokemon2, pokemon1);
                pokemon2.addExperience(exp);
                int levelAfter = pokemon2.getLevel();
                b.sendMessage(channel, pokemon1.getName() + " fainted! " + pokemon2.getName() + " gained " + exp + " Exp. Points!");
                if (levelBefore < levelAfter) {
                    b.sendMessage(channel, pokemon2.getName() + " grew to Level " + levelAfter + "! PogChamp");
                }
                b.sendMessage(channel, player1.getTrainerName() + " is out of usable Pokemon! " + player2 + " wins! PogChamp");
            }
        } else if (!pokemon1.isFainted() && pokemon2.isFainted()) {
            if (pokemon1.getLevel() == 100) {
                b.sendMessage(channel, pokemon2.getName() + " fainted! " + player2.getTrainerName() + " is out of usable Pokemon! " + player1 + " wins! PogChamp");
            } else {
                int levelBefore = pokemon1.getLevel();
                int exp = Pokemon.calculateExperience(true, pokemon1, pokemon2);
                pokemon1.addExperience(exp);
                int levelAfter = pokemon1.getLevel();
                b.sendMessage(channel, pokemon2.getName() + " fainted! " + pokemon1.getName() + " gained " + exp + " Exp. Points!");
                if (levelBefore < levelAfter) {
                    b.sendMessage(channel, pokemon1.getName() + " grew to Level " + levelAfter + "! PogChamp");
                }
                b.sendMessage(channel, player2.getTrainerName() + " is out of usable Pokemon! " + player1 + " wins! PogChamp");
            }
        } else {
            System.err.println("Loop dun fucked up.");
        }
        b.music.clear();
    }

    public boolean mainBattle(Pokemon p1, Pokemon p2, String move1, String move2, BattleBot b, String channel) {
        doMove(b, channel, move1, p1, p2);
        if (!p2.isFainted()) {
            if (p2.isFlinched()) {
                p2.setFlinch(false);
                b.sendMessage(channel, p2.getName() + " flinched!");
                return false;
            }
            doMove(b, channel, move2, p2, p1);
            return false;
        } else {
            return true;
        }
    }

    public boolean continueBattle() {
        if (pokemon1.isFainted()) {
            if (player1.getPokemon().isEmpty()) {
                return false;
            }
        }
        if (pokemon2.isFainted()) {
            if (player2.getPokemon().isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
