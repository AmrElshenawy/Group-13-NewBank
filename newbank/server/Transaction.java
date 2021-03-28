package newbank.server;

import java.util.Date;

public class Transaction {
    private int transactionId;
    private Date transactionDateTime;
    private int senderAccountId;
    private String senderName;
    private int receiverAccountId;
    private String receiverName;
    private Double amount;
    private String message;

    public Transaction(Date dateTime, int senderId, String senderName, int receiverId, String receiverName, Double amount, String message){
        this.transactionDateTime = dateTime;
        this.senderAccountId = senderId;
        this.senderName = senderName;
        this.receiverAccountId = receiverId;
        this.receiverName = receiverName;
        this.amount = amount;
        this.message = message;
        //transaction id defined a hashcode of combination datetime + sender account + receiver account
        this.transactionId = (dateTime.toString()+senderId+receiverId).hashCode();
    }

    //getters
    public Date getTransactionDateTime() {
        return transactionDateTime;
    }

    public int getSenderId() {
        return senderAccountId;
    }

    public String getSenderName() { return senderName; }

    public int getReceiverId() {
        return receiverAccountId;
    }

    public String getReceiverName() { return receiverName; }

    public Double getAmount() {
        return amount;
    }

    public String getMessage() {
        return message;
    }

    public int getTransactionId(){ return transactionId; }

    //setters
    public void setTransactionDateTime(Date transactionDateTime) {
        this.transactionDateTime = transactionDateTime;
    }

    public void setSenderId(int senderId) {
        this.senderAccountId = senderId;
    }

    public void setSenderName(String senderName) { this.senderName = senderName; }

    public void setReceiverId(int receiverId) {
        this.receiverAccountId = receiverId;
    }

    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
