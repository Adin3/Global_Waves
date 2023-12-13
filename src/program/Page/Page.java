package program.page;

import lombok.Getter;
import program.Manager;

public class Page {
    private PageStrategy printingStrategy;
    @Getter
    private String nonUserName;
    public Page() {
        setPrintingStrategy(new HomePage());
    }

    public boolean changePage(String pageName) {
        nonUserName = null;
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
    private void setPrintingStrategy(PageStrategy printingStrategy) {
        this.printingStrategy = printingStrategy;
    }

    public String printCurrentPage() {
        if (printingStrategy != null) {
            return printingStrategy.printCurrentPage();
        } else {
            return "Error";
        }
    }
}
