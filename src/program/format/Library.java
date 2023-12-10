package program.format;

import fileio.input.PodcastInput;
import fileio.input.UserInput;
import fileio.input.LibraryInput;
import program.Manager;
import program.user.User;

import java.util.ArrayList;

public final class Library {
    private static Library instance = null;
    private static ArrayList<Song> songs = new ArrayList<>();
    private static ArrayList<Podcast> podcasts = new ArrayList<>();
    private static ArrayList<User> users = new ArrayList<>();

    private Library() { }


    /**
     * Returns the instance of Library
     */
    public static Library getInstance() {
        if (instance == null) {
            instance = new Library();
        }
        return instance;
    }

    /**
     * Sets the instance of library
     * @param lib get the libraryInput info
     */
    public static void setInstance(final LibraryInput lib) {
        instance = new Library();
        songs.clear();
        podcasts.clear();
        users.clear();
        for (int i = 0;  i < lib.getSongs().size(); i++) {
            songs.add(new Song(lib.getSongs().get(i), i));
        }
        for (PodcastInput podcast : lib.getPodcasts()) {
            podcasts.add(new Podcast(podcast));
        }
        for (UserInput user : lib.getUsers()) {
            Manager.getNormals().add(user.getUsername());
            users.add(new User(user));
        }
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public void setSongs(final ArrayList<Song> songs) {
        this.songs = songs;
    }

    public ArrayList<Podcast> getPodcasts() {
        return podcasts;
    }

    public void setPodcasts(final ArrayList<Podcast> podcasts) {
        this.podcasts = podcasts;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(final ArrayList<User> users) {
        this.users = users;
    }

    /**
     * Sets the maximum duration for all songs
     */
    public void setMaxDuration() {
        for (Song song : songs) {
            song.setMaxDuration(song.getDuration());
        }

        for (Podcast podcast : podcasts) {
            for (Episode episode : podcast.getEpisodes()) {
                episode.setMaxDuration(episode.getDuration());
            }
        }
    }
}
