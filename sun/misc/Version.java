package sun.misc;

import java.io.PrintStream;

public class Version {
  private static final String launcher_name = "java";
  
  private static final String java_version = "1.8.0_191";
  
  private static final String java_runtime_name = "Java(TM) SE Runtime Environment";
  
  private static final String java_profile_name = "";
  
  private static final String java_runtime_version = "1.8.0_191-b12";
  
  private static boolean versionsInitialized;
  
  private static int jvm_major_version;
  
  private static int jvm_minor_version;
  
  private static int jvm_micro_version;
  
  private static int jvm_update_version;
  
  private static int jvm_build_number;
  
  private static String jvm_special_version;
  
  private static int jdk_major_version;
  
  private static int jdk_minor_version;
  
  private static int jdk_micro_version;
  
  private static int jdk_update_version;
  
  private static int jdk_build_number;
  
  private static String jdk_special_version;
  
  private static boolean jvmVersionInfoAvailable;
  
  public static void init() {
    System.setProperty("java.version", "1.8.0_191");
    System.setProperty("java.runtime.version", "1.8.0_191-b12");
    System.setProperty("java.runtime.name", "Java(TM) SE Runtime Environment");
  }
  
  public static void print() { print(System.err); }
  
  public static void println() {
    print(System.err);
    System.err.println();
  }
  
  public static void print(PrintStream paramPrintStream) {
    boolean bool = false;
    String str1 = System.getProperty("java.awt.headless");
    if (str1 != null && str1.equalsIgnoreCase("true"))
      bool = true; 
    paramPrintStream.println("java version \"1.8.0_191\"");
    paramPrintStream.print("Java(TM) SE Runtime Environment (build 1.8.0_191-b12");
    if ("".length() > 0)
      paramPrintStream.print(", profile "); 
    if ("Java(TM) SE Runtime Environment".indexOf("Embedded") != -1 && bool)
      paramPrintStream.print(", headless"); 
    paramPrintStream.println(')');
    String str2 = System.getProperty("java.vm.name");
    String str3 = System.getProperty("java.vm.version");
    String str4 = System.getProperty("java.vm.info");
    paramPrintStream.println(str2 + " (build " + str3 + ", " + str4 + ")");
  }
  
  public static int jvmMajorVersion() {
    if (!versionsInitialized)
      initVersions(); 
    return jvm_major_version;
  }
  
  public static int jvmMinorVersion() {
    if (!versionsInitialized)
      initVersions(); 
    return jvm_minor_version;
  }
  
  public static int jvmMicroVersion() {
    if (!versionsInitialized)
      initVersions(); 
    return jvm_micro_version;
  }
  
  public static int jvmUpdateVersion() {
    if (!versionsInitialized)
      initVersions(); 
    return jvm_update_version;
  }
  
  public static String jvmSpecialVersion() {
    if (!versionsInitialized)
      initVersions(); 
    if (jvm_special_version == null)
      jvm_special_version = getJvmSpecialVersion(); 
    return jvm_special_version;
  }
  
  public static native String getJvmSpecialVersion();
  
  public static int jvmBuildNumber() {
    if (!versionsInitialized)
      initVersions(); 
    return jvm_build_number;
  }
  
  public static int jdkMajorVersion() {
    if (!versionsInitialized)
      initVersions(); 
    return jdk_major_version;
  }
  
  public static int jdkMinorVersion() {
    if (!versionsInitialized)
      initVersions(); 
    return jdk_minor_version;
  }
  
  public static int jdkMicroVersion() {
    if (!versionsInitialized)
      initVersions(); 
    return jdk_micro_version;
  }
  
  public static int jdkUpdateVersion() {
    if (!versionsInitialized)
      initVersions(); 
    return jdk_update_version;
  }
  
  public static String jdkSpecialVersion() {
    if (!versionsInitialized)
      initVersions(); 
    if (jdk_special_version == null)
      jdk_special_version = getJdkSpecialVersion(); 
    return jdk_special_version;
  }
  
  public static native String getJdkSpecialVersion();
  
  public static int jdkBuildNumber() {
    if (!versionsInitialized)
      initVersions(); 
    return jdk_build_number;
  }
  
  private static void initVersions() {
    if (versionsInitialized)
      return; 
    jvmVersionInfoAvailable = getJvmVersionInfo();
    if (!jvmVersionInfoAvailable) {
      String str = System.getProperty("java.vm.version");
      if (str.length() >= 5 && Character.isDigit(str.charAt(0)) && str.charAt(1) == '.' && Character.isDigit(str.charAt(2)) && str.charAt(3) == '.' && Character.isDigit(str.charAt(4))) {
        jvm_major_version = Character.digit(str.charAt(0), 10);
        jvm_minor_version = Character.digit(str.charAt(2), 10);
        jvm_micro_version = Character.digit(str.charAt(4), 10);
        CharSequence charSequence = str.subSequence(5, str.length());
        if (charSequence.charAt(0) == '_' && charSequence.length() >= 3) {
          byte b = 0;
          if (Character.isDigit(charSequence.charAt(1)) && Character.isDigit(charSequence.charAt(2)) && Character.isDigit(charSequence.charAt(3))) {
            b = 4;
          } else if (Character.isDigit(charSequence.charAt(1)) && Character.isDigit(charSequence.charAt(2))) {
            b = 3;
          } 
          try {
            String str1 = charSequence.subSequence(1, b).toString();
            jvm_update_version = Integer.valueOf(str1).intValue();
            if (charSequence.length() >= b + 1) {
              char c = charSequence.charAt(b);
              if (c >= 'a' && c <= 'z') {
                jvm_special_version = Character.toString(c);
                b++;
              } 
            } 
          } catch (NumberFormatException numberFormatException) {
            return;
          } 
          charSequence = charSequence.subSequence(b, charSequence.length());
        } 
        if (charSequence.charAt(0) == '-') {
          charSequence = charSequence.subSequence(1, charSequence.length());
          String[] arrayOfString = charSequence.toString().split("-");
          for (String str1 : arrayOfString) {
            if (str1.charAt(0) == 'b' && str1.length() == 3 && Character.isDigit(str1.charAt(1)) && Character.isDigit(str1.charAt(2))) {
              jvm_build_number = Integer.valueOf(str1.substring(1, 3)).intValue();
              break;
            } 
          } 
        } 
      } 
    } 
    getJdkVersionInfo();
    versionsInitialized = true;
  }
  
  private static native boolean getJvmVersionInfo();
  
  private static native void getJdkVersionInfo();
  
  static  {
    init();
    versionsInitialized = false;
    jvm_major_version = 0;
    jvm_minor_version = 0;
    jvm_micro_version = 0;
    jvm_update_version = 0;
    jvm_build_number = 0;
    jvm_special_version = null;
    jdk_major_version = 0;
    jdk_minor_version = 0;
    jdk_micro_version = 0;
    jdk_update_version = 0;
    jdk_build_number = 0;
    jdk_special_version = null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\Version.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */