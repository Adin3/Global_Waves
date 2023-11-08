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
            if (isCreated && a < 1) {
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
        LibraryInput.instance = objectMapper.readValue(new File(LIBRARY_PATH), LibraryInput.class);
        Command[] commands = objectMapper.readValue(new File("input/" + filePath1), Command[].class);

        ArrayList<SongInput> song = new ArrayList<>();
        ArrayList<PodcastInput> podcast = new ArrayList<>();

        ArrayNode result = objectMapper.createArrayNode();

        for (Command command : commands) {
            ObjectNode partialResult = objectMapper.createObjectNode();
            partialResult.put("command", command.getCommand());
            partialResult.put("user", command.getUsername());
            partialResult.put("timestamp", command.getTimestamp());
            switch (command.getCommand()) {
                case "search":
                    song.clear();
                    podcast.clear();

                    switch (command.getType()) {
                        case "song":
                            if (command.getFilters().getAlbum() != null) {
                                song.addAll(SearchBar.getInstance().SearchSongByAlbum(command.getFilters().getAlbum()));
                            }
                            else if (command.getFilters().getArtist() != null) {
                                song.addAll(SearchBar.getInstance().SearchSongByArtist(command.getFilters().getArtist()));
                            }
                            else if (command.getFilters().getName() != null) {
                                song.addAll(SearchBar.getInstance().SearchSongByName(command.getFilters().getName()));
                            }
                            else if (command.getFilters().getGenre() != null) {
                                song.addAll(SearchBar.getInstance().SearchSongByGenre(command.getFilters().getGenre()));
                            }
                            else if (command.getFilters().getLyrics() != null) {
                                song.addAll(SearchBar.getInstance().SearchSongByLyrics(command.getFilters().getLyrics()));
                            }
                            else if (command.getFilters().getReleaseYear() != null) {
                                song.addAll(SearchBar.getInstance().SearchSongByYear(command.getFilters().getReleaseYear()));
                            }
                            else if (command.getFilters().getTags() != null) {
                                song.addAll(SearchBar.getInstance().SearchSongByTags(command.getFilters().getTags()));
                            }

                            while (song.size() > 5) {
                                song.remove(song.size()-1);
                            }

                            partialResult.put("message", "Search returned " + song.size() + " results");

                            ArrayNode songNames = objectMapper.createArrayNode();

                            for (SongInput sg : song) {
                                songNames.add(sg.getName());
                            }

                            partialResult.set("results", songNames);

                            break;
                        case "playlist":
                            break;
                        case "podcast":
                            if (command.getFilters().getName() != null) {
                                podcast.addAll(SearchBar.getInstance().SearchPodcastByName(command.getFilters().getName()));
                            }
                            if (command.getFilters().getOwner() != null) {
                                podcast.addAll(SearchBar.getInstance().SearchPodcastByOwner(command.getFilters().getOwner()));
                            }

                            while (podcast.size() > 5) {
                                podcast.remove(podcast.size()-1);
                            }

                            partialResult.put("message", "Search returned " + podcast.size() + " results");

                            ArrayNode podcastNames = objectMapper.createArrayNode();

                            for (PodcastInput pod : podcast) {
                                podcastNames.add(pod.getName());
                            }
                            partialResult.set("results", podcastNames);

                            break;
                    }
                    break;
                case "select":
                    if (command.getItemNumber() <= song.size()) {
                        partialResult.put("message", "Successfully selected " + song.get(command.getItemNumber()-1).getName() + ".");
                    } else {
                        partialResult.put("message", "The selected ID is too high.");
                    }
                    break;
            }
            result.add(partialResult);
        }



        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
        objectWriter.writeValue(new File(filePath2), result);
    }
}
