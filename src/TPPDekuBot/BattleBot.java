package TPPDekuBot;

import PircBot.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Michael
 */
public class BattleBot extends PircBot {
//valid starters = Bulbasaur, Charmander, Squirtle, Chikorita, Cyndaquil, Totodile, Treecko, Torchic, Mudkip, Turtwig, Chimchar, Piplup, Snivy, Tepig, Oshawott, Chespin, Fennekin, Froakie

    public boolean inPokemonBattle = false;
    public String personInBattle = "";
    public String lastMessage = "";
    public boolean inMultiBattle = false;
    public boolean waitingPlayer = false;
    public boolean inSafariBattle = false;
    public String waitingOn = "";
    public static LinkedBlockingQueue<String> pokemonMessages = new LinkedBlockingQueue<>();
    public final LinkedBlockingQueue<String> player = new LinkedBlockingQueue<>();
    public final LinkedBlockingQueue<String> p1 = new LinkedBlockingQueue<>();
    public final LinkedBlockingQueue<String> p2 = new LinkedBlockingQueue<>();
    public MultiplayerBattle mpB = null;
    public SafariBattle sB = null;
    public static String BASE_PATH = "";
    public String oAuth;

    public BattleBot(String BASE_PATH, String oAuth) {
        this.setName("Wow_BattleBot_OneHand");
        this.setMessageDelay(2500);
        inPokemonBattle = false;
        personInBattle = "";
        lastMessage = "";
        this.BASE_PATH = BASE_PATH;
        this.oAuth = oAuth;
    }

    public String longMessage(String message) {
        if (message.equals(lastMessage)) {
            //message += " &#32";
            message += '\u0012';
        }
        lastMessage = message;
        return message;
    }

    @Override
    public void onDisconnect() {
        System.err.println("Trying to reconnect...");
        try {
            this.reconnect();
            this.joinChannel("#_keredau_1423645868201");
        } catch (Exception ex) {
            try {
                Thread.sleep(10000);
            } catch (Exception ex2) {
            }
            onDisconnect();
        }
    }

//    @Override
//    public void onNotice(String sourceNick, String sourceLogin, String sourceHostname, String target, String notice) {
//        if (notice.contains("That user's settings prevent them from receiving this whisper.")) {
//            
//        }
//    }
    @Override
    public void onWhisper(User sender, String target, String message) {
        if ((sender.getNick().equalsIgnoreCase("the_chef1337") || sender.getNick().equalsIgnoreCase("wow_deku_onehand")) && message.toLowerCase().startsWith("!sendrawline ")) {
            String line = message.split(" ", 2)[1];
            this.sendRawLine(line);
            return;
        }
        if ((message.toLowerCase().startsWith("!accept")) && waitingPlayer && sender.getNick().equalsIgnoreCase(waitingOn)) {
            try {
                player.put(sender.getNick());
            } catch (Exception ex) {
            }
        }
        if (inMultiBattle && mpB != null) {
            String channel = "#_keredau_1423645868201";
            if (message.toLowerCase().startsWith("!run") || (message.toLowerCase().startsWith("!switch") && message.length() >= 8 && Character.isDigit(message.charAt(7))) || Move.isValidMove(message)) {
                if (sender.getNick().equalsIgnoreCase(mpB.getPlayer1())) {
                    try {
                        mpB.p1msg.put(message);
                    } catch (Exception ex) {
                    }
                }
                if (sender.getNick().equalsIgnoreCase(mpB.getPlayer2())) {
                    try {
                        mpB.p2msg.put(message);
                    } catch (Exception ex) {
                    }
                }
            }
            if (message.toLowerCase().startsWith("!list")) {
                if (sender.getNick().equalsIgnoreCase(mpB.getPlayer1())) {
                    try {
                        String pokemon = mpB.player1.getPokemonList();
                        this.sendMessage(channel, "/w " + sender.getNick() + " Your pokemon are: " + pokemon);
                    } catch (Exception ex) {
                        this.sendMessage(channel, "/w " + sender.getNick() + " You have no other Pokemon in your party!");
                    }
                } else if (sender.getNick().equalsIgnoreCase(mpB.getPlayer2())) {
                    try {
                        String pokemon = mpB.player2.getPokemonList();
                        this.sendMessage(channel, "/w " + sender.getNick() + " Your pokemon are: " + pokemon);
                    } catch (Exception ex) {
                        this.sendMessage(channel, "/w " + sender.getNick() + " You have no other Pokemon in your party!");
                    }
                }
                return;
            }
            if (message.toLowerCase().startsWith("!check") && message.length() >= 7 && Character.isDigit(message.charAt(6))) {
                int check = Integer.parseInt(message.charAt(6) + "");
                if (sender.getNick().equalsIgnoreCase(mpB.getPlayer1())) {
                    Pokemon p = mpB.player1.getPokemon(check);
                    this.sendMessage(channel, "/w " + sender.getNick() + " Status of " + p.getName() + ": " + p.getStat(Stats.HP) + " out of " + p.getMaxHP() + "hp left. Has these moves: " + p.getMove1().getName() + ", " + p.getMove2().getName() + ", " + p.getMove3().getName() + ", " + p.getMove4().getName());
                } else if (sender.getNick().equalsIgnoreCase(mpB.getPlayer2())) {
                    Pokemon p = mpB.player2.getPokemon(check);
                    this.sendMessage(channel, "/w " + sender.getNick() + " Status of " + p.getName() + ": " + p.getStat(Stats.HP) + " out of " + p.getMaxHP() + "hp left. Has these moves: " + p.getMove1().getName() + ", " + p.getMove2().getName() + ", " + p.getMove3().getName() + ", " + p.getMove4().getName());
                }
            }
            if (message.toLowerCase().startsWith("!help") && inMultiBattle && (sender.getNick().equalsIgnoreCase(mpB.getPlayer1()) || sender.getNick().equalsIgnoreCase(mpB.getPlayer2()))) {
                this.sendMessage(channel, "/w " + sender.getNick() + " Type !list to see a list of your Pokemon. Type !checkx where x is the number of the Pokemon from !list to see it's moves. Type !switchx where x is number of the Pokemon from !list to switch to a Pokemon.");
            }
        }
    }

    @Override
    public void onAction(User sender, Channel channel, String action) {
        onMessage(channel, sender, action);
    }

    @Override
    public void onMessage(Channel channel, User sender, String message) {
        if (sender.getNick().equalsIgnoreCase("the_chef1337") && message.toLowerCase().startsWith("!sendrawline ")) {
            String line = message.split(" ", 2)[1];
            this.sendRawLine(line);
            return;
        }
        //banlist goes here for simplicity
        if (sender.getNick().equalsIgnoreCase("trainertimmy") || sender.getNick().equalsIgnoreCase("trainertimmybot") || sender.getNick().equalsIgnoreCase("pikabowser2082") || sender.getNick().equalsIgnoreCase("wallbot303") || sender.getNick().equalsIgnoreCase("frunky5")) {
            return;
        }
        if (sender.getNick().equalsIgnoreCase("minhs2") || sender.getNick().equalsIgnoreCase("minhs3")) {
            return;
        }
        //end banlist
        //System.out.println(DekuBot.getDateTime() + " " + sender + ": " + message);
        while (Character.isWhitespace(message.charAt(0)) && message.length() > 2) {
            message = message.substring(1);
        }
        if (message.length() < 2) {
            return;
        }
        if (sender.getNick().equalsIgnoreCase("Minhs2") && message.toLowerCase().startsWith("!battle bigbrother")) {
            this.sendMessage(channel.getChannelName(), longMessage("FUNgineer"));
            return;
        }
        if ((message.toLowerCase().startsWith("!accept")) && waitingPlayer && sender.getNick().equalsIgnoreCase(waitingOn)) {
            try {
                player.put(sender.getNick());
            } catch (Exception ex) {
            }
        }
        if ((message.toLowerCase().startsWith("!changeclass ") || message.toLowerCase().startsWith("!selectclass ")) && !inMultiBattle) {
            String newClass = message.split(" ", 2)[1];
            if (newClass.length() > 19) {
                newClass = newClass.substring(0, 19);
            }
            if (newClass.isEmpty()) {
                this.sendMessage(channel.getChannelName(), "@" + sender.getNick() + " Invalid Trainer Class FUNgineer");
                return;
            }
            while (Character.isWhitespace(newClass.charAt(0))) {
                newClass = newClass.substring(1);
            }
            while (newClass.contains("  ")) {
                newClass = newClass.replace("  ", " ");
                newClass = newClass.trim();
            }
            if (newClass.toLowerCase().contains("gym leader") || newClass.toLowerCase().contains("leader") || newClass.toLowerCase().contains("champion") || newClass.toLowerCase().contains("elite four") || (newClass.toLowerCase().charAt(0) == '/' || newClass.toLowerCase().charAt(0) == '.' || !Character.isLetter(newClass.toLowerCase().charAt(0))) || containsBannedChar(newClass)) {
                this.sendMessage(channel.getChannelName(), "@" + sender.getNick() + " Invalid Trainer Class FUNgineer");
                return;
            }
            //if (Trainer.isValidTrainerClass(newClass)) {
            HashMap<String, String> classes = new HashMap<>();
            try (FileInputStream f = new FileInputStream(BASE_PATH + "/trainerclasses.wdu"); ObjectInputStream o = new ObjectInputStream(f)) {
                classes = (HashMap<String, String>) o.readObject();
            } catch (Exception ex) {
                System.err.println("[ERROR] Error reading classes file! " + ex);
                return;
            }
            classes.put(sender.getNick().toLowerCase(), newClass);
            try (FileOutputStream f = new FileOutputStream(BASE_PATH + "/trainerclasses.wdu"); ObjectOutputStream o = new ObjectOutputStream(f)) {
                o.writeObject(classes);
            } catch (Exception ex) {
                System.err.println("[ERROR] Error writing new classes file! " + ex);
                return;
            }
            this.sendMessage(channel.getChannelName(), "@" + sender.getNick() + " updated your Trainer Class to " + newClass + "!");
            //} else {
            // this.sendMessage(channel.getChannelName(), "@" + sender + " Invalid Trainer Class. FUNgineer For a list of valid classes, go here: http://pastebin.com/raw.php?i=rhA55Dd0");
            //}
            return;
        }
        if (inMultiBattle && mpB != null) {
            if (message.toLowerCase().startsWith("!run") || (message.toLowerCase().startsWith("!switch") && message.length() >= 8 && Character.isDigit(message.charAt(7))) || Move.isValidMove(message)) {
                if (sender.getNick().equalsIgnoreCase(mpB.getPlayer1())) {
                    try {
                        mpB.p1msg.put(message);
                    } catch (Exception ex) {
                    }
                }
                if (sender.getNick().equalsIgnoreCase(mpB.getPlayer2())) {
                    try {
                        mpB.p2msg.put(message);
                    } catch (Exception ex) {
                    }
                }
            }
            if (message.toLowerCase().startsWith("!list")) {
                if (sender.getNick().equalsIgnoreCase(mpB.getPlayer1())) {
                    try {
                        String pokemon = mpB.player1.getPokemonList();
                        this.sendMessage(channel.getChannelName(), "/w " + sender.getNick() + " Your pokemon are: " + pokemon);
                    } catch (Exception ex) {
                        this.sendMessage(channel.getChannelName(), "/w " + sender.getNick() + " You have no other Pokemon in your party!");
                    }
                } else if (sender.getNick().equalsIgnoreCase(mpB.getPlayer2())) {
                    try {
                        String pokemon = mpB.player2.getPokemonList();
                        this.sendMessage(channel.getChannelName(), "/w " + sender.getNick() + " Your pokemon are: " + pokemon);
                    } catch (Exception ex) {
                        this.sendMessage(channel.getChannelName(), "/w " + sender.getNick() + " You have no other Pokemon in your party!");
                    }
                }
                return;
            }
            if (message.toLowerCase().startsWith("!check") && message.length() >= 7 && Character.isDigit(message.charAt(6))) {
                int check = Integer.parseInt(message.charAt(6) + "");
                if (sender.getNick().equalsIgnoreCase(mpB.getPlayer1())) {
                    Pokemon p = mpB.player1.getPokemon(check);
                    this.sendMessage(channel.getChannelName(), "/w " + sender.getNick() + " Status of " + p.getName() + ": " + p.getStat(Stats.HP) + " out of " + p.getMaxHP() + "hp left. Has these moves: " + p.getMove1().getName() + ", " + p.getMove2().getName() + ", " + p.getMove3().getName() + ", " + p.getMove4().getName());
                } else if (sender.getNick().equalsIgnoreCase(mpB.getPlayer2())) {
                    Pokemon p = mpB.player2.getPokemon(check);
                    this.sendMessage(channel.getChannelName(), "/w " + sender.getNick() + " Status of " + p.getName() + ": " + p.getStat(Stats.HP) + " out of " + p.getMaxHP() + "hp left. Has these moves: " + p.getMove1().getName() + ", " + p.getMove2().getName() + ", " + p.getMove3().getName() + ", " + p.getMove4().getName());
                }
            }
            if (message.toLowerCase().startsWith("!help") && inMultiBattle && (sender.getNick().equalsIgnoreCase(mpB.getPlayer1()) || sender.getNick().equalsIgnoreCase(mpB.getPlayer2()))) {
                this.sendMessage(channel.getChannelName(), "/w " + sender.getNick() + " Type !list to see a list of your Pokemon. Type !checkx where x is the number of the Pokemon from !list to see it's moves. Type !switchx where x is number of the Pokemon from !list to switch to a Pokemon.");
            }
        }
        if (inSafariBattle && sB != null) {
            if (sender.getNick().equalsIgnoreCase(sB.user.getTrainerName())) {
                if (message.toLowerCase().startsWith("!rock") || message.toLowerCase().startsWith("!bait") || message.toLowerCase().startsWith("!ball") || message.toLowerCase().startsWith("!run")) {
                    if (this.getOutgoingQueueSize() == 0) {
                        sB.msg.add(message.split(" ", 2)[0].toLowerCase());
                    }
                }
            }
        }
        if (!inMultiBattle && !waitingPlayer && !inPokemonBattle && !inSafariBattle) {
            if (message.startsWith("!safari")) {
                inSafariBattle = true;
                Thread t = new Thread(() -> {
                    int level = new SecureRandom().nextInt(100 - 20 + 1) + 20;
                    int id = new SecureRandom().nextInt(721 - 1 + 1) + 1;
                    System.err.println("Attempting Pokemon ID " + id + " level " + level);
                    sB = new SafariBattle(sender.getNick(), new Pokemon(id, level));
                    sB.doBattle(this, channel.getChannelName());
                    System.err.println("Now out of Safari Battle");
                    sB = null;
                    inSafariBattle = false;
                });
                t.start();
            }
        }
        if (message.toLowerCase().startsWith("!help") && !inMultiBattle) {
            this.sendMessage(channel.getChannelName(), "http://pastebin.com/raw.php?i=YVsxvPUK");
        }
        if (message.toLowerCase().startsWith("!randbat @") || message.toLowerCase().startsWith("!randombattle @") && !inMultiBattle && !inPokemonBattle && !inSafariBattle && !waitingPlayer) {
            //if ((message.toLowerCase().startsWith("!challenge @") || message.toLowerCase().startsWith("!multibattle @")) && !inMultiBattle && !inPokemonBattle && !inSafariBattle) {
            final String messageFinal = message;
            Thread t = new Thread(() -> {
                try {
                    String target = messageFinal.split("@", 2)[1].split(" ", 2)[0];
                    int pkmAmt = 1;
                    try {
                        pkmAmt = Integer.parseInt(messageFinal.split("@", 2)[1].split(" ", 2)[1].split(" ", 2)[0]);
                    } catch (Exception ex2) {
                        pkmAmt = 1;
                    }
                    if (pkmAmt < 1) {
                        pkmAmt = 1;
                    }
                    if (pkmAmt > 6) {
                        pkmAmt = 6;
                    }
                    if (target.equalsIgnoreCase(sender.getNick())) {
                        this.sendMessage(channel.getChannelName(), "You cannot challenge yourself FUNgineer");
                        return;
                    }
                    if (target.equalsIgnoreCase("frunky5") || target.equalsIgnoreCase("wallbot303")) {

                    } else if (target.equalsIgnoreCase("wow_deku_onehand") || target.equalsIgnoreCase("wow_battlebot_onehand") || User.isBot(target) || target.equalsIgnoreCase("killermapper")) {
                        this.sendMessage(channel.getChannelName(), "FUNgineer");
                        return;
                    }
                    if (!waitingPlayer) {
                        waitingPlayer = true;
                        waitingOn = target;
                        this.sendMessage(channel.getChannelName(), "Challenging " + target + "...");
                        int level = new SecureRandom().nextInt(100 - 20 + 1) + 20;
                        while (level < 20) {
                            level = new SecureRandom().nextInt(100 - 20 + 1) + 20;
                        }
                        boolean isHere = false;
                        for (User el : this.getUsers(channel.getChannelName())) {
                            if (target.equalsIgnoreCase(el.getNick())) {
                                isHere = true;
                                break;
                            }
                        }
                        if (!isHere) {
                            BattleBot.sendAnInvite(target, "_keredau_1423645868201", oAuth);
                        }
                        this.sendWhisper(channel.getChannelName(), target, "You have been challenged to a Pokemon Battle by " + sender.getNick() + "! To accept, go to the Battle Dungeon and type !accept. You have one minute.");
                        String player2 = player.poll(60, TimeUnit.SECONDS);
                        if (player2 == null) {
                            this.sendMessage(channel.getChannelName(), target + " did not respond to the challenge BibleThump");
                            inMultiBattle = false;
                            inPokemonBattle = false;
                            waitingPlayer = false;
                            waitingOn = "";
                            return;
                        }
                        inMultiBattle = true;
                        inPokemonBattle = true;
                        waitingPlayer = false;
                        waitingOn = "";
                        this.sendMessage(channel.getChannelName(), "Generating Pokemon, give me a minute...");
                        System.err.println("Going into Multiplayer Battle");
                        mpB = new MultiplayerBattle(sender.getNick(), target, level, pkmAmt);
                        mpB.doBattle(this, channel.getChannelName());
                        inMultiBattle = false;
                        inPokemonBattle = false;
                        mpB = null;
                        System.err.println("Now out of Multiplayer Battle");
                    }
                } catch (Exception ex) {
                    inMultiBattle = false;
                    inPokemonBattle = false;
                    waitingPlayer = false;
                    mpB = null;
                }
            });
            t.start();

        }
        if (message.toLowerCase().startsWith("!battle") && !inMultiBattle && !waitingPlayer && !inSafariBattle && !inPokemonBattle) {
            boolean bigbrother = false, fromChef = false;
            if (message.contains("BigBrother") || sender.getNick().equalsIgnoreCase("dewgong98") || sender.getNick().equalsIgnoreCase("mad_king98") || sender.getNick().equalsIgnoreCase("Starmiewaifu")) {
                bigbrother = true;
                if (sender.getNick().equalsIgnoreCase("the_chef1337")) {
                    fromChef = true;
                }
            }
            final boolean bbrother = bigbrother;
            final boolean fChef = fromChef;
            if (sender.getNick().equalsIgnoreCase("twitchplaysleaderboard")) {
                return;
            } else {
                final String senderFinal = sender.getNick();
                if (!inPokemonBattle && !inMultiBattle && !inSafariBattle && !waitingPlayer) {
                    Thread t = new Thread(() -> {
                        try {
                            inPokemonBattle = true;
                            pokemonMessages = new LinkedBlockingQueue<>();
                            personInBattle = senderFinal;
                            System.err.println("Going into Pokemon Battle");
                            PokemonBattle a = new PokemonBattle(this, channel.getChannelName(), bbrother, fChef, sender.getNick());
                            System.err.println("Now out of Pokemon Battle");
                            inPokemonBattle = false;
                            pokemonMessages = new LinkedBlockingQueue<>();
                            personInBattle = "";
                        } catch (Exception ex) {
                            inPokemonBattle = false;
                            personInBattle = "";
                            pokemonMessages = new LinkedBlockingQueue<>();
                            this.sendMessage(channel.getChannelName(), "Something fucked up OneHand this battle is now over both Pokemon exploded violently KAPOW");
                            System.err.println("[POKEMON] Uh oh " + ex);
                            ex.printStackTrace();
                        }
                    });
                    t.start();
                }
            }
        }
        if (message.toLowerCase().startsWith("!run")) {
            if (!channel.getChannelName().equals("#_keredau_1423645868201")) {
                return;
            }
            if (inPokemonBattle) {
                if (sender.getNick().equalsIgnoreCase(personInBattle)) {
//                    if (DekuBot.containsOtherChar(message)) {
//                        this.sendMessage(channel.getChannelName(), sender + "... TriHard");
//                        return;
//                    }
                    personInBattle = "";
                    inPokemonBattle = false;
                    pokemonMessages.add("run");
                }
            }
        }

        if (message.toLowerCase().startsWith("!move1") || message.toLowerCase().startsWith("!move2") || message.toLowerCase().startsWith("!move3") || message.toLowerCase().startsWith("!move4")) {
            if (sender.getNick().equalsIgnoreCase("wow_deku_onehand")) {
                return;
            }
            if (!channel.getChannelName().equals("#_keredau_1423645868201")) {
                return;
            }
            if (inPokemonBattle) {
                if (sender.getNick().equalsIgnoreCase(personInBattle)) {
                    pokemonMessages.add("" + message.charAt(5));
                }
            }
        }
    }

