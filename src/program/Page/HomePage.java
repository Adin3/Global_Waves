package program.page;

import com.fasterxml.jackson.databind.node.ArrayNode;
import program.Manager;
import program.format.Library;
import program.user.User;
import program.format.Song;

import java.util.*;

class HomePage implements PageStrategy {
    @Override
    public String printCurrentPage() {
        StringBuilder result = new StringBuilder("Liked songs:\n\t[");
        User user = Manager.getCurrentUser();
        ArrayList<Song> likedSongs = new ArrayList<>();
        for (Song s : user.getLikedSongs()) {
            likedSongs.add(Manager.findObjectByCondition(Library.getInstance().getSongs(), s));
        }

        likedSongs.sort(new Comparator<>() {
            @Override
            public int compare(Song song1, Song song2) {
                int like = Integer.compare(song1.getLikes(), song2.getLikes());

                if (like == 0) {
                    return 0;
                }

                return -like;
            }
        });

        while (likedSongs.size() > 5) {
            likedSongs.remove(likedSongs.size() - 1);
        }

        for (int i = 0; i < likedSongs.size(); i++) {
            result.append(likedSongs.get(i).getName());
            System.out.println(likedSongs.get(i).getName() + " " + likedSongs.get(i).getLikes());
            if (i < likedSongs.size() - 1) {
                result.append(", ");
            }
        }
        System.out.println("\n");
        result.append("]\n\nFollowed playlists:\n\t[");
        List<String> followedPlaylists = user.getFollowedPlaylists();
        for (int i = 0; i < followedPlaylists.size(); i++) {
            String playlistName = followedPlaylists.get(i);
            result.append(playlistName);
            if (i < followedPlaylists.size() - 1) {
                result.append(", ");
            }
        }
        result.append("]");
        return result.toString();
    }
}
