package com.sun.org.apache.regexp.internal;

import java.io.Serializable;
import java.util.Vector;

public class RE implements Serializable {
  public static final int MATCH_NORMAL = 0;
  
  public static final int MATCH_CASEINDEPENDENT = 1;
  
  public static final int MATCH_MULTILINE = 2;
  
  public static final int MATCH_SINGLELINE = 4;
  
  static final char OP_END = 'E';
  
  static final char OP_BOL = '^';
  
  static final char OP_EOL = '$';
  
  static final char OP_ANY = '.';
  
  static final char OP_ANYOF = '[';
  
  static final char OP_BRANCH = '|';
  
  static final char OP_ATOM = 'A';
  
  static final char OP_STAR = '*';
  
  static final char OP_PLUS = '+';
  
  static final char OP_MAYBE = '?';
  
  static final char OP_ESCAPE = '\\';
  
  static final char OP_OPEN = '(';
  
  static final char OP_OPEN_CLUSTER = '<';
  
  static final char OP_CLOSE = ')';
  
  static final char OP_CLOSE_CLUSTER = '>';
  
  static final char OP_BACKREF = '#';
  
  static final char OP_GOTO = 'G';
  
  static final char OP_NOTHING = 'N';
  
  static final char OP_RELUCTANTSTAR = '8';
  
  static final char OP_RELUCTANTPLUS = '=';
  
  static final char OP_RELUCTANTMAYBE = '/';
  
  static final char OP_POSIXCLASS = 'P';
  
  static final char E_ALNUM = 'w';
  
  static final char E_NALNUM = 'W';
  
  static final char E_BOUND = 'b';
  
  static final char E_NBOUND = 'B';
  
  static final char E_SPACE = 's';
  
  static final char E_NSPACE = 'S';
  
  static final char E_DIGIT = 'd';
  
  static final char E_NDIGIT = 'D';
  
  static final char POSIX_CLASS_ALNUM = 'w';
  
  static final char POSIX_CLASS_ALPHA = 'a';
  
  static final char POSIX_CLASS_BLANK = 'b';
  
  static final char POSIX_CLASS_CNTRL = 'c';
  
  static final char POSIX_CLASS_DIGIT = 'd';
  
  static final char POSIX_CLASS_GRAPH = 'g';
  
  static final char POSIX_CLASS_LOWER = 'l';
  
  static final char POSIX_CLASS_PRINT = 'p';
  
  static final char POSIX_CLASS_PUNCT = '!';
  
  static final char POSIX_CLASS_SPACE = 's';
  
  static final char POSIX_CLASS_UPPER = 'u';
  
  static final char POSIX_CLASS_XDIGIT = 'x';
  
  static final char POSIX_CLASS_JSTART = 'j';
  
  static final char POSIX_CLASS_JPART = 'k';
  
  static final int maxNode = 65536;
  
  static final int MAX_PAREN = 16;
  
  static final int offsetOpcode = 0;
  
  static final int offsetOpdata = 1;
  
  static final int offsetNext = 2;
  
  static final int nodeSize = 3;
  
  REProgram program;
  
  CharacterIterator search;
  
  int matchFlags;
  
  int maxParen = 16;
  
  int parenCount;
  
  int start0;
  
  int end0;
  
  int start1;
  
  int end1;
  
  int start2;
  
  int end2;
  
  int[] startn;
  
  int[] endn;
  
  int[] startBackref;
  
  int[] endBackref;
  
  public static final int REPLACE_ALL = 0;
  
  public static final int REPLACE_FIRSTONLY = 1;
  
  public static final int REPLACE_BACKREFERENCES = 2;
  
  public RE(String paramString) throws RESyntaxException { this(paramString, 0); }
  
  public RE(String paramString, int paramInt) throws RESyntaxException {
    this((new RECompiler()).compile(paramString));
    setMatchFlags(paramInt);
  }
  
  public RE(REProgram paramREProgram, int paramInt) {
    setProgram(paramREProgram);
    setMatchFlags(paramInt);
  }
  
  public RE(REProgram paramREProgram) { this(paramREProgram, 0); }
  
  public RE() { this((REProgram)null, 0); }
  
