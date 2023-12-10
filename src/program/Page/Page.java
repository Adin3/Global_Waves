package program.page;

import program.Manager;

public class Page {
    private PageStrategy printingStrategy;

    public Page() {
        setPrintingStrategy(new HomePage());
    }

    public boolean changePage(String pageName) {
        switch (pageName) {
            case "Home" -> setPrintingStrategy(new HomePage());
            case "LikedContent" -> setPrintingStrategy(new LikedContent());
            default -> {
                if (Manager.getArtists().contains(pageName)) {
                    setPrintingStrategy(new ArtistPage(pageName));
                    return true;
                }
                if (Manager.getHosts().contains(pageName)) {
                    setPrintingStrategy(new HostPage(pageName));
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
