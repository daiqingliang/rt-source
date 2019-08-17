package sun.security.krb5.internal.tools;

import java.net.InetAddress;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.RealmException;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.ccache.Credentials;
import sun.security.krb5.internal.ccache.CredentialsCache;
import sun.security.krb5.internal.crypto.EType;
import sun.security.krb5.internal.ktab.KeyTab;
import sun.security.krb5.internal.ktab.KeyTabEntry;

public class Klist {
  Object target;
  
  char[] options = new char[4];
  
  String name;
  
  char action;
  
  private static boolean DEBUG = Krb5.DEBUG;
  
  public static void main(String[] paramArrayOfString) {
    KeyTab keyTab;
    Klist klist = new Klist();
    if (paramArrayOfString == null || paramArrayOfString.length == 0) {
      klist.action = 'c';
    } else {
      klist.processArgs(paramArrayOfString);
    } 
    switch (klist.action) {
      case 'c':
        if (klist.name == null) {
          klist.target = CredentialsCache.getInstance();
          klist.name = CredentialsCache.cacheName();
        } else {
          klist.target = CredentialsCache.getInstance(klist.name);
        } 
        if (klist.target != null) {
          klist.displayCache();
        } else {
          klist.displayMessage("Credentials cache");
          System.exit(-1);
        } 
        return;
      case 'k':
        keyTab = KeyTab.getInstance(klist.name);
        if (keyTab.isMissing()) {
          System.out.println("KeyTab " + klist.name + " not found.");
          System.exit(-1);
        } else if (!keyTab.isValid()) {
          System.out.println("KeyTab " + klist.name + " format not supported.");
          System.exit(-1);
        } 
        klist.target = keyTab;
        klist.name = keyTab.tabName();
        klist.displayTab();
        return;
    } 
    if (klist.name != null) {
      klist.printHelp();
      System.exit(-1);
    } else {
      klist.target = CredentialsCache.getInstance();
      klist.name = CredentialsCache.cacheName();
      if (klist.target != null) {
        klist.displayCache();
      } else {
        klist.displayMessage("Credentials cache");
        System.exit(-1);
      } 
    } 
  }
  
  void processArgs(String[] paramArrayOfString) {
    for (byte b = 0; b < paramArrayOfString.length; b++) {
      if (paramArrayOfString[b].length() >= 2 && paramArrayOfString[b].startsWith("-")) {
        Character character = new Character(paramArrayOfString[b].charAt(1));
        switch (character.charValue()) {
          case 'c':
            this.action = 'c';
            break;
          case 'k':
            this.action = 'k';
            break;
          case 'a':
            this.options[2] = 'a';
            break;
          case 'n':
            this.options[3] = 'n';
            break;
          case 'f':
            this.options[1] = 'f';
            break;
          case 'e':
            this.options[0] = 'e';
            break;
          case 'K':
            this.options[1] = 'K';
            break;
          case 't':
            this.options[2] = 't';
            break;
          default:
            printHelp();
            System.exit(-1);
            break;
        } 
      } else if (!paramArrayOfString[b].startsWith("-") && b == paramArrayOfString.length - 1) {
        this.name = paramArrayOfString[b];
        Object object = null;
      } else {
        printHelp();
        System.exit(-1);
      } 
    } 
  }
  
  void displayTab() {
    KeyTab keyTab = (KeyTab)this.target;
    KeyTabEntry[] arrayOfKeyTabEntry = keyTab.getEntries();
    if (arrayOfKeyTabEntry.length == 0) {
      System.out.println("\nKey tab: " + this.name + ",  0 entries found.\n");
    } else {
      if (arrayOfKeyTabEntry.length == 1) {
        System.out.println("\nKey tab: " + this.name + ", " + arrayOfKeyTabEntry.length + " entry found.\n");
      } else {
        System.out.println("\nKey tab: " + this.name + ", " + arrayOfKeyTabEntry.length + " entries found.\n");
      } 
      for (byte b = 0; b < arrayOfKeyTabEntry.length; b++) {
        System.out.println("[" + (b + true) + "] Service principal: " + arrayOfKeyTabEntry[b].getService().toString());
        System.out.println("\t KVNO: " + arrayOfKeyTabEntry[b].getKey().getKeyVersionNumber());
        if (this.options[0] == 'e') {
          EncryptionKey encryptionKey = arrayOfKeyTabEntry[b].getKey();
          System.out.println("\t Key type: " + encryptionKey.getEType());
        } 
        if (this.options[1] == 'K') {
          EncryptionKey encryptionKey = arrayOfKeyTabEntry[b].getKey();
          System.out.println("\t Key: " + arrayOfKeyTabEntry[b].getKeyString());
        } 
        if (this.options[2] == 't')
          System.out.println("\t Time stamp: " + format(arrayOfKeyTabEntry[b].getTimeStamp())); 
      } 
    } 
  }
  
