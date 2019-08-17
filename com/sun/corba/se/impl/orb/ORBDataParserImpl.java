package com.sun.corba.se.impl.orb;

import com.sun.corba.se.impl.encoding.CodeSetComponentInfo;
import com.sun.corba.se.impl.legacy.connection.USLPort;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.pept.transport.Acceptor;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.legacy.connection.ORBSocketFactory;
import com.sun.corba.se.spi.orb.DataCollector;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBData;
import com.sun.corba.se.spi.orb.ParserImplTableBase;
import com.sun.corba.se.spi.orb.StringPair;
import com.sun.corba.se.spi.transport.CorbaContactInfoListFactory;
import com.sun.corba.se.spi.transport.IIOPPrimaryToContactInfo;
import com.sun.corba.se.spi.transport.IORToSocketInfo;
import com.sun.corba.se.spi.transport.ORBSocketFactory;
import com.sun.corba.se.spi.transport.ReadTimeouts;
import java.net.URL;
import org.omg.CORBA.CompletionStatus;
import org.omg.PortableInterceptor.ORBInitializer;

public class ORBDataParserImpl extends ParserImplTableBase implements ORBData {
  private ORB orb;
  
  private ORBUtilSystemException wrapper;
  
  private String ORBInitialHost;
  
  private int ORBInitialPort;
  
  private String ORBServerHost;
  
  private int ORBServerPort;
  
  private String listenOnAllInterfaces;
  
  private ORBSocketFactory legacySocketFactory;
  
  private ORBSocketFactory socketFactory;
  
  private USLPort[] userSpecifiedListenPorts;
  
  private IORToSocketInfo iorToSocketInfo;
  
  private IIOPPrimaryToContactInfo iiopPrimaryToContactInfo;
  
  private String orbId;
  
  private boolean orbServerIdPropertySpecified;
  
  private URL servicesURL;
  
  private String propertyInitRef;
  
  private boolean allowLocalOptimization;
  
  private GIOPVersion giopVersion;
  
  private int highWaterMark;
  
  private int lowWaterMark;
  
  private int numberToReclaim;
  
  private int giopFragmentSize;
  
  private int giopBufferSize;
  
  private int giop11BuffMgr;
  
  private int giop12BuffMgr;
  
  private short giopTargetAddressPreference;
  
  private short giopAddressDisposition;
  
  private boolean useByteOrderMarkers;
  
  private boolean useByteOrderMarkersInEncaps;
  
  private boolean alwaysSendCodeSetCtx;
  
  private boolean persistentPortInitialized;
  
  private int persistentServerPort;
  
  private boolean persistentServerIdInitialized;
  
  private int persistentServerId;
  
  private boolean serverIsORBActivated;
  
  private Class badServerIdHandlerClass;
  
  private CodeSetComponentInfo.CodeSetComponent charData;
  
  private CodeSetComponentInfo.CodeSetComponent wcharData;
  
  private ORBInitializer[] orbInitializers;
  
  private StringPair[] orbInitialReferences;
  
  private String defaultInitRef;
  
  private String[] debugFlags;
  
  private Acceptor[] acceptors;
  
  private CorbaContactInfoListFactory corbaContactInfoListFactory;
  
  private String acceptorSocketType;
  
  private boolean acceptorSocketUseSelectThreadToWait;
  
  private boolean acceptorSocketUseWorkerThreadForEvent;
  
  private String connectionSocketType;
  
  private boolean connectionSocketUseSelectThreadToWait;
  
  private boolean connectionSocketUseWorkerThreadForEvent;
  
  private ReadTimeouts readTimeouts;
  
  private boolean disableDirectByteBufferUse;
  
  private boolean enableJavaSerialization;
  
  private boolean useRepId;
  
  private CodeSetComponentInfo codesets;
  
  public String getORBInitialHost() { return this.ORBInitialHost; }
  
  public int getORBInitialPort() { return this.ORBInitialPort; }
  
  public String getORBServerHost() { return this.ORBServerHost; }
  
  public String getListenOnAllInterfaces() { return this.listenOnAllInterfaces; }
  
  public int getORBServerPort() { return this.ORBServerPort; }
  
  public ORBSocketFactory getLegacySocketFactory() { return this.legacySocketFactory; }
  
  public ORBSocketFactory getSocketFactory() { return this.socketFactory; }
  
  public USLPort[] getUserSpecifiedListenPorts() { return this.userSpecifiedListenPorts; }
  
  public IORToSocketInfo getIORToSocketInfo() { return this.iorToSocketInfo; }
  
  public IIOPPrimaryToContactInfo getIIOPPrimaryToContactInfo() { return this.iiopPrimaryToContactInfo; }
  
  public String getORBId() { return this.orbId; }
  
  public boolean getORBServerIdPropertySpecified() { return this.orbServerIdPropertySpecified; }
  
  public boolean isLocalOptimizationAllowed() { return this.allowLocalOptimization; }
  
  public GIOPVersion getGIOPVersion() { return this.giopVersion; }
  
  public int getHighWaterMark() { return this.highWaterMark; }
  
  public int getLowWaterMark() { return this.lowWaterMark; }
  
  public int getNumberToReclaim() { return this.numberToReclaim; }
  
  public int getGIOPFragmentSize() { return this.giopFragmentSize; }
  
  public int getGIOPBufferSize() { return this.giopBufferSize; }
  
  public int getGIOPBuffMgrStrategy(GIOPVersion paramGIOPVersion) {
    if (paramGIOPVersion != null) {
      if (paramGIOPVersion.equals(GIOPVersion.V1_0))
        return 0; 
      if (paramGIOPVersion.equals(GIOPVersion.V1_1))
        return this.giop11BuffMgr; 
      if (paramGIOPVersion.equals(GIOPVersion.V1_2))
        return this.giop12BuffMgr; 
    } 
    return 0;
  }
  
  public short getGIOPTargetAddressPreference() { return this.giopTargetAddressPreference; }
  
  public short getGIOPAddressDisposition() { return this.giopAddressDisposition; }
  
  public boolean useByteOrderMarkers() { return this.useByteOrderMarkers; }
  
  public boolean useByteOrderMarkersInEncapsulations() { return this.useByteOrderMarkersInEncaps; }
  
  public boolean alwaysSendCodeSetServiceContext() { return this.alwaysSendCodeSetCtx; }
  
  public boolean getPersistentPortInitialized() { return this.persistentPortInitialized; }
  
  public int getPersistentServerPort() {
    if (this.persistentPortInitialized)
      return this.persistentServerPort; 
    throw this.wrapper.persistentServerportNotSet(CompletionStatus.COMPLETED_MAYBE);
  }
  
  public boolean getPersistentServerIdInitialized() { return this.persistentServerIdInitialized; }
  
  public int getPersistentServerId() {
    if (this.persistentServerIdInitialized)
      return this.persistentServerId; 
    throw this.wrapper.persistentServeridNotSet(CompletionStatus.COMPLETED_MAYBE);
  }
  
  public boolean getServerIsORBActivated() { return this.serverIsORBActivated; }
  
  public Class getBadServerIdHandler() { return this.badServerIdHandlerClass; }
  
  public CodeSetComponentInfo getCodeSetComponentInfo() { return this.codesets; }
  
  public ORBInitializer[] getORBInitializers() { return this.orbInitializers; }
  
  public StringPair[] getORBInitialReferences() { return this.orbInitialReferences; }
  
  public String getORBDefaultInitialReference() { return this.defaultInitRef; }
  
  public String[] getORBDebugFlags() { return this.debugFlags; }
  
  public Acceptor[] getAcceptors() { return this.acceptors; }
  
  public CorbaContactInfoListFactory getCorbaContactInfoListFactory() { return this.corbaContactInfoListFactory; }
  
  public String acceptorSocketType() { return this.acceptorSocketType; }
  
  public boolean acceptorSocketUseSelectThreadToWait() { return this.acceptorSocketUseSelectThreadToWait; }
  
  public boolean acceptorSocketUseWorkerThreadForEvent() { return this.acceptorSocketUseWorkerThreadForEvent; }
  
  public String connectionSocketType() { return this.connectionSocketType; }
  
  public boolean connectionSocketUseSelectThreadToWait() { return this.connectionSocketUseSelectThreadToWait; }
  
  public boolean connectionSocketUseWorkerThreadForEvent() { return this.connectionSocketUseWorkerThreadForEvent; }
  
  public boolean isJavaSerializationEnabled() { return this.enableJavaSerialization; }
  
  public ReadTimeouts getTransportTCPReadTimeouts() { return this.readTimeouts; }
  
  public boolean disableDirectByteBufferUse() { return this.disableDirectByteBufferUse; }
  
  public boolean useRepId() { return this.useRepId; }
  
  public ORBDataParserImpl(ORB paramORB, DataCollector paramDataCollector) {
    super(ParserTable.get().getParserData());
    this.orb = paramORB;
    this.wrapper = ORBUtilSystemException.get(paramORB, "orb.lifecycle");
    init(paramDataCollector);
    complete();
  }
  
  public void complete() { this.codesets = new CodeSetComponentInfo(this.charData, this.wcharData); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\orb\ORBDataParserImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */