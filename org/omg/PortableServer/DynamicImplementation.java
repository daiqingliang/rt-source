package org.omg.PortableServer;

import org.omg.CORBA.ServerRequest;

public abstract class DynamicImplementation extends Servant {
  public abstract void invoke(ServerRequest paramServerRequest);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\PortableServer\DynamicImplementation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */