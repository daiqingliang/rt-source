package sun.font;

public final class ScriptRun {
  private char[] text;
  
  private int textStart;
  
  private int textLimit;
  
  private int scriptStart;
  
  private int scriptLimit;
  
  private int scriptCode;
  
  private int[] stack;
  
  private int parenSP;
  
  static final int SURROGATE_START = 65536;
  
  static final int LEAD_START = 55296;
  
  static final int LEAD_LIMIT = 56320;
  
  static final int TAIL_START = 56320;
  
  static final int TAIL_LIMIT = 57344;
  
  static final int LEAD_SURROGATE_SHIFT = 10;
  
  static final int SURROGATE_OFFSET = -56613888;
  
  static final int DONE = -1;
  
  private static int[] pairedChars = { 
      40, 41, 60, 62, 91, 93, 123, 125, 171, 187, 
      8216, 8217, 8220, 8221, 8249, 8250, 12296, 12297, 12298, 12299, 
      12300, 12301, 12302, 12303, 12304, 12305, 12308, 12309, 12310, 12311, 
      12312, 12313, 12314, 12315 };
  
  private static final int pairedCharPower = 1 << highBit(pairedChars.length);
  
  private static final int pairedCharExtra = pairedChars.length - pairedCharPower;
  
  public ScriptRun() {}
  
  public ScriptRun(char[] paramArrayOfChar, int paramInt1, int paramInt2) { init(paramArrayOfChar, paramInt1, paramInt2); }
  
  public void init(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    if (paramArrayOfChar == null || paramInt1 < 0 || paramInt2 < 0 || paramInt2 > paramArrayOfChar.length - paramInt1)
      throw new IllegalArgumentException(); 
    this.text = paramArrayOfChar;
    this.textStart = paramInt1;
    this.textLimit = paramInt1 + paramInt2;
    this.scriptStart = this.textStart;
    this.scriptLimit = this.textStart;
    this.scriptCode = -1;
    this.parenSP = 0;
  }
  
  public final int getScriptStart() { return this.scriptStart; }
  
  public final int getScriptLimit() { return this.scriptLimit; }
  
  public final int getScriptCode() { return this.scriptCode; }
  
  public final boolean next() {
    int i = this.parenSP;
    if (this.scriptLimit >= this.textLimit)
      return false; 
    this.scriptCode = 0;
    this.scriptStart = this.scriptLimit;
    int j;
    while ((j = nextCodePoint()) != -1) {
      int k = ScriptRunData.getScript(j);
      int m = (k == 0) ? getPairIndex(j) : -1;
      if (m >= 0)
        if ((m & true) == 0) {
          if (this.stack == null) {
            this.stack = new int[32];
          } else if (this.parenSP == this.stack.length) {
            int[] arrayOfInt = new int[this.stack.length + 32];
            System.arraycopy(this.stack, 0, arrayOfInt, 0, this.stack.length);
            this.stack = arrayOfInt;
          } 
          this.stack[this.parenSP++] = m;
          this.stack[this.parenSP++] = this.scriptCode;
        } else if (this.parenSP > 0) {
          int n = m & 0xFFFFFFFE;
          while (this.parenSP -= 2 >= 0 && this.stack[this.parenSP] != n);
          if (this.parenSP >= 0) {
            k = this.stack[this.parenSP + 1];
          } else {
            this.parenSP = 0;
          } 
          if (this.parenSP < i)
            i = this.parenSP; 
        }  
      if (sameScript(this.scriptCode, k)) {
        if (this.scriptCode <= 1 && k > 1) {
          this.scriptCode = k;
          while (i < this.parenSP) {
            this.stack[i + 1] = this.scriptCode;
            i += 2;
          } 
        } 
        if (m > 0 && (m & true) != 0 && this.parenSP > 0)
          this.parenSP -= 2; 
        continue;
      } 
      pushback(j);
    } 
    return true;
  }
  
  private final int nextCodePoint() {
    if (this.scriptLimit >= this.textLimit)
      return -1; 
    char c = this.text[this.scriptLimit++];
    if (c >= '?' && c < '?' && this.scriptLimit < this.textLimit) {
      char c1 = this.text[this.scriptLimit];
      if (c1 >= '?' && c1 < '') {
        this.scriptLimit++;
        c = (c << '\n') + c1 + -56613888;
      } 
    } 
    return c;
  }
  
  private final void pushback(int paramInt) {
    if (paramInt >= 0)
      if (paramInt >= 65536) {
        this.scriptLimit -= 2;
      } else {
        this.scriptLimit--;
      }  
  }
  
  private static boolean sameScript(int paramInt1, int paramInt2) { return (paramInt1 == paramInt2 || paramInt1 <= 1 || paramInt2 <= 1); }
  
  private static final byte highBit(int paramInt) {
    if (paramInt <= 0)
      return -32; 
    byte b = 0;
    if (paramInt >= 65536) {
      paramInt >>= 16;
      b = (byte)(b + 16);
    } 
    if (paramInt >= 256) {
      paramInt >>= 8;
      b = (byte)(b + 8);
    } 
    if (paramInt >= 16) {
      paramInt >>= 4;
      b = (byte)(b + 4);
    } 
    if (paramInt >= 4) {
      paramInt >>= 2;
      b = (byte)(b + 2);
    } 
    if (paramInt >= 2) {
      paramInt >>= 1;
      b = (byte)(b + 1);
    } 
    return b;
  }
  
  private static int getPairIndex(int paramInt) {
    int i = pairedCharPower;
    int j = 0;
    if (paramInt >= pairedChars[pairedCharExtra])
      j = pairedCharExtra; 
    while (i > 1) {
      i >>= 1;
      if (paramInt >= pairedChars[j + i])
        j += i; 
    } 
    if (pairedChars[j] != paramInt)
      j = -1; 
    return j;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\font\ScriptRun.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */