package program.user;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.UserInput;
import lombok.Getter;
import program.Manager;
import program.format.*;
import program.page.Page;
import program.player.*;
import program.searchbar.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

public class ArtistUser extends User {

    @Getter
    private final ArrayList<String> albums = new ArrayList<>();

    @Getter
    private final ArrayList<Event> events = new ArrayList<>();

    @Getter
    private final ArrayList<Merch> merch = new ArrayList<>();

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
        if (!Objects.equals(Manager.getCurrentUser().getUserType(), "artist")) {
            Manager.getPartialResult().put("message",
                    username + " is not an artist.");
            return;
        }

        if (Manager.getCurrentUser().getAlbums().contains(Manager.getCommand().getName())) {
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

        Manager.getAlbums().put(Manager.getCommand().getName(), album);
        Manager.getCurrentUser().getAlbums().add(album.getName());

        Manager.getPartialResult().put("message",
                username + " has added new album successfully.");
    }

    public void showAlbums() {
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
    public void addEvent() {
        if (!Objects.equals(Manager.getCurrentUser().getUserType(), "artist")) {
            Manager.getPartialResult().put("message",
                    username + " is not an artist.");
            return;
        }

        for (Event event1 : Manager.getCurrentUser().getEvents()) {
            if (event1.getName().equals(Manager.getCommand().getName())) {
                Manager.getPartialResult().put("message",
                        username + " has another event with the same name.");
                return;
            }
        }

        Event event = new Event(Manager.getCommand().getName(), Manager.getCommand().getDescription(),
                Manager.getCommand().getDate());

        if (!event.checkDate()) {
            Manager.getPartialResult().put("message", "Event for "
                    + username + " does not have a valid date.");
            return;
        }

        Manager.getCurrentUser().getEvents().add(event);
        Manager.getPartialResult().put("message",
                username + " has added new event successfully.");
    }

    public void addMerch() {
        if (!Objects.equals(Manager.getCurrentUser().getUserType(), "artist")) {
            Manager.getPartialResult().put("message",
                    username + " is not an artist.");
            return;
        }

        for (Merch merch : Manager.getCurrentUser().getMerch()) {
            if (merch.getName().equals(Manager.getCommand().getName())) {
                Manager.getPartialResult().put("message",
                        username + " has merchandise with the same name.");
                return;
            }
        }

        Merch merch = new Merch(Manager.getCommand().getName(),
                Manager.getCommand().getPrice(), Manager.getCommand().getDescription());

        if (!merch.validMerch()) {
            Manager.getPartialResult().put("message", "Price for merchandise"
                    + " can not be negative.");
            return;
        }

        Manager.getCurrentUser().getMerch().add(merch);
        Manager.getPartialResult().put("message",
                username + " has added new merchandise successfully.");
    }

    @Override
    public void deleteUser() {
        boolean used = isUsed();

        if (!used) {
            Library.getInstance().getSongs().removeIf(song -> song.getArtist().equals(username));
            Manager.getAlbums().values().removeIf(album -> album.getOwner().equals(username));
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
}
