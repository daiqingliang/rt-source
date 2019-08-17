package sun.misc;

public class Regexp {
  public boolean ignoreCase;
  
  public String exp;
  
  public String prefix;
  
  public String suffix;
  
  public boolean exact;
  
  public int prefixLen;
  
  public int suffixLen;
  
  public int totalLen;
  
  public String[] mids;
  
  public Regexp(String paramString) {
    this.exp = paramString;
    int i = paramString.indexOf('*');
    int j = paramString.lastIndexOf('*');
    if (i < 0) {
      this.totalLen = paramString.length();
      this.exact = true;
    } else {
      this.prefixLen = i;
      if (i == 0) {
        this.prefix = null;
      } else {
        this.prefix = paramString.substring(0, i);
      } 
      this.suffixLen = paramString.length() - j - 1;
      if (this.suffixLen == 0) {
        this.suffix = null;
      } else {
        this.suffix = paramString.substring(j + 1);
      } 
      byte b = 0;
      int k;
      for (k = i; k < j && k >= 0; k = paramString.indexOf('*', k + 1))
        b++; 
      this.totalLen = this.prefixLen + this.suffixLen;
      if (b > 0) {
        this.mids = new String[b];
        k = i;
        for (byte b1 = 0; b1 < b; b1++) {
          int m = paramString.indexOf('*', ++k);
          if (k < m) {
            this.mids[b1] = paramString.substring(k, m);
            this.totalLen += this.mids[b1].length();
          } 
          k = m;
        } 
      } 
    } 
  }
  
  final boolean matches(String paramString) { return matches(paramString, 0, paramString.length()); }
  
  boolean matches(String paramString, int paramInt1, int paramInt2) {
    if (this.exact)
      return (paramInt2 == this.totalLen && this.exp.regionMatches(this.ignoreCase, 0, paramString, paramInt1, paramInt2)); 
    if (paramInt2 < this.totalLen)
      return false; 
    if ((this.prefixLen > 0 && !this.prefix.regionMatches(this.ignoreCase, 0, paramString, paramInt1, this.prefixLen)) || (this.suffixLen > 0 && !this.suffix.regionMatches(this.ignoreCase, 0, paramString, paramInt1 + paramInt2 - this.suffixLen, this.suffixLen)))
      return false; 
    if (this.mids == null)
      return true; 
    int i = this.mids.length;
    int j = paramInt1 + this.prefixLen;
    int k = paramInt1 + paramInt2 - this.suffixLen;
    for (byte b = 0; b < i; b++) {
      String str = this.mids[b];
      int m = str.length();
      while (j + m <= k && !str.regionMatches(this.ignoreCase, 0, paramString, j, m))
        j++; 
      if (j + m > k)
        return false; 
      j += m;
    } 
    return true;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\Regexp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */