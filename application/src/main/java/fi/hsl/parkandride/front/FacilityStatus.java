package fi.hsl.parkandride.front;


import org.joda.time.Instant;

public class FacilityStatus {
    private Instant timestamp;

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
