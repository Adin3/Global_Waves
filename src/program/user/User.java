package program.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import fileio.input.UserInput;
import lombok.Getter;
import lombok.Setter;
import program.admin.Manager;
import program.format.Event;
import program.format.Merch;
import program.format.Announcement;
import program.page.Page;
import program.format.Playlist;
import program.format.Song;
import program.format.Album;
import program.player.Player;
import program.searchbar.SearchBar;

import java.util.ArrayList;
import java.util.Map;

public class User {
    protected static ObjectMapper objectMapper = new ObjectMapper();

    @Getter @Setter
    protected String username;

    @Getter @Setter
    protected int age;

    @Getter @Setter
    protected String city;

    @Getter
    protected String userType;

    /**
     * returns the player
     */
    public Player getMusicplayer() {
        return null;
    }
    /**
     * returns the searchbar
     */
    public SearchBar getSearchBar() {
        return null;
    }
    /**
     * returns user's type
     */
    public String getFormatType() {
        return null;
    }
    /**
     * returns the current page
     */
    public Page getCurrentPage() {
        return null;
    }
    /**
     * returns all user's playlists
     */
    public ArrayList<Playlist> getPlaylists() {
        return null;
    }
    /**
     * returns all user's albums
     */
    public Map<String, Album> getAlbums() {
        return null;
    }
    /**
     * returns all user's liked songs
     */
    public ArrayList<Song> getLikedSongs() {
        return null;
    }
    /**
     * returns all user's events
     */
    public Map<String, Event> getEvents() {
        return null;
    }
    /**
     * returns all user's merch
     */
    public Map<String, Merch> getMerchs() {
        return null;
    }
    /**
     * returns all user's followed playlists
     */
    public ArrayList<String> getFollowedPlaylists() {
        return null;
    }
    /**
     * returns all user's podcasts
     */
    public ArrayList<String> getPodcasts() {
        return null;
    }
    /**
     * returns all user's announcements
     */
    public ArrayList<Announcement> getAnnouncements() {
        return null;
    }
    /**
     * returns the status of the user
     */
    public UserStatus getStatus() {
        return null;
    }

    protected enum UserStatus {
        ONLINE,
        OFFLINE,
    }

    protected UserStatus status = UserStatus.ONLINE;

    public User() { }
    public User(final UserInput user) {
        this.age = user.getAge();
        this.city = user.getCity();
        this.username = user.getUsername();
        this.userType = "user";
    }

    public User(final String username, final int age,
                final String city, final String userType) {
        this.username = username;
        this.age = age;
        this.city = city;
        this.userType = userType;
    }
    /**
     * updates the player of specific user
     */
    public void updatePlayer(final int deltaTime) { }
    /**
     * changes the status of user
     */
    public void changeStatus() { }
    /**
     * checks if user is offline
     */
    public boolean isOffline() {
        return false;
    }
    /**
     * checks if user is a normal user
     */
    public boolean isNotNormalUser() {
        return !userType.equals("user");
    }
    /**
     * adds playlist to user
     * @param playlist a playlist
     */
    public void addPlaylist(final Playlist playlist) { }

    /**
     * adds song to liked song collection
     * @param song to be liked song
     */
    public void addLikedSong(final Song song) { }

    /**
     * removes song from liked song collection
     * @param song liked song
     */
    public void removeLikedSong(final Song song) { }

    /**
     * creates new player based on searches
     */
    public void setMusicPlayer() { }
    /**
     * creates a searchbar depending on the type searched
     * @param commandType search type
     */
    public void setSearchBar(final String commandType) { }
    /**
     * searches in library files, collections, or users
     */
    public void search() {
        Manager.getPartialResult().put("message", username + " is not a normal user.");
    }
    /**
     * selects one element from the array returned by search
     */
    public void select() {
        Manager.getPartialResult().put("message", username + " is not a normal user.");
    }
    /**
     * loads a file or collection into player
     */
    public void load() {
        Manager.getPartialResult().put("message", username + " is not a normal user.");
    }
    /**
     * shows the status of player
     */
    public void status() {
        Manager.getPartialResult().put("message", username + " is not a normal user.");
    }
    /**
     * toggles the play/pause of player
     */
    public void playPause() {
        Manager.getPartialResult().put("message", username + " is not a normal user.");
    }
    /**
     * toggles the repeat function of player
     */
    public void repeat() {
        Manager.getPartialResult().put("message", username + " is not a normal user.");
    }
    /**
     * shuffles the collection
     */
    public void shuffle() {
        Manager.getPartialResult().put("message", username + " is not a normal user.");
    }
    /**
     * goes forward to next file
     */
    public void next() {
        Manager.getPartialResult().put("message", username + " is not a normal user.");
    }
    /**
     * goes back to previous file
     */
    public void prev() {
        Manager.getPartialResult().put("message", username + " is not a normal user.");
    }
    /**
     * skips forward for 90 seconds
     */
    public void forward() {
        Manager.getPartialResult().put("message", username + " is not a normal user.");
    }
    /**
     * goes backward for 90 seconds
     */
    public void backward() {
        Manager.getPartialResult().put("message", username + " is not a normal user.");
    }
    /**
     * Adds a new playlist
     */
    public void createPlaylist() {
        Manager.getPartialResult().put("message", username + " is not a normal user.");
    }

    /**
     * adds/removes the current song in the playlist
     */
    public void addRemoveInPlaylist() {
        Manager.getPartialResult().put("message", username + " is not a normal user.");
    }
    /**
     * change the visibility of the playlist
     */
    public void switchVisibility() {
        Manager.getPartialResult().put("message", username + " is not a normal user.");
    }
    /**
     * follow/unfollow the current playlist
     */
    public void follow() {
        Manager.getPartialResult().put("message", username + " is not a normal user.");
    }

    /**
     * like/unlike the current song
     */
    public void like() {
        Manager.getPartialResult().put("message", username + " is not a normal user.");
    }

    /**
     * shows all playlists created by the specific user
     */
    public void showPlaylists() {
        Manager.getPartialResult().put("message", username + " is not a normal user.");
    }

    /**
     * shows all liked songs by the specific user
     */
    public void showPreferredSongs() {
        Manager.getPartialResult().put("message", username + " is not a normal user.");
    }

    /**
     * shows top 5 playlists on app
     */
    public void getTop5Playlists() {
        Manager.getPartialResult().put("message", "not a user.");
    }

    /**
     * shows top 5 songs in app
     */
    public void getTop5Songs() {
        Manager.getPartialResult().put("message", "not a user.");
    }
    /**
     * switch connection status of the user
     */
    public void switchConnectionStatus() {
        Manager.getPartialResult().put("message", username + " is not a normal user.");
    }
    /**
     * shows all online normal users
     */
    public void getOnlineUsers() {
        Manager.getPartialResult().put("message", "not a user.");
    }
    /**
     * adds a new user in the system
     */
    public void addUser() {
        Manager.getPartialResult().put("message", "not a user.");
    }
    /**
     * adds a new album in the system
     */
    public void addAlbum() {
        Manager.getPartialResult().put("message", username + " is not an artist.");
    }
    /**
     * shows all albums of that artist
     */
    public void showAlbums() {
        Manager.getPartialResult().put("message", username + " is not an artist.");
    }
    /**
     * prints the current page accessed
     */
    public void printCurrentPage() {
        Manager.getPartialResult().put("message", username + " is not a normal user.");
    }
    /**
     * adds a new event
     */
    public void addEvent() {
        Manager.getPartialResult().put("message", username + " is not an artist.");
    }
    /**
     * adds new merch
     */
    public void addMerch() {
        Manager.getPartialResult().put("message", username + " is not an artist.");
    }
    /**
     * shows all users in the system
     */
    public void getAllUsers() {
        Manager.getPartialResult().put("message", "not a user.");
    }
    /**
     * deletes a user
     */
    public void deleteUser() {
        Manager.getPartialResult().put("message", "not a specific user.");
    }
    /**
     * adds a new podcast to the library
     */
    public void addPodcast() {
        Manager.getPartialResult().put("message", username + " is not a host.");
    }
    /**
     * add a new announcement
     */
    public void addAnnouncement() {
        Manager.getPartialResult().put("message", username + " is not a host.");
    }
    /**
     * removes an announcement
     */
    public void removeAnnouncement() {
        Manager.getPartialResult().put("message", username + " is not a host.");
    }
    /**
     * shows all podcasts of that host
     */
    public void showPodcasts() {
        Manager.getPartialResult().put("message", username + " is not a host.");
    }
    /**
     * removes an album
     */
    public void removeAlbum() {
        Manager.getPartialResult().put("message", username + " is not an artist.");
    }
    /**
     * changes the page
     */
    public void changePage() {
        Manager.getPartialResult().put("message", username + " is not a normal user.");
    }

    /**
     * removes a podcast
     */
    public void removePodcast() {
        Manager.getPartialResult().put("message", username + " is not a host.");
    }

    /**
     * removes an event
     */
    public void removeEvent() {
        Manager.getPartialResult().put("message", username + " is not an artist.");
    }
}
