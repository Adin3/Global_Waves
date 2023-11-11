package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.LibraryInput;
import fileio.input.SongInput;
import fileio.input.UserInput;
import lombok.Getter;
import lombok.Setter;

import javax.security.auth.login.CredentialNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Manager {

    private static Manager instance = null;

    private static final Map<String, UserInput> users = new HashMap<>();

    @Getter
    private static final ArrayList<Playlist> playlists = new ArrayList<>();

    public static ArrayNode result;
    public static ObjectNode partialResult;
    private static final Map<String, Source> sources = new HashMap<>();

    private Manager() {}

    public static Manager getInstance() {
        if (instance == null) {
            instance = new Manager();
        }
        return instance;
    }

    public static void addUser(UserInput user) {
        users.put(user.getUsername(), user);
    }

    public static UserInput getUser(String username) {
        return users.get(username);
    }

    public static SearchBar searchBar(String username) {
        return users.get(username).getSearchBar();
    }

    public static Player musicPlayer(String username) {
        return users.get(username).getMusicplayer();
    }

    public static void addSource(String username) {
        sources.put(username, new Source());
    }

    public static Source getSource(String username) {
        return sources.get(username);
    }

    public static void updatePlayers(int deltaTime) {
        for (UserInput user : users.values()) {
            user.getMusicplayer().updateDuration(deltaTime);
            user.getMusicplayer().updatePlayer();
        }
    }
    public static void addPlaylist(String username, String name) {
        for (Playlist p : playlists) {
            if (p.getName().equals(name)) {
                Manager.partialResult.put("message", "A playlist with the same name already exists.");
                return;
            }
        }

        Playlist p = new Playlist(username, name);
        playlists.add(p);
        users.get(username).addPlaylist(p);
        Manager.partialResult.put("message", "Playlist created successfully.");
    }

//    public static void removePlaylist(String username, String name) {
//        if (username.equals(playlists.get(name).getOwner())) {
//            playlists.remove(name);
//        }
//    }

    public static void addRemoveInPlaylist(String owner, int id) {
//        if (!Manager.getUser(owner).getType().isEmpty()) {
//            Manager.partialResult.put("message", "Please load a source before adding to or removing from the playlist.");
//            return;
//        }
        if (!Manager.getUser(owner).getType().equals("song")) {
            Manager.partialResult.put("message", "The loaded source is not a song.");
            return;
        }

        if (playlists.size() < (id-1)) {
            Manager.partialResult.put("message", "The specified playlist does not exist.");
            return;
        }

        if (searchBar(owner).getSongLoaded() == null) {
            Manager.partialResult.put("message", "Please load a source before adding to or removing from the playlist.");
            return;
        }

//        if (!playlists.get(id-1).getOwner().equals(owner)) {
//            Manager.partialResult.put("message", "Successfully added to playlist.");
//            return;
//        }

        Playlist playlist = playlists.get(id-1);
        if (playlist == null) {
            Manager.partialResult.put("message", "Successfully added to playlist.");
            return;
        }

        SongInput song = Manager.musicPlayer(owner).getSong();

        if (!playlist.getSongs().contains(song)) {
            playlist.addSong(song);
            Manager.partialResult.put("message", "Successfully added to playlist.");
        } else {
            playlist.removeSong(song);
            Manager.partialResult.put("message", "Successfully removed from playlist.");
        }
    }

    public static void like(String username) {

        SongInput song = Manager.searchBar(username).getSongLoaded();

        if (!Manager.getUser(username).getType().equals("song") && sources.get(username).isSourceLoaded()) {
            Manager.partialResult.put("message", "Loaded source is not a song.");
            return;
        }

        if (!sources.get(username).isSourceLoaded()) {
            Manager.partialResult.put("message", "Please load a source before liking or unliking.");
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

    public static void showPlaylists(String username) {
        Playlist play = Manager.searchBar(username).getPlaylistLoaded();
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode rez = objectMapper.createArrayNode();
        ObjectNode status = objectMapper.createObjectNode();
        if (play != null) {

            status.put("name", play.getName());
            ArrayNode node = objectMapper.createArrayNode();


            for (SongInput sg : play.getSongs())
                node.add(sg.getName());

            status.set("songs", node);
            status.put("visibility", play.getVisibility());
            status.put("followers", play.getFollowers());
        }
        rez.add(status);
        Manager.partialResult.set("result", rez);
    }

    public static void showPreferredSongs(String username) {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode songs = objectMapper.createArrayNode();

        for (SongInput sg : getUser(username).getLikedSongs()) {
            System.out.println(sg.getName());
            songs.add(sg.getName());
        }

        Manager.partialResult.set("result", songs);
    }

    public static void checkSource(String username, String command) {

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
