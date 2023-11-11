package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import fileio.input.LibraryInput;
import fileio.input.PodcastInput;
import fileio.input.SongInput;
import lombok.Getter;

import java.util.ArrayList;

public class SearchBarPlaylist extends SearchBar{
    @Getter
    private final ArrayList<Playlist> playlists = new ArrayList<>();

    @Getter
    private Playlist playlistLoaded;

    @Getter
    private SongInput songLoaded;
    public SearchBarPlaylist() {}

    public void clearSearch() {
        sourceSearched = false;
        sourceSelected = false;
    }

    public void select(int number) {
        if (!playlists.isEmpty()) {
            if (number <= playlists.size()) {
                playlistLoaded = playlists.get((number-1));
                sourceSelected = true;
                sourceSearched = false;
                Manager.partialResult.put("message", "Successfully selected " + playlistLoaded.getName() + ".");
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

        if (!playlists.isEmpty()) {
            Manager.partialResult.put("message", "Search returned " +
                    playlists.size() + " results");

            for (Playlist sg : playlists)
                node.add(sg.getName());
        }

        Manager.partialResult.set("results", node);
        sourceSearched = true;
    }

    public void search(Filters filter) {
        if (filter.getName() != null)
            playlists.addAll(SearchPlaylistByName(filter.getName()));

        if (filter.getOwner() != null)
            playlists.addAll(SearchPlaylistByOwner(filter.getOwner()));

        while (playlists.size() > 5) {
            playlists.remove(playlists.size()-1);
        }
    }
    private ArrayList<Playlist> SearchPlaylistByName(String name) {

        ArrayList<Playlist> play = new ArrayList<>();
        for (Playlist pl : Manager.getPlaylists()) {
            if (pl.getName().startsWith(name)) {
                play.add(pl);
            }
        }
        return play;
    }

    private ArrayList<Playlist> SearchPlaylistByOwner(String owner) {

        ArrayList<Playlist> play = new ArrayList<>();
        for (Playlist pl : Manager.getPlaylists()) {
            if (pl.getOwner().equals(owner)) {
                play.add(pl);
            }
        }
        return play;
    }
}
