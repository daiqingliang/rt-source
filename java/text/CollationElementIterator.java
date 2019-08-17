package java.text;

import java.util.Vector;
import sun.text.CollatorUtilities;
import sun.text.normalizer.NormalizerBase;

public final class CollationElementIterator {
  public static final int NULLORDER = -1;
  
  static final int UNMAPPEDCHARVALUE = 2147418112;
  
  private NormalizerBase text = null;
  
  private int[] buffer = null;
  
  private int expIndex = 0;
  
  private StringBuffer key = new StringBuffer(5);
  
  private int swapOrder = 0;
  
  private RBCollationTables ordering;
  
  private RuleBasedCollator owner;
  
  CollationElementIterator(String paramString, RuleBasedCollator paramRuleBasedCollator) {
    this.owner = paramRuleBasedCollator;
    this.ordering = paramRuleBasedCollator.getTables();
    if (paramString.length() != 0) {
      NormalizerBase.Mode mode = CollatorUtilities.toNormalizerMode(paramRuleBasedCollator.getDecomposition());
      this.text = new NormalizerBase(paramString, mode);
    } 
  }
  
  CollationElementIterator(CharacterIterator paramCharacterIterator, RuleBasedCollator paramRuleBasedCollator) {
    this.owner = paramRuleBasedCollator;
    this.ordering = paramRuleBasedCollator.getTables();
    NormalizerBase.Mode mode = CollatorUtilities.toNormalizerMode(paramRuleBasedCollator.getDecomposition());
    this.text = new NormalizerBase(paramCharacterIterator, mode);
  }
  
  public void reset() {
    if (this.text != null) {
      this.text.reset();
      NormalizerBase.Mode mode = CollatorUtilities.toNormalizerMode(this.owner.getDecomposition());
      this.text.setMode(mode);
    } 
    this.buffer = null;
    this.expIndex = 0;
    this.swapOrder = 0;
  }
  
  public int next() {
    if (this.text == null)
      return -1; 
    NormalizerBase.Mode mode1 = this.text.getMode();
    NormalizerBase.Mode mode2 = CollatorUtilities.toNormalizerMode(this.owner.getDecomposition());
    if (mode1 != mode2)
      this.text.setMode(mode2); 
    if (this.buffer != null) {
      if (this.expIndex < this.buffer.length)
        return strengthOrder(this.buffer[this.expIndex++]); 
      this.buffer = null;
      this.expIndex = 0;
    } else if (this.swapOrder != 0) {
      if (Character.isSupplementaryCodePoint(this.swapOrder)) {
        char[] arrayOfChar = Character.toChars(this.swapOrder);
        this.swapOrder = arrayOfChar[1];
        return arrayOfChar[0] << '\020';
      } 
      int k = this.swapOrder << 16;
      this.swapOrder = 0;
      return k;
    } 
    int i = this.text.next();
    if (i == -1)
      return -1; 
    int j = this.ordering.getUnicodeOrder(i);
    if (j == -1) {
      this.swapOrder = i;
      return 2147418112;
    } 
    if (j >= 2130706432)
      j = nextContractChar(i); 
    if (j >= 2113929216) {
      this.buffer = this.ordering.getExpandValueList(j);
      this.expIndex = 0;
      j = this.buffer[this.expIndex++];
    } 
    if (this.ordering.isSEAsianSwapping()) {
      if (isThaiPreVowel(i)) {
        int k = this.text.next();
        if (isThaiBaseConsonant(k)) {
          this.buffer = makeReorderedBuffer(k, j, this.buffer, true);
          j = this.buffer[0];
          this.expIndex = 1;
        } else if (k != -1) {
          this.text.previous();
        } 
      } 
      if (isLaoPreVowel(i)) {
        int k = this.text.next();
        if (isLaoBaseConsonant(k)) {
          this.buffer = makeReorderedBuffer(k, j, this.buffer, true);
          j = this.buffer[0];
          this.expIndex = 1;
        } else if (k != -1) {
          this.text.previous();
        } 
      } 
    } 
    return strengthOrder(j);
  }
  
