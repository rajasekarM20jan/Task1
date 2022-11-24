package model;

public class ComparisonListModel {
    String companyName;
    int companyLogo;

    public ComparisonListModel(String companyName, int companyLogo) {
        this.companyName = companyName;
        this.companyLogo = companyLogo;
    }

    public String getCompanyName() {
        return companyName;
    }

    public int getCompanyLogo() {
        return companyLogo;
    }
}
