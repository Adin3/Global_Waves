package program.player;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.Getter;
import program.format.Episode;
import program.admin.Manager;
import program.format.Podcast;

public class PodcastPlayer extends Player {
    public PodcastPlayer() { }

    private static final int SKIP90 = 90;

    @Getter
    private Podcast podcast;

    private Episode currentEpisode;

    private String repeat = "No Repeat";

    private boolean paused = false;

    private boolean shuffled = false;

    private boolean repeatOnce = false;

    private int savedTime;

    private int podcastPosition;

    public PodcastPlayer(final String owner) {
        this.owner = owner;
    }

    private String owner;

    /**
     * clear player
     */
    public void clearPlayer() {
        currentEpisode = null;
    }

    /**
     * load song
     */
    public void load() {
        Podcast temp = Manager.searchBar(owner).getPodcastLoaded();

        if (temp == null || !Manager.getSource(owner).isSourceSelected()) {
            Manager.getPartialResult().put("message",
                    "Please select a source before attempting to load.");
            return;
        }

        this.podcast = temp;

        if (this.podcast.getEpisodes().isEmpty()) {
            Manager.getPartialResult().put("message",
                    "You can't load an empty audio collection!");
            return;
        }

        Manager.getPartialResult().put("message",
                "Playback loaded successfully.");
        currentEpisode = podcast.getEpisodes().get(0);
        currentEpisode.setMaxDuration(currentEpisode.getDuration());
        podcastPosition = 0;
    }

    /**
     * changes repeat status
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
        if (!Manager.getSource(owner).isSourceLoaded()) {
            Manager.getPartialResult().put("message",
                    "Please load a source before using the shuffle function.");
            return;
        }
        Manager.getPartialResult().put("message",
                "The loaded source is not a playlist or an album.");
    }

    /**
     * play next song
     */
    public void next() {
        if (!Manager.getSource(owner).isSourceLoaded()) {
            Manager.getPartialResult().put("message",
                    "Please load a source before skipping to the next track.");
            return;
        }

        boolean end = podcastPosition >= podcast.getEpisodes().size();
        if (!repeat.equals("No Repeat")) {
            if (end) {
                podcastPosition = 0;
            } else {
                podcastPosition++;
            }

            currentEpisode.setDuration(currentEpisode.getMaxDuration());
            currentEpisode = podcast.getEpisodes().get(podcastPosition);
            currentEpisode.setDuration(currentEpisode.getMaxDuration());
            Manager.getPartialResult().put("message",
                    "Skipped to next track successfully."
                            + " The current track is " + currentEpisode.getName() + ".");
            return;
        }

        if (end) {
            currentEpisode = null;
            return;
        }

        podcastPosition++;
        currentEpisode.setDuration(currentEpisode.getMaxDuration());
        currentEpisode = podcast.getEpisodes().get(podcastPosition);
        currentEpisode.setDuration(currentEpisode.getMaxDuration());
        Manager.getPartialResult().put("message",
                "Skipped to next track successfully."
                        + " The current track is " + currentEpisode.getName() + ".");
    }

    /**
     * play previous song
     */
    public void prev() {
        if (!Manager.getSource(owner).isSourceLoaded()) {
            Manager.getPartialResult().put("message",
                    "Please load a source before returning to the previous track.");
            return;
        }

        if (!currentEpisode.getDuration().equals(currentEpisode.getMaxDuration())) {
            currentEpisode.setDuration(currentEpisode.getMaxDuration());
            Manager.getPartialResult().put("message",
                    "Returned to previous track successfully."
                            + " The current track is " + currentEpisode.getName() + ".");
            return;
        }

        if (podcastPosition <= 0) {
            podcastPosition++;
        }

        podcastPosition--;
        currentEpisode.setDuration(currentEpisode.getMaxDuration());
        currentEpisode = podcast.getEpisodes().get(podcastPosition);
        currentEpisode.setDuration(currentEpisode.getMaxDuration());
        Manager.getPartialResult().put("message",
                "Returned to previous track successfully."
                        + " The current track is " + currentEpisode.getName() + ".");
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
        int time = currentEpisode.getDuration() - SKIP90;

        if (time <= 0) {
            next();
        }

        currentEpisode.setDuration(time);
        Manager.getPartialResult().put("message",
                "Skipped forward successfully.");
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
        int time = currentEpisode.getDuration() + SKIP90;

        currentEpisode.setDuration(time);

        if (time >= currentEpisode.getMaxDuration()) {
            currentEpisode.setDuration(currentEpisode.getMaxDuration());
        }

        Manager.getPartialResult().put("message",
                "Rewound successfully.");
    }

    /**
     * shows status
     */
    public void status() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode status = objectMapper.createObjectNode();
        if (currentEpisode == null) {
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
        Manager.getPartialResult().set("stats", status);
    }

    /**
     * changes pause status
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
     * trigger pause
     */
    public void pauseButton() {
        paused = !paused;
    }

    /**
     * trigger shuffle
     */
    public void shuffleButton() {
        shuffled = !shuffled;
    }

    /**
     * trigger repeat
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
     * update the song duration
     */
    public void updateDuration(final int deltaTime) {
        if (currentEpisode == null) {
            return;
        }

        if (paused) {
            return;
        }

        int time = currentEpisode.getDuration() - deltaTime;
        savedTime = 0;
        if (time < 0) {
            podcastPosition++;
            savedTime = -time;
            time = 0;
        }
        currentEpisode.setDuration(time);
    }
    /**
     * update the player
     */
    public void updatePlayer() {
        if (currentEpisode == null) {
            return;
        }

        if (currentEpisode.getDuration() == 0) {
            while (savedTime != 0) {
                boolean end = podcastPosition >= podcast.getEpisodes().size();
                if (end && repeat.equals("No Repeat")) {
                    currentEpisode = null;
                    paused = true;
                    shuffled = false;
                    repeat = "No Repeat";
                    break;
                }

                if (end && repeat.equals("Repeat Once")) {
                    repeat = "No Repeat";
                    repeatState = 0;
                    podcastPosition = 0;
                }

                if (end && repeat.equals("Repeat Infinite")) {
                    podcastPosition = 0;
                }

                currentEpisode.setDuration(currentEpisode.getMaxDuration());
                currentEpisode = podcast.getEpisodes().get(podcastPosition);
                currentEpisode.setMaxDuration(currentEpisode.getDuration());
                updateDuration(savedTime);
            }

        }
    }
}
