package main;

import fileio.input.LibraryInput;
import fileio.input.PodcastInput;
import fileio.input.SongInput;
import lombok.Getter;

import java.util.ArrayList;
public class SearchBar {

    @Getter
    private final ArrayList<SongInput> song = new ArrayList<>();

    @Getter
    private final ArrayList<PodcastInput> podcast = new ArrayList<>();

    @Getter
    private SongInput songLoaded;

    @Getter
    private PodcastInput podcastLoaded;
    public SearchBar() {}

    public void clearSearch() {}

    public int select(int number) { return 0; }

    public void search(Filters filter) {}
}
