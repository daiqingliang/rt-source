package java.text;

import java.util.Vector;
import sun.text.ComposedCharIter;
import sun.text.IntHashtable;
import sun.text.UCompactIntArray;
import sun.text.normalizer.NormalizerImpl;

final class RBTableBuilder {
  static final int CHARINDEX = 1879048192;
  
  private static final int IGNORABLEMASK = 65535;
  
  private static final int PRIMARYORDERINCREMENT = 65536;
  
  private static final int SECONDARYORDERINCREMENT = 256;
  
  private static final int TERTIARYORDERINCREMENT = 1;
  
  private static final int INITIALTABLESIZE = 20;
  
  private static final int MAXKEYSIZE = 5;
  
  private RBCollationTables.BuildAPI tables = null;
  
  private MergeCollation mPattern = null;
  
  private boolean isOverIgnore = false;
  
  private char[] keyBuf = new char[5];
  
  private IntHashtable contractFlags = new IntHashtable(100);
  
  private boolean frenchSec = false;
  
  private boolean seAsianSwapping = false;
  
  private UCompactIntArray mapping = null;
  
  private Vector<Vector<EntryPair>> contractTable = null;
  
  private Vector<int[]> expandTable = null;
  
  private short maxSecOrder = 0;
  
  private short maxTerOrder = 0;
  
  public RBTableBuilder(RBCollationTables.BuildAPI paramBuildAPI) { this.tables = paramBuildAPI; }
  
  public void build(String paramString, int paramInt) throws ParseException {
    boolean bool = true;
    byte b = 0;
    if (paramString.length() == 0)
      throw new ParseException("Build rules empty.", 0); 
    this.mapping = new UCompactIntArray(-1);
    paramString = NormalizerImpl.canonicalDecomposeWithSingleQuotation(paramString);
    this.mPattern = new MergeCollation(paramString);
    int i = 0;
    for (b = 0; b < this.mPattern.getCount(); b++) {
      PatternEntry patternEntry = this.mPattern.getItemAt(b);
      if (patternEntry != null) {
        String str2 = patternEntry.getChars();
        if (str2.length() > 1)
          switch (str2.charAt(str2.length() - 1)) {
            case '@':
              this.frenchSec = true;
              str2 = str2.substring(0, str2.length() - 1);
              break;
            case '!':
              this.seAsianSwapping = true;
              str2 = str2.substring(0, str2.length() - 1);
              break;
          }  
        i = increment(patternEntry.getStrength(), i);
        String str1 = patternEntry.getExtension();
        if (str1.length() != 0) {
          addExpandOrder(str2, str1, i);
        } else if (str2.length() > 1) {
          char c = str2.charAt(0);
          if (Character.isHighSurrogate(c) && str2.length() == 2) {
            addOrder(Character.toCodePoint(c, str2.charAt(1)), i);
          } else {
            addContractOrder(str2, i);
          } 
        } else {
          char c = str2.charAt(0);
          addOrder(c, i);
        } 
      } 
    } 
    addComposedChars();
    commit();
    this.mapping.compact();
    this.tables.fillInTables(this.frenchSec, this.seAsianSwapping, this.mapping, this.contractTable, this.expandTable, this.contractFlags, this.maxSecOrder, this.maxTerOrder);
  }
  
  private void addComposedChars() throws ParseException {
    ComposedCharIter composedCharIter = new ComposedCharIter();
    int i;
    while ((i = composedCharIter.next()) != -1) {
      if (getCharOrder(i) == -1) {
        String str = composedCharIter.decomposition();
        if (str.length() == 1) {
          int k = getCharOrder(str.charAt(0));
          if (k != -1)
            addOrder(i, k); 
          continue;
        } 
        if (str.length() == 2) {
          char c = str.charAt(0);
          if (Character.isHighSurrogate(c)) {
            int k = getCharOrder(str.codePointAt(0));
            if (k != -1)
              addOrder(i, k); 
            continue;
          } 
        } 
        int j = getContractOrder(str);
        if (j != -1) {
          addOrder(i, j);
          continue;
        } 
        boolean bool = true;
        for (byte b = 0; b < str.length(); b++) {
          if (getCharOrder(str.charAt(b)) == -1) {
            bool = false;
            break;
          } 
        } 
        if (bool)
          addExpandOrder(i, str, -1); 
      } 
    } 
  }
  
  private final void commit() throws ParseException {
    if (this.expandTable != null)
      for (byte b = 0; b < this.expandTable.size(); b++) {
        int[] arrayOfInt = (int[])this.expandTable.elementAt(b);
        for (byte b1 = 0; b1 < arrayOfInt.length; b1++) {
          int i = arrayOfInt[b1];
          if (i < 2113929216 && i > 1879048192) {
            int j = i - 1879048192;
            int k = getCharOrder(j);
            if (k == -1) {
              arrayOfInt[b1] = 0xFFFF & j;
            } else {
              arrayOfInt[b1] = k;
            } 
          } 
        } 
      }  
  }
  
  private final int increment(int paramInt1, int paramInt2) {
    switch (paramInt1) {
      case 0:
        paramInt2 += 65536;
        paramInt2 &= 0xFFFF0000;
        this.isOverIgnore = true;
        break;
      case 1:
        paramInt2 += 256;
        paramInt2 &= 0xFFFFFF00;
        if (!this.isOverIgnore)
          this.maxSecOrder = (short)(this.maxSecOrder + 1); 
        break;
      case 2:
        paramInt2++;
        if (!this.isOverIgnore)
          this.maxTerOrder = (short)(this.maxTerOrder + 1); 
        break;
    } 
    return paramInt2;
  }
  
  private final void addOrder(int paramInt1, int paramInt2) {
    int i = this.mapping.elementAt(paramInt1);
    if (i >= 2130706432) {
      int j = 1;
      if (Character.isSupplementaryCodePoint(paramInt1)) {
        j = Character.toChars(paramInt1, this.keyBuf, 0);
      } else {
        this.keyBuf[0] = (char)paramInt1;
      } 
      addContractOrder(new String(this.keyBuf, 0, j), paramInt2);
    } else {
      this.mapping.setElementAt(paramInt1, paramInt2);
    } 
  }
  
  private final void addContractOrder(String paramString, int paramInt) throws ParseException { addContractOrder(paramString, paramInt, true); }
  
  private final void addContractOrder(String paramString, int paramInt, boolean paramBoolean) {
    if (this.contractTable == null)
      this.contractTable = new Vector(20); 
    int i = paramString.codePointAt(0);
    int j = this.mapping.elementAt(i);
    Vector vector = getContractValuesImpl(j - 2130706432);
    if (vector == null) {
      int m = 2130706432 + this.contractTable.size();
      vector = new Vector(20);
      this.contractTable.addElement(vector);
      vector.addElement(new EntryPair(paramString.substring(0, Character.charCount(i)), j));
      this.mapping.setElementAt(i, m);
    } 
    int k = RBCollationTables.getEntry(vector, paramString, paramBoolean);
    if (k != -1) {
      EntryPair entryPair = (EntryPair)vector.elementAt(k);
      entryPair.value = paramInt;
    } else {
      EntryPair entryPair = (EntryPair)vector.lastElement();
      if (paramString.length() > entryPair.entryName.length()) {
        vector.addElement(new EntryPair(paramString, paramInt, paramBoolean));
      } else {
        vector.insertElementAt(new EntryPair(paramString, paramInt, paramBoolean), vector.size() - 1);
      } 
    } 
    if (paramBoolean && paramString.length() > 1) {
      addContractFlags(paramString);
      addContractOrder((new StringBuffer(paramString)).reverse().toString(), paramInt, false);
    } 
  }
  
  private int getContractOrder(String paramString) {
    int i = -1;
    if (this.contractTable != null) {
      int j = paramString.codePointAt(0);
      Vector vector = getContractValues(j);
      if (vector != null) {
        int k = RBCollationTables.getEntry(vector, paramString, true);
        if (k != -1) {
          EntryPair entryPair = (EntryPair)vector.elementAt(k);
          i = entryPair.value;
        } 
      } 
    } 
    return i;
  }
  
  private final int getCharOrder(int paramInt) {
    int i = this.mapping.elementAt(paramInt);
    if (i >= 2130706432) {
      Vector vector = getContractValuesImpl(i - 2130706432);
      EntryPair entryPair = (EntryPair)vector.firstElement();
      i = entryPair.value;
    } 
    return i;
  }
  
  private Vector<EntryPair> getContractValues(int paramInt) {
    int i = this.mapping.elementAt(paramInt);
    return getContractValuesImpl(i - 2130706432);
  }
  
  private Vector<EntryPair> getContractValuesImpl(int paramInt) { return (paramInt >= 0) ? (Vector)this.contractTable.elementAt(paramInt) : null; }
  
  private final void addExpandOrder(String paramString1, String paramString2, int paramInt) throws ParseException {
    int i = addExpansion(paramInt, paramString2);
    if (paramString1.length() > 1) {
      char c = paramString1.charAt(0);
      if (Character.isHighSurrogate(c) && paramString1.length() == 2) {
        char c1 = paramString1.charAt(1);
        if (Character.isLowSurrogate(c1))
          addOrder(Character.toCodePoint(c, c1), i); 
      } else {
        addContractOrder(paramString1, i);
      } 
    } else {
      addOrder(paramString1.charAt(0), i);
    } 
  }
  
  private final void addExpandOrder(int paramInt1, String paramString, int paramInt2) throws ParseException {
    int i = addExpansion(paramInt2, paramString);
    addOrder(paramInt1, i);
  }
  
  private int addExpansion(int paramInt, String paramString) {
    if (this.expandTable == null)
      this.expandTable = new Vector(20); 
    int i = (paramInt == -1) ? 0 : 1;
    int[] arrayOfInt = new int[paramString.length() + i];
    if (i == 1)
      arrayOfInt[0] = paramInt; 
    int j = i;
    int k;
    for (k = 0; k < paramString.length(); k++) {
      char c1;
      char c = paramString.charAt(k);
      if (Character.isHighSurrogate(c)) {
        char c2;
        if (++k == paramString.length() || !Character.isLowSurrogate(c2 = paramString.charAt(k)))
          break; 
        c1 = Character.toCodePoint(c, c2);
      } else {
        c1 = c;
      } 
      int m = getCharOrder(c1);
      if (m != -1) {
        arrayOfInt[j++] = m;
      } else {
        arrayOfInt[j++] = 1879048192 + c1;
      } 
    } 
    if (j < arrayOfInt.length) {
      int[] arrayOfInt1 = new int[j];
      while (--j >= 0)
        arrayOfInt1[j] = arrayOfInt[j]; 
      arrayOfInt = arrayOfInt1;
    } 
    k = 2113929216 + this.expandTable.size();
    this.expandTable.addElement(arrayOfInt);
    return k;
  }
  
  private void addContractFlags(String paramString) {
    int i = paramString.length();
    for (byte b = 0; b < i; b++) {
      char c = paramString.charAt(b);
      int j = Character.isHighSurrogate(c) ? Character.toCodePoint(c, paramString.charAt(++b)) : c;
      this.contractFlags.put(j, 1);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\text\RBTableBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */