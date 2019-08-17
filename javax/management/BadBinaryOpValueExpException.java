package javax.management;

public class BadBinaryOpValueExpException extends Exception {
  private static final long serialVersionUID = 5068475589449021227L;
  
  private ValueExp exp;
  
  public BadBinaryOpValueExpException(ValueExp paramValueExp) { this.exp = paramValueExp; }
  
  public ValueExp getExp() { return this.exp; }
  
  public String toString() { return "BadBinaryOpValueExpException: " + this.exp; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\BadBinaryOpValueExpException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */