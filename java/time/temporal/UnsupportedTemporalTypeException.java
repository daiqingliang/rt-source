package java.time.temporal;

import java.time.DateTimeException;

public class UnsupportedTemporalTypeException extends DateTimeException {
  private static final long serialVersionUID = -6158898438688206006L;
  
  public UnsupportedTemporalTypeException(String paramString) { super(paramString); }
  
  public UnsupportedTemporalTypeException(String paramString, Throwable paramThrowable) { super(paramString, paramThrowable); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\temporal\UnsupportedTemporalTypeException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */