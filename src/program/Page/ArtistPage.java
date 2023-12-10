package program.Page;

class ArtistPage implements PageStrategy {
    @Override
    public String printCurrentPage() {
        return "Artist";
    }
}
