package program.searchbar;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.Getter;
import program.CommandList;
import program.Manager;
import program.command.Filters;
import program.format.Library;
import program.format.Playlist;
import program.format.Podcast;
import program.format.Song;

import java.util.ArrayList;
import java.util.Collections;

public class SearchBarUser extends SearchBar {
    private static final int MAX_USERS = 5;
    @Getter
    private final ArrayList<String> users = new ArrayList<>();

    @Getter
    private Podcast podcastLoaded;

    @Getter
    private Song songLoaded;

    @Getter
    private Playlist playlistLoaded;

    @Getter
    private String userLoaded;

    private String owner;

    /**
     * clears search
     */
    public void clearSearch() {
    }

    public SearchBarUser(String owner) {
        this.owner = owner;
    }

    /**
     * select searched podcasts
     * @param number podcast's id
     * @param username user's name
     */
    public void select(final int number, final String username) {
        if (!users.isEmpty() && Manager.getSource(username).isSourceSearched()) {
            if (number <= users.size()) {
                userLoaded = users.get((number - 1));
                Manager.getPartialResult().put("message",
                        "Successfully selected " + userLoaded + "'s page.");
                Manager.getUser(username).getCurrentPage().changePage(userLoaded);
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

        int size = users.size();

        Manager.getPartialResult().put("message",
                "Search returned " + size + " results");
        for (String sg : users) {
            node.add(sg);
        }

        Manager.getPartialResult().set("results", node);
    }

    /**
     * search podcasts
     * @param filter search filters
     */
    public void search(final Filters filter) {
        switch (CommandList.getCommand().getType()) {
            case "artist" -> {
                if (filter.getName() != null) {
                    users.addAll(searchArtistByName(filter.getName()));
                }
            }
            case "host" -> {
                if (filter.getName() != null) {
                    users.addAll(searchHostByName(filter.getName()));
                }
            }
        }

        while (users.size() > MAX_USERS) {
            users.remove(users.size() - 1);
        }
    }

    /**
     * search podcast by name
     * @param name podcast's owner
     */
    private ArrayList<String> searchArtistByName(final String name) {

        ArrayList<String> a = new ArrayList<>();
        for (String artist : Manager.getArtists()) {
            if (artist.startsWith(name)) {
                a.add(artist);
            }
        }
        return a;
    }

    /**
     * search podcast by owner
     * @param name playlist's owner
     */
    private ArrayList<String> searchHostByName(final String name) {

        ArrayList<String> h = new ArrayList<>();
        for (String host : Manager.getHosts()) {
            if (host.startsWith(name)) {
                h.add(host);
            }
        }
        return h;
    }
}
