package program.user;

import fileio.input.UserInput;
import lombok.Getter;
import program.page.Page;
import program.format.Playlist;
import program.format.Song;
import program.player.MusicPlayer;
import program.player.Player;
import program.player.PlaylistPlayer;
import program.player.PodcastPlayer;
import program.searchbar.SearchBar;
import program.searchbar.SearchBarPlaylist;
import program.searchbar.SearchBarPodcast;
import program.searchbar.SearchBarSong;

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
    private String formatType;

    @Getter
    private String userType;

    @Getter
    private Page currentPage = new Page();

    @Getter
    private final ArrayList<Playlist> playlists = new ArrayList<>();

    @Getter
    private final ArrayList<String> albums = new ArrayList<>();

    @Getter
    private final ArrayList<String> followedPlaylist = new ArrayList<>();

    @Getter
    private final ArrayList<Song> likedSongs = new ArrayList<>();

    public ArrayList<String> getFollowedPlaylists() {
        return followedPlaylist;
    }

    private enum userStatus {
        ONLINE,
        OFFLINE,
    }

    private userStatus status = userStatus.ONLINE;

    public User() {}
    public User(final UserInput user) {
        this.age = user.getAge();
        this.city = user.getCity();
        this.username = user.getUsername();
        this.userType = "normal";
    }

    public User(String username, int age, String city, String userType) {
        this.username = username;
        this.age = age;
        this.city = city;
        this.userType = userType;
    }

    public void changeStatus() {
        switch (status) {
            case ONLINE -> status = userStatus.OFFLINE;
            case OFFLINE -> status = userStatus.ONLINE;
        }
    }

    public boolean isOffline() {
        return status == userStatus.OFFLINE;
    }

    public boolean isNotNormalUser() {
        return !userType.equals("normal");
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
        switch (formatType) {
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
        this.formatType = commandType;
        switch (this.formatType) {
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
