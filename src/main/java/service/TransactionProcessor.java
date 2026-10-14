package service;
import java.util.concurrent.*;
import java.util.Map;
import model.Account;
public class TransactionProcessor {
    private final ExecutorService executor=Executors.newFixedThreadPool(4);
    private final ConcurrentHashMap<String,Account> sharedAccounts=new ConcurrentHashMap<>();
    public void register(Account account){sharedAccounts.put(account.getAccountNumber(),account);}
    public Map<String,Account> getSharedAccounts(){return sharedAccounts;}
    public void submit(Runnable task){executor.execute(task);}
    public void stop() throws InterruptedException {executor.shutdown(); if(!executor.awaitTermination(30,TimeUnit.SECONDS)) executor.shutdownNow();}
}
