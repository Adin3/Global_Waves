package program;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import program.command.Command;
import program.format.Album;
import program.format.Playlist;
import program.player.Player;
import program.searchbar.SearchBar;
import program.user.User;

import java.util.*;

public final class Manager {

    private static ObjectMapper objectMapper = new ObjectMapper();
    private static Manager instance = null;
    @Getter
    private static Map<String, User> users = new HashMap<>();

    @Getter
    private static ArrayList<String> artists = new ArrayList<>();

    @Getter
    private static ArrayList<String> hosts = new ArrayList<>();

    @Getter
    private static ArrayList<String> normals = new ArrayList<>();

    @Getter
    private static Map<String, Album> albums = new HashMap<>();

    @Getter
    private static Map<String, Playlist> playlists = new HashMap<>();
    @Getter @Setter
    private static ArrayNode result;
    @Getter @Setter
    private static ObjectNode partialResult;
    @Getter
    private static Map<String, Source> sources = new HashMap<>();

    @Getter
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

    public static boolean checkUser() {
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
            user.updatePlayer(Manager.deltaTime);
        }
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

    public static <T> T findObjectByCondition(List<T> list, T condition) {
        for (T obj : list) {
            if (condition.equals(obj)) {
                return obj;
            }
        }
        return null;
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

