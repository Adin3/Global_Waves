package fileio.input;

import main.SearchBar;

import java.util.ArrayList;

public final class LibraryInput {

    private static LibraryInput instance = null;
    private ArrayList<SongInput> songs;
    private ArrayList<PodcastInput> podcasts;
    private ArrayList<UserInput> users;

    private LibraryInput() {}

    public static LibraryInput getInstance() {
        if (instance == null) {
            instance = new LibraryInput();
        }
        return instance;
    }

    public static void setInstance(LibraryInput lib) {
        instance = lib;
    }

    public ArrayList<SongInput> getSongs() {
        return songs;
    }

    public void setSongs(final ArrayList<SongInput> songs) {
        this.songs = songs;
    }

    public ArrayList<PodcastInput> getPodcasts() {
        return podcasts;
    }

    public void setPodcasts(final ArrayList<PodcastInput> podcasts) {
        this.podcasts = podcasts;
    }

    public ArrayList<UserInput> getUsers() {
        return users;
    }

    public void setUsers(final ArrayList<UserInput> users) {
        this.users = users;
    }

    public void setMaxDuration() {
        for (SongInput song : songs) {
            song.setMaxDuration(song.getDuration());
        }

        for (PodcastInput podcast : podcasts) {
            for (EpisodeInput episode : podcast.getEpisodes()) {
                episode.setMaxDuration(episode.getDuration());
            }
        }
    }
}
