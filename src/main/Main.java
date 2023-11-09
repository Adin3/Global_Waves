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
            if (isCreated && a < 2) {
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

        ArrayNode result = objectMapper.createArrayNode();
        MusicPlayer musicPlayer = new MusicPlayer();
        int deltaTime = 0;
        int lastTime = 0;

        for (Command command : commands) {

            String username = command.getUsername();
            int nextTime = command.getTimestamp();
            deltaTime = nextTime - lastTime;
            lastTime = nextTime;

            Manager.updatePlayers(deltaTime);

            ObjectNode partialResult = objectMapper.createObjectNode();
            partialResult.put("command", command.getCommand());
            partialResult.put("user", command.getUsername());
            partialResult.put("timestamp", command.getTimestamp());
            switch (command.getCommand()) {
                case "search":
                    Manager.searchBar(username).clearSearch();

                    Manager.searchBar(username).search(command.getFilters(), command.getType());

                    ArrayNode node = objectMapper.createArrayNode();

                    ArrayList<SongInput> song = Manager.searchBar(username).getSong();

                    ArrayList<PodcastInput> podcast = Manager.searchBar(username).getPodcast();

                    if (!song.isEmpty()) {
                        partialResult.put("message", "Search returned " + song.size() + " results");

                        for (SongInput sg : song)
                            node.add(sg.getName());

                    }
                    if (!podcast.isEmpty()) {
                        partialResult.put("message", "Search returned " + podcast.size() + " results");

                        for (PodcastInput pod : podcast)
                            node.add(pod.getName());
                    }

                    partialResult.set("results", node);

                    break;
                case "select":
                    int ret = Manager.searchBar(username).select(command.getItemNumber());

                    SongInput songLoaded = Manager.searchBar(username).getSongLoaded();

                    PodcastInput podcastLoaded = Manager.searchBar(username).getPodcastLoaded();

                    if (ret == 0) {
                        if (songLoaded != null) {
                            partialResult.put("message", "Successfully selected " + songLoaded.getName() + ".");
                        } else if (podcastLoaded != null) {
                            partialResult.put("message", "Successfully selected " + podcastLoaded.getName() + ".");
                        }
                    } else if (ret == 1) {
                        partialResult.put("message", "The selected ID is too high.");
                    } else {
                        partialResult.put("message", "Please conduct a search before making a selection.");
                    }

                    break;
                case "load":
                    if (Manager.searchBar(username).getSongLoaded() != null) {
                        Manager.musicPlayer(username).loadSong(Manager.searchBar(username).getSongLoaded());
                        partialResult.put("message", "Playback loaded successfully.");
                    }
                    break;
                case "status":
                    ObjectNode status = objectMapper.createObjectNode();
                    MusicPlayer player = Manager.musicPlayer(username);
                    if (player.getSong() == null) {
                        status.put("name", "");
                        status.put("remainedTime", 0);
                        status.put("repeat", "No Repeat");
                        status.put("shuffle", false);
                        status.put("paused", true);
                    } else {
                        status.put("name", player.getSong().getName());
                        status.put("remainedTime", player.getSong().getDuration());
                        status.put("repeat", player.repeats());
                        status.put("shuffle", player.shuffles());
                        status.put("paused", player.paused());
                    }
                    partialResult.set("stats", status);
                    break;
                case "playPause":
                    Manager.musicPlayer(username).pauseButton();
                    if (Manager.musicPlayer(username).paused()) {
                        partialResult.put("message", "Playback paused successfully.");
                    } else {
                        partialResult.put("message", "Playback resumed successfully.");
                    }
                    break;
            }
            result.add(partialResult);
        }



        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
        objectWriter.writeValue(new File(filePath2), result);
    }
}
