package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;

public class SearchBarPlaylist extends SearchBar {
    @Getter
    private final ArrayList<Playlist> playlists = new ArrayList<>();

    private static final int MAX_PLAYLISTS = 5;
    @Getter
    private Playlist playlistLoaded;

    @Getter
    private Song songLoaded;

    @Getter
    private final String owner;
    public SearchBarPlaylist(final String owner) {
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
     * selects what was searched
     * @param number id of the item
     * @param username name of the user
     */
    public void select(final int number, final String username) {
        if (Manager.getSource(username).isSourceSearched()) {
            if (number <= playlists.size()) {
                playlistLoaded = playlists.get((number - 1));
                sourceSelected = true;
                sourceSearched = false;
                Manager.getPartialResult().put("message",
                        "Successfully selected " + playlistLoaded.getName() + ".");
                return;
            }
            Manager.getPartialResult().put("message",
                    "The selected ID is too high.");
            Manager.getSource(owner).setSourceSearched(false);
            Manager.getSource(owner).setSourceSelected(false);
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

        int size = playlists.size();

        Manager.getPartialResult().put("message", "Search returned "
                + size + " results");

        for (Playlist sg : playlists) {
            node.add(sg.getName());
        }

        Manager.getPartialResult().set("results", node);
        sourceSearched = true;
    }

    /**
     * search by filters
     * @param filter search filters
     */
    public void search(final Filters filter) {
        int options = 0;
        if (filter.getName() != null) {
            options++;
            playlists.addAll(searchPlaylistByName(filter.getName()));
        }

        if (filter.getOwner() != null) {
            options++;
            playlists.addAll(searchPlaylistByOwner(filter.getOwner()));
        }

        for (int i = 0; i < playlists.size(); i++) {
            if (playlists.get(i).getVisibility().equals("private")
                    && !playlists.get(i).getOwner().equals(owner)) {

                playlists.remove(i);
                i--;
            }
        }

        if (options > 1) {
            ArrayList<Playlist> temp = new ArrayList<>();

            for (Playlist p : playlists) {
                if (Collections.frequency(playlists, p) > 1) {
                    temp.add(p);
                }
            }

            playlists.clear();
            playlists.addAll(temp);
            temp.clear();

            for (Playlist p : playlists) {
                if (!temp.contains(p)) {
                    temp.add(p);
                }
            }

            playlists.clear();
            playlists.addAll(temp);
        }

        while (playlists.size() > MAX_PLAYLISTS) {
            playlists.remove(playlists.size() - 1);
        }
    }
    /**
     * search playlist by name
     * @param name the name of the playlist
     */
    private ArrayList<Playlist> searchPlaylistByName(final String name) {

        ArrayList<Playlist> play = new ArrayList<>();
        for (Playlist pl : Manager.getPlaylists()) {
            if (pl.getName().startsWith(name)) {
                play.add(pl);
            }
        }
        return play;
    }

    /**
     * search playlist by owner
     * @param playlistOwner playlist's owner
     */
    private ArrayList<Playlist> searchPlaylistByOwner(final String playlistOwner) {

        ArrayList<Playlist> play = new ArrayList<>();
        for (Playlist pl : Manager.getPlaylists()) {
            if (pl.getOwner().equals(playlistOwner)) {
                play.add(pl);
            }
        }
        return play;
    }
}
