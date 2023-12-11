package program.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.UserInput;
import lombok.Getter;
import lombok.Setter;
import program.Manager;
import program.command.Command;
import program.format.*;
import program.page.Page;
import program.player.*;
import program.searchbar.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Objects;

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

    public Player getMusicplayer() {
        return null;
    }

    public SearchBar getSearchBar() {
        return null;
    }

    public String getFormatType() {
        return null;
    }

    public Page getCurrentPage() {
        return null;
    }

    public ArrayList<Playlist> getPlaylists() {
        return null;
    }

    public ArrayList<String> getAlbums() {
        return null;
    }

    public ArrayList<String> getFollowedPlaylist() {
        return null;
    }

    public ArrayList<Song> getLikedSongs() {
        return null;
    }

    public ArrayList<Event> getEvents() {
        return null;
    }

    public ArrayList<Merch> getMerch() {
        return null;
    }

    public ArrayList<String> getFollowedPlaylists() {
        return null;
    }
    public userStatus getStatus() {
        return null;
    }

    protected enum userStatus {
        ONLINE,
        OFFLINE,
    }

    protected userStatus status = userStatus.ONLINE;

    public User() {}
    public User(final UserInput user) {
        this.age = user.getAge();
        this.city = user.getCity();
        this.username = user.getUsername();
        this.userType = "user";
    }

    public User(String username, int age, String city, String userType) {
        this.username = username;
        this.age = age;
        this.city = city;
        this.userType = userType;
    }

    public void updatePlayer(int deltaTime) {}
    public void changeStatus() {}

    public boolean isOffline() {
        return false;
    }

    public boolean isNotNormalUser() {
        return !userType.equals("user");
    }
    /**
     * adds playlist to user
     * @param playlist a playlist
     */
    public void addPlaylist(final Playlist playlist) {}

    /**
     * adds song to liked song collection
     * @param song to be liked song
     */
    public void addLikedSong(final Song song) {}

    /**
     * removes song from liked song collection
     * @param song liked song
     */
    public void removeLikedSong(final Song song) {}

    /**
     * creates new player based on searches
     */
    public void setMusicPlayer() {}
    /**
     * creates a searchbar depending on the type searched
     * @param commandType search type
     */
    public void setSearchBar(final String commandType) {}

    public void search() {
        Manager.getPartialResult().put("message", username + " is not a user.");
    }

    public void select() {
        Manager.getPartialResult().put("message", username + " is not a user.");
    }

    public void load() {
        Manager.getPartialResult().put("message", username + " is not a user.");
    }

    public void status() {
        Manager.getPartialResult().put("message", username + " is not a user.");
    }

    public void playPause() {
        Manager.getPartialResult().put("message", username + " is not a user.");
    }

    public void repeat() {
        Manager.getPartialResult().put("message", username + " is not a user.");
    }

    public void shuffle() {
        Manager.getPartialResult().put("message", username + " is not a user.");
    }

    public void next() {
        Manager.getPartialResult().put("message", username + " is not a user.");
    }

    public void prev() {
        Manager.getPartialResult().put("message", username + " is not a user.");
    }

    public void forward() {
        Manager.getPartialResult().put("message", username + " is not a user.");
    }

    public void backward() {
        Manager.getPartialResult().put("message", username + " is not a user.");
    }
    /**
     * Adds a new playlist
     */
    public void createPlaylist() {
        Manager.getPartialResult().put("message", username + " is not a user.");
    }

    /**
     * adds/removes the current song in the playlist
     */
    public void addRemoveInPlaylist() {
        Manager.getPartialResult().put("message", username + " is not a user.");
    }
    /**
     * change the visibility of the playlist
     */
    public void switchVisibility() {
        Manager.getPartialResult().put("message", username + " is not a user.");
    }
    /**
     * follow/unfollow the current playlist
     */
    public void follow() {
        Manager.getPartialResult().put("message", username + " is not a user.");
    }

    /**
     * like/unlike the current song
     */
    public void like() {
        Manager.getPartialResult().put("message", username + " is not a user.");
    }

    /**
     * shows all playlists created by the specific user
     */
    public void showPlaylists() {
        Manager.getPartialResult().put("message", username + " is not a user.");
    }

    /**
     * shows all liked songs by the specific user
     */
    public void showPreferredSongs() {
        Manager.getPartialResult().put("message", username + " is not a user.");
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

    public void switchConnectionStatus() {
        Manager.getPartialResult().put("message", username + " is not a user.");
    }

    public void getOnlineUsers() {
        Manager.getPartialResult().put("message", "not a user.");
    }

    public void addUser() {
        Manager.getPartialResult().put("message", "not a user.");
    }

    public void addAlbum() {
        Manager.getPartialResult().put("message", username + " is not an artist.");
    }

    public void showAlbums() {
        Manager.getPartialResult().put("message", username + " is not an artist.");
    }

    public void printCurrentPage() {
        Manager.getPartialResult().put("message", username + " is not a user.");
    }

    public void addEvent() {
        Manager.getPartialResult().put("message", username + " is not an artist.");
    }

    public void addMerch() {
        Manager.getPartialResult().put("message", username + " is not an artist.");
    }

    public void getAllUsers() {
        Manager.getPartialResult().put("message", "not a user.");
    }

    public void deleteUser() {
        Manager.getPartialResult().put("message", "not a user.");
    }
}
