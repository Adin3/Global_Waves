package main;

import com.fasterxml.jackson.core.ObjectCodec;
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

    private static final Map<String, User> users = new HashMap<>();

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

    public static void addUser(User user) {
        users.put(user.getUsername(), user);
    }

    public static User getUser(String username) {
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
        for (User user : users.values()) {
            user.getMusicplayer().updateDuration(deltaTime);
            user.getMusicplayer().updatePlayer();
        }
    }
    public static void addPlaylist(String username, String name, int time) {
        for (Playlist p : playlists) {
            if (p.getName().equals(name)) {
                Manager.partialResult.put("message", "A playlist with the same name already exists.");
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

    public static void addRemoveInPlaylist(String owner, int id) {
//        if (!Manager.getUser(owner).getType().isEmpty()) {
//            Manager.partialResult.put("message", "Please load a source before adding to or removing from the playlist.");
//            return;
//        }
        if (!Manager.getSource(owner).isSourceLoaded()) {
            Manager.partialResult.put("message", "Please load a source before adding to or removing from the playlist.");
            return;
        }

        if (!Manager.getUser(owner).getType().equals("song")) {
            Manager.partialResult.put("message", "The loaded source is not a song.");
            return;
        }

        if (getUser(owner).getPlaylists().size() < (id-1)) {
            Manager.partialResult.put("message", "The specified playlist does not exist.");
            return;
        }

//        if (!playlists.get(id-1).getOwner().equals(owner)) {
//            Manager.partialResult.put("message", "Successfully added to playlist.");
//            return;
//        }

        Playlist playlist = getUser(owner).getPlaylists().get(id-1);

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

    public static void switchVisibility(String username, int id) {

        if (getUser(username).getPlaylists().size() < (id-1)) {
            Manager.partialResult.put("message", "The specified playlist ID is too high.");
            return;
        }

        Playlist pl = getUser(username).getPlaylists().get(id-1);

        pl.changeVisibility();

        Manager.partialResult.put("message", "Visibility status updated successfully to " + pl.getVisibility() + ".");

        for (int i = 0; i < playlists.size(); i++) {
            if (playlists.get(i).getName().equals(pl.getName())) {
                playlists.set(i, pl);
            }
        }
    }

    public static void follow(String owner) {

        if (!Manager.getSource(owner).isSourceSelected()) {
            Manager.partialResult.put("message", "Please select a source before following or unfollowing.");
            return;
        }

        if (!Manager.getUser(owner).getType().equals("playlist")) {
            Manager.partialResult.put("message", "The selected source is not a playlist.");
            return;
        }

        Playlist pl = Manager.searchBar(owner).getPlaylistLoaded();

        if (pl.getOwner().equals(owner)) {
            Manager.partialResult.put("message", "You cannot follow or unfollow your own playlist.");
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


    public static void like(String username) {

        Song song = null;
        if (Manager.getUser(username).getType().equals("song")) {
            song = Manager.searchBar(username).getSongLoaded();
        } else {
            song = Manager.musicPlayer(username).getCurrentSong();
        }

        if (song == null && sources.get(username).isSourceLoaded()) {
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
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode rez = objectMapper.createArrayNode();

        for (Playlist play : getUser(username).getPlaylists()) {
            ObjectNode status = objectMapper.createObjectNode();
            if (play != null) {

                status.put("name", play.getName());
                ArrayNode node = objectMapper.createArrayNode();


                for (Song sg : play.getSongs())
                    node.add(sg.getName());

                status.set("songs", node);
//                if (play.isVisibility()) {
//                    status.put("visibility", "public");
//                } else {
//                    status.put("visibility", "private");
//                }
                status.put("visibility", play.getVisibility());
                status.put("followers", play.getFollowers().size());
            }
            rez.add(status);
        }
        Manager.partialResult.set("result", rez);
    }

    public static void showPreferredSongs(String username) {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode songs = objectMapper.createArrayNode();

        for (Song sg : getUser(username).getLikedSongs()) {
            songs.add(sg.getName());
        }

        Manager.partialResult.set("result", songs);
    }

    public static void getTop5Playlists() {
        ArrayList<Playlist> playlist = new ArrayList<>();

        for (Playlist play : playlists) {
            if (play.getVisibility().equals("public"))
                playlist.add(play);
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

        for (int i = 0; i < playlist.size() && i < 5; i++) {
//            System.out.print(pl.getName());
//            System.out.print(pl.numberOfFollowers());
//            System.out.println(pl.getTime());
            node.add(playlist.get(i).getName());
        }
//        System.out.println("-----------");

        Manager.partialResult.set("result", node);

    }

    public static void getTop5Songs() {

        ArrayList<Song> tempSongs = Library.getInstance().getSongs();
        int[] freqVec = new int[tempSongs.size()];

        for (User user : Library.getInstance().getUsers()) {
            for (Song song : user.getLikedSongs()) {
                freqVec[tempSongs.indexOf(song)]++;
            }
        }

        for (int i = 0; i < freqVec.length; i++) {
            System.out.print(tempSongs.get(i).getName());
            System.out.print(" ");
            System.out.println(freqVec[i]);
        }

        for (int i = tempSongs.size() - 1; i >= 0; i--) {
            for (int j = tempSongs.size() - 1; j > 0; j--) {
                if (freqVec[j] > freqVec[j-1]) {
                    int temp = freqVec[j-1];
                    freqVec[j-1] = freqVec[j];
                    freqVec[j] = temp;

                    Song sg = tempSongs.get(j-1);
                    tempSongs.set(j-1, tempSongs.get(j));
                    tempSongs.set(j, sg);

                }
            }
        }

        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode node = objectMapper.createArrayNode();

        for (Song sg : tempSongs.subList(0, 5)) {
            System.out.print(sg.getName());
            System.out.println(freqVec[tempSongs.indexOf(sg)]);
            node.add(sg.getName());

        }
        System.out.println("---------------");
        Manager.partialResult.set("result", node);

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
