package com.petgame.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.WindowConstants;

import com.petgame.core.Pet;
import com.petgame.core.Stats;
import com.petgame.core.actions.Bath;
import com.petgame.core.actions.Feed;
import com.petgame.core.actions.Play;
import com.petgame.core.actions.Sleep;
import com.petgame.io.PersistenceService;

public class SwingApp {

    // cooldowns (seconds)
    private static final long FEED_CD  = 30;
    private static final long PLAY_CD  = 45;
    private static final long SLEEP_CD = 60;
    private static final long BATH_CD  = 40;

    // model & services
    private Pet pet = new Pet("Momo", new Stats(20, 80, 80, 80));
    private final Feed feed = new Feed();
    private final Play play = new Play();
    private final Sleep sleep = new Sleep();
    private final Bath bath = new Bath();
    private final PersistenceService store = new PersistenceService();
    private final String SAVE_PATH = "pet-save.json";

    // cooldown stamps
    private final Map<String, Long> last = new HashMap<>();

    // UI widgets
    private JLabel lblState, lblName;
    private JProgressBar barHunger, barHappiness, barEnergy, barHygiene;
    private JButton btnFeed, btnPlay, btnSleep, btnBath, btnSave, btnLoad;

    private static long nowSec() { return System.currentTimeMillis() / 1000L; }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SwingApp().start());
    }

    private void start() {
        JFrame f = new JFrame("Virtual Pet");
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.setSize(520, 300);
        f.setLocationRelativeTo(null);

        lblName  = new JLabel("Pet: " + pet.getName());
        lblState = new JLabel("State: " + pet.getState());

        barHunger    = mkBar("Hunger");     // 0 good, 100 bad
        barHappiness = mkBar("Happiness");  // 0 bad, 100 good
        barEnergy    = mkBar("Energy");     // 0 bad, 100 good
        barHygiene   = mkBar("Hygiene");    // 0 dirty, 100 clean

        JPanel bars = new JPanel(new GridLayout(4,1,6,6));
        bars.add(wrap("Hunger (lower is better)", barHunger));
        bars.add(wrap("Happiness", barHappiness));
        bars.add(wrap("Energy", barEnergy));
        bars.add(wrap("Hygiene", barHygiene));

        btnFeed  = new JButton("Feed");
        btnPlay  = new JButton("Play");
        btnSleep = new JButton("Sleep");
        btnBath  = new JButton("Bath");
        btnSave  = new JButton("Save");
        btnLoad  = new JButton("Load");

        JPanel actions = new JPanel(new GridLayout(2,3,8,8));
        actions.add(btnFeed); actions.add(btnPlay); actions.add(btnSleep);
        actions.add(btnBath); actions.add(btnSave); actions.add(btnLoad);

        JPanel top = new JPanel(new GridLayout(2,1));
        top.add(lblName);
        top.add(lblState);

        f.setLayout(new BorderLayout(10,10));
        f.add(top, BorderLayout.NORTH);
        f.add(bars, BorderLayout.CENTER);
        f.add(actions, BorderLayout.SOUTH);

        // wire actions (with cooldown)
        btnFeed.addActionListener(e -> runAction("feed", FEED_CD, () -> feed.execute(pet)));
        btnPlay.addActionListener(e -> runAction("play", PLAY_CD, () -> play.execute(pet)));
        btnSleep.addActionListener(e -> runAction("sleep", SLEEP_CD, () -> sleep.execute(pet)));
        btnBath.addActionListener(e -> runAction("bath", BATH_CD, () -> bath.execute(pet)));
        btnSave.addActionListener(e -> {
            store.save(pet, SAVE_PATH);
            JOptionPane.showMessageDialog(f, "Saved to " + SAVE_PATH);
        });
        btnLoad.addActionListener(e -> {
            pet = store.load(SAVE_PATH);   // replace model
            refresh();
            JOptionPane.showMessageDialog(f, "Loaded from " + SAVE_PATH);
        });

        // decay timer: every 5s one tick
        new Timer(5000, e -> { pet.tickDecay(); refresh(); }).start();

        // cooldown UI timer: every 1s update button text/enabled
        new Timer(1000, e -> updateCooldownButtons()).start();

        refresh();
        f.setVisible(true);
    }

    // ----- helpers -----

    private JProgressBar mkBar(String name) {
        JProgressBar b = new JProgressBar(0, 100);
        b.setStringPainted(true);
        b.putClientProperty("name", name);
        return b;
    }

    private JPanel wrap(String title, JComponent c) {
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JLabel(title), BorderLayout.NORTH);
        p.add(c, BorderLayout.CENTER);
        return p;
    }

    private void refresh() {
        Stats s = pet.getStats();
        lblState.setText("State: " + pet.getState());
        barHunger.setValue(s.hunger);
        barHunger.setString(String.valueOf(s.hunger));
        barHappiness.setValue(s.happiness);
        barHappiness.setString(String.valueOf(s.happiness));
        barEnergy.setValue(s.energy);
        barEnergy.setString(String.valueOf(s.energy));
        barHygiene.setValue(s.hygiene);
        barHygiene.setString(String.valueOf(s.hygiene));
    }

    private void runAction(String name, long cdSec, Runnable action) {
        long now = nowSec();
        Long t = last.get(name);
        if (t != null) {
            long left = cdSec - (now - t);
            if (left > 0) {
                JOptionPane.showMessageDialog(null, "'" + name + "' on cooldown: " + left + "s");
                return;
            }
        }
        action.run();
        last.put(name, now);
        refresh();
    }

    private void updateCooldownButtons() {
        updateButton(btnFeed,  "feed",  FEED_CD);
        updateButton(btnPlay,  "play",  PLAY_CD);
        updateButton(btnSleep, "sleep", SLEEP_CD);
        updateButton(btnBath,  "bath",  BATH_CD);
    }

    private void updateButton(JButton b, String name, long cd) {
        long now = nowSec();
        Long t = last.get(name);
        if (t == null) {
            b.setEnabled(true);
            b.setText(cap(name));
            return;
        }
        long left = cd - (now - t);
        if (left > 0) {
            b.setEnabled(false);
            b.setText(cap(name) + " (" + left + "s)");
        } else {
            b.setEnabled(true);
            b.setText(cap(name));
        }
    }

    private String cap(String s) { return s.substring(0,1).toUpperCase() + s.substring(1); }
}
