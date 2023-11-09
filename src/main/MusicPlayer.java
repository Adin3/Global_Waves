package main;

import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.SongInput;
import fileio.input.UserInput;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MusicPlayer extends Player{

    public MusicPlayer() {}

    @Getter
    private SongInput song;

    private String repeat = "No Repeat";

    private boolean paused = false;

    private boolean shuffled = false;

    public MusicPlayer(String owner) {
        this.owner = owner;
    }

    private String owner;

    public void load() {
        this.song = Manager.searchBar(owner).getSongLoaded();

        if (this.song != null) {
            Manager.partialResult.put("message", "Playback loaded successfully.");
        }
    }

    public void status() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode status = objectMapper.createObjectNode();
        if (song == null) {
            status.put("name", "");
            status.put("remainedTime", 0);
            status.put("repeat", "No Repeat");
            status.put("shuffle", false);
            status.put("paused", true);
        } else {
            status.put("name", song.getName());
            status.put("remainedTime", song.getDuration());
            status.put("repeat", repeats());
            status.put("shuffle", shuffles());
            status.put("paused", paused());
        }
        Manager.partialResult.set("stats", status);
    }

    public void playPause() {
        Manager.musicPlayer(owner).pauseButton();

        if (Manager.musicPlayer(owner).paused()) {
            Manager.partialResult.put("message", "Playback paused successfully.");
        } else {
            Manager.partialResult.put("message", "Playback resumed successfully.");
        }
    }
    public boolean paused() {
        return paused;
    }

    public boolean shuffles() {
        return shuffled;
    }

    public String repeats() {
        return repeat;
    }

    public void pauseButton() {
        paused = !paused;
    }

    public void shuffleButton() {
        shuffled = !shuffled;
    }

    public void repeatButton() {
        if (repeat.startsWith("No")) {
            repeat = "Repeat";
        } else {
            repeat = "No Repeat";
        }
    }

    public void updateDuration(int deltaTime) {
        if (song == null) return;

        int time = song.getDuration() - deltaTime;
        if (time < 0) time = 0;
        if (!paused)
            song.setDuration(time);
    }
    public void updatePlayer() {
        if (song == null) return;

        if (song.getDuration() == 0) {
            song = null;
            paused = true;
            shuffled = false;
            repeat = "No Repeat";
        }
    }
}