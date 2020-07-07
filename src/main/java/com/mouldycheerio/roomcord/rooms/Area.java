package com.mouldycheerio.roomcord.rooms;

import java.util.List;

import org.json.JSONObject;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

public class Area {

    private String emoji;
    private Category category;
    private Role role;
    private TextChannel menuChannel;
    private JSONObject connections;

    public Area(Category category, Role role, TextChannel menuChannel, String emoji, JSONObject connections) {
        this.category = category;
        this.role = role;
        this.menuChannel = menuChannel;
        this.emoji = emoji;
        this.connections = connections;
    }

    public void fixPermissions() {
        category.getRolePermissionOverrides().forEach(p -> p.delete().queue());
        category.putPermissionOverride(role).setAllow(List.of(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE)).queue();
        category.putPermissionOverride(category.getGuild().getPublicRole()).setDeny(List.of(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE)).queue();

        category.getChannels().forEach(c -> {
            if (c.getId().equals(menuChannel.getId())) {
                c.putPermissionOverride(role).setAllow(List.of(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE)).queue();
                c.putPermissionOverride(role).setDeny(List.of(Permission.MESSAGE_ADD_REACTION)).queue();

            }
            c.putPermissionOverride(category.getGuild().getPublicRole()).setDeny(List.of(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE)).queue();
        });
    }

    public JSONObject toJSON() {
        JSONObject o = new JSONObject();
        o.put("category", category.getId());
        o.put("role", role.getId());
        o.put("menu", menuChannel.getId());
        o.put("emoji", emoji);
        o.put("connections", connections);
        return o;
    }

    public static Area fromJSON(JSONObject o, JDA jda) {
        Category category = jda.getCategoryById(o.getString("category"));
        Role role = jda.getRoleById(o.getString("role"));
        TextChannel menu = jda.getTextChannelById(o.getString("menu"));
        String emoji = o.getString("emoji");

        JSONObject connections = o.getJSONObject("connections");

        Area area = new Area(category, role, menu, emoji, connections);
        return area;

    }

    public Role getRole() {
        return role;
    }

    public Category getCategory() {
        return category;
    }

    public TextChannel getMenuChannel() {
        return menuChannel;
    }

}
