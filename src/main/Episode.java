package main;

import fileio.input.EpisodeInput;
import lombok.Getter;
import lombok.Setter;

public class Episode {
    private String name;
    private Integer duration;

    @Getter @Setter
    private Integer maxDuration;
    private String description;

    public Episode(EpisodeInput episode) {
        this.name = episode.getName();
        this.duration = episode.getDuration();
        this.description = episode.getDescription();
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
