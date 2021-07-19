package Model.AssetsApiResponses;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class AssestsEndpoint {
    @JsonProperty("timestamp")
    Long timeStamp;
    @JsonProperty("data")
    List<Assets> assets;

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public List<Assets> getAssets() {
        return assets;
    }

    public void setAssets(List<Assets> assets) {
        this.assets = assets;
    }
}
