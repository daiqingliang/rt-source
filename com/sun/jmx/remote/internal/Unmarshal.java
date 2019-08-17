package com.sun.jmx.remote.internal;

import java.io.IOException;
import java.rmi.MarshalledObject;

public interface Unmarshal {
  Object get(MarshalledObject<?> paramMarshalledObject) throws IOException, ClassNotFoundException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\remote\internal\Unmarshal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */