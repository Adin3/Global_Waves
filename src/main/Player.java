package main;

import lombok.Getter;

public class Player {
    public Player() {}

    @Getter
    private Song song;

    @Getter
    private Song currentSong;

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

    public void shuffle(int seed) {
        Manager.partialResult.put("message", "Please load a source before using the shuffle function.");
    }

    public void next() {
        Manager.partialResult.put("message", "Please load a source before skipping to the next track.");
    }

    public void prev() {
        Manager.partialResult.put("message", "Please load a source before returning to the previous track.");
    }

    public void forward() {
        Manager.partialResult.put("message", "Please load a source before skipping forward.");
    }

    public void backward() {
        Manager.partialResult.put("message", "Please select a source before rewinding.");
    }

    public void pauseButton() {}

    public void shuffleButton() {}

    public void repeatButton() {}

    public void updateDuration(int deltaTime) {}

    public void updatePlayer() {}
}
