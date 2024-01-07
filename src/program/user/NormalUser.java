package program.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.UserInput;
import lombok.Getter;
import program.admin.Manager;
import program.format.Song;
import program.format.Playlist;
import program.format.Episode;
import program.format.Library;
import program.format.Merch;
import program.page.Page;
import program.player.Player;
import program.player.PlayerFactory;
import program.searchbar.SearchBar;
import program.searchbar.SearchBarFactory;

import java.util.Map;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.Comparator;
import java.util.Random;

public class NormalUser extends User implements SubscribeObserver {

    private static final int TOP5 = 5;

    private static final int SEC30_PASSED = 30;

    private static final double PREMIUM_PRICE = 1000000.0;
    @Getter
    private Player musicplayer = new Player();

    @Getter
    private SearchBar searchBar = new SearchBar();

    @Getter
    private String formatType;

    @Getter
    private final Page currentPage;

    @Getter
    private boolean premium = false;

    @Getter
    private final ArrayList<Playlist> playlists = new ArrayList<>();

    @Getter
    private final ArrayList<String> followedPlaylists = new ArrayList<>();

    @Getter
    private final ArrayList<Song> likedSongs = new ArrayList<>();

    private ArrayList<String> merch = new ArrayList<>();

    @Getter
    private Map<String, Integer> listenedSongs = new LinkedHashMap<String, Integer>();

    @Getter
    private Map<String, Integer> listenedAlbums = new LinkedHashMap<String, Integer>();

    @Getter
    private Map<String, Integer> listenedArtists = new LinkedHashMap<String, Integer>();

    @Getter
    private Map<String, Integer> listenedGenres = new LinkedHashMap<String, Integer>();

    @Getter
    private Map<String, Integer> listenedEpisodes = new LinkedHashMap<String, Integer>();

    @Getter
    private Map<String, Map<String, Integer>> premiumListens = new LinkedHashMap<>();

    @Getter
    private ArrayList<String> playlistsRecommended = new ArrayList<>();

    @Getter
    private ArrayList<Song> songsRecommended = new ArrayList<>();

    @Getter
    private ArrayList<Song> freeSongQueue = new ArrayList<>();

    private ArrayList<String> notifications = new ArrayList<>();

    private boolean lastSongRecommended = false;
    private int premiumListensSize = 0;

    public NormalUser() {
        this.currentPage = new Page(username);
    }
    public NormalUser(final UserInput user) {
        this.age = user.getAge();
        this.city = user.getCity();
        this.username = user.getUsername();
        this.userType = "user";
        idCount++;
        this.id = idCount;
        this.currentPage = new Page(username);
    }

    public NormalUser(final String username, final int age,
                      final String city, final String userType) {
        this.username = username;
        this.age = age;
        this.city = city;
        this.userType = userType;
        idCount++;
        this.id = idCount;
        this.currentPage = new Page(username);
    }
    /**
     * changes the status of the user
     */
    public void changeStatus() {
        switch (status) {
            case ONLINE -> {
                status = UserStatus.OFFLINE;
            }
            case OFFLINE -> {
                status = UserStatus.ONLINE;
            }
            default -> { }
        }
    }
    /**
     * updates the player
     */
    public void updatePlayer(final int deltaTime) {
        if (!isOffline() && musicplayer != null) {
            musicplayer.updateDuration(deltaTime);
            musicplayer.updatePlayer();
        }
    }
    /**
     * checks is user is offline
     */
    public boolean isOffline() {
        return status == User.UserStatus.OFFLINE;
    }
    /**
     * checks if user is a normal user
     */
    public boolean isNotNormalUser() {
        return !userType.equals("user");
    }
    /**
     * adds playlist to user
     * @param playlist a playlist
     */
    public void addPlaylist(final Playlist playlist) {
        playlists.add(playlist);
    }

    /**
     * adds song to liked song collection
     * @param song to be liked song
     */
    public void addLikedSong(final Song song) {
        likedSongs.add(song);
    }

    /**
     * removes song from liked song collection
     * @param song liked song
     */
    public void removeLikedSong(final Song song) {
        likedSongs.remove(song);
    }

