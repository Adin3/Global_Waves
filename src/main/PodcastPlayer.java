package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.EpisodeInput;
import fileio.input.PodcastInput;
import fileio.input.SongInput;
import lombok.Getter;

public class PodcastPlayer extends Player {
    public PodcastPlayer() {}

    @Getter
    private PodcastInput podcast;

    private EpisodeInput currentEpisode;

    private String repeat = "No Repeat";

    private boolean paused = false;

    private boolean shuffled = false;

    private int savedTime;

    private int podcastPosition;

    public PodcastPlayer(String owner) {
        this.owner = owner;
    }

    private String owner;

    public void clearPlayer() {
        currentEpisode = null;
    }

    public void load() {
        this.podcast = Manager.searchBar(owner).getPodcastLoaded();

        if (this.podcast == null && !Manager.searchBar(owner).sourceSelected) {
            Manager.partialResult.put("message", "Please select a source before attempting to load.");
            return;
        }

        if (this.podcast.getEpisodes().isEmpty()) {
            Manager.partialResult.put("message", "You can't load an empty audio collection!");
            return;
        }

        Manager.partialResult.put("message", "Playback loaded successfully.");
        currentEpisode = podcast.getEpisodes().get(0);
        currentEpisode.setMaxDuration(currentEpisode.getDuration());
        podcastPosition = 0;
    }

    public void status() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode status = objectMapper.createObjectNode();
        if ( currentEpisode == null) {
            status.put("name", "");
            status.put("remainedTime", 0);
            status.put("repeat", "No Repeat");
            status.put("shuffle", false);
            status.put("paused", true);
        } else {
            status.put("name", currentEpisode.getName());
            status.put("remainedTime", currentEpisode.getDuration());
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
        if (repeat.startsWith("No")) {
            repeat = "Repeat";
        } else {
            repeat = "No Repeat";
        }
    }

    public void updateDuration(int deltaTime) {
        if (currentEpisode == null) return;

        int time = currentEpisode.getDuration() - deltaTime;
        savedTime = 0;
        if (time < 0) {
            podcastPosition++;
            savedTime = -time;
            time = 0;
        }
        if (!paused)
            currentEpisode.setDuration(time);
    }
    public void updatePlayer() {
        if (currentEpisode == null) return;

        if (currentEpisode.getDuration() == 0) {
            while (savedTime != 0) {
                if (podcastPosition >= podcast.getEpisodes().size()) {
                    currentEpisode = null;
                    paused = true;
                    shuffled = false;
                    repeat = "No Repeat";
                    break;
                }

                currentEpisode.setDuration(currentEpisode.getMaxDuration());
                currentEpisode = podcast.getEpisodes().get(podcastPosition);
                currentEpisode.setMaxDuration(currentEpisode.getDuration());
                updateDuration(savedTime);
            }

        }
    }
}
