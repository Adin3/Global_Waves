package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;

public class SearchBarPodcast extends SearchBar {

    @Getter
    private final ArrayList<Podcast> podcast = new ArrayList<>();

    @Getter
    private Podcast podcastLoaded;

    @Getter
    private Song songLoaded;

    @Getter
    private Playlist playlistLoaded;

    @Getter
    private final String owner;

    public SearchBarPodcast(String owner) {
        this.owner = owner;
    }

    public void clearSearch() {
        sourceSearched = false;
        sourceSelected = false;
    }

    public void select(int number, String username) {
        if (!podcast.isEmpty() && Manager.getSource(username).isSourceSearched()) {
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
        for (Podcast sg : podcast)
            node.add(sg.getName());

        Manager.partialResult.set("results", node);
        sourceSearched = true;
    }

    public void search(Filters filter) {
        int options = 0;
        if (filter.getName() != null) {
            options++;
            podcast.addAll(SearchPodcastByName(filter.getName()));
        }
        if (filter.getOwner() != null) {
            options++;
            podcast.addAll(SearchPodcastByOwner(filter.getOwner()));
        }

        if (options > 1) {
            ArrayList<Podcast> temp = new ArrayList<>();

            for(Podcast p : podcast)
                if(Collections.frequency(podcast, p) > 1)
                    temp.add(p);

            podcast.clear();
            podcast.addAll(temp);
            temp.clear();

            for (Podcast p : podcast) {
                if (!temp.contains(p)) {
                    temp.add(p);
                }
            }

            podcast.clear();
            podcast.addAll(temp);
        }

        while (podcast.size() > 5) {
            podcast.remove(podcast.size()-1);
        }
    }
    private ArrayList<Podcast> SearchPodcastByName(String name) {

        ArrayList<Podcast> podcasts = new ArrayList<>();
        for (Podcast podcast : Library.getInstance().getPodcasts()) {
            if (podcast.getName().startsWith(name)) {
                podcasts.add(podcast);
            }
        }
        return podcasts;
    }

    private ArrayList<Podcast> SearchPodcastByOwner(String owner) {

        ArrayList<Podcast> podcasts = new ArrayList<>();
        for (Podcast podcast : Library.getInstance().getPodcasts()) {
            if (podcast.getOwner().equals(owner)) {
                podcasts.add(podcast);
            }
        }
        return podcasts;
    }
}
