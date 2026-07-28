package com.library.model;

public class Fine {
    private String id;
    private Loan loan;
    private double amount;
    private boolean paid;

    public Fine(String id, Loan loan, double amount) {
        this.id = id;
        this.loan = loan;
        this.amount = amount;
        this.paid = false;
    }

    public String getId() { return id; }
    public Loan getLoan() { return loan; }
    public double getAmount() { return amount; }
    public boolean isPaid() { return paid; }

    public void setAmount(double amount) { this.amount = amount; }
    public void setPaid(boolean paid) { this.paid = paid; }

    @Override
    public String toString() {
        return String.format("Fine[ID=%s, Loan=%s, Amount=$%.2f, Paid=%s]",
                id, loan.getId(), amount, paid);
    }
}
