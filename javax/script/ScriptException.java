package javax.script;

public class ScriptException extends Exception {
  private static final long serialVersionUID = 8265071037049225001L;
  
  private String fileName = null;
  
  private int lineNumber = -1;
  
  private int columnNumber = -1;
  
  public ScriptException(String paramString) { super(paramString); }
  
  public ScriptException(Exception paramException) { super(paramException); }
  
  public ScriptException(String paramString1, String paramString2, int paramInt) { super(paramString1); }
  
  public ScriptException(String paramString1, String paramString2, int paramInt1, int paramInt2) { super(paramString1); }
  
  public String getMessage() {
    String str = super.getMessage();
    if (this.fileName != null) {
      str = str + " in " + this.fileName;
      if (this.lineNumber != -1)
        str = str + " at line number " + this.lineNumber; 
      if (this.columnNumber != -1)
        str = str + " at column number " + this.columnNumber; 
    } 
    return str;
  }
  
  public int getLineNumber() { return this.lineNumber; }
  
  public int getColumnNumber() { return this.columnNumber; }
  
  public String getFileName() { return this.fileName; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\script\ScriptException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */