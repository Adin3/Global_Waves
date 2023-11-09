package main;

import checker.Checker;
import checker.CheckerConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.LibraryInput;
import fileio.input.PodcastInput;
import fileio.input.SongInput;
import fileio.input.UserInput;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;

/**
 * The entry point to this homework. It runs the checker that tests your implentation.
 */

public final class Main {
    static final String LIBRARY_PATH = CheckerConstants.TESTS_PATH + "library/library.json";

    /**
     * for coding style
     */
    private Main() {
    }

    /**
     * DO NOT MODIFY MAIN METHOD
     * Call the checker
     * @param args from command line
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void main(final String[] args) throws IOException {
        File directory = new File(CheckerConstants.TESTS_PATH);
        Path path = Paths.get(CheckerConstants.RESULT_PATH);

        if (Files.exists(path)) {
            File resultFile = new File(String.valueOf(path));
            for (File file : Objects.requireNonNull(resultFile.listFiles())) {
                file.delete();
            }
            resultFile.delete();
        }
        Files.createDirectories(path);
        int a = 0;
        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.getName().startsWith("library")) {
                continue;
            }

            String filepath = CheckerConstants.OUT_PATH + file.getName();
            File out = new File(filepath);
            boolean isCreated = out.createNewFile();
            if (isCreated && a < 3) {
                action(file.getName(), filepath);
                a++;
            }
        }

        Checker.calculateScore();
    }

    /**
     * @param filePath1 for input file
     * @param filePath2 for output file
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void action(final String filePath1,
                              final String filePath2) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        LibraryInput libraryInput = objectMapper.readValue(new File(LIBRARY_PATH), LibraryInput.class);
        LibraryInput.setInstance(libraryInput);
        Command[] commands = objectMapper.readValue(new File("input/" + filePath1), Command[].class);

        Manager man = Manager.getInstance();
        LibraryInput lib = LibraryInput.getInstance();

        for (UserInput user : lib.getUsers()) {
            Manager.addUser(user);
        }

        Manager.result = objectMapper.createArrayNode();

        int deltaTime = 0;
        int lastTime = 0;

        for (Command command : commands) {

            String username = command.getUsername();
            int nextTime = command.getTimestamp();
            deltaTime = nextTime - lastTime;
            lastTime = nextTime;


            Manager.updatePlayers(deltaTime);

            Manager.partialResult = objectMapper.createObjectNode();
            Manager.partialResult.put("command", command.getCommand());
            Manager.partialResult.put("user", command.getUsername());
            Manager.partialResult.put("timestamp", command.getTimestamp());

            switch (command.getCommand()) {
                case "search":
                    Manager.searchBar(username).clearSearch();

                    Manager.getUser(username).setSearchBar(command.getType());

                    Manager.searchBar(username).search(command.getFilters());

                    Manager.searchBar(username).searchDone();

                    break;
                case "select":
                    Manager.searchBar(username).select(command.getItemNumber());

                    break;
                case "load":
                    Manager.getUser(username).setMusicPlayer();

                    Manager.musicPlayer(username).load();

                    break;
                case "status":
                    Manager.musicPlayer(username).status();

                    break;
                case "playPause":
                    Manager.musicPlayer(username).playPause();

                    break;
                case "createPlaylist":
                    Manager.addPlaylist(username, command.getPlaylistName());

                    break;
                case "addRemoveInPlaylist":
                    int id = command.getPlaylistId();
                    Playlist playlist = Manager.playlist(username, id);
                    if (playlist == null) {
                        Manager.partialResult.put("message", "Error");
                    } else {
                        playlist.addSong(Manager.searchBar(username).getSongLoaded());
                    }
                    break;
            }
            Manager.result.add(Manager.partialResult);
        }



        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
        objectWriter.writeValue(new File(filePath2), Manager.result);
    }
}