  void displayCache() {
    CredentialsCache credentialsCache = (CredentialsCache)this.target;
    Credentials[] arrayOfCredentials = credentialsCache.getCredsList();
    if (arrayOfCredentials == null) {
      System.out.println("No credentials available in the cache " + this.name);
      System.exit(-1);
    } 
    System.out.println("\nCredentials cache: " + this.name);
    String str = credentialsCache.getPrimaryPrincipal().toString();
    int i = arrayOfCredentials.length;
    if (i == 1) {
      System.out.println("\nDefault principal: " + str + ", " + arrayOfCredentials.length + " entry found.\n");
    } else {
      System.out.println("\nDefault principal: " + str + ", " + arrayOfCredentials.length + " entries found.\n");
    } 
    if (arrayOfCredentials != null) {
      for (byte b = 0; b < arrayOfCredentials.length; b++) {
        try {
          String str1;
          if (arrayOfCredentials[b].getStartTime() != null) {
            str1 = format(arrayOfCredentials[b].getStartTime());
          } else {
            str1 = format(arrayOfCredentials[b].getAuthTime());
          } 
          String str2 = format(arrayOfCredentials[b].getEndTime());
          String str3 = arrayOfCredentials[b].getServicePrincipal().toString();
          System.out.println("[" + (b + true) + "]  Service Principal:  " + str3);
          System.out.println("     Valid starting:     " + str1);
          System.out.println("     Expires:            " + str2);
          if (arrayOfCredentials[b].getRenewTill() != null) {
            String str4 = format(arrayOfCredentials[b].getRenewTill());
            System.out.println("     Renew until:        " + str4);
          } 
          if (this.options[0] == 'e') {
            String str4 = EType.toString(arrayOfCredentials[b].getEType());
            String str5 = EType.toString(arrayOfCredentials[b].getTktEType());
            System.out.println("     EType (skey, tkt):  " + str4 + ", " + str5);
          } 
          if (this.options[1] == 'f')
            System.out.println("     Flags:              " + arrayOfCredentials[b].getTicketFlags().toString()); 
          if (this.options[2] == 'a') {
            boolean bool = true;
            InetAddress[] arrayOfInetAddress = arrayOfCredentials[b].setKrbCreds().getClientAddresses();
            if (arrayOfInetAddress != null) {
              for (InetAddress inetAddress : arrayOfInetAddress) {
                String str4;
                if (this.options[3] == 'n') {
                  str4 = inetAddress.getHostAddress();
                } else {
                  str4 = inetAddress.getCanonicalHostName();
                } 
                System.out.println("     " + (bool ? "Addresses:" : "          ") + "       " + str4);
                bool = false;
              } 
            } else {
              System.out.println("     [No host addresses info]");
            } 
          } 
        } catch (RealmException realmException) {
          System.out.println("Error reading principal from the entry.");
          if (DEBUG)
            realmException.printStackTrace(); 
          System.exit(-1);
        } 
      } 
    } else {
      System.out.println("\nNo entries found.");
    } 
  }
  
  void displayMessage(String paramString) {
    if (this.name == null) {
      System.out.println("Default " + paramString + " not found.");
    } else {
      System.out.println(paramString + " " + this.name + " not found.");
    } 
  }
  
  private String format(KerberosTime paramKerberosTime) {
    String str = paramKerberosTime.toDate().toString();
    return str.substring(4, 7) + " " + str.substring(8, 10) + ", " + str.substring(24) + " " + str.substring(11, 19);
  }
  
  void printHelp() {
    System.out.println("\nUsage: klist [[-c] [-f] [-e] [-a [-n]]] [-k [-t] [-K]] [name]");
    System.out.println("   name\t name of credentials cache or  keytab with the prefix. File-based cache or keytab's prefix is FILE:.");
    System.out.println("   -c specifies that credential cache is to be listed");
    System.out.println("   -k specifies that key tab is to be listed");
    System.out.println("   options for credentials caches:");
    System.out.println("\t-f \t shows credentials flags");
    System.out.println("\t-e \t shows the encryption type");
    System.out.println("\t-a \t shows addresses");
    System.out.println("\t  -n \t   do not reverse-resolve addresses");
    System.out.println("   options for keytabs:");
    System.out.println("\t-t \t shows keytab entry timestamps");
    System.out.println("\t-K \t shows keytab entry key value");
    System.out.println("\t-e \t shows keytab entry key type");
    System.out.println("\nUsage: java sun.security.krb5.tools.Klist -help for help.");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\tools\Klist.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */