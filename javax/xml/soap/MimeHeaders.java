package javax.xml.soap;

import java.util.Iterator;
import java.util.Vector;

public class MimeHeaders {
  private Vector headers = new Vector();
  
  public String[] getHeader(String paramString) {
    Vector vector = new Vector();
    for (byte b = 0; b < this.headers.size(); b++) {
      MimeHeader mimeHeader = (MimeHeader)this.headers.elementAt(b);
      if (mimeHeader.getName().equalsIgnoreCase(paramString) && mimeHeader.getValue() != null)
        vector.addElement(mimeHeader.getValue()); 
    } 
    if (vector.size() == 0)
      return null; 
    String[] arrayOfString = new String[vector.size()];
    vector.copyInto(arrayOfString);
    return arrayOfString;
  }
  
  public void setHeader(String paramString1, String paramString2) {
    boolean bool = false;
    if (paramString1 == null || paramString1.equals(""))
      throw new IllegalArgumentException("Illegal MimeHeader name"); 
    for (byte b = 0; b < this.headers.size(); b++) {
      MimeHeader mimeHeader = (MimeHeader)this.headers.elementAt(b);
      if (mimeHeader.getName().equalsIgnoreCase(paramString1))
        if (!bool) {
          this.headers.setElementAt(new MimeHeader(mimeHeader.getName(), paramString2), b);
          bool = true;
        } else {
          this.headers.removeElementAt(b--);
        }  
    } 
    if (!bool)
      addHeader(paramString1, paramString2); 
  }
  
  public void addHeader(String paramString1, String paramString2) {
    if (paramString1 == null || paramString1.equals(""))
      throw new IllegalArgumentException("Illegal MimeHeader name"); 
    int i = this.headers.size();
    for (int j = i - 1; j >= 0; j--) {
      MimeHeader mimeHeader = (MimeHeader)this.headers.elementAt(j);
      if (mimeHeader.getName().equalsIgnoreCase(paramString1)) {
        this.headers.insertElementAt(new MimeHeader(paramString1, paramString2), j + 1);
        return;
      } 
    } 
    this.headers.addElement(new MimeHeader(paramString1, paramString2));
  }
  
  public void removeHeader(String paramString) {
    for (byte b = 0; b < this.headers.size(); b++) {
      MimeHeader mimeHeader = (MimeHeader)this.headers.elementAt(b);
      if (mimeHeader.getName().equalsIgnoreCase(paramString))
        this.headers.removeElementAt(b--); 
    } 
  }
  
  public void removeAllHeaders() { this.headers.removeAllElements(); }
  
  public Iterator getAllHeaders() { return this.headers.iterator(); }
  
  public Iterator getMatchingHeaders(String[] paramArrayOfString) { return new MatchingIterator(paramArrayOfString, true); }
  
  public Iterator getNonMatchingHeaders(String[] paramArrayOfString) { return new MatchingIterator(paramArrayOfString, false); }
  
  class MatchingIterator implements Iterator {
    private boolean match;
    
    private Iterator iterator;
    
    private String[] names;
    
    private Object nextHeader;
    
    MatchingIterator(String[] param1ArrayOfString, boolean param1Boolean) {
      this.match = param1Boolean;
      this.names = param1ArrayOfString;
      this.iterator = this$0.headers.iterator();
    }
    
    private Object nextMatch() {
      label21: while (this.iterator.hasNext()) {
        MimeHeader mimeHeader = (MimeHeader)this.iterator.next();
        if (this.names == null)
          return this.match ? null : mimeHeader; 
        for (byte b = 0; b < this.names.length; b++) {
          if (mimeHeader.getName().equalsIgnoreCase(this.names[b])) {
            if (this.match)
              return mimeHeader; 
            continue label21;
          } 
        } 
        if (!this.match)
          return mimeHeader; 
      } 
      return null;
    }
    
    public boolean hasNext() {
      if (this.nextHeader == null)
        this.nextHeader = nextMatch(); 
      return (this.nextHeader != null);
    }
    
    public Object next() {
      if (this.nextHeader != null) {
        Object object = this.nextHeader;
        this.nextHeader = null;
        return object;
      } 
      return hasNext() ? this.nextHeader : null;
    }
    
    public void remove() { this.iterator.remove(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\soap\MimeHeaders.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */