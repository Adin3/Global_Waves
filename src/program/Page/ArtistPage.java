package program.page;

import program.admin.Manager;
import program.format.Event;
import program.format.Merch;
import program.user.User;

import java.util.ArrayList;

public class ArtistPage implements PageStrategy {
    private String artist;

    ArtistPage(final String artist) {
        this.artist = artist;
    }


    /**
     * prints the artist's page
     */
    @Override
    public String printCurrentPage() {
        StringBuilder result = new StringBuilder("Albums:\n\t[");
        User user = Manager.getUsers().get(artist);
        String[] albumName = user.getAlbums().keySet().toArray(new String[0]);
        for (int i = 0; i < albumName.length; i++) {
            result.append(albumName[i]);
            if (i < user.getAlbums().size() - 1) {
                result.append(", ");
            }
        }

        result.append("]\n\nMerch:\n\t[");
        ArrayList<Merch> merch = new ArrayList<>(user.getMerchs().values());
        for (int i = 0; i < merch.size(); i++) {
            result.append(merch.get(i));
            if (i < merch.size() - 1) {
                result.append(", ");
            }
        }

        result.append("]\n\nEvents:\n\t[");
        ArrayList<Event> event = new ArrayList<>(user.getEvents().values());
        for (int i = 0; i < event.size(); i++) {
            result.append(event.get(i));
            if (i < event.size() - 1) {
                result.append(", ");
            }
        }

        result.append("]");
        return result.toString();
    }
}
