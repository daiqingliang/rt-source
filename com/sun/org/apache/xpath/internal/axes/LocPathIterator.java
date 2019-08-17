package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMFilter;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xml.internal.dtm.DTMManager;
import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import com.sun.org.apache.xpath.internal.compiler.Compiler;
import com.sun.org.apache.xpath.internal.objects.XNodeSet;
import com.sun.org.apache.xpath.internal.objects.XObject;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import javax.xml.transform.TransformerException;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public abstract class LocPathIterator extends PredicatedNodeTest implements Cloneable, DTMIterator, Serializable, PathComponent {
  static final long serialVersionUID = -4602476357268405754L;
  
  protected boolean m_allowDetach = true;
  
  protected IteratorPool m_clones = new IteratorPool(this);
  
  protected DTM m_cdtm;
  
  int m_stackFrame = -1;
  
  private boolean m_isTopLevel = false;
  
  public int m_lastFetched = -1;
  
  protected int m_context = -1;
  
  protected int m_currentContextNode = -1;
  
  protected int m_pos = 0;
  
  protected int m_length = -1;
  
  private PrefixResolver m_prefixResolver;
  
  protected XPathContext m_execContext;
  
  protected LocPathIterator() {}
  
  protected LocPathIterator(PrefixResolver paramPrefixResolver) {
    setLocPathIterator(this);
    this.m_prefixResolver = paramPrefixResolver;
  }
  
  protected LocPathIterator(Compiler paramCompiler, int paramInt1, int paramInt2) throws TransformerException { this(paramCompiler, paramInt1, paramInt2, true); }
  
  protected LocPathIterator(Compiler paramCompiler, int paramInt1, int paramInt2, boolean paramBoolean) throws TransformerException { setLocPathIterator(this); }
  
  public int getAnalysisBits() {
    int i = getAxis();
    return WalkerFactory.getAnalysisBitFromAxes(i);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, TransformerException {
    try {
      paramObjectInputStream.defaultReadObject();
      this.m_clones = new IteratorPool(this);
    } catch (ClassNotFoundException classNotFoundException) {
      throw new TransformerException(classNotFoundException);
    } 
  }
  
  public void setEnvironment(Object paramObject) {}
  
  public DTM getDTM(int paramInt) { return this.m_execContext.getDTM(paramInt); }
  
  public DTMManager getDTMManager() { return this.m_execContext.getDTMManager(); }
  
  public XObject execute(XPathContext paramXPathContext) throws TransformerException {
    XNodeSet xNodeSet = new XNodeSet((LocPathIterator)this.m_clones.getInstance());
    xNodeSet.setRoot(paramXPathContext.getCurrentNode(), paramXPathContext);
    return xNodeSet;
  }
  
  public void executeCharsToContentHandler(XPathContext paramXPathContext, ContentHandler paramContentHandler) throws TransformerException, SAXException {
    LocPathIterator locPathIterator = (LocPathIterator)this.m_clones.getInstance();
    int i = paramXPathContext.getCurrentNode();
    locPathIterator.setRoot(i, paramXPathContext);
    int j = locPathIterator.nextNode();
    DTM dTM = locPathIterator.getDTM(j);
    locPathIterator.detach();
    if (j != -1)
      dTM.dispatchCharactersEvents(j, paramContentHandler, false); 
  }
  
  public DTMIterator asIterator(XPathContext paramXPathContext, int paramInt) throws TransformerException {
    XNodeSet xNodeSet = new XNodeSet((LocPathIterator)this.m_clones.getInstance());
    xNodeSet.setRoot(paramInt, paramXPathContext);
    return xNodeSet;
  }
  
  public boolean isNodesetExpr() { return true; }
  
  public int asNode(XPathContext paramXPathContext) throws TransformerException {
    DTMIterator dTMIterator = this.m_clones.getInstance();
    int i = paramXPathContext.getCurrentNode();
    dTMIterator.setRoot(i, paramXPathContext);
    int j = dTMIterator.nextNode();
    dTMIterator.detach();
    return j;
  }
  
  public boolean bool(XPathContext paramXPathContext) throws TransformerException { return (asNode(paramXPathContext) != -1); }
  
  public void setIsTopLevel(boolean paramBoolean) { this.m_isTopLevel = paramBoolean; }
  
  public boolean getIsTopLevel() { return this.m_isTopLevel; }
  
  public void setRoot(int paramInt, Object paramObject) {
    this.m_context = paramInt;
    XPathContext xPathContext = (XPathContext)paramObject;
    this.m_execContext = xPathContext;
    this.m_cdtm = xPathContext.getDTM(paramInt);
    this.m_currentContextNode = paramInt;
    if (null == this.m_prefixResolver)
      this.m_prefixResolver = xPathContext.getNamespaceContext(); 
    this.m_lastFetched = -1;
    this.m_foundLast = false;
    this.m_pos = 0;
    this.m_length = -1;
    if (this.m_isTopLevel)
      this.m_stackFrame = xPathContext.getVarStack().getStackFrame(); 
  }
  
  protected void setNextPosition(int paramInt) { assertion(false, "setNextPosition not supported in this iterator!"); }
  
  public final int getCurrentPos() { return this.m_pos; }
  
  public void setShouldCacheNodes(boolean paramBoolean) { assertion(false, "setShouldCacheNodes not supported by this iterater!"); }
  
  public boolean isMutable() { return false; }
  
  public void setCurrentPos(int paramInt) { assertion(false, "setCurrentPos not supported by this iterator!"); }
  
  public void incrementCurrentPos() { this.m_pos++; }
  
  public int size() {
    assertion(false, "size() not supported by this iterator!");
    return 0;
  }
  
  public int item(int paramInt) {
    assertion(false, "item(int index) not supported by this iterator!");
    return 0;
  }
  
  public void setItem(int paramInt1, int paramInt2) { assertion(false, "setItem not supported by this iterator!"); }
  
  public int getLength() {
    LocPathIterator locPathIterator;
    boolean bool = (this == this.m_execContext.getSubContextList()) ? 1 : 0;
    int i = getPredicateCount();
    if (-1 != this.m_length && bool && this.m_predicateIndex < 1)
      return this.m_length; 
    if (this.m_foundLast)
      return this.m_pos; 
    int j = (this.m_predicateIndex >= 0) ? getProximityPosition() : this.m_pos;
    try {
      locPathIterator = (LocPathIterator)clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      return -1;
    } 
    if (i > 0 && bool)
      locPathIterator.m_predCount = this.m_predicateIndex; 
    int k;
    while (-1 != (k = locPathIterator.nextNode()))
      j++; 
    if (bool && this.m_predicateIndex < 1)
      this.m_length = j; 
    return j;
  }
  
  public boolean isFresh() { return (this.m_pos == 0); }
  
  public int previousNode() { throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESETDTM_CANNOT_ITERATE", null)); }
  
  public int getWhatToShow() { return -17; }
  
  public DTMFilter getFilter() { return null; }
  
  public int getRoot() { return this.m_context; }
  
  public boolean getExpandEntityReferences() { return true; }
  
  public void allowDetachToRelease(boolean paramBoolean) { this.m_allowDetach = paramBoolean; }
  
  public void detach() {
    if (this.m_allowDetach) {
      this.m_execContext = null;
      this.m_cdtm = null;
      this.m_length = -1;
      this.m_pos = 0;
      this.m_lastFetched = -1;
      this.m_context = -1;
      this.m_currentContextNode = -1;
      this.m_clones.freeInstance(this);
    } 
  }
  
  public void reset() { assertion(false, "This iterator can not reset!"); }
  
  public DTMIterator cloneWithReset() throws CloneNotSupportedException {
    LocPathIterator locPathIterator = (LocPathIterator)this.m_clones.getInstanceOrThrow();
    locPathIterator.m_execContext = this.m_execContext;
    locPathIterator.m_cdtm = this.m_cdtm;
    locPathIterator.m_context = this.m_context;
    locPathIterator.m_currentContextNode = this.m_currentContextNode;
    locPathIterator.m_stackFrame = this.m_stackFrame;
    return locPathIterator;
  }
  
  public abstract int nextNode();
  
  protected int returnNextNode(int paramInt) {
    if (-1 != paramInt)
      this.m_pos++; 
    this.m_lastFetched = paramInt;
    if (-1 == paramInt)
      this.m_foundLast = true; 
    return paramInt;
  }
  
  public int getCurrentNode() { return this.m_lastFetched; }
  
  public void runTo(int paramInt) {
    if (this.m_foundLast || (paramInt >= 0 && paramInt <= getCurrentPos()))
      return; 
    if (-1 == paramInt) {
      int i;
      while (-1 != (i = nextNode()));
    } else {
      int i;
      do {
      
      } while (-1 != (i = nextNode()) && getCurrentPos() < paramInt);
    } 
  }
  
  public final boolean getFoundLast() { return this.m_foundLast; }
  
  public final XPathContext getXPathContext() { return this.m_execContext; }
  
  public final int getContext() { return this.m_context; }
  
  public final int getCurrentContextNode() { return this.m_currentContextNode; }
  
  public final void setCurrentContextNode(int paramInt) { this.m_currentContextNode = paramInt; }
  
  public final PrefixResolver getPrefixResolver() {
    if (null == this.m_prefixResolver)
      this.m_prefixResolver = (PrefixResolver)getExpressionOwner(); 
    return this.m_prefixResolver;
  }
  
  public void callVisitors(ExpressionOwner paramExpressionOwner, XPathVisitor paramXPathVisitor) {
    if (paramXPathVisitor.visitLocationPath(paramExpressionOwner, this)) {
      paramXPathVisitor.visitStep(paramExpressionOwner, this);
      callPredicateVisitors(paramXPathVisitor);
    } 
  }
  
  public boolean isDocOrdered() { return true; }
  
  public int getAxis() { return -1; }
  
  public int getLastPos(XPathContext paramXPathContext) throws TransformerException { return getLength(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\axes\LocPathIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */