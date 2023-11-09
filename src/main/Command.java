package main;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter @Setter
public class Command {

    private String command;

    private String username;

    private int timestamp;

    private String type;

    private int itemNumber;

    private Filters filters;

    private String playlistName;

    private int playlistId;
}
