package sun.security.util;

import java.math.BigInteger;
import java.security.AccessController;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sun.security.action.GetPropertyAction;

public class Debug {
  private String prefix;
  
  private static String args = (String)AccessController.doPrivileged(new GetPropertyAction("java.security.debug"));
  
  private static final char[] hexDigits;
  
  public static void Help() {
    System.err.println();
    System.err.println("all           turn on all debugging");
    System.err.println("access        print all checkPermission results");
    System.err.println("certpath      PKIX CertPathBuilder and");
    System.err.println("              CertPathValidator debugging");
    System.err.println("combiner      SubjectDomainCombiner debugging");
    System.err.println("gssloginconfig");
    System.err.println("              GSS LoginConfigImpl debugging");
    System.err.println("configfile    JAAS ConfigFile loading");
    System.err.println("configparser  JAAS ConfigFile parsing");
    System.err.println("jar           jar verification");
    System.err.println("logincontext  login context results");
    System.err.println("jca           JCA engine class debugging");
    System.err.println("policy        loading and granting");
    System.err.println("provider      security provider debugging");
    System.err.println("pkcs11        PKCS11 session manager debugging");
    System.err.println("pkcs11keystore");
    System.err.println("              PKCS11 KeyStore debugging");
    System.err.println("sunpkcs11     SunPKCS11 provider debugging");
    System.err.println("scl           permissions SecureClassLoader assigns");
    System.err.println("ts            timestamping");
    System.err.println();
    System.err.println("The following can be used with access:");
    System.err.println();
    System.err.println("stack         include stack trace");
    System.err.println("domain        dump all domains in context");
    System.err.println("failure       before throwing exception, dump stack");
    System.err.println("              and domain that didn't have permission");
    System.err.println();
    System.err.println("The following can be used with stack and domain:");
    System.err.println();
    System.err.println("permission=<classname>");
    System.err.println("              only dump output if specified permission");
    System.err.println("              is being checked");
    System.err.println("codebase=<URL>");
    System.err.println("              only dump output if specified codebase");
    System.err.println("              is being checked");
    System.err.println();
    System.err.println("The following can be used with provider:");
    System.err.println();
    System.err.println("engine=<engines>");
    System.err.println("              only dump output for the specified list");
    System.err.println("              of JCA engines. Supported values:");
    System.err.println("              Cipher, KeyAgreement, KeyGenerator,");
    System.err.println("              KeyPairGenerator, KeyStore, Mac,");
    System.err.println("              MessageDigest, SecureRandom, Signature.");
    System.err.println();
    System.err.println("Note: Separate multiple options with a comma");
    System.exit(0);
  }
  
  public static Debug getInstance(String paramString) { return getInstance(paramString, paramString); }
  
  public static Debug getInstance(String paramString1, String paramString2) {
    if (isOn(paramString1)) {
      Debug debug = new Debug();
      debug.prefix = paramString2;
      return debug;
    } 
    return null;
  }
  
  public static boolean isOn(String paramString) { return (args == null) ? false : ((args.indexOf("all") != -1) ? true : ((args.indexOf(paramString) != -1))); }
  
  public void println(String paramString) { System.err.println(this.prefix + ": " + paramString); }
  
  public void println() { System.err.println(this.prefix + ":"); }
  
  public static void println(String paramString1, String paramString2) { System.err.println(paramString1 + ": " + paramString2); }
  
  public static String toHexString(BigInteger paramBigInteger) {
    String str = paramBigInteger.toString(16);
    StringBuffer stringBuffer = new StringBuffer(str.length() * 2);
    if (str.startsWith("-")) {
      stringBuffer.append("   -");
      str = str.substring(1);
    } else {
      stringBuffer.append("    ");
    } 
    if (str.length() % 2 != 0)
      str = "0" + str; 
    byte b = 0;
    while (b < str.length()) {
      stringBuffer.append(str.substring(b, b + 2));
      b += 2;
      if (b != str.length()) {
        if (b % 64 == 0) {
          stringBuffer.append("\n    ");
          continue;
        } 
        if (b % 8 == 0)
          stringBuffer.append(" "); 
      } 
    } 
    return stringBuffer.toString();
  }
  
  private static String marshal(String paramString) {
    if (paramString != null) {
      StringBuffer stringBuffer1 = new StringBuffer();
      StringBuffer stringBuffer2 = new StringBuffer(paramString);
      String str1 = "[Pp][Ee][Rr][Mm][Ii][Ss][Ss][Ii][Oo][Nn]=";
      String str2 = "permission=";
      String str3 = str1 + "[a-zA-Z_$][a-zA-Z0-9_$]*([.][a-zA-Z_$][a-zA-Z0-9_$]*)*";
      Pattern pattern = Pattern.compile(str3);
      Matcher matcher = pattern.matcher(stringBuffer2);
      StringBuffer stringBuffer3 = new StringBuffer();
      while (matcher.find()) {
        String str = matcher.group();
        stringBuffer1.append(str.replaceFirst(str1, str2));
        stringBuffer1.append("  ");
        matcher.appendReplacement(stringBuffer3, "");
      } 
      matcher.appendTail(stringBuffer3);
      stringBuffer2 = stringBuffer3;
      str1 = "[Cc][Oo][Dd][Ee][Bb][Aa][Ss][Ee]=";
      str2 = "codebase=";
      str3 = str1 + "[^, ;]*";
      pattern = Pattern.compile(str3);
      matcher = pattern.matcher(stringBuffer2);
      stringBuffer3 = new StringBuffer();
      while (matcher.find()) {
        String str = matcher.group();
        stringBuffer1.append(str.replaceFirst(str1, str2));
        stringBuffer1.append("  ");
        matcher.appendReplacement(stringBuffer3, "");
      } 
      matcher.appendTail(stringBuffer3);
      stringBuffer2 = stringBuffer3;
      stringBuffer1.append(stringBuffer2.toString().toLowerCase(Locale.ENGLISH));
      return stringBuffer1.toString();
    } 
    return null;
  }
  
  public static String toString(byte[] paramArrayOfByte) {
    if (paramArrayOfByte == null)
      return "(null)"; 
    StringBuilder stringBuilder = new StringBuilder(paramArrayOfByte.length * 3);
    for (byte b = 0; b < paramArrayOfByte.length; b++) {
      byte b1 = paramArrayOfByte[b] & 0xFF;
      if (b)
        stringBuilder.append(':'); 
      stringBuilder.append(hexDigits[b1 >>> 4]);
      stringBuilder.append(hexDigits[b1 & 0xF]);
    } 
    return stringBuilder.toString();
  }
  
  static  {
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("java.security.auth.debug"));
    if (args == null) {
      args = str;
    } else if (str != null) {
      args += "," + str;
    } 
    if (args != null) {
      args = marshal(args);
      if (args.equals("help"))
        Help(); 
    } 
    hexDigits = "0123456789abcdef".toCharArray();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\securit\\util\Debug.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */