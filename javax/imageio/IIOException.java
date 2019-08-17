package javax.imageio;

import java.io.IOException;

public class IIOException extends IOException {
  public IIOException(String paramString) { super(paramString); }
  
  public IIOException(String paramString, Throwable paramThrowable) {
    super(paramString);
    initCause(paramThrowable);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\imageio\IIOException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */