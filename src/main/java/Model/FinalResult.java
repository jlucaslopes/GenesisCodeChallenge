package Model;

public class FinalResult {
    String symbol;
    Double position;
    Double originalPrice;
    Double perfomance;

    public Double getPerfomance() {
        return perfomance;
    }

    public void setPerfomance(Double perfomance) {
        this.perfomance = perfomance;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Double getPosition() {
        return position;
    }

    public void setPosition(Double position) {
        this.position = position;
    }

    public Double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(Double originalPrice) {
        this.originalPrice = originalPrice;
    }
}
