package model;
public class CurrentAccount extends Account {
    private static final long serialVersionUID = 1L;
    private long overdraftLimit;
    public CurrentAccount(String ownerName, long openingBalance, long overdraftLimit) {
        super(ownerName, openingBalance);
        if (overdraftLimit < 0) throw new IllegalArgumentException("Overdraft limit cannot be negative.");
        this.overdraftLimit = overdraftLimit;
    }
    @Override public double interestRate() { return 0.0; }
    @Override public boolean canWithdraw(long amount) { return amount > 0 && balance - amount >= -overdraftLimit; }
    public long getOverdraftLimit() { return overdraftLimit; }
}
