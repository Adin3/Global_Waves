package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;

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
        if (!Manager.getSource(username).isSourceSearched()) {
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
            Manager.getSource(username).setSourceSearched(false);
            return;
        }
        Manager.getSource(username).setSourceSearched(false);
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
    }

    public void search(Filters filter) {
        int options = 0;
        if (filter.getAlbum() != null) {
            options++;
            song.addAll(SearchSongByAlbum(filter.getAlbum()));
        }

        if (filter.getArtist() != null) {
            options++;
            song.addAll(SearchSongByArtist(filter.getArtist()));
        }

        if (filter.getName() != null) {
            options++;
            song.addAll(SearchSongByName(filter.getName()));
        }

        if (filter.getGenre() != null) {
            options++;
            song.addAll(SearchSongByGenre(filter.getGenre()));
        }

        if (filter.getLyrics() != null) {
            options++;
            song.addAll(SearchSongByLyrics(filter.getLyrics()));
        }

        if (filter.getReleaseYear() != null) {
            options++;
            song.addAll(SearchSongByYear(filter.getReleaseYear()));
        }

        if (filter.getTags() != null) {
            options++;
            song.addAll(SearchSongByTags(filter.getTags()));
        }

        if (options > 1) {
            ArrayList<Song> temp = new ArrayList<>();

            for(Song s : song)
                if(Collections.frequency(song, s) > 1)
                    temp.add(s);

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
