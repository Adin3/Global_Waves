package main;

import fileio.input.LibraryInput;
import fileio.input.PodcastInput;
import fileio.input.SongInput;
import lombok.Getter;

import java.util.ArrayList;

public class SearchBarPodcast extends SearchBar{
    private Filters filter;

    @Getter
    private final ArrayList<SongInput> song = new ArrayList<>();

    @Getter
    private final ArrayList<PodcastInput> podcast = new ArrayList<>();

    @Getter
    private SongInput songLoaded;

    @Getter
    private PodcastInput podcastLoaded;
    public SearchBarPodcast() {}

    public void clearSearch() {

    }

    public int select(int number) {
        if (podcast != null) {
            if (number <= podcast.size()) {
                podcastLoaded = podcast.get((number-1));
                return 0;
            }
            return 1;
        }
        return 2;
    }
    public void search(Filters filter) {
        if (filter.getName() != null) {
            podcast.addAll(SearchPodcastByName(filter.getName()));
        }
        if (filter.getOwner() != null) {
            podcast.addAll(SearchPodcastByOwner(filter.getOwner()));
        }

        while (podcast.size() > 5) {
            podcast.remove(podcast.size()-1);
        }
    }
    private ArrayList<PodcastInput> SearchPodcastByName(String name) {

        ArrayList<PodcastInput> podcasts = new ArrayList<>();
        for (PodcastInput podcast : LibraryInput.getInstance().getPodcasts()) {
            if (podcast.getName().startsWith(name)) {
                podcasts.add(podcast);
            }
        }
        return podcasts;
    }

    private ArrayList<PodcastInput> SearchPodcastByOwner(String owner) {

        ArrayList<PodcastInput> podcasts = new ArrayList<>();
        for (PodcastInput podcast : LibraryInput.getInstance().getPodcasts()) {
            if (podcast.getOwner().equals(owner)) {
                podcasts.add(podcast);
            }
        }
        return podcasts;
    }
}
