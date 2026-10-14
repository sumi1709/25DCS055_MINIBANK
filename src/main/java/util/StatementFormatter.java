package util;
import model.Account;
public class StatementFormatter {
    public static String buildStatement(Account a){StringBuilder b=new StringBuilder();b.append("\n===== ACCOUNT STATEMENT =====\n");b.append("Account Number: ").append(a.getAccountNumber()).append('\n');b.append("Owner: ").append(a.getOwnerName()).append('\n');b.append("Balance: Rs. ").append(a.getBalance()).append('\n');b.append("Interest Rate: ").append(a.interestRate()).append("%\n");b.append("Estimated Yearly Interest: Rs. ").append(a.yearlyInterest()).append('\n');b.append("=============================\n");return b.toString();}
}
