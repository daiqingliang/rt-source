package java.awt.font;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Set;

public final class NumericShaper implements Serializable {
  private int key;
  
  private int mask;
  
  private Range shapingRange;
  
  private Set<Range> rangeSet;
  
  private Range[] rangeArray;
  
  private static final int BSEARCH_THRESHOLD = 3;
  
  private static final long serialVersionUID = -8022764705923730308L;
  
  public static final int EUROPEAN = 1;
  
  public static final int ARABIC = 2;
  
  public static final int EASTERN_ARABIC = 4;
  
  public static final int DEVANAGARI = 8;
  
  public static final int BENGALI = 16;
  
  public static final int GURMUKHI = 32;
  
  public static final int GUJARATI = 64;
  
  public static final int ORIYA = 128;
  
  public static final int TAMIL = 256;
  
  public static final int TELUGU = 512;
  
  public static final int KANNADA = 1024;
  
  public static final int MALAYALAM = 2048;
  
  public static final int THAI = 4096;
  
  public static final int LAO = 8192;
  
  public static final int TIBETAN = 16384;
  
  public static final int MYANMAR = 32768;
  
  public static final int ETHIOPIC = 65536;
  
  public static final int KHMER = 131072;
  
  public static final int MONGOLIAN = 262144;
  
  public static final int ALL_RANGES = 524287;
  
  private static final int EUROPEAN_KEY = 0;
  
  private static final int ARABIC_KEY = 1;
  
  private static final int EASTERN_ARABIC_KEY = 2;
  
  private static final int DEVANAGARI_KEY = 3;
  
  private static final int BENGALI_KEY = 4;
  
  private static final int GURMUKHI_KEY = 5;
  
  private static final int GUJARATI_KEY = 6;
  
  private static final int ORIYA_KEY = 7;
  
  private static final int TAMIL_KEY = 8;
  
  private static final int TELUGU_KEY = 9;
  
  private static final int KANNADA_KEY = 10;
  
  private static final int MALAYALAM_KEY = 11;
  
  private static final int THAI_KEY = 12;
  
  private static final int LAO_KEY = 13;
  
  private static final int TIBETAN_KEY = 14;
  
  private static final int MYANMAR_KEY = 15;
  
  private static final int ETHIOPIC_KEY = 16;
  
  private static final int KHMER_KEY = 17;
  
  private static final int MONGOLIAN_KEY = 18;
  
  private static final int NUM_KEYS = 19;
  
  private static final int CONTEXTUAL_MASK = -2147483648;
  
  private static final char[] bases = { 
      Character.MIN_VALUE, 'ذ', 'ۀ', 'श', 'শ', 'ਸ਼', 'શ', 'ଶ', 'ஶ', 'శ', 
      'ಶ', 'ശ', 'ภ', 'ຠ', '໰', 'တ', 'ጸ', 'ឰ', '០' };
  
  private static final char[] contexts = { 
      Character.MIN_VALUE, '̀', '؀', 'ހ', '؀', 'ހ', 'ऀ', 'ঀ', 'ঀ', '਀', 
      '਀', '઀', '઀', '଀', '଀', '஀', '஀', 'ఀ', 'ఀ', 'ಀ', 
      'ಀ', 'ഀ', 'ഀ', '඀', '฀', '຀', '຀', 'ༀ', 'ༀ', 'က', 
      'က', 'ႀ', 'ሀ', 'ᎀ', 'ក', '᠀', '᠀', 'ᤀ', Character.MAX_VALUE };
  
  private static int ctCache = 0;
  
  private static int ctCacheLimit = contexts.length - 2;
  
  private static int[] strongTable = { 
      0, 65, 91, 97, 123, 170, 171, 181, 182, 186, 
      187, 192, 215, 216, 247, 248, 697, 699, 706, 720, 
      722, 736, 741, 750, 751, 880, 884, 886, 894, 902, 
      903, 904, 1014, 1015, 1155, 1162, 1418, 1470, 1471, 1472, 
      1473, 1475, 1476, 1478, 1479, 1488, 1536, 1544, 1545, 1547, 
      1548, 1549, 1550, 1563, 1611, 1645, 1648, 1649, 1750, 1765, 
      1767, 1774, 1776, 1786, 1809, 1810, 1840, 1869, 1958, 1969, 
      2027, 2036, 2038, 2042, 2070, 2074, 2075, 2084, 2085, 2088, 
      2089, 2096, 2137, 2142, 2276, 2307, 2362, 2363, 2364, 2365, 
      2369, 2377, 2381, 2382, 2385, 2392, 2402, 2404, 2433, 2434, 
      2492, 2493, 2497, 2503, 2509, 2510, 2530, 2534, 2546, 2548, 
      2555, 2563, 2620, 2622, 2625, 2649, 2672, 2674, 2677, 2691, 
      2748, 2749, 2753, 2761, 2765, 2768, 2786, 2790, 2801, 2818, 
      2876, 2877, 2879, 2880, 2881, 2887, 2893, 2903, 2914, 2918, 
      2946, 2947, 3008, 3009, 3021, 3024, 3059, 3073, 3134, 3137, 
      3142, 3160, 3170, 3174, 3192, 3199, 3260, 3261, 3276, 3285, 
      3298, 3302, 3393, 3398, 3405, 3406, 3426, 3430, 3530, 3535, 
      3538, 3544, 3633, 3634, 3636, 3648, 3655, 3663, 3761, 3762, 
      3764, 3773, 3784, 3792, 3864, 3866, 3893, 3894, 3895, 3896, 
      3897, 3902, 3953, 3967, 3968, 3973, 3974, 3976, 3981, 4030, 
      4038, 4039, 4141, 4145, 4146, 4152, 4153, 4155, 4157, 4159, 
      4184, 4186, 4190, 4193, 4209, 4213, 4226, 4227, 4229, 4231, 
      4237, 4238, 4253, 4254, 4957, 4960, 5008, 5024, 5120, 5121, 
      5760, 5761, 5787, 5792, 5906, 5920, 5938, 5941, 5970, 5984, 
      6002, 6016, 6068, 6070, 6071, 6078, 6086, 6087, 6089, 6100, 
      6107, 6108, 6109, 6112, 6128, 6160, 6313, 6314, 6432, 6435, 
      6439, 6441, 6450, 6451, 6457, 6470, 6622, 6656, 6679, 6681, 
      6742, 6743, 6744, 6753, 6754, 6755, 6757, 6765, 6771, 6784, 
      6912, 6916, 6964, 6965, 6966, 6971, 6972, 6973, 6978, 6979, 
      7019, 7028, 7040, 7042, 7074, 7078, 7080, 7082, 7083, 7084, 
      7142, 7143, 7144, 7146, 7149, 7150, 7151, 7154, 7212, 7220, 
      7222, 7227, 7376, 7379, 7380, 7393, 7394, 7401, 7405, 7406, 
      7412, 7413, 7616, 7680, 8125, 8126, 8127, 8130, 8141, 8144, 
      8157, 8160, 8173, 8178, 8189, 8206, 8208, 8305, 8308, 8319, 
      8320, 8336, 8352, 8450, 8451, 8455, 8456, 8458, 8468, 8469, 
      8470, 8473, 8478, 8484, 8485, 8486, 8487, 8488, 8489, 8490, 
      8494, 8495, 8506, 8508, 8512, 8517, 8522, 8526, 8528, 8544, 
      8585, 9014, 9083, 9109, 9110, 9372, 9450, 9900, 9901, 10240, 
      10496, 11264, 11493, 11499, 11503, 11506, 11513, 11520, 11647, 11648, 
      11744, 12293, 12296, 12321, 12330, 12337, 12342, 12344, 12349, 12353, 
      12441, 12445, 12448, 12449, 12539, 12540, 12736, 12784, 12829, 12832, 
      12880, 12896, 12924, 12927, 12977, 12992, 13004, 13008, 13175, 13179, 
      13278, 13280, 13311, 13312, 19904, 19968, 42128, 42192, 42509, 42512, 
      42607, 42624, 42655, 42656, 42736, 42738, 42752, 42786, 42888, 42889, 
      43010, 43011, 43014, 43015, 43019, 43020, 43045, 43047, 43048, 43056, 
      43064, 43072, 43124, 43136, 43204, 43214, 43232, 43250, 43302, 43310, 
      43335, 43346, 43392, 43395, 43443, 43444, 43446, 43450, 43452, 43453, 
      43561, 43567, 43569, 43571, 43573, 43584, 43587, 43588, 43596, 43597, 
      43696, 43697, 43698, 43701, 43703, 43705, 43710, 43712, 43713, 43714, 
      43756, 43758, 43766, 43777, 44005, 44006, 44008, 44009, 44013, 44016, 
      64286, 64287, 64297, 64298, 64830, 64848, 65021, 65136, 65279, 65313, 
      65339, 65345, 65371, 65382, 65504, 65536, 65793, 65794, 65856, 66000, 
      66045, 66176, 67871, 67872, 68097, 68112, 68152, 68160, 68409, 68416, 
      69216, 69632, 69633, 69634, 69688, 69703, 69714, 69734, 69760, 69762, 
      69811, 69815, 69817, 69819, 69888, 69891, 69927, 69932, 69933, 69942, 
      70016, 70018, 70070, 70079, 71339, 71340, 71341, 71342, 71344, 71350, 
      71351, 71360, 94095, 94099, 119143, 119146, 119155, 119171, 119173, 119180, 
      119210, 119214, 119296, 119648, 120539, 120540, 120597, 120598, 120655, 120656, 
      120713, 120714, 120771, 120772, 120782, 126464, 126704, 127248, 127338, 127344, 
      127744, 128140, 128141, 128292, 128293, 131072, 917505, 983040, 1114110, 1114111 };
  
  private static int getContextKey(char paramChar) {
    if (paramChar < contexts[ctCache]) {
      while (ctCache > 0 && paramChar < contexts[ctCache])
        ctCache--; 
    } else if (paramChar >= contexts[ctCache + 1]) {
      while (ctCache < ctCacheLimit && paramChar >= contexts[ctCache + 1])
        ctCache++; 
    } 
    return ((ctCache & true) == 0) ? (ctCache / 2) : 0;
  }
  
  private Range rangeForCodePoint(int paramInt) {
    if (this.currentRange.inRange(paramInt))
      return this.currentRange; 
    Range[] arrayOfRange = this.rangeArray;
    if (arrayOfRange.length > 3) {
      int i = 0;
      int j = arrayOfRange.length - 1;
      while (i <= j) {
        int k = (i + j) / 2;
        Range range;
        if (paramInt < range.start) {
          j = k - 1;
          continue;
        } 
        if (paramInt >= range.end) {
          i = k + 1;
          continue;
        } 
        this.currentRange = range;
        return range;
      } 
    } else {
      for (byte b = 0; b < arrayOfRange.length; b++) {
        if (arrayOfRange[b].inRange(paramInt))
          return arrayOfRange[b]; 
      } 
    } 
    return Range.EUROPEAN;
  }
  
  private boolean isStrongDirectional(char paramChar) {
    int i = this.stCache;
    if (paramChar < strongTable[i]) {
      i = search(paramChar, strongTable, 0, i);
    } else if (paramChar >= strongTable[i + 1]) {
      i = search(paramChar, strongTable, i + 1, strongTable.length - i - 1);
    } 
    boolean bool = ((i & true) == 1);
    this.stCache = i;
    return bool;
  }
  
  private static int getKeyFromMask(int paramInt) {
    byte b;
    for (b = 0; b < 19 && (paramInt & true << b) == 0; b++);
    if (b == 19 || (paramInt & (1 << b ^ 0xFFFFFFFF)) != 0)
      throw new IllegalArgumentException("invalid shaper: " + Integer.toHexString(paramInt)); 
    return b;
  }
  
  public static NumericShaper getShaper(int paramInt) {
    int i = getKeyFromMask(paramInt);
    return new NumericShaper(i, paramInt);
  }
  
  public static NumericShaper getShaper(Range paramRange) { return new NumericShaper(paramRange, EnumSet.of(paramRange)); }
  
  public static NumericShaper getContextualShaper(int paramInt) {
    paramInt |= Integer.MIN_VALUE;
    return new NumericShaper(0, paramInt);
  }
  
  public static NumericShaper getContextualShaper(Set<Range> paramSet) {
    NumericShaper numericShaper = new NumericShaper(Range.EUROPEAN, paramSet);
    numericShaper.mask = Integer.MIN_VALUE;
    return numericShaper;
  }
  
  public static NumericShaper getContextualShaper(int paramInt1, int paramInt2) {
    int i = getKeyFromMask(paramInt2);
    paramInt1 |= Integer.MIN_VALUE;
    return new NumericShaper(i, paramInt1);
  }
  
  public static NumericShaper getContextualShaper(Set<Range> paramSet, Range paramRange) {
    if (paramRange == null)
      throw new NullPointerException(); 
    NumericShaper numericShaper = new NumericShaper(paramRange, paramSet);
    numericShaper.mask = Integer.MIN_VALUE;
    return numericShaper;
  }
  
  private NumericShaper(int paramInt1, int paramInt2) {
    this.key = paramInt1;
    this.mask = paramInt2;
  }
  
  private NumericShaper(Range paramRange, Set<Range> paramSet) {
    this.shapingRange = paramRange;
    this.rangeSet = EnumSet.copyOf(paramSet);
    if (this.rangeSet.contains(Range.EASTERN_ARABIC) && this.rangeSet.contains(Range.ARABIC))
      this.rangeSet.remove(Range.ARABIC); 
    if (this.rangeSet.contains(Range.TAI_THAM_THAM) && this.rangeSet.contains(Range.TAI_THAM_HORA))
      this.rangeSet.remove(Range.TAI_THAM_HORA); 
    this.rangeArray = (Range[])this.rangeSet.toArray(new Range[this.rangeSet.size()]);
    if (this.rangeArray.length > 3)
      Arrays.sort(this.rangeArray, new Comparator<Range>() {
            public int compare(NumericShaper.Range param1Range1, NumericShaper.Range param1Range2) { return (param1Range1.base > param1Range2.base) ? 1 : ((param1Range1.base == param1Range2.base) ? 0 : -1); }
          }); 
  }
  
  public void shape(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    checkParams(paramArrayOfChar, paramInt1, paramInt2);
    if (isContextual()) {
      if (this.rangeSet == null) {
        shapeContextually(paramArrayOfChar, paramInt1, paramInt2, this.key);
      } else {
        shapeContextually(paramArrayOfChar, paramInt1, paramInt2, this.shapingRange);
      } 
    } else {
      shapeNonContextually(paramArrayOfChar, paramInt1, paramInt2);
    } 
  }
  
  public void shape(char[] paramArrayOfChar, int paramInt1, int paramInt2, int paramInt3) {
    checkParams(paramArrayOfChar, paramInt1, paramInt2);
    if (isContextual()) {
      int i = getKeyFromMask(paramInt3);
      if (this.rangeSet == null) {
        shapeContextually(paramArrayOfChar, paramInt1, paramInt2, i);
      } else {
        shapeContextually(paramArrayOfChar, paramInt1, paramInt2, Range.values()[i]);
      } 
    } else {
      shapeNonContextually(paramArrayOfChar, paramInt1, paramInt2);
    } 
  }
  
  public void shape(char[] paramArrayOfChar, int paramInt1, int paramInt2, Range paramRange) {
    checkParams(paramArrayOfChar, paramInt1, paramInt2);
    if (paramRange == null)
      throw new NullPointerException("context is null"); 
    if (isContextual()) {
      if (this.rangeSet != null) {
        shapeContextually(paramArrayOfChar, paramInt1, paramInt2, paramRange);
      } else {
        int i = Range.toRangeIndex(paramRange);
        if (i >= 0) {
          shapeContextually(paramArrayOfChar, paramInt1, paramInt2, i);
        } else {
          shapeContextually(paramArrayOfChar, paramInt1, paramInt2, this.shapingRange);
        } 
      } 
    } else {
      shapeNonContextually(paramArrayOfChar, paramInt1, paramInt2);
    } 
  }
  
  private void checkParams(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    if (paramArrayOfChar == null)
      throw new NullPointerException("text is null"); 
    if (paramInt1 < 0 || paramInt1 > paramArrayOfChar.length || paramInt1 + paramInt2 < 0 || paramInt1 + paramInt2 > paramArrayOfChar.length)
      throw new IndexOutOfBoundsException("bad start or count for text of length " + paramArrayOfChar.length); 
  }
  
  public boolean isContextual() { return ((this.mask & 0x80000000) != 0); }
  
  public int getRanges() { return this.mask & 0x7FFFFFFF; }
  
  public Set<Range> getRangeSet() { return (this.rangeSet != null) ? EnumSet.copyOf(this.rangeSet) : Range.maskToRangeSet(this.mask); }
  
  private void shapeNonContextually(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    char c;
    char c1 = '0';
    if (this.shapingRange != null) {
      c = this.shapingRange.getDigitBase();
      c1 = (char)(c1 + this.shapingRange.getNumericBase());
    } else {
      c = bases[this.key];
      if (this.key == 16)
        c1 = (char)(c1 + '\001'); 
    } 
    int i = paramInt1;
    int j = paramInt1 + paramInt2;
    while (i < j) {
      char c2 = paramArrayOfChar[i];
      if (c2 >= c1 && c2 <= '9')
        paramArrayOfChar[i] = (char)(c2 + c); 
      i++;
    } 
  }
  
  private void shapeContextually(char[] paramArrayOfChar, int paramInt1, int paramInt2, int paramInt3) {
    if ((this.mask & 1 << paramInt3) == 0)
      paramInt3 = 0; 
    int i = paramInt3;
    char c = bases[paramInt3];
    byte b = (paramInt3 == 16) ? 49 : 48;
    synchronized (NumericShaper.class) {
      int j = paramInt1;
      int k = paramInt1 + paramInt2;
      while (j < k) {
        char c1 = paramArrayOfChar[j];
        if (c1 >= b && c1 <= '9')
          paramArrayOfChar[j] = (char)(c1 + c); 
        if (isStrongDirectional(c1)) {
          int m = getContextKey(c1);
          if (m != i) {
            i = m;
            paramInt3 = m;
            if ((this.mask & 0x4) != 0 && (paramInt3 == 1 || paramInt3 == 2)) {
              paramInt3 = 2;
            } else if ((this.mask & 0x2) != 0 && (paramInt3 == 1 || paramInt3 == 2)) {
              paramInt3 = 1;
            } else if ((this.mask & 1 << paramInt3) == 0) {
              paramInt3 = 0;
            } 
            c = bases[paramInt3];
            b = (paramInt3 == 16) ? 49 : 48;
          } 
        } 
        j++;
      } 
    } 
  }
  
  private void shapeContextually(char[] paramArrayOfChar, int paramInt1, int paramInt2, Range paramRange) {
    if (paramRange == null || !this.rangeSet.contains(paramRange))
      paramRange = Range.EUROPEAN; 
    Range range;
    int i = paramRange.getDigitBase();
    char c = (char)('0' + paramRange.getNumericBase());
    int j = paramInt1 + paramInt2;
    for (int k = paramInt1; k < j; k++) {
      char c1 = paramArrayOfChar[k];
      if (c1 >= c && c1 <= '9') {
        paramArrayOfChar[k] = (char)(c1 + i);
      } else if (isStrongDirectional(c1)) {
        paramRange = rangeForCodePoint(c1);
        if (paramRange != range) {
          i = paramRange.getDigitBase();
          c = (char)('0' + paramRange.getNumericBase());
        } 
      } 
    } 
  }
  
  public int hashCode() {
    int i = this.mask;
    if (this.rangeSet != null) {
      i &= Integer.MIN_VALUE;
      i ^= this.rangeSet.hashCode();
    } 
    return i;
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject != null)
      try {
        NumericShaper numericShaper = (NumericShaper)paramObject;
        if (this.rangeSet != null)
          return (numericShaper.rangeSet != null) ? ((isContextual() == numericShaper.isContextual() && this.rangeSet.equals(numericShaper.rangeSet) && this.shapingRange == numericShaper.shapingRange)) : ((isContextual() == numericShaper.isContextual() && this.rangeSet.equals(Range.maskToRangeSet(numericShaper.mask)) && this.shapingRange == Range.indexToRange(numericShaper.key))); 
        if (numericShaper.rangeSet != null) {
          Set set = Range.maskToRangeSet(this.mask);
          Range range = Range.indexToRange(this.key);
          return (isContextual() == numericShaper.isContextual() && set.equals(numericShaper.rangeSet) && range == numericShaper.shapingRange);
        } 
        return (numericShaper.mask == this.mask && numericShaper.key == this.key);
      } catch (ClassCastException classCastException) {} 
    return false;
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder(super.toString());
    stringBuilder.append("[contextual:").append(isContextual());
    Object object = null;
    if (isContextual()) {
      stringBuilder.append(", context:");
      stringBuilder.append((this.shapingRange == null) ? Range.values()[this.key] : this.shapingRange);
    } 
    if (this.rangeSet == null) {
      stringBuilder.append(", range(s): ");
      boolean bool = true;
      for (byte b = 0; b < 19; b++) {
        if ((this.mask & true << b) != 0) {
          if (bool) {
            bool = false;
          } else {
            stringBuilder.append(", ");
          } 
          stringBuilder.append(Range.values()[b]);
        } 
      } 
    } else {
      stringBuilder.append(", range set: ").append(this.rangeSet);
    } 
    stringBuilder.append(']');
    return stringBuilder.toString();
  }
  
  private static int getHighBit(int paramInt) {
    if (paramInt <= 0)
      return -32; 
    byte b = 0;
    if (paramInt >= 65536) {
      paramInt >>= 16;
      b += true;
    } 
    if (paramInt >= 256) {
      paramInt >>= 8;
      b += true;
    } 
    if (paramInt >= 16) {
      paramInt >>= 4;
      b += true;
    } 
    if (paramInt >= 4) {
      paramInt >>= 2;
      b += true;
    } 
    if (paramInt >= 2)
      b++; 
    return b;
  }
  
  private static int search(int paramInt1, int[] paramArrayOfInt, int paramInt2, int paramInt3) {
    int i = 1 << getHighBit(paramInt3);
    int j = paramInt3 - i;
    int k = i;
    int m = paramInt2;
    if (paramInt1 >= paramArrayOfInt[m + j])
      m += j; 
    while (k > 1) {
      k >>= 1;
      if (paramInt1 >= paramArrayOfInt[m + k])
        m += k; 
    } 
    return m;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    if (this.shapingRange != null) {
      int i = Range.toRangeIndex(this.shapingRange);
      if (i >= 0)
        this.key = i; 
    } 
    if (this.rangeSet != null)
      this.mask |= Range.toRangeMask(this.rangeSet); 
    paramObjectOutputStream.defaultWriteObject();
  }
  
  public final enum Range {
    EUROPEAN, ARABIC, EASTERN_ARABIC, DEVANAGARI, BENGALI, GURMUKHI, GUJARATI, ORIYA, TAMIL, TELUGU, KANNADA, MALAYALAM, THAI, LAO, TIBETAN, MYANMAR, ETHIOPIC, KHMER, MONGOLIAN, NKO, MYANMAR_SHAN, LIMBU, NEW_TAI_LUE, BALINESE, SUNDANESE, LEPCHA, OL_CHIKI, VAI, SAURASHTRA, KAYAH_LI, CHAM, TAI_THAM_HORA, TAI_THAM_THAM, JAVANESE, MEETEI_MAYEK;
    
    private final int base;
    
    private final int start;
    
    private final int end;
    
    private static int toRangeIndex(Range param1Range) {
      int i = param1Range.ordinal();
      return (i < 19) ? i : -1;
    }
    
    private static Range indexToRange(int param1Int) { return (param1Int < 19) ? values()[param1Int] : null; }
    
    private static int toRangeMask(Set<Range> param1Set) {
      int i = 0;
      for (Range range : param1Set) {
        int j = range.ordinal();
        if (j < 19)
          i |= 1 << j; 
      } 
      return i;
    }
    
    private static Set<Range> maskToRangeSet(int param1Int) {
      EnumSet enumSet = EnumSet.noneOf(Range.class);
      Range[] arrayOfRange = values();
      for (byte b = 0; b < 19; b++) {
        if ((param1Int & true << b) != 0)
          enumSet.add(arrayOfRange[b]); 
      } 
      return enumSet;
    }
    
    Range(int param1Int1, int param1Int2, int param1Int3) {
      this.base = param1Int1 - '0' + getNumericBase();
      this.start = param1Int2;
      this.end = param1Int3;
    }
    
    private int getDigitBase() { return this.base; }
    
    char getNumericBase() { return Character.MIN_VALUE; }
    
    private boolean inRange(int param1Int) { return (this.start <= param1Int && param1Int < this.end); }
    
    static  {
      // Byte code:
      //   0: new java/awt/font/NumericShaper$Range
      //   3: dup
      //   4: ldc 'EUROPEAN'
      //   6: iconst_0
      //   7: bipush #48
      //   9: iconst_0
      //   10: sipush #768
      //   13: invokespecial <init> : (Ljava/lang/String;IIII)V
      //   16: putstatic java/awt/font/NumericShaper$Range.EUROPEAN : Ljava/awt/font/NumericShaper$Range;
      //   19: new java/awt/font/NumericShaper$Range
      //   22: dup
      //   23: ldc 'ARABIC'
      //   25: iconst_1
      //   26: sipush #1632
      //   29: sipush #1536
      //   32: sipush #1920
      //   35: invokespecial <init> : (Ljava/lang/String;IIII)V
      //   38: putstatic java/awt/font/NumericShaper$Range.ARABIC : Ljava/awt/font/NumericShaper$Range;
      //   41: new java/awt/font/NumericShaper$Range
      //   44: dup
      //   45: ldc 'EASTERN_ARABIC'
      //   47: iconst_2
      //   48: sipush #1776
      //   51: sipush #1536
      //   54: sipush #1920
      //   57: invokespecial <init> : (Ljava/lang/String;IIII)V
      //   60: putstatic java/awt/font/NumericShaper$Range.EASTERN_ARABIC : Ljava/awt/font/NumericShaper$Range;
      //   63: new java/awt/font/NumericShaper$Range
      //   66: dup
      //   67: ldc 'DEVANAGARI'
      //   69: iconst_3
      //   70: sipush #2406
      //   73: sipush #2304
      //   76: sipush #2432
      //   79: invokespecial <init> : (Ljava/lang/String;IIII)V
      //   82: putstatic java/awt/font/NumericShaper$Range.DEVANAGARI : Ljava/awt/font/NumericShaper$Range;
      //   85: new java/awt/font/NumericShaper$Range
      //   88: dup
      //   89: ldc 'BENGALI'
      //   91: iconst_4
      //   92: sipush #2534
      //   95: sipush #2432
      //   98: sipush #2560
      //   101: invokespecial <init> : (Ljava/lang/String;IIII)V
      //   104: putstatic java/awt/font/NumericShaper$Range.BENGALI : Ljava/awt/font/NumericShaper$Range;
      //   107: new java/awt/font/NumericShaper$Range
      //   110: dup
      //   111: ldc 'GURMUKHI'
      //   113: iconst_5
      //   114: sipush #2662
      //   117: sipush #2560
      //   120: sipush #2688
      //   123: invokespecial <init> : (Ljava/lang/String;IIII)V
      //   126: putstatic java/awt/font/NumericShaper$Range.GURMUKHI : Ljava/awt/font/NumericShaper$Range;
      //   129: new java/awt/font/NumericShaper$Range
      //   132: dup
      //   133: ldc 'GUJARATI'
      //   135: bipush #6
      //   137: sipush #2790
      //   140: sipush #2816
      //   143: sipush #2944
      //   146: invokespecial <init> : (Ljava/lang/String;IIII)V
      //   149: putstatic java/awt/font/NumericShaper$Range.GUJARATI : Ljava/awt/font/NumericShaper$Range;
      //   152: new java/awt/font/NumericShaper$Range
      //   155: dup
      //   156: ldc 'ORIYA'
      //   158: bipush #7
      //   160: sipush #2918
      //   163: sipush #2816
      //   166: sipush #2944
      //   169: invokespecial <init> : (Ljava/lang/String;IIII)V
      //   172: putstatic java/awt/font/NumericShaper$Range.ORIYA : Ljava/awt/font/NumericShaper$Range;
      //   175: new java/awt/font/NumericShaper$Range
      //   178: dup
      //   179: ldc 'TAMIL'
      //   181: bipush #8
      //   183: sipush #3046
      //   186: sipush #2944
      //   189: sipush #3072
      //   192: invokespecial <init> : (Ljava/lang/String;IIII)V
      //   195: putstatic java/awt/font/NumericShaper$Range.TAMIL : Ljava/awt/font/NumericShaper$Range;
      //   198: new java/awt/font/NumericShaper$Range
      //   201: dup
      //   202: ldc 'TELUGU'
      //   204: bipush #9
      //   206: sipush #3174
      //   209: sipush #3072
      //   212: sipush #3200
      //   215: invokespecial <init> : (Ljava/lang/String;IIII)V
      //   218: putstatic java/awt/font/NumericShaper$Range.TELUGU : Ljava/awt/font/NumericShaper$Range;
      //   221: new java/awt/font/NumericShaper$Range
      //   224: dup
      //   225: ldc 'KANNADA'
      //   227: bipush #10
      //   229: sipush #3302
      //   232: sipush #3200
      //   235: sipush #3328
      //   238: invokespecial <init> : (Ljava/lang/String;IIII)V
      //   241: putstatic java/awt/font/NumericShaper$Range.KANNADA : Ljava/awt/font/NumericShaper$Range;
      //   244: new java/awt/font/NumericShaper$Range
      //   247: dup
      //   248: ldc 'MALAYALAM'
      //   250: bipush #11
      //   252: sipush #3430
      //   255: sipush #3328
      //   258: sipush #3456
      //   261: invokespecial <init> : (Ljava/lang/String;IIII)V
      //   264: putstatic java/awt/font/NumericShaper$Range.MALAYALAM : Ljava/awt/font/NumericShaper$Range;
      //   267: new java/awt/font/NumericShaper$Range
      //   270: dup
      //   271: ldc 'THAI'
      //   273: bipush #12
      //   275: sipush #3664
      //   278: sipush #3584
      //   281: sipush #3712
      //   284: invokespecial <init> : (Ljava/lang/String;IIII)V
      //   287: putstatic java/awt/font/NumericShaper$Range.THAI : Ljava/awt/font/NumericShaper$Range;
      //   290: new java/awt/font/NumericShaper$Range
      //   293: dup
      //   294: ldc 'LAO'
      //   296: bipush #13
      //   298: sipush #3792
      //   301: sipush #3712
      //   304: sipush #3840
      //   307: invokespecial <init> : (Ljava/lang/String;IIII)V
      //   310: putstatic java/awt/font/NumericShaper$Range.LAO : Ljava/awt/font/NumericShaper$Range;
      //   313: new java/awt/font/NumericShaper$Range
      //   316: dup
      //   317: ldc 'TIBETAN'
      //   319: bipush #14
      //   321: sipush #3872
      //   324: sipush #3840
      //   327: sipush #4096
      //   330: invokespecial <init> : (Ljava/lang/String;IIII)V
      //   333: putstatic java/awt/font/NumericShaper$Range.TIBETAN : Ljava/awt/font/NumericShaper$Range;
      //   336: new java/awt/font/NumericShaper$Range
      //   339: dup
      //   340: ldc 'MYANMAR'
      //   342: bipush #15
      //   344: sipush #4160
      //   347: sipush #4096
      //   350: sipush #4224
      //   353: invokespecial <init> : (Ljava/lang/String;IIII)V
      //   356: putstatic java/awt/font/NumericShaper$Range.MYANMAR : Ljava/awt/font/NumericShaper$Range;
      //   359: new java/awt/font/NumericShaper$Range$1
      //   362: dup
      //   363: ldc 'ETHIOPIC'
      //   365: bipush #16
      //   367: sipush #4969
      //   370: sipush #4608
      //   373: sipush #4992
      //   376: invokespecial <init> : (Ljava/lang/String;IIII)V
      //   379: putstatic java/awt/font/NumericShaper$Range.ETHIOPIC : Ljava/awt/font/NumericShaper$Range;
      //   382: new java/awt/font/NumericShaper$Range
      //   385: dup
      //   386: ldc 'KHMER'
      //   388: bipush #17
      //   390: sipush #6112
      //   393: sipush #6016
      //   396: sipush #6144
      //   399: invokespecial <init> : (Ljava/lang/String;IIII)V
      //   402: putstatic java/awt/font/NumericShaper$Range.KHMER : Ljava/awt/font/NumericShaper$Range;
      //   405: new java/awt/font/NumericShaper$Range
      //   408: dup
      //   409: ldc 'MONGOLIAN'
      //   411: bipush #18
      //   413: sipush #6160
      //   416: sipush #6144
      //   419: sipush #6400
      //   422: invokespecial <init> : (Ljava/lang/String;IIII)V
      //   425: putstatic java/awt/font/NumericShaper$Range.MONGOLIAN : Ljava/awt/font/NumericShaper$Range;
      //   428: new java/awt/font/NumericShaper$Range
      //   431: dup
      //   432: ldc 'NKO'
      //   434: bipush #19
      //   436: sipush #1984
      //   439: sipush #1984
      //   442: sipush #2048
      //   445: invokespecial <init> : (Ljava/lang/String;IIII)V
      //   448: putstatic java/awt/font/NumericShaper$Range.NKO : Ljava/awt/font/NumericShaper$Range;
      //   451: new java/awt/font/NumericShaper$Range
      //   454: dup
      //   455: ldc 'MYANMAR_SHAN'
      //   457: bipush #20
      //   459: sipush #4240
      //   462: sipush #4096
      //   465: sipush #4256
      //   468: invokespecial <init> : (Ljava/lang/String;IIII)V
      //   471: putstatic java/awt/font/NumericShaper$Range.MYANMAR_SHAN : Ljava/awt/font/NumericShaper$Range;
      //   474: new java/awt/font/NumericShaper$Range
      //   477: dup
      //   478: ldc 'LIMBU'
      //   480: bipush #21
      //   482: sipush #6470
      //   485: sipush #6400
      //   488: sipush #6480
      //   491: invokespecial <init> : (Ljava/lang/String;IIII)V
      //   494: putstatic java/awt/font/NumericShaper$Range.LIMBU : Ljava/awt/font/NumericShaper$Range;
      //   497: new java/awt/font/NumericShaper$Range
      //   500: dup
      //   501: ldc 'NEW_TAI_LUE'
      //   503: bipush #22
      //   505: sipush #6608
      //   508: sipush #6528
      //   511: sipush #6624
      //   514: invokespecial <init> : (Ljava/lang/String;IIII)V
      //   517: putstatic java/awt/font/NumericShaper$Range.NEW_TAI_LUE : Ljava/awt/font/NumericShaper$Range;
      //   520: new java/awt/font/NumericShaper$Range
      //   523: dup
      //   524: ldc 'BALINESE'
      //   526: bipush #23
      //   528: sipush #6992
      //   531: sipush #6912
      //   534: sipush #7040
      //   537: invokespecial <init> : (Ljava/lang/String;IIII)V
      //   540: putstatic java/awt/font/NumericShaper$Range.BALINESE : Ljava/awt/font/NumericShaper$Range;
      //   543: new java/awt/font/NumericShaper$Range
      //   546: dup
      //   547: ldc 'SUNDANESE'
      //   549: bipush #24
      //   551: sipush #7088
      //   554: sipush #7040
      //   557: sipush #7104
      //   560: invokespecial <init> : (Ljava/lang/String;IIII)V
      //   563: putstatic java/awt/font/NumericShaper$Range.SUNDANESE : Ljava/awt/font/NumericShaper$Range;
      //   566: new java/awt/font/NumericShaper$Range
      //   569: dup
      //   570: ldc 'LEPCHA'
      //   572: bipush #25
      //   574: sipush #7232
      //   577: sipush #7168
      //   580: sipush #7248
      //   583: invokespecial <init> : (Ljava/lang/String;IIII)V
      //   586: putstatic java/awt/font/NumericShaper$Range.LEPCHA : Ljava/awt/font/NumericShaper$Range;
      //   589: new java/awt/font/NumericShaper$Range
      //   592: dup
      //   593: ldc 'OL_CHIKI'
      //   595: bipush #26
      //   597: sipush #7248
      //   600: sipush #7248
      //   603: sipush #7296
      //   606: invokespecial <init> : (Ljava/lang/String;IIII)V
      //   609: putstatic java/awt/font/NumericShaper$Range.OL_CHIKI : Ljava/awt/font/NumericShaper$Range;
      //   612: new java/awt/font/NumericShaper$Range
      //   615: dup
      //   616: ldc 'VAI'
      //   618: bipush #27
      //   620: ldc 42528
      //   622: ldc 42240
      //   624: ldc 42560
      //   626: invokespecial <init> : (Ljava/lang/String;IIII)V
      //   629: putstatic java/awt/font/NumericShaper$Range.VAI : Ljava/awt/font/NumericShaper$Range;
      //   632: new java/awt/font/NumericShaper$Range
      //   635: dup
      //   636: ldc 'SAURASHTRA'
      //   638: bipush #28
      //   640: ldc 43216
      //   642: ldc 43136
      //   644: ldc 43232
      //   646: invokespecial <init> : (Ljava/lang/String;IIII)V
      //   649: putstatic java/awt/font/NumericShaper$Range.SAURASHTRA : Ljava/awt/font/NumericShaper$Range;
      //   652: new java/awt/font/NumericShaper$Range
      //   655: dup
      //   656: ldc 'KAYAH_LI'
      //   658: bipush #29
      //   660: ldc 43264
      //   662: ldc 43264
      //   664: ldc 43312
      //   666: invokespecial <init> : (Ljava/lang/String;IIII)V
      //   669: putstatic java/awt/font/NumericShaper$Range.KAYAH_LI : Ljava/awt/font/NumericShaper$Range;
      //   672: new java/awt/font/NumericShaper$Range
      //   675: dup
      //   676: ldc 'CHAM'
      //   678: bipush #30
      //   680: ldc 43600
      //   682: ldc 43520
      //   684: ldc 43616
      //   686: invokespecial <init> : (Ljava/lang/String;IIII)V
      //   689: putstatic java/awt/font/NumericShaper$Range.CHAM : Ljava/awt/font/NumericShaper$Range;
      //   692: new java/awt/font/NumericShaper$Range
      //   695: dup
      //   696: ldc 'TAI_THAM_HORA'
      //   698: bipush #31
      //   700: sipush #6784
      //   703: sipush #6688
      //   706: sipush #6832
      //   709: invokespecial <init> : (Ljava/lang/String;IIII)V
      //   712: putstatic java/awt/font/NumericShaper$Range.TAI_THAM_HORA : Ljava/awt/font/NumericShaper$Range;
      //   715: new java/awt/font/NumericShaper$Range
      //   718: dup
      //   719: ldc 'TAI_THAM_THAM'
      //   721: bipush #32
      //   723: sipush #6800
      //   726: sipush #6688
      //   729: sipush #6832
      //   732: invokespecial <init> : (Ljava/lang/String;IIII)V
      //   735: putstatic java/awt/font/NumericShaper$Range.TAI_THAM_THAM : Ljava/awt/font/NumericShaper$Range;
      //   738: new java/awt/font/NumericShaper$Range
      //   741: dup
      //   742: ldc 'JAVANESE'
      //   744: bipush #33
      //   746: ldc 43472
      //   748: ldc 43392
      //   750: ldc 43488
      //   752: invokespecial <init> : (Ljava/lang/String;IIII)V
      //   755: putstatic java/awt/font/NumericShaper$Range.JAVANESE : Ljava/awt/font/NumericShaper$Range;
      //   758: new java/awt/font/NumericShaper$Range
      //   761: dup
      //   762: ldc 'MEETEI_MAYEK'
      //   764: bipush #34
      //   766: ldc 44016
      //   768: ldc 43968
      //   770: ldc 44032
      //   772: invokespecial <init> : (Ljava/lang/String;IIII)V
      //   775: putstatic java/awt/font/NumericShaper$Range.MEETEI_MAYEK : Ljava/awt/font/NumericShaper$Range;
      //   778: bipush #35
      //   780: anewarray java/awt/font/NumericShaper$Range
      //   783: dup
      //   784: iconst_0
      //   785: getstatic java/awt/font/NumericShaper$Range.EUROPEAN : Ljava/awt/font/NumericShaper$Range;
      //   788: aastore
      //   789: dup
      //   790: iconst_1
      //   791: getstatic java/awt/font/NumericShaper$Range.ARABIC : Ljava/awt/font/NumericShaper$Range;
      //   794: aastore
      //   795: dup
      //   796: iconst_2
      //   797: getstatic java/awt/font/NumericShaper$Range.EASTERN_ARABIC : Ljava/awt/font/NumericShaper$Range;
      //   800: aastore
      //   801: dup
      //   802: iconst_3
      //   803: getstatic java/awt/font/NumericShaper$Range.DEVANAGARI : Ljava/awt/font/NumericShaper$Range;
      //   806: aastore
      //   807: dup
      //   808: iconst_4
      //   809: getstatic java/awt/font/NumericShaper$Range.BENGALI : Ljava/awt/font/NumericShaper$Range;
      //   812: aastore
      //   813: dup
      //   814: iconst_5
      //   815: getstatic java/awt/font/NumericShaper$Range.GURMUKHI : Ljava/awt/font/NumericShaper$Range;
      //   818: aastore
      //   819: dup
      //   820: bipush #6
      //   822: getstatic java/awt/font/NumericShaper$Range.GUJARATI : Ljava/awt/font/NumericShaper$Range;
      //   825: aastore
      //   826: dup
      //   827: bipush #7
      //   829: getstatic java/awt/font/NumericShaper$Range.ORIYA : Ljava/awt/font/NumericShaper$Range;
      //   832: aastore
      //   833: dup
      //   834: bipush #8
      //   836: getstatic java/awt/font/NumericShaper$Range.TAMIL : Ljava/awt/font/NumericShaper$Range;
      //   839: aastore
      //   840: dup
      //   841: bipush #9
      //   843: getstatic java/awt/font/NumericShaper$Range.TELUGU : Ljava/awt/font/NumericShaper$Range;
      //   846: aastore
      //   847: dup
      //   848: bipush #10
      //   850: getstatic java/awt/font/NumericShaper$Range.KANNADA : Ljava/awt/font/NumericShaper$Range;
      //   853: aastore
      //   854: dup
      //   855: bipush #11
      //   857: getstatic java/awt/font/NumericShaper$Range.MALAYALAM : Ljava/awt/font/NumericShaper$Range;
      //   860: aastore
      //   861: dup
      //   862: bipush #12
      //   864: getstatic java/awt/font/NumericShaper$Range.THAI : Ljava/awt/font/NumericShaper$Range;
      //   867: aastore
      //   868: dup
      //   869: bipush #13
      //   871: getstatic java/awt/font/NumericShaper$Range.LAO : Ljava/awt/font/NumericShaper$Range;
      //   874: aastore
      //   875: dup
      //   876: bipush #14
      //   878: getstatic java/awt/font/NumericShaper$Range.TIBETAN : Ljava/awt/font/NumericShaper$Range;
      //   881: aastore
      //   882: dup
      //   883: bipush #15
      //   885: getstatic java/awt/font/NumericShaper$Range.MYANMAR : Ljava/awt/font/NumericShaper$Range;
      //   888: aastore
      //   889: dup
      //   890: bipush #16
      //   892: getstatic java/awt/font/NumericShaper$Range.ETHIOPIC : Ljava/awt/font/NumericShaper$Range;
      //   895: aastore
      //   896: dup
      //   897: bipush #17
      //   899: getstatic java/awt/font/NumericShaper$Range.KHMER : Ljava/awt/font/NumericShaper$Range;
      //   902: aastore
      //   903: dup
      //   904: bipush #18
      //   906: getstatic java/awt/font/NumericShaper$Range.MONGOLIAN : Ljava/awt/font/NumericShaper$Range;
      //   909: aastore
      //   910: dup
      //   911: bipush #19
      //   913: getstatic java/awt/font/NumericShaper$Range.NKO : Ljava/awt/font/NumericShaper$Range;
      //   916: aastore
      //   917: dup
      //   918: bipush #20
      //   920: getstatic java/awt/font/NumericShaper$Range.MYANMAR_SHAN : Ljava/awt/font/NumericShaper$Range;
      //   923: aastore
      //   924: dup
      //   925: bipush #21
      //   927: getstatic java/awt/font/NumericShaper$Range.LIMBU : Ljava/awt/font/NumericShaper$Range;
      //   930: aastore
      //   931: dup
      //   932: bipush #22
      //   934: getstatic java/awt/font/NumericShaper$Range.NEW_TAI_LUE : Ljava/awt/font/NumericShaper$Range;
      //   937: aastore
      //   938: dup
      //   939: bipush #23
      //   941: getstatic java/awt/font/NumericShaper$Range.BALINESE : Ljava/awt/font/NumericShaper$Range;
      //   944: aastore
      //   945: dup
      //   946: bipush #24
      //   948: getstatic java/awt/font/NumericShaper$Range.SUNDANESE : Ljava/awt/font/NumericShaper$Range;
      //   951: aastore
      //   952: dup
      //   953: bipush #25
      //   955: getstatic java/awt/font/NumericShaper$Range.LEPCHA : Ljava/awt/font/NumericShaper$Range;
      //   958: aastore
      //   959: dup
      //   960: bipush #26
      //   962: getstatic java/awt/font/NumericShaper$Range.OL_CHIKI : Ljava/awt/font/NumericShaper$Range;
      //   965: aastore
      //   966: dup
      //   967: bipush #27
      //   969: getstatic java/awt/font/NumericShaper$Range.VAI : Ljava/awt/font/NumericShaper$Range;
      //   972: aastore
      //   973: dup
      //   974: bipush #28
      //   976: getstatic java/awt/font/NumericShaper$Range.SAURASHTRA : Ljava/awt/font/NumericShaper$Range;
      //   979: aastore
      //   980: dup
      //   981: bipush #29
      //   983: getstatic java/awt/font/NumericShaper$Range.KAYAH_LI : Ljava/awt/font/NumericShaper$Range;
      //   986: aastore
      //   987: dup
      //   988: bipush #30
      //   990: getstatic java/awt/font/NumericShaper$Range.CHAM : Ljava/awt/font/NumericShaper$Range;
      //   993: aastore
      //   994: dup
      //   995: bipush #31
      //   997: getstatic java/awt/font/NumericShaper$Range.TAI_THAM_HORA : Ljava/awt/font/NumericShaper$Range;
      //   1000: aastore
      //   1001: dup
      //   1002: bipush #32
      //   1004: getstatic java/awt/font/NumericShaper$Range.TAI_THAM_THAM : Ljava/awt/font/NumericShaper$Range;
      //   1007: aastore
      //   1008: dup
      //   1009: bipush #33
      //   1011: getstatic java/awt/font/NumericShaper$Range.JAVANESE : Ljava/awt/font/NumericShaper$Range;
      //   1014: aastore
      //   1015: dup
      //   1016: bipush #34
      //   1018: getstatic java/awt/font/NumericShaper$Range.MEETEI_MAYEK : Ljava/awt/font/NumericShaper$Range;
      //   1021: aastore
      //   1022: putstatic java/awt/font/NumericShaper$Range.$VALUES : [Ljava/awt/font/NumericShaper$Range;
      //   1025: return
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\font\NumericShaper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */