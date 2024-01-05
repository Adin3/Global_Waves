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

import java.util.*;

public class ArtistUser extends User {

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
        id_count++;
        this.id = id_count;
    }

    public ArtistUser(final String username, final int age,
                      final String city, final String userType) {
        this.username = username;
        this.age = age;
        this.city = city;
        this.userType = userType;
        id_count++;
        this.id = id_count;
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

        Event event = new Event(Manager.getCommand().getName(),
                Manager.getCommand().getDescription(),
                Manager.getCommand().getDate());

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
//                if (username.equals(user.getCurrentPage().getNonUserName())) {
//                    used = true; break;
//                }
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

    public void setListenedSong(Song song, String listener) {
        int freq = listenedSongs.getOrDefault(song.getName(), 0) + 1;
        listenedSongs.put(song.getName(), freq);
        freq = listenedAlbums.getOrDefault(song.getAlbum(), 0) + 1;
        listenedAlbums.put(song.getAlbum(), freq);
        freq = listeners.getOrDefault(listener, 0) + 1;
        listeners.put(listener, freq);
        listened = true;
    }

    public void addSongRevenue(final double revenue) {
        songRevenue += revenue;
    }

    public void addMerchRevenue(final double revenue) {
        merchRevenue += revenue;
        listened = true;
    }

    public boolean addSubscriber(SubscribeObserver subscriber) {
        if (subscribers.contains(subscriber)) {
            subscribers.remove(subscriber);
            return false;
        }
        subscribers.add(subscriber);
        return true;
    }

    private void updateSubscribers(String name, String description) {
        for (SubscribeObserver s : subscribers) {
            s.addNotification(name, description);
        }
    }

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

        while (songs.size() > 5) {
            songs.remove(songs.size()-1);
        }

        for (String name : songs) {
            topSongs.put(name, listenedSongs.get(name));
        }

        return topSongs;
    }

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

        while (albums.size() > 5) {
            albums.remove(albums.size()-1);
        }

        for (String name : albums) {
            topAlbums.put(name, listenedAlbums.get(name));
        }
        return topAlbums;
    }

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

        while (fans.size() > 5) {
            fans.remove(fans.size()-1);
        }

        for (String name : fans) {
            topFans.add(name);
        }
        return topFans;
    }

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

    public void calculateSongRevenue(String name, double sum) {
        double newSum = premiumListens.getOrDefault(name, 0.0) + sum;
        premiumListens.put(name, newSum);
    }

    public double getTotalRevenue() {
        return songRevenue + merchRevenue;
    }

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
            public int compare(String o1, String o2) {
                double rez = premiumListens.get(o1) - premiumListens.get(o2);

                if (rez == 0) {
                    return o1.compareTo(o2);
                }

                return (int)-rez;
            }
        });
        mostProfitableSong = profitableSong.get(0);
    }

    public ObjectNode endProgram(final int rank) {
        if (!listened) return null;
        ObjectNode art = objectMapper.createObjectNode();
        double rev = Math.round(songRevenue * 100.0) / 100.0;
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
