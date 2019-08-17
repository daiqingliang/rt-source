package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import com.sun.org.apache.xerces.internal.xs.XSNamespaceItem;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import com.sun.org.apache.xerces.internal.xs.XSParticle;
import com.sun.org.apache.xerces.internal.xs.XSTerm;

public class XSParticleDecl implements XSParticle {
  public static final short PARTICLE_EMPTY = 0;
  
  public static final short PARTICLE_ELEMENT = 1;
  
  public static final short PARTICLE_WILDCARD = 2;
  
  public static final short PARTICLE_MODELGROUP = 3;
  
  public static final short PARTICLE_ZERO_OR_MORE = 4;
  
  public static final short PARTICLE_ZERO_OR_ONE = 5;
  
  public static final short PARTICLE_ONE_OR_MORE = 6;
  
  public short fType = 0;
  
  public XSTerm fValue = null;
  
  public int fMinOccurs = 1;
  
  public int fMaxOccurs = 1;
  
  public XSObjectList fAnnotations = null;
  
  private String fDescription = null;
  
  public XSParticleDecl makeClone() {
    XSParticleDecl xSParticleDecl = new XSParticleDecl();
    xSParticleDecl.fType = this.fType;
    xSParticleDecl.fMinOccurs = this.fMinOccurs;
    xSParticleDecl.fMaxOccurs = this.fMaxOccurs;
    xSParticleDecl.fDescription = this.fDescription;
    xSParticleDecl.fValue = this.fValue;
    xSParticleDecl.fAnnotations = this.fAnnotations;
    return xSParticleDecl;
  }
  
  public boolean emptiable() { return (minEffectiveTotalRange() == 0); }
  
  public boolean isEmpty() { return (this.fType == 0) ? true : ((this.fType == 1 || this.fType == 2) ? false : ((XSModelGroupImpl)this.fValue).isEmpty()); }
  
  public int minEffectiveTotalRange() { return (this.fType == 0) ? 0 : ((this.fType == 3) ? (((XSModelGroupImpl)this.fValue).minEffectiveTotalRange() * this.fMinOccurs) : this.fMinOccurs); }
  
  public int maxEffectiveTotalRange() {
    if (this.fType == 0)
      return 0; 
    if (this.fType == 3) {
      int i = ((XSModelGroupImpl)this.fValue).maxEffectiveTotalRange();
      return (i == -1) ? -1 : ((i != 0 && this.fMaxOccurs == -1) ? -1 : (i * this.fMaxOccurs));
    } 
    return this.fMaxOccurs;
  }
  
  public String toString() {
    if (this.fDescription == null) {
      StringBuffer stringBuffer = new StringBuffer();
      appendParticle(stringBuffer);
      if ((this.fMinOccurs != 0 || this.fMaxOccurs != 0) && (this.fMinOccurs != 1 || this.fMaxOccurs != 1)) {
        stringBuffer.append('{').append(this.fMinOccurs);
        if (this.fMaxOccurs == -1) {
          stringBuffer.append("-UNBOUNDED");
        } else if (this.fMinOccurs != this.fMaxOccurs) {
          stringBuffer.append('-').append(this.fMaxOccurs);
        } 
        stringBuffer.append('}');
      } 
      this.fDescription = stringBuffer.toString();
    } 
    return this.fDescription;
  }
  
  void appendParticle(StringBuffer paramStringBuffer) {
    switch (this.fType) {
      case 0:
        paramStringBuffer.append("EMPTY");
        break;
      case 1:
        paramStringBuffer.append(this.fValue.toString());
        break;
      case 2:
        paramStringBuffer.append('(');
        paramStringBuffer.append(this.fValue.toString());
        paramStringBuffer.append(')');
        break;
      case 3:
        paramStringBuffer.append(this.fValue.toString());
        break;
    } 
  }
  
  public void reset() {
    this.fType = 0;
    this.fValue = null;
    this.fMinOccurs = 1;
    this.fMaxOccurs = 1;
    this.fDescription = null;
    this.fAnnotations = null;
  }
  
  public short getType() { return 8; }
  
  public String getName() { return null; }
  
  public String getNamespace() { return null; }
  
  public int getMinOccurs() { return this.fMinOccurs; }
  
  public boolean getMaxOccursUnbounded() { return (this.fMaxOccurs == -1); }
  
  public int getMaxOccurs() { return this.fMaxOccurs; }
  
  public XSTerm getTerm() { return this.fValue; }
  
  public XSNamespaceItem getNamespaceItem() { return null; }
  
  public XSObjectList getAnnotations() { return (this.fAnnotations != null) ? this.fAnnotations : XSObjectListImpl.EMPTY_LIST; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\XSParticleDecl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */