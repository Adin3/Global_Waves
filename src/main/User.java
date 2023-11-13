package main;

import fileio.input.SongInput;
import fileio.input.UserInput;
import lombok.Getter;

import java.util.ArrayList;

public class User {
    private String username;
    private int age;
    private String city;

    @Getter
    private Player musicplayer = new Player();

    @Getter
    private SearchBar searchBar = new SearchBar();

    @Getter
    private String type;

    @Getter
    private final ArrayList<Playlist> playlists = new ArrayList<>();

    @Getter
    private final ArrayList<Song> likedSongs = new ArrayList<>();

    public User(UserInput user) {
        this.age = user.getAge();
        this.city = user.getCity();
        this.username = user.getUsername();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public int getAge() {
        return age;
    }

    public void setAge(final int age) {
        this.age = age;
    }

    public String getCity() {
        return city;
    }

    public void setCity(final String city) {
        this.city = city;
    }

    public void addPlaylist(Playlist playlist) {
        playlists.add(playlist);
    }

    public void addLikedSong(Song song) {
        likedSongs.add(song);
    }

    public void removeLikedSong(Song song) {
        likedSongs.remove(song);
    }

    public void setMusicPlayer() {
        switch (type) {
            case "song":
                musicplayer = new MusicPlayer(username);
                break;
            case "podcast":
                musicplayer = new PodcastPlayer(username);
                break;
            case "playlist":
                musicplayer = new PlaylistPlayer(username);
                break;
        }
    }

    public void setPlaylistPlayer() {

    }

    public void setPodcastPlayer() {

    }

    public void setSearchBar(String type) {
        this.type = type;
        switch (type) {
            case "song":
                searchBar = new SearchBarSong(username);
                break;
            case "podcast":
                searchBar = new SearchBarPodcast(username);
                break;
            case "playlist":
                searchBar = new SearchBarPlaylist(username);
                break;
        }
    }

    public void removePlayer() {

    }
}
