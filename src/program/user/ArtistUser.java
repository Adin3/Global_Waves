package program.user;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.UserInput;
import lombok.Getter;
import program.Duplet;
import program.Manager;
import program.format.*;
import program.page.Page;
import program.player.*;
import program.searchbar.*;

import java.util.*;

public class ArtistUser extends User {

    @Getter
    private final Map<String, Album> albums = new LinkedHashMap<>();

    @Getter
    private final Map<String, Event> events = new LinkedHashMap<>();

    @Getter
    private final Map<String, Merch> merchs = new LinkedHashMap<>();

    public ArtistUser() {}
    public ArtistUser(final UserInput user) {
        this.age = user.getAge();
        this.city = user.getCity();
        this.username = user.getUsername();
        this.userType = "artist";
    }

    public ArtistUser(String username, int age, String city, String userType) {
        this.username = username;
        this.age = age;
        this.city = city;
        this.userType = userType;
    }

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
                Manager.getCommand().getReleaseYear(), Manager.getCommand().getDescription());

        for (Song song : Manager.getCommand().getSongs()) {
            song.setMaxDuration(song.getDuration());
            Library.getInstance().getSongs().add(song);
            album.addSong(song);
        }

        Manager.getAlbums().add(album);
        albums.put(album.getName(), album);

        Manager.getPartialResult().put("message",
                username + " has added new album successfully.");
    }

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
    public void addEvent() {
        if (events.containsKey(Manager.getCommand().getName())) {
            Manager.getPartialResult().put("message",
                    username + " has another event with the same name.");
            return;
        }

        Event event = new Event(Manager.getCommand().getName(), Manager.getCommand().getDescription(),
                Manager.getCommand().getDate());

        if (!event.checkDate()) {
            Manager.getPartialResult().put("message", "Event for "
                    + username + " does not have a valid date.");
            return;
        }

        events.put(event.getName(), event);
        Manager.getPartialResult().put("message",
                username + " has added new event successfully.");
    }

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
        Manager.getPartialResult().put("message",
                username + " has added new merchandise successfully.");
    }

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
                if (username.equals(user.getCurrentPage().getNonUserName())) {
                    used = true; break;
                }
            }
        }
        return used;
    }

    public void removeAlbum() {
        if (!albums.containsKey(Manager.getCommand().getName())) {
            Manager.getPartialResult().put("message",
                    username + " doesn't have an album with the given name.");
            return;
        }
        Manager.getPartialResult().put("message",
                username + " can't delete this album.");
    }

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
}
