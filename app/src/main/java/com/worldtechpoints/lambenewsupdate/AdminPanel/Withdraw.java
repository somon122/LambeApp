package com.worldtechpoints.lambenewsupdate.AdminPanel;

public class Withdraw {

    String paymentMethodName;
    String phoneNumber;
    String money;
    String uId;

    public Withdraw() {}


    public Withdraw(String paymentMethodName, String phoneNumber, String money, String uId) {
        this.paymentMethodName = paymentMethodName;
        this.phoneNumber = phoneNumber;
        this.money = money;
        this.uId = uId;
    }

    public String getPaymentMethodName() {
        return paymentMethodName;
    }

    public void setPaymentMethodName(String paymentMethodName) {
        this.paymentMethodName = paymentMethodName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }
}