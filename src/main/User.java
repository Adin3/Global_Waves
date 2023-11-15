package main;

import fileio.input.UserInput;
import lombok.Getter;

import java.util.ArrayList;

public final class User {
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

    public User(final UserInput user) {
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
    /**
     * adds playlist to user
     * @param playlist a playlist
     */
    public void addPlaylist(final Playlist playlist) {
        playlists.add(playlist);
    }

    /**
     * adds song to liked song collection
     * @param song to be liked song
     */
    public void addLikedSong(final Song song) {
        likedSongs.add(song);
    }

    /**
     * removes song from liked song collection
     * @param song liked song
     */
    public void removeLikedSong(final Song song) {
        likedSongs.remove(song);
    }

    /**
     * creates new player based on searches
     */
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
            default:
                break;
        }
    }
    /**
     * creates a searchbar depending on the type searched
     * @param commandType search type
     */
    public void setSearchBar(final String commandType) {
        this.type = commandType;
        switch (this.type) {
            case "song":
                searchBar = new SearchBarSong(username);
                break;
            case "podcast":
                searchBar = new SearchBarPodcast(username);
                break;
            case "playlist":
                searchBar = new SearchBarPlaylist(username);
                break;
            default:
                break;
        }
    }
}
