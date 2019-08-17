package sun.invoke;

import java.lang.invoke.MethodHandle;

public interface WrapperInstance {
  MethodHandle getWrapperInstanceTarget();
  
  Class<?> getWrapperInstanceType();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\invoke\WrapperInstance.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */