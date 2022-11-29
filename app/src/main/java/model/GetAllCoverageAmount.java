package model;

public class GetAllCoverageAmount {
    String coverageAmountText;
    int coverageAmount;

    public GetAllCoverageAmount(String coverageAmountText, int coverageAmount) {
        this.coverageAmountText = coverageAmountText;
        this.coverageAmount = coverageAmount;
    }

    public String getCoverageAmountText() {
        return coverageAmountText;
    }

    public int getCoverageAmount() {
        return coverageAmount;
    }
}
