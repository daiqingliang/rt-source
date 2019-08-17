package java.util.regex;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Spliterators;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import sun.text.Normalizer;

public final class Pattern implements Serializable {
  public static final int UNIX_LINES = 1;
  
  public static final int CASE_INSENSITIVE = 2;
  
  public static final int COMMENTS = 4;
  
  public static final int MULTILINE = 8;
  
  public static final int LITERAL = 16;
  
  public static final int DOTALL = 32;
  
  public static final int UNICODE_CASE = 64;
  
  public static final int CANON_EQ = 128;
  
  public static final int UNICODE_CHARACTER_CLASS = 256;
  
  private static final long serialVersionUID = 5073258162644648461L;
  
  private String pattern;
  
  private int flags;
  
  private String normalizedPattern;
  
  Node root;
  
  Node matchRoot;
  
  int[] buffer;
  
  GroupHead[] groupNodes;
  
  private int[] temp;
  
  int capturingGroupCount;
  
  int localCount;
  
  private int cursor;
  
  private int patternLength;
  
  private boolean hasSupplementary;
  
  static final int MAX_REPS = 2147483647;
  
  static final int GREEDY = 0;
  
  static final int LAZY = 1;
  
  static final int POSSESSIVE = 2;
  
  static final int INDEPENDENT = 3;
  
  static Node lookbehindEnd = new Node() {
      boolean match(Matcher param1Matcher, int param1Int, CharSequence param1CharSequence) { return (param1Int == param1Matcher.lookbehindTo); }
    };
  
  static Node accept = new Node();
  
  static Node lastAccept = new LastNode();
  
  public static Pattern compile(String paramString) { return new Pattern(paramString, 0); }
  
  public static Pattern compile(String paramString, int paramInt) { return new Pattern(paramString, paramInt); }
  
  public String pattern() { return this.pattern; }
  
  public String toString() { return this.pattern; }
  
  public Matcher matcher(CharSequence paramCharSequence) {
    if (!this.compiled)
      synchronized (this) {
        if (!this.compiled)
          compile(); 
      }  
    return new Matcher(this, paramCharSequence);
  }
  
  public int flags() { return this.flags; }
  
  public static boolean matches(String paramString, CharSequence paramCharSequence) {
    Pattern pattern1 = compile(paramString);
    Matcher matcher = pattern1.matcher(paramCharSequence);
    return matcher.matches();
  }
  
  public String[] split(CharSequence paramCharSequence, int paramInt) {
    int i = 0;
    boolean bool = (paramInt > 0) ? 1 : 0;
    ArrayList arrayList = new ArrayList();
    Matcher matcher = matcher(paramCharSequence);
    while (matcher.find()) {
      if (!bool || arrayList.size() < paramInt - 1) {
        if (!i && i == matcher.start() && matcher.start() == matcher.end())
          continue; 
        String str = paramCharSequence.subSequence(i, matcher.start()).toString();
        arrayList.add(str);
        i = matcher.end();
        continue;
      } 
      if (arrayList.size() == paramInt - 1) {
        String str = paramCharSequence.subSequence(i, paramCharSequence.length()).toString();
        arrayList.add(str);
        i = matcher.end();
      } 
    } 
    if (i == 0)
      return new String[] { paramCharSequence.toString() }; 
    if (!bool || arrayList.size() < paramInt)
      arrayList.add(paramCharSequence.subSequence(i, paramCharSequence.length()).toString()); 
    int j = arrayList.size();
    if (paramInt == 0)
      while (j > 0 && ((String)arrayList.get(j - 1)).equals(""))
        j--;  
    String[] arrayOfString = new String[j];
    return (String[])arrayList.subList(0, j).toArray(arrayOfString);
  }
  
  public String[] split(CharSequence paramCharSequence) { return split(paramCharSequence, 0); }
  
  public static String quote(String paramString) {
    int i = paramString.indexOf("\\E");
    if (i == -1)
      return "\\Q" + paramString + "\\E"; 
    StringBuilder stringBuilder = new StringBuilder(paramString.length() * 2);
    stringBuilder.append("\\Q");
    i = 0;
    int j = 0;
    while ((i = paramString.indexOf("\\E", j)) != -1) {
      stringBuilder.append(paramString.substring(j, i));
      j = i + 2;
      stringBuilder.append("\\E\\\\E\\Q");
    } 
    stringBuilder.append(paramString.substring(j, paramString.length()));
    stringBuilder.append("\\E");
    return stringBuilder.toString();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    this.capturingGroupCount = 1;
    this.localCount = 0;
    this.compiled = false;
    if (this.pattern.length() == 0) {
      this.root = new Start(lastAccept);
      this.matchRoot = lastAccept;
      this.compiled = true;
    } 
  }
  
  private Pattern(String paramString, int paramInt) {
    this.pattern = paramString;
    this.flags = paramInt;
    if ((this.flags & 0x100) != 0)
      this.flags |= 0x40; 
    this.capturingGroupCount = 1;
    this.localCount = 0;
    if (this.pattern.length() > 0) {
      compile();
    } else {
      this.root = new Start(lastAccept);
      this.matchRoot = lastAccept;
    } 
  }
  
  private void normalize() {
    boolean bool = false;
    int i = -1;
    this.normalizedPattern = Normalizer.normalize(this.pattern, Normalizer.Form.NFD);
    this.patternLength = this.normalizedPattern.length();
    StringBuilder stringBuilder = new StringBuilder(this.patternLength);
    int j;
    for (j = 0; j < this.patternLength; j += Character.charCount(k)) {
      int k = this.normalizedPattern.codePointAt(j);
      if (Character.getType(k) == 6 && i != -1) {
        StringBuilder stringBuilder1 = new StringBuilder();
        stringBuilder1.appendCodePoint(i);
        stringBuilder1.appendCodePoint(k);
        while (Character.getType(k) == 6) {
          j += Character.charCount(k);
          if (j >= this.patternLength)
            break; 
          k = this.normalizedPattern.codePointAt(j);
          stringBuilder1.appendCodePoint(k);
        } 
        String str = produceEquivalentAlternation(stringBuilder1.toString());
        stringBuilder.setLength(stringBuilder.length() - Character.charCount(i));
        stringBuilder.append("(?:").append(str).append(")");
      } else if (k == 91 && i != 92) {
        j = normalizeCharClass(stringBuilder, j);
      } else {
        stringBuilder.appendCodePoint(k);
      } 
      i = k;
    } 
    this.normalizedPattern = stringBuilder.toString();
  }
  
