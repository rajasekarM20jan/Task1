package model;

public class ListModel {
    String policyTerm,tillYouTurn
            ,claimSettlement,lumpsum
            ,monthlyIncome,premedical
            ,cover,limitedPay,priceInc
            ,download,kshButtonTxt;
    int drawableID;

    public ListModel(String policyTerm, String tillYouTurn,
                     String claimSettlement, String lumpsum,
                     String monthlyIncome, String premedical, String cover,
                     String limitedPay, String priceInc, String download, String kshButtonTxt, int drawableID) {
        this.policyTerm = policyTerm;
        this.tillYouTurn = tillYouTurn;
        this.claimSettlement = claimSettlement;
        this.lumpsum = lumpsum;
        this.monthlyIncome = monthlyIncome;
        this.premedical = premedical;
        this.cover = cover;
        this.limitedPay = limitedPay;
        this.priceInc = priceInc;
        this.download = download;
        this.kshButtonTxt = kshButtonTxt;
        this.drawableID=drawableID;
    }


    public String getPolicyTerm() {
        return policyTerm;
    }

    public String getTillYouTurn() {
        return tillYouTurn;
    }

    public String getClaimSettlement() {
        return claimSettlement;
    }

    public String getLumpsum() {
        return lumpsum;
    }

    public String getMonthlyIncome() {
        return monthlyIncome;
    }

    public String getPremedical() {
        return premedical;
    }

    public String getCover() {
        return cover;
    }

    public String getLimitedPay() {
        return limitedPay;
    }

    public String getPriceInc() {
        return priceInc;
    }

    public String getDownload() {
        return download;
    }

    public String getKshButtonTxt() {
        return kshButtonTxt;
    }

    public int getDrawableID() {
        return drawableID;
    }
}
