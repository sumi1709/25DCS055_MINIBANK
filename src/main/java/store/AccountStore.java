package store;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import model.Account;
public class AccountStore {
    private final HashMap<String,Account> accounts=new HashMap<>();
    private final ArrayList<String> transactionHistory=new ArrayList<>();
    public void add(Account account){accounts.put(account.getAccountNumber(),account);}
    public Account get(String accountNumber){return accounts.get(accountNumber);}
    public ArrayList<Account> getAll(){return new ArrayList<>(accounts.values());}
    public void addHistory(String line){transactionHistory.add(line);}
    public ArrayList<String> getTransactionHistory(){return new ArrayList<>(transactionHistory);}
    public ArrayList<Account> sortedByBalance(){ArrayList<Account> list=getAll();list.sort(Comparator.comparingDouble(Account::getBalance));return list;}
}
