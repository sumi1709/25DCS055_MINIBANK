package service;
import model.Account;
import exception.BankException;
public class AccountWorker implements Runnable {
    private final Account account; private final int times; private final long amount;
    public AccountWorker(Account account,int times,long amount){this.account=account;this.times=times;this.amount=amount;}
    @Override public void run(){System.out.println(Thread.currentThread().getName()+" started."); for(int i=0;i<times;i++){try{account.deposit(amount);}catch(BankException e){System.err.println(e.getMessage());}} System.out.println(Thread.currentThread().getName()+" finished.");}
}
