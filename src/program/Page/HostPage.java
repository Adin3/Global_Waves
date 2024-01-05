package program.page;

import program.admin.Manager;
import program.format.Library;
import program.format.Podcast;
import program.user.User;

public class HostPage implements PageStrategy {
    private String host;

    HostPage(final String host) {
        this.host = host;
    }

    /**
     * prints the host's page
     */
    @Override
    public String printCurrentPage() {
        StringBuilder result = new StringBuilder("Podcasts:\n\t[");
        User user = Manager.getUsers().get(host);
        for (int i = 0; i < user.getPodcasts().size(); i++) {
            for (Podcast pod : Library.getInstance().getPodcasts()) {
                if (pod.getName().equals(user.getPodcasts().get(i))) {
                    result.append(pod);
                    break;
                }
            }
            if (i < user.getPodcasts().size() - 1) {
                result.append(", ");
            }
        }

        result.append("]\n\nAnnouncements:\n\t[");
        for (int i = 0; i < user.getAnnouncements().size(); i++) {
            result.append(user.getAnnouncements().get(i));
            if (i < user.getAnnouncements().size() - 1) {
                result.append(",");
            }
        }

        result.append("]");
        return result.toString();
    }

    public String checkPage() {
        return "HostPage";
    }
}
