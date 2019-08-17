package javax.management;

public class InvalidApplicationException extends Exception {
  private static final long serialVersionUID = -3048022274675537269L;
  
  private Object val;
  
  public InvalidApplicationException(Object paramObject) { this.val = paramObject; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\InvalidApplicationException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */