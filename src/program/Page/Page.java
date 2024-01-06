package program.page;

import lombok.Getter;
import lombok.Setter;
import program.admin.Manager;

public class Page {
    private PageStrategy printingStrategy;
    @Getter
    private String nonUserName;
    private PageInvoker invoker = new PageInvoker();

    @Getter @Setter
    private String previousPage = "Home";

    @Getter
    private final String owner;

    public Page(String owner) {
        this.owner = owner;
        setPrintingStrategy(new HomePage());
    }
    /**
     * change the current page of user
     * @param pageName the name of the changed page
     */
    public boolean changePage(final String pageName) {
        nonUserName = null;
        invoker.addCommandHistory(this, pageName);

        switch (pageName) {
            case "Home" -> setPrintingStrategy(new HomePage());
            case "LikedContent" -> setPrintingStrategy(new LikedContent());
            default -> {
                if (Manager.getArtists().contains(pageName)) {
                    setPrintingStrategy(new ArtistPage(pageName));
                    nonUserName = pageName;
                    return true;
                }
                if (Manager.getHosts().contains(pageName)) {
                    setPrintingStrategy(new HostPage(pageName));
                    nonUserName = pageName;
                    return true;
                }
                return false;
            }
        }

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

    public String checkPage() {
        if (printingStrategy != null) {
            return printingStrategy.checkPage();
        } else {
            return "Error";
        }
    }

    public void previousPage() {
        invoker.undo();
    }

    public void nextPage() {
        invoker.redo();
    }
}
