package java.text;

import java.util.ArrayList;

final class MergeCollation {
  ArrayList<PatternEntry> patterns = new ArrayList();
  
  private PatternEntry saveEntry = null;
  
  private PatternEntry lastEntry = null;
  
  private StringBuffer excess = new StringBuffer();
  
  private byte[] statusArray = new byte[8192];
  
  private final byte BITARRAYMASK = 1;
  
  private final int BYTEPOWER = 3;
  
  private final int BYTEMASK = 7;
  
  public MergeCollation(String paramString) throws ParseException {
    for (byte b = 0; b < this.statusArray.length; b++)
      this.statusArray[b] = 0; 
    setPattern(paramString);
  }
  
  public String getPattern() { return getPattern(true); }
  
  public String getPattern(boolean paramBoolean) {
    StringBuffer stringBuffer = new StringBuffer();
    PatternEntry patternEntry = null;
    ArrayList arrayList = null;
    byte b;
    for (b = 0; b < this.patterns.size(); b++) {
      PatternEntry patternEntry1 = (PatternEntry)this.patterns.get(b);
      if (patternEntry1.extension.length() != 0) {
        if (arrayList == null)
          arrayList = new ArrayList(); 
        arrayList.add(patternEntry1);
      } else {
        if (arrayList != null) {
          PatternEntry patternEntry2 = findLastWithNoExtension(b - 1);
          for (int i = arrayList.size() - 1; i >= 0; i--) {
            patternEntry = (PatternEntry)arrayList.get(i);
            patternEntry.addToBuffer(stringBuffer, false, paramBoolean, patternEntry2);
          } 
          arrayList = null;
        } 
        patternEntry1.addToBuffer(stringBuffer, false, paramBoolean, null);
      } 
    } 
    if (arrayList != null) {
      PatternEntry patternEntry1 = findLastWithNoExtension(b - 1);
      for (int i = arrayList.size() - 1; i >= 0; i--) {
        patternEntry = (PatternEntry)arrayList.get(i);
        patternEntry.addToBuffer(stringBuffer, false, paramBoolean, patternEntry1);
      } 
      arrayList = null;
    } 
    return stringBuffer.toString();
  }
  
  private final PatternEntry findLastWithNoExtension(int paramInt) {
    while (--paramInt >= 0) {
      PatternEntry patternEntry = (PatternEntry)this.patterns.get(paramInt);
      if (patternEntry.extension.length() == 0)
        return patternEntry; 
      paramInt--;
    } 
    return null;
  }
  
  public String emitPattern() { return emitPattern(true); }
  
  public String emitPattern(boolean paramBoolean) {
    StringBuffer stringBuffer = new StringBuffer();
    for (byte b = 0; b < this.patterns.size(); b++) {
      PatternEntry patternEntry = (PatternEntry)this.patterns.get(b);
      if (patternEntry != null)
        patternEntry.addToBuffer(stringBuffer, true, paramBoolean, null); 
    } 
    return stringBuffer.toString();
  }
  
  public void setPattern(String paramString) throws ParseException {
    this.patterns.clear();
    addPattern(paramString);
  }
  
  public void addPattern(String paramString) throws ParseException {
    if (paramString == null)
      return; 
    PatternEntry.Parser parser = new PatternEntry.Parser(paramString);
    for (PatternEntry patternEntry = parser.next(); patternEntry != null; patternEntry = parser.next())
      fixEntry(patternEntry); 
  }
  
  public int getCount() { return this.patterns.size(); }
  
  public PatternEntry getItemAt(int paramInt) { return (PatternEntry)this.patterns.get(paramInt); }
  
  private final void fixEntry(PatternEntry paramPatternEntry) throws ParseException {
    if (this.lastEntry != null && paramPatternEntry.chars.equals(this.lastEntry.chars) && paramPatternEntry.extension.equals(this.lastEntry.extension)) {
      if (paramPatternEntry.strength != 3 && paramPatternEntry.strength != -2)
        throw new ParseException("The entries " + this.lastEntry + " and " + paramPatternEntry + " are adjacent in the rules, but have conflicting strengths: A character can't be unequal to itself.", -1); 
      return;
    } 
    boolean bool = true;
    if (paramPatternEntry.strength != -2) {
      int i = -1;
      if (paramPatternEntry.chars.length() == 1) {
        char c1 = paramPatternEntry.chars.charAt(0);
        char c2 = c1 >> '\003';
        byte b1 = this.statusArray[c2];
        byte b2 = (byte)('\001' << (c1 & 0x7));
        if (b1 != 0 && (b1 & b2) != 0) {
          i = this.patterns.lastIndexOf(paramPatternEntry);
        } else {
          this.statusArray[c2] = (byte)(b1 | b2);
        } 
      } else {
        i = this.patterns.lastIndexOf(paramPatternEntry);
      } 
      if (i != -1)
        this.patterns.remove(i); 
      this.excess.setLength(0);
      int j = findLastEntry(this.lastEntry, this.excess);
      if (this.excess.length() != 0) {
        paramPatternEntry.extension = this.excess + paramPatternEntry.extension;
        if (j != this.patterns.size()) {
          this.lastEntry = this.saveEntry;
          bool = false;
        } 
      } 
      if (j == this.patterns.size()) {
        this.patterns.add(paramPatternEntry);
        this.saveEntry = paramPatternEntry;
      } else {
        this.patterns.add(j, paramPatternEntry);
      } 
    } 
    if (bool)
      this.lastEntry = paramPatternEntry; 
  }
  
  private final int findLastEntry(PatternEntry paramPatternEntry, StringBuffer paramStringBuffer) throws ParseException {
    if (paramPatternEntry == null)
      return 0; 
    if (paramPatternEntry.strength != -2) {
      int j = -1;
      if (paramPatternEntry.chars.length() == 1) {
        char c = paramPatternEntry.chars.charAt(0) >> '\003';
        if ((this.statusArray[c] & '\001' << (paramPatternEntry.chars.charAt(0) & 0x7)) != 0)
          j = this.patterns.lastIndexOf(paramPatternEntry); 
      } else {
        j = this.patterns.lastIndexOf(paramPatternEntry);
      } 
      if (j == -1)
        throw new ParseException("couldn't find last entry: " + paramPatternEntry, j); 
      return j + 1;
    } 
    int i;
    for (i = this.patterns.size() - 1; i >= 0; i--) {
      PatternEntry patternEntry = (PatternEntry)this.patterns.get(i);
      if (patternEntry.chars.regionMatches(0, paramPatternEntry.chars, 0, patternEntry.chars.length())) {
        paramStringBuffer.append(paramPatternEntry.chars.substring(patternEntry.chars.length(), paramPatternEntry.chars.length()));
        break;
      } 
    } 
    if (i == -1)
      throw new ParseException("couldn't find: " + paramPatternEntry, i); 
    return i + 1;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\text\MergeCollation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */