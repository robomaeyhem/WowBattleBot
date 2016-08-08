package TPPDekuBot;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.SecureRandom;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class PWTBattle extends Battle {

    private PWTType type;
    private PWTClass pwtclass;
    public Trainer player1;
    public Trainer player2;
    private Pokemon pokemon1;
    private Pokemon pokemon2;
    private PWTRound round;
    private boolean endBattle = false;
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
     * @param channel Channel to send message to.
     * @return Trainer that was victorious, null if tie.
     * @throws Exception If Battle ends in an error
     */
    public Trainer doBattle(String channel) throws Exception {
        Trainer winner = null;
        pokemon1 = player1.getPokemon(0);
        pokemon2 = player2.getPokemon(0);
        player1.removePokemon(0);
        player2.removePokemon(0);
        p1msg = new LinkedBlockingQueue<>();
        p2msg = new LinkedBlockingQueue<>();
        b.sendMessage(channel, player1.getTrainerName() + " sends out " + pokemon1.getName() + " (Level " + pokemon1.getLevel() + ")! " + player2.getTrainerName() + " sends out " + pokemon2.getName() + " (Level " + pokemon2.getLevel() + ")!");
        do {
            singlebattle:
            while (!pokemon1.isFainted() && !pokemon2.isFainted()) {
                p1msg = new LinkedBlockingQueue<>();
                p2msg = new LinkedBlockingQueue<>();
                b.sendMessage(channel, "Waiting on communication...");
                if (Trainer.isUserBot(player1.getTrainerName()) || !player1.isAI()) {
                    b.sendWhisper(player1.getTrainerName(), "What will " + pokemon1.getName() + " do? (!move1)" + pokemon1.getMove1().getName() + ", (!move2)" + pokemon1.getMove2().getName() + ", (!move3)" + pokemon1.getMove3().getName() + ", (!move4)" + pokemon1.getMove4().getName() + " (!help)Additional Commands (reply in Battle Dungeon)");
                } else {
                    p1msg.add(PWTBattle.PWTAIMove(pokemon1, pokemon2));
                }
                if (Trainer.isUserBot(player2.getTrainerName()) || !player2.isAI()) {
                    b.sendWhisper(player2.getTrainerName(), "What will " + pokemon2.getName() + " do? (!move1)" + pokemon2.getMove1().getName() + ", (!move2)" + pokemon2.getMove2().getName() + ", (!move3)" + pokemon2.getMove3().getName() + ", (!move4)" + pokemon2.getMove4().getName() + " (!help)Additional Commands (reply in Battle Dungeon)");
                } else {
                    p2msg.add(PWTBattle.PWTAIMove(pokemon2, pokemon1));
                }
                pokemon1.setFlinch(false);
                pokemon2.setFlinch(false);
                String p1move = "";
                String p2move = "";
                int p1switchTo = -1;
                int p2switchTo = -1;
                boolean p1switch = false, p2switch = false;
                boolean p1forfeit = false, p2forfeit = false;
                try {
                    while (!p1move.startsWith("!move") && !p1move.startsWith("!run") && !p1move.startsWith("!switch")) {
                        p1move = p1msg.poll(60, TimeUnit.SECONDS);
                        if (p1move == null) {
                            b.sendMessage(channel, player1.getTrainerName() + " did not select a move in time. " + player2 + " wins!");
                            player1.addPokemonAtBeginning(pokemon1);
                            player2.addPokemonAtBeginning(pokemon2);
                            player1.heal();
                            player2.heal();
                            return player2;
                        }
                        p1move = p1move.toLowerCase();
                        if (p1move.startsWith("!switch")) {
                            if (p1move.length() >= 8) {
                                if (Character.isDigit(p1move.charAt(7))) {
                                    p1switchTo = Integer.parseInt(p1move.split("!switch", 2)[1].split(" ", 2)[0]);
                                    if (p1switchTo >= 2 || p1switchTo < 0) {
                                        p1switchTo = -1;
                                        p1move = "";
                                        b.sendMessage(channel, "/w " + player1.getTrainerName() + " Invalid Pokemon Position FUNgineer");
                                    } else if (player1.getPokemon(p1switchTo).isFainted()) {
                                        p1switchTo = -1;
                                        p1move = "";
                                        b.sendMessage(channel, "/w " + player1.getTrainerName() + " You cannot switch to a fainted Pokemon FUNgineer");
                                    } else {
                                        p1switch = true;
                                        break;
                                    }
                                } else {
                                    b.sendMessage(channel, "/w " + player1.getTrainerName() + " Syntax: !switch<number> where <number> is the number of the Pokemon you want to switch to from !list. Make sure there is no space between the number and the !switch command. For example, !switch0 will switch to the first Pokemon in the list.");
                                    p1switchTo = -1;
                                    p1move = "";
                                }
                            } else {
                                p1move = "";
                            }
                        } else if (p1move.startsWith("!move")) {
                            if (p1move.length() >= 5 && Character.isDigit(p1move.charAt(5))) {
                                p1move = p1move.charAt(5) + "";
                                break;
                            } else {
                                p2move = "";
                            }
                        } else if (p1move.startsWith("!run")) {
                            p1forfeit = true;
                            break;
                        } else {
                            p1move = "";
                        }
                    }
                } catch (Exception ex) {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    ex.printStackTrace(pw);
                    b.music.sendMessage(b.music.getChannel(), b.music.CHEF.mention() + " ```An error occurred while attempting to get " + player1 + "'s move\n" + sw.toString() + "```");
                }
                try {
                    while (!p2move.startsWith("!move") && !p2move.startsWith("!run") && !p2move.startsWith("!switch")) {
                        p2move = p2msg.poll(60, TimeUnit.SECONDS);
                        if (p2move == null) {
                            b.sendMessage(channel, player2.getTrainerName() + " did not select a move in time. " + player1 + " wins!");
                            player1.addPokemonAtBeginning(pokemon1);
                            player2.addPokemonAtBeginning(pokemon2);
                            player1.heal();
                            player2.heal();
                            return player1;
                        }
                        p2move = p2move.toLowerCase();
                        if (p2move.startsWith("!switch")) {
                            if (p2move.length() >= 8) {
                                if (Character.isDigit(p2move.charAt(7))) {
                                    p2switchTo = Integer.parseInt(p2move.split("!switch", 2)[1].split(" ", 2)[0]);
                                    if (p2switchTo >= 2 || p2switchTo < 0) {
                                        p2switchTo = -1;
                                        p2move = "";
                                        b.sendMessage(channel, "/w " + player2.getTrainerName() + " Invalid Pokemon Position FUNgineer");
                                    } else if (player2.getPokemon(p2switchTo).isFainted()) {
                                        p2switchTo = -1;
                                        p2move = "";
                                        b.sendMessage(channel, "/w " + player2.getTrainerName() + " You cannot switch to a fainted Pokemon FUNgineer");
                                    } else {
                                        p2switch = true;
                                        break;
                                    }
                                } else {
                                    b.sendMessage(channel, "/w " + player2.getTrainerName() + " Syntax: !switch<number> where <number> is the number of the Pokemon you want to switch to from !list. Make sure there is no space between the number and the !switch command. For example, !switch0 will switch to the first Pokemon in the list.");
                                    p2switchTo = -1;
                                    p2move = "";
                                }
                            } else {
                                p2move = "";
                            }
                        } else if (p2move.startsWith("!move")) {
                            if (p2move.length() >= 5 && Character.isDigit(p2move.charAt(5))) {
                                p2move = p2move.charAt(5) + "";
                                break;
                            } else {
                                p2move = "";
                            }
                        } else if (p2move.startsWith("!run")) {
                            p2forfeit = true;
                            break;
                        } else {
                            p2move = "";
                        }
                    }
                } catch (Exception ex) {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    ex.printStackTrace(pw);
                    b.music.sendMessage(b.music.getChannel(), b.music.CHEF.mention() + " ```An error occurred while attempting to get " + player2 + "'s move\n" + sw.toString() + "```");
                }
                //here we go
                if (p1forfeit) {
                    if (p2forfeit) {
                        b.sendMessage(channel, player1.getTrainerName() + " forfeits! But " + player2 + " forfeits as well! This match ends in a draw!");
                        player1.addPokemonAtBeginning(pokemon1);
                        player2.addPokemonAtBeginning(pokemon2);
                        player1.heal();
                        player2.heal();
                        return null;
                    } else {
                        b.sendMessage(channel, player1.getTrainerName() + " forfeits! " + player2 + " wins!");
                        player1.addPokemonAtBeginning(pokemon1);
                        player2.addPokemonAtBeginning(pokemon2);
                        player1.heal();
                        player2.heal();
                        return player2;
                    }
                } else if (p2forfeit) {
                    if (p1forfeit) {
                        b.sendMessage(channel, player2.getTrainerName() + " forfeits! But " + player1 + " forfeits as well! This match ends in a draw!");
                        player1.addPokemonAtBeginning(pokemon1);
                        player2.addPokemonAtBeginning(pokemon2);
                        player1.heal();
                        player2.heal();
                        return null;
                    } else {
                        b.sendMessage(channel, player2.getTrainerName() + " forfeits! " + player1 + " wins!");
                        player1.addPokemonAtBeginning(pokemon1);
                        player2.addPokemonAtBeginning(pokemon2);
                        player1.heal();
                        player2.heal();
                        return player1;
                    }
                }
                if (p1switch && !p2switch) {
                    String oldName = pokemon1.getName();
                    String newName = "";
                    player1.addPokemon(pokemon1);
                    pokemon1 = player1.getPokemon(p1switchTo);
                    player1.removePokemon(p1switchTo);
                    newName = pokemon1.getName();
                    b.sendMessage(channel, player1.getTrainerName() + " calls back " + oldName + " and sent out " + newName + "!");
                    doMove(channel, p2move, pokemon2, pokemon1);
                    if (pokemon1.isFainted()) {
                        break singlebattle;
                    }
                    continue;
                }
                if (p2switch && !p1switch) {
                    String oldName = pokemon2.getName();
                    String newName = "";
                    player2.addPokemon(pokemon2);
                    pokemon2 = player2.getPokemon(p2switchTo);
                    player2.removePokemon(p2switchTo);
                    newName = pokemon2.getName();
                    b.sendMessage(channel, player2.getTrainerName() + " calls back " + oldName + " and sent out " + newName + "!");
                    doMove(channel, p1move, pokemon1, pokemon2);
                    if (pokemon2.isFainted()) {
                        break singlebattle;
                    }
                    continue;
                }
                if (p1switch && p2switch) {
                    String oldName = pokemon1.getName();
                    String newName = "";
                    player1.addPokemon(pokemon1);
                    pokemon1 = player1.getPokemon(p1switchTo);
                    player1.removePokemon(p1switchTo);
                    newName = pokemon1.getName();
                    b.sendMessage(channel, player1.getTrainerName() + " calls back " + oldName + " and sent out " + newName + "!");
                    oldName = pokemon2.getName();
                    newName = "";
                    player2.addPokemon(pokemon2);
                    pokemon2 = player2.getPokemon(p2switchTo);
                    player2.removePokemon(p2switchTo);
                    newName = pokemon2.getName();
                    b.sendMessage(channel, player2.getTrainerName() + " calls back " + oldName + " and sent out " + newName + "!");
                    continue;
                }
                int p1speed = pokemon1.getStat(Stats.SPEED);
                int p2speed = pokemon2.getStat(Stats.SPEED);
                Pokemon first, second;
                String m1, m2;
                if (pokemon1.getStatus() == Status.PARALYSIS) {
                    p1speed = p1speed / 2;
                }
                if (pokemon2.getStatus() == Status.PARALYSIS) {
                    p2speed = p2speed / 2;
                }
                if (p1speed > p2speed) {
                    first = pokemon1;
                    m1 = p1move;
                    second = pokemon2;
                    m2 = p2move;
                } else if (p1speed < p2speed) {
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
                }
                boolean fainted = mainBattle(first, second, m1, m2, channel);
                if (fainted) {
                    break singlebattle;
                }
            }
            if (pokemon1.isFainted()) {
                if (player1.getNumberOfPokemonRemaining() == 0) {
                    break;
                } else {
                    switchPokemon(player1, this.pokemon1, channel);
                    if (endBattle) {
                        b.sendMessage(channel, "Something went wrong this battle is now over all the Pokemon got stolen by Team Rocket RuleFive");
                        player1.addPokemonAtBeginning(pokemon1);
                        player2.addPokemonAtBeginning(pokemon2);
                        player1.heal();
                        player2.heal();
                        return null;
                    }
                    continue;
                }
            } else if (pokemon2.isFainted()) {
                if (player2.getNumberOfPokemonRemaining() == 0) {
                    break;
                } else {
                    switchPokemon(player2, this.pokemon2, channel);
                    if (endBattle) {
                        b.sendMessage(channel, "Something went wrong this battle is now over all the Pokemon got stolen by Team Rocket RuleFive");
                        player1.addPokemonAtBeginning(pokemon1);
                        player2.addPokemonAtBeginning(pokemon2);
                        player1.heal();
                        player2.heal();
                        return null;
                    }
                    continue;
                }
            }
            if (endBattle) {
                b.sendMessage(channel, "Something went wrong this battle is now over all the Pokemon got stolen by Team Rocket RuleFive");
                player1.addPokemonAtBeginning(pokemon1);
                player2.addPokemonAtBeginning(pokemon2);
                player1.heal();
                player2.heal();
                return null;
            }
        } while (continueBattle());
        player1.addPokemonAtBeginning(pokemon1);
        player2.addPokemonAtBeginning(pokemon2);
        if (pokemon1.isFainted()) {
            if (pokemon2.isFainted()) {
                b.sendMessage(channel, player1.getTrainerName() + " is out of usable Pokemon! " + player2 + " is out of usable Pokemon as well! The result is a draw! RuleFive");
                player1.heal();
                player2.heal();
                return null;
            }
            b.sendMessage(channel, player1.getTrainerName() + " is out of usable Pokemon! " + player2 + " wins! PogChamp");
            player1.heal();
            player2.heal();
            return player2;
        }
        if (pokemon2.isFainted()) {
            if (pokemon1.isFainted()) {
                b.sendMessage(channel, player2.getTrainerName() + " is out of usable Pokemon! " + player1 + " is out of usable Pokemon as well! The result is a draw! RuleFive");
                player1.heal();
                player2.heal();
                return null;
            }
            b.sendMessage(channel, player2.getTrainerName() + " is out of usable Pokemon! " + player1 + " wins! PogChamp");
            player1.heal();
            player2.heal();
            return player1;
        }
        System.err.println("nulled");
        player1.heal();
        player2.heal();
        return winner;
    }

    /**
     * This is for Switching Pokemon after the player's Pokemon has fainted.
     *
     * @param player Player being forced to Switch
     * @param playerPokemon Fainted Pokemon
     * @param channel Channel to send Message to
     */
    public void switchPokemon(Trainer player, Pokemon playerPokemon, String channel) {
        p1msg = new LinkedBlockingQueue<>();
        p2msg = new LinkedBlockingQueue<>();
        b.sendMessage(channel, playerPokemon.getName() + " fainted! What Pokemon will " + player.getTrainerName() + " switch to?");
        LinkedBlockingQueue<String> feed = (player.getTrainerName().equalsIgnoreCase(player1.getTrainerName()) ? p1msg : p2msg);
        if (Trainer.isUserBot(player.getTrainerName()) || !player.isAI()) {
            b.sendWhisper(player1.getTrainerName(), "Type !list to get a list of your Pokemon. Type !switch<number> to switch to that Pokemon (for example, the if you want to switch to the first Pokemon, type !switch0 )");
            try {
                int switchTo = -1;
                String temp = "";
                while (!temp.startsWith("!switch")) {
                    temp = feed.poll(60, TimeUnit.SECONDS);
                    if (temp == null) {
                        if (player.getTrainerName().equalsIgnoreCase(player1.getTrainerName())) {
                            b.sendMessage(channel, player1.getTrainerName() + " did not select a new Pokemon in time. " + player2.getTrainerName() + " wins!");
                        } else {
                            b.sendMessage(channel, player2.getTrainerName() + " did not select a new Pokemon in time. " + player1.getTrainerName() + " wins!");
                        }
                        endBattle = true;
                        player1.addPokemonAtBeginning(pokemon1);
                        player2.addPokemonAtBeginning(pokemon2);
                        player1.heal();
                        player2.heal();
                        return;
                    }
                    if (temp.startsWith("!switch") && temp.length() >= 8) {
                        if (!Character.isDigit(temp.charAt(7))) {
                            temp = "";
                            b.sendMessage(channel, "/w " + player.getTrainerName() + " Invalid Pokemon Position FUNgineer");
                            continue;
                        }
                        switchTo = Integer.parseInt(temp.split("!switch", 2)[1].split(" ", 2)[0]);
                        if (switchTo >= 2 || switchTo < 0) {
                            temp = "";
                            b.sendMessage(channel, "/w " + player.getTrainerName() + " Invalid Pokemon Position FUNgineer");
                            continue;
                        }
                        if (player.getPokemon(switchTo).isFainted()) {
                            temp = "";
                            b.sendMessage(channel, "/w " + player.getTrainerName() + " You cannot switch to a fainted Pokemon FUNgineer");
                            continue;
                        }
                        if (player.getTrainerName().equalsIgnoreCase(player1.getTrainerName())) {
                            p1msg = new LinkedBlockingQueue<>();
                            player1.getPokemon().add(this.pokemon1);
                            this.pokemon1 = player1.getPokemon(switchTo);
                            player1.removePokemon(switchTo);
                            b.sendMessage(channel, player + " sends out " + this.pokemon1 + " (Level " + this.pokemon1.getLevel() + ")!");
                            break;
                        } else {
                            p2msg = new LinkedBlockingQueue<>();
                            player2.getPokemon().add(this.pokemon2);
                            this.pokemon2 = player2.getPokemon(switchTo);
                            player2.removePokemon(switchTo);
                            b.sendMessage(channel, player + " sends out " + this.pokemon2 + " (Level " + this.pokemon2.getLevel() + ")!");
                            break;
                        }
                    }
                }
            } catch (Exception ex) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                ex.printStackTrace(pw);
                b.music.sendMessage(b.music.getChannel(), b.music.CHEF.mention() + " ```An error occurred while attempting to switch Pokemon\n" + sw.toString() + "\nPlayer=" + player.getTrainerName() + ",Pokemon=" + playerPokemon.getName() + "```");
            }
        } else if (player.getTrainerName().equalsIgnoreCase(player1.getTrainerName())) {
            p1msg = new LinkedBlockingQueue<>();
            player1.getPokemon().add(this.pokemon1);
            this.pokemon1 = player1.getPokemon(0);
            player1.removePokemon(0);
            b.sendMessage(channel, player + " sends out " + this.pokemon1 + " (Level " + this.pokemon1.getLevel() + ")!");
        } else {
            p2msg = new LinkedBlockingQueue<>();
            player2.getPokemon().add(this.pokemon2);
            this.pokemon2 = player2.getPokemon(0);
            player2.removePokemon(0);
            b.sendMessage(channel, player + " sends out " + this.pokemon2 + " (Level " + this.pokemon2.getLevel() + ")!");
        }
    }

    public boolean mainBattle(Pokemon p1, Pokemon p2, String move1, String move2, String channel) {
        doMove(channel, move1, p1, p2);
        if (!p2.isFainted()) {
            if (p2.isFlinched()) {
                p2.setFlinch(false);
                b.sendMessage(channel, p2.getName() + " flinched!");
                return false;
            }
            doMove(channel, move2, p2, p1);
            return false;
        } else {
            return true;
        }
    }

    private void doMove(String channel, String move, Pokemon user, Pokemon opponent) {
        int m = Integer.parseInt(move);
        b.sendMessage(channel, user.attack(opponent, user.getMoveByNumber(m), this).replace("\n", " "));
    }

    public boolean continueBattle() {
        if (pokemon1.isFainted()) {
            if (player1.getNumberOfPokemonRemaining() == 0) {
                return false;
            }
        }
        if (pokemon2.isFainted()) {
            if (player2.getNumberOfPokemonRemaining() == 0) {
                return false;
            }
        }
        return true;
    }

    public static File determineMusic(PWTBattle battle) {
        battle.b.music.clear();
        if (battle.getRound() == PWTRound.FINALS && battle.getType() != PWTType.CHAMPIONS) {
            return new File(BattleBot.ROOT_PATH + "pwt\\pwt-final.mp3");
        }
        File toReturn = new File(BattleBot.ROOT_PATH + "pwt\\pwt-trainer.mp3");
        Trainer ai = null;
        Trainer p1 = battle.player1;
        Trainer p2 = battle.player2;
        if (p1.getTrnClass().contains("Champion")) {
            ai = p1;
        } else if (p2.getTrnClass().contains("Champion")) {
            ai = p2;
        } else if (p1.getTrnClass().contains("Gym Leader")) {
            ai = p1;
        } else if (p2.getTrnClass().contains("Gym Leader")) {
            ai = p2;
        } else {
            ai = new SecureRandom().nextBoolean() ? p1 : p2;
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

    /**
     * Selects the AI Move for PWT Battles. Easily expandable in the future.
     *
     * @param user AI User Pokemon
     * @param opponent Opponent Pokemon
     * @return String containing which move to use
     */
    public static String PWTAIMove(Pokemon user, Pokemon opponent) {
        Move mostPowerful = Move.selectBestMove(user, opponent);
        if (mostPowerful == null) {
            System.err.println("Move is null!!");
            return "!move" + new SecureRandom().nextInt(3) + 1;
        }
        if (mostPowerful.equals(user.getMove1())) {
            return "!move1";
        } else if (mostPowerful.equals(user.getMove2())) {
            return "!move2";
        } else if (mostPowerful.equals(user.getMove3())) {
            return "!move3";
        } else {
            return "!move4";
        }
    }
}
