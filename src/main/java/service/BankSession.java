package service;

public class BankSession implements AutoCloseable {
    public BankSession() { System.out.println("Bank session opened."); }
    public String message() { return "Using try-with-resources for a bank session."; }
    @Override public void close() { System.out.println("Bank session closed automatically."); }
}
