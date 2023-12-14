package program.format;

import fileio.input.SongInput;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Objects;

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
    private int likes;

    @Getter
    private int id;

    public Song() { }

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

    /**
     * adds a new like to the songs
     */
    public void addLike() {
        likes++;
    }
    /**
     * removes a like from the songs
     */
    public void removeLike() {
        likes--;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Song song = (Song) o;
        return Objects.equals(name, song.name)
                && Objects.equals(maxDuration, song.maxDuration)
                && Objects.equals(album, song.album)
                && Objects.equals(tags, song.tags)
                && Objects.equals(lyrics, song.lyrics)
                && Objects.equals(genre, song.genre)
                && Objects.equals(releaseYear, song.releaseYear)
                && Objects.equals(artist, song.artist);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, maxDuration, album, tags, lyrics, genre, releaseYear, artist);
    }
}
