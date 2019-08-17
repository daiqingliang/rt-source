package java.util;

public class IllegalFormatConversionException extends IllegalFormatException {
  private static final long serialVersionUID = 17000126L;
  
  private char c;
  
  private Class<?> arg;
  
  public IllegalFormatConversionException(char paramChar, Class<?> paramClass) {
    if (paramClass == null)
      throw new NullPointerException(); 
    this.c = paramChar;
    this.arg = paramClass;
  }
  
  public char getConversion() { return this.c; }
  
  public Class<?> getArgumentClass() { return this.arg; }
  
  public String getMessage() { return String.format("%c != %s", new Object[] { Character.valueOf(this.c), this.arg.getName() }); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\IllegalFormatConversionException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */