package model;

import java.io.Serializable;
import model.annotation.Id;
import model.annotation.Positive;
import model.annotation.MaxLength;
import exception.BankException;
import exception.InsufficientFundsException;
import exception.InvalidAmountException;

public abstract class Account implements Transactable, InterestBearing, Comparable<Account>, Serializable {
    private static final long serialVersionUID = 1L;
    @Id private final String accountNumber;
    @MaxLength(20) private String ownerName;
    @Positive protected long balance;
    private boolean active = true;
    private static long accountCounter = 0;

    public Account(String ownerName, long openingBalance) { this(ownerName, openingBalance, true); }
    protected Account(String ownerName, long openingBalance, boolean validateOpeningBalance) {
        if (validateOpeningBalance && openingBalance < 0) throw new IllegalArgumentException("Opening balance cannot be negative.");
        synchronized (Account.class) { accountCounter++; this.accountNumber = String.format("AC%04d", accountCounter); }
        this.ownerName = ownerName; this.balance = openingBalance;
    }
    public Account(String ownerName) { this(ownerName, 0); }
    public abstract double interestRate();
    public abstract boolean canWithdraw(long amount);

    @Override public synchronized void deposit(long amount) throws InvalidAmountException {
        if (amount <= 0) throw new InvalidAmountException("Deposit amount must be greater than zero.");
        if (!active) throw new InvalidAmountException("Account is inactive.");
        balance = Math.addExact(balance, amount);
    }
    @Override public synchronized boolean withdraw(long amount) throws InsufficientFundsException, InvalidAmountException {
        if (amount <= 0) throw new InvalidAmountException("Withdrawal amount must be greater than zero.");
        if (!active) throw new InvalidAmountException("Account is inactive.");
        if (!canWithdraw(amount)) {
            long shortfall = amount;
            if (!(this instanceof FixedDepositAccount)) shortfall = Math.max(1L, amount - Math.max(0L, balance));
            throw new InsufficientFundsException("Withdrawal is not allowed or funds are insufficient.", shortfall);
        }
        balance -= amount;
        return true;
    }

    public void transfer(Account to, long amount) throws BankException {
        if (to == null) throw new BankException("Destination account is required.");
        if (to == this) throw new BankException("Source and destination accounts must be different.");
        Account first = this.accountNumber.compareTo(to.accountNumber) < 0 ? this : to;
        Account second = first == this ? to : this;
        try {
            synchronized (first) {
                synchronized (second) {
                    this.withdraw(amount);
                    try { to.deposit(amount); }
                    catch (BankException e) { this.balance += amount; throw e; }
                }
            }
        } catch (BankException e) {
            throw e;
        } finally {
            // Java releases both account locks when the synchronized blocks exit.
        }
    }

    public static synchronized void updateCounterFrom(Account[] accounts) {
        if (accounts == null) return;
        for (Account account : accounts) {
            if (account == null) continue;
            String id = account.getAccountNumber();
            if (id != null && id.matches("AC[0-9]+")) {
                try { accountCounter = Math.max(accountCounter, Long.parseLong(id.substring(2))); }
                catch (NumberFormatException ignored) { }
            }
        }
    }
    public String getAccountNumber() { return accountNumber; }
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    @Override public synchronized double getBalance() { return balance; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    @Override public String toString() { return "Account Number: " + accountNumber + ", Owner: " + ownerName + ", Balance: Rs. " + getBalance() + ", Interest Rate: " + interestRate() + "%"; }
    @Override public boolean equals(Object obj) { if (this == obj) return true; if (!(obj instanceof Account other)) return false; return accountNumber.equals(other.accountNumber); }
    @Override public int hashCode() { return accountNumber.hashCode(); }
    @Override public int compareTo(Account other) { return this.accountNumber.compareTo(other.accountNumber); }
}
