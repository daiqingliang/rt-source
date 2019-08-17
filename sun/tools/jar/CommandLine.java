package sun.tools.jar;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.List;

public class CommandLine {
  public static String[] parse(String[] paramArrayOfString) throws IOException {
    ArrayList arrayList = new ArrayList(paramArrayOfString.length);
    for (byte b = 0; b < paramArrayOfString.length; b++) {
      String str = paramArrayOfString[b];
      if (str.length() > 1 && str.charAt(0) == '@') {
        str = str.substring(1);
        if (str.charAt(0) == '@') {
          arrayList.add(str);
        } else {
          loadCmdFile(str, arrayList);
        } 
      } else {
        arrayList.add(str);
      } 
    } 
    return (String[])arrayList.toArray(new String[arrayList.size()]);
  }
  
  private static void loadCmdFile(String paramString, List<String> paramList) throws IOException {
    BufferedReader bufferedReader = new BufferedReader(new FileReader(paramString));
    StreamTokenizer streamTokenizer = new StreamTokenizer(bufferedReader);
    streamTokenizer.resetSyntax();
    streamTokenizer.wordChars(32, 255);
    streamTokenizer.whitespaceChars(0, 32);
    streamTokenizer.commentChar(35);
    streamTokenizer.quoteChar(34);
    streamTokenizer.quoteChar(39);
    while (streamTokenizer.nextToken() != -1)
      paramList.add(streamTokenizer.sval); 
    bufferedReader.close();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\tools\jar\CommandLine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */