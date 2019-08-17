package sun.reflect.annotation;

import java.io.Serializable;

public abstract class ExceptionProxy implements Serializable {
  protected abstract RuntimeException generateException();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\annotation\ExceptionProxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */