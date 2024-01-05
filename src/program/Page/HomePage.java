package program.page;
import program.admin.Manager;
import program.format.Library;
import program.user.User;
import program.format.Song;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HomePage implements PageStrategy {
    private static final int TOP5 = 5;
    /**
     * prints the home page of the user
     */
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
            public int compare(final Song song1, final Song song2) {
                int like = Integer.compare(song1.getLikes(), song2.getLikes());

                if (like == 0) {
                    return 0;
                }

                return -like;
            }
        });

        while (likedSongs.size() > TOP5) {
            likedSongs.remove(likedSongs.size() - 1);
        }

        for (int i = 0; i < likedSongs.size(); i++) {
            result.append(likedSongs.get(i).getName());
            if (i < likedSongs.size() - 1) {
                result.append(", ");
            }
        }

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

    public String checkPage() {
        return "HomePage";
    }
}
