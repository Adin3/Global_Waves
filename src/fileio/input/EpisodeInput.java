package fileio.input;

import lombok.Getter;
import lombok.Setter;

public final class EpisodeInput {
    private String name;
    private Integer duration;

    @Getter @Setter
    private Integer maxDuration;
    private String description;

    public EpisodeInput() {
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(final Integer duration) {
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }
}
