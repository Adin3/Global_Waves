package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class Manager {

    private static final int TOP5 = 5;
    private static Manager instance = null;

    private static Map<String, User> users = new HashMap<>();

    @Getter
    private static ArrayList<Playlist> playlists = new ArrayList<>();
    @Getter @Setter
    private static ArrayNode result;
    @Getter @Setter
    private static ObjectNode partialResult;
    private static Map<String, Source> sources = new HashMap<>();

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
     * @param deltaTime variation of time
     */
    public static void updatePlayers(final int deltaTime) {
        for (User user : users.values()) {
            user.getMusicplayer().updateDuration(deltaTime);
            user.getMusicplayer().updatePlayer();
        }
    }
    /**
     * Adds a new playlist
     * @param username the username of the user
     * @param name the name of the playlist
     * @param time the creation date
     */
    public static void addPlaylist(final String username, final String name, final int time) {
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

//    public static void removePlaylist(String username, String name) {
//        if (username.equals(playlists.get(name).getOwner())) {
//            playlists.remove(name);
//        }
//    }
    /**
     * adds/removes the current song in the playlist
     * @param owner the username of the user
     * @param id the id of the playlist
     */
    public static void addRemoveInPlaylist(final String owner, final int id) {
        if (!Manager.getSource(owner).isSourceLoaded()) {
            Manager.partialResult.put("message",
                    "Please load a source before adding to or removing from the playlist.");
            return;
        }

        if (!Manager.getUser(owner).getType().equals("song")
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

//        if (!playlists.get(id-1).getOwner().equals(owner)) {
//            Manager.partialResult.put("message", "Successfully added to playlist.");
//            return;
//        }

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
     * @param username the username of the user
     * @param id the id of the playlist
     */
    public static void switchVisibility(final String username, final int id) {

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
     * @param owner the username of the user
     */
    public static void follow(final String owner) {

        if (!Manager.getSource(owner).isSourceSelected()) {
            Manager.partialResult.put("message",
                    "Please select a source before following or unfollowing.");
            return;
        }

        if (!Manager.getUser(owner).getType().equals("playlist")) {
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
     * @param username the username of the user
     */
    public static void like(final String username) {

        Song song = null;
        if (Manager.getUser(username).getType().equals("song")) {
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
     * @param username the username of the user
     */
    public static void showPlaylists(final String username) {
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
     * @param username the username of the user
     */
    public static void showPreferredSongs(final String username) {
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
                        || playlist.get(i).getTime() < playlist.get(j).getTime()) {
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

    /**
     * checks the control flow of specific user
     * @param username the username of the user
     * @param command the command used
     */
    public static void checkSource(final String username, final String command) {

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

@Getter @Setter
class Source {

    private boolean sourceSearched = false;

    private boolean sourceSelected = false;

    private boolean sourceLoaded = false;
}
