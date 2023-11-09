package main;

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

    public void loadSong(SongInput song) {
        this.song = song;
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