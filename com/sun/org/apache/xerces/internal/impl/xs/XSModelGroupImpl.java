package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import com.sun.org.apache.xerces.internal.xs.XSAnnotation;
import com.sun.org.apache.xerces.internal.xs.XSModelGroup;
import com.sun.org.apache.xerces.internal.xs.XSNamespaceItem;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;

public class XSModelGroupImpl implements XSModelGroup {
  public static final short MODELGROUP_CHOICE = 101;
  
  public static final short MODELGROUP_SEQUENCE = 102;
  
  public static final short MODELGROUP_ALL = 103;
  
  public short fCompositor;
  
  public XSParticleDecl[] fParticles = null;
  
  public int fParticleCount = 0;
  
  public XSObjectList fAnnotations = null;
  
  private String fDescription = null;
  
  public boolean isEmpty() {
    for (byte b = 0; b < this.fParticleCount; b++) {
      if (!this.fParticles[b].isEmpty())
        return false; 
    } 
    return true;
  }
  
  public int minEffectiveTotalRange() { return (this.fCompositor == 101) ? minEffectiveTotalRangeChoice() : minEffectiveTotalRangeAllSeq(); }
  
  private int minEffectiveTotalRangeAllSeq() {
    int i = 0;
    for (byte b = 0; b < this.fParticleCount; b++)
      i += this.fParticles[b].minEffectiveTotalRange(); 
    return i;
  }
  
  private int minEffectiveTotalRangeChoice() {
    int i = 0;
    if (this.fParticleCount > 0)
      i = this.fParticles[0].minEffectiveTotalRange(); 
    for (byte b = 1; b < this.fParticleCount; b++) {
      int j = this.fParticles[b].minEffectiveTotalRange();
      if (j < i)
        i = j; 
    } 
    return i;
  }
  
  public int maxEffectiveTotalRange() { return (this.fCompositor == 101) ? maxEffectiveTotalRangeChoice() : maxEffectiveTotalRangeAllSeq(); }
  
  private int maxEffectiveTotalRangeAllSeq() {
    int i = 0;
    for (byte b = 0; b < this.fParticleCount; b++) {
      int j = this.fParticles[b].maxEffectiveTotalRange();
      if (j == -1)
        return -1; 
      i += j;
    } 
    return i;
  }
  
  private int maxEffectiveTotalRangeChoice() {
    int i = 0;
    if (this.fParticleCount > 0) {
      i = this.fParticles[0].maxEffectiveTotalRange();
      if (i == -1)
        return -1; 
    } 
    for (byte b = 1; b < this.fParticleCount; b++) {
      int j = this.fParticles[b].maxEffectiveTotalRange();
      if (j == -1)
        return -1; 
      if (j > i)
        i = j; 
    } 
    return i;
  }
  
  public String toString() {
    if (this.fDescription == null) {
      StringBuffer stringBuffer = new StringBuffer();
      if (this.fCompositor == 103) {
        stringBuffer.append("all(");
      } else {
        stringBuffer.append('(');
      } 
      if (this.fParticleCount > 0)
        stringBuffer.append(this.fParticles[0].toString()); 
      for (byte b = 1; b < this.fParticleCount; b++) {
        if (this.fCompositor == 101) {
          stringBuffer.append('|');
        } else {
          stringBuffer.append(',');
        } 
        stringBuffer.append(this.fParticles[b].toString());
      } 
      stringBuffer.append(')');
      this.fDescription = stringBuffer.toString();
    } 
    return this.fDescription;
  }
  
  public void reset() {
    this.fCompositor = 102;
    this.fParticles = null;
    this.fParticleCount = 0;
    this.fDescription = null;
    this.fAnnotations = null;
  }
  
  public short getType() { return 7; }
  
  public String getName() { return null; }
  
  public String getNamespace() { return null; }
  
  public short getCompositor() { return (this.fCompositor == 101) ? 2 : ((this.fCompositor == 102) ? 1 : 3); }
  
  public XSObjectList getParticles() { return new XSObjectListImpl(this.fParticles, this.fParticleCount); }
  
  public XSAnnotation getAnnotation() { return (this.fAnnotations != null) ? (XSAnnotation)this.fAnnotations.item(0) : null; }
  
  public XSObjectList getAnnotations() { return (this.fAnnotations != null) ? this.fAnnotations : XSObjectListImpl.EMPTY_LIST; }
  
  public XSNamespaceItem getNamespaceItem() { return null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\XSModelGroupImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */