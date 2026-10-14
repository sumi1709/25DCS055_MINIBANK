# MiniBank — Practicals 1–14

Beginner-friendly Java 17 console banking project. It includes the main requirements from the practical handout: records/enums and menu, encapsulated customer/accounts, equality/cloning, regex validation and command parsing, account inheritance/polymorphism, interfaces/lambda, custom annotations/reflection, checked banking exceptions, synchronized transactions/threads, executor and producer-consumer utilities, serialization and NIO reports, collections, a generic repository, and a final service/menu with JUnit tests.

## Requirements
- JDK 17 or later
- Maven 3.8+ (to build the JAR and run JUnit tests)

## Build and run
From this folder (the folder containing `pom.xml`):

```bash
mvn clean test package
java -jar target/MiniBank-1.0.jar
```

If you don't have Maven installed, install Maven or open this project in IntelliJ IDEA / VS Code with the Java and Maven extensions. Java alone can compile the source, but Maven is needed for the included JUnit 5 tests.

## Data files
- `data/accounts.dat` — saved accounts, created on exit
- `logs/transactions.log` — append-only transaction log
- `reports/end-of-day.txt` — generated report

These files are created automatically as you use the program.

## Menu
1. Open Savings Account
2. Deposit
3. Withdraw
4. Transfer
5. View All Accounts
6. Generate End-of-Day Report
7. Concurrency Demo
8. Collections / Repository Demo
9. Save and Exit

The startup account-type examples are demonstrations only and are not stored in the service. Their generated IDs mean your first menu-created account may be `AC0005` rather than `AC0001`; always use the ID printed when the account is created. New accounts use a zero minimum balance to keep the demo easy to test. The model still supports a configurable minimum balance in `SavingsAccount`.

## Practical mapping
- P1–P3: `MiniBank`, `BankInfo`, menu enum, `Customer`, `Account`, nested `Address`, cloning, equality.
- P4–P5: `Validator`, `Command`, `CommandParser`, `StatementFormatter`, account subclasses.
- P6–P7: interfaces, lambda and anonymous class, packages, runnable JAR, annotations and reflection.
- P8: `exception` package and checked banking exceptions, transfer `try/catch/finally`.
- P9–P10: `AccountWorker`, synchronized account operations, `TransactionProcessor`, `TaskBuffer`.
- P11: `StatePersister`, `TransactionLog`, `ReportGenerator` using NIO.
- P12–P13: `AccountStore`, `Repository<T>`, `@Id` reflection, `@SafeVarargs`, bounded generic average.
- P14: `BankService`, complete menu, persistence, report, JUnit tests, runnable JAR.

`BankSession` demonstrates try-with-resources and `TaskBuffer` is used for the wait/notify producer-consumer demo; `TransactionProcessor` demonstrates a four-thread executor and a `ConcurrentHashMap`. Account transfers lock accounts in ascending account-number order to avoid deadlock.
