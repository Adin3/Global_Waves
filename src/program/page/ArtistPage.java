package program.page;

class ArtistPage implements PageStrategy {
    @Override
    public String printCurrentPage() {
        return "Artist";
    }
}
