package main;

import fileio.input.SongInput;
import lombok.Getter;

public class Player {
    public Player() {}

    @Getter
    private SongInput song;

    private String repeat = "No Repeat";

    private boolean paused = false;

    private boolean shuffled = false;

    public void clearPlayer() {};
    public void load() {}

    public void status() {}
    public void playPause() {}

    public boolean paused() {
        return false;
    }

    public boolean shuffles() {
        return false;
    }

    public String repeats() {
        return null;
    }

    public void pauseButton() {}

    public void shuffleButton() {}

    public void repeatButton() {}

    public void updateDuration(int deltaTime) {}

    public void updatePlayer() {}
}
