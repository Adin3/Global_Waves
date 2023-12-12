package program.player;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.Getter;
import program.Manager;
import program.format.Album;
import program.format.Playlist;
import program.format.Song;

import java.util.ArrayList;
import java.util.Random;

public class AlbumPlayer extends Player {
    public AlbumPlayer() { }

    @Getter
    private Album album;

    @Getter
    private Song currentSong;

    private String repeat = "No Repeat";

    private boolean paused = false;

    private boolean shuffled = false;

    private int savedTime;

    private int albumPosition;

    private int previousPlaylistPosition = 0;
    private int[] shuffledIndexes;

    private ArrayList<Song> unshuffledalbum = new ArrayList<>();

    public AlbumPlayer(final String owner) {
        this.owner = owner;
    }

    private String owner;
    /**
     * clear player
     */
    public void clearPlayer() {
        currentSong = null;
    }

    public Song getSong() {
        return currentSong;
    }
    /**
     * load song
     */
    public void load() {
        Album temp = Manager.searchBar(owner).getAlbumLoaded();

        if (temp == null || !Manager.getSource(owner).isSourceSelected()) {
            Manager.getPartialResult().put("message",
                    "Please select a source before attempting to load.");
            return;
        }

        this.album = new Album(temp);
        String user = this.album.getOwner();
        Manager.getUser(user).addReference();

        if (this.album.getSongs().isEmpty()) {
            Manager.getPartialResult().put("message",
                    "You can't load an empty audio collection!");
            return;
        }

        Manager.getPartialResult().put("message",
                "Playback loaded successfully.");
        currentSong = new Song(album.getSong(0));
        currentSong.setDuration(currentSong.getMaxDuration());
        albumPosition = 0;
        previousPlaylistPosition = 0;
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

        if (!Manager.getSource(owner).isSourceLoaded() || album == null || currentSong == null) {
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
                for (int i = 0; i < album.getSongs().size(); i++) {
                    album.setSong(unshuffledalbum.get(i), i);
                }

                for (int i = 0; i < album.getSongs().size(); i++) {
                    if (album.getSongs().get(i).getName().equals(currentSong.getName())) {
                        albumPosition = i;
                    }
                }
            }
        }
    }

    /**
     * shuffle the album
     */
    private void createShuffledVec(final int seed) {
        final Random rand = new Random(seed);
        rand.setSeed(seed);
        unshuffledalbum.addAll(album.getSongs());

        shuffledIndexes = new int[album.getSongs().size()];

        for (int i = 0; i < album.getSongs().size(); i++) {
            shuffledIndexes[i] = i;
        }

        for (int i = shuffledIndexes.length - 1; i >= 0; i--) {
            int index = rand.nextInt(i + 1);
            int temp = shuffledIndexes[index];
            shuffledIndexes[index] = shuffledIndexes[i];
            shuffledIndexes[i] = temp;
        }


        for (int i = 0; i < album.getSongs().size(); i++) {
            album.setSong(unshuffledalbum.get(shuffledIndexes[i]), i);
        }

        for (int i = 0; i < album.getSongs().size(); i++) {
            if (album.getSongs().get(i).getName().equals(currentSong.getName())) {
                albumPosition = i;
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
            return;
        }

        boolean end = albumPosition + 1 >= album.getSongs().size();
        if (repeat.equals("Repeat All")) {
            previousPlaylistPosition = albumPosition;
            if (end) {
                albumPosition = 0;
            } else {
                albumPosition++;
            }

            currentSong.setDuration(currentSong.getMaxDuration());
            currentSong = new Song(album.getSong(albumPosition));
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
        previousPlaylistPosition = albumPosition;
        albumPosition++;
        currentSong.setDuration(currentSong.getMaxDuration());
        currentSong = new Song(album.getSong(albumPosition));
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

        if (repeat.equals("Repeat Current Song")) {
            currentSong.setDuration(currentSong.getMaxDuration());
            Manager.getPartialResult().put("message",
                    "Returned to previous track successfully."
                            + " The current track is " + currentSong.getName() + ".");
            paused = false;
            return;
        }

        if (albumPosition <= 0) {
            albumPosition++;
        }

        albumPosition--;
        currentSong.setDuration(currentSong.getMaxDuration());
        currentSong = new Song(album.getSong(albumPosition));
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
            albumPosition++;
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
                if (albumPosition >= album.getSongs().size() && repeat.equals("No Repeat")) {
                    currentSong = null;
                    paused = true;
                    shuffled = false;
                    repeat = "No Repeat";
                    Manager.getSource(owner).setSourceLoaded(false);
                    break;
                }

                if (albumPosition >= album.getSongs().size() && repeat.equals("Repeat All")) {
                    albumPosition = 0;
                } else if (repeat.equals("Repeat Current Song")) {
                    albumPosition--;
                }

                currentSong.setDuration(currentSong.getMaxDuration());
                currentSong = new Song(album.getSong(albumPosition));
                currentSong.setDuration(currentSong.getMaxDuration());
                updateDuration(savedTime);
            }

        }
    }
}
