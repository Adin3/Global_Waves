package program.searchbar;

import lombok.Getter;
import program.Manager;
import program.format.Album;
import program.format.Playlist;
import program.format.Podcast;
import program.format.Song;
import program.command.Filters;

public class SearchBar {

    @Getter
    private Song songLoaded;

    @Getter
    private Playlist playlistLoaded;

    @Getter
    private Podcast podcastLoaded;

    @Getter
    protected boolean sourceSearched;

    @Getter
    protected boolean sourceSelected;

    public Album getAlbumLoaded() {
        return null;
    }

    public SearchBar() { }
    /**
     * clears the search
     */
    public void clearSearch() { }

    /**
     * select the searched item
     */
    public void select(final int number, final String username) {
        Manager.getPartialResult().put("message",
                "Please conduct a search before making a selection.");
    }

    /**
     * search items by filters
     */
    public void search(final Filters filter) { }

    /**
     * returns what was searched
     */
    public void searchDone() { }
}
