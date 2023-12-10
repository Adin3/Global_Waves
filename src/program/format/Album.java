package program.format;

import lombok.Getter;

import java.util.ArrayList;

public class Album {
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
    private final int releaseYear;

    @Getter
    private String description;

    public Album(String owner, String name, int releaseYear, String description) {
        this.owner = owner;
        this.name = name;
        this.releaseYear = releaseYear;
        this.description = description;
    }

    /**
     * adds song in playlist
     */
    public void addSong(final Song song) {
        this.songs.add(song);
    }
    /**
     * set song on this index
     */
    public void setSong(final Song song, final int index) {
        songs.set(index, song);
    }
    /**
     * remove song from playlist
     */
    public void removeSong(final Song song) {
        songs.remove(song);
    }
    /**
     * change the visibility of the playlist
     */
    public void changeVisibility() {
        if (visibility.equals("public")) {
            visibility = "private";
        } else {
            visibility = "public";
        }
    }
    /**
     * get song
     */
    public Song getSong(final int index) {
        return songs.get(index);
    }
    /**
     * add follow
     */
    public void addFollower(final String username) {
        followers.add(username);
    }
    /**
     * remove follower
     */
    public void removeFollower(final String username) {
        followers.remove(username);
    }
    /**
     * returns number of followers
     */
    public int numberOfFollowers() {
        return followers.size();
    }
}
