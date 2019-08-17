package sun.text.normalizer;

import java.text.CharacterIterator;
import java.text.Normalizer;

public final class NormalizerBase implements Cloneable {
  private char[] buffer = new char[100];
  
  private int bufferStart = 0;
  
  private int bufferPos = 0;
  
  private int bufferLimit = 0;
  
  private UCharacterIterator text;
  
  private Mode mode = NFC;
  
  private int options = 0;
  
  private int currentIndex;
  
  private int nextIndex;
  
  public static final int UNICODE_3_2 = 32;
  
  public static final int DONE = -1;
  
  public static final Mode NONE = new Mode(1, null);
  
  public static final Mode NFD = new NFDMode(2, null);
  
  public static final Mode NFKD = new NFKDMode(3, null);
  
  public static final Mode NFC = new NFCMode(4, null);
  
  public static final Mode NFKC = new NFKCMode(5, null);
  
  public static final QuickCheckResult NO = new QuickCheckResult(0, null);
  
  public static final QuickCheckResult YES = new QuickCheckResult(1, null);
  
  public static final QuickCheckResult MAYBE = new QuickCheckResult(2, null);
  
  private static final int MAX_BUF_SIZE_COMPOSE = 2;
  
  private static final int MAX_BUF_SIZE_DECOMPOSE = 3;
  
  public static final int UNICODE_3_2_0_ORIGINAL = 262432;
  
  public static final int UNICODE_LATEST = 0;
  
  public NormalizerBase(String paramString, Mode paramMode, int paramInt) {
    this.text = UCharacterIterator.getInstance(paramString);
    this.mode = paramMode;
    this.options = paramInt;
  }
  
  public NormalizerBase(CharacterIterator paramCharacterIterator, Mode paramMode) { this(paramCharacterIterator, paramMode, 0); }
  
  public NormalizerBase(CharacterIterator paramCharacterIterator, Mode paramMode, int paramInt) {
    this.text = UCharacterIterator.getInstance((CharacterIterator)paramCharacterIterator.clone());
    this.mode = paramMode;
    this.options = paramInt;
  }
  
  public Object clone() {
    try {
      NormalizerBase normalizerBase = (NormalizerBase)super.clone();
      normalizerBase.text = (UCharacterIterator)this.text.clone();
      if (this.buffer != null) {
        normalizerBase.buffer = new char[this.buffer.length];
        System.arraycopy(this.buffer, 0, normalizerBase.buffer, 0, this.buffer.length);
      } 
      return normalizerBase;
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError(cloneNotSupportedException.toString(), cloneNotSupportedException);
    } 
  }
  
  public static String compose(String paramString, boolean paramBoolean, int paramInt) {
    char[] arrayOfChar2;
    char[] arrayOfChar1;
    if (paramInt == 262432) {
      String str = NormalizerImpl.convert(paramString);
      arrayOfChar1 = new char[str.length() * 2];
      arrayOfChar2 = str.toCharArray();
    } else {
      arrayOfChar1 = new char[paramString.length() * 2];
      arrayOfChar2 = paramString.toCharArray();
    } 
    int i = 0;
    UnicodeSet unicodeSet = NormalizerImpl.getNX(paramInt);
    paramInt &= 0xFFFFCF00;
    if (paramBoolean)
      paramInt |= 0x1000; 
    while (true) {
      i = NormalizerImpl.compose(arrayOfChar2, 0, arrayOfChar2.length, arrayOfChar1, 0, arrayOfChar1.length, paramInt, unicodeSet);
      if (i <= arrayOfChar1.length)
        return new String(arrayOfChar1, 0, i); 
      arrayOfChar1 = new char[i];
    } 
  }
  
  public static String decompose(String paramString, boolean paramBoolean) { return decompose(paramString, paramBoolean, 0); }
  
  public static String decompose(String paramString, boolean paramBoolean, int paramInt) {
    int[] arrayOfInt = new int[1];
    int i = 0;
    UnicodeSet unicodeSet = NormalizerImpl.getNX(paramInt);
    if (paramInt == 262432) {
      String str = NormalizerImpl.convert(paramString);
      for (char[] arrayOfChar1 = new char[str.length() * 3];; arrayOfChar1 = new char[i]) {
        i = NormalizerImpl.decompose(str.toCharArray(), 0, str.length(), arrayOfChar1, 0, arrayOfChar1.length, paramBoolean, arrayOfInt, unicodeSet);
        if (i <= arrayOfChar1.length)
          return new String(arrayOfChar1, 0, i); 
      } 
    } 
    for (char[] arrayOfChar = new char[paramString.length() * 3];; arrayOfChar = new char[i]) {
      i = NormalizerImpl.decompose(paramString.toCharArray(), 0, paramString.length(), arrayOfChar, 0, arrayOfChar.length, paramBoolean, arrayOfInt, unicodeSet);
      if (i <= arrayOfChar.length)
        return new String(arrayOfChar, 0, i); 
    } 
  }
  
  public static int normalize(char[] paramArrayOfChar1, int paramInt1, int paramInt2, char[] paramArrayOfChar2, int paramInt3, int paramInt4, Mode paramMode, int paramInt5) {
    int i = paramMode.normalize(paramArrayOfChar1, paramInt1, paramInt2, paramArrayOfChar2, paramInt3, paramInt4, paramInt5);
    if (i <= paramInt4 - paramInt3)
      return i; 
    throw new IndexOutOfBoundsException(Integer.toString(i));
  }
  
  public int current() { return (this.bufferPos < this.bufferLimit || nextNormalize()) ? getCodePointAt(this.bufferPos) : -1; }
  
  public int next() {
    if (this.bufferPos < this.bufferLimit || nextNormalize()) {
      int i = getCodePointAt(this.bufferPos);
      this.bufferPos += ((i > 65535) ? 2 : 1);
      return i;
    } 
    return -1;
  }
  
  public int previous() {
    if (this.bufferPos > 0 || previousNormalize()) {
      int i = getCodePointAt(this.bufferPos - 1);
      this.bufferPos -= ((i > 65535) ? 2 : 1);
      return i;
    } 
    return -1;
  }
  
  public void reset() {
    this.text.setIndex(0);
    this.currentIndex = this.nextIndex = 0;
    clearBuffer();
  }
  
  public void setIndexOnly(int paramInt) {
    this.text.setIndex(paramInt);
    this.currentIndex = this.nextIndex = paramInt;
    clearBuffer();
  }
  
  @Deprecated
  public int setIndex(int paramInt) {
    setIndexOnly(paramInt);
    return current();
  }
  
  @Deprecated
  public int getBeginIndex() { return 0; }
  
  @Deprecated
  public int getEndIndex() { return endIndex(); }
  
  public int getIndex() { return (this.bufferPos < this.bufferLimit) ? this.currentIndex : this.nextIndex; }
  
  public int endIndex() { return this.text.getLength(); }
  
  public void setMode(Mode paramMode) { this.mode = paramMode; }
  
  public Mode getMode() { return this.mode; }
  
  public void setText(String paramString) {
    UCharacterIterator uCharacterIterator = UCharacterIterator.getInstance(paramString);
    if (uCharacterIterator == null)
      throw new InternalError("Could not create a new UCharacterIterator"); 
    this.text = uCharacterIterator;
    reset();
  }
  
  public void setText(CharacterIterator paramCharacterIterator) {
    UCharacterIterator uCharacterIterator = UCharacterIterator.getInstance(paramCharacterIterator);
    if (uCharacterIterator == null)
      throw new InternalError("Could not create a new UCharacterIterator"); 
    this.text = uCharacterIterator;
    this.currentIndex = this.nextIndex = 0;
    clearBuffer();
  }
  
  private static long getPrevNorm32(UCharacterIterator paramUCharacterIterator, int paramInt1, int paramInt2, char[] paramArrayOfChar) {
    int i = 0;
    if ((i = paramUCharacterIterator.previous()) == -1)
      return 0L; 
    paramArrayOfChar[0] = (char)i;
    paramArrayOfChar[1] = Character.MIN_VALUE;
    if (paramArrayOfChar[0] < paramInt1)
      return 0L; 
    if (!UTF16.isSurrogate(paramArrayOfChar[0]))
      return NormalizerImpl.getNorm32(paramArrayOfChar[0]); 
    if (UTF16.isLeadSurrogate(paramArrayOfChar[0]) || paramUCharacterIterator.getIndex() == 0) {
      paramArrayOfChar[1] = (char)paramUCharacterIterator.current();
      return 0L;
    } 
    if (UTF16.isLeadSurrogate(paramArrayOfChar[1] = (char)paramUCharacterIterator.previous())) {
      long l = NormalizerImpl.getNorm32(paramArrayOfChar[1]);
      return ((l & paramInt2) == 0L) ? 0L : NormalizerImpl.getNorm32FromSurrogatePair(l, paramArrayOfChar[0]);
    } 
    paramUCharacterIterator.moveIndex(1);
    return 0L;
  }
  
  private static int findPreviousIterationBoundary(UCharacterIterator paramUCharacterIterator, IsPrevBoundary paramIsPrevBoundary, int paramInt1, int paramInt2, char[] paramArrayOfChar, int[] paramArrayOfInt) {
    char[] arrayOfChar = new char[2];
    paramArrayOfInt[0] = paramArrayOfChar.length;
    arrayOfChar[0] = Character.MIN_VALUE;
    while (paramUCharacterIterator.getIndex() > 0 && arrayOfChar[0] != -1) {
      boolean bool = paramIsPrevBoundary.isPrevBoundary(paramUCharacterIterator, paramInt1, paramInt2, arrayOfChar);
      if (paramArrayOfInt[0] < ((arrayOfChar[1] == '\000') ? 1 : 2)) {
        char[] arrayOfChar1 = new char[paramArrayOfChar.length * 2];
        System.arraycopy(paramArrayOfChar, paramArrayOfInt[0], arrayOfChar1, arrayOfChar1.length - paramArrayOfChar.length - paramArrayOfInt[0], paramArrayOfChar.length - paramArrayOfInt[0]);
        paramArrayOfInt[0] = paramArrayOfInt[0] + arrayOfChar1.length - paramArrayOfChar.length;
        paramArrayOfChar = arrayOfChar1;
        arrayOfChar1 = null;
      } 
      paramArrayOfInt[0] = paramArrayOfInt[0] - 1;
      paramArrayOfChar[paramArrayOfInt[0] - 1] = arrayOfChar[0];
      if (arrayOfChar[1] != '\000') {
        paramArrayOfInt[0] = paramArrayOfInt[0] - 1;
        paramArrayOfChar[paramArrayOfInt[0] - 1] = arrayOfChar[1];
      } 
      if (bool)
        break; 
    } 
    return paramArrayOfChar.length - paramArrayOfInt[0];
  }
  
  private static int previous(UCharacterIterator paramUCharacterIterator, char[] paramArrayOfChar, int paramInt1, int paramInt2, Mode paramMode, boolean paramBoolean, boolean[] paramArrayOfBoolean, int paramInt3) {
    int m = paramInt2 - paramInt1;
    int i = 0;
    if (paramArrayOfBoolean != null)
      paramArrayOfBoolean[0] = false; 
    char c = (char)paramMode.getMinC();
    int k = paramMode.getMask();
    IsPrevBoundary isPrevBoundary = paramMode.getPrevBoundary();
    if (isPrevBoundary == null) {
      i = 0;
      int n;
      if ((n = paramUCharacterIterator.previous()) >= 0) {
        i = 1;
        if (UTF16.isTrailSurrogate((char)n)) {
          int i1 = paramUCharacterIterator.previous();
          if (i1 != -1)
            if (UTF16.isLeadSurrogate((char)i1)) {
              if (m >= 2) {
                paramArrayOfChar[1] = (char)n;
                i = 2;
              } 
              n = i1;
            } else {
              paramUCharacterIterator.moveIndex(1);
            }  
        } 
        if (m > 0)
          paramArrayOfChar[0] = (char)n; 
      } 
      return i;
    } 
    char[] arrayOfChar = new char[100];
    int[] arrayOfInt = new int[1];
    int j = findPreviousIterationBoundary(paramUCharacterIterator, isPrevBoundary, c, k, arrayOfChar, arrayOfInt);
    if (j > 0)
      if (paramBoolean) {
        i = normalize(arrayOfChar, arrayOfInt[0], arrayOfInt[0] + j, paramArrayOfChar, paramInt1, paramInt2, paramMode, paramInt3);
        if (paramArrayOfBoolean != null)
          paramArrayOfBoolean[0] = (i != j || Utility.arrayRegionMatches(arrayOfChar, 0, paramArrayOfChar, paramInt1, paramInt2)); 
      } else if (m > 0) {
        System.arraycopy(arrayOfChar, arrayOfInt[0], paramArrayOfChar, 0, (j < m) ? j : m);
      }  
    return i;
  }
  
  private static long getNextNorm32(UCharacterIterator paramUCharacterIterator, int paramInt1, int paramInt2, int[] paramArrayOfInt) {
    paramArrayOfInt[0] = paramUCharacterIterator.next();
    paramArrayOfInt[1] = 0;
    if (paramArrayOfInt[0] < paramInt1)
      return 0L; 
    long l = NormalizerImpl.getNorm32((char)paramArrayOfInt[0]);
    if (UTF16.isLeadSurrogate((char)paramArrayOfInt[0])) {
      paramArrayOfInt[1] = paramUCharacterIterator.current();
      if (paramUCharacterIterator.current() != -1 && UTF16.isTrailSurrogate((char)paramUCharacterIterator.current())) {
        paramUCharacterIterator.moveIndex(1);
        return ((l & paramInt2) == 0L) ? 0L : NormalizerImpl.getNorm32FromSurrogatePair(l, (char)paramArrayOfInt[1]);
      } 
      return 0L;
    } 
    return l;
  }
  
  private static int findNextIterationBoundary(UCharacterIterator paramUCharacterIterator, IsNextBoundary paramIsNextBoundary, int paramInt1, int paramInt2, char[] paramArrayOfChar) {
    if (paramUCharacterIterator.current() == -1)
      return 0; 
    int[] arrayOfInt = new int[2];
    arrayOfInt[0] = paramUCharacterIterator.next();
    paramArrayOfChar[0] = (char)arrayOfInt[0];
    byte b = 1;
    if (UTF16.isLeadSurrogate((char)arrayOfInt[0]) && paramUCharacterIterator.current() != -1) {
      arrayOfInt[1] = paramUCharacterIterator.next();
      if (UTF16.isTrailSurrogate((char)paramUCharacterIterator.next())) {
        paramArrayOfChar[b++] = (char)arrayOfInt[1];
      } else {
        paramUCharacterIterator.moveIndex(-1);
      } 
    } 
    while (paramUCharacterIterator.current() != -1) {
      if (paramIsNextBoundary.isNextBoundary(paramUCharacterIterator, paramInt1, paramInt2, arrayOfInt)) {
        paramUCharacterIterator.moveIndex((arrayOfInt[1] == 0) ? -1 : -2);
        break;
      } 
      if (b + ((arrayOfInt[1] == 0) ? 1 : 2) <= paramArrayOfChar.length) {
        paramArrayOfChar[b++] = (char)arrayOfInt[0];
        if (arrayOfInt[1] != 0)
          paramArrayOfChar[b++] = (char)arrayOfInt[1]; 
        continue;
      } 
      char[] arrayOfChar = new char[paramArrayOfChar.length * 2];
      System.arraycopy(paramArrayOfChar, 0, arrayOfChar, 0, b);
      paramArrayOfChar = arrayOfChar;
      paramArrayOfChar[b++] = (char)arrayOfInt[0];
      if (arrayOfInt[1] != 0)
        paramArrayOfChar[b++] = (char)arrayOfInt[1]; 
    } 
    return b;
  }
  
  private static int next(UCharacterIterator paramUCharacterIterator, char[] paramArrayOfChar, int paramInt1, int paramInt2, Mode paramMode, boolean paramBoolean, boolean[] paramArrayOfBoolean, int paramInt3) {
    int k = paramInt2 - paramInt1;
    int m = 0;
    if (paramArrayOfBoolean != null)
      paramArrayOfBoolean[0] = false; 
    char c = (char)paramMode.getMinC();
    int i = paramMode.getMask();
    IsNextBoundary isNextBoundary = paramMode.getNextBoundary();
    if (isNextBoundary == null) {
      m = 0;
      int n = paramUCharacterIterator.next();
      if (n != -1) {
        m = 1;
        if (UTF16.isLeadSurrogate((char)n)) {
          int i1 = paramUCharacterIterator.next();
          if (i1 != -1)
            if (UTF16.isTrailSurrogate((char)i1)) {
              if (k >= 2) {
                paramArrayOfChar[1] = (char)i1;
                m = 2;
              } 
            } else {
              paramUCharacterIterator.moveIndex(-1);
            }  
        } 
        if (k > 0)
          paramArrayOfChar[0] = (char)n; 
      } 
      return m;
    } 
    char[] arrayOfChar = new char[100];
    int[] arrayOfInt = new int[1];
    int j = findNextIterationBoundary(paramUCharacterIterator, isNextBoundary, c, i, arrayOfChar);
    if (j > 0)
      if (paramBoolean) {
        m = paramMode.normalize(arrayOfChar, arrayOfInt[0], j, paramArrayOfChar, paramInt1, paramInt2, paramInt3);
        if (paramArrayOfBoolean != null)
          paramArrayOfBoolean[0] = (m != j || Utility.arrayRegionMatches(arrayOfChar, arrayOfInt[0], paramArrayOfChar, paramInt1, m)); 
      } else if (k > 0) {
        System.arraycopy(arrayOfChar, 0, paramArrayOfChar, paramInt1, Math.min(j, k));
      }  
    return m;
  }
  
  private void clearBuffer() { this.bufferLimit = this.bufferStart = this.bufferPos = 0; }
  
  private boolean nextNormalize() {
    clearBuffer();
    this.currentIndex = this.nextIndex;
    this.text.setIndex(this.nextIndex);
    this.bufferLimit = next(this.text, this.buffer, this.bufferStart, this.buffer.length, this.mode, true, null, this.options);
    this.nextIndex = this.text.getIndex();
    return (this.bufferLimit > 0);
  }
  
  private boolean previousNormalize() {
    clearBuffer();
    this.nextIndex = this.currentIndex;
    this.text.setIndex(this.currentIndex);
    this.bufferLimit = previous(this.text, this.buffer, this.bufferStart, this.buffer.length, this.mode, true, null, this.options);
    this.currentIndex = this.text.getIndex();
    this.bufferPos = this.bufferLimit;
    return (this.bufferLimit > 0);
  }
  
  private int getCodePointAt(int paramInt) {
    if (UTF16.isSurrogate(this.buffer[paramInt]))
      if (UTF16.isLeadSurrogate(this.buffer[paramInt])) {
        if (paramInt + 1 < this.bufferLimit && UTF16.isTrailSurrogate(this.buffer[paramInt + 1]))
          return UCharacterProperty.getRawSupplementary(this.buffer[paramInt], this.buffer[paramInt + 1]); 
      } else if (UTF16.isTrailSurrogate(this.buffer[paramInt]) && paramInt > 0 && UTF16.isLeadSurrogate(this.buffer[paramInt - 1])) {
        return UCharacterProperty.getRawSupplementary(this.buffer[paramInt - 1], this.buffer[paramInt]);
      }  
    return this.buffer[paramInt];
  }
  
  public static boolean isNFSkippable(int paramInt, Mode paramMode) { return paramMode.isNFSkippable(paramInt); }
  
  public NormalizerBase(String paramString, Mode paramMode) { this(paramString, paramMode, 0); }
  
  public static String normalize(String paramString, Normalizer.Form paramForm) { return normalize(paramString, paramForm, 0); }
  
  public static String normalize(String paramString, Normalizer.Form paramForm, int paramInt) {
    int i = paramString.length();
    boolean bool = true;
    if (i < 80) {
      for (byte b = 0; b < i; b++) {
        if (paramString.charAt(b) > '') {
          bool = false;
          break;
        } 
      } 
    } else {
      char[] arrayOfChar = paramString.toCharArray();
      for (byte b = 0; b < i; b++) {
        if (arrayOfChar[b] > '') {
          bool = false;
          break;
        } 
      } 
    } 
    switch (paramForm) {
      case NFC:
        return bool ? paramString : NFC.normalize(paramString, paramInt);
      case NFD:
        return bool ? paramString : NFD.normalize(paramString, paramInt);
      case NFKC:
        return bool ? paramString : NFKC.normalize(paramString, paramInt);
      case NFKD:
        return bool ? paramString : NFKD.normalize(paramString, paramInt);
    } 
    throw new IllegalArgumentException("Unexpected normalization form: " + paramForm);
  }
  
  public static boolean isNormalized(String paramString, Normalizer.Form paramForm) { return isNormalized(paramString, paramForm, 0); }
  
  public static boolean isNormalized(String paramString, Normalizer.Form paramForm, int paramInt) {
    switch (paramForm) {
      case NFC:
        return (NFC.quickCheck(paramString.toCharArray(), false, paramString.length(), false, NormalizerImpl.getNX(paramInt)) == YES);
      case NFD:
        return (NFD.quickCheck(paramString.toCharArray(), false, paramString.length(), false, NormalizerImpl.getNX(paramInt)) == YES);
      case NFKC:
        return (NFKC.quickCheck(paramString.toCharArray(), false, paramString.length(), false, NormalizerImpl.getNX(paramInt)) == YES);
      case NFKD:
        return (NFKD.quickCheck(paramString.toCharArray(), false, paramString.length(), false, NormalizerImpl.getNX(paramInt)) == YES);
    } 
    throw new IllegalArgumentException("Unexpected normalization form: " + paramForm);
  }
  
  private static interface IsNextBoundary {
    boolean isNextBoundary(UCharacterIterator param1UCharacterIterator, int param1Int1, int param1Int2, int[] param1ArrayOfInt);
  }
  
  private static final class IsNextNFDSafe implements IsNextBoundary {
    private IsNextNFDSafe() {}
    
    public boolean isNextBoundary(UCharacterIterator param1UCharacterIterator, int param1Int1, int param1Int2, int[] param1ArrayOfInt) { return NormalizerImpl.isNFDSafe(NormalizerBase.getNextNorm32(param1UCharacterIterator, param1Int1, param1Int2, param1ArrayOfInt), param1Int2, param1Int2 & 0x3F); }
  }
  
  private static final class IsNextTrueStarter implements IsNextBoundary {
    private IsNextTrueStarter() {}
    
    public boolean isNextBoundary(UCharacterIterator param1UCharacterIterator, int param1Int1, int param1Int2, int[] param1ArrayOfInt) {
      int i = param1Int2 << 2 & 0xF;
      long l = NormalizerBase.getNextNorm32(param1UCharacterIterator, param1Int1, param1Int2 | i, param1ArrayOfInt);
      return NormalizerImpl.isTrueStarter(l, param1Int2, i);
    }
  }
  
  private static interface IsPrevBoundary {
    boolean isPrevBoundary(UCharacterIterator param1UCharacterIterator, int param1Int1, int param1Int2, char[] param1ArrayOfChar);
  }
  
  private static final class IsPrevNFDSafe implements IsPrevBoundary {
    private IsPrevNFDSafe() {}
    
    public boolean isPrevBoundary(UCharacterIterator param1UCharacterIterator, int param1Int1, int param1Int2, char[] param1ArrayOfChar) { return NormalizerImpl.isNFDSafe(NormalizerBase.getPrevNorm32(param1UCharacterIterator, param1Int1, param1Int2, param1ArrayOfChar), param1Int2, param1Int2 & 0x3F); }
  }
  
  private static final class IsPrevTrueStarter implements IsPrevBoundary {
    private IsPrevTrueStarter() {}
    
    public boolean isPrevBoundary(UCharacterIterator param1UCharacterIterator, int param1Int1, int param1Int2, char[] param1ArrayOfChar) {
      int i = param1Int2 << 2 & 0xF;
      long l = NormalizerBase.getPrevNorm32(param1UCharacterIterator, param1Int1, param1Int2 | i, param1ArrayOfChar);
      return NormalizerImpl.isTrueStarter(l, param1Int2, i);
    }
  }
  
  public static class Mode {
    private int modeValue;
    
    private Mode(int param1Int) { this.modeValue = param1Int; }
    
    protected int normalize(char[] param1ArrayOfChar1, int param1Int1, int param1Int2, char[] param1ArrayOfChar2, int param1Int3, int param1Int4, UnicodeSet param1UnicodeSet) {
      int i = param1Int2 - param1Int1;
      int j = param1Int4 - param1Int3;
      if (i > j)
        return i; 
      System.arraycopy(param1ArrayOfChar1, param1Int1, param1ArrayOfChar2, param1Int3, i);
      return i;
    }
    
    protected int normalize(char[] param1ArrayOfChar1, int param1Int1, int param1Int2, char[] param1ArrayOfChar2, int param1Int3, int param1Int4, int param1Int5) { return normalize(param1ArrayOfChar1, param1Int1, param1Int2, param1ArrayOfChar2, param1Int3, param1Int4, NormalizerImpl.getNX(param1Int5)); }
    
    protected String normalize(String param1String, int param1Int) { return param1String; }
    
    protected int getMinC() { return -1; }
    
    protected int getMask() { return -1; }
    
    protected NormalizerBase.IsPrevBoundary getPrevBoundary() { return null; }
    
    protected NormalizerBase.IsNextBoundary getNextBoundary() { return null; }
    
    protected NormalizerBase.QuickCheckResult quickCheck(char[] param1ArrayOfChar, int param1Int1, int param1Int2, boolean param1Boolean, UnicodeSet param1UnicodeSet) { return param1Boolean ? NormalizerBase.MAYBE : NormalizerBase.NO; }
    
    protected boolean isNFSkippable(int param1Int) { return true; }
  }
  
  private static final class NFCMode extends Mode {
    private NFCMode(int param1Int) { super(param1Int, null); }
    
    protected int normalize(char[] param1ArrayOfChar1, int param1Int1, int param1Int2, char[] param1ArrayOfChar2, int param1Int3, int param1Int4, UnicodeSet param1UnicodeSet) { return NormalizerImpl.compose(param1ArrayOfChar1, param1Int1, param1Int2, param1ArrayOfChar2, param1Int3, param1Int4, 0, param1UnicodeSet); }
    
    protected String normalize(String param1String, int param1Int) { return NormalizerBase.compose(param1String, false, param1Int); }
    
    protected int getMinC() { return NormalizerImpl.getFromIndexesArr(6); }
    
    protected NormalizerBase.IsPrevBoundary getPrevBoundary() { return new NormalizerBase.IsPrevTrueStarter(null); }
    
    protected NormalizerBase.IsNextBoundary getNextBoundary() { return new NormalizerBase.IsNextTrueStarter(null); }
    
    protected int getMask() { return 65297; }
    
    protected NormalizerBase.QuickCheckResult quickCheck(char[] param1ArrayOfChar, int param1Int1, int param1Int2, boolean param1Boolean, UnicodeSet param1UnicodeSet) { return NormalizerImpl.quickCheck(param1ArrayOfChar, param1Int1, param1Int2, NormalizerImpl.getFromIndexesArr(6), 17, 0, param1Boolean, param1UnicodeSet); }
    
    protected boolean isNFSkippable(int param1Int) { return NormalizerImpl.isNFSkippable(param1Int, this, 65473L); }
  }
  
  private static final class NFDMode extends Mode {
    private NFDMode(int param1Int) { super(param1Int, null); }
    
    protected int normalize(char[] param1ArrayOfChar1, int param1Int1, int param1Int2, char[] param1ArrayOfChar2, int param1Int3, int param1Int4, UnicodeSet param1UnicodeSet) {
      int[] arrayOfInt = new int[1];
      return NormalizerImpl.decompose(param1ArrayOfChar1, param1Int1, param1Int2, param1ArrayOfChar2, param1Int3, param1Int4, false, arrayOfInt, param1UnicodeSet);
    }
    
    protected String normalize(String param1String, int param1Int) { return NormalizerBase.decompose(param1String, false, param1Int); }
    
    protected int getMinC() { return 768; }
    
    protected NormalizerBase.IsPrevBoundary getPrevBoundary() { return new NormalizerBase.IsPrevNFDSafe(null); }
    
    protected NormalizerBase.IsNextBoundary getNextBoundary() { return new NormalizerBase.IsNextNFDSafe(null); }
    
    protected int getMask() { return 65284; }
    
    protected NormalizerBase.QuickCheckResult quickCheck(char[] param1ArrayOfChar, int param1Int1, int param1Int2, boolean param1Boolean, UnicodeSet param1UnicodeSet) { return NormalizerImpl.quickCheck(param1ArrayOfChar, param1Int1, param1Int2, NormalizerImpl.getFromIndexesArr(8), 4, 0, param1Boolean, param1UnicodeSet); }
    
    protected boolean isNFSkippable(int param1Int) { return NormalizerImpl.isNFSkippable(param1Int, this, 65284L); }
  }
  
  private static final class NFKCMode extends Mode {
    private NFKCMode(int param1Int) { super(param1Int, null); }
    
    protected int normalize(char[] param1ArrayOfChar1, int param1Int1, int param1Int2, char[] param1ArrayOfChar2, int param1Int3, int param1Int4, UnicodeSet param1UnicodeSet) { return NormalizerImpl.compose(param1ArrayOfChar1, param1Int1, param1Int2, param1ArrayOfChar2, param1Int3, param1Int4, 4096, param1UnicodeSet); }
    
    protected String normalize(String param1String, int param1Int) { return NormalizerBase.compose(param1String, true, param1Int); }
    
    protected int getMinC() { return NormalizerImpl.getFromIndexesArr(7); }
    
    protected NormalizerBase.IsPrevBoundary getPrevBoundary() { return new NormalizerBase.IsPrevTrueStarter(null); }
    
    protected NormalizerBase.IsNextBoundary getNextBoundary() { return new NormalizerBase.IsNextTrueStarter(null); }
    
    protected int getMask() { return 65314; }
    
    protected NormalizerBase.QuickCheckResult quickCheck(char[] param1ArrayOfChar, int param1Int1, int param1Int2, boolean param1Boolean, UnicodeSet param1UnicodeSet) { return NormalizerImpl.quickCheck(param1ArrayOfChar, param1Int1, param1Int2, NormalizerImpl.getFromIndexesArr(7), 34, 4096, param1Boolean, param1UnicodeSet); }
    
    protected boolean isNFSkippable(int param1Int) { return NormalizerImpl.isNFSkippable(param1Int, this, 65474L); }
  }
  
  private static final class NFKDMode extends Mode {
    private NFKDMode(int param1Int) { super(param1Int, null); }
    
    protected int normalize(char[] param1ArrayOfChar1, int param1Int1, int param1Int2, char[] param1ArrayOfChar2, int param1Int3, int param1Int4, UnicodeSet param1UnicodeSet) {
      int[] arrayOfInt = new int[1];
      return NormalizerImpl.decompose(param1ArrayOfChar1, param1Int1, param1Int2, param1ArrayOfChar2, param1Int3, param1Int4, true, arrayOfInt, param1UnicodeSet);
    }
    
    protected String normalize(String param1String, int param1Int) { return NormalizerBase.decompose(param1String, true, param1Int); }
    
    protected int getMinC() { return 768; }
    
    protected NormalizerBase.IsPrevBoundary getPrevBoundary() { return new NormalizerBase.IsPrevNFDSafe(null); }
    
    protected NormalizerBase.IsNextBoundary getNextBoundary() { return new NormalizerBase.IsNextNFDSafe(null); }
    
    protected int getMask() { return 65288; }
    
    protected NormalizerBase.QuickCheckResult quickCheck(char[] param1ArrayOfChar, int param1Int1, int param1Int2, boolean param1Boolean, UnicodeSet param1UnicodeSet) { return NormalizerImpl.quickCheck(param1ArrayOfChar, param1Int1, param1Int2, NormalizerImpl.getFromIndexesArr(9), 8, 4096, param1Boolean, param1UnicodeSet); }
    
    protected boolean isNFSkippable(int param1Int) { return NormalizerImpl.isNFSkippable(param1Int, this, 65288L); }
  }
  
  public static final class QuickCheckResult {
    private int resultValue;
    
    private QuickCheckResult(int param1Int) { this.resultValue = param1Int; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\text\normalizer\NormalizerBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */