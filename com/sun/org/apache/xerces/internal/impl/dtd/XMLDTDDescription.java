package com.sun.org.apache.xerces.internal.impl.dtd;

import com.sun.org.apache.xerces.internal.util.XMLResourceIdentifierImpl;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLDTDDescription;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import java.util.ArrayList;
import java.util.Vector;

public class XMLDTDDescription extends XMLResourceIdentifierImpl implements XMLDTDDescription {
  protected String fRootName = null;
  
  protected ArrayList fPossibleRoots = null;
  
  public XMLDTDDescription(XMLResourceIdentifier paramXMLResourceIdentifier, String paramString) {
    setValues(paramXMLResourceIdentifier.getPublicId(), paramXMLResourceIdentifier.getLiteralSystemId(), paramXMLResourceIdentifier.getBaseSystemId(), paramXMLResourceIdentifier.getExpandedSystemId());
    this.fRootName = paramString;
    this.fPossibleRoots = null;
  }
  
  public XMLDTDDescription(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5) {
    setValues(paramString1, paramString2, paramString3, paramString4);
    this.fRootName = paramString5;
    this.fPossibleRoots = null;
  }
  
  public XMLDTDDescription(XMLInputSource paramXMLInputSource) {
    setValues(paramXMLInputSource.getPublicId(), null, paramXMLInputSource.getBaseSystemId(), paramXMLInputSource.getSystemId());
    this.fRootName = null;
    this.fPossibleRoots = null;
  }
  
  public String getGrammarType() { return "http://www.w3.org/TR/REC-xml"; }
  
  public String getRootName() { return this.fRootName; }
  
  public void setRootName(String paramString) {
    this.fRootName = paramString;
    this.fPossibleRoots = null;
  }
  
  public void setPossibleRoots(ArrayList paramArrayList) { this.fPossibleRoots = paramArrayList; }
  
  public void setPossibleRoots(Vector paramVector) { this.fPossibleRoots = (paramVector != null) ? new ArrayList(paramVector) : null; }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof XMLGrammarDescription))
      return false; 
    if (!getGrammarType().equals(((XMLGrammarDescription)paramObject).getGrammarType()))
      return false; 
    XMLDTDDescription xMLDTDDescription = (XMLDTDDescription)paramObject;
    if (this.fRootName != null) {
      if (xMLDTDDescription.fRootName != null && !xMLDTDDescription.fRootName.equals(this.fRootName))
        return false; 
      if (xMLDTDDescription.fPossibleRoots != null && !xMLDTDDescription.fPossibleRoots.contains(this.fRootName))
        return false; 
    } else if (this.fPossibleRoots != null) {
      if (xMLDTDDescription.fRootName != null) {
        if (!this.fPossibleRoots.contains(xMLDTDDescription.fRootName))
          return false; 
      } else {
        if (xMLDTDDescription.fPossibleRoots == null)
          return false; 
        boolean bool = false;
        int i = this.fPossibleRoots.size();
        for (byte b = 0; b < i; b++) {
          String str = (String)this.fPossibleRoots.get(b);
          bool = xMLDTDDescription.fPossibleRoots.contains(str);
          if (bool)
            break; 
        } 
        if (!bool)
          return false; 
      } 
    } 
    if (this.fExpandedSystemId != null) {
      if (!this.fExpandedSystemId.equals(xMLDTDDescription.fExpandedSystemId))
        return false; 
    } else if (xMLDTDDescription.fExpandedSystemId != null) {
      return false;
    } 
    if (this.fPublicId != null) {
      if (!this.fPublicId.equals(xMLDTDDescription.fPublicId))
        return false; 
    } else if (xMLDTDDescription.fPublicId != null) {
      return false;
    } 
    return true;
  }
  
  public int hashCode() { return (this.fExpandedSystemId != null) ? this.fExpandedSystemId.hashCode() : ((this.fPublicId != null) ? this.fPublicId.hashCode() : 0); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\dtd\XMLDTDDescription.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */