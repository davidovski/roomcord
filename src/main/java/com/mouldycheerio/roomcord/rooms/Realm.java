package com.mouldycheerio.roomcord.rooms;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

public class Realm {

    private List<Area> areas;
    private Guild guild;

    public Realm(Guild g) {
        guild = g;
        areas = new ArrayList<Area>();

    }

    public JSONObject toJSON() {
        JSONObject object = new JSONObject();

        JSONArray areas = new JSONArray();
        getAreas().forEach(a -> areas.put(a.toJSON()));
        object.put("areas", areas);
        return object;
    }

    public static Realm fromJSON(JSONObject o, JDA jda, String id) {
        JSONArray a = o.getJSONArray("areas");
        List<Area> areas = new ArrayList<Area>();
        for (int i = 0; i < a.length(); i++) {
            JSONObject areaJSON = a.getJSONObject(i);
            Area area = Area.fromJSON(areaJSON, jda);
            areas.add(area);
        }

        Realm realm = new Realm(jda.getGuildById(id));

        areas.forEach(e -> realm.getAreas().add(e));
        return realm;

    }

    public Guild getGuild() {
        return guild;
    }

    public List<Area> getAreas() {
        return areas;
    }

    public void createArea(String name, String emote) {
        guild.createCategory(name).queue(c -> {
            guild.createRole().setName("in: " + name).setColor(Color.BLUE.brighter()).queue((r) -> {
                c.createTextChannel("menu").queue(t -> {
                    Area area = new Area(c, r, t, emote, new JSONObject());
                    c.createTextChannel(name).queue();
                    areas.add(area);
                    area.fixPermissions();
                });
            });
        });
    }

}
