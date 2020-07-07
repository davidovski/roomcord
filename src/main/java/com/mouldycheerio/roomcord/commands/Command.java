package com.mouldycheerio.roomcord.commands;

import com.mouldycheerio.roomcord.RoomCordBot;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;



public interface Command {
    public void execute(MessageReceivedEvent e, RoomCordBot op, String[] args);
}