    public static void sendAnInvite(String name, String channel, String oAuth) {
        try {
            ArrayList<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("irc_channel", channel));
            params.add(new BasicNameValuePair("username", name));
            params.add(new BasicNameValuePair("oauth_token", oAuth));
            ArrayList<NameValuePair> header = new ArrayList<>();
            header.add(new BasicNameValuePair("Content-Type", "application/x-www-form-urlencoded"));
            header.add(new BasicNameValuePair("Authorization", "OAuth " + oAuth));
            System.err.println("[HTTPPOST] " + BattleBot.sendPost("https://chatdepot.twitch.tv/room_memberships", params, header));
        } catch (Exception ex) {
            ex.printStackTrace();
        };
    }

    public static boolean isPureAscii(String v) {
        byte bytearray[] = v.getBytes();
        CharsetDecoder d = Charset.forName("US-ASCII").newDecoder();
        try {
            CharBuffer r = d.decode(ByteBuffer.wrap(bytearray));
            r.toString();
        } catch (CharacterCodingException e) {
            return false;
        }
        return true;
    }

    public static boolean containsBannedChar(String input) {
        char[] banned = {'\u0001', '\u0012', '\u0010'};
        for (char el : input.toCharArray()) {
            for (char el2 : banned) {
                if (el == el2) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String sendPost(String url, List<NameValuePair> params, List<NameValuePair> header) throws IOException {
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(url);
        httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        if (header != null && !header.isEmpty()) {
            for (NameValuePair el : header) {
                httppost.addHeader(el.getName(), el.getValue());
            }
        }
        HttpResponse response = httpclient.execute(httppost);
        return response.toString();
    }

}
