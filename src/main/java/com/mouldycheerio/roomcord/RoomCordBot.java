package com.mouldycheerio.roomcord;

import java.awt.Color;
import java.io.File;
import java.util.Random;
import java.util.function.Consumer;

import javax.security.auth.login.LoginException;

import org.json.JSONException;
import org.json.JSONObject;

import com.mouldycheerio.roomcord.commands.CommandController;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

public class RoomCordBot {
    private JSONObject config;
    private File datadir;
    private long creation;
    private Random random;
    private String prefix;
    private net.dv8tion.jda.api.JDA client;
    public static final Color COLOUR = new Color(54, 57, 63);
    private CommandController commandController;
    private RoomsController roomsController;


    public RoomCordBot(JSONObject config) throws InterruptedException, LoginException, JSONException {
        RestAction.setPassContext(true);


        this.setConfig(config);
        datadir = new File(config.getString("dataDir"));
        creation = System.currentTimeMillis();

        this.prefix = config.getString("prefix");
        random = new Random();

        System.out.println("Logging into discord...");

        setJDA(new JDABuilder(config.getString("token")).build());

        getJDA().awaitReady();
        System.out.println("Logged in as " + getJDA().getSelfUser().getId() + " " + getJDA().getSelfUser().getName());
        getJDA().getSelfUser().getManager().setName(config.getString("name")).queue();

        commandController = new CommandController(this, getPrefix());

        client.addEventListener(commandController);
        roomsController = new RoomsController(this);


    }


    public JDA getJDA() {
        return client;
    }

    public void setJDA(JDA client) {
        this.client = client;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public MessageEmbed createMessageEmbed(Message e, String title, String content, String imageUrl, Color colour) {
        EmbedBuilder b = new EmbedBuilder();
        b.setColor(colour);
        if (imageUrl != "") {
            b.setImage(imageUrl);
        }
        b.setTitle(title);
        b.setDescription(content);
        // b.setFooter(e.getAuthor().getName() + "#" + e.getAuthor().getDiscriminator(), e.getAuthor().getAvatarUrl());
        MessageEmbed build = b.build();
        return build;
    }

    public void sendMessage(MessageReceivedEvent e, String title, String content, String imageUrl, Color c) {
        MessageEmbed build = createMessageEmbed(e.getMessage(), title, content, imageUrl, c);
        MessageAction ma = e.getChannel().sendMessage(build);
        ma.queue();
    }

    public void sendMessage(MessageReceivedEvent e, String title, String content, String imageUrl) {
        sendMessage(e, title, content, imageUrl, COLOUR);
    }

    public void sendMessage(MessageReceivedEvent e, String title, String content) {
        sendMessage(e, title, content, "", COLOUR);
    }

    public void sendMessage(MessageReceivedEvent e, String content) {
        sendMessage(e, content, "", "", COLOUR);
    }

    public void sendMessage(MessageReceivedEvent e, String title, String content, Color c) {
        sendMessage(e, title, content, "", c);
    }

    public void sendMessage(MessageReceivedEvent e, String content, Color c) {
        sendMessage(e, getConfig().getString("name"), content, "", c);
    }

    public void sendLink(MessageReceivedEvent e, String link, String text) {

        EmbedBuilder b = new EmbedBuilder();
        b.setDescription("[" + text + "](" + link + ")");
        b.setColor(COLOUR);
        b.setFooter(e.getAuthor().getName() + "#" + e.getAuthor().getDiscriminator(), e.getAuthor().getAvatarUrl());
        MessageEmbed build = b.build();
        MessageAction ma = e.getChannel().sendMessage(build);
        ma.queue();
    }

    public void editMessage(Message e, String title, String content, Consumer<? super Message> callback) {

        MessageEmbed build = createMessageEmbed(e, title, content, "", COLOUR);
        e.editMessage(build).queue(callback);
    }

    public void sendMessageQueue(MessageReceivedEvent e, String title, String content, String imageURL, Consumer<? super Message> callback) {
        MessageEmbed embed = createMessageEmbed(e.getMessage(), title, content, imageURL, COLOUR);
        e.getChannel().sendMessage(embed).queue(callback);
    }

    public Message sendMessageBlock(MessageReceivedEvent e, String title, String content) {
        MessageEmbed embed = createMessageEmbed(e.getMessage(), title, content, "", COLOUR);
        Message complete = e.getChannel().sendMessage(embed).complete();
        return complete;
    }

    public void sendMessageQueue(MessageReceivedEvent e, String title, String content, Consumer<? super Message> callback) {
        sendMessageQueue(e, title, content, "", callback);
    }

    public void sendMessageQueue(MessageReceivedEvent e, String content, Consumer<? super Message> callback) {
        sendMessageQueue(e, getConfig().getString("name"), content, "", callback);
    }

    public void sendNoPerms(MessageReceivedEvent e) {
        sendMessage(e, "You are not permitted to do this command!", "You are missing the permission: **manage server**");
    }

    public void sendPlainMessage(MessageReceivedEvent e, String content) {
        e.getChannel().sendMessage(content).queue();
    }

    public File getDatadir() {
        return datadir;
    }

    public void setDatadir(File datadir) {
        this.datadir = datadir;
    }

    public CommandController getCommandController() {
        return commandController;
    }

    public void setCommandController(CommandController commandController) {
        this.commandController = commandController;
    }

    public JSONObject getConfig() {
        return config;
    }

    public void setConfig(JSONObject config) {
        this.config = config;
    }
}
