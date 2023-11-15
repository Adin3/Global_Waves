package main;

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
    private final ArrayList<Song> songs = new ArrayList<>();

    @Getter
    private final int time;
    public Playlist(String owner, String name, int time) {
        this.owner = owner;
        this.name = name;
        this.time = time;
    }

    public Playlist(Playlist playlist) {
        this.owner = playlist.getOwner();
        this.name = playlist.getName();
        this.time = playlist.getTime();
        this.visibility = playlist.getVisibility();
        this.followers = playlist.getFollowers();
        this.songs.addAll(playlist.getSongs());
    }
    public void addSong(Song song) {
        this.songs.add(song);
    }

    public void setSong(Song song, int index) {
        songs.set(index, song);
    }

    public void removeSong(Song song) {
        songs.remove(song);
    }
    public void changeVisibility() {
        if (visibility.equals("public")) {
            visibility = "private";
        } else {
            visibility = "public";
        }
    }

    public Song getSong(int index) {
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
