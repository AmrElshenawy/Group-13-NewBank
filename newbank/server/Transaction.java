package newbank.server;

import java.time.LocalDateTime;

public class Transaction {
    protected int transactionId;
    protected LocalDateTime transactionDateTime;
    protected int senderAccountId;
    protected String senderName;
    protected int receiverAccountId;
    protected String receiverName;
    protected Double amount;
    protected String message;
    enum TransactionType {DEPOSIT, WITHDRAWAL, TRANSFER, PAYMENT, MICROLOAN, REPAYMENT}
    private Transaction.TransactionType transactionType;

    public Transaction(LocalDateTime dateTime, int senderId, String senderName, int receiverId, String receiverName, Double amount, String message, TransactionType type){
        this.transactionDateTime = dateTime;
        this.senderAccountId = senderId;
        this.senderName = senderName;
        this.receiverAccountId = receiverId;
        this.receiverName = receiverName;
        this.amount = amount;
        this.message = message;
        //transaction id defined a hashcode of combination datetime + sender account + receiver account
        this.transactionId = (dateTime.toString()+senderId+receiverId).hashCode();
        this.transactionType = type;
    }

    //getters
    public LocalDateTime getTransactionDateTime() {
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

    public TransactionType getTransactionType() {
        return transactionType;
    }

    //setters
    public void setTransactionDateTime(LocalDateTime transactionDateTime) {
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