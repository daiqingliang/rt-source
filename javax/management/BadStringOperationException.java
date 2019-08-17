package javax.management;

public class BadStringOperationException extends Exception {
  private static final long serialVersionUID = 7802201238441662100L;
  
  private String op;
  
  public BadStringOperationException(String paramString) { this.op = paramString; }
  
  public String toString() { return "BadStringOperationException: " + this.op; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\BadStringOperationException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */