package sun.nio.cs;

import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Comparator;

public class CharsetMapping {
  public static final char UNMAPPABLE_DECODING = '�';
  
  public static final int UNMAPPABLE_ENCODING = 65533;
  
  char[] b2cSB;
  
  char[] b2cDB1;
  
  char[] b2cDB2;
  
  int b2Min;
  
  int b2Max;
  
  int b1MinDB1;
  
  int b1MaxDB1;
  
  int b1MinDB2;
  
  int b1MaxDB2;
  
  int dbSegSize;
  
  char[] c2b;
  
  char[] c2bIndex;
  
  char[] b2cSupp;
  
  char[] c2bSupp;
  
  Entry[] b2cComp;
  
  Entry[] c2bComp;
  
  static Comparator<Entry> comparatorBytes = new Comparator<Entry>() {
      public int compare(CharsetMapping.Entry param1Entry1, CharsetMapping.Entry param1Entry2) { return param1Entry1.bs - param1Entry2.bs; }
      
      public boolean equals(Object param1Object) { return (this == param1Object); }
    };
  
  static Comparator<Entry> comparatorCP = new Comparator<Entry>() {
      public int compare(CharsetMapping.Entry param1Entry1, CharsetMapping.Entry param1Entry2) { return param1Entry1.cp - param1Entry2.cp; }
      
      public boolean equals(Object param1Object) { return (this == param1Object); }
    };
  
  static Comparator<Entry> comparatorComp = new Comparator<Entry>() {
      public int compare(CharsetMapping.Entry param1Entry1, CharsetMapping.Entry param1Entry2) {
        int i = param1Entry1.cp - param1Entry2.cp;
        if (i == 0)
          i = param1Entry1.cp2 - param1Entry2.cp2; 
        return i;
      }
      
      public boolean equals(Object param1Object) { return (this == param1Object); }
    };
  
  private static final int MAP_SINGLEBYTE = 1;
  
  private static final int MAP_DOUBLEBYTE1 = 2;
  
  private static final int MAP_DOUBLEBYTE2 = 3;
  
  private static final int MAP_SUPPLEMENT = 5;
  
  private static final int MAP_SUPPLEMENT_C2B = 6;
  
  private static final int MAP_COMPOSITE = 7;
  
  private static final int MAP_INDEXC2B = 8;
  
  int off = 0;
  
  byte[] bb;
  
  public char decodeSingle(int paramInt) { return this.b2cSB[paramInt]; }
  
  public char decodeDouble(int paramInt1, int paramInt2) {
    if (paramInt2 >= this.b2Min && paramInt2 < this.b2Max) {
      paramInt2 -= this.b2Min;
      if (paramInt1 >= this.b1MinDB1 && paramInt1 <= this.b1MaxDB1) {
        paramInt1 -= this.b1MinDB1;
        return this.b2cDB1[paramInt1 * this.dbSegSize + paramInt2];
      } 
      if (paramInt1 >= this.b1MinDB2 && paramInt1 <= this.b1MaxDB2) {
        paramInt1 -= this.b1MinDB2;
        return this.b2cDB2[paramInt1 * this.dbSegSize + paramInt2];
      } 
    } 
    return '�';
  }
  
  public char[] decodeSurrogate(int paramInt, char[] paramArrayOfChar) {
    int i = this.b2cSupp.length / 2;
    int j = Arrays.binarySearch(this.b2cSupp, 0, i, (char)paramInt);
    if (j >= 0) {
      Character.toChars(this.b2cSupp[i + j] + 131072, paramArrayOfChar, 0);
      return paramArrayOfChar;
    } 
    return null;
  }
  
  public char[] decodeComposite(Entry paramEntry, char[] paramArrayOfChar) {
    int i = findBytes(this.b2cComp, paramEntry);
    if (i >= 0) {
      paramArrayOfChar[0] = (char)(this.b2cComp[i]).cp;
      paramArrayOfChar[1] = (char)(this.b2cComp[i]).cp2;
      return paramArrayOfChar;
    } 
    return null;
  }
  
  public int encodeChar(char paramChar) {
    char c = this.c2bIndex[paramChar >> '\b'];
    return (c == Character.MAX_VALUE) ? 65533 : this.c2b[c + (paramChar & 0xFF)];
  }
  
  public int encodeSurrogate(char paramChar1, char paramChar2) {
    int i = Character.toCodePoint(paramChar1, paramChar2);
    if (i < 131072 || i >= 196608)
      return 65533; 
    int j = this.c2bSupp.length / 2;
    int k = Arrays.binarySearch(this.c2bSupp, 0, j, (char)i);
    return (k >= 0) ? this.c2bSupp[j + k] : 65533;
  }
  
  public boolean isCompositeBase(Entry paramEntry) { return (paramEntry.cp <= 12791 && paramEntry.cp >= 230) ? ((findCP(this.c2bComp, paramEntry) >= 0)) : false; }
  
  public int encodeComposite(Entry paramEntry) {
    int i = findComp(this.c2bComp, paramEntry);
    return (i >= 0) ? (this.c2bComp[i]).bs : 65533;
  }
  
  public static CharsetMapping get(final InputStream is) { return (CharsetMapping)AccessController.doPrivileged(new PrivilegedAction<CharsetMapping>() {
          public CharsetMapping run() { return (new CharsetMapping()).load(is); }
        }); }
  
  static int findBytes(Entry[] paramArrayOfEntry, Entry paramEntry) { return Arrays.binarySearch(paramArrayOfEntry, 0, paramArrayOfEntry.length, paramEntry, comparatorBytes); }
  
  static int findCP(Entry[] paramArrayOfEntry, Entry paramEntry) { return Arrays.binarySearch(paramArrayOfEntry, 0, paramArrayOfEntry.length, paramEntry, comparatorCP); }
  
  static int findComp(Entry[] paramArrayOfEntry, Entry paramEntry) { return Arrays.binarySearch(paramArrayOfEntry, 0, paramArrayOfEntry.length, paramEntry, comparatorComp); }
  
  private static final boolean readNBytes(InputStream paramInputStream, byte[] paramArrayOfByte, int paramInt) throws IOException {
    for (int i = 0; paramInt > 0; i += j) {
      int j = paramInputStream.read(paramArrayOfByte, i, paramInt);
      if (j == -1)
        return false; 
      paramInt -= j;
    } 
    return true;
  }
  
  private char[] readCharArray() {
    byte b = (this.bb[this.off++] & 0xFF) << 8 | this.bb[this.off++] & 0xFF;
    char[] arrayOfChar = new char[b];
    for (byte b1 = 0; b1 < b; b1++)
      arrayOfChar[b1] = (char)((this.bb[this.off++] & 0xFF) << 8 | this.bb[this.off++] & 0xFF); 
    return arrayOfChar;
  }
  
  void readSINGLEBYTE() {
    char[] arrayOfChar = readCharArray();
    for (byte b = 0; b < arrayOfChar.length; b++) {
      char c = arrayOfChar[b];
      if (c != '�')
        this.c2b[this.c2bIndex[c >> '\b'] + (c & 0xFF)] = (char)b; 
    } 
    this.b2cSB = arrayOfChar;
  }
  
  void readINDEXC2B() {
    char[] arrayOfChar = readCharArray();
    for (int i = arrayOfChar.length - 1; i >= 0; i--) {
      if (this.c2b == null && arrayOfChar[i] != -1) {
        this.c2b = new char[arrayOfChar[i] + 'Ā'];
        Arrays.fill(this.c2b, '�');
        break;
      } 
    } 
    this.c2bIndex = arrayOfChar;
  }
  
