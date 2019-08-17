package com.sun.org.apache.xpath.internal;

import com.sun.org.apache.xalan.internal.extensions.ExpressionContext;
import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMFilter;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xml.internal.dtm.DTMManager;
import com.sun.org.apache.xml.internal.dtm.DTMWSFilter;
import com.sun.org.apache.xml.internal.dtm.ref.DTMNodeIterator;
import com.sun.org.apache.xml.internal.dtm.ref.sax2dtm.SAX2RTFDTM;
import com.sun.org.apache.xml.internal.utils.DefaultErrorHandler;
import com.sun.org.apache.xml.internal.utils.IntStack;
import com.sun.org.apache.xml.internal.utils.NodeVector;
import com.sun.org.apache.xml.internal.utils.ObjectStack;
import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import com.sun.org.apache.xml.internal.utils.QName;
import com.sun.org.apache.xml.internal.utils.XMLString;
import com.sun.org.apache.xpath.internal.axes.OneStepIteratorForward;
import com.sun.org.apache.xpath.internal.axes.SubContextList;
import com.sun.org.apache.xpath.internal.objects.DTMXRTreeFrag;
import com.sun.org.apache.xpath.internal.objects.XMLStringFactoryImpl;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.objects.XString;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;
import java.util.Vector;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.XMLReader;

public class XPathContext extends DTMManager {
  IntStack m_last_pushed_rtfdtm = new IntStack();
  
  private Vector m_rtfdtm_stack = null;
  
  private int m_which_rtfdtm = -1;
  
  private SAX2RTFDTM m_global_rtfdtm = null;
  
  private HashMap m_DTMXRTreeFrags = null;
  
  private boolean m_isSecureProcessing = false;
  
  private boolean m_overrideDefaultParser;
  
  protected DTMManager m_dtmManager = null;
  
  ObjectStack m_saxLocations = new ObjectStack(4096);
  
  private Object m_owner;
  
  private Method m_ownerGetErrorListener;
  
  private VariableStack m_variableStacks = new VariableStack();
  
  private SourceTreeManager m_sourceTreeManager = new SourceTreeManager();
  
  private ErrorListener m_errorListener;
  
  private ErrorListener m_defaultErrorListener;
  
  private URIResolver m_uriResolver;
  
  public XMLReader m_primaryReader;
  
  private Stack m_contextNodeLists = new Stack();
  
  public static final int RECURSIONLIMIT = 4096;
  
  private IntStack m_currentNodes = new IntStack(4096);
  
  private NodeVector m_iteratorRoots = new NodeVector();
  
  private NodeVector m_predicateRoots = new NodeVector();
  
  private IntStack m_currentExpressionNodes = new IntStack(4096);
  
  private IntStack m_predicatePos = new IntStack();
  
  private ObjectStack m_prefixResolvers = new ObjectStack(4096);
  
  private Stack m_axesIteratorStack = new Stack();
  
  XPathExpressionContext expressionContext = new XPathExpressionContext();
  
  public DTMManager getDTMManager() { return this.m_dtmManager; }
  
  public void setSecureProcessing(boolean paramBoolean) { this.m_isSecureProcessing = paramBoolean; }
  
  public boolean isSecureProcessing() { return this.m_isSecureProcessing; }
  
  public DTM getDTM(Source paramSource, boolean paramBoolean1, DTMWSFilter paramDTMWSFilter, boolean paramBoolean2, boolean paramBoolean3) { return this.m_dtmManager.getDTM(paramSource, paramBoolean1, paramDTMWSFilter, paramBoolean2, paramBoolean3); }
  
  public DTM getDTM(int paramInt) { return this.m_dtmManager.getDTM(paramInt); }
  
  public int getDTMHandleFromNode(Node paramNode) { return this.m_dtmManager.getDTMHandleFromNode(paramNode); }
  
  public int getDTMIdentity(DTM paramDTM) { return this.m_dtmManager.getDTMIdentity(paramDTM); }
  
  public DTM createDocumentFragment() { return this.m_dtmManager.createDocumentFragment(); }
  
  public boolean release(DTM paramDTM, boolean paramBoolean) { return (this.m_rtfdtm_stack != null && this.m_rtfdtm_stack.contains(paramDTM)) ? false : this.m_dtmManager.release(paramDTM, paramBoolean); }
  
  public DTMIterator createDTMIterator(Object paramObject, int paramInt) { return this.m_dtmManager.createDTMIterator(paramObject, paramInt); }
  
  public DTMIterator createDTMIterator(String paramString, PrefixResolver paramPrefixResolver) { return this.m_dtmManager.createDTMIterator(paramString, paramPrefixResolver); }
  
  public DTMIterator createDTMIterator(int paramInt, DTMFilter paramDTMFilter, boolean paramBoolean) { return this.m_dtmManager.createDTMIterator(paramInt, paramDTMFilter, paramBoolean); }
  
  public DTMIterator createDTMIterator(int paramInt) {
    OneStepIteratorForward oneStepIteratorForward = new OneStepIteratorForward(13);
    oneStepIteratorForward.setRoot(paramInt, this);
    return oneStepIteratorForward;
  }
  
  public XPathContext() { this(false); }
  
  public XPathContext(boolean paramBoolean) { init(paramBoolean); }
  
  public XPathContext(Object paramObject) {
    this.m_owner = paramObject;
    try {
      this.m_ownerGetErrorListener = this.m_owner.getClass().getMethod("getErrorListener", new Class[0]);
    } catch (NoSuchMethodException noSuchMethodException) {}
    init(false);
  }
  
  private void init(boolean paramBoolean) {
    this.m_prefixResolvers.push(null);
    this.m_currentNodes.push(-1);
    this.m_currentExpressionNodes.push(-1);
    this.m_saxLocations.push(null);
    this.m_overrideDefaultParser = paramBoolean;
    this.m_dtmManager = DTMManager.newInstance(XMLStringFactoryImpl.getFactory());
  }
  
  public void reset() {
    releaseDTMXRTreeFrags();
    if (this.m_rtfdtm_stack != null) {
      Enumeration enumeration = this.m_rtfdtm_stack.elements();
      while (enumeration.hasMoreElements())
        this.m_dtmManager.release((DTM)enumeration.nextElement(), true); 
    } 
    this.m_rtfdtm_stack = null;
    this.m_which_rtfdtm = -1;
    if (this.m_global_rtfdtm != null)
      this.m_dtmManager.release(this.m_global_rtfdtm, true); 
    this.m_global_rtfdtm = null;
    this.m_dtmManager = DTMManager.newInstance(XMLStringFactoryImpl.getFactory());
    this.m_saxLocations.removeAllElements();
    this.m_axesIteratorStack.removeAllElements();
    this.m_contextNodeLists.removeAllElements();
    this.m_currentExpressionNodes.removeAllElements();
    this.m_currentNodes.removeAllElements();
    this.m_iteratorRoots.RemoveAllNoClear();
    this.m_predicatePos.removeAllElements();
    this.m_predicateRoots.RemoveAllNoClear();
    this.m_prefixResolvers.removeAllElements();
    this.m_prefixResolvers.push(null);
    this.m_currentNodes.push(-1);
    this.m_currentExpressionNodes.push(-1);
    this.m_saxLocations.push(null);
  }
  
  public void setSAXLocator(SourceLocator paramSourceLocator) { this.m_saxLocations.setTop(paramSourceLocator); }
  
  public void pushSAXLocator(SourceLocator paramSourceLocator) { this.m_saxLocations.push(paramSourceLocator); }
  
  public void pushSAXLocatorNull() { this.m_saxLocations.push(null); }
  
  public void popSAXLocator() { this.m_saxLocations.pop(); }
  
  public SourceLocator getSAXLocator() { return (SourceLocator)this.m_saxLocations.peek(); }
  
  public Object getOwnerObject() { return this.m_owner; }
  
  public final VariableStack getVarStack() { return this.m_variableStacks; }
  
  public final void setVarStack(VariableStack paramVariableStack) { this.m_variableStacks = paramVariableStack; }
  
  public final SourceTreeManager getSourceTreeManager() { return this.m_sourceTreeManager; }
  
  public void setSourceTreeManager(SourceTreeManager paramSourceTreeManager) { this.m_sourceTreeManager = paramSourceTreeManager; }
  
  public final ErrorListener getErrorListener() {
    if (null != this.m_errorListener)
      return this.m_errorListener; 
    ErrorListener errorListener = null;
    try {
      if (null != this.m_ownerGetErrorListener)
        errorListener = (ErrorListener)this.m_ownerGetErrorListener.invoke(this.m_owner, new Object[0]); 
    } catch (Exception exception) {}
    if (null == errorListener) {
      if (null == this.m_defaultErrorListener)
        this.m_defaultErrorListener = new DefaultErrorHandler(); 
      errorListener = this.m_defaultErrorListener;
    } 
    return errorListener;
  }
  
  public void setErrorListener(ErrorListener paramErrorListener) throws IllegalArgumentException {
    if (paramErrorListener == null)
      throw new IllegalArgumentException(XSLMessages.createXPATHMessage("ER_NULL_ERROR_HANDLER", null)); 
    this.m_errorListener = paramErrorListener;
  }
  
  public final URIResolver getURIResolver() { return this.m_uriResolver; }
  
  public void setURIResolver(URIResolver paramURIResolver) { this.m_uriResolver = paramURIResolver; }
  
  public final XMLReader getPrimaryReader() { return this.m_primaryReader; }
  
  public void setPrimaryReader(XMLReader paramXMLReader) { this.m_primaryReader = paramXMLReader; }
  
  public Stack getContextNodeListsStack() { return this.m_contextNodeLists; }
  
  public void setContextNodeListsStack(Stack paramStack) { this.m_contextNodeLists = paramStack; }
  
  public final DTMIterator getContextNodeList() { return (this.m_contextNodeLists.size() > 0) ? (DTMIterator)this.m_contextNodeLists.peek() : null; }
  
  public final void pushContextNodeList(DTMIterator paramDTMIterator) { this.m_contextNodeLists.push(paramDTMIterator); }
  
  public final void popContextNodeList() {
    if (this.m_contextNodeLists.isEmpty()) {
      System.err.println("Warning: popContextNodeList when stack is empty!");
    } else {
      this.m_contextNodeLists.pop();
    } 
  }
  
  public IntStack getCurrentNodeStack() { return this.m_currentNodes; }
  
  public void setCurrentNodeStack(IntStack paramIntStack) { this.m_currentNodes = paramIntStack; }
  
  public final int getCurrentNode() { return this.m_currentNodes.peek(); }
  
  public final void pushCurrentNodeAndExpression(int paramInt1, int paramInt2) {
    this.m_currentNodes.push(paramInt1);
    this.m_currentExpressionNodes.push(paramInt1);
  }
  
  public final void popCurrentNodeAndExpression() {
    this.m_currentNodes.quickPop(1);
    this.m_currentExpressionNodes.quickPop(1);
  }
  
  public final void pushExpressionState(int paramInt1, int paramInt2, PrefixResolver paramPrefixResolver) {
    this.m_currentNodes.push(paramInt1);
    this.m_currentExpressionNodes.push(paramInt1);
    this.m_prefixResolvers.push(paramPrefixResolver);
  }
  
  public final void popExpressionState() {
    this.m_currentNodes.quickPop(1);
    this.m_currentExpressionNodes.quickPop(1);
    this.m_prefixResolvers.pop();
  }
  
  public final void pushCurrentNode(int paramInt) { this.m_currentNodes.push(paramInt); }
  
  public final void popCurrentNode() { this.m_currentNodes.quickPop(1); }
  
  public final void pushPredicateRoot(int paramInt) { this.m_predicateRoots.push(paramInt); }
  
  public final void popPredicateRoot() { this.m_predicateRoots.popQuick(); }
  
  public final int getPredicateRoot() { return this.m_predicateRoots.peepOrNull(); }
  
  public final void pushIteratorRoot(int paramInt) { this.m_iteratorRoots.push(paramInt); }
  
  public final void popIteratorRoot() { this.m_iteratorRoots.popQuick(); }
  
  public final int getIteratorRoot() { return this.m_iteratorRoots.peepOrNull(); }
  
  public IntStack getCurrentExpressionNodeStack() { return this.m_currentExpressionNodes; }
  
  public void setCurrentExpressionNodeStack(IntStack paramIntStack) { this.m_currentExpressionNodes = paramIntStack; }
  
  public final int getPredicatePos() { return this.m_predicatePos.peek(); }
  
  public final void pushPredicatePos(int paramInt) { this.m_predicatePos.push(paramInt); }
  
  public final void popPredicatePos() { this.m_predicatePos.pop(); }
  
  public final int getCurrentExpressionNode() { return this.m_currentExpressionNodes.peek(); }
  
  public final void pushCurrentExpressionNode(int paramInt) { this.m_currentExpressionNodes.push(paramInt); }
  
  public final void popCurrentExpressionNode() { this.m_currentExpressionNodes.quickPop(1); }
  
  public final PrefixResolver getNamespaceContext() { return (PrefixResolver)this.m_prefixResolvers.peek(); }
  
  public final void setNamespaceContext(PrefixResolver paramPrefixResolver) { this.m_prefixResolvers.setTop(paramPrefixResolver); }
  
  public final void pushNamespaceContext(PrefixResolver paramPrefixResolver) { this.m_prefixResolvers.push(paramPrefixResolver); }
  
  public final void pushNamespaceContextNull() { this.m_prefixResolvers.push(null); }
  
  public final void popNamespaceContext() { this.m_prefixResolvers.pop(); }
  
  public Stack getAxesIteratorStackStacks() { return this.m_axesIteratorStack; }
  
  public void setAxesIteratorStackStacks(Stack paramStack) { this.m_axesIteratorStack = paramStack; }
  
  public final void pushSubContextList(SubContextList paramSubContextList) { this.m_axesIteratorStack.push(paramSubContextList); }
  
  public final void popSubContextList() { this.m_axesIteratorStack.pop(); }
  
  public SubContextList getSubContextList() { return this.m_axesIteratorStack.isEmpty() ? null : (SubContextList)this.m_axesIteratorStack.peek(); }
  
  public SubContextList getCurrentNodeList() { return this.m_axesIteratorStack.isEmpty() ? null : (SubContextList)this.m_axesIteratorStack.elementAt(0); }
  
  public final int getContextNode() { return getCurrentNode(); }
  
  public final DTMIterator getContextNodes() {
    try {
      DTMIterator dTMIterator = getContextNodeList();
      return (null != dTMIterator) ? dTMIterator.cloneWithReset() : null;
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      return null;
    } 
  }
  
  public ExpressionContext getExpressionContext() { return this.expressionContext; }
  
  public DTM getGlobalRTFDTM() {
    if (this.m_global_rtfdtm == null || this.m_global_rtfdtm.isTreeIncomplete())
      this.m_global_rtfdtm = (SAX2RTFDTM)this.m_dtmManager.getDTM(null, true, null, false, false); 
    return this.m_global_rtfdtm;
  }
  
  public DTM getRTFDTM() {
    SAX2RTFDTM sAX2RTFDTM;
    if (this.m_rtfdtm_stack == null) {
      this.m_rtfdtm_stack = new Vector();
      sAX2RTFDTM = (SAX2RTFDTM)this.m_dtmManager.getDTM(null, true, null, false, false);
      this.m_rtfdtm_stack.addElement(sAX2RTFDTM);
      this.m_which_rtfdtm++;
    } else if (this.m_which_rtfdtm < 0) {
      sAX2RTFDTM = (SAX2RTFDTM)this.m_rtfdtm_stack.elementAt(++this.m_which_rtfdtm);
    } else {
      sAX2RTFDTM = (SAX2RTFDTM)this.m_rtfdtm_stack.elementAt(this.m_which_rtfdtm);
      if (sAX2RTFDTM.isTreeIncomplete())
        if (++this.m_which_rtfdtm < this.m_rtfdtm_stack.size()) {
          sAX2RTFDTM = (SAX2RTFDTM)this.m_rtfdtm_stack.elementAt(this.m_which_rtfdtm);
        } else {
          sAX2RTFDTM = (SAX2RTFDTM)this.m_dtmManager.getDTM(null, true, null, false, false);
          this.m_rtfdtm_stack.addElement(sAX2RTFDTM);
        }  
    } 
    return sAX2RTFDTM;
  }
  
  public void pushRTFContext() {
    this.m_last_pushed_rtfdtm.push(this.m_which_rtfdtm);
    if (null != this.m_rtfdtm_stack)
      ((SAX2RTFDTM)getRTFDTM()).pushRewindMark(); 
  }
  
  public void popRTFContext() {
    int i = this.m_last_pushed_rtfdtm.pop();
    if (null == this.m_rtfdtm_stack)
      return; 
    if (this.m_which_rtfdtm == i) {
      if (i >= 0)
        boolean bool = ((SAX2RTFDTM)this.m_rtfdtm_stack.elementAt(i)).popRewindMark(); 
    } else {
      while (this.m_which_rtfdtm != i) {
        boolean bool = ((SAX2RTFDTM)this.m_rtfdtm_stack.elementAt(this.m_which_rtfdtm)).popRewindMark();
        this.m_which_rtfdtm--;
      } 
    } 
  }
  
  public DTMXRTreeFrag getDTMXRTreeFrag(int paramInt) {
    if (this.m_DTMXRTreeFrags == null)
      this.m_DTMXRTreeFrags = new HashMap(); 
    if (this.m_DTMXRTreeFrags.containsKey(new Integer(paramInt)))
      return (DTMXRTreeFrag)this.m_DTMXRTreeFrags.get(new Integer(paramInt)); 
    DTMXRTreeFrag dTMXRTreeFrag = new DTMXRTreeFrag(paramInt, this);
    this.m_DTMXRTreeFrags.put(new Integer(paramInt), dTMXRTreeFrag);
    return dTMXRTreeFrag;
  }
  
  private final void releaseDTMXRTreeFrags() {
    if (this.m_DTMXRTreeFrags == null)
      return; 
    Iterator iterator = this.m_DTMXRTreeFrags.values().iterator();
    while (iterator.hasNext()) {
      DTMXRTreeFrag dTMXRTreeFrag = (DTMXRTreeFrag)iterator.next();
      dTMXRTreeFrag.destruct();
      iterator.remove();
    } 
    this.m_DTMXRTreeFrags = null;
  }
  
  public class XPathExpressionContext implements ExpressionContext {
    public XPathContext getXPathContext() { return XPathContext.this; }
    
    public DTMManager getDTMManager() { return XPathContext.this.m_dtmManager; }
    
    public Node getContextNode() {
      int i = XPathContext.this.getCurrentNode();
      return XPathContext.this.getDTM(i).getNode(i);
    }
    
    public NodeIterator getContextNodes() { return new DTMNodeIterator(XPathContext.this.getContextNodeList()); }
    
    public ErrorListener getErrorListener() { return XPathContext.this.getErrorListener(); }
    
    public boolean overrideDefaultParser() { return XPathContext.this.m_overrideDefaultParser; }
    
    public void setOverrideDefaultParser(boolean param1Boolean) { XPathContext.this.m_overrideDefaultParser = param1Boolean; }
    
    public double toNumber(Node param1Node) {
      int i = XPathContext.this.getDTMHandleFromNode(param1Node);
      DTM dTM = XPathContext.this.getDTM(i);
      XString xString = (XString)dTM.getStringValue(i);
      return xString.num();
    }
    
    public String toString(Node param1Node) {
      int i = XPathContext.this.getDTMHandleFromNode(param1Node);
      DTM dTM = XPathContext.this.getDTM(i);
      XMLString xMLString = dTM.getStringValue(i);
      return xMLString.toString();
    }
    
    public final XObject getVariableOrParam(QName param1QName) throws TransformerException { return XPathContext.this.m_variableStacks.getVariableOrParam(XPathContext.this, param1QName); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\XPathContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */