package sun.security.provider;

abstract class SHA2 extends DigestBase {
  private static final int ITERATION = 64;
  
  private static final int[] ROUND_CONSTS = { 
      1116352408, 1899447441, -1245643825, -373957723, 961987163, 1508970993, -1841331548, -1424204075, -670586216, 310598401, 
      607225278, 1426881987, 1925078388, -2132889090, -1680079193, -1046744716, -459576895, -272742522, 264347078, 604807628, 
      770255983, 1249150122, 1555081692, 1996064986, -1740746414, -1473132947, -1341970488, -1084653625, -958395405, -710438585, 
      113926993, 338241895, 666307205, 773529912, 1294757372, 1396182291, 1695183700, 1986661051, -2117940946, -1838011259, 
      -1564481375, -1474664885, -1035236496, -949202525, -778901479, -694614492, -200395387, 275423344, 430227734, 506948616, 
      659060556, 883997877, 958139571, 1322822218, 1537002063, 1747873779, 1955562222, 2024104815, -2067236844, -1933114872, 
      -1866530822, -1538233109, -1090935817, -965641998 };
  
  private int[] W;
  
  private int[] state;
  
  private final int[] initialHashes;
  
  SHA2(String paramString, int paramInt, int[] paramArrayOfInt) {
    super(paramString, paramInt, 64);
    this.initialHashes = paramArrayOfInt;
    this.state = new int[8];
    this.W = new int[64];
    implReset();
  }
  
  void implReset() { System.arraycopy(this.initialHashes, 0, this.state, 0, this.state.length); }
  
  void implDigest(byte[] paramArrayOfByte, int paramInt) {
    long l = this.bytesProcessed << 3;
    int i = (int)this.bytesProcessed & 0x3F;
    int j = (i < 56) ? (56 - i) : (120 - i);
    engineUpdate(padding, 0, j);
    ByteArrayAccess.i2bBig4((int)(l >>> 32), this.buffer, 56);
    ByteArrayAccess.i2bBig4((int)l, this.buffer, 60);
    implCompress(this.buffer, 0);
    ByteArrayAccess.i2bBig(this.state, 0, paramArrayOfByte, paramInt, engineGetDigestLength());
  }
  
  private static int lf_ch(int paramInt1, int paramInt2, int paramInt3) { return paramInt1 & paramInt2 ^ (paramInt1 ^ 0xFFFFFFFF) & paramInt3; }
  
  private static int lf_maj(int paramInt1, int paramInt2, int paramInt3) { return paramInt1 & paramInt2 ^ paramInt1 & paramInt3 ^ paramInt2 & paramInt3; }
  
  private static int lf_R(int paramInt1, int paramInt2) { return paramInt1 >>> paramInt2; }
  
  private static int lf_S(int paramInt1, int paramInt2) { return paramInt1 >>> paramInt2 | paramInt1 << 32 - paramInt2; }
  
  private static int lf_sigma0(int paramInt) { return lf_S(paramInt, 2) ^ lf_S(paramInt, 13) ^ lf_S(paramInt, 22); }
  
  private static int lf_sigma1(int paramInt) { return lf_S(paramInt, 6) ^ lf_S(paramInt, 11) ^ lf_S(paramInt, 25); }
  
  private static int lf_delta0(int paramInt) { return lf_S(paramInt, 7) ^ lf_S(paramInt, 18) ^ lf_R(paramInt, 3); }
  
  private static int lf_delta1(int paramInt) { return lf_S(paramInt, 17) ^ lf_S(paramInt, 19) ^ lf_R(paramInt, 10); }
  
  void implCompress(byte[] paramArrayOfByte, int paramInt) {
    ByteArrayAccess.b2iBig64(paramArrayOfByte, paramInt, this.W);
    int i;
    for (i = 16; i < 64; i++)
      this.W[i] = lf_delta1(this.W[i - 2]) + this.W[i - 7] + lf_delta0(this.W[i - 15]) + this.W[i - 16]; 
    i = this.state[0];
    int j = this.state[1];
    int k = this.state[2];
    int m = this.state[3];
    int n = this.state[4];
    int i1 = this.state[5];
    int i2 = this.state[6];
    int i3 = this.state[7];
    for (byte b = 0; b < 64; b++) {
      int i4 = i3 + lf_sigma1(n) + lf_ch(n, i1, i2) + ROUND_CONSTS[b] + this.W[b];
      int i5 = lf_sigma0(i) + lf_maj(i, j, k);
      i3 = i2;
      i2 = i1;
      i1 = n;
      n = m + i4;
      m = k;
      k = j;
      j = i;
      i = i4 + i5;
    } 
    this.state[0] = this.state[0] + i;
    this.state[1] = this.state[1] + j;
    this.state[2] = this.state[2] + k;
    this.state[3] = this.state[3] + m;
    this.state[4] = this.state[4] + n;
    this.state[5] = this.state[5] + i1;
    this.state[6] = this.state[6] + i2;
    this.state[7] = this.state[7] + i3;
  }
  
  public Object clone() throws CloneNotSupportedException {
    SHA2 sHA2 = (SHA2)super.clone();
    sHA2.state = (int[])sHA2.state.clone();
    sHA2.W = new int[64];
    return sHA2;
  }
  
  public static final class SHA224 extends SHA2 {
    private static final int[] INITIAL_HASHES = { -1056596264, 914150663, 812702999, -150054599, -4191439, 1750603025, 1694076839, -1090891868 };
    
    public SHA224() { super("SHA-224", 28, INITIAL_HASHES); }
  }
  
  public static final class SHA256 extends SHA2 {
    private static final int[] INITIAL_HASHES = { 1779033703, -1150833019, 1013904242, -1521486534, 1359893119, -1694144372, 528734635, 1541459225 };
    
    public SHA256() { super("SHA-256", 32, INITIAL_HASHES); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\provider\SHA2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */