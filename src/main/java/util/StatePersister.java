package util;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import model.Account;
public class StatePersister {
    public static void save(Account[] accounts,Path file) throws IOException {Path parent=file.getParent();if(parent!=null)Files.createDirectories(parent);try(ObjectOutputStream out=new ObjectOutputStream(Files.newOutputStream(file))){out.writeObject(accounts);}}
    public static Account[] load(Path file) throws IOException,ClassNotFoundException {if(!Files.exists(file))return new Account[0];try(ObjectInputStream in=new ObjectInputStream(Files.newInputStream(file))){Account[] accounts=(Account[])in.readObject();Account.updateCounterFrom(accounts);return accounts;}}
}
