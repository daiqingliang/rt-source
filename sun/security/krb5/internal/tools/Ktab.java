package sun.security.krb5.internal.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import sun.security.krb5.KrbException;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.internal.crypto.EType;
import sun.security.krb5.internal.ktab.KeyTab;
import sun.security.krb5.internal.ktab.KeyTabEntry;

public class Ktab {
  KeyTab table;
  
  char action;
  
  String name;
  
  String principal;
  
  boolean showEType;
  
  boolean showTime;
  
  int etype = -1;
  
  char[] password = null;
  
  boolean forced = false;
  
  boolean append = false;
  
  int vDel = -1;
  
  int vAdd = -1;
  
  public static void main(String[] paramArrayOfString) {
    Ktab ktab = new Ktab();
    if (paramArrayOfString.length == 1 && paramArrayOfString[0].equalsIgnoreCase("-help")) {
      ktab.printHelp();
      return;
    } 
    if (paramArrayOfString == null || paramArrayOfString.length == 0) {
      ktab.action = 'l';
    } else {
      ktab.processArgs(paramArrayOfString);
    } 
    ktab.table = KeyTab.getInstance(ktab.name);
    if (ktab.table.isMissing() && ktab.action != 'a') {
      if (ktab.name == null) {
        System.out.println("No default key table exists.");
      } else {
        System.out.println("Key table " + ktab.name + " does not exist.");
      } 
      System.exit(-1);
    } 
    if (!ktab.table.isValid()) {
      if (ktab.name == null) {
        System.out.println("The format of the default key table  is incorrect.");
      } else {
        System.out.println("The format of key table " + ktab.name + " is incorrect.");
      } 
      System.exit(-1);
    } 
    switch (ktab.action) {
      case 'l':
        ktab.listKt();
        return;
      case 'a':
        ktab.addEntry();
        return;
      case 'd':
        ktab.deleteEntry();
        return;
    } 
    ktab.error(new String[] { "A command must be provided" });
  }
  
  void processArgs(String[] paramArrayOfString) {
    boolean bool = false;
    for (byte b = 0; b < paramArrayOfString.length; b++) {
      if (paramArrayOfString[b].startsWith("-")) {
        switch (paramArrayOfString[b].toLowerCase(Locale.US)) {
          case "-l":
            this.action = 'l';
            break;
          case "-a":
            this.action = 'a';
            if (++b >= paramArrayOfString.length || paramArrayOfString[b].startsWith("-"))
              error(new String[] { "A principal name must be specified after -a" }); 
            this.principal = paramArrayOfString[b];
            break;
          case "-d":
            this.action = 'd';
            if (++b >= paramArrayOfString.length || paramArrayOfString[b].startsWith("-"))
              error(new String[] { "A principal name must be specified after -d" }); 
            this.principal = paramArrayOfString[b];
            break;
          case "-e":
            if (this.action == 'l') {
              this.showEType = true;
              break;
            } 
            if (this.action == 'd') {
              if (++b >= paramArrayOfString.length || paramArrayOfString[b].startsWith("-"))
                error(new String[] { "An etype must be specified after -e" }); 
              try {
                this.etype = Integer.parseInt(paramArrayOfString[b]);
                if (this.etype <= 0)
                  throw new NumberFormatException(); 
              } catch (NumberFormatException numberFormatException) {
                error(new String[] { paramArrayOfString[b] + " is not a valid etype" });
              } 
              break;
            } 
            error(new String[] { paramArrayOfString[b] + " is not valid after -" + this.action });
            break;
          case "-n":
            if (++b >= paramArrayOfString.length || paramArrayOfString[b].startsWith("-"))
              error(new String[] { "A KVNO must be specified after -n" }); 
            try {
              this.vAdd = Integer.parseInt(paramArrayOfString[b]);
              if (this.vAdd < 0)
                throw new NumberFormatException(); 
            } catch (NumberFormatException numberFormatException) {
              error(new String[] { paramArrayOfString[b] + " is not a valid KVNO" });
            } 
            break;
          case "-k":
            if (++b >= paramArrayOfString.length || paramArrayOfString[b].startsWith("-"))
              error(new String[] { "A keytab name must be specified after -k" }); 
            if (paramArrayOfString[b].length() >= 5 && paramArrayOfString[b].substring(0, 5).equalsIgnoreCase("FILE:")) {
              this.name = paramArrayOfString[b].substring(5);
              break;
            } 
            this.name = paramArrayOfString[b];
            break;
          case "-t":
            this.showTime = true;
            break;
          case "-f":
            this.forced = true;
            break;
          case "-append":
            this.append = true;
            break;
          default:
            error(new String[] { "Unknown command: " + paramArrayOfString[b] });
            break;
        } 
      } else {
        if (bool)
          error(new String[] { "Useless extra argument " + paramArrayOfString[b] }); 
        if (this.action == 'a') {
          this.password = paramArrayOfString[b].toCharArray();
        } else if (this.action == 'd') {
          switch (paramArrayOfString[b]) {
            case "all":
              this.vDel = -1;
              break;
            case "old":
              this.vDel = -2;
              break;
            default:
              try {
                this.vDel = Integer.parseInt(paramArrayOfString[b]);
                if (this.vDel < 0)
                  throw new NumberFormatException(); 
              } catch (NumberFormatException numberFormatException) {
                error(new String[] { paramArrayOfString[b] + " is not a valid KVNO" });
              } 
              break;
          } 
        } else {
          error(new String[] { "Useless extra argument " + paramArrayOfString[b] });
        } 
        bool = true;
      } 
    } 
  }
  
  void addEntry() {
    PrincipalName principalName = null;
    try {
      principalName = new PrincipalName(this.principal);
    } catch (KrbException krbException) {
      System.err.println("Failed to add " + this.principal + " to keytab.");
      krbException.printStackTrace();
      System.exit(-1);
    } 
    if (this.password == null)
      try {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Password for " + principalName.toString() + ":");
        System.out.flush();
        this.password = bufferedReader.readLine().toCharArray();
      } catch (IOException iOException) {
        System.err.println("Failed to read the password.");
        iOException.printStackTrace();
        System.exit(-1);
      }  
    try {
      this.table.addEntry(principalName, this.password, this.vAdd, this.append);
      Arrays.fill(this.password, '0');
      this.table.save();
      System.out.println("Done!");
      System.out.println("Service key for " + this.principal + " is saved in " + this.table.tabName());
    } catch (KrbException krbException) {
      System.err.println("Failed to add " + this.principal + " to keytab.");
      krbException.printStackTrace();
      System.exit(-1);
    } catch (IOException iOException) {
      System.err.println("Failed to save new entry.");
      iOException.printStackTrace();
      System.exit(-1);
    } 
  }
  
  void listKt() {
    System.out.println("Keytab name: " + this.table.tabName());
    KeyTabEntry[] arrayOfKeyTabEntry = this.table.getEntries();
    if (arrayOfKeyTabEntry != null && arrayOfKeyTabEntry.length > 0) {
      String[][] arrayOfString = new String[arrayOfKeyTabEntry.length + 1][this.showTime ? 3 : 2];
      byte b1 = 0;
      arrayOfString[0][b1++] = "KVNO";
      if (this.showTime)
        arrayOfString[0][b1++] = "Timestamp"; 
      arrayOfString[0][b1++] = "Principal";
      for (byte b2 = 0; b2 < arrayOfKeyTabEntry.length; b2++) {
        b1 = 0;
        arrayOfString[b2 + true][b1++] = arrayOfKeyTabEntry[b2].getKey().getKeyVersionNumber().toString();
        if (this.showTime)
          arrayOfString[b2 + true][b1++] = DateFormat.getDateTimeInstance(3, 3).format(new Date(arrayOfKeyTabEntry[b2].getTimeStamp().getTime())); 
        String str = arrayOfKeyTabEntry[b2].getService().toString();
        if (this.showEType) {
          int i = arrayOfKeyTabEntry[b2].getKey().getEType();
          arrayOfString[b2 + true][b1++] = str + " (" + i + ":" + EType.toString(i) + ")";
        } else {
          arrayOfString[b2 + true][b1++] = str;
        } 
      } 
      int[] arrayOfInt = new int[b1];
      byte b3;
      for (b3 = 0; b3 < b1; b3++) {
        for (byte b = 0; b <= arrayOfKeyTabEntry.length; b++) {
          if (arrayOfString[b][b3].length() > arrayOfInt[b3])
            arrayOfInt[b3] = arrayOfString[b][b3].length(); 
        } 
        if (b3)
          arrayOfInt[b3] = -arrayOfInt[b3]; 
      } 
      for (b3 = 0; b3 < b1; b3++) {
        System.out.printf("%" + arrayOfInt[b3] + "s ", new Object[] { arrayOfString[0][b3] });
      } 
      System.out.println();
      for (b3 = 0; b3 < b1; b3++) {
        for (byte b = 0; b < Math.abs(arrayOfInt[b3]); b++)
          System.out.print("-"); 
        System.out.print(" ");
      } 
      System.out.println();
      for (b3 = 0; b3 < arrayOfKeyTabEntry.length; b3++) {
        for (byte b = 0; b < b1; b++) {
          System.out.printf("%" + arrayOfInt[b] + "s ", new Object[] { arrayOfString[b3 + 1][b] });
        } 
        System.out.println();
      } 
    } else {
      System.out.println("0 entry.");
    } 
  }
  
  void deleteEntry() {
    PrincipalName principalName = null;
    try {
      principalName = new PrincipalName(this.principal);
      if (!this.forced) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Are you sure you want to delete service key(s) for " + principalName.toString() + " (" + ((this.etype == -1) ? "all etypes" : ("etype=" + this.etype)) + ", " + ((this.vDel == -1) ? "all kvno" : ((this.vDel == -2) ? "old kvno" : ("kvno=" + this.vDel))) + ") in " + this.table.tabName() + "? (Y/[N]): ");
        System.out.flush();
        String str = bufferedReader.readLine();
        if (!str.equalsIgnoreCase("Y") && !str.equalsIgnoreCase("Yes"))
          System.exit(0); 
      } 
    } catch (KrbException krbException) {
      System.err.println("Error occurred while deleting the entry. Deletion failed.");
      krbException.printStackTrace();
      System.exit(-1);
    } catch (IOException iOException) {
      System.err.println("Error occurred while deleting the entry.  Deletion failed.");
      iOException.printStackTrace();
      System.exit(-1);
    } 
    int i = this.table.deleteEntries(principalName, this.etype, this.vDel);
    if (i == 0) {
      System.err.println("No matched entry in the keytab. Deletion fails.");
      System.exit(-1);
    } else {
      try {
        this.table.save();
      } catch (IOException iOException) {
        System.err.println("Error occurs while saving the keytab. Deletion fails.");
        iOException.printStackTrace();
        System.exit(-1);
      } 
      System.out.println("Done! " + i + " entries removed.");
    } 
  }
  
  void error(String... paramVarArgs) {
    for (String str : paramVarArgs)
      System.out.println("Error: " + str + "."); 
    printHelp();
    System.exit(-1);
  }
  
  void printHelp() {
    System.out.println("\nUsage: ktab <commands> <options>");
    System.out.println();
    System.out.println("Available commands:");
    System.out.println();
    System.out.println("-l [-e] [-t]\n    list the keytab name and entries. -e with etype, -t with timestamp.");
    System.out.println("-a <principal name> [<password>] [-n <kvno>] [-append]\n    add new key entries to the keytab for the given principal name with\n    optional <password>. If a <kvno> is specified, new keys' Key Version\n    Numbers equal to the value, otherwise, automatically incrementing\n    the Key Version Numbers. If -append is specified, new keys are\n    appended to the keytab, otherwise, old keys for the\n    same principal are removed.");
    System.out.println("-d <principal name> [-f] [-e <etype>] [<kvno> | all | old]\n    delete key entries from the keytab for the specified principal. If\n    <kvno> is specified, delete keys whose Key Version Numbers match\n    kvno. If \"all\" is specified, delete all keys. If \"old\" is specified,\n    delete all keys except those with the highest kvno. Default action\n    is \"all\". If <etype> is specified, only keys of this encryption type\n    are deleted. <etype> should be specified as the numberic value etype\n    defined in RFC 3961, section 8. A prompt to confirm the deletion is\n    displayed unless -f is specified.");
    System.out.println();
    System.out.println("Common option(s):");
    System.out.println();
    System.out.println("-k <keytab name>\n    specify keytab name and path with prefix FILE:");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\tools\Ktab.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */