package sun.util.locale.provider;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.text.BreakIterator;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.MissingResourceException;
import sun.text.CompactByteArray;
import sun.text.SupplementaryCharacterData;

class RuleBasedBreakIterator extends BreakIterator {
  protected static final byte IGNORE = -1;
  
  private static final short START_STATE = 1;
  
  private static final short STOP_STATE = 0;
  
  static final byte[] LABEL = { 66, 73, 100, 97, 116, 97, 0 };
  
  static final int LABEL_LENGTH = LABEL.length;
  
  static final byte supportedVersion = 1;
  
  private static final int HEADER_LENGTH = 36;
  
  private static final int BMP_INDICES_LENGTH = 512;
  
  private CompactByteArray charCategoryTable = null;
  
  private SupplementaryCharacterData supplementaryCharCategoryTable = null;
  
  private short[] stateTable = null;
  
  private short[] backwardsStateTable = null;
  
  private boolean[] endStates = null;
  
  private boolean[] lookaheadStates = null;
  
  private byte[] additionalData = null;
  
  private int numCategories;
  
  private CharacterIterator text = null;
  
  private long checksum;
  
  private int cachedLastKnownBreak = -1;
  
  RuleBasedBreakIterator(String paramString) throws IOException, MissingResourceException { readTables(paramString); }
  
  protected final void readTables(String paramString) throws IOException, MissingResourceException {
    byte[] arrayOfByte1 = readFile(paramString);
    int i = getInt(arrayOfByte1, 0);
    int j = getInt(arrayOfByte1, 4);
    int k = getInt(arrayOfByte1, 8);
    int m = getInt(arrayOfByte1, 12);
    int n = getInt(arrayOfByte1, 16);
    int i1 = getInt(arrayOfByte1, 20);
    int i2 = getInt(arrayOfByte1, 24);
    this.checksum = getLong(arrayOfByte1, 28);
    this.stateTable = new short[i];
    int i3 = 36;
    byte b1 = 0;
    while (b1 < i) {
      this.stateTable[b1] = getShort(arrayOfByte1, i3);
      b1++;
      i3 += 2;
    } 
    this.backwardsStateTable = new short[j];
    b1 = 0;
    while (b1 < j) {
      this.backwardsStateTable[b1] = getShort(arrayOfByte1, i3);
      b1++;
      i3 += 2;
    } 
    this.endStates = new boolean[k];
    b1 = 0;
    while (b1 < k) {
      this.endStates[b1] = (arrayOfByte1[i3] == 1);
      b1++;
      i3++;
    } 
    this.lookaheadStates = new boolean[m];
    b1 = 0;
    while (b1 < m) {
      this.lookaheadStates[b1] = (arrayOfByte1[i3] == 1);
      b1++;
      i3++;
    } 
    short[] arrayOfShort = new short[512];
    byte b2 = 0;
    while (b2 < 'Ȁ') {
      arrayOfShort[b2] = getShort(arrayOfByte1, i3);
      b2++;
      i3 += 2;
    } 
    byte[] arrayOfByte2 = new byte[n];
    System.arraycopy(arrayOfByte1, i3, arrayOfByte2, 0, n);
    i3 += n;
    this.charCategoryTable = new CompactByteArray(arrayOfShort, arrayOfByte2);
    int[] arrayOfInt = new int[i1];
    byte b3 = 0;
    while (b3 < i1) {
      arrayOfInt[b3] = getInt(arrayOfByte1, i3);
      b3++;
      i3 += 4;
    } 
    this.supplementaryCharCategoryTable = new SupplementaryCharacterData(arrayOfInt);
    if (i2 > 0) {
      this.additionalData = new byte[i2];
      System.arraycopy(arrayOfByte1, i3, this.additionalData, 0, i2);
    } 
    this.numCategories = this.stateTable.length / this.endStates.length;
  }
  
  protected byte[] readFile(final String datafile) throws IOException, MissingResourceException {
    BufferedInputStream bufferedInputStream;
    try {
      bufferedInputStream = (BufferedInputStream)AccessController.doPrivileged(new PrivilegedExceptionAction<BufferedInputStream>() {
            public BufferedInputStream run() throws Exception { return new BufferedInputStream(getClass().getResourceAsStream("/sun/text/resources/" + datafile)); }
          });
    } catch (PrivilegedActionException privilegedActionException) {
      throw new InternalError(privilegedActionException.toString(), privilegedActionException);
    } 
    byte b1 = 0;
    int i = LABEL_LENGTH + 5;
    byte[] arrayOfByte = new byte[i];
    if (bufferedInputStream.read(arrayOfByte) != i)
      throw new MissingResourceException("Wrong header length", paramString, ""); 
    byte b2 = 0;
    while (b2 < LABEL_LENGTH) {
      if (arrayOfByte[b1] != LABEL[b1])
        throw new MissingResourceException("Wrong magic number", paramString, ""); 
      b2++;
      b1++;
    } 
    if (arrayOfByte[b1] != 1)
      throw new MissingResourceException("Unsupported version(" + arrayOfByte[b1] + ")", paramString, ""); 
    i = getInt(arrayOfByte, ++b1);
    arrayOfByte = new byte[i];
    if (bufferedInputStream.read(arrayOfByte) != i)
      throw new MissingResourceException("Wrong data length", paramString, ""); 
    bufferedInputStream.close();
    return arrayOfByte;
  }
  
  byte[] getAdditionalData() { return this.additionalData; }
  
  void setAdditionalData(byte[] paramArrayOfByte) { this.additionalData = paramArrayOfByte; }
  
  public Object clone() {
    RuleBasedBreakIterator ruleBasedBreakIterator = (RuleBasedBreakIterator)super.clone();
    if (this.text != null)
      ruleBasedBreakIterator.text = (CharacterIterator)this.text.clone(); 
    return ruleBasedBreakIterator;
  }
  
  public boolean equals(Object paramObject) {
    try {
      if (paramObject == null)
        return false; 
      RuleBasedBreakIterator ruleBasedBreakIterator = (RuleBasedBreakIterator)paramObject;
      return (this.checksum != ruleBasedBreakIterator.checksum) ? false : ((this.text == null) ? ((ruleBasedBreakIterator.text == null)) : this.text.equals(ruleBasedBreakIterator.text));
    } catch (ClassCastException classCastException) {
      return false;
    } 
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append('[');
    stringBuilder.append("checksum=0x");
    stringBuilder.append(Long.toHexString(this.checksum));
    stringBuilder.append(']');
    return stringBuilder.toString();
  }
  
  public int hashCode() { return (int)this.checksum; }
  
  public int first() {
    CharacterIterator characterIterator = getText();
    characterIterator.first();
    return characterIterator.getIndex();
  }
  
  public int last() {
    CharacterIterator characterIterator = getText();
    characterIterator.setIndex(characterIterator.getEndIndex());
    return characterIterator.getIndex();
  }
  
  public int next(int paramInt) {
    int i = current();
    while (paramInt > 0) {
      i = handleNext();
      paramInt--;
    } 
    while (paramInt < 0) {
      i = previous();
      paramInt++;
    } 
    return i;
  }
  
  public int next() { return handleNext(); }
  
  public int previous() {
    CharacterIterator characterIterator = getText();
    if (current() == characterIterator.getBeginIndex())
      return -1; 
    int i = current();
    int j = this.cachedLastKnownBreak;
    if (j >= i || j <= -1) {
      getPrevious();
      j = handlePrevious();
    } else {
      characterIterator.setIndex(j);
    } 
    int k;
    for (k = j; k != -1 && k < i; k = handleNext())
      j = k; 
    characterIterator.setIndex(j);
    this.cachedLastKnownBreak = j;
    return j;
  }
  
  private int getPrevious() {
    char c = this.text.previous();
    if (Character.isLowSurrogate(c) && this.text.getIndex() > this.text.getBeginIndex()) {
      char c1 = this.text.previous();
      if (Character.isHighSurrogate(c1))
        return Character.toCodePoint(c1, c); 
      this.text.next();
    } 
    return c;
  }
  
  int getCurrent() {
    char c = this.text.current();
    if (Character.isHighSurrogate(c) && this.text.getIndex() < this.text.getEndIndex()) {
      char c1 = this.text.next();
      this.text.previous();
      if (Character.isLowSurrogate(c1))
        return Character.toCodePoint(c, c1); 
    } 
    return c;
  }
  
  private int getCurrentCodePointCount() {
    char c = this.text.current();
    if (Character.isHighSurrogate(c) && this.text.getIndex() < this.text.getEndIndex()) {
      char c1 = this.text.next();
      this.text.previous();
      if (Character.isLowSurrogate(c1))
        return 2; 
    } 
    return 1;
  }
  
  int getNext() {
    int i = this.text.getIndex();
    int j = this.text.getEndIndex();
    if (i == j || i += getCurrentCodePointCount() >= j)
      return 65535; 
    this.text.setIndex(i);
    return getCurrent();
  }
  
  private int getNextIndex() {
    int i = this.text.getIndex() + getCurrentCodePointCount();
    int j = this.text.getEndIndex();
    return (i > j) ? j : i;
  }
  
  protected static final void checkOffset(int paramInt, CharacterIterator paramCharacterIterator) {
    if (paramInt < paramCharacterIterator.getBeginIndex() || paramInt > paramCharacterIterator.getEndIndex())
      throw new IllegalArgumentException("offset out of bounds"); 
  }
  
  public int following(int paramInt) {
    CharacterIterator characterIterator = getText();
    checkOffset(paramInt, characterIterator);
    characterIterator.setIndex(paramInt);
    if (paramInt == characterIterator.getBeginIndex()) {
      this.cachedLastKnownBreak = handleNext();
      return this.cachedLastKnownBreak;
    } 
    int i = this.cachedLastKnownBreak;
    if (i >= paramInt || i <= -1) {
      i = handlePrevious();
    } else {
      characterIterator.setIndex(i);
    } 
    while (i != -1 && i <= paramInt)
      i = handleNext(); 
    this.cachedLastKnownBreak = i;
    return i;
  }
  
  public int preceding(int paramInt) {
    CharacterIterator characterIterator = getText();
    checkOffset(paramInt, characterIterator);
    characterIterator.setIndex(paramInt);
    return previous();
  }
  
  public boolean isBoundary(int paramInt) {
    CharacterIterator characterIterator = getText();
    checkOffset(paramInt, characterIterator);
    return (paramInt == characterIterator.getBeginIndex()) ? true : ((following(paramInt - 1) == paramInt));
  }
  
  public int current() { return getText().getIndex(); }
  
  public CharacterIterator getText() {
    if (this.text == null)
      this.text = new StringCharacterIterator(""); 
    return this.text;
  }
  
  public void setText(CharacterIterator paramCharacterIterator) {
    boolean bool;
    int i = paramCharacterIterator.getEndIndex();
    try {
      paramCharacterIterator.setIndex(i);
      bool = (paramCharacterIterator.getIndex() == i) ? 1 : 0;
    } catch (IllegalArgumentException illegalArgumentException) {
      bool = false;
    } 
    if (bool) {
      this.text = paramCharacterIterator;
    } else {
      this.text = new SafeCharIterator(paramCharacterIterator);
    } 
    this.text.first();
    this.cachedLastKnownBreak = -1;
  }
  
  protected int handleNext() {
    CharacterIterator characterIterator = getText();
    if (characterIterator.getIndex() == characterIterator.getEndIndex())
      return -1; 
    int i = getNextIndex();
    int j = 0;
    int k = 1;
    int m;
    for (m = getCurrent(); m != 65535 && k; m = getNext()) {
      int n = lookupCategory(m);
      if (n != -1)
        k = lookupState(k, n); 
      if (this.lookaheadStates[k]) {
        if (this.endStates[k]) {
          i = j;
        } else {
          j = getNextIndex();
        } 
      } else if (this.endStates[k]) {
        i = getNextIndex();
      } 
    } 
    if (m == 65535 && j == characterIterator.getEndIndex())
      i = j; 
    characterIterator.setIndex(i);
    return i;
  }
  
  protected int handlePrevious() {
    CharacterIterator characterIterator = getText();
    int i = 1;
    int j = 0;
    byte b = 0;
    int k;
    for (k = getCurrent(); k != 65535 && i; k = getPrevious()) {
      b = j;
      j = lookupCategory(k);
      if (j != -1)
        i = lookupBackwardState(i, j); 
    } 
    if (k != 65535)
      if (b != -1) {
        getNext();
        getNext();
      } else {
        getNext();
      }  
    return characterIterator.getIndex();
  }
  
  protected int lookupCategory(int paramInt) { return (paramInt < 65536) ? this.charCategoryTable.elementAt((char)paramInt) : this.supplementaryCharCategoryTable.getValue(paramInt); }
  
  protected int lookupState(int paramInt1, int paramInt2) { return this.stateTable[paramInt1 * this.numCategories + paramInt2]; }
  
  protected int lookupBackwardState(int paramInt1, int paramInt2) { return this.backwardsStateTable[paramInt1 * this.numCategories + paramInt2]; }
  
  static long getLong(byte[] paramArrayOfByte, int paramInt) {
    long l = (paramArrayOfByte[paramInt] & 0xFF);
    for (int i = 1; i < 8; i++)
      l = l << 8 | (paramArrayOfByte[paramInt + i] & 0xFF); 
    return l;
  }
  
  static int getInt(byte[] paramArrayOfByte, int paramInt) {
    byte b = paramArrayOfByte[paramInt] & 0xFF;
    for (int i = 1; i < 4; i++)
      b = b << 8 | paramArrayOfByte[paramInt + i] & 0xFF; 
    return b;
  }
  
  static short getShort(byte[] paramArrayOfByte, int paramInt) {
    null = (short)(paramArrayOfByte[paramInt] & 0xFF);
    return (short)(null << 8 | paramArrayOfByte[paramInt + 1] & 0xFF);
  }
  
  private static final class SafeCharIterator implements CharacterIterator, Cloneable {
    private CharacterIterator base;
    
    private int rangeStart;
    
    private int rangeLimit;
    
    private int currentIndex;
    
    SafeCharIterator(CharacterIterator param1CharacterIterator) {
      this.base = param1CharacterIterator;
      this.rangeStart = param1CharacterIterator.getBeginIndex();
      this.rangeLimit = param1CharacterIterator.getEndIndex();
      this.currentIndex = param1CharacterIterator.getIndex();
    }
    
    public char first() { return setIndex(this.rangeStart); }
    
    public char last() { return setIndex(this.rangeLimit - 1); }
    
    public char current() { return (this.currentIndex < this.rangeStart || this.currentIndex >= this.rangeLimit) ? Character.MAX_VALUE : this.base.setIndex(this.currentIndex); }
    
    public char next() {
      this.currentIndex++;
      if (this.currentIndex >= this.rangeLimit) {
        this.currentIndex = this.rangeLimit;
        return Character.MAX_VALUE;
      } 
      return this.base.setIndex(this.currentIndex);
    }
    
    public char previous() {
      this.currentIndex--;
      if (this.currentIndex < this.rangeStart) {
        this.currentIndex = this.rangeStart;
        return Character.MAX_VALUE;
      } 
      return this.base.setIndex(this.currentIndex);
    }
    
    public char setIndex(int param1Int) {
      if (param1Int < this.rangeStart || param1Int > this.rangeLimit)
        throw new IllegalArgumentException("Invalid position"); 
      this.currentIndex = param1Int;
      return current();
    }
    
    public int getBeginIndex() { return this.rangeStart; }
    
    public int getEndIndex() { return this.rangeLimit; }
    
    public int getIndex() { return this.currentIndex; }
    
    public Object clone() {
      SafeCharIterator safeCharIterator = null;
      try {
        safeCharIterator = (SafeCharIterator)super.clone();
      } catch (CloneNotSupportedException cloneNotSupportedException) {
        throw new Error("Clone not supported: " + cloneNotSupportedException);
      } 
      CharacterIterator characterIterator = (CharacterIterator)this.base.clone();
      safeCharIterator.base = characterIterator;
      return safeCharIterator;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\su\\util\locale\provider\RuleBasedBreakIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */