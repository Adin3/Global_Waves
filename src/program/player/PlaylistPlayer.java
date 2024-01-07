package program.player;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.Getter;
import program.admin.Manager;
import program.format.Playlist;
import program.format.Song;

import java.util.ArrayList;
import java.util.Random;

public class PlaylistPlayer extends Player {
    public PlaylistPlayer() { }

    @Getter
    private Playlist playlist;

    @Getter
    private Song currentSong;

    private String repeat = "No Repeat";

    private boolean paused = false;

    private boolean shuffled = false;

    private int savedTime;

    private int playlistPosition;
    private int[] shuffledIndexes;

    private ArrayList<Song> unshuffledplaylist = new ArrayList<>();

    public PlaylistPlayer(final String owner) {
        this.owner = owner;
    }

    private String owner;
    /**
     * clear player
     */
    public void clearPlayer() {
        currentSong = null;
    }

    /**
     * load song
     */
    public void load() {
        Playlist temp = Manager.searchBar(owner).getPlaylistLoaded();

        if (temp == null || !Manager.getSource(owner).isSourceSelected()) {
            Manager.getPartialResult().put("message",
                    "Please select a source before attempting to load.");
            return;
        }

        this.playlist = new Playlist(temp);

        if (this.playlist.getSongs().isEmpty()) {
            Manager.getPartialResult().put("message",
                    "You can't load an empty audio collection!");
            return;
        }

        Manager.getPartialResult().put("message",
                "Playback loaded successfully.");
        currentSong = new Song(playlist.getSong(0));
        currentSong.setDuration(currentSong.getMaxDuration());
        Manager.getUser(owner).setListenedSong(currentSong);
        Manager.getUser(currentSong.getArtist()).setListenedSong(currentSong, owner);
        playlistPosition = 0;
    }

    /**
     * load playlist
     * @param playlistLoaded the playlist that will be loaded
     */
    public void load(final Playlist playlistLoaded) {
        Playlist temp = playlistLoaded;

        if (temp == null) {
            Manager.getPartialResult().put("message",
                    "Please select a source before attempting to load.");
            return;
        }

        this.playlist = new Playlist(playlist);

        if (this.playlist.getSongs().isEmpty()) {
            Manager.getPartialResult().put("message",
                    "You can't load an empty audio collection!");
            return;
        }

        Manager.getPartialResult().put("message",
                "Playback loaded successfully.");
        currentSong = new Song(playlist.getSong(0));
        currentSong.setDuration(currentSong.getMaxDuration());
        Manager.getUser(owner).setListenedSong(currentSong);
        Manager.getUser(currentSong.getArtist()).setListenedSong(currentSong, owner);
        playlistPosition = 0;
    }

    /**
     * change repeat status
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
     * changes shuffle status
     */
    public void shuffle(final int seed) {

        if (!Manager.getSource(owner).isSourceLoaded() || playlist == null || currentSong == null) {
            Manager.getPartialResult().put("message",
                    "Please load a source before using the shuffle function.");
            return;
        }

        shuffleButton();

        if (shuffled) {
            Manager.getPartialResult().put("message",
                    "Shuffle function activated successfully.");
            createShuffledVec(seed);
        } else {
            Manager.getPartialResult().put("message",
                    "Shuffle function deactivated successfully.");
            if (currentSong != null) {
                for (int i = 0; i < playlist.getSongs().size(); i++) {
                    playlist.setSong(unshuffledplaylist.get(i), i);
                }

                for (int i = 0; i < playlist.getSongs().size(); i++) {
                    if (playlist.getSongs().get(i).getName().equals(currentSong.getName())) {
                        playlistPosition = i;
                    }
                }
            }
        }
    }

    /**
     * shuffle the playlist
     */
    private void createShuffledVec(final int seed) {
        final Random rand = new Random(seed);
        rand.setSeed(seed);
        unshuffledplaylist.addAll(playlist.getSongs());

        shuffledIndexes = new int[playlist.getSongs().size()];

        for (int i = 0; i < playlist.getSongs().size(); i++) {
            shuffledIndexes[i] = i;
        }

        for (int i = shuffledIndexes.length - 1; i >= 0; i--) {
            int index = rand.nextInt(i + 1);
            int temp = shuffledIndexes[index];
            shuffledIndexes[index] = shuffledIndexes[i];
            shuffledIndexes[i] = temp;
        }


        for (int i = 0; i < playlist.getSongs().size(); i++) {
            playlist.setSong(unshuffledplaylist.get(shuffledIndexes[i]), i);
        }

        for (int i = 0; i < playlist.getSongs().size(); i++) {
            if (playlist.getSongs().get(i).getName().equals(currentSong.getName())) {
                playlistPosition = i;
            }
        }
    }

    /**
     * play next song
     */
    public void next() {
        if (!Manager.getSource(owner).isSourceLoaded() || currentSong == null) {
            Manager.getPartialResult().put("message",
                    "Please load a source before skipping to the next track.");
            return;
        }

        if (repeat.equals("Repeat Current Song")) {
            currentSong.setDuration(currentSong.getMaxDuration());
            Manager.getPartialResult().put("message",
                    "Skipped to next track successfully."
                            + " The current track is " + currentSong.getName() + ".");
            paused = false;
            return;
        }

        boolean end = playlistPosition + 1 >= playlist.getSongs().size();
        if (repeat.equals("Repeat All")) {
            if (end) {
                playlistPosition = 0;
            } else {
                playlistPosition++;
            }

            currentSong.setDuration(currentSong.getMaxDuration());
            currentSong = new Song(playlist.getSong(playlistPosition));
            currentSong.setDuration(currentSong.getMaxDuration());
            Manager.getPartialResult().put("message",
                    "Skipped to next track successfully."
                            + " The current track is " + currentSong.getName() + ".");
            paused = false;
            return;
        }

        if (end) {
            Manager.getPartialResult().put("message",
                    "Please load a source before skipping to the next track.");
            currentSong = null;
            paused = true;
            Manager.getSource(owner).setSourceLoaded(false);
            return;
        }
        playlistPosition++;
        currentSong.setDuration(currentSong.getMaxDuration());
        currentSong = new Song(playlist.getSong(playlistPosition));
        currentSong.setDuration(currentSong.getMaxDuration());
        Manager.getPartialResult().put("message",
                "Skipped to next track successfully."
                        + " The current track is " + currentSong.getName() + ".");
        paused = false;
    }

    /**
     * play previous song
     */
    public void prev() {
        if (!Manager.getSource(owner).isSourceLoaded() || currentSong == null) {
            Manager.getPartialResult().put("message",
                    "Please load a source before returning to the previous track.");
            return;
        }

        if (!currentSong.getDuration().equals(currentSong.getMaxDuration())) {
            currentSong.setDuration(currentSong.getMaxDuration());
            Manager.getPartialResult().put("message",
                    "Returned to previous track successfully."
                            + " The current track is " + currentSong.getName() + ".");
            paused = false;
            return;
        }

        if (playlistPosition <= 0) {
            playlistPosition++;
        }

        playlistPosition--;
        currentSong.setDuration(currentSong.getMaxDuration());
        currentSong = new Song(playlist.getSong(playlistPosition));
        currentSong.setDuration(currentSong.getMaxDuration());
        Manager.getPartialResult().put("message",
                "Returned to previous track successfully."
                        + " The current track is " + currentSong.getName() + ".");
        paused = false;
    }

    /**
     * skip forward
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
     * go backwards
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
     * shows status
     */
    public void status() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode status = objectMapper.createObjectNode();
        if (currentSong == null) {
            status.put("name", "");
            status.put("remainedTime", 0);
            status.put("repeat", "No Repeat");
            status.put("shuffle", false);
            status.put("paused", true);
            Manager.getSource(owner).setSourceLoaded(false);
        } else {
            status.put("name", currentSong.getName());
            status.put("remainedTime", currentSong.getDuration());
            status.put("repeat", repeats());
            status.put("shuffle", shuffles());
            status.put("paused", paused());
        }
        Manager.getPartialResult().set("stats", status);
    }

    /**
     * changes pause status
     */
    public void playPause() {

        if (!Manager.getSource(owner).isSourceLoaded() || currentSong == null) {
            Manager.getPartialResult().put("message",
                    "Please load a source before attempting to pause or resume playback.");
            return;
        }
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
     * changes shuffle status
     */
    public boolean shuffles() {
        return shuffled;
    }

    /**
     * changes repeat status
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
                repeat = "Repeat All";
                repeatState = 1;
                break;
            case 1:
                repeat = "Repeat Current Song";
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
     * update song duration
     */
    public void updateDuration(final int deltaTime) {
        if (currentSong == null) {
            return;
        }

        if (paused) {
            return;
        }

        int time = currentSong.getDuration() - deltaTime;
        savedTime = 0;
        if (time < 0) {
            playlistPosition++;
            savedTime = -time;
            time = 0;
        }
        currentSong.setDuration(time);
    }
    /**
     * update player
     */
    public void updatePlayer() {
        if (currentSong == null) {
            return;
        }

        if (currentSong.getDuration() == 0) {
            while (savedTime != 0) {
                if (playlistPosition >= playlist.getSongs().size() && repeat.equals("No Repeat")) {
                    currentSong = null;
                    paused = true;
                    shuffled = false;
                    repeat = "No Repeat";
                    Manager.getSource(owner).setSourceLoaded(false);
                    break;
                }

                if (playlistPosition >= playlist.getSongs().size() && repeat.equals("Repeat All")) {
                    playlistPosition = 0;
                } else if (repeat.equals("Repeat Current Song")) {
                    playlistPosition--;
                }

                currentSong.setDuration(currentSong.getMaxDuration());
                currentSong = new Song(playlist.getSong(playlistPosition));
                currentSong.setDuration(currentSong.getMaxDuration());
                Manager.getUser(owner).setListenedSong(currentSong);
                Manager.getUser(currentSong.getArtist()).setListenedSong(currentSong, owner);
                updateDuration(savedTime);
            }

        }
    }
}
