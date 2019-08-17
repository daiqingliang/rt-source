package com.sun.jmx.snmp;

public interface SnmpDefinitions {
  public static final int noAuthNoPriv = 0;
  
  public static final int authNoPriv = 1;
  
  public static final int authPriv = 3;
  
  public static final int reportableFlag = 4;
  
  public static final int authMask = 1;
  
  public static final int privMask = 2;
  
  public static final int authPrivMask = 3;
  
  public static final int CtxtConsFlag = 160;
  
  public static final byte snmpVersionOne = 0;
  
  public static final byte snmpVersionTwo = 1;
  
  public static final byte snmpVersionThree = 3;
  
  public static final int pduGetRequestPdu = 160;
  
  public static final int pduGetNextRequestPdu = 161;
  
  public static final int pduGetResponsePdu = 162;
  
  public static final int pduSetRequestPdu = 163;
  
  public static final int pduGetBulkRequestPdu = 165;
  
  public static final int pduWalkRequest = 253;
  
  public static final int pduV1TrapPdu = 164;
  
  public static final int pduV2TrapPdu = 167;
  
  public static final int pduInformRequestPdu = 166;
  
  public static final int pduReportPdu = 168;
  
  public static final int trapColdStart = 0;
  
  public static final int trapWarmStart = 1;
  
  public static final int trapLinkDown = 2;
  
  public static final int trapLinkUp = 3;
  
  public static final int trapAuthenticationFailure = 4;
  
  public static final int trapEgpNeighborLoss = 5;
  
  public static final int trapEnterpriseSpecific = 6;
  
  public static final int snmpRspNoError = 0;
  
  public static final int snmpRspTooBig = 1;
  
  public static final int snmpRspNoSuchName = 2;
  
  public static final int snmpRspBadValue = 3;
  
  public static final int snmpRspReadOnly = 4;
  
  public static final int snmpRspGenErr = 5;
  
  public static final int snmpRspNoAccess = 6;
  
  public static final int snmpRspWrongType = 7;
  
  public static final int snmpRspWrongLength = 8;
  
  public static final int snmpRspWrongEncoding = 9;
  
  public static final int snmpRspWrongValue = 10;
  
  public static final int snmpRspNoCreation = 11;
  
  public static final int snmpRspInconsistentValue = 12;
  
  public static final int snmpRspResourceUnavailable = 13;
  
  public static final int snmpRspCommitFailed = 14;
  
  public static final int snmpRspUndoFailed = 15;
  
  public static final int snmpRspAuthorizationError = 16;
  
  public static final int snmpRspNotWritable = 17;
  
  public static final int snmpRspInconsistentName = 18;
  
  public static final int noSuchView = 19;
  
  public static final int noSuchContext = 20;
  
  public static final int noGroupName = 21;
  
  public static final int notInView = 22;
  
  public static final int snmpReqTimeout = 224;
  
  public static final int snmpReqAborted = 225;
  
  public static final int snmpRspDecodingError = 226;
  
  public static final int snmpReqEncodingError = 227;
  
  public static final int snmpReqPacketOverflow = 228;
  
  public static final int snmpRspEndOfTable = 229;
  
  public static final int snmpReqRefireAfterVbFix = 230;
  
  public static final int snmpReqHandleTooBig = 231;
  
  public static final int snmpReqTooBigImpossible = 232;
  
  public static final int snmpReqInternalError = 240;
  
  public static final int snmpReqSocketIOError = 241;
  
  public static final int snmpReqUnknownError = 242;
  
  public static final int snmpWrongSnmpVersion = 243;
  
  public static final int snmpUnknownPrincipal = 244;
  
  public static final int snmpAuthNotSupported = 245;
  
  public static final int snmpPrivNotSupported = 246;
  
  public static final int snmpBadSecurityLevel = 249;
  
  public static final int snmpUsmBadEngineId = 247;
  
  public static final int snmpUsmInvalidTimeliness = 248;
  
  public static final int snmpV1SecurityModel = 1;
  
  public static final int snmpV2SecurityModel = 2;
  
  public static final int snmpUsmSecurityModel = 3;
  
  public static final int snmpV1MsgProcessingModel = 0;
  
  public static final int snmpV2MsgProcessingModel = 1;
  
  public static final int snmpV3MsgProcessingModel = 3;
  
  public static final int snmpV1AccessControlModel = 0;
  
  public static final int snmpV2AccessControlModel = 1;
  
  public static final int snmpV3AccessControlModel = 3;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\SnmpDefinitions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */