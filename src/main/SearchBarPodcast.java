package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import fileio.input.LibraryInput;
import fileio.input.PodcastInput;
import fileio.input.SongInput;
import lombok.Getter;

import java.util.ArrayList;

public class SearchBarPodcast extends SearchBar {

    @Getter
    private final ArrayList<PodcastInput> podcast = new ArrayList<>();

    @Getter
    private PodcastInput podcastLoaded;

    @Getter
    private SongInput songLoaded;

    @Getter
    private Playlist playlistLoaded;

    public SearchBarPodcast() {}

    public void clearSearch() {
        sourceSearched = false;
        sourceSelected = false;
    }

    public void select(int number) {
        if (!podcast.isEmpty()) {
            if (number <= podcast.size()) {
                podcastLoaded = podcast.get((number-1));
                sourceSelected = true;
                sourceSearched = false;
                Manager.partialResult.put("message", "Successfully selected " + podcastLoaded.getName() + ".");
                return;
            }
            Manager.partialResult.put("message", "The selected ID is too high.");
            return;
        }
        Manager.partialResult.put("message", "Please conduct a search before making a selection.");
    }

    public void searchDone() {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode node = objectMapper.createArrayNode();

        int size = podcast.size();

        Manager.partialResult.put("message", "Search returned " +
                size + " results");
        for (PodcastInput sg : podcast)
            node.add(sg.getName());

        Manager.partialResult.set("results", node);
        sourceSearched = true;
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
