package program.searchbar;

public final class SearchBarFactory {
    private static SearchBarFactory instance;
    private SearchBarFactory() { }

    /**
     * @return the instance of SearchBarFactory
     */
    public static SearchBarFactory getInstance() {
        if (instance == null) {
            instance = new SearchBarFactory();
        }

        return instance;
    }

    /**
     * create a new searchbar depending on the type
     * @param type the type of searchbar
     * @param username the owner of the searchbar
     */
    public SearchBar createSearchBar(final String type, final String username) {
        switch (type) {
            case "song" -> {
                return new SearchBarSong(username);
            }
            case "podcast" -> {
                return new SearchBarPodcast(username);
            }
            case "playlist" -> {
                return new SearchBarPlaylist(username);
            }
            case "artist", "host" -> {
                return new SearchBarUser(username);
            }
            case "album" -> {
                return new SearchBarAlbum(username);
            }
            default -> { }
        }

        return null;
    }
}
