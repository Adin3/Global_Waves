package program.page;

import program.Manager;
import program.format.Event;
import program.format.Merch;
import program.format.Song;
import program.user.User;

class ArtistPage implements PageStrategy {
    private String artist;

    public ArtistPage(String artist) {
        this.artist = artist;
    }
    @Override
    public String printCurrentPage() {
        StringBuilder result = new StringBuilder("Albums:\n\t[");
        User user = Manager.getUsers().get(artist);
        for (int i = 0; i < user.getAlbums().size(); i++) {
            result.append(user.getAlbums().get(i));
            if (i < user.getAlbums().size() - 1) {
                result.append(", ");
            }
        }

        result.append("]\n\nMerch:\n\t[");
        for (int i = 0; i < user.getMerch().size(); i++) {
            result.append(user.getMerch().get(i));
            if (i < user.getMerch().size() - 1) {
                result.append(", ");
            }
        }

        result.append("]\n\nEvents:\n\t[");
        for (int i = 0; i < user.getEvents().size(); i++) {
            result.append(user.getEvents().get(i));
            if (i < user.getEvents().size() - 1) {
                result.append(", ");
            }
        }

        result.append("]");
        return result.toString();
    }
}