    /**
     * saves the listened song
     * @param song the song that was listened
     */
    public void setListenedSong(final Song song) {
        int freq = listenedSongs.getOrDefault(song.getName(), 0) + 1;
        listenedSongs.put(song.getName(), freq);
        freq = listenedAlbums.getOrDefault(song.getAlbum(), 0) + 1;
        listenedAlbums.put(song.getAlbum(), freq);
        freq = listenedArtists.getOrDefault(song.getArtist(), 0) + 1;
        listenedArtists.put(song.getArtist(), freq);
        freq = listenedGenres.getOrDefault(song.getGenre(), 0) + 1;
        listenedGenres.put(song.getGenre(), freq);
        if (premium) {
            Map<String, Integer> listen;
            listen = premiumListens.getOrDefault(song.getArtist(), new HashMap<>());
            freq = listen.getOrDefault(song.getName(), 0) + 1;
            listen.put(song.getName(), freq);
            premiumListens.put(song.getArtist(), listen);
            premiumListensSize++;
        }
    }

    /**
     * saves the listened episode
     * @param episode the listened episode
     */
    public void setListenedEpisode(final Episode episode) {
        int freq = listenedEpisodes.getOrDefault(episode.getName(), 0) + 1;
        listenedEpisodes.put(episode.getName(), freq);
    }

    /**
     * creates new player based on searches
     */
    public void setMusicPlayer() {
        musicplayer = PlayerFactory.getInstance().createPlayer(formatType, username);
    }
    /**
     * creates a searchbar depending on the type searched
     * @param commandType search type
     */
    public void setSearchBar(final String commandType) {
        this.formatType = commandType;
        searchBar = SearchBarFactory.getInstance().createSearchBar(this.formatType, username);
    }
    /**
     * searches in library files, collections, or users
     */
    public void search() {
        if (isOffline()) {
            ArrayNode results = objectMapper.createArrayNode();
            Manager.getPartialResult().set("results", results);
            Manager.getPartialResult().put("message", username + " is offline.");
            return;
        }

        setSearchBar(Manager.getCommand().getType());

        searchBar.clearSearch();

        musicplayer.clearPlayer();

        searchBar.search(Manager.getCommand().getFilters());

        searchBar.searchDone();
    }
    /**
     * selects one element from the array returned by search
     */
    public void select() {
        searchBar.select(Manager.getCommand().getItemNumber(), username);
    }
    /**
     * loads a file or collection into player
     */
    public void load() {
        if (!Manager.getSource(username).isSourceLoaded()) {
            setMusicPlayer();
        }
        if (musicplayer == null) {
            Manager.getPartialResult().put("message",
                    "Please select a source before attempting to load.");
            Manager.getSource(username).setSourceLoaded(false);
            Manager.getSource(username).setSourceSelected(false);
            Manager.getSource(username).setSourceSearched(false);
            return;
        }

        musicplayer.load();
    }
    /**
     * shows the status of player
     */
    public void status() {
        musicplayer.status();
    }
    /**
     * toggles the play/pause of player
     */
    public void playPause() {
        musicplayer.playPause();
    }
    /**
     * toggles the repeat function of player
     */
    public void repeat() {
        musicplayer.repeat();
    }
    /**
     * shuffles the collection
     */
    public void shuffle() {
        musicplayer.shuffle(Manager.getCommand().getSeed());
    }
    /**
     * goes forward to next file
     */
    public void next() {
        musicplayer.next();
    }
    /**
     * goes back to previous file
     */
    public void prev() {
        musicplayer.prev();
    }
    /**
     * skips forward for 90 seconds
     */
    public void forward() {
        musicplayer.forward();
    }
    /**
     * goes backward for 90 seconds
     */
    public void backward() {
        musicplayer.backward();
    }
    /**
     * Adds a new playlist
     */
    public void createPlaylist() {
        final String name = Manager.getCommand().getPlaylistName();
        final int time = Manager.getCommand().getTimestamp();

        if (Manager.checkUser()) {
            return;
        }

        for (Playlist p : Manager.getPlaylists().values()) {
            if (p.getName().equals(name)) {
                Manager.getPartialResult().put("message",
                        "A playlist with the same name already exists.");
                return;
            }
        }

        Playlist p = new Playlist(username, name, time);
        Manager.getPlaylists().put(p.getName(), p);
        this.playlists.add(p);
        Manager.getPartialResult().put("message", "Playlist created successfully.");
    }

    /**
     * adds/removes the current song in the playlist
     */
    public void addRemoveInPlaylist() {
        final String owner = Manager.getCommand().getUsername();
        final int id = Manager.getCommand().getPlaylistId();

        if (!Manager.getSource(owner).isSourceLoaded()) {
            Manager.getPartialResult().put("message",
                    "Please load a source before adding to or removing from the playlist.");
            return;
        }

        if (Manager.musicPlayer(owner).getSong() == null) {
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
    }
    /**
     * change the visibility of the playlist
     */
    public void switchVisibility() {
        final int id = Manager.getCommand().getPlaylistId();
        if (Manager.getUser(username).getPlaylists().size() <= (id - 1)) {
            Manager.getPartialResult().put("message", "The specified playlist ID is too high.");
            return;
        }

        Playlist pl = Manager.getUser(username).getPlaylists().get(id - 1);

        pl.changeVisibility();

        Manager.getPartialResult().put("message",
                "Visibility status updated successfully to " + pl.getVisibility() + ".");
    }

    /**
     * lets user follow a playlist
     */
    public void follow() {
        final String owner = Manager.getCommand().getUsername();

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
            followedPlaylists.remove(pl.getName());
            Manager.getPlaylists().get(pl.getName()).removeFollower(owner);
            Manager.getPartialResult().put("message", "Playlist unfollowed successfully.");
        } else {
            followedPlaylists.add(pl.getName());
            Manager.getPlaylists().get(pl.getName()).addFollower(owner);
            Manager.getPartialResult().put("message", "Playlist followed successfully.");
        }
    }

    /**
     * like/unlike the current song
     */
    public void like() {
        final String username = Manager.getCommand().getUsername();
        if (Manager.checkUser()) {
            return;
        }

        if (Manager.getCurrentUser().isOffline()) {
            Manager.getPartialResult().put("message", username + " is offline.");
            return;
        }

        Song song = null;
        if (formatType.equals("song")) {
            song = musicplayer.getSong();
            if (song != null) {
                song = Manager.findObjectByCondition(Library.getInstance().getSongs(), song);
            }
        } else {
            song = Manager.musicPlayer(username).getCurrentSong();
            if (song != null) {
                song = Manager.findObjectByCondition(Library.getInstance().getSongs(), song);
            }
        }

        if (!Manager.getSources().get(username).isSourceLoaded()) {
            Manager.getPartialResult().put("message",
                    "Please load a source before liking or unliking.");
            return;
        }

        if (song == null && Manager.getSources().get(username).isSourceLoaded()) {
            Manager.getPartialResult().put("message", "Loaded source is not a song.");
            return;
        }


        if (!likedSongs.contains(song)) {
            addLikedSong(song);
            song.addLike();
            Manager.getPartialResult().put("message", "Like registered successfully.");
        } else {
            removeLikedSong(song);
            song.removeLike();
            Manager.getPartialResult().put("message", "Unlike registered successfully.");
        }
    }

    /**
     * shows all playlists created by the specific user
     */
    public void showPlaylists() {
        final String username = Manager.getCommand().getUsername();
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
    public void showPreferredSongs() {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode songs = objectMapper.createArrayNode();

        for (Song sg : Manager.getUser(username).getLikedSongs()) {
            songs.add(sg.getName());
        }

        Manager.getPartialResult().set("result", songs);
    }
    /**
     * switches the connection status to online or offline
     */
    public void switchConnectionStatus() {
        Manager.getCurrentUser().changeStatus();
        Manager.getPartialResult().put("message", username + " has changed status successfully.");
    }
    /**
     * prints the current page
     */
    public void printCurrentPage() {
        if (Manager.getCurrentUser().isOffline()) {
            Manager.getPartialResult().put("message",
                    Manager.getCurrentUser().getUsername() + " is offline.");
            return;
        }
        Manager.getPartialResult().put("message",
                Manager.getCurrentUser().getCurrentPage().printCurrentPage());
    }
    /**
     * changes the page to nextPage
     */
    public void changePage() {
        if (Manager.getCurrentUser().isOffline()) {
            Manager.getPartialResult().put("message",
                    username + " is offline.");
            return;
        }

        if (currentPage.changePage(Manager.getCommand().getNextPage())) {
            Manager.getPartialResult().put("message",
                    username + " accessed " + Manager.getCommand().getNextPage()
                            + " successfully.");
        } else {
            Manager.getPartialResult().put("message",
                    username + " is trying to access a non-existent page.");
        }
    }
    /**
     * removes an album
     */
    public void deleteUser() {
        boolean used = isUsed();

        if (!used) {
            Manager.getNormals().remove(username);
            Manager.getSources().remove(username);
            Manager.getUsers().remove(username);
            Manager.getUsers().values().forEach(user -> {
                if (user.getFollowedPlaylists() != null) {
                    user.getFollowedPlaylists().removeIf(pl ->
                            Manager.getPlaylists().get(pl).getOwner().equals(username)
                    );
                }
            });
            Manager.getPlaylists().values().forEach(playlist ->
                playlist.removeFollower(username));
            Manager.getPartialResult().put("message",
                    username + " was successfully deleted.");
            return;
        }
        Manager.getPartialResult().put("message",
                username + " can't be deleted.");
    }

    private boolean isUsed() {
        boolean used = false;
        if (premium) {
            return true;
        }
        for (User user : Manager.getUsers().values()) {
            if (user.getUserType().equals("user")) {
                Playlist play = user.getMusicplayer().getPlaylist();
                if (play != null && play.getOwner().equals(username)
                        && user.getMusicplayer().getCurrentSong() != null) {
                    used = true; break;
                }
            }
        }
        return used;
    }

    /**
     * @return top 5 artists
     */
    private ObjectNode topArtists() {
        ObjectNode topArtists = objectMapper.createObjectNode();
        ArrayList<String> artists = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : listenedArtists.entrySet()) {
            artists.add(entry.getKey());
        }

        artists.sort(new Comparator<String>() {
            @Override
            public int compare(final String o1, final String o2) {
                int rez = listenedArtists.get(o1) - listenedArtists.get(o2);

                if (rez == 0) {
                    return o1.compareTo(o2);
                }

                return -rez;
            }
        });

        while (artists.size() > TOP5) {
            artists.remove(artists.size() - 1);
        }

        for (String name : artists) {
            topArtists.put(name, listenedArtists.get(name));
        }
        return topArtists;
    }
    /**
     * @return top 5 genres
     */
    private ObjectNode topGenres() {
        ObjectNode topGenres = objectMapper.createObjectNode();
        ArrayList<String> genres = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : listenedGenres.entrySet()) {
            genres.add(entry.getKey());
        }
        genres.sort(new Comparator<String>() {
            @Override
            public int compare(final String o1, final String o2) {
                int rez = listenedGenres.get(o1) - listenedGenres.get(o2);

                if (rez == 0) {
                    return o1.compareTo(o2);
                }

                return -rez;
            }
        });

        while (genres.size() > TOP5) {
            genres.remove(genres.size() - 1);
        }

        for (String name : genres) {
            topGenres.put(name, listenedGenres.get(name));
        }
        return topGenres;
    }
    /**
     * @return top 5 songs
     */
    private ObjectNode topSongs() {
        ObjectNode topSongs = objectMapper.createObjectNode();
        ArrayList<String> songs = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : listenedSongs.entrySet()) {
            songs.add(entry.getKey());
        }

        songs.sort(new Comparator<String>() {
            @Override
            public int compare(final String o1, final String o2) {
                int rez = listenedSongs.get(o1) - listenedSongs.get(o2);

                if (rez == 0) {
                    return o1.compareTo(o2);
                }

                return -rez;
            }
        });

        while (songs.size() > TOP5) {
            songs.remove(songs.size() - 1);
        }

        for (String name : songs) {
            topSongs.put(name, listenedSongs.get(name));
        }

        return topSongs;
    }
    /**
     * @return top 5 albums
     */
    private ObjectNode topAlbums() {
        ObjectNode topAlbums = objectMapper.createObjectNode();
        ArrayList<String> albums = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : listenedAlbums.entrySet()) {
            albums.add(entry.getKey());
        }

        albums.sort(new Comparator<String>() {
            @Override
            public int compare(final String o1, final String o2) {
                int rez = listenedAlbums.get(o1) - listenedAlbums.get(o2);

                if (rez == 0) {
                    return o1.compareTo(o2);
                }

                return -rez;
            }
        });

        while (albums.size() > TOP5) {
            albums.remove(albums.size() - 1);
        }

        for (String name : albums) {
            topAlbums.put(name, listenedAlbums.get(name));
        }

        return topAlbums;
    }
    /**
     * @return top 5 episodes
     */
    private ObjectNode topEpisodes() {
        ObjectNode topEpisodes = objectMapper.createObjectNode();
        for (Map.Entry<String, Integer> entry : listenedEpisodes.entrySet()) {
            topEpisodes.put(entry.getKey(), entry.getValue());
        }
        return topEpisodes;
    }

    private int emptyWrapped() {
        return listenedArtists.size()
                + listenedGenres.size()
                + listenedAlbums.size()
                + listenedEpisodes.size()
                + listenedSongs.size();
    }
    /**
     * shows that user's wrapped
     */
    public void wrapped() {
        ObjectNode result = objectMapper.createObjectNode();
        if (emptyWrapped() == 0) {
            Manager.getPartialResult().put("message", "No data to show for user "
            + username + ".");
            return;
        }
        result.set("topArtists", topArtists());
        result.set("topGenres", topGenres());
        result.set("topSongs", topSongs());
        result.set("topAlbums", topAlbums());
        result.set("topEpisodes", topEpisodes());
        Manager.getPartialResult().set("result", result);
    }

    /**
     * buys premium subscription
     */
    public void buyPremium() {
        if (premium) {
            Manager.getPartialResult().put("message", username + " is already a premium user.");
        } else {
            premium = true;
            Manager.getPartialResult().put("message",
                    username + " bought the subscription successfully.");
        }
    }

    /**
     * cancel premium subscription
     */
    public void cancelPremium() {
        if (premium) {
            premium = false;
            Manager.getPartialResult().put("message",
                    username + " cancelled the subscription successfully.");
        } else {
            Manager.getPartialResult().put("message", username + " is not a premium user.");
        }
    }

    /**
     * calculate revenue for all songs
     */
    public void calculateAllSongRevenue() {
        for (Map.Entry<String, Map<String, Integer>> artist : premiumListens.entrySet()) {
            int numberOfSongs = 0;
            for (Map.Entry<String, Integer> song : artist.getValue().entrySet()) {
                numberOfSongs += song.getValue();
                double sum = (PREMIUM_PRICE / premiumListensSize) * song.getValue();
                Manager.getUser(artist.getKey()).calculateSongRevenue(song.getKey(), sum);
            }
            double value = (PREMIUM_PRICE / premiumListensSize) * numberOfSongs;
            Manager.getUser(artist.getKey()).addSongRevenue(value);
        }
    }

    /**
     * loads a new ad to player
     */
    public void adBreak() {
        if (musicplayer == null || musicplayer.getSong() == null) {
            Manager.getPartialResult().put("message", username + " is not playing any music.");
            return;
        }

        Manager.getPartialResult().put("message", "Ad inserted successfully.");
        musicplayer.setAdBreak(true);
        musicplayer.setAdPrice(Manager.getCommand().getPrice());
    }

    /**
     * pays artists with the ad
     * @param price the price of the ad
     */
    public void payAds(final int price) {
        double sum = (double) price / freeSongQueue.size();
        for (Song s : freeSongQueue) {
            Manager.getUser(s.getArtist()).calculateSongRevenue(s.getName(), sum);
            Manager.getUser(s.getArtist()).addSongRevenue(sum);
        }
        freeSongQueue.clear();
    }

    /**
     * buy merch from artist page
     */
    public void buyMerch() {
        String merchName = Manager.getCommand().getName();
        if (!merchName.startsWith(currentPage.getNonUserName())) {
            Manager.getPartialResult().put("message",
                    "Cannot buy merch from this page.");
            return;
        }

        if (!Manager.getUser(currentPage.getNonUserName()).getMerchs().containsKey(merchName)) {
            Manager.getPartialResult().put("message",
                    "The merch " + merchName + " doesn't exist.");
            return;
        }

        merch.add(merchName);
        Merch merchBought = Manager.getUser(currentPage.getNonUserName())
                .getMerchs().get(merchName);
        Manager.getUser(currentPage.getNonUserName()).addMerchRevenue(merchBought.getPrice());
        Manager.getPartialResult().put("message",
                username + " has added new merch successfully.");
    }

    /**
     * see bought merch
     */
    public void seeMerch() {
        ArrayNode result = objectMapper.createArrayNode();
        for (String m : merch) {
            result.add(m);
        }
        Manager.getPartialResult().set("result", result);
    }

    /**
     * subscribe to an artist/host or playlist
     */
    public void subscribe() {
        String page = currentPage.checkPage();
        if (!page.equals("ArtistPage") && !page.equals("HostPage")) {
            Manager.getPartialResult().put("message",  "To subscribe you need to be "
                    + "on the page of an artist or host.");
            return;
        }

        String artisthost = currentPage.getNonUserName();
        if (Manager.getUser(artisthost).addSubscriber(this)) {
            Manager.getPartialResult().put("message",
                    username + " subscribed to " + artisthost + " successfully.");
        } else {
            Manager.getPartialResult().put("message",
                    username + " unsubscribed from " + artisthost + " successfully.");
        }
    }

    /**
     * shows all notifications
     */
    public void getNotifications() {
        ArrayNode notificationsNode = objectMapper.createArrayNode();
        for (int i = 0; i < notifications.size(); i += 2) {
            ObjectNode objNode = objectMapper.createObjectNode();
            objNode.put("name", notifications.get(i));
            objNode.put("description", notifications.get(i + 1));
            notificationsNode.add(objNode);
        }
        Manager.getPartialResult().set("notifications", notificationsNode);
        notifications.clear();
    }

    /**
     * adds new notification
     * @param name name of notification
     * @param description description of notification
     */
    public void addNotification(final String name, final String description) {
        notifications.add(name);
        notifications.add(description);
    }

    /**
     * returns to previous page
     */
    public void previousPage() {
        currentPage.previousPage();
    }

    /**
     * goes back to next page
     */
    public void nextPage() {
        currentPage.nextPage();
    }

    private boolean randomSongRecommendation() {
        Song song = musicplayer.getSong();
        if (song == null) {
            return false;
        }
        int passedTime = song.getMaxDuration() - song.getDuration();
        if (passedTime < SEC30_PASSED) {
            songsRecommended.add(song);
        } else {
            ArrayList<Song> genreSongs = new ArrayList<>();
            for (Song s : Library.getInstance().getSongs()) {
                if (s.getGenre().equals(song.getGenre())) {
                    genreSongs.add(s);
                }
            }
            Random random = new Random(passedTime);
            int index = random.nextInt(genreSongs.size());
            songsRecommended.add(genreSongs.get(index));
        }
        lastSongRecommended = true;
        return true;
    }

    private boolean randomPlaylistRecommendation() {
        if (followedPlaylists.isEmpty() && likedSongs.isEmpty() && playlists.isEmpty()) {
            return false;
        }

        Playlist p = new Playlist(username, username + "'s recommendations",
                Manager.getCommand().getTimestamp());
        Manager.getPlaylists().put(p.getName(), p);
        this.playlists.add(p);

        // todo adauga melodii in playlist;

        playlistsRecommended.add(p.getName());
        lastSongRecommended = false;
        return true;
    }

    private boolean fanPlaylistRecommendation() {
        Song song = musicplayer.getSong();
        if (song == null || Manager.getUser(song.getArtist()).getListeners().isEmpty()) {
            return false;
        }

        String artistName = song.getArtist();
        Playlist p = new Playlist(username, artistName + " Fan Club recommendations",
                Manager.getCommand().getTimestamp());
        Manager.getPlaylists().put(p.getName(), p);
        this.playlists.add(p);

        // todo adauga melodii in playlist;

        playlistsRecommended.add(p.getName());
        lastSongRecommended = false;
        return true;
    }

    private boolean getRecommendation() {
        switch (Manager.getCommand().getRecommendationType()) {
            case "random_song" -> {
                return randomSongRecommendation();
            }
            case "random_playlist" -> {
                return randomPlaylistRecommendation();
            }
            case "fans_playlist" -> {
                return fanPlaylistRecommendation();
            }
            default -> {
                return false;
            }
        }
    }

    /**
     * update the recommendation page
     */
    public void updateRecommendations() {
        boolean ret = getRecommendation();

        if (ret) {
            Manager.getPartialResult().put("message", "The recommendations for user "
                    + username + " have been updated successfully.");
        } else {
            Manager.getPartialResult().put("message", "No new recommendations were found");
        }
    }

    /**
     * loads last recommendation
     */
    public void loadRecommendations() {
        if (lastSongRecommended) {
            if (songsRecommended.isEmpty()) {
                Manager.getPartialResult().put("message", "No recommendations available.");
                return;
            }
            Manager.getSource(username).setSourceLoaded(true);
            musicplayer = PlayerFactory.getInstance().createPlayer("song", username);
            musicplayer.load(songsRecommended.get(songsRecommended.size() - 1));
            formatType = "song";
        } else {
            if (playlistsRecommended.isEmpty()) {
                Manager.getPartialResult().put("message", "No recommendations available.");
                return;
            }
            Manager.getSource(username).setSourceLoaded(true);
            musicplayer = PlayerFactory.getInstance().createPlayer("playlist", username);
            Playlist playlist = Manager.getPlaylists().get(playlistsRecommended.
                    get(playlistsRecommended.size() - 1));
            musicplayer.load(playlist);
            formatType = "playlist";
        }
    }
}
