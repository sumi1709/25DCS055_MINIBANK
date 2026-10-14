import model.SavingsAccount;
import model.Account;
import service.AccountWorker;
import exception.BankException;
import exception.InsufficientFundsException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MiniBankTest {
    @Test void depositIncreasesBalance() throws BankException {
        Account a=new SavingsAccount("Test",1000,0);
        a.deposit(500);
        assertEquals(1500,a.getBalance());
    }
    @Test void withdrawalOverBalanceThrows() {
        Account a=new SavingsAccount("Test",1000,0);
        assertThrows(InsufficientFundsException.class,()->a.withdraw(1500));
    }
    @Test void concurrentDepositsKeepCorrectBalance() throws InterruptedException {
        Account a=new SavingsAccount("Thread Test",0,0);
        Thread[] threads=new Thread[10];
        for(int i=0;i<threads.length;i++){threads[i]=new Thread(new AccountWorker(a,1000,1));threads[i].start();}
        for(Thread t:threads)t.join();
        assertEquals(10000,a.getBalance());
    }
}
