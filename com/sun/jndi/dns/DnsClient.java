package com.sun.jndi.dns;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.naming.CommunicationException;
import javax.naming.ConfigurationException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;
import javax.naming.ServiceUnavailableException;
import sun.security.jca.JCAUtil;

public class DnsClient {
  private static final int IDENT_OFFSET = 0;
  
  private static final int FLAGS_OFFSET = 2;
  
  private static final int NUMQ_OFFSET = 4;
  
  private static final int NUMANS_OFFSET = 6;
  
  private static final int NUMAUTH_OFFSET = 8;
  
  private static final int NUMADD_OFFSET = 10;
  
  private static final int DNS_HDR_SIZE = 12;
  
  private static final int NO_ERROR = 0;
  
  private static final int FORMAT_ERROR = 1;
  
  private static final int SERVER_FAILURE = 2;
  
  private static final int NAME_ERROR = 3;
  
  private static final int NOT_IMPL = 4;
  
  private static final int REFUSED = 5;
  
  private static final String[] rcodeDescription = { "No error", "DNS format error", "DNS server failure", "DNS name not found", "DNS operation not supported", "DNS service refused" };
  
  private static final int DEFAULT_PORT = 53;
  
  private static final int TRANSACTION_ID_BOUND = 65536;
  
  private static final SecureRandom random = JCAUtil.getSecureRandom();
  
  private InetAddress[] servers;
  
  private int[] serverPorts;
  
  private int timeout;
  
  private int retries;
  
  private final Object udpSocketLock = new Object();
  
  private static final DNSDatagramSocketFactory factory = new DNSDatagramSocketFactory(random);
  
  private Map<Integer, ResourceRecord> reqs;
  
  private Map<Integer, byte[]> resps;
  
  private Object queuesLock = new Object();
  
  private static final boolean debug = false;
  
  public DnsClient(String[] paramArrayOfString, int paramInt1, int paramInt2) throws NamingException {
    this.timeout = paramInt1;
    this.retries = paramInt2;
    this.servers = new InetAddress[paramArrayOfString.length];
    this.serverPorts = new int[paramArrayOfString.length];
    for (byte b = 0; b < paramArrayOfString.length; b++) {
      int i = paramArrayOfString[b].indexOf(':', paramArrayOfString[b].indexOf(']') + 1);
      this.serverPorts[b] = (i < 0) ? 53 : Integer.parseInt(paramArrayOfString[b].substring(i + 1));
      String str = (i < 0) ? paramArrayOfString[b] : paramArrayOfString[b].substring(0, i);
      try {
        this.servers[b] = InetAddress.getByName(str);
      } catch (UnknownHostException unknownHostException) {
        ConfigurationException configurationException = new ConfigurationException("Unknown DNS server: " + str);
        configurationException.setRootCause(unknownHostException);
        throw configurationException;
      } 
    } 
    this.reqs = Collections.synchronizedMap(new HashMap());
    this.resps = Collections.synchronizedMap(new HashMap());
  }
  
  DatagramSocket getDatagramSocket() throws NamingException {
    try {
      return factory.open();
    } catch (SocketException socketException) {
      ConfigurationException configurationException = new ConfigurationException();
      configurationException.setRootCause(socketException);
      throw configurationException;
    } 
  }
  
  protected void finalize() { close(); }
  
  public void close() {
    synchronized (this.queuesLock) {
      this.reqs.clear();
      this.resps.clear();
    } 
  }
  
  ResourceRecords query(DnsName paramDnsName, int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2) throws NamingException {
    ResourceRecord resourceRecord;
    Packet packet;
    do {
      i = random.nextInt(65536);
      packet = makeQueryPacket(paramDnsName, i, paramInt1, paramInt2, paramBoolean1);
      resourceRecord = (ResourceRecord)this.reqs.putIfAbsent(Integer.valueOf(i), new ResourceRecord(packet.getData(), packet.length(), 12, true, false));
    } while (resourceRecord != null);
    NamingException namingException = null;
    boolean[] arrayOfBoolean = new boolean[this.servers.length];
    try {
      for (byte b = 0; b < this.retries; b++) {
        for (byte b1 = 0; b1 < this.servers.length; b1++) {
          CommunicationException communicationException1;
          if (arrayOfBoolean[b1])
            continue; 
          try {
            byte[] arrayOfByte = null;
            arrayOfByte = doUdpQuery(packet, this.servers[b1], this.serverPorts[b1], b, i);
            if (arrayOfByte == null) {
              if (this.resps.size() > 0)
                arrayOfByte = lookupResponse(Integer.valueOf(i)); 
              if (arrayOfByte == null)
                continue; 
            } 
            Header header = new Header(arrayOfByte, arrayOfByte.length);
            if (paramBoolean2 && !header.authoritative) {
              namingException = new NameNotFoundException("DNS response not authoritative");
              arrayOfBoolean[b1] = true;
            } else {
              if (header.truncated)
                for (byte b2 = 0; b2 < this.servers.length; b2++) {
                  int j = (b1 + b2) % this.servers.length;
                  if (!arrayOfBoolean[j])
                    try {
                      byte[] arrayOfByte1;
                      tcp = new Tcp(this.servers[j], this.serverPorts[j]);
                      try {
                        arrayOfByte1 = doTcpQuery(tcp, packet);
                      } finally {
                        tcp.close();
                      } 
                      Header header1 = new Header(arrayOfByte1, arrayOfByte1.length);
                      if (header1.query)
                        throw new CommunicationException("DNS error: expecting response"); 
                      checkResponseCode(header1);
                      if (!paramBoolean2 || header1.authoritative) {
                        header = header1;
                        arrayOfByte = arrayOfByte1;
                        break;
                      } 
                      arrayOfBoolean[j] = true;
                    } catch (Exception exception) {} 
                }  
              return new ResourceRecords(arrayOfByte, arrayOfByte.length, header, false);
            } 
            continue;
          } catch (IOException iOException) {
            if (namingException == null)
              communicationException1 = iOException; 
            if (iOException.getClass().getName().equals("java.net.PortUnreachableException"))
              arrayOfBoolean[b1] = true; 
            continue;
          } catch (NameNotFoundException nameNotFoundException) {
            throw nameNotFoundException;
          } catch (CommunicationException communicationException2) {
            if (communicationException1 == null)
              communicationException1 = communicationException2; 
            continue;
          } catch (NamingException namingException1) {
            if (communicationException1 == null)
              namingException = namingException1; 
            arrayOfBoolean[b1] = true;
            continue;
          } 
        } 
      } 
    } finally {
      this.reqs.remove(Integer.valueOf(i));
    } 
    if (namingException instanceof NamingException)
      throw (NamingException)namingException; 
    CommunicationException communicationException = new CommunicationException("DNS error");
    communicationException.setRootCause(namingException);
    throw communicationException;
  }
  
  ResourceRecords queryZone(DnsName paramDnsName, int paramInt, boolean paramBoolean) throws NamingException {
    int i = random.nextInt(65536);
    Packet packet = makeQueryPacket(paramDnsName, i, paramInt, 252, paramBoolean);
    NamingException namingException = null;
    for (byte b = 0; b < this.servers.length; b++) {
      try {
        tcp = new Tcp(this.servers[b], this.serverPorts[b]);
        try {
          byte[] arrayOfByte = doTcpQuery(tcp, packet);
          Header header = new Header(arrayOfByte, arrayOfByte.length);
          checkResponseCode(header);
          ResourceRecords resourceRecords = new ResourceRecords(arrayOfByte, arrayOfByte.length, header, true);
          if (resourceRecords.getFirstAnsType() != 6)
            throw new CommunicationException("DNS error: zone xfer doesn't begin with SOA"); 
          if (resourceRecords.answer.size() == 1 || resourceRecords.getLastAnsType() != 6)
            do {
              arrayOfByte = continueTcpQuery(tcp);
              if (arrayOfByte == null)
                throw new CommunicationException("DNS error: incomplete zone transfer"); 
              header = new Header(arrayOfByte, arrayOfByte.length);
              checkResponseCode(header);
              resourceRecords.add(arrayOfByte, arrayOfByte.length, header);
            } while (resourceRecords.getLastAnsType() != 6); 
          resourceRecords.answer.removeElementAt(resourceRecords.answer.size() - 1);
          return resourceRecords;
        } finally {
          tcp.close();
        } 
      } catch (IOException iOException) {
        namingException = iOException;
      } catch (NameNotFoundException nameNotFoundException) {
        throw nameNotFoundException;
      } catch (NamingException namingException1) {
        namingException = namingException1;
      } 
    } 
    if (namingException instanceof NamingException)
      throw (NamingException)namingException; 
    CommunicationException communicationException = new CommunicationException("DNS error during zone transfer");
    communicationException.setRootCause(namingException);
    throw communicationException;
  }
  
  private byte[] doUdpQuery(Packet paramPacket, InetAddress paramInetAddress, int paramInt1, int paramInt2, int paramInt3) throws IOException, NamingException {
    byte b = 50;
    synchronized (this.udpSocketLock) {
      try (DatagramSocket null = getDatagramSocket()) {
        DatagramPacket datagramPacket1 = new DatagramPacket(paramPacket.getData(), paramPacket.length(), paramInetAddress, paramInt1);
        DatagramPacket datagramPacket2 = new DatagramPacket(new byte[8000], 8000);
        datagramSocket.connect(paramInetAddress, paramInt1);
        int i = this.timeout * (1 << paramInt2);
        try {
          datagramSocket.send(datagramPacket1);
          int j = i;
          boolean bool = false;
          do {
            datagramSocket.setSoTimeout(j);
            long l1 = System.currentTimeMillis();
            datagramSocket.receive(datagramPacket2);
            long l2 = System.currentTimeMillis();
            byte[] arrayOfByte = datagramPacket2.getData();
            if (isMatchResponse(arrayOfByte, paramInt3))
              return arrayOfByte; 
            j = i - (int)(l2 - l1);
          } while (j > b);
        } finally {
          datagramSocket.disconnect();
        } 
        return null;
      } 
    } 
  }
  
  private byte[] doTcpQuery(Tcp paramTcp, Packet paramPacket) throws IOException {
    int i = paramPacket.length();
    paramTcp.out.write(i >> 8);
    paramTcp.out.write(i);
    paramTcp.out.write(paramPacket.getData(), 0, i);
    paramTcp.out.flush();
    byte[] arrayOfByte = continueTcpQuery(paramTcp);
    if (arrayOfByte == null)
      throw new IOException("DNS error: no response"); 
    return arrayOfByte;
  }
  
  private byte[] continueTcpQuery(Tcp paramTcp) throws IOException {
    int i = paramTcp.in.read();
    if (i == -1)
      return null; 
    int j = paramTcp.in.read();
    if (j == -1)
      throw new IOException("Corrupted DNS response: bad length"); 
    int k = i << 8 | j;
    byte[] arrayOfByte = new byte[k];
    int m;
    for (m = 0; k > 0; m += n) {
      int n = paramTcp.in.read(arrayOfByte, m, k);
      if (n == -1)
        throw new IOException("Corrupted DNS response: too little data"); 
      k -= n;
    } 
    return arrayOfByte;
  }
  
  private Packet makeQueryPacket(DnsName paramDnsName, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean) {
    short s1 = paramDnsName.getOctets();
    short s2 = 12 + s1 + 4;
    Packet packet = new Packet(s2);
    char c = paramBoolean ? 256 : 0;
    packet.putShort(paramInt1, 0);
    packet.putShort(c, 2);
    packet.putShort(1, 4);
    packet.putShort(0, 6);
    packet.putInt(0, 8);
    makeQueryName(paramDnsName, packet, 12);
    packet.putShort(paramInt3, 12 + s1);
    packet.putShort(paramInt2, 12 + s1 + 2);
    return packet;
  }
  
  private void makeQueryName(DnsName paramDnsName, Packet paramPacket, int paramInt) {
    for (int i = paramDnsName.size() - 1; i >= 0; i--) {
      String str = paramDnsName.get(i);
      int j = str.length();
      paramPacket.putByte(j, paramInt++);
      for (byte b = 0; b < j; b++)
        paramPacket.putByte(str.charAt(b), paramInt++); 
    } 
    if (!paramDnsName.hasRootLabel())
      paramPacket.putByte(0, paramInt); 
  }
  
  private byte[] lookupResponse(Integer paramInteger) throws NamingException {
    byte[] arrayOfByte;
    if ((arrayOfByte = (byte[])this.resps.get(paramInteger)) != null) {
      checkResponseCode(new Header(arrayOfByte, arrayOfByte.length));
      synchronized (this.queuesLock) {
        this.resps.remove(paramInteger);
        this.reqs.remove(paramInteger);
      } 
    } 
    return arrayOfByte;
  }
  
  private boolean isMatchResponse(byte[] paramArrayOfByte, int paramInt) throws NamingException {
    Header header = new Header(paramArrayOfByte, paramArrayOfByte.length);
    if (header.query)
      throw new CommunicationException("DNS error: expecting response"); 
    if (!this.reqs.containsKey(Integer.valueOf(paramInt)))
      return false; 
    if (header.xid == paramInt) {
      checkResponseCode(header);
      if (!header.query && header.numQuestions == 1) {
        ResourceRecord resourceRecord1 = new ResourceRecord(paramArrayOfByte, paramArrayOfByte.length, 12, true, false);
        ResourceRecord resourceRecord2 = (ResourceRecord)this.reqs.get(Integer.valueOf(paramInt));
        int i = resourceRecord2.getType();
        int j = resourceRecord2.getRrclass();
        DnsName dnsName = resourceRecord2.getName();
        if ((i == 255 || i == resourceRecord1.getType()) && (j == 255 || j == resourceRecord1.getRrclass()) && dnsName.equals(resourceRecord1.getName())) {
          synchronized (this.queuesLock) {
            this.resps.remove(Integer.valueOf(paramInt));
            this.reqs.remove(Integer.valueOf(paramInt));
          } 
          return true;
        } 
      } 
      return false;
    } 
    synchronized (this.queuesLock) {
      if (this.reqs.containsKey(Integer.valueOf(header.xid)))
        this.resps.put(Integer.valueOf(header.xid), paramArrayOfByte); 
    } 
    return false;
  }
  
  private void checkResponseCode(Header paramHeader) throws NamingException {
    int i = paramHeader.rcode;
    if (i == 0)
      return; 
    String str = (i < rcodeDescription.length) ? rcodeDescription[i] : "DNS error";
    str = str + " [response code " + i + "]";
    switch (i) {
      case 2:
        throw new ServiceUnavailableException(str);
      case 3:
        throw new NameNotFoundException(str);
      case 4:
      case 5:
        throw new OperationNotSupportedException(str);
    } 
    throw new NamingException(str);
  }
  
  private static void dprint(String paramString) {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\dns\DnsClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */