package main;

import fileio.input.LibraryInput;
import fileio.input.UserInput;
import lombok.Getter;
import lombok.Setter;

import javax.security.auth.login.CredentialNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class Manager {

    private static Manager instance = null;

    private static final Map<String, UserInput> users = new HashMap<>();

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

    public static MusicPlayer musicPlayer(String username) {
        return users.get(username).getMusicplayer();
    }

    public static void updatePlayers(int deltaTime) {
        for (UserInput user : users.values()) {
            user.getMusicplayer().updateDuration(deltaTime);
            user.getMusicplayer().updatePlayer();
        }
    }

}
