package com.mouldycheerio.roomcord;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.json.JSONObject;

import com.mouldycheerio.roomcord.rooms.Realm;

import net.dv8tion.jda.api.entities.Guild;

public class RoomsController {
    private List<Realm> realms;
    private RoomCordBot bot;
    private File dir;

    public RoomsController(RoomCordBot bot) {
        // TODO Auto-generated constructor stub

        this.bot = bot;
        realms = new ArrayList<Realm>();
        bot.getCommandController().addCommand("setup", (e, op, args) -> {
            if (e.getGuild().getOwner().getIdLong() == e.getAuthor().getIdLong()) {
                Realm r = new Realm(e.getGuild());
                realms.add(r);
                op.sendMessage(e, "Realm initialised");
                save(r);
            } else {
                op.sendMessage(e, ":x: Only the owner can perform this command");
            }

        });

        bot.getCommandController().addCommand("newplace", (e, op, args) -> {
            if (e.getGuild().getOwner().getIdLong() == e.getAuthor().getIdLong()) {
                Optional<Realm> realmOptional = getRealm(e.getGuild());
                if (realmOptional.isPresent()) {
                    Realm realm = realmOptional.get();
                    realm.createArea(args[0], args[1]);
                    save(realm);

                } else {
                    op.sendMessage(e, ":x: Please use setup to initialise the guild realm");
                }

            } else {
                op.sendMessage(e, ":x: Only the owner can perform this command");
            }

        });
        dir = new File(bot.getDatadir(), "realms");
        dir.mkdirs();
        loadAll();
    }

    public void saveAll() {
        for (Realm r : realms) {
            save(r);
        }
    }

    public void save(Realm r) {
        File file = new File(dir, r.getGuild().getId() + ".json");
        PeelingUtils.saveJSON(file, r.toJSON());
    }

    public void loadAll() {
        if (dir.exists()) {
            File[] list = dir.listFiles();
            for (File file : list) {
                load(file);
            }
        }
    }

    private void load(File file) {
        String id = file.getName().substring(0, file.getName().length() - 5);
        JSONObject loadJSON = PeelingUtils.loadJSON(file);
        Realm r = Realm.fromJSON(loadJSON, bot.getJDA(), id);
        realms.add(r);
    }

    public Optional<Realm> getRealm(Guild g) {
        for (Realm r : realms) {
            if (r.getGuild().equals(g)) {
                return Optional.of(r);
            }
        }

        return Optional.empty();
    }
}
