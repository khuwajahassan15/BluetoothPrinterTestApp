package com.example.pritertestappchritopher;

public class InvoiceForPrint {
    private  double msatoshi;
    private String payment_preimage;
    private  long  paid_at;
    private String purchasedItems;
    private String tax;


    public double getMsatoshi() {
        return msatoshi;
    }

    public void setMsatoshi(double msatoshi) {
        this.msatoshi = msatoshi;
    }

    public String getPayment_preimage() {
        return payment_preimage;
    }

    public void setPayment_preimage(String payment_preimage) {
        this.payment_preimage = payment_preimage;
    }

    public long getPaid_at() {
        return paid_at;
    }

    public void setPaid_at(long paid_at) {
        this.paid_at = paid_at;
    }

    public String getPurchasedItems() {
        return purchasedItems;
    }

    public void setPurchasedItems(String purchasedItems) {
        this.purchasedItems = purchasedItems;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }
}
