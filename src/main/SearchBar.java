package main;

import lombok.Getter;

import java.util.ArrayList;
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
    public SearchBar() {}

    public void clearSearch() {}

    public void select(int number, String username) {}

    public void search(Filters filter) {}

    public void searchDone() {}
}
