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
    private ArrayList<String> followers = new ArrayList<>();

    @Getter
    private final ArrayList<SongInput> songs = new ArrayList<>();

    @Getter
    private final int time;
    public Playlist(String owner, String name, int time) {
        this.owner = owner;
        this.name = name;
        this.time = time;
    }
    public void addSong(SongInput song) {
        this.songs.add(song);
    }

    public void removeSong(SongInput song) {
        songs.remove(song);
    }
    public void changeVisibility() {
        if (visibility.equals("public")) {
            visibility = "private";
        } else {
            visibility = "public";
        }
    }

    public SongInput getSong(int index) {
        return songs.get(index);
    }

    public void addFollower(String username) {
        followers.add(username);
    }
    public void removeFollower(String username) {
        followers.remove(username);
    }

    public int numberOfFollowers() {
        return followers.size();
    }

}
