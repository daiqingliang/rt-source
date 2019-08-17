package java.io;

import java.security.AccessController;
import java.util.Locale;
import sun.security.action.GetPropertyAction;

class WinNTFileSystem extends FileSystem {
  private final char slash = ((String)AccessController.doPrivileged(new GetPropertyAction("file.separator"))).charAt(0);
  
  private final char altSlash = (this.slash == '\\') ? '/' : '\\';
  
  private final char semicolon = ((String)AccessController.doPrivileged(new GetPropertyAction("path.separator"))).charAt(0);
  
  private static String[] driveDirCache = new String[26];
  
  private ExpiringCache cache = new ExpiringCache();
  
  private ExpiringCache prefixCache = new ExpiringCache();
  
  private boolean isSlash(char paramChar) { return (paramChar == '\\' || paramChar == '/'); }
  
  private boolean isLetter(char paramChar) { return ((paramChar >= 'a' && paramChar <= 'z') || (paramChar >= 'A' && paramChar <= 'Z')); }
  
  private String slashify(String paramString) { return (paramString.length() > 0 && paramString.charAt(0) != this.slash) ? (this.slash + paramString) : paramString; }
  
  public char getSeparator() { return this.slash; }
  
  public char getPathSeparator() { return this.semicolon; }
  
  public String normalize(String paramString) {
    int i = paramString.length();
    char c1 = this.slash;
    char c2 = this.altSlash;
    char c = Character.MIN_VALUE;
    for (byte b = 0; b < i; b++) {
      char c3 = paramString.charAt(b);
      if (c3 == c2)
        return normalize(paramString, i, (c == c1) ? (b - 1) : b); 
      if (c3 == c1 && c == c1 && b > 1)
        return normalize(paramString, i, b - 1); 
      if (c3 == ':' && b > 1)
        return normalize(paramString, i, 0); 
      c = c3;
    } 
    return (c == c1) ? normalize(paramString, i, i - 1) : paramString;
  }
  
  private String normalize(String paramString, int paramInt1, int paramInt2) {
    int i;
    if (paramInt1 == 0)
      return paramString; 
    if (paramInt2 < 3)
      paramInt2 = 0; 
    char c = this.slash;
    StringBuffer stringBuffer = new StringBuffer(paramInt1);
    if (paramInt2 == 0) {
      i = normalizePrefix(paramString, paramInt1, stringBuffer);
    } else {
      i = paramInt2;
      stringBuffer.append(paramString.substring(0, paramInt2));
    } 
    while (i < paramInt1) {
      char c1 = paramString.charAt(i++);
      if (isSlash(c1)) {
        while (i < paramInt1 && isSlash(paramString.charAt(i)))
          i++; 
        if (i == paramInt1) {
          int j = stringBuffer.length();
          if (j == 2 && stringBuffer.charAt(1) == ':') {
            stringBuffer.append(c);
            break;
          } 
          if (j == 0) {
            stringBuffer.append(c);
            break;
          } 
          if (j == 1 && isSlash(stringBuffer.charAt(0)))
            stringBuffer.append(c); 
          break;
        } 
        stringBuffer.append(c);
        continue;
      } 
      stringBuffer.append(c1);
    } 
    return stringBuffer.toString();
  }
  
  private int normalizePrefix(String paramString, int paramInt, StringBuffer paramStringBuffer) {
    int i;
    for (i = 0; i < paramInt && isSlash(paramString.charAt(i)); i++);
    char c;
    if (paramInt - i >= 2 && isLetter(c = paramString.charAt(i)) && paramString.charAt(i + 1) == ':') {
      paramStringBuffer.append(c);
      paramStringBuffer.append(':');
      i += 2;
    } else {
      i = 0;
      if (paramInt >= 2 && isSlash(paramString.charAt(0)) && isSlash(paramString.charAt(1))) {
        i = 1;
        paramStringBuffer.append(this.slash);
      } 
    } 
    return i;
  }
  
  public int prefixLength(String paramString) {
    char c1 = this.slash;
    int i = paramString.length();
    if (i == 0)
      return 0; 
    char c2 = paramString.charAt(0);
    char c3 = (i > 1) ? paramString.charAt(1) : 0;
    return (c2 == c1) ? ((c3 == c1) ? 2 : 1) : ((isLetter(c2) && c3 == ':') ? ((i > 2 && paramString.charAt(2) == c1) ? 3 : 2) : 0);
  }
  
  public String resolve(String paramString1, String paramString2) {
    int i = paramString1.length();
    if (i == 0)
      return paramString2; 
    int j = paramString2.length();
    if (j == 0)
      return paramString1; 
    String str = paramString2;
    int k = 0;
    int m = i;
    if (j > 1 && str.charAt(0) == this.slash) {
      if (str.charAt(1) == this.slash) {
        k = 2;
      } else {
        k = 1;
      } 
      if (j == k)
        return (paramString1.charAt(i - 1) == this.slash) ? paramString1.substring(0, i - 1) : paramString1; 
    } 
    if (paramString1.charAt(i - 1) == this.slash)
      m--; 
    int n = m + j - k;
    char[] arrayOfChar = null;
    if (paramString2.charAt(k) == this.slash) {
      arrayOfChar = new char[n];
      paramString1.getChars(0, m, arrayOfChar, 0);
      paramString2.getChars(k, j, arrayOfChar, m);
    } else {
      arrayOfChar = new char[n + 1];
      paramString1.getChars(0, m, arrayOfChar, 0);
      arrayOfChar[m] = this.slash;
      paramString2.getChars(k, j, arrayOfChar, m + 1);
    } 
    return new String(arrayOfChar);
  }
  
  public String getDefaultParent() { return "" + this.slash; }
  
  public String fromURIPath(String paramString) {
    String str = paramString;
    if (str.length() > 2 && str.charAt(2) == ':') {
      str = str.substring(1);
      if (str.length() > 3 && str.endsWith("/"))
        str = str.substring(0, str.length() - 1); 
    } else if (str.length() > 1 && str.endsWith("/")) {
      str = str.substring(0, str.length() - 1);
    } 
    return str;
  }
  
  public boolean isAbsolute(File paramFile) {
    int i = paramFile.getPrefixLength();
    return ((i == 2 && paramFile.getPath().charAt(0) == this.slash) || i == 3);
  }
  
  public String resolve(File paramFile) {
    String str = paramFile.getPath();
    int i = paramFile.getPrefixLength();
    if (i == 2 && str.charAt(0) == this.slash)
      return str; 
    if (i == 3)
      return str; 
    if (i == 0)
      return getUserPath() + slashify(str); 
    if (i == 1) {
      String str1 = getUserPath();
      String str2 = getDrive(str1);
      return (str2 != null) ? (str2 + str) : (str1 + str);
    } 
    if (i == 2) {
      String str1 = getUserPath();
      String str2 = getDrive(str1);
      if (str2 != null && str.startsWith(str2))
        return str1 + slashify(str.substring(2)); 
      char c = str.charAt(0);
      String str3 = getDriveDirectory(c);
      if (str3 != null) {
        String str4 = c + ':' + str3 + slashify(str.substring(2));
        SecurityManager securityManager = System.getSecurityManager();
        try {
          if (securityManager != null)
            securityManager.checkRead(str4); 
        } catch (SecurityException securityException) {
          throw new SecurityException("Cannot resolve path " + str);
        } 
        return str4;
      } 
      return c + ":" + slashify(str.substring(2));
    } 
    throw new InternalError("Unresolvable path: " + str);
  }
  
  private String getUserPath() { return normalize(System.getProperty("user.dir")); }
  
  private String getDrive(String paramString) {
    int i = prefixLength(paramString);
    return (i == 3) ? paramString.substring(0, 2) : null;
  }
  
  private static int driveIndex(char paramChar) { return (paramChar >= 'a' && paramChar <= 'z') ? (paramChar - 'a') : ((paramChar >= 'A' && paramChar <= 'Z') ? (paramChar - 'A') : -1); }
  
  private native String getDriveDirectory(int paramInt);
  
  private String getDriveDirectory(char paramChar) {
    int i = driveIndex(paramChar);
    if (i < 0)
      return null; 
    String str = driveDirCache[i];
    if (str != null)
      return str; 
    str = getDriveDirectory(i + 1);
    driveDirCache[i] = str;
    return str;
  }
  
  public String canonicalize(String paramString) {
    int i = paramString.length();
    if (i == 2 && isLetter(paramString.charAt(0)) && paramString.charAt(1) == ':') {
      char c = paramString.charAt(0);
      return (c >= 'A' && c <= 'Z') ? paramString : ("" + (char)(c - ' ') + ':');
    } 
    if (i == 3 && isLetter(paramString.charAt(0)) && paramString.charAt(1) == ':' && paramString.charAt(2) == '\\') {
      char c = paramString.charAt(0);
      return (c >= 'A' && c <= 'Z') ? paramString : ("" + (char)(c - ' ') + ':' + '\\');
    } 
    if (!useCanonCaches)
      return canonicalize0(paramString); 
    String str = this.cache.get(paramString);
    if (str == null) {
      String str1 = null;
      String str2 = null;
      if (useCanonPrefixCache) {
        str1 = parentOrNull(paramString);
        if (str1 != null) {
          str2 = this.prefixCache.get(str1);
          if (str2 != null) {
            String str3 = paramString.substring(1 + str1.length());
            str = canonicalizeWithPrefix(str2, str3);
            this.cache.put(str1 + File.separatorChar + str3, str);
          } 
        } 
      } 
      if (str == null) {
        str = canonicalize0(paramString);
        this.cache.put(paramString, str);
        if (useCanonPrefixCache && str1 != null) {
          str2 = parentOrNull(str);
          if (str2 != null) {
            File file = new File(str);
            if (file.exists() && !file.isDirectory())
              this.prefixCache.put(str1, str2); 
          } 
        } 
      } 
    } 
    return str;
  }
  
  private native String canonicalize0(String paramString);
  
  private String canonicalizeWithPrefix(String paramString1, String paramString2) { return canonicalizeWithPrefix0(paramString1, paramString1 + File.separatorChar + paramString2); }
  
  private native String canonicalizeWithPrefix0(String paramString1, String paramString2);
  
  private static String parentOrNull(String paramString) {
    if (paramString == null)
      return null; 
    char c = File.separatorChar;
    byte b1 = 47;
    int i = paramString.length() - 1;
    int j = i;
    byte b2 = 0;
    byte b3 = 0;
    while (j > 0) {
      char c1 = paramString.charAt(j);
      if (c1 == '.') {
        if (++b2 >= 2)
          return null; 
        if (!b3)
          return null; 
      } else {
        if (c1 == c)
          return (b2 == 1 && !b3) ? null : ((j == 0 || j >= i - 1 || paramString.charAt(j - 1) == c || paramString.charAt(j - 1) == b1) ? null : paramString.substring(0, j)); 
        if (c1 == b1)
          return null; 
        if (c1 == '*' || c1 == '?')
          return null; 
        b3++;
        b2 = 0;
      } 
      j--;
    } 
    return null;
  }
  
  public native int getBooleanAttributes(File paramFile);
  
  public native boolean checkAccess(File paramFile, int paramInt);
  
  public native long getLastModifiedTime(File paramFile);
  
  public native long getLength(File paramFile);
  
  public native boolean setPermission(File paramFile, int paramInt, boolean paramBoolean1, boolean paramBoolean2);
  
  public native boolean createFileExclusively(String paramString) throws IOException;
  
  public native String[] list(File paramFile);
  
  public native boolean createDirectory(File paramFile);
  
  public native boolean setLastModifiedTime(File paramFile, long paramLong);
  
  public native boolean setReadOnly(File paramFile);
  
  public boolean delete(File paramFile) {
    this.cache.clear();
    this.prefixCache.clear();
    return delete0(paramFile);
  }
  
  private native boolean delete0(File paramFile);
  
  public boolean rename(File paramFile1, File paramFile2) {
    this.cache.clear();
    this.prefixCache.clear();
    return rename0(paramFile1, paramFile2);
  }
  
  private native boolean rename0(File paramFile1, File paramFile2);
  
  public File[] listRoots() {
    int i = listRoots0();
    byte b1 = 0;
    for (int j = 0; j < 26; j++) {
      if ((i >> j & true) != 0)
        if (!access((char)(65 + j) + ":" + this.slash)) {
          i &= (1 << j ^ 0xFFFFFFFF);
        } else {
          b1++;
        }  
    } 
    File[] arrayOfFile = new File[b1];
    byte b2 = 0;
    char c = this.slash;
    for (int k = 0; k < 26; k++) {
      if ((i >> k & true) != 0)
        arrayOfFile[b2++] = new File((char)(65 + k) + ":" + c); 
    } 
    return arrayOfFile;
  }
  
  private static native int listRoots0();
  
  private boolean access(String paramString) throws IOException {
    try {
      SecurityManager securityManager = System.getSecurityManager();
      if (securityManager != null)
        securityManager.checkRead(paramString); 
      return true;
    } catch (SecurityException securityException) {
      return false;
    } 
  }
  
  public long getSpace(File paramFile, int paramInt) { return paramFile.exists() ? getSpace0(paramFile, paramInt) : 0L; }
  
  private native long getSpace0(File paramFile, int paramInt);
  
  public int compare(File paramFile1, File paramFile2) { return paramFile1.getPath().compareToIgnoreCase(paramFile2.getPath()); }
  
  public int hashCode(File paramFile) { return paramFile.getPath().toLowerCase(Locale.ENGLISH).hashCode() ^ 0x12D591; }
  
  private static native void initIDs();
  
  static  {
    initIDs();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\io\WinNTFileSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */