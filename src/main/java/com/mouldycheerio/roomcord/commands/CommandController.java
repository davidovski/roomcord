package com.mouldycheerio.roomcord.commands;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.mouldycheerio.roomcord.PeelingUtils;
import com.mouldycheerio.roomcord.RoomCordBot;

import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandController extends ListenerAdapter {
    private RoomCordBot bot;
    private String prefix;
    private Map<String, Command> commands;
    private String tagPrefix;
    private long currentTimeMessage;
    private long currentTimeAPI;
    private long currentTimeDiscord;

    private File saveFile;

    public CommandController(RoomCordBot bot, String prefix) {
        this.bot = bot;
        this.prefix = prefix;
        tagPrefix = "<@>";
        commands = new HashMap<String, Command>();

        addCommand("ping", (e, b, args) -> {

            long currentTimeDiscord = System.currentTimeMillis();
            boolean discord = PeelingUtils.pingHost("discordapp.com", 80, 1000);
            currentTimeDiscord = System.currentTimeMillis() - currentTimeDiscord;

            b.sendMessage(e, "Pong! " + currentTimeDiscord + "ms");
        });

        addCommand("help,?,commands", (e, b, args) -> {
            StringBuilder cmds = new StringBuilder();
            commands.forEach((n, c) -> {
                cmds.append(n + "\n");
            });
            b.sendMessage(e, "```yml\nCommand List```\n" + cmds);

        });

        addCommand("invite", (e, b, args) -> {
            b.sendLink(e, "https://discordapp.com/oauth2/authorize?client_id=" + bot.getJDA().getSelfUser().getId() + "&scope=bot&permissions=2146958847", "Click here to add me to your server");
        });

    }

    public void addCommand(String name, Command command) {
        commands.put(name, command);
    }

    @Override
    public void onReady(ReadyEvent e) {
        tagPrefix = bot.getJDA().getSelfUser().getAsMention() + " ";
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        if (!e.getAuthor().isBot()) {
            String content = e.getMessage().getContentRaw();
            if (content.startsWith(bot.getPrefix())) {
                if (!(e.getChannel() instanceof PrivateChannel)) {
                    content = content.substring(bot.getPrefix().length());
                    String[] commandSplit = content.split(" ");
                    Command c = getCommand(commandSplit[0]);
                    if (c != null) {
                        String[] args = Arrays.copyOfRange(commandSplit, 1, commandSplit.length);
                        c.execute(e, bot, args);
                    }
                } else {
                    bot.sendMessage(e, "Commands can only be run in a server. Sorry!");
                }
            }
        }
    }

    public Command getCommand(String query) {
        for (Entry<String, Command> entry : commands.entrySet()) {
            for (String alias : entry.getKey().split(",")) {
                if (alias.equalsIgnoreCase(query)) {
                    return entry.getValue();
                }
            }
        }
        return null;

    }

    public Map<String, Command> getCommands() {
        return commands;
    }


}