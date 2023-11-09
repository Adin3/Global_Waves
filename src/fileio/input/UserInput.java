package fileio.input;

import lombok.Getter;
import main.MusicPlayer;
import main.Playlist;
import main.SearchBar;

import java.util.ArrayList;

public final class UserInput {
    private String username;
    private int age;
    private String city;

    @Getter
    private final MusicPlayer musicplayer = new MusicPlayer();

    @Getter
    private final SearchBar searchBar = new SearchBar();

    private ArrayList<Playlist> playlists;

    private ArrayList<SongInput> likedSongs;

    public UserInput() {
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
}
