package program.page;

import program.Manager;
import program.format.Library;
import program.format.Song;
import program.user.User;

class LikedContent implements PageStrategy {
    @Override
    public String printCurrentPage() {
        StringBuilder result = new StringBuilder("Liked songs:\n\t[");
        User user = Manager.getCurrentUser();
        for (Song s : user.getLikedSongs()) {
            result.append(s.getName());
            result.append(" - ");
            result.append(s.getArtist());
        }
        result.append("]\n\nFollowed playlists:\n\t[");
        for (String p : user.getFollowedPlaylists()) {
            result.append(p);
            result.append(" - ");
            result.append(Manager.getPlaylists().get(p).getOwner());
        }
        result.append("]");
        return result.toString();
    }
}
