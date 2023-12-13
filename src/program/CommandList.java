package program;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import program.command.Command;
import program.format.*;
import program.user.ArtistUser;
import program.user.HostUser;
import program.user.NormalUser;
import program.user.User;

import java.util.*;

public class CommandList {

    private static ObjectMapper objectMapper = new ObjectMapper();

    @Setter @Getter
    private static Command command;

    private static final int TOP5 = 5;
    public static void search() {
        String username = command.getUsername();

        if (Manager.checkUser()) {
            return;
        }

        Manager.getCurrentUser().search();
    }

    public static void select() {
        String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }
        Manager.getCurrentUser().select();
    }

    public static void load() {
        String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }

        Manager.getCurrentUser().load();
    }

    public static void status() {
        String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }
        Manager.getCurrentUser().status();
    }

    public static void playPause() {
        String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }
        Manager.getCurrentUser().playPause();
    }

    public static void repeat() {
        String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }
        Manager.getCurrentUser().repeat();
    }

    public static void shuffle() {
        String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }
        Manager.getCurrentUser().shuffle();
    }

    public static void next() {
        String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }
        Manager.getCurrentUser().next();
    }

    public static void prev() {
        String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }
        Manager.getCurrentUser().prev();
    }

    public static void forward() {
        String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }
        Manager.getCurrentUser().forward();
    }

    public static void backward() {
        String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }
        Manager.getCurrentUser().backward();
    }
    /**
     * Adds a new playlist
     */
    public static void createPlaylist() {
        final String username = command.getUsername();
        final String name = command.getPlaylistName();
        final int time = command.getTimestamp();

        if (Manager.checkUser()) {
            return;
        }

        Manager.getCurrentUser().createPlaylist();
    }

    /**
     * adds/removes the current song in the playlist
     */
    public static void addRemoveInPlaylist() {
        final String owner = command.getUsername();
        final int id = command.getPlaylistId();

        if (Manager.checkUser()) {
            return;
        }

        Manager.getCurrentUser().addRemoveInPlaylist();
    }
    /**
     * change the visibility of the playlist
     */
    public static void switchVisibility() {
        final String username = command.getUsername();
        final int id = command.getPlaylistId();

        if (Manager.checkUser()) {
            return;
        }

        Manager.getCurrentUser().switchVisibility();
    }
    /**
     * follow/unfollow the current playlist
     */
    public static void follow() {
        final String owner = command.getUsername();

        if (Manager.checkUser()) {
            return;
        }

        Manager.getCurrentUser().follow();
    }

    /**
     * like/unlike the current song
     */
    public static void like() {
        final String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }

        Manager.getCurrentUser().like();
    }

    /**
     * shows all playlists created by the specific user
     */
    public static void showPlaylists() {
        final String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }

        Manager.getCurrentUser().showPlaylists();
    }

    /**
     * shows all liked songs by the specific user
     */
    public static void showPreferredSongs() {
        final String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }
        Manager.getCurrentUser().showPreferredSongs();
    }

    /**
     * shows top 5 playlists on app
     */
    public static void getTop5Playlists() {
        ArrayList<Playlist> playlist = new ArrayList<>();

        for (Playlist play : Manager.getPlaylists().values()) {
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

        Manager.getPartialResult().set("result", node);

    }

    /**
     * shows top 5 songs in app
     */
    public static void getTop5Songs() {

        ArrayList<Song> tempSongs = new ArrayList<>(Library.getInstance().getSongs());
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
        Manager.getPartialResult().set("result", node);

    }

    public static void switchConnectionStatus() {
        final String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }

        Manager.getCurrentUser().switchConnectionStatus();
    }

    public static void getOnlineUsers() {
        ArrayList<User> user = new ArrayList<>(Manager.getUsers().values());
        user.removeIf(User::isOffline);
        user.removeIf(User::isNotNormalUser);
        user.sort(Comparator.comparing(User::getUsername));
        ArrayNode onlineUsers = objectMapper.createArrayNode();
        user.forEach((u) -> onlineUsers.add(u.getUsername()));
        Manager.getPartialResult().set("result", onlineUsers);
    }

    public static void addUser() {
        String username = command.getUsername();
        if (Manager.getUsers().containsKey(username)) {
            Manager.getPartialResult().put("message", "The username "
                    + username + " is already taken.");
            return;
        }

        User user = new User();
        switch (command.getType()) {
            case "artist" -> {
                Manager.getArtists().add(username);
                user = new ArtistUser(command.getUsername(), command.getAge(),
                        command.getCity(), command.getType());
            }
            case "host" -> {
                Manager.getHosts().add(username);
                user = new HostUser(command.getUsername(), command.getAge(),
                        command.getCity(), command.getType());
            }
            case "user" -> {
                Manager.getNormals().add(username);
                user = new NormalUser(command.getUsername(), command.getAge(),
                        command.getCity(), command.getType());
            }
        }
        Manager.getUsers().put(username, user);
        Manager.addSource(username);
        Manager.getPartialResult().put("message", "The username "
                + username + " has been added successfully.");
    }

    public static void addAlbum() {
        final String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }

        Manager.getCurrentUser().addAlbum();
    }

    public static void showAlbums() {
        Manager.getCurrentUser().showAlbums();
    }

    public static void printCurrentPage() {
        if (Manager.getCurrentUser().isOffline()) {
            Manager.getPartialResult().put("message",
                    Manager.getCurrentUser().getUsername() + " is offline.");
            return;
        }
        Manager.getCurrentUser().printCurrentPage();
    }

    public static void addEvent() {
        final String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }

        Manager.getCurrentUser().addEvent();
    }

    public static void addMerch() {
        final String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }

        Manager.getCurrentUser().addMerch();
    }

    public static void getAllUsers() {
        ArrayList<String> user = new ArrayList<>();

        user.addAll(Manager.getNormals());
        user.addAll(Manager.getArtists());
        user.addAll(Manager.getHosts());

        ArrayNode onlineUsers = objectMapper.createArrayNode();
        user.forEach(onlineUsers::add);
        Manager.getPartialResult().set("result", onlineUsers);
    }

    public static void deleteUser() {
        final String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }

        Manager.getUser(username).deleteUser();
    }

    public static void addPodcast() {
        final String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }

        Manager.getUser(username).addPodcast();
    }

    public static void addAnnouncement() {
        final String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }

        Manager.getUser(username).addAnnouncement();
    }

    public static void removeAnnouncement() {
        final String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }

        Manager.getUser(username).removeAnnouncement();
    }

    public static void showPodcasts() {
        final String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }

        Manager.getUser(username).showPodcasts();
    }

    public static void removeAlbum() {
        final String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }

        Manager.getUser(username).removeAlbum();
    }

    public static void changePage() {
        final String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }

        Manager.getUser(username).changePage();
    }

    public static void removePodcast() {
        final String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }

        Manager.getUser(username).removePodcast();
    }

    public static void removeEvent() {
        final String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }

        Manager.getUser(username).removeEvent();
    }

    public static void getTop5Albums() {

        Map<String, Integer> albumsLikes = new HashMap<>();
        for (Album album : Manager.getAlbums().values()) {
            int numberOfLikes = 0;
            for (Song song : album.getSongs()) {
                Song s = Manager.findObjectByCondition(Library.getInstance().getSongs(), song);
                if (s != null) {
                    numberOfLikes += s.getLikes();
                }
            }
            albumsLikes.put(album.getName(), numberOfLikes);
        }

        List<Map.Entry<String, Integer>> sortAlbums = new ArrayList<>(albumsLikes.entrySet());

        sortAlbums.sort(Comparator.comparing(Map.Entry::getValue));
        ArrayNode result = objectMapper.createArrayNode();
        for (int i = sortAlbums.size() - 1; i >= 0 && i > sortAlbums.size() - 6; i--) {
            result.add(sortAlbums.get(i).getKey());
            System.out.println(sortAlbums.get(i).getKey() + " " + sortAlbums.get(i).getValue());
        }
        Manager.getPartialResult().set("result", result);
    }
}
