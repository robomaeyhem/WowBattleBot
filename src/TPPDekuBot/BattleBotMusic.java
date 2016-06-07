package TPPDekuBot;

import java.io.File;
import sx.blah.discord.api.*;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;

public class BattleBotMusic {

    private IDiscordClient client;
    private boolean isVoiceReady = false;
    private IVoiceChannel voice;
    private IChannel channel;
    public IUser CHEF;
    private String nowPlaying = "";
    private BattleBot b;

    public BattleBotMusic(String email, String password, BattleBot b) throws DiscordException {
        ClientBuilder clientBuilder = new ClientBuilder();
        clientBuilder.withLogin(email, password);
        this.client = clientBuilder.login();
        this.b = b;
        EventDispatcher dispatcher = client.getDispatcher();
        dispatcher.registerListener(this);
    }

    @EventSubscriber
    public void onReady(ReadyEvent event) {
        IGuild guild = client.getGuildByID("162752501131640832");
        channel = client.getChannelByID("162752501131640832");
        voice = guild.getVoiceChannelByID("162752501131640833");
        voice.join();
        try {
            voice.getAudioChannel().setVolume(0.25F);
        } catch (Exception ex) {
        }
        isVoiceReady = true;
        CHEF = client.getGuildByID("162752501131640832").getUserByID("94696943652966400");
    }

    public void play(File file) {
        if (file == null) {
            System.err.println("File null");
            return;
        }
        if (file.exists()) {
            try {
                if (b.playingVictoryPWT) {
                    b.playingVictoryPWT = false;
                    voice.getAudioChannel().clearQueue();
                }
                voice.getAudioChannel().queueFile(file);
                nowPlaying = file.getName();
            } catch (Exception ex) {

            }
        } else {
            System.err.println("File not found: " + file.getAbsolutePath());
        }
    }

    public void skip() {
        try {
            voice.getAudioChannel().skip();
        } catch (Exception ex) {

        }
    }

    public void clear() {
        try {
            voice.getAudioChannel().clearQueue();
            nowPlaying = "";
        } catch (Exception ex) {
        }
    }

    public String getNowPlaying() {
        return nowPlaying;
    }

    public void sendMessage(IChannel channel, String message) {
        try {
            new MessageBuilder(client).withChannel(channel).withContent(message).build();
            System.out.println(System.currentTimeMillis() + " >>>DISCORD to " + channel.getName() + " :" + message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public IChannel getChannel() {
        return channel;
    }
}
