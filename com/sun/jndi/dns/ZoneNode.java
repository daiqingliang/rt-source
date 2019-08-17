package com.sun.jndi.dns;

import java.lang.ref.SoftReference;
import java.util.Date;

class ZoneNode extends NameNode {
  private SoftReference<NameNode> contentsRef = null;
  
  private long serialNumber = -1L;
  
  private Date expiration = null;
  
  ZoneNode(String paramString) { super(paramString); }
  
  protected NameNode newNameNode(String paramString) { return new ZoneNode(paramString); }
  
  void depopulate() {
    this.contentsRef = null;
    this.serialNumber = -1L;
  }
  
  boolean isPopulated() { return (getContents() != null); }
  
  NameNode getContents() { return (this.contentsRef != null) ? (NameNode)this.contentsRef.get() : null; }
  
  boolean isExpired() { return (this.expiration != null && this.expiration.before(new Date())); }
  
  ZoneNode getDeepestPopulated(DnsName paramDnsName) {
    ZoneNode zoneNode1 = this;
    ZoneNode zoneNode2 = isPopulated() ? this : null;
    for (byte b = 1; b < paramDnsName.size(); b++) {
      zoneNode1 = (ZoneNode)zoneNode1.get(paramDnsName.getKey(b));
      if (zoneNode1 == null)
        break; 
      if (zoneNode1.isPopulated())
        zoneNode2 = zoneNode1; 
    } 
    return zoneNode2;
  }
  
  NameNode populate(DnsName paramDnsName, ResourceRecords paramResourceRecords) {
    NameNode nameNode = new NameNode(null);
    for (byte b = 0; b < paramResourceRecords.answer.size(); b++) {
      ResourceRecord resourceRecord1 = (ResourceRecord)paramResourceRecords.answer.elementAt(b);
      DnsName dnsName = resourceRecord1.getName();
      if (dnsName.size() > paramDnsName.size() && dnsName.startsWith(paramDnsName)) {
        NameNode nameNode1 = nameNode.add(dnsName, paramDnsName.size());
        if (resourceRecord1.getType() == 2)
          nameNode1.setZoneCut(true); 
      } 
    } 
    ResourceRecord resourceRecord = (ResourceRecord)paramResourceRecords.answer.firstElement();
    synchronized (this) {
      this.contentsRef = new SoftReference(nameNode);
      this.serialNumber = getSerialNumber(resourceRecord);
      setExpiration(getMinimumTtl(resourceRecord));
      return nameNode;
    } 
  }
  
  private void setExpiration(long paramLong) { this.expiration = new Date(System.currentTimeMillis() + 1000L * paramLong); }
  
  private static long getMinimumTtl(ResourceRecord paramResourceRecord) {
    String str = (String)paramResourceRecord.getRdata();
    int i = str.lastIndexOf(' ') + 1;
    return Long.parseLong(str.substring(i));
  }
  
  int compareSerialNumberTo(ResourceRecord paramResourceRecord) { return ResourceRecord.compareSerialNumbers(this.serialNumber, getSerialNumber(paramResourceRecord)); }
  
  private static long getSerialNumber(ResourceRecord paramResourceRecord) {
    String str = (String)paramResourceRecord.getRdata();
    int i = str.length();
    int j = -1;
    for (byte b = 0; b < 5; b++) {
      j = i;
      i = str.lastIndexOf(' ', j - 1);
    } 
    return Long.parseLong(str.substring(i + 1, j));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\dns\ZoneNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */