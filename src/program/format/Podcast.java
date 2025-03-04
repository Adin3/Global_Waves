package program.format;

import fileio.input.EpisodeInput;
import fileio.input.PodcastInput;

import java.util.ArrayList;

public final class Podcast {
    private String name;
    private String owner;
    private ArrayList<Episode> episodes = new ArrayList<>();

    public Podcast(final PodcastInput podcast) {
        this.name = podcast.getName();
        this.owner = podcast.getOwner();
        for (EpisodeInput episode : podcast.getEpisodes()) {
            episodes.add(new Episode(episode));
        }
    }

    public Podcast(final String name, final String owner, final ArrayList<Episode> episodes) {
        this.name = name;
        this.owner = owner;
        this.episodes.addAll(episodes);
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

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(name);
        result.append(":\n\t[");
        for (int i = 0; i < getEpisodes().size(); i++) {
            result.append(getEpisodes().get(i));
            if (i < getEpisodes().size() - 1) {
                result.append(", ");
            }
        }
        result.append("]\n");
        return result.toString();
    }
}
