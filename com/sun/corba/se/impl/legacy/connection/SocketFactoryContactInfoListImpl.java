package com.sun.corba.se.impl.legacy.connection;

import com.sun.corba.se.impl.transport.CorbaContactInfoListImpl;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.orb.ORB;
import java.util.Iterator;

public class SocketFactoryContactInfoListImpl extends CorbaContactInfoListImpl {
  public SocketFactoryContactInfoListImpl(ORB paramORB) { super(paramORB); }
  
  public SocketFactoryContactInfoListImpl(ORB paramORB, IOR paramIOR) { super(paramORB, paramIOR); }
  
  public Iterator iterator() { return new SocketFactoryContactInfoListIteratorImpl(this.orb, this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\legacy\connection\SocketFactoryContactInfoListImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */