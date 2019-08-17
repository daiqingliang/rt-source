package java.util.regex;

import java.util.Objects;

public final class Matcher implements MatchResult {
  Pattern parentPattern;
  
  int[] groups;
  
  int from;
  
  int to;
  
  int lookbehindTo;
  
  CharSequence text;
  
  static final int ENDANCHOR = 1;
  
  static final int NOANCHOR = 0;
  
  int acceptMode = 0;
  
  int first = -1;
  
  int last = 0;
  
  int oldLast = -1;
  
  int lastAppendPosition = 0;
  
  int[] locals;
  
  boolean hitEnd;
  
  boolean requireEnd;
  
  boolean transparentBounds = false;
  
  boolean anchoringBounds = true;
  
  Matcher() {}
  
  Matcher(Pattern paramPattern, CharSequence paramCharSequence) {
    this.parentPattern = paramPattern;
    this.text = paramCharSequence;
    int i = Math.max(paramPattern.capturingGroupCount, 10);
    this.groups = new int[i * 2];
    this.locals = new int[paramPattern.localCount];
    reset();
  }
  
  public Pattern pattern() { return this.parentPattern; }
  
  public MatchResult toMatchResult() {
    Matcher matcher = new Matcher(this.parentPattern, this.text.toString());
    matcher.first = this.first;
    matcher.last = this.last;
    matcher.groups = (int[])this.groups.clone();
    return matcher;
  }
  
  public Matcher usePattern(Pattern paramPattern) {
    if (paramPattern == null)
      throw new IllegalArgumentException("Pattern cannot be null"); 
    this.parentPattern = paramPattern;
    int i = Math.max(paramPattern.capturingGroupCount, 10);
    this.groups = new int[i * 2];
    this.locals = new int[paramPattern.localCount];
    byte b;
    for (b = 0; b < this.groups.length; b++)
      this.groups[b] = -1; 
    for (b = 0; b < this.locals.length; b++)
      this.locals[b] = -1; 
    return this;
  }
  
  public Matcher reset() {
    this.first = -1;
    this.last = 0;
    this.oldLast = -1;
    byte b;
    for (b = 0; b < this.groups.length; b++)
      this.groups[b] = -1; 
    for (b = 0; b < this.locals.length; b++)
      this.locals[b] = -1; 
    this.lastAppendPosition = 0;
    this.from = 0;
    this.to = getTextLength();
    return this;
  }
  
  public Matcher reset(CharSequence paramCharSequence) {
    this.text = paramCharSequence;
    return reset();
  }
  
  public int start() {
    if (this.first < 0)
      throw new IllegalStateException("No match available"); 
    return this.first;
  }
  
  public int start(int paramInt) {
    if (this.first < 0)
      throw new IllegalStateException("No match available"); 
    if (paramInt < 0 || paramInt > groupCount())
      throw new IndexOutOfBoundsException("No group " + paramInt); 
    return this.groups[paramInt * 2];
  }
  
  public int start(String paramString) { return this.groups[getMatchedGroupIndex(paramString) * 2]; }
  
  public int end() {
    if (this.first < 0)
      throw new IllegalStateException("No match available"); 
    return this.last;
  }
  
  public int end(int paramInt) {
    if (this.first < 0)
      throw new IllegalStateException("No match available"); 
    if (paramInt < 0 || paramInt > groupCount())
      throw new IndexOutOfBoundsException("No group " + paramInt); 
    return this.groups[paramInt * 2 + 1];
  }
  
  public int end(String paramString) { return this.groups[getMatchedGroupIndex(paramString) * 2 + 1]; }
  
  public String group() { return group(0); }
  
  public String group(int paramInt) {
    if (this.first < 0)
      throw new IllegalStateException("No match found"); 
    if (paramInt < 0 || paramInt > groupCount())
      throw new IndexOutOfBoundsException("No group " + paramInt); 
    return (this.groups[paramInt * 2] == -1 || this.groups[paramInt * 2 + 1] == -1) ? null : getSubSequence(this.groups[paramInt * 2], this.groups[paramInt * 2 + 1]).toString();
  }
  
  public String group(String paramString) {
    int i = getMatchedGroupIndex(paramString);
    return (this.groups[i * 2] == -1 || this.groups[i * 2 + 1] == -1) ? null : getSubSequence(this.groups[i * 2], this.groups[i * 2 + 1]).toString();
  }
  
  public int groupCount() { return this.parentPattern.capturingGroupCount - 1; }
  
  public boolean matches() { return match(this.from, 1); }
  
  public boolean find() {
    int i = this.last;
    if (i == this.first)
      i++; 
    if (i < this.from)
      i = this.from; 
    if (i > this.to) {
      for (byte b = 0; b < this.groups.length; b++)
        this.groups[b] = -1; 
      return false;
    } 
    return search(i);
  }
  
  public boolean find(int paramInt) {
    int i = getTextLength();
    if (paramInt < 0 || paramInt > i)
      throw new IndexOutOfBoundsException("Illegal start index"); 
    reset();
    return search(paramInt);
  }
  
  public boolean lookingAt() { return match(this.from, 0); }
  
  public static String quoteReplacement(String paramString) {
    if (paramString.indexOf('\\') == -1 && paramString.indexOf('$') == -1)
      return paramString; 
    StringBuilder stringBuilder = new StringBuilder();
    for (byte b = 0; b < paramString.length(); b++) {
      char c = paramString.charAt(b);
      if (c == '\\' || c == '$')
        stringBuilder.append('\\'); 
      stringBuilder.append(c);
    } 
    return stringBuilder.toString();
  }
  
  public Matcher appendReplacement(StringBuffer paramStringBuffer, String paramString) {
    if (this.first < 0)
      throw new IllegalStateException("No match available"); 
    byte b = 0;
    StringBuilder stringBuilder = new StringBuilder();
    while (b < paramString.length()) {
      char c = paramString.charAt(b);
      if (c == '\\') {
        if (++b == paramString.length())
          throw new IllegalArgumentException("character to be escaped is missing"); 
        c = paramString.charAt(b);
        stringBuilder.append(c);
        b++;
        continue;
      } 
      if (c == '$') {
        if (++b == paramString.length())
          throw new IllegalArgumentException("Illegal group reference: group index is missing"); 
        c = paramString.charAt(b);
        int i = -1;
        if (c == '{') {
          b++;
          StringBuilder stringBuilder1 = new StringBuilder();
          while (b < paramString.length()) {
            c = paramString.charAt(b);
            if (ASCII.isLower(c) || ASCII.isUpper(c) || ASCII.isDigit(c)) {
              stringBuilder1.append(c);
              b++;
            } 
          } 
          if (stringBuilder1.length() == 0)
            throw new IllegalArgumentException("named capturing group has 0 length name"); 
          if (c != '}')
            throw new IllegalArgumentException("named capturing group is missing trailing '}'"); 
          String str = stringBuilder1.toString();
          if (ASCII.isDigit(str.charAt(0)))
            throw new IllegalArgumentException("capturing group name {" + str + "} starts with digit character"); 
          if (!this.parentPattern.namedGroups().containsKey(str))
            throw new IllegalArgumentException("No group with name {" + str + "}"); 
          i = ((Integer)this.parentPattern.namedGroups().get(str)).intValue();
          b++;
        } else {
          i = c - '0';
          if (i < 0 || i > 9)
            throw new IllegalArgumentException("Illegal group reference"); 
          b++;
          boolean bool = false;
          while (!bool && b < paramString.length()) {
            char c1 = paramString.charAt(b) - '0';
            if (c1 < '\000' || c1 > '\t')
              break; 
            int j = i * 10 + c1;
            if (groupCount() < j) {
              bool = true;
              continue;
            } 
            i = j;
            b++;
          } 
        } 
        if (start(i) != -1 && end(i) != -1)
          stringBuilder.append(this.text, start(i), end(i)); 
        continue;
      } 
      stringBuilder.append(c);
      b++;
    } 
    paramStringBuffer.append(this.text, this.lastAppendPosition, this.first);
    paramStringBuffer.append(stringBuilder);
    this.lastAppendPosition = this.last;
    return this;
  }
  
  public StringBuffer appendTail(StringBuffer paramStringBuffer) {
    paramStringBuffer.append(this.text, this.lastAppendPosition, getTextLength());
    return paramStringBuffer;
  }
  
  public String replaceAll(String paramString) {
    reset();
    boolean bool = find();
    if (bool) {
      StringBuffer stringBuffer = new StringBuffer();
      do {
        appendReplacement(stringBuffer, paramString);
        bool = find();
      } while (bool);
      appendTail(stringBuffer);
      return stringBuffer.toString();
    } 
    return this.text.toString();
  }
  
  public String replaceFirst(String paramString) {
    if (paramString == null)
      throw new NullPointerException("replacement"); 
    reset();
    if (!find())
      return this.text.toString(); 
    StringBuffer stringBuffer = new StringBuffer();
    appendReplacement(stringBuffer, paramString);
    appendTail(stringBuffer);
    return stringBuffer.toString();
  }
  
  public Matcher region(int paramInt1, int paramInt2) {
    if (paramInt1 < 0 || paramInt1 > getTextLength())
      throw new IndexOutOfBoundsException("start"); 
    if (paramInt2 < 0 || paramInt2 > getTextLength())
      throw new IndexOutOfBoundsException("end"); 
    if (paramInt1 > paramInt2)
      throw new IndexOutOfBoundsException("start > end"); 
    reset();
    this.from = paramInt1;
    this.to = paramInt2;
    return this;
  }
  
  public int regionStart() { return this.from; }
  
  public int regionEnd() { return this.to; }
  
  public boolean hasTransparentBounds() { return this.transparentBounds; }
  
  public Matcher useTransparentBounds(boolean paramBoolean) {
    this.transparentBounds = paramBoolean;
    return this;
  }
  
  public boolean hasAnchoringBounds() { return this.anchoringBounds; }
  
  public Matcher useAnchoringBounds(boolean paramBoolean) {
    this.anchoringBounds = paramBoolean;
    return this;
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("java.util.regex.Matcher");
    stringBuilder.append("[pattern=" + pattern());
    stringBuilder.append(" region=");
    stringBuilder.append(regionStart() + "," + regionEnd());
    stringBuilder.append(" lastmatch=");
    if (this.first >= 0 && group() != null)
      stringBuilder.append(group()); 
    stringBuilder.append("]");
    return stringBuilder.toString();
  }
  
  public boolean hitEnd() { return this.hitEnd; }
  
  public boolean requireEnd() { return this.requireEnd; }
  
  boolean search(int paramInt) {
    this.hitEnd = false;
    this.requireEnd = false;
    paramInt = (paramInt < 0) ? 0 : paramInt;
    this.first = paramInt;
    this.oldLast = (this.oldLast < 0) ? paramInt : this.oldLast;
    for (byte b = 0; b < this.groups.length; b++)
      this.groups[b] = -1; 
    this.acceptMode = 0;
    boolean bool = this.parentPattern.root.match(this, paramInt, this.text);
    if (!bool)
      this.first = -1; 
    this.oldLast = this.last;
    return bool;
  }
  
  boolean match(int paramInt1, int paramInt2) {
    this.hitEnd = false;
    this.requireEnd = false;
    paramInt1 = (paramInt1 < 0) ? 0 : paramInt1;
    this.first = paramInt1;
    this.oldLast = (this.oldLast < 0) ? paramInt1 : this.oldLast;
    for (byte b = 0; b < this.groups.length; b++)
      this.groups[b] = -1; 
    this.acceptMode = paramInt2;
    boolean bool = this.parentPattern.matchRoot.match(this, paramInt1, this.text);
    if (!bool)
      this.first = -1; 
    this.oldLast = this.last;
    return bool;
  }
  
  int getTextLength() { return this.text.length(); }
  
  CharSequence getSubSequence(int paramInt1, int paramInt2) { return this.text.subSequence(paramInt1, paramInt2); }
  
  char charAt(int paramInt) { return this.text.charAt(paramInt); }
  
  int getMatchedGroupIndex(String paramString) {
    Objects.requireNonNull(paramString, "Group name");
    if (this.first < 0)
      throw new IllegalStateException("No match found"); 
    if (!this.parentPattern.namedGroups().containsKey(paramString))
      throw new IllegalArgumentException("No group with name <" + paramString + ">"); 
    return ((Integer)this.parentPattern.namedGroups().get(paramString)).intValue();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\regex\Matcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */