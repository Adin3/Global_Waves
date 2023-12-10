package program.format;

import fileio.input.SongInput;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public final class Song {
    private String name;
    private Integer duration;

    @Getter
    @Setter
    private Integer maxDuration;

    private String album;
    private ArrayList<String> tags = new ArrayList<>();
    private String lyrics;
    private String genre;
    private Integer releaseYear;
    private String artist;

    @Getter
    private int id;

    public Song() {}

    public Song(final SongInput song, final int id) {
        this.name = song.getName();
        this.album = song.getAlbum();
        this.artist = song.getArtist();
        this.genre = song.getGenre();
        this.lyrics = song.getLyrics();
        this.duration = song.getDuration();
        this.maxDuration = song.getDuration();
        this.releaseYear = song.getReleaseYear();
        this.tags.addAll(song.getTags());
        this.id = id;
    }

    public Song(final Song song) {
        this.name = song.getName();
        this.album = song.getAlbum();
        this.artist = song.getArtist();
        this.genre = song.getGenre();
        this.lyrics = song.getLyrics();
        this.duration = song.getDuration();
        this.releaseYear = song.getReleaseYear();
        this.tags.addAll(song.getTags());
        this.maxDuration = song.getMaxDuration();
        this.id = song.getId();
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(final Integer duration) {
        this.duration = duration;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(final String album) {
        this.album = album;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(final ArrayList<String> tags) {
        this.tags = tags;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(final String lyrics) {
        this.lyrics = lyrics;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(final String genre) {
        this.genre = genre;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(final int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(final String artist) {
        this.artist = artist;
    }
}
