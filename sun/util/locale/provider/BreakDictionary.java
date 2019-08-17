package sun.util.locale.provider;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.MissingResourceException;
import sun.text.CompactByteArray;
import sun.text.SupplementaryCharacterData;

class BreakDictionary {
  private static int supportedVersion = 1;
  
  private CompactByteArray columnMap = null;
  
  private SupplementaryCharacterData supplementaryCharColumnMap = null;
  
  private int numCols;
  
  private int numColGroups;
  
  private short[] table = null;
  
  private short[] rowIndex = null;
  
  private int[] rowIndexFlags = null;
  
  private short[] rowIndexFlagsIndex = null;
  
  private byte[] rowIndexShifts = null;
  
  BreakDictionary(String paramString) throws IOException, MissingResourceException { readDictionaryFile(paramString); }
  
  private void readDictionaryFile(final String dictionaryName) throws IOException, MissingResourceException {
    BufferedInputStream bufferedInputStream;
    try {
      bufferedInputStream = (BufferedInputStream)AccessController.doPrivileged(new PrivilegedExceptionAction<BufferedInputStream>() {
            public BufferedInputStream run() throws Exception { return new BufferedInputStream(getClass().getResourceAsStream("/sun/text/resources/" + dictionaryName)); }
          });
    } catch (PrivilegedActionException privilegedActionException) {
      throw new InternalError(privilegedActionException.toString(), privilegedActionException);
    } 
    byte[] arrayOfByte1 = new byte[8];
    if (bufferedInputStream.read(arrayOfByte1) != 8)
      throw new MissingResourceException("Wrong data length", paramString, ""); 
    int i = RuleBasedBreakIterator.getInt(arrayOfByte1, 0);
    if (i != supportedVersion)
      throw new MissingResourceException("Dictionary version(" + i + ") is unsupported", paramString, ""); 
    int j = RuleBasedBreakIterator.getInt(arrayOfByte1, 4);
    arrayOfByte1 = new byte[j];
    if (bufferedInputStream.read(arrayOfByte1) != j)
      throw new MissingResourceException("Wrong data length", paramString, ""); 
    bufferedInputStream.close();
    byte b1 = 0;
    int k = RuleBasedBreakIterator.getInt(arrayOfByte1, b1);
    b1 += 4;
    short[] arrayOfShort = new short[k];
    byte b2 = 0;
    while (b2 < k) {
      arrayOfShort[b2] = RuleBasedBreakIterator.getShort(arrayOfByte1, b1);
      b2++;
      b1 += 2;
    } 
    k = RuleBasedBreakIterator.getInt(arrayOfByte1, b1);
    b1 += 4;
    byte[] arrayOfByte2 = new byte[k];
    byte b3 = 0;
    while (b3 < k) {
      arrayOfByte2[b3] = arrayOfByte1[b1];
      b3++;
      b1++;
    } 
    this.columnMap = new CompactByteArray(arrayOfShort, arrayOfByte2);
    this.numCols = RuleBasedBreakIterator.getInt(arrayOfByte1, b1);
    b1 += 4;
    this.numColGroups = RuleBasedBreakIterator.getInt(arrayOfByte1, b1);
    b1 += 4;
    k = RuleBasedBreakIterator.getInt(arrayOfByte1, b1);
    b1 += 4;
    this.rowIndex = new short[k];
    b3 = 0;
    while (b3 < k) {
      this.rowIndex[b3] = RuleBasedBreakIterator.getShort(arrayOfByte1, b1);
      b3++;
      b1 += 2;
    } 
    k = RuleBasedBreakIterator.getInt(arrayOfByte1, b1);
    b1 += 4;
    this.rowIndexFlagsIndex = new short[k];
    b3 = 0;
    while (b3 < k) {
      this.rowIndexFlagsIndex[b3] = RuleBasedBreakIterator.getShort(arrayOfByte1, b1);
      b3++;
      b1 += 2;
    } 
    k = RuleBasedBreakIterator.getInt(arrayOfByte1, b1);
    b1 += 4;
    this.rowIndexFlags = new int[k];
    b3 = 0;
    while (b3 < k) {
      this.rowIndexFlags[b3] = RuleBasedBreakIterator.getInt(arrayOfByte1, b1);
      b3++;
      b1 += 4;
    } 
    k = RuleBasedBreakIterator.getInt(arrayOfByte1, b1);
    b1 += 4;
    this.rowIndexShifts = new byte[k];
    b3 = 0;
    while (b3 < k) {
      this.rowIndexShifts[b3] = arrayOfByte1[b1];
      b3++;
      b1++;
    } 
    k = RuleBasedBreakIterator.getInt(arrayOfByte1, b1);
    b1 += 4;
    this.table = new short[k];
    b3 = 0;
    while (b3 < k) {
      this.table[b3] = RuleBasedBreakIterator.getShort(arrayOfByte1, b1);
      b3++;
      b1 += 2;
    } 
    k = RuleBasedBreakIterator.getInt(arrayOfByte1, b1);
    b1 += 4;
    int[] arrayOfInt = new int[k];
    byte b4 = 0;
    while (b4 < k) {
      arrayOfInt[b4] = RuleBasedBreakIterator.getInt(arrayOfByte1, b1);
      b4++;
      b1 += 4;
    } 
    this.supplementaryCharColumnMap = new SupplementaryCharacterData(arrayOfInt);
  }
  
  public final short getNextStateFromCharacter(int paramInt1, int paramInt2) {
    int i;
    if (paramInt2 < 65536) {
      i = this.columnMap.elementAt((char)paramInt2);
    } else {
      i = this.supplementaryCharColumnMap.getValue(paramInt2);
    } 
    return getNextState(paramInt1, i);
  }
  
  public final short getNextState(int paramInt1, int paramInt2) { return cellIsPopulated(paramInt1, paramInt2) ? internalAt(this.rowIndex[paramInt1], paramInt2 + this.rowIndexShifts[paramInt1]) : 0; }
  
  private boolean cellIsPopulated(int paramInt1, int paramInt2) {
    if (this.rowIndexFlagsIndex[paramInt1] < 0)
      return (paramInt2 == -this.rowIndexFlagsIndex[paramInt1]); 
    int i = this.rowIndexFlags[this.rowIndexFlagsIndex[paramInt1] + (paramInt2 >> 5)];
    return ((i & 1 << (paramInt2 & 0x1F)) != 0);
  }
  
  private short internalAt(int paramInt1, int paramInt2) { return this.table[paramInt1 * this.numCols + paramInt2]; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\su\\util\locale\provider\BreakDictionary.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */