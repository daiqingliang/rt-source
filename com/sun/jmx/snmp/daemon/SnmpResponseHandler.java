package com.sun.jmx.snmp.daemon;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.snmp.SnmpMessage;
import com.sun.jmx.snmp.SnmpPduFactory;
import com.sun.jmx.snmp.SnmpPduPacket;
import com.sun.jmx.snmp.SnmpPduRequest;
import java.net.DatagramPacket;
import java.util.logging.Level;

class SnmpResponseHandler {
  SnmpAdaptorServer adaptor = null;
  
  SnmpQManager snmpq = null;
  
  public SnmpResponseHandler(SnmpAdaptorServer paramSnmpAdaptorServer, SnmpQManager paramSnmpQManager) {
    this.adaptor = paramSnmpAdaptorServer;
    this.snmpq = paramSnmpQManager;
  }
  
  public void processDatagram(DatagramPacket paramDatagramPacket) {
    byte[] arrayOfByte = paramDatagramPacket.getData();
    int i = paramDatagramPacket.getLength();
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpResponseHandler.class.getName(), "action", "processDatagram", "Received from " + paramDatagramPacket.getAddress().toString() + " Length = " + i + "\nDump : \n" + SnmpMessage.dumpHexBuffer(arrayOfByte, 0, i)); 
    try {
      SnmpMessage snmpMessage = new SnmpMessage();
      snmpMessage.decodeMessage(arrayOfByte, i);
      snmpMessage.address = paramDatagramPacket.getAddress();
      snmpMessage.port = paramDatagramPacket.getPort();
      SnmpPduFactory snmpPduFactory = this.adaptor.getPduFactory();
      if (snmpPduFactory == null) {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpResponseHandler.class.getName(), "processDatagram", "Dropping packet. Unable to find the pdu factory of the SNMP adaptor server"); 
      } else {
        SnmpPduPacket snmpPduPacket = (SnmpPduPacket)snmpPduFactory.decodeSnmpPdu(snmpMessage);
        if (snmpPduPacket == null) {
          if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpResponseHandler.class.getName(), "processDatagram", "Dropping packet. Pdu factory returned a null value"); 
        } else if (snmpPduPacket instanceof SnmpPduRequest) {
          SnmpPduRequest snmpPduRequest = (SnmpPduRequest)snmpPduPacket;
          SnmpInformRequest snmpInformRequest = this.snmpq.removeRequest(snmpPduRequest.requestId);
          if (snmpInformRequest != null) {
            snmpInformRequest.invokeOnResponse(snmpPduRequest);
          } else if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpResponseHandler.class.getName(), "processDatagram", "Dropping packet. Unable to find corresponding for InformRequestId = " + snmpPduRequest.requestId);
          } 
        } else if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpResponseHandler.class.getName(), "processDatagram", "Dropping packet. The packet does not contain an inform response");
        } 
        snmpPduPacket = null;
      } 
    } catch (Exception exception) {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpResponseHandler.class.getName(), "processDatagram", "Exception while processsing", exception); 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\daemon\SnmpResponseHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */