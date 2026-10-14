package service;
import java.io.IOException;
import java.util.ArrayList;
import exception.AccountNotFoundException;
import exception.BankException;
import exception.InvalidAmountException;
import model.Account;
import model.SavingsAccount;
import store.Repository;
import util.Validator;
import util.TransactionLog;
import java.nio.file.Path;
public class BankService {
    private final Repository<Account> repository = new Repository<>();
    private final Path logFile = Path.of("logs", "transactions.log");
    public boolean validateContactDetails(String email, String mobile) {
        return Validator.isValidEmail(email) && Validator.isValidMobile(mobile);
    }
    public Account openAccount(String ownerName,long openingBalance) throws BankException {
        if(ownerName==null || ownerName.isBlank()) throw new InvalidAmountException("Owner name cannot be empty.");
        if(ownerName.length()>20) throw new InvalidAmountException("Owner name must be at most 20 characters.");
        if(openingBalance<0) throw new InvalidAmountException("Opening balance cannot be negative.");
        Account account=new SavingsAccount(ownerName,openingBalance,0);
        repository.save(account); return account;
    }
    public Account deposit(String id,long amount) throws BankException, IOException {
        Account a=requireAccount(id); a.deposit(amount); log("DEPOSIT|"+id+"|"+amount+"|"+System.currentTimeMillis()); return a;
    }
    public Account withdraw(String id,long amount) throws BankException, IOException {
        Account a=requireAccount(id); a.withdraw(amount); log("WITHDRAW|"+id+"|"+amount+"|"+System.currentTimeMillis()); return a;
    }
    public void transfer(String fromId,String toId,long amount) throws BankException, IOException {
        Account from=requireAccount(fromId), to=requireAccount(toId); from.transfer(to,amount);
        log("TRANSFER|"+fromId+"|"+toId+"|"+amount+"|"+System.currentTimeMillis());
    }
    public String statement(String id) throws AccountNotFoundException { return util.StatementFormatter.buildStatement(requireAccount(id)); }
    public ArrayList<Account> listAccounts(){return repository.findAll();}
    public Account findAccount(String id){return repository.findById(id);}
    public void loadAccounts(Account[] accounts){if(accounts!=null) for(Account a:accounts) if(a!=null) repository.save(a);}
    private Account requireAccount(String id) throws AccountNotFoundException {Account a=repository.findById(id);if(a==null)throw new AccountNotFoundException(id);return a;}
    private void log(String line) throws IOException {TransactionLog.append(logFile,line);}
}
