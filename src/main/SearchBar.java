package main;

import fileio.input.LibraryInput;
import fileio.input.PodcastInput;
import fileio.input.SongInput;
import lombok.Getter;

import java.util.ArrayList;

public class SearchBar {

    private Filters filter;

    @Getter
    private final ArrayList<SongInput> song = new ArrayList<>();

    @Getter
    private final ArrayList<PodcastInput> podcast = new ArrayList<>();

    @Getter
    private SongInput songLoaded;

    @Getter
    private PodcastInput podcastLoaded;
    public SearchBar() {}

    public void clearSearch() {
        song.clear();
        podcast.clear();
        songLoaded = null;
    }

    public int select(int number) {
        if (song != null) {
            if (number <= song.size()) {
                songLoaded = song.get(number-1);
                return 0;
            }
            return 1;
        } else if (podcast != null) {
            if (number <= podcast.size()) {
                podcastLoaded = podcast.get((number-1));
                return 0;
            }
            return 1;
        }
        return 2;
    }

    public void search(Filters filter, String type) {
        this.filter = filter;
        switch (type) {
            case "song":
                searchSong();
                break;
            case "playlist":
                break;
            case "podcast":
                searchPodcast();
                break;
        }
    }

    private void searchSong() {
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

    private void searchPodcast() {
        if (filter.getName() != null) {
            podcast.addAll(SearchPodcastByName(filter.getName()));
        }
        if (filter.getOwner() != null) {
            podcast.addAll(SearchPodcastByOwner(filter.getOwner()));
        }

        while (podcast.size() > 5) {
            podcast.remove(podcast.size()-1);
        }
    }
    private ArrayList<SongInput> SearchSongByName(String name) {

        ArrayList<SongInput> songs = new ArrayList<>();
        for (SongInput song : LibraryInput.getInstance().getSongs()) {
            if (song.getName().startsWith(name)) {
                songs.add(song);
            }
        }
        return songs;
    }

    private ArrayList<SongInput> SearchSongByAlbum(String album) {

        ArrayList<SongInput> songs = new ArrayList<>();
        for (SongInput song : LibraryInput.getInstance().getSongs()) {
            if (song.getAlbum().equals(album)) {
                songs.add(song);
            }
        }
        return songs;
    }

    private ArrayList<SongInput> SearchSongByTags(ArrayList<String> tags) {

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

    private ArrayList<SongInput> SearchSongByLyrics(String lyric) {

        ArrayList<SongInput> songs = new ArrayList<>();
        for (SongInput song : LibraryInput.getInstance().getSongs()) {
            if (song.getLyrics().contains(lyric)) {
                songs.add(song);
            }
        }
        return songs;
    }

    private ArrayList<SongInput> SearchSongByGenre(String genre) {

        ArrayList<SongInput> songs = new ArrayList<>();
        for (SongInput song : LibraryInput.getInstance().getSongs()) {
            if (song.getGenre().toLowerCase().equals(genre)) {
                songs.add(song);
            }
        }
        return songs;
    }

    private ArrayList<SongInput> SearchSongByYear(String year) {

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

    private ArrayList<SongInput> SearchSongByArtist(String artist) {

        ArrayList<SongInput> songs = new ArrayList<>();
        for (SongInput song : LibraryInput.getInstance().getSongs()) {
            if (song.getArtist().equals(artist)) {
                songs.add(song);
            }
        }
        return songs;
    }

    private ArrayList<PodcastInput> SearchPodcastByName(String name) {

        ArrayList<PodcastInput> podcasts = new ArrayList<>();
        for (PodcastInput podcast : LibraryInput.getInstance().getPodcasts()) {
            if (podcast.getName().startsWith(name)) {
                podcasts.add(podcast);
            }
        }
        return podcasts;
    }

    private ArrayList<PodcastInput> SearchPodcastByOwner(String owner) {

        ArrayList<PodcastInput> podcasts = new ArrayList<>();
        for (PodcastInput podcast : LibraryInput.getInstance().getPodcasts()) {
            if (podcast.getOwner().equals(owner)) {
                podcasts.add(podcast);
            }
        }
        return podcasts;
    }
}
