package program.Page;
public class Page {
    private PageStrategy printingStrategy;

    public Page() {
        setPrintingStrategy(new HomePage());
    }

    public boolean changePage(String pageName) {
        switch (pageName) {
            case "Home" -> setPrintingStrategy(new HomePage());
            case "LikedContent" -> setPrintingStrategy(new LikedContent());
            case "Artist" -> setPrintingStrategy(new ArtistPage());
            case "Host" -> setPrintingStrategy(new HostPage());
            default -> {
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
