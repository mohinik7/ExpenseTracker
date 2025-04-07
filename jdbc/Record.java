package dbms_minip;

import java.sql.Date;
import java.sql.Time;

public class Record {
    private int recordId;
    private Date date;
    private Time time;
    private String categoryName;
    private String accountName;
    private double amount;
    private String notes;

    // Constructor
    public Record(int recordId, Date date, Time time, String categoryName, String accountName, double amount, String notes) {
        this.recordId = recordId;
        this.date = date;
        this.time = time;
        this.categoryName = categoryName;
        this.accountName = accountName;
        this.amount = amount;
        this.notes = notes;
    }

    // Getter and Setter methods
    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "Record{" +
                "recordId=" + recordId +
                ", date=" + date +
                ", time=" + time +
                ", categoryName='" + categoryName + '\'' +
                ", accountName='" + accountName + '\'' +
                ", amount=" + amount +
                ", notes='" + notes + '\'' +
                '}';
    }
}
