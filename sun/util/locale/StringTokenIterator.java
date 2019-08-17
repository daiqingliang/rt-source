package sun.util.locale;

public class StringTokenIterator {
  private String text;
  
  private String dlms;
  
  private char delimiterChar;
  
  private String token;
  
  private int start;
  
  private int end;
  
  private boolean done;
  
  public StringTokenIterator(String paramString1, String paramString2) {
    this.text = paramString1;
    if (paramString2.length() == 1) {
      this.delimiterChar = paramString2.charAt(0);
    } else {
      this.dlms = paramString2;
    } 
    setStart(0);
  }
  
  public String first() {
    setStart(0);
    return this.token;
  }
  
  public String current() { return this.token; }
  
  public int currentStart() { return this.start; }
  
  public int currentEnd() { return this.end; }
  
  public boolean isDone() { return this.done; }
  
  public String next() {
    if (hasNext()) {
      this.start = this.end + 1;
      this.end = nextDelimiter(this.start);
      this.token = this.text.substring(this.start, this.end);
    } else {
      this.start = this.end;
      this.token = null;
      this.done = true;
    } 
    return this.token;
  }
  
  public boolean hasNext() { return (this.end < this.text.length()); }
  
  public StringTokenIterator setStart(int paramInt) {
    if (paramInt > this.text.length())
      throw new IndexOutOfBoundsException(); 
    this.start = paramInt;
    this.end = nextDelimiter(this.start);
    this.token = this.text.substring(this.start, this.end);
    this.done = false;
    return this;
  }
  
  public StringTokenIterator setText(String paramString) {
    this.text = paramString;
    setStart(0);
    return this;
  }
  
  private int nextDelimiter(int paramInt) {
    int i = this.text.length();
    if (this.dlms == null) {
      for (int j = paramInt; j < i; j++) {
        if (this.text.charAt(j) == this.delimiterChar)
          return j; 
      } 
    } else {
      int j = this.dlms.length();
      for (int k = paramInt; k < i; k++) {
        char c = this.text.charAt(k);
        for (byte b = 0; b < j; b++) {
          if (c == this.dlms.charAt(b))
            return k; 
        } 
      } 
    } 
    return i;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\su\\util\locale\StringTokenIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */