package com.sun.corba.se.impl.logging;

import com.sun.corba.se.spi.logging.LogWrapperBase;
import com.sun.corba.se.spi.logging.LogWrapperFactory;
import com.sun.corba.se.spi.orb.ORB;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.omg.CORBA.BAD_INV_ORDER;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.BAD_TYPECODE;
import org.omg.CORBA.COMM_FAILURE;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.DATA_CONVERSION;
import org.omg.CORBA.INITIALIZE;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.INV_OBJREF;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.omg.CORBA.OBJ_ADAPTER;
import org.omg.CORBA.TRANSIENT;
import org.omg.CORBA.UNKNOWN;

public class ORBUtilSystemException extends LogWrapperBase {
  private static LogWrapperFactory factory = new LogWrapperFactory() {
      public LogWrapperBase create(Logger param1Logger) { return new ORBUtilSystemException(param1Logger); }
    };
  
  public static final int ADAPTER_ID_NOT_AVAILABLE = 1398079689;
  
  public static final int SERVER_ID_NOT_AVAILABLE = 1398079690;
  
  public static final int ORB_ID_NOT_AVAILABLE = 1398079691;
  
  public static final int OBJECT_ADAPTER_ID_NOT_AVAILABLE = 1398079692;
  
  public static final int CONNECTING_SERVANT = 1398079693;
  
  public static final int EXTRACT_WRONG_TYPE = 1398079694;
  
  public static final int EXTRACT_WRONG_TYPE_LIST = 1398079695;
  
  public static final int BAD_STRING_BOUNDS = 1398079696;
  
  public static final int INSERT_OBJECT_INCOMPATIBLE = 1398079698;
  
  public static final int INSERT_OBJECT_FAILED = 1398079699;
  
  public static final int EXTRACT_OBJECT_INCOMPATIBLE = 1398079700;
  
  public static final int FIXED_NOT_MATCH = 1398079701;
  
  public static final int FIXED_BAD_TYPECODE = 1398079702;
  
  public static final int SET_EXCEPTION_CALLED_NULL_ARGS = 1398079711;
  
  public static final int SET_EXCEPTION_CALLED_BAD_TYPE = 1398079712;
  
  public static final int CONTEXT_CALLED_OUT_OF_ORDER = 1398079713;
  
  public static final int BAD_ORB_CONFIGURATOR = 1398079714;
  
  public static final int ORB_CONFIGURATOR_ERROR = 1398079715;
  
  public static final int ORB_DESTROYED = 1398079716;
  
  public static final int NEGATIVE_BOUNDS = 1398079717;
  
  public static final int EXTRACT_NOT_INITIALIZED = 1398079718;
  
  public static final int EXTRACT_OBJECT_FAILED = 1398079719;
  
  public static final int METHOD_NOT_FOUND_IN_TIE = 1398079720;
  
  public static final int CLASS_NOT_FOUND1 = 1398079721;
  
  public static final int CLASS_NOT_FOUND2 = 1398079722;
  
  public static final int CLASS_NOT_FOUND3 = 1398079723;
  
  public static final int GET_DELEGATE_SERVANT_NOT_ACTIVE = 1398079724;
  
  public static final int GET_DELEGATE_WRONG_POLICY = 1398079725;
  
  public static final int SET_DELEGATE_REQUIRES_STUB = 1398079726;
  
  public static final int GET_DELEGATE_REQUIRES_STUB = 1398079727;
  
  public static final int GET_TYPE_IDS_REQUIRES_STUB = 1398079728;
  
  public static final int GET_ORB_REQUIRES_STUB = 1398079729;
  
  public static final int CONNECT_REQUIRES_STUB = 1398079730;
  
  public static final int IS_LOCAL_REQUIRES_STUB = 1398079731;
  
  public static final int REQUEST_REQUIRES_STUB = 1398079732;
  
  public static final int BAD_ACTIVATE_TIE_CALL = 1398079733;
  
  public static final int IO_EXCEPTION_ON_CLOSE = 1398079734;
  
  public static final int NULL_PARAM = 1398079689;
  
  public static final int UNABLE_FIND_VALUE_FACTORY = 1398079690;
  
  public static final int ABSTRACT_FROM_NON_ABSTRACT = 1398079691;
  
  public static final int INVALID_TAGGED_PROFILE = 1398079692;
  
  public static final int OBJREF_FROM_FOREIGN_ORB = 1398079693;
  
  public static final int LOCAL_OBJECT_NOT_ALLOWED = 1398079694;
  
  public static final int NULL_OBJECT_REFERENCE = 1398079695;
  
  public static final int COULD_NOT_LOAD_CLASS = 1398079696;
  
  public static final int BAD_URL = 1398079697;
  
  public static final int FIELD_NOT_FOUND = 1398079698;
  
  public static final int ERROR_SETTING_FIELD = 1398079699;
  
  public static final int BOUNDS_ERROR_IN_DII_REQUEST = 1398079700;
  
  public static final int PERSISTENT_SERVER_INIT_ERROR = 1398079701;
  
  public static final int COULD_NOT_CREATE_ARRAY = 1398079702;
  
  public static final int COULD_NOT_SET_ARRAY = 1398079703;
  
  public static final int ILLEGAL_BOOTSTRAP_OPERATION = 1398079704;
  
  public static final int BOOTSTRAP_RUNTIME_EXCEPTION = 1398079705;
  
  public static final int BOOTSTRAP_EXCEPTION = 1398079706;
  
  public static final int STRING_EXPECTED = 1398079707;
  
  public static final int INVALID_TYPECODE_KIND = 1398079708;
  
  public static final int SOCKET_FACTORY_AND_CONTACT_INFO_LIST_AT_SAME_TIME = 1398079709;
  
  public static final int ACCEPTORS_AND_LEGACY_SOCKET_FACTORY_AT_SAME_TIME = 1398079710;
  
  public static final int BAD_ORB_FOR_SERVANT = 1398079711;
  
  public static final int INVALID_REQUEST_PARTITIONING_POLICY_VALUE = 1398079712;
  
  public static final int INVALID_REQUEST_PARTITIONING_COMPONENT_VALUE = 1398079713;
  
  public static final int INVALID_REQUEST_PARTITIONING_ID = 1398079714;
  
  public static final int ERROR_IN_SETTING_DYNAMIC_STUB_FACTORY_FACTORY = 1398079715;
  
  public static final int DSIMETHOD_NOTCALLED = 1398079689;
  
  public static final int ARGUMENTS_CALLED_MULTIPLE = 1398079690;
  
  public static final int ARGUMENTS_CALLED_AFTER_EXCEPTION = 1398079691;
  
  public static final int ARGUMENTS_CALLED_NULL_ARGS = 1398079692;
  
  public static final int ARGUMENTS_NOT_CALLED = 1398079693;
  
  public static final int SET_RESULT_CALLED_MULTIPLE = 1398079694;
  
  public static final int SET_RESULT_AFTER_EXCEPTION = 1398079695;
  
  public static final int SET_RESULT_CALLED_NULL_ARGS = 1398079696;
  
  public static final int BAD_REMOTE_TYPECODE = 1398079689;
  
  public static final int UNRESOLVED_RECURSIVE_TYPECODE = 1398079690;
  
  public static final int CONNECT_FAILURE = 1398079689;
  
  public static final int CONNECTION_CLOSE_REBIND = 1398079690;
  
  public static final int WRITE_ERROR_SEND = 1398079691;
  
  public static final int GET_PROPERTIES_ERROR = 1398079692;
  
  public static final int BOOTSTRAP_SERVER_NOT_AVAIL = 1398079693;
  
  public static final int INVOKE_ERROR = 1398079694;
  
  public static final int DEFAULT_CREATE_SERVER_SOCKET_GIVEN_NON_IIOP_CLEAR_TEXT = 1398079695;
  
  public static final int CONNECTION_ABORT = 1398079696;
  
  public static final int CONNECTION_REBIND = 1398079697;
  
  public static final int RECV_MSG_ERROR = 1398079698;
  
  public static final int IOEXCEPTION_WHEN_READING_CONNECTION = 1398079699;
  
  public static final int SELECTION_KEY_INVALID = 1398079700;
  
  public static final int EXCEPTION_IN_ACCEPT = 1398079701;
  
  public static final int SECURITY_EXCEPTION_IN_ACCEPT = 1398079702;
  
  public static final int TRANSPORT_READ_TIMEOUT_EXCEEDED = 1398079703;
  
  public static final int CREATE_LISTENER_FAILED = 1398079704;
  
  public static final int BUFFER_READ_MANAGER_TIMEOUT = 1398079705;
  
  public static final int BAD_STRINGIFIED_IOR_LEN = 1398079689;
  
  public static final int BAD_STRINGIFIED_IOR = 1398079690;
  
  public static final int BAD_MODIFIER = 1398079691;
  
  public static final int CODESET_INCOMPATIBLE = 1398079692;
  
  public static final int BAD_HEX_DIGIT = 1398079693;
  
  public static final int BAD_UNICODE_PAIR = 1398079694;
  
  public static final int BTC_RESULT_MORE_THAN_ONE_CHAR = 1398079695;
  
  public static final int BAD_CODESETS_FROM_CLIENT = 1398079696;
  
  public static final int INVALID_SINGLE_CHAR_CTB = 1398079697;
  
  public static final int BAD_GIOP_1_1_CTB = 1398079698;
  
  public static final int BAD_SEQUENCE_BOUNDS = 1398079700;
  
  public static final int ILLEGAL_SOCKET_FACTORY_TYPE = 1398079701;
  
  public static final int BAD_CUSTOM_SOCKET_FACTORY = 1398079702;
  
  public static final int FRAGMENT_SIZE_MINIMUM = 1398079703;
  
  public static final int FRAGMENT_SIZE_DIV = 1398079704;
  
  public static final int ORB_INITIALIZER_FAILURE = 1398079705;
  
  public static final int ORB_INITIALIZER_TYPE = 1398079706;
  
  public static final int ORB_INITIALREFERENCE_SYNTAX = 1398079707;
  
  public static final int ACCEPTOR_INSTANTIATION_FAILURE = 1398079708;
  
  public static final int ACCEPTOR_INSTANTIATION_TYPE_FAILURE = 1398079709;
  
  public static final int ILLEGAL_CONTACT_INFO_LIST_FACTORY_TYPE = 1398079710;
  
  public static final int BAD_CONTACT_INFO_LIST_FACTORY = 1398079711;
  
  public static final int ILLEGAL_IOR_TO_SOCKET_INFO_TYPE = 1398079712;
  
  public static final int BAD_CUSTOM_IOR_TO_SOCKET_INFO = 1398079713;
  
  public static final int ILLEGAL_IIOP_PRIMARY_TO_CONTACT_INFO_TYPE = 1398079714;
  
  public static final int BAD_CUSTOM_IIOP_PRIMARY_TO_CONTACT_INFO = 1398079715;
  
  public static final int BAD_CORBALOC_STRING = 1398079689;
  
  public static final int NO_PROFILE_PRESENT = 1398079690;
  
  public static final int CANNOT_CREATE_ORBID_DB = 1398079689;
  
  public static final int CANNOT_READ_ORBID_DB = 1398079690;
  
  public static final int CANNOT_WRITE_ORBID_DB = 1398079691;
  
  public static final int GET_SERVER_PORT_CALLED_BEFORE_ENDPOINTS_INITIALIZED = 1398079692;
  
  public static final int PERSISTENT_SERVERPORT_NOT_SET = 1398079693;
  
  public static final int PERSISTENT_SERVERID_NOT_SET = 1398079694;
  
  public static final int NON_EXISTENT_ORBID = 1398079689;
  
  public static final int NO_SERVER_SUBCONTRACT = 1398079690;
  
  public static final int SERVER_SC_TEMP_SIZE = 1398079691;
  
  public static final int NO_CLIENT_SC_CLASS = 1398079692;
  
  public static final int SERVER_SC_NO_IIOP_PROFILE = 1398079693;
  
  public static final int GET_SYSTEM_EX_RETURNED_NULL = 1398079694;
  
  public static final int PEEKSTRING_FAILED = 1398079695;
  
  public static final int GET_LOCAL_HOST_FAILED = 1398079696;
  
  public static final int BAD_LOCATE_REQUEST_STATUS = 1398079698;
  
  public static final int STRINGIFY_WRITE_ERROR = 1398079699;
  
  public static final int BAD_GIOP_REQUEST_TYPE = 1398079700;
  
  public static final int ERROR_UNMARSHALING_USEREXC = 1398079701;
  
  public static final int RequestDispatcherRegistry_ERROR = 1398079702;
  
  public static final int LOCATIONFORWARD_ERROR = 1398079703;
  
  public static final int WRONG_CLIENTSC = 1398079704;
  
  public static final int BAD_SERVANT_READ_OBJECT = 1398079705;
  
  public static final int MULT_IIOP_PROF_NOT_SUPPORTED = 1398079706;
  
  public static final int GIOP_MAGIC_ERROR = 1398079708;
  
  public static final int GIOP_VERSION_ERROR = 1398079709;
  
  public static final int ILLEGAL_REPLY_STATUS = 1398079710;
  
  public static final int ILLEGAL_GIOP_MSG_TYPE = 1398079711;
  
  public static final int FRAGMENTATION_DISALLOWED = 1398079712;
  
  public static final int BAD_REPLYSTATUS = 1398079713;
  
  public static final int CTB_CONVERTER_FAILURE = 1398079714;
  
  public static final int BTC_CONVERTER_FAILURE = 1398079715;
  
  public static final int WCHAR_ARRAY_UNSUPPORTED_ENCODING = 1398079716;
  
  public static final int ILLEGAL_TARGET_ADDRESS_DISPOSITION = 1398079717;
  
  public static final int NULL_REPLY_IN_GET_ADDR_DISPOSITION = 1398079718;
  
  public static final int ORB_TARGET_ADDR_PREFERENCE_IN_EXTRACT_OBJECTKEY_INVALID = 1398079719;
  
  public static final int INVALID_ISSTREAMED_TCKIND = 1398079720;
  
  public static final int INVALID_JDK1_3_1_PATCH_LEVEL = 1398079721;
  
  public static final int SVCCTX_UNMARSHAL_ERROR = 1398079722;
  
  public static final int NULL_IOR = 1398079723;
  
  public static final int UNSUPPORTED_GIOP_VERSION = 1398079724;
  
  public static final int APPLICATION_EXCEPTION_IN_SPECIAL_METHOD = 1398079725;
  
  public static final int STATEMENT_NOT_REACHABLE1 = 1398079726;
  
  public static final int STATEMENT_NOT_REACHABLE2 = 1398079727;
  
  public static final int STATEMENT_NOT_REACHABLE3 = 1398079728;
  
  public static final int STATEMENT_NOT_REACHABLE4 = 1398079729;
  
  public static final int STATEMENT_NOT_REACHABLE5 = 1398079730;
  
  public static final int STATEMENT_NOT_REACHABLE6 = 1398079731;
  
  public static final int UNEXPECTED_DII_EXCEPTION = 1398079732;
  
  public static final int METHOD_SHOULD_NOT_BE_CALLED = 1398079733;
  
  public static final int CANCEL_NOT_SUPPORTED = 1398079734;
  
  public static final int EMPTY_STACK_RUN_SERVANT_POST_INVOKE = 1398079735;
  
  public static final int PROBLEM_WITH_EXCEPTION_TYPECODE = 1398079736;
  
  public static final int ILLEGAL_SUBCONTRACT_ID = 1398079737;
  
  public static final int BAD_SYSTEM_EXCEPTION_IN_LOCATE_REPLY = 1398079738;
  
  public static final int BAD_SYSTEM_EXCEPTION_IN_REPLY = 1398079739;
  
  public static final int BAD_COMPLETION_STATUS_IN_LOCATE_REPLY = 1398079740;
  
  public static final int BAD_COMPLETION_STATUS_IN_REPLY = 1398079741;
  
  public static final int BADKIND_CANNOT_OCCUR = 1398079742;
  
  public static final int ERROR_RESOLVING_ALIAS = 1398079743;
  
  public static final int TK_LONG_DOUBLE_NOT_SUPPORTED = 1398079744;
  
  public static final int TYPECODE_NOT_SUPPORTED = 1398079745;
  
  public static final int BOUNDS_CANNOT_OCCUR = 1398079747;
  
  public static final int NUM_INVOCATIONS_ALREADY_ZERO = 1398079749;
  
  public static final int ERROR_INIT_BADSERVERIDHANDLER = 1398079750;
  
  public static final int NO_TOA = 1398079751;
  
  public static final int NO_POA = 1398079752;
  
  public static final int INVOCATION_INFO_STACK_EMPTY = 1398079753;
  
  public static final int BAD_CODE_SET_STRING = 1398079754;
  
  public static final int UNKNOWN_NATIVE_CODESET = 1398079755;
  
  public static final int UNKNOWN_CONVERSION_CODE_SET = 1398079756;
  
  public static final int INVALID_CODE_SET_NUMBER = 1398079757;
  
  public static final int INVALID_CODE_SET_STRING = 1398079758;
  
  public static final int INVALID_CTB_CONVERTER_NAME = 1398079759;
  
  public static final int INVALID_BTC_CONVERTER_NAME = 1398079760;
  
  public static final int COULD_NOT_DUPLICATE_CDR_INPUT_STREAM = 1398079761;
  
  public static final int BOOTSTRAP_APPLICATION_EXCEPTION = 1398079762;
  
  public static final int DUPLICATE_INDIRECTION_OFFSET = 1398079763;
  
  public static final int BAD_MESSAGE_TYPE_FOR_CANCEL = 1398079764;
  
  public static final int DUPLICATE_EXCEPTION_DETAIL_MESSAGE = 1398079765;
  
  public static final int BAD_EXCEPTION_DETAIL_MESSAGE_SERVICE_CONTEXT_TYPE = 1398079766;
  
  public static final int UNEXPECTED_DIRECT_BYTE_BUFFER_WITH_NON_CHANNEL_SOCKET = 1398079767;
  
  public static final int UNEXPECTED_NON_DIRECT_BYTE_BUFFER_WITH_CHANNEL_SOCKET = 1398079768;
  
  public static final int INVALID_CONTACT_INFO_LIST_ITERATOR_FAILURE_EXCEPTION = 1398079770;
  
  public static final int REMARSHAL_WITH_NOWHERE_TO_GO = 1398079771;
  
  public static final int EXCEPTION_WHEN_SENDING_CLOSE_CONNECTION = 1398079772;
  
  public static final int INVOCATION_ERROR_IN_REFLECTIVE_TIE = 1398079773;
  
  public static final int BAD_HELPER_WRITE_METHOD = 1398079774;
  
  public static final int BAD_HELPER_READ_METHOD = 1398079775;
  
  public static final int BAD_HELPER_ID_METHOD = 1398079776;
  
  public static final int WRITE_UNDECLARED_EXCEPTION = 1398079777;
  
  public static final int READ_UNDECLARED_EXCEPTION = 1398079778;
  
  public static final int UNABLE_TO_SET_SOCKET_FACTORY_ORB = 1398079779;
  
  public static final int UNEXPECTED_EXCEPTION = 1398079780;
  
  public static final int NO_INVOCATION_HANDLER = 1398079781;
  
  public static final int INVALID_BUFF_MGR_STRATEGY = 1398079782;
  
  public static final int JAVA_STREAM_INIT_FAILED = 1398079783;
  
  public static final int DUPLICATE_ORB_VERSION_SERVICE_CONTEXT = 1398079784;
  
  public static final int DUPLICATE_SENDING_CONTEXT_SERVICE_CONTEXT = 1398079785;
  
  public static final int WORK_QUEUE_THREAD_INTERRUPTED = 1398079786;
  
  public static final int WORKER_THREAD_CREATED = 1398079792;
  
  public static final int WORKER_THREAD_THROWABLE_FROM_REQUEST_WORK = 1398079797;
  
  public static final int WORKER_THREAD_NOT_NEEDED = 1398079798;
  
  public static final int WORKER_THREAD_DO_WORK_THROWABLE = 1398079799;
  
  public static final int WORKER_THREAD_CAUGHT_UNEXPECTED_THROWABLE = 1398079800;
  
  public static final int WORKER_THREAD_CREATION_FAILURE = 1398079801;
  
  public static final int WORKER_THREAD_SET_NAME_FAILURE = 1398079802;
  
  public static final int WORK_QUEUE_REQUEST_WORK_NO_WORK_FOUND = 1398079804;
  
  public static final int THREAD_POOL_CLOSE_ERROR = 1398079814;
  
  public static final int THREAD_GROUP_IS_DESTROYED = 1398079815;
  
  public static final int THREAD_GROUP_HAS_ACTIVE_THREADS_IN_CLOSE = 1398079816;
  
  public static final int THREAD_GROUP_HAS_SUB_GROUPS_IN_CLOSE = 1398079817;
  
  public static final int THREAD_GROUP_DESTROY_FAILED = 1398079818;
  
  public static final int INTERRUPTED_JOIN_CALL_WHILE_CLOSING_THREAD_POOL = 1398079819;
  
  public static final int CHUNK_OVERFLOW = 1398079689;
  
  public static final int UNEXPECTED_EOF = 1398079690;
  
  public static final int READ_OBJECT_EXCEPTION = 1398079691;
  
  public static final int CHARACTER_OUTOFRANGE = 1398079692;
  
  public static final int DSI_RESULT_EXCEPTION = 1398079693;
  
  public static final int IIOPINPUTSTREAM_GROW = 1398079694;
  
  public static final int END_OF_STREAM = 1398079695;
  
  public static final int INVALID_OBJECT_KEY = 1398079696;
  
  public static final int MALFORMED_URL = 1398079697;
  
  public static final int VALUEHANDLER_READ_ERROR = 1398079698;
  
  public static final int VALUEHANDLER_READ_EXCEPTION = 1398079699;
  
  public static final int BAD_KIND = 1398079700;
  
  public static final int CNFE_READ_CLASS = 1398079701;
  
  public static final int BAD_REP_ID_INDIRECTION = 1398079702;
  
  public static final int BAD_CODEBASE_INDIRECTION = 1398079703;
  
  public static final int UNKNOWN_CODESET = 1398079704;
  
  public static final int WCHAR_DATA_IN_GIOP_1_0 = 1398079705;
  
  public static final int NEGATIVE_STRING_LENGTH = 1398079706;
  
  public static final int EXPECTED_TYPE_NULL_AND_NO_REP_ID = 1398079707;
  
  public static final int READ_VALUE_AND_NO_REP_ID = 1398079708;
  
  public static final int UNEXPECTED_ENCLOSING_VALUETYPE = 1398079710;
  
  public static final int POSITIVE_END_TAG = 1398079711;
  
  public static final int NULL_OUT_CALL = 1398079712;
  
  public static final int WRITE_LOCAL_OBJECT = 1398079713;
  
  public static final int BAD_INSERTOBJ_PARAM = 1398079714;
  
  public static final int CUSTOM_WRAPPER_WITH_CODEBASE = 1398079715;
  
  public static final int CUSTOM_WRAPPER_INDIRECTION = 1398079716;
  
  public static final int CUSTOM_WRAPPER_NOT_SINGLE_REPID = 1398079717;
  
  public static final int BAD_VALUE_TAG = 1398079718;
  
  public static final int BAD_TYPECODE_FOR_CUSTOM_VALUE = 1398079719;
  
  public static final int ERROR_INVOKING_HELPER_WRITE = 1398079720;
  
  public static final int BAD_DIGIT_IN_FIXED = 1398079721;
  
  public static final int REF_TYPE_INDIR_TYPE = 1398079722;
  
  public static final int BAD_RESERVED_LENGTH = 1398079723;
  
  public static final int NULL_NOT_ALLOWED = 1398079724;
  
  public static final int UNION_DISCRIMINATOR_ERROR = 1398079726;
  
  public static final int CANNOT_MARSHAL_NATIVE = 1398079727;
  
  public static final int CANNOT_MARSHAL_BAD_TCKIND = 1398079728;
  
  public static final int INVALID_INDIRECTION = 1398079729;
  
  public static final int INDIRECTION_NOT_FOUND = 1398079730;
  
  public static final int RECURSIVE_TYPECODE_ERROR = 1398079731;
  
  public static final int INVALID_SIMPLE_TYPECODE = 1398079732;
  
  public static final int INVALID_COMPLEX_TYPECODE = 1398079733;
  
  public static final int INVALID_TYPECODE_KIND_MARSHAL = 1398079734;
  
  public static final int UNEXPECTED_UNION_DEFAULT = 1398079735;
  
  public static final int ILLEGAL_UNION_DISCRIMINATOR_TYPE = 1398079736;
  
  public static final int COULD_NOT_SKIP_BYTES = 1398079737;
  
  public static final int BAD_CHUNK_LENGTH = 1398079738;
  
  public static final int UNABLE_TO_LOCATE_REP_ID_ARRAY = 1398079739;
  
  public static final int BAD_FIXED = 1398079740;
  
  public static final int READ_OBJECT_LOAD_CLASS_FAILURE = 1398079741;
  
  public static final int COULD_NOT_INSTANTIATE_HELPER = 1398079742;
  
  public static final int BAD_TOA_OAID = 1398079743;
  
  public static final int COULD_NOT_INVOKE_HELPER_READ_METHOD = 1398079744;
  
  public static final int COULD_NOT_FIND_CLASS = 1398079745;
  
  public static final int BAD_ARGUMENTS_NVLIST = 1398079746;
  
  public static final int STUB_CREATE_ERROR = 1398079747;
  
  public static final int JAVA_SERIALIZATION_EXCEPTION = 1398079748;
  
  public static final int GENERIC_NO_IMPL = 1398079689;
  
  public static final int CONTEXT_NOT_IMPLEMENTED = 1398079690;
  
  public static final int GETINTERFACE_NOT_IMPLEMENTED = 1398079691;
  
  public static final int SEND_DEFERRED_NOTIMPLEMENTED = 1398079692;
  
  public static final int LONG_DOUBLE_NOT_IMPLEMENTED = 1398079693;
  
  public static final int NO_SERVER_SC_IN_DISPATCH = 1398079689;
  
  public static final int ORB_CONNECT_ERROR = 1398079690;
  
  public static final int ADAPTER_INACTIVE_IN_ACTIVATION = 1398079691;
  
  public static final int LOCATE_UNKNOWN_OBJECT = 1398079689;
  
  public static final int BAD_SERVER_ID = 1398079690;
  
  public static final int BAD_SKELETON = 1398079691;
  
  public static final int SERVANT_NOT_FOUND = 1398079692;
  
  public static final int NO_OBJECT_ADAPTER_FACTORY = 1398079693;
  
  public static final int BAD_ADAPTER_ID = 1398079694;
  
  public static final int DYN_ANY_DESTROYED = 1398079695;
  
  public static final int REQUEST_CANCELED = 1398079689;
  
  public static final int UNKNOWN_CORBA_EXC = 1398079689;
  
  public static final int RUNTIMEEXCEPTION = 1398079690;
  
  public static final int UNKNOWN_SERVER_ERROR = 1398079691;
  
  public static final int UNKNOWN_DSI_SYSEX = 1398079692;
  
  public static final int UNKNOWN_SYSEX = 1398079693;
  
  public static final int WRONG_INTERFACE_DEF = 1398079694;
  
  public static final int NO_INTERFACE_DEF_STUB = 1398079695;
  
  public static final int UNKNOWN_EXCEPTION_IN_DISPATCH = 1398079697;
  
  public ORBUtilSystemException(Logger paramLogger) { super(paramLogger); }
  
  public static ORBUtilSystemException get(ORB paramORB, String paramString) { return (ORBUtilSystemException)paramORB.getLogWrapper(paramString, "ORBUTIL", factory); }
  
  public static ORBUtilSystemException get(String paramString) { return (ORBUtilSystemException)ORB.staticGetLogWrapper(paramString, "ORBUTIL", factory); }
  
  public BAD_OPERATION adapterIdNotAvailable(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_OPERATION bAD_OPERATION = new BAD_OPERATION(1398079689, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_OPERATION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.adapterIdNotAvailable", arrayOfObject, ORBUtilSystemException.class, bAD_OPERATION);
    } 
    return bAD_OPERATION;
  }
  
  public BAD_OPERATION adapterIdNotAvailable(CompletionStatus paramCompletionStatus) { return adapterIdNotAvailable(paramCompletionStatus, null); }
  
  public BAD_OPERATION adapterIdNotAvailable(Throwable paramThrowable) { return adapterIdNotAvailable(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_OPERATION adapterIdNotAvailable() { return adapterIdNotAvailable(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_OPERATION serverIdNotAvailable(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_OPERATION bAD_OPERATION = new BAD_OPERATION(1398079690, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_OPERATION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.serverIdNotAvailable", arrayOfObject, ORBUtilSystemException.class, bAD_OPERATION);
    } 
    return bAD_OPERATION;
  }
  
  public BAD_OPERATION serverIdNotAvailable(CompletionStatus paramCompletionStatus) { return serverIdNotAvailable(paramCompletionStatus, null); }
  
  public BAD_OPERATION serverIdNotAvailable(Throwable paramThrowable) { return serverIdNotAvailable(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_OPERATION serverIdNotAvailable() { return serverIdNotAvailable(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_OPERATION orbIdNotAvailable(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_OPERATION bAD_OPERATION = new BAD_OPERATION(1398079691, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_OPERATION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.orbIdNotAvailable", arrayOfObject, ORBUtilSystemException.class, bAD_OPERATION);
    } 
    return bAD_OPERATION;
  }
  
  public BAD_OPERATION orbIdNotAvailable(CompletionStatus paramCompletionStatus) { return orbIdNotAvailable(paramCompletionStatus, null); }
  
  public BAD_OPERATION orbIdNotAvailable(Throwable paramThrowable) { return orbIdNotAvailable(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_OPERATION orbIdNotAvailable() { return orbIdNotAvailable(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_OPERATION objectAdapterIdNotAvailable(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_OPERATION bAD_OPERATION = new BAD_OPERATION(1398079692, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_OPERATION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.objectAdapterIdNotAvailable", arrayOfObject, ORBUtilSystemException.class, bAD_OPERATION);
    } 
    return bAD_OPERATION;
  }
  
  public BAD_OPERATION objectAdapterIdNotAvailable(CompletionStatus paramCompletionStatus) { return objectAdapterIdNotAvailable(paramCompletionStatus, null); }
  
  public BAD_OPERATION objectAdapterIdNotAvailable(Throwable paramThrowable) { return objectAdapterIdNotAvailable(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_OPERATION objectAdapterIdNotAvailable() { return objectAdapterIdNotAvailable(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_OPERATION connectingServant(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_OPERATION bAD_OPERATION = new BAD_OPERATION(1398079693, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_OPERATION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.connectingServant", arrayOfObject, ORBUtilSystemException.class, bAD_OPERATION);
    } 
    return bAD_OPERATION;
  }
  
  public BAD_OPERATION connectingServant(CompletionStatus paramCompletionStatus) { return connectingServant(paramCompletionStatus, null); }
  
  public BAD_OPERATION connectingServant(Throwable paramThrowable) { return connectingServant(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_OPERATION connectingServant() { return connectingServant(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_OPERATION extractWrongType(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject1, Object paramObject2) {
    BAD_OPERATION bAD_OPERATION = new BAD_OPERATION(1398079694, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_OPERATION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = new Object[2];
      arrayOfObject[0] = paramObject1;
      arrayOfObject[1] = paramObject2;
      doLog(Level.FINE, "ORBUTIL.extractWrongType", arrayOfObject, ORBUtilSystemException.class, bAD_OPERATION);
    } 
    return bAD_OPERATION;
  }
  
  public BAD_OPERATION extractWrongType(CompletionStatus paramCompletionStatus, Object paramObject1, Object paramObject2) { return extractWrongType(paramCompletionStatus, null, paramObject1, paramObject2); }
  
  public BAD_OPERATION extractWrongType(Throwable paramThrowable, Object paramObject1, Object paramObject2) { return extractWrongType(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject1, paramObject2); }
  
  public BAD_OPERATION extractWrongType(Object paramObject1, Object paramObject2) { return extractWrongType(CompletionStatus.COMPLETED_NO, null, paramObject1, paramObject2); }
  
  public BAD_OPERATION extractWrongTypeList(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject1, Object paramObject2) {
    BAD_OPERATION bAD_OPERATION = new BAD_OPERATION(1398079695, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_OPERATION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[2];
      arrayOfObject[0] = paramObject1;
      arrayOfObject[1] = paramObject2;
      doLog(Level.WARNING, "ORBUTIL.extractWrongTypeList", arrayOfObject, ORBUtilSystemException.class, bAD_OPERATION);
    } 
    return bAD_OPERATION;
  }
  
  public BAD_OPERATION extractWrongTypeList(CompletionStatus paramCompletionStatus, Object paramObject1, Object paramObject2) { return extractWrongTypeList(paramCompletionStatus, null, paramObject1, paramObject2); }
  
  public BAD_OPERATION extractWrongTypeList(Throwable paramThrowable, Object paramObject1, Object paramObject2) { return extractWrongTypeList(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject1, paramObject2); }
  
  public BAD_OPERATION extractWrongTypeList(Object paramObject1, Object paramObject2) { return extractWrongTypeList(CompletionStatus.COMPLETED_NO, null, paramObject1, paramObject2); }
  
  public BAD_OPERATION badStringBounds(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject1, Object paramObject2) {
    BAD_OPERATION bAD_OPERATION = new BAD_OPERATION(1398079696, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_OPERATION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[2];
      arrayOfObject[0] = paramObject1;
      arrayOfObject[1] = paramObject2;
      doLog(Level.WARNING, "ORBUTIL.badStringBounds", arrayOfObject, ORBUtilSystemException.class, bAD_OPERATION);
    } 
    return bAD_OPERATION;
  }
  
  public BAD_OPERATION badStringBounds(CompletionStatus paramCompletionStatus, Object paramObject1, Object paramObject2) { return badStringBounds(paramCompletionStatus, null, paramObject1, paramObject2); }
  
  public BAD_OPERATION badStringBounds(Throwable paramThrowable, Object paramObject1, Object paramObject2) { return badStringBounds(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject1, paramObject2); }
  
  public BAD_OPERATION badStringBounds(Object paramObject1, Object paramObject2) { return badStringBounds(CompletionStatus.COMPLETED_NO, null, paramObject1, paramObject2); }
  
  public BAD_OPERATION insertObjectIncompatible(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_OPERATION bAD_OPERATION = new BAD_OPERATION(1398079698, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_OPERATION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.insertObjectIncompatible", arrayOfObject, ORBUtilSystemException.class, bAD_OPERATION);
    } 
    return bAD_OPERATION;
  }
  
  public BAD_OPERATION insertObjectIncompatible(CompletionStatus paramCompletionStatus) { return insertObjectIncompatible(paramCompletionStatus, null); }
  
  public BAD_OPERATION insertObjectIncompatible(Throwable paramThrowable) { return insertObjectIncompatible(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_OPERATION insertObjectIncompatible() { return insertObjectIncompatible(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_OPERATION insertObjectFailed(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_OPERATION bAD_OPERATION = new BAD_OPERATION(1398079699, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_OPERATION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.insertObjectFailed", arrayOfObject, ORBUtilSystemException.class, bAD_OPERATION);
    } 
    return bAD_OPERATION;
  }
  
  public BAD_OPERATION insertObjectFailed(CompletionStatus paramCompletionStatus) { return insertObjectFailed(paramCompletionStatus, null); }
  
  public BAD_OPERATION insertObjectFailed(Throwable paramThrowable) { return insertObjectFailed(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_OPERATION insertObjectFailed() { return insertObjectFailed(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_OPERATION extractObjectIncompatible(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_OPERATION bAD_OPERATION = new BAD_OPERATION(1398079700, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_OPERATION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.extractObjectIncompatible", arrayOfObject, ORBUtilSystemException.class, bAD_OPERATION);
    } 
    return bAD_OPERATION;
  }
  
  public BAD_OPERATION extractObjectIncompatible(CompletionStatus paramCompletionStatus) { return extractObjectIncompatible(paramCompletionStatus, null); }
  
  public BAD_OPERATION extractObjectIncompatible(Throwable paramThrowable) { return extractObjectIncompatible(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_OPERATION extractObjectIncompatible() { return extractObjectIncompatible(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_OPERATION fixedNotMatch(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_OPERATION bAD_OPERATION = new BAD_OPERATION(1398079701, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_OPERATION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.fixedNotMatch", arrayOfObject, ORBUtilSystemException.class, bAD_OPERATION);
    } 
    return bAD_OPERATION;
  }
  
  public BAD_OPERATION fixedNotMatch(CompletionStatus paramCompletionStatus) { return fixedNotMatch(paramCompletionStatus, null); }
  
  public BAD_OPERATION fixedNotMatch(Throwable paramThrowable) { return fixedNotMatch(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_OPERATION fixedNotMatch() { return fixedNotMatch(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_OPERATION fixedBadTypecode(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_OPERATION bAD_OPERATION = new BAD_OPERATION(1398079702, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_OPERATION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.fixedBadTypecode", arrayOfObject, ORBUtilSystemException.class, bAD_OPERATION);
    } 
    return bAD_OPERATION;
  }
  
  public BAD_OPERATION fixedBadTypecode(CompletionStatus paramCompletionStatus) { return fixedBadTypecode(paramCompletionStatus, null); }
  
  public BAD_OPERATION fixedBadTypecode(Throwable paramThrowable) { return fixedBadTypecode(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_OPERATION fixedBadTypecode() { return fixedBadTypecode(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_OPERATION setExceptionCalledNullArgs(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_OPERATION bAD_OPERATION = new BAD_OPERATION(1398079711, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_OPERATION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.setExceptionCalledNullArgs", arrayOfObject, ORBUtilSystemException.class, bAD_OPERATION);
    } 
    return bAD_OPERATION;
  }
  
  public BAD_OPERATION setExceptionCalledNullArgs(CompletionStatus paramCompletionStatus) { return setExceptionCalledNullArgs(paramCompletionStatus, null); }
  
  public BAD_OPERATION setExceptionCalledNullArgs(Throwable paramThrowable) { return setExceptionCalledNullArgs(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_OPERATION setExceptionCalledNullArgs() { return setExceptionCalledNullArgs(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_OPERATION setExceptionCalledBadType(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_OPERATION bAD_OPERATION = new BAD_OPERATION(1398079712, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_OPERATION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.setExceptionCalledBadType", arrayOfObject, ORBUtilSystemException.class, bAD_OPERATION);
    } 
    return bAD_OPERATION;
  }
  
  public BAD_OPERATION setExceptionCalledBadType(CompletionStatus paramCompletionStatus) { return setExceptionCalledBadType(paramCompletionStatus, null); }
  
  public BAD_OPERATION setExceptionCalledBadType(Throwable paramThrowable) { return setExceptionCalledBadType(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_OPERATION setExceptionCalledBadType() { return setExceptionCalledBadType(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_OPERATION contextCalledOutOfOrder(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_OPERATION bAD_OPERATION = new BAD_OPERATION(1398079713, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_OPERATION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.contextCalledOutOfOrder", arrayOfObject, ORBUtilSystemException.class, bAD_OPERATION);
    } 
    return bAD_OPERATION;
  }
  
  public BAD_OPERATION contextCalledOutOfOrder(CompletionStatus paramCompletionStatus) { return contextCalledOutOfOrder(paramCompletionStatus, null); }
  
  public BAD_OPERATION contextCalledOutOfOrder(Throwable paramThrowable) { return contextCalledOutOfOrder(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_OPERATION contextCalledOutOfOrder() { return contextCalledOutOfOrder(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_OPERATION badOrbConfigurator(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    BAD_OPERATION bAD_OPERATION = new BAD_OPERATION(1398079714, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_OPERATION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.WARNING, "ORBUTIL.badOrbConfigurator", arrayOfObject, ORBUtilSystemException.class, bAD_OPERATION);
    } 
    return bAD_OPERATION;
  }
  
  public BAD_OPERATION badOrbConfigurator(CompletionStatus paramCompletionStatus, Object paramObject) { return badOrbConfigurator(paramCompletionStatus, null, paramObject); }
  
  public BAD_OPERATION badOrbConfigurator(Throwable paramThrowable, Object paramObject) { return badOrbConfigurator(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public BAD_OPERATION badOrbConfigurator(Object paramObject) { return badOrbConfigurator(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public BAD_OPERATION orbConfiguratorError(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_OPERATION bAD_OPERATION = new BAD_OPERATION(1398079715, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_OPERATION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.orbConfiguratorError", arrayOfObject, ORBUtilSystemException.class, bAD_OPERATION);
    } 
    return bAD_OPERATION;
  }
  
  public BAD_OPERATION orbConfiguratorError(CompletionStatus paramCompletionStatus) { return orbConfiguratorError(paramCompletionStatus, null); }
  
  public BAD_OPERATION orbConfiguratorError(Throwable paramThrowable) { return orbConfiguratorError(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_OPERATION orbConfiguratorError() { return orbConfiguratorError(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_OPERATION orbDestroyed(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_OPERATION bAD_OPERATION = new BAD_OPERATION(1398079716, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_OPERATION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.orbDestroyed", arrayOfObject, ORBUtilSystemException.class, bAD_OPERATION);
    } 
    return bAD_OPERATION;
  }
  
  public BAD_OPERATION orbDestroyed(CompletionStatus paramCompletionStatus) { return orbDestroyed(paramCompletionStatus, null); }
  
  public BAD_OPERATION orbDestroyed(Throwable paramThrowable) { return orbDestroyed(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_OPERATION orbDestroyed() { return orbDestroyed(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_OPERATION negativeBounds(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_OPERATION bAD_OPERATION = new BAD_OPERATION(1398079717, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_OPERATION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.negativeBounds", arrayOfObject, ORBUtilSystemException.class, bAD_OPERATION);
    } 
    return bAD_OPERATION;
  }
  
  public BAD_OPERATION negativeBounds(CompletionStatus paramCompletionStatus) { return negativeBounds(paramCompletionStatus, null); }
  
  public BAD_OPERATION negativeBounds(Throwable paramThrowable) { return negativeBounds(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_OPERATION negativeBounds() { return negativeBounds(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_OPERATION extractNotInitialized(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_OPERATION bAD_OPERATION = new BAD_OPERATION(1398079718, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_OPERATION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.extractNotInitialized", arrayOfObject, ORBUtilSystemException.class, bAD_OPERATION);
    } 
    return bAD_OPERATION;
  }
  
  public BAD_OPERATION extractNotInitialized(CompletionStatus paramCompletionStatus) { return extractNotInitialized(paramCompletionStatus, null); }
  
  public BAD_OPERATION extractNotInitialized(Throwable paramThrowable) { return extractNotInitialized(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_OPERATION extractNotInitialized() { return extractNotInitialized(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_OPERATION extractObjectFailed(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_OPERATION bAD_OPERATION = new BAD_OPERATION(1398079719, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_OPERATION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.extractObjectFailed", arrayOfObject, ORBUtilSystemException.class, bAD_OPERATION);
    } 
    return bAD_OPERATION;
  }
  
  public BAD_OPERATION extractObjectFailed(CompletionStatus paramCompletionStatus) { return extractObjectFailed(paramCompletionStatus, null); }
  
  public BAD_OPERATION extractObjectFailed(Throwable paramThrowable) { return extractObjectFailed(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_OPERATION extractObjectFailed() { return extractObjectFailed(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_OPERATION methodNotFoundInTie(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject1, Object paramObject2) {
    BAD_OPERATION bAD_OPERATION = new BAD_OPERATION(1398079720, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_OPERATION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = new Object[2];
      arrayOfObject[0] = paramObject1;
      arrayOfObject[1] = paramObject2;
      doLog(Level.FINE, "ORBUTIL.methodNotFoundInTie", arrayOfObject, ORBUtilSystemException.class, bAD_OPERATION);
    } 
    return bAD_OPERATION;
  }
  
  public BAD_OPERATION methodNotFoundInTie(CompletionStatus paramCompletionStatus, Object paramObject1, Object paramObject2) { return methodNotFoundInTie(paramCompletionStatus, null, paramObject1, paramObject2); }
  
  public BAD_OPERATION methodNotFoundInTie(Throwable paramThrowable, Object paramObject1, Object paramObject2) { return methodNotFoundInTie(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject1, paramObject2); }
  
  public BAD_OPERATION methodNotFoundInTie(Object paramObject1, Object paramObject2) { return methodNotFoundInTie(CompletionStatus.COMPLETED_NO, null, paramObject1, paramObject2); }
  
  public BAD_OPERATION classNotFound1(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    BAD_OPERATION bAD_OPERATION = new BAD_OPERATION(1398079721, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_OPERATION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.FINE, "ORBUTIL.classNotFound1", arrayOfObject, ORBUtilSystemException.class, bAD_OPERATION);
    } 
    return bAD_OPERATION;
  }
  
  public BAD_OPERATION classNotFound1(CompletionStatus paramCompletionStatus, Object paramObject) { return classNotFound1(paramCompletionStatus, null, paramObject); }
  
  public BAD_OPERATION classNotFound1(Throwable paramThrowable, Object paramObject) { return classNotFound1(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public BAD_OPERATION classNotFound1(Object paramObject) { return classNotFound1(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public BAD_OPERATION classNotFound2(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    BAD_OPERATION bAD_OPERATION = new BAD_OPERATION(1398079722, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_OPERATION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.FINE, "ORBUTIL.classNotFound2", arrayOfObject, ORBUtilSystemException.class, bAD_OPERATION);
    } 
    return bAD_OPERATION;
  }
  
  public BAD_OPERATION classNotFound2(CompletionStatus paramCompletionStatus, Object paramObject) { return classNotFound2(paramCompletionStatus, null, paramObject); }
  
  public BAD_OPERATION classNotFound2(Throwable paramThrowable, Object paramObject) { return classNotFound2(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public BAD_OPERATION classNotFound2(Object paramObject) { return classNotFound2(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public BAD_OPERATION classNotFound3(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    BAD_OPERATION bAD_OPERATION = new BAD_OPERATION(1398079723, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_OPERATION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.FINE, "ORBUTIL.classNotFound3", arrayOfObject, ORBUtilSystemException.class, bAD_OPERATION);
    } 
    return bAD_OPERATION;
  }
  
  public BAD_OPERATION classNotFound3(CompletionStatus paramCompletionStatus, Object paramObject) { return classNotFound3(paramCompletionStatus, null, paramObject); }
  
  public BAD_OPERATION classNotFound3(Throwable paramThrowable, Object paramObject) { return classNotFound3(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public BAD_OPERATION classNotFound3(Object paramObject) { return classNotFound3(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public BAD_OPERATION getDelegateServantNotActive(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_OPERATION bAD_OPERATION = new BAD_OPERATION(1398079724, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_OPERATION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.getDelegateServantNotActive", arrayOfObject, ORBUtilSystemException.class, bAD_OPERATION);
    } 
    return bAD_OPERATION;
  }
  
  public BAD_OPERATION getDelegateServantNotActive(CompletionStatus paramCompletionStatus) { return getDelegateServantNotActive(paramCompletionStatus, null); }
  
  public BAD_OPERATION getDelegateServantNotActive(Throwable paramThrowable) { return getDelegateServantNotActive(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_OPERATION getDelegateServantNotActive() { return getDelegateServantNotActive(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_OPERATION getDelegateWrongPolicy(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_OPERATION bAD_OPERATION = new BAD_OPERATION(1398079725, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_OPERATION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.getDelegateWrongPolicy", arrayOfObject, ORBUtilSystemException.class, bAD_OPERATION);
    } 
    return bAD_OPERATION;
  }
  
  public BAD_OPERATION getDelegateWrongPolicy(CompletionStatus paramCompletionStatus) { return getDelegateWrongPolicy(paramCompletionStatus, null); }
  
  public BAD_OPERATION getDelegateWrongPolicy(Throwable paramThrowable) { return getDelegateWrongPolicy(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_OPERATION getDelegateWrongPolicy() { return getDelegateWrongPolicy(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_OPERATION setDelegateRequiresStub(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_OPERATION bAD_OPERATION = new BAD_OPERATION(1398079726, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_OPERATION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = null;
      doLog(Level.FINE, "ORBUTIL.setDelegateRequiresStub", arrayOfObject, ORBUtilSystemException.class, bAD_OPERATION);
    } 
    return bAD_OPERATION;
  }
  
  public BAD_OPERATION setDelegateRequiresStub(CompletionStatus paramCompletionStatus) { return setDelegateRequiresStub(paramCompletionStatus, null); }
  
  public BAD_OPERATION setDelegateRequiresStub(Throwable paramThrowable) { return setDelegateRequiresStub(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_OPERATION setDelegateRequiresStub() { return setDelegateRequiresStub(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_OPERATION getDelegateRequiresStub(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_OPERATION bAD_OPERATION = new BAD_OPERATION(1398079727, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_OPERATION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.getDelegateRequiresStub", arrayOfObject, ORBUtilSystemException.class, bAD_OPERATION);
    } 
    return bAD_OPERATION;
  }
  
  public BAD_OPERATION getDelegateRequiresStub(CompletionStatus paramCompletionStatus) { return getDelegateRequiresStub(paramCompletionStatus, null); }
  
  public BAD_OPERATION getDelegateRequiresStub(Throwable paramThrowable) { return getDelegateRequiresStub(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_OPERATION getDelegateRequiresStub() { return getDelegateRequiresStub(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_OPERATION getTypeIdsRequiresStub(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_OPERATION bAD_OPERATION = new BAD_OPERATION(1398079728, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_OPERATION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.getTypeIdsRequiresStub", arrayOfObject, ORBUtilSystemException.class, bAD_OPERATION);
    } 
    return bAD_OPERATION;
  }
  
  public BAD_OPERATION getTypeIdsRequiresStub(CompletionStatus paramCompletionStatus) { return getTypeIdsRequiresStub(paramCompletionStatus, null); }
  
  public BAD_OPERATION getTypeIdsRequiresStub(Throwable paramThrowable) { return getTypeIdsRequiresStub(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_OPERATION getTypeIdsRequiresStub() { return getTypeIdsRequiresStub(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_OPERATION getOrbRequiresStub(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_OPERATION bAD_OPERATION = new BAD_OPERATION(1398079729, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_OPERATION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.getOrbRequiresStub", arrayOfObject, ORBUtilSystemException.class, bAD_OPERATION);
    } 
    return bAD_OPERATION;
  }
  
  public BAD_OPERATION getOrbRequiresStub(CompletionStatus paramCompletionStatus) { return getOrbRequiresStub(paramCompletionStatus, null); }
  
  public BAD_OPERATION getOrbRequiresStub(Throwable paramThrowable) { return getOrbRequiresStub(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_OPERATION getOrbRequiresStub() { return getOrbRequiresStub(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_OPERATION connectRequiresStub(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_OPERATION bAD_OPERATION = new BAD_OPERATION(1398079730, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_OPERATION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.connectRequiresStub", arrayOfObject, ORBUtilSystemException.class, bAD_OPERATION);
    } 
    return bAD_OPERATION;
  }
  
  public BAD_OPERATION connectRequiresStub(CompletionStatus paramCompletionStatus) { return connectRequiresStub(paramCompletionStatus, null); }
  
  public BAD_OPERATION connectRequiresStub(Throwable paramThrowable) { return connectRequiresStub(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_OPERATION connectRequiresStub() { return connectRequiresStub(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_OPERATION isLocalRequiresStub(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_OPERATION bAD_OPERATION = new BAD_OPERATION(1398079731, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_OPERATION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.isLocalRequiresStub", arrayOfObject, ORBUtilSystemException.class, bAD_OPERATION);
    } 
    return bAD_OPERATION;
  }
  
  public BAD_OPERATION isLocalRequiresStub(CompletionStatus paramCompletionStatus) { return isLocalRequiresStub(paramCompletionStatus, null); }
  
  public BAD_OPERATION isLocalRequiresStub(Throwable paramThrowable) { return isLocalRequiresStub(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_OPERATION isLocalRequiresStub() { return isLocalRequiresStub(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_OPERATION requestRequiresStub(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_OPERATION bAD_OPERATION = new BAD_OPERATION(1398079732, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_OPERATION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.requestRequiresStub", arrayOfObject, ORBUtilSystemException.class, bAD_OPERATION);
    } 
    return bAD_OPERATION;
  }
  
  public BAD_OPERATION requestRequiresStub(CompletionStatus paramCompletionStatus) { return requestRequiresStub(paramCompletionStatus, null); }
  
  public BAD_OPERATION requestRequiresStub(Throwable paramThrowable) { return requestRequiresStub(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_OPERATION requestRequiresStub() { return requestRequiresStub(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_OPERATION badActivateTieCall(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_OPERATION bAD_OPERATION = new BAD_OPERATION(1398079733, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_OPERATION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.badActivateTieCall", arrayOfObject, ORBUtilSystemException.class, bAD_OPERATION);
    } 
    return bAD_OPERATION;
  }
  
  public BAD_OPERATION badActivateTieCall(CompletionStatus paramCompletionStatus) { return badActivateTieCall(paramCompletionStatus, null); }
  
  public BAD_OPERATION badActivateTieCall(Throwable paramThrowable) { return badActivateTieCall(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_OPERATION badActivateTieCall() { return badActivateTieCall(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_OPERATION ioExceptionOnClose(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_OPERATION bAD_OPERATION = new BAD_OPERATION(1398079734, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_OPERATION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = null;
      doLog(Level.FINE, "ORBUTIL.ioExceptionOnClose", arrayOfObject, ORBUtilSystemException.class, bAD_OPERATION);
    } 
    return bAD_OPERATION;
  }
  
  public BAD_OPERATION ioExceptionOnClose(CompletionStatus paramCompletionStatus) { return ioExceptionOnClose(paramCompletionStatus, null); }
  
  public BAD_OPERATION ioExceptionOnClose(Throwable paramThrowable) { return ioExceptionOnClose(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_OPERATION ioExceptionOnClose() { return ioExceptionOnClose(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_PARAM nullParam(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1398079689, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.nullParam", arrayOfObject, ORBUtilSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM nullParam(CompletionStatus paramCompletionStatus) { return nullParam(paramCompletionStatus, null); }
  
  public BAD_PARAM nullParam(Throwable paramThrowable) { return nullParam(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_PARAM nullParam() { return nullParam(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_PARAM unableFindValueFactory(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1398079690, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = null;
      doLog(Level.FINE, "ORBUTIL.unableFindValueFactory", arrayOfObject, ORBUtilSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM unableFindValueFactory(CompletionStatus paramCompletionStatus) { return unableFindValueFactory(paramCompletionStatus, null); }
  
  public BAD_PARAM unableFindValueFactory(Throwable paramThrowable) { return unableFindValueFactory(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_PARAM unableFindValueFactory() { return unableFindValueFactory(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_PARAM abstractFromNonAbstract(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1398079691, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.abstractFromNonAbstract", arrayOfObject, ORBUtilSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM abstractFromNonAbstract(CompletionStatus paramCompletionStatus) { return abstractFromNonAbstract(paramCompletionStatus, null); }
  
  public BAD_PARAM abstractFromNonAbstract(Throwable paramThrowable) { return abstractFromNonAbstract(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_PARAM abstractFromNonAbstract() { return abstractFromNonAbstract(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_PARAM invalidTaggedProfile(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1398079692, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.invalidTaggedProfile", arrayOfObject, ORBUtilSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM invalidTaggedProfile(CompletionStatus paramCompletionStatus) { return invalidTaggedProfile(paramCompletionStatus, null); }
  
  public BAD_PARAM invalidTaggedProfile(Throwable paramThrowable) { return invalidTaggedProfile(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_PARAM invalidTaggedProfile() { return invalidTaggedProfile(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_PARAM objrefFromForeignOrb(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1398079693, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.objrefFromForeignOrb", arrayOfObject, ORBUtilSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM objrefFromForeignOrb(CompletionStatus paramCompletionStatus) { return objrefFromForeignOrb(paramCompletionStatus, null); }
  
  public BAD_PARAM objrefFromForeignOrb(Throwable paramThrowable) { return objrefFromForeignOrb(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_PARAM objrefFromForeignOrb() { return objrefFromForeignOrb(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_PARAM localObjectNotAllowed(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1398079694, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = null;
      doLog(Level.FINE, "ORBUTIL.localObjectNotAllowed", arrayOfObject, ORBUtilSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM localObjectNotAllowed(CompletionStatus paramCompletionStatus) { return localObjectNotAllowed(paramCompletionStatus, null); }
  
  public BAD_PARAM localObjectNotAllowed(Throwable paramThrowable) { return localObjectNotAllowed(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_PARAM localObjectNotAllowed() { return localObjectNotAllowed(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_PARAM nullObjectReference(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1398079695, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.nullObjectReference", arrayOfObject, ORBUtilSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM nullObjectReference(CompletionStatus paramCompletionStatus) { return nullObjectReference(paramCompletionStatus, null); }
  
  public BAD_PARAM nullObjectReference(Throwable paramThrowable) { return nullObjectReference(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_PARAM nullObjectReference() { return nullObjectReference(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_PARAM couldNotLoadClass(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1398079696, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.WARNING, "ORBUTIL.couldNotLoadClass", arrayOfObject, ORBUtilSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM couldNotLoadClass(CompletionStatus paramCompletionStatus, Object paramObject) { return couldNotLoadClass(paramCompletionStatus, null, paramObject); }
  
  public BAD_PARAM couldNotLoadClass(Throwable paramThrowable, Object paramObject) { return couldNotLoadClass(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public BAD_PARAM couldNotLoadClass(Object paramObject) { return couldNotLoadClass(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public BAD_PARAM badUrl(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1398079697, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.WARNING, "ORBUTIL.badUrl", arrayOfObject, ORBUtilSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM badUrl(CompletionStatus paramCompletionStatus, Object paramObject) { return badUrl(paramCompletionStatus, null, paramObject); }
  
  public BAD_PARAM badUrl(Throwable paramThrowable, Object paramObject) { return badUrl(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public BAD_PARAM badUrl(Object paramObject) { return badUrl(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public BAD_PARAM fieldNotFound(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1398079698, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.WARNING, "ORBUTIL.fieldNotFound", arrayOfObject, ORBUtilSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM fieldNotFound(CompletionStatus paramCompletionStatus, Object paramObject) { return fieldNotFound(paramCompletionStatus, null, paramObject); }
  
  public BAD_PARAM fieldNotFound(Throwable paramThrowable, Object paramObject) { return fieldNotFound(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public BAD_PARAM fieldNotFound(Object paramObject) { return fieldNotFound(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public BAD_PARAM errorSettingField(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject1, Object paramObject2) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1398079699, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[2];
      arrayOfObject[0] = paramObject1;
      arrayOfObject[1] = paramObject2;
      doLog(Level.WARNING, "ORBUTIL.errorSettingField", arrayOfObject, ORBUtilSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM errorSettingField(CompletionStatus paramCompletionStatus, Object paramObject1, Object paramObject2) { return errorSettingField(paramCompletionStatus, null, paramObject1, paramObject2); }
  
  public BAD_PARAM errorSettingField(Throwable paramThrowable, Object paramObject1, Object paramObject2) { return errorSettingField(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject1, paramObject2); }
  
  public BAD_PARAM errorSettingField(Object paramObject1, Object paramObject2) { return errorSettingField(CompletionStatus.COMPLETED_NO, null, paramObject1, paramObject2); }
  
  public BAD_PARAM boundsErrorInDiiRequest(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1398079700, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.boundsErrorInDiiRequest", arrayOfObject, ORBUtilSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM boundsErrorInDiiRequest(CompletionStatus paramCompletionStatus) { return boundsErrorInDiiRequest(paramCompletionStatus, null); }
  
  public BAD_PARAM boundsErrorInDiiRequest(Throwable paramThrowable) { return boundsErrorInDiiRequest(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_PARAM boundsErrorInDiiRequest() { return boundsErrorInDiiRequest(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_PARAM persistentServerInitError(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1398079701, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.persistentServerInitError", arrayOfObject, ORBUtilSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM persistentServerInitError(CompletionStatus paramCompletionStatus) { return persistentServerInitError(paramCompletionStatus, null); }
  
  public BAD_PARAM persistentServerInitError(Throwable paramThrowable) { return persistentServerInitError(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_PARAM persistentServerInitError() { return persistentServerInitError(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_PARAM couldNotCreateArray(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject1, Object paramObject2, Object paramObject3) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1398079702, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[3];
      arrayOfObject[0] = paramObject1;
      arrayOfObject[1] = paramObject2;
      arrayOfObject[2] = paramObject3;
      doLog(Level.WARNING, "ORBUTIL.couldNotCreateArray", arrayOfObject, ORBUtilSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM couldNotCreateArray(CompletionStatus paramCompletionStatus, Object paramObject1, Object paramObject2, Object paramObject3) { return couldNotCreateArray(paramCompletionStatus, null, paramObject1, paramObject2, paramObject3); }
  
  public BAD_PARAM couldNotCreateArray(Throwable paramThrowable, Object paramObject1, Object paramObject2, Object paramObject3) { return couldNotCreateArray(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject1, paramObject2, paramObject3); }
  
  public BAD_PARAM couldNotCreateArray(Object paramObject1, Object paramObject2, Object paramObject3) { return couldNotCreateArray(CompletionStatus.COMPLETED_NO, null, paramObject1, paramObject2, paramObject3); }
  
  public BAD_PARAM couldNotSetArray(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1398079703, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[5];
      arrayOfObject[0] = paramObject1;
      arrayOfObject[1] = paramObject2;
      arrayOfObject[2] = paramObject3;
      arrayOfObject[3] = paramObject4;
      arrayOfObject[4] = paramObject5;
      doLog(Level.WARNING, "ORBUTIL.couldNotSetArray", arrayOfObject, ORBUtilSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM couldNotSetArray(CompletionStatus paramCompletionStatus, Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5) { return couldNotSetArray(paramCompletionStatus, null, paramObject1, paramObject2, paramObject3, paramObject4, paramObject5); }
  
  public BAD_PARAM couldNotSetArray(Throwable paramThrowable, Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5) { return couldNotSetArray(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject1, paramObject2, paramObject3, paramObject4, paramObject5); }
  
  public BAD_PARAM couldNotSetArray(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5) { return couldNotSetArray(CompletionStatus.COMPLETED_NO, null, paramObject1, paramObject2, paramObject3, paramObject4, paramObject5); }
  
  public BAD_PARAM illegalBootstrapOperation(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1398079704, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.WARNING, "ORBUTIL.illegalBootstrapOperation", arrayOfObject, ORBUtilSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM illegalBootstrapOperation(CompletionStatus paramCompletionStatus, Object paramObject) { return illegalBootstrapOperation(paramCompletionStatus, null, paramObject); }
  
  public BAD_PARAM illegalBootstrapOperation(Throwable paramThrowable, Object paramObject) { return illegalBootstrapOperation(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public BAD_PARAM illegalBootstrapOperation(Object paramObject) { return illegalBootstrapOperation(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public BAD_PARAM bootstrapRuntimeException(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1398079705, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.bootstrapRuntimeException", arrayOfObject, ORBUtilSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM bootstrapRuntimeException(CompletionStatus paramCompletionStatus) { return bootstrapRuntimeException(paramCompletionStatus, null); }
  
  public BAD_PARAM bootstrapRuntimeException(Throwable paramThrowable) { return bootstrapRuntimeException(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_PARAM bootstrapRuntimeException() { return bootstrapRuntimeException(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_PARAM bootstrapException(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1398079706, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.bootstrapException", arrayOfObject, ORBUtilSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM bootstrapException(CompletionStatus paramCompletionStatus) { return bootstrapException(paramCompletionStatus, null); }
  
  public BAD_PARAM bootstrapException(Throwable paramThrowable) { return bootstrapException(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_PARAM bootstrapException() { return bootstrapException(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_PARAM stringExpected(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1398079707, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.stringExpected", arrayOfObject, ORBUtilSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM stringExpected(CompletionStatus paramCompletionStatus) { return stringExpected(paramCompletionStatus, null); }
  
  public BAD_PARAM stringExpected(Throwable paramThrowable) { return stringExpected(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_PARAM stringExpected() { return stringExpected(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_PARAM invalidTypecodeKind(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1398079708, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.WARNING, "ORBUTIL.invalidTypecodeKind", arrayOfObject, ORBUtilSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM invalidTypecodeKind(CompletionStatus paramCompletionStatus, Object paramObject) { return invalidTypecodeKind(paramCompletionStatus, null, paramObject); }
  
  public BAD_PARAM invalidTypecodeKind(Throwable paramThrowable, Object paramObject) { return invalidTypecodeKind(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public BAD_PARAM invalidTypecodeKind(Object paramObject) { return invalidTypecodeKind(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public BAD_PARAM socketFactoryAndContactInfoListAtSameTime(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1398079709, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.socketFactoryAndContactInfoListAtSameTime", arrayOfObject, ORBUtilSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM socketFactoryAndContactInfoListAtSameTime(CompletionStatus paramCompletionStatus) { return socketFactoryAndContactInfoListAtSameTime(paramCompletionStatus, null); }
  
  public BAD_PARAM socketFactoryAndContactInfoListAtSameTime(Throwable paramThrowable) { return socketFactoryAndContactInfoListAtSameTime(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_PARAM socketFactoryAndContactInfoListAtSameTime() { return socketFactoryAndContactInfoListAtSameTime(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_PARAM acceptorsAndLegacySocketFactoryAtSameTime(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1398079710, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.acceptorsAndLegacySocketFactoryAtSameTime", arrayOfObject, ORBUtilSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM acceptorsAndLegacySocketFactoryAtSameTime(CompletionStatus paramCompletionStatus) { return acceptorsAndLegacySocketFactoryAtSameTime(paramCompletionStatus, null); }
  
  public BAD_PARAM acceptorsAndLegacySocketFactoryAtSameTime(Throwable paramThrowable) { return acceptorsAndLegacySocketFactoryAtSameTime(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_PARAM acceptorsAndLegacySocketFactoryAtSameTime() { return acceptorsAndLegacySocketFactoryAtSameTime(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_PARAM badOrbForServant(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1398079711, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.badOrbForServant", arrayOfObject, ORBUtilSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM badOrbForServant(CompletionStatus paramCompletionStatus) { return badOrbForServant(paramCompletionStatus, null); }
  
  public BAD_PARAM badOrbForServant(Throwable paramThrowable) { return badOrbForServant(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_PARAM badOrbForServant() { return badOrbForServant(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_PARAM invalidRequestPartitioningPolicyValue(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject1, Object paramObject2, Object paramObject3) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1398079712, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[3];
      arrayOfObject[0] = paramObject1;
      arrayOfObject[1] = paramObject2;
      arrayOfObject[2] = paramObject3;
      doLog(Level.WARNING, "ORBUTIL.invalidRequestPartitioningPolicyValue", arrayOfObject, ORBUtilSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM invalidRequestPartitioningPolicyValue(CompletionStatus paramCompletionStatus, Object paramObject1, Object paramObject2, Object paramObject3) { return invalidRequestPartitioningPolicyValue(paramCompletionStatus, null, paramObject1, paramObject2, paramObject3); }
  
  public BAD_PARAM invalidRequestPartitioningPolicyValue(Throwable paramThrowable, Object paramObject1, Object paramObject2, Object paramObject3) { return invalidRequestPartitioningPolicyValue(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject1, paramObject2, paramObject3); }
  
  public BAD_PARAM invalidRequestPartitioningPolicyValue(Object paramObject1, Object paramObject2, Object paramObject3) { return invalidRequestPartitioningPolicyValue(CompletionStatus.COMPLETED_NO, null, paramObject1, paramObject2, paramObject3); }
  
  public BAD_PARAM invalidRequestPartitioningComponentValue(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject1, Object paramObject2, Object paramObject3) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1398079713, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[3];
      arrayOfObject[0] = paramObject1;
      arrayOfObject[1] = paramObject2;
      arrayOfObject[2] = paramObject3;
      doLog(Level.WARNING, "ORBUTIL.invalidRequestPartitioningComponentValue", arrayOfObject, ORBUtilSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM invalidRequestPartitioningComponentValue(CompletionStatus paramCompletionStatus, Object paramObject1, Object paramObject2, Object paramObject3) { return invalidRequestPartitioningComponentValue(paramCompletionStatus, null, paramObject1, paramObject2, paramObject3); }
  
  public BAD_PARAM invalidRequestPartitioningComponentValue(Throwable paramThrowable, Object paramObject1, Object paramObject2, Object paramObject3) { return invalidRequestPartitioningComponentValue(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject1, paramObject2, paramObject3); }
  
  public BAD_PARAM invalidRequestPartitioningComponentValue(Object paramObject1, Object paramObject2, Object paramObject3) { return invalidRequestPartitioningComponentValue(CompletionStatus.COMPLETED_NO, null, paramObject1, paramObject2, paramObject3); }
  
  public BAD_PARAM invalidRequestPartitioningId(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject1, Object paramObject2, Object paramObject3) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1398079714, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[3];
      arrayOfObject[0] = paramObject1;
      arrayOfObject[1] = paramObject2;
      arrayOfObject[2] = paramObject3;
      doLog(Level.WARNING, "ORBUTIL.invalidRequestPartitioningId", arrayOfObject, ORBUtilSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM invalidRequestPartitioningId(CompletionStatus paramCompletionStatus, Object paramObject1, Object paramObject2, Object paramObject3) { return invalidRequestPartitioningId(paramCompletionStatus, null, paramObject1, paramObject2, paramObject3); }
  
  public BAD_PARAM invalidRequestPartitioningId(Throwable paramThrowable, Object paramObject1, Object paramObject2, Object paramObject3) { return invalidRequestPartitioningId(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject1, paramObject2, paramObject3); }
  
  public BAD_PARAM invalidRequestPartitioningId(Object paramObject1, Object paramObject2, Object paramObject3) { return invalidRequestPartitioningId(CompletionStatus.COMPLETED_NO, null, paramObject1, paramObject2, paramObject3); }
  
  public BAD_PARAM errorInSettingDynamicStubFactoryFactory(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1398079715, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.FINE, "ORBUTIL.errorInSettingDynamicStubFactoryFactory", arrayOfObject, ORBUtilSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM errorInSettingDynamicStubFactoryFactory(CompletionStatus paramCompletionStatus, Object paramObject) { return errorInSettingDynamicStubFactoryFactory(paramCompletionStatus, null, paramObject); }
  
  public BAD_PARAM errorInSettingDynamicStubFactoryFactory(Throwable paramThrowable, Object paramObject) { return errorInSettingDynamicStubFactoryFactory(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public BAD_PARAM errorInSettingDynamicStubFactoryFactory(Object paramObject) { return errorInSettingDynamicStubFactoryFactory(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public BAD_INV_ORDER dsimethodNotcalled(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_INV_ORDER bAD_INV_ORDER = new BAD_INV_ORDER(1398079689, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_INV_ORDER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.dsimethodNotcalled", arrayOfObject, ORBUtilSystemException.class, bAD_INV_ORDER);
    } 
    return bAD_INV_ORDER;
  }
  
  public BAD_INV_ORDER dsimethodNotcalled(CompletionStatus paramCompletionStatus) { return dsimethodNotcalled(paramCompletionStatus, null); }
  
  public BAD_INV_ORDER dsimethodNotcalled(Throwable paramThrowable) { return dsimethodNotcalled(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_INV_ORDER dsimethodNotcalled() { return dsimethodNotcalled(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_INV_ORDER argumentsCalledMultiple(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_INV_ORDER bAD_INV_ORDER = new BAD_INV_ORDER(1398079690, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_INV_ORDER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.argumentsCalledMultiple", arrayOfObject, ORBUtilSystemException.class, bAD_INV_ORDER);
    } 
    return bAD_INV_ORDER;
  }
  
  public BAD_INV_ORDER argumentsCalledMultiple(CompletionStatus paramCompletionStatus) { return argumentsCalledMultiple(paramCompletionStatus, null); }
  
  public BAD_INV_ORDER argumentsCalledMultiple(Throwable paramThrowable) { return argumentsCalledMultiple(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_INV_ORDER argumentsCalledMultiple() { return argumentsCalledMultiple(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_INV_ORDER argumentsCalledAfterException(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_INV_ORDER bAD_INV_ORDER = new BAD_INV_ORDER(1398079691, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_INV_ORDER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.argumentsCalledAfterException", arrayOfObject, ORBUtilSystemException.class, bAD_INV_ORDER);
    } 
    return bAD_INV_ORDER;
  }
  
  public BAD_INV_ORDER argumentsCalledAfterException(CompletionStatus paramCompletionStatus) { return argumentsCalledAfterException(paramCompletionStatus, null); }
  
  public BAD_INV_ORDER argumentsCalledAfterException(Throwable paramThrowable) { return argumentsCalledAfterException(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_INV_ORDER argumentsCalledAfterException() { return argumentsCalledAfterException(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_INV_ORDER argumentsCalledNullArgs(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_INV_ORDER bAD_INV_ORDER = new BAD_INV_ORDER(1398079692, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_INV_ORDER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.argumentsCalledNullArgs", arrayOfObject, ORBUtilSystemException.class, bAD_INV_ORDER);
    } 
    return bAD_INV_ORDER;
  }
  
  public BAD_INV_ORDER argumentsCalledNullArgs(CompletionStatus paramCompletionStatus) { return argumentsCalledNullArgs(paramCompletionStatus, null); }
  
  public BAD_INV_ORDER argumentsCalledNullArgs(Throwable paramThrowable) { return argumentsCalledNullArgs(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_INV_ORDER argumentsCalledNullArgs() { return argumentsCalledNullArgs(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_INV_ORDER argumentsNotCalled(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_INV_ORDER bAD_INV_ORDER = new BAD_INV_ORDER(1398079693, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_INV_ORDER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = null;
      doLog(Level.FINE, "ORBUTIL.argumentsNotCalled", arrayOfObject, ORBUtilSystemException.class, bAD_INV_ORDER);
    } 
    return bAD_INV_ORDER;
  }
  
  public BAD_INV_ORDER argumentsNotCalled(CompletionStatus paramCompletionStatus) { return argumentsNotCalled(paramCompletionStatus, null); }
  
  public BAD_INV_ORDER argumentsNotCalled(Throwable paramThrowable) { return argumentsNotCalled(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_INV_ORDER argumentsNotCalled() { return argumentsNotCalled(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_INV_ORDER setResultCalledMultiple(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_INV_ORDER bAD_INV_ORDER = new BAD_INV_ORDER(1398079694, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_INV_ORDER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.setResultCalledMultiple", arrayOfObject, ORBUtilSystemException.class, bAD_INV_ORDER);
    } 
    return bAD_INV_ORDER;
  }
  
  public BAD_INV_ORDER setResultCalledMultiple(CompletionStatus paramCompletionStatus) { return setResultCalledMultiple(paramCompletionStatus, null); }
  
  public BAD_INV_ORDER setResultCalledMultiple(Throwable paramThrowable) { return setResultCalledMultiple(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_INV_ORDER setResultCalledMultiple() { return setResultCalledMultiple(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_INV_ORDER setResultAfterException(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_INV_ORDER bAD_INV_ORDER = new BAD_INV_ORDER(1398079695, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_INV_ORDER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = null;
      doLog(Level.FINE, "ORBUTIL.setResultAfterException", arrayOfObject, ORBUtilSystemException.class, bAD_INV_ORDER);
    } 
    return bAD_INV_ORDER;
  }
  
  public BAD_INV_ORDER setResultAfterException(CompletionStatus paramCompletionStatus) { return setResultAfterException(paramCompletionStatus, null); }
  
  public BAD_INV_ORDER setResultAfterException(Throwable paramThrowable) { return setResultAfterException(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_INV_ORDER setResultAfterException() { return setResultAfterException(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_INV_ORDER setResultCalledNullArgs(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_INV_ORDER bAD_INV_ORDER = new BAD_INV_ORDER(1398079696, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_INV_ORDER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.setResultCalledNullArgs", arrayOfObject, ORBUtilSystemException.class, bAD_INV_ORDER);
    } 
    return bAD_INV_ORDER;
  }
  
  public BAD_INV_ORDER setResultCalledNullArgs(CompletionStatus paramCompletionStatus) { return setResultCalledNullArgs(paramCompletionStatus, null); }
  
  public BAD_INV_ORDER setResultCalledNullArgs(Throwable paramThrowable) { return setResultCalledNullArgs(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_INV_ORDER setResultCalledNullArgs() { return setResultCalledNullArgs(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_TYPECODE badRemoteTypecode(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_TYPECODE bAD_TYPECODE = new BAD_TYPECODE(1398079689, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_TYPECODE.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.badRemoteTypecode", arrayOfObject, ORBUtilSystemException.class, bAD_TYPECODE);
    } 
    return bAD_TYPECODE;
  }
  
  public BAD_TYPECODE badRemoteTypecode(CompletionStatus paramCompletionStatus) { return badRemoteTypecode(paramCompletionStatus, null); }
  
  public BAD_TYPECODE badRemoteTypecode(Throwable paramThrowable) { return badRemoteTypecode(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_TYPECODE badRemoteTypecode() { return badRemoteTypecode(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_TYPECODE unresolvedRecursiveTypecode(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_TYPECODE bAD_TYPECODE = new BAD_TYPECODE(1398079690, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_TYPECODE.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.unresolvedRecursiveTypecode", arrayOfObject, ORBUtilSystemException.class, bAD_TYPECODE);
    } 
    return bAD_TYPECODE;
  }
  
  public BAD_TYPECODE unresolvedRecursiveTypecode(CompletionStatus paramCompletionStatus) { return unresolvedRecursiveTypecode(paramCompletionStatus, null); }
  
  public BAD_TYPECODE unresolvedRecursiveTypecode(Throwable paramThrowable) { return unresolvedRecursiveTypecode(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_TYPECODE unresolvedRecursiveTypecode() { return unresolvedRecursiveTypecode(CompletionStatus.COMPLETED_NO, null); }
  
  public COMM_FAILURE connectFailure(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject1, Object paramObject2, Object paramObject3) {
    COMM_FAILURE cOMM_FAILURE = new COMM_FAILURE(1398079689, paramCompletionStatus);
    if (paramThrowable != null)
      cOMM_FAILURE.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[3];
      arrayOfObject[0] = paramObject1;
      arrayOfObject[1] = paramObject2;
      arrayOfObject[2] = paramObject3;
      doLog(Level.WARNING, "ORBUTIL.connectFailure", arrayOfObject, ORBUtilSystemException.class, cOMM_FAILURE);
    } 
    return cOMM_FAILURE;
  }
  
  public COMM_FAILURE connectFailure(CompletionStatus paramCompletionStatus, Object paramObject1, Object paramObject2, Object paramObject3) { return connectFailure(paramCompletionStatus, null, paramObject1, paramObject2, paramObject3); }
  
  public COMM_FAILURE connectFailure(Throwable paramThrowable, Object paramObject1, Object paramObject2, Object paramObject3) { return connectFailure(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject1, paramObject2, paramObject3); }
  
  public COMM_FAILURE connectFailure(Object paramObject1, Object paramObject2, Object paramObject3) { return connectFailure(CompletionStatus.COMPLETED_NO, null, paramObject1, paramObject2, paramObject3); }
  
  public COMM_FAILURE connectionCloseRebind(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    COMM_FAILURE cOMM_FAILURE = new COMM_FAILURE(1398079690, paramCompletionStatus);
    if (paramThrowable != null)
      cOMM_FAILURE.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.connectionCloseRebind", arrayOfObject, ORBUtilSystemException.class, cOMM_FAILURE);
    } 
    return cOMM_FAILURE;
  }
  
  public COMM_FAILURE connectionCloseRebind(CompletionStatus paramCompletionStatus) { return connectionCloseRebind(paramCompletionStatus, null); }
  
  public COMM_FAILURE connectionCloseRebind(Throwable paramThrowable) { return connectionCloseRebind(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public COMM_FAILURE connectionCloseRebind() { return connectionCloseRebind(CompletionStatus.COMPLETED_NO, null); }
  
  public COMM_FAILURE writeErrorSend(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    COMM_FAILURE cOMM_FAILURE = new COMM_FAILURE(1398079691, paramCompletionStatus);
    if (paramThrowable != null)
      cOMM_FAILURE.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = null;
      doLog(Level.FINE, "ORBUTIL.writeErrorSend", arrayOfObject, ORBUtilSystemException.class, cOMM_FAILURE);
    } 
    return cOMM_FAILURE;
  }
  
  public COMM_FAILURE writeErrorSend(CompletionStatus paramCompletionStatus) { return writeErrorSend(paramCompletionStatus, null); }
  
  public COMM_FAILURE writeErrorSend(Throwable paramThrowable) { return writeErrorSend(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public COMM_FAILURE writeErrorSend() { return writeErrorSend(CompletionStatus.COMPLETED_NO, null); }
  
  public COMM_FAILURE getPropertiesError(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    COMM_FAILURE cOMM_FAILURE = new COMM_FAILURE(1398079692, paramCompletionStatus);
    if (paramThrowable != null)
      cOMM_FAILURE.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.getPropertiesError", arrayOfObject, ORBUtilSystemException.class, cOMM_FAILURE);
    } 
    return cOMM_FAILURE;
  }
  
  public COMM_FAILURE getPropertiesError(CompletionStatus paramCompletionStatus) { return getPropertiesError(paramCompletionStatus, null); }
  
  public COMM_FAILURE getPropertiesError(Throwable paramThrowable) { return getPropertiesError(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public COMM_FAILURE getPropertiesError() { return getPropertiesError(CompletionStatus.COMPLETED_NO, null); }
  
  public COMM_FAILURE bootstrapServerNotAvail(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    COMM_FAILURE cOMM_FAILURE = new COMM_FAILURE(1398079693, paramCompletionStatus);
    if (paramThrowable != null)
      cOMM_FAILURE.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.bootstrapServerNotAvail", arrayOfObject, ORBUtilSystemException.class, cOMM_FAILURE);
    } 
    return cOMM_FAILURE;
  }
  
  public COMM_FAILURE bootstrapServerNotAvail(CompletionStatus paramCompletionStatus) { return bootstrapServerNotAvail(paramCompletionStatus, null); }
  
  public COMM_FAILURE bootstrapServerNotAvail(Throwable paramThrowable) { return bootstrapServerNotAvail(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public COMM_FAILURE bootstrapServerNotAvail() { return bootstrapServerNotAvail(CompletionStatus.COMPLETED_NO, null); }
  
  public COMM_FAILURE invokeError(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    COMM_FAILURE cOMM_FAILURE = new COMM_FAILURE(1398079694, paramCompletionStatus);
    if (paramThrowable != null)
      cOMM_FAILURE.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.invokeError", arrayOfObject, ORBUtilSystemException.class, cOMM_FAILURE);
    } 
    return cOMM_FAILURE;
  }
  
  public COMM_FAILURE invokeError(CompletionStatus paramCompletionStatus) { return invokeError(paramCompletionStatus, null); }
  
  public COMM_FAILURE invokeError(Throwable paramThrowable) { return invokeError(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public COMM_FAILURE invokeError() { return invokeError(CompletionStatus.COMPLETED_NO, null); }
  
  public COMM_FAILURE defaultCreateServerSocketGivenNonIiopClearText(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    COMM_FAILURE cOMM_FAILURE = new COMM_FAILURE(1398079695, paramCompletionStatus);
    if (paramThrowable != null)
      cOMM_FAILURE.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.WARNING, "ORBUTIL.defaultCreateServerSocketGivenNonIiopClearText", arrayOfObject, ORBUtilSystemException.class, cOMM_FAILURE);
    } 
    return cOMM_FAILURE;
  }
  
  public COMM_FAILURE defaultCreateServerSocketGivenNonIiopClearText(CompletionStatus paramCompletionStatus, Object paramObject) { return defaultCreateServerSocketGivenNonIiopClearText(paramCompletionStatus, null, paramObject); }
  
  public COMM_FAILURE defaultCreateServerSocketGivenNonIiopClearText(Throwable paramThrowable, Object paramObject) { return defaultCreateServerSocketGivenNonIiopClearText(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public COMM_FAILURE defaultCreateServerSocketGivenNonIiopClearText(Object paramObject) { return defaultCreateServerSocketGivenNonIiopClearText(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public COMM_FAILURE connectionAbort(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    COMM_FAILURE cOMM_FAILURE = new COMM_FAILURE(1398079696, paramCompletionStatus);
    if (paramThrowable != null)
      cOMM_FAILURE.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = null;
      doLog(Level.FINE, "ORBUTIL.connectionAbort", arrayOfObject, ORBUtilSystemException.class, cOMM_FAILURE);
    } 
    return cOMM_FAILURE;
  }
  
  public COMM_FAILURE connectionAbort(CompletionStatus paramCompletionStatus) { return connectionAbort(paramCompletionStatus, null); }
  
  public COMM_FAILURE connectionAbort(Throwable paramThrowable) { return connectionAbort(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public COMM_FAILURE connectionAbort() { return connectionAbort(CompletionStatus.COMPLETED_NO, null); }
  
  public COMM_FAILURE connectionRebind(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    COMM_FAILURE cOMM_FAILURE = new COMM_FAILURE(1398079697, paramCompletionStatus);
    if (paramThrowable != null)
      cOMM_FAILURE.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = null;
      doLog(Level.FINE, "ORBUTIL.connectionRebind", arrayOfObject, ORBUtilSystemException.class, cOMM_FAILURE);
    } 
    return cOMM_FAILURE;
  }
  
  public COMM_FAILURE connectionRebind(CompletionStatus paramCompletionStatus) { return connectionRebind(paramCompletionStatus, null); }
  
  public COMM_FAILURE connectionRebind(Throwable paramThrowable) { return connectionRebind(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public COMM_FAILURE connectionRebind() { return connectionRebind(CompletionStatus.COMPLETED_NO, null); }
  
  public COMM_FAILURE recvMsgError(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    COMM_FAILURE cOMM_FAILURE = new COMM_FAILURE(1398079698, paramCompletionStatus);
    if (paramThrowable != null)
      cOMM_FAILURE.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.recvMsgError", arrayOfObject, ORBUtilSystemException.class, cOMM_FAILURE);
    } 
    return cOMM_FAILURE;
  }
  
  public COMM_FAILURE recvMsgError(CompletionStatus paramCompletionStatus) { return recvMsgError(paramCompletionStatus, null); }
  
  public COMM_FAILURE recvMsgError(Throwable paramThrowable) { return recvMsgError(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public COMM_FAILURE recvMsgError() { return recvMsgError(CompletionStatus.COMPLETED_NO, null); }
  
  public COMM_FAILURE ioexceptionWhenReadingConnection(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    COMM_FAILURE cOMM_FAILURE = new COMM_FAILURE(1398079699, paramCompletionStatus);
    if (paramThrowable != null)
      cOMM_FAILURE.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = null;
      doLog(Level.FINE, "ORBUTIL.ioexceptionWhenReadingConnection", arrayOfObject, ORBUtilSystemException.class, cOMM_FAILURE);
    } 
    return cOMM_FAILURE;
  }
  
  public COMM_FAILURE ioexceptionWhenReadingConnection(CompletionStatus paramCompletionStatus) { return ioexceptionWhenReadingConnection(paramCompletionStatus, null); }
  
  public COMM_FAILURE ioexceptionWhenReadingConnection(Throwable paramThrowable) { return ioexceptionWhenReadingConnection(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public COMM_FAILURE ioexceptionWhenReadingConnection() { return ioexceptionWhenReadingConnection(CompletionStatus.COMPLETED_NO, null); }
  
  public COMM_FAILURE selectionKeyInvalid(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    COMM_FAILURE cOMM_FAILURE = new COMM_FAILURE(1398079700, paramCompletionStatus);
    if (paramThrowable != null)
      cOMM_FAILURE.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.FINE, "ORBUTIL.selectionKeyInvalid", arrayOfObject, ORBUtilSystemException.class, cOMM_FAILURE);
    } 
    return cOMM_FAILURE;
  }
  
  public COMM_FAILURE selectionKeyInvalid(CompletionStatus paramCompletionStatus, Object paramObject) { return selectionKeyInvalid(paramCompletionStatus, null, paramObject); }
  
  public COMM_FAILURE selectionKeyInvalid(Throwable paramThrowable, Object paramObject) { return selectionKeyInvalid(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public COMM_FAILURE selectionKeyInvalid(Object paramObject) { return selectionKeyInvalid(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public COMM_FAILURE exceptionInAccept(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    COMM_FAILURE cOMM_FAILURE = new COMM_FAILURE(1398079701, paramCompletionStatus);
    if (paramThrowable != null)
      cOMM_FAILURE.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.FINE, "ORBUTIL.exceptionInAccept", arrayOfObject, ORBUtilSystemException.class, cOMM_FAILURE);
    } 
    return cOMM_FAILURE;
  }
  
  public COMM_FAILURE exceptionInAccept(CompletionStatus paramCompletionStatus, Object paramObject) { return exceptionInAccept(paramCompletionStatus, null, paramObject); }
  
  public COMM_FAILURE exceptionInAccept(Throwable paramThrowable, Object paramObject) { return exceptionInAccept(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public COMM_FAILURE exceptionInAccept(Object paramObject) { return exceptionInAccept(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public COMM_FAILURE securityExceptionInAccept(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject1, Object paramObject2) {
    COMM_FAILURE cOMM_FAILURE = new COMM_FAILURE(1398079702, paramCompletionStatus);
    if (paramThrowable != null)
      cOMM_FAILURE.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = new Object[2];
      arrayOfObject[0] = paramObject1;
      arrayOfObject[1] = paramObject2;
      doLog(Level.FINE, "ORBUTIL.securityExceptionInAccept", arrayOfObject, ORBUtilSystemException.class, cOMM_FAILURE);
    } 
    return cOMM_FAILURE;
  }
  
  public COMM_FAILURE securityExceptionInAccept(CompletionStatus paramCompletionStatus, Object paramObject1, Object paramObject2) { return securityExceptionInAccept(paramCompletionStatus, null, paramObject1, paramObject2); }
  
  public COMM_FAILURE securityExceptionInAccept(Throwable paramThrowable, Object paramObject1, Object paramObject2) { return securityExceptionInAccept(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject1, paramObject2); }
  
  public COMM_FAILURE securityExceptionInAccept(Object paramObject1, Object paramObject2) { return securityExceptionInAccept(CompletionStatus.COMPLETED_NO, null, paramObject1, paramObject2); }
  
  public COMM_FAILURE transportReadTimeoutExceeded(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4) {
    COMM_FAILURE cOMM_FAILURE = new COMM_FAILURE(1398079703, paramCompletionStatus);
    if (paramThrowable != null)
      cOMM_FAILURE.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[4];
      arrayOfObject[0] = paramObject1;
      arrayOfObject[1] = paramObject2;
      arrayOfObject[2] = paramObject3;
      arrayOfObject[3] = paramObject4;
      doLog(Level.WARNING, "ORBUTIL.transportReadTimeoutExceeded", arrayOfObject, ORBUtilSystemException.class, cOMM_FAILURE);
    } 
    return cOMM_FAILURE;
  }
  
  public COMM_FAILURE transportReadTimeoutExceeded(CompletionStatus paramCompletionStatus, Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4) { return transportReadTimeoutExceeded(paramCompletionStatus, null, paramObject1, paramObject2, paramObject3, paramObject4); }
  
  public COMM_FAILURE transportReadTimeoutExceeded(Throwable paramThrowable, Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4) { return transportReadTimeoutExceeded(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject1, paramObject2, paramObject3, paramObject4); }
  
  public COMM_FAILURE transportReadTimeoutExceeded(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4) { return transportReadTimeoutExceeded(CompletionStatus.COMPLETED_NO, null, paramObject1, paramObject2, paramObject3, paramObject4); }
  
  public COMM_FAILURE createListenerFailed(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    COMM_FAILURE cOMM_FAILURE = new COMM_FAILURE(1398079704, paramCompletionStatus);
    if (paramThrowable != null)
      cOMM_FAILURE.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.SEVERE)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.SEVERE, "ORBUTIL.createListenerFailed", arrayOfObject, ORBUtilSystemException.class, cOMM_FAILURE);
    } 
    return cOMM_FAILURE;
  }
  
  public COMM_FAILURE createListenerFailed(CompletionStatus paramCompletionStatus, Object paramObject) { return createListenerFailed(paramCompletionStatus, null, paramObject); }
  
  public COMM_FAILURE createListenerFailed(Throwable paramThrowable, Object paramObject) { return createListenerFailed(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public COMM_FAILURE createListenerFailed(Object paramObject) { return createListenerFailed(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public COMM_FAILURE bufferReadManagerTimeout(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    COMM_FAILURE cOMM_FAILURE = new COMM_FAILURE(1398079705, paramCompletionStatus);
    if (paramThrowable != null)
      cOMM_FAILURE.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.bufferReadManagerTimeout", arrayOfObject, ORBUtilSystemException.class, cOMM_FAILURE);
    } 
    return cOMM_FAILURE;
  }
  
  public COMM_FAILURE bufferReadManagerTimeout(CompletionStatus paramCompletionStatus) { return bufferReadManagerTimeout(paramCompletionStatus, null); }
  
  public COMM_FAILURE bufferReadManagerTimeout(Throwable paramThrowable) { return bufferReadManagerTimeout(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public COMM_FAILURE bufferReadManagerTimeout() { return bufferReadManagerTimeout(CompletionStatus.COMPLETED_NO, null); }
  
  public DATA_CONVERSION badStringifiedIorLen(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    DATA_CONVERSION dATA_CONVERSION = new DATA_CONVERSION(1398079689, paramCompletionStatus);
    if (paramThrowable != null)
      dATA_CONVERSION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.badStringifiedIorLen", arrayOfObject, ORBUtilSystemException.class, dATA_CONVERSION);
    } 
    return dATA_CONVERSION;
  }
  
  public DATA_CONVERSION badStringifiedIorLen(CompletionStatus paramCompletionStatus) { return badStringifiedIorLen(paramCompletionStatus, null); }
  
  public DATA_CONVERSION badStringifiedIorLen(Throwable paramThrowable) { return badStringifiedIorLen(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public DATA_CONVERSION badStringifiedIorLen() { return badStringifiedIorLen(CompletionStatus.COMPLETED_NO, null); }
  
  public DATA_CONVERSION badStringifiedIor(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    DATA_CONVERSION dATA_CONVERSION = new DATA_CONVERSION(1398079690, paramCompletionStatus);
    if (paramThrowable != null)
      dATA_CONVERSION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.badStringifiedIor", arrayOfObject, ORBUtilSystemException.class, dATA_CONVERSION);
    } 
    return dATA_CONVERSION;
  }
  
  public DATA_CONVERSION badStringifiedIor(CompletionStatus paramCompletionStatus) { return badStringifiedIor(paramCompletionStatus, null); }
  
  public DATA_CONVERSION badStringifiedIor(Throwable paramThrowable) { return badStringifiedIor(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public DATA_CONVERSION badStringifiedIor() { return badStringifiedIor(CompletionStatus.COMPLETED_NO, null); }
  
  public DATA_CONVERSION badModifier(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    DATA_CONVERSION dATA_CONVERSION = new DATA_CONVERSION(1398079691, paramCompletionStatus);
    if (paramThrowable != null)
      dATA_CONVERSION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.badModifier", arrayOfObject, ORBUtilSystemException.class, dATA_CONVERSION);
    } 
    return dATA_CONVERSION;
  }
  
  public DATA_CONVERSION badModifier(CompletionStatus paramCompletionStatus) { return badModifier(paramCompletionStatus, null); }
  
  public DATA_CONVERSION badModifier(Throwable paramThrowable) { return badModifier(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public DATA_CONVERSION badModifier() { return badModifier(CompletionStatus.COMPLETED_NO, null); }
  
  public DATA_CONVERSION codesetIncompatible(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    DATA_CONVERSION dATA_CONVERSION = new DATA_CONVERSION(1398079692, paramCompletionStatus);
    if (paramThrowable != null)
      dATA_CONVERSION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.codesetIncompatible", arrayOfObject, ORBUtilSystemException.class, dATA_CONVERSION);
    } 
    return dATA_CONVERSION;
  }
  
  public DATA_CONVERSION codesetIncompatible(CompletionStatus paramCompletionStatus) { return codesetIncompatible(paramCompletionStatus, null); }
  
  public DATA_CONVERSION codesetIncompatible(Throwable paramThrowable) { return codesetIncompatible(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public DATA_CONVERSION codesetIncompatible() { return codesetIncompatible(CompletionStatus.COMPLETED_NO, null); }
  
  public DATA_CONVERSION badHexDigit(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    DATA_CONVERSION dATA_CONVERSION = new DATA_CONVERSION(1398079693, paramCompletionStatus);
    if (paramThrowable != null)
      dATA_CONVERSION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.badHexDigit", arrayOfObject, ORBUtilSystemException.class, dATA_CONVERSION);
    } 
    return dATA_CONVERSION;
  }
  
  public DATA_CONVERSION badHexDigit(CompletionStatus paramCompletionStatus) { return badHexDigit(paramCompletionStatus, null); }
  
  public DATA_CONVERSION badHexDigit(Throwable paramThrowable) { return badHexDigit(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public DATA_CONVERSION badHexDigit() { return badHexDigit(CompletionStatus.COMPLETED_NO, null); }
  
  public DATA_CONVERSION badUnicodePair(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    DATA_CONVERSION dATA_CONVERSION = new DATA_CONVERSION(1398079694, paramCompletionStatus);
    if (paramThrowable != null)
      dATA_CONVERSION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.badUnicodePair", arrayOfObject, ORBUtilSystemException.class, dATA_CONVERSION);
    } 
    return dATA_CONVERSION;
  }
  
  public DATA_CONVERSION badUnicodePair(CompletionStatus paramCompletionStatus) { return badUnicodePair(paramCompletionStatus, null); }
  
  public DATA_CONVERSION badUnicodePair(Throwable paramThrowable) { return badUnicodePair(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public DATA_CONVERSION badUnicodePair() { return badUnicodePair(CompletionStatus.COMPLETED_NO, null); }
  
  public DATA_CONVERSION btcResultMoreThanOneChar(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    DATA_CONVERSION dATA_CONVERSION = new DATA_CONVERSION(1398079695, paramCompletionStatus);
    if (paramThrowable != null)
      dATA_CONVERSION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.btcResultMoreThanOneChar", arrayOfObject, ORBUtilSystemException.class, dATA_CONVERSION);
    } 
    return dATA_CONVERSION;
  }
  
  public DATA_CONVERSION btcResultMoreThanOneChar(CompletionStatus paramCompletionStatus) { return btcResultMoreThanOneChar(paramCompletionStatus, null); }
  
  public DATA_CONVERSION btcResultMoreThanOneChar(Throwable paramThrowable) { return btcResultMoreThanOneChar(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public DATA_CONVERSION btcResultMoreThanOneChar() { return btcResultMoreThanOneChar(CompletionStatus.COMPLETED_NO, null); }
  
  public DATA_CONVERSION badCodesetsFromClient(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    DATA_CONVERSION dATA_CONVERSION = new DATA_CONVERSION(1398079696, paramCompletionStatus);
    if (paramThrowable != null)
      dATA_CONVERSION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.badCodesetsFromClient", arrayOfObject, ORBUtilSystemException.class, dATA_CONVERSION);
    } 
    return dATA_CONVERSION;
  }
  
  public DATA_CONVERSION badCodesetsFromClient(CompletionStatus paramCompletionStatus) { return badCodesetsFromClient(paramCompletionStatus, null); }
  
  public DATA_CONVERSION badCodesetsFromClient(Throwable paramThrowable) { return badCodesetsFromClient(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public DATA_CONVERSION badCodesetsFromClient() { return badCodesetsFromClient(CompletionStatus.COMPLETED_NO, null); }
  
  public DATA_CONVERSION invalidSingleCharCtb(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    DATA_CONVERSION dATA_CONVERSION = new DATA_CONVERSION(1398079697, paramCompletionStatus);
    if (paramThrowable != null)
      dATA_CONVERSION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.invalidSingleCharCtb", arrayOfObject, ORBUtilSystemException.class, dATA_CONVERSION);
    } 
    return dATA_CONVERSION;
  }
  
  public DATA_CONVERSION invalidSingleCharCtb(CompletionStatus paramCompletionStatus) { return invalidSingleCharCtb(paramCompletionStatus, null); }
  
  public DATA_CONVERSION invalidSingleCharCtb(Throwable paramThrowable) { return invalidSingleCharCtb(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public DATA_CONVERSION invalidSingleCharCtb() { return invalidSingleCharCtb(CompletionStatus.COMPLETED_NO, null); }
  
  public DATA_CONVERSION badGiop11Ctb(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    DATA_CONVERSION dATA_CONVERSION = new DATA_CONVERSION(1398079698, paramCompletionStatus);
    if (paramThrowable != null)
      dATA_CONVERSION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.badGiop11Ctb", arrayOfObject, ORBUtilSystemException.class, dATA_CONVERSION);
    } 
    return dATA_CONVERSION;
  }
  
  public DATA_CONVERSION badGiop11Ctb(CompletionStatus paramCompletionStatus) { return badGiop11Ctb(paramCompletionStatus, null); }
  
  public DATA_CONVERSION badGiop11Ctb(Throwable paramThrowable) { return badGiop11Ctb(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public DATA_CONVERSION badGiop11Ctb() { return badGiop11Ctb(CompletionStatus.COMPLETED_NO, null); }
  
  public DATA_CONVERSION badSequenceBounds(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject1, Object paramObject2) {
    DATA_CONVERSION dATA_CONVERSION = new DATA_CONVERSION(1398079700, paramCompletionStatus);
    if (paramThrowable != null)
      dATA_CONVERSION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[2];
      arrayOfObject[0] = paramObject1;
      arrayOfObject[1] = paramObject2;
      doLog(Level.WARNING, "ORBUTIL.badSequenceBounds", arrayOfObject, ORBUtilSystemException.class, dATA_CONVERSION);
    } 
    return dATA_CONVERSION;
  }
  
  public DATA_CONVERSION badSequenceBounds(CompletionStatus paramCompletionStatus, Object paramObject1, Object paramObject2) { return badSequenceBounds(paramCompletionStatus, null, paramObject1, paramObject2); }
  
  public DATA_CONVERSION badSequenceBounds(Throwable paramThrowable, Object paramObject1, Object paramObject2) { return badSequenceBounds(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject1, paramObject2); }
  
  public DATA_CONVERSION badSequenceBounds(Object paramObject1, Object paramObject2) { return badSequenceBounds(CompletionStatus.COMPLETED_NO, null, paramObject1, paramObject2); }
  
  public DATA_CONVERSION illegalSocketFactoryType(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    DATA_CONVERSION dATA_CONVERSION = new DATA_CONVERSION(1398079701, paramCompletionStatus);
    if (paramThrowable != null)
      dATA_CONVERSION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.WARNING, "ORBUTIL.illegalSocketFactoryType", arrayOfObject, ORBUtilSystemException.class, dATA_CONVERSION);
    } 
    return dATA_CONVERSION;
  }
  
  public DATA_CONVERSION illegalSocketFactoryType(CompletionStatus paramCompletionStatus, Object paramObject) { return illegalSocketFactoryType(paramCompletionStatus, null, paramObject); }
  
  public DATA_CONVERSION illegalSocketFactoryType(Throwable paramThrowable, Object paramObject) { return illegalSocketFactoryType(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public DATA_CONVERSION illegalSocketFactoryType(Object paramObject) { return illegalSocketFactoryType(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public DATA_CONVERSION badCustomSocketFactory(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    DATA_CONVERSION dATA_CONVERSION = new DATA_CONVERSION(1398079702, paramCompletionStatus);
    if (paramThrowable != null)
      dATA_CONVERSION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.WARNING, "ORBUTIL.badCustomSocketFactory", arrayOfObject, ORBUtilSystemException.class, dATA_CONVERSION);
    } 
    return dATA_CONVERSION;
  }
  
  public DATA_CONVERSION badCustomSocketFactory(CompletionStatus paramCompletionStatus, Object paramObject) { return badCustomSocketFactory(paramCompletionStatus, null, paramObject); }
  
  public DATA_CONVERSION badCustomSocketFactory(Throwable paramThrowable, Object paramObject) { return badCustomSocketFactory(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public DATA_CONVERSION badCustomSocketFactory(Object paramObject) { return badCustomSocketFactory(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public DATA_CONVERSION fragmentSizeMinimum(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject1, Object paramObject2) {
    DATA_CONVERSION dATA_CONVERSION = new DATA_CONVERSION(1398079703, paramCompletionStatus);
    if (paramThrowable != null)
      dATA_CONVERSION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[2];
      arrayOfObject[0] = paramObject1;
      arrayOfObject[1] = paramObject2;
      doLog(Level.WARNING, "ORBUTIL.fragmentSizeMinimum", arrayOfObject, ORBUtilSystemException.class, dATA_CONVERSION);
    } 
    return dATA_CONVERSION;
  }
  
  public DATA_CONVERSION fragmentSizeMinimum(CompletionStatus paramCompletionStatus, Object paramObject1, Object paramObject2) { return fragmentSizeMinimum(paramCompletionStatus, null, paramObject1, paramObject2); }
  
  public DATA_CONVERSION fragmentSizeMinimum(Throwable paramThrowable, Object paramObject1, Object paramObject2) { return fragmentSizeMinimum(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject1, paramObject2); }
  
  public DATA_CONVERSION fragmentSizeMinimum(Object paramObject1, Object paramObject2) { return fragmentSizeMinimum(CompletionStatus.COMPLETED_NO, null, paramObject1, paramObject2); }
  
  public DATA_CONVERSION fragmentSizeDiv(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject1, Object paramObject2) {
    DATA_CONVERSION dATA_CONVERSION = new DATA_CONVERSION(1398079704, paramCompletionStatus);
    if (paramThrowable != null)
      dATA_CONVERSION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[2];
      arrayOfObject[0] = paramObject1;
      arrayOfObject[1] = paramObject2;
      doLog(Level.WARNING, "ORBUTIL.fragmentSizeDiv", arrayOfObject, ORBUtilSystemException.class, dATA_CONVERSION);
    } 
    return dATA_CONVERSION;
  }
  
  public DATA_CONVERSION fragmentSizeDiv(CompletionStatus paramCompletionStatus, Object paramObject1, Object paramObject2) { return fragmentSizeDiv(paramCompletionStatus, null, paramObject1, paramObject2); }
  
  public DATA_CONVERSION fragmentSizeDiv(Throwable paramThrowable, Object paramObject1, Object paramObject2) { return fragmentSizeDiv(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject1, paramObject2); }
  
  public DATA_CONVERSION fragmentSizeDiv(Object paramObject1, Object paramObject2) { return fragmentSizeDiv(CompletionStatus.COMPLETED_NO, null, paramObject1, paramObject2); }
  
  public DATA_CONVERSION orbInitializerFailure(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    DATA_CONVERSION dATA_CONVERSION = new DATA_CONVERSION(1398079705, paramCompletionStatus);
    if (paramThrowable != null)
      dATA_CONVERSION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.WARNING, "ORBUTIL.orbInitializerFailure", arrayOfObject, ORBUtilSystemException.class, dATA_CONVERSION);
    } 
    return dATA_CONVERSION;
  }
  
  public DATA_CONVERSION orbInitializerFailure(CompletionStatus paramCompletionStatus, Object paramObject) { return orbInitializerFailure(paramCompletionStatus, null, paramObject); }
  
  public DATA_CONVERSION orbInitializerFailure(Throwable paramThrowable, Object paramObject) { return orbInitializerFailure(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public DATA_CONVERSION orbInitializerFailure(Object paramObject) { return orbInitializerFailure(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public DATA_CONVERSION orbInitializerType(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    DATA_CONVERSION dATA_CONVERSION = new DATA_CONVERSION(1398079706, paramCompletionStatus);
    if (paramThrowable != null)
      dATA_CONVERSION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.WARNING, "ORBUTIL.orbInitializerType", arrayOfObject, ORBUtilSystemException.class, dATA_CONVERSION);
    } 
    return dATA_CONVERSION;
  }
  
  public DATA_CONVERSION orbInitializerType(CompletionStatus paramCompletionStatus, Object paramObject) { return orbInitializerType(paramCompletionStatus, null, paramObject); }
  
  public DATA_CONVERSION orbInitializerType(Throwable paramThrowable, Object paramObject) { return orbInitializerType(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public DATA_CONVERSION orbInitializerType(Object paramObject) { return orbInitializerType(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public DATA_CONVERSION orbInitialreferenceSyntax(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    DATA_CONVERSION dATA_CONVERSION = new DATA_CONVERSION(1398079707, paramCompletionStatus);
    if (paramThrowable != null)
      dATA_CONVERSION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.orbInitialreferenceSyntax", arrayOfObject, ORBUtilSystemException.class, dATA_CONVERSION);
    } 
    return dATA_CONVERSION;
  }
  
  public DATA_CONVERSION orbInitialreferenceSyntax(CompletionStatus paramCompletionStatus) { return orbInitialreferenceSyntax(paramCompletionStatus, null); }
  
  public DATA_CONVERSION orbInitialreferenceSyntax(Throwable paramThrowable) { return orbInitialreferenceSyntax(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public DATA_CONVERSION orbInitialreferenceSyntax() { return orbInitialreferenceSyntax(CompletionStatus.COMPLETED_NO, null); }
  
  public DATA_CONVERSION acceptorInstantiationFailure(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    DATA_CONVERSION dATA_CONVERSION = new DATA_CONVERSION(1398079708, paramCompletionStatus);
    if (paramThrowable != null)
      dATA_CONVERSION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.WARNING, "ORBUTIL.acceptorInstantiationFailure", arrayOfObject, ORBUtilSystemException.class, dATA_CONVERSION);
    } 
    return dATA_CONVERSION;
  }
  
  public DATA_CONVERSION acceptorInstantiationFailure(CompletionStatus paramCompletionStatus, Object paramObject) { return acceptorInstantiationFailure(paramCompletionStatus, null, paramObject); }
  
  public DATA_CONVERSION acceptorInstantiationFailure(Throwable paramThrowable, Object paramObject) { return acceptorInstantiationFailure(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public DATA_CONVERSION acceptorInstantiationFailure(Object paramObject) { return acceptorInstantiationFailure(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public DATA_CONVERSION acceptorInstantiationTypeFailure(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    DATA_CONVERSION dATA_CONVERSION = new DATA_CONVERSION(1398079709, paramCompletionStatus);
    if (paramThrowable != null)
      dATA_CONVERSION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.WARNING, "ORBUTIL.acceptorInstantiationTypeFailure", arrayOfObject, ORBUtilSystemException.class, dATA_CONVERSION);
    } 
    return dATA_CONVERSION;
  }
  
  public DATA_CONVERSION acceptorInstantiationTypeFailure(CompletionStatus paramCompletionStatus, Object paramObject) { return acceptorInstantiationTypeFailure(paramCompletionStatus, null, paramObject); }
  
  public DATA_CONVERSION acceptorInstantiationTypeFailure(Throwable paramThrowable, Object paramObject) { return acceptorInstantiationTypeFailure(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public DATA_CONVERSION acceptorInstantiationTypeFailure(Object paramObject) { return acceptorInstantiationTypeFailure(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public DATA_CONVERSION illegalContactInfoListFactoryType(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    DATA_CONVERSION dATA_CONVERSION = new DATA_CONVERSION(1398079710, paramCompletionStatus);
    if (paramThrowable != null)
      dATA_CONVERSION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.WARNING, "ORBUTIL.illegalContactInfoListFactoryType", arrayOfObject, ORBUtilSystemException.class, dATA_CONVERSION);
    } 
    return dATA_CONVERSION;
  }
  
  public DATA_CONVERSION illegalContactInfoListFactoryType(CompletionStatus paramCompletionStatus, Object paramObject) { return illegalContactInfoListFactoryType(paramCompletionStatus, null, paramObject); }
  
  public DATA_CONVERSION illegalContactInfoListFactoryType(Throwable paramThrowable, Object paramObject) { return illegalContactInfoListFactoryType(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public DATA_CONVERSION illegalContactInfoListFactoryType(Object paramObject) { return illegalContactInfoListFactoryType(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public DATA_CONVERSION badContactInfoListFactory(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    DATA_CONVERSION dATA_CONVERSION = new DATA_CONVERSION(1398079711, paramCompletionStatus);
    if (paramThrowable != null)
      dATA_CONVERSION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.WARNING, "ORBUTIL.badContactInfoListFactory", arrayOfObject, ORBUtilSystemException.class, dATA_CONVERSION);
    } 
    return dATA_CONVERSION;
  }
  
  public DATA_CONVERSION badContactInfoListFactory(CompletionStatus paramCompletionStatus, Object paramObject) { return badContactInfoListFactory(paramCompletionStatus, null, paramObject); }
  
  public DATA_CONVERSION badContactInfoListFactory(Throwable paramThrowable, Object paramObject) { return badContactInfoListFactory(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public DATA_CONVERSION badContactInfoListFactory(Object paramObject) { return badContactInfoListFactory(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public DATA_CONVERSION illegalIorToSocketInfoType(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    DATA_CONVERSION dATA_CONVERSION = new DATA_CONVERSION(1398079712, paramCompletionStatus);
    if (paramThrowable != null)
      dATA_CONVERSION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.WARNING, "ORBUTIL.illegalIorToSocketInfoType", arrayOfObject, ORBUtilSystemException.class, dATA_CONVERSION);
    } 
    return dATA_CONVERSION;
  }
  
  public DATA_CONVERSION illegalIorToSocketInfoType(CompletionStatus paramCompletionStatus, Object paramObject) { return illegalIorToSocketInfoType(paramCompletionStatus, null, paramObject); }
  
  public DATA_CONVERSION illegalIorToSocketInfoType(Throwable paramThrowable, Object paramObject) { return illegalIorToSocketInfoType(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public DATA_CONVERSION illegalIorToSocketInfoType(Object paramObject) { return illegalIorToSocketInfoType(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public DATA_CONVERSION badCustomIorToSocketInfo(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    DATA_CONVERSION dATA_CONVERSION = new DATA_CONVERSION(1398079713, paramCompletionStatus);
    if (paramThrowable != null)
      dATA_CONVERSION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.WARNING, "ORBUTIL.badCustomIorToSocketInfo", arrayOfObject, ORBUtilSystemException.class, dATA_CONVERSION);
    } 
    return dATA_CONVERSION;
  }
  
  public DATA_CONVERSION badCustomIorToSocketInfo(CompletionStatus paramCompletionStatus, Object paramObject) { return badCustomIorToSocketInfo(paramCompletionStatus, null, paramObject); }
  
  public DATA_CONVERSION badCustomIorToSocketInfo(Throwable paramThrowable, Object paramObject) { return badCustomIorToSocketInfo(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public DATA_CONVERSION badCustomIorToSocketInfo(Object paramObject) { return badCustomIorToSocketInfo(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public DATA_CONVERSION illegalIiopPrimaryToContactInfoType(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    DATA_CONVERSION dATA_CONVERSION = new DATA_CONVERSION(1398079714, paramCompletionStatus);
    if (paramThrowable != null)
      dATA_CONVERSION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.WARNING, "ORBUTIL.illegalIiopPrimaryToContactInfoType", arrayOfObject, ORBUtilSystemException.class, dATA_CONVERSION);
    } 
    return dATA_CONVERSION;
  }
  
  public DATA_CONVERSION illegalIiopPrimaryToContactInfoType(CompletionStatus paramCompletionStatus, Object paramObject) { return illegalIiopPrimaryToContactInfoType(paramCompletionStatus, null, paramObject); }
  
  public DATA_CONVERSION illegalIiopPrimaryToContactInfoType(Throwable paramThrowable, Object paramObject) { return illegalIiopPrimaryToContactInfoType(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public DATA_CONVERSION illegalIiopPrimaryToContactInfoType(Object paramObject) { return illegalIiopPrimaryToContactInfoType(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public DATA_CONVERSION badCustomIiopPrimaryToContactInfo(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    DATA_CONVERSION dATA_CONVERSION = new DATA_CONVERSION(1398079715, paramCompletionStatus);
    if (paramThrowable != null)
      dATA_CONVERSION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.WARNING, "ORBUTIL.badCustomIiopPrimaryToContactInfo", arrayOfObject, ORBUtilSystemException.class, dATA_CONVERSION);
    } 
    return dATA_CONVERSION;
  }
  
  public DATA_CONVERSION badCustomIiopPrimaryToContactInfo(CompletionStatus paramCompletionStatus, Object paramObject) { return badCustomIiopPrimaryToContactInfo(paramCompletionStatus, null, paramObject); }
  
  public DATA_CONVERSION badCustomIiopPrimaryToContactInfo(Throwable paramThrowable, Object paramObject) { return badCustomIiopPrimaryToContactInfo(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public DATA_CONVERSION badCustomIiopPrimaryToContactInfo(Object paramObject) { return badCustomIiopPrimaryToContactInfo(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public INV_OBJREF badCorbalocString(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INV_OBJREF iNV_OBJREF = new INV_OBJREF(1398079689, paramCompletionStatus);
    if (paramThrowable != null)
      iNV_OBJREF.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.badCorbalocString", arrayOfObject, ORBUtilSystemException.class, iNV_OBJREF);
    } 
    return iNV_OBJREF;
  }
  
  public INV_OBJREF badCorbalocString(CompletionStatus paramCompletionStatus) { return badCorbalocString(paramCompletionStatus, null); }
  
  public INV_OBJREF badCorbalocString(Throwable paramThrowable) { return badCorbalocString(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INV_OBJREF badCorbalocString() { return badCorbalocString(CompletionStatus.COMPLETED_NO, null); }
  
  public INV_OBJREF noProfilePresent(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INV_OBJREF iNV_OBJREF = new INV_OBJREF(1398079690, paramCompletionStatus);
    if (paramThrowable != null)
      iNV_OBJREF.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.noProfilePresent", arrayOfObject, ORBUtilSystemException.class, iNV_OBJREF);
    } 
    return iNV_OBJREF;
  }
  
  public INV_OBJREF noProfilePresent(CompletionStatus paramCompletionStatus) { return noProfilePresent(paramCompletionStatus, null); }
  
  public INV_OBJREF noProfilePresent(Throwable paramThrowable) { return noProfilePresent(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INV_OBJREF noProfilePresent() { return noProfilePresent(CompletionStatus.COMPLETED_NO, null); }
  
  public INITIALIZE cannotCreateOrbidDb(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INITIALIZE iNITIALIZE = new INITIALIZE(1398079689, paramCompletionStatus);
    if (paramThrowable != null)
      iNITIALIZE.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.cannotCreateOrbidDb", arrayOfObject, ORBUtilSystemException.class, iNITIALIZE);
    } 
    return iNITIALIZE;
  }
  
  public INITIALIZE cannotCreateOrbidDb(CompletionStatus paramCompletionStatus) { return cannotCreateOrbidDb(paramCompletionStatus, null); }
  
  public INITIALIZE cannotCreateOrbidDb(Throwable paramThrowable) { return cannotCreateOrbidDb(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INITIALIZE cannotCreateOrbidDb() { return cannotCreateOrbidDb(CompletionStatus.COMPLETED_NO, null); }
  
  public INITIALIZE cannotReadOrbidDb(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INITIALIZE iNITIALIZE = new INITIALIZE(1398079690, paramCompletionStatus);
    if (paramThrowable != null)
      iNITIALIZE.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.cannotReadOrbidDb", arrayOfObject, ORBUtilSystemException.class, iNITIALIZE);
    } 
    return iNITIALIZE;
  }
  
  public INITIALIZE cannotReadOrbidDb(CompletionStatus paramCompletionStatus) { return cannotReadOrbidDb(paramCompletionStatus, null); }
  
  public INITIALIZE cannotReadOrbidDb(Throwable paramThrowable) { return cannotReadOrbidDb(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INITIALIZE cannotReadOrbidDb() { return cannotReadOrbidDb(CompletionStatus.COMPLETED_NO, null); }
  
  public INITIALIZE cannotWriteOrbidDb(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INITIALIZE iNITIALIZE = new INITIALIZE(1398079691, paramCompletionStatus);
    if (paramThrowable != null)
      iNITIALIZE.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.cannotWriteOrbidDb", arrayOfObject, ORBUtilSystemException.class, iNITIALIZE);
    } 
    return iNITIALIZE;
  }
  
  public INITIALIZE cannotWriteOrbidDb(CompletionStatus paramCompletionStatus) { return cannotWriteOrbidDb(paramCompletionStatus, null); }
  
  public INITIALIZE cannotWriteOrbidDb(Throwable paramThrowable) { return cannotWriteOrbidDb(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INITIALIZE cannotWriteOrbidDb() { return cannotWriteOrbidDb(CompletionStatus.COMPLETED_NO, null); }
  
  public INITIALIZE getServerPortCalledBeforeEndpointsInitialized(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INITIALIZE iNITIALIZE = new INITIALIZE(1398079692, paramCompletionStatus);
    if (paramThrowable != null)
      iNITIALIZE.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.getServerPortCalledBeforeEndpointsInitialized", arrayOfObject, ORBUtilSystemException.class, iNITIALIZE);
    } 
    return iNITIALIZE;
  }
  
  public INITIALIZE getServerPortCalledBeforeEndpointsInitialized(CompletionStatus paramCompletionStatus) { return getServerPortCalledBeforeEndpointsInitialized(paramCompletionStatus, null); }
  
  public INITIALIZE getServerPortCalledBeforeEndpointsInitialized(Throwable paramThrowable) { return getServerPortCalledBeforeEndpointsInitialized(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INITIALIZE getServerPortCalledBeforeEndpointsInitialized() { return getServerPortCalledBeforeEndpointsInitialized(CompletionStatus.COMPLETED_NO, null); }
  
  public INITIALIZE persistentServerportNotSet(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INITIALIZE iNITIALIZE = new INITIALIZE(1398079693, paramCompletionStatus);
    if (paramThrowable != null)
      iNITIALIZE.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.persistentServerportNotSet", arrayOfObject, ORBUtilSystemException.class, iNITIALIZE);
    } 
    return iNITIALIZE;
  }
  
  public INITIALIZE persistentServerportNotSet(CompletionStatus paramCompletionStatus) { return persistentServerportNotSet(paramCompletionStatus, null); }
  
  public INITIALIZE persistentServerportNotSet(Throwable paramThrowable) { return persistentServerportNotSet(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INITIALIZE persistentServerportNotSet() { return persistentServerportNotSet(CompletionStatus.COMPLETED_NO, null); }
  
  public INITIALIZE persistentServeridNotSet(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INITIALIZE iNITIALIZE = new INITIALIZE(1398079694, paramCompletionStatus);
    if (paramThrowable != null)
      iNITIALIZE.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.persistentServeridNotSet", arrayOfObject, ORBUtilSystemException.class, iNITIALIZE);
    } 
    return iNITIALIZE;
  }
  
  public INITIALIZE persistentServeridNotSet(CompletionStatus paramCompletionStatus) { return persistentServeridNotSet(paramCompletionStatus, null); }
  
  public INITIALIZE persistentServeridNotSet(Throwable paramThrowable) { return persistentServeridNotSet(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INITIALIZE persistentServeridNotSet() { return persistentServeridNotSet(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL nonExistentOrbid(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079689, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.nonExistentOrbid", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL nonExistentOrbid(CompletionStatus paramCompletionStatus) { return nonExistentOrbid(paramCompletionStatus, null); }
  
  public INTERNAL nonExistentOrbid(Throwable paramThrowable) { return nonExistentOrbid(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL nonExistentOrbid() { return nonExistentOrbid(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL noServerSubcontract(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079690, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.noServerSubcontract", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL noServerSubcontract(CompletionStatus paramCompletionStatus) { return noServerSubcontract(paramCompletionStatus, null); }
  
  public INTERNAL noServerSubcontract(Throwable paramThrowable) { return noServerSubcontract(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL noServerSubcontract() { return noServerSubcontract(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL serverScTempSize(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079691, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.serverScTempSize", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL serverScTempSize(CompletionStatus paramCompletionStatus) { return serverScTempSize(paramCompletionStatus, null); }
  
  public INTERNAL serverScTempSize(Throwable paramThrowable) { return serverScTempSize(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL serverScTempSize() { return serverScTempSize(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL noClientScClass(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079692, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.noClientScClass", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL noClientScClass(CompletionStatus paramCompletionStatus) { return noClientScClass(paramCompletionStatus, null); }
  
  public INTERNAL noClientScClass(Throwable paramThrowable) { return noClientScClass(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL noClientScClass() { return noClientScClass(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL serverScNoIiopProfile(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079693, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.serverScNoIiopProfile", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL serverScNoIiopProfile(CompletionStatus paramCompletionStatus) { return serverScNoIiopProfile(paramCompletionStatus, null); }
  
  public INTERNAL serverScNoIiopProfile(Throwable paramThrowable) { return serverScNoIiopProfile(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL serverScNoIiopProfile() { return serverScNoIiopProfile(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL getSystemExReturnedNull(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079694, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.getSystemExReturnedNull", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL getSystemExReturnedNull(CompletionStatus paramCompletionStatus) { return getSystemExReturnedNull(paramCompletionStatus, null); }
  
  public INTERNAL getSystemExReturnedNull(Throwable paramThrowable) { return getSystemExReturnedNull(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL getSystemExReturnedNull() { return getSystemExReturnedNull(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL peekstringFailed(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079695, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.peekstringFailed", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL peekstringFailed(CompletionStatus paramCompletionStatus) { return peekstringFailed(paramCompletionStatus, null); }
  
  public INTERNAL peekstringFailed(Throwable paramThrowable) { return peekstringFailed(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL peekstringFailed() { return peekstringFailed(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL getLocalHostFailed(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079696, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.getLocalHostFailed", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL getLocalHostFailed(CompletionStatus paramCompletionStatus) { return getLocalHostFailed(paramCompletionStatus, null); }
  
  public INTERNAL getLocalHostFailed(Throwable paramThrowable) { return getLocalHostFailed(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL getLocalHostFailed() { return getLocalHostFailed(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL badLocateRequestStatus(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079698, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.badLocateRequestStatus", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL badLocateRequestStatus(CompletionStatus paramCompletionStatus) { return badLocateRequestStatus(paramCompletionStatus, null); }
  
  public INTERNAL badLocateRequestStatus(Throwable paramThrowable) { return badLocateRequestStatus(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL badLocateRequestStatus() { return badLocateRequestStatus(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL stringifyWriteError(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079699, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.stringifyWriteError", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL stringifyWriteError(CompletionStatus paramCompletionStatus) { return stringifyWriteError(paramCompletionStatus, null); }
  
  public INTERNAL stringifyWriteError(Throwable paramThrowable) { return stringifyWriteError(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL stringifyWriteError() { return stringifyWriteError(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL badGiopRequestType(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079700, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.badGiopRequestType", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL badGiopRequestType(CompletionStatus paramCompletionStatus) { return badGiopRequestType(paramCompletionStatus, null); }
  
  public INTERNAL badGiopRequestType(Throwable paramThrowable) { return badGiopRequestType(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL badGiopRequestType() { return badGiopRequestType(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL errorUnmarshalingUserexc(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079701, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.errorUnmarshalingUserexc", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL errorUnmarshalingUserexc(CompletionStatus paramCompletionStatus) { return errorUnmarshalingUserexc(paramCompletionStatus, null); }
  
  public INTERNAL errorUnmarshalingUserexc(Throwable paramThrowable) { return errorUnmarshalingUserexc(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL errorUnmarshalingUserexc() { return errorUnmarshalingUserexc(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL requestdispatcherregistryError(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079702, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.requestdispatcherregistryError", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL requestdispatcherregistryError(CompletionStatus paramCompletionStatus) { return requestdispatcherregistryError(paramCompletionStatus, null); }
  
  public INTERNAL requestdispatcherregistryError(Throwable paramThrowable) { return requestdispatcherregistryError(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL requestdispatcherregistryError() { return requestdispatcherregistryError(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL locationforwardError(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079703, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.locationforwardError", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL locationforwardError(CompletionStatus paramCompletionStatus) { return locationforwardError(paramCompletionStatus, null); }
  
  public INTERNAL locationforwardError(Throwable paramThrowable) { return locationforwardError(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL locationforwardError() { return locationforwardError(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL wrongClientsc(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079704, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.wrongClientsc", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL wrongClientsc(CompletionStatus paramCompletionStatus) { return wrongClientsc(paramCompletionStatus, null); }
  
  public INTERNAL wrongClientsc(Throwable paramThrowable) { return wrongClientsc(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL wrongClientsc() { return wrongClientsc(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL badServantReadObject(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079705, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.badServantReadObject", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL badServantReadObject(CompletionStatus paramCompletionStatus) { return badServantReadObject(paramCompletionStatus, null); }
  
  public INTERNAL badServantReadObject(Throwable paramThrowable) { return badServantReadObject(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL badServantReadObject() { return badServantReadObject(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL multIiopProfNotSupported(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079706, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.multIiopProfNotSupported", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL multIiopProfNotSupported(CompletionStatus paramCompletionStatus) { return multIiopProfNotSupported(paramCompletionStatus, null); }
  
  public INTERNAL multIiopProfNotSupported(Throwable paramThrowable) { return multIiopProfNotSupported(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL multIiopProfNotSupported() { return multIiopProfNotSupported(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL giopMagicError(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079708, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.giopMagicError", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL giopMagicError(CompletionStatus paramCompletionStatus) { return giopMagicError(paramCompletionStatus, null); }
  
  public INTERNAL giopMagicError(Throwable paramThrowable) { return giopMagicError(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL giopMagicError() { return giopMagicError(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL giopVersionError(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079709, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.giopVersionError", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL giopVersionError(CompletionStatus paramCompletionStatus) { return giopVersionError(paramCompletionStatus, null); }
  
  public INTERNAL giopVersionError(Throwable paramThrowable) { return giopVersionError(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL giopVersionError() { return giopVersionError(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL illegalReplyStatus(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079710, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.illegalReplyStatus", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL illegalReplyStatus(CompletionStatus paramCompletionStatus) { return illegalReplyStatus(paramCompletionStatus, null); }
  
  public INTERNAL illegalReplyStatus(Throwable paramThrowable) { return illegalReplyStatus(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL illegalReplyStatus() { return illegalReplyStatus(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL illegalGiopMsgType(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079711, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.illegalGiopMsgType", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL illegalGiopMsgType(CompletionStatus paramCompletionStatus) { return illegalGiopMsgType(paramCompletionStatus, null); }
  
  public INTERNAL illegalGiopMsgType(Throwable paramThrowable) { return illegalGiopMsgType(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL illegalGiopMsgType() { return illegalGiopMsgType(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL fragmentationDisallowed(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079712, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.fragmentationDisallowed", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL fragmentationDisallowed(CompletionStatus paramCompletionStatus) { return fragmentationDisallowed(paramCompletionStatus, null); }
  
  public INTERNAL fragmentationDisallowed(Throwable paramThrowable) { return fragmentationDisallowed(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL fragmentationDisallowed() { return fragmentationDisallowed(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL badReplystatus(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079713, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.badReplystatus", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL badReplystatus(CompletionStatus paramCompletionStatus) { return badReplystatus(paramCompletionStatus, null); }
  
  public INTERNAL badReplystatus(Throwable paramThrowable) { return badReplystatus(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL badReplystatus() { return badReplystatus(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL ctbConverterFailure(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079714, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.ctbConverterFailure", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL ctbConverterFailure(CompletionStatus paramCompletionStatus) { return ctbConverterFailure(paramCompletionStatus, null); }
  
  public INTERNAL ctbConverterFailure(Throwable paramThrowable) { return ctbConverterFailure(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL ctbConverterFailure() { return ctbConverterFailure(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL btcConverterFailure(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079715, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.btcConverterFailure", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL btcConverterFailure(CompletionStatus paramCompletionStatus) { return btcConverterFailure(paramCompletionStatus, null); }
  
  public INTERNAL btcConverterFailure(Throwable paramThrowable) { return btcConverterFailure(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL btcConverterFailure() { return btcConverterFailure(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL wcharArrayUnsupportedEncoding(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079716, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.wcharArrayUnsupportedEncoding", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL wcharArrayUnsupportedEncoding(CompletionStatus paramCompletionStatus) { return wcharArrayUnsupportedEncoding(paramCompletionStatus, null); }
  
  public INTERNAL wcharArrayUnsupportedEncoding(Throwable paramThrowable) { return wcharArrayUnsupportedEncoding(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL wcharArrayUnsupportedEncoding() { return wcharArrayUnsupportedEncoding(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL illegalTargetAddressDisposition(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079717, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.illegalTargetAddressDisposition", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL illegalTargetAddressDisposition(CompletionStatus paramCompletionStatus) { return illegalTargetAddressDisposition(paramCompletionStatus, null); }
  
  public INTERNAL illegalTargetAddressDisposition(Throwable paramThrowable) { return illegalTargetAddressDisposition(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL illegalTargetAddressDisposition() { return illegalTargetAddressDisposition(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL nullReplyInGetAddrDisposition(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079718, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.nullReplyInGetAddrDisposition", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL nullReplyInGetAddrDisposition(CompletionStatus paramCompletionStatus) { return nullReplyInGetAddrDisposition(paramCompletionStatus, null); }
  
  public INTERNAL nullReplyInGetAddrDisposition(Throwable paramThrowable) { return nullReplyInGetAddrDisposition(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL nullReplyInGetAddrDisposition() { return nullReplyInGetAddrDisposition(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL orbTargetAddrPreferenceInExtractObjectkeyInvalid(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079719, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.orbTargetAddrPreferenceInExtractObjectkeyInvalid", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL orbTargetAddrPreferenceInExtractObjectkeyInvalid(CompletionStatus paramCompletionStatus) { return orbTargetAddrPreferenceInExtractObjectkeyInvalid(paramCompletionStatus, null); }
  
  public INTERNAL orbTargetAddrPreferenceInExtractObjectkeyInvalid(Throwable paramThrowable) { return orbTargetAddrPreferenceInExtractObjectkeyInvalid(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL orbTargetAddrPreferenceInExtractObjectkeyInvalid() { return orbTargetAddrPreferenceInExtractObjectkeyInvalid(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL invalidIsstreamedTckind(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    INTERNAL iNTERNAL = new INTERNAL(1398079720, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.WARNING, "ORBUTIL.invalidIsstreamedTckind", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL invalidIsstreamedTckind(CompletionStatus paramCompletionStatus, Object paramObject) { return invalidIsstreamedTckind(paramCompletionStatus, null, paramObject); }
  
  public INTERNAL invalidIsstreamedTckind(Throwable paramThrowable, Object paramObject) { return invalidIsstreamedTckind(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public INTERNAL invalidIsstreamedTckind(Object paramObject) { return invalidIsstreamedTckind(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public INTERNAL invalidJdk131PatchLevel(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079721, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.invalidJdk131PatchLevel", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL invalidJdk131PatchLevel(CompletionStatus paramCompletionStatus) { return invalidJdk131PatchLevel(paramCompletionStatus, null); }
  
  public INTERNAL invalidJdk131PatchLevel(Throwable paramThrowable) { return invalidJdk131PatchLevel(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL invalidJdk131PatchLevel() { return invalidJdk131PatchLevel(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL svcctxUnmarshalError(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079722, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.svcctxUnmarshalError", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL svcctxUnmarshalError(CompletionStatus paramCompletionStatus) { return svcctxUnmarshalError(paramCompletionStatus, null); }
  
  public INTERNAL svcctxUnmarshalError(Throwable paramThrowable) { return svcctxUnmarshalError(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL svcctxUnmarshalError() { return svcctxUnmarshalError(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL nullIor(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079723, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.nullIor", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL nullIor(CompletionStatus paramCompletionStatus) { return nullIor(paramCompletionStatus, null); }
  
  public INTERNAL nullIor(Throwable paramThrowable) { return nullIor(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL nullIor() { return nullIor(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL unsupportedGiopVersion(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    INTERNAL iNTERNAL = new INTERNAL(1398079724, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.WARNING, "ORBUTIL.unsupportedGiopVersion", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL unsupportedGiopVersion(CompletionStatus paramCompletionStatus, Object paramObject) { return unsupportedGiopVersion(paramCompletionStatus, null, paramObject); }
  
  public INTERNAL unsupportedGiopVersion(Throwable paramThrowable, Object paramObject) { return unsupportedGiopVersion(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public INTERNAL unsupportedGiopVersion(Object paramObject) { return unsupportedGiopVersion(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public INTERNAL applicationExceptionInSpecialMethod(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079725, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.applicationExceptionInSpecialMethod", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL applicationExceptionInSpecialMethod(CompletionStatus paramCompletionStatus) { return applicationExceptionInSpecialMethod(paramCompletionStatus, null); }
  
  public INTERNAL applicationExceptionInSpecialMethod(Throwable paramThrowable) { return applicationExceptionInSpecialMethod(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL applicationExceptionInSpecialMethod() { return applicationExceptionInSpecialMethod(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL statementNotReachable1(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079726, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.statementNotReachable1", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL statementNotReachable1(CompletionStatus paramCompletionStatus) { return statementNotReachable1(paramCompletionStatus, null); }
  
  public INTERNAL statementNotReachable1(Throwable paramThrowable) { return statementNotReachable1(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL statementNotReachable1() { return statementNotReachable1(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL statementNotReachable2(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079727, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.statementNotReachable2", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL statementNotReachable2(CompletionStatus paramCompletionStatus) { return statementNotReachable2(paramCompletionStatus, null); }
  
  public INTERNAL statementNotReachable2(Throwable paramThrowable) { return statementNotReachable2(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL statementNotReachable2() { return statementNotReachable2(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL statementNotReachable3(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079728, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.statementNotReachable3", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL statementNotReachable3(CompletionStatus paramCompletionStatus) { return statementNotReachable3(paramCompletionStatus, null); }
  
  public INTERNAL statementNotReachable3(Throwable paramThrowable) { return statementNotReachable3(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL statementNotReachable3() { return statementNotReachable3(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL statementNotReachable4(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079729, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = null;
      doLog(Level.FINE, "ORBUTIL.statementNotReachable4", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL statementNotReachable4(CompletionStatus paramCompletionStatus) { return statementNotReachable4(paramCompletionStatus, null); }
  
  public INTERNAL statementNotReachable4(Throwable paramThrowable) { return statementNotReachable4(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL statementNotReachable4() { return statementNotReachable4(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL statementNotReachable5(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079730, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.statementNotReachable5", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL statementNotReachable5(CompletionStatus paramCompletionStatus) { return statementNotReachable5(paramCompletionStatus, null); }
  
  public INTERNAL statementNotReachable5(Throwable paramThrowable) { return statementNotReachable5(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL statementNotReachable5() { return statementNotReachable5(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL statementNotReachable6(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079731, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.statementNotReachable6", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL statementNotReachable6(CompletionStatus paramCompletionStatus) { return statementNotReachable6(paramCompletionStatus, null); }
  
  public INTERNAL statementNotReachable6(Throwable paramThrowable) { return statementNotReachable6(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL statementNotReachable6() { return statementNotReachable6(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL unexpectedDiiException(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079732, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.unexpectedDiiException", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL unexpectedDiiException(CompletionStatus paramCompletionStatus) { return unexpectedDiiException(paramCompletionStatus, null); }
  
  public INTERNAL unexpectedDiiException(Throwable paramThrowable) { return unexpectedDiiException(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL unexpectedDiiException() { return unexpectedDiiException(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL methodShouldNotBeCalled(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079733, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.methodShouldNotBeCalled", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL methodShouldNotBeCalled(CompletionStatus paramCompletionStatus) { return methodShouldNotBeCalled(paramCompletionStatus, null); }
  
  public INTERNAL methodShouldNotBeCalled(Throwable paramThrowable) { return methodShouldNotBeCalled(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL methodShouldNotBeCalled() { return methodShouldNotBeCalled(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL cancelNotSupported(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079734, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.cancelNotSupported", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL cancelNotSupported(CompletionStatus paramCompletionStatus) { return cancelNotSupported(paramCompletionStatus, null); }
  
  public INTERNAL cancelNotSupported(Throwable paramThrowable) { return cancelNotSupported(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL cancelNotSupported() { return cancelNotSupported(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL emptyStackRunServantPostInvoke(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079735, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.emptyStackRunServantPostInvoke", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL emptyStackRunServantPostInvoke(CompletionStatus paramCompletionStatus) { return emptyStackRunServantPostInvoke(paramCompletionStatus, null); }
  
  public INTERNAL emptyStackRunServantPostInvoke(Throwable paramThrowable) { return emptyStackRunServantPostInvoke(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL emptyStackRunServantPostInvoke() { return emptyStackRunServantPostInvoke(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL problemWithExceptionTypecode(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079736, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.problemWithExceptionTypecode", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL problemWithExceptionTypecode(CompletionStatus paramCompletionStatus) { return problemWithExceptionTypecode(paramCompletionStatus, null); }
  
  public INTERNAL problemWithExceptionTypecode(Throwable paramThrowable) { return problemWithExceptionTypecode(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL problemWithExceptionTypecode() { return problemWithExceptionTypecode(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL illegalSubcontractId(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    INTERNAL iNTERNAL = new INTERNAL(1398079737, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.WARNING, "ORBUTIL.illegalSubcontractId", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL illegalSubcontractId(CompletionStatus paramCompletionStatus, Object paramObject) { return illegalSubcontractId(paramCompletionStatus, null, paramObject); }
  
  public INTERNAL illegalSubcontractId(Throwable paramThrowable, Object paramObject) { return illegalSubcontractId(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public INTERNAL illegalSubcontractId(Object paramObject) { return illegalSubcontractId(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public INTERNAL badSystemExceptionInLocateReply(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079738, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.badSystemExceptionInLocateReply", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL badSystemExceptionInLocateReply(CompletionStatus paramCompletionStatus) { return badSystemExceptionInLocateReply(paramCompletionStatus, null); }
  
  public INTERNAL badSystemExceptionInLocateReply(Throwable paramThrowable) { return badSystemExceptionInLocateReply(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL badSystemExceptionInLocateReply() { return badSystemExceptionInLocateReply(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL badSystemExceptionInReply(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079739, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.badSystemExceptionInReply", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL badSystemExceptionInReply(CompletionStatus paramCompletionStatus) { return badSystemExceptionInReply(paramCompletionStatus, null); }
  
  public INTERNAL badSystemExceptionInReply(Throwable paramThrowable) { return badSystemExceptionInReply(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL badSystemExceptionInReply() { return badSystemExceptionInReply(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL badCompletionStatusInLocateReply(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    INTERNAL iNTERNAL = new INTERNAL(1398079740, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.WARNING, "ORBUTIL.badCompletionStatusInLocateReply", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL badCompletionStatusInLocateReply(CompletionStatus paramCompletionStatus, Object paramObject) { return badCompletionStatusInLocateReply(paramCompletionStatus, null, paramObject); }
  
  public INTERNAL badCompletionStatusInLocateReply(Throwable paramThrowable, Object paramObject) { return badCompletionStatusInLocateReply(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public INTERNAL badCompletionStatusInLocateReply(Object paramObject) { return badCompletionStatusInLocateReply(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public INTERNAL badCompletionStatusInReply(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    INTERNAL iNTERNAL = new INTERNAL(1398079741, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.WARNING, "ORBUTIL.badCompletionStatusInReply", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL badCompletionStatusInReply(CompletionStatus paramCompletionStatus, Object paramObject) { return badCompletionStatusInReply(paramCompletionStatus, null, paramObject); }
  
  public INTERNAL badCompletionStatusInReply(Throwable paramThrowable, Object paramObject) { return badCompletionStatusInReply(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public INTERNAL badCompletionStatusInReply(Object paramObject) { return badCompletionStatusInReply(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public INTERNAL badkindCannotOccur(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079742, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.badkindCannotOccur", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL badkindCannotOccur(CompletionStatus paramCompletionStatus) { return badkindCannotOccur(paramCompletionStatus, null); }
  
  public INTERNAL badkindCannotOccur(Throwable paramThrowable) { return badkindCannotOccur(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL badkindCannotOccur() { return badkindCannotOccur(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL errorResolvingAlias(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079743, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.errorResolvingAlias", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL errorResolvingAlias(CompletionStatus paramCompletionStatus) { return errorResolvingAlias(paramCompletionStatus, null); }
  
  public INTERNAL errorResolvingAlias(Throwable paramThrowable) { return errorResolvingAlias(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL errorResolvingAlias() { return errorResolvingAlias(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL tkLongDoubleNotSupported(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079744, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.tkLongDoubleNotSupported", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL tkLongDoubleNotSupported(CompletionStatus paramCompletionStatus) { return tkLongDoubleNotSupported(paramCompletionStatus, null); }
  
  public INTERNAL tkLongDoubleNotSupported(Throwable paramThrowable) { return tkLongDoubleNotSupported(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL tkLongDoubleNotSupported() { return tkLongDoubleNotSupported(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL typecodeNotSupported(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079745, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.typecodeNotSupported", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL typecodeNotSupported(CompletionStatus paramCompletionStatus) { return typecodeNotSupported(paramCompletionStatus, null); }
  
  public INTERNAL typecodeNotSupported(Throwable paramThrowable) { return typecodeNotSupported(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL typecodeNotSupported() { return typecodeNotSupported(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL boundsCannotOccur(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079747, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.boundsCannotOccur", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL boundsCannotOccur(CompletionStatus paramCompletionStatus) { return boundsCannotOccur(paramCompletionStatus, null); }
  
  public INTERNAL boundsCannotOccur(Throwable paramThrowable) { return boundsCannotOccur(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL boundsCannotOccur() { return boundsCannotOccur(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL numInvocationsAlreadyZero(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079749, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.numInvocationsAlreadyZero", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL numInvocationsAlreadyZero(CompletionStatus paramCompletionStatus) { return numInvocationsAlreadyZero(paramCompletionStatus, null); }
  
  public INTERNAL numInvocationsAlreadyZero(Throwable paramThrowable) { return numInvocationsAlreadyZero(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL numInvocationsAlreadyZero() { return numInvocationsAlreadyZero(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL errorInitBadserveridhandler(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079750, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.errorInitBadserveridhandler", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL errorInitBadserveridhandler(CompletionStatus paramCompletionStatus) { return errorInitBadserveridhandler(paramCompletionStatus, null); }
  
  public INTERNAL errorInitBadserveridhandler(Throwable paramThrowable) { return errorInitBadserveridhandler(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL errorInitBadserveridhandler() { return errorInitBadserveridhandler(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL noToa(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079751, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.noToa", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL noToa(CompletionStatus paramCompletionStatus) { return noToa(paramCompletionStatus, null); }
  
  public INTERNAL noToa(Throwable paramThrowable) { return noToa(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL noToa() { return noToa(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL noPoa(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079752, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.noPoa", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL noPoa(CompletionStatus paramCompletionStatus) { return noPoa(paramCompletionStatus, null); }
  
  public INTERNAL noPoa(Throwable paramThrowable) { return noPoa(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL noPoa() { return noPoa(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL invocationInfoStackEmpty(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079753, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.invocationInfoStackEmpty", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL invocationInfoStackEmpty(CompletionStatus paramCompletionStatus) { return invocationInfoStackEmpty(paramCompletionStatus, null); }
  
  public INTERNAL invocationInfoStackEmpty(Throwable paramThrowable) { return invocationInfoStackEmpty(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL invocationInfoStackEmpty() { return invocationInfoStackEmpty(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL badCodeSetString(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079754, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.badCodeSetString", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL badCodeSetString(CompletionStatus paramCompletionStatus) { return badCodeSetString(paramCompletionStatus, null); }
  
  public INTERNAL badCodeSetString(Throwable paramThrowable) { return badCodeSetString(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL badCodeSetString() { return badCodeSetString(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL unknownNativeCodeset(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    INTERNAL iNTERNAL = new INTERNAL(1398079755, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.WARNING, "ORBUTIL.unknownNativeCodeset", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL unknownNativeCodeset(CompletionStatus paramCompletionStatus, Object paramObject) { return unknownNativeCodeset(paramCompletionStatus, null, paramObject); }
  
  public INTERNAL unknownNativeCodeset(Throwable paramThrowable, Object paramObject) { return unknownNativeCodeset(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public INTERNAL unknownNativeCodeset(Object paramObject) { return unknownNativeCodeset(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public INTERNAL unknownConversionCodeSet(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    INTERNAL iNTERNAL = new INTERNAL(1398079756, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.WARNING, "ORBUTIL.unknownConversionCodeSet", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL unknownConversionCodeSet(CompletionStatus paramCompletionStatus, Object paramObject) { return unknownConversionCodeSet(paramCompletionStatus, null, paramObject); }
  
  public INTERNAL unknownConversionCodeSet(Throwable paramThrowable, Object paramObject) { return unknownConversionCodeSet(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public INTERNAL unknownConversionCodeSet(Object paramObject) { return unknownConversionCodeSet(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public INTERNAL invalidCodeSetNumber(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079757, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.invalidCodeSetNumber", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL invalidCodeSetNumber(CompletionStatus paramCompletionStatus) { return invalidCodeSetNumber(paramCompletionStatus, null); }
  
  public INTERNAL invalidCodeSetNumber(Throwable paramThrowable) { return invalidCodeSetNumber(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL invalidCodeSetNumber() { return invalidCodeSetNumber(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL invalidCodeSetString(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    INTERNAL iNTERNAL = new INTERNAL(1398079758, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.WARNING, "ORBUTIL.invalidCodeSetString", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL invalidCodeSetString(CompletionStatus paramCompletionStatus, Object paramObject) { return invalidCodeSetString(paramCompletionStatus, null, paramObject); }
  
  public INTERNAL invalidCodeSetString(Throwable paramThrowable, Object paramObject) { return invalidCodeSetString(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public INTERNAL invalidCodeSetString(Object paramObject) { return invalidCodeSetString(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public INTERNAL invalidCtbConverterName(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    INTERNAL iNTERNAL = new INTERNAL(1398079759, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.WARNING, "ORBUTIL.invalidCtbConverterName", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL invalidCtbConverterName(CompletionStatus paramCompletionStatus, Object paramObject) { return invalidCtbConverterName(paramCompletionStatus, null, paramObject); }
  
  public INTERNAL invalidCtbConverterName(Throwable paramThrowable, Object paramObject) { return invalidCtbConverterName(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public INTERNAL invalidCtbConverterName(Object paramObject) { return invalidCtbConverterName(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public INTERNAL invalidBtcConverterName(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    INTERNAL iNTERNAL = new INTERNAL(1398079760, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.WARNING, "ORBUTIL.invalidBtcConverterName", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL invalidBtcConverterName(CompletionStatus paramCompletionStatus, Object paramObject) { return invalidBtcConverterName(paramCompletionStatus, null, paramObject); }
  
  public INTERNAL invalidBtcConverterName(Throwable paramThrowable, Object paramObject) { return invalidBtcConverterName(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public INTERNAL invalidBtcConverterName(Object paramObject) { return invalidBtcConverterName(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public INTERNAL couldNotDuplicateCdrInputStream(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079761, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.couldNotDuplicateCdrInputStream", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL couldNotDuplicateCdrInputStream(CompletionStatus paramCompletionStatus) { return couldNotDuplicateCdrInputStream(paramCompletionStatus, null); }
  
  public INTERNAL couldNotDuplicateCdrInputStream(Throwable paramThrowable) { return couldNotDuplicateCdrInputStream(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL couldNotDuplicateCdrInputStream() { return couldNotDuplicateCdrInputStream(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL bootstrapApplicationException(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079762, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.bootstrapApplicationException", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL bootstrapApplicationException(CompletionStatus paramCompletionStatus) { return bootstrapApplicationException(paramCompletionStatus, null); }
  
  public INTERNAL bootstrapApplicationException(Throwable paramThrowable) { return bootstrapApplicationException(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL bootstrapApplicationException() { return bootstrapApplicationException(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL duplicateIndirectionOffset(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079763, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.duplicateIndirectionOffset", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL duplicateIndirectionOffset(CompletionStatus paramCompletionStatus) { return duplicateIndirectionOffset(paramCompletionStatus, null); }
  
  public INTERNAL duplicateIndirectionOffset(Throwable paramThrowable) { return duplicateIndirectionOffset(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL duplicateIndirectionOffset() { return duplicateIndirectionOffset(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL badMessageTypeForCancel(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079764, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.badMessageTypeForCancel", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL badMessageTypeForCancel(CompletionStatus paramCompletionStatus) { return badMessageTypeForCancel(paramCompletionStatus, null); }
  
  public INTERNAL badMessageTypeForCancel(Throwable paramThrowable) { return badMessageTypeForCancel(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL badMessageTypeForCancel() { return badMessageTypeForCancel(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL duplicateExceptionDetailMessage(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079765, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.duplicateExceptionDetailMessage", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL duplicateExceptionDetailMessage(CompletionStatus paramCompletionStatus) { return duplicateExceptionDetailMessage(paramCompletionStatus, null); }
  
  public INTERNAL duplicateExceptionDetailMessage(Throwable paramThrowable) { return duplicateExceptionDetailMessage(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL duplicateExceptionDetailMessage() { return duplicateExceptionDetailMessage(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL badExceptionDetailMessageServiceContextType(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079766, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.badExceptionDetailMessageServiceContextType", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL badExceptionDetailMessageServiceContextType(CompletionStatus paramCompletionStatus) { return badExceptionDetailMessageServiceContextType(paramCompletionStatus, null); }
  
  public INTERNAL badExceptionDetailMessageServiceContextType(Throwable paramThrowable) { return badExceptionDetailMessageServiceContextType(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL badExceptionDetailMessageServiceContextType() { return badExceptionDetailMessageServiceContextType(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL unexpectedDirectByteBufferWithNonChannelSocket(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079767, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.unexpectedDirectByteBufferWithNonChannelSocket", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL unexpectedDirectByteBufferWithNonChannelSocket(CompletionStatus paramCompletionStatus) { return unexpectedDirectByteBufferWithNonChannelSocket(paramCompletionStatus, null); }
  
  public INTERNAL unexpectedDirectByteBufferWithNonChannelSocket(Throwable paramThrowable) { return unexpectedDirectByteBufferWithNonChannelSocket(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL unexpectedDirectByteBufferWithNonChannelSocket() { return unexpectedDirectByteBufferWithNonChannelSocket(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL unexpectedNonDirectByteBufferWithChannelSocket(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079768, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.unexpectedNonDirectByteBufferWithChannelSocket", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL unexpectedNonDirectByteBufferWithChannelSocket(CompletionStatus paramCompletionStatus) { return unexpectedNonDirectByteBufferWithChannelSocket(paramCompletionStatus, null); }
  
  public INTERNAL unexpectedNonDirectByteBufferWithChannelSocket(Throwable paramThrowable) { return unexpectedNonDirectByteBufferWithChannelSocket(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL unexpectedNonDirectByteBufferWithChannelSocket() { return unexpectedNonDirectByteBufferWithChannelSocket(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL invalidContactInfoListIteratorFailureException(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079770, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.invalidContactInfoListIteratorFailureException", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL invalidContactInfoListIteratorFailureException(CompletionStatus paramCompletionStatus) { return invalidContactInfoListIteratorFailureException(paramCompletionStatus, null); }
  
  public INTERNAL invalidContactInfoListIteratorFailureException(Throwable paramThrowable) { return invalidContactInfoListIteratorFailureException(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL invalidContactInfoListIteratorFailureException() { return invalidContactInfoListIteratorFailureException(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL remarshalWithNowhereToGo(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079771, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.remarshalWithNowhereToGo", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL remarshalWithNowhereToGo(CompletionStatus paramCompletionStatus) { return remarshalWithNowhereToGo(paramCompletionStatus, null); }
  
  public INTERNAL remarshalWithNowhereToGo(Throwable paramThrowable) { return remarshalWithNowhereToGo(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL remarshalWithNowhereToGo() { return remarshalWithNowhereToGo(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL exceptionWhenSendingCloseConnection(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079772, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.exceptionWhenSendingCloseConnection", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL exceptionWhenSendingCloseConnection(CompletionStatus paramCompletionStatus) { return exceptionWhenSendingCloseConnection(paramCompletionStatus, null); }
  
  public INTERNAL exceptionWhenSendingCloseConnection(Throwable paramThrowable) { return exceptionWhenSendingCloseConnection(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL exceptionWhenSendingCloseConnection() { return exceptionWhenSendingCloseConnection(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL invocationErrorInReflectiveTie(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject1, Object paramObject2) {
    INTERNAL iNTERNAL = new INTERNAL(1398079773, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[2];
      arrayOfObject[0] = paramObject1;
      arrayOfObject[1] = paramObject2;
      doLog(Level.WARNING, "ORBUTIL.invocationErrorInReflectiveTie", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL invocationErrorInReflectiveTie(CompletionStatus paramCompletionStatus, Object paramObject1, Object paramObject2) { return invocationErrorInReflectiveTie(paramCompletionStatus, null, paramObject1, paramObject2); }
  
  public INTERNAL invocationErrorInReflectiveTie(Throwable paramThrowable, Object paramObject1, Object paramObject2) { return invocationErrorInReflectiveTie(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject1, paramObject2); }
  
  public INTERNAL invocationErrorInReflectiveTie(Object paramObject1, Object paramObject2) { return invocationErrorInReflectiveTie(CompletionStatus.COMPLETED_NO, null, paramObject1, paramObject2); }
  
  public INTERNAL badHelperWriteMethod(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    INTERNAL iNTERNAL = new INTERNAL(1398079774, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.WARNING, "ORBUTIL.badHelperWriteMethod", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL badHelperWriteMethod(CompletionStatus paramCompletionStatus, Object paramObject) { return badHelperWriteMethod(paramCompletionStatus, null, paramObject); }
  
  public INTERNAL badHelperWriteMethod(Throwable paramThrowable, Object paramObject) { return badHelperWriteMethod(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public INTERNAL badHelperWriteMethod(Object paramObject) { return badHelperWriteMethod(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public INTERNAL badHelperReadMethod(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    INTERNAL iNTERNAL = new INTERNAL(1398079775, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.WARNING, "ORBUTIL.badHelperReadMethod", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL badHelperReadMethod(CompletionStatus paramCompletionStatus, Object paramObject) { return badHelperReadMethod(paramCompletionStatus, null, paramObject); }
  
  public INTERNAL badHelperReadMethod(Throwable paramThrowable, Object paramObject) { return badHelperReadMethod(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public INTERNAL badHelperReadMethod(Object paramObject) { return badHelperReadMethod(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public INTERNAL badHelperIdMethod(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    INTERNAL iNTERNAL = new INTERNAL(1398079776, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.WARNING, "ORBUTIL.badHelperIdMethod", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL badHelperIdMethod(CompletionStatus paramCompletionStatus, Object paramObject) { return badHelperIdMethod(paramCompletionStatus, null, paramObject); }
  
  public INTERNAL badHelperIdMethod(Throwable paramThrowable, Object paramObject) { return badHelperIdMethod(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public INTERNAL badHelperIdMethod(Object paramObject) { return badHelperIdMethod(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public INTERNAL writeUndeclaredException(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    INTERNAL iNTERNAL = new INTERNAL(1398079777, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.WARNING, "ORBUTIL.writeUndeclaredException", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL writeUndeclaredException(CompletionStatus paramCompletionStatus, Object paramObject) { return writeUndeclaredException(paramCompletionStatus, null, paramObject); }
  
  public INTERNAL writeUndeclaredException(Throwable paramThrowable, Object paramObject) { return writeUndeclaredException(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public INTERNAL writeUndeclaredException(Object paramObject) { return writeUndeclaredException(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public INTERNAL readUndeclaredException(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    INTERNAL iNTERNAL = new INTERNAL(1398079778, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.WARNING, "ORBUTIL.readUndeclaredException", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL readUndeclaredException(CompletionStatus paramCompletionStatus, Object paramObject) { return readUndeclaredException(paramCompletionStatus, null, paramObject); }
  
  public INTERNAL readUndeclaredException(Throwable paramThrowable, Object paramObject) { return readUndeclaredException(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public INTERNAL readUndeclaredException(Object paramObject) { return readUndeclaredException(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public INTERNAL unableToSetSocketFactoryOrb(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079779, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.unableToSetSocketFactoryOrb", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL unableToSetSocketFactoryOrb(CompletionStatus paramCompletionStatus) { return unableToSetSocketFactoryOrb(paramCompletionStatus, null); }
  
  public INTERNAL unableToSetSocketFactoryOrb(Throwable paramThrowable) { return unableToSetSocketFactoryOrb(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL unableToSetSocketFactoryOrb() { return unableToSetSocketFactoryOrb(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL unexpectedException(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079780, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.unexpectedException", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL unexpectedException(CompletionStatus paramCompletionStatus) { return unexpectedException(paramCompletionStatus, null); }
  
  public INTERNAL unexpectedException(Throwable paramThrowable) { return unexpectedException(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL unexpectedException() { return unexpectedException(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL noInvocationHandler(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    INTERNAL iNTERNAL = new INTERNAL(1398079781, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.WARNING, "ORBUTIL.noInvocationHandler", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL noInvocationHandler(CompletionStatus paramCompletionStatus, Object paramObject) { return noInvocationHandler(paramCompletionStatus, null, paramObject); }
  
  public INTERNAL noInvocationHandler(Throwable paramThrowable, Object paramObject) { return noInvocationHandler(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public INTERNAL noInvocationHandler(Object paramObject) { return noInvocationHandler(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public INTERNAL invalidBuffMgrStrategy(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    INTERNAL iNTERNAL = new INTERNAL(1398079782, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.WARNING, "ORBUTIL.invalidBuffMgrStrategy", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL invalidBuffMgrStrategy(CompletionStatus paramCompletionStatus, Object paramObject) { return invalidBuffMgrStrategy(paramCompletionStatus, null, paramObject); }
  
  public INTERNAL invalidBuffMgrStrategy(Throwable paramThrowable, Object paramObject) { return invalidBuffMgrStrategy(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public INTERNAL invalidBuffMgrStrategy(Object paramObject) { return invalidBuffMgrStrategy(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public INTERNAL javaStreamInitFailed(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079783, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.javaStreamInitFailed", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL javaStreamInitFailed(CompletionStatus paramCompletionStatus) { return javaStreamInitFailed(paramCompletionStatus, null); }
  
  public INTERNAL javaStreamInitFailed(Throwable paramThrowable) { return javaStreamInitFailed(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL javaStreamInitFailed() { return javaStreamInitFailed(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL duplicateOrbVersionServiceContext(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079784, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.duplicateOrbVersionServiceContext", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL duplicateOrbVersionServiceContext(CompletionStatus paramCompletionStatus) { return duplicateOrbVersionServiceContext(paramCompletionStatus, null); }
  
  public INTERNAL duplicateOrbVersionServiceContext(Throwable paramThrowable) { return duplicateOrbVersionServiceContext(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL duplicateOrbVersionServiceContext() { return duplicateOrbVersionServiceContext(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL duplicateSendingContextServiceContext(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079785, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.duplicateSendingContextServiceContext", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL duplicateSendingContextServiceContext(CompletionStatus paramCompletionStatus) { return duplicateSendingContextServiceContext(paramCompletionStatus, null); }
  
  public INTERNAL duplicateSendingContextServiceContext(Throwable paramThrowable) { return duplicateSendingContextServiceContext(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL duplicateSendingContextServiceContext() { return duplicateSendingContextServiceContext(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL workQueueThreadInterrupted(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject1, Object paramObject2) {
    INTERNAL iNTERNAL = new INTERNAL(1398079786, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = new Object[2];
      arrayOfObject[0] = paramObject1;
      arrayOfObject[1] = paramObject2;
      doLog(Level.FINE, "ORBUTIL.workQueueThreadInterrupted", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL workQueueThreadInterrupted(CompletionStatus paramCompletionStatus, Object paramObject1, Object paramObject2) { return workQueueThreadInterrupted(paramCompletionStatus, null, paramObject1, paramObject2); }
  
  public INTERNAL workQueueThreadInterrupted(Throwable paramThrowable, Object paramObject1, Object paramObject2) { return workQueueThreadInterrupted(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject1, paramObject2); }
  
  public INTERNAL workQueueThreadInterrupted(Object paramObject1, Object paramObject2) { return workQueueThreadInterrupted(CompletionStatus.COMPLETED_NO, null, paramObject1, paramObject2); }
  
  public INTERNAL workerThreadCreated(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject1, Object paramObject2) {
    INTERNAL iNTERNAL = new INTERNAL(1398079792, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = new Object[2];
      arrayOfObject[0] = paramObject1;
      arrayOfObject[1] = paramObject2;
      doLog(Level.FINE, "ORBUTIL.workerThreadCreated", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL workerThreadCreated(CompletionStatus paramCompletionStatus, Object paramObject1, Object paramObject2) { return workerThreadCreated(paramCompletionStatus, null, paramObject1, paramObject2); }
  
  public INTERNAL workerThreadCreated(Throwable paramThrowable, Object paramObject1, Object paramObject2) { return workerThreadCreated(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject1, paramObject2); }
  
  public INTERNAL workerThreadCreated(Object paramObject1, Object paramObject2) { return workerThreadCreated(CompletionStatus.COMPLETED_NO, null, paramObject1, paramObject2); }
  
  public INTERNAL workerThreadThrowableFromRequestWork(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject1, Object paramObject2, Object paramObject3) {
    INTERNAL iNTERNAL = new INTERNAL(1398079797, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = new Object[3];
      arrayOfObject[0] = paramObject1;
      arrayOfObject[1] = paramObject2;
      arrayOfObject[2] = paramObject3;
      doLog(Level.FINE, "ORBUTIL.workerThreadThrowableFromRequestWork", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL workerThreadThrowableFromRequestWork(CompletionStatus paramCompletionStatus, Object paramObject1, Object paramObject2, Object paramObject3) { return workerThreadThrowableFromRequestWork(paramCompletionStatus, null, paramObject1, paramObject2, paramObject3); }
  
  public INTERNAL workerThreadThrowableFromRequestWork(Throwable paramThrowable, Object paramObject1, Object paramObject2, Object paramObject3) { return workerThreadThrowableFromRequestWork(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject1, paramObject2, paramObject3); }
  
  public INTERNAL workerThreadThrowableFromRequestWork(Object paramObject1, Object paramObject2, Object paramObject3) { return workerThreadThrowableFromRequestWork(CompletionStatus.COMPLETED_NO, null, paramObject1, paramObject2, paramObject3); }
  
  public INTERNAL workerThreadNotNeeded(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject1, Object paramObject2, Object paramObject3) {
    INTERNAL iNTERNAL = new INTERNAL(1398079798, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = new Object[3];
      arrayOfObject[0] = paramObject1;
      arrayOfObject[1] = paramObject2;
      arrayOfObject[2] = paramObject3;
      doLog(Level.FINE, "ORBUTIL.workerThreadNotNeeded", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL workerThreadNotNeeded(CompletionStatus paramCompletionStatus, Object paramObject1, Object paramObject2, Object paramObject3) { return workerThreadNotNeeded(paramCompletionStatus, null, paramObject1, paramObject2, paramObject3); }
  
  public INTERNAL workerThreadNotNeeded(Throwable paramThrowable, Object paramObject1, Object paramObject2, Object paramObject3) { return workerThreadNotNeeded(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject1, paramObject2, paramObject3); }
  
  public INTERNAL workerThreadNotNeeded(Object paramObject1, Object paramObject2, Object paramObject3) { return workerThreadNotNeeded(CompletionStatus.COMPLETED_NO, null, paramObject1, paramObject2, paramObject3); }
  
  public INTERNAL workerThreadDoWorkThrowable(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject1, Object paramObject2) {
    INTERNAL iNTERNAL = new INTERNAL(1398079799, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = new Object[2];
      arrayOfObject[0] = paramObject1;
      arrayOfObject[1] = paramObject2;
      doLog(Level.FINE, "ORBUTIL.workerThreadDoWorkThrowable", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL workerThreadDoWorkThrowable(CompletionStatus paramCompletionStatus, Object paramObject1, Object paramObject2) { return workerThreadDoWorkThrowable(paramCompletionStatus, null, paramObject1, paramObject2); }
  
  public INTERNAL workerThreadDoWorkThrowable(Throwable paramThrowable, Object paramObject1, Object paramObject2) { return workerThreadDoWorkThrowable(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject1, paramObject2); }
  
  public INTERNAL workerThreadDoWorkThrowable(Object paramObject1, Object paramObject2) { return workerThreadDoWorkThrowable(CompletionStatus.COMPLETED_NO, null, paramObject1, paramObject2); }
  
  public INTERNAL workerThreadCaughtUnexpectedThrowable(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject1, Object paramObject2) {
    INTERNAL iNTERNAL = new INTERNAL(1398079800, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[2];
      arrayOfObject[0] = paramObject1;
      arrayOfObject[1] = paramObject2;
      doLog(Level.WARNING, "ORBUTIL.workerThreadCaughtUnexpectedThrowable", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL workerThreadCaughtUnexpectedThrowable(CompletionStatus paramCompletionStatus, Object paramObject1, Object paramObject2) { return workerThreadCaughtUnexpectedThrowable(paramCompletionStatus, null, paramObject1, paramObject2); }
  
  public INTERNAL workerThreadCaughtUnexpectedThrowable(Throwable paramThrowable, Object paramObject1, Object paramObject2) { return workerThreadCaughtUnexpectedThrowable(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject1, paramObject2); }
  
  public INTERNAL workerThreadCaughtUnexpectedThrowable(Object paramObject1, Object paramObject2) { return workerThreadCaughtUnexpectedThrowable(CompletionStatus.COMPLETED_NO, null, paramObject1, paramObject2); }
  
  public INTERNAL workerThreadCreationFailure(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    INTERNAL iNTERNAL = new INTERNAL(1398079801, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.SEVERE)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.SEVERE, "ORBUTIL.workerThreadCreationFailure", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL workerThreadCreationFailure(CompletionStatus paramCompletionStatus, Object paramObject) { return workerThreadCreationFailure(paramCompletionStatus, null, paramObject); }
  
  public INTERNAL workerThreadCreationFailure(Throwable paramThrowable, Object paramObject) { return workerThreadCreationFailure(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public INTERNAL workerThreadCreationFailure(Object paramObject) { return workerThreadCreationFailure(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public INTERNAL workerThreadSetNameFailure(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject1, Object paramObject2, Object paramObject3) {
    INTERNAL iNTERNAL = new INTERNAL(1398079802, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[3];
      arrayOfObject[0] = paramObject1;
      arrayOfObject[1] = paramObject2;
      arrayOfObject[2] = paramObject3;
      doLog(Level.WARNING, "ORBUTIL.workerThreadSetNameFailure", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL workerThreadSetNameFailure(CompletionStatus paramCompletionStatus, Object paramObject1, Object paramObject2, Object paramObject3) { return workerThreadSetNameFailure(paramCompletionStatus, null, paramObject1, paramObject2, paramObject3); }
  
  public INTERNAL workerThreadSetNameFailure(Throwable paramThrowable, Object paramObject1, Object paramObject2, Object paramObject3) { return workerThreadSetNameFailure(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject1, paramObject2, paramObject3); }
  
  public INTERNAL workerThreadSetNameFailure(Object paramObject1, Object paramObject2, Object paramObject3) { return workerThreadSetNameFailure(CompletionStatus.COMPLETED_NO, null, paramObject1, paramObject2, paramObject3); }
  
  public INTERNAL workQueueRequestWorkNoWorkFound(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject1, Object paramObject2) {
    INTERNAL iNTERNAL = new INTERNAL(1398079804, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[2];
      arrayOfObject[0] = paramObject1;
      arrayOfObject[1] = paramObject2;
      doLog(Level.WARNING, "ORBUTIL.workQueueRequestWorkNoWorkFound", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL workQueueRequestWorkNoWorkFound(CompletionStatus paramCompletionStatus, Object paramObject1, Object paramObject2) { return workQueueRequestWorkNoWorkFound(paramCompletionStatus, null, paramObject1, paramObject2); }
  
  public INTERNAL workQueueRequestWorkNoWorkFound(Throwable paramThrowable, Object paramObject1, Object paramObject2) { return workQueueRequestWorkNoWorkFound(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject1, paramObject2); }
  
  public INTERNAL workQueueRequestWorkNoWorkFound(Object paramObject1, Object paramObject2) { return workQueueRequestWorkNoWorkFound(CompletionStatus.COMPLETED_NO, null, paramObject1, paramObject2); }
  
  public INTERNAL threadPoolCloseError(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079814, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.threadPoolCloseError", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL threadPoolCloseError(CompletionStatus paramCompletionStatus) { return threadPoolCloseError(paramCompletionStatus, null); }
  
  public INTERNAL threadPoolCloseError(Throwable paramThrowable) { return threadPoolCloseError(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL threadPoolCloseError() { return threadPoolCloseError(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL threadGroupIsDestroyed(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    INTERNAL iNTERNAL = new INTERNAL(1398079815, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.WARNING, "ORBUTIL.threadGroupIsDestroyed", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL threadGroupIsDestroyed(CompletionStatus paramCompletionStatus, Object paramObject) { return threadGroupIsDestroyed(paramCompletionStatus, null, paramObject); }
  
  public INTERNAL threadGroupIsDestroyed(Throwable paramThrowable, Object paramObject) { return threadGroupIsDestroyed(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public INTERNAL threadGroupIsDestroyed(Object paramObject) { return threadGroupIsDestroyed(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public INTERNAL threadGroupHasActiveThreadsInClose(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject1, Object paramObject2) {
    INTERNAL iNTERNAL = new INTERNAL(1398079816, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[2];
      arrayOfObject[0] = paramObject1;
      arrayOfObject[1] = paramObject2;
      doLog(Level.WARNING, "ORBUTIL.threadGroupHasActiveThreadsInClose", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL threadGroupHasActiveThreadsInClose(CompletionStatus paramCompletionStatus, Object paramObject1, Object paramObject2) { return threadGroupHasActiveThreadsInClose(paramCompletionStatus, null, paramObject1, paramObject2); }
  
  public INTERNAL threadGroupHasActiveThreadsInClose(Throwable paramThrowable, Object paramObject1, Object paramObject2) { return threadGroupHasActiveThreadsInClose(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject1, paramObject2); }
  
  public INTERNAL threadGroupHasActiveThreadsInClose(Object paramObject1, Object paramObject2) { return threadGroupHasActiveThreadsInClose(CompletionStatus.COMPLETED_NO, null, paramObject1, paramObject2); }
  
  public INTERNAL threadGroupHasSubGroupsInClose(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject1, Object paramObject2) {
    INTERNAL iNTERNAL = new INTERNAL(1398079817, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[2];
      arrayOfObject[0] = paramObject1;
      arrayOfObject[1] = paramObject2;
      doLog(Level.WARNING, "ORBUTIL.threadGroupHasSubGroupsInClose", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL threadGroupHasSubGroupsInClose(CompletionStatus paramCompletionStatus, Object paramObject1, Object paramObject2) { return threadGroupHasSubGroupsInClose(paramCompletionStatus, null, paramObject1, paramObject2); }
  
  public INTERNAL threadGroupHasSubGroupsInClose(Throwable paramThrowable, Object paramObject1, Object paramObject2) { return threadGroupHasSubGroupsInClose(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject1, paramObject2); }
  
  public INTERNAL threadGroupHasSubGroupsInClose(Object paramObject1, Object paramObject2) { return threadGroupHasSubGroupsInClose(CompletionStatus.COMPLETED_NO, null, paramObject1, paramObject2); }
  
  public INTERNAL threadGroupDestroyFailed(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    INTERNAL iNTERNAL = new INTERNAL(1398079818, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.WARNING, "ORBUTIL.threadGroupDestroyFailed", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL threadGroupDestroyFailed(CompletionStatus paramCompletionStatus, Object paramObject) { return threadGroupDestroyFailed(paramCompletionStatus, null, paramObject); }
  
  public INTERNAL threadGroupDestroyFailed(Throwable paramThrowable, Object paramObject) { return threadGroupDestroyFailed(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public INTERNAL threadGroupDestroyFailed(Object paramObject) { return threadGroupDestroyFailed(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public INTERNAL interruptedJoinCallWhileClosingThreadPool(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject1, Object paramObject2) {
    INTERNAL iNTERNAL = new INTERNAL(1398079819, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[2];
      arrayOfObject[0] = paramObject1;
      arrayOfObject[1] = paramObject2;
      doLog(Level.WARNING, "ORBUTIL.interruptedJoinCallWhileClosingThreadPool", arrayOfObject, ORBUtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL interruptedJoinCallWhileClosingThreadPool(CompletionStatus paramCompletionStatus, Object paramObject1, Object paramObject2) { return interruptedJoinCallWhileClosingThreadPool(paramCompletionStatus, null, paramObject1, paramObject2); }
  
  public INTERNAL interruptedJoinCallWhileClosingThreadPool(Throwable paramThrowable, Object paramObject1, Object paramObject2) { return interruptedJoinCallWhileClosingThreadPool(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject1, paramObject2); }
  
  public INTERNAL interruptedJoinCallWhileClosingThreadPool(Object paramObject1, Object paramObject2) { return interruptedJoinCallWhileClosingThreadPool(CompletionStatus.COMPLETED_NO, null, paramObject1, paramObject2); }
  
  public MARSHAL chunkOverflow(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    MARSHAL mARSHAL = new MARSHAL(1398079689, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.chunkOverflow", arrayOfObject, ORBUtilSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL chunkOverflow(CompletionStatus paramCompletionStatus) { return chunkOverflow(paramCompletionStatus, null); }
  
  public MARSHAL chunkOverflow(Throwable paramThrowable) { return chunkOverflow(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public MARSHAL chunkOverflow() { return chunkOverflow(CompletionStatus.COMPLETED_NO, null); }
  
  public MARSHAL unexpectedEof(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    MARSHAL mARSHAL = new MARSHAL(1398079690, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.unexpectedEof", arrayOfObject, ORBUtilSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL unexpectedEof(CompletionStatus paramCompletionStatus) { return unexpectedEof(paramCompletionStatus, null); }
  
  public MARSHAL unexpectedEof(Throwable paramThrowable) { return unexpectedEof(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public MARSHAL unexpectedEof() { return unexpectedEof(CompletionStatus.COMPLETED_NO, null); }
  
  public MARSHAL readObjectException(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    MARSHAL mARSHAL = new MARSHAL(1398079691, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.readObjectException", arrayOfObject, ORBUtilSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL readObjectException(CompletionStatus paramCompletionStatus) { return readObjectException(paramCompletionStatus, null); }
  
  public MARSHAL readObjectException(Throwable paramThrowable) { return readObjectException(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public MARSHAL readObjectException() { return readObjectException(CompletionStatus.COMPLETED_NO, null); }
  
  public MARSHAL characterOutofrange(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    MARSHAL mARSHAL = new MARSHAL(1398079692, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.characterOutofrange", arrayOfObject, ORBUtilSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL characterOutofrange(CompletionStatus paramCompletionStatus) { return characterOutofrange(paramCompletionStatus, null); }
  
  public MARSHAL characterOutofrange(Throwable paramThrowable) { return characterOutofrange(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public MARSHAL characterOutofrange() { return characterOutofrange(CompletionStatus.COMPLETED_NO, null); }
  
  public MARSHAL dsiResultException(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    MARSHAL mARSHAL = new MARSHAL(1398079693, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.dsiResultException", arrayOfObject, ORBUtilSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL dsiResultException(CompletionStatus paramCompletionStatus) { return dsiResultException(paramCompletionStatus, null); }
  
  public MARSHAL dsiResultException(Throwable paramThrowable) { return dsiResultException(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public MARSHAL dsiResultException() { return dsiResultException(CompletionStatus.COMPLETED_NO, null); }
  
  public MARSHAL iiopinputstreamGrow(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    MARSHAL mARSHAL = new MARSHAL(1398079694, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.iiopinputstreamGrow", arrayOfObject, ORBUtilSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL iiopinputstreamGrow(CompletionStatus paramCompletionStatus) { return iiopinputstreamGrow(paramCompletionStatus, null); }
  
  public MARSHAL iiopinputstreamGrow(Throwable paramThrowable) { return iiopinputstreamGrow(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public MARSHAL iiopinputstreamGrow() { return iiopinputstreamGrow(CompletionStatus.COMPLETED_NO, null); }
  
  public MARSHAL endOfStream(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    MARSHAL mARSHAL = new MARSHAL(1398079695, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = null;
      doLog(Level.FINE, "ORBUTIL.endOfStream", arrayOfObject, ORBUtilSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL endOfStream(CompletionStatus paramCompletionStatus) { return endOfStream(paramCompletionStatus, null); }
  
  public MARSHAL endOfStream(Throwable paramThrowable) { return endOfStream(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public MARSHAL endOfStream() { return endOfStream(CompletionStatus.COMPLETED_NO, null); }
  
  public MARSHAL invalidObjectKey(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    MARSHAL mARSHAL = new MARSHAL(1398079696, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.invalidObjectKey", arrayOfObject, ORBUtilSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL invalidObjectKey(CompletionStatus paramCompletionStatus) { return invalidObjectKey(paramCompletionStatus, null); }
  
  public MARSHAL invalidObjectKey(Throwable paramThrowable) { return invalidObjectKey(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public MARSHAL invalidObjectKey() { return invalidObjectKey(CompletionStatus.COMPLETED_NO, null); }
  
  public MARSHAL malformedUrl(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject1, Object paramObject2) {
    MARSHAL mARSHAL = new MARSHAL(1398079697, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[2];
      arrayOfObject[0] = paramObject1;
      arrayOfObject[1] = paramObject2;
      doLog(Level.WARNING, "ORBUTIL.malformedUrl", arrayOfObject, ORBUtilSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL malformedUrl(CompletionStatus paramCompletionStatus, Object paramObject1, Object paramObject2) { return malformedUrl(paramCompletionStatus, null, paramObject1, paramObject2); }
  
  public MARSHAL malformedUrl(Throwable paramThrowable, Object paramObject1, Object paramObject2) { return malformedUrl(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject1, paramObject2); }
  
  public MARSHAL malformedUrl(Object paramObject1, Object paramObject2) { return malformedUrl(CompletionStatus.COMPLETED_NO, null, paramObject1, paramObject2); }
  
  public MARSHAL valuehandlerReadError(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    MARSHAL mARSHAL = new MARSHAL(1398079698, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.valuehandlerReadError", arrayOfObject, ORBUtilSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL valuehandlerReadError(CompletionStatus paramCompletionStatus) { return valuehandlerReadError(paramCompletionStatus, null); }
  
  public MARSHAL valuehandlerReadError(Throwable paramThrowable) { return valuehandlerReadError(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public MARSHAL valuehandlerReadError() { return valuehandlerReadError(CompletionStatus.COMPLETED_NO, null); }
  
  public MARSHAL valuehandlerReadException(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    MARSHAL mARSHAL = new MARSHAL(1398079699, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.valuehandlerReadException", arrayOfObject, ORBUtilSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL valuehandlerReadException(CompletionStatus paramCompletionStatus) { return valuehandlerReadException(paramCompletionStatus, null); }
  
  public MARSHAL valuehandlerReadException(Throwable paramThrowable) { return valuehandlerReadException(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public MARSHAL valuehandlerReadException() { return valuehandlerReadException(CompletionStatus.COMPLETED_NO, null); }
  
  public MARSHAL badKind(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    MARSHAL mARSHAL = new MARSHAL(1398079700, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.badKind", arrayOfObject, ORBUtilSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL badKind(CompletionStatus paramCompletionStatus) { return badKind(paramCompletionStatus, null); }
  
  public MARSHAL badKind(Throwable paramThrowable) { return badKind(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public MARSHAL badKind() { return badKind(CompletionStatus.COMPLETED_NO, null); }
  
  public MARSHAL cnfeReadClass(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    MARSHAL mARSHAL = new MARSHAL(1398079701, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.WARNING, "ORBUTIL.cnfeReadClass", arrayOfObject, ORBUtilSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL cnfeReadClass(CompletionStatus paramCompletionStatus, Object paramObject) { return cnfeReadClass(paramCompletionStatus, null, paramObject); }
  
  public MARSHAL cnfeReadClass(Throwable paramThrowable, Object paramObject) { return cnfeReadClass(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public MARSHAL cnfeReadClass(Object paramObject) { return cnfeReadClass(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public MARSHAL badRepIdIndirection(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    MARSHAL mARSHAL = new MARSHAL(1398079702, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.WARNING, "ORBUTIL.badRepIdIndirection", arrayOfObject, ORBUtilSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL badRepIdIndirection(CompletionStatus paramCompletionStatus, Object paramObject) { return badRepIdIndirection(paramCompletionStatus, null, paramObject); }
  
  public MARSHAL badRepIdIndirection(Throwable paramThrowable, Object paramObject) { return badRepIdIndirection(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public MARSHAL badRepIdIndirection(Object paramObject) { return badRepIdIndirection(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public MARSHAL badCodebaseIndirection(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    MARSHAL mARSHAL = new MARSHAL(1398079703, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.WARNING, "ORBUTIL.badCodebaseIndirection", arrayOfObject, ORBUtilSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL badCodebaseIndirection(CompletionStatus paramCompletionStatus, Object paramObject) { return badCodebaseIndirection(paramCompletionStatus, null, paramObject); }
  
  public MARSHAL badCodebaseIndirection(Throwable paramThrowable, Object paramObject) { return badCodebaseIndirection(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public MARSHAL badCodebaseIndirection(Object paramObject) { return badCodebaseIndirection(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public MARSHAL unknownCodeset(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    MARSHAL mARSHAL = new MARSHAL(1398079704, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.WARNING, "ORBUTIL.unknownCodeset", arrayOfObject, ORBUtilSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL unknownCodeset(CompletionStatus paramCompletionStatus, Object paramObject) { return unknownCodeset(paramCompletionStatus, null, paramObject); }
  
  public MARSHAL unknownCodeset(Throwable paramThrowable, Object paramObject) { return unknownCodeset(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public MARSHAL unknownCodeset(Object paramObject) { return unknownCodeset(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public MARSHAL wcharDataInGiop10(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    MARSHAL mARSHAL = new MARSHAL(1398079705, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.wcharDataInGiop10", arrayOfObject, ORBUtilSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL wcharDataInGiop10(CompletionStatus paramCompletionStatus) { return wcharDataInGiop10(paramCompletionStatus, null); }
  
  public MARSHAL wcharDataInGiop10(Throwable paramThrowable) { return wcharDataInGiop10(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public MARSHAL wcharDataInGiop10() { return wcharDataInGiop10(CompletionStatus.COMPLETED_NO, null); }
  
  public MARSHAL negativeStringLength(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    MARSHAL mARSHAL = new MARSHAL(1398079706, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.WARNING, "ORBUTIL.negativeStringLength", arrayOfObject, ORBUtilSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL negativeStringLength(CompletionStatus paramCompletionStatus, Object paramObject) { return negativeStringLength(paramCompletionStatus, null, paramObject); }
  
  public MARSHAL negativeStringLength(Throwable paramThrowable, Object paramObject) { return negativeStringLength(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public MARSHAL negativeStringLength(Object paramObject) { return negativeStringLength(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public MARSHAL expectedTypeNullAndNoRepId(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    MARSHAL mARSHAL = new MARSHAL(1398079707, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.expectedTypeNullAndNoRepId", arrayOfObject, ORBUtilSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL expectedTypeNullAndNoRepId(CompletionStatus paramCompletionStatus) { return expectedTypeNullAndNoRepId(paramCompletionStatus, null); }
  
  public MARSHAL expectedTypeNullAndNoRepId(Throwable paramThrowable) { return expectedTypeNullAndNoRepId(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public MARSHAL expectedTypeNullAndNoRepId() { return expectedTypeNullAndNoRepId(CompletionStatus.COMPLETED_NO, null); }
  
  public MARSHAL readValueAndNoRepId(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    MARSHAL mARSHAL = new MARSHAL(1398079708, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.readValueAndNoRepId", arrayOfObject, ORBUtilSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL readValueAndNoRepId(CompletionStatus paramCompletionStatus) { return readValueAndNoRepId(paramCompletionStatus, null); }
  
  public MARSHAL readValueAndNoRepId(Throwable paramThrowable) { return readValueAndNoRepId(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public MARSHAL readValueAndNoRepId() { return readValueAndNoRepId(CompletionStatus.COMPLETED_NO, null); }
  
  public MARSHAL unexpectedEnclosingValuetype(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject1, Object paramObject2) {
    MARSHAL mARSHAL = new MARSHAL(1398079710, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[2];
      arrayOfObject[0] = paramObject1;
      arrayOfObject[1] = paramObject2;
      doLog(Level.WARNING, "ORBUTIL.unexpectedEnclosingValuetype", arrayOfObject, ORBUtilSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL unexpectedEnclosingValuetype(CompletionStatus paramCompletionStatus, Object paramObject1, Object paramObject2) { return unexpectedEnclosingValuetype(paramCompletionStatus, null, paramObject1, paramObject2); }
  
  public MARSHAL unexpectedEnclosingValuetype(Throwable paramThrowable, Object paramObject1, Object paramObject2) { return unexpectedEnclosingValuetype(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject1, paramObject2); }
  
  public MARSHAL unexpectedEnclosingValuetype(Object paramObject1, Object paramObject2) { return unexpectedEnclosingValuetype(CompletionStatus.COMPLETED_NO, null, paramObject1, paramObject2); }
  
  public MARSHAL positiveEndTag(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject1, Object paramObject2) {
    MARSHAL mARSHAL = new MARSHAL(1398079711, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[2];
      arrayOfObject[0] = paramObject1;
      arrayOfObject[1] = paramObject2;
      doLog(Level.WARNING, "ORBUTIL.positiveEndTag", arrayOfObject, ORBUtilSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL positiveEndTag(CompletionStatus paramCompletionStatus, Object paramObject1, Object paramObject2) { return positiveEndTag(paramCompletionStatus, null, paramObject1, paramObject2); }
  
  public MARSHAL positiveEndTag(Throwable paramThrowable, Object paramObject1, Object paramObject2) { return positiveEndTag(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject1, paramObject2); }
  
  public MARSHAL positiveEndTag(Object paramObject1, Object paramObject2) { return positiveEndTag(CompletionStatus.COMPLETED_NO, null, paramObject1, paramObject2); }
  
  public MARSHAL nullOutCall(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    MARSHAL mARSHAL = new MARSHAL(1398079712, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.nullOutCall", arrayOfObject, ORBUtilSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL nullOutCall(CompletionStatus paramCompletionStatus) { return nullOutCall(paramCompletionStatus, null); }
  
  public MARSHAL nullOutCall(Throwable paramThrowable) { return nullOutCall(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public MARSHAL nullOutCall() { return nullOutCall(CompletionStatus.COMPLETED_NO, null); }
  
  public MARSHAL writeLocalObject(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    MARSHAL mARSHAL = new MARSHAL(1398079713, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.writeLocalObject", arrayOfObject, ORBUtilSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL writeLocalObject(CompletionStatus paramCompletionStatus) { return writeLocalObject(paramCompletionStatus, null); }
  
  public MARSHAL writeLocalObject(Throwable paramThrowable) { return writeLocalObject(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public MARSHAL writeLocalObject() { return writeLocalObject(CompletionStatus.COMPLETED_NO, null); }
  
  public MARSHAL badInsertobjParam(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    MARSHAL mARSHAL = new MARSHAL(1398079714, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.WARNING, "ORBUTIL.badInsertobjParam", arrayOfObject, ORBUtilSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL badInsertobjParam(CompletionStatus paramCompletionStatus, Object paramObject) { return badInsertobjParam(paramCompletionStatus, null, paramObject); }
  
  public MARSHAL badInsertobjParam(Throwable paramThrowable, Object paramObject) { return badInsertobjParam(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public MARSHAL badInsertobjParam(Object paramObject) { return badInsertobjParam(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public MARSHAL customWrapperWithCodebase(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    MARSHAL mARSHAL = new MARSHAL(1398079715, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.customWrapperWithCodebase", arrayOfObject, ORBUtilSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL customWrapperWithCodebase(CompletionStatus paramCompletionStatus) { return customWrapperWithCodebase(paramCompletionStatus, null); }
  
  public MARSHAL customWrapperWithCodebase(Throwable paramThrowable) { return customWrapperWithCodebase(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public MARSHAL customWrapperWithCodebase() { return customWrapperWithCodebase(CompletionStatus.COMPLETED_NO, null); }
  
  public MARSHAL customWrapperIndirection(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    MARSHAL mARSHAL = new MARSHAL(1398079716, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.customWrapperIndirection", arrayOfObject, ORBUtilSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL customWrapperIndirection(CompletionStatus paramCompletionStatus) { return customWrapperIndirection(paramCompletionStatus, null); }
  
  public MARSHAL customWrapperIndirection(Throwable paramThrowable) { return customWrapperIndirection(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public MARSHAL customWrapperIndirection() { return customWrapperIndirection(CompletionStatus.COMPLETED_NO, null); }
  
  public MARSHAL customWrapperNotSingleRepid(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    MARSHAL mARSHAL = new MARSHAL(1398079717, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.customWrapperNotSingleRepid", arrayOfObject, ORBUtilSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL customWrapperNotSingleRepid(CompletionStatus paramCompletionStatus) { return customWrapperNotSingleRepid(paramCompletionStatus, null); }
  
  public MARSHAL customWrapperNotSingleRepid(Throwable paramThrowable) { return customWrapperNotSingleRepid(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public MARSHAL customWrapperNotSingleRepid() { return customWrapperNotSingleRepid(CompletionStatus.COMPLETED_NO, null); }
  
  public MARSHAL badValueTag(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    MARSHAL mARSHAL = new MARSHAL(1398079718, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.WARNING, "ORBUTIL.badValueTag", arrayOfObject, ORBUtilSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL badValueTag(CompletionStatus paramCompletionStatus, Object paramObject) { return badValueTag(paramCompletionStatus, null, paramObject); }
  
  public MARSHAL badValueTag(Throwable paramThrowable, Object paramObject) { return badValueTag(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public MARSHAL badValueTag(Object paramObject) { return badValueTag(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public MARSHAL badTypecodeForCustomValue(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    MARSHAL mARSHAL = new MARSHAL(1398079719, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.badTypecodeForCustomValue", arrayOfObject, ORBUtilSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL badTypecodeForCustomValue(CompletionStatus paramCompletionStatus) { return badTypecodeForCustomValue(paramCompletionStatus, null); }
  
  public MARSHAL badTypecodeForCustomValue(Throwable paramThrowable) { return badTypecodeForCustomValue(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public MARSHAL badTypecodeForCustomValue() { return badTypecodeForCustomValue(CompletionStatus.COMPLETED_NO, null); }
  
  public MARSHAL errorInvokingHelperWrite(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    MARSHAL mARSHAL = new MARSHAL(1398079720, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.errorInvokingHelperWrite", arrayOfObject, ORBUtilSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL errorInvokingHelperWrite(CompletionStatus paramCompletionStatus) { return errorInvokingHelperWrite(paramCompletionStatus, null); }
  
  public MARSHAL errorInvokingHelperWrite(Throwable paramThrowable) { return errorInvokingHelperWrite(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public MARSHAL errorInvokingHelperWrite() { return errorInvokingHelperWrite(CompletionStatus.COMPLETED_NO, null); }
  
  public MARSHAL badDigitInFixed(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    MARSHAL mARSHAL = new MARSHAL(1398079721, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.badDigitInFixed", arrayOfObject, ORBUtilSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL badDigitInFixed(CompletionStatus paramCompletionStatus) { return badDigitInFixed(paramCompletionStatus, null); }
  
  public MARSHAL badDigitInFixed(Throwable paramThrowable) { return badDigitInFixed(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public MARSHAL badDigitInFixed() { return badDigitInFixed(CompletionStatus.COMPLETED_NO, null); }
  
  public MARSHAL refTypeIndirType(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    MARSHAL mARSHAL = new MARSHAL(1398079722, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.refTypeIndirType", arrayOfObject, ORBUtilSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL refTypeIndirType(CompletionStatus paramCompletionStatus) { return refTypeIndirType(paramCompletionStatus, null); }
  
  public MARSHAL refTypeIndirType(Throwable paramThrowable) { return refTypeIndirType(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public MARSHAL refTypeIndirType() { return refTypeIndirType(CompletionStatus.COMPLETED_NO, null); }
  
  public MARSHAL badReservedLength(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    MARSHAL mARSHAL = new MARSHAL(1398079723, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.badReservedLength", arrayOfObject, ORBUtilSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL badReservedLength(CompletionStatus paramCompletionStatus) { return badReservedLength(paramCompletionStatus, null); }
  
  public MARSHAL badReservedLength(Throwable paramThrowable) { return badReservedLength(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public MARSHAL badReservedLength() { return badReservedLength(CompletionStatus.COMPLETED_NO, null); }
  
  public MARSHAL nullNotAllowed(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    MARSHAL mARSHAL = new MARSHAL(1398079724, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.nullNotAllowed", arrayOfObject, ORBUtilSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL nullNotAllowed(CompletionStatus paramCompletionStatus) { return nullNotAllowed(paramCompletionStatus, null); }
  
  public MARSHAL nullNotAllowed(Throwable paramThrowable) { return nullNotAllowed(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public MARSHAL nullNotAllowed() { return nullNotAllowed(CompletionStatus.COMPLETED_NO, null); }
  
  public MARSHAL unionDiscriminatorError(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    MARSHAL mARSHAL = new MARSHAL(1398079726, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.unionDiscriminatorError", arrayOfObject, ORBUtilSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL unionDiscriminatorError(CompletionStatus paramCompletionStatus) { return unionDiscriminatorError(paramCompletionStatus, null); }
  
  public MARSHAL unionDiscriminatorError(Throwable paramThrowable) { return unionDiscriminatorError(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public MARSHAL unionDiscriminatorError() { return unionDiscriminatorError(CompletionStatus.COMPLETED_NO, null); }
  
  public MARSHAL cannotMarshalNative(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    MARSHAL mARSHAL = new MARSHAL(1398079727, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.cannotMarshalNative", arrayOfObject, ORBUtilSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL cannotMarshalNative(CompletionStatus paramCompletionStatus) { return cannotMarshalNative(paramCompletionStatus, null); }
  
  public MARSHAL cannotMarshalNative(Throwable paramThrowable) { return cannotMarshalNative(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public MARSHAL cannotMarshalNative() { return cannotMarshalNative(CompletionStatus.COMPLETED_NO, null); }
  
  public MARSHAL cannotMarshalBadTckind(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    MARSHAL mARSHAL = new MARSHAL(1398079728, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.cannotMarshalBadTckind", arrayOfObject, ORBUtilSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL cannotMarshalBadTckind(CompletionStatus paramCompletionStatus) { return cannotMarshalBadTckind(paramCompletionStatus, null); }
  
  public MARSHAL cannotMarshalBadTckind(Throwable paramThrowable) { return cannotMarshalBadTckind(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public MARSHAL cannotMarshalBadTckind() { return cannotMarshalBadTckind(CompletionStatus.COMPLETED_NO, null); }
  
  public MARSHAL invalidIndirection(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    MARSHAL mARSHAL = new MARSHAL(1398079729, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.WARNING, "ORBUTIL.invalidIndirection", arrayOfObject, ORBUtilSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL invalidIndirection(CompletionStatus paramCompletionStatus, Object paramObject) { return invalidIndirection(paramCompletionStatus, null, paramObject); }
  
  public MARSHAL invalidIndirection(Throwable paramThrowable, Object paramObject) { return invalidIndirection(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public MARSHAL invalidIndirection(Object paramObject) { return invalidIndirection(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public MARSHAL indirectionNotFound(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    MARSHAL mARSHAL = new MARSHAL(1398079730, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.FINE, "ORBUTIL.indirectionNotFound", arrayOfObject, ORBUtilSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL indirectionNotFound(CompletionStatus paramCompletionStatus, Object paramObject) { return indirectionNotFound(paramCompletionStatus, null, paramObject); }
  
  public MARSHAL indirectionNotFound(Throwable paramThrowable, Object paramObject) { return indirectionNotFound(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public MARSHAL indirectionNotFound(Object paramObject) { return indirectionNotFound(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public MARSHAL recursiveTypecodeError(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    MARSHAL mARSHAL = new MARSHAL(1398079731, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.recursiveTypecodeError", arrayOfObject, ORBUtilSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL recursiveTypecodeError(CompletionStatus paramCompletionStatus) { return recursiveTypecodeError(paramCompletionStatus, null); }
  
  public MARSHAL recursiveTypecodeError(Throwable paramThrowable) { return recursiveTypecodeError(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public MARSHAL recursiveTypecodeError() { return recursiveTypecodeError(CompletionStatus.COMPLETED_NO, null); }
  
  public MARSHAL invalidSimpleTypecode(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    MARSHAL mARSHAL = new MARSHAL(1398079732, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.invalidSimpleTypecode", arrayOfObject, ORBUtilSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL invalidSimpleTypecode(CompletionStatus paramCompletionStatus) { return invalidSimpleTypecode(paramCompletionStatus, null); }
  
  public MARSHAL invalidSimpleTypecode(Throwable paramThrowable) { return invalidSimpleTypecode(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public MARSHAL invalidSimpleTypecode() { return invalidSimpleTypecode(CompletionStatus.COMPLETED_NO, null); }
  
  public MARSHAL invalidComplexTypecode(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    MARSHAL mARSHAL = new MARSHAL(1398079733, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.invalidComplexTypecode", arrayOfObject, ORBUtilSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL invalidComplexTypecode(CompletionStatus paramCompletionStatus) { return invalidComplexTypecode(paramCompletionStatus, null); }
  
  public MARSHAL invalidComplexTypecode(Throwable paramThrowable) { return invalidComplexTypecode(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public MARSHAL invalidComplexTypecode() { return invalidComplexTypecode(CompletionStatus.COMPLETED_NO, null); }
  
  public MARSHAL invalidTypecodeKindMarshal(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    MARSHAL mARSHAL = new MARSHAL(1398079734, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.invalidTypecodeKindMarshal", arrayOfObject, ORBUtilSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL invalidTypecodeKindMarshal(CompletionStatus paramCompletionStatus) { return invalidTypecodeKindMarshal(paramCompletionStatus, null); }
  
  public MARSHAL invalidTypecodeKindMarshal(Throwable paramThrowable) { return invalidTypecodeKindMarshal(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public MARSHAL invalidTypecodeKindMarshal() { return invalidTypecodeKindMarshal(CompletionStatus.COMPLETED_NO, null); }
  
  public MARSHAL unexpectedUnionDefault(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    MARSHAL mARSHAL = new MARSHAL(1398079735, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.unexpectedUnionDefault", arrayOfObject, ORBUtilSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL unexpectedUnionDefault(CompletionStatus paramCompletionStatus) { return unexpectedUnionDefault(paramCompletionStatus, null); }
  
  public MARSHAL unexpectedUnionDefault(Throwable paramThrowable) { return unexpectedUnionDefault(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public MARSHAL unexpectedUnionDefault() { return unexpectedUnionDefault(CompletionStatus.COMPLETED_NO, null); }
  
  public MARSHAL illegalUnionDiscriminatorType(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    MARSHAL mARSHAL = new MARSHAL(1398079736, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.illegalUnionDiscriminatorType", arrayOfObject, ORBUtilSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL illegalUnionDiscriminatorType(CompletionStatus paramCompletionStatus) { return illegalUnionDiscriminatorType(paramCompletionStatus, null); }
  
  public MARSHAL illegalUnionDiscriminatorType(Throwable paramThrowable) { return illegalUnionDiscriminatorType(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public MARSHAL illegalUnionDiscriminatorType() { return illegalUnionDiscriminatorType(CompletionStatus.COMPLETED_NO, null); }
  
  public MARSHAL couldNotSkipBytes(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject1, Object paramObject2) {
    MARSHAL mARSHAL = new MARSHAL(1398079737, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[2];
      arrayOfObject[0] = paramObject1;
      arrayOfObject[1] = paramObject2;
      doLog(Level.WARNING, "ORBUTIL.couldNotSkipBytes", arrayOfObject, ORBUtilSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL couldNotSkipBytes(CompletionStatus paramCompletionStatus, Object paramObject1, Object paramObject2) { return couldNotSkipBytes(paramCompletionStatus, null, paramObject1, paramObject2); }
  
  public MARSHAL couldNotSkipBytes(Throwable paramThrowable, Object paramObject1, Object paramObject2) { return couldNotSkipBytes(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject1, paramObject2); }
  
  public MARSHAL couldNotSkipBytes(Object paramObject1, Object paramObject2) { return couldNotSkipBytes(CompletionStatus.COMPLETED_NO, null, paramObject1, paramObject2); }
  
  public MARSHAL badChunkLength(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject1, Object paramObject2) {
    MARSHAL mARSHAL = new MARSHAL(1398079738, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[2];
      arrayOfObject[0] = paramObject1;
      arrayOfObject[1] = paramObject2;
      doLog(Level.WARNING, "ORBUTIL.badChunkLength", arrayOfObject, ORBUtilSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL badChunkLength(CompletionStatus paramCompletionStatus, Object paramObject1, Object paramObject2) { return badChunkLength(paramCompletionStatus, null, paramObject1, paramObject2); }
  
  public MARSHAL badChunkLength(Throwable paramThrowable, Object paramObject1, Object paramObject2) { return badChunkLength(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject1, paramObject2); }
  
  public MARSHAL badChunkLength(Object paramObject1, Object paramObject2) { return badChunkLength(CompletionStatus.COMPLETED_NO, null, paramObject1, paramObject2); }
  
  public MARSHAL unableToLocateRepIdArray(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    MARSHAL mARSHAL = new MARSHAL(1398079739, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.WARNING, "ORBUTIL.unableToLocateRepIdArray", arrayOfObject, ORBUtilSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL unableToLocateRepIdArray(CompletionStatus paramCompletionStatus, Object paramObject) { return unableToLocateRepIdArray(paramCompletionStatus, null, paramObject); }
  
  public MARSHAL unableToLocateRepIdArray(Throwable paramThrowable, Object paramObject) { return unableToLocateRepIdArray(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public MARSHAL unableToLocateRepIdArray(Object paramObject) { return unableToLocateRepIdArray(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public MARSHAL badFixed(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject1, Object paramObject2) {
    MARSHAL mARSHAL = new MARSHAL(1398079740, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[2];
      arrayOfObject[0] = paramObject1;
      arrayOfObject[1] = paramObject2;
      doLog(Level.WARNING, "ORBUTIL.badFixed", arrayOfObject, ORBUtilSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL badFixed(CompletionStatus paramCompletionStatus, Object paramObject1, Object paramObject2) { return badFixed(paramCompletionStatus, null, paramObject1, paramObject2); }
  
  public MARSHAL badFixed(Throwable paramThrowable, Object paramObject1, Object paramObject2) { return badFixed(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject1, paramObject2); }
  
  public MARSHAL badFixed(Object paramObject1, Object paramObject2) { return badFixed(CompletionStatus.COMPLETED_NO, null, paramObject1, paramObject2); }
  
  public MARSHAL readObjectLoadClassFailure(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject1, Object paramObject2) {
    MARSHAL mARSHAL = new MARSHAL(1398079741, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[2];
      arrayOfObject[0] = paramObject1;
      arrayOfObject[1] = paramObject2;
      doLog(Level.WARNING, "ORBUTIL.readObjectLoadClassFailure", arrayOfObject, ORBUtilSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL readObjectLoadClassFailure(CompletionStatus paramCompletionStatus, Object paramObject1, Object paramObject2) { return readObjectLoadClassFailure(paramCompletionStatus, null, paramObject1, paramObject2); }
  
  public MARSHAL readObjectLoadClassFailure(Throwable paramThrowable, Object paramObject1, Object paramObject2) { return readObjectLoadClassFailure(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject1, paramObject2); }
  
  public MARSHAL readObjectLoadClassFailure(Object paramObject1, Object paramObject2) { return readObjectLoadClassFailure(CompletionStatus.COMPLETED_NO, null, paramObject1, paramObject2); }
  
  public MARSHAL couldNotInstantiateHelper(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    MARSHAL mARSHAL = new MARSHAL(1398079742, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.WARNING, "ORBUTIL.couldNotInstantiateHelper", arrayOfObject, ORBUtilSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL couldNotInstantiateHelper(CompletionStatus paramCompletionStatus, Object paramObject) { return couldNotInstantiateHelper(paramCompletionStatus, null, paramObject); }
  
  public MARSHAL couldNotInstantiateHelper(Throwable paramThrowable, Object paramObject) { return couldNotInstantiateHelper(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public MARSHAL couldNotInstantiateHelper(Object paramObject) { return couldNotInstantiateHelper(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public MARSHAL badToaOaid(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    MARSHAL mARSHAL = new MARSHAL(1398079743, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.badToaOaid", arrayOfObject, ORBUtilSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL badToaOaid(CompletionStatus paramCompletionStatus) { return badToaOaid(paramCompletionStatus, null); }
  
  public MARSHAL badToaOaid(Throwable paramThrowable) { return badToaOaid(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public MARSHAL badToaOaid() { return badToaOaid(CompletionStatus.COMPLETED_NO, null); }
  
  public MARSHAL couldNotInvokeHelperReadMethod(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    MARSHAL mARSHAL = new MARSHAL(1398079744, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.WARNING, "ORBUTIL.couldNotInvokeHelperReadMethod", arrayOfObject, ORBUtilSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL couldNotInvokeHelperReadMethod(CompletionStatus paramCompletionStatus, Object paramObject) { return couldNotInvokeHelperReadMethod(paramCompletionStatus, null, paramObject); }
  
  public MARSHAL couldNotInvokeHelperReadMethod(Throwable paramThrowable, Object paramObject) { return couldNotInvokeHelperReadMethod(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public MARSHAL couldNotInvokeHelperReadMethod(Object paramObject) { return couldNotInvokeHelperReadMethod(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public MARSHAL couldNotFindClass(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    MARSHAL mARSHAL = new MARSHAL(1398079745, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.couldNotFindClass", arrayOfObject, ORBUtilSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL couldNotFindClass(CompletionStatus paramCompletionStatus) { return couldNotFindClass(paramCompletionStatus, null); }
  
  public MARSHAL couldNotFindClass(Throwable paramThrowable) { return couldNotFindClass(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public MARSHAL couldNotFindClass() { return couldNotFindClass(CompletionStatus.COMPLETED_NO, null); }
  
  public MARSHAL badArgumentsNvlist(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    MARSHAL mARSHAL = new MARSHAL(1398079746, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = null;
      doLog(Level.FINE, "ORBUTIL.badArgumentsNvlist", arrayOfObject, ORBUtilSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL badArgumentsNvlist(CompletionStatus paramCompletionStatus) { return badArgumentsNvlist(paramCompletionStatus, null); }
  
  public MARSHAL badArgumentsNvlist(Throwable paramThrowable) { return badArgumentsNvlist(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public MARSHAL badArgumentsNvlist() { return badArgumentsNvlist(CompletionStatus.COMPLETED_NO, null); }
  
  public MARSHAL stubCreateError(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    MARSHAL mARSHAL = new MARSHAL(1398079747, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = null;
      doLog(Level.FINE, "ORBUTIL.stubCreateError", arrayOfObject, ORBUtilSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL stubCreateError(CompletionStatus paramCompletionStatus) { return stubCreateError(paramCompletionStatus, null); }
  
  public MARSHAL stubCreateError(Throwable paramThrowable) { return stubCreateError(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public MARSHAL stubCreateError() { return stubCreateError(CompletionStatus.COMPLETED_NO, null); }
  
  public MARSHAL javaSerializationException(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    MARSHAL mARSHAL = new MARSHAL(1398079748, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.WARNING, "ORBUTIL.javaSerializationException", arrayOfObject, ORBUtilSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL javaSerializationException(CompletionStatus paramCompletionStatus, Object paramObject) { return javaSerializationException(paramCompletionStatus, null, paramObject); }
  
  public MARSHAL javaSerializationException(Throwable paramThrowable, Object paramObject) { return javaSerializationException(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public MARSHAL javaSerializationException(Object paramObject) { return javaSerializationException(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public NO_IMPLEMENT genericNoImpl(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    NO_IMPLEMENT nO_IMPLEMENT = new NO_IMPLEMENT(1398079689, paramCompletionStatus);
    if (paramThrowable != null)
      nO_IMPLEMENT.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = null;
      doLog(Level.FINE, "ORBUTIL.genericNoImpl", arrayOfObject, ORBUtilSystemException.class, nO_IMPLEMENT);
    } 
    return nO_IMPLEMENT;
  }
  
  public NO_IMPLEMENT genericNoImpl(CompletionStatus paramCompletionStatus) { return genericNoImpl(paramCompletionStatus, null); }
  
  public NO_IMPLEMENT genericNoImpl(Throwable paramThrowable) { return genericNoImpl(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public NO_IMPLEMENT genericNoImpl() { return genericNoImpl(CompletionStatus.COMPLETED_NO, null); }
  
  public NO_IMPLEMENT contextNotImplemented(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    NO_IMPLEMENT nO_IMPLEMENT = new NO_IMPLEMENT(1398079690, paramCompletionStatus);
    if (paramThrowable != null)
      nO_IMPLEMENT.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = null;
      doLog(Level.FINE, "ORBUTIL.contextNotImplemented", arrayOfObject, ORBUtilSystemException.class, nO_IMPLEMENT);
    } 
    return nO_IMPLEMENT;
  }
  
  public NO_IMPLEMENT contextNotImplemented(CompletionStatus paramCompletionStatus) { return contextNotImplemented(paramCompletionStatus, null); }
  
  public NO_IMPLEMENT contextNotImplemented(Throwable paramThrowable) { return contextNotImplemented(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public NO_IMPLEMENT contextNotImplemented() { return contextNotImplemented(CompletionStatus.COMPLETED_NO, null); }
  
  public NO_IMPLEMENT getinterfaceNotImplemented(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    NO_IMPLEMENT nO_IMPLEMENT = new NO_IMPLEMENT(1398079691, paramCompletionStatus);
    if (paramThrowable != null)
      nO_IMPLEMENT.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = null;
      doLog(Level.FINE, "ORBUTIL.getinterfaceNotImplemented", arrayOfObject, ORBUtilSystemException.class, nO_IMPLEMENT);
    } 
    return nO_IMPLEMENT;
  }
  
  public NO_IMPLEMENT getinterfaceNotImplemented(CompletionStatus paramCompletionStatus) { return getinterfaceNotImplemented(paramCompletionStatus, null); }
  
  public NO_IMPLEMENT getinterfaceNotImplemented(Throwable paramThrowable) { return getinterfaceNotImplemented(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public NO_IMPLEMENT getinterfaceNotImplemented() { return getinterfaceNotImplemented(CompletionStatus.COMPLETED_NO, null); }
  
  public NO_IMPLEMENT sendDeferredNotimplemented(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    NO_IMPLEMENT nO_IMPLEMENT = new NO_IMPLEMENT(1398079692, paramCompletionStatus);
    if (paramThrowable != null)
      nO_IMPLEMENT.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = null;
      doLog(Level.FINE, "ORBUTIL.sendDeferredNotimplemented", arrayOfObject, ORBUtilSystemException.class, nO_IMPLEMENT);
    } 
    return nO_IMPLEMENT;
  }
  
  public NO_IMPLEMENT sendDeferredNotimplemented(CompletionStatus paramCompletionStatus) { return sendDeferredNotimplemented(paramCompletionStatus, null); }
  
  public NO_IMPLEMENT sendDeferredNotimplemented(Throwable paramThrowable) { return sendDeferredNotimplemented(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public NO_IMPLEMENT sendDeferredNotimplemented() { return sendDeferredNotimplemented(CompletionStatus.COMPLETED_NO, null); }
  
  public NO_IMPLEMENT longDoubleNotImplemented(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    NO_IMPLEMENT nO_IMPLEMENT = new NO_IMPLEMENT(1398079693, paramCompletionStatus);
    if (paramThrowable != null)
      nO_IMPLEMENT.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = null;
      doLog(Level.FINE, "ORBUTIL.longDoubleNotImplemented", arrayOfObject, ORBUtilSystemException.class, nO_IMPLEMENT);
    } 
    return nO_IMPLEMENT;
  }
  
  public NO_IMPLEMENT longDoubleNotImplemented(CompletionStatus paramCompletionStatus) { return longDoubleNotImplemented(paramCompletionStatus, null); }
  
  public NO_IMPLEMENT longDoubleNotImplemented(Throwable paramThrowable) { return longDoubleNotImplemented(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public NO_IMPLEMENT longDoubleNotImplemented() { return longDoubleNotImplemented(CompletionStatus.COMPLETED_NO, null); }
  
  public OBJ_ADAPTER noServerScInDispatch(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    OBJ_ADAPTER oBJ_ADAPTER = new OBJ_ADAPTER(1398079689, paramCompletionStatus);
    if (paramThrowable != null)
      oBJ_ADAPTER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.noServerScInDispatch", arrayOfObject, ORBUtilSystemException.class, oBJ_ADAPTER);
    } 
    return oBJ_ADAPTER;
  }
  
  public OBJ_ADAPTER noServerScInDispatch(CompletionStatus paramCompletionStatus) { return noServerScInDispatch(paramCompletionStatus, null); }
  
  public OBJ_ADAPTER noServerScInDispatch(Throwable paramThrowable) { return noServerScInDispatch(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public OBJ_ADAPTER noServerScInDispatch() { return noServerScInDispatch(CompletionStatus.COMPLETED_NO, null); }
  
  public OBJ_ADAPTER orbConnectError(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    OBJ_ADAPTER oBJ_ADAPTER = new OBJ_ADAPTER(1398079690, paramCompletionStatus);
    if (paramThrowable != null)
      oBJ_ADAPTER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.orbConnectError", arrayOfObject, ORBUtilSystemException.class, oBJ_ADAPTER);
    } 
    return oBJ_ADAPTER;
  }
  
  public OBJ_ADAPTER orbConnectError(CompletionStatus paramCompletionStatus) { return orbConnectError(paramCompletionStatus, null); }
  
  public OBJ_ADAPTER orbConnectError(Throwable paramThrowable) { return orbConnectError(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public OBJ_ADAPTER orbConnectError() { return orbConnectError(CompletionStatus.COMPLETED_NO, null); }
  
  public OBJ_ADAPTER adapterInactiveInActivation(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    OBJ_ADAPTER oBJ_ADAPTER = new OBJ_ADAPTER(1398079691, paramCompletionStatus);
    if (paramThrowable != null)
      oBJ_ADAPTER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = null;
      doLog(Level.FINE, "ORBUTIL.adapterInactiveInActivation", arrayOfObject, ORBUtilSystemException.class, oBJ_ADAPTER);
    } 
    return oBJ_ADAPTER;
  }
  
  public OBJ_ADAPTER adapterInactiveInActivation(CompletionStatus paramCompletionStatus) { return adapterInactiveInActivation(paramCompletionStatus, null); }
  
  public OBJ_ADAPTER adapterInactiveInActivation(Throwable paramThrowable) { return adapterInactiveInActivation(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public OBJ_ADAPTER adapterInactiveInActivation() { return adapterInactiveInActivation(CompletionStatus.COMPLETED_NO, null); }
  
  public OBJECT_NOT_EXIST locateUnknownObject(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    OBJECT_NOT_EXIST oBJECT_NOT_EXIST = new OBJECT_NOT_EXIST(1398079689, paramCompletionStatus);
    if (paramThrowable != null)
      oBJECT_NOT_EXIST.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.locateUnknownObject", arrayOfObject, ORBUtilSystemException.class, oBJECT_NOT_EXIST);
    } 
    return oBJECT_NOT_EXIST;
  }
  
  public OBJECT_NOT_EXIST locateUnknownObject(CompletionStatus paramCompletionStatus) { return locateUnknownObject(paramCompletionStatus, null); }
  
  public OBJECT_NOT_EXIST locateUnknownObject(Throwable paramThrowable) { return locateUnknownObject(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public OBJECT_NOT_EXIST locateUnknownObject() { return locateUnknownObject(CompletionStatus.COMPLETED_NO, null); }
  
  public OBJECT_NOT_EXIST badServerId(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    OBJECT_NOT_EXIST oBJECT_NOT_EXIST = new OBJECT_NOT_EXIST(1398079690, paramCompletionStatus);
    if (paramThrowable != null)
      oBJECT_NOT_EXIST.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = null;
      doLog(Level.FINE, "ORBUTIL.badServerId", arrayOfObject, ORBUtilSystemException.class, oBJECT_NOT_EXIST);
    } 
    return oBJECT_NOT_EXIST;
  }
  
  public OBJECT_NOT_EXIST badServerId(CompletionStatus paramCompletionStatus) { return badServerId(paramCompletionStatus, null); }
  
  public OBJECT_NOT_EXIST badServerId(Throwable paramThrowable) { return badServerId(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public OBJECT_NOT_EXIST badServerId() { return badServerId(CompletionStatus.COMPLETED_NO, null); }
  
  public OBJECT_NOT_EXIST badSkeleton(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    OBJECT_NOT_EXIST oBJECT_NOT_EXIST = new OBJECT_NOT_EXIST(1398079691, paramCompletionStatus);
    if (paramThrowable != null)
      oBJECT_NOT_EXIST.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.badSkeleton", arrayOfObject, ORBUtilSystemException.class, oBJECT_NOT_EXIST);
    } 
    return oBJECT_NOT_EXIST;
  }
  
  public OBJECT_NOT_EXIST badSkeleton(CompletionStatus paramCompletionStatus) { return badSkeleton(paramCompletionStatus, null); }
  
  public OBJECT_NOT_EXIST badSkeleton(Throwable paramThrowable) { return badSkeleton(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public OBJECT_NOT_EXIST badSkeleton() { return badSkeleton(CompletionStatus.COMPLETED_NO, null); }
  
  public OBJECT_NOT_EXIST servantNotFound(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    OBJECT_NOT_EXIST oBJECT_NOT_EXIST = new OBJECT_NOT_EXIST(1398079692, paramCompletionStatus);
    if (paramThrowable != null)
      oBJECT_NOT_EXIST.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.servantNotFound", arrayOfObject, ORBUtilSystemException.class, oBJECT_NOT_EXIST);
    } 
    return oBJECT_NOT_EXIST;
  }
  
  public OBJECT_NOT_EXIST servantNotFound(CompletionStatus paramCompletionStatus) { return servantNotFound(paramCompletionStatus, null); }
  
  public OBJECT_NOT_EXIST servantNotFound(Throwable paramThrowable) { return servantNotFound(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public OBJECT_NOT_EXIST servantNotFound() { return servantNotFound(CompletionStatus.COMPLETED_NO, null); }
  
  public OBJECT_NOT_EXIST noObjectAdapterFactory(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    OBJECT_NOT_EXIST oBJECT_NOT_EXIST = new OBJECT_NOT_EXIST(1398079693, paramCompletionStatus);
    if (paramThrowable != null)
      oBJECT_NOT_EXIST.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.noObjectAdapterFactory", arrayOfObject, ORBUtilSystemException.class, oBJECT_NOT_EXIST);
    } 
    return oBJECT_NOT_EXIST;
  }
  
  public OBJECT_NOT_EXIST noObjectAdapterFactory(CompletionStatus paramCompletionStatus) { return noObjectAdapterFactory(paramCompletionStatus, null); }
  
  public OBJECT_NOT_EXIST noObjectAdapterFactory(Throwable paramThrowable) { return noObjectAdapterFactory(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public OBJECT_NOT_EXIST noObjectAdapterFactory() { return noObjectAdapterFactory(CompletionStatus.COMPLETED_NO, null); }
  
  public OBJECT_NOT_EXIST badAdapterId(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    OBJECT_NOT_EXIST oBJECT_NOT_EXIST = new OBJECT_NOT_EXIST(1398079694, paramCompletionStatus);
    if (paramThrowable != null)
      oBJECT_NOT_EXIST.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.badAdapterId", arrayOfObject, ORBUtilSystemException.class, oBJECT_NOT_EXIST);
    } 
    return oBJECT_NOT_EXIST;
  }
  
  public OBJECT_NOT_EXIST badAdapterId(CompletionStatus paramCompletionStatus) { return badAdapterId(paramCompletionStatus, null); }
  
  public OBJECT_NOT_EXIST badAdapterId(Throwable paramThrowable) { return badAdapterId(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public OBJECT_NOT_EXIST badAdapterId() { return badAdapterId(CompletionStatus.COMPLETED_NO, null); }
  
  public OBJECT_NOT_EXIST dynAnyDestroyed(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    OBJECT_NOT_EXIST oBJECT_NOT_EXIST = new OBJECT_NOT_EXIST(1398079695, paramCompletionStatus);
    if (paramThrowable != null)
      oBJECT_NOT_EXIST.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.dynAnyDestroyed", arrayOfObject, ORBUtilSystemException.class, oBJECT_NOT_EXIST);
    } 
    return oBJECT_NOT_EXIST;
  }
  
  public OBJECT_NOT_EXIST dynAnyDestroyed(CompletionStatus paramCompletionStatus) { return dynAnyDestroyed(paramCompletionStatus, null); }
  
  public OBJECT_NOT_EXIST dynAnyDestroyed(Throwable paramThrowable) { return dynAnyDestroyed(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public OBJECT_NOT_EXIST dynAnyDestroyed() { return dynAnyDestroyed(CompletionStatus.COMPLETED_NO, null); }
  
  public TRANSIENT requestCanceled(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    TRANSIENT tRANSIENT = new TRANSIENT(1398079689, paramCompletionStatus);
    if (paramThrowable != null)
      tRANSIENT.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.requestCanceled", arrayOfObject, ORBUtilSystemException.class, tRANSIENT);
    } 
    return tRANSIENT;
  }
  
  public TRANSIENT requestCanceled(CompletionStatus paramCompletionStatus) { return requestCanceled(paramCompletionStatus, null); }
  
  public TRANSIENT requestCanceled(Throwable paramThrowable) { return requestCanceled(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public TRANSIENT requestCanceled() { return requestCanceled(CompletionStatus.COMPLETED_NO, null); }
  
  public UNKNOWN unknownCorbaExc(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    UNKNOWN uNKNOWN = new UNKNOWN(1398079689, paramCompletionStatus);
    if (paramThrowable != null)
      uNKNOWN.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.unknownCorbaExc", arrayOfObject, ORBUtilSystemException.class, uNKNOWN);
    } 
    return uNKNOWN;
  }
  
  public UNKNOWN unknownCorbaExc(CompletionStatus paramCompletionStatus) { return unknownCorbaExc(paramCompletionStatus, null); }
  
  public UNKNOWN unknownCorbaExc(Throwable paramThrowable) { return unknownCorbaExc(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public UNKNOWN unknownCorbaExc() { return unknownCorbaExc(CompletionStatus.COMPLETED_NO, null); }
  
  public UNKNOWN runtimeexception(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    UNKNOWN uNKNOWN = new UNKNOWN(1398079690, paramCompletionStatus);
    if (paramThrowable != null)
      uNKNOWN.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.runtimeexception", arrayOfObject, ORBUtilSystemException.class, uNKNOWN);
    } 
    return uNKNOWN;
  }
  
  public UNKNOWN runtimeexception(CompletionStatus paramCompletionStatus) { return runtimeexception(paramCompletionStatus, null); }
  
  public UNKNOWN runtimeexception(Throwable paramThrowable) { return runtimeexception(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public UNKNOWN runtimeexception() { return runtimeexception(CompletionStatus.COMPLETED_NO, null); }
  
  public UNKNOWN unknownServerError(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    UNKNOWN uNKNOWN = new UNKNOWN(1398079691, paramCompletionStatus);
    if (paramThrowable != null)
      uNKNOWN.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.unknownServerError", arrayOfObject, ORBUtilSystemException.class, uNKNOWN);
    } 
    return uNKNOWN;
  }
  
  public UNKNOWN unknownServerError(CompletionStatus paramCompletionStatus) { return unknownServerError(paramCompletionStatus, null); }
  
  public UNKNOWN unknownServerError(Throwable paramThrowable) { return unknownServerError(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public UNKNOWN unknownServerError() { return unknownServerError(CompletionStatus.COMPLETED_NO, null); }
  
  public UNKNOWN unknownDsiSysex(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    UNKNOWN uNKNOWN = new UNKNOWN(1398079692, paramCompletionStatus);
    if (paramThrowable != null)
      uNKNOWN.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.unknownDsiSysex", arrayOfObject, ORBUtilSystemException.class, uNKNOWN);
    } 
    return uNKNOWN;
  }
  
  public UNKNOWN unknownDsiSysex(CompletionStatus paramCompletionStatus) { return unknownDsiSysex(paramCompletionStatus, null); }
  
  public UNKNOWN unknownDsiSysex(Throwable paramThrowable) { return unknownDsiSysex(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public UNKNOWN unknownDsiSysex() { return unknownDsiSysex(CompletionStatus.COMPLETED_NO, null); }
  
  public UNKNOWN unknownSysex(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    UNKNOWN uNKNOWN = new UNKNOWN(1398079693, paramCompletionStatus);
    if (paramThrowable != null)
      uNKNOWN.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.unknownSysex", arrayOfObject, ORBUtilSystemException.class, uNKNOWN);
    } 
    return uNKNOWN;
  }
  
  public UNKNOWN unknownSysex(CompletionStatus paramCompletionStatus) { return unknownSysex(paramCompletionStatus, null); }
  
  public UNKNOWN unknownSysex(Throwable paramThrowable) { return unknownSysex(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public UNKNOWN unknownSysex() { return unknownSysex(CompletionStatus.COMPLETED_NO, null); }
  
  public UNKNOWN wrongInterfaceDef(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    UNKNOWN uNKNOWN = new UNKNOWN(1398079694, paramCompletionStatus);
    if (paramThrowable != null)
      uNKNOWN.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.wrongInterfaceDef", arrayOfObject, ORBUtilSystemException.class, uNKNOWN);
    } 
    return uNKNOWN;
  }
  
  public UNKNOWN wrongInterfaceDef(CompletionStatus paramCompletionStatus) { return wrongInterfaceDef(paramCompletionStatus, null); }
  
  public UNKNOWN wrongInterfaceDef(Throwable paramThrowable) { return wrongInterfaceDef(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public UNKNOWN wrongInterfaceDef() { return wrongInterfaceDef(CompletionStatus.COMPLETED_NO, null); }
  
  public UNKNOWN noInterfaceDefStub(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    UNKNOWN uNKNOWN = new UNKNOWN(1398079695, paramCompletionStatus);
    if (paramThrowable != null)
      uNKNOWN.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ORBUTIL.noInterfaceDefStub", arrayOfObject, ORBUtilSystemException.class, uNKNOWN);
    } 
    return uNKNOWN;
  }
  
  public UNKNOWN noInterfaceDefStub(CompletionStatus paramCompletionStatus) { return noInterfaceDefStub(paramCompletionStatus, null); }
  
  public UNKNOWN noInterfaceDefStub(Throwable paramThrowable) { return noInterfaceDefStub(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public UNKNOWN noInterfaceDefStub() { return noInterfaceDefStub(CompletionStatus.COMPLETED_NO, null); }
  
  public UNKNOWN unknownExceptionInDispatch(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    UNKNOWN uNKNOWN = new UNKNOWN(1398079697, paramCompletionStatus);
    if (paramThrowable != null)
      uNKNOWN.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = null;
      doLog(Level.FINE, "ORBUTIL.unknownExceptionInDispatch", arrayOfObject, ORBUtilSystemException.class, uNKNOWN);
    } 
    return uNKNOWN;
  }
  
  public UNKNOWN unknownExceptionInDispatch(CompletionStatus paramCompletionStatus) { return unknownExceptionInDispatch(paramCompletionStatus, null); }
  
  public UNKNOWN unknownExceptionInDispatch(Throwable paramThrowable) { return unknownExceptionInDispatch(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public UNKNOWN unknownExceptionInDispatch() { return unknownExceptionInDispatch(CompletionStatus.COMPLETED_NO, null); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\logging\ORBUtilSystemException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */