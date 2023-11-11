package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.SongInput;
import lombok.Getter;

public class PlaylistPlayer extends Player{
    public PlaylistPlayer() {}

    @Getter
    private Playlist playlist;

    private SongInput currentSong;

    private String repeat = "No Repeat";

    private boolean paused = false;

    private boolean shuffled = false;

    private int savedTime;

    private int playlistPosition;

    public PlaylistPlayer(String owner) {
        this.owner = owner;
    }

    private String owner;

    public void clearPlayer() {
        currentSong = null;
    }

    public void load() {
        Playlist temp = Manager.searchBar(owner).getPlaylistLoaded();

        if (temp == null || !Manager.getSource(owner).isSourceSelected()) {
            Manager.partialResult.put("message", "Please select a source before attempting to load.");
            return;
        }

        this.playlist = temp;

        if (this.playlist.getSongs().isEmpty()) {
            Manager.partialResult.put("message", "You can't load an empty audio collection!");
            return;
        }

        Manager.partialResult.put("message", "Playback loaded successfully.");
        currentSong = playlist.getSong(0);
        currentSong.setDuration(currentSong.getMaxDuration());
        playlistPosition = 0;
    }

    public void repeat() {

        if (!Manager.getSource(owner).isSourceLoaded()) {
            Manager.partialResult.put("message", "Please load a source before setting the repeat status.");
            return;
        }

        repeatButton();

        Manager.partialResult.put("message", "Repeat mode changed to " + repeat.toLowerCase() + ".");
    }

    public void status() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode status = objectMapper.createObjectNode();
        if ( currentSong == null) {
            status.put("name", "");
            status.put("remainedTime", 0);
            status.put("repeat", "No Repeat");
            status.put("shuffle", false);
            status.put("paused", true);
        } else {
            status.put("name", currentSong.getName());
            status.put("remainedTime", currentSong.getDuration());
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
        }
    }

    public void updateDuration(int deltaTime) {
        if (currentSong == null) return;

        int time = currentSong.getDuration() - deltaTime;
        savedTime = 0;
        if (time < 0) {
            playlistPosition++;
            savedTime = -time;
            time = 0;
        }
        if (!paused)
            currentSong.setDuration(time);
    }
    public void updatePlayer() {
        if (currentSong == null) return;

        if (currentSong.getDuration() == 0) {
            while (savedTime != 0) {
                if (playlistPosition >= playlist.getSongs().size() && repeat.equals("No Repeat")) {
                    currentSong = null;
                    paused = true;
                    shuffled = false;
                    repeat = "No Repeat";
                    break;
                }

                if (playlistPosition >= playlist.getSongs().size() && repeat.equals("Repeat All")) {
                    playlistPosition = 0;
                } else if (repeat.equals("Repeat Current Song")) {
                    playlistPosition--;
                }

                currentSong.setDuration(currentSong.getMaxDuration());
                currentSong = playlist.getSong(playlistPosition);
                currentSong.setDuration(currentSong.getMaxDuration());
                updateDuration(savedTime);
            }

        }
    }
}
