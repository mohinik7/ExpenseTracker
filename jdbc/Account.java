package dbms_minip;

public class Account {
    private int accountId;
    private int userId;
    private String accountName;
    private double initialAmount;

    // Constructor
    public Account(int accountId, int userId, String accountName, double initialAmount) {
        this.accountId = accountId;
        this.userId = userId;
        this.accountName = accountName;
        this.initialAmount = initialAmount;
    }

    // Getters and setters for each field
    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public double getInitialAmount() {
        return initialAmount;
    }

    public void setInitialAmount(double initialAmount) {
        this.initialAmount = initialAmount;
    }
}