  public int previous() {
    if (this.text == null)
      return -1; 
    NormalizerBase.Mode mode1 = this.text.getMode();
    NormalizerBase.Mode mode2 = CollatorUtilities.toNormalizerMode(this.owner.getDecomposition());
    if (mode1 != mode2)
      this.text.setMode(mode2); 
    if (this.buffer != null) {
      if (this.expIndex > 0)
        return strengthOrder(this.buffer[--this.expIndex]); 
      this.buffer = null;
      this.expIndex = 0;
    } else if (this.swapOrder != 0) {
      if (Character.isSupplementaryCodePoint(this.swapOrder)) {
        char[] arrayOfChar = Character.toChars(this.swapOrder);
        this.swapOrder = arrayOfChar[1];
        return arrayOfChar[0] << '\020';
      } 
      int k = this.swapOrder << 16;
      this.swapOrder = 0;
      return k;
    } 
    int i = this.text.previous();
    if (i == -1)
      return -1; 
    int j = this.ordering.getUnicodeOrder(i);
    if (j == -1) {
      this.swapOrder = 2147418112;
      return i;
    } 
    if (j >= 2130706432)
      j = prevContractChar(i); 
    if (j >= 2113929216) {
      this.buffer = this.ordering.getExpandValueList(j);
      this.expIndex = this.buffer.length;
      j = this.buffer[--this.expIndex];
    } 
    if (this.ordering.isSEAsianSwapping()) {
      if (isThaiBaseConsonant(i)) {
        int k = this.text.previous();
        if (isThaiPreVowel(k)) {
          this.buffer = makeReorderedBuffer(k, j, this.buffer, false);
          this.expIndex = this.buffer.length - 1;
          j = this.buffer[this.expIndex];
        } else {
          this.text.next();
        } 
      } 
      if (isLaoBaseConsonant(i)) {
        int k = this.text.previous();
        if (isLaoPreVowel(k)) {
          this.buffer = makeReorderedBuffer(k, j, this.buffer, false);
          this.expIndex = this.buffer.length - 1;
          j = this.buffer[this.expIndex];
        } else {
          this.text.next();
        } 
      } 
    } 
    return strengthOrder(j);
  }
  
  public static final int primaryOrder(int paramInt) {
    paramInt &= 0xFFFF0000;
    return paramInt >>> 16;
  }
  
  public static final short secondaryOrder(int paramInt) {
    paramInt &= 0xFF00;
    return (short)(paramInt >> 8);
  }
  
  public static final short tertiaryOrder(int paramInt) { return (short)(paramInt &= 0xFF); }
  
  final int strengthOrder(int paramInt) {
    int i = this.owner.getStrength();
    if (i == 0) {
      paramInt &= 0xFFFF0000;
    } else if (i == 1) {
      paramInt &= 0xFFFFFF00;
    } 
    return paramInt;
  }
  
  public void setOffset(int paramInt) {
    if (this.text != null)
      if (paramInt < this.text.getBeginIndex() || paramInt >= this.text.getEndIndex()) {
        this.text.setIndexOnly(paramInt);
      } else {
        int i = this.text.setIndex(paramInt);
        if (this.ordering.usedInContractSeq(i)) {
          while (this.ordering.usedInContractSeq(i))
            i = this.text.previous(); 
          int j = this.text.getIndex();
          while (this.text.getIndex() <= paramInt) {
            j = this.text.getIndex();
            next();
          } 
          this.text.setIndexOnly(j);
        } 
      }  
    this.buffer = null;
    this.expIndex = 0;
    this.swapOrder = 0;
  }
  
  public int getOffset() { return (this.text != null) ? this.text.getIndex() : 0; }
  
  public int getMaxExpansion(int paramInt) { return this.ordering.getMaxExpansion(paramInt); }
  
  public void setText(String paramString) {
    this.buffer = null;
    this.swapOrder = 0;
    this.expIndex = 0;
    NormalizerBase.Mode mode = CollatorUtilities.toNormalizerMode(this.owner.getDecomposition());
    if (this.text == null) {
      this.text = new NormalizerBase(paramString, mode);
    } else {
      this.text.setMode(mode);
      this.text.setText(paramString);
    } 
  }
  
  public void setText(CharacterIterator paramCharacterIterator) {
    this.buffer = null;
    this.swapOrder = 0;
    this.expIndex = 0;
    NormalizerBase.Mode mode = CollatorUtilities.toNormalizerMode(this.owner.getDecomposition());
    if (this.text == null) {
      this.text = new NormalizerBase(paramCharacterIterator, mode);
    } else {
      this.text.setMode(mode);
      this.text.setText(paramCharacterIterator);
    } 
  }
  
  private static final boolean isThaiPreVowel(int paramInt) { return (paramInt >= 3648 && paramInt <= 3652); }
  
  private static final boolean isThaiBaseConsonant(int paramInt) { return (paramInt >= 3585 && paramInt <= 3630); }
  
  private static final boolean isLaoPreVowel(int paramInt) { return (paramInt >= 3776 && paramInt <= 3780); }
  
