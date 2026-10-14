package util;
public class CommandParser {
    public static Command parse(String line){String[] p=line.trim().split("\\s+");if(p.length!=3)throw new IllegalArgumentException("Use: TYPE ACCOUNT_NUMBER AMOUNT");TransactionType t;try{t=TransactionType.valueOf(p[0].toUpperCase());}catch(IllegalArgumentException e){throw new IllegalArgumentException("Invalid transaction type.");}long amount;try{amount=Long.parseLong(p[2]);}catch(NumberFormatException e){throw new IllegalArgumentException("Amount must be a whole number.");}if(amount<=0)throw new IllegalArgumentException("Amount must be positive.");return new Command(t,p[1],amount);}
}
