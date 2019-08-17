package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

public final class ChildLoader {
  public final Loader loader;
  
  public final Receiver receiver;
  
  public ChildLoader(Loader paramLoader, Receiver paramReceiver) {
    assert paramLoader != null;
    this.loader = paramLoader;
    this.receiver = paramReceiver;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtim\\unmarshaller\ChildLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */