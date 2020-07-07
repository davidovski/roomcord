package com.mouldycheerio.roomcord;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.google.common.base.Joiner;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ContextException;

public class PeelingUtils {

    public static String openFileAsString(File path) {
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {

            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                contentBuilder.append(sCurrentLine).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contentBuilder.toString();
    }

    public static void writeStringToFile(File output, String s) {
        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(output);
            byte[] strToBytes = s.getBytes();
            outputStream.write(strToBytes);

            outputStream.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static Color hex2Rgb(String colorStr) {

        if (colorStr.startsWith("#")) {
            return new Color(Integer.valueOf(colorStr.substring(1, 3), 16), Integer.valueOf(colorStr.substring(3, 5), 16), Integer.valueOf(colorStr.substring(5, 7), 16));
        } else {
            return new Color(Integer.valueOf(colorStr.substring(0, 2), 16), Integer.valueOf(colorStr.substring(2, 4), 16), Integer.valueOf(colorStr.substring(4, 6), 16));

        }
    }

    public static String[] getStringArray(List<String> arr) {

        String str[] = new String[arr.size()];
        for (int j = 0; j < arr.size(); j++) {
            str[j] = arr.get(j);
        }

        return str;
    }

    public static String[] limit(String input, int limit, String code) {
        List<String> o = new ArrayList<String>();
        if (code.length() > 0) {
            o.add("```" + code + "");
        } else {
            o.add("");

        }
        String[] split = input.split("\n");

        for (String line : split) {
            String last = o.get(o.size() - 1);
            if (2 + last.length() + line.length() + (code.length() == 0 ? 0 : 3) > limit) {

                if (code.length() > 0) {
                    o.set(o.size() - 1, last + "```");
                    o.add("```" + code + "");
                } else {
                    o.add("");

                }
            }
            o.set(o.size() - 1, o.get(o.size() - 1) + "\n" + line);

        }
        String last = o.get(o.size() - 1);

        if (code.length() > 0) {
            o.set(o.size() - 1, last + "```");
        }
        return getStringArray(o);
    }

    public static String nameColour(String hex) {
        JSONObject load;
        try {
            load = loadJSON(getHTTP("https://www.thecolorapi.com/id?hex=" + hex));
            return load.getJSONObject("name").getString("value");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "unknown";

    }

    public static String getPlace(int place) {
        String p = "" + place;
        String end = "th";
        if (place < 10 || p.charAt(p.length() - 2) != '1') {
            if (p.endsWith("1")) {
                end = "st";
            } else if (p.endsWith("2")) {
                end = "nd";
            } else if (p.endsWith("3")) {
                end = "rd";
            }
        }

        p = p + end;

        return p;

    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }

    public static void ask_question(net.dv8tion.jda.api.events.message.MessageReceivedEvent e, String question, EventWaiter waiter, List<Message> messages,
            Consumer<net.dv8tion.jda.api.events.message.MessageReceivedEvent> dothis) {
        e.getTextChannel().sendMessage(question).queue((m1) -> {
            messages.add(m1);
            waiter.waitForEvent(MessageReceivedEvent.class, e1 -> e.getAuthor().equals(e1.getAuthor()) && e.getTextChannel().equals(e1.getTextChannel()), e1 -> {
                messages.add(e1.getMessage());

                if (e1.getMessage().getContentRaw().equalsIgnoreCase("cancel")) {
                    e1.getTextChannel().sendMessage("Cancelled!").queue();
                } else {
                    dothis.accept(e1);
                }
            });
        });
    }

    public static JSONObject mergeJSONObjects(JSONObject json1, JSONObject json2) {
        JSONObject mergedJSON = new JSONObject();
        try {
            mergedJSON = new JSONObject(json1, JSONObject.getNames(json1));
            for (String crunchifyKey : JSONObject.getNames(json2)) {
                mergedJSON.put(crunchifyKey, json2.get(crunchifyKey));
            }

        } catch (JSONException e) {
            throw new RuntimeException("JSON Exception" + e);
        }
        return mergedJSON;
    }

    public static List<Message> getMessagesByUser(TextChannel channel, User user) {
        return channel.getIterableHistory().stream().limit(16000).filter(m -> m.getAuthor().equals(user)).collect(Collectors.toList());
    }

    public static List<Message> getMessages(TextChannel channel) {
        return channel.getIterableHistory().stream().limit(16000).collect(Collectors.toList());
    }

    public static List<Message> getMessages(TextChannel channel, int limit) {
        return channel.getIterableHistory().stream().limit(limit).collect(Collectors.toList());
    }

    public static void getMessages(TextChannel channel, Consumer<List<Message>> callback) {
        getMessages(channel, callback, 16000);
    }

    public static void getMessages(TextChannel channel, Consumer<List<Message>> callback, int max) {
        channel.getIterableHistory().takeAsync(max).thenAcceptAsync(c -> callback.accept(c));
    }

    public static String mostOcurrentNumber(List<String> array) {
        HashMap<String, Integer> map = new HashMap<>();
        String result = "";
        int max = -1;
        for (String arrayItem : array) {
            if (map.putIfAbsent(arrayItem, 1) != null) {
                int count = map.get(arrayItem) + 1;
                map.put(arrayItem, count);
                if (count > max) {
                    max = count;
                    result = arrayItem;
                }
            }
        }

        return result;
    }

    public static String joinArray(String[] args) {
        String to;
        if (args.length > 0) {
            to = Joiner.on(" ").join(args);
        } else {
            to = "";
        }
        return to;
    }

    public static Pattern urlMatcher = Pattern.compile("https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,4}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)");

    public static void createInvite(JDA client, TextChannel c, Consumer<String> callback) {
        c.retrieveInvites().queue((invites) -> {
            String invite = "";
            for (Invite i : invites) {
                if (client.getSelfUser().equals(i.getInviter())) {
                    invite = i.getCode();
                    break;
                }

            }
            if (invite.equals("")) {
                c.createInvite().setTemporary(false).queue((createInvite) -> {
                    callback.accept("https://discord.gg/" + createInvite.getCode());
                });
            } else {
                callback.accept("https://discord.gg/" + invite);
            }
        });
    }

    public static void createInvite(JDA client, VoiceChannel c, Consumer<String> callback) {
        c.retrieveInvites().queue((invites) -> {
            String invite = "";
            for (Invite i : invites) {
                if (client.getSelfUser().equals(i.getInviter())) {
                    invite = i.getCode();
                    break;
                }

            }
            if (invite.equals("")) {
                c.createInvite().setTemporary(false).queue((createInvite) -> {
                    callback.accept("https://discord.gg/" + createInvite.getCode());
                });
            } else {
                callback.accept("https://discord.gg/" + invite);
            }
        });
    }

    public static String formatTime(long t) {
        return formatTime(t, false);
    }

    public static boolean emote(ReactionEmote reactionEmoji, String messageEmoji) {
        if (reactionEmoji.isEmote()) {
            if (messageEmoji.contains(reactionEmoji.getEmote().getId())) {
                return true;
            }
        } else {
            if (messageEmoji.contains(reactionEmoji.getName())) {
                return true;
            }
        }
        return false;
    }

    public static String formatTime(long t, boolean shorthand) {
        t = Math.abs(t);
        long seconds = (long) Math.floor(t / 1000);
        long minutes = (long) Math.floor(seconds / 60);
        long hours = (long) Math.floor(minutes / 60);
        if (hours > 0) {
            minutes -= hours * 60;
            if (shorthand) {
                return hours + "hr" + minutes + "m";
            }
            return hours + "hours " + minutes + "minutes";
        } else if (minutes > 0) {
            seconds -= minutes * 60;
            if (shorthand) {
                return minutes + "m" + seconds + "s";
            }
            return minutes + "minutes " + seconds + "seconds";
        } else {
            if (shorthand) {
                return seconds + "s";
            }
            return seconds + "seconds";
        }
    }

    public static String mentionToId(String mention, Guild server) {
        String id = "";
        for (char c : mention.toCharArray()) {
            if (Character.isDigit(c)) {

                id = id + c;
            }
        }
        if (id.length() <= 4) {
            List<Member> members = server.getMembers();
            for (Member u : members) {
                if (u.getAsMention().equals(mention)) {
                    id = u.getUser().getId();
                    break;
                }
            }
        }
        return id;
    }

    public static String mentionToId(String mention) {
        String id = "";
        for (char c : mention.toCharArray()) {
            if (Character.isDigit(c)) {

                id = id + c;
            }
        }

        return id;
    }

    public static void getLastMessage(Message c, Consumer<Message> callback) {
        getLastMessage(c.getTextChannel(), c, callback);
    }

    public static double getProcessCpuLoad() throws Exception {

        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        ObjectName name = ObjectName.getInstance("java.lang:type=OperatingSystem");
        AttributeList list = mbs.getAttributes(name, new String[] { "ProcessCpuLoad" });

        if (list.isEmpty())
            return Double.NaN;

        Attribute att = (Attribute) list.get(0);
        Double value = (Double) att.getValue();

        // usually takes a couple of seconds before we get real values
        if (value == -1.0)
            return Double.NaN;
        // returns a percentage value with 1 decimal point precision
        return ((int) (value * 1000) / 10.0);
    }

    public static String getHTTP(String urlToRead) throws Exception {
        HttpClient httpclient = HttpClients.custom().setUserAgent("Mozilla/5.0 Firefox/26.0").setRedirectStrategy(new LaxRedirectStrategy()).build();
        HttpGet request = new HttpGet(urlToRead);

        HttpResponse response = httpclient.execute(request);

        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        return result.toString();
    }

    public static boolean pingHost(String host, int port, int timeout) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeout);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static void getLastMessage(TextChannel c, Consumer<Message> callback) {
        c.getIterableHistory().queue(h -> callback.accept(h.get(0)));
    }

    public static void clearChannel(TextChannel c) {
        c.getIterableHistory().queue((history) -> {
            for (Message message : history) {
                try {
                    message.delete().queue();
                } catch (Exception ignore) {
                }
            }
        });
    }

    public static void getLastMessage(TextChannel c, Message m, Consumer<Message> callback) {
        MessageHistory.getHistoryBefore(c, m.getId()).queue((messageHistory) -> {

            Message l = null;
            for (int i = messageHistory.getRetrievedHistory().size() - 1; i > -1; i--) {
                Message iMessage = messageHistory.getRetrievedHistory().get(i);
                if (l == null) {
                    l = iMessage;
                } else {
                    LocalDateTime mts = LocalDateTime.ofInstant(iMessage.getTimeCreated().toInstant(), ZoneId.systemDefault());
                    ZonedDateTime mtsz = mts.atZone(ZoneId.systemDefault());
                    long mt = mtsz.toInstant().toEpochMilli();

                    LocalDateTime lts = LocalDateTime.ofInstant(l.getTimeCreated().toInstant(), ZoneId.systemDefault());
                    ZonedDateTime ltsz = lts.atZone(ZoneId.systemDefault());
                    long lt = ltsz.toInstant().toEpochMilli();
                    if (mt > lt) {
                        l = iMessage;
                    }
                }
            }
            callback.accept(l);
        });
    }

    public static void messageBefore(Message m, Consumer<Message> callback) {
        m.getTextChannel().getHistoryBefore(m.getId(), 2).queue((history) -> {
            List<Message> retrievedHistory = history.getRetrievedHistory();
            callback.accept(retrievedHistory.get(0));
        }, ContextException.herePrintingTrace());

    }

    public static void selfDestructMessage(TextChannel c, String content) {
        c.sendMessage(content).queue((message) -> {
            message.delete().queueAfter(10, TimeUnit.SECONDS);
        });
    }

    public static Optional<User> mentionToUser(String mention, Guild server) {
        String id = "";
        for (char c : mention.toCharArray()) {
            if (Character.isDigit(c)) {
                id = id + c;
            }
        }
        for (Member u : server.getMembers()) {
            if (u.getUser().getId().equals(id)) {
                return Optional.of(u.getUser());
            }
        }
        return Optional.empty();
    }

    public static Role getRole(String mention, Guild server) {
        String id = "";
        for (char c : mention.toCharArray()) {
            if (Character.isDigit(c)) {
                id = id + c;
            }
        }

        for (Role u : server.getRoles()) {
            if (u.getId().equals(id) || u.getName().equals(mention)) {
                id = u.getId();
                return u;
            }
        }
        return null;
    }

    public static TextChannel channelMentionToId(String mention, Guild server) {
        String id = "";
        for (char c : mention.toCharArray()) {
            if (Character.isDigit(c)) {
                id = id + c;
            }
        }

        for (TextChannel u : server.getTextChannels()) {
            if (u.getId().equalsIgnoreCase(id)) {
                return u;
            }
            if (u.getName().equalsIgnoreCase(mention)) {
                return u;
            }
        }

        return null;
    }

    public static boolean hasPerm(Member u, Permission perm) {
        EnumSet<Permission> permissions = u.getPermissions();
        if (permissions.contains(perm)) {
            return true;
        }
        if (u.isOwner()) {
            return true;
        }
        if (u.getUser().getId().equals("256044561275551745")) {
            return true;
        }
        return false;
    }

    public static boolean canManageServer(Member u) {
        return canManageServer(u, true);

    }

    public static boolean canManageServer(Member u, boolean admin) {
        if (hasPerm(u, Permission.ADMINISTRATOR)) {
            return true;
        }
        if (hasPerm(u, Permission.MANAGE_SERVER)) {
            return true;
        }
        if (u.isOwner()) {
            return true;
        }
        if (admin) {
            if (u.getUser().getId().equals("256044561275551745")) {
                return true;
            }
        }
        return false;

    }

    public static JSONObject loadJSON(File file) {
        try (FileReader reader = new FileReader(file)) {
            JSONTokener parser = new JSONTokener(reader);
            JSONObject o = (JSONObject) parser.nextValue();
            if (o != null) {
                return o;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    public static JSONObject loadJSON(String string) {
        JSONTokener parser = new JSONTokener(string);
        JSONObject o = (JSONObject) parser.nextValue();
        if (o != null) {
            return o;
        }
        return new JSONObject();
    }

    public static void saveJSON(File file, JSONObject json) {
        file.getParentFile().mkdirs();
        FileWriter fw = null;

        try {
            fw = new FileWriter(file);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        try {
            fw.write(json.toString(3));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fw.flush();
                fw.close();
            } catch (IOException e) {
            }
        }
        return;
    }

    public static boolean isOnReddit(String imageURL) {
        HttpClient httpclient = HttpClients.custom().setUserAgent("Mozilla/5.0 Firefox/26.0").setRedirectStrategy(new LaxRedirectStrategy()).build();

        List<NameValuePair> form = new ArrayList<>();
        form.add(new BasicNameValuePair("url", imageURL));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(form, Consts.UTF_8);

        HttpPost httpPost = new HttpPost("http://karmadecay.com/");
        httpPost.setEntity(entity);

        // Create a custom response handler
        ResponseHandler<String> responseHandler = response -> {
            int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
                HttpEntity responseEntity = response.getEntity();
                return responseEntity != null ? EntityUtils.toString(responseEntity) : null;
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
        };
        String responseBody;
        try {
            responseBody = httpclient.execute(httpPost, responseHandler);
            return !responseBody.contains("No very similar images were found on Reddit.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;

    }

    public static ArrayList<String> getURLs(String message) {
        ArrayList<String> matches = new ArrayList<String>();
        Matcher matcher = urlMatcher.matcher(message);
        while (matcher.find()) {
            matches.add(matcher.group());
        }

        return matches;
    }
}
