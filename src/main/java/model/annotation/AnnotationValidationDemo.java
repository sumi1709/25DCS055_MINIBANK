package model.annotation;
import model.Account;
public class AnnotationValidationDemo {
    private static class InvalidAccount extends Account {
        private static final long serialVersionUID=1L;
        InvalidAccount(){super("This customer name is definitely too long", -100, false);}
        @Override public double interestRate(){return 0;}
        @Override public boolean canWithdraw(long amount){return false;}
    }
    public static String[] run(){return AnnotationValidator.validate(new InvalidAccount());}
}
