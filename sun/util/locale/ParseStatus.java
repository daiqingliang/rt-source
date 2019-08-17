package sun.util.locale;

public class ParseStatus {
  int parseLength;
  
  int errorIndex;
  
  String errorMsg;
  
  public ParseStatus() { reset(); }
  
  public void reset() {
    this.parseLength = 0;
    this.errorIndex = -1;
    this.errorMsg = null;
  }
  
  public boolean isError() { return (this.errorIndex >= 0); }
  
  public int getErrorIndex() { return this.errorIndex; }
  
  public int getParseLength() { return this.parseLength; }
  
  public String getErrorMessage() { return this.errorMsg; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\su\\util\locale\ParseStatus.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */