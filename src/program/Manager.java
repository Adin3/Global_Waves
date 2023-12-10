package program;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import program.command.Command;
import program.format.Album;
import program.format.Library;
import program.format.Playlist;
import program.format.Song;
import program.player.Player;
import program.searchbar.SearchBar;

import java.util.*;

public final class Manager {

    private static ObjectMapper objectMapper = new ObjectMapper();
    private static final int TOP5 = 5;
    private static Manager instance = null;
    @Getter
    private static Map<String, User> users = new HashMap<>();

    @Getter
    private static Map<String, Album> albums = new HashMap<>();

    @Getter
    private static ArrayList<Playlist> playlists = new ArrayList<>();
    @Getter @Setter
    private static ArrayNode result;
    @Getter @Setter
    private static ObjectNode partialResult;
    @Getter
    private static Map<String, Source> sources = new HashMap<>();

    private static Command command;

    private static int deltaTime = 0;
    private static int lastTime = 0;

    private Manager() { }
    /**
     * Gets instance of Manager Class
     */
    public static Manager getInstance() {
        if (instance == null) {
            instance = new Manager();
        }
        return instance;
    }
    public static User getCurrentUser() {
        return users.get(command.getUsername());
    }

    public static boolean userExists() {
        return getCurrentUser() != null;
    }

    private static boolean checkUser() {
        if (!userExists()) {
            Manager.partialResult.put("message", "The username "
                    + command.getUsername() + " doesn't exist.");
            return true;
        }
        return false;
    }
    /**
     * Adds user to Manager
     * @param user the user in question
     */
    public static void addUser(final User user) {
        users.put(user.getUsername(), user);
    }
    /**
     * returns the user with specific username
     * @param username the username of the user
     */
    public static User getUser(final String username) {
        return users.get(username);
    }
    /**
     * returns the searchbar of a specific user
     * @param username the username of the user
     */
    public static SearchBar searchBar(final String username) {
        return users.get(username).getSearchBar();
    }
    /**
     * returns the player of a specific user
     * @param username the username of the user
     */
    public static Player musicPlayer(final String username) {
        return users.get(username).getMusicplayer();
    }
    /**
     * Adds a source for every user
     * @param username the username of the user
     */
    public static void addSource(final String username) {
        sources.put(username, new Source());
    }

    /**
     * returns the source of a specific user
     * @param username the username of the user
     */
    public static Source getSource(final String username) {
        return sources.get(username);
    }
    /**
     * updates all players
     */
    public static void updatePlayers() {
        for (User user : users.values()) {
            if (userExists() && !getCurrentUser().isOffline()) {
                user.getMusicplayer().updateDuration(deltaTime);
                user.getMusicplayer().updatePlayer();
            }
        }
    }
    public static void search() {
        String username = command.getUsername();

        if (checkUser()) {
            return;
        }

        if (getCurrentUser().isOffline()) {
            ArrayNode results = objectMapper.createArrayNode();
            Manager.partialResult.set("results", results);
            Manager.partialResult.put("message", username + " is offline.");
            return;
        }

        Manager.getUser(username).setSearchBar(command.getType());

        Manager.searchBar(username).clearSearch();

        Manager.musicPlayer(username).clearPlayer();

        Manager.searchBar(username).search(command.getFilters());

        Manager.searchBar(username).searchDone();
    }

    public static void select() {
        String username = command.getUsername();
        if (checkUser()) {
            return;
        }
        Manager.searchBar(username).select(command.getItemNumber(), username);
    }

    public static void load() {
        String username = command.getUsername();
        if (checkUser()) {
            return;
        }

        if (!Manager.getSource(username).isSourceLoaded()) {
            Manager.getUser(username).setMusicPlayer();
        }

        Manager.musicPlayer(username).load();
    }

    public static void status() {
        String username = command.getUsername();
        if (checkUser()) {
            return;
        }
        Manager.musicPlayer(username).status();
    }

    public static void playPause() {
        String username = command.getUsername();
        if (checkUser()) {
            return;
        }
        Manager.musicPlayer(username).playPause();
    }

    public static void repeat() {
        String username = command.getUsername();
        if (checkUser()) {
            return;
        }
        Manager.musicPlayer(username).repeat();
    }

    public static void shuffle() {
        String username = command.getUsername();
        if (checkUser()) {
            return;
        }
        Manager.musicPlayer(username).shuffle(command.getSeed());
    }

    public static void next() {
        String username = command.getUsername();
        if (checkUser()) {
            return;
        }
        Manager.musicPlayer(username).next();
    }

    public static void prev() {
        String username = command.getUsername();
        if (checkUser()) {
            return;
        }
        Manager.musicPlayer(username).prev();
    }

    public static void forward() {
        String username = command.getUsername();
        if (checkUser()) {
            return;
        }
        Manager.musicPlayer(username).forward();
    }

    public static void backward() {
        String username = command.getUsername();
        if (checkUser()) {
            return;
        }
        Manager.musicPlayer(username).backward();
    }
    /**
     * Adds a new playlist
     */
    public static void createPlaylist() {
        final String username = command.getUsername();
        final String name = command.getPlaylistName();
        final int time = command.getTimestamp();

        if (checkUser()) {
            return;
        }

        for (Playlist p : playlists) {
            if (p.getName().equals(name)) {
                Manager.partialResult.put("message",
                        "A playlist with the same name already exists.");
                return;
            }
        }

        Playlist p = new Playlist(username, name, time);
        playlists.add(p);
        users.get(username).addPlaylist(p);
        Manager.partialResult.put("message", "Playlist created successfully.");
    }

    /**
     * adds/removes the current song in the playlist
     */
    public static void addRemoveInPlaylist() {
        final String owner = command.getUsername();
        final int id = command.getPlaylistId();

        if (checkUser()) {
            return;
        }

        if (!Manager.getSource(owner).isSourceLoaded()) {
            Manager.partialResult.put("message",
                    "Please load a source before adding to or removing from the playlist.");
            return;
        }

        if (!Manager.getUser(owner).getFormatType().equals("song")
                || Manager.musicPlayer(owner).getSong() == null) {
            Manager.partialResult.put("message",
                    "The loaded source is not a song.");
            return;
        }

        if (getUser(owner).getPlaylists().size() <= (id - 1)) {
            Manager.partialResult.put("message",
                    "The specified playlist does not exist.");
            return;
        }

        Playlist playlist = getUser(owner).getPlaylists().get(id - 1);

        Song song = Manager.musicPlayer(owner).getSong();

        if (!playlist.getSongs().contains(song)) {
            playlist.addSong(song);
            Manager.partialResult.put("message", "Successfully added to playlist.");
        } else {
            playlist.removeSong(song);
            Manager.partialResult.put("message", "Successfully removed from playlist.");
        }

        for (int i = 0; i < playlists.size(); i++) {
            if (playlists.get(i).getName().equals(playlist.getName())) {
                playlists.set(i, playlist);
            }
        }
    }
    /**
     * change the visibility of the playlist
     */
    public static void switchVisibility() {
        final String username = command.getUsername();
        final int id = command.getPlaylistId();

        if (checkUser()) {
            return;
        }

        if (getUser(username).getPlaylists().size() <= (id - 1)) {
            Manager.partialResult.put("message", "The specified playlist ID is too high.");
            return;
        }

        Playlist pl = getUser(username).getPlaylists().get(id - 1);

        pl.changeVisibility();

        Manager.partialResult.put("message",
                "Visibility status updated successfully to " + pl.getVisibility() + ".");

        for (int i = 0; i < playlists.size(); i++) {
            if (playlists.get(i).getName().equals(pl.getName())) {
                playlists.set(i, pl);
            }
        }
    }
    /**
     * follow/unfollow the current playlist
     */
    public static void follow() {
        final String owner = command.getUsername();

        if (checkUser()) {
            return;
        }
        if (!Manager.getSource(owner).isSourceSelected()) {
            Manager.partialResult.put("message",
                    "Please select a source before following or unfollowing.");
            return;
        }

        if (!Manager.getUser(owner).getFormatType().equals("playlist")) {
            Manager.partialResult.put("message",
                    "The selected source is not a playlist.");
            return;
        }

        Playlist pl = Manager.searchBar(owner).getPlaylistLoaded();

        if (pl.getOwner().equals(owner)) {
            Manager.partialResult.put("message",
                    "You cannot follow or unfollow your own playlist.");
            return;
        }

        if (pl.getFollowers().contains(owner)) {
            pl.removeFollower(owner);
            Manager.partialResult.put("message", "Playlist unfollowed successfully.");
        } else {
            pl.addFollower(owner);
            Manager.partialResult.put("message", "Playlist followed successfully.");
        }

        for (int i = 0; i < playlists.size(); i++) {
            if (playlists.get(i).getName().equals(pl.getName())) {
                playlists.set(i, pl);
            }
        }
    }

    /**
     * like/unlike the current song
     */
    public static void like() {
        final String username = command.getUsername();
        if (checkUser()) {
            return;
        }

        if (getCurrentUser().isOffline()) {
            Manager.partialResult.put("message", username + " is offline.");
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

        if (!sources.get(username).isSourceLoaded()) {
            Manager.partialResult.put("message", "Please load a source before liking or unliking.");
            return;
        }

        if (song == null && sources.get(username).isSourceLoaded()) {
            Manager.partialResult.put("message", "Loaded source is not a song.");
            return;
        }


        if (!Manager.getUser(username).getLikedSongs().contains(song)) {
            Manager.getUser(username).addLikedSong(song);
            Manager.partialResult.put("message", "Like registered successfully.");
        } else {
            Manager.getUser(username).removeLikedSong(song);
            Manager.partialResult.put("message", "Unlike registered successfully.");
        }
    }

    /**
     * shows all playlists created by the specific user
     */
    public static void showPlaylists() {
        final String username = command.getUsername();
        if (checkUser()) {
            return;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode rez = objectMapper.createArrayNode();

        for (Playlist play : getUser(username).getPlaylists()) {
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
        Manager.partialResult.set("result", rez);
    }

    /**
     * shows all liked songs by the specific user
     */
    public static void showPreferredSongs() {
        final String username = command.getUsername();
        if (checkUser()) {
            return;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode songs = objectMapper.createArrayNode();

        for (Song sg : getUser(username).getLikedSongs()) {
            songs.add(sg.getName());
        }

        Manager.partialResult.set("result", songs);
    }

    /**
     * shows top 5 playlists on app
     */
    public static void getTop5Playlists() {
        ArrayList<Playlist> playlist = new ArrayList<>();

        for (Playlist play : playlists) {
            if (play.getVisibility().equals("public")) {
                playlist.add(play);
            }
        }

        for (int i = 0; i < playlist.size(); i++) {
            for (int j = 0; j < playlist.size(); j++) {
                if (playlist.get(i).numberOfFollowers() > playlist.get(j).numberOfFollowers()
                        || (playlist.get(i).getTime() < playlist.get(j).getTime()
                        && playlist.get(i).numberOfFollowers()
                        == playlist.get(j).numberOfFollowers())) {
                    Playlist temp = playlist.get(i);
                    playlist.set(i, playlist.get(j));
                    playlist.set(j, temp);
                }
            }
        }

        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode node = objectMapper.createArrayNode();

        for (int i = 0; i < playlist.size() && i < TOP5; i++) {
            node.add(playlist.get(i).getName());
        }

        Manager.partialResult.set("result", node);

    }

    /**
     * shows top 5 songs in app
     */
    public static void getTop5Songs() {

        ArrayList<Song> tempSongs = Library.getInstance().getSongs();
        int[] freqVec = new int[tempSongs.size()];

        for (User user : Library.getInstance().getUsers()) {
            for (Song song : user.getLikedSongs()) {
                freqVec[tempSongs.indexOf(song)]++;
            }
        }

        for (int i = tempSongs.size() - 1; i >= 0; i--) {
            for (int j = tempSongs.size() - 1; j > 0; j--) {
                if (freqVec[j] > freqVec[j - 1]) {
                    int temp = freqVec[j - 1];
                    freqVec[j - 1] = freqVec[j];
                    freqVec[j] = temp;

                    Song sg = tempSongs.get(j - 1);
                    tempSongs.set(j - 1, tempSongs.get(j));
                    tempSongs.set(j, sg);

                }
            }
        }

        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode node = objectMapper.createArrayNode();

        for (Song sg : tempSongs.subList(0, TOP5)) {
            node.add(sg.getName());

        }
        Manager.partialResult.set("result", node);

    }

    public static void switchConnectionStatus() {
        final String username = command.getUsername();
        if (checkUser()) {
            return;
        }

        getCurrentUser().changeStatus();
        Manager.partialResult.put("message", username + " has changed status successfully.");
    }

    public static void getOnlineUsers() {
        ArrayList<User> user = new ArrayList<>(users.values());
        user.removeIf(User::isOffline);
        user.sort(Comparator.comparing(User::getUsername));
        ArrayNode onlineUsers = objectMapper.createArrayNode();
        user.forEach((u) -> onlineUsers.add(u.getUsername()));
        Manager.partialResult.set("result", onlineUsers);
    }

    public static void addUser() {
        String username = command.getUsername();
        if (users.containsKey(username)) {
            Manager.partialResult.put("message", "The username "
                    + username + " is already taken.");
            return;
        }

        User user = new User(command.getUsername(), command.getAge(),
                command.getCity(), command.getType());
        users.put(username, user);
        Manager.addSource(username);
        Manager.partialResult.put("message", "The username "
                + username + " has been added successfully.");
    }

    public static void addAlbum() {
        final String username = command.getUsername();
        if (checkUser()) {
            return;
        }

        if (!Objects.equals(getCurrentUser().getUserType(), "artist")) {
            Manager.partialResult.put("message",
                    username + " is not an artist.");
            return;
        }

        if (getCurrentUser().getAlbums().contains(command.getName())) {
            Manager.partialResult.put("message",
                    username + " has another album with the same name.");
            return;
        }

        HashSet<String> set = new HashSet<>();
        for (Song song : command.getSongs()) {
            if (!set.add(song.getName())) {
                Manager.partialResult.put("message",
                        username + " has the same song at least twice in this album.");
                return;
            }
        }

        Album album = new Album(command.getUsername(), command.getName(),
                command.getReleaseYear(), command.getDescription());

        for (Song song : command.getSongs()) {
            song.setMaxDuration(song.getDuration());
            Library.getInstance().getSongs().add(song);
            album.addSong(song);
        }

        Manager.albums.put(command.getName(), album);
        getCurrentUser().getAlbums().add(album.getName());

        Manager.partialResult.put("message",
                username + " has added new album successfully.");
    }

    public static void showAlbums() {
        ArrayNode result = objectMapper.createArrayNode();
        for (Album album : albums.values()) {
            ObjectNode albumNode = objectMapper.createObjectNode();
            albumNode.put("name", album.getName());
            ArrayNode songs = objectMapper.createArrayNode();
            album.getSongs().forEach((s) -> songs.add(s.getName()));
            albumNode.set("songs", songs);
            result.add(albumNode);
        }
        Manager.partialResult.set("result", result);
    }

    public static void printCurrentPage() {
        Manager.partialResult.put("message",
                getCurrentUser().getCurrentPage().printCurrentPage());
    }

    public static void updateDeltaTime() {
        String username = command.getUsername();
        int nextTime = command.getTimestamp();
        deltaTime = nextTime - lastTime;
        lastTime = nextTime;
    }

    public static void commandInfo() {
        Manager.setPartialResult(objectMapper.createObjectNode());
        Manager.getPartialResult().put("command", command.getCommand());
        if (command.getUsername() != null) {
            Manager.getPartialResult().put("user", command.getUsername());
        }
        Manager.getPartialResult().put("timestamp", command.getTimestamp());
    }

    public static void getCurrentCommand(Command command) {
        Manager.command = command;
    }

    public static void checkSource() {
        String username = command.getUsername();

        if (username != null) {
            Manager.checkSource(username, command.getCommand());
        }
    }
    /**
     * checks the control flow of specific user
     * @param username the username of the user
     * @param command the command used
     */
    public static void checkSource(final String username, final String command) {
        if (!Manager.users.containsKey(username)) {
            return;
        }

        if (command.equals("search")) {
            sources.get(username).setSourceSearched(true);
            sources.get(username).setSourceLoaded(false);
        } else if (sources.get(username).isSourceSearched() && command.equals("select")) {
            sources.get(username).setSourceSelected(true);
            sources.get(username).setSourceSearched(false);
        } else if (sources.get(username).isSourceSelected() && command.equals("load")) {
            sources.get(username).setSourceLoaded(true);
            sources.get(username).setSourceSelected(false);
        }
    }
}

