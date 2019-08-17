package com.sun.corba.se.impl.orb;

import com.sun.corba.se.impl.encoding.CodeSetComponentInfo;
import com.sun.corba.se.impl.legacy.connection.USLPort;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.oa.poa.BadServerIdHandler;
import com.sun.corba.se.impl.transport.DefaultIORToSocketInfoImpl;
import com.sun.corba.se.impl.transport.DefaultSocketFactoryImpl;
import com.sun.corba.se.pept.broker.Broker;
import com.sun.corba.se.pept.encoding.InputObject;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.pept.transport.Acceptor;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.pept.transport.ContactInfo;
import com.sun.corba.se.pept.transport.EventHandler;
import com.sun.corba.se.pept.transport.InboundConnectionCache;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.legacy.connection.ORBSocketFactory;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.Operation;
import com.sun.corba.se.spi.orb.OperationFactory;
import com.sun.corba.se.spi.orb.ParserData;
import com.sun.corba.se.spi.orb.ParserDataFactory;
import com.sun.corba.se.spi.orb.StringPair;
import com.sun.corba.se.spi.transport.CorbaContactInfoList;
import com.sun.corba.se.spi.transport.CorbaContactInfoListFactory;
import com.sun.corba.se.spi.transport.IIOPPrimaryToContactInfo;
import com.sun.corba.se.spi.transport.IORToSocketInfo;
import com.sun.corba.se.spi.transport.ORBSocketFactory;
import com.sun.corba.se.spi.transport.ReadTimeouts;
import com.sun.corba.se.spi.transport.SocketInfo;
import com.sun.corba.se.spi.transport.TransportDefault;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.ORB;
import org.omg.PortableInterceptor.ORBInitInfo;
import org.omg.PortableInterceptor.ORBInitializer;
import sun.corba.SharedSecrets;

public class ParserTable {
  private static String MY_CLASS_NAME = ParserTable.class.getName();
  
  private static ParserTable myInstance = new ParserTable();
  
  private ORBUtilSystemException wrapper = ORBUtilSystemException.get("orb.lifecycle");
  
  private ParserData[] parserData;
  
  public static ParserTable get() { return myInstance; }
  
  public ParserData[] getParserData() {
    ParserData[] arrayOfParserData = new ParserData[this.parserData.length];
    System.arraycopy(this.parserData, 0, arrayOfParserData, 0, this.parserData.length);
    return arrayOfParserData;
  }
  
  private ParserTable() {
    String str1 = "65537,65801,65568";
    String[] arrayOfString = { "subcontract", "poa", "transport" };
    USLPort[] arrayOfUSLPort = { new USLPort("FOO", 2701), new USLPort("BAR", 3333) };
    ReadTimeouts readTimeouts = TransportDefault.makeReadTimeoutsFactory().create(100, 3000, 300, 20);
    ORBInitializer[] arrayOfORBInitializer = { null, new TestORBInitializer1(), new TestORBInitializer2() };
    StringPair[] arrayOfStringPair1 = { new StringPair("foo.bar.blech.NonExistent", "dummy"), new StringPair(MY_CLASS_NAME + "$TestORBInitializer1", "dummy"), new StringPair(MY_CLASS_NAME + "$TestORBInitializer2", "dummy") };
    Acceptor[] arrayOfAcceptor = { new TestAcceptor2(), new TestAcceptor1(), null };
    StringPair[] arrayOfStringPair2 = { new StringPair("foo.bar.blech.NonExistent", "dummy"), new StringPair(MY_CLASS_NAME + "$TestAcceptor1", "dummy"), new StringPair(MY_CLASS_NAME + "$TestAcceptor2", "dummy") };
    StringPair[] arrayOfStringPair3 = { new StringPair("Foo", "ior:930492049394"), new StringPair("Bar", "ior:3453465785633576") };
    URL uRL = null;
    String str2 = "corbaloc::camelot/NameService";
    try {
      uRL = new URL(str2);
    } catch (Exception exception) {}
    ParserData[] arrayOfParserData = { 
        ParserDataFactory.make("com.sun.CORBA.ORBDebug", OperationFactory.listAction(",", OperationFactory.stringAction()), "debugFlags", new String[0], arrayOfString, "subcontract,poa,transport"), ParserDataFactory.make("org.omg.CORBA.ORBInitialHost", OperationFactory.stringAction(), "ORBInitialHost", "", "Foo", "Foo"), ParserDataFactory.make("org.omg.CORBA.ORBInitialPort", OperationFactory.integerAction(), "ORBInitialPort", new Integer(900), new Integer(27314), "27314"), ParserDataFactory.make("com.sun.CORBA.ORBServerHost", OperationFactory.stringAction(), "ORBServerHost", "", "camelot", "camelot"), ParserDataFactory.make("com.sun.CORBA.ORBServerPort", OperationFactory.integerAction(), "ORBServerPort", new Integer(0), new Integer(38143), "38143"), ParserDataFactory.make("com.sun.CORBA.INTERNAL USE ONLY: listen on all interfaces", OperationFactory.stringAction(), "listenOnAllInterfaces", "com.sun.CORBA.INTERNAL USE ONLY: listen on all interfaces", "foo", "foo"), ParserDataFactory.make("org.omg.CORBA.ORBId", OperationFactory.stringAction(), "orbId", "", "foo", "foo"), ParserDataFactory.make("com.sun.CORBA.ORBid", OperationFactory.stringAction(), "orbId", "", "foo", "foo"), ParserDataFactory.make("org.omg.CORBA.ORBServerId", OperationFactory.integerAction(), "persistentServerId", new Integer(-1), new Integer(1234), "1234"), ParserDataFactory.make("org.omg.CORBA.ORBServerId", OperationFactory.setFlagAction(), "persistentServerIdInitialized", Boolean.FALSE, Boolean.TRUE, "1234"), 
        ParserDataFactory.make("org.omg.CORBA.ORBServerId", OperationFactory.setFlagAction(), "orbServerIdPropertySpecified", Boolean.FALSE, Boolean.TRUE, "1234"), ParserDataFactory.make("com.sun.CORBA.connection.ORBHighWaterMark", OperationFactory.integerAction(), "highWaterMark", new Integer(240), new Integer(3745), "3745"), ParserDataFactory.make("com.sun.CORBA.connection.ORBLowWaterMark", OperationFactory.integerAction(), "lowWaterMark", new Integer(100), new Integer(12), "12"), ParserDataFactory.make("com.sun.CORBA.connection.ORBNumberToReclaim", OperationFactory.integerAction(), "numberToReclaim", new Integer(5), new Integer(231), "231"), ParserDataFactory.make("com.sun.CORBA.giop.ORBGIOPVersion", makeGVOperation(), "giopVersion", GIOPVersion.DEFAULT_VERSION, new GIOPVersion(2, 3), "2.3"), ParserDataFactory.make("com.sun.CORBA.giop.ORBFragmentSize", makeFSOperation(), "giopFragmentSize", new Integer(1024), new Integer(65536), "65536"), ParserDataFactory.make("com.sun.CORBA.giop.ORBBufferSize", OperationFactory.integerAction(), "giopBufferSize", new Integer(1024), new Integer(234000), "234000"), ParserDataFactory.make("com.sun.CORBA.giop.ORBGIOP11BuffMgr", makeBMGROperation(), "giop11BuffMgr", new Integer(0), new Integer(1), "CLCT"), ParserDataFactory.make("com.sun.CORBA.giop.ORBGIOP12BuffMgr", makeBMGROperation(), "giop12BuffMgr", new Integer(2), new Integer(0), "GROW"), ParserDataFactory.make("com.sun.CORBA.giop.ORBTargetAddressing", OperationFactory.compose(OperationFactory.integerRangeAction(0, 3), OperationFactory.convertIntegerToShort()), "giopTargetAddressPreference", new Short((short)3), new Short((short)2), "2"), 
        ParserDataFactory.make("com.sun.CORBA.giop.ORBTargetAddressing", makeADOperation(), "giopAddressDisposition", new Short((short)0), new Short((short)2), "2"), ParserDataFactory.make("com.sun.CORBA.codeset.AlwaysSendCodeSetCtx", OperationFactory.booleanAction(), "alwaysSendCodeSetCtx", Boolean.TRUE, Boolean.FALSE, "false"), ParserDataFactory.make("com.sun.CORBA.codeset.UseByteOrderMarkers", OperationFactory.booleanAction(), "useByteOrderMarkers", Boolean.valueOf(true), Boolean.FALSE, "false"), ParserDataFactory.make("com.sun.CORBA.codeset.UseByteOrderMarkersInEncaps", OperationFactory.booleanAction(), "useByteOrderMarkersInEncaps", Boolean.valueOf(false), Boolean.FALSE, "false"), ParserDataFactory.make("com.sun.CORBA.codeset.charsets", makeCSOperation(), "charData", CodeSetComponentInfo.JAVASOFT_DEFAULT_CODESETS.getCharComponent(), CodeSetComponentInfo.createFromString(str1), str1), ParserDataFactory.make("com.sun.CORBA.codeset.wcharsets", makeCSOperation(), "wcharData", CodeSetComponentInfo.JAVASOFT_DEFAULT_CODESETS.getWCharComponent(), CodeSetComponentInfo.createFromString(str1), str1), ParserDataFactory.make("com.sun.CORBA.ORBAllowLocalOptimization", OperationFactory.booleanAction(), "allowLocalOptimization", Boolean.FALSE, Boolean.TRUE, "true"), ParserDataFactory.make("com.sun.CORBA.legacy.connection.ORBSocketFactoryClass", makeLegacySocketFactoryOperation(), "legacySocketFactory", null, new TestLegacyORBSocketFactory(), MY_CLASS_NAME + "$TestLegacyORBSocketFactory"), ParserDataFactory.make("com.sun.CORBA.transport.ORBSocketFactoryClass", makeSocketFactoryOperation(), "socketFactory", new DefaultSocketFactoryImpl(), new TestORBSocketFactory(), MY_CLASS_NAME + "$TestORBSocketFactory"), ParserDataFactory.make("com.sun.CORBA.transport.ORBListenSocket", makeUSLOperation(), "userSpecifiedListenPorts", new USLPort[0], arrayOfUSLPort, "FOO:2701,BAR:3333"), 
        ParserDataFactory.make("com.sun.CORBA.transport.ORBIORToSocketInfoClass", makeIORToSocketInfoOperation(), "iorToSocketInfo", new DefaultIORToSocketInfoImpl(), new TestIORToSocketInfo(), MY_CLASS_NAME + "$TestIORToSocketInfo"), ParserDataFactory.make("com.sun.CORBA.transport.ORBIIOPPrimaryToContactInfoClass", makeIIOPPrimaryToContactInfoOperation(), "iiopPrimaryToContactInfo", null, new TestIIOPPrimaryToContactInfo(), MY_CLASS_NAME + "$TestIIOPPrimaryToContactInfo"), ParserDataFactory.make("com.sun.CORBA.transport.ORBContactInfoList", makeContactInfoListFactoryOperation(), "corbaContactInfoListFactory", null, new TestContactInfoListFactory(), MY_CLASS_NAME + "$TestContactInfoListFactory"), ParserDataFactory.make("com.sun.CORBA.POA.ORBPersistentServerPort", OperationFactory.integerAction(), "persistentServerPort", new Integer(0), new Integer(2743), "2743"), ParserDataFactory.make("com.sun.CORBA.POA.ORBPersistentServerPort", OperationFactory.setFlagAction(), "persistentPortInitialized", Boolean.FALSE, Boolean.TRUE, "2743"), ParserDataFactory.make("com.sun.CORBA.POA.ORBServerId", OperationFactory.integerAction(), "persistentServerId", new Integer(0), new Integer(294), "294"), ParserDataFactory.make("com.sun.CORBA.POA.ORBServerId", OperationFactory.setFlagAction(), "persistentServerIdInitialized", Boolean.FALSE, Boolean.TRUE, "294"), ParserDataFactory.make("com.sun.CORBA.POA.ORBServerId", OperationFactory.setFlagAction(), "orbServerIdPropertySpecified", Boolean.FALSE, Boolean.TRUE, "294"), ParserDataFactory.make("com.sun.CORBA.POA.ORBActivated", OperationFactory.booleanAction(), "serverIsORBActivated", Boolean.FALSE, Boolean.TRUE, "true"), ParserDataFactory.make("com.sun.CORBA.POA.ORBBadServerIdHandlerClass", OperationFactory.classAction(), "badServerIdHandlerClass", null, TestBadServerIdHandler.class, MY_CLASS_NAME + "$TestBadServerIdHandler"), 
        ParserDataFactory.make("org.omg.PortableInterceptor.ORBInitializerClass.", makeROIOperation(), "orbInitializers", new ORBInitializer[0], arrayOfORBInitializer, arrayOfStringPair1, ORBInitializer.class), ParserDataFactory.make("com.sun.CORBA.transport.ORBAcceptor", makeAcceptorInstantiationOperation(), "acceptors", new Acceptor[0], arrayOfAcceptor, arrayOfStringPair2, Acceptor.class), ParserDataFactory.make("com.sun.CORBA.transport.ORBAcceptorSocketType", OperationFactory.stringAction(), "acceptorSocketType", "SocketChannel", "foo", "foo"), ParserDataFactory.make("com.sun.CORBA.transport.ORBUseNIOSelectToWait", OperationFactory.booleanAction(), "acceptorSocketUseSelectThreadToWait", Boolean.TRUE, Boolean.TRUE, "true"), ParserDataFactory.make("com.sun.CORBA.transport.ORBAcceptorSocketUseWorkerThreadForEvent", OperationFactory.booleanAction(), "acceptorSocketUseWorkerThreadForEvent", Boolean.TRUE, Boolean.TRUE, "true"), ParserDataFactory.make("com.sun.CORBA.transport.ORBConnectionSocketType", OperationFactory.stringAction(), "connectionSocketType", "SocketChannel", "foo", "foo"), ParserDataFactory.make("com.sun.CORBA.transport.ORBUseNIOSelectToWait", OperationFactory.booleanAction(), "connectionSocketUseSelectThreadToWait", Boolean.TRUE, Boolean.TRUE, "true"), ParserDataFactory.make("com.sun.CORBA.transport.ORBConnectionSocketUseWorkerThreadForEvent", OperationFactory.booleanAction(), "connectionSocketUseWorkerThreadForEvent", Boolean.TRUE, Boolean.TRUE, "true"), ParserDataFactory.make("com.sun.CORBA.transport.ORBDisableDirectByteBufferUse", OperationFactory.booleanAction(), "disableDirectByteBufferUse", Boolean.FALSE, Boolean.TRUE, "true"), ParserDataFactory.make("com.sun.CORBA.transport.ORBTCPReadTimeouts", makeTTCPRTOperation(), "readTimeouts", TransportDefault.makeReadTimeoutsFactory().create(100, 3000, 300, 20), readTimeouts, "100:3000:300:20"), 
        ParserDataFactory.make("com.sun.CORBA.encoding.ORBEnableJavaSerialization", OperationFactory.booleanAction(), "enableJavaSerialization", Boolean.FALSE, Boolean.FALSE, "false"), ParserDataFactory.make("com.sun.CORBA.ORBUseRepId", OperationFactory.booleanAction(), "useRepId", Boolean.TRUE, Boolean.TRUE, "true"), ParserDataFactory.make("org.omg.CORBA.ORBInitRef", OperationFactory.identityAction(), "orbInitialReferences", new StringPair[0], arrayOfStringPair3, arrayOfStringPair3, StringPair.class) };
    this.parserData = arrayOfParserData;
  }
  
  private Operation makeTTCPRTOperation() {
    Operation[] arrayOfOperation = { OperationFactory.integerAction(), OperationFactory.integerAction(), OperationFactory.integerAction(), OperationFactory.integerAction() };
    Operation operation1 = OperationFactory.sequenceAction(":", arrayOfOperation);
    Operation operation2 = new Operation() {
        public Object operate(Object param1Object) {
          Object[] arrayOfObject = (Object[])param1Object;
          Integer integer1 = (Integer)arrayOfObject[0];
          Integer integer2 = (Integer)arrayOfObject[1];
          Integer integer3 = (Integer)arrayOfObject[2];
          Integer integer4 = (Integer)arrayOfObject[3];
          return TransportDefault.makeReadTimeoutsFactory().create(integer1.intValue(), integer2.intValue(), integer3.intValue(), integer4.intValue());
        }
      };
    return OperationFactory.compose(operation1, operation2);
  }
  
  private Operation makeUSLOperation() {
    Operation[] arrayOfOperation = { OperationFactory.stringAction(), OperationFactory.integerAction() };
    Operation operation1 = OperationFactory.sequenceAction(":", arrayOfOperation);
    Operation operation2 = new Operation() {
        public Object operate(Object param1Object) {
          Object[] arrayOfObject = (Object[])param1Object;
          String str = (String)arrayOfObject[0];
          Integer integer = (Integer)arrayOfObject[1];
          return new USLPort(str, integer.intValue());
        }
      };
    Operation operation3 = OperationFactory.compose(operation1, operation2);
    return OperationFactory.listAction(",", operation3);
  }
  
  private Operation makeMapOperation(final Map map) { return new Operation() {
        public Object operate(Object param1Object) { return map.get(param1Object); }
      }; }
  
  private Operation makeBMGROperation() {
    HashMap hashMap = new HashMap();
    hashMap.put("GROW", new Integer(0));
    hashMap.put("CLCT", new Integer(1));
    hashMap.put("STRM", new Integer(2));
    return makeMapOperation(hashMap);
  }
  
  private Operation makeLegacySocketFactoryOperation() { return new Operation() {
        public Object operate(Object param1Object) {
          String str = (String)param1Object;
          try {
            Class clazz = SharedSecrets.getJavaCorbaAccess().loadClass(str);
            if (ORBSocketFactory.class.isAssignableFrom(clazz))
              return clazz.newInstance(); 
            throw ParserTable.this.wrapper.illegalSocketFactoryType(clazz.toString());
          } catch (Exception exception) {
            throw ParserTable.this.wrapper.badCustomSocketFactory(exception, str);
          } 
        }
      }; }
  
  private Operation makeSocketFactoryOperation() { return new Operation() {
        public Object operate(Object param1Object) {
          String str = (String)param1Object;
          try {
            Class clazz = SharedSecrets.getJavaCorbaAccess().loadClass(str);
            if (ORBSocketFactory.class.isAssignableFrom(clazz))
              return clazz.newInstance(); 
            throw ParserTable.this.wrapper.illegalSocketFactoryType(clazz.toString());
          } catch (Exception exception) {
            throw ParserTable.this.wrapper.badCustomSocketFactory(exception, str);
          } 
        }
      }; }
  
  private Operation makeIORToSocketInfoOperation() { return new Operation() {
        public Object operate(Object param1Object) {
          String str = (String)param1Object;
          try {
            Class clazz = SharedSecrets.getJavaCorbaAccess().loadClass(str);
            if (IORToSocketInfo.class.isAssignableFrom(clazz))
              return clazz.newInstance(); 
            throw ParserTable.this.wrapper.illegalIorToSocketInfoType(clazz.toString());
          } catch (Exception exception) {
            throw ParserTable.this.wrapper.badCustomIorToSocketInfo(exception, str);
          } 
        }
      }; }
  
  private Operation makeIIOPPrimaryToContactInfoOperation() { return new Operation() {
        public Object operate(Object param1Object) {
          String str = (String)param1Object;
          try {
            Class clazz = SharedSecrets.getJavaCorbaAccess().loadClass(str);
            if (IIOPPrimaryToContactInfo.class.isAssignableFrom(clazz))
              return clazz.newInstance(); 
            throw ParserTable.this.wrapper.illegalIiopPrimaryToContactInfoType(clazz.toString());
          } catch (Exception exception) {
            throw ParserTable.this.wrapper.badCustomIiopPrimaryToContactInfo(exception, str);
          } 
        }
      }; }
  
  private Operation makeContactInfoListFactoryOperation() { return new Operation() {
        public Object operate(Object param1Object) {
          String str = (String)param1Object;
          try {
            Class clazz = SharedSecrets.getJavaCorbaAccess().loadClass(str);
            if (CorbaContactInfoListFactory.class.isAssignableFrom(clazz))
              return clazz.newInstance(); 
            throw ParserTable.this.wrapper.illegalContactInfoListFactoryType(clazz.toString());
          } catch (Exception exception) {
            throw ParserTable.this.wrapper.badContactInfoListFactory(exception, str);
          } 
        }
      }; }
  
  private Operation makeCSOperation() { return new Operation() {
        public Object operate(Object param1Object) {
          String str = (String)param1Object;
          return CodeSetComponentInfo.createFromString(str);
        }
      }; }
  
  private Operation makeADOperation() {
    Operation operation1 = new Operation() {
        private Integer[] map = { new Integer(0), new Integer(1), new Integer(2), new Integer(0) };
        
        public Object operate(Object param1Object) {
          int i = ((Integer)param1Object).intValue();
          return this.map[i];
        }
      };
    Operation operation2 = OperationFactory.integerRangeAction(0, 3);
    Operation operation3 = OperationFactory.compose(operation2, operation1);
    return OperationFactory.compose(operation3, OperationFactory.convertIntegerToShort());
  }
  
  private Operation makeFSOperation() {
    Operation operation = new Operation() {
        public Object operate(Object param1Object) {
          int i = ((Integer)param1Object).intValue();
          if (i < 32)
            throw ParserTable.this.wrapper.fragmentSizeMinimum(new Integer(i), new Integer(32)); 
          if (i % 8 != 0)
            throw ParserTable.this.wrapper.fragmentSizeDiv(new Integer(i), new Integer(8)); 
          return param1Object;
        }
      };
    return OperationFactory.compose(OperationFactory.integerAction(), operation);
  }
  
  private Operation makeGVOperation() {
    Operation operation1 = OperationFactory.listAction(".", OperationFactory.integerAction());
    Operation operation2 = new Operation() {
        public Object operate(Object param1Object) {
          Object[] arrayOfObject = (Object[])param1Object;
          int i = ((Integer)arrayOfObject[0]).intValue();
          int j = ((Integer)arrayOfObject[1]).intValue();
          return new GIOPVersion(i, j);
        }
      };
    return OperationFactory.compose(operation1, operation2);
  }
  
  private Operation makeROIOperation() {
    Operation operation1 = OperationFactory.classAction();
    Operation operation2 = OperationFactory.suffixAction();
    Operation operation3 = OperationFactory.compose(operation2, operation1);
    Operation operation4 = OperationFactory.maskErrorAction(operation3);
    Operation operation5 = new Operation() {
        public Object operate(Object param1Object) {
          final Class initClass = (Class)param1Object;
          if (clazz == null)
            return null; 
          if (ORBInitializer.class.isAssignableFrom(clazz)) {
            ORBInitializer oRBInitializer = null;
            try {
              oRBInitializer = (ORBInitializer)AccessController.doPrivileged(new PrivilegedExceptionAction() {
                    public Object run() throws InstantiationException, IllegalAccessException { return initClass.newInstance(); }
                  });
            } catch (PrivilegedActionException privilegedActionException) {
              throw ParserTable.this.wrapper.orbInitializerFailure(privilegedActionException.getException(), clazz.getName());
            } catch (Exception exception) {
              throw ParserTable.this.wrapper.orbInitializerFailure(exception, clazz.getName());
            } 
            return oRBInitializer;
          } 
          throw ParserTable.this.wrapper.orbInitializerType(clazz.getName());
        }
      };
    return OperationFactory.compose(operation4, operation5);
  }
  
  private Operation makeAcceptorInstantiationOperation() {
    Operation operation1 = OperationFactory.classAction();
    Operation operation2 = OperationFactory.suffixAction();
    Operation operation3 = OperationFactory.compose(operation2, operation1);
    Operation operation4 = OperationFactory.maskErrorAction(operation3);
    Operation operation5 = new Operation() {
        public Object operate(Object param1Object) {
          final Class initClass = (Class)param1Object;
          if (clazz == null)
            return null; 
          if (Acceptor.class.isAssignableFrom(clazz)) {
            Acceptor acceptor = null;
            try {
              acceptor = (Acceptor)AccessController.doPrivileged(new PrivilegedExceptionAction() {
                    public Object run() throws InstantiationException, IllegalAccessException { return initClass.newInstance(); }
                  });
            } catch (PrivilegedActionException privilegedActionException) {
              throw ParserTable.this.wrapper.acceptorInstantiationFailure(privilegedActionException.getException(), clazz.getName());
            } catch (Exception exception) {
              throw ParserTable.this.wrapper.acceptorInstantiationFailure(exception, clazz.getName());
            } 
            return acceptor;
          } 
          throw ParserTable.this.wrapper.acceptorInstantiationTypeFailure(clazz.getName());
        }
      };
    return OperationFactory.compose(operation4, operation5);
  }
  
  private Operation makeInitRefOperation() { return new Operation() {
        public Object operate(Object param1Object) {
          String[] arrayOfString = (String[])param1Object;
          if (arrayOfString.length != 2)
            throw ParserTable.this.wrapper.orbInitialreferenceSyntax(); 
          return arrayOfString[0] + "=" + arrayOfString[1];
        }
      }; }
  
  public static final class TestAcceptor1 implements Acceptor {
    public boolean equals(Object param1Object) { return param1Object instanceof TestAcceptor1; }
    
    public int hashCode() { return 1; }
    
    public boolean initialize() { return true; }
    
    public boolean initialized() { return true; }
    
    public String getConnectionCacheType() { return "FOO"; }
    
    public void setConnectionCache(InboundConnectionCache param1InboundConnectionCache) {}
    
    public InboundConnectionCache getConnectionCache() { return null; }
    
    public boolean shouldRegisterAcceptEvent() { return true; }
    
    public void setUseSelectThreadForConnections(boolean param1Boolean) {}
    
    public boolean shouldUseSelectThreadForConnections() { return true; }
    
    public void setUseWorkerThreadForConnections(boolean param1Boolean) {}
    
    public boolean shouldUseWorkerThreadForConnections() { return true; }
    
    public void accept() {}
    
    public void close() {}
    
    public EventHandler getEventHandler() { return null; }
    
    public MessageMediator createMessageMediator(Broker param1Broker, Connection param1Connection) { return null; }
    
    public MessageMediator finishCreatingMessageMediator(Broker param1Broker, Connection param1Connection, MessageMediator param1MessageMediator) { return null; }
    
    public InputObject createInputObject(Broker param1Broker, MessageMediator param1MessageMediator) { return null; }
    
    public OutputObject createOutputObject(Broker param1Broker, MessageMediator param1MessageMediator) { return null; }
  }
  
  public static final class TestAcceptor2 implements Acceptor {
    public boolean equals(Object param1Object) { return param1Object instanceof TestAcceptor2; }
    
    public int hashCode() { return 1; }
    
    public boolean initialize() { return true; }
    
    public boolean initialized() { return true; }
    
    public String getConnectionCacheType() { return "FOO"; }
    
    public void setConnectionCache(InboundConnectionCache param1InboundConnectionCache) {}
    
    public InboundConnectionCache getConnectionCache() { return null; }
    
    public boolean shouldRegisterAcceptEvent() { return true; }
    
    public void setUseSelectThreadForConnections(boolean param1Boolean) {}
    
    public boolean shouldUseSelectThreadForConnections() { return true; }
    
    public void setUseWorkerThreadForConnections(boolean param1Boolean) {}
    
    public boolean shouldUseWorkerThreadForConnections() { return true; }
    
    public void accept() {}
    
    public void close() {}
    
    public EventHandler getEventHandler() { return null; }
    
    public MessageMediator createMessageMediator(Broker param1Broker, Connection param1Connection) { return null; }
    
    public MessageMediator finishCreatingMessageMediator(Broker param1Broker, Connection param1Connection, MessageMediator param1MessageMediator) { return null; }
    
    public InputObject createInputObject(Broker param1Broker, MessageMediator param1MessageMediator) { return null; }
    
    public OutputObject createOutputObject(Broker param1Broker, MessageMediator param1MessageMediator) { return null; }
  }
  
  public final class TestBadServerIdHandler implements BadServerIdHandler {
    public boolean equals(Object param1Object) { return param1Object instanceof TestBadServerIdHandler; }
    
    public int hashCode() { return 1; }
    
    public void handle(ObjectKey param1ObjectKey) {}
  }
  
  public static final class TestContactInfoListFactory implements CorbaContactInfoListFactory {
    public boolean equals(Object param1Object) { return param1Object instanceof TestContactInfoListFactory; }
    
    public int hashCode() { return 1; }
    
    public void setORB(ORB param1ORB) {}
    
    public CorbaContactInfoList create(IOR param1IOR) { return null; }
  }
  
  public static final class TestIIOPPrimaryToContactInfo implements IIOPPrimaryToContactInfo {
    public void reset(ContactInfo param1ContactInfo) {}
    
    public boolean hasNext(ContactInfo param1ContactInfo1, ContactInfo param1ContactInfo2, List param1List) { return true; }
    
    public ContactInfo next(ContactInfo param1ContactInfo1, ContactInfo param1ContactInfo2, List param1List) { return null; }
  }
  
  public static final class TestIORToSocketInfo implements IORToSocketInfo {
    public boolean equals(Object param1Object) { return param1Object instanceof TestIORToSocketInfo; }
    
    public int hashCode() { return 1; }
    
    public List getSocketInfo(IOR param1IOR) { return null; }
  }
  
  public static final class TestLegacyORBSocketFactory implements ORBSocketFactory {
    public boolean equals(Object param1Object) { return param1Object instanceof TestLegacyORBSocketFactory; }
    
    public int hashCode() { return 1; }
    
    public ServerSocket createServerSocket(String param1String, int param1Int) { return null; }
    
    public SocketInfo getEndPointInfo(ORB param1ORB, IOR param1IOR, SocketInfo param1SocketInfo) { return null; }
    
    public Socket createSocket(SocketInfo param1SocketInfo) { return null; }
  }
  
  public static final class TestORBInitializer1 extends LocalObject implements ORBInitializer {
    public boolean equals(Object param1Object) { return param1Object instanceof TestORBInitializer1; }
    
    public int hashCode() { return 1; }
    
    public void pre_init(ORBInitInfo param1ORBInitInfo) {}
    
    public void post_init(ORBInitInfo param1ORBInitInfo) {}
  }
  
  public static final class TestORBInitializer2 extends LocalObject implements ORBInitializer {
    public boolean equals(Object param1Object) { return param1Object instanceof TestORBInitializer2; }
    
    public int hashCode() { return 1; }
    
    public void pre_init(ORBInitInfo param1ORBInitInfo) {}
    
    public void post_init(ORBInitInfo param1ORBInitInfo) {}
  }
  
  public static final class TestORBSocketFactory implements ORBSocketFactory {
    public boolean equals(Object param1Object) { return param1Object instanceof TestORBSocketFactory; }
    
    public int hashCode() { return 1; }
    
    public void setORB(ORB param1ORB) {}
    
    public ServerSocket createServerSocket(String param1String, InetSocketAddress param1InetSocketAddress) { return null; }
    
    public Socket createSocket(String param1String, InetSocketAddress param1InetSocketAddress) { return null; }
    
    public void setAcceptedSocketOptions(Acceptor param1Acceptor, ServerSocket param1ServerSocket, Socket param1Socket) {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\orb\ParserTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */