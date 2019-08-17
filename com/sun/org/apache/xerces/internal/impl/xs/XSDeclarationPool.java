package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.impl.dv.xs.SchemaDVFactoryImpl;
import com.sun.org.apache.xerces.internal.impl.dv.xs.XSSimpleTypeDecl;

public final class XSDeclarationPool {
  private static final int CHUNK_SHIFT = 8;
  
  private static final int CHUNK_SIZE = 256;
  
  private static final int CHUNK_MASK = 255;
  
  private static final int INITIAL_CHUNK_COUNT = 4;
  
  private XSElementDecl[][] fElementDecl = new XSElementDecl[4][];
  
  private int fElementDeclIndex = 0;
  
  private XSParticleDecl[][] fParticleDecl = new XSParticleDecl[4][];
  
  private int fParticleDeclIndex = 0;
  
  private XSModelGroupImpl[][] fModelGroup = new XSModelGroupImpl[4][];
  
  private int fModelGroupIndex = 0;
  
  private XSAttributeDecl[][] fAttrDecl = new XSAttributeDecl[4][];
  
  private int fAttrDeclIndex = 0;
  
  private XSComplexTypeDecl[][] fCTDecl = new XSComplexTypeDecl[4][];
  
  private int fCTDeclIndex = 0;
  
  private XSSimpleTypeDecl[][] fSTDecl = new XSSimpleTypeDecl[4][];
  
  private int fSTDeclIndex = 0;
  
  private XSAttributeUseImpl[][] fAttributeUse = new XSAttributeUseImpl[4][];
  
  private int fAttributeUseIndex = 0;
  
  private SchemaDVFactoryImpl dvFactory;
  
  public void setDVFactory(SchemaDVFactoryImpl paramSchemaDVFactoryImpl) { this.dvFactory = paramSchemaDVFactoryImpl; }
  
  public final XSElementDecl getElementDecl() {
    int i = this.fElementDeclIndex >> 8;
    int j = this.fElementDeclIndex & 0xFF;
    ensureElementDeclCapacity(i);
    if (this.fElementDecl[i][j] == null) {
      this.fElementDecl[i][j] = new XSElementDecl();
    } else {
      this.fElementDecl[i][j].reset();
    } 
    this.fElementDeclIndex++;
    return this.fElementDecl[i][j];
  }
  
  public final XSAttributeDecl getAttributeDecl() {
    int i = this.fAttrDeclIndex >> 8;
    int j = this.fAttrDeclIndex & 0xFF;
    ensureAttrDeclCapacity(i);
    if (this.fAttrDecl[i][j] == null) {
      this.fAttrDecl[i][j] = new XSAttributeDecl();
    } else {
      this.fAttrDecl[i][j].reset();
    } 
    this.fAttrDeclIndex++;
    return this.fAttrDecl[i][j];
  }
  
  public final XSAttributeUseImpl getAttributeUse() {
    int i = this.fAttributeUseIndex >> 8;
    int j = this.fAttributeUseIndex & 0xFF;
    ensureAttributeUseCapacity(i);
    if (this.fAttributeUse[i][j] == null) {
      this.fAttributeUse[i][j] = new XSAttributeUseImpl();
    } else {
      this.fAttributeUse[i][j].reset();
    } 
    this.fAttributeUseIndex++;
    return this.fAttributeUse[i][j];
  }
  
  public final XSComplexTypeDecl getComplexTypeDecl() {
    int i = this.fCTDeclIndex >> 8;
    int j = this.fCTDeclIndex & 0xFF;
    ensureCTDeclCapacity(i);
    if (this.fCTDecl[i][j] == null) {
      this.fCTDecl[i][j] = new XSComplexTypeDecl();
    } else {
      this.fCTDecl[i][j].reset();
    } 
    this.fCTDeclIndex++;
    return this.fCTDecl[i][j];
  }
  
  public final XSSimpleTypeDecl getSimpleTypeDecl() {
    int i = this.fSTDeclIndex >> 8;
    int j = this.fSTDeclIndex & 0xFF;
    ensureSTDeclCapacity(i);
    if (this.fSTDecl[i][j] == null) {
      this.fSTDecl[i][j] = this.dvFactory.newXSSimpleTypeDecl();
    } else {
      this.fSTDecl[i][j].reset();
    } 
    this.fSTDeclIndex++;
    return this.fSTDecl[i][j];
  }
  
  public final XSParticleDecl getParticleDecl() {
    int i = this.fParticleDeclIndex >> 8;
    int j = this.fParticleDeclIndex & 0xFF;
    ensureParticleDeclCapacity(i);
    if (this.fParticleDecl[i][j] == null) {
      this.fParticleDecl[i][j] = new XSParticleDecl();
    } else {
      this.fParticleDecl[i][j].reset();
    } 
    this.fParticleDeclIndex++;
    return this.fParticleDecl[i][j];
  }
  
  public final XSModelGroupImpl getModelGroup() {
    int i = this.fModelGroupIndex >> 8;
    int j = this.fModelGroupIndex & 0xFF;
    ensureModelGroupCapacity(i);
    if (this.fModelGroup[i][j] == null) {
      this.fModelGroup[i][j] = new XSModelGroupImpl();
    } else {
      this.fModelGroup[i][j].reset();
    } 
    this.fModelGroupIndex++;
    return this.fModelGroup[i][j];
  }
  
  private boolean ensureElementDeclCapacity(int paramInt) {
    if (paramInt >= this.fElementDecl.length) {
      this.fElementDecl = resize(this.fElementDecl, this.fElementDecl.length * 2);
    } else if (this.fElementDecl[paramInt] != null) {
      return false;
    } 
    this.fElementDecl[paramInt] = new XSElementDecl[256];
    return true;
  }
  
  private static XSElementDecl[][] resize(XSElementDecl[][] paramArrayOfXSElementDecl, int paramInt) {
    XSElementDecl[][] arrayOfXSElementDecl = new XSElementDecl[paramInt][];
    System.arraycopy(paramArrayOfXSElementDecl, 0, arrayOfXSElementDecl, 0, paramArrayOfXSElementDecl.length);
    return arrayOfXSElementDecl;
  }
  
  private boolean ensureParticleDeclCapacity(int paramInt) {
    if (paramInt >= this.fParticleDecl.length) {
      this.fParticleDecl = resize(this.fParticleDecl, this.fParticleDecl.length * 2);
    } else if (this.fParticleDecl[paramInt] != null) {
      return false;
    } 
    this.fParticleDecl[paramInt] = new XSParticleDecl[256];
    return true;
  }
  
  private boolean ensureModelGroupCapacity(int paramInt) {
    if (paramInt >= this.fModelGroup.length) {
      this.fModelGroup = resize(this.fModelGroup, this.fModelGroup.length * 2);
    } else if (this.fModelGroup[paramInt] != null) {
      return false;
    } 
    this.fModelGroup[paramInt] = new XSModelGroupImpl[256];
    return true;
  }
  
  private static XSParticleDecl[][] resize(XSParticleDecl[][] paramArrayOfXSParticleDecl, int paramInt) {
    XSParticleDecl[][] arrayOfXSParticleDecl = new XSParticleDecl[paramInt][];
    System.arraycopy(paramArrayOfXSParticleDecl, 0, arrayOfXSParticleDecl, 0, paramArrayOfXSParticleDecl.length);
    return arrayOfXSParticleDecl;
  }
  
  private static XSModelGroupImpl[][] resize(XSModelGroupImpl[][] paramArrayOfXSModelGroupImpl, int paramInt) {
    XSModelGroupImpl[][] arrayOfXSModelGroupImpl = new XSModelGroupImpl[paramInt][];
    System.arraycopy(paramArrayOfXSModelGroupImpl, 0, arrayOfXSModelGroupImpl, 0, paramArrayOfXSModelGroupImpl.length);
    return arrayOfXSModelGroupImpl;
  }
  
  private boolean ensureAttrDeclCapacity(int paramInt) {
    if (paramInt >= this.fAttrDecl.length) {
      this.fAttrDecl = resize(this.fAttrDecl, this.fAttrDecl.length * 2);
    } else if (this.fAttrDecl[paramInt] != null) {
      return false;
    } 
    this.fAttrDecl[paramInt] = new XSAttributeDecl[256];
    return true;
  }
  
  private static XSAttributeDecl[][] resize(XSAttributeDecl[][] paramArrayOfXSAttributeDecl, int paramInt) {
    XSAttributeDecl[][] arrayOfXSAttributeDecl = new XSAttributeDecl[paramInt][];
    System.arraycopy(paramArrayOfXSAttributeDecl, 0, arrayOfXSAttributeDecl, 0, paramArrayOfXSAttributeDecl.length);
    return arrayOfXSAttributeDecl;
  }
  
  private boolean ensureAttributeUseCapacity(int paramInt) {
    if (paramInt >= this.fAttributeUse.length) {
      this.fAttributeUse = resize(this.fAttributeUse, this.fAttributeUse.length * 2);
    } else if (this.fAttributeUse[paramInt] != null) {
      return false;
    } 
    this.fAttributeUse[paramInt] = new XSAttributeUseImpl[256];
    return true;
  }
  
  private static XSAttributeUseImpl[][] resize(XSAttributeUseImpl[][] paramArrayOfXSAttributeUseImpl, int paramInt) {
    XSAttributeUseImpl[][] arrayOfXSAttributeUseImpl = new XSAttributeUseImpl[paramInt][];
    System.arraycopy(paramArrayOfXSAttributeUseImpl, 0, arrayOfXSAttributeUseImpl, 0, paramArrayOfXSAttributeUseImpl.length);
    return arrayOfXSAttributeUseImpl;
  }
  
  private boolean ensureSTDeclCapacity(int paramInt) {
    if (paramInt >= this.fSTDecl.length) {
      this.fSTDecl = resize(this.fSTDecl, this.fSTDecl.length * 2);
    } else if (this.fSTDecl[paramInt] != null) {
      return false;
    } 
    this.fSTDecl[paramInt] = new XSSimpleTypeDecl[256];
    return true;
  }
  
  private static XSSimpleTypeDecl[][] resize(XSSimpleTypeDecl[][] paramArrayOfXSSimpleTypeDecl, int paramInt) {
    XSSimpleTypeDecl[][] arrayOfXSSimpleTypeDecl = new XSSimpleTypeDecl[paramInt][];
    System.arraycopy(paramArrayOfXSSimpleTypeDecl, 0, arrayOfXSSimpleTypeDecl, 0, paramArrayOfXSSimpleTypeDecl.length);
    return arrayOfXSSimpleTypeDecl;
  }
  
  private boolean ensureCTDeclCapacity(int paramInt) {
    if (paramInt >= this.fCTDecl.length) {
      this.fCTDecl = resize(this.fCTDecl, this.fCTDecl.length * 2);
    } else if (this.fCTDecl[paramInt] != null) {
      return false;
    } 
    this.fCTDecl[paramInt] = new XSComplexTypeDecl[256];
    return true;
  }
  
  private static XSComplexTypeDecl[][] resize(XSComplexTypeDecl[][] paramArrayOfXSComplexTypeDecl, int paramInt) {
    XSComplexTypeDecl[][] arrayOfXSComplexTypeDecl = new XSComplexTypeDecl[paramInt][];
    System.arraycopy(paramArrayOfXSComplexTypeDecl, 0, arrayOfXSComplexTypeDecl, 0, paramArrayOfXSComplexTypeDecl.length);
    return arrayOfXSComplexTypeDecl;
  }
  
  public void reset() {
    this.fElementDeclIndex = 0;
    this.fParticleDeclIndex = 0;
    this.fModelGroupIndex = 0;
    this.fSTDeclIndex = 0;
    this.fCTDeclIndex = 0;
    this.fAttrDeclIndex = 0;
    this.fAttributeUseIndex = 0;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\XSDeclarationPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */