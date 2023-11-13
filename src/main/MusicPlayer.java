package main;

import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MusicPlayer extends Player{

    public MusicPlayer() {}

    @Getter
    private Song song;

    private String repeat = "No Repeat";

    private boolean paused = false;

    private boolean shuffled = false;

    private int savedTime = 0;

    private boolean repeatOnce = false;

    public MusicPlayer(String owner) {
        this.owner = owner;
    }

    private String owner;

    public void clearPlayer() {
        song = null;
    }

    public void load() {

        if (!Manager.getSource(owner).isSourceSelected()) {
            Manager.partialResult.put("message", "Please select a source before attempting to load.");
            return;
        }

        this.song = Manager.searchBar(owner).getSongLoaded();
        if (this.song != null) {
            Manager.partialResult.put("message", "Playback loaded successfully.");
            song.setDuration(song.getMaxDuration());
        } else {
            Manager.partialResult.put("message", "You can't load an empty audio collection!");
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

    public void repeat() {

        if (!Manager.getSource(owner).isSourceLoaded()) {
            Manager.partialResult.put("message", "Please load a source before setting the repeat status.");
            return;
        }

        repeatButton();

        Manager.partialResult.put("message", "Repeat mode changed to " + repeat.toLowerCase() + ".");
    }

    public void shuffle(int seed) {
        if (!Manager.getSource(owner).isSourceLoaded()) {
            Manager.partialResult.put("message", "Please load a source before using the shuffle function.");
            return;
        }
        Manager.partialResult.put("message", "The loaded source is not a playlist.");
    }

    public void next() {
        if (!Manager.getSource(owner).isSourceLoaded()) {
            Manager.partialResult.put("message", "Please load a source before skipping to the next track.");
            return;
        }

        if (repeat.equals("No Repeat")) {
            song = null;
            return;
        }

        if (repeat.equals("Repeat Once")) {
            repeat = "No Repeat";
            repeatState = 0;
        }

        song.setDuration(song.getMaxDuration());
        Manager.partialResult.put("message",
                "Skipped to next track successfully. The current track is " + song.getName() + ".");
    }

    public void prev() {

        if (!Manager.getSource(owner).isSourceLoaded()) {
            Manager.partialResult.put("message", "Please load a source before returning to the previous track.");
            return;
        }

        if (!song.getDuration().equals(song.getMaxDuration())) {
            song.setDuration(song.getMaxDuration());
            Manager.partialResult.put("message",
                    "Returned to previous track successfully. The current track is " + song.getName() + ".");
            return;
        }

        if (repeat.equals("No Repeat")) {
            song = null;
            return;
        }

        if (repeat.equals("Repeat Once")) {
            repeat = "No Repeat";
            repeatState = 0;
        }

        song.setDuration(song.getMaxDuration());
        Manager.partialResult.put("message",
                "Returned to previous track successfully. The current track is " + song.getName() + ".");
    }

    public void forward() {
        if (!Manager.getSource(owner).isSourceLoaded()) {
            Manager.partialResult.put("message", "Please load a source before attempting to forward.");
            return;
        }
        Manager.partialResult.put("message", "The loaded source is not a podcast.");
    }

    public void backward() {
        if (!Manager.getSource(owner).isSourceLoaded()) {
            Manager.partialResult.put("message", "Please load a source before attempting to backward.");
            return;
        }
        Manager.partialResult.put("message", "The loaded source is not a podcast.");
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
        switch (repeatState) {
            case 0:
                repeat = "Repeat Once";
                repeatOnce = true;
                repeatState = 1;
                break;
            case 1:
                repeat = "Repeat Infinite";
                repeatState = 2;
                break;
            case 2:
                repeat = "No Repeat";
                repeatState = 0;
                break;
        }
    }

    public void updateDuration(int deltaTime) {
        if (song == null) return;

        int time = song.getDuration() - deltaTime;
        savedTime = 0;
        if (time < 0) {
            savedTime = -time;
            time = 0;
        }
        if (!paused)
            song.setDuration(time);
    }
    public void updatePlayer() {
        if (song == null) return;

        while (savedTime != 0 && !repeat.equals("No Repeat")) {
            if (repeat.equals("Repeat Once")) {
                repeat = "No Repeat";
                repeatState = 0;
                song.setDuration(song.getMaxDuration());
            }

            if (repeat.equals("Repeat Infinite")) {
                song.setDuration(song.getMaxDuration());
            }

            updateDuration(savedTime);
        }

        if (song.getDuration() == 0) {
            song = null;
            paused = true;
            shuffled = false;
            repeat = "No Repeat";
        }
    }
}