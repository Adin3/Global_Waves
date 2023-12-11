package program.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.UserInput;
import lombok.Getter;
import program.Manager;
import program.format.*;
import program.page.Page;
import program.player.*;
import program.searchbar.*;

import java.util.ArrayList;

public class NormalUser extends User {

    @Getter
    private Player musicplayer = new Player();

    @Getter
    private SearchBar searchBar = new SearchBar();

    @Getter
    private String formatType;

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

    @Getter
    private final ArrayList<Event> events = new ArrayList<>();

    @Getter
    private final ArrayList<Merch> merch = new ArrayList<>();

    public ArrayList<String> getFollowedPlaylists() {
        return followedPlaylist;
    }

    private enum userStatus {
        ONLINE,
        OFFLINE,
    }

    private User.userStatus status = User.userStatus.ONLINE;

    public NormalUser() {}
    public NormalUser(final UserInput user) {
        this.age = user.getAge();
        this.city = user.getCity();
        this.username = user.getUsername();
        this.userType = "user";
    }

    public NormalUser(String username, int age, String city, String userType) {
        this.username = username;
        this.age = age;
        this.city = city;
        this.userType = userType;
    }

    public void changeStatus() {
        switch (status) {
            case ONLINE -> status = User.userStatus.OFFLINE;
            case OFFLINE -> status = User.userStatus.ONLINE;
        }
    }

    public void updatePlayer(int deltaTime) {
        if (!isOffline()) {
            musicplayer.updateDuration(deltaTime);
            musicplayer.updatePlayer();
        }
    }

    public boolean isOffline() {
        return status == User.userStatus.OFFLINE;
    }

    public boolean isNotNormalUser() {
        return !userType.equals("user");
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
            case "song" -> musicplayer = new MusicPlayer(username);
            case "podcast" -> musicplayer = new PodcastPlayer(username);
            case "playlist" -> musicplayer = new PlaylistPlayer(username);
            case "album" -> musicplayer = new AlbumPlayer(username);
            default -> {}
        }
    }
    /**
     * creates a searchbar depending on the type searched
     * @param commandType search type
     */
    public void setSearchBar(final String commandType) {
        this.formatType = commandType;
        switch (this.formatType) {
            case "song" -> searchBar = new SearchBarSong(username);
            case "podcast" -> searchBar = new SearchBarPodcast(username);
            case "playlist" -> searchBar = new SearchBarPlaylist(username);
            case "artist", "host" -> searchBar = new SearchBarUser(username);
            case "album" -> searchBar = new SearchBarAlbum(username);
            default -> {
            }
        }
    }

    public void search() {
        if (isOffline()) {
            ArrayNode results = objectMapper.createArrayNode();
            Manager.getPartialResult().set("results", results);
            Manager.getPartialResult().put("message", username + " is offline.");
            return;
        }

        setSearchBar(Manager.getCommand().getType());

        searchBar.clearSearch();

        musicplayer.clearPlayer();

        searchBar.search(Manager.getCommand().getFilters());

        searchBar.searchDone();
    }

    public void select() {
        searchBar.select(Manager.getCommand().getItemNumber(), username);
    }

    public void load() {
        if (!Manager.getSource(username).isSourceLoaded()) {
            setMusicPlayer();
        }

        musicplayer.load();
    }

    public void status() {
        musicplayer.status();
    }

    public void playPause() {
        musicplayer.playPause();
    }

    public void repeat() {
        musicplayer.repeat();
    }

    public void shuffle() {
        musicplayer.shuffle(Manager.getCommand().getSeed());
    }

    public void next() {
        musicplayer.next();
    }

    public void prev() {
        musicplayer.prev();
    }

    public void forward() {
        musicplayer.forward();
    }

    public void backward() {
        musicplayer.backward();
    }
    /**
     * Adds a new playlist
     */
    public void createPlaylist() {
        final String name = Manager.getCommand().getPlaylistName();
        final int time = Manager.getCommand().getTimestamp();

        if (Manager.checkUser()) {
            return;
        }

        for (Playlist p : Manager.getPlaylists()) {
            if (p.getName().equals(name)) {
                Manager.getPartialResult().put("message",
                        "A playlist with the same name already exists.");
                return;
            }
        }

        Playlist p = new Playlist(username, name, time);
        Manager.getPlaylists().add(p);
        Manager.getUsers().get(username).addPlaylist(p);
        Manager.getPartialResult().put("message", "Playlist created successfully.");
    }

    /**
     * adds/removes the current song in the playlist
     */
    public void addRemoveInPlaylist() {
        final String owner = Manager.getCommand().getUsername();
        final int id = Manager.getCommand().getPlaylistId();

        if (!Manager.getSource(owner).isSourceLoaded()) {
            Manager.getPartialResult().put("message",
                    "Please load a source before adding to or removing from the playlist.");
            return;
        }

        if (!Manager.getUser(owner).getFormatType().equals("song")
                || Manager.musicPlayer(owner).getSong() == null) {
            Manager.getPartialResult().put("message",
                    "The loaded source is not a song.");
            return;
        }

        if (Manager.getUser(owner).getPlaylists().size() <= (id - 1)) {
            Manager.getPartialResult().put("message",
                    "The specified playlist does not exist.");
            return;
        }

        Playlist playlist = Manager.getUser(owner).getPlaylists().get(id - 1);

        Song song = Manager.musicPlayer(owner).getSong();

        if (!playlist.getSongs().contains(song)) {
            playlist.addSong(song);
            Manager.getPartialResult().put("message", "Successfully added to playlist.");
        } else {
            playlist.removeSong(song);
            Manager.getPartialResult().put("message", "Successfully removed from playlist.");
        }

        for (int i = 0; i < Manager.getPlaylists().size(); i++) {
            if (Manager.getPlaylists().get(i).getName().equals(playlist.getName())) {
                Manager.getPlaylists().set(i, playlist);
            }
        }
    }
    /**
     * change the visibility of the playlist
     */
    public void switchVisibility() {
        final int id = Manager.getCommand().getPlaylistId();
        if (Manager.getUser(username).getPlaylists().size() <= (id - 1)) {
            Manager.getPartialResult().put("message", "The specified playlist ID is too high.");
            return;
        }

        Playlist pl = Manager.getUser(username).getPlaylists().get(id - 1);

        pl.changeVisibility();

        Manager.getPartialResult().put("message",
                "Visibility status updated successfully to " + pl.getVisibility() + ".");

        for (int i = 0; i < Manager.getPlaylists().size(); i++) {
            if (Manager.getPlaylists().get(i).getName().equals(pl.getName())) {
                Manager.getPlaylists().set(i, pl);
            }
        }
    }
    public void follow() {
        final String owner = Manager.getCommand().getUsername();

        if (!Manager.getSource(owner).isSourceSelected()) {
            Manager.getPartialResult().put("message",
                    "Please select a source before following or unfollowing.");
            return;
        }

        if (!Manager.getUser(owner).getFormatType().equals("playlist")) {
            Manager.getPartialResult().put("message",
                    "The selected source is not a playlist.");
            return;
        }

        Playlist pl = Manager.searchBar(owner).getPlaylistLoaded();

        if (pl.getOwner().equals(owner)) {
            Manager.getPartialResult().put("message",
                    "You cannot follow or unfollow your own playlist.");
            return;
        }

        if (pl.getFollowers().contains(owner)) {
            pl.removeFollower(owner);
            Manager.getPartialResult().put("message", "Playlist unfollowed successfully.");
        } else {
            pl.addFollower(owner);
            Manager.getPartialResult().put("message", "Playlist followed successfully.");
        }

        for (int i = 0; i < Manager.getPlaylists().size(); i++) {
            if (Manager.getPlaylists().get(i).getName().equals(pl.getName())) {
                Manager.getPlaylists().set(i, pl);
            }
        }
    }

    /**
     * like/unlike the current song
     */
    public void like() {
        final String username = Manager.getCommand().getUsername();
        if (Manager.checkUser()) {
            return;
        }

        if (Manager.getCurrentUser().isOffline()) {
            Manager.getPartialResult().put("message", username + " is offline.");
            return;
        }

        Song song = null;
        if (Manager.getUser(username).getFormatType().equals("song")) {
            song = Manager.searchBar(username).getSongLoaded();
        } else {
            song = Manager.musicPlayer(username).getCurrentSong();
            if (song != null) {
                for (Song s : Library.getInstance().getSongs()) {
                    if (s.getName().equals(song.getName())) {
                        song = s;
                    }
                }
            }
        }

        if (!Manager.getSources().get(username).isSourceLoaded()) {
            Manager.getPartialResult().put("message", "Please load a source before liking or unliking.");
            return;
        }

        if (song == null && Manager.getSources().get(username).isSourceLoaded()) {
            Manager.getPartialResult().put("message", "Loaded source is not a song.");
            return;
        }


        if (!Manager.getUser(username).getLikedSongs().contains(song)) {
            Manager.getUser(username).addLikedSong(song);
            Manager.getPartialResult().put("message", "Like registered successfully.");
        } else {
            Manager.getUser(username).removeLikedSong(song);
            Manager.getPartialResult().put("message", "Unlike registered successfully.");
        }
    }

    /**
     * shows all playlists created by the specific user
     */
    public void showPlaylists() {
        final String username = Manager.getCommand().getUsername();
        if (Manager.checkUser()) {
            return;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode rez = objectMapper.createArrayNode();

        for (Playlist play : Manager.getUser(username).getPlaylists()) {
            ObjectNode status = objectMapper.createObjectNode();
            if (play != null) {

                status.put("name", play.getName());
                ArrayNode node = objectMapper.createArrayNode();

                for (Song sg : play.getSongs()) {
                    node.add(sg.getName());
                }

                status.set("songs", node);
                status.put("visibility", play.getVisibility());
                status.put("followers", play.getFollowers().size());
            }
            rez.add(status);
        }
        Manager.getPartialResult().set("result", rez);
    }

    /**
     * shows all liked songs by the specific user
     */
    public void showPreferredSongs() {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode songs = objectMapper.createArrayNode();

        for (Song sg : Manager.getUser(username).getLikedSongs()) {
            songs.add(sg.getName());
        }

        Manager.getPartialResult().set("result", songs);
    }

    public void switchConnectionStatus() {
        Manager.getCurrentUser().changeStatus();
        Manager.getPartialResult().put("message", username + " has changed status successfully.");
    }

    public void printCurrentPage() {
        if (Manager.getCurrentUser().isOffline()) {
            Manager.getPartialResult().put("message",
                    Manager.getCurrentUser().getUsername() + " is offline.");
            return;
        }
        Manager.getPartialResult().put("message",
                Manager.getCurrentUser().getCurrentPage().printCurrentPage());
    }
}
