package com.sun.jmx.snmp.daemon;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.snmp.InetAddressAcl;
import com.sun.jmx.snmp.SnmpDefinitions;
import com.sun.jmx.snmp.SnmpMessage;
import com.sun.jmx.snmp.SnmpPduBulk;
import com.sun.jmx.snmp.SnmpPduFactory;
import com.sun.jmx.snmp.SnmpPduPacket;
import com.sun.jmx.snmp.SnmpPduRequest;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpTooBigException;
import com.sun.jmx.snmp.SnmpValue;
import com.sun.jmx.snmp.SnmpVarBind;
import com.sun.jmx.snmp.SnmpVarBindList;
import com.sun.jmx.snmp.agent.SnmpMibAgent;
import com.sun.jmx.snmp.agent.SnmpUserDataFactory;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import javax.management.MBeanServer;
import javax.management.ObjectName;

class SnmpRequestHandler extends ClientHandler implements SnmpDefinitions {
  private DatagramSocket socket = null;
  
  private DatagramPacket packet = null;
  
  private Vector<SnmpMibAgent> mibs = null;
  
  private Hashtable<SnmpMibAgent, SnmpSubRequestHandler> subs = null;
  
  private SnmpMibTree root;
  
  private InetAddressAcl ipacl = null;
  
  private SnmpPduFactory pduFactory = null;
  
  private SnmpUserDataFactory userDataFactory = null;
  
  private SnmpAdaptorServer adaptor = null;
  
  private static final String InterruptSysCallMsg = "Interrupted system call";
  
  public SnmpRequestHandler(SnmpAdaptorServer paramSnmpAdaptorServer, int paramInt, DatagramSocket paramDatagramSocket, DatagramPacket paramDatagramPacket, SnmpMibTree paramSnmpMibTree, Vector<SnmpMibAgent> paramVector, InetAddressAcl paramInetAddressAcl, SnmpPduFactory paramSnmpPduFactory, SnmpUserDataFactory paramSnmpUserDataFactory, MBeanServer paramMBeanServer, ObjectName paramObjectName) {
    super(paramSnmpAdaptorServer, paramInt, paramMBeanServer, paramObjectName);
    this.adaptor = paramSnmpAdaptorServer;
    this.socket = paramDatagramSocket;
    this.packet = paramDatagramPacket;
    this.root = paramSnmpMibTree;
    this.mibs = new Vector(paramVector);
    this.subs = new Hashtable(this.mibs.size());
    this.ipacl = paramInetAddressAcl;
    this.pduFactory = paramSnmpPduFactory;
    this.userDataFactory = paramSnmpUserDataFactory;
  }
  
  public void doRun() {
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "doRun", "Packet received:\n" + SnmpMessage.dumpHexBuffer(this.packet.getData(), 0, this.packet.getLength())); 
    DatagramPacket datagramPacket = makeResponsePacket(this.packet);
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER) && datagramPacket != null)
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "doRun", "Packet to be sent:\n" + SnmpMessage.dumpHexBuffer(datagramPacket.getData(), 0, datagramPacket.getLength())); 
    if (datagramPacket != null)
      try {
        this.socket.send(datagramPacket);
      } catch (SocketException socketException) {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
          if (socketException.getMessage().equals("Interrupted system call")) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "doRun", "interrupted");
          } else {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "doRun", "I/O exception", socketException);
          }  
      } catch (InterruptedIOException interruptedIOException) {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "doRun", "interrupted"); 
      } catch (Exception exception) {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "doRun", "failure when sending response", exception); 
      }  
  }
  
  private DatagramPacket makeResponsePacket(DatagramPacket paramDatagramPacket) {
    DatagramPacket datagramPacket = null;
    SnmpMessage snmpMessage1 = new SnmpMessage();
    try {
      snmpMessage1.decodeMessage(paramDatagramPacket.getData(), paramDatagramPacket.getLength());
      snmpMessage1.address = paramDatagramPacket.getAddress();
      snmpMessage1.port = paramDatagramPacket.getPort();
    } catch (SnmpStatusException snmpStatusException) {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "makeResponsePacket", "packet decoding failed", snmpStatusException); 
      snmpMessage1 = null;
      ((SnmpAdaptorServer)this.adaptorServer).incSnmpInASNParseErrs(1);
    } 
    SnmpMessage snmpMessage2 = null;
    if (snmpMessage1 != null)
      snmpMessage2 = makeResponseMessage(snmpMessage1); 
    if (snmpMessage2 != null)
      try {
        paramDatagramPacket.setLength(snmpMessage2.encodeMessage(paramDatagramPacket.getData()));
        datagramPacket = paramDatagramPacket;
      } catch (SnmpTooBigException snmpTooBigException) {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "makeResponsePacket", "response message is too big"); 
        try {
          snmpMessage2 = newTooBigMessage(snmpMessage1);
          paramDatagramPacket.setLength(snmpMessage2.encodeMessage(paramDatagramPacket.getData()));
          datagramPacket = paramDatagramPacket;
        } catch (SnmpTooBigException snmpTooBigException1) {
          if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "makeResponsePacket", "'too big' is 'too big' !!!"); 
          this.adaptor.incSnmpSilentDrops(1);
        } 
      }  
    return datagramPacket;
  }
  
  private SnmpMessage makeResponseMessage(SnmpMessage paramSnmpMessage) {
    SnmpPduPacket snmpPduPacket1;
    SnmpMessage snmpMessage = null;
    Object object = null;
    try {
      snmpPduPacket1 = (SnmpPduPacket)this.pduFactory.decodeSnmpPdu(paramSnmpMessage);
      if (snmpPduPacket1 != null && this.userDataFactory != null)
        object = this.userDataFactory.allocateUserData(snmpPduPacket1); 
    } catch (SnmpStatusException snmpStatusException) {
      snmpPduPacket1 = null;
      SnmpAdaptorServer snmpAdaptorServer = (SnmpAdaptorServer)this.adaptorServer;
      snmpAdaptorServer.incSnmpInASNParseErrs(1);
      if (snmpStatusException.getStatus() == 243)
        snmpAdaptorServer.incSnmpInBadVersions(1); 
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "makeResponseMessage", "message decoding failed", snmpStatusException); 
    } 
    SnmpPduPacket snmpPduPacket2 = null;
    if (snmpPduPacket1 != null) {
      snmpPduPacket2 = makeResponsePdu(snmpPduPacket1, object);
      try {
        if (this.userDataFactory != null)
          this.userDataFactory.releaseUserData(object, snmpPduPacket2); 
      } catch (SnmpStatusException snmpStatusException) {
        snmpPduPacket2 = null;
      } 
    } 
    if (snmpPduPacket2 != null)
      try {
        snmpMessage = (SnmpMessage)this.pduFactory.encodeSnmpPdu(snmpPduPacket2, this.packet.getData().length);
      } catch (SnmpStatusException snmpStatusException) {
        snmpMessage = null;
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "makeResponseMessage", "failure when encoding the response message", snmpStatusException); 
      } catch (SnmpTooBigException snmpTooBigException) {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "makeResponseMessage", "response message is too big"); 
        try {
          if (this.packet.getData().length <= 32)
            throw snmpTooBigException; 
          int i = snmpTooBigException.getVarBindCount();
          if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "makeResponseMessage", "fail on element" + i); 
          while (true) {
            try {
              snmpPduPacket2 = reduceResponsePdu(snmpPduPacket1, snmpPduPacket2, i);
              snmpMessage = (SnmpMessage)this.pduFactory.encodeSnmpPdu(snmpPduPacket2, this.packet.getData().length - 32);
              break;
            } catch (SnmpTooBigException snmpTooBigException1) {
              if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
                JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "makeResponseMessage", "response message is still too big"); 
              int j = i;
              i = snmpTooBigException1.getVarBindCount();
              if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
                JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "makeResponseMessage", "fail on element" + i); 
              if (i == j)
                throw snmpTooBigException1; 
            } 
          } 
        } catch (SnmpStatusException snmpStatusException) {
          snmpMessage = null;
          if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "makeResponseMessage", "failure when encoding the response message", snmpStatusException); 
        } catch (SnmpTooBigException snmpTooBigException1) {
          try {
            snmpPduPacket2 = newTooBigPdu(snmpPduPacket1);
            snmpMessage = (SnmpMessage)this.pduFactory.encodeSnmpPdu(snmpPduPacket2, this.packet.getData().length);
          } catch (SnmpTooBigException snmpTooBigException2) {
            snmpMessage = null;
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
              JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "makeResponseMessage", "'too big' is 'too big' !!!"); 
            this.adaptor.incSnmpSilentDrops(1);
          } catch (Exception exception) {
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
              JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "makeResponseMessage", "Got unexpected exception", exception); 
            snmpMessage = null;
          } 
        } catch (Exception exception) {
          if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "makeResponseMessage", "Got unexpected exception", exception); 
          snmpMessage = null;
        } 
      }  
    return snmpMessage;
  }
  
  private SnmpPduPacket makeResponsePdu(SnmpPduPacket paramSnmpPduPacket, Object paramObject) {
    SnmpAdaptorServer snmpAdaptorServer = (SnmpAdaptorServer)this.adaptorServer;
    SnmpPduPacket snmpPduPacket = null;
    snmpAdaptorServer.updateRequestCounters(paramSnmpPduPacket.type);
    if (paramSnmpPduPacket.varBindList != null)
      snmpAdaptorServer.updateVarCounters(paramSnmpPduPacket.type, paramSnmpPduPacket.varBindList.length); 
    if (checkPduType(paramSnmpPduPacket)) {
      snmpPduPacket = checkAcl(paramSnmpPduPacket);
      if (snmpPduPacket == null) {
        if (this.mibs.size() < 1) {
          if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "makeResponsePdu", "Request " + paramSnmpPduPacket.requestId + " received but no MIB registered."); 
          return makeNoMibErrorPdu((SnmpPduRequest)paramSnmpPduPacket, paramObject);
        } 
        switch (paramSnmpPduPacket.type) {
          case 160:
          case 161:
          case 163:
            snmpPduPacket = makeGetSetResponsePdu((SnmpPduRequest)paramSnmpPduPacket, paramObject);
            break;
          case 165:
            snmpPduPacket = makeGetBulkResponsePdu((SnmpPduBulk)paramSnmpPduPacket, paramObject);
            break;
        } 
      } else {
        if (!snmpAdaptorServer.getAuthRespEnabled())
          snmpPduPacket = null; 
        if (snmpAdaptorServer.getAuthTrapEnabled())
          try {
            snmpAdaptorServer.snmpV1Trap(4, 0, new SnmpVarBindList());
          } catch (Exception exception) {
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
              JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "makeResponsePdu", "Failure when sending authentication trap", exception); 
          }  
      } 
    } 
    return snmpPduPacket;
  }
  
  SnmpPduPacket makeErrorVarbindPdu(SnmpPduPacket paramSnmpPduPacket, int paramInt) {
    byte b;
    SnmpVarBind[] arrayOfSnmpVarBind = paramSnmpPduPacket.varBindList;
    int i = arrayOfSnmpVarBind.length;
    switch (paramInt) {
      case 130:
        for (b = 0; b < i; b++)
          (arrayOfSnmpVarBind[b]).value = SnmpVarBind.endOfMibView; 
        return newValidResponsePdu(paramSnmpPduPacket, arrayOfSnmpVarBind);
      case 128:
        for (b = 0; b < i; b++)
          (arrayOfSnmpVarBind[b]).value = SnmpVarBind.noSuchObject; 
        return newValidResponsePdu(paramSnmpPduPacket, arrayOfSnmpVarBind);
      case 129:
        for (b = 0; b < i; b++)
          (arrayOfSnmpVarBind[b]).value = SnmpVarBind.noSuchInstance; 
        return newValidResponsePdu(paramSnmpPduPacket, arrayOfSnmpVarBind);
    } 
    return newErrorResponsePdu(paramSnmpPduPacket, 5, 1);
  }
  
  SnmpPduPacket makeNoMibErrorPdu(SnmpPduRequest paramSnmpPduRequest, Object paramObject) {
    if (paramSnmpPduRequest.version == 0)
      return newErrorResponsePdu(paramSnmpPduRequest, 2, 1); 
    if (paramSnmpPduRequest.version == 1)
      switch (paramSnmpPduRequest.type) {
        case 163:
        case 253:
          return newErrorResponsePdu(paramSnmpPduRequest, 6, 1);
        case 160:
          return makeErrorVarbindPdu(paramSnmpPduRequest, 128);
        case 161:
        case 165:
          return makeErrorVarbindPdu(paramSnmpPduRequest, 130);
      }  
    return newErrorResponsePdu(paramSnmpPduRequest, 5, 1);
  }
  
  private SnmpPduPacket makeGetSetResponsePdu(SnmpPduRequest paramSnmpPduRequest, Object paramObject) {
    if (paramSnmpPduRequest.varBindList == null)
      return newValidResponsePdu(paramSnmpPduRequest, null); 
    splitRequest(paramSnmpPduRequest);
    int i = this.subs.size();
    if (i == 1)
      return turboProcessingGetSet(paramSnmpPduRequest, paramObject); 
    SnmpPduPacket snmpPduPacket = executeSubRequest(paramSnmpPduRequest, paramObject);
    if (snmpPduPacket != null)
      return snmpPduPacket; 
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "makeGetSetResponsePdu", "Build the unified response for request " + paramSnmpPduRequest.requestId); 
    return mergeResponses(paramSnmpPduRequest);
  }
  
  private SnmpPduPacket executeSubRequest(SnmpPduPacket paramSnmpPduPacket, Object paramObject) {
    byte b1 = 0;
    if (paramSnmpPduPacket.type == 163) {
      byte b = 0;
      Enumeration enumeration1 = this.subs.elements();
      while (enumeration1.hasMoreElements()) {
        SnmpSubRequestHandler snmpSubRequestHandler = (SnmpSubRequestHandler)enumeration1.nextElement();
        snmpSubRequestHandler.setUserData(paramObject);
        snmpSubRequestHandler.type = 253;
        snmpSubRequestHandler.run();
        snmpSubRequestHandler.type = 163;
        if (snmpSubRequestHandler.getErrorStatus() != 0) {
          if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "executeSubRequest", "an error occurs"); 
          return newErrorResponsePdu(paramSnmpPduPacket, b1, snmpSubRequestHandler.getErrorIndex() + 1);
        } 
        b++;
      } 
    } 
    byte b2 = 0;
    Enumeration enumeration = this.subs.elements();
    while (enumeration.hasMoreElements()) {
      SnmpSubRequestHandler snmpSubRequestHandler = (SnmpSubRequestHandler)enumeration.nextElement();
      snmpSubRequestHandler.setUserData(paramObject);
      snmpSubRequestHandler.run();
      if (snmpSubRequestHandler.getErrorStatus() != 0) {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "executeSubRequest", "an error occurs"); 
        return newErrorResponsePdu(paramSnmpPduPacket, b1, snmpSubRequestHandler.getErrorIndex() + 1);
      } 
      b2++;
    } 
    return null;
  }
  
  private SnmpPduPacket turboProcessingGetSet(SnmpPduRequest paramSnmpPduRequest, Object paramObject) {
    SnmpSubRequestHandler snmpSubRequestHandler = (SnmpSubRequestHandler)this.subs.elements().nextElement();
    snmpSubRequestHandler.setUserData(paramObject);
    if (paramSnmpPduRequest.type == 163) {
      snmpSubRequestHandler.type = 253;
      snmpSubRequestHandler.run();
      snmpSubRequestHandler.type = 163;
      int j = snmpSubRequestHandler.getErrorStatus();
      if (j != 0)
        return newErrorResponsePdu(paramSnmpPduRequest, j, snmpSubRequestHandler.getErrorIndex() + 1); 
    } 
    snmpSubRequestHandler.run();
    int i = snmpSubRequestHandler.getErrorStatus();
    if (i != 0) {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "turboProcessingGetSet", "an error occurs"); 
      int j = snmpSubRequestHandler.getErrorIndex() + 1;
      return newErrorResponsePdu(paramSnmpPduRequest, i, j);
    } 
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "turboProcessingGetSet", "build the unified response for request " + paramSnmpPduRequest.requestId); 
    return mergeResponses(paramSnmpPduRequest);
  }
  
  private SnmpPduPacket makeGetBulkResponsePdu(SnmpPduBulk paramSnmpPduBulk, Object paramObject) {
    int n;
    int i = paramSnmpPduBulk.varBindList.length;
    int j = Math.max(Math.min(paramSnmpPduBulk.nonRepeaters, i), 0);
    int k = Math.max(paramSnmpPduBulk.maxRepetitions, 0);
    int m = i - j;
    if (paramSnmpPduBulk.varBindList == null)
      return newValidResponsePdu(paramSnmpPduBulk, null); 
    splitBulkRequest(paramSnmpPduBulk, j, k, m);
    SnmpPduPacket snmpPduPacket = executeSubRequest(paramSnmpPduBulk, paramObject);
    if (snmpPduPacket != null)
      return snmpPduPacket; 
    SnmpVarBind[] arrayOfSnmpVarBind = mergeBulkResponses(j + k * m);
    int i1;
    for (i1 = arrayOfSnmpVarBind.length; i1 > j && (arrayOfSnmpVarBind[i1 - 1]).value.equals(SnmpVarBind.endOfMibView); i1--);
    if (i1 == j) {
      n = j + m;
    } else {
      n = j + ((i1 - 1 - j) / m + 2) * m;
    } 
    if (n < arrayOfSnmpVarBind.length) {
      SnmpVarBind[] arrayOfSnmpVarBind1 = new SnmpVarBind[n];
      for (byte b = 0; b < n; b++)
        arrayOfSnmpVarBind1[b] = arrayOfSnmpVarBind[b]; 
      arrayOfSnmpVarBind = arrayOfSnmpVarBind1;
    } 
    return newValidResponsePdu(paramSnmpPduBulk, arrayOfSnmpVarBind);
  }
  
  private boolean checkPduType(SnmpPduPacket paramSnmpPduPacket) {
    switch (paramSnmpPduPacket.type) {
      case 160:
      case 161:
      case 163:
      case 165:
        return true;
    } 
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "checkPduType", "cannot respond to this kind of PDU"); 
    return false;
  }
  
  private SnmpPduPacket checkAcl(SnmpPduPacket paramSnmpPduPacket) {
    SnmpPduRequest snmpPduRequest = null;
    String str = new String(paramSnmpPduPacket.community);
    if (this.ipacl != null)
      if (paramSnmpPduPacket.type == 163) {
        if (!this.ipacl.checkWritePermission(paramSnmpPduPacket.address, str)) {
          if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "checkAcl", "sender is " + paramSnmpPduPacket.address + " with " + str + ". Sender has no write permission"); 
          int i = SnmpSubRequestHandler.mapErrorStatus(16, paramSnmpPduPacket.version, paramSnmpPduPacket.type);
          snmpPduRequest = newErrorResponsePdu(paramSnmpPduPacket, i, 0);
        } else if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "checkAcl", "sender is " + paramSnmpPduPacket.address + " with " + str + ". Sender has write permission");
        } 
      } else if (!this.ipacl.checkReadPermission(paramSnmpPduPacket.address, str)) {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "checkAcl", "sender is " + paramSnmpPduPacket.address + " with " + str + ". Sender has no read permission"); 
        int i = SnmpSubRequestHandler.mapErrorStatus(16, paramSnmpPduPacket.version, paramSnmpPduPacket.type);
        snmpPduRequest = newErrorResponsePdu(paramSnmpPduPacket, i, 0);
        SnmpAdaptorServer snmpAdaptorServer = (SnmpAdaptorServer)this.adaptorServer;
        snmpAdaptorServer.updateErrorCounters(2);
      } else if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "checkAcl", "sender is " + paramSnmpPduPacket.address + " with " + str + ". Sender has read permission");
      }  
    if (snmpPduRequest != null) {
      SnmpAdaptorServer snmpAdaptorServer = (SnmpAdaptorServer)this.adaptorServer;
      snmpAdaptorServer.incSnmpInBadCommunityUses(1);
      if (!this.ipacl.checkCommunity(str))
        snmpAdaptorServer.incSnmpInBadCommunityNames(1); 
    } 
    return snmpPduRequest;
  }
  
  private SnmpPduRequest newValidResponsePdu(SnmpPduPacket paramSnmpPduPacket, SnmpVarBind[] paramArrayOfSnmpVarBind) {
    SnmpPduRequest snmpPduRequest = new SnmpPduRequest();
    snmpPduRequest.address = paramSnmpPduPacket.address;
    snmpPduRequest.port = paramSnmpPduPacket.port;
    snmpPduRequest.version = paramSnmpPduPacket.version;
    snmpPduRequest.community = paramSnmpPduPacket.community;
    snmpPduRequest.type = 162;
    snmpPduRequest.requestId = paramSnmpPduPacket.requestId;
    snmpPduRequest.errorStatus = 0;
    snmpPduRequest.errorIndex = 0;
    snmpPduRequest.varBindList = paramArrayOfSnmpVarBind;
    ((SnmpAdaptorServer)this.adaptorServer).updateErrorCounters(snmpPduRequest.errorStatus);
    return snmpPduRequest;
  }
  
  private SnmpPduRequest newErrorResponsePdu(SnmpPduPacket paramSnmpPduPacket, int paramInt1, int paramInt2) {
    SnmpPduRequest snmpPduRequest = newValidResponsePdu(paramSnmpPduPacket, null);
    snmpPduRequest.errorStatus = paramInt1;
    snmpPduRequest.errorIndex = paramInt2;
    snmpPduRequest.varBindList = paramSnmpPduPacket.varBindList;
    ((SnmpAdaptorServer)this.adaptorServer).updateErrorCounters(snmpPduRequest.errorStatus);
    return snmpPduRequest;
  }
  
  private SnmpMessage newTooBigMessage(SnmpMessage paramSnmpMessage) {
    SnmpMessage snmpMessage = null;
    try {
      SnmpPduPacket snmpPduPacket = (SnmpPduPacket)this.pduFactory.decodeSnmpPdu(paramSnmpMessage);
      if (snmpPduPacket != null) {
        SnmpPduPacket snmpPduPacket1 = newTooBigPdu(snmpPduPacket);
        snmpMessage = (SnmpMessage)this.pduFactory.encodeSnmpPdu(snmpPduPacket1, this.packet.getData().length);
      } 
    } catch (SnmpStatusException snmpStatusException) {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "newTooBigMessage", "Internal error", snmpStatusException); 
      throw new InternalError(snmpStatusException);
    } 
    return snmpMessage;
  }
  
  private SnmpPduPacket newTooBigPdu(SnmpPduPacket paramSnmpPduPacket) {
    SnmpPduRequest snmpPduRequest = newErrorResponsePdu(paramSnmpPduPacket, 1, 0);
    snmpPduRequest.varBindList = null;
    return snmpPduRequest;
  }
  
  private SnmpPduPacket reduceResponsePdu(SnmpPduPacket paramSnmpPduPacket1, SnmpPduPacket paramSnmpPduPacket2, int paramInt) throws SnmpTooBigException {
    int i;
    if (paramSnmpPduPacket1.type != 165) {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "reduceResponsePdu", "cannot remove anything"); 
      throw new SnmpTooBigException(paramInt);
    } 
    if (paramInt >= 3) {
      i = Math.min(paramInt - 1, paramSnmpPduPacket2.varBindList.length);
    } else if (paramInt == 1) {
      i = 1;
    } else {
      i = paramSnmpPduPacket2.varBindList.length / 2;
    } 
    if (i < 1) {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "reduceResponsePdu", "cannot remove anything"); 
      throw new SnmpTooBigException(paramInt);
    } 
    SnmpVarBind[] arrayOfSnmpVarBind = new SnmpVarBind[i];
    for (byte b = 0; b < i; b++)
      arrayOfSnmpVarBind[b] = paramSnmpPduPacket2.varBindList[b]; 
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "reduceResponsePdu", (paramSnmpPduPacket2.varBindList.length - arrayOfSnmpVarBind.length) + " items have been removed"); 
    paramSnmpPduPacket2.varBindList = arrayOfSnmpVarBind;
    return paramSnmpPduPacket2;
  }
  
  private void splitRequest(SnmpPduRequest paramSnmpPduRequest) {
    int i = this.mibs.size();
    SnmpMibAgent snmpMibAgent = (SnmpMibAgent)this.mibs.firstElement();
    if (i == 1) {
      this.subs.put(snmpMibAgent, new SnmpSubRequestHandler(snmpMibAgent, paramSnmpPduRequest, true));
      return;
    } 
    if (paramSnmpPduRequest.type == 161) {
      Enumeration enumeration = this.mibs.elements();
      while (enumeration.hasMoreElements()) {
        SnmpMibAgent snmpMibAgent1 = (SnmpMibAgent)enumeration.nextElement();
        this.subs.put(snmpMibAgent1, new SnmpSubNextRequestHandler(this.adaptor, snmpMibAgent1, paramSnmpPduRequest));
      } 
      return;
    } 
    int j = paramSnmpPduRequest.varBindList.length;
    SnmpVarBind[] arrayOfSnmpVarBind = paramSnmpPduRequest.varBindList;
    for (byte b = 0; b < j; b++) {
      snmpMibAgent = this.root.getAgentMib((arrayOfSnmpVarBind[b]).oid);
      SnmpSubRequestHandler snmpSubRequestHandler = (SnmpSubRequestHandler)this.subs.get(snmpMibAgent);
      if (snmpSubRequestHandler == null) {
        snmpSubRequestHandler = new SnmpSubRequestHandler(snmpMibAgent, paramSnmpPduRequest);
        this.subs.put(snmpMibAgent, snmpSubRequestHandler);
      } 
      snmpSubRequestHandler.updateRequest(arrayOfSnmpVarBind[b], b);
    } 
  }
  
  private void splitBulkRequest(SnmpPduBulk paramSnmpPduBulk, int paramInt1, int paramInt2, int paramInt3) {
    Enumeration enumeration = this.mibs.elements();
    while (enumeration.hasMoreElements()) {
      SnmpMibAgent snmpMibAgent = (SnmpMibAgent)enumeration.nextElement();
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "splitBulkRequest", "Create a sub with : " + snmpMibAgent + " " + paramInt1 + " " + paramInt2 + " " + paramInt3); 
      this.subs.put(snmpMibAgent, new SnmpSubBulkRequestHandler(this.adaptor, snmpMibAgent, paramSnmpPduBulk, paramInt1, paramInt2, paramInt3));
    } 
  }
  
  private SnmpPduPacket mergeResponses(SnmpPduRequest paramSnmpPduRequest) {
    if (paramSnmpPduRequest.type == 161)
      return mergeNextResponses(paramSnmpPduRequest); 
    SnmpVarBind[] arrayOfSnmpVarBind = paramSnmpPduRequest.varBindList;
    Enumeration enumeration = this.subs.elements();
    while (enumeration.hasMoreElements()) {
      SnmpSubRequestHandler snmpSubRequestHandler = (SnmpSubRequestHandler)enumeration.nextElement();
      snmpSubRequestHandler.updateResult(arrayOfSnmpVarBind);
    } 
    return newValidResponsePdu(paramSnmpPduRequest, arrayOfSnmpVarBind);
  }
  
  private SnmpPduPacket mergeNextResponses(SnmpPduRequest paramSnmpPduRequest) {
    int i = paramSnmpPduRequest.varBindList.length;
    SnmpVarBind[] arrayOfSnmpVarBind = new SnmpVarBind[i];
    Enumeration enumeration = this.subs.elements();
    while (enumeration.hasMoreElements()) {
      SnmpSubRequestHandler snmpSubRequestHandler = (SnmpSubRequestHandler)enumeration.nextElement();
      snmpSubRequestHandler.updateResult(arrayOfSnmpVarBind);
    } 
    if (paramSnmpPduRequest.version == 1)
      return newValidResponsePdu(paramSnmpPduRequest, arrayOfSnmpVarBind); 
    for (byte b = 0; b < i; b++) {
      SnmpValue snmpValue = (arrayOfSnmpVarBind[b]).value;
      if (snmpValue == SnmpVarBind.endOfMibView)
        return newErrorResponsePdu(paramSnmpPduRequest, 2, b + true); 
    } 
    return newValidResponsePdu(paramSnmpPduRequest, arrayOfSnmpVarBind);
  }
  
  private SnmpVarBind[] mergeBulkResponses(int paramInt) {
    SnmpVarBind[] arrayOfSnmpVarBind = new SnmpVarBind[paramInt];
    for (int i = paramInt - 1; i >= 0; i--) {
      arrayOfSnmpVarBind[i] = new SnmpVarBind();
      (arrayOfSnmpVarBind[i]).value = SnmpVarBind.endOfMibView;
    } 
    Enumeration enumeration = this.subs.elements();
    while (enumeration.hasMoreElements()) {
      SnmpSubRequestHandler snmpSubRequestHandler = (SnmpSubRequestHandler)enumeration.nextElement();
      snmpSubRequestHandler.updateResult(arrayOfSnmpVarBind);
    } 
    return arrayOfSnmpVarBind;
  }
  
  protected String makeDebugTag() { return "SnmpRequestHandler[" + this.adaptorServer.getProtocol() + ":" + this.adaptorServer.getPort() + "]"; }
  
  Thread createThread(Runnable paramRunnable) { return null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\daemon\SnmpRequestHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */