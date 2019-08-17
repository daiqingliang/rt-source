package sun.nio.fs;

import java.util.regex.PatternSyntaxException;

public class Globs {
  private static final String regexMetaChars = ".^$+{[]|()";
  
  private static final String globMetaChars = "\\*?[{";
  
  private static char EOL = Character.MIN_VALUE;
  
  private static boolean isRegexMeta(char paramChar) { return (".^$+{[]|()".indexOf(paramChar) != -1); }
  
  private static boolean isGlobMeta(char paramChar) { return ("\\*?[{".indexOf(paramChar) != -1); }
  
  private static char next(String paramString, int paramInt) { return (paramInt < paramString.length()) ? paramString.charAt(paramInt) : EOL; }
  
  private static String toRegexPattern(String paramString, boolean paramBoolean) {
    boolean bool = false;
    StringBuilder stringBuilder = new StringBuilder("^");
    byte b = 0;
    while (b < paramString.length()) {
      char c;
      boolean bool1;
      char c2;
      char c1 = paramString.charAt(b++);
      switch (c1) {
        case '\\':
          if (b == paramString.length())
            throw new PatternSyntaxException("No character to escape", paramString, b - 1); 
          c2 = paramString.charAt(b++);
          if (isGlobMeta(c2) || isRegexMeta(c2))
            stringBuilder.append('\\'); 
          stringBuilder.append(c2);
          continue;
        case '/':
          if (paramBoolean) {
            stringBuilder.append("\\\\");
            continue;
          } 
          stringBuilder.append(c1);
          continue;
        case '[':
          if (paramBoolean) {
            stringBuilder.append("[[^\\\\]&&[");
          } else {
            stringBuilder.append("[[^/]&&[");
          } 
          if (next(paramString, b) == '^') {
            stringBuilder.append("\\^");
            b++;
          } else {
            if (next(paramString, b) == '!') {
              stringBuilder.append('^');
              b++;
            } 
            if (next(paramString, b) == '-') {
              stringBuilder.append('-');
              b++;
            } 
          } 
          bool1 = false;
          for (c = Character.MIN_VALUE; b < paramString.length(); c = c1) {
            c1 = paramString.charAt(b++);
            if (c1 == ']')
              break; 
            if (c1 == '/' || (paramBoolean && c1 == '\\'))
              throw new PatternSyntaxException("Explicit 'name separator' in class", paramString, b - 1); 
            if (c1 == '\\' || c1 == '[' || (c1 == '&' && next(paramString, b) == '&'))
              stringBuilder.append('\\'); 
            stringBuilder.append(c1);
            if (c1 == '-') {
              if (!bool1)
                throw new PatternSyntaxException("Invalid range", paramString, b - 1); 
              if ((c1 = next(paramString, b++)) == EOL || c1 == ']')
                break; 
              if (c1 < c)
                throw new PatternSyntaxException("Invalid range", paramString, b - 3); 
              stringBuilder.append(c1);
              bool1 = false;
              continue;
            } 
            bool1 = true;
          } 
          if (c1 != ']')
            throw new PatternSyntaxException("Missing ']", paramString, b - 1); 
          stringBuilder.append("]]");
          continue;
        case '{':
          if (bool)
            throw new PatternSyntaxException("Cannot nest groups", paramString, b - 1); 
          stringBuilder.append("(?:(?:");
          bool = true;
          continue;
        case '}':
          if (bool) {
            stringBuilder.append("))");
            bool = false;
            continue;
          } 
          stringBuilder.append('}');
          continue;
        case ',':
          if (bool) {
            stringBuilder.append(")|(?:");
            continue;
          } 
          stringBuilder.append(',');
          continue;
        case '*':
          if (next(paramString, b) == '*') {
            stringBuilder.append(".*");
            b++;
            continue;
          } 
          if (paramBoolean) {
            stringBuilder.append("[^\\\\]*");
            continue;
          } 
          stringBuilder.append("[^/]*");
          continue;
        case '?':
          if (paramBoolean) {
            stringBuilder.append("[^\\\\]");
            continue;
          } 
          stringBuilder.append("[^/]");
          continue;
      } 
      if (isRegexMeta(c1))
        stringBuilder.append('\\'); 
      stringBuilder.append(c1);
    } 
    if (bool)
      throw new PatternSyntaxException("Missing '}", paramString, b - 1); 
    return stringBuilder.append('$').toString();
  }
  
  static String toUnixRegexPattern(String paramString) { return toRegexPattern(paramString, false); }
  
  static String toWindowsRegexPattern(String paramString) { return toRegexPattern(paramString, true); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\fs\Globs.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */