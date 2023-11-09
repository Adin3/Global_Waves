package main;

import fileio.input.SongInput;
import lombok.Getter;

import java.util.ArrayList;
public class Playlist {

    @Getter
    private final String owner;

    @Getter
    private boolean visibility = true;

    @Getter
    private final String name;
    private final ArrayList<SongInput> song = new ArrayList<>();
    public Playlist(String owner, String name) {
        this.owner = owner;
        this.name = name;
    }
    public void addSong(SongInput song) {
        this.song.add(song);
    }
    public void changeVisibility(boolean visibility) {
        this.visibility = visibility;
    }

    public SongInput getSong(int index) {
        return song.get(index);
    }
}
