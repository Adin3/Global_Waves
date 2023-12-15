package program.searchbar;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.Getter;
import program.admin.Manager;
import program.command.Filters;
import program.format.Album;
import program.format.Playlist;
import program.format.Song;

import java.util.ArrayList;
import java.util.Collections;

public class SearchBarAlbum extends SearchBar {
    @Getter
    private final ArrayList<Album> albums = new ArrayList<>();

    private static final int MAX_ALBUMS = 5;
    @Getter
    private Playlist playlistLoaded;

    @Getter
    private Song songLoaded;

    @Getter
    private final String owner;

    @Getter
    private Album albumLoaded;
    public SearchBarAlbum(final String owner) {
        this.owner = owner;
    }

    /**
     * clears search
     */
    public void clearSearch() {
    }

    /**
     * selects what was searched
     * @param number id of the item
     * @param username name of the user
     */
    public void select(final int number, final String username) {
        if (Manager.getSource(username).isSourceSearched()) {
            if (number <= albums.size()) {
                albumLoaded = albums.get((number - 1));
                Manager.getPartialResult().put("message",
                        "Successfully selected " + albumLoaded.getName() + ".");
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

        int size = albums.size();

        Manager.getPartialResult().put("message", "Search returned "
                + size + " results");

        for (Album al : albums) {
            node.add(al.getName());
        }

        Manager.getPartialResult().set("results", node);
    }

    /**
     * search by filters
     * @param filter search filters
     */
    public void search(final Filters filter) {
        int options = 0;
        if (filter.getName() != null) {
            options++;
            albums.addAll(searchAlbumByName(filter.getName()));
        }

        if (filter.getOwner() != null) {
            options++;
            albums.addAll(searchAlbumByOwner(filter.getOwner()));
        }

        if (options > 1) {
            ArrayList<Album> temp = new ArrayList<>();

            for (Album p : albums) {
                if (Collections.frequency(albums, p) > 1) {
                    temp.add(p);
                }
            }

            albums.clear();
            albums.addAll(temp);
            temp.clear();

            for (Album p : albums) {
                if (!temp.contains(p)) {
                    temp.add(p);
                }
            }

            albums.clear();
            albums.addAll(temp);
        }

        while (albums.size() > MAX_ALBUMS) {
            albums.remove(albums.size() - 1);
        }
    }
    /**
     * search playlist by name
     * @param name the name of the playlist
     */
    private ArrayList<Album> searchAlbumByName(final String name) {

        ArrayList<Album> play = new ArrayList<>();
        for (Album pl : Manager.getAlbums()) {
            if (pl.getName().startsWith(name)) {
                play.add(pl);
            }
        }
        return play;
    }

    /**
     * search playlist by owner
     * @param albumOwner playlist's owner
     */
    private ArrayList<Album> searchAlbumByOwner(final String albumOwner) {

        ArrayList<Album> play = new ArrayList<>();
        for (Album pl : Manager.getAlbums()) {
            if (pl.getOwner().equals(albumOwner)) {
                play.add(pl);
            }
        }
        return play;
    }
}
