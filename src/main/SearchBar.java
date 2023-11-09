package main;

import fileio.input.LibraryInput;
import fileio.input.PodcastInput;
import fileio.input.SongInput;
import lombok.Getter;

import java.util.ArrayList;
public class SearchBar {

    @Getter
    private SongInput songLoaded;
    public SearchBar() {}

    public void clearSearch() {}

    public void select(int number) {}

    public void search(Filters filter) {}

    public void searchDone() {}
}
