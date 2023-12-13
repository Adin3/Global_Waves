package program.user;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.UserInput;
import lombok.Getter;
import program.Manager;
import program.format.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

public class HostUser extends User {

    @Getter
    private ArrayList<String> podcasts = new ArrayList<>();

    @Getter
    private ArrayList<Announcement> announcements = new ArrayList<>();
    public HostUser() {}
    public HostUser(final UserInput user) {
        this.age = user.getAge();
        this.city = user.getCity();
        this.username = user.getUsername();
        this.userType = "user";
    }

    public HostUser(String username, int age, String city, String userType) {
        this.username = username;
        this.age = age;
        this.city = city;
        this.userType = userType;
    }

    public void addPodcast() {
        if (podcasts.contains(Manager.getCommand().getName())) {
            Manager.getPartialResult().put("message",
                    username + " has another podcast with the same name.");
            return;
        }

        HashSet<String> set = new HashSet<>();
        for (Episode episode : Manager.getCommand().getEpisodes()) {
            if (!set.add(episode.getName())) {
                Manager.getPartialResult().put("message",
                        username + " has the same episode in this podcast.");
                return;
            }
        }

        Podcast podcast = new Podcast(Manager.getCommand().getName(),
                username, Manager.getCommand().getEpisodes());
        Library.getInstance().getPodcasts().add(podcast);
        podcasts.add(podcast.getName());

        Manager.getPartialResult().put("message",
                username + " has added new podcast successfully.");
    }

    public void addAnnouncement() {
        for (Announcement announce : announcements) {
            if (announce.getName().equals(Manager.getCommand().getName())) {
                Manager.getPartialResult().put("message",
                        username + " has already added an announcement with this name.");
                return;
            }
        }

        Announcement announce = new Announcement(Manager.getCommand().getName(),
                Manager.getCommand().getDescription());
        announcements.add(announce);

        Manager.getPartialResult().put("message",
                username + " has successfully added new announcement.");
    }

    public void removeAnnouncement() {
        boolean exists = false;
        for (Announcement announce : announcements) {
            if (announce.getName().equals(Manager.getCommand().getName())) {
                exists = true;
            }
        }

        if (!exists) {
            Manager.getPartialResult().put("message",
                    username + " has no announcement with the given name.");
            return;
        }

        announcements.removeIf(announcement ->
                announcement.getName().equals(Manager.getCommand().getName()));
        Manager.getPartialResult().put("message",
                username + " has successfully deleted the announcement.");
    }

    public void showPodcasts() {
        ArrayNode result = objectMapper.createArrayNode();
        for (Podcast podcast : Library.getInstance().getPodcasts()) {
            if (podcast.getOwner().equals(username)) {
                ObjectNode albumNode = objectMapper.createObjectNode();
                albumNode.put("name", podcast.getName());
                ArrayNode episodes = objectMapper.createArrayNode();
                podcast.getEpisodes().forEach((e) -> episodes.add(e.getName()));
                albumNode.set("episodes", episodes);
                result.add(albumNode);
            }
        }
        Manager.getPartialResult().set("result", result);
    }

    public void deleteUser() {
        boolean used = isUsed();

        if (!used) {
            Library.getInstance().getPodcasts().removeIf(podcast -> podcast.getOwner().equals(username));
            Manager.getHosts().remove(username);
            Manager.getSources().remove(username);
            Manager.getUsers().remove(username);
            Manager.getPartialResult().put("message",
                    username + " was successfully deleted.");
            return;
        }
        Manager.getPartialResult().put("message",
                username + " can't be deleted.");
    }

    private boolean isUsed() {
        boolean used = false;
        for (User user : Manager.getUsers().values()) {
            if (user.getUserType().equals("user")) {
                Podcast podcast = user.getMusicplayer().getPodcast();
                if (podcast != null && podcast.getOwner().equals(username)) {
                    used = true; break;
                }
            }
        }
        return used;
    }

    public void removePodcast() {
        if (!podcasts.contains(Manager.getCommand().getName())) {
            Manager.getPartialResult().put("message",
                    username + " doesn't have a podcast with the given name.");
            return;
        }

        boolean used = false;
        for (User user : Manager.getUsers().values()) {
            if (user.getUserType().equals("user")) {
                Podcast podcast = user.getMusicplayer().getPodcast();
                if (podcast != null && podcast.getName().equals(Manager.getCommand().getName())) {
                    used = true; break;
                }
            }
        }

        if (used) {
            Manager.getPartialResult().put("message",
                    username + " can't delete this podcast.");
        } else {
            podcasts.remove(Manager.getCommand().getName());
            Library.getInstance().getPodcasts().removeIf(podcast ->
                    podcast.getName().equals(Manager.getCommand().getName()));
            Manager.getPartialResult().put("message",
                    username + " deleted the podcast successfully.");
        }
    }
}
