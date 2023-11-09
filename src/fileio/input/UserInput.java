package fileio.input;

import lombok.Getter;
import main.*;

import java.util.ArrayList;

public final class UserInput {
    private String username;
    private int age;
    private String city;

    @Getter
    private Player musicplayer = new Player();

    @Getter
    private SearchBar searchBar = new SearchBar();

    private ArrayList<Playlist> playlists = new ArrayList<>();
    private ArrayList<SongInput> likedSongs;

    public UserInput() {}

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

    public void addLikedSong(SongInput song) {
        likedSongs.add(song);
    }

    public void setMusicPlayer() {
        musicplayer = new MusicPlayer();
    }

    public void setPlaylistPlayer() {

    }

    public void setPodcastPlayer() {

    }

    public void setSearchBar(String type) {
        switch (type) {
            case "song":
                searchBar = new SearchBarSong();
                break;
            case "podcast":
                searchBar = new SearchBarPodcast();
                break;
            case "playlist":
                break;
        }
    }

    public void removePlayer() {

    }
}