  public static String simplePatternToFullRegularExpression(String paramString) {
    StringBuffer stringBuffer = new StringBuffer();
    for (byte b = 0; b < paramString.length(); b++) {
      char c = paramString.charAt(b);
      switch (c) {
        case '*':
          stringBuffer.append(".*");
          break;
        case '$':
        case '(':
        case ')':
        case '+':
        case '.':
        case '?':
        case '[':
        case '\\':
        case ']':
        case '^':
        case '{':
        case '|':
        case '}':
          stringBuffer.append('\\');
        default:
          stringBuffer.append(c);
          break;
      } 
    } 
    return stringBuffer.toString();
  }
  
  public void setMatchFlags(int paramInt) { this.matchFlags = paramInt; }
  
  public int getMatchFlags() { return this.matchFlags; }
  
  public void setProgram(REProgram paramREProgram) {
    this.program = paramREProgram;
    if (paramREProgram != null && paramREProgram.maxParens != -1) {
      this.maxParen = paramREProgram.maxParens;
    } else {
      this.maxParen = 16;
    } 
  }
  
  public REProgram getProgram() { return this.program; }
  
  public int getParenCount() { return this.parenCount; }
  
  public String getParen(int paramInt) {
    int i;
    return (paramInt < this.parenCount && (i = getParenStart(paramInt)) >= 0) ? this.search.substring(i, getParenEnd(paramInt)) : null;
  }
  
  public final int getParenStart(int paramInt) {
    if (paramInt < this.parenCount) {
      switch (paramInt) {
        case 0:
          return this.start0;
        case 1:
          return this.start1;
        case 2:
          return this.start2;
      } 
      if (this.startn == null)
        allocParens(); 
      return this.startn[paramInt];
    } 
    return -1;
  }
  
  public final int getParenEnd(int paramInt) {
    if (paramInt < this.parenCount) {
      switch (paramInt) {
        case 0:
          return this.end0;
        case 1:
          return this.end1;
        case 2:
          return this.end2;
      } 
      if (this.endn == null)
        allocParens(); 
      return this.endn[paramInt];
    } 
    return -1;
  }
  
  public final int getParenLength(int paramInt) { return (paramInt < this.parenCount) ? (getParenEnd(paramInt) - getParenStart(paramInt)) : -1; }
  
  protected final void setParenStart(int paramInt1, int paramInt2) {
    if (paramInt1 < this.parenCount) {
      switch (paramInt1) {
        case 0:
          this.start0 = paramInt2;
          return;
        case 1:
          this.start1 = paramInt2;
          return;
        case 2:
          this.start2 = paramInt2;
          return;
      } 
      if (this.startn == null)
        allocParens(); 
      this.startn[paramInt1] = paramInt2;
    } 
  }
  
  protected final void setParenEnd(int paramInt1, int paramInt2) {
    if (paramInt1 < this.parenCount) {
      switch (paramInt1) {
        case 0:
          this.end0 = paramInt2;
          return;
        case 1:
          this.end1 = paramInt2;
          return;
        case 2:
          this.end2 = paramInt2;
          return;
      } 
      if (this.endn == null)
        allocParens(); 
      this.endn[paramInt1] = paramInt2;
    } 
  }
  
  protected void internalError(String paramString) throws RESyntaxException { throw new Error("RE internal error: " + paramString); }
  
  private final void allocParens() {
    this.startn = new int[this.maxParen];
    this.endn = new int[this.maxParen];
    for (byte b = 0; b < this.maxParen; b++) {
      this.startn[b] = -1;
      this.endn[b] = -1;
    } 
  }
  
  protected int matchNodes(int paramInt1, int paramInt2, int paramInt3) {
    int i = paramInt3;
    char[] arrayOfChar = this.program.instruction;
    int j;
    for (j = paramInt1; j < paramInt2; j = k) {
      int i8;
      int i7;
      int i6;
      int i5;
      int i4;
      int i3;
      int i2;
      int i1;
      char c3;
      int n;
      short s;
      int m;
      char c1 = arrayOfChar[j + 0];
      int k = j + (short)arrayOfChar[j + 2];
      char c2 = arrayOfChar[j + 1];
      switch (c1) {
        case '/':
          n = 0;
          do {
            int i9;
            if ((i9 = matchNodes(k, 65536, i)) != -1)
              return i9; 
          } while (n++ == 0 && (i = matchNodes(j + 3, k, i)) != -1);
          return -1;
        case '=':
          while ((i = matchNodes(j + 3, k, i)) != -1) {
            int i9;
            if ((i9 = matchNodes(k, 65536, i)) != -1)
              return i9; 
          } 
          return -1;
        case '8':
          do {
            int i9;
            if ((i9 = matchNodes(k, 65536, i)) != -1)
              return i9; 
          } while ((i = matchNodes(j + 3, k, i)) != -1);
          return -1;
        case '(':
          if ((this.program.flags & true) != 0)
            this.startBackref[c2] = i; 
          if ((m = matchNodes(k, 65536, i)) != -1) {
            if (c2 + '\001' > this.parenCount)
              this.parenCount = c2 + '\001'; 
            if (getParenStart(c2) == -1)
              setParenStart(c2, i); 
          } 
          return m;
        case ')':
          if ((this.program.flags & true) != 0)
            this.endBackref[c2] = i; 
          if ((m = matchNodes(k, 65536, i)) != -1) {
            if (c2 + '\001' > this.parenCount)
              this.parenCount = c2 + '\001'; 
            if (getParenEnd(c2) == -1)
              setParenEnd(c2, i); 
          } 
          return m;
        case '<':
        case '>':
          return matchNodes(k, 65536, i);
        case '#':
          n = this.startBackref[c2];
          i2 = this.endBackref[c2];
          if (n == -1 || i2 == -1)
            return -1; 
          if (n == i2)
            break; 
          i4 = i2 - n;
          if (this.search.isEnd(i + i4 - 1))
            return -1; 
          i6 = ((this.matchFlags & true) != 0) ? 1 : 0;
          for (i7 = 0; i7 < i4; i7++) {
            if (compareChars(this.search.charAt(i++), this.search.charAt(n + i7), i6) != 0)
              return -1; 
          } 
          break;
        case '^':
          if (i != 0) {
            if ((this.matchFlags & 0x2) == 2) {
              if (i <= 0 || !isNewline(i - 1))
                return -1; 
              break;
            } 
            return -1;
          } 
          break;
        case '$':
          if (!this.search.isEnd(0) && !this.search.isEnd(i)) {
            if ((this.matchFlags & 0x2) == 2) {
              if (!isNewline(i))
                return -1; 
              break;
            } 
            return -1;
          } 
          break;
        case '\\':
          switch (c2) {
            case 'B':
            case 'b':
              n = (i == 0) ? 10 : this.search.charAt(i - 1);
              i2 = this.search.isEnd(i) ? 10 : this.search.charAt(i);
              if (((Character.isLetterOrDigit(n) == Character.isLetterOrDigit(i2)) ? 1 : 0) == ((c2 == 'b') ? 1 : 0))
                return -1; 
              break;
            case 'D':
            case 'S':
            case 'W':
            case 'd':
            case 's':
            case 'w':
              if (this.search.isEnd(i))
                return -1; 
              c3 = this.search.charAt(i);
              switch (c2) {
                case 'W':
                case 'w':
                  if (((Character.isLetterOrDigit(c3) || c3 == '_') ? 1 : 0) != ((c2 == 'w') ? 1 : 0))
                    return -1; 
                  break;
                case 'D':
                case 'd':
                  if (Character.isDigit(c3) != ((c2 == 'd')))
                    return -1; 
                  break;
                case 'S':
                case 's':
                  if (Character.isWhitespace(c3) != ((c2 == 's')))
                    return -1; 
                  break;
              } 
              i++;
              break;
          } 
          internalError("Unrecognized escape '" + c2 + "'");
          break;
        case '.':
          if ((this.matchFlags & 0x4) == 4) {
            if (this.search.isEnd(i))
              return -1; 
          } else if (this.search.isEnd(i) || isNewline(i)) {
            return -1;
          } 
          i++;
          break;
        case 'A':
          if (this.search.isEnd(i))
            return -1; 
          c3 = c2;
          i1 = j + 3;
          if (this.search.isEnd(c3 + i - '\001'))
            return -1; 
          i4 = ((this.matchFlags & true) != 0) ? 1 : 0;
          for (i6 = 0; i6 < c3; i6++) {
            if (compareChars(this.search.charAt(i++), arrayOfChar[i1 + i6], i4) != 0)
              return -1; 
          } 
          break;
        case 'P':
          if (this.search.isEnd(i))
            return -1; 
          switch (c2) {
            case 'w':
              if (!Character.isLetterOrDigit(this.search.charAt(i)))
                return -1; 
              break;
            case 'a':
              if (!Character.isLetter(this.search.charAt(i)))
                return -1; 
              break;
            case 'd':
              if (!Character.isDigit(this.search.charAt(i)))
                return -1; 
              break;
            case 'b':
              if (!Character.isSpaceChar(this.search.charAt(i)))
                return -1; 
              break;
            case 's':
              if (!Character.isWhitespace(this.search.charAt(i)))
                return -1; 
              break;
            case 'c':
              if (Character.getType(this.search.charAt(i)) != 15)
                return -1; 
              break;
            case 'g':
              switch (Character.getType(this.search.charAt(i))) {
                case 25:
                case 26:
                case 27:
                case 28:
                  break;
              } 
              return -1;
            case 'l':
              if (Character.getType(this.search.charAt(i)) != 2)
                return -1; 
              break;
            case 'u':
              if (Character.getType(this.search.charAt(i)) != 1)
                return -1; 
              break;
            case 'p':
              if (Character.getType(this.search.charAt(i)) == 15)
                return -1; 
              break;
            case '!':
              s = Character.getType(this.search.charAt(i));
              switch (s) {
                case 20:
                case 21:
                case 22:
                case 23:
                case 24:
                  break;
              } 
              return -1;
            case 'x':
              s = ((this.search.charAt(i) >= '0' && this.search.charAt(i) <= '9') || (this.search.charAt(i) >= 'a' && this.search.charAt(i) <= 'f') || (this.search.charAt(i) >= 'A' && this.search.charAt(i) <= 'F')) ? 1 : 0;
              if (s == 0)
                return -1; 
              break;
            case 'j':
              if (!Character.isJavaIdentifierStart(this.search.charAt(i)))
                return -1; 
              break;
            case 'k':
              if (!Character.isJavaIdentifierPart(this.search.charAt(i)))
                return -1; 
              break;
            default:
              internalError("Bad posix class");
              break;
          } 
          i++;
          break;
        case '[':
          if (this.search.isEnd(i))
            return -1; 
          s = this.search.charAt(i);
          i1 = ((this.matchFlags & true) != 0) ? 1 : 0;
          i3 = j + 3;
          i5 = i3 + c2 * '\002';
          i7 = 0;
          i8 = i3;
          while (i7 == 0 && i8 < i5) {
            char c4 = arrayOfChar[i8++];
            char c5 = arrayOfChar[i8++];
            i7 = (compareChars(s, c4, i1) >= 0 && compareChars(s, c5, i1) <= 0) ? 1 : 0;
          } 
          if (i7 == 0)
            return -1; 
          i++;
          break;
        case '|':
          if (arrayOfChar[k + 0] != '|') {
            j += 3;
            continue;
          } 
          do {
            if ((m = matchNodes(j + 3, 65536, i)) != -1)
              return m; 
            s = (short)arrayOfChar[j + 2];
            j += s;
          } while (s != 0 && arrayOfChar[j + 0] == '|');
          return -1;
        case 'G':
        case 'N':
          break;
        case 'E':
          setParenEnd(0, i);
          return i;
        default:
          internalError("Invalid opcode '" + c1 + "'");
          break;
      } 
    } 
    internalError("Corrupt program");
    return -1;
  }
  
  protected boolean matchAt(int paramInt) {
    this.start0 = -1;
    this.end0 = -1;
    this.start1 = -1;
    this.end1 = -1;
    this.start2 = -1;
    this.end2 = -1;
    this.startn = null;
    this.endn = null;
    this.parenCount = 1;
    setParenStart(0, paramInt);
    if ((this.program.flags & true) != 0) {
      this.startBackref = new int[this.maxParen];
      this.endBackref = new int[this.maxParen];
    } 
    int i;
    if ((i = matchNodes(0, 65536, paramInt)) != -1) {
      setParenEnd(0, i);
      return true;
    } 
    this.parenCount = 0;
    return false;
  }
  
  public boolean match(String paramString, int paramInt) { return match(new StringCharacterIterator(paramString), paramInt); }
  
  public boolean match(CharacterIterator paramCharacterIterator, int paramInt) {
    if (this.program == null)
      internalError("No RE program to run!"); 
    this.search = paramCharacterIterator;
    if (this.program.prefix == null) {
      while (!paramCharacterIterator.isEnd(paramInt - 1)) {
        if (matchAt(paramInt))
          return true; 
        paramInt++;
      } 
      return false;
    } 
    boolean bool = ((this.matchFlags & true) != 0);
    char[] arrayOfChar = this.program.prefix;
    while (!paramCharacterIterator.isEnd(paramInt + arrayOfChar.length - 1)) {
      boolean bool1;
      int i = paramInt;
      byte b = 0;
      do {
        bool1 = (compareChars(paramCharacterIterator.charAt(i++), arrayOfChar[b++], bool) == 0) ? 1 : 0;
      } while (bool1 && b < arrayOfChar.length);
      if (b == arrayOfChar.length && matchAt(paramInt))
        return true; 
      paramInt++;
    } 
    return false;
  }
  
  public boolean match(String paramString) { return match(paramString, 0); }
  
  public String[] split(String paramString) {
    Vector vector = new Vector();
    int i = 0;
    int j = paramString.length();
    while (i < j && match(paramString, i)) {
      int k = getParenStart(0);
      int m = getParenEnd(0);
      if (m == i) {
        vector.addElement(paramString.substring(i, k + 1));
        m++;
      } else {
        vector.addElement(paramString.substring(i, k));
      } 
      i = m;
    } 
    String str = paramString.substring(i);
    if (str.length() != 0)
      vector.addElement(str); 
    String[] arrayOfString = new String[vector.size()];
    vector.copyInto(arrayOfString);
    return arrayOfString;
  }
  
  public String subst(String paramString1, String paramString2) { return subst(paramString1, paramString2, 0); }
  
  public String subst(String paramString1, String paramString2, int paramInt) {
    StringBuffer stringBuffer = new StringBuffer();
    int i = 0;
    int j = paramString1.length();
    while (i < j && match(paramString1, i)) {
      stringBuffer.append(paramString1.substring(i, getParenStart(0)));
      if ((paramInt & 0x2) != 0) {
        int m = 0;
        int n = -2;
        int i1 = paramString2.length();
        boolean bool = false;
        while ((m = paramString2.indexOf("$", m)) >= 0) {
          if ((m == 0 || paramString2.charAt(m - 1) != '\\') && m + 1 < i1) {
            char c = paramString2.charAt(m + 1);
            if (c >= '0' && c <= '9') {
              if (!bool) {
                stringBuffer.append(paramString2.substring(0, m));
                bool = true;
              } else {
                stringBuffer.append(paramString2.substring(n + 2, m));
              } 
              stringBuffer.append(getParen(c - '0'));
              n = m;
            } 
          } 
          m++;
        } 
        stringBuffer.append(paramString2.substring(n + 2, i1));
      } else {
        stringBuffer.append(paramString2);
      } 
      int k = getParenEnd(0);
      if (k == i)
        k++; 
      i = k;
      if ((paramInt & true) != 0)
        break; 
    } 
    if (i < j)
      stringBuffer.append(paramString1.substring(i)); 
    return stringBuffer.toString();
  }
  
  public String[] grep(Object[] paramArrayOfObject) {
    Vector vector = new Vector();
    for (byte b = 0; b < paramArrayOfObject.length; b++) {
      String str = paramArrayOfObject[b].toString();
      if (match(str))
        vector.addElement(str); 
    } 
    String[] arrayOfString = new String[vector.size()];
    vector.copyInto(arrayOfString);
    return arrayOfString;
  }
  
  private boolean isNewline(int paramInt) {
    char c = this.search.charAt(paramInt);
    return (c == '\n' || c == '\r' || c == '' || c == ' ' || c == ' ');
  }
  
  private int compareChars(char paramChar1, char paramChar2, boolean paramBoolean) {
    if (paramBoolean) {
      paramChar1 = Character.toLowerCase(paramChar1);
      paramChar2 = Character.toLowerCase(paramChar2);
    } 
    return paramChar1 - paramChar2;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\regexp\internal\RE.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */