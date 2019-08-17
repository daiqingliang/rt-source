package com.sun.corba.se.spi.orb;

import com.sun.corba.se.impl.encoding.CodeSetComponentInfo;
import com.sun.corba.se.impl.legacy.connection.USLPort;
import com.sun.corba.se.pept.transport.Acceptor;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.legacy.connection.ORBSocketFactory;
import com.sun.corba.se.spi.transport.CorbaContactInfoListFactory;
import com.sun.corba.se.spi.transport.IIOPPrimaryToContactInfo;
import com.sun.corba.se.spi.transport.IORToSocketInfo;
import com.sun.corba.se.spi.transport.ORBSocketFactory;
import com.sun.corba.se.spi.transport.ReadTimeouts;
import org.omg.PortableInterceptor.ORBInitializer;

public interface ORBData {
  String getORBInitialHost();
  
  int getORBInitialPort();
  
  String getORBServerHost();
  
  int getORBServerPort();
  
  String getListenOnAllInterfaces();
  
  ORBSocketFactory getLegacySocketFactory();
  
  ORBSocketFactory getSocketFactory();
  
  USLPort[] getUserSpecifiedListenPorts();
  
  IORToSocketInfo getIORToSocketInfo();
  
  IIOPPrimaryToContactInfo getIIOPPrimaryToContactInfo();
  
  String getORBId();
  
  boolean getORBServerIdPropertySpecified();
  
  boolean isLocalOptimizationAllowed();
  
  GIOPVersion getGIOPVersion();
  
  int getHighWaterMark();
  
  int getLowWaterMark();
  
  int getNumberToReclaim();
  
  int getGIOPFragmentSize();
  
  int getGIOPBufferSize();
  
  int getGIOPBuffMgrStrategy(GIOPVersion paramGIOPVersion);
  
  short getGIOPTargetAddressPreference();
  
  short getGIOPAddressDisposition();
  
  boolean useByteOrderMarkers();
  
  boolean useByteOrderMarkersInEncapsulations();
  
  boolean alwaysSendCodeSetServiceContext();
  
  boolean getPersistentPortInitialized();
  
  int getPersistentServerPort();
  
  boolean getPersistentServerIdInitialized();
  
  int getPersistentServerId();
  
  boolean getServerIsORBActivated();
  
  Class getBadServerIdHandler();
  
  CodeSetComponentInfo getCodeSetComponentInfo();
  
  ORBInitializer[] getORBInitializers();
  
  StringPair[] getORBInitialReferences();
  
  String getORBDefaultInitialReference();
  
  String[] getORBDebugFlags();
  
  Acceptor[] getAcceptors();
  
  CorbaContactInfoListFactory getCorbaContactInfoListFactory();
  
  String acceptorSocketType();
  
  boolean acceptorSocketUseSelectThreadToWait();
  
  boolean acceptorSocketUseWorkerThreadForEvent();
  
  String connectionSocketType();
  
  boolean connectionSocketUseSelectThreadToWait();
  
  boolean connectionSocketUseWorkerThreadForEvent();
  
  ReadTimeouts getTransportTCPReadTimeouts();
  
  boolean disableDirectByteBufferUse();
  
  boolean isJavaSerializationEnabled();
  
  boolean useRepId();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\orb\ORBData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */