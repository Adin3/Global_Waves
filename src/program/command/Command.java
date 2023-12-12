package program.command;

import lombok.Getter;
import lombok.Setter;
import program.format.Episode;
import program.format.Song;

import java.util.ArrayList;


@Getter @Setter
public final class Command {

    private String command;

    private String username;

    private int timestamp;

    private String type;

    private int itemNumber;

    private Filters filters;

    private String playlistName;

    private int playlistId;

    private int seed;

    private int age;

    private String city;

    private String name;

    private int releaseYear;

    private String description;

    private ArrayList<Song> songs;

    private String date;

    private int price;

    private ArrayList<Episode> episodes;

    private String nextPage;
}
