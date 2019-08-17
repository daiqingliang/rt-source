package java.util.prefs;

import java.io.ByteArrayOutputStream;
import java.util.StringTokenizer;
import sun.util.logging.PlatformLogger;

class WindowsPreferences extends AbstractPreferences {
  private static PlatformLogger logger;
  
  private static final byte[] WINDOWS_ROOT_PATH = stringToByteArray("Software\\JavaSoft\\Prefs");
  
  private static final int HKEY_CURRENT_USER = -2147483647;
  
  private static final int HKEY_LOCAL_MACHINE = -2147483646;
  
  private static final int USER_ROOT_NATIVE_HANDLE = -2147483647;
  
  private static final int SYSTEM_ROOT_NATIVE_HANDLE = -2147483646;
  
  private static final int MAX_WINDOWS_PATH_LENGTH = 256;
  
  static final Preferences userRoot = new WindowsPreferences(-2147483647, WINDOWS_ROOT_PATH);
  
  static final Preferences systemRoot = new WindowsPreferences(-2147483646, WINDOWS_ROOT_PATH);
  
  private static final int ERROR_SUCCESS = 0;
  
  private static final int ERROR_FILE_NOT_FOUND = 2;
  
  private static final int ERROR_ACCESS_DENIED = 5;
  
  private static final int NATIVE_HANDLE = 0;
  
  private static final int ERROR_CODE = 1;
  
  private static final int SUBKEYS_NUMBER = 0;
  
  private static final int VALUES_NUMBER = 2;
  
  private static final int MAX_KEY_LENGTH = 3;
  
  private static final int MAX_VALUE_NAME_LENGTH = 4;
  
  private static final int DISPOSITION = 2;
  
  private static final int REG_CREATED_NEW_KEY = 1;
  
  private static final int REG_OPENED_EXISTING_KEY = 2;
  
  private static final int NULL_NATIVE_HANDLE = 0;
  
  private static final int DELETE = 65536;
  
  private static final int KEY_QUERY_VALUE = 1;
  
  private static final int KEY_SET_VALUE = 2;
  
  private static final int KEY_CREATE_SUB_KEY = 4;
  
  private static final int KEY_ENUMERATE_SUB_KEYS = 8;
  
  private static final int KEY_READ = 131097;
  
  private static final int KEY_WRITE = 131078;
  
  private static final int KEY_ALL_ACCESS = 983103;
  
  private static int INIT_SLEEP_TIME = 50;
  
  private static int MAX_ATTEMPTS = 5;
  
  private boolean isBackingStoreAvailable = true;
  
  private static native int[] WindowsRegOpenKey(int paramInt1, byte[] paramArrayOfByte, int paramInt2);
  
  private static int[] WindowsRegOpenKey1(int paramInt1, byte[] paramArrayOfByte, int paramInt2) {
    int[] arrayOfInt = WindowsRegOpenKey(paramInt1, paramArrayOfByte, paramInt2);
    if (arrayOfInt[1] == 0)
      return arrayOfInt; 
    if (arrayOfInt[1] == 2) {
      logger().warning("Trying to recreate Windows registry node " + byteArrayToString(paramArrayOfByte) + " at root 0x" + Integer.toHexString(paramInt1) + ".");
      int i = WindowsRegCreateKeyEx(paramInt1, paramArrayOfByte)[0];
      WindowsRegCloseKey(i);
      return WindowsRegOpenKey(paramInt1, paramArrayOfByte, paramInt2);
    } 
    if (arrayOfInt[1] != 5) {
      long l = INIT_SLEEP_TIME;
      for (byte b = 0; b < MAX_ATTEMPTS; b++) {
        try {
          Thread.sleep(l);
        } catch (InterruptedException interruptedException) {
          return arrayOfInt;
        } 
        l *= 2L;
        arrayOfInt = WindowsRegOpenKey(paramInt1, paramArrayOfByte, paramInt2);
        if (arrayOfInt[1] == 0)
          return arrayOfInt; 
      } 
    } 
    return arrayOfInt;
  }
  
  private static native int WindowsRegCloseKey(int paramInt);
  
  private static native int[] WindowsRegCreateKeyEx(int paramInt, byte[] paramArrayOfByte);
  
  private static int[] WindowsRegCreateKeyEx1(int paramInt, byte[] paramArrayOfByte) {
    int[] arrayOfInt = WindowsRegCreateKeyEx(paramInt, paramArrayOfByte);
    if (arrayOfInt[1] == 0)
      return arrayOfInt; 
    long l = INIT_SLEEP_TIME;
    for (byte b = 0; b < MAX_ATTEMPTS; b++) {
      try {
        Thread.sleep(l);
      } catch (InterruptedException interruptedException) {
        return arrayOfInt;
      } 
      l *= 2L;
      arrayOfInt = WindowsRegCreateKeyEx(paramInt, paramArrayOfByte);
      if (arrayOfInt[1] == 0)
        return arrayOfInt; 
    } 
    return arrayOfInt;
  }
  
  private static native int WindowsRegDeleteKey(int paramInt, byte[] paramArrayOfByte);
  
  private static native int WindowsRegFlushKey(int paramInt);
  
  private static int WindowsRegFlushKey1(int paramInt) {
    int i = WindowsRegFlushKey(paramInt);
    if (i == 0)
      return i; 
    long l = INIT_SLEEP_TIME;
    for (byte b = 0; b < MAX_ATTEMPTS; b++) {
      try {
        Thread.sleep(l);
      } catch (InterruptedException interruptedException) {
        return i;
      } 
      l *= 2L;
      i = WindowsRegFlushKey(paramInt);
      if (i == 0)
        return i; 
    } 
    return i;
  }
  
  private static native byte[] WindowsRegQueryValueEx(int paramInt, byte[] paramArrayOfByte);
  
  private static native int WindowsRegSetValueEx(int paramInt, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2);
  
  private static int WindowsRegSetValueEx1(int paramInt, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2) {
    int i = WindowsRegSetValueEx(paramInt, paramArrayOfByte1, paramArrayOfByte2);
    if (i == 0)
      return i; 
    long l = INIT_SLEEP_TIME;
    for (byte b = 0; b < MAX_ATTEMPTS; b++) {
      try {
        Thread.sleep(l);
      } catch (InterruptedException interruptedException) {
        return i;
      } 
      l *= 2L;
      i = WindowsRegSetValueEx(paramInt, paramArrayOfByte1, paramArrayOfByte2);
      if (i == 0)
        return i; 
    } 
    return i;
  }
  
  private static native int WindowsRegDeleteValue(int paramInt, byte[] paramArrayOfByte);
  
  private static native int[] WindowsRegQueryInfoKey(int paramInt);
  
  private static int[] WindowsRegQueryInfoKey1(int paramInt) {
    int[] arrayOfInt = WindowsRegQueryInfoKey(paramInt);
    if (arrayOfInt[1] == 0)
      return arrayOfInt; 
    long l = INIT_SLEEP_TIME;
    for (byte b = 0; b < MAX_ATTEMPTS; b++) {
      try {
        Thread.sleep(l);
      } catch (InterruptedException interruptedException) {
        return arrayOfInt;
      } 
      l *= 2L;
      arrayOfInt = WindowsRegQueryInfoKey(paramInt);
      if (arrayOfInt[1] == 0)
        return arrayOfInt; 
    } 
    return arrayOfInt;
  }
  
  private static native byte[] WindowsRegEnumKeyEx(int paramInt1, int paramInt2, int paramInt3);
  
  private static byte[] WindowsRegEnumKeyEx1(int paramInt1, int paramInt2, int paramInt3) {
    byte[] arrayOfByte = WindowsRegEnumKeyEx(paramInt1, paramInt2, paramInt3);
    if (arrayOfByte != null)
      return arrayOfByte; 
    long l = INIT_SLEEP_TIME;
    for (byte b = 0; b < MAX_ATTEMPTS; b++) {
      try {
        Thread.sleep(l);
      } catch (InterruptedException interruptedException) {
        return arrayOfByte;
      } 
      l *= 2L;
      arrayOfByte = WindowsRegEnumKeyEx(paramInt1, paramInt2, paramInt3);
      if (arrayOfByte != null)
        return arrayOfByte; 
    } 
    return arrayOfByte;
  }
  
  private static native byte[] WindowsRegEnumValue(int paramInt1, int paramInt2, int paramInt3);
  
  private static byte[] WindowsRegEnumValue1(int paramInt1, int paramInt2, int paramInt3) {
    byte[] arrayOfByte = WindowsRegEnumValue(paramInt1, paramInt2, paramInt3);
    if (arrayOfByte != null)
      return arrayOfByte; 
    long l = INIT_SLEEP_TIME;
    for (byte b = 0; b < MAX_ATTEMPTS; b++) {
      try {
        Thread.sleep(l);
      } catch (InterruptedException interruptedException) {
        return arrayOfByte;
      } 
      l *= 2L;
      arrayOfByte = WindowsRegEnumValue(paramInt1, paramInt2, paramInt3);
      if (arrayOfByte != null)
        return arrayOfByte; 
    } 
    return arrayOfByte;
  }
  
  private WindowsPreferences(WindowsPreferences paramWindowsPreferences, String paramString) {
    super(paramWindowsPreferences, paramString);
    int i = paramWindowsPreferences.openKey(4, 131097);
    if (i == 0) {
      this.isBackingStoreAvailable = false;
      return;
    } 
    int[] arrayOfInt = WindowsRegCreateKeyEx1(i, toWindowsName(paramString));
    if (arrayOfInt[1] != 0) {
      logger().warning("Could not create windows registry node " + byteArrayToString(windowsAbsolutePath()) + " at root 0x" + Integer.toHexString(rootNativeHandle()) + ". Windows RegCreateKeyEx(...) returned error code " + arrayOfInt[1] + ".");
      this.isBackingStoreAvailable = false;
      return;
    } 
    this.newNode = (arrayOfInt[2] == 1);
    closeKey(i);
    closeKey(arrayOfInt[0]);
  }
  
  private WindowsPreferences(int paramInt, byte[] paramArrayOfByte) {
    super(null, "");
    int[] arrayOfInt = WindowsRegCreateKeyEx1(paramInt, paramArrayOfByte);
    if (arrayOfInt[1] != 0) {
      logger().warning("Could not open/create prefs root node " + byteArrayToString(windowsAbsolutePath()) + " at root 0x" + Integer.toHexString(rootNativeHandle()) + ". Windows RegCreateKeyEx(...) returned error code " + arrayOfInt[1] + ".");
      this.isBackingStoreAvailable = false;
      return;
    } 
    this.newNode = (arrayOfInt[2] == 1);
    closeKey(arrayOfInt[0]);
  }
  
  private byte[] windowsAbsolutePath() {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    byteArrayOutputStream.write(WINDOWS_ROOT_PATH, 0, WINDOWS_ROOT_PATH.length - 1);
    StringTokenizer stringTokenizer = new StringTokenizer(absolutePath(), "/");
    while (stringTokenizer.hasMoreTokens()) {
      byteArrayOutputStream.write(92);
      String str = stringTokenizer.nextToken();
      byte[] arrayOfByte = toWindowsName(str);
      byteArrayOutputStream.write(arrayOfByte, 0, arrayOfByte.length - 1);
    } 
    byteArrayOutputStream.write(0);
    return byteArrayOutputStream.toByteArray();
  }
  
  private int openKey(int paramInt) { return openKey(paramInt, paramInt); }
  
  private int openKey(int paramInt1, int paramInt2) { return openKey(windowsAbsolutePath(), paramInt1, paramInt2); }
  
  private int openKey(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    if (paramArrayOfByte.length <= 257) {
      int[] arrayOfInt = WindowsRegOpenKey1(rootNativeHandle(), paramArrayOfByte, paramInt1);
      if (arrayOfInt[1] == 5 && paramInt2 != paramInt1)
        arrayOfInt = WindowsRegOpenKey1(rootNativeHandle(), paramArrayOfByte, paramInt2); 
      if (arrayOfInt[1] != 0) {
        logger().warning("Could not open windows registry node " + byteArrayToString(windowsAbsolutePath()) + " at root 0x" + Integer.toHexString(rootNativeHandle()) + ". Windows RegOpenKey(...) returned error code " + arrayOfInt[1] + ".");
        arrayOfInt[0] = 0;
        if (arrayOfInt[1] == 5)
          throw new SecurityException("Could not open windows registry node " + byteArrayToString(windowsAbsolutePath()) + " at root 0x" + Integer.toHexString(rootNativeHandle()) + ": Access denied"); 
      } 
      return arrayOfInt[0];
    } 
    return openKey(rootNativeHandle(), paramArrayOfByte, paramInt1, paramInt2);
  }
  
  private int openKey(int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3) {
    if (paramArrayOfByte.length <= 257) {
      int[] arrayOfInt = WindowsRegOpenKey1(paramInt1, paramArrayOfByte, paramInt2);
      if (arrayOfInt[1] == 5 && paramInt3 != paramInt2)
        arrayOfInt = WindowsRegOpenKey1(paramInt1, paramArrayOfByte, paramInt3); 
      if (arrayOfInt[1] != 0) {
        logger().warning("Could not open windows registry node " + byteArrayToString(windowsAbsolutePath()) + " at root 0x" + Integer.toHexString(paramInt1) + ". Windows RegOpenKey(...) returned error code " + arrayOfInt[1] + ".");
        arrayOfInt[0] = 0;
      } 
      return arrayOfInt[0];
    } 
    int i = -1;
    for (short s = 256; s > 0; s--) {
      if (paramArrayOfByte[s] == 92) {
        i = s;
        break;
      } 
    } 
    byte[] arrayOfByte1 = new byte[i + 1];
    System.arraycopy(paramArrayOfByte, 0, arrayOfByte1, 0, i);
    arrayOfByte1[i] = 0;
    byte[] arrayOfByte2 = new byte[paramArrayOfByte.length - i - 1];
    System.arraycopy(paramArrayOfByte, i + 1, arrayOfByte2, 0, arrayOfByte2.length);
    int j = openKey(paramInt1, arrayOfByte1, paramInt2, paramInt3);
    if (j == 0)
      return 0; 
    int k = openKey(j, arrayOfByte2, paramInt2, paramInt3);
    closeKey(j);
    return k;
  }
  
  private void closeKey(int paramInt) {
    int i = WindowsRegCloseKey(paramInt);
    if (i != 0)
      logger().warning("Could not close windows registry node " + byteArrayToString(windowsAbsolutePath()) + " at root 0x" + Integer.toHexString(rootNativeHandle()) + ". Windows RegCloseKey(...) returned error code " + i + "."); 
  }
  
  protected void putSpi(String paramString1, String paramString2) {
    int i = openKey(2);
    if (i == 0) {
      this.isBackingStoreAvailable = false;
      return;
    } 
    int j = WindowsRegSetValueEx1(i, toWindowsName(paramString1), toWindowsValueString(paramString2));
    if (j != 0) {
      logger().warning("Could not assign value to key " + byteArrayToString(toWindowsName(paramString1)) + " at Windows registry node " + byteArrayToString(windowsAbsolutePath()) + " at root 0x" + Integer.toHexString(rootNativeHandle()) + ". Windows RegSetValueEx(...) returned error code " + j + ".");
      this.isBackingStoreAvailable = false;
    } 
    closeKey(i);
  }
  
  protected String getSpi(String paramString) {
    int i = openKey(1);
    if (i == 0)
      return null; 
    byte[] arrayOfByte = WindowsRegQueryValueEx(i, toWindowsName(paramString));
    if (arrayOfByte == null) {
      closeKey(i);
      return null;
    } 
    closeKey(i);
    return toJavaValueString((byte[])arrayOfByte);
  }
  
  protected void removeSpi(String paramString) {
    int i = openKey(2);
    if (i == 0)
      return; 
    int j = WindowsRegDeleteValue(i, toWindowsName(paramString));
    if (j != 0 && j != 2) {
      logger().warning("Could not delete windows registry value " + byteArrayToString(windowsAbsolutePath()) + "\\" + toWindowsName(paramString) + " at root 0x" + Integer.toHexString(rootNativeHandle()) + ". Windows RegDeleteValue(...) returned error code " + j + ".");
      this.isBackingStoreAvailable = false;
    } 
    closeKey(i);
  }
  
  protected String[] keysSpi() throws BackingStoreException {
    int i = openKey(1);
    if (i == 0)
      throw new BackingStoreException("Could not open windows registry node " + byteArrayToString(windowsAbsolutePath()) + " at root 0x" + Integer.toHexString(rootNativeHandle()) + "."); 
    int[] arrayOfInt = WindowsRegQueryInfoKey1(i);
    if (arrayOfInt[1] != 0) {
      String str = "Could not query windows registry node " + byteArrayToString(windowsAbsolutePath()) + " at root 0x" + Integer.toHexString(rootNativeHandle()) + ". Windows RegQueryInfoKeyEx(...) returned error code " + arrayOfInt[1] + ".";
      logger().warning(str);
      throw new BackingStoreException(str);
    } 
    int j = arrayOfInt[4];
    int k = arrayOfInt[2];
    if (k == 0) {
      closeKey(i);
      return new String[0];
    } 
    String[] arrayOfString = new String[k];
    for (byte b = 0; b < k; b++) {
      byte[] arrayOfByte = WindowsRegEnumValue1(i, b, j + 1);
      if (arrayOfByte == null) {
        String str = "Could not enumerate value #" + b + "  of windows node " + byteArrayToString(windowsAbsolutePath()) + " at root 0x" + Integer.toHexString(rootNativeHandle()) + ".";
        logger().warning(str);
        throw new BackingStoreException(str);
      } 
      arrayOfString[b] = toJavaName(arrayOfByte);
    } 
    closeKey(i);
    return arrayOfString;
  }
  
  protected String[] childrenNamesSpi() throws BackingStoreException {
    int i = openKey(9);
    if (i == 0)
      throw new BackingStoreException("Could not open windows registry node " + byteArrayToString(windowsAbsolutePath()) + " at root 0x" + Integer.toHexString(rootNativeHandle()) + "."); 
    int[] arrayOfInt = WindowsRegQueryInfoKey1(i);
    if (arrayOfInt[1] != 0) {
      String str = "Could not query windows registry node " + byteArrayToString(windowsAbsolutePath()) + " at root 0x" + Integer.toHexString(rootNativeHandle()) + ". Windows RegQueryInfoKeyEx(...) returned error code " + arrayOfInt[1] + ".";
      logger().warning(str);
      throw new BackingStoreException(str);
    } 
    int j = arrayOfInt[3];
    int k = arrayOfInt[0];
    if (k == 0) {
      closeKey(i);
      return new String[0];
    } 
    String[] arrayOfString1 = new String[k];
    String[] arrayOfString2 = new String[k];
    for (byte b = 0; b < k; b++) {
      byte[] arrayOfByte = WindowsRegEnumKeyEx1(i, b, j + 1);
      if (arrayOfByte == null) {
        String str1 = "Could not enumerate key #" + b + "  of windows node " + byteArrayToString(windowsAbsolutePath()) + " at root 0x" + Integer.toHexString(rootNativeHandle()) + ". ";
        logger().warning(str1);
        throw new BackingStoreException(str1);
      } 
      String str = toJavaName(arrayOfByte);
      arrayOfString2[b] = str;
    } 
    closeKey(i);
    return arrayOfString2;
  }
  