  private static final boolean isLaoBaseConsonant(int paramInt) { return (paramInt >= 3713 && paramInt <= 3758); }
  
  private int[] makeReorderedBuffer(int paramInt1, int paramInt2, int[] paramArrayOfInt, boolean paramBoolean) {
    int[] arrayOfInt1;
    int i = this.ordering.getUnicodeOrder(paramInt1);
    if (i >= 2130706432)
      i = paramBoolean ? nextContractChar(paramInt1) : prevContractChar(paramInt1); 
    int[] arrayOfInt2 = null;
    if (i >= 2113929216)
      arrayOfInt2 = this.ordering.getExpandValueList(i); 
    if (!paramBoolean) {
      int j = i;
      i = paramInt2;
      paramInt2 = j;
      int[] arrayOfInt = arrayOfInt2;
      arrayOfInt2 = paramArrayOfInt;
      paramArrayOfInt = arrayOfInt;
    } 
    if (arrayOfInt2 == null && paramArrayOfInt == null) {
      arrayOfInt1 = new int[2];
      arrayOfInt1[0] = i;
      arrayOfInt1[1] = paramInt2;
    } else {
      byte b1 = (arrayOfInt2 == null) ? 1 : arrayOfInt2.length;
      byte b2 = (paramArrayOfInt == null) ? 1 : paramArrayOfInt.length;
      arrayOfInt1 = new int[b1 + b2];
      if (arrayOfInt2 == null) {
        arrayOfInt1[0] = i;
      } else {
        System.arraycopy(arrayOfInt2, 0, arrayOfInt1, 0, b1);
      } 
      if (paramArrayOfInt == null) {
        arrayOfInt1[b1] = paramInt2;
      } else {
        System.arraycopy(paramArrayOfInt, 0, arrayOfInt1, b1, b2);
      } 
    } 
    return arrayOfInt1;
  }
  
  static final boolean isIgnorable(int paramInt) { return (primaryOrder(paramInt) == 0); }
  
  private int nextContractChar(int paramInt) {
    Vector vector = this.ordering.getContractValues(paramInt);
    EntryPair entryPair = (EntryPair)vector.firstElement();
    int i = entryPair.value;
    entryPair = (EntryPair)vector.lastElement();
    int j = entryPair.entryName.length();
    NormalizerBase normalizerBase = (NormalizerBase)this.text.clone();
    normalizerBase.previous();
    this.key.setLength(0);
    int k;
    for (k = normalizerBase.next(); j > 0 && k != -1; k = normalizerBase.next()) {
      if (Character.isSupplementaryCodePoint(k)) {
        this.key.append(Character.toChars(k));
        j -= 2;
      } else {
        this.key.append((char)k);
        j--;
      } 
    } 
    String str = this.key.toString();
    j = 1;
    for (int m = vector.size() - 1; m > 0; m--) {
      entryPair = (EntryPair)vector.elementAt(m);
      if (entryPair.fwd && str.startsWith(entryPair.entryName) && entryPair.entryName.length() > j) {
        j = entryPair.entryName.length();
        i = entryPair.value;
      } 
    } 
    while (j > 1) {
      k = this.text.next();
      j -= Character.charCount(k);
    } 
    return i;
  }
  
  private int prevContractChar(int paramInt) {
    Vector vector = this.ordering.getContractValues(paramInt);
    EntryPair entryPair = (EntryPair)vector.firstElement();
    int i = entryPair.value;
    entryPair = (EntryPair)vector.lastElement();
    int j = entryPair.entryName.length();
    NormalizerBase normalizerBase = (NormalizerBase)this.text.clone();
    normalizerBase.next();
    this.key.setLength(0);
    int k;
    for (k = normalizerBase.previous(); j > 0 && k != -1; k = normalizerBase.previous()) {
      if (Character.isSupplementaryCodePoint(k)) {
        this.key.append(Character.toChars(k));
        j -= 2;
      } else {
        this.key.append((char)k);
        j--;
      } 
    } 
    String str = this.key.toString();
    j = 1;
    for (int m = vector.size() - 1; m > 0; m--) {
      entryPair = (EntryPair)vector.elementAt(m);
      if (!entryPair.fwd && str.startsWith(entryPair.entryName) && entryPair.entryName.length() > j) {
        j = entryPair.entryName.length();
        i = entryPair.value;
      } 
    } 
    while (j > 1) {
      k = this.text.previous();
      j -= Character.charCount(k);
    } 
    return i;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\text\CollationElementIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */