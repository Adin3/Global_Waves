package main;

import fileio.input.SongInput;
import lombok.Getter;

import java.util.ArrayList;
public class Playlist {

    @Getter
    private final String owner;

    @Getter
    private String visibility = "public";

    @Getter
    private final String name;

    @Getter
    private int followers;

    @Getter
    private final ArrayList<SongInput> songs = new ArrayList<>();
    public Playlist(String owner, String name) {
        this.owner = owner;
        this.name = name;
    }
    public void addSong(SongInput song) {
        this.songs.add(song);
    }

    public void removeSong(SongInput song) {
        songs.remove(song);
    }
    public void changeVisibility(String visibility) {
        this.visibility = visibility;
    }

    public SongInput getSong(int index) {
        return songs.get(index);
    }
}
