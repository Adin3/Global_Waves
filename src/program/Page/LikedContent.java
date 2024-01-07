package program.page;

import program.admin.Manager;
import program.format.Library;
import program.format.Playlist;
import program.format.Song;
import program.user.User;

import java.util.ArrayList;
import java.util.Map;

public class LikedContent implements PageStrategy {
    /**
     * prints the liked content page of the current user
     */
    @Override
    public String printCurrentPage() {
        StringBuilder result = new StringBuilder("Liked songs:\n\t[");
        User user = Manager.getCurrentUser();
        ArrayList<Song> likedSongs = new ArrayList<>();
        for (Song s : user.getLikedSongs()) {
            likedSongs.add(Manager.findObjectByCondition(Library.getInstance().getSongs(), s));
        }

        for (int i = 0; i < user.getLikedSongs().size(); i++) {
            Song s = user.getLikedSongs().get(i);
            result.append(s.getName());
            result.append(" - ");
            result.append(s.getArtist());

            if (i < user.getLikedSongs().size() - 1) {
                result.append(", ");
            }
        }
        result.append("]\n\nFollowed playlists:\n\t[");
        ArrayList<String> followedPlaylists = user.getFollowedPlaylists();
        Map<String, Playlist> playlists = Manager.getPlaylists();

        for (int i = 0; i < followedPlaylists.size(); i++) {
            String playlistName = followedPlaylists.get(i);
            Playlist playlist = playlists.get(playlistName);

            result.append(playlistName);
            result.append(" - ");
            result.append(playlist.getOwner());

            if (i < followedPlaylists.size() - 1) {
                result.append(", ");
            }
        }
        result.append("]");
        return result.toString();
    }

    /**
     * @return "LikedContent"
     */
    public String checkPage() {
        return "LikedContent";
    }
}
