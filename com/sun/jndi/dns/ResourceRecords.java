package com.sun.jndi.dns;

import java.util.Vector;
import javax.naming.CommunicationException;
import javax.naming.NamingException;

class ResourceRecords {
  Vector<ResourceRecord> question = new Vector();
  
  Vector<ResourceRecord> answer = new Vector();
  
  Vector<ResourceRecord> authority = new Vector();
  
  Vector<ResourceRecord> additional = new Vector();
  
  boolean zoneXfer;
  
  ResourceRecords(byte[] paramArrayOfByte, int paramInt, Header paramHeader, boolean paramBoolean) throws NamingException {
    if (paramBoolean)
      this.answer.ensureCapacity(8192); 
    this.zoneXfer = paramBoolean;
    add(paramArrayOfByte, paramInt, paramHeader);
  }
  
  int getFirstAnsType() { return (this.answer.size() == 0) ? -1 : ((ResourceRecord)this.answer.firstElement()).getType(); }
  
  int getLastAnsType() { return (this.answer.size() == 0) ? -1 : ((ResourceRecord)this.answer.lastElement()).getType(); }
  
  void add(byte[] paramArrayOfByte, int paramInt, Header paramHeader) throws NamingException {
    int i = 12;
    try {
      byte b;
      for (b = 0; b < paramHeader.numQuestions; b++) {
        ResourceRecord resourceRecord = new ResourceRecord(paramArrayOfByte, paramInt, i, true, false);
        if (!this.zoneXfer)
          this.question.addElement(resourceRecord); 
        i += resourceRecord.size();
      } 
      for (b = 0; b < paramHeader.numAnswers; b++) {
        ResourceRecord resourceRecord = new ResourceRecord(paramArrayOfByte, paramInt, i, false, !this.zoneXfer);
        this.answer.addElement(resourceRecord);
        i += resourceRecord.size();
      } 
      if (this.zoneXfer)
        return; 
      for (b = 0; b < paramHeader.numAuthorities; b++) {
        ResourceRecord resourceRecord = new ResourceRecord(paramArrayOfByte, paramInt, i, false, true);
        this.authority.addElement(resourceRecord);
        i += resourceRecord.size();
      } 
    } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
      throw new CommunicationException("DNS error: corrupted message");
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\dns\ResourceRecords.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */