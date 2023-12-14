package program.page;

import program.Manager;
import program.format.Library;
import program.format.Playlist;
import program.format.Song;
import program.user.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;

class LikedContent implements PageStrategy {
    @Override
    public String printCurrentPage() {
        StringBuilder result = new StringBuilder("Liked songs:\n\t[");
        User user = Manager.getCurrentUser();
        ArrayList<Song> likedSongs = new ArrayList<>();
        for (Song s : user.getLikedSongs()) {
            likedSongs.add(Manager.findObjectByCondition(Library.getInstance().getSongs(), s));
        }
//
//        likedSongs.sort(new Comparator<>() {
//            @Override
//            public int compare(Song song1, Song song2) {
//                int like = Integer.compare(song1.getLikes(), song2.getLikes());
//
//                if (like == 0) {
//                    int rez = song1.getName().compareTo(song2.getName());
//                    return -rez;
//                }
//
//                return -like;
//            }
//        });

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
}
