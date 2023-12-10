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
import program.user.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Objects;

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

        if (Manager.getCurrentUser().isOffline()) {
            ArrayNode results = objectMapper.createArrayNode();
            Manager.getPartialResult().set("results", results);
            Manager.getPartialResult().put("message", username + " is offline.");
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
        if (Manager.checkUser()) {
            return;
        }
        Manager.searchBar(username).select(command.getItemNumber(), username);
    }

    public static void load() {
        String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }

        if (!Manager.getSource(username).isSourceLoaded()) {
            Manager.getUser(username).setMusicPlayer();
        }

        Manager.musicPlayer(username).load();
    }

    public static void status() {
        String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }
        Manager.musicPlayer(username).status();
    }

    public static void playPause() {
        String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }
        Manager.musicPlayer(username).playPause();
    }

    public static void repeat() {
        String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }
        Manager.musicPlayer(username).repeat();
    }

    public static void shuffle() {
        String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }
        Manager.musicPlayer(username).shuffle(command.getSeed());
    }

    public static void next() {
        String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }
        Manager.musicPlayer(username).next();
    }

    public static void prev() {
        String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }
        Manager.musicPlayer(username).prev();
    }

    public static void forward() {
        String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }
        Manager.musicPlayer(username).forward();
    }

    public static void backward() {
        String username = command.getUsername();
        if (Manager.checkUser()) {
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
    public static void addRemoveInPlaylist() {
        final String owner = command.getUsername();
        final int id = command.getPlaylistId();

        if (Manager.checkUser()) {
            return;
        }

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
    public static void switchVisibility() {
        final String username = command.getUsername();
        final int id = command.getPlaylistId();

        if (Manager.checkUser()) {
            return;
        }

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
    /**
     * follow/unfollow the current playlist
     */
    public static void follow() {
        final String owner = command.getUsername();

        if (Manager.checkUser()) {
            return;
        }
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
    public static void like() {
        final String username = command.getUsername();
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
    public static void showPlaylists() {
        final String username = command.getUsername();
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
    public static void showPreferredSongs() {
        final String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode songs = objectMapper.createArrayNode();

        for (Song sg : Manager.getUser(username).getLikedSongs()) {
            songs.add(sg.getName());
        }

        Manager.getPartialResult().set("result", songs);
    }

    /**
     * shows top 5 playlists on app
     */
    public static void getTop5Playlists() {
        ArrayList<Playlist> playlist = new ArrayList<>();

        for (Playlist play : Manager.getPlaylists()) {
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
        Manager.getPartialResult().set("result", node);

    }

    public static void switchConnectionStatus() {
        final String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }

        Manager.getCurrentUser().changeStatus();
        Manager.getPartialResult().put("message", username + " has changed status successfully.");
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

        User user = new User(command.getUsername(), command.getAge(),
                command.getCity(), command.getType());
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

        if (!Objects.equals(Manager.getCurrentUser().getUserType(), "artist")) {
            Manager.getPartialResult().put("message",
                    username + " is not an artist.");
            return;
        }

        if (Manager.getCurrentUser().getAlbums().contains(command.getName())) {
            Manager.getPartialResult().put("message",
                    username + " has another album with the same name.");
            return;
        }

        HashSet<String> set = new HashSet<>();
        for (Song song : command.getSongs()) {
            if (!set.add(song.getName())) {
                Manager.getPartialResult().put("message",
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

        Manager.getAlbums().put(command.getName(), album);
        Manager.getCurrentUser().getAlbums().add(album.getName());

        Manager.getPartialResult().put("message",
                username + " has added new album successfully.");
    }

    public static void showAlbums() {
        ArrayNode result = objectMapper.createArrayNode();
        for (String albumName : Manager.getCurrentUser().getAlbums()) {
            Album album = Manager.getAlbums().get(albumName);
            ObjectNode albumNode = objectMapper.createObjectNode();
            albumNode.put("name", album.getName());
            ArrayNode songs = objectMapper.createArrayNode();
            album.getSongs().forEach((s) -> songs.add(s.getName()));
            albumNode.set("songs", songs);
            result.add(albumNode);
        }
        Manager.getPartialResult().set("result", result);
    }

    public static void printCurrentPage() {
        Manager.getPartialResult().put("message",
                Manager.getCurrentUser().getCurrentPage().printCurrentPage());
    }

    public static void addEvent() {

    }
}
