package program.page;

import program.Manager;
import program.user.User;

class HostPage implements PageStrategy {
    private String host;

    public HostPage(String host) {
        this.host = host;
    }
    @Override
    public String printCurrentPage() {
        StringBuilder result = new StringBuilder("Podcasts:\n\t[");
        User user = Manager.getUsers().get(host);
        for (int i = 0; i < user.getPodcasts().size(); i++) {
            result.append(user.getPodcasts().get(i));
            if (i < user.getPodcasts().size() - 1) {
                result.append(", ");
            }
        }

        result.append("]\n\nAnnouncements\n\t[");
        for (int i = 0; i < user.getAnnouncements().size(); i++) {
            result.append(user.getAnnouncements().get(i));
            if (i < user.getAnnouncements().size() - 1) {
                result.append(",");
            }
        }

        result.append("]");
        return result.toString();
    }
}
