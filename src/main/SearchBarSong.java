package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.Getter;

import java.util.ArrayList;

public class SearchBarSong extends SearchBar{
    @Getter
    private final ArrayList<Song> song = new ArrayList<>();

    @Getter
    private Song songLoaded;

    @Getter
    private Playlist playlistLoaded;

    @Getter
    private final String owner;

    public SearchBarSong(String owner) {
        this.owner = owner;
    }

    public void clearSearch() {
        song.clear();
        songLoaded = null;
        sourceSelected = false;
        sourceSearched = false;
    }

    public void select(int number, String username) {
        if (song.isEmpty() || !Manager.getSource(username).isSourceSearched()) {
            Manager.partialResult.put("message", "Please conduct a search before making a selection.");
        } else {
            if (number <= song.size()) {
                songLoaded = song.get((number - 1));
                sourceSelected = true;
                sourceSearched = false;
                Manager.partialResult.put("message", "Successfully selected " + songLoaded.getName() + ".");
                return;
            }
            Manager.partialResult.put("message", "The selected ID is too high.");
            return;
        }
    }

    public void searchDone() {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode node = objectMapper.createArrayNode();

        int size = song.size();

        Manager.partialResult.put("message", "Search returned " +
                size + " results");
        for (Song sg : song)
            node.add(sg.getName());

        Manager.partialResult.set("results", node);
        sourceSearched = true;
    }

    public void search(Filters filter) {

        if (filter.getAlbum() != null) {
            song.addAll(SearchSongByAlbum(filter.getAlbum()));
        }
        else if (filter.getArtist() != null) {
            song.addAll(SearchSongByArtist(filter.getArtist()));
        }
        else if (filter.getName() != null) {
            song.addAll(SearchSongByName(filter.getName()));
        }
        else if (filter.getGenre() != null) {
            song.addAll(SearchSongByGenre(filter.getGenre()));
        }
        else if (filter.getLyrics() != null) {
            song.addAll(SearchSongByLyrics(filter.getLyrics()));
        }
        else if (filter.getReleaseYear() != null) {
            song.addAll(SearchSongByYear(filter.getReleaseYear()));
        }
        else if (filter.getTags() != null) {
            song.addAll(SearchSongByTags(filter.getTags()));
        }

        while (song.size() > 5) {
            song.remove(song.size()-1);
        }
    }
    private ArrayList<Song> SearchSongByName(String name) {

        ArrayList<Song> songs = new ArrayList<>();
        for (Song song : Library.getInstance().getSongs()) {
            if (song.getName().startsWith(name)) {
                songs.add(song);
            }
        }
        return songs;
    }

    private ArrayList<Song> SearchSongByAlbum(String album) {

        ArrayList<Song> songs = new ArrayList<>();
        for (Song song : Library.getInstance().getSongs()) {
            if (song.getAlbum().equals(album)) {
                songs.add(song);
            }
        }
        return songs;
    }

    private ArrayList<Song> SearchSongByTags(ArrayList<String> tags) {

        ArrayList<Song> songs = new ArrayList<>();
        for (Song song : Library.getInstance().getSongs()) {
            boolean containsAll = true;
            for (String tag : tags) {
                if (!song.getTags().contains(tag)) {
                    containsAll = false;
                    break;
                }
            }
            if (containsAll) {
                songs.add(song);
            }
        }
        return songs;
    }

    private ArrayList<Song> SearchSongByLyrics(String lyric) {

        ArrayList<Song> songs = new ArrayList<>();
        for (Song song : Library.getInstance().getSongs()) {
            if (song.getLyrics().toLowerCase().contains(lyric.toLowerCase())) {
                songs.add(song);
            }
        }
        return songs;
    }

    private ArrayList<Song> SearchSongByGenre(String genre) {

        ArrayList<Song> songs = new ArrayList<>();
        for (Song song : Library.getInstance().getSongs()) {
            if (song.getGenre().equalsIgnoreCase(genre)) {
                songs.add(song);
            }
        }
        return songs;
    }

    private ArrayList<Song> SearchSongByYear(String year) {

        ArrayList<Song> songs = new ArrayList<>();

        for (Song song : Library.getInstance().getSongs()) {
            int sign;
            if (year.startsWith("<"))
                sign = Integer.parseInt(year.substring(1)) - song.getReleaseYear();
            else
                sign = song.getReleaseYear() - Integer.parseInt(year.substring(1));

            if (sign > 0) {
                songs.add(song);
            }
        }
        return songs;
    }

    private ArrayList<Song> SearchSongByArtist(String artist) {

        ArrayList<Song> songs = new ArrayList<>();
        for (Song song : Library.getInstance().getSongs()) {
            if (song.getArtist().equals(artist)) {
                songs.add(song);
            }
        }
        return songs;
    }
}
