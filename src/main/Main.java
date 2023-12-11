package main;

import checker.Checker;
import checker.CheckerConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import fileio.input.LibraryInput;
import program.CommandList;
import program.command.Command;
import program.format.Library;
import program.Manager;
import program.user.User;

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
            if (isCreated && a < 6) {
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
            CommandList.setCommand(command);
            Manager.updateDeltaTime();

            Manager.updatePlayers();

            Manager.commandInfo();

            switch (command.getCommand()) {
                case "search" -> CommandList.search();
                case "select" -> CommandList.select();
                case "load" -> CommandList.load();
                case "status" -> CommandList.status();
                case "playPause" -> CommandList.playPause();
                case "createPlaylist" -> CommandList.createPlaylist();
                case "addRemoveInPlaylist" -> CommandList.addRemoveInPlaylist();
                case "like" -> CommandList.like();
                case "showPlaylists" -> CommandList.showPlaylists();
                case "showPreferredSongs" -> CommandList.showPreferredSongs();
                case "repeat" -> CommandList.repeat();
                case "shuffle" -> CommandList.shuffle();
                case "next" -> CommandList.next();
                case "prev" -> CommandList.prev();
                case "forward" -> CommandList.forward();
                case "backward" -> CommandList.backward();
                case "follow" -> CommandList.follow();
                case "switchVisibility" -> CommandList.switchVisibility();
                case "getTop5Playlists" -> CommandList.getTop5Playlists();
                case "getTop5Songs" -> CommandList.getTop5Songs();
                case "switchConnectionStatus" -> CommandList.switchConnectionStatus();
                case "getOnlineUsers" -> CommandList.getOnlineUsers();
                case "addUser" -> CommandList.addUser();
                case "addAlbum" -> CommandList.addAlbum();
                case "showAlbums" -> CommandList.showAlbums();
                case "printCurrentPage" -> CommandList.printCurrentPage();
                case "addEvent" -> CommandList.addEvent();
                case "addMerch" -> CommandList.addMerch();
                case "getAllUsers" -> CommandList.getAllUsers();
                case "deleteUser" -> CommandList.deleteUser();
                case "addPodcast" -> CommandList.addPodcast();
                case "addAnnouncement" -> CommandList.addAnnouncement();
                case "removeAnnouncement" -> CommandList.removeAnnouncement();
                case "showPodcasts" -> CommandList.showPodcasts();
                default -> {}
            }
            Manager.checkSource();
            Manager.getResult().add(Manager.getPartialResult());
        }



        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
        objectWriter.writeValue(new File(filePath2), Manager.getResult());

        Manager.getPlaylists().clear();
        Manager.getAlbums().clear();
        Manager.getUsers().clear();
        Manager.getSources().clear();
        Manager.getArtists().clear();
        Manager.getHosts().clear();
        Manager.getNormals().clear();
    }
}
