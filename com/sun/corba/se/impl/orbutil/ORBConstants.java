package com.sun.corba.se.impl.orbutil;

public class ORBConstants {
  public static final String STRINGIFY_PREFIX = "IOR:";
  
  public static final int NEO_FIRST_SERVICE_CONTEXT = 1313165056;
  
  public static final int NUM_NEO_SERVICE_CONTEXTS = 15;
  
  public static final int TAG_ORB_VERSION = 1313165056;
  
  public static final int SUN_TAGGED_COMPONENT_ID_BASE = 1398099456;
  
  public static final int SUN_SERVICE_CONTEXT_ID_BASE = 1398099456;
  
  public static final int TAG_CONTAINER_ID = 1398099456;
  
  public static final int TAG_REQUEST_PARTITIONING_ID = 1398099457;
  
  public static final int TAG_JAVA_SERIALIZATION_ID = 1398099458;
  
  public static final int CONTAINER_ID_SERVICE_CONTEXT = 1398099456;
  
  public static final int SERVANT_CACHING_POLICY = 1398079488;
  
  public static final int ZERO_PORT_POLICY = 1398079489;
  
  public static final int COPY_OBJECT_POLICY = 1398079490;
  
  public static final int REQUEST_PARTITIONING_POLICY = 1398079491;
  
  public static final int TOA_SCID = 2;
  
  public static final int DEFAULT_SCID = 2;
  
  public static final int FIRST_POA_SCID = 32;
  
  public static final int MAX_POA_SCID = 63;
  
  public static final int TRANSIENT_SCID = 32;
  
  public static final int PERSISTENT_SCID = makePersistent(32);
  
  public static final int SC_TRANSIENT_SCID = 36;
  
  public static final int SC_PERSISTENT_SCID = makePersistent(36);
  
  public static final int IISC_TRANSIENT_SCID = 40;
  
  public static final int IISC_PERSISTENT_SCID = makePersistent(40);
  
  public static final int MINSC_TRANSIENT_SCID = 44;
  
  public static final int MINSC_PERSISTENT_SCID = makePersistent(44);
  
  public static final String ORG_OMG_PREFIX = "org.omg.";
  
  public static final String ORG_OMG_CORBA_PREFIX = "org.omg.CORBA.";
  
  public static final String INITIAL_HOST_PROPERTY = "org.omg.CORBA.ORBInitialHost";
  
  public static final String INITIAL_PORT_PROPERTY = "org.omg.CORBA.ORBInitialPort";
  
  public static final String INITIAL_SERVICES_PROPERTY = "org.omg.CORBA.ORBInitialServices";
  
  public static final String DEFAULT_INIT_REF_PROPERTY = "org.omg.CORBA.ORBDefaultInitRef";
  
  public static final String ORB_INIT_REF_PROPERTY = "org.omg.CORBA.ORBInitRef";
  
  public static final String SUN_PREFIX = "com.sun.CORBA.";
  
  public static final String ALLOW_LOCAL_OPTIMIZATION = "com.sun.CORBA.ORBAllowLocalOptimization";
  
  public static final String SERVER_PORT_PROPERTY = "com.sun.CORBA.ORBServerPort";
  
  public static final String SERVER_HOST_PROPERTY = "com.sun.CORBA.ORBServerHost";
  
  public static final String ORB_ID_PROPERTY = "org.omg.CORBA.ORBId";
  
  public static final String OLD_ORB_ID_PROPERTY = "com.sun.CORBA.ORBid";
  
  public static final String ORB_SERVER_ID_PROPERTY = "org.omg.CORBA.ORBServerId";
  
  public static final String DEBUG_PROPERTY = "com.sun.CORBA.ORBDebug";
  
  public static final String USE_REP_ID = "com.sun.CORBA.ORBUseRepId";
  
  public static final String LISTEN_ON_ALL_INTERFACES = "com.sun.CORBA.INTERNAL USE ONLY: listen on all interfaces";
  
  public static final String GIOP_VERSION = "com.sun.CORBA.giop.ORBGIOPVersion";
  
  public static final String GIOP_FRAGMENT_SIZE = "com.sun.CORBA.giop.ORBFragmentSize";
  
  public static final String GIOP_BUFFER_SIZE = "com.sun.CORBA.giop.ORBBufferSize";
  
  public static final String GIOP_11_BUFFMGR = "com.sun.CORBA.giop.ORBGIOP11BuffMgr";
  
  public static final String GIOP_12_BUFFMGR = "com.sun.CORBA.giop.ORBGIOP12BuffMgr";
  
  public static final String GIOP_TARGET_ADDRESSING = "com.sun.CORBA.giop.ORBTargetAddressing";
  
  public static final int GIOP_DEFAULT_FRAGMENT_SIZE = 1024;
  
  public static final int GIOP_DEFAULT_BUFFER_SIZE = 1024;
  
  public static final int DEFAULT_GIOP_11_BUFFMGR = 0;
  
  public static final int DEFAULT_GIOP_12_BUFFMGR = 2;
  
  public static final short ADDR_DISP_OBJKEY = 0;
  
  public static final short ADDR_DISP_PROFILE = 1;
  
  public static final short ADDR_DISP_IOR = 2;
  
  public static final short ADDR_DISP_HANDLE_ALL = 3;
  
  public static final int GIOP_12_MSG_BODY_ALIGNMENT = 8;
  
  public static final int GIOP_FRAGMENT_DIVISOR = 8;
  
  public static final int GIOP_FRAGMENT_MINIMUM_SIZE = 32;
  
  public static final String HIGH_WATER_MARK_PROPERTY = "com.sun.CORBA.connection.ORBHighWaterMark";
  
  public static final String LOW_WATER_MARK_PROPERTY = "com.sun.CORBA.connection.ORBLowWaterMark";
  
  public static final String NUMBER_TO_RECLAIM_PROPERTY = "com.sun.CORBA.connection.ORBNumberToReclaim";
  
  public static final String ACCEPTOR_CLASS_PREFIX_PROPERTY = "com.sun.CORBA.transport.ORBAcceptor";
  
  public static final String CONTACT_INFO_LIST_FACTORY_CLASS_PROPERTY = "com.sun.CORBA.transport.ORBContactInfoList";
  
  public static final String LEGACY_SOCKET_FACTORY_CLASS_PROPERTY = "com.sun.CORBA.legacy.connection.ORBSocketFactoryClass";
  
  public static final String SOCKET_FACTORY_CLASS_PROPERTY = "com.sun.CORBA.transport.ORBSocketFactoryClass";
  
  public static final String LISTEN_SOCKET_PROPERTY = "com.sun.CORBA.transport.ORBListenSocket";
  
  public static final String IOR_TO_SOCKET_INFO_CLASS_PROPERTY = "com.sun.CORBA.transport.ORBIORToSocketInfoClass";
  
  public static final String IIOP_PRIMARY_TO_CONTACT_INFO_CLASS_PROPERTY = "com.sun.CORBA.transport.ORBIIOPPrimaryToContactInfoClass";
  
  public static final int REQUEST_PARTITIONING_MIN_THREAD_POOL_ID = 0;
  
  public static final int REQUEST_PARTITIONING_MAX_THREAD_POOL_ID = 63;
  
  public static final String TRANSPORT_TCP_READ_TIMEOUTS_PROPERTY = "com.sun.CORBA.transport.ORBTCPReadTimeouts";
  
  public static final int TRANSPORT_TCP_INITIAL_TIME_TO_WAIT = 100;
  
  public static final int TRANSPORT_TCP_MAX_TIME_TO_WAIT = 3000;
  
  public static final int TRANSPORT_TCP_GIOP_HEADER_MAX_TIME_TO_WAIT = 300;
  
  public static final int TRANSPORT_TCP_TIME_TO_WAIT_BACKOFF_FACTOR = 20;
  
  public static final String USE_NIO_SELECT_TO_WAIT_PROPERTY = "com.sun.CORBA.transport.ORBUseNIOSelectToWait";
  
  public static final String ACCEPTOR_SOCKET_TYPE_PROPERTY = "com.sun.CORBA.transport.ORBAcceptorSocketType";
  
  public static final String ACCEPTOR_SOCKET_USE_WORKER_THREAD_FOR_EVENT_PROPERTY = "com.sun.CORBA.transport.ORBAcceptorSocketUseWorkerThreadForEvent";
  
  public static final String CONNECTION_SOCKET_TYPE_PROPERTY = "com.sun.CORBA.transport.ORBConnectionSocketType";
  
  public static final String CONNECTION_SOCKET_USE_WORKER_THREAD_FOR_EVENT_PROPERTY = "com.sun.CORBA.transport.ORBConnectionSocketUseWorkerThreadForEvent";
  
  public static final String DISABLE_DIRECT_BYTE_BUFFER_USE_PROPERTY = "com.sun.CORBA.transport.ORBDisableDirectByteBufferUse";
  
  public static final String SOCKET = "Socket";
  
  public static final String SOCKETCHANNEL = "SocketChannel";
  
  public static final String PERSISTENT_SERVER_PORT_PROPERTY = "com.sun.CORBA.POA.ORBPersistentServerPort";
  
  public static final String SERVER_ID_PROPERTY = "com.sun.CORBA.POA.ORBServerId";
  
  public static final String BAD_SERVER_ID_HANDLER_CLASS_PROPERTY = "com.sun.CORBA.POA.ORBBadServerIdHandlerClass";
  
  public static final String ACTIVATED_PROPERTY = "com.sun.CORBA.POA.ORBActivated";
  
  public static final String SERVER_NAME_PROPERTY = "com.sun.CORBA.POA.ORBServerName";
  
  public static final String SERVER_DEF_VERIFY_PROPERTY = "com.sun.CORBA.activation.ORBServerVerify";
  
  public static final String SUN_LC_PREFIX = "com.sun.corba.";
  
  public static final String SUN_LC_VERSION_PREFIX = "com.sun.corba.se.";
  
  public static final String JTS_CLASS_PROPERTY = "com.sun.corba.se.CosTransactions.ORBJTSClass";
  
  public static final String ENABLE_JAVA_SERIALIZATION_PROPERTY = "com.sun.CORBA.encoding.ORBEnableJavaSerialization";
  
  public static final String PI_ORB_INITIALIZER_CLASS_PREFIX = "org.omg.PortableInterceptor.ORBInitializerClass.";
  
  public static final String USE_DYNAMIC_STUB_PROPERTY = "com.sun.CORBA.ORBUseDynamicStub";
  
  public static final String DYNAMIC_STUB_FACTORY_FACTORY_CLASS = "com.sun.CORBA.ORBDynamicStubFactoryFactoryClass";
  
  public static final int DEFAULT_INITIAL_PORT = 900;
  
  public static final String DEFAULT_INS_HOST = "localhost";
  
  public static final int DEFAULT_INS_PORT = 2089;
  
  public static final int DEFAULT_INS_GIOP_MAJOR_VERSION = 1;
  
  public static final int DEFAULT_INS_GIOP_MINOR_VERSION = 0;
  
  public static final int MAJORNUMBER_SUPPORTED = 1;
  
  public static final int MINORNUMBERMAX = 2;
  
  public static final int TRANSIENT = 1;
  
  public static final int PERSISTENT = 2;
  
  public static final String DB_DIR_PROPERTY = "com.sun.CORBA.activation.DbDir";
  
  public static final String DB_PROPERTY = "com.sun.CORBA.activation.db";
  
  public static final String ORBD_PORT_PROPERTY = "com.sun.CORBA.activation.Port";
  
  public static final String SERVER_POLLING_TIME = "com.sun.CORBA.activation.ServerPollingTime";
  
  public static final String SERVER_STARTUP_DELAY = "com.sun.CORBA.activation.ServerStartupDelay";
  
  public static final int DEFAULT_ACTIVATION_PORT = 1049;
  
  public static final int RI_NAMESERVICE_PORT = 1050;
  
  public static final int DEFAULT_SERVER_POLLING_TIME = 1000;
  
  public static final int DEFAULT_SERVER_STARTUP_DELAY = 1000;
  
  public static final String LOG_LEVEL_PROPERTY = "com.sun.CORBA.ORBLogLevel";
  
  public static final String LOG_RESOURCE_FILE = "com.sun.corba.se.impl.logging.LogStrings";
  
  public static final String TRANSIENT_NAME_SERVICE_NAME = "TNameService";
  
  public static final String PERSISTENT_NAME_SERVICE_NAME = "NameService";
  
  public static final String NAME_SERVICE_SERVER_ID = "1000000";
  
  public static final String ROOT_POA_NAME = "RootPOA";
  
  public static final String POA_CURRENT_NAME = "POACurrent";
  
  public static final String SERVER_ACTIVATOR_NAME = "ServerActivator";
  
  public static final String SERVER_LOCATOR_NAME = "ServerLocator";
  
  public static final String SERVER_REPOSITORY_NAME = "ServerRepository";
  
  public static final String INITIAL_NAME_SERVICE_NAME = "InitialNameService";
  
  public static final String TRANSACTION_CURRENT_NAME = "TransactionCurrent";
  
  public static final String DYN_ANY_FACTORY_NAME = "DynAnyFactory";
  
  public static final String PI_CURRENT_NAME = "PICurrent";
  
  public static final String CODEC_FACTORY_NAME = "CodecFactory";
  
  public static final String DEFAULT_DB_DIR = "orb.db";
  
  public static final String DEFAULT_DB_NAME = "db";
  
  public static final String INITIAL_ORB_DB = "initial.db";
  
  public static final String SERVER_LOG_DIR = "logs";
  
  public static final String ORBID_DIR_BASE = "orbids";
  
  public static final String ORBID_DB_FILE_NAME = "orbids.db";
  
  public static final int DEFAULT_INACTIVITY_TIMEOUT = 120000;
  
  public static final String THREADPOOL_DEFAULT_NAME = "default-threadpool";
  
  public static final String WORKQUEUE_DEFAULT_NAME = "default-workqueue";
  
  public static final int LEGACY_SUN_NOT_SERIALIZABLE = 1398079489;
  
  public static final boolean DEFAULT_ALWAYS_SEND_CODESET_CTX = true;
  
  public static final String ALWAYS_SEND_CODESET_CTX_PROPERTY = "com.sun.CORBA.codeset.AlwaysSendCodeSetCtx";
  
  public static final boolean DEFAULT_USE_BYTE_ORDER_MARKERS = true;
  
  public static final String USE_BOMS = "com.sun.CORBA.codeset.UseByteOrderMarkers";
  
  public static final boolean DEFAULT_USE_BYTE_ORDER_MARKERS_IN_ENCAPS = false;
  
  public static final String USE_BOMS_IN_ENCAPS = "com.sun.CORBA.codeset.UseByteOrderMarkersInEncaps";
  
  public static final String CHAR_CODESETS = "com.sun.CORBA.codeset.charsets";
  
  public static final String WCHAR_CODESETS = "com.sun.CORBA.codeset.wcharsets";
  
  public static final byte STREAM_FORMAT_VERSION_1 = 1;
  
  public static final byte STREAM_FORMAT_VERSION_2 = 2;
  
  public static boolean isTransient(int paramInt) { return ((paramInt & 0x2) == 0); }
  
  public static int makePersistent(int paramInt) { return paramInt | 0x2; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\orbutil\ORBConstants.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */