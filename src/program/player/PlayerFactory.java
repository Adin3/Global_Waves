package program.player;

public final class PlayerFactory {
    private static PlayerFactory instance;
    private PlayerFactory() { }

    /**
     * @return the instance of SearchBarFactory
     */
    public static PlayerFactory getInstance() {
        if (instance == null) {
            instance = new PlayerFactory();
        }

        return instance;
    }

    /**
     * create a new player depending on the type
     * @param type the type of player
     * @param username the owner of the player
     */
    public Player createPlayer(final String type, final String username) {
        switch (type) {
            case "song" -> {
                return new MusicPlayer(username);
            }
            case "podcast" -> {
                return new PodcastPlayer(username);
            }
            case "playlist" -> {
                return new PlaylistPlayer(username);
            }
            case "album" -> {
                return new AlbumPlayer(username);
            }
            default -> { }
        }

        return new Player();
    }
}
