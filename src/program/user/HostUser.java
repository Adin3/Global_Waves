package program.user;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.UserInput;
import lombok.Getter;
import program.admin.Manager;
import program.format.Announcement;
import program.format.Episode;
import program.format.Library;
import program.format.Podcast;

import java.util.Map;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Comparator;

public class HostUser extends User {

    private static final int TOP5 = 5;
    @Getter
    private ArrayList<String> podcasts = new ArrayList<>();

    @Getter
    private ArrayList<Announcement> announcements = new ArrayList<>();

    @Getter
    private Map<String, Integer> listenedEpisodes = new LinkedHashMap<>();

    private Set<String> listeners = new HashSet<>();
    public HostUser() { }
    public HostUser(final UserInput user) {
        this.age = user.getAge();
        this.city = user.getCity();
        this.username = user.getUsername();
        this.userType = "host";
        idCount++;
        this.id = idCount;
    }

    public HostUser(final String username, final int age,
                    final String city, final String userType) {
        this.username = username;
        this.age = age;
        this.city = city;
        this.userType = userType;
        idCount++;
        this.id = idCount;
    }
    /**
     * adds a new Podcast
     */
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
    /**
     * adds an announcement
     */
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

    /**
     * removes an announcement
     */
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

    /**
     * shows all the podcasts of the host
     */
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

    /**
     * deletes the user
     */
    public void deleteUser() {
        boolean used = isUsed();

        if (!used) {
            Library.getInstance().getPodcasts().removeIf(
                    podcast -> podcast.getOwner().equals(username));
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
                if (username.equals(user.getCurrentPage().getNonUserName())) {
                    used = true; break;
                }
            }
        }
        return used;
    }
    /**
     * removes a podcast from library
     */
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

    /**
     * save the listened episode
     * @param episode the listened episode
     * @param listener the listener
     */
    public void setListenedEpisode(final Episode episode, final String listener) {
        int freq = listenedEpisodes.getOrDefault(episode.getName(), 0) + 1;
        listenedEpisodes.put(episode.getName(), freq);
        listeners.add(listener);
    }

    private ObjectNode topEpisodes() {
        ObjectNode topEpisodes = objectMapper.createObjectNode();
        ArrayList<String> episodes = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : listenedEpisodes.entrySet()) {
            episodes.add(entry.getKey());
        }

        episodes.sort(new Comparator<String>() {
            @Override
            public int compare(final String o1, final String o2) {
                int rez = listenedEpisodes.get(o1) - listenedEpisodes.get(o2);

                if (rez == 0) {
                    return o1.compareTo(o2);
                }

                return -rez;
            }
        });

        while (episodes.size() > TOP5) {
            episodes.remove(episodes.size() - 1);
        }

        for (String name : episodes) {
            topEpisodes.put(name, listenedEpisodes.get(name));
        }
        return topEpisodes;
    }

    private int emptyWrapped() {
        return listeners.size()
                + listenedEpisodes.size();
    }
    /**
     * shows that user's wrapped
     */
    public void wrapped() {
        ObjectNode result = objectMapper.createObjectNode();
        if (emptyWrapped() == 0) {
            Manager.getPartialResult().put("message", "No data to show for host "
                    + username + ".");
            return;
        }
        result.set("topEpisodes", topEpisodes());
        result.put("listeners", listeners.size());
        Manager.getPartialResult().set("result", result);
    }
}
