package com.petgame;

import com.petgame.core.Pet;
import com.petgame.core.Stats;
import com.petgame.core.actions.Bath;
import com.petgame.core.actions.Feed;
import com.petgame.core.actions.Play;
import com.petgame.core.actions.Sleep;
import com.petgame.io.PersistenceService;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {

    // cooldown settings (seconds)
    private static final long FEED_CD = 30;
    private static final long PLAY_CD = 45;
    private static final long SLEEP_CD = 60;
    private static final long BATH_CD = 40;

    private static long nowSec() {
        return System.currentTimeMillis() / 1000L;
    }

    public static void main(String[] args) {
        Pet pet = new Pet("Momo", new Stats(20, 80, 80, 80));
        Feed feed = new Feed();
        Play play = new Play();
        Sleep sleep = new Sleep();
        Bath bath = new Bath();

        // 持久化服务
        PersistenceService store = new PersistenceService();
        final String SAVE_PATH = "pet-save.json";

        // last-used timestamps
        Map<String, Long> last = new HashMap<>();

        System.out.println("Welcome to Virtual Pet!");
        System.out.println("Commands: feed | play | sleep | bath | skip | save | load | quit");
        System.out.println("Current: " + pet);

        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.print("> ");
            String cmd = sc.nextLine().trim().toLowerCase();
            long now = nowSec();

            switch (cmd) {
                case "feed": {
                    if (onCooldown("feed", last, now, FEED_CD)) break;
                    feed.execute(pet);
                    stamp("feed", last, now);
                    System.out.println("Fed.  " + pet);
                    break;
                }
                case "play": {
                    if (onCooldown("play", last, now, PLAY_CD)) break;
                    play.execute(pet);
                    stamp("play", last, now);
                    System.out.println("Played.  " + pet);
                    break;
                }
                case "sleep": {
                    if (onCooldown("sleep", last, now, SLEEP_CD)) break;
                    sleep.execute(pet);
                    stamp("sleep", last, now);
                    System.out.println("Slept.  " + pet);
                    break;
                }
                case "bath": {
                    if (onCooldown("bath", last, now, BATH_CD)) break;
                    bath.execute(pet);
                    stamp("bath", last, now);
                    System.out.println("Bathed.  " + pet);
                    break;
                }
                case "skip": {
                    pet.tickDecay();
                    System.out.println("Time passed.  " + pet);
                    break;
                }
                case "save": {
                    store.save(pet, SAVE_PATH);
                    System.out.println("Saved to " + SAVE_PATH + ".  " + pet);
                    break;
                }
                case "load": {
                    pet = store.load(SAVE_PATH);
                    System.out.println("Loaded from " + SAVE_PATH + ".  " + pet);
                    break;
                }
                case "quit":
                    System.out.println("Bye!");
                    sc.close();
                    return;
                default:
                    System.out.println("Unknown command. Use: feed | play | sleep | bath | skip | save | load | quit");
            }
        }
    }

    private static boolean onCooldown(String name, Map<String, Long> last, long now, long cd) {
        Long t = last.get(name);
        if (t == null) return false;
        long left = cd - (now - t);
        if (left > 0) {
            System.out.println("Action '" + name + "' is on cooldown. Try again in " + left + "s.");
            return true;
        }
        return false;
    }

    private static void stamp(String name, Map<String, Long> last, long now) {
        last.put(name, now);
    }
}
