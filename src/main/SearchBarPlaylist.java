package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;

public class SearchBarPlaylist extends SearchBar{
    @Getter
    private final ArrayList<Playlist> playlists = new ArrayList<>();

    @Getter
    private Playlist playlistLoaded;

    @Getter
    private Song songLoaded;

    @Getter
    private final String owner;
    public SearchBarPlaylist(String owner) {
        this.owner = owner;
    }

    public void clearSearch() {
        sourceSearched = false;
        sourceSelected = false;
    }

    public void select(int number, String username) {
        if (Manager.getSource(username).isSourceSearched()) {
            if (number <= playlists.size()) {
                playlistLoaded = playlists.get((number-1));
                sourceSelected = true;
                sourceSearched = false;
                Manager.partialResult.put("message", "Successfully selected " + playlistLoaded.getName() + ".");
                return;
            }
            Manager.partialResult.put("message", "The selected ID is too high.");
            Manager.getSource(owner).setSourceSearched(false);
            Manager.getSource(owner).setSourceSelected(false);
            return;
        }
        Manager.partialResult.put("message", "Please conduct a search before making a selection.");
    }

    public void searchDone() {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode node = objectMapper.createArrayNode();

        int size = playlists.size();

        Manager.partialResult.put("message", "Search returned " +
                size + " results");

        for (Playlist sg : playlists)
            node.add(sg.getName());

        Manager.partialResult.set("results", node);
        sourceSearched = true;
    }

    public void search(Filters filter) {
        int options = 0;
        if (filter.getName() != null) {
            options++;
            playlists.addAll(SearchPlaylistByName(filter.getName()));
        }

        if (filter.getOwner() != null) {
            options++;
            playlists.addAll(SearchPlaylistByOwner(filter.getOwner()));
        }

        for(int i = 0; i < playlists.size(); i++) {
            if(playlists.get(i).getVisibility().equals("private") && !playlists.get(i).getOwner().equals(owner)) {
                playlists.remove(i);
                i--;
            }
        }

        if (options > 1) {
            ArrayList<Playlist> temp = new ArrayList<>();

            for(Playlist p : playlists)
                if(Collections.frequency(playlists, p) > 1)
                    temp.add(p);

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
