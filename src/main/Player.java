package main;

import fileio.input.SongInput;
import lombok.Getter;

public class Player {
    public Player() {}

    @Getter
    private SongInput song;

    private String repeat = "No Repeat";

    protected int repeatState = 0;

    private boolean paused = false;

    private boolean shuffled = false;

    public void clearPlayer() {};
    public void load() {
        Manager.partialResult.put("message", "Please select a source before attempting to load.");
    }

    public void status() {}
    public void playPause() {}

    public boolean paused() {
        return false;
    }

    public boolean shuffles() {
        return false;
    }

    public void repeat() {
        Manager.partialResult.put("message", "Please load a source before setting the repeat status.");
    }

    public void pauseButton() {}

    public void shuffleButton() {}

    public void repeatButton() {}

    public void updateDuration(int deltaTime) {}

    public void updatePlayer() {}
}
