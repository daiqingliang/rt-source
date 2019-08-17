package sun.reflect;

public class SignatureIterator {
  private final String sig;
  
  private int idx;
  
  public SignatureIterator(String paramString) {
    this.sig = paramString;
    reset();
  }
  
  public void reset() { this.idx = 1; }
  
  public boolean atEnd() { return (this.sig.charAt(this.idx) == ')'); }
  
  public String next() {
    if (atEnd())
      return null; 
    char c = this.sig.charAt(this.idx);
    if (c != '[' && c != 'L') {
      this.idx++;
      return new String(new char[] { c });
    } 
    int i = this.idx;
    if (c == '[')
      while ((c = this.sig.charAt(i)) == '[')
        i++;  
    if (c == 'L')
      while (this.sig.charAt(i) != ';')
        i++;  
    int j = this.idx;
    this.idx = i + 1;
    return this.sig.substring(j, this.idx);
  }
  
  public String returnType() {
    if (!atEnd())
      throw new InternalError("Illegal use of SignatureIterator"); 
    return this.sig.substring(this.idx + 1, this.sig.length());
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\SignatureIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */