package program.player;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import program.admin.Manager;
import program.format.Song;


public final class MusicPlayer extends Player {

    public MusicPlayer() { }

    @Getter
    private Song song;

    private String repeat = "No Repeat";

    private boolean paused = false;

    private boolean shuffled = false;

    private int savedTime = 0;

    private boolean repeatOnce = false;

    public MusicPlayer(final String owner) {
        this.owner = owner;
    }

    private String owner;

    /**
     * clear player
     */
    public void clearPlayer() {
        song = null;
    }

    /**
     * load song in music player
     */
    public void load() {

        if (!Manager.getSource(owner).isSourceSelected()) {
            Manager.getPartialResult().put("message",
                    "Please select a source before attempting to load.");
            return;
        }

        this.song = Manager.searchBar(owner).getSongLoaded();
        if (this.song != null) {
            Manager.getPartialResult().put("message",
                    "Playback loaded successfully.");
            song.setDuration(song.getMaxDuration());
        } else {
            Manager.getPartialResult().put("message",
                    "You can't load an empty audio collection!");
        }
    }

    /**
     * shows status
     */
    public void status() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode status = objectMapper.createObjectNode();
        if (song == null) {
            status.put("name", "");
            status.put("remainedTime", 0);
            status.put("repeat", "No Repeat");
            status.put("shuffle", false);
            status.put("paused", true);
            Manager.getSource(owner).setSourceLoaded(false);
        } else {
            status.put("name", song.getName());
            status.put("remainedTime", song.getDuration());
            status.put("repeat", repeats());
            status.put("shuffle", shuffles());
            status.put("paused", paused());
        }
        Manager.getPartialResult().set("stats", status);
    }

    /**
     * trigger repeat
     */
    public void repeat() {

        if (!Manager.getSource(owner).isSourceLoaded()) {
            Manager.getPartialResult().put("message",
                    "Please load a source before setting the repeat status.");
            return;
        }

        repeatButton();

        Manager.getPartialResult().put("message",
                "Repeat mode changed to " + repeat.toLowerCase() + ".");
    }

    /**
     * trigger shuffle
     */
    public void shuffle(final int seed) {
        if (!Manager.getSource(owner).isSourceLoaded()) {
            Manager.getPartialResult().put("message",
                    "Please load a source before using the shuffle function.");
            return;
        }
        Manager.getPartialResult().put("message",
                "The loaded source is not a playlist or an album.");
    }

    /**
     * play the next song
     */
    public void next() {
        if (!Manager.getSource(owner).isSourceLoaded()) {
            Manager.getPartialResult().put("message",
                    "Please load a source before skipping to the next track.");
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
        Manager.getPartialResult().put("message",
                "Skipped to next track successfully. The current track is " + song.getName() + ".");
    }

    /**
     * play the previous song
     */
    public void prev() {

        if (!Manager.getSource(owner).isSourceLoaded()) {
            Manager.getPartialResult().put("message",
                    "Please load a source before returning to the previous track.");
            return;
        }

        if (!song.getDuration().equals(song.getMaxDuration())) {
            song.setDuration(song.getMaxDuration());
            Manager.getPartialResult().put("message",
                    "Returned to previous track successfully."
                            + " The current track is " + song.getName() + ".");
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
        Manager.getPartialResult().put("message",
                "Returned to previous track successfully."
                        + " The current track is " + song.getName() + ".");
    }

    /**
     * skip forward for 90 seconds
     */
    public void forward() {
        if (!Manager.getSource(owner).isSourceLoaded()) {
            Manager.getPartialResult().put("message",
                    "Please load a source before attempting to forward.");
            return;
        }
        Manager.getPartialResult().put("message",
                "The loaded source is not a podcast.");
    }

    /**
     * go backwards for 90 seconds
     */
    public void backward() {
        if (!Manager.getSource(owner).isSourceLoaded()) {
            Manager.getPartialResult().put("message",
                    "Please load a source before attempting to backward.");
            return;
        }
        Manager.getPartialResult().put("message",
                "The loaded source is not a podcast.");
    }

    /**
     * trigger pause
     */
    public void playPause() {
        Manager.musicPlayer(owner).pauseButton();

        if (Manager.musicPlayer(owner).paused()) {
            Manager.getPartialResult().put("message",
                    "Playback paused successfully.");
        } else {
            Manager.getPartialResult().put("message",
                    "Playback resumed successfully.");
        }
    }
    /**
     * returns pause status
     */
    public boolean paused() {
        return paused;
    }

    /**
     * returns shuffle status
     */
    public boolean shuffles() {
        return shuffled;
    }

    /**
     * returns repeat status
     */
    public String repeats() {
        return repeat;
    }

    /**
     * changes pause status
     */
    public void pauseButton() {
        paused = !paused;
    }

    /**
     * changes shuffle status
     */
    public void shuffleButton() {
        shuffled = !shuffled;
    }

    /**
     * changes repeat status
     */
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
            default:
                break;
        }
    }

    /**
     * update the duration of the current song
     * @param deltaTime the variation of time
     */
    public void updateDuration(final int deltaTime) {
        if (song == null) {
            return;
        }

        if (paused) {
            return;
        }

        int time = song.getDuration() - deltaTime;
        savedTime = 0;
        if (time < 0) {
            savedTime = -time;
            time = 0;
        }
        song.setDuration(time);
    }
    /**
     * updates the player
     */
    public void updatePlayer() {
        if (song == null) {
            return;
        }

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
