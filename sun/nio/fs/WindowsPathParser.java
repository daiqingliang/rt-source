package sun.nio.fs;

import java.nio.file.InvalidPathException;

class WindowsPathParser {
  private static final String reservedChars = "<>:\"|?*";
  
  static Result parse(String paramString) { return parse(paramString, true); }
  
  static Result parseNormalizedPath(String paramString) { return parse(paramString, false); }
  
  private static Result parse(String paramString, boolean paramBoolean) {
    String str = "";
    WindowsPathType windowsPathType = null;
    int i = paramString.length();
    int j = 0;
    if (i > 1) {
      char c1 = paramString.charAt(0);
      char c2 = paramString.charAt(1);
      boolean bool = false;
      int k = 2;
      if (isSlash(c1) && isSlash(c2)) {
        windowsPathType = WindowsPathType.UNC;
        j = nextNonSlash(paramString, k, i);
        k = nextSlash(paramString, j, i);
        if (j == k)
          throw new InvalidPathException(paramString, "UNC path is missing hostname"); 
        String str1 = paramString.substring(j, k);
        j = nextNonSlash(paramString, k, i);
        k = nextSlash(paramString, j, i);
        if (j == k)
          throw new InvalidPathException(paramString, "UNC path is missing sharename"); 
        str = "\\\\" + str1 + "\\" + paramString.substring(j, k) + "\\";
        j = k;
      } else if (isLetter(c1) && c2 == ':') {
        char c;
        if (i > 2 && isSlash(c = paramString.charAt(2))) {
          if (c == '\\') {
            str = paramString.substring(0, 3);
          } else {
            str = paramString.substring(0, 2) + '\\';
          } 
          j = 3;
          windowsPathType = WindowsPathType.ABSOLUTE;
        } else {
          str = paramString.substring(0, 2);
          j = 2;
          windowsPathType = WindowsPathType.DRIVE_RELATIVE;
        } 
      } 
    } 
    if (j == 0)
      if (i > 0 && isSlash(paramString.charAt(0))) {
        windowsPathType = WindowsPathType.DIRECTORY_RELATIVE;
        str = "\\";
      } else {
        windowsPathType = WindowsPathType.RELATIVE;
      }  
    if (paramBoolean) {
      StringBuilder stringBuilder = new StringBuilder(paramString.length());
      stringBuilder.append(str);
      return new Result(windowsPathType, str, normalize(stringBuilder, paramString, j));
    } 
    return new Result(windowsPathType, str, paramString);
  }
  
  private static String normalize(StringBuilder paramStringBuilder, String paramString, int paramInt) {
    int i = paramString.length();
    paramInt = nextNonSlash(paramString, paramInt, i);
    int j = paramInt;
    char c = Character.MIN_VALUE;
    while (paramInt < i) {
      char c1 = paramString.charAt(paramInt);
      if (isSlash(c1)) {
        if (c == ' ')
          throw new InvalidPathException(paramString, "Trailing char <" + c + ">", paramInt - 1); 
        paramStringBuilder.append(paramString, j, paramInt);
        paramInt = nextNonSlash(paramString, paramInt, i);
        if (paramInt != i)
          paramStringBuilder.append('\\'); 
        j = paramInt;
        continue;
      } 
      if (isInvalidPathChar(c1))
        throw new InvalidPathException(paramString, "Illegal char <" + c1 + ">", paramInt); 
      c = c1;
      paramInt++;
    } 
    if (j != paramInt) {
      if (c == ' ')
        throw new InvalidPathException(paramString, "Trailing char <" + c + ">", paramInt - 1); 
      paramStringBuilder.append(paramString, j, paramInt);
    } 
    return paramStringBuilder.toString();
  }
  
  private static final boolean isSlash(char paramChar) { return (paramChar == '\\' || paramChar == '/'); }
  
  private static final int nextNonSlash(String paramString, int paramInt1, int paramInt2) {
    while (paramInt1 < paramInt2 && isSlash(paramString.charAt(paramInt1)))
      paramInt1++; 
    return paramInt1;
  }
  
  private static final int nextSlash(String paramString, int paramInt1, int paramInt2) {
    char c;
    while (paramInt1 < paramInt2 && !isSlash(c = paramString.charAt(paramInt1))) {
      if (isInvalidPathChar(c))
        throw new InvalidPathException(paramString, "Illegal character [" + c + "] in path", paramInt1); 
      paramInt1++;
    } 
    return paramInt1;
  }
  
  private static final boolean isLetter(char paramChar) { return ((paramChar >= 'a' && paramChar <= 'z') || (paramChar >= 'A' && paramChar <= 'Z')); }
  
  private static final boolean isInvalidPathChar(char paramChar) { return (paramChar < ' ' || "<>:\"|?*".indexOf(paramChar) != -1); }
  
  static class Result {
    private final WindowsPathType type;
    
    private final String root;
    
    private final String path;
    
    Result(WindowsPathType param1WindowsPathType, String param1String1, String param1String2) {
      this.type = param1WindowsPathType;
      this.root = param1String1;
      this.path = param1String2;
    }
    
    WindowsPathType type() { return this.type; }
    
    String root() { return this.root; }
    
    String path() { return this.path; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\fs\WindowsPathParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */