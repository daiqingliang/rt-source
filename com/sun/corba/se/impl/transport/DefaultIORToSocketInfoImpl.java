package com.sun.corba.se.impl.transport;

import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.iiop.AlternateIIOPAddressComponent;
import com.sun.corba.se.spi.ior.iiop.IIOPAddress;
import com.sun.corba.se.spi.ior.iiop.IIOPProfileTemplate;
import com.sun.corba.se.spi.transport.IORToSocketInfo;
import com.sun.corba.se.spi.transport.SocketInfo;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DefaultIORToSocketInfoImpl implements IORToSocketInfo {
  public List getSocketInfo(IOR paramIOR) {
    ArrayList arrayList = new ArrayList();
    IIOPProfileTemplate iIOPProfileTemplate = (IIOPProfileTemplate)paramIOR.getProfile().getTaggedProfileTemplate();
    IIOPAddress iIOPAddress = iIOPProfileTemplate.getPrimaryAddress();
    String str = iIOPAddress.getHost().toLowerCase();
    int i = iIOPAddress.getPort();
    SocketInfo socketInfo = createSocketInfo(str, i);
    arrayList.add(socketInfo);
    Iterator iterator = iIOPProfileTemplate.iteratorById(3);
    while (iterator.hasNext()) {
      AlternateIIOPAddressComponent alternateIIOPAddressComponent = (AlternateIIOPAddressComponent)iterator.next();
      str = alternateIIOPAddressComponent.getAddress().getHost().toLowerCase();
      i = alternateIIOPAddressComponent.getAddress().getPort();
      socketInfo = createSocketInfo(str, i);
      arrayList.add(socketInfo);
    } 
    return arrayList;
  }
  
  private SocketInfo createSocketInfo(final String hostname, final int port) { return new SocketInfo() {
        public String getType() { return "IIOP_CLEAR_TEXT"; }
        
        public String getHost() { return hostname; }
        
        public int getPort() { return port; }
      }; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\transport\DefaultIORToSocketInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */