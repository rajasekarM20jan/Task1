package model;

public class PaymentType {
    String payoutTypeName;
    int payoutTypeID;

    public PaymentType(String payoutTypeName, int payoutTypeID) {
        this.payoutTypeName = payoutTypeName;
        this.payoutTypeID = payoutTypeID;
    }

    public String getPayoutTypeName() {
        return payoutTypeName;
    }

    public int getPayoutTypeID() {
        return payoutTypeID;
    }
}
