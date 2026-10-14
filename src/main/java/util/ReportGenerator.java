package util;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
public class ReportGenerator {
    public static String generate(Path logsFolder,Path reportFile) throws IOException {
        long deposits=0,withdrawals=0;List<String> details=new ArrayList<>();
        if(Files.isDirectory(logsFolder)) try(DirectoryStream<Path> stream=Files.newDirectoryStream(logsFolder,"*.log")) {
            for(Path file:stream){try(BufferedReader reader=Files.newBufferedReader(file)){String line;while((line=reader.readLine())!=null){String[] p=line.split("\\|");try{if(p[0].equals("DEPOSIT")&&p.length>=3)deposits+=Long.parseLong(p[2]);else if(p[0].equals("WITHDRAW")&&p.length>=3)withdrawals+=Long.parseLong(p[2]);else if(p[0].equals("TRANSFER")&&p.length>=4){long amount=Long.parseLong(p[3]);withdrawals+=amount;deposits+=amount;}}catch(NumberFormatException ignored){}}}BasicFileAttributes a=Files.readAttributes(file,BasicFileAttributes.class);details.add(file.getFileName()+" | size="+a.size()+" bytes | modified="+a.lastModifiedTime());}
        }
        String report="MINIBANK END-OF-DAY REPORT\nTotal deposits: Rs. "+deposits+"\nTotal withdrawals: Rs. "+withdrawals+"\nNet movement (deposit minus withdrawal): Rs. "+(deposits-withdrawals)+"\nLog files:\n"+(details.isEmpty()?"No log files found.":String.join("\n",details))+"\n";
        Path parent=reportFile.getParent();if(parent!=null)Files.createDirectories(parent);Files.writeString(reportFile,report,StandardOpenOption.CREATE,StandardOpenOption.TRUNCATE_EXISTING);return report;
    }
}
