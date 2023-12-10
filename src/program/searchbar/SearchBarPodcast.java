package program.searchbar;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import lombok.Getter;
import program.Manager;
import program.format.Playlist;
import program.format.Podcast;
import program.format.Song;
import program.command.Filters;
import program.format.Library;

import java.util.ArrayList;
import java.util.Collections;

public class SearchBarPodcast extends SearchBar {

    private static final int MAX_PODCASTS = 5;
    @Getter
    private final ArrayList<Podcast> podcasts = new ArrayList<>();

    @Getter
    private Podcast podcastLoaded;

    @Getter
    private Song songLoaded;

    @Getter
    private Playlist playlistLoaded;

    @Getter
    private final String owner;

    public SearchBarPodcast(final String owner) {
        this.owner = owner;
    }

    /**
     * clears search
     */
    public void clearSearch() {
        sourceSearched = false;
        sourceSelected = false;
    }

    /**
     * select searched podcasts
     * @param number podcast's id
     * @param username user's name
     */
    public void select(final int number, final String username) {
        if (!podcasts.isEmpty() && Manager.getSource(username).isSourceSearched()) {
            if (number <= podcasts.size()) {
                podcastLoaded = podcasts.get((number - 1));
                sourceSelected = true;
                sourceSearched = false;
                Manager.getPartialResult().put("message",
                        "Successfully selected " + podcastLoaded.getName() + ".");
                return;
            }
            Manager.getPartialResult().put("message",
                    "The selected ID is too high.");
            return;
        }
        Manager.getPartialResult().put("message",
                "Please conduct a search before making a selection.");
    }

    /**
     * returns what was searched
     */
    public void searchDone() {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode node = objectMapper.createArrayNode();

        int size = podcasts.size();

        Manager.getPartialResult().put("message",
                "Search returned " + size + " results");
        for (Podcast sg : podcasts) {
            node.add(sg.getName());
        }

        Manager.getPartialResult().set("results", node);
        sourceSearched = true;
    }

    /**
     * search podcasts
     * @param filter search filters
     */
    public void search(final Filters filter) {
        int options = 0;
        if (filter.getName() != null) {
            options++;
            podcasts.addAll(searchPodcastByName(filter.getName()));
        }
        if (filter.getOwner() != null) {
            options++;
            podcasts.addAll(searchPodcastByOwner(filter.getOwner()));
        }

        if (options > 1) {
            ArrayList<Podcast> temp = new ArrayList<>();

            for (Podcast p : podcasts) {
                if (Collections.frequency(podcasts, p) > 1) {
                    temp.add(p);
                }
            }

            podcasts.clear();
            podcasts.addAll(temp);
            temp.clear();

            for (Podcast p : podcasts) {
                if (!temp.contains(p)) {
                    temp.add(p);
                }
            }

            podcasts.clear();
            podcasts.addAll(temp);
        }

        while (podcasts.size() > MAX_PODCASTS) {
            podcasts.remove(podcasts.size() - 1);
        }
    }

    /**
     * search podcast by name
     * @param name podcast's owner
     */
    private ArrayList<Podcast> searchPodcastByName(final String name) {

        ArrayList<Podcast> pod = new ArrayList<>();
        for (Podcast podcast : Library.getInstance().getPodcasts()) {
            if (podcast.getName().startsWith(name)) {
                pod.add(podcast);
            }
        }
        return pod;
    }

    /**
     * search podcast by owner
     * @param podcastOwner playlist's owner
     */
    private ArrayList<Podcast> searchPodcastByOwner(final String podcastOwner) {

        ArrayList<Podcast> pod = new ArrayList<>();
        for (Podcast podcast : Library.getInstance().getPodcasts()) {
            if (podcast.getOwner().equals(podcastOwner)) {
                pod.add(podcast);
            }
        }
        return pod;
    }
}
