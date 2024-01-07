package program.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import program.command.Command;
import program.format.Library;
import program.format.Playlist;
import program.format.Album;
import program.format.Song;
import program.user.ArtistUser;
import program.user.HostUser;
import program.user.NormalUser;
import program.user.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;


public final class CommandList {

    private static ObjectMapper objectMapper = new ObjectMapper();

    @Setter @Getter
    private static Command command;

    private static final int TOP5 = 5;

    private CommandList() { }
    /**
     * search command wrapper
     */
    public static void search() {
        String username = command.getUsername();

        if (Manager.checkUser()) {
            return;
        }

        Manager.getCurrentUser().search();
    }
    /**
     * select command wrapper
     */
    public static void select() {
        String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }
        Manager.getCurrentUser().select();
    }

    /**
     * load command wrapper
     */
    public static void load() {
        String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }

        Manager.getCurrentUser().load();
    }

    /**
     * status command wrapper
     */
    public static void status() {
        String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }
        Manager.getCurrentUser().status();
    }

    /**
     * playPause command wrapper
     */
    public static void playPause() {
        String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }
        Manager.getCurrentUser().playPause();
    }

    /**
     * repeat command wrapper
     */
    public static void repeat() {
        String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }
        Manager.getCurrentUser().repeat();
    }

    /**
     * shuffle command wrapper
     */
    public static void shuffle() {
        String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }
        Manager.getCurrentUser().shuffle();
    }

    /**
     * next command wrapper
     */
    public static void next() {
        String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }
        Manager.getCurrentUser().next();
    }

    /**
     * prev command wrapper
     */
    public static void prev() {
        String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }
        Manager.getCurrentUser().prev();
    }

    /**
     * forward command wrapper
     */
    public static void forward() {
        String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }
        Manager.getCurrentUser().forward();
    }

    /**
     * backward command wrapper
     */
    public static void backward() {
        String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }
        Manager.getCurrentUser().backward();
    }
    /**
     * createPlaylist command wrapper
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
     * addRemoveInPlaylist command wrapper
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
     * switchVisibility command wrapper
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
     * follow command wrapper
     */
    public static void follow() {
        final String owner = command.getUsername();

        if (Manager.checkUser()) {
            return;
        }

        Manager.getCurrentUser().follow();
    }

    /**
     * like command wrapper
     */
    public static void like() {
        final String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }

        Manager.getCurrentUser().like();
    }

    /**
     * showPlaylists command wrapper
     */
    public static void showPlaylists() {
        final String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }

        Manager.getCurrentUser().showPlaylists();
    }

    /**
     * showPreferredSongs command wrapper
     */
    public static void showPreferredSongs() {
        final String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }
        Manager.getCurrentUser().showPreferredSongs();
    }

    /**
     * getTop5Playlists command wrapper
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

        ArrayNode node = objectMapper.createArrayNode();

        for (int i = 0; i < playlist.size() && i < TOP5; i++) {
            node.add(playlist.get(i).getName());
        }

        Manager.getPartialResult().set("result", node);

    }

    /**
     * getTop5Songs command wrapper
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

        ArrayNode node = objectMapper.createArrayNode();

        for (Song sg : tempSongs.subList(0, TOP5)) {
            node.add(sg.getName());

        }
        Manager.getPartialResult().set("result", node);

    }
    /**
     * switchConnectionStatus command wrapper
     */
    public static void switchConnectionStatus() {
        final String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }

        Manager.getCurrentUser().switchConnectionStatus();
    }

    /**
     * getOnlineUsers command wrapper
     */
    public static void getOnlineUsers() {
        ArrayList<User> user = new ArrayList<>(Manager.getUsers().values());
        user.removeIf(User::isOffline);
        user.removeIf(User::isNotNormalUser);
        user.sort(Comparator.comparing(User::getUsername));
        ArrayNode onlineUsers = objectMapper.createArrayNode();
        user.forEach((u) -> onlineUsers.add(u.getUsername()));
        Manager.getPartialResult().set("result", onlineUsers);
    }

    /**
     * addUser command wrapper
     */
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
            default -> { }
        }
        Manager.getUsers().put(username, user);
        Manager.addSource(username);
        Manager.getPartialResult().put("message", "The username "
                + username + " has been added successfully.");
    }
    /**
     * addAlbum command wrapper
     */
    public static void addAlbum() {
        final String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }

        Manager.getCurrentUser().addAlbum();
    }

    /**
     * showAlbums command wrapper
     */
    public static void showAlbums() {
        Manager.getCurrentUser().showAlbums();
    }

    /**
     * printCurrentPage command wrapper
     */
    public static void printCurrentPage() {
        if (Manager.getCurrentUser().isOffline()) {
            Manager.getPartialResult().put("message",
                    Manager.getCurrentUser().getUsername() + " is offline.");
            return;
        }
        Manager.getCurrentUser().printCurrentPage();
    }
    /**
     * addEvent command wrapper
     */
    public static void addEvent() {
        final String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }

        Manager.getCurrentUser().addEvent();
    }
    /**
     * addMerch command wrapper
     */
    public static void addMerch() {
        final String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }

        Manager.getCurrentUser().addMerch();
    }

    /**
     * getAllUsers command wrapper
     */
    public static void getAllUsers() {
        ArrayList<String> user = new ArrayList<>();

        user.addAll(Manager.getNormals());
        user.addAll(Manager.getArtists());
        user.addAll(Manager.getHosts());

        ArrayNode onlineUsers = objectMapper.createArrayNode();
        user.forEach(onlineUsers::add);
        Manager.getPartialResult().set("result", onlineUsers);
    }

    /**
     * deleteUser command wrapper
     */
    public static void deleteUser() {
        final String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }

        Manager.getUser(username).deleteUser();
    }
    /**
     * addPodcast command wrapper
     */
    public static void addPodcast() {
        final String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }

        Manager.getUser(username).addPodcast();
    }
    /**
     * addAnnouncement command wrapper
     */
    public static void addAnnouncement() {
        final String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }

        Manager.getUser(username).addAnnouncement();
    }
    /**
     * removeAnnouncement command wrapper
     */
    public static void removeAnnouncement() {
        final String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }

        Manager.getUser(username).removeAnnouncement();
    }
    /**
     * showPodcasts command wrapper
     */
    public static void showPodcasts() {
        final String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }

        Manager.getUser(username).showPodcasts();
    }
    /**
     * removeAlbum command wrapper
     */
    public static void removeAlbum() {
        final String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }

        Manager.getUser(username).removeAlbum();
    }
    /**
     * changePage command wrapper
     */
    public static void changePage() {
        final String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }

        Manager.getUser(username).changePage();
    }
    /**
     * removePodcast command wrapper
     */
    public static void removePodcast() {
        final String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }

        Manager.getUser(username).removePodcast();
    }
    /**
     * removeEvent command wrapper
     */
    public static void removeEvent() {
        final String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }

        Manager.getUser(username).removeEvent();
    }
    /**
     * getTop5Albums command wrapper
     */
    public static void getTop5Albums() {
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

        Map<String, Integer> albumsValue = new HashMap<>();
        ArrayList<String> sortedAlbums = new ArrayList<>();
        for (Album a : Manager.getAlbums()) {
            albumsValue.put(a.getName(), 0);
            sortedAlbums.add(a.getName());
        }

        for (int i = 0; i < tempSongs.size() && freqVec[i] != 0; i++) {
            if (albumsValue.containsKey(tempSongs.get(i).getAlbum())) {
                albumsValue.put(tempSongs.get(i).getAlbum(),
                        albumsValue.get(tempSongs.get(i).getAlbum()) + freqVec[i]);
            }
        }
        sortedAlbums.sort(new Comparator<String>() {
            @Override
            public int compare(final String o1, final String o2) {
                int rez = albumsValue.get(o1) - albumsValue.get(o2);

                if (rez == 0) {
                    return o1.compareTo(o2);
                }

                return -rez;
            }
        });
        Set<String> uniqueAlbums = new LinkedHashSet<>(sortedAlbums);

        ArrayNode result = objectMapper.createArrayNode();
        int times = 0;
        for (String s : uniqueAlbums) {
            result.add(s);
            times++;
            if (times == TOP5) {
                break;
            }
        }
        Manager.getPartialResult().set("result", result);

    }
    /**
     * getTop5Artists command wrapper
     */
    public static void getTop5Artists() {

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

        Map<String, Integer> artistsValue = new HashMap<>();
        ArrayList<String> sortedArtists = new ArrayList<>();
        for (String a : Manager.getArtists()) {
            artistsValue.put(a, 0);
            sortedArtists.add(a);
        }

        for (int i = 0; i < tempSongs.size() && freqVec[i] != 0; i++) {
            if (artistsValue.containsKey(tempSongs.get(i).getArtist())) {
                artistsValue.put(tempSongs.get(i).getArtist(),
                        artistsValue.get(tempSongs.get(i).getArtist()) + freqVec[i]);
            }
        }
        sortedArtists.sort(new Comparator<String>() {
            @Override
            public int compare(final String o1, final String o2) {
                int rez = artistsValue.get(o1) - artistsValue.get(o2);

                if (rez == 0) {
                    return o1.compareTo(o2);
                }

                return -rez;
            }
        });

        ArrayNode result = objectMapper.createArrayNode();
        int times = 0;
        for (String s : sortedArtists) {
            result.add(s);
            times++;
            if (times == TOP5) {
                break;
            }
        }
        Manager.getPartialResult().set("result", result);
    }

    /**
     * wrapped command wrapper
     */
    public static void wrapped() {
        final String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }

        Manager.getUser(username).wrapped();
    }

    /**
     * buyPremium command wrapper
     */
    public static void buyPremium() {
        final String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }

        Manager.getUser(username).buyPremium();
    }

    /**
     * cancelPremium command wrapper
     */
    public static void cancelPremium() {
        final String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }

        Manager.getUser(username).cancelPremium();
    }

    /**
     * endProgram command wrapper
     */
    public static void endProgram() {
        Manager.setPartialResult(objectMapper.createObjectNode());
        Manager.getPartialResult().put("command", "endProgram");
        ObjectNode result = objectMapper.createObjectNode();

        int rank = 1;
        for (String normal : Manager.getNormals()) {
            Manager.getUser(normal).calculateAllSongRevenue();
        }
        Manager.getPartialResult().set("result", result);
        Manager.orderArtists();
        for (String artist : Manager.getArtists()) {
            ObjectNode art = Manager.getUser(artist).endProgram(rank);
            if (art != null) {
                rank++;
                result.set(artist, art);
            }
        }
        Manager.getResult().add(Manager.getPartialResult());
    }

    /**
     * adBreak command wrapper
     */
    public static void adBreak() {
        final String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }

        Manager.getUser(username).adBreak();
    }


    /**
     * buyMerch command wrapper
     */
    public static void buyMerch() {
        final String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }

        Manager.getUser(username).buyMerch();
    }

    /**
     * seeMerch command wrapper
     */
    public static void seeMerch() {
        final String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }

        Manager.getUser(username).seeMerch();
    }


    /**
     * subscribe command wrapper
     */
    public static void subscribe() {
        final String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }

        Manager.getUser(username).subscribe();
    }


    /**
     * getNotifications command wrapper
     */
    public static void getNotifications() {
        final String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }

        Manager.getUser(username).getNotifications();
    }


    /**
     * previousPage command wrapper
     */
    public static void previousPage() {
        final String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }

        Manager.getUser(username).previousPage();
    }


    /**
     * nextPage command wrapper
     */
    public static void nextPage() {
        final String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }

        Manager.getUser(username).nextPage();
    }

    /**
     * updateRecommendations command wrapper
     */
    public static void updateRecommendations() {
        final String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }

        Manager.getUser(username).updateRecommendations();
    }

    /**
     * loadRecommendations command wrapper
     */
    public static void loadRecommendations() {
        final String username = command.getUsername();
        if (Manager.checkUser()) {
            return;
        }

        Manager.getUser(username).loadRecommendations();
    }

}
