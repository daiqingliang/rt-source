package com.sun.jndi.dns;

import javax.naming.CommunicationException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

class Resolver {
  private DnsClient dnsClient;
  
  private int timeout;
  
  private int retries;
  
  Resolver(String[] paramArrayOfString, int paramInt1, int paramInt2) throws NamingException {
    this.timeout = paramInt1;
    this.retries = paramInt2;
    this.dnsClient = new DnsClient(paramArrayOfString, paramInt1, paramInt2);
  }
  
  public void close() {
    this.dnsClient.close();
    this.dnsClient = null;
  }
  
  ResourceRecords query(DnsName paramDnsName, int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2) throws NamingException { return this.dnsClient.query(paramDnsName, paramInt1, paramInt2, paramBoolean1, paramBoolean2); }
  
  ResourceRecords queryZone(DnsName paramDnsName, int paramInt, boolean paramBoolean) throws NamingException {
    dnsClient1 = new DnsClient(findNameServers(paramDnsName, paramBoolean), this.timeout, this.retries);
    try {
      return dnsClient1.queryZone(paramDnsName, paramInt, paramBoolean);
    } finally {
      dnsClient1.close();
    } 
  }
  
  DnsName findZoneName(DnsName paramDnsName, int paramInt, boolean paramBoolean) throws NamingException {
    paramDnsName = (DnsName)paramDnsName.clone();
    while (paramDnsName.size() > 1) {
      ResourceRecords resourceRecords = null;
      try {
        resourceRecords = query(paramDnsName, paramInt, 6, paramBoolean, false);
      } catch (NameNotFoundException nameNotFoundException) {
        throw nameNotFoundException;
      } catch (NamingException namingException) {}
      if (resourceRecords != null) {
        if (resourceRecords.answer.size() > 0)
          return paramDnsName; 
        for (byte b = 0; b < resourceRecords.authority.size(); b++) {
          ResourceRecord resourceRecord = (ResourceRecord)resourceRecords.authority.elementAt(b);
          if (resourceRecord.getType() == 6) {
            DnsName dnsName = resourceRecord.getName();
            if (paramDnsName.endsWith(dnsName))
              return dnsName; 
          } 
        } 
      } 
      paramDnsName.remove(paramDnsName.size() - 1);
    } 
    return paramDnsName;
  }
  
  ResourceRecord findSoa(DnsName paramDnsName, int paramInt, boolean paramBoolean) throws NamingException {
    ResourceRecords resourceRecords = query(paramDnsName, paramInt, 6, paramBoolean, false);
    for (byte b = 0; b < resourceRecords.answer.size(); b++) {
      ResourceRecord resourceRecord = (ResourceRecord)resourceRecords.answer.elementAt(b);
      if (resourceRecord.getType() == 6)
        return resourceRecord; 
    } 
    return null;
  }
  
  private String[] findNameServers(DnsName paramDnsName, boolean paramBoolean) throws NamingException {
    ResourceRecords resourceRecords = query(paramDnsName, 1, 2, paramBoolean, false);
    String[] arrayOfString = new String[resourceRecords.answer.size()];
    for (byte b = 0; b < arrayOfString.length; b++) {
      ResourceRecord resourceRecord = (ResourceRecord)resourceRecords.answer.elementAt(b);
      if (resourceRecord.getType() != 2)
        throw new CommunicationException("Corrupted DNS message"); 
      arrayOfString[b] = (String)resourceRecord.getRdata();
      arrayOfString[b] = arrayOfString[b].substring(0, arrayOfString[b].length() - 1);
    } 
    return arrayOfString;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\dns\Resolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */