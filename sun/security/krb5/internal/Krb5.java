package sun.security.krb5.internal;

import java.security.AccessController;
import java.util.Hashtable;
import sun.misc.HexDumpEncoder;
import sun.security.action.GetBooleanAction;

public class Krb5 {
  public static final int DEFAULT_ALLOWABLE_CLOCKSKEW = 300;
  
  public static final int DEFAULT_MINIMUM_LIFETIME = 300;
  
  public static final int DEFAULT_MAXIMUM_RENEWABLE_LIFETIME = 604800;
  
  public static final int DEFAULT_MAXIMUM_TICKET_LIFETIME = 86400;
  
  public static final boolean DEFAULT_FORWARDABLE_ALLOWED = true;
  
  public static final boolean DEFAULT_PROXIABLE_ALLOWED = true;
  
  public static final boolean DEFAULT_POSTDATE_ALLOWED = true;
  
  public static final boolean DEFAULT_RENEWABLE_ALLOWED = true;
  
  public static final boolean AP_EMPTY_ADDRESSES_ALLOWED = true;
  
  public static final int AP_OPTS_RESERVED = 0;
  
  public static final int AP_OPTS_USE_SESSION_KEY = 1;
  
  public static final int AP_OPTS_MUTUAL_REQUIRED = 2;
  
  public static final int AP_OPTS_MAX = 31;
  
  public static final int TKT_OPTS_RESERVED = 0;
  
  public static final int TKT_OPTS_FORWARDABLE = 1;
  
  public static final int TKT_OPTS_FORWARDED = 2;
  
  public static final int TKT_OPTS_PROXIABLE = 3;
  
  public static final int TKT_OPTS_PROXY = 4;
  
  public static final int TKT_OPTS_MAY_POSTDATE = 5;
  
  public static final int TKT_OPTS_POSTDATED = 6;
  
  public static final int TKT_OPTS_INVALID = 7;
  
  public static final int TKT_OPTS_RENEWABLE = 8;
  
  public static final int TKT_OPTS_INITIAL = 9;
  
  public static final int TKT_OPTS_PRE_AUTHENT = 10;
  
  public static final int TKT_OPTS_HW_AUTHENT = 11;
  
  public static final int TKT_OPTS_DELEGATE = 13;
  
  public static final int TKT_OPTS_MAX = 31;
  
  public static final int KDC_OPTS_MAX = 31;
  
  public static final int KRB_FLAGS_MAX = 31;
  
  public static final int LRTYPE_NONE = 0;
  
  public static final int LRTYPE_TIME_OF_INITIAL_TGT = 1;
  
  public static final int LRTYPE_TIME_OF_INITIAL_REQ = 2;
  
  public static final int LRTYPE_TIME_OF_NEWEST_TGT = 3;
  
  public static final int LRTYPE_TIME_OF_LAST_RENEWAL = 4;
  
  public static final int LRTYPE_TIME_OF_LAST_REQ = 5;
  
  public static final int ADDR_LEN_INET = 4;
  
  public static final int ADDR_LEN_CHAOS = 2;
  
  public static final int ADDR_LEN_OSI = 0;
  
  public static final int ADDR_LEN_XNS = 6;
  
  public static final int ADDR_LEN_APPLETALK = 3;
  
  public static final int ADDR_LEN_DECNET = 2;
  
  public static final int ADDRTYPE_UNIX = 1;
  
  public static final int ADDRTYPE_INET = 2;
  
  public static final int ADDRTYPE_IMPLINK = 3;
  
  public static final int ADDRTYPE_PUP = 4;
  
  public static final int ADDRTYPE_CHAOS = 5;
  
  public static final int ADDRTYPE_XNS = 6;
  
  public static final int ADDRTYPE_IPX = 6;
  
  public static final int ADDRTYPE_ISO = 7;
  
  public static final int ADDRTYPE_ECMA = 8;
  
  public static final int ADDRTYPE_DATAKIT = 9;
  
  public static final int ADDRTYPE_CCITT = 10;
  
  public static final int ADDRTYPE_SNA = 11;
  
  public static final int ADDRTYPE_DECNET = 12;
  
  public static final int ADDRTYPE_DLI = 13;
  
  public static final int ADDRTYPE_LAT = 14;
  
  public static final int ADDRTYPE_HYLINK = 15;
  
  public static final int ADDRTYPE_APPLETALK = 16;
  
  public static final int ADDRTYPE_NETBIOS = 17;
  
  public static final int ADDRTYPE_VOICEVIEW = 18;
  
  public static final int ADDRTYPE_FIREFOX = 19;
  
  public static final int ADDRTYPE_BAN = 21;
  
  public static final int ADDRTYPE_ATM = 22;
  
  public static final int ADDRTYPE_INET6 = 24;
  
  public static final int KDC_INET_DEFAULT_PORT = 88;
  
  public static final int KDC_RETRY_LIMIT = 3;
  
  public static final int KDC_DEFAULT_UDP_PREF_LIMIT = 1465;
  
  public static final int KDC_HARD_UDP_LIMIT = 32700;
  
  public static final int KEYTYPE_NULL = 0;
  
  public static final int KEYTYPE_DES = 1;
  
  public static final int KEYTYPE_DES3 = 2;
  
  public static final int KEYTYPE_AES = 3;
  
  public static final int KEYTYPE_ARCFOUR_HMAC = 4;
  
  public static final int PA_TGS_REQ = 1;
  
  public static final int PA_ENC_TIMESTAMP = 2;
  
  public static final int PA_PW_SALT = 3;
  
  public static final int PA_ETYPE_INFO = 11;
  
  public static final int PA_ETYPE_INFO2 = 19;
  
  public static final int PA_FOR_USER = 129;
  
  public static final int OSF_DCE = 64;
  
  public static final int SESAME = 65;
  
  public static final int ATT_CHALLENGE_RESPONSE = 64;
  
  public static final int DOMAIN_X500_COMPRESS = 1;
  
  public static final int PVNO = 5;
  
  public static final int AUTHNETICATOR_VNO = 5;
  
  public static final int TICKET_VNO = 5;
  
  public static final int KRB_AS_REQ = 10;
  
  public static final int KRB_AS_REP = 11;
  
  public static final int KRB_TGS_REQ = 12;
  
  public static final int KRB_TGS_REP = 13;
  
  public static final int KRB_AP_REQ = 14;
  
  public static final int KRB_AP_REP = 15;
  
  public static final int KRB_SAFE = 20;
  
  public static final int KRB_PRIV = 21;
  
  public static final int KRB_CRED = 22;
  
  public static final int KRB_ERROR = 30;
  
  public static final int KRB_TKT = 1;
  
  public static final int KRB_AUTHENTICATOR = 2;
  
  public static final int KRB_ENC_TKT_PART = 3;
  
  public static final int KRB_ENC_AS_REP_PART = 25;
  
  public static final int KRB_ENC_TGS_REP_PART = 26;
  
  public static final int KRB_ENC_AP_REP_PART = 27;
  
  public static final int KRB_ENC_KRB_PRIV_PART = 28;
  
  public static final int KRB_ENC_KRB_CRED_PART = 29;
  
  public static final int KDC_ERR_NONE = 0;
  
  public static final int KDC_ERR_NAME_EXP = 1;
  
  public static final int KDC_ERR_SERVICE_EXP = 2;
  
  public static final int KDC_ERR_BAD_PVNO = 3;
  
  public static final int KDC_ERR_C_OLD_MAST_KVNO = 4;
  
  public static final int KDC_ERR_S_OLD_MAST_KVNO = 5;
  
  public static final int KDC_ERR_C_PRINCIPAL_UNKNOWN = 6;
  
  public static final int KDC_ERR_S_PRINCIPAL_UNKNOWN = 7;
  
  public static final int KDC_ERR_PRINCIPAL_NOT_UNIQUE = 8;
  
  public static final int KDC_ERR_NULL_KEY = 9;
  
  public static final int KDC_ERR_CANNOT_POSTDATE = 10;
  
  public static final int KDC_ERR_NEVER_VALID = 11;
  
  public static final int KDC_ERR_POLICY = 12;
  
  public static final int KDC_ERR_BADOPTION = 13;
  
  public static final int KDC_ERR_ETYPE_NOSUPP = 14;
  
  public static final int KDC_ERR_SUMTYPE_NOSUPP = 15;
  
  public static final int KDC_ERR_PADATA_TYPE_NOSUPP = 16;
  
  public static final int KDC_ERR_TRTYPE_NOSUPP = 17;
  
  public static final int KDC_ERR_CLIENT_REVOKED = 18;
  
  public static final int KDC_ERR_SERVICE_REVOKED = 19;
  
  public static final int KDC_ERR_TGT_REVOKED = 20;
  
  public static final int KDC_ERR_CLIENT_NOTYET = 21;
  
  public static final int KDC_ERR_SERVICE_NOTYET = 22;
  
  public static final int KDC_ERR_KEY_EXPIRED = 23;
  
  public static final int KDC_ERR_PREAUTH_FAILED = 24;
  
  public static final int KDC_ERR_PREAUTH_REQUIRED = 25;
  
  public static final int KRB_AP_ERR_BAD_INTEGRITY = 31;
  
  public static final int KRB_AP_ERR_TKT_EXPIRED = 32;
  
  public static final int KRB_AP_ERR_TKT_NYV = 33;
  
  public static final int KRB_AP_ERR_REPEAT = 34;
  
  public static final int KRB_AP_ERR_NOT_US = 35;
  
  public static final int KRB_AP_ERR_BADMATCH = 36;
  
  public static final int KRB_AP_ERR_SKEW = 37;
  
  public static final int KRB_AP_ERR_BADADDR = 38;
  
  public static final int KRB_AP_ERR_BADVERSION = 39;
  
  public static final int KRB_AP_ERR_MSG_TYPE = 40;
  
  public static final int KRB_AP_ERR_MODIFIED = 41;
  
  public static final int KRB_AP_ERR_BADORDER = 42;
  
  public static final int KRB_AP_ERR_BADKEYVER = 44;
  
  public static final int KRB_AP_ERR_NOKEY = 45;
  
  public static final int KRB_AP_ERR_MUT_FAIL = 46;
  
  public static final int KRB_AP_ERR_BADDIRECTION = 47;
  
  public static final int KRB_AP_ERR_METHOD = 48;
  
  public static final int KRB_AP_ERR_BADSEQ = 49;
  
  public static final int KRB_AP_ERR_INAPP_CKSUM = 50;
  
  public static final int KRB_ERR_RESPONSE_TOO_BIG = 52;
  
  public static final int KRB_ERR_GENERIC = 60;
  
  public static final int KRB_ERR_FIELD_TOOLONG = 61;
  
  public static final int KRB_CRYPTO_NOT_SUPPORT = 100;
  
  public static final int KRB_AP_ERR_NOREALM = 62;
  
  public static final int KRB_AP_ERR_GEN_CRED = 63;
  
  public static final int KRB_AP_ERR_REQ_OPTIONS = 101;
  
  public static final int API_INVALID_ARG = 400;
  
  public static final int BITSTRING_SIZE_INVALID = 500;
  
  public static final int BITSTRING_INDEX_OUT_OF_BOUNDS = 501;
  
  public static final int BITSTRING_BAD_LENGTH = 502;
  
  public static final int REALM_ILLCHAR = 600;
  
  public static final int REALM_NULL = 601;
  
  public static final int ASN1_BAD_TIMEFORMAT = 900;
  
  public static final int ASN1_MISSING_FIELD = 901;
  
  public static final int ASN1_MISPLACED_FIELD = 902;
  
  public static final int ASN1_TYPE_MISMATCH = 903;
  
  public static final int ASN1_OVERFLOW = 904;
  
  public static final int ASN1_OVERRUN = 905;
  
  public static final int ASN1_BAD_ID = 906;
  
  public static final int ASN1_BAD_LENGTH = 907;
  
  public static final int ASN1_BAD_FORMAT = 908;
  
  public static final int ASN1_PARSE_ERROR = 909;
  
  public static final int ASN1_BAD_CLASS = 910;
  
  public static final int ASN1_BAD_TYPE = 911;
  
  public static final int ASN1_BAD_TAG = 912;
  
  public static final int ASN1_UNSUPPORTED_TYPE = 913;
  
  public static final int ASN1_CANNOT_ENCODE = 914;
  
  private static Hashtable<Integer, String> errMsgList;
  
  public static final boolean DEBUG = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.security.krb5.debug"))).booleanValue();
  
  public static final HexDumpEncoder hexDumper = new HexDumpEncoder();
  
  public static String getErrorMessage(int paramInt) { return (String)errMsgList.get(Integer.valueOf(paramInt)); }
  
  static  {
    errMsgList = new Hashtable();
    errMsgList.put(Integer.valueOf(0), "No error");
    errMsgList.put(Integer.valueOf(1), "Client's entry in database expired");
    errMsgList.put(Integer.valueOf(2), "Server's entry in database has expired");
    errMsgList.put(Integer.valueOf(3), "Requested protocol version number not supported");
    errMsgList.put(Integer.valueOf(4), "Client's key encrypted in old master key");
    errMsgList.put(Integer.valueOf(5), "Server's key encrypted in old master key");
    errMsgList.put(Integer.valueOf(6), "Client not found in Kerberos database");
    errMsgList.put(Integer.valueOf(7), "Server not found in Kerberos database");
    errMsgList.put(Integer.valueOf(8), "Multiple principal entries in database");
    errMsgList.put(Integer.valueOf(9), "The client or server has a null key");
    errMsgList.put(Integer.valueOf(10), "Ticket not eligible for postdating");
    errMsgList.put(Integer.valueOf(11), "Requested start time is later than end time");
    errMsgList.put(Integer.valueOf(12), "KDC policy rejects request");
    errMsgList.put(Integer.valueOf(13), "KDC cannot accommodate requested option");
    errMsgList.put(Integer.valueOf(14), "KDC has no support for encryption type");
    errMsgList.put(Integer.valueOf(15), "KDC has no support for checksum type");
    errMsgList.put(Integer.valueOf(16), "KDC has no support for padata type");
    errMsgList.put(Integer.valueOf(17), "KDC has no support for transited type");
    errMsgList.put(Integer.valueOf(18), "Clients credentials have been revoked");
    errMsgList.put(Integer.valueOf(19), "Credentials for server have been revoked");
    errMsgList.put(Integer.valueOf(20), "TGT has been revoked");
    errMsgList.put(Integer.valueOf(21), "Client not yet valid - try again later");
    errMsgList.put(Integer.valueOf(22), "Server not yet valid - try again later");
    errMsgList.put(Integer.valueOf(23), "Password has expired - change password to reset");
    errMsgList.put(Integer.valueOf(24), "Pre-authentication information was invalid");
    errMsgList.put(Integer.valueOf(25), "Additional pre-authentication required");
    errMsgList.put(Integer.valueOf(31), "Integrity check on decrypted field failed");
    errMsgList.put(Integer.valueOf(32), "Ticket expired");
    errMsgList.put(Integer.valueOf(33), "Ticket not yet valid");
    errMsgList.put(Integer.valueOf(34), "Request is a replay");
    errMsgList.put(Integer.valueOf(35), "The ticket isn't for us");
    errMsgList.put(Integer.valueOf(36), "Ticket and authenticator don't match");
    errMsgList.put(Integer.valueOf(37), "Clock skew too great");
    errMsgList.put(Integer.valueOf(38), "Incorrect net address");
    errMsgList.put(Integer.valueOf(39), "Protocol version mismatch");
    errMsgList.put(Integer.valueOf(40), "Invalid msg type");
    errMsgList.put(Integer.valueOf(41), "Message stream modified");
    errMsgList.put(Integer.valueOf(42), "Message out of order");
    errMsgList.put(Integer.valueOf(44), "Specified version of key is not available");
    errMsgList.put(Integer.valueOf(45), "Service key not available");
    errMsgList.put(Integer.valueOf(46), "Mutual authentication failed");
    errMsgList.put(Integer.valueOf(47), "Incorrect message direction");
    errMsgList.put(Integer.valueOf(48), "Alternative authentication method required");
    errMsgList.put(Integer.valueOf(49), "Incorrect sequence number in message");
    errMsgList.put(Integer.valueOf(50), "Inappropriate type of checksum in message");
    errMsgList.put(Integer.valueOf(52), "Response too big for UDP, retry with TCP");
    errMsgList.put(Integer.valueOf(60), "Generic error (description in e-text)");
    errMsgList.put(Integer.valueOf(61), "Field is too long for this implementation");
    errMsgList.put(Integer.valueOf(62), "Realm name not available");
    errMsgList.put(Integer.valueOf(400), "Invalid argument");
    errMsgList.put(Integer.valueOf(500), "BitString size does not match input byte array");
    errMsgList.put(Integer.valueOf(501), "BitString bit index does not fall within size");
    errMsgList.put(Integer.valueOf(502), "BitString length is wrong for the expected type");
    errMsgList.put(Integer.valueOf(600), "Illegal character in realm name; one of: '/', ':', '\000'");
    errMsgList.put(Integer.valueOf(601), "Null realm name");
    errMsgList.put(Integer.valueOf(900), "Input not in GeneralizedTime format");
    errMsgList.put(Integer.valueOf(901), "Structure is missing a required field");
    errMsgList.put(Integer.valueOf(902), "Unexpected field number");
    errMsgList.put(Integer.valueOf(903), "Type numbers are inconsistent");
    errMsgList.put(Integer.valueOf(904), "Value too large");
    errMsgList.put(Integer.valueOf(905), "Encoding ended unexpectedly");
    errMsgList.put(Integer.valueOf(906), "Identifier doesn't match expected value");
    errMsgList.put(Integer.valueOf(907), "Length doesn't match expected value");
    errMsgList.put(Integer.valueOf(908), "Badly-formatted encoding");
    errMsgList.put(Integer.valueOf(909), "Parse error");
    errMsgList.put(Integer.valueOf(910), "Bad class number");
    errMsgList.put(Integer.valueOf(911), "Bad type number");
    errMsgList.put(Integer.valueOf(912), "Bad tag number");
    errMsgList.put(Integer.valueOf(913), "Unsupported ASN.1 type encountered");
    errMsgList.put(Integer.valueOf(914), "Encoding failed due to invalid parameter(s)");
    errMsgList.put(Integer.valueOf(100), "Client has no support for crypto type");
    errMsgList.put(Integer.valueOf(101), "Invalid option setting in ticket request.");
    errMsgList.put(Integer.valueOf(63), "Fail to create credential.");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\Krb5.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */