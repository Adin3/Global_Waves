package program.searchbar;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.Getter;
import program.Manager;
import program.format.Playlist;
import program.format.Song;
import program.command.Filters;
import program.format.Library;

import java.util.ArrayList;
import java.util.Collections;

public class SearchBarSong extends SearchBar {

    private static final int MAX_SONGS = 5;
    @Getter
    private final ArrayList<Song> song = new ArrayList<>();

    @Getter
    private Song songLoaded;

    @Getter
    private Playlist playlistLoaded;

    @Getter
    private final String owner;

    public SearchBarSong(final String owner) {
        this.owner = owner;
    }
    /**
     * clears search
     */
    public void clearSearch() {
        song.clear();
        songLoaded = null;
        sourceSelected = false;
        sourceSearched = false;
    }

    /**
     * select searched song
     * @param number songs' id
     * @param username user's name
     */
    public void select(final int number, final String username) {
        if (!Manager.getSource(username).isSourceSearched()) {
            Manager.getPartialResult().put("message",
                    "Please conduct a search before making a selection.");
        } else {
            if (number <= song.size()) {
                songLoaded = song.get((number - 1));
                sourceSelected = true;
                sourceSearched = false;
                Manager.getPartialResult().put("message",
                        "Successfully selected " + songLoaded.getName() + ".");
                return;
            }
            Manager.getPartialResult().put("message",
                    "The selected ID is too high.");
            Manager.getSource(username).setSourceSearched(false);
            return;
        }
        Manager.getSource(username).setSourceSearched(false);
    }
    /**
     * returns what was searched
     */
    public void searchDone() {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode node = objectMapper.createArrayNode();

        int size = song.size();

        Manager.getPartialResult().put("message", "Search returned "
                + size + " results");
        for (Song sg : song) {
            node.add(sg.getName());
        }

        Manager.getPartialResult().set("results", node);
    }

    /**
     * search songs
     * @param filter search filters
     */
    public void search(final Filters filter) {
        int options = 0;
        if (filter.getAlbum() != null) {
            options++;
            song.addAll(searchSongByAlbum(filter.getAlbum()));
        }

        if (filter.getArtist() != null) {
            options++;
            song.addAll(searchSongByArtist(filter.getArtist()));
        }

        if (filter.getName() != null) {
            options++;
            song.addAll(searchSongByName(filter.getName()));
        }

        if (filter.getGenre() != null) {
            options++;
            song.addAll(searchSongByGenre(filter.getGenre()));
        }

        if (filter.getLyrics() != null) {
            options++;
            song.addAll(searchSongByLyrics(filter.getLyrics()));
        }

        if (filter.getReleaseYear() != null) {
            options++;
            song.addAll(searchSongByYear(filter.getReleaseYear()));
        }

        if (filter.getTags() != null) {
            options++;
            song.addAll(searchSongByTags(filter.getTags()));
        }

        if (options > 1) {
            ArrayList<Song> temp = new ArrayList<>();

            for (Song s : song) {
                if (Collections.frequency(song, s) > 1) {
                    temp.add(s);
                }
            }

            song.clear();
            song.addAll(temp);
            temp.clear();

            for (Song s : song) {
                if (!temp.contains(s)) {
                    temp.add(s);
                }
            }

            song.clear();
            song.addAll(temp);
        }

        while (song.size() > MAX_SONGS) {
            song.remove(song.size() - 1);
        }
    }
    /**
     * search song by name
     * @param name song's name
     */
    private ArrayList<Song> searchSongByName(final String name) {

        ArrayList<Song> songs = new ArrayList<>();
        for (Song s : Library.getInstance().getSongs()) {
            if (s.getName().startsWith(name)) {
                songs.add(s);
            }
        }
        return songs;
    }
    /**
     * search song by album
     * @param album song's album
     */
    private ArrayList<Song> searchSongByAlbum(final String album) {

        ArrayList<Song> songs = new ArrayList<>();
        for (Song s : Library.getInstance().getSongs()) {
            if (s.getAlbum().equals(album)) {
                songs.add(s);
            }
        }
        return songs;
    }

    /**
     * search song by tags
     * @param tags song's tags
     */
    private ArrayList<Song> searchSongByTags(final ArrayList<String> tags) {

        ArrayList<Song> songs = new ArrayList<>();
        for (Song s : Library.getInstance().getSongs()) {
            boolean containsAll = true;
            for (String tag : tags) {
                if (!s.getTags().contains(tag)) {
                    containsAll = false;
                    break;
                }
            }
            if (containsAll) {
                songs.add(s);
            }
        }
        return songs;
    }
    /**
     * search song by lyrics
     * @param lyric song's lyrics
     */
    private ArrayList<Song> searchSongByLyrics(final String lyric) {

        ArrayList<Song> songs = new ArrayList<>();
        for (Song s : Library.getInstance().getSongs()) {
            if (s.getLyrics().toLowerCase().contains(lyric.toLowerCase())) {
                songs.add(s);
            }
        }
        return songs;
    }
    /**
     * search song by genre
     * @param genre song's genre
     */
    private ArrayList<Song> searchSongByGenre(final String genre) {

        ArrayList<Song> songs = new ArrayList<>();
        for (Song s : Library.getInstance().getSongs()) {
            if (s.getGenre().equalsIgnoreCase(genre)) {
                songs.add(s);
            }
        }
        return songs;
    }
    /**
     * search song by release year
     * @param year song's release year
     */
    private ArrayList<Song> searchSongByYear(final String year) {

        ArrayList<Song> songs = new ArrayList<>();

        for (Song s : Library.getInstance().getSongs()) {
            int sign;
            if (year.startsWith("<")) {
                sign = Integer.parseInt(year.substring(1)) - s.getReleaseYear();
            } else {
                sign = s.getReleaseYear() - Integer.parseInt(year.substring(1));
            }

            if (sign > 0) {
                songs.add(s);
            }
        }
        return songs;
    }
    /**
     * search song by artist
     * @param artist song's artist
     */
    private ArrayList<Song> searchSongByArtist(final String artist) {

        ArrayList<Song> songs = new ArrayList<>();
        for (Song s : Library.getInstance().getSongs()) {
            if (s.getArtist().equals(artist)) {
                songs.add(s);
            }
        }
        return songs;
    }
}
