package main;

import fileio.input.LibraryInput;
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

    private static ArrayList<Playlist> playlists = new ArrayList<>();

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

    public static void updatePlayers(int deltaTime) {
        for (UserInput user : users.values()) {
            user.getMusicplayer().updateDuration(deltaTime);
            user.getMusicplayer().updatePlayer();
        }
    }
    public static boolean addPlaylist(String username, String name) {
        for (Playlist p : playlists) {
            if (p.getName().equals(name)) return false;
        }

        Playlist p = new Playlist(username, name);
        playlists.add(p);
        users.get(username).addPlaylist(p);
        return true;
    }

//    public static void removePlaylist(String username, String name) {
//        if (username.equals(playlists.get(name).getOwner())) {
//            playlists.remove(name);
//        }
//    }

    public static Playlist playlist(String owner, int id) {
        if (playlists.size() < (id-1) && playlists.get(id-1).getOwner().equals(owner)) {
            return null;
        }
        return playlists.get(id-1);
    }
}