  char[] readDB(int paramInt1, int paramInt2, int paramInt3) {
    char[] arrayOfChar = readCharArray();
    for (int i = 0; i < arrayOfChar.length; i++) {
      char c = arrayOfChar[i];
      if (c != '�') {
        int j = i / paramInt3;
        int k = i % paramInt3;
        int m = (j + paramInt1) * 256 + k + paramInt2;
        this.c2b[this.c2bIndex[c >> '\b'] + (c & 0xFF)] = (char)m;
      } 
    } 
    return arrayOfChar;
  }
  
  void readDOUBLEBYTE1() {
    this.b1MinDB1 = (this.bb[this.off++] & 0xFF) << 8 | this.bb[this.off++] & 0xFF;
    this.b1MaxDB1 = (this.bb[this.off++] & 0xFF) << 8 | this.bb[this.off++] & 0xFF;
    this.b2Min = (this.bb[this.off++] & 0xFF) << 8 | this.bb[this.off++] & 0xFF;
    this.b2Max = (this.bb[this.off++] & 0xFF) << 8 | this.bb[this.off++] & 0xFF;
    this.dbSegSize = this.b2Max - this.b2Min + 1;
    this.b2cDB1 = readDB(this.b1MinDB1, this.b2Min, this.dbSegSize);
  }
  
  void readDOUBLEBYTE2() {
    this.b1MinDB2 = (this.bb[this.off++] & 0xFF) << 8 | this.bb[this.off++] & 0xFF;
    this.b1MaxDB2 = (this.bb[this.off++] & 0xFF) << 8 | this.bb[this.off++] & 0xFF;
    this.b2Min = (this.bb[this.off++] & 0xFF) << 8 | this.bb[this.off++] & 0xFF;
    this.b2Max = (this.bb[this.off++] & 0xFF) << 8 | this.bb[this.off++] & 0xFF;
    this.dbSegSize = this.b2Max - this.b2Min + 1;
    this.b2cDB2 = readDB(this.b1MinDB2, this.b2Min, this.dbSegSize);
  }
  
  void readCOMPOSITE() {
    char[] arrayOfChar = readCharArray();
    int i = arrayOfChar.length / 3;
    this.b2cComp = new Entry[i];
    this.c2bComp = new Entry[i];
    byte b1 = 0;
    byte b2 = 0;
    while (b1 < i) {
      Entry entry = new Entry();
      entry.bs = arrayOfChar[b2++];
      entry.cp = arrayOfChar[b2++];
      entry.cp2 = arrayOfChar[b2++];
      this.b2cComp[b1] = entry;
      this.c2bComp[b1] = entry;
      b1++;
    } 
    Arrays.sort(this.c2bComp, 0, this.c2bComp.length, comparatorComp);
  }
  
  CharsetMapping load(InputStream paramInputStream) {
    try {
      int i = (paramInputStream.read() & 0xFF) << 24 | (paramInputStream.read() & 0xFF) << 16 | (paramInputStream.read() & 0xFF) << 8 | paramInputStream.read() & 0xFF;
      this.bb = new byte[i];
      this.off = 0;
      if (!readNBytes(paramInputStream, this.bb, i))
        throw new RuntimeException("Corrupted data file"); 
      paramInputStream.close();
      while (this.off < i) {
        byte b = (this.bb[this.off++] & 0xFF) << 8 | this.bb[this.off++] & 0xFF;
        switch (b) {
          case 8:
            readINDEXC2B();
            continue;
          case 1:
            readSINGLEBYTE();
            continue;
          case 2:
            readDOUBLEBYTE1();
            continue;
          case 3:
            readDOUBLEBYTE2();
            continue;
          case 5:
            this.b2cSupp = readCharArray();
            continue;
          case 6:
            this.c2bSupp = readCharArray();
            continue;
          case 7:
            readCOMPOSITE();
            continue;
        } 
        throw new RuntimeException("Corrupted data file");
      } 
      this.bb = null;
      return this;
    } catch (IOException iOException) {
      iOException.printStackTrace();
      return null;
    } 
  }
  
  public static class Entry {
    public int bs;
    
    public int cp;
    
    public int cp2;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\cs\CharsetMapping.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */