package main;

import checker.Checker;
import checker.CheckerConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import fileio.input.LibraryInput;
import program.command.Command;
import program.format.Library;
import program.Manager;
import program.User;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
            if (isCreated && a < 5) {
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
        LibraryInput libraryInput = objectMapper.readValue(new File(LIBRARY_PATH),
                LibraryInput.class);
        Library.setInstance(libraryInput);
        Library.getInstance().setMaxDuration();
        Command[] commands = objectMapper.readValue(new File("input/" + filePath1),
                Command[].class);

        Manager.getPlaylists().clear();
        Manager.getAlbums().clear();
        Manager.getUsers().clear();
        Manager.getSources().clear();

        Library lib = Library.getInstance();
        for (User user : lib.getUsers()) {
            Manager.addUser(user);
        }
        for (User user : lib.getUsers()) {
            Manager.addSource(user.getUsername());
        }

        Manager.setResult(objectMapper.createArrayNode());

        for (Command command : commands) {
            Manager.getCurrentCommand(command);
            Manager.updateDeltaTime();

            Manager.updatePlayers();

            Manager.commandInfo();

            switch (command.getCommand()) {
                case "search" -> Manager.search();
                case "select" -> Manager.select();
                case "load" -> Manager.load();
                case "status" -> Manager.status();
                case "playPause" -> Manager.playPause();
                case "createPlaylist" -> Manager.createPlaylist();
                case "addRemoveInPlaylist" -> Manager.addRemoveInPlaylist();
                case "like" -> Manager.like();
                case "showPlaylists" -> Manager.showPlaylists();
                case "showPreferredSongs" -> Manager.showPreferredSongs();
                case "repeat" -> Manager.repeat();
                case "shuffle" -> Manager.shuffle();
                case "next" -> Manager.next();
                case "prev" -> Manager.prev();
                case "forward" -> Manager.forward();
                case "backward" -> Manager.backward();
                case "follow" -> Manager.follow();
                case "switchVisibility" -> Manager.switchVisibility();
                case "getTop5Playlists" -> Manager.getTop5Playlists();
                case "getTop5Songs" -> Manager.getTop5Songs();
                case "switchConnectionStatus" -> Manager.switchConnectionStatus();
                case "getOnlineUsers" -> Manager.getOnlineUsers();
                case "addUser" -> Manager.addUser();
                case "addAlbum" -> Manager.addAlbum();
                case "showAlbums" -> Manager.showAlbums();
                case "printCurrentPage" -> Manager.printCurrentPage();
                default -> {}
            }
            Manager.checkSource();
            Manager.getResult().add(Manager.getPartialResult());
        }



        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
        objectWriter.writeValue(new File(filePath2), Manager.getResult());
    }
}
