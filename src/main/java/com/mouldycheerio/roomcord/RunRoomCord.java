package com.mouldycheerio.roomcord;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;

import org.json.JSONObject;
import org.json.JSONTokener;

public class RunRoomCord {
    private static JSONObject config;

    public static void main(String[] args) throws InterruptedException {
        LocalDateTime time = LocalDateTime.now();
        System.out.println("");
        System.out.println("===================================== " + time.getDayOfMonth() + "/" + time.getMonthValue() + "/" + time.getYear() + " " + time.getHour() + ":" + time.getMinute() + ":" + time.getSecond() + " =====================================");
        config = new JSONObject();
        load();

        RoomCordBot bot = null;
        try {
            bot = new RoomCordBot(config);
        } catch (Exception e1) {
            // TODO Auto-generated catch block

            e1.printStackTrace();
            System.exit(0);

        }

    }

    public static void load() {

        try {
            JSONTokener parser = new JSONTokener(new FileReader("config.json"));
            JSONObject obj = (JSONObject) parser.nextValue();
            config = obj;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
