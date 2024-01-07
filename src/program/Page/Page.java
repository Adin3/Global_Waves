package program.page;

import lombok.Getter;
import lombok.Setter;
import program.admin.Manager;
import program.user.User;

public class Page {
    private PageStrategy printingStrategy;
    @Getter
    private String nonUserName;
    private PageInvoker invoker = new PageInvoker();

    @Getter @Setter
    private String previousPage = "Home";

    @Getter
    private final String owner;

    public Page(final String owner) {
        this.owner = owner;
        setPrintingStrategy(new HomePage());
        invoker.addCommandHistory(this, "Home");
    }
    /**
     * change the current page of user
     * @param pageName the name of the changed page
     */
    public boolean changePage(final String pageName) {
        nonUserName = null;
        String pageSave = pageName;

        switch (pageName) {
            case "Home" -> setPrintingStrategy(new HomePage());
            case "LikedContent" -> setPrintingStrategy(new LikedContent());
            case "Artist" -> {
                User artist = Manager.getCurrentUser();
                if (artist.getMusicplayer() == null || artist.getMusicplayer().getSong() == null) {
                    return false;
                }
                nonUserName = Manager.getCurrentUser().getMusicplayer().getSong().getArtist();
                setPrintingStrategy(new ArtistPage(nonUserName));
                pageSave = nonUserName;
            }
            case "Host" -> {
                User host = Manager.getCurrentUser();
                if (host.getMusicplayer() == null || host.getMusicplayer().getPodcast() == null) {
                    return false;
                }
                nonUserName = Manager.getCurrentUser().getMusicplayer().getPodcast().getOwner();
                setPrintingStrategy(new HostPage(nonUserName));
                pageSave = nonUserName;
            }
            default -> {
                if (Manager.getArtists().contains(pageName)) {
                    setPrintingStrategy(new ArtistPage(pageName));
                    nonUserName = pageName;
                    invoker.addCommandHistory(this, pageSave);
                    return true;
                }
                if (Manager.getHosts().contains(pageName)) {
                    setPrintingStrategy(new HostPage(pageName));
                    nonUserName = pageName;
                    invoker.addCommandHistory(this, pageSave);
                    return true;
                }
                return false;
            }
        }

        invoker.addCommandHistory(this, pageSave);
        return true;
    }
    /**
     * sets the type of page
     * @param printingStrategy variable that decides what type of page will be loaded
     */
    private void setPrintingStrategy(final PageStrategy printingStrategy) {
        this.printingStrategy = printingStrategy;
    }

    /**
     * prints the current page
     */
    public String printCurrentPage() {
        if (printingStrategy != null) {
            return printingStrategy.printCurrentPage();
        } else {
            return "Error";
        }
    }

    /**
     * @return the page type
     */
    public String checkPage() {
        if (printingStrategy != null) {
            return printingStrategy.checkPage();
        } else {
            return "Error";
        }
    }

    /**
     * return to previous page
     */
    public void previousPage() {
        invoker.undo();
    }


    /**
     * go to next page
     */
    public void nextPage() {
        invoker.redo();
    }
}
