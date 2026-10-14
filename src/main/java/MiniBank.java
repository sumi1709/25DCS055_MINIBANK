import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;

import model.Account;
import model.Customer;
import model.SavingsAccount;
import model.CurrentAccount;
import model.FixedDepositAccount;
import model.Premium;
import model.annotation.AnnotationValidator;
import service.BankService;
import service.BankSession;
import service.WithdrawRule;
import service.AccountWorker;
import service.TransactionProcessor;
import service.TaskBuffer;
import util.Validator;
import util.Command;
import util.CommandParser;
import util.StatementFormatter;
import util.ReportGenerator;
import util.StatePersister;
import model.annotation.AnnotationValidationDemo;
import store.AccountStore;
import store.Repository;
import exception.BankException;

import static java.lang.System.out;

public class MiniBank {
    private static final Path DATA_FILE = Path.of("data", "accounts.dat");
    private static final Path LOG_DIR = Path.of("logs");
    private static final Path REPORT_FILE = Path.of("reports", "end-of-day.txt");

    record BankInfo(String name, String branch) {}
    enum MenuOption { OPEN_ACCOUNT, DEPOSIT, WITHDRAW, TRANSFER, EXIT }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        BankService service = new BankService();
        try {
            Account[] saved = StatePersister.load(DATA_FILE);
            service.loadAccounts(saved);
            out.println("Loaded " + saved.length + " saved account(s).");
        } catch (IOException | ClassNotFoundException e) {
            out.println("Could not load saved accounts: " + e.getMessage());
        }

        BankInfo bank = new BankInfo("MiniBank", "Ahmedabad");
        out.println("================================");
        out.println(bank.name() + " | Branch: " + bank.branch());
        out.println("================================");

        runDemos(service);
        int choice = 0;
        while (choice != 9) {
            out.println("\n========== MINIBANK MENU ==========");
            out.println("1. Open Savings Account");
            out.println("2. Deposit");
            out.println("3. Withdraw");
            out.println("4. Transfer");
            out.println("5. View All Accounts");
            out.println("6. Generate End-of-Day Report");
            out.println("7. Concurrency Demo");
            out.println("8. Collections / Repository Demo");
            out.println("9. Save and Exit");
            out.print("Enter your choice: ");
            if (!sc.hasNextInt()) { out.println("Please enter a number."); sc.nextLine(); continue; }
            choice = sc.nextInt(); sc.nextLine();
            try {
                // Switch expression fulfils the menu-shell requirement while invoking real operations.
                String result = switch (choice) {
                    case 1 -> openAccount(sc, service);
                    case 2 -> deposit(sc, service);
                    case 3 -> withdraw(sc, service);
                    case 4 -> transfer(sc, service);
                    case 5 -> listAccounts(service);
                    case 6 -> generateReport();
                    case 7 -> concurrencyDemo(service);
                    case 8 -> collectionsDemo(service);
                    case 9 -> "Saving data. Goodbye!";
                    default -> "Invalid choice. Choose 1–9.";
                };
                out.println(result);
            } catch (BankException | IOException | InterruptedException e) {
                out.println("Operation could not be completed: " + e.getMessage());
                if (e instanceof InterruptedException) Thread.currentThread().interrupt();
            } finally {
                // Every menu operation reaches this cleanup point.
            }
        }
        try {
            StatePersister.save(service.listAccounts().toArray(Account[]::new), DATA_FILE);
            out.println("Saved " + service.listAccounts().size() + " account(s).");
        } catch (IOException e) { out.println("Could not save accounts: " + e.getMessage()); }
        sc.close();
    }

    private static void runDemos(BankService service) {
        out.println("\n===== ACCOUNT TYPES / POLYMORPHISM =====");
        out.println("The accounts below are demonstrations only; open an account from the menu to transact.");
        Account[] examples = {
            new SavingsAccount("Priya Patel", 5000, 500),
            new CurrentAccount("Rohan Mehta", 2000, 5000),
            new FixedDepositAccount("Neha Shah", 10000)
        };
        for (Account account : examples) {
            out.println(account);
            out.println("Yearly interest estimate: Rs. " + account.yearlyInterest());
            if (account instanceof Premium) out.println("Premium account");
        }
        WithdrawRule anonymousRule = new WithdrawRule() {
            @Override public boolean allow(Account account, long amount) { return account.canWithdraw(amount); }
        };
        WithdrawRule lambdaRule = (account, amount) -> account.canWithdraw(amount);
        out.println("Anonymous-class rule: " + anonymousRule.allow(examples[0], 1000));
        out.println("Lambda rule: " + lambdaRule.allow(examples[0], 1000));

        out.println("\n===== VALIDATOR / COMMAND PARSER =====");
        out.println("Mobile valid/invalid: " + Validator.isValidMobile("9876543210") + " / " + Validator.isValidMobile("123"));
        out.println("Email valid/invalid: " + Validator.isValidEmail("test@gmail.com") + " / " + Validator.isValidEmail("bad-email"));
        out.println("PAN valid/invalid: " + Validator.isValidPan("ABCDE1234F") + " / " + Validator.isValidPan("123"));
        out.println("IFSC valid/invalid: " + Validator.isValidIfsc("ABCD0123456") + " / " + Validator.isValidIfsc("bad"));
        try { Command c = CommandParser.parse("DEPOSIT AC0001 500"); out.println("Parsed command: " + c); }
        catch (IllegalArgumentException e) { out.println(e.getMessage()); }

        Customer c = new Customer("Aarav Shah", "aarav@gmail.com", "9876543210");
        c.setAddress(new Customer.Address("Satellite Road", "Ahmedabad", "380015"));
        out.println("\n===== CUSTOMER / CLONE =====\n" + c + "\nClone: " + c.clone());
        try (BankSession session = new BankSession()) { out.println(session.message()); }
        out.println("\n===== ANNOTATION VALIDATION =====");
        for (String error : AnnotationValidationDemo.run()) out.println("- " + error);
    }

    private static String openAccount(Scanner sc, BankService service) throws BankException {
        out.print("Owner name (max 20 characters): "); String name = sc.nextLine().trim();
        out.print("Customer email (validated for this demo): "); String email = sc.nextLine().trim();
        out.print("Customer mobile (10 digits): "); String mobile = sc.nextLine().trim();
        if (!service.validateContactDetails(email, mobile)) {
            throw new exception.InvalidAmountException("Invalid email or mobile number.");
        }
        out.print("Opening balance in whole rupees: "); long amount = readLong(sc);
        Account account = service.openAccount(name, amount);
        return "Account created.\n" + StatementFormatter.buildStatement(account);
    }
    private static String deposit(Scanner sc, BankService service) throws BankException, IOException {
        out.print("Account number: "); String id = sc.nextLine().trim();
        out.print("Deposit amount: "); long amount = readLong(sc);
        Account a = service.deposit(id, amount);
        return StatementFormatter.buildStatement(a);
    }
    private static String withdraw(Scanner sc, BankService service) throws BankException, IOException {
        out.print("Account number: "); String id = sc.nextLine().trim();
        out.print("Withdrawal amount: "); long amount = readLong(sc);
        Account a = service.withdraw(id, amount);
        return StatementFormatter.buildStatement(a);
    }
    private static String transfer(Scanner sc, BankService service) throws BankException, IOException {
        out.print("From account number: "); String from = sc.nextLine().trim();
        out.print("To account number: "); String to = sc.nextLine().trim();
        out.print("Transfer amount: "); long amount = readLong(sc);
        service.transfer(from, to, amount);
        return "Transfer completed successfully.";
    }
    private static String listAccounts(BankService service) {
        ArrayList<Account> accounts = service.listAccounts();
        if (accounts.isEmpty()) return "No accounts have been opened yet.";
        StringBuilder b = new StringBuilder("\n===== ACCOUNTS =====\n");
        for (Account a : accounts) b.append(StatementFormatter.buildStatement(a));
        return b.toString();
    }
    private static String generateReport() throws IOException {
        String report = ReportGenerator.generate(LOG_DIR, REPORT_FILE);
        return "End-of-day report created at " + REPORT_FILE + "\n" + report;
    }
    private static String concurrencyDemo(BankService service) throws InterruptedException, BankException {
        Account account;
        if (service.listAccounts().isEmpty()) account = service.openAccount("Thread Demo", 0);
        else account = service.listAccounts().get(0);
        long before = (long) account.getBalance();
        Thread[] threads = new Thread[10];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new AccountWorker(account, 100, 1), "BankWorker-" + (i + 1));
            threads[i].start();
        }
        for (Thread thread : threads) thread.join();
        long afterWorkers = (long) account.getBalance();

        // Practical 10: fixed thread pool and thread-safe account registry.
        TransactionProcessor processor = new TransactionProcessor();
        processor.register(account);
        for (int i = 0; i < 20; i++) {
            processor.submit(() -> {
                try { account.deposit(1); }
                catch (BankException e) { System.err.println(e.getMessage()); }
            });
        }
        processor.stop();

        // Practical 10: producer-consumer using wait() / notifyAll().
        TaskBuffer buffer = new TaskBuffer();
        Thread producer = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                buffer.put(() -> {
                    try { account.deposit(1); }
                    catch (BankException e) { System.err.println(e.getMessage()); }
                });
            }
            buffer.close();
        }, "Transaction-Producer");
        Thread consumer = new Thread(() -> {
            try {
                Runnable task;
                while ((task = buffer.take()) != null) task.run();
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }, "Transaction-Consumer");
        producer.start(); consumer.start(); producer.join(); consumer.join();

        return "Concurrency demo finished. Before: Rs. " + before
                + ", after AccountWorker threads: Rs. " + afterWorkers
                + ", after executor and producer-consumer tasks: Rs. " + account.getBalance()
                + ". Executor registered account: " + processor.getSharedAccounts().containsKey(account.getAccountNumber());
    }
    private static String collectionsDemo(BankService service) throws BankException {
        AccountStore store = new AccountStore();
        for (Account account : service.listAccounts()) store.add(account);
        if (store.getAll().isEmpty()) {
            store.add(new SavingsAccount("Demo One", 3000, 0));
            store.add(new CurrentAccount("Demo Two", 1000, 1000));
        }
        StringBuilder b = new StringBuilder("Accounts sorted by balance:\n");
        for (Account a : store.sortedByBalance()) b.append(a).append('\n');
        b.append("Repository lookup demo:\n");
        Repository<Account> repo = new Repository<>();
        for (Account a : store.getAll()) repo.save(a);
        for (Account a : repo.findAll()) b.append(a.getAccountNumber()).append(" -> ").append(a.getOwnerName()).append('\n');
        Double[] nums = {10.0, 20.0, 30.0};
        b.append("Generic average demo: ").append(Repository.average(nums));
        return b.toString();
    }
    private static long readLong(Scanner sc) {
        while (!sc.hasNextLong()) { out.print("Enter a whole-number amount: "); sc.next(); }
        long n = sc.nextLong(); sc.nextLine(); return n;
    }
}
