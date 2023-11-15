package main;

import lombok.Getter;
import lombok.Setter;


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
}
