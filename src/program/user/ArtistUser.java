package program.user;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.UserInput;
import lombok.Getter;
import program.admin.Manager;
import program.format.Album;
import program.format.Event;
import program.format.Merch;
import program.format.Song;
import program.format.Library;

import java.util.Map;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.LinkedHashMap;
import java.util.Comparator;

public class ArtistUser extends User {

    private static final int TOP5 = 5;

    private static final double MATH_ROUND = 100.0;
    @Getter
    private final Map<String, Album> albums = new LinkedHashMap<>();

    @Getter
    private final Map<String, Event> events = new LinkedHashMap<>();

    @Getter
    private final Map<String, Merch> merchs = new LinkedHashMap<>();

    @Getter
    private Map<String, Integer> listenedSongs = new LinkedHashMap<String, Integer>();

    @Getter
    private Map<String, Integer> listenedAlbums = new LinkedHashMap<String, Integer>();

    @Getter
    private Map<String, Integer> listeners = new LinkedHashMap<String, Integer>();

    @Getter
    private ArrayList<SubscribeObserver> subscribers = new ArrayList<>();

    private Map<String, Double> premiumListens = new LinkedHashMap<>();
    @Getter
    private double songRevenue = 0.0;
    @Getter
    private double merchRevenue = 0.0;

    private String mostProfitableSong;

    @Getter
    private boolean listened = false;

    public ArtistUser() { }
    public ArtistUser(final UserInput user) {
        this.age = user.getAge();
        this.city = user.getCity();
        this.username = user.getUsername();
        this.userType = "artist";
        idCount++;
        this.id = idCount;
    }

    public ArtistUser(final String username, final int age,
                      final String city, final String userType) {
        this.username = username;
        this.age = age;
        this.city = city;
        this.userType = userType;
        idCount++;
        this.id = idCount;
    }
    /**
     * adds new album in the system
     */
    public void addAlbum() {
        if (albums.containsKey(Manager.getCommand().getName())) {
            Manager.getPartialResult().put("message",
                    username + " has another album with the same name.");
            return;
        }

        HashSet<String> set = new HashSet<>();
        for (Song song : Manager.getCommand().getSongs()) {
            if (!set.add(song.getName())) {
                Manager.getPartialResult().put("message",
                        username + " has the same song at least twice in this album.");
                return;
            }
        }

        Album album = new Album(Manager.getCommand().getUsername(), Manager.getCommand().getName(),
                Manager.getCommand().getReleaseYear(), Manager.getCommand().getDescription(),
                Manager.getCommand().getTimestamp());

        for (Song song : Manager.getCommand().getSongs()) {
            song.setMaxDuration(song.getDuration());
            Library.getInstance().getSongs().add(song);
            album.addSong(song);
        }

        Manager.getAlbums().add(album);
        albums.put(album.getName(), album);
        updateSubscribers("New Album", "New Album from " + username + ".");
        Manager.getPartialResult().put("message",
                username + " has added new album successfully.");
    }
    /**
     * show all albums made by the artist
     */
    public void showAlbums() {
        ArrayNode result = objectMapper.createArrayNode();
        for (Album albumName : Manager.getCurrentUser().getAlbums().values()) {
            Album album = Manager.findObjectByCondition(Manager.getAlbums(), albumName);
            ObjectNode albumNode = objectMapper.createObjectNode();
            albumNode.put("name", album.getName());
            ArrayNode songs = objectMapper.createArrayNode();
            album.getSongs().forEach((s) -> songs.add(s.getName()));
            albumNode.set("songs", songs);
            result.add(albumNode);
        }
        Manager.getPartialResult().set("result", result);
    }
    /**
     * adds new event
     */
    public void addEvent() {
        if (events.containsKey(Manager.getCommand().getName())) {
            Manager.getPartialResult().put("message",
                    username + " has another event with the same name.");
            return;
        }

        Event event = new Event.EventBuilder()
                .withName(Manager.getCommand().getName())
                .withDescription(Manager.getCommand().getDescription())
                .withDate(Manager.getCommand().getDate())
                .build();

        if (!event.checkDate()) {
            Manager.getPartialResult().put("message", "Event for "
                    + username + " does not have a valid date.");
            return;
        }

        events.put(event.getName(), event);
        updateSubscribers("New Event", "New Event from " + username + ".");
        Manager.getPartialResult().put("message",
                username + " has added new event successfully.");
    }
    /**
     * adds new merch
     */
    public void addMerch() {
        if (merchs.containsKey(Manager.getCommand().getName())) {
            Manager.getPartialResult().put("message",
                    username + " has merchandise with the same name.");
            return;
        }

        Merch merch = new Merch(Manager.getCommand().getName(),
                Manager.getCommand().getPrice(), Manager.getCommand().getDescription());

        if (!merch.validMerch()) {
            Manager.getPartialResult().put("message", "Price for merchandise"
                    + " can not be negative.");
            return;
        }

        merchs.put(merch.getName(), merch);
        updateSubscribers("New Merchandise", "New Merchandise from " + username + ".");
        Manager.getPartialResult().put("message",
                username + " has added new merchandise successfully.");
    }
    /**
     * deletes the artist
     */
    @Override
    public void deleteUser() {
        boolean used = isUsed();

        if (!used) {
            Library.getInstance().getSongs().removeIf(song -> song.getArtist().equals(username));
            Manager.getAlbums().removeIf(album -> album.getOwner().equals(username));
            Manager.getArtists().remove(username);
            Manager.getSources().remove(username);
            Manager.getUsers().remove(username);
            Manager.getUsers().values().forEach(user -> {
                if (user.getLikedSongs() != null) {
                    user.getLikedSongs().removeIf(song ->
                            Objects.equals(song.getArtist(), username)
                    );
                }
            });
            Manager.getPartialResult().put("message",
                    username + " was successfully deleted.");
            return;
        }
        Manager.getPartialResult().put("message",
                username + " can't be deleted.");
    }

    private boolean isUsed() {
        boolean used = false;
        for (User user : Manager.getUsers().values()) {
            if (user.getUserType().equals("user")) {
                Song song = user.getMusicplayer().getSong();
                if (song != null && song.getArtist().equals(username)) {
                    used = true; break;
                }
                Song album = user.getMusicplayer().getCurrentSong();
                if (album != null && album.getArtist().equals(username)) {
                    used = true; break;
                }
            }
        }
        return used;
    }
    /**
     * removes an album from library
     */
    public void removeAlbum() {
        if (!albums.containsKey(Manager.getCommand().getName())) {
            Manager.getPartialResult().put("message",
                    username + " doesn't have an album with the given name.");
            return;
        }

        boolean used = isUsed();

        if (used) {
            Manager.getPartialResult().put("message",
                    username + " can't delete this album.");
        } else {
            albums.remove(Manager.getCommand().getName());
            Manager.getAlbums().removeIf(
                    album -> album.getName().equals(Manager.getCommand().getName()));
            Manager.getPartialResult().put("message",
                    username + " deleted the album successfully.");
        }
    }
    /**
     * removes an event
     */
    public void removeEvent() {
        if (!events.containsKey(Manager.getCommand().getName())) {
            Manager.getPartialResult().put("message",
                    username + " doesn't have an event with the given name.");
            return;
        }

        events.remove(Manager.getCommand().getName());
        Manager.getPartialResult().put("message",
                username + " deleted the event successfully.");
    }

    /**
     * saves the song that was listened
     * @param song the song listened
     * @param listener the listener
     */
    public void setListenedSong(final Song song, final String listener) {
        int freq = listenedSongs.getOrDefault(song.getName(), 0) + 1;
        listenedSongs.put(song.getName(), freq);
        freq = listenedAlbums.getOrDefault(song.getAlbum(), 0) + 1;
        listenedAlbums.put(song.getAlbum(), freq);
        freq = listeners.getOrDefault(listener, 0) + 1;
        listeners.put(listener, freq);
        listened = true;
    }

    /**
     * adds the song earning to revenue
     * @param revenue the earning
     */
    public void addSongRevenue(final double revenue) {
        songRevenue += revenue;
    }

    /**
     * adds the merch earning to revenue
     * @param revenue the earning
     */
    public void addMerchRevenue(final double revenue) {
        merchRevenue += revenue;
        listened = true;
    }

    /**
     * adds a new subscriber
     * @param subscriber the subscriber
     */
    public boolean addSubscriber(final SubscribeObserver subscriber) {
        if (subscribers.contains(subscriber)) {
            subscribers.remove(subscriber);
            return false;
        }
        subscribers.add(subscriber);
        return true;
    }

    /**
     * updates all subscribers
     * @param name name of notification
     * @param description description of notification
     */
    private void updateSubscribers(final String name, final String description) {
        for (SubscribeObserver s : subscribers) {
            s.addNotification(name, description);
        }
    }

    /**
     * @return Top 5 songs
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
     * @return Top 5 albums
     */
    private ObjectNode topAlbums() {
        ObjectNode topAlbums = objectMapper.createObjectNode();
        ArrayList<String> album = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : listenedAlbums.entrySet()) {
            album.add(entry.getKey());
        }

        album.sort(new Comparator<String>() {
            @Override
            public int compare(final String o1, final String o2) {
                int rez = listenedAlbums.get(o1) - listenedAlbums.get(o2);

                if (rez == 0) {
                    return o1.compareTo(o2);
                }

                return -rez;
            }
        });

        while (album.size() > TOP5) {
            album.remove(album.size() - 1);
        }

        for (String name : album) {
            topAlbums.put(name, listenedAlbums.get(name));
        }
        return topAlbums;
    }
    /**
     * @return Top 5 fans
     */
    private ArrayNode topFans() {
        ArrayNode topFans = objectMapper.createArrayNode();
        ArrayList<String> fans = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : listeners.entrySet()) {
            fans.add(entry.getKey());
        }

        fans.sort(new Comparator<String>() {
            @Override
            public int compare(final String o1, final String o2) {
                int rez = listeners.get(o1) - listeners.get(o2);

                if (rez == 0) {
                    return o1.compareTo(o2);
                }

                return -rez;
            }
        });

        while (fans.size() > TOP5) {
            fans.remove(fans.size() - 1);
        }

        for (String name : fans) {
            topFans.add(name);
        }
        return topFans;
    }

    /**
     * checks if wrapped has something to show
     */
    private int emptyWrapped() {
        return listeners.size()
                + listenedAlbums.size()
                + listenedSongs.size();
    }
    /**
     * shows that user's wrapped
     */
    public void wrapped() {
        ObjectNode result = objectMapper.createObjectNode();
        if (emptyWrapped() == 0) {
            Manager.getPartialResult().put("message", "No data to show for artist "
                    + username + ".");
            return;
        }
        result.put("listeners", listeners.size());
        result.set("topAlbums", topAlbums());
        result.set("topFans", topFans());
        result.set("topSongs", topSongs());
        Manager.getPartialResult().set("result", result);
    }

    /**
     * calculate the revenue made by the songs
     * @param name name of the song
     * @param sum the revenue
     */
    public void calculateSongRevenue(final String name, final double sum) {
        double newSum = premiumListens.getOrDefault(name, 0.0) + sum;
        premiumListens.put(name, newSum);
    }

    /**
     * @return total revenue
     */
    public double getTotalRevenue() {
        return songRevenue + merchRevenue;
    }

    /**
     * find most profitable song
     */
    private void mostProfitableSong() {
        if (premiumListens.isEmpty()) {
            return;
        }

        ArrayList<String> profitableSong = new ArrayList<>();
        for (Map.Entry<String, Double> entry : premiumListens.entrySet()) {
            profitableSong.add(entry.getKey());
        }
        profitableSong.sort(new Comparator<String>() {
            @Override
            public int compare(final String o1, final String o2) {
                double rez = premiumListens.get(o1) - premiumListens.get(o2);

                if (rez == 0) {
                    return o1.compareTo(o2);
                }

                return (int) -rez;
            }
        });
        mostProfitableSong = profitableSong.get(0);
    }

    /**
     * show stats for artist at the end of program
     * @param rank global ranking
     */
    public ObjectNode endProgram(final int rank) {
        if (!listened) {
            return null;
        }
        ObjectNode art = objectMapper.createObjectNode();
        double rev = Math.round(songRevenue * MATH_ROUND) / MATH_ROUND;
        mostProfitableSong();


        art.put("songRevenue", rev);
        art.put("merchRevenue", merchRevenue);
        art.put("ranking", rank);
        if (rev != 0) {
            art.put("mostProfitableSong", mostProfitableSong);
        } else {
            art.put("mostProfitableSong", "N/A");
        }
        return art;
    }

}