  private int normalizeCharClass(StringBuilder paramStringBuilder, int paramInt) {
    String str;
    StringBuilder stringBuilder1 = new StringBuilder();
    StringBuilder stringBuilder2 = null;
    int i = -1;
    if (++paramInt == this.normalizedPattern.length())
      throw error("Unclosed character class"); 
    stringBuilder1.append("[");
    while (true) {
      int j = this.normalizedPattern.codePointAt(paramInt);
      if (j == 93 && i != 92) {
        stringBuilder1.append((char)j);
        break;
      } 
      if (Character.getType(j) == 6) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.appendCodePoint(i);
        while (Character.getType(j) == 6) {
          stringBuilder.appendCodePoint(j);
          paramInt += Character.charCount(j);
          if (paramInt >= this.normalizedPattern.length())
            break; 
          j = this.normalizedPattern.codePointAt(paramInt);
        } 
        String str1 = produceEquivalentAlternation(stringBuilder.toString());
        stringBuilder1.setLength(stringBuilder1.length() - Character.charCount(i));
        if (stringBuilder2 == null)
          stringBuilder2 = new StringBuilder(); 
        stringBuilder2.append('|');
        stringBuilder2.append(str1);
      } else {
        stringBuilder1.appendCodePoint(j);
        paramInt++;
      } 
      if (paramInt == this.normalizedPattern.length())
        throw error("Unclosed character class"); 
      i = j;
    } 
    if (stringBuilder2 != null) {
      str = "(?:" + stringBuilder1.toString() + stringBuilder2.toString() + ")";
    } else {
      str = stringBuilder1.toString();
    } 
    paramStringBuilder.append(str);
    return paramInt;
  }
  
  private String produceEquivalentAlternation(String paramString) {
    int i = countChars(paramString, 0, 1);
    if (paramString.length() == i)
      return paramString; 
    String str1 = paramString.substring(0, i);
    String str2 = paramString.substring(i);
    String[] arrayOfString = producePermutations(str2);
    StringBuilder stringBuilder = new StringBuilder(paramString);
    for (byte b = 0; b < arrayOfString.length; b++) {
      String str = str1 + arrayOfString[b];
      if (b)
        stringBuilder.append("|" + str); 
      str = composeOneStep(str);
      if (str != null)
        stringBuilder.append("|" + produceEquivalentAlternation(str)); 
    } 
    return stringBuilder.toString();
  }
  
  private String[] producePermutations(String paramString) {
    if (paramString.length() == countChars(paramString, 0, 1))
      return new String[] { paramString }; 
    if (paramString.length() == countChars(paramString, 0, 2)) {
      int m = Character.codePointAt(paramString, 0);
      int n = Character.codePointAt(paramString, Character.charCount(m));
      if (getClass(n) == getClass(m))
        return new String[] { paramString }; 
      String[] arrayOfString = new String[2];
      arrayOfString[0] = paramString;
      StringBuilder stringBuilder = new StringBuilder(2);
      stringBuilder.appendCodePoint(n);
      stringBuilder.appendCodePoint(m);
      arrayOfString[1] = stringBuilder.toString();
      return arrayOfString;
    } 
    byte b = 1;
    int i = countCodePoints(paramString);
    for (byte b1 = 1; b1 < i; b1++)
      b *= (b1 + true); 
    String[] arrayOfString1 = new String[b];
    int[] arrayOfInt = new int[i];
    byte b2 = 0;
    int j = 0;
    while (b2 < i) {
      int m = Character.codePointAt(paramString, j);
      arrayOfInt[b2] = getClass(m);
      j += Character.charCount(m);
      b2++;
    } 
    b2 = 0;
    byte b3 = 0;
    int k;
    for (k = 0; b3 < i; k += j) {
      j = countChars(paramString, k, 1);
      boolean bool = false;
      for (byte b4 = b3 - true; b4; b4--) {
        if (arrayOfInt[b4] == arrayOfInt[b3])
          break label37; 
      } 
      StringBuilder stringBuilder = new StringBuilder(paramString);
      String str1 = stringBuilder.delete(k, k + j).toString();
      String[] arrayOfString = producePermutations(str1);
      String str2 = paramString.substring(k, k + j);
      byte b5;
      label37: for (b5 = 0; b5 < arrayOfString.length; b5++)
        arrayOfString1[b2++] = str2 + arrayOfString[b5]; 
      b3++;
    } 
    String[] arrayOfString2 = new String[b2];
    for (k = 0; k < b2; k++)
      arrayOfString2[k] = arrayOfString1[k]; 
    return arrayOfString2;
  }
  
  private int getClass(int paramInt) { return Normalizer.getCombiningClass(paramInt); }
  
  private String composeOneStep(String paramString) {
    int i = countChars(paramString, 0, 2);
    String str1 = paramString.substring(0, i);
    String str2 = Normalizer.normalize(str1, Normalizer.Form.NFC);
    if (str2.equals(str1))
      return null; 
    String str3 = paramString.substring(i);
    return str2 + str3;
  }
  
  private void RemoveQEQuoting() {
    int i = this.patternLength;
    int j = 0;
    while (j < i - 1) {
      if (this.temp[j] != 92) {
        j++;
        continue;
      } 
      if (this.temp[j + 1] != 81)
        j += 2; 
    } 
    if (j >= i - 1)
      return; 
    int k = j;
    j += 2;
    int[] arrayOfInt = new int[k + 3 * (i - j) + 2];
    System.arraycopy(this.temp, 0, arrayOfInt, 0, k);
    boolean bool1 = true;
    boolean bool2;
    for (bool2 = true; j < i; bool2 = false) {
      int m = this.temp[j++];
      if (!ASCII.isAscii(m) || ASCII.isAlpha(m)) {
        arrayOfInt[k++] = m;
      } else if (ASCII.isDigit(m)) {
        if (bool2) {
          arrayOfInt[k++] = 92;
          arrayOfInt[k++] = 120;
          arrayOfInt[k++] = 51;
        } 
        arrayOfInt[k++] = m;
      } else if (m != 92) {
        if (bool1)
          arrayOfInt[k++] = 92; 
        arrayOfInt[k++] = m;
      } else if (bool1) {
        if (this.temp[j] == 69) {
          j++;
          bool1 = false;
        } else {
          arrayOfInt[k++] = 92;
          arrayOfInt[k++] = 92;
        } 
      } else {
        if (this.temp[j] == 81) {
          j++;
          bool1 = true;
          bool2 = true;
          continue;
        } 
        arrayOfInt[k++] = m;
        if (j != i)
          arrayOfInt[k++] = this.temp[j++]; 
      } 
    } 
    this.patternLength = k;
    this.temp = Arrays.copyOf(arrayOfInt, k + 2);
  }
  
  private void compile() {
    if (has(128) && !has(16)) {
      normalize();
    } else {
      this.normalizedPattern = this.pattern;
    } 
    this.patternLength = this.normalizedPattern.length();
    this.temp = new int[this.patternLength + 2];
    this.hasSupplementary = false;
    byte b = 0;
    for (int i = 0; i < this.patternLength; i += Character.charCount(j)) {
      int j = this.normalizedPattern.codePointAt(i);
      if (isSupplementary(j))
        this.hasSupplementary = true; 
      this.temp[b++] = j;
    } 
    this.patternLength = b;
    if (!has(16))
      RemoveQEQuoting(); 
    this.buffer = new int[32];
    this.groupNodes = new GroupHead[10];
    this.namedGroups = null;
    if (has(16)) {
      this.matchRoot = newSlice(this.temp, this.patternLength, this.hasSupplementary);
      this.matchRoot.next = lastAccept;
    } else {
      this.matchRoot = expr(lastAccept);
      if (this.patternLength != this.cursor) {
        if (peek() == 41)
          throw error("Unmatched closing ')'"); 
        throw error("Unexpected internal error");
      } 
    } 
    if (this.matchRoot instanceof Slice) {
      this.root = BnM.optimize(this.matchRoot);
      if (this.root == this.matchRoot)
        this.root = this.hasSupplementary ? new StartS(this.matchRoot) : new Start(this.matchRoot); 
    } else if (this.matchRoot instanceof Begin || this.matchRoot instanceof First) {
      this.root = this.matchRoot;
    } else {
      this.root = this.hasSupplementary ? new StartS(this.matchRoot) : new Start(this.matchRoot);
    } 
    this.temp = null;
    this.buffer = null;
    this.groupNodes = null;
    this.patternLength = 0;
    this.compiled = true;
  }
  
  Map<String, Integer> namedGroups() {
    if (this.namedGroups == null)
      this.namedGroups = new HashMap(2); 
    return this.namedGroups;
  }
  
  private static void printObjectTree(Node paramNode) {
    while (paramNode != null) {
      if (paramNode instanceof Prolog) {
        System.out.println(paramNode);
        printObjectTree(((Prolog)paramNode).loop);
        System.out.println("**** end contents prolog loop");
      } else if (paramNode instanceof Loop) {
        System.out.println(paramNode);
        printObjectTree(((Loop)paramNode).body);
        System.out.println("**** end contents Loop body");
      } else if (paramNode instanceof Curly) {
        System.out.println(paramNode);
        printObjectTree(((Curly)paramNode).atom);
        System.out.println("**** end contents Curly body");
      } else if (paramNode instanceof GroupCurly) {
        System.out.println(paramNode);
        printObjectTree(((GroupCurly)paramNode).atom);
        System.out.println("**** end contents GroupCurly body");
      } else {
        if (paramNode instanceof GroupTail) {
          System.out.println(paramNode);
          System.out.println("Tail next is " + paramNode.next);
          return;
        } 
        System.out.println(paramNode);
      } 
      paramNode = paramNode.next;
      if (paramNode != null)
        System.out.println("->next:"); 
      if (paramNode == accept) {
        System.out.println("Accept Node");
        paramNode = null;
      } 
    } 
  }
  
  private boolean has(int paramInt) { return ((this.flags & paramInt) != 0); }
  
  private void accept(int paramInt, String paramString) {
    int i = this.temp[this.cursor++];
    if (has(4))
      i = parsePastWhitespace(i); 
    if (paramInt != i)
      throw error(paramString); 
  }
  
  private void mark(int paramInt) { this.temp[this.patternLength] = paramInt; }
  
  private int peek() {
    int i = this.temp[this.cursor];
    if (has(4))
      i = peekPastWhitespace(i); 
    return i;
  }
  
  private int read() {
    int i = this.temp[this.cursor++];
    if (has(4))
      i = parsePastWhitespace(i); 
    return i;
  }
  
  private int readEscaped() { return this.temp[this.cursor++]; }
  
  private int next() {
    int i = this.temp[++this.cursor];
    if (has(4))
      i = peekPastWhitespace(i); 
    return i;
  }
  
  private int nextEscaped() { return this.temp[++this.cursor]; }
  
  private int peekPastWhitespace(int paramInt) {
    while (ASCII.isSpace(paramInt) || paramInt == 35) {
      while (ASCII.isSpace(paramInt))
        paramInt = this.temp[++this.cursor]; 
      if (paramInt == 35)
        paramInt = peekPastLine(); 
    } 
    return paramInt;
  }
  
  private int parsePastWhitespace(int paramInt) {
    while (ASCII.isSpace(paramInt) || paramInt == 35) {
      while (ASCII.isSpace(paramInt))
        paramInt = this.temp[this.cursor++]; 
      if (paramInt == 35)
        paramInt = parsePastLine(); 
    } 
    return paramInt;
  }
  
  private int parsePastLine() {
    int i;
    for (i = this.temp[this.cursor++]; i != 0 && !isLineSeparator(i); i = this.temp[this.cursor++]);
    return i;
  }
  
  private int peekPastLine() {
    int i;
    for (i = this.temp[++this.cursor]; i != 0 && !isLineSeparator(i); i = this.temp[++this.cursor]);
    return i;
  }
  
  private boolean isLineSeparator(int paramInt) { return has(1) ? ((paramInt == 10)) : ((paramInt == 10 || paramInt == 13 || (paramInt | true) == 8233 || paramInt == 133)); }
  
  private int skip() {
    int i = this.cursor;
    int j = this.temp[i + 1];
    this.cursor = i + 2;
    return j;
  }
  
  private void unread() { this.cursor--; }
  
  private PatternSyntaxException error(String paramString) { return new PatternSyntaxException(paramString, this.normalizedPattern, this.cursor - 1); }
  
  private boolean findSupplementary(int paramInt1, int paramInt2) {
    for (int i = paramInt1; i < paramInt2; i++) {
      if (isSupplementary(this.temp[i]))
        return true; 
    } 
    return false;
  }
  
  private static final boolean isSupplementary(int paramInt) { return (paramInt >= 65536 || Character.isSurrogate((char)paramInt)); }
  
  private Node expr(Node paramNode) {
    Node node1 = null;
    Node node2 = null;
    Branch branch = null;
    BranchConn branchConn = null;
    while (true) {
      Node node3 = sequence(paramNode);
      Node node4 = this.root;
      if (node1 == null) {
        node1 = node3;
        node2 = node4;
      } else {
        if (branchConn == null) {
          branchConn = new BranchConn();
          branchConn.next = paramNode;
        } 
        if (node3 == paramNode) {
          node3 = null;
        } else {
          node4.next = branchConn;
        } 
        if (node1 == branch) {
          branch.add(node3);
        } else {
          if (node1 == paramNode) {
            node1 = null;
          } else {
            node2.next = branchConn;
          } 
          node1 = branch = new Branch(node1, node3, branchConn);
        } 
      } 
      if (peek() != 124)
        return node1; 
      next();
    } 
  }
  
  private Node sequence(Node paramNode) {
    Node node1 = null;
    Node node2 = null;
    Node node3 = null;
    while (true) {
      int i = peek();
      switch (i) {
        case 40:
          node3 = group0();
          if (node3 == null)
            continue; 
          if (node1 == null) {
            node1 = node3;
          } else {
            node2.next = node3;
          } 
          node2 = this.root;
          continue;
        case 91:
          node3 = clazz(true);
          break;
        case 92:
          i = nextEscaped();
          if (i == 112 || i == 80) {
            boolean bool1 = true;
            boolean bool2 = (i == 80);
            i = next();
            if (i != 123) {
              unread();
            } else {
              bool1 = false;
            } 
            node3 = family(bool1, bool2);
            break;
          } 
          unread();
          node3 = atom();
          break;
        case 94:
          next();
          if (has(8)) {
            if (has(1)) {
              node3 = new UnixCaret();
              break;
            } 
            node3 = new Caret();
            break;
          } 
          node3 = new Begin();
          break;
        case 36:
          next();
          if (has(1)) {
            node3 = new UnixDollar(has(8));
            break;
          } 
          node3 = new Dollar(has(8));
          break;
        case 46:
          next();
          if (has(32)) {
            node3 = new All();
            break;
          } 
          if (has(1)) {
            node3 = new UnixDot();
            break;
          } 
          node3 = new Dot();
          break;
        case 41:
        case 124:
          break;
        case 93:
        case 125:
          node3 = atom();
          break;
        case 42:
        case 43:
        case 63:
          next();
          throw error("Dangling meta character '" + (char)i + "'");
        case 0:
          if (this.cursor >= this.patternLength)
            break; 
        default:
          node3 = atom();
          break;
      } 
      node3 = closure(node3);
      if (node1 == null) {
        node1 = node2 = node3;
        continue;
      } 
      node2.next = node3;
      node2 = node3;
    } 
    if (node1 == null)
      return paramNode; 
    node2.next = paramNode;
    this.root = node2;
    return node1;
  }
  
  private Node atom() {
    byte b = 0;
    int i = -1;
    boolean bool = false;
    int j;
    for (j = peek();; j = next()) {
      switch (j) {
        case 42:
        case 43:
        case 63:
        case 123:
          if (b > 1) {
            this.cursor = i;
            b--;
          } 
          break;
        case 36:
        case 40:
        case 41:
        case 46:
        case 91:
        case 94:
        case 124:
          break;
        case 92:
          j = nextEscaped();
          if (j == 112 || j == 80) {
            if (b > 0) {
              unread();
              break;
            } 
            boolean bool1 = (j == 80);
            boolean bool2 = true;
            j = next();
            if (j != 123) {
              unread();
            } else {
              bool2 = false;
            } 
            return family(bool2, bool1);
          } 
          unread();
          i = this.cursor;
          j = escape(false, (b == 0), false);
          if (j >= 0) {
            append(j, b);
            b++;
            if (isSupplementary(j))
              bool = true; 
            j = peek();
            continue;
          } 
          if (b == 0)
            return this.root; 
          this.cursor = i;
          break;
        case 0:
          if (this.cursor >= this.patternLength)
            break; 
          break;
      } 
      i = this.cursor;
      append(j, b);
      b++;
      if (isSupplementary(j))
        bool = true; 
    } 
    return (b == 1) ? newSingle(this.buffer[0]) : newSlice(this.buffer, b, bool);
  }
  
  private void append(int paramInt1, int paramInt2) {
    if (paramInt2 >= this.buffer.length) {
      int[] arrayOfInt = new int[paramInt2 + paramInt2];
      System.arraycopy(this.buffer, 0, arrayOfInt, 0, paramInt2);
      this.buffer = arrayOfInt;
    } 
    this.buffer[paramInt2] = paramInt1;
  }
  
  private Node ref(int paramInt) {
    for (boolean bool = false; !bool; bool = true) {
      int j;
      int i = peek();
      switch (i) {
        case 48:
        case 49:
        case 50:
        case 51:
        case 52:
        case 53:
        case 54:
        case 55:
        case 56:
        case 57:
          j = paramInt * 10 + i - 48;
          if (this.capturingGroupCount - 1 < j) {
            bool = true;
            continue;
          } 
          paramInt = j;
          read();
          continue;
      } 
    } 
    return has(2) ? new CIBackRef(paramInt, has(64)) : new BackRef(paramInt);
  }
  
  private int escape(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3) {
    int i = skip();
    switch (i) {
      case 48:
        return o();
      case 49:
      case 50:
      case 51:
      case 52:
      case 53:
      case 54:
      case 55:
      case 56:
      case 57:
        if (!paramBoolean1) {
          if (paramBoolean2)
            this.root = ref(i - 48); 
          return -1;
        } 
      case 65:
        if (!paramBoolean1) {
          if (paramBoolean2)
            this.root = new Begin(); 
          return -1;
        } 
      case 66:
        if (!paramBoolean1) {
          if (paramBoolean2)
            this.root = new Bound(Bound.NONE, has(256)); 
          return -1;
        } 
      case 67:
        throw error("Illegal/unsupported escape sequence");
      case 68:
        if (paramBoolean2)
          this.root = has(256) ? (new Utype(UnicodeProp.DIGIT)).complement() : (new Ctype(1024)).complement(); 
        return -1;
      case 69:
      case 70:
        throw error("Illegal/unsupported escape sequence");
      case 71:
        if (!paramBoolean1) {
          if (paramBoolean2)
            this.root = new LastMatch(); 
          return -1;
        } 
      case 72:
        if (paramBoolean2)
          this.root = (new HorizWS()).complement(); 
        return -1;
      case 73:
      case 74:
      case 75:
      case 76:
      case 77:
      case 78:
      case 79:
      case 80:
      case 81:
        throw error("Illegal/unsupported escape sequence");
      case 82:
        if (!paramBoolean1) {
          if (paramBoolean2)
            this.root = new LineEnding(); 
          return -1;
        } 
      case 83:
        if (paramBoolean2)
          this.root = has(256) ? (new Utype(UnicodeProp.WHITE_SPACE)).complement() : (new Ctype(2048)).complement(); 
        return -1;
      case 84:
      case 85:
        throw error("Illegal/unsupported escape sequence");
      case 86:
        if (paramBoolean2)
          this.root = (new VertWS()).complement(); 
        return -1;
      case 87:
        if (paramBoolean2)
          this.root = has(256) ? (new Utype(UnicodeProp.WORD)).complement() : (new Ctype(67328)).complement(); 
        return -1;
      case 88:
      case 89:
        throw error("Illegal/unsupported escape sequence");
      case 90:
        if (!paramBoolean1) {
          if (paramBoolean2)
            if (has(1)) {
              this.root = new UnixDollar(false);
            } else {
              this.root = new Dollar(false);
            }  
          return -1;
        } 
      case 97:
        return 7;
      case 98:
        if (!paramBoolean1) {
          if (paramBoolean2)
            this.root = new Bound(Bound.BOTH, has(256)); 
          return -1;
        } 
      case 99:
        return c();
      case 100:
        if (paramBoolean2)
          this.root = has(256) ? new Utype(UnicodeProp.DIGIT) : new Ctype(1024); 
        return -1;
      case 101:
        return 27;
      case 102:
        return 12;
      case 103:
        throw error("Illegal/unsupported escape sequence");
      case 104:
        if (paramBoolean2)
          this.root = new HorizWS(); 
        return -1;
      case 105:
      case 106:
        throw error("Illegal/unsupported escape sequence");
      case 107:
        if (!paramBoolean1) {
          if (read() != 60)
            throw error("\\k is not followed by '<' for named capturing group"); 
          String str = groupname(read());
          if (!namedGroups().containsKey(str))
            throw error("(named capturing group <" + str + "> does not exit"); 
          if (paramBoolean2)
            if (has(2)) {
              this.root = new CIBackRef(((Integer)namedGroups().get(str)).intValue(), has(64));
            } else {
              this.root = new BackRef(((Integer)namedGroups().get(str)).intValue());
            }  
          return -1;
        } 
      case 108:
      case 109:
        throw error("Illegal/unsupported escape sequence");
      case 110:
        return 10;
      case 111:
      case 112:
      case 113:
        throw error("Illegal/unsupported escape sequence");
      case 114:
        return 13;
      case 115:
        if (paramBoolean2)
          this.root = has(256) ? new Utype(UnicodeProp.WHITE_SPACE) : new Ctype(2048); 
        return -1;
      case 116:
        return 9;
      case 117:
        return u();
      case 118:
        if (paramBoolean3)
          return 11; 
        if (paramBoolean2)
          this.root = new VertWS(); 
        return -1;
      case 119:
        if (paramBoolean2)
          this.root = has(256) ? new Utype(UnicodeProp.WORD) : new Ctype(67328); 
        return -1;
      case 120:
        return x();
      case 121:
        throw error("Illegal/unsupported escape sequence");
      case 122:
        if (!paramBoolean1) {
          if (paramBoolean2)
            this.root = new End(); 
          return -1;
        } 
    } 
    return i;
  }
  
  private CharProperty clazz(boolean paramBoolean) {
    CharProperty charProperty1 = null;
    CharProperty charProperty2 = null;
    BitClass bitClass = new BitClass();
    boolean bool1 = true;
    boolean bool2 = true;
    int i;
    for (i = next();; i = peek()) {
      switch (i) {
        case 94:
          if (!bool2 || this.temp[this.cursor - 1] != 91)
            break; 
          i = next();
          bool1 = !bool1 ? 1 : 0;
          continue;
        case 91:
          bool2 = false;
          charProperty2 = clazz(true);
          if (charProperty1 == null) {
            charProperty1 = charProperty2;
          } else {
            charProperty1 = union(charProperty1, charProperty2);
          } 
          i = peek();
          continue;
        case 38:
          bool2 = false;
          i = next();
          if (i == 38) {
            i = next();
            CharProperty charProperty = null;
            while (i != 93 && i != 38) {
              if (i == 91) {
                if (charProperty == null) {
                  charProperty = clazz(true);
                } else {
                  charProperty = union(charProperty, clazz(true));
                } 
              } else {
                unread();
                charProperty = clazz(false);
              } 
              i = peek();
            } 
            if (charProperty != null)
              charProperty2 = charProperty; 
            if (charProperty1 == null) {
              if (charProperty == null)
                throw error("Bad class syntax"); 
              charProperty1 = charProperty;
              continue;
            } 
            charProperty1 = intersection(charProperty1, charProperty2);
            continue;
          } 
          unread();
          break;
        case 0:
          bool2 = false;
          if (this.cursor >= this.patternLength)
            throw error("Unclosed character class"); 
          break;
        case 93:
          bool2 = false;
          if (charProperty1 != null) {
            if (paramBoolean)
              next(); 
            return charProperty1;
          } 
          break;
        default:
          bool2 = false;
          break;
      } 
      charProperty2 = range(bitClass);
      if (bool1) {
        if (charProperty1 == null) {
          charProperty1 = charProperty2;
        } else if (charProperty1 != charProperty2) {
          charProperty1 = union(charProperty1, charProperty2);
        } 
      } else if (charProperty1 == null) {
        charProperty1 = charProperty2.complement();
      } else if (charProperty1 != charProperty2) {
        charProperty1 = setDifference(charProperty1, charProperty2);
      } 
    } 
  }
  
  private CharProperty bitsOrSingle(BitClass paramBitClass, int paramInt) { return (paramInt < 256 && (!has(2) || !has(64) || (paramInt != 255 && paramInt != 181 && paramInt != 73 && paramInt != 105 && paramInt != 83 && paramInt != 115 && paramInt != 75 && paramInt != 107 && paramInt != 197 && paramInt != 229))) ? paramBitClass.add(paramInt, flags()) : newSingle(paramInt); }
  
  private CharProperty range(BitClass paramBitClass) {
    int i = peek();
    if (i == 92) {
      i = nextEscaped();
      if (i == 112 || i == 80) {
        boolean bool1 = (i == 80);
        boolean bool2 = true;
        i = next();
        if (i != 123) {
          unread();
        } else {
          bool2 = false;
        } 
        return family(bool2, bool1);
      } 
      boolean bool = (this.temp[this.cursor + 1] == 45);
      unread();
      i = escape(true, true, bool);
      if (i == -1)
        return (CharProperty)this.root; 
    } else {
      next();
    } 
    if (i >= 0) {
      if (peek() == 45) {
        int j = this.temp[this.cursor + 1];
        if (j == 91)
          return bitsOrSingle(paramBitClass, i); 
        if (j != 93) {
          next();
          int k = peek();
          if (k == 92) {
            k = escape(true, false, true);
          } else {
            next();
          } 
          if (k < i)
            throw error("Illegal character range"); 
          return has(2) ? caseInsensitiveRangeFor(i, k) : rangeFor(i, k);
        } 
      } 
      return bitsOrSingle(paramBitClass, i);
    } 
    throw error("Unexpected character '" + (char)i + "'");
  }
  
  private CharProperty family(boolean paramBoolean1, boolean paramBoolean2) {
    String str;
    next();
    CharProperty charProperty = null;
    if (paramBoolean1) {
      int j = this.temp[this.cursor];
      if (!Character.isSupplementaryCodePoint(j)) {
        str = String.valueOf((char)j);
      } else {
        str = new String(this.temp, this.cursor, 1);
      } 
      read();
    } else {
      int j = this.cursor;
      mark(125);
      while (read() != 125);
      mark(0);
      int k = this.cursor;
      if (k > this.patternLength)
        throw error("Unclosed character family"); 
      if (j + 1 >= k)
        throw error("Empty character family"); 
      str = new String(this.temp, j, k - j - 1);
    } 
    int i = str.indexOf('=');
    if (i != -1) {
      String str1 = str.substring(i + 1);
      str = str.substring(0, i).toLowerCase(Locale.ENGLISH);
      if ("sc".equals(str) || "script".equals(str)) {
        charProperty = unicodeScriptPropertyFor(str1);
      } else if ("blk".equals(str) || "block".equals(str)) {
        charProperty = unicodeBlockPropertyFor(str1);
      } else if ("gc".equals(str) || "general_category".equals(str)) {
        charProperty = charPropertyNodeFor(str1);
      } else {
        throw error("Unknown Unicode property {name=<" + str + ">, value=<" + str1 + ">}");
      } 
    } else if (str.startsWith("In")) {
      charProperty = unicodeBlockPropertyFor(str.substring(2));
    } else if (str.startsWith("Is")) {
      str = str.substring(2);
      UnicodeProp unicodeProp = UnicodeProp.forName(str);
      if (unicodeProp != null)
        charProperty = new Utype(unicodeProp); 
      if (charProperty == null)
        charProperty = CharPropertyNames.charPropertyFor(str); 
      if (charProperty == null)
        charProperty = unicodeScriptPropertyFor(str); 
    } else {
      if (has(256)) {
        UnicodeProp unicodeProp = UnicodeProp.forPOSIXName(str);
        if (unicodeProp != null)
          charProperty = new Utype(unicodeProp); 
      } 
      if (charProperty == null)
        charProperty = charPropertyNodeFor(str); 
    } 
    if (paramBoolean2) {
      if (charProperty instanceof Category || charProperty instanceof Block)
        this.hasSupplementary = true; 
      charProperty = charProperty.complement();
    } 
    return charProperty;
  }
  
  private CharProperty unicodeScriptPropertyFor(String paramString) {
    Character.UnicodeScript unicodeScript;
    try {
      unicodeScript = Character.UnicodeScript.forName(paramString);
    } catch (IllegalArgumentException illegalArgumentException) {
      throw error("Unknown character script name {" + paramString + "}");
    } 
    return new Script(unicodeScript);
  }
  
  private CharProperty unicodeBlockPropertyFor(String paramString) {
    Character.UnicodeBlock unicodeBlock;
    try {
      unicodeBlock = Character.UnicodeBlock.forName(paramString);
    } catch (IllegalArgumentException illegalArgumentException) {
      throw error("Unknown character block name {" + paramString + "}");
    } 
    return new Block(unicodeBlock);
  }
  
  private CharProperty charPropertyNodeFor(String paramString) {
    CharProperty charProperty = CharPropertyNames.charPropertyFor(paramString);
    if (charProperty == null)
      throw error("Unknown character property name {" + paramString + "}"); 
    return charProperty;
  }
  
  private String groupname(int paramInt) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(Character.toChars(paramInt));
    while (ASCII.isLower(paramInt = read()) || ASCII.isUpper(paramInt) || ASCII.isDigit(paramInt))
      stringBuilder.append(Character.toChars(paramInt)); 
    if (stringBuilder.length() == 0)
      throw error("named capturing group has 0 length name"); 
    if (paramInt != 62)
      throw error("named capturing group is missing trailing '>'"); 
    return stringBuilder.toString();
  }
  
  private Node group0() {
    boolean bool = false;
    Node node1 = null;
    Node node2 = null;
    int i = this.flags;
    this.root = null;
    int j = next();
    if (j == 63) {
      boolean bool1;
      TreeInfo treeInfo;
      int k;
      j = skip();
      switch (j) {
        case 58:
          node1 = createGroup(true);
          node2 = this.root;
          node1.next = expr(node2);
          break;
        case 33:
        case 61:
          node1 = createGroup(true);
          node2 = this.root;
          node1.next = expr(node2);
          if (j == 61) {
            node1 = node2 = new Pos(node1);
            break;
          } 
          node1 = node2 = new Neg(node1);
          break;
        case 62:
          node1 = createGroup(true);
          node2 = this.root;
          node1.next = expr(node2);
          node1 = node2 = new Ques(node1, 3);
          break;
        case 60:
          j = read();
          if (ASCII.isLower(j) || ASCII.isUpper(j)) {
            String str = groupname(j);
            if (namedGroups().containsKey(str))
              throw error("Named capturing group <" + str + "> is already defined"); 
            bool = true;
            node1 = createGroup(false);
            node2 = this.root;
            namedGroups().put(str, Integer.valueOf(this.capturingGroupCount - 1));
            node1.next = expr(node2);
            break;
          } 
          k = this.cursor;
          node1 = createGroup(true);
          node2 = this.root;
          node1.next = expr(node2);
          node2.next = lookbehindEnd;
          treeInfo = new TreeInfo();
          node1.study(treeInfo);
          if (!treeInfo.maxValid)
            throw error("Look-behind group does not have an obvious maximum length"); 
          bool1 = findSupplementary(k, this.patternLength);
          if (j == 61) {
            node1 = node2 = bool1 ? new BehindS(node1, treeInfo.maxLength, treeInfo.minLength) : new Behind(node1, treeInfo.maxLength, treeInfo.minLength);
            break;
          } 
          if (j == 33) {
            node1 = node2 = bool1 ? new NotBehindS(node1, treeInfo.maxLength, treeInfo.minLength) : new NotBehind(node1, treeInfo.maxLength, treeInfo.minLength);
            break;
          } 
          throw error("Unknown look-behind group");
        case 36:
        case 64:
          throw error("Unknown group type");
        default:
          unread();
          addFlag();
          j = read();
          if (j == 41)
            return null; 
          if (j != 58)
            throw error("Unknown inline modifier"); 
          node1 = createGroup(true);
          node2 = this.root;
          node1.next = expr(node2);
          break;
      } 
    } else {
      bool = true;
      node1 = createGroup(false);
      node2 = this.root;
      node1.next = expr(node2);
    } 
    accept(41, "Unclosed group");
    this.flags = i;
    Node node3 = closure(node1);
    if (node3 == node1) {
      this.root = node2;
      return node3;
    } 
    if (node1 == node2) {
      this.root = node3;
      return node3;
    } 
    if (node3 instanceof Ques) {
      Ques ques = (Ques)node3;
      if (ques.type == 2) {
        this.root = node3;
        return node3;
      } 
      node2.next = new BranchConn();
      node2 = node2.next;
      if (ques.type == 0) {
        node1 = new Branch(node1, null, node2);
      } else {
        node1 = new Branch(null, node1, node2);
      } 
      this.root = node2;
      return node1;
    } 
    if (node3 instanceof Curly) {
      LazyLoop lazyLoop;
      Curly curly = (Curly)node3;
      if (curly.type == 2) {
        this.root = node3;
        return node3;
      } 
      TreeInfo treeInfo = new TreeInfo();
      if (node1.study(treeInfo)) {
        GroupTail groupTail = (GroupTail)node2;
        return this.root = new GroupCurly(node1.next, curly.cmin, curly.cmax, curly.type, ((GroupTail)node2).localIndex, ((GroupTail)node2).groupIndex, bool);
      } 
      int k = ((GroupHead)node1).localIndex;
      if (curly.type == 0) {
        lazyLoop = new Loop(this.localCount, k);
      } else {
        lazyLoop = new LazyLoop(this.localCount, k);
      } 
      Prolog prolog = new Prolog(lazyLoop);
      this.localCount++;
      lazyLoop.cmin = curly.cmin;
      lazyLoop.cmax = curly.cmax;
      lazyLoop.body = node1;
      node2.next = lazyLoop;
      this.root = lazyLoop;
      return prolog;
    } 
    throw error("Internal logic error");
  }
  
  private Node createGroup(boolean paramBoolean) {
    int i = this.localCount++;
    int j = 0;
    if (!paramBoolean)
      j = this.capturingGroupCount++; 
    GroupHead groupHead = new GroupHead(i);
    this.root = new GroupTail(i, j);
    if (!paramBoolean && j < 10)
      this.groupNodes[j] = groupHead; 
    return groupHead;
  }
  
  private void addFlag() {
    for (int i = peek();; i = next()) {
      switch (i) {
        case 105:
          this.flags |= 0x2;
          break;
        case 109:
          this.flags |= 0x8;
          break;
        case 115:
          this.flags |= 0x20;
          break;
        case 100:
          this.flags |= 0x1;
          break;
        case 117:
          this.flags |= 0x40;
          break;
        case 99:
          this.flags |= 0x80;
          break;
        case 120:
          this.flags |= 0x4;
          break;
        case 85:
          this.flags |= 0x140;
          break;
        case 45:
          i = next();
          subFlag();
        default:
          return;
      } 
    } 
  }
  
  private void subFlag() {
    for (int i = peek();; i = next()) {
      switch (i) {
        case 105:
          this.flags &= 0xFFFFFFFD;
          break;
        case 109:
          this.flags &= 0xFFFFFFF7;
          break;
        case 115:
          this.flags &= 0xFFFFFFDF;
          break;
        case 100:
          this.flags &= 0xFFFFFFFE;
          break;
        case 117:
          this.flags &= 0xFFFFFFBF;
          break;
        case 99:
          this.flags &= 0xFFFFFF7F;
          break;
        case 120:
          this.flags &= 0xFFFFFFFB;
          break;
        case 85:
          this.flags &= 0xFFFFFEBF;
        default:
          return;
      } 
    } 
  }
  
  private Node closure(Node paramNode) {
    int i = peek();
    switch (i) {
      case 63:
        i = next();
        if (i == 63) {
          next();
          return new Ques(paramNode, 1);
        } 
        if (i == 43) {
          next();
          return new Ques(paramNode, 2);
        } 
        return new Ques(paramNode, 0);
      case 42:
        i = next();
        if (i == 63) {
          next();
          return new Curly(paramNode, 0, 2147483647, 1);
        } 
        if (i == 43) {
          next();
          return new Curly(paramNode, 0, 2147483647, 2);
        } 
        return new Curly(paramNode, 0, 2147483647, 0);
      case 43:
        i = next();
        if (i == 63) {
          next();
          return new Curly(paramNode, 1, 2147483647, 1);
        } 
        if (i == 43) {
          next();
          return new Curly(paramNode, 1, 2147483647, 2);
        } 
        return new Curly(paramNode, 1, 2147483647, 0);
      case 123:
        i = this.temp[this.cursor + 1];
        if (ASCII.isDigit(i)) {
          Curly curly;
          skip();
          int j = 0;
          do {
            j = j * 10 + i - 48;
          } while (ASCII.isDigit(i = read()));
          int k = j;
          if (i == 44) {
            i = read();
            k = Integer.MAX_VALUE;
            if (i != 125) {
              k = 0;
              while (ASCII.isDigit(i)) {
                k = k * 10 + i - 48;
                i = read();
              } 
            } 
          } 
          if (i != 125)
            throw error("Unclosed counted closure"); 
          if ((j | k | k - j) < 0)
            throw error("Illegal repetition range"); 
          i = peek();
          if (i == 63) {
            next();
            curly = new Curly(paramNode, j, k, 1);
          } else if (i == 43) {
            next();
            curly = new Curly(paramNode, j, k, 2);
          } else {
            curly = new Curly(paramNode, j, k, 0);
          } 
          return curly;
        } 
        throw error("Illegal repetition");
    } 
    return paramNode;
  }
  
  private int c() {
    if (this.cursor < this.patternLength)
      return read() ^ 0x40; 
    throw error("Illegal control escape sequence");
  }
  
  private int o() {
    int i = read();
    if ((i - 48 | 55 - i) >= 0) {
      int j = read();
      if ((j - 48 | 55 - j) >= 0) {
        int k = read();
        if ((k - 48 | 55 - k) >= 0 && (i - 48 | 51 - i) >= 0)
          return (i - 48) * 64 + (j - 48) * 8 + k - 48; 
        unread();
        return (i - 48) * 8 + j - 48;
      } 
      unread();
      return i - 48;
    } 
    throw error("Illegal octal escape sequence");
  }
  
  private int x() {
    int i = read();
    if (ASCII.isHexDigit(i)) {
      int j = read();
      if (ASCII.isHexDigit(j))
        return ASCII.toDigit(i) * 16 + ASCII.toDigit(j); 
    } else if (i == 123 && ASCII.isHexDigit(peek())) {
      int j = 0;
      while (ASCII.isHexDigit(i = read())) {
        j = (j << 4) + ASCII.toDigit(i);
        if (j > 1114111)
          throw error("Hexadecimal codepoint is too big"); 
      } 
      if (i != 125)
        throw error("Unclosed hexadecimal escape sequence"); 
      return j;
    } 
    throw error("Illegal hexadecimal escape sequence");
  }
  
  private int cursor() { return this.cursor; }
  
  private void setcursor(int paramInt) { this.cursor = paramInt; }
  
  private int uxxxx() {
    int i = 0;
    for (byte b = 0; b < 4; b++) {
      int j = read();
      if (!ASCII.isHexDigit(j))
        throw error("Illegal Unicode escape sequence"); 
      i = i * 16 + ASCII.toDigit(j);
    } 
    return i;
  }
  
  private int u() {
    int i = uxxxx();
    if (Character.isHighSurrogate((char)i)) {
      int j = cursor();
      if (read() == 92 && read() == 117) {
        int k = uxxxx();
        if (Character.isLowSurrogate((char)k))
          return Character.toCodePoint((char)i, (char)k); 
      } 
      setcursor(j);
    } 
    return i;
  }
  
  private static final int countChars(CharSequence paramCharSequence, int paramInt1, int paramInt2) {
    if (paramInt2 == 1 && !Character.isHighSurrogate(paramCharSequence.charAt(paramInt1))) {
      assert paramInt1 >= 0 && paramInt1 < paramCharSequence.length();
      return 1;
    } 
    int i = paramCharSequence.length();
    int j = paramInt1;
    if (paramInt2 >= 0) {
      assert paramInt1 >= 0 && paramInt1 < i;
      for (byte b1 = 0; j < i && b1 < paramInt2; b1++) {
        if (Character.isHighSurrogate(paramCharSequence.charAt(j++)) && j < i && Character.isLowSurrogate(paramCharSequence.charAt(j)))
          j++; 
      } 
      return j - paramInt1;
    } 
    assert paramInt1 >= 0 && paramInt1 <= i;
    if (paramInt1 == 0)
      return 0; 
    int k = -paramInt2;
    for (byte b = 0; j > 0 && b < k; b++) {
      if (Character.isLowSurrogate(paramCharSequence.charAt(--j)) && j > 0 && Character.isHighSurrogate(paramCharSequence.charAt(j - 1)))
        j--; 
    } 
    return paramInt1 - j;
  }
  
  private static final int countCodePoints(CharSequence paramCharSequence) {
    int i = paramCharSequence.length();
    byte b1 = 0;
    byte b2 = 0;
    while (b2 < i) {
      b1++;
      if (Character.isHighSurrogate(paramCharSequence.charAt(b2++)) && b2 < i && Character.isLowSurrogate(paramCharSequence.charAt(b2)))
        b2++; 
    } 
    return b1;
  }
  
  private CharProperty newSingle(int paramInt) {
    if (has(2))
      if (has(64)) {
        int j = Character.toUpperCase(paramInt);
        int i = Character.toLowerCase(j);
        if (j != i)
          return new SingleU(i); 
      } else if (ASCII.isAscii(paramInt)) {
        int i = ASCII.toLower(paramInt);
        int j = ASCII.toUpper(paramInt);
        if (i != j)
          return new SingleI(i, j); 
      }  
    return isSupplementary(paramInt) ? new SingleS(paramInt) : new Single(paramInt);
  }
  
  private Node newSlice(int[] paramArrayOfInt, int paramInt, boolean paramBoolean) {
    int[] arrayOfInt = new int[paramInt];
    if (has(2)) {
      if (has(64)) {
        for (byte b2 = 0; b2 < paramInt; b2++)
          arrayOfInt[b2] = Character.toLowerCase(Character.toUpperCase(paramArrayOfInt[b2])); 
        return paramBoolean ? new SliceUS(arrayOfInt) : new SliceU(arrayOfInt);
      } 
      for (byte b1 = 0; b1 < paramInt; b1++)
        arrayOfInt[b1] = ASCII.toLower(paramArrayOfInt[b1]); 
      return paramBoolean ? new SliceIS(arrayOfInt) : new SliceI(arrayOfInt);
    } 
    for (byte b = 0; b < paramInt; b++)
      arrayOfInt[b] = paramArrayOfInt[b]; 
    return paramBoolean ? new SliceS(arrayOfInt) : new Slice(arrayOfInt);
  }
  
  private static boolean inRange(int paramInt1, int paramInt2, int paramInt3) { return (paramInt1 <= paramInt2 && paramInt2 <= paramInt3); }
  
  private static CharProperty rangeFor(final int lower, final int upper) { return new CharProperty() {
        boolean isSatisfiedBy(int param1Int) { return Pattern.inRange(lower, param1Int, upper); }
      }; }
  
  private CharProperty caseInsensitiveRangeFor(final int lower, final int upper) { return has(64) ? new CharProperty() {
        boolean isSatisfiedBy(int param1Int) {
          if (Pattern.inRange(lower, param1Int, upper))
            return true; 
          int i = Character.toUpperCase(param1Int);
          return (Pattern.inRange(lower, i, upper) || Pattern.inRange(lower, Character.toLowerCase(i), upper));
        }
      } : new CharProperty() {
        boolean isSatisfiedBy(int param1Int) { return (Pattern.inRange(lower, param1Int, upper) || (ASCII.isAscii(param1Int) && (Pattern.inRange(lower, ASCII.toUpper(param1Int), upper) || Pattern.inRange(lower, ASCII.toLower(param1Int), upper)))); }
      }; }
  
  private static CharProperty union(final CharProperty lhs, final CharProperty rhs) { return new CharProperty() {
        boolean isSatisfiedBy(int param1Int) { return (lhs.isSatisfiedBy(param1Int) || rhs.isSatisfiedBy(param1Int)); }
      }; }
  
  private static CharProperty intersection(final CharProperty lhs, final CharProperty rhs) { return new CharProperty() {
        boolean isSatisfiedBy(int param1Int) { return (lhs.isSatisfiedBy(param1Int) && rhs.isSatisfiedBy(param1Int)); }
      }; }
  
  private static CharProperty setDifference(final CharProperty lhs, final CharProperty rhs) { return new CharProperty() {
        boolean isSatisfiedBy(int param1Int) { return (!rhs.isSatisfiedBy(param1Int) && lhs.isSatisfiedBy(param1Int)); }
      }; }
  
  private static boolean hasBaseCharacter(Matcher paramMatcher, int paramInt, CharSequence paramCharSequence) {
    int i = !paramMatcher.transparentBounds ? paramMatcher.from : 0;
    int j = paramInt;
    while (j >= i) {
      int k = Character.codePointAt(paramCharSequence, j);
      if (Character.isLetterOrDigit(k))
        return true; 
      if (Character.getType(k) == 6) {
        j--;
        continue;
      } 
      return false;
    } 
    return false;
  }
  
  public Predicate<String> asPredicate() { return paramString -> matcher(paramString).find(); }
  
  public Stream<String> splitAsStream(final CharSequence input) {
    class MatcherIterator extends Object implements Iterator<String> {
      private final Matcher matcher;
      
      private int current;
      
      private String nextElement;
      
      private int emptyElementCount;
      
      MatcherIterator() { this.matcher = this$0.matcher(input); }
      
      public String next() {
        if (!hasNext())
          throw new NoSuchElementException(); 
        if (this.emptyElementCount == 0) {
          String str = this.nextElement;
          this.nextElement = null;
          return str;
        } 
        this.emptyElementCount--;
        return "";
      }
      
      public boolean hasNext() {
        if (this.nextElement != null || this.emptyElementCount > 0)
          return true; 
        if (this.current == input.length())
          return false; 
        while (this.matcher.find()) {
          this.nextElement = input.subSequence(this.current, this.matcher.start()).toString();
          this.current = this.matcher.end();
          if (!this.nextElement.isEmpty())
            return true; 
          if (this.current > 0)
            this.emptyElementCount++; 
        } 
        this.nextElement = input.subSequence(this.current, input.length()).toString();
        this.current = input.length();
        if (!this.nextElement.isEmpty())
          return true; 
        this.emptyElementCount = 0;
        this.nextElement = null;
        return false;
      }
    };
    return StreamSupport.stream(Spliterators.spliteratorUnknownSize(new MatcherIterator(), 272), false);
  }
  
  static final class All extends CharProperty {
    All() { super(null); }
    
    boolean isSatisfiedBy(int param1Int) { return true; }
  }
  
  static class BackRef extends Node {
    int groupIndex;
    
    BackRef(int param1Int) { this.groupIndex = param1Int + param1Int; }
    
    boolean match(Matcher param1Matcher, int param1Int, CharSequence param1CharSequence) {
      int i = param1Matcher.groups[this.groupIndex];
      int j = param1Matcher.groups[this.groupIndex + 1];
      int k = j - i;
      if (i < 0)
        return false; 
      if (param1Int + k > param1Matcher.to) {
        param1Matcher.hitEnd = true;
        return false;
      } 
      for (int m = 0; m < k; m++) {
        if (param1CharSequence.charAt(param1Int + m) != param1CharSequence.charAt(i + m))
          return false; 
      } 
      return this.next.match(param1Matcher, param1Int + k, param1CharSequence);
    }
    
    boolean study(Pattern.TreeInfo param1TreeInfo) {
      param1TreeInfo.maxValid = false;
      return this.next.study(param1TreeInfo);
    }
  }
  
  static final class Begin extends Node {
    boolean match(Matcher param1Matcher, int param1Int, CharSequence param1CharSequence) {
      int i = param1Matcher.anchoringBounds ? param1Matcher.from : 0;
      if (param1Int == i && this.next.match(param1Matcher, param1Int, param1CharSequence)) {
        param1Matcher.first = param1Int;
        param1Matcher.groups[0] = param1Int;
        param1Matcher.groups[1] = param1Matcher.last;
        return true;
      } 
      return false;
    }
  }
  
  static class Behind extends Node {
    Pattern.Node cond;
    
    int rmax;
    
    int rmin;
    
    Behind(Pattern.Node param1Node, int param1Int1, int param1Int2) {
      this.cond = param1Node;
      this.rmax = param1Int1;
      this.rmin = param1Int2;
    }
    
    boolean match(Matcher param1Matcher, int param1Int, CharSequence param1CharSequence) {
      int i = param1Matcher.from;
      boolean bool = false;
      int j = !param1Matcher.transparentBounds ? param1Matcher.from : 0;
      int k = Math.max(param1Int - this.rmax, j);
      int m = param1Matcher.lookbehindTo;
      param1Matcher.lookbehindTo = param1Int;
      if (param1Matcher.transparentBounds)
        param1Matcher.from = 0; 
      for (int n = param1Int - this.rmin; !bool && n >= k; n--)
        bool = this.cond.match(param1Matcher, n, param1CharSequence); 
      param1Matcher.from = i;
      param1Matcher.lookbehindTo = m;
      return (bool && this.next.match(param1Matcher, param1Int, param1CharSequence));
    }
  }
  
  static final class BehindS extends Behind {
    BehindS(Pattern.Node param1Node, int param1Int1, int param1Int2) { super(param1Node, param1Int1, param1Int2); }
    
    boolean match(Matcher param1Matcher, int param1Int, CharSequence param1CharSequence) {
      int i = Pattern.countChars(param1CharSequence, param1Int, -this.rmax);
      int j = Pattern.countChars(param1CharSequence, param1Int, -this.rmin);
      int k = param1Matcher.from;
      int m = !param1Matcher.transparentBounds ? param1Matcher.from : 0;
      boolean bool = false;
      int n = Math.max(param1Int - i, m);
      int i1 = param1Matcher.lookbehindTo;
      param1Matcher.lookbehindTo = param1Int;
      if (param1Matcher.transparentBounds)
        param1Matcher.from = 0; 
      int i2;
      for (i2 = param1Int - j; !bool && i2 >= n; i2 -= ((i2 > n) ? Pattern.countChars(param1CharSequence, i2, -1) : 1))
        bool = this.cond.match(param1Matcher, i2, param1CharSequence); 
      param1Matcher.from = k;
      param1Matcher.lookbehindTo = i1;
      return (bool && this.next.match(param1Matcher, param1Int, param1CharSequence));
    }
  }
  
  private static final class BitClass extends BmpCharProperty {
    final boolean[] bits = new boolean[256];
    
    BitClass() { super(null); }
    
    private BitClass(boolean[] param1ArrayOfBoolean) { super(null); }
    
    BitClass add(int param1Int1, int param1Int2) {
      assert param1Int1 >= 0 && param1Int1 <= 255;
      if ((param1Int2 & 0x2) != 0)
        if (ASCII.isAscii(param1Int1)) {
          this.bits[ASCII.toUpper(param1Int1)] = true;
          this.bits[ASCII.toLower(param1Int1)] = true;
        } else if ((param1Int2 & 0x40) != 0) {
          this.bits[Character.toLowerCase(param1Int1)] = true;
          this.bits[Character.toUpperCase(param1Int1)] = true;
        }  
      this.bits[param1Int1] = true;
      return this;
    }
    
    boolean isSatisfiedBy(int param1Int) { return (param1Int < 256 && this.bits[param1Int]); }
  }
  
  static final class Block extends CharProperty {
    final Character.UnicodeBlock block;
    
    Block(Character.UnicodeBlock param1UnicodeBlock) {
      super(null);
      this.block = param1UnicodeBlock;
    }
    
    boolean isSatisfiedBy(int param1Int) { return (this.block == Character.UnicodeBlock.of(param1Int)); }
  }
  
  private static abstract class BmpCharProperty extends CharProperty {
    private BmpCharProperty() { super(null); }
    
    boolean match(Matcher param1Matcher, int param1Int, CharSequence param1CharSequence) {
      if (param1Int < param1Matcher.to)
        return (isSatisfiedBy(param1CharSequence.charAt(param1Int)) && this.next.match(param1Matcher, param1Int + 1, param1CharSequence)); 
      param1Matcher.hitEnd = true;
      return false;
    }
  }
  
  static class BnM extends Node {
    int[] buffer;
    
    int[] lastOcc;
    
    int[] optoSft;
    
    static Pattern.Node optimize(Pattern.Node param1Node) {
      if (!(param1Node instanceof Pattern.Slice))
        return param1Node; 
      int[] arrayOfInt1 = ((Pattern.Slice)param1Node).buffer;
      int i = arrayOfInt1.length;
      if (i < 4)
        return param1Node; 
      int[] arrayOfInt2 = new int[128];
      int[] arrayOfInt3 = new int[i];
      int j;
      for (j = 0; j < i; j++)
        arrayOfInt2[arrayOfInt1[j] & 0x7F] = j + true; 
      for (j = i; j > 0; j--) {
        int k = i - 1;
        while (true) {
          if (k >= j) {
            if (arrayOfInt1[k] == arrayOfInt1[k - j]) {
              arrayOfInt3[k - 1] = j;
              k--;
              continue;
            } 
            break;
          } 
          while (k > 0)
            arrayOfInt3[--k] = j; 
          break;
        } 
      } 
      arrayOfInt3[i - 1] = 1;
      return (param1Node instanceof Pattern.SliceS) ? new Pattern.BnMS(arrayOfInt1, arrayOfInt2, arrayOfInt3, param1Node.next) : new BnM(arrayOfInt1, arrayOfInt2, arrayOfInt3, param1Node.next);
    }
    
    BnM(int[] param1ArrayOfInt1, int[] param1ArrayOfInt2, int[] param1ArrayOfInt3, Pattern.Node param1Node) {
      this.buffer = param1ArrayOfInt1;
      this.lastOcc = param1ArrayOfInt2;
      this.optoSft = param1ArrayOfInt3;
      this.next = param1Node;
    }
    
    boolean match(Matcher param1Matcher, int param1Int, CharSequence param1CharSequence) {
      int[] arrayOfInt = this.buffer;
      int i = arrayOfInt.length;
      int j = param1Matcher.to - i;
      label19: while (param1Int <= j) {
        for (int k = i - 1; k >= 0; k--) {
          char c = param1CharSequence.charAt(param1Int + k);
          if (c != arrayOfInt[k]) {
            param1Int += Math.max(k + 1 - this.lastOcc[c & 0x7F], this.optoSft[k]);
            continue label19;
          } 
        } 
        param1Matcher.first = param1Int;
        boolean bool = this.next.match(param1Matcher, param1Int + i, param1CharSequence);
        if (bool) {
          param1Matcher.first = param1Int;
          param1Matcher.groups[0] = param1Matcher.first;
          param1Matcher.groups[1] = param1Matcher.last;
          return true;
        } 
        param1Int++;
      } 
      param1Matcher.hitEnd = true;
      return false;
    }
    
    boolean study(Pattern.TreeInfo param1TreeInfo) {
      param1TreeInfo.minLength += this.buffer.length;
      param1TreeInfo.maxValid = false;
      return this.next.study(param1TreeInfo);
    }
  }
  
  static final class BnMS extends BnM {
    int lengthInChars;
    
    BnMS(int[] param1ArrayOfInt1, int[] param1ArrayOfInt2, int[] param1ArrayOfInt3, Pattern.Node param1Node) {
      super(param1ArrayOfInt1, param1ArrayOfInt2, param1ArrayOfInt3, param1Node);
      for (byte b = 0; b < this.buffer.length; b++)
        this.lengthInChars += Character.charCount(this.buffer[b]); 
    }
    
    boolean match(Matcher param1Matcher, int param1Int, CharSequence param1CharSequence) {
      int[] arrayOfInt = this.buffer;
      int i = arrayOfInt.length;
      int j = param1Matcher.to - this.lengthInChars;
      label19: while (param1Int <= j) {
        int k = Pattern.countChars(param1CharSequence, param1Int, i);
        for (int m = i - 1; k > 0; m--) {
          int n = Character.codePointBefore(param1CharSequence, param1Int + k);
          if (n != arrayOfInt[m]) {
            int i1 = Math.max(m + 1 - this.lastOcc[n & 0x7F], this.optoSft[m]);
            param1Int += Pattern.countChars(param1CharSequence, param1Int, i1);
            continue label19;
          } 
          k -= Character.charCount(n);
        } 
        param1Matcher.first = param1Int;
        boolean bool = this.next.match(param1Matcher, param1Int + this.lengthInChars, param1CharSequence);
        if (bool) {
          param1Matcher.first = param1Int;
          param1Matcher.groups[0] = param1Matcher.first;
          param1Matcher.groups[1] = param1Matcher.last;
          return true;
        } 
        param1Int += Pattern.countChars(param1CharSequence, param1Int, 1);
      } 
      param1Matcher.hitEnd = true;
      return false;
    }
  }
  
  static final class Bound extends Node {
    static int LEFT = 1;
    
    static int RIGHT = 2;
    
    static int BOTH = 3;
    
    static int NONE = 4;
    
    int type;
    
    boolean useUWORD;
    
    Bound(int param1Int, boolean param1Boolean) {
      this.type = param1Int;
      this.useUWORD = param1Boolean;
    }
    
    boolean isWord(int param1Int) { return this.useUWORD ? UnicodeProp.WORD.is(param1Int) : ((param1Int == 95 || Character.isLetterOrDigit(param1Int)) ? 1 : 0); }
    
    int check(Matcher param1Matcher, int param1Int, CharSequence param1CharSequence) {
      byte b1 = 0;
      int i = param1Matcher.from;
      int j = param1Matcher.to;
      if (param1Matcher.transparentBounds) {
        i = 0;
        j = param1Matcher.getTextLength();
      } 
      if (param1Int > i) {
        int k = Character.codePointBefore(param1CharSequence, param1Int);
        b1 = (isWord(k) || (Character.getType(k) == 6 && Pattern.hasBaseCharacter(param1Matcher, param1Int - 1, param1CharSequence))) ? 1 : 0;
      } 
      byte b2 = 0;
      if (param1Int < j) {
        int k = Character.codePointAt(param1CharSequence, param1Int);
        b2 = (isWord(k) || (Character.getType(k) == 6 && Pattern.hasBaseCharacter(param1Matcher, param1Int, param1CharSequence))) ? 1 : 0;
      } else {
        param1Matcher.hitEnd = true;
        param1Matcher.requireEnd = true;
      } 
      return (b1 ^ b2) ? ((b2 != 0) ? LEFT : RIGHT) : NONE;
    }
    
    boolean match(Matcher param1Matcher, int param1Int, CharSequence param1CharSequence) { return ((check(param1Matcher, param1Int, param1CharSequence) & this.type) > 0 && this.next.match(param1Matcher, param1Int, param1CharSequence)); }
  }
  
  static final class Branch extends Node {
    Pattern.Node[] atoms = new Pattern.Node[2];
    
    int size = 2;
    
    Pattern.Node conn;
    
    Branch(Pattern.Node param1Node1, Pattern.Node param1Node2, Pattern.Node param1Node3) {
      this.conn = param1Node3;
      this.atoms[0] = param1Node1;
      this.atoms[1] = param1Node2;
    }
    
    void add(Pattern.Node param1Node) {
      if (this.size >= this.atoms.length) {
        Pattern.Node[] arrayOfNode = new Pattern.Node[this.atoms.length * 2];
        System.arraycopy(this.atoms, 0, arrayOfNode, 0, this.atoms.length);
        this.atoms = arrayOfNode;
      } 
      this.atoms[this.size++] = param1Node;
    }
    
    boolean match(Matcher param1Matcher, int param1Int, CharSequence param1CharSequence) {
      for (byte b = 0; b < this.size; b++) {
        if (this.atoms[b] == null) {
          if (this.conn.next.match(param1Matcher, param1Int, param1CharSequence))
            return true; 
        } else if (this.atoms[b].match(param1Matcher, param1Int, param1CharSequence)) {
          return true;
        } 
      } 
      return false;
    }
    
    boolean study(Pattern.TreeInfo param1TreeInfo) {
      int i = param1TreeInfo.minLength;
      int j = param1TreeInfo.maxLength;
      boolean bool = param1TreeInfo.maxValid;
      int k = Integer.MAX_VALUE;
      int m = -1;
      for (byte b = 0; b < this.size; b++) {
        param1TreeInfo.reset();
        if (this.atoms[b] != null)
          this.atoms[b].study(param1TreeInfo); 
        k = Math.min(k, param1TreeInfo.minLength);
        m = Math.max(m, param1TreeInfo.maxLength);
        bool &= param1TreeInfo.maxValid;
      } 
      i += k;
      j += m;
      param1TreeInfo.reset();
      this.conn.next.study(param1TreeInfo);
      param1TreeInfo.minLength += i;
      param1TreeInfo.maxLength += j;
      param1TreeInfo.maxValid &= bool;
      param1TreeInfo.deterministic = false;
      return false;
    }
  }
  
  static final class BranchConn extends Node {
    boolean match(Matcher param1Matcher, int param1Int, CharSequence param1CharSequence) { return this.next.match(param1Matcher, param1Int, param1CharSequence); }
    
    boolean study(Pattern.TreeInfo param1TreeInfo) { return param1TreeInfo.deterministic; }
  }
  
  static class CIBackRef extends Node {
    int groupIndex;
    
    boolean doUnicodeCase;
    
    CIBackRef(int param1Int, boolean param1Boolean) {
      this.groupIndex = param1Int + param1Int;
      this.doUnicodeCase = param1Boolean;
    }
    
    boolean match(Matcher param1Matcher, int param1Int, CharSequence param1CharSequence) {
      int i = param1Matcher.groups[this.groupIndex];
      int j = param1Matcher.groups[this.groupIndex + 1];
      int k = j - i;
      if (i < 0)
        return false; 
      if (param1Int + k > param1Matcher.to) {
        param1Matcher.hitEnd = true;
        return false;
      } 
      int m = param1Int;
      for (byte b = 0; b < k; b++) {
        int n = Character.codePointAt(param1CharSequence, m);
        int i1 = Character.codePointAt(param1CharSequence, i);
        if (n != i1)
          if (this.doUnicodeCase) {
            int i2 = Character.toUpperCase(n);
            int i3 = Character.toUpperCase(i1);
            if (i2 != i3 && Character.toLowerCase(i2) != Character.toLowerCase(i3))
              return false; 
          } else if (ASCII.toLower(n) != ASCII.toLower(i1)) {
            return false;
          }  
        m += Character.charCount(n);
        i += Character.charCount(i1);
      } 
      return this.next.match(param1Matcher, param1Int + k, param1CharSequence);
    }
    
    boolean study(Pattern.TreeInfo param1TreeInfo) {
      param1TreeInfo.maxValid = false;
      return this.next.study(param1TreeInfo);
    }
  }
  
  static final class Caret extends Node {
    boolean match(Matcher param1Matcher, int param1Int, CharSequence param1CharSequence) {
      int i = param1Matcher.from;
      int j = param1Matcher.to;
      if (!param1Matcher.anchoringBounds) {
        i = 0;
        j = param1Matcher.getTextLength();
      } 
      if (param1Int == j) {
        param1Matcher.hitEnd = true;
        return false;
      } 
      if (param1Int > i) {
        char c = param1CharSequence.charAt(param1Int - 1);
        if (c != '\n' && c != '\r' && (c | true) != ' ' && c != '')
          return false; 
        if (c == '\r' && param1CharSequence.charAt(param1Int) == '\n')
          return false; 
      } 
      return this.next.match(param1Matcher, param1Int, param1CharSequence);
    }
  }
  
  static final class Category extends CharProperty {
    final int typeMask;
    
    Category(int param1Int) {
      super(null);
      this.typeMask = param1Int;
    }
    
    boolean isSatisfiedBy(int param1Int) { return ((this.typeMask & 1 << Character.getType(param1Int)) != 0); }
  }
  
  private static abstract class CharProperty extends Node {
    private CharProperty() {}
    
    abstract boolean isSatisfiedBy(int param1Int);
    
    CharProperty complement() { return new CharProperty() {
          boolean isSatisfiedBy(int param2Int) { return !CharProperty.this.isSatisfiedBy(param2Int); }
        }; }
    
    boolean match(Matcher param1Matcher, int param1Int, CharSequence param1CharSequence) {
      if (param1Int < param1Matcher.to) {
        int i = Character.codePointAt(param1CharSequence, param1Int);
        return (isSatisfiedBy(i) && this.next.match(param1Matcher, param1Int + Character.charCount(i), param1CharSequence));
      } 
      param1Matcher.hitEnd = true;
      return false;
    }
    
    boolean study(Pattern.TreeInfo param1TreeInfo) {
      param1TreeInfo.minLength++;
      param1TreeInfo.maxLength++;
      return this.next.study(param1TreeInfo);
    }
  }
  
  private static class CharPropertyNames {
    private static final HashMap<String, CharPropertyFactory> map = new HashMap();
    
    static Pattern.CharProperty charPropertyFor(String param1String) {
      CharPropertyFactory charPropertyFactory = (CharPropertyFactory)map.get(param1String);
      return (charPropertyFactory == null) ? null : charPropertyFactory.make();
    }
    
    private static void defCategory(String param1String, final int typeMask) { map.put(param1String, new CharPropertyFactory() {
            Pattern.CharProperty make() { return new Pattern.Category(typeMask); }
          }); }
    
    private static void defRange(String param1String, final int lower, final int upper) { map.put(param1String, new CharPropertyFactory() {
            Pattern.CharProperty make() { return Pattern.rangeFor(lower, upper); }
          }); }
    
    private static void defCtype(String param1String, final int ctype) { map.put(param1String, new CharPropertyFactory() {
            Pattern.CharProperty make() { return new Pattern.Ctype(ctype); }
          }); }
    
    private static void defClone(String param1String, final CloneableProperty p) { map.put(param1String, new CharPropertyFactory() {
            Pattern.CharProperty make() { return p.clone(); }
          }); }
    
    static  {
      defCategory("Cn", 1);
      defCategory("Lu", 2);
      defCategory("Ll", 4);
      defCategory("Lt", 8);
      defCategory("Lm", 16);
      defCategory("Lo", 32);
      defCategory("Mn", 64);
      defCategory("Me", 128);
      defCategory("Mc", 256);
      defCategory("Nd", 512);
      defCategory("Nl", 1024);
      defCategory("No", 2048);
      defCategory("Zs", 4096);
      defCategory("Zl", 8192);
      defCategory("Zp", 16384);
      defCategory("Cc", 32768);
      defCategory("Cf", 65536);
      defCategory("Co", 262144);
      defCategory("Cs", 524288);
      defCategory("Pd", 1048576);
      defCategory("Ps", 2097152);
      defCategory("Pe", 4194304);
      defCategory("Pc", 8388608);
      defCategory("Po", 16777216);
      defCategory("Sm", 33554432);
      defCategory("Sc", 67108864);
      defCategory("Sk", 134217728);
      defCategory("So", 268435456);
      defCategory("Pi", 536870912);
      defCategory("Pf", 1073741824);
      defCategory("L", 62);
      defCategory("M", 448);
      defCategory("N", 3584);
      defCategory("Z", 28672);
      defCategory("C", 884736);
      defCategory("P", 1643118592);
      defCategory("S", 503316480);
      defCategory("LC", 14);
      defCategory("LD", 574);
      defRange("L1", 0, 255);
      map.put("all", new CharPropertyFactory() {
            Pattern.CharProperty make() { return new Pattern.All(); }
          });
      defRange("ASCII", 0, 127);
      defCtype("Alnum", 1792);
      defCtype("Alpha", 768);
      defCtype("Blank", 16384);
      defCtype("Cntrl", 8192);
      defRange("Digit", 48, 57);
      defCtype("Graph", 5888);
      defRange("Lower", 97, 122);
      defRange("Print", 32, 126);
      defCtype("Punct", 4096);
      defCtype("Space", 2048);
      defRange("Upper", 65, 90);
      defCtype("XDigit", 32768);
      defClone("javaLowerCase", new CloneableProperty() {
            boolean isSatisfiedBy(int param2Int) { return Character.isLowerCase(param2Int); }
          });
      defClone("javaUpperCase", new CloneableProperty() {
            boolean isSatisfiedBy(int param2Int) { return Character.isUpperCase(param2Int); }
          });
      defClone("javaAlphabetic", new CloneableProperty() {
            boolean isSatisfiedBy(int param2Int) { return Character.isAlphabetic(param2Int); }
          });
      defClone("javaIdeographic", new CloneableProperty() {
            boolean isSatisfiedBy(int param2Int) { return Character.isIdeographic(param2Int); }
          });
      defClone("javaTitleCase", new CloneableProperty() {
            boolean isSatisfiedBy(int param2Int) { return Character.isTitleCase(param2Int); }
          });
      defClone("javaDigit", new CloneableProperty() {
            boolean isSatisfiedBy(int param2Int) { return Character.isDigit(param2Int); }
          });
      defClone("javaDefined", new CloneableProperty() {
            boolean isSatisfiedBy(int param2Int) { return Character.isDefined(param2Int); }
          });
      defClone("javaLetter", new CloneableProperty() {
            boolean isSatisfiedBy(int param2Int) { return Character.isLetter(param2Int); }
          });
      defClone("javaLetterOrDigit", new CloneableProperty() {
            boolean isSatisfiedBy(int param2Int) { return Character.isLetterOrDigit(param2Int); }
          });
      defClone("javaJavaIdentifierStart", new CloneableProperty() {
            boolean isSatisfiedBy(int param2Int) { return Character.isJavaIdentifierStart(param2Int); }
          });
      defClone("javaJavaIdentifierPart", new CloneableProperty() {
            boolean isSatisfiedBy(int param2Int) { return Character.isJavaIdentifierPart(param2Int); }
          });
      defClone("javaUnicodeIdentifierStart", new CloneableProperty() {
            boolean isSatisfiedBy(int param2Int) { return Character.isUnicodeIdentifierStart(param2Int); }
          });
      defClone("javaUnicodeIdentifierPart", new CloneableProperty() {
            boolean isSatisfiedBy(int param2Int) { return Character.isUnicodeIdentifierPart(param2Int); }
          });
      defClone("javaIdentifierIgnorable", new CloneableProperty() {
            boolean isSatisfiedBy(int param2Int) { return Character.isIdentifierIgnorable(param2Int); }
          });
      defClone("javaSpaceChar", new CloneableProperty() {
            boolean isSatisfiedBy(int param2Int) { return Character.isSpaceChar(param2Int); }
          });
      defClone("javaWhitespace", new CloneableProperty() {
            boolean isSatisfiedBy(int param2Int) { return Character.isWhitespace(param2Int); }
          });
      defClone("javaISOControl", new CloneableProperty() {
            boolean isSatisfiedBy(int param2Int) { return Character.isISOControl(param2Int); }
          });
      defClone("javaMirrored", new CloneableProperty() {
            boolean isSatisfiedBy(int param2Int) { return Character.isMirrored(param2Int); }
          });
    }
    
    private static abstract class CharPropertyFactory {
      private CharPropertyFactory() {}
      
      abstract Pattern.CharProperty make();
    }
    
    private static abstract class CloneableProperty extends Pattern.CharProperty implements Cloneable {
      private CloneableProperty() { super(null); }
      
      public CloneableProperty clone() {
        try {
          return (CloneableProperty)super.clone();
        } catch (CloneNotSupportedException cloneNotSupportedException) {
          throw new AssertionError(cloneNotSupportedException);
        } 
      }
    }
  }
  
  static final class Conditional extends Node {
    Pattern.Node cond;
    
    Pattern.Node yes;
    
    Pattern.Node not;
    
    Conditional(Pattern.Node param1Node1, Pattern.Node param1Node2, Pattern.Node param1Node3) {
      this.cond = param1Node1;
      this.yes = param1Node2;
      this.not = param1Node3;
    }
    
    boolean match(Matcher param1Matcher, int param1Int, CharSequence param1CharSequence) { return this.cond.match(param1Matcher, param1Int, param1CharSequence) ? this.yes.match(param1Matcher, param1Int, param1CharSequence) : this.not.match(param1Matcher, param1Int, param1CharSequence); }
    
    boolean study(Pattern.TreeInfo param1TreeInfo) {
      int i = param1TreeInfo.minLength;
      int j = param1TreeInfo.maxLength;
      boolean bool1 = param1TreeInfo.maxValid;
      param1TreeInfo.reset();
      this.yes.study(param1TreeInfo);
      int k = param1TreeInfo.minLength;
      int m = param1TreeInfo.maxLength;
      boolean bool2 = param1TreeInfo.maxValid;
      param1TreeInfo.reset();
      this.not.study(param1TreeInfo);
      param1TreeInfo.minLength = i + Math.min(k, param1TreeInfo.minLength);
      param1TreeInfo.maxLength = j + Math.max(m, param1TreeInfo.maxLength);
      param1TreeInfo.maxValid = bool1 & bool2 & param1TreeInfo.maxValid;
      param1TreeInfo.deterministic = false;
      return this.next.study(param1TreeInfo);
    }
  }
  
  static final class Ctype extends BmpCharProperty {
    final int ctype;
    
    Ctype(int param1Int) {
      super(null);
      this.ctype = param1Int;
    }
    
    boolean isSatisfiedBy(int param1Int) { return (param1Int < 128 && ASCII.isType(param1Int, this.ctype)); }
  }
  
  static final class Curly extends Node {
    Pattern.Node atom;
    
    int type;
    
    int cmin;
    
    int cmax;
    
    Curly(Pattern.Node param1Node, int param1Int1, int param1Int2, int param1Int3) {
      this.atom = param1Node;
      this.type = param1Int3;
      this.cmin = param1Int1;
      this.cmax = param1Int2;
    }
    
    boolean match(Matcher param1Matcher, int param1Int, CharSequence param1CharSequence) {
      byte b;
      for (b = 0; b < this.cmin; b++) {
        if (this.atom.match(param1Matcher, param1Int, param1CharSequence)) {
          param1Int = param1Matcher.last;
        } else {
          return false;
        } 
      } 
      return (this.type == 0) ? match0(param1Matcher, param1Int, b, param1CharSequence) : ((this.type == 1) ? match1(param1Matcher, param1Int, b, param1CharSequence) : match2(param1Matcher, param1Int, b, param1CharSequence));
    }
    
    boolean match0(Matcher param1Matcher, int param1Int1, int param1Int2, CharSequence param1CharSequence) {
      if (param1Int2 >= this.cmax)
        return this.next.match(param1Matcher, param1Int1, param1CharSequence); 
      int i = param1Int2;
      if (this.atom.match(param1Matcher, param1Int1, param1CharSequence)) {
        int j = param1Matcher.last - param1Int1;
        if (j != 0) {
          param1Int1 = param1Matcher.last;
          while (++param1Int2 < this.cmax && this.atom.match(param1Matcher, param1Int1, param1CharSequence)) {
            if (param1Int1 + j != param1Matcher.last) {
              if (match0(param1Matcher, param1Matcher.last, param1Int2 + 1, param1CharSequence))
                return true; 
              break;
            } 
            param1Int1 += j;
            param1Int2++;
          } 
          while (param1Int2 >= i) {
            if (this.next.match(param1Matcher, param1Int1, param1CharSequence))
              return true; 
            param1Int1 -= j;
            param1Int2--;
          } 
          return false;
        } 
      } 
      return this.next.match(param1Matcher, param1Int1, param1CharSequence);
    }
    
    boolean match1(Matcher param1Matcher, int param1Int1, int param1Int2, CharSequence param1CharSequence) {
      while (true) {
        if (this.next.match(param1Matcher, param1Int1, param1CharSequence))
          return true; 
        if (param1Int2 >= this.cmax)
          return false; 
        if (!this.atom.match(param1Matcher, param1Int1, param1CharSequence))
          return false; 
        if (param1Int1 == param1Matcher.last)
          return false; 
        param1Int1 = param1Matcher.last;
        param1Int2++;
      } 
    }
    
    boolean match2(Matcher param1Matcher, int param1Int1, int param1Int2, CharSequence param1CharSequence) {
      while (param1Int2 < this.cmax && this.atom.match(param1Matcher, param1Int1, param1CharSequence) && param1Int1 != param1Matcher.last) {
        param1Int1 = param1Matcher.last;
        param1Int2++;
      } 
      return this.next.match(param1Matcher, param1Int1, param1CharSequence);
    }
    
    boolean study(Pattern.TreeInfo param1TreeInfo) {
      int i = param1TreeInfo.minLength;
      int j = param1TreeInfo.maxLength;
      boolean bool1 = param1TreeInfo.maxValid;
      boolean bool2 = param1TreeInfo.deterministic;
      param1TreeInfo.reset();
      this.atom.study(param1TreeInfo);
      int k = param1TreeInfo.minLength * this.cmin + i;
      if (k < i)
        k = 268435455; 
      param1TreeInfo.minLength = k;
      if (bool1 & param1TreeInfo.maxValid) {
        k = param1TreeInfo.maxLength * this.cmax + j;
        param1TreeInfo.maxLength = k;
        if (k < j)
          param1TreeInfo.maxValid = false; 
      } else {
        param1TreeInfo.maxValid = false;
      } 
      if (param1TreeInfo.deterministic && this.cmin == this.cmax) {
        param1TreeInfo.deterministic = bool2;
      } else {
        param1TreeInfo.deterministic = false;
      } 
      return this.next.study(param1TreeInfo);
    }
  }
  
  static final class Dollar extends Node {
    boolean multiline;
    
    Dollar(boolean param1Boolean) { this.multiline = param1Boolean; }
    
    boolean match(Matcher param1Matcher, int param1Int, CharSequence param1CharSequence) {
      int i = param1Matcher.anchoringBounds ? param1Matcher.to : param1Matcher.getTextLength();
      if (!this.multiline) {
        if (param1Int < i - 2)
          return false; 
        if (param1Int == i - 2) {
          char c = param1CharSequence.charAt(param1Int);
          if (c != '\r')
            return false; 
          c = param1CharSequence.charAt(param1Int + 1);
          if (c != '\n')
            return false; 
        } 
      } 
      if (param1Int < i) {
        char c = param1CharSequence.charAt(param1Int);
        if (c == '\n') {
          if (param1Int > 0 && param1CharSequence.charAt(param1Int - 1) == '\r')
            return false; 
          if (this.multiline)
            return this.next.match(param1Matcher, param1Int, param1CharSequence); 
        } else if (c == '\r' || c == '' || (c | true) == ' ') {
          if (this.multiline)
            return this.next.match(param1Matcher, param1Int, param1CharSequence); 
        } else {
          return false;
        } 
      } 
      param1Matcher.hitEnd = true;
      param1Matcher.requireEnd = true;
      return this.next.match(param1Matcher, param1Int, param1CharSequence);
    }
    
    boolean study(Pattern.TreeInfo param1TreeInfo) {
      this.next.study(param1TreeInfo);
      return param1TreeInfo.deterministic;
    }
  }
  
  static final class Dot extends CharProperty {
    Dot() { super(null); }
    
    boolean isSatisfiedBy(int param1Int) { return (param1Int != 10 && param1Int != 13 && (param1Int | true) != 8233 && param1Int != 133); }
  }
  
  static final class End extends Node {
    boolean match(Matcher param1Matcher, int param1Int, CharSequence param1CharSequence) {
      int i = param1Matcher.anchoringBounds ? param1Matcher.to : param1Matcher.getTextLength();
      if (param1Int == i) {
        param1Matcher.hitEnd = true;
        return this.next.match(param1Matcher, param1Int, param1CharSequence);
      } 
      return false;
    }
  }
  
  static final class First extends Node {
    Pattern.Node atom;
    
    First(Pattern.Node param1Node) { this.atom = Pattern.BnM.optimize(param1Node); }
    
    boolean match(Matcher param1Matcher, int param1Int, CharSequence param1CharSequence) {
      if (this.atom instanceof Pattern.BnM)
        return (this.atom.match(param1Matcher, param1Int, param1CharSequence) && this.next.match(param1Matcher, param1Matcher.last, param1CharSequence)); 
      while (true) {
        if (param1Int > param1Matcher.to) {
          param1Matcher.hitEnd = true;
          return false;
        } 
        if (this.atom.match(param1Matcher, param1Int, param1CharSequence))
          return this.next.match(param1Matcher, param1Matcher.last, param1CharSequence); 
        param1Int += Pattern.countChars(param1CharSequence, param1Int, 1);
        param1Matcher.first++;
      } 
    }
    
    boolean study(Pattern.TreeInfo param1TreeInfo) {
      this.atom.study(param1TreeInfo);
      param1TreeInfo.maxValid = false;
      param1TreeInfo.deterministic = false;
      return this.next.study(param1TreeInfo);
    }
  }
  
  static final class GroupCurly extends Node {
    Pattern.Node atom;
    
    int type;
    
    int cmin;
    
    int cmax;
    
    int localIndex;
    
    int groupIndex;
    
    boolean capture;
    
    GroupCurly(Pattern.Node param1Node, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5, boolean param1Boolean) {
      this.atom = param1Node;
      this.type = param1Int3;
      this.cmin = param1Int1;
      this.cmax = param1Int2;
      this.localIndex = param1Int4;
      this.groupIndex = param1Int5;
      this.capture = param1Boolean;
    }
    
    boolean match(Matcher param1Matcher, int param1Int, CharSequence param1CharSequence) {
      int[] arrayOfInt1 = param1Matcher.groups;
      int[] arrayOfInt2 = param1Matcher.locals;
      int i = arrayOfInt2[this.localIndex];
      int j = 0;
      int k = 0;
      if (this.capture) {
        j = arrayOfInt1[this.groupIndex];
        k = arrayOfInt1[this.groupIndex + 1];
      } 
      arrayOfInt2[this.localIndex] = -1;
      boolean bool = true;
      for (byte b = 0; b < this.cmin; b++) {
        if (this.atom.match(param1Matcher, param1Int, param1CharSequence)) {
          if (this.capture) {
            arrayOfInt1[this.groupIndex] = param1Int;
            arrayOfInt1[this.groupIndex + 1] = param1Matcher.last;
          } 
          param1Int = param1Matcher.last;
        } else {
          bool = false;
          break;
        } 
      } 
      if (bool)
        if (this.type == 0) {
          bool = match0(param1Matcher, param1Int, this.cmin, param1CharSequence);
        } else if (this.type == 1) {
          bool = match1(param1Matcher, param1Int, this.cmin, param1CharSequence);
        } else {
          bool = match2(param1Matcher, param1Int, this.cmin, param1CharSequence);
        }  
      if (!bool) {
        arrayOfInt2[this.localIndex] = i;
        if (this.capture) {
          arrayOfInt1[this.groupIndex] = j;
          arrayOfInt1[this.groupIndex + 1] = k;
        } 
      } 
      return bool;
    }
    
    boolean match0(Matcher param1Matcher, int param1Int1, int param1Int2, CharSequence param1CharSequence) {
      int i = param1Int2;
      int[] arrayOfInt = param1Matcher.groups;
      int j = 0;
      int k = 0;
      if (this.capture) {
        j = arrayOfInt[this.groupIndex];
        k = arrayOfInt[this.groupIndex + 1];
      } 
      if (param1Int2 < this.cmax && this.atom.match(param1Matcher, param1Int1, param1CharSequence)) {
        int m = param1Matcher.last - param1Int1;
        if (m <= 0) {
          if (this.capture) {
            arrayOfInt[this.groupIndex] = param1Int1;
            arrayOfInt[this.groupIndex + 1] = param1Int1 + m;
          } 
          param1Int1 += m;
        } else {
          while (true) {
            if (this.capture) {
              arrayOfInt[this.groupIndex] = param1Int1;
              arrayOfInt[this.groupIndex + 1] = param1Int1 + m;
            } 
            param1Int1 += m;
            if (++param1Int2 >= this.cmax || !this.atom.match(param1Matcher, param1Int1, param1CharSequence))
              break; 
            if (param1Int1 + m != param1Matcher.last) {
              if (match0(param1Matcher, param1Int1, param1Int2, param1CharSequence))
                return true; 
              break;
            } 
          } 
          while (param1Int2 > i) {
            if (this.next.match(param1Matcher, param1Int1, param1CharSequence)) {
              if (this.capture) {
                arrayOfInt[this.groupIndex + 1] = param1Int1;
                arrayOfInt[this.groupIndex] = param1Int1 - m;
              } 
              return true;
            } 
            param1Int1 -= m;
            if (this.capture) {
              arrayOfInt[this.groupIndex + 1] = param1Int1;
              arrayOfInt[this.groupIndex] = param1Int1 - m;
            } 
            param1Int2--;
          } 
        } 
      } 
      if (this.capture) {
        arrayOfInt[this.groupIndex] = j;
        arrayOfInt[this.groupIndex + 1] = k;
      } 
      return this.next.match(param1Matcher, param1Int1, param1CharSequence);
    }
    
    boolean match1(Matcher param1Matcher, int param1Int1, int param1Int2, CharSequence param1CharSequence) {
      while (true) {
        if (this.next.match(param1Matcher, param1Int1, param1CharSequence))
          return true; 
        if (param1Int2 >= this.cmax)
          return false; 
        if (!this.atom.match(param1Matcher, param1Int1, param1CharSequence))
          return false; 
        if (param1Int1 == param1Matcher.last)
          return false; 
        if (this.capture) {
          param1Matcher.groups[this.groupIndex] = param1Int1;
          param1Matcher.groups[this.groupIndex + 1] = param1Matcher.last;
        } 
        param1Int1 = param1Matcher.last;
        param1Int2++;
      } 
    }
    
    boolean match2(Matcher param1Matcher, int param1Int1, int param1Int2, CharSequence param1CharSequence) {
      while (param1Int2 < this.cmax && this.atom.match(param1Matcher, param1Int1, param1CharSequence)) {
        if (this.capture) {
          param1Matcher.groups[this.groupIndex] = param1Int1;
          param1Matcher.groups[this.groupIndex + 1] = param1Matcher.last;
        } 
        if (param1Int1 == param1Matcher.last)
          break; 
        param1Int1 = param1Matcher.last;
        param1Int2++;
      } 
      return this.next.match(param1Matcher, param1Int1, param1CharSequence);
    }
    
    boolean study(Pattern.TreeInfo param1TreeInfo) {
      int i = param1TreeInfo.minLength;
      int j = param1TreeInfo.maxLength;
      boolean bool1 = param1TreeInfo.maxValid;
      boolean bool2 = param1TreeInfo.deterministic;
      param1TreeInfo.reset();
      this.atom.study(param1TreeInfo);
      int k = param1TreeInfo.minLength * this.cmin + i;
      if (k < i)
        k = 268435455; 
      param1TreeInfo.minLength = k;
      if (bool1 & param1TreeInfo.maxValid) {
        k = param1TreeInfo.maxLength * this.cmax + j;
        param1TreeInfo.maxLength = k;
        if (k < j)
          param1TreeInfo.maxValid = false; 
      } else {
        param1TreeInfo.maxValid = false;
      } 
      if (param1TreeInfo.deterministic && this.cmin == this.cmax) {
        param1TreeInfo.deterministic = bool2;
      } else {
        param1TreeInfo.deterministic = false;
      } 
      return this.next.study(param1TreeInfo);
    }
  }
  
  static final class GroupHead extends Node {
    int localIndex;
    
    GroupHead(int param1Int) { this.localIndex = param1Int; }
    
    boolean match(Matcher param1Matcher, int param1Int, CharSequence param1CharSequence) {
      int i = param1Matcher.locals[this.localIndex];
      param1Matcher.locals[this.localIndex] = param1Int;
      boolean bool = this.next.match(param1Matcher, param1Int, param1CharSequence);
      param1Matcher.locals[this.localIndex] = i;
      return bool;
    }
    
    boolean matchRef(Matcher param1Matcher, int param1Int, CharSequence param1CharSequence) {
      int i = param1Matcher.locals[this.localIndex];
      param1Matcher.locals[this.localIndex] = param1Int ^ 0xFFFFFFFF;
      boolean bool = this.next.match(param1Matcher, param1Int, param1CharSequence);
      param1Matcher.locals[this.localIndex] = i;
      return bool;
    }
  }
  
  static final class GroupRef extends Node {
    Pattern.GroupHead head;
    
    GroupRef(Pattern.GroupHead param1GroupHead) { this.head = param1GroupHead; }
    
    boolean match(Matcher param1Matcher, int param1Int, CharSequence param1CharSequence) { return (this.head.matchRef(param1Matcher, param1Int, param1CharSequence) && this.next.match(param1Matcher, param1Matcher.last, param1CharSequence)); }
    
    boolean study(Pattern.TreeInfo param1TreeInfo) {
      param1TreeInfo.maxValid = false;
      param1TreeInfo.deterministic = false;
      return this.next.study(param1TreeInfo);
    }
  }
  
  static final class GroupTail extends Node {
    int localIndex;
    
    int groupIndex;
    
    GroupTail(int param1Int1, int param1Int2) {
      this.localIndex = param1Int1;
      this.groupIndex = param1Int2 + param1Int2;
    }
    
    boolean match(Matcher param1Matcher, int param1Int, CharSequence param1CharSequence) {
      int i = param1Matcher.locals[this.localIndex];
      if (i >= 0) {
        int j = param1Matcher.groups[this.groupIndex];
        int k = param1Matcher.groups[this.groupIndex + 1];
        param1Matcher.groups[this.groupIndex] = i;
        param1Matcher.groups[this.groupIndex + 1] = param1Int;
        if (this.next.match(param1Matcher, param1Int, param1CharSequence))
          return true; 
        param1Matcher.groups[this.groupIndex] = j;
        param1Matcher.groups[this.groupIndex + 1] = k;
        return false;
      } 
      param1Matcher.last = param1Int;
      return true;
    }
  }
  
  static final class HorizWS extends BmpCharProperty {
    HorizWS() { super(null); }
    
    boolean isSatisfiedBy(int param1Int) { return (param1Int == 9 || param1Int == 32 || param1Int == 160 || param1Int == 5760 || param1Int == 6158 || (param1Int >= 8192 && param1Int <= 8202) || param1Int == 8239 || param1Int == 8287 || param1Int == 12288); }
  }
  
  static final class LastMatch extends Node {
    boolean match(Matcher param1Matcher, int param1Int, CharSequence param1CharSequence) { return (param1Int != param1Matcher.oldLast) ? false : this.next.match(param1Matcher, param1Int, param1CharSequence); }
  }
  
  static class LastNode extends Node {
    boolean match(Matcher param1Matcher, int param1Int, CharSequence param1CharSequence) {
      if (param1Matcher.acceptMode == 1 && param1Int != param1Matcher.to)
        return false; 
      param1Matcher.last = param1Int;
      param1Matcher.groups[0] = param1Matcher.first;
      param1Matcher.groups[1] = param1Matcher.last;
      return true;
    }
  }
  
  static final class LazyLoop extends Loop {
    LazyLoop(int param1Int1, int param1Int2) { super(param1Int1, param1Int2); }
    
    boolean match(Matcher param1Matcher, int param1Int, CharSequence param1CharSequence) {
      if (param1Int > param1Matcher.locals[this.beginIndex]) {
        int i = param1Matcher.locals[this.countIndex];
        if (i < this.cmin) {
          param1Matcher.locals[this.countIndex] = i + 1;
          boolean bool = this.body.match(param1Matcher, param1Int, param1CharSequence);
          if (!bool)
            param1Matcher.locals[this.countIndex] = i; 
          return bool;
        } 
        if (this.next.match(param1Matcher, param1Int, param1CharSequence))
          return true; 
        if (i < this.cmax) {
          param1Matcher.locals[this.countIndex] = i + 1;
          boolean bool = this.body.match(param1Matcher, param1Int, param1CharSequence);
          if (!bool)
            param1Matcher.locals[this.countIndex] = i; 
          return bool;
        } 
        return false;
      } 
      return this.next.match(param1Matcher, param1Int, param1CharSequence);
    }
    
    boolean matchInit(Matcher param1Matcher, int param1Int, CharSequence param1CharSequence) {
      int i = param1Matcher.locals[this.countIndex];
      boolean bool = false;
      if (0 < this.cmin) {
        param1Matcher.locals[this.countIndex] = 1;
        bool = this.body.match(param1Matcher, param1Int, param1CharSequence);
      } else if (this.next.match(param1Matcher, param1Int, param1CharSequence)) {
        bool = true;
      } else if (0 < this.cmax) {
        param1Matcher.locals[this.countIndex] = 1;
        bool = this.body.match(param1Matcher, param1Int, param1CharSequence);
      } 
      param1Matcher.locals[this.countIndex] = i;
      return bool;
    }
    
    boolean study(Pattern.TreeInfo param1TreeInfo) {
      param1TreeInfo.maxValid = false;
      param1TreeInfo.deterministic = false;
      return false;
    }
  }
  
  static final class LineEnding extends Node {
    boolean match(Matcher param1Matcher, int param1Int, CharSequence param1CharSequence) {
      if (param1Int < param1Matcher.to) {
        char c = param1CharSequence.charAt(param1Int);
        if (c == '\n' || c == '\013' || c == '\f' || c == '' || c == ' ' || c == ' ')
          return this.next.match(param1Matcher, param1Int + 1, param1CharSequence); 
        if (c == '\r') {
          if (++param1Int < param1Matcher.to && param1CharSequence.charAt(param1Int) == '\n')
            param1Int++; 
          return this.next.match(param1Matcher, param1Int, param1CharSequence);
        } 
      } else {
        param1Matcher.hitEnd = true;
      } 
      return false;
    }
    
    boolean study(Pattern.TreeInfo param1TreeInfo) {
      param1TreeInfo.minLength++;
      param1TreeInfo.maxLength += 2;
      return this.next.study(param1TreeInfo);
    }
  }
  
  static class Loop extends Node {
    Pattern.Node body;
    
    int countIndex;
    
    int beginIndex;
    
    int cmin;
    
    int cmax;
    
    Loop(int param1Int1, int param1Int2) {
      this.countIndex = param1Int1;
      this.beginIndex = param1Int2;
    }
    
    boolean match(Matcher param1Matcher, int param1Int, CharSequence param1CharSequence) {
      if (param1Int > param1Matcher.locals[this.beginIndex]) {
        int i = param1Matcher.locals[this.countIndex];
        if (i < this.cmin) {
          param1Matcher.locals[this.countIndex] = i + 1;
          boolean bool = this.body.match(param1Matcher, param1Int, param1CharSequence);
          if (!bool)
            param1Matcher.locals[this.countIndex] = i; 
          return bool;
        } 
        if (i < this.cmax) {
          param1Matcher.locals[this.countIndex] = i + 1;
          boolean bool = this.body.match(param1Matcher, param1Int, param1CharSequence);
          if (!bool) {
            param1Matcher.locals[this.countIndex] = i;
          } else {
            return true;
          } 
        } 
      } 
      return this.next.match(param1Matcher, param1Int, param1CharSequence);
    }
    
    boolean matchInit(Matcher param1Matcher, int param1Int, CharSequence param1CharSequence) {
      int i = param1Matcher.locals[this.countIndex];
      boolean bool = false;
      if (0 < this.cmin) {
        param1Matcher.locals[this.countIndex] = 1;
        bool = this.body.match(param1Matcher, param1Int, param1CharSequence);
      } else if (0 < this.cmax) {
        param1Matcher.locals[this.countIndex] = 1;
        bool = this.body.match(param1Matcher, param1Int, param1CharSequence);
        if (!bool)
          bool = this.next.match(param1Matcher, param1Int, param1CharSequence); 
      } else {
        bool = this.next.match(param1Matcher, param1Int, param1CharSequence);
      } 
      param1Matcher.locals[this.countIndex] = i;
      return bool;
    }
    
    boolean study(Pattern.TreeInfo param1TreeInfo) {
      param1TreeInfo.maxValid = false;
      param1TreeInfo.deterministic = false;
      return false;
    }
  }
  
  static final class Neg extends Node {
    Pattern.Node cond;
    
    Neg(Pattern.Node param1Node) { this.cond = param1Node; }
    
    boolean match(Matcher param1Matcher, int param1Int, CharSequence param1CharSequence) {
      i = param1Matcher.to;
      boolean bool = false;
      if (param1Matcher.transparentBounds)
        param1Matcher.to = param1Matcher.getTextLength(); 
      try {
        if (param1Int < param1Matcher.to) {
          bool = !this.cond.match(param1Matcher, param1Int, param1CharSequence) ? 1 : 0;
        } else {
          param1Matcher.requireEnd = true;
          bool = !this.cond.match(param1Matcher, param1Int, param1CharSequence) ? 1 : 0;
        } 
      } finally {
        param1Matcher.to = i;
      } 
      return (bool && this.next.match(param1Matcher, param1Int, param1CharSequence));
    }
  }
  
  static class Node {
    Node next = Pattern.accept;
    
    boolean match(Matcher param1Matcher, int param1Int, CharSequence param1CharSequence) {
      param1Matcher.last = param1Int;
      param1Matcher.groups[0] = param1Matcher.first;
      param1Matcher.groups[1] = param1Matcher.last;
      return true;
    }
    
    boolean study(Pattern.TreeInfo param1TreeInfo) { return (this.next != null) ? this.next.study(param1TreeInfo) : param1TreeInfo.deterministic; }
  }
  
  static class NotBehind extends Node {
    Pattern.Node cond;
    
    int rmax;
    
    int rmin;
    
    NotBehind(Pattern.Node param1Node, int param1Int1, int param1Int2) {
      this.cond = param1Node;
      this.rmax = param1Int1;
      this.rmin = param1Int2;
    }
    
    boolean match(Matcher param1Matcher, int param1Int, CharSequence param1CharSequence) {
      int i = param1Matcher.lookbehindTo;
      int j = param1Matcher.from;
      boolean bool = false;
      int k = !param1Matcher.transparentBounds ? param1Matcher.from : 0;
      int m = Math.max(param1Int - this.rmax, k);
      param1Matcher.lookbehindTo = param1Int;
      if (param1Matcher.transparentBounds)
        param1Matcher.from = 0; 
      for (int n = param1Int - this.rmin; !bool && n >= m; n--)
        bool = this.cond.match(param1Matcher, n, param1CharSequence); 
      param1Matcher.from = j;
      param1Matcher.lookbehindTo = i;
      return (!bool && this.next.match(param1Matcher, param1Int, param1CharSequence));
    }
  }
  
  static final class NotBehindS extends NotBehind {
    NotBehindS(Pattern.Node param1Node, int param1Int1, int param1Int2) { super(param1Node, param1Int1, param1Int2); }
    
    boolean match(Matcher param1Matcher, int param1Int, CharSequence param1CharSequence) {
      int i = Pattern.countChars(param1CharSequence, param1Int, -this.rmax);
      int j = Pattern.countChars(param1CharSequence, param1Int, -this.rmin);
      int k = param1Matcher.from;
      int m = param1Matcher.lookbehindTo;
      boolean bool = false;
      int n = !param1Matcher.transparentBounds ? param1Matcher.from : 0;
      int i1 = Math.max(param1Int - i, n);
      param1Matcher.lookbehindTo = param1Int;
      if (param1Matcher.transparentBounds)
        param1Matcher.from = 0; 
      int i2;
      for (i2 = param1Int - j; !bool && i2 >= i1; i2 -= ((i2 > i1) ? Pattern.countChars(param1CharSequence, i2, -1) : 1))
        bool = this.cond.match(param1Matcher, i2, param1CharSequence); 
      param1Matcher.from = k;
      param1Matcher.lookbehindTo = m;
      return (!bool && this.next.match(param1Matcher, param1Int, param1CharSequence));
    }
  }
  
  static final class Pos extends Node {
    Pattern.Node cond;
    
    Pos(Pattern.Node param1Node) { this.cond = param1Node; }
    
    boolean match(Matcher param1Matcher, int param1Int, CharSequence param1CharSequence) {
      i = param1Matcher.to;
      boolean bool = false;
      if (param1Matcher.transparentBounds)
        param1Matcher.to = param1Matcher.getTextLength(); 
      try {
        bool = this.cond.match(param1Matcher, param1Int, param1CharSequence);
      } finally {
        param1Matcher.to = i;
      } 
      return (bool && this.next.match(param1Matcher, param1Int, param1CharSequence));
    }
  }
  
  static final class Prolog extends Node {
    Pattern.Loop loop;
    
    Prolog(Pattern.Loop param1Loop) { this.loop = param1Loop; }
    
    boolean match(Matcher param1Matcher, int param1Int, CharSequence param1CharSequence) { return this.loop.matchInit(param1Matcher, param1Int, param1CharSequence); }
    
    boolean study(Pattern.TreeInfo param1TreeInfo) { return this.loop.study(param1TreeInfo); }
  }
  
  static final class Ques extends Node {
    Pattern.Node atom;
    
    int type;
    
    Ques(Pattern.Node param1Node, int param1Int) {
      this.atom = param1Node;
      this.type = param1Int;
    }
    
    boolean match(Matcher param1Matcher, int param1Int, CharSequence param1CharSequence) {
      switch (this.type) {
        case 0:
          return ((this.atom.match(param1Matcher, param1Int, param1CharSequence) && this.next.match(param1Matcher, param1Matcher.last, param1CharSequence)) || this.next.match(param1Matcher, param1Int, param1CharSequence));
        case 1:
          return (this.next.match(param1Matcher, param1Int, param1CharSequence) || (this.atom.match(param1Matcher, param1Int, param1CharSequence) && this.next.match(param1Matcher, param1Matcher.last, param1CharSequence)));
        case 2:
          if (this.atom.match(param1Matcher, param1Int, param1CharSequence))
            param1Int = param1Matcher.last; 
          return this.next.match(param1Matcher, param1Int, param1CharSequence);
      } 
      return (this.atom.match(param1Matcher, param1Int, param1CharSequence) && this.next.match(param1Matcher, param1Matcher.last, param1CharSequence));
    }
    
    boolean study(Pattern.TreeInfo param1TreeInfo) {
      if (this.type != 3) {
        int i = param1TreeInfo.minLength;
        this.atom.study(param1TreeInfo);
        param1TreeInfo.minLength = i;
        param1TreeInfo.deterministic = false;
        return this.next.study(param1TreeInfo);
      } 
      this.atom.study(param1TreeInfo);
      return this.next.study(param1TreeInfo);
    }
  }
  
  static final class Script extends CharProperty {
    final Character.UnicodeScript script;
    
    Script(Character.UnicodeScript param1UnicodeScript) {
      super(null);
      this.script = param1UnicodeScript;
    }
    
    boolean isSatisfiedBy(int param1Int) { return (this.script == Character.UnicodeScript.of(param1Int)); }
  }
  
  static final class Single extends BmpCharProperty {
    final int c;
    
    Single(int param1Int) {
      super(null);
      this.c = param1Int;
    }
    
    boolean isSatisfiedBy(int param1Int) { return (param1Int == this.c); }
  }
  
  static final class SingleI extends BmpCharProperty {
    final int lower;
    
    final int upper;
    
    SingleI(int param1Int1, int param1Int2) {
      super(null);
      this.lower = param1Int1;
      this.upper = param1Int2;
    }
    
    boolean isSatisfiedBy(int param1Int) { return (param1Int == this.lower || param1Int == this.upper); }
  }
  
  static final class SingleS extends CharProperty {
    final int c;
    
    SingleS(int param1Int) {
      super(null);
      this.c = param1Int;
    }
    
    boolean isSatisfiedBy(int param1Int) { return (param1Int == this.c); }
  }
  
  static final class SingleU extends CharProperty {
    final int lower;
    
    SingleU(int param1Int) {
      super(null);
      this.lower = param1Int;
    }
    
    boolean isSatisfiedBy(int param1Int) { return (this.lower == param1Int || this.lower == Character.toLowerCase(Character.toUpperCase(param1Int))); }
  }
  
  static final class Slice extends SliceNode {
    Slice(int[] param1ArrayOfInt) { super(param1ArrayOfInt); }
    
    boolean match(Matcher param1Matcher, int param1Int, CharSequence param1CharSequence) {
      int[] arrayOfInt = this.buffer;
      int i = arrayOfInt.length;
      for (int j = 0; j < i; j++) {
        if (param1Int + j >= param1Matcher.to) {
          param1Matcher.hitEnd = true;
          return false;
        } 
        if (arrayOfInt[j] != param1CharSequence.charAt(param1Int + j))
          return false; 
      } 
      return this.next.match(param1Matcher, param1Int + i, param1CharSequence);
    }
  }
  
  static class SliceI extends SliceNode {
    SliceI(int[] param1ArrayOfInt) { super(param1ArrayOfInt); }
    
    boolean match(Matcher param1Matcher, int param1Int, CharSequence param1CharSequence) {
      int[] arrayOfInt = this.buffer;
      int i = arrayOfInt.length;
      for (int j = 0; j < i; j++) {
        if (param1Int + j >= param1Matcher.to) {
          param1Matcher.hitEnd = true;
          return false;
        } 
        char c = param1CharSequence.charAt(param1Int + j);
        if (arrayOfInt[j] != c && arrayOfInt[j] != ASCII.toLower(c))
          return false; 
      } 
      return this.next.match(param1Matcher, param1Int + i, param1CharSequence);
    }
  }
  
  static class SliceIS extends SliceNode {
    SliceIS(int[] param1ArrayOfInt) { super(param1ArrayOfInt); }
    
    int toLower(int param1Int) { return ASCII.toLower(param1Int); }
    
    boolean match(Matcher param1Matcher, int param1Int, CharSequence param1CharSequence) {
      int[] arrayOfInt = this.buffer;
      int i = param1Int;
      for (byte b = 0; b < arrayOfInt.length; b++) {
        if (i >= param1Matcher.to) {
          param1Matcher.hitEnd = true;
          return false;
        } 
        int j = Character.codePointAt(param1CharSequence, i);
        if (arrayOfInt[b] != j && arrayOfInt[b] != toLower(j))
          return false; 
        i += Character.charCount(j);
        if (i > param1Matcher.to) {
          param1Matcher.hitEnd = true;
          return false;
        } 
      } 
      return this.next.match(param1Matcher, i, param1CharSequence);
    }
  }
  
  static class SliceNode extends Node {
    int[] buffer;
    
    SliceNode(int[] param1ArrayOfInt) { this.buffer = param1ArrayOfInt; }
    
    boolean study(Pattern.TreeInfo param1TreeInfo) {
      param1TreeInfo.minLength += this.buffer.length;
      param1TreeInfo.maxLength += this.buffer.length;
      return this.next.study(param1TreeInfo);
    }
  }
  
  static final class SliceS extends SliceNode {
    SliceS(int[] param1ArrayOfInt) { super(param1ArrayOfInt); }
    
    boolean match(Matcher param1Matcher, int param1Int, CharSequence param1CharSequence) {
      int[] arrayOfInt = this.buffer;
      int i = param1Int;
      for (byte b = 0; b < arrayOfInt.length; b++) {
        if (i >= param1Matcher.to) {
          param1Matcher.hitEnd = true;
          return false;
        } 
        int j = Character.codePointAt(param1CharSequence, i);
        if (arrayOfInt[b] != j)
          return false; 
        i += Character.charCount(j);
        if (i > param1Matcher.to) {
          param1Matcher.hitEnd = true;
          return false;
        } 
      } 
      return this.next.match(param1Matcher, i, param1CharSequence);
    }
  }
  
  static final class SliceU extends SliceNode {
    SliceU(int[] param1ArrayOfInt) { super(param1ArrayOfInt); }
    
    boolean match(Matcher param1Matcher, int param1Int, CharSequence param1CharSequence) {
      int[] arrayOfInt = this.buffer;
      int i = arrayOfInt.length;
      for (int j = 0; j < i; j++) {
        if (param1Int + j >= param1Matcher.to) {
          param1Matcher.hitEnd = true;
          return false;
        } 
        char c = param1CharSequence.charAt(param1Int + j);
        if (arrayOfInt[j] != c && arrayOfInt[j] != Character.toLowerCase(Character.toUpperCase(c)))
          return false; 
      } 
      return this.next.match(param1Matcher, param1Int + i, param1CharSequence);
    }
  }
  
  static final class SliceUS extends SliceIS {
    SliceUS(int[] param1ArrayOfInt) { super(param1ArrayOfInt); }
    
    int toLower(int param1Int) { return Character.toLowerCase(Character.toUpperCase(param1Int)); }
  }
  
  static class Start extends Node {
    int minLength;
    
    Start(Pattern.Node param1Node) {
      this.next = param1Node;
      Pattern.TreeInfo treeInfo = new Pattern.TreeInfo();
      this.next.study(treeInfo);
      this.minLength = treeInfo.minLength;
    }
    
    boolean match(Matcher param1Matcher, int param1Int, CharSequence param1CharSequence) {
      if (param1Int > param1Matcher.to - this.minLength) {
        param1Matcher.hitEnd = true;
        return false;
      } 
      int i = param1Matcher.to - this.minLength;
      while (param1Int <= i) {
        if (this.next.match(param1Matcher, param1Int, param1CharSequence)) {
          param1Matcher.first = param1Int;
          param1Matcher.groups[0] = param1Matcher.first;
          param1Matcher.groups[1] = param1Matcher.last;
          return true;
        } 
        param1Int++;
      } 
      param1Matcher.hitEnd = true;
      return false;
    }
    
    boolean study(Pattern.TreeInfo param1TreeInfo) {
      this.next.study(param1TreeInfo);
      param1TreeInfo.maxValid = false;
      param1TreeInfo.deterministic = false;
      return false;
    }
  }
  
  static final class StartS extends Start {
    StartS(Pattern.Node param1Node) { super(param1Node); }
    
    boolean match(Matcher param1Matcher, int param1Int, CharSequence param1CharSequence) {
      if (param1Int > param1Matcher.to - this.minLength) {
        param1Matcher.hitEnd = true;
        return false;
      } 
      int i = param1Matcher.to - this.minLength;
      while (param1Int <= i) {
        if (this.next.match(param1Matcher, param1Int, param1CharSequence)) {
          param1Matcher.first = param1Int;
          param1Matcher.groups[0] = param1Matcher.first;
          param1Matcher.groups[1] = param1Matcher.last;
          return true;
        } 
        if (param1Int == i)
          break; 
        if (Character.isHighSurrogate(param1CharSequence.charAt(param1Int++)) && param1Int < param1CharSequence.length() && Character.isLowSurrogate(param1CharSequence.charAt(param1Int)))
          param1Int++; 
      } 
      param1Matcher.hitEnd = true;
      return false;
    }
  }
  
  static final class TreeInfo {
    int minLength;
    
    int maxLength;
    
    boolean maxValid;
    
    boolean deterministic;
    
    TreeInfo() { reset(); }
    
    void reset() {
      this.minLength = 0;
      this.maxLength = 0;
      this.maxValid = true;
      this.deterministic = true;
    }
  }
  
  static final class UnixCaret extends Node {
    boolean match(Matcher param1Matcher, int param1Int, CharSequence param1CharSequence) {
      int i = param1Matcher.from;
      int j = param1Matcher.to;
      if (!param1Matcher.anchoringBounds) {
        i = 0;
        j = param1Matcher.getTextLength();
      } 
      if (param1Int == j) {
        param1Matcher.hitEnd = true;
        return false;
      } 
      if (param1Int > i) {
        char c = param1CharSequence.charAt(param1Int - 1);
        if (c != '\n')
          return false; 
      } 
      return this.next.match(param1Matcher, param1Int, param1CharSequence);
    }
  }
  
  static final class UnixDollar extends Node {
    boolean multiline;
    
    UnixDollar(boolean param1Boolean) { this.multiline = param1Boolean; }
    
    boolean match(Matcher param1Matcher, int param1Int, CharSequence param1CharSequence) {
      int i = param1Matcher.anchoringBounds ? param1Matcher.to : param1Matcher.getTextLength();
      if (param1Int < i) {
        char c = param1CharSequence.charAt(param1Int);
        if (c == '\n') {
          if (!this.multiline && param1Int != i - 1)
            return false; 
          if (this.multiline)
            return this.next.match(param1Matcher, param1Int, param1CharSequence); 
        } else {
          return false;
        } 
      } 
      param1Matcher.hitEnd = true;
      param1Matcher.requireEnd = true;
      return this.next.match(param1Matcher, param1Int, param1CharSequence);
    }
    
    boolean study(Pattern.TreeInfo param1TreeInfo) {
      this.next.study(param1TreeInfo);
      return param1TreeInfo.deterministic;
    }
  }
  
  static final class UnixDot extends CharProperty {
    UnixDot() { super(null); }
    
    boolean isSatisfiedBy(int param1Int) { return (param1Int != 10); }
  }
  
  static final class Utype extends CharProperty {
    final UnicodeProp uprop;
    
    Utype(UnicodeProp param1UnicodeProp) {
      super(null);
      this.uprop = param1UnicodeProp;
    }
    
    boolean isSatisfiedBy(int param1Int) { return this.uprop.is(param1Int); }
  }
  
  static final class VertWS extends BmpCharProperty {
    VertWS() { super(null); }
    
    boolean isSatisfiedBy(int param1Int) { return ((param1Int >= 10 && param1Int <= 13) || param1Int == 133 || param1Int == 8232 || param1Int == 8233); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\regex\Pattern.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */