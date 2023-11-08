package main;

import fileio.input.LibraryInput;
import fileio.input.PodcastInput;
import fileio.input.SongInput;

import java.util.ArrayList;

public class SearchBar {

    private static SearchBar instance = null;
    private SearchBar() {}

    public static SearchBar getInstance() {
        if (instance == null) {
            instance = new SearchBar();
        }
        return instance;
    }


    public ArrayList<SongInput> SearchSongByName(String name) {

        ArrayList<SongInput> songs = new ArrayList<>();
        for (SongInput song : LibraryInput.getInstance().getSongs()) {
            if (song.getName().startsWith(name)) {
                songs.add(song);
            }
        }
        return songs;
    }

    public ArrayList<SongInput> SearchSongByAlbum(String album) {

        ArrayList<SongInput> songs = new ArrayList<>();
        for (SongInput song : LibraryInput.getInstance().getSongs()) {
            if (song.getAlbum().equals(album)) {
                songs.add(song);
            }
        }
        return songs;
    }

    public ArrayList<SongInput> SearchSongByTags(ArrayList<String> tags) {

        ArrayList<SongInput> songs = new ArrayList<>();
        for (SongInput song : LibraryInput.getInstance().getSongs()) {
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

    public ArrayList<SongInput> SearchSongByLyrics(String lyric) {

        ArrayList<SongInput> songs = new ArrayList<>();
        for (SongInput song : LibraryInput.getInstance().getSongs()) {
            if (song.getLyrics().contains(lyric)) {
                songs.add(song);
            }
        }
        return songs;
    }

    public ArrayList<SongInput> SearchSongByGenre(String genre) {

        ArrayList<SongInput> songs = new ArrayList<>();
        for (SongInput song : LibraryInput.getInstance().getSongs()) {
            if (song.getGenre().toLowerCase().equals(genre)) {
                songs.add(song);
            }
        }
        return songs;
    }

    public ArrayList<SongInput> SearchSongByYear(String year) {

        ArrayList<SongInput> songs = new ArrayList<>();

        for (SongInput song : LibraryInput.getInstance().getSongs()) {
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

    public ArrayList<SongInput> SearchSongByArtist(String artist) {

        ArrayList<SongInput> songs = new ArrayList<>();
        for (SongInput song : LibraryInput.getInstance().getSongs()) {
            if (song.getArtist().equals(artist)) {
                songs.add(song);
            }
        }
        return songs;
    }

    public ArrayList<PodcastInput> SearchPodcastByName(String name) {

        ArrayList<PodcastInput> podcasts = new ArrayList<>();
        for (PodcastInput podcast : LibraryInput.getInstance().getPodcasts()) {
            if (podcast.getName().startsWith(name)) {
                podcasts.add(podcast);
            }
        }
        return podcasts;
    }

    public ArrayList<PodcastInput> SearchPodcastByOwner(String owner) {

        ArrayList<PodcastInput> podcasts = new ArrayList<>();
        for (PodcastInput podcast : LibraryInput.getInstance().getPodcasts()) {
            if (podcast.getOwner().equals(owner)) {
                podcasts.add(podcast);
            }
        }
        return podcasts;
    }
}
