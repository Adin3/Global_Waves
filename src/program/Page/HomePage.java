package program.Page;

import program.Manager;
import program.User;
import program.format.Song;

class HomePage implements PageStrategy {
    @Override
    public String printCurrentPage() {
        StringBuilder result = new StringBuilder("Liked songs:\n\t[");
        User user = Manager.getCurrentUser();
        for (Song s : user.getLikedSongs()) {
            result.append(s.getName());
        }
        result.append("]\n\nFollowed playlists:\n\t[");
        for (String p : user.getFollowedPlaylists()) {
            result.append(p);
        }
        result.append("]");
        return result.toString();
    }
}