  public void flush() throws BackingStoreException {
    if (isRemoved()) {
      this.parent.flush();
      return;
    } 
    if (!this.isBackingStoreAvailable)
      throw new BackingStoreException("flush(): Backing store not available."); 
    int i = openKey(131097);
    if (i == 0)
      throw new BackingStoreException("Could not open windows registry node " + byteArrayToString(windowsAbsolutePath()) + " at root 0x" + Integer.toHexString(rootNativeHandle()) + "."); 
    int j = WindowsRegFlushKey1(i);
    if (j != 0) {
      String str = "Could not flush windows registry node " + byteArrayToString(windowsAbsolutePath()) + " at root 0x" + Integer.toHexString(rootNativeHandle()) + ". Windows RegFlushKey(...) returned error code " + j + ".";
      logger().warning(str);
      throw new BackingStoreException(str);
    } 
    closeKey(i);
  }
  
  public void sync() throws BackingStoreException {
    if (isRemoved())
      throw new IllegalStateException("Node has been removed"); 
    flush();
  }
  
  protected AbstractPreferences childSpi(String paramString) { return new WindowsPreferences(this, paramString); }
  
  public void removeNodeSpi() throws BackingStoreException {
    int i = ((WindowsPreferences)parent()).openKey(65536);
    if (i == 0)
      throw new BackingStoreException("Could not open parent windows registry node of " + byteArrayToString(windowsAbsolutePath()) + " at root 0x" + Integer.toHexString(rootNativeHandle()) + "."); 
    int j = WindowsRegDeleteKey(i, toWindowsName(name()));
    if (j != 0) {
      String str = "Could not delete windows registry node " + byteArrayToString(windowsAbsolutePath()) + " at root 0x" + Integer.toHexString(rootNativeHandle()) + ". Windows RegDeleteKeyEx(...) returned error code " + j + ".";
      logger().warning(str);
      throw new BackingStoreException(str);
    } 
    closeKey(i);
  }
  
  private static String toJavaName(byte[] paramArrayOfByte) {
    String str = byteArrayToString(paramArrayOfByte);
    if (str.length() > 1 && str.substring(0, 2).equals("/!"))
      return toJavaAlt64Name(str); 
    StringBuilder stringBuilder = new StringBuilder();
    for (byte b = 0; b < str.length(); b++) {
      char c;
      if ((c = str.charAt(b)) == '/') {
        char c1 = ' ';
        if (str.length() > b + 1 && (c1 = str.charAt(b + 1)) >= 'A' && c1 <= 'Z') {
          c = c1;
          b++;
        } else if (str.length() > b + 1 && c1 == '/') {
          c = '\\';
          b++;
        } 
      } else if (c == '\\') {
        c = '/';
      } 
      stringBuilder.append(c);
    } 
    return stringBuilder.toString();
  }
  
  private static String toJavaAlt64Name(String paramString) {
    byte[] arrayOfByte = Base64.altBase64ToByteArray(paramString.substring(2));
    StringBuilder stringBuilder = new StringBuilder();
    for (byte b = 0; b < arrayOfByte.length; b++) {
      byte b1 = arrayOfByte[b++] & 0xFF;
      byte b2 = arrayOfByte[b] & 0xFF;
      stringBuilder.append((char)((b1 << 8) + b2));
    } 
    return stringBuilder.toString();
  }
  
  private static byte[] toWindowsName(String paramString) {
    StringBuilder stringBuilder = new StringBuilder();
    for (byte b = 0; b < paramString.length(); b++) {
      char c = paramString.charAt(b);
      if (c < ' ' || c > '')
        return toWindowsAlt64Name(paramString); 
      if (c == '\\') {
        stringBuilder.append("//");
      } else if (c == '/') {
        stringBuilder.append('\\');
      } else if (c >= 'A' && c <= 'Z') {
        stringBuilder.append('/').append(c);
      } else {
        stringBuilder.append(c);
      } 
    } 
    return stringToByteArray(stringBuilder.toString());
  }
  
  private static byte[] toWindowsAlt64Name(String paramString) {
    byte[] arrayOfByte = new byte[2 * paramString.length()];
    byte b1 = 0;
    for (byte b2 = 0; b2 < paramString.length(); b2++) {
      char c = paramString.charAt(b2);
      arrayOfByte[b1++] = (byte)(c >>> '\b');
      arrayOfByte[b1++] = (byte)c;
    } 
    return stringToByteArray("/!" + Base64.byteArrayToAltBase64(arrayOfByte));
  }
  
  private static String toJavaValueString(byte[] paramArrayOfByte) {
    String str = byteArrayToString(paramArrayOfByte);
    StringBuilder stringBuilder = new StringBuilder();
    for (byte b = 0; b < str.length(); b++) {
      char c;
      if ((c = str.charAt(b)) == '/') {
        char c1 = ' ';
        if (str.length() > b + 1 && (c1 = str.charAt(b + 1)) == 'u') {
          if (str.length() < b + 6)
            break; 
          c = (char)Integer.parseInt(str.substring(b + 2, b + 6), 16);
          b += 5;
        } else if (str.length() > b + 1 && str.charAt(b + 1) >= 'A' && c1 <= 'Z') {
          c = c1;
          b++;
        } else if (str.length() > b + 1 && c1 == '/') {
          c = '\\';
          b++;
        } 
      } else if (c == '\\') {
        c = '/';
      } 
      stringBuilder.append(c);
    } 
    return stringBuilder.toString();
  }
  
  private static byte[] toWindowsValueString(String paramString) {
    StringBuilder stringBuilder = new StringBuilder();
    for (byte b = 0; b < paramString.length(); b++) {
      char c = paramString.charAt(b);
      if (c < ' ' || c > '') {
        stringBuilder.append("/u");
        String str = Integer.toHexString(paramString.charAt(b));
        StringBuilder stringBuilder1 = new StringBuilder(str);
        stringBuilder1.reverse();
        int i = 4 - stringBuilder1.length();
        byte b1;
        for (b1 = 0; b1 < i; b1++)
          stringBuilder1.append('0'); 
        for (b1 = 0; b1 < 4; b1++)
          stringBuilder.append(stringBuilder1.charAt(3 - b1)); 
      } else if (c == '\\') {
        stringBuilder.append("//");
      } else if (c == '/') {
        stringBuilder.append('\\');
      } else if (c >= 'A' && c <= 'Z') {
        stringBuilder.append('/').append(c);
      } else {
        stringBuilder.append(c);
      } 
    } 
    return stringToByteArray(stringBuilder.toString());
  }
  
  private int rootNativeHandle() { return isUserNode() ? -2147483647 : -2147483646; }
  
  private static byte[] stringToByteArray(String paramString) {
    byte[] arrayOfByte = new byte[paramString.length() + 1];
    for (byte b = 0; b < paramString.length(); b++)
      arrayOfByte[b] = (byte)paramString.charAt(b); 
    arrayOfByte[paramString.length()] = 0;
    return arrayOfByte;
  }
  
  private static String byteArrayToString(byte[] paramArrayOfByte) {
    StringBuilder stringBuilder = new StringBuilder();
    for (byte b = 0; b < paramArrayOfByte.length - 1; b++)
      stringBuilder.append((char)paramArrayOfByte[b]); 
    return stringBuilder.toString();
  }
  
  protected void flushSpi() throws BackingStoreException {}
  
  protected void syncSpi() throws BackingStoreException {}
  
  private static PlatformLogger logger() {
    if (logger == null)
      logger = PlatformLogger.getLogger("java.util.prefs"); 
    return logger;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\prefs\WindowsPreferences.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */