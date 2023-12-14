package program.player;

import lombok.Getter;
import program.Manager;
import program.format.Album;
import program.format.Playlist;
import program.format.Podcast;
import program.format.Song;

public class Player {
    public Player() { }

    @Getter
    private Song song;

    @Getter
    private Song currentSong;

    @Getter
    private Album album;

    private String repeat = "No Repeat";

    protected int repeatState = 0;

    private boolean paused = false;

    private boolean shuffled = false;

    /**
     * @return the podcast played by player
     */
    public Podcast getPodcast() {
        return null;
    }
    /**
     * @return the playlist played by player
     */
    public Playlist getPlaylist() {
        return null;
    }
    /**
     * clear player
     */
    public void clearPlayer() { };
    /**
     * load song
     */
    public void load() {
        Manager.getPartialResult().put("message",
                "Please select a source before attempting to load.");
    }

    /**
     * shows status
     */
    public void status() { }

    /**
     * trigger pause
     */
    public void playPause() { }

    /**
     * shows pause status
     */
    public boolean paused() {
        return false;
    }

    /**
     * shows shuffle status
     */
    public boolean shuffles() {
        return false;
    }

    /**
     * trigger repeat
     */
    public void repeat() {
        Manager.getPartialResult().put("message",
                "Please load a source before setting the repeat status.");
    }

    /**
     * trigger shuffle
     */
    public void shuffle(final int seed) {
        Manager.getPartialResult().put("message",
                "Please load a source before using the shuffle function.");
    }

    /**
     * play next song
     */
    public void next() {
        Manager.getPartialResult().put("message",
                "Please load a source before skipping to the next track.");
    }

    /**
     * play previous song
     */
    public void prev() {
        Manager.getPartialResult().put("message",
                "Please load a source before returning to the previous track.");
    }

    /**
     * skip forward
     */
    public void forward() {
        Manager.getPartialResult().put("message",
                "Please load a source before skipping forward.");
    }

    /**
     * go backwards
     */
    public void backward() {
        Manager.getPartialResult().put("message", "Please select a source before rewinding.");
    }

    /**
     * changes pause status
     */
    public void pauseButton() { }

    /**
     * changes shuffle status
     */
    public void shuffleButton() { }

    /**
     * changes repeat status
     */
    public void repeatButton() { }

    /**
     * update song duration
     */
    public void updateDuration(final int deltaTime) { }

    /**
     * update the player
     */
    public void updatePlayer() { }
}
