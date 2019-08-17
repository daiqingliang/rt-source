package com.sun.org.apache.xerces.internal.impl.xpath.regex;

import java.text.CharacterIterator;

public final class REUtil {
  static final int CACHESIZE = 20;
  
  static final RegularExpression[] regexCache = new RegularExpression[20];
  
  static final int composeFromSurrogates(int paramInt1, int paramInt2) { return 65536 + (paramInt1 - 55296 << 10) + paramInt2 - 56320; }
  
  static final boolean isLowSurrogate(int paramInt) { return ((paramInt & 0xFC00) == 56320); }
  
  static final boolean isHighSurrogate(int paramInt) { return ((paramInt & 0xFC00) == 55296); }
  
  static final String decomposeToSurrogates(int paramInt) {
    char[] arrayOfChar = new char[2];
    paramInt -= 65536;
    arrayOfChar[0] = (char)((paramInt >> 10) + 55296);
    arrayOfChar[1] = (char)((paramInt & 0x3FF) + 56320);
    return new String(arrayOfChar);
  }
  
  static final String substring(CharacterIterator paramCharacterIterator, int paramInt1, int paramInt2) {
    char[] arrayOfChar = new char[paramInt2 - paramInt1];
    for (int i = 0; i < arrayOfChar.length; i++)
      arrayOfChar[i] = paramCharacterIterator.setIndex(i + paramInt1); 
    return new String(arrayOfChar);
  }
  
  static final int getOptionValue(int paramInt) {
    char c = Character.MIN_VALUE;
    switch (paramInt) {
      case 105:
        c = '\002';
        break;
      case 109:
        c = '\b';
        break;
      case 115:
        c = '\004';
        break;
      case 120:
        c = '\020';
        break;
      case 117:
        c = ' ';
        break;
      case 119:
        c = '@';
        break;
      case 70:
        c = 'Ā';
        break;
      case 72:
        c = '';
        break;
      case 88:
        c = 'Ȁ';
        break;
      case 44:
        c = 'Ѐ';
        break;
    } 
    return c;
  }
  
  static final int parseOptions(String paramString) throws ParseException {
    if (paramString == null)
      return 0; 
    int i = 0;
    for (byte b = 0; b < paramString.length(); b++) {
      int j = getOptionValue(paramString.charAt(b));
      if (j == 0)
        throw new ParseException("Unknown Option: " + paramString.substring(b), -1); 
      i |= j;
    } 
    return i;
  }
  
  static final String createOptionString(int paramInt) {
    StringBuffer stringBuffer = new StringBuffer(9);
    if ((paramInt & 0x100) != 0)
      stringBuffer.append('F'); 
    if ((paramInt & 0x80) != 0)
      stringBuffer.append('H'); 
    if ((paramInt & 0x200) != 0)
      stringBuffer.append('X'); 
    if ((paramInt & 0x2) != 0)
      stringBuffer.append('i'); 
    if ((paramInt & 0x8) != 0)
      stringBuffer.append('m'); 
    if ((paramInt & 0x4) != 0)
      stringBuffer.append('s'); 
    if ((paramInt & 0x20) != 0)
      stringBuffer.append('u'); 
    if ((paramInt & 0x40) != 0)
      stringBuffer.append('w'); 
    if ((paramInt & 0x10) != 0)
      stringBuffer.append('x'); 
    if ((paramInt & 0x400) != 0)
      stringBuffer.append(','); 
    return stringBuffer.toString().intern();
  }
  
  static String stripExtendedComment(String paramString) {
    int i = paramString.length();
    StringBuffer stringBuffer = new StringBuffer(i);
    byte b = 0;
    while (b < i) {
      char c = paramString.charAt(b++);
      if (c == '\t' || c == '\n' || c == '\f' || c == '\r' || c == ' ')
        continue; 
      if (c == '#') {
        while (b < i) {
          c = paramString.charAt(b++);
          if (c == '\r' || c == '\n')
            break; 
        } 
        continue;
      } 
      if (c == '\\' && b < i) {
        char c1;
        if ((c1 = paramString.charAt(b)) == '#' || c1 == '\t' || c1 == '\n' || c1 == '\f' || c1 == '\r' || c1 == ' ') {
          stringBuffer.append((char)c1);
          b++;
          continue;
        } 
        stringBuffer.append('\\');
        stringBuffer.append((char)c1);
        b++;
        continue;
      } 
      stringBuffer.append((char)c);
    } 
    return stringBuffer.toString();
  }
  
  public static void main(String[] paramArrayOfString) {
    String str = null;
    try {
      String str1 = "";
      String str2 = null;
      if (paramArrayOfString.length == 0) {
        System.out.println("Error:Usage: java REUtil -i|-m|-s|-u|-w|-X regularExpression String");
        System.exit(0);
      } 
      for (byte b1 = 0; b1 < paramArrayOfString.length; b1++) {
        if (paramArrayOfString[b1].length() == 0 || paramArrayOfString[b1].charAt(0) != '-') {
          if (str == null) {
            str = paramArrayOfString[b1];
          } else if (str2 == null) {
            str2 = paramArrayOfString[b1];
          } else {
            System.err.println("Unnecessary: " + paramArrayOfString[b1]);
          } 
        } else if (paramArrayOfString[b1].equals("-i")) {
          str1 = str1 + "i";
        } else if (paramArrayOfString[b1].equals("-m")) {
          str1 = str1 + "m";
        } else if (paramArrayOfString[b1].equals("-s")) {
          str1 = str1 + "s";
        } else if (paramArrayOfString[b1].equals("-u")) {
          str1 = str1 + "u";
        } else if (paramArrayOfString[b1].equals("-w")) {
          str1 = str1 + "w";
        } else if (paramArrayOfString[b1].equals("-X")) {
          str1 = str1 + "X";
        } else {
          System.err.println("Unknown option: " + paramArrayOfString[b1]);
        } 
      } 
      RegularExpression regularExpression = new RegularExpression(str, str1);
      System.out.println("RegularExpression: " + regularExpression);
      Match match = new Match();
      regularExpression.matches(str2, match);
      for (byte b2 = 0; b2 < match.getNumberOfGroups(); b2++) {
        if (!b2) {
          System.out.print("Matched range for the whole pattern: ");
        } else {
          System.out.print("[" + b2 + "]: ");
        } 
        if (match.getBeginning(b2) < 0) {
          System.out.println("-1");
        } else {
          System.out.print(match.getBeginning(b2) + ", " + match.getEnd(b2) + ", ");
          System.out.println("\"" + match.getCapturedText(b2) + "\"");
        } 
      } 
    } catch (ParseException parseException) {
      if (str == null) {
        parseException.printStackTrace();
      } else {
        System.err.println("com.sun.org.apache.xerces.internal.utils.regex.ParseException: " + parseException.getMessage());
        String str1 = "        ";
        System.err.println(str1 + str);
        int i = parseException.getLocation();
        if (i >= 0) {
          System.err.print(str1);
          for (byte b = 0; b < i; b++)
            System.err.print("-"); 
          System.err.println("^");
        } 
      } 
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
  
  public static RegularExpression createRegex(String paramString1, String paramString2) throws ParseException {
    RegularExpression regularExpression = null;
    int i = parseOptions(paramString2);
    synchronized (regexCache) {
      byte b;
      for (b = 0; b < 20; b++) {
        RegularExpression regularExpression1 = regexCache[b];
        if (regularExpression1 == null) {
          b = -1;
          break;
        } 
        if (regularExpression1.equals(paramString1, i)) {
          regularExpression = regularExpression1;
          break;
        } 
      } 
      if (regularExpression != null) {
        if (b != 0) {
          System.arraycopy(regexCache, 0, regexCache, 1, b);
          regexCache[0] = regularExpression;
        } 
      } else {
        regularExpression = new RegularExpression(paramString1, paramString2);
        System.arraycopy(regexCache, 0, regexCache, 1, 19);
        regexCache[0] = regularExpression;
      } 
    } 
    return regularExpression;
  }
  
  public static boolean matches(String paramString1, String paramString2) throws ParseException { return createRegex(paramString1, null).matches(paramString2); }
  
  public static boolean matches(String paramString1, String paramString2, String paramString3) throws ParseException { return createRegex(paramString1, paramString2).matches(paramString3); }
  
  public static String quoteMeta(String paramString) {
    int i = paramString.length();
    StringBuffer stringBuffer = null;
    for (int j = 0; j < i; j++) {
      char c = paramString.charAt(j);
      if (".*+?{[()|\\^$".indexOf(c) >= 0) {
        if (stringBuffer == null) {
          stringBuffer = new StringBuffer(j + (i - j) * 2);
          if (j > 0)
            stringBuffer.append(paramString.substring(0, j)); 
        } 
        stringBuffer.append('\\');
        stringBuffer.append((char)c);
      } else if (stringBuffer != null) {
        stringBuffer.append((char)c);
      } 
    } 
    return (stringBuffer != null) ? stringBuffer.toString() : paramString;
  }
  
  static void dumpString(String paramString) {
    for (byte b = 0; b < paramString.length(); b++) {
      System.out.print(Integer.toHexString(paramString.charAt(b)));
      System.out.print(" ");
    } 
    System.out.println();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xpath\regex\REUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */