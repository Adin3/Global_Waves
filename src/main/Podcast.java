package main;

import fileio.input.EpisodeInput;
import fileio.input.PodcastInput;

import java.util.ArrayList;

public class Podcast {
    private String name;
    private String owner;
    private ArrayList<Episode> episodes = new ArrayList<>();

    public Podcast(PodcastInput podcast) {
        this.name = podcast.getName();
        this.owner = podcast.getOwner();
        for (EpisodeInput episode : podcast.getEpisodes()) {
            episodes.add(new Episode(episode));
        }
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(final String owner) {
        this.owner = owner;
    }

    public ArrayList<Episode> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(final ArrayList<Episode> episodes) {
        this.episodes = episodes;
    }
}
