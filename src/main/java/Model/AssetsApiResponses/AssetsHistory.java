package Model.AssetsApiResponses;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class AssetsHistory {

    @JsonProperty("data")
    List<AssetsHistoryData> historyDataList;

    @JsonProperty("timestamp")
    Long timeStamp;

    public List<AssetsHistoryData> getHistoryDataList() {
        return historyDataList;
    }

    public void setHistoryDataList(List<AssetsHistoryData> historyDataList) {
        this.historyDataList = historyDataList;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
