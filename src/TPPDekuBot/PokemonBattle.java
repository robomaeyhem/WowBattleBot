package TPPDekuBot;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class PokemonBattle extends Battle {

    private BattleBot b;
    private String channel;
    private String player;

    public PokemonBattle(BattleBot b, String channel, boolean bigBrother, boolean fromChef, String player, boolean test) {
        super(b);
        this.b = b;
        this.channel = channel;
        this.player = player;
        b.battle = this;
        SecureRandom rand = new SecureRandom();
        int rand1 = rand.nextInt(718);
        int rand2 = rand1;
        while (rand2 == rand1) {
            rand2 = rand.nextInt(718);
        }
        if (Pokemon.isBannedPokemon(rand1) || Pokemon.isBannedPokemon(rand2)) {
            rand1 = rand.nextInt(718);
            rand2 = rand1;
            while (rand2 == rand1) {
                rand2 = rand.nextInt(718);
            }
        }
        int level1 = rand.nextInt(100 - 20 + 1) + 20;
        while (level1 < 20) {
            level1 = rand.nextInt(100 - 20 + 1) + 20;
        }
        int level1bufUpper = level1 + 7;
        int level1bufLower = level1 - 7;
        if (level1bufUpper > 100) {
            level1bufUpper = 100;
        }
        if (level1bufLower < 20) {
            level1bufLower = 20;
        }
        int level2 = rand.nextInt((level1bufUpper - level1bufLower) + 1) + level1bufLower;
        if (bigBrother) {
            if (fromChef) {
                level1 = 100;
                rand1 = 129;
            } else {
                level2 = 100;
                rand2 = 129;
            }
        }
        System.out.println("Generated userID " + rand1 + ", level " + level1 + " and computerID " + rand2 + ", level " + level2);
        Pokemon user = new Pokemon(rand1, level1);
        Pokemon computer = new Pokemon(rand2, level2);
        try {
            if (bigBrother) {
                if (fromChef) {
                    user.setMove1(Pokemon.getMove("Shadow-force"));
                    user.setMove2(Pokemon.getMove("Explosion"));
                    user.setMove3(Pokemon.getMove("Overheat"));
                    user.setMove4(Pokemon.getMove("Hydro-pump"));
                    user.setStat(Stats.HP, 1000);
                    user.setStat(Stats.ATTACK, 1000);
                    user.setStat(Stats.DEFENSE, 1000);
                    user.setStat(Stats.SPEED, 1000);
                    user.setStat(Stats.SP_ATTACK, 1000);
                    user.setStat(Stats.SP_DEFENSE, 1000);
                    computer.assignMoves();
                } else {
                    computer.setMove1(Pokemon.getMove("Shadow-force"));
                    computer.setMove2(Pokemon.getMove("Explosion"));
                    computer.setMove3(Pokemon.getMove("Overheat"));
                    computer.setMove4(Pokemon.getMove("Hydro-pump"));
                    computer.setStat(Stats.HP, 1000);
                    computer.setStat(Stats.ATTACK, 1000);
                    computer.setStat(Stats.DEFENSE, 1000);
                    computer.setStat(Stats.SPEED, 1000);
                    computer.setStat(Stats.SP_ATTACK, 1000);
                    computer.setStat(Stats.SP_DEFENSE, 1000);
                    user.assignMoves();
                }
            }
            if (user.getLevel() < 0 || computer.getLevel() < 0) {
                throw new Exception("Level is negative!!");
            }
            if (!bigBrother) {
                user.assignMoves();
                computer.assignMoves();
            }
        } catch (Exception ex) {
            System.err.println("[POKEMON] Failed to generate moves! UserID: " + rand1 + ", ComputerID: " + rand2 + "\n[POKEMON] " + ex);
            b.sendMessage(channel, "Something fucked up OneHand give it another try");
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            b.music.sendMessage(b.music.getChannel(), b.music.CHEF.mention() + " ```" + sw.toString() + "\nUser ID: " + rand1 + " Computer ID: " + rand2 + "```");
            b.battle = null;
            return;
        }
        if (test) {
            user = new Pokemon(363, 70);
            computer = new Pokemon(170, 51);
            HashMap<String, Move> moves = Pokemon.reloadMoves();
            user.setMove1(moves.get("Horn-drill"));
            user.setMove2(moves.get("Double-edge"));
            user.setMove3(moves.get("Toxic"));
            user.setMove4(moves.get("Double-slap"));
            user.setStat(Stats.SPEED, 1000);
            user.setStat(Stats.HP, 10000);
            computer.setMove1(moves.get("Take-down"));
            computer.setMove2(moves.get("Thunderbolt"));
            computer.setMove3(moves.get("Round"));
            computer.setMove4(moves.get("Blizzard"));
        }
        b.music.play(b.determineMusic(computer));
        b.sendMessage(channel, (BattleBot.isLegendary(computer.getId()) ? "Woah! " : "") + "A wild " + computer.getName() + " (level " + (bigBrother && !fromChef ? "1͗̎̔ͪͫ̃͒͜͠҉̥̝̜ͅͅ0̴̵̞͖̪̻͎̦̯̒̔ͫ̾ͣ̃̅̉0̑̔̽̓͊̈̏ͧ̀̾͆͜͏̸̹̹͇̠̺̞̻̯̦͈̦̹̥͕̙" : computer.getLevel()) + ") appeared! Go " + user.getName() + "! (Level " + (bigBrother && fromChef && user.getName().equalsIgnoreCase("Magikarp") && user.getMove1().getName().equalsIgnoreCase("Shadow-force") ? "1͗̎̔ͪͫ̃͒͜͠҉̥̝̜ͅͅ0̴̵̞͖̪̻͎̦̯̒̔ͫ̾ͣ̃̅̉0̑̔̽̓͊̈̏ͧ̀̾͆͜͏̸̹̹͇̠̺̞̻̯̦͈̦̹̥͕̙" : user.getLevel()) + ")");
        System.err.println("User moves = " + user.getMove1().getName() + ", " + user.getMove2().getName() + ", " + user.getMove3().getName() + ", " + user.getMove4().getName() + ", ");
        System.err.println("Computer moves = " + computer.getMove1().getName() + ", " + computer.getMove2().getName() + ", " + computer.getMove3().getName() + ", " + computer.getMove4().getName() + ", ");
        try {
            while (!user.isFainted() && !computer.isFainted()) {
                BattleBot.pokemonMessages = new LinkedBlockingQueue<>();
                b.sendMessage(channel, "What will " + user.getName() + " do? (!move1)" + user.getMove1().getName() + ", (!move2)" + user.getMove2().getName() + ", (!move3)" + user.getMove3().getName() + ", (!move4)" + user.getMove4().getName());
                user.setFlinch(false);
                computer.setFlinch(false);
                int userSpeed = user.getStat(Stats.SPEED);
                int compSpeed = computer.getStat(Stats.SPEED);
                if (user.getStatus() == Status.PARALYSIS) {
                    userSpeed = userSpeed / 2;
                }
                if (computer.getStatus() == Status.PARALYSIS) {
                    compSpeed = compSpeed / 2;
                }
                if (userSpeed > compSpeed) {
                    String move = BattleBot.pokemonMessages.poll(60, TimeUnit.SECONDS);
                    if (move == null) {
                        b.sendMessage(channel, player + " did not select a move in time and got their Pokemon stolen by Team Rocket! RuleFive");
                        return;
                    }
                    if (move.equalsIgnoreCase("run")) {
                        b.sendMessage(channel, "You got away safely!");
                        return;
                    }
                    doUsersMove(user, computer, move);
                    if (!computer.isFainted()) {
                        if (computer.isFlinched()) {
                            b.sendMessage(channel, computer.getName() + " flinched!");
                            computer.setFlinch(false);
                        } else {
                            doComputerMove(user, computer);
                        }
                    }
                } else if (userSpeed < compSpeed) {
                    String move = BattleBot.pokemonMessages.poll(60, TimeUnit.SECONDS);
                    if (move == null) {
                        b.sendMessage(channel, player + " did not select a move in time and got their Pokemon stolen by Team Rocket! RuleFive");
                        return;
                    }
                    doComputerMove(user, computer);
                    if (!user.isFainted()) {
                        if (user.isFlinched()) {
                            b.sendMessage(channel, user.getName() + " flinched!");
                            user.setFlinch(false);
                        } else {
                            if (move.equalsIgnoreCase("run")) {
                                b.sendMessage(channel, "You got away safely!");
                                return;
                            }
                            doUsersMove(user, computer, move);
                        }
                    }
                } else {
                    rand = new SecureRandom();
                    int chance = rand.nextInt(2);
                    if (chance == 1) {
                        String move = BattleBot.pokemonMessages.poll(60, TimeUnit.SECONDS);
                        if (move == null) {
                            b.sendMessage(channel, player + " did not select a move in time and got their Pokemon stolen by Team Rocket! RuleFive");
                            return;
                        }
                        if (move.equalsIgnoreCase("run")) {
                            b.sendMessage(channel, "You got away safely!");
                            return;
                        }
                        doUsersMove(user, computer, move);
                        if (!computer.isFainted()) {
                            if (computer.isFlinched()) {
                                b.sendMessage(channel, computer.getName() + " flinched!");
                                computer.setFlinch(false);
                            } else {
                                doComputerMove(user, computer);
                            }
                        }
                    } else {
                        String move = BattleBot.pokemonMessages.poll(60, TimeUnit.SECONDS);
                        if (move == null) {
                            b.sendMessage(channel, player + " did not select a move in time and got their Pokemon stolen by Team Rocket! RuleFive");
                            return;
                        }
                        doComputerMove(user, computer);
                        if (!user.isFainted()) {
                            if (user.isFlinched()) {
                                b.sendMessage(channel, user.getName() + " flinched!");
                                user.setFlinch(false);
                            } else {
                                if (move.equalsIgnoreCase("run")) {
                                    b.sendMessage(channel, "You got away safely!");
                                    return;
                                }
                                doUsersMove(user, computer, move);
                            }
                        }
                    }
                }
            }
            if (user.isFainted()) {
                b.sendMessage(channel, user.getName() + " fainted! You lose! BibleThump");
            } else if (computer.isFainted()) {
                b.sendMessage(channel, computer.getName() + " fainted! You Win! PogChamp");
                if (user.getLevel() != 100) {
                    int levelBefore = user.getLevel();
                    int exp = Pokemon.calculateExperience(false, user, computer);
                    user.addExperience(exp);
                    int levelAfter = user.getLevel();
                    b.sendMessage(channel, user.getName() + " gained " + exp + " Exp. Points!");
                    if (levelBefore < levelAfter) {
                        b.sendMessage(channel, user.getName() + " grew to Level " + levelAfter + "! PogChamp");
                    }
                }
            }
        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            b.music.sendMessage(b.music.getChannel(), b.music.CHEF.mention() + " ```" + sw.toString() + "\nUser ID: " + rand1 + " Computer ID: " + rand2 + "```");
        }
    }

    private void doComputerMove(Pokemon user, Pokemon computer) {
        b.sendMessage(channel, computer.attack(user, Move.selectBestMove(computer, user), this).replace("\n", " "));
    }

    private void doUsersMove(Pokemon user, Pokemon computer, String move) {
        switch (move) {
            case "1":
                b.sendMessage(channel, user.attack(computer, user.getMove1(), this).replace("\n", " "));
                break;
            case "2":
                b.sendMessage(channel, user.attack(computer, user.getMove2(), this).replace("\n", " "));
                break;
            case "3":
                b.sendMessage(channel, user.attack(computer, user.getMove3(), this).replace("\n", " "));
                break;
            case "4":
                b.sendMessage(channel, user.attack(computer, user.getMove4(), this).replace("\n", " "));
                break;
            case "run":
                b.sendMessage(channel, "You got away safely!");
                return;
        }
    }

}
