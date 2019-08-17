package com.sun.org.apache.xml.internal.dtm.ref;

import com.sun.org.apache.xml.internal.dtm.Axis;
import com.sun.org.apache.xml.internal.dtm.DTMAxisTraverser;
import com.sun.org.apache.xml.internal.dtm.DTMException;
import com.sun.org.apache.xml.internal.dtm.DTMManager;
import com.sun.org.apache.xml.internal.dtm.DTMWSFilter;
import com.sun.org.apache.xml.internal.res.XMLMessages;
import com.sun.org.apache.xml.internal.utils.XMLStringFactory;
import javax.xml.transform.Source;

public abstract class DTMDefaultBaseTraversers extends DTMDefaultBase {
  public DTMDefaultBaseTraversers(DTMManager paramDTMManager, Source paramSource, int paramInt, DTMWSFilter paramDTMWSFilter, XMLStringFactory paramXMLStringFactory, boolean paramBoolean) { super(paramDTMManager, paramSource, paramInt, paramDTMWSFilter, paramXMLStringFactory, paramBoolean); }
  
  public DTMDefaultBaseTraversers(DTMManager paramDTMManager, Source paramSource, int paramInt1, DTMWSFilter paramDTMWSFilter, XMLStringFactory paramXMLStringFactory, boolean paramBoolean1, int paramInt2, boolean paramBoolean2, boolean paramBoolean3) { super(paramDTMManager, paramSource, paramInt1, paramDTMWSFilter, paramXMLStringFactory, paramBoolean1, paramInt2, paramBoolean2, paramBoolean3); }
  
  public DTMAxisTraverser getAxisTraverser(int paramInt) {
    FollowingSiblingTraverser followingSiblingTraverser;
    RootTraverser rootTraverser;
    DescendantTraverser descendantTraverser;
    NamespaceDeclsTraverser namespaceDeclsTraverser;
    SelfTraverser selfTraverser;
    AllFromRootTraverser allFromRootTraverser;
    PrecedingAndAncestorTraverser precedingAndAncestorTraverser;
    AllFromNodeTraverser allFromNodeTraverser;
    NamespaceTraverser namespaceTraverser;
    DescendantOrSelfFromRootTraverser descendantOrSelfFromRootTraverser;
    ChildTraverser childTraverser;
    DescendantFromRootTraverser descendantFromRootTraverser;
    AttributeTraverser attributeTraverser;
    AncestorTraverser ancestorTraverser;
    PrecedingSiblingTraverser precedingSiblingTraverser;
    ParentTraverser parentTraverser;
    PrecedingTraverser precedingTraverser;
    if (null == this.m_traversers) {
      this.m_traversers = new DTMAxisTraverser[Axis.getNamesLength()];
      Object object = null;
    } else {
      DTMAxisTraverser dTMAxisTraverser = this.m_traversers[paramInt];
      if (dTMAxisTraverser != null)
        return dTMAxisTraverser; 
    } 
    switch (paramInt) {
      case 0:
        ancestorTraverser = new AncestorTraverser(null);
        break;
      case 1:
        ancestorTraverser = new AncestorOrSelfTraverser(null);
        break;
      case 2:
        attributeTraverser = new AttributeTraverser(null);
        break;
      case 3:
        childTraverser = new ChildTraverser(null);
        break;
      case 4:
        descendantTraverser = new DescendantTraverser(null);
        break;
      case 5:
        descendantTraverser = new DescendantOrSelfTraverser(null);
        break;
      case 6:
        descendantTraverser = new FollowingTraverser(null);
        break;
      case 7:
        followingSiblingTraverser = new FollowingSiblingTraverser(null);
        break;
      case 9:
        namespaceTraverser = new NamespaceTraverser(null);
        break;
      case 8:
        namespaceDeclsTraverser = new NamespaceDeclsTraverser(null);
        break;
      case 10:
        parentTraverser = new ParentTraverser(null);
        break;
      case 11:
        precedingTraverser = new PrecedingTraverser(null);
        break;
      case 12:
        precedingSiblingTraverser = new PrecedingSiblingTraverser(null);
        break;
      case 13:
        selfTraverser = new SelfTraverser(null);
        break;
      case 16:
        allFromRootTraverser = new AllFromRootTraverser(null);
        break;
      case 14:
        allFromNodeTraverser = new AllFromNodeTraverser(null);
        break;
      case 15:
        precedingAndAncestorTraverser = new PrecedingAndAncestorTraverser(null);
        break;
      case 17:
        descendantFromRootTraverser = new DescendantFromRootTraverser(null);
        break;
      case 18:
        descendantOrSelfFromRootTraverser = new DescendantOrSelfFromRootTraverser(null);
        break;
      case 19:
        rootTraverser = new RootTraverser(null);
        break;
      case 20:
        return null;
      default:
        throw new DTMException(XMLMessages.createXMLMessage("ER_UNKNOWN_AXIS_TYPE", new Object[] { Integer.toString(paramInt) }));
    } 
    if (null == rootTraverser)
      throw new DTMException(XMLMessages.createXMLMessage("ER_AXIS_TRAVERSER_NOT_SUPPORTED", new Object[] { Axis.getNames(paramInt) })); 
    this.m_traversers[paramInt] = rootTraverser;
    return rootTraverser;
  }
  
  private class AllFromNodeTraverser extends DescendantOrSelfTraverser {
    private AllFromNodeTraverser() { super(DTMDefaultBaseTraversers.this, null); }
    
    public int next(int param1Int1, int param1Int2) {
      int i = DTMDefaultBaseTraversers.this.makeNodeIdentity(param1Int1);
      param1Int2 = DTMDefaultBaseTraversers.this.makeNodeIdentity(param1Int2) + 1;
      DTMDefaultBaseTraversers.this._exptype(param1Int2);
      return !isDescendant(i, param1Int2) ? -1 : DTMDefaultBaseTraversers.this.makeNodeHandle(param1Int2);
    }
  }
  
  private class AllFromRootTraverser extends AllFromNodeTraverser {
    private AllFromRootTraverser() { super(DTMDefaultBaseTraversers.this, null); }
    
    public int first(int param1Int) { return DTMDefaultBaseTraversers.this.getDocumentRoot(param1Int); }
    
    public int first(int param1Int1, int param1Int2) { return (DTMDefaultBaseTraversers.this.getExpandedTypeID(DTMDefaultBaseTraversers.this.getDocumentRoot(param1Int1)) == param1Int2) ? param1Int1 : next(param1Int1, param1Int1, param1Int2); }
    
    public int next(int param1Int1, int param1Int2) {
      int i = DTMDefaultBaseTraversers.this.makeNodeIdentity(param1Int1);
      param1Int2 = DTMDefaultBaseTraversers.this.makeNodeIdentity(param1Int2) + 1;
      short s = DTMDefaultBaseTraversers.this._type(param1Int2);
      return (s == -1) ? -1 : DTMDefaultBaseTraversers.this.makeNodeHandle(param1Int2);
    }
    
    public int next(int param1Int1, int param1Int2, int param1Int3) {
      int i = DTMDefaultBaseTraversers.this.makeNodeIdentity(param1Int1);
      param1Int2 = DTMDefaultBaseTraversers.this.makeNodeIdentity(param1Int2) + 1;
      while (true) {
        int j = DTMDefaultBaseTraversers.this._exptype(param1Int2);
        if (j == -1)
          return -1; 
        if (j != param1Int3) {
          param1Int2++;
          continue;
        } 
        return DTMDefaultBaseTraversers.this.makeNodeHandle(param1Int2);
      } 
    }
  }
  
  private class AncestorOrSelfTraverser extends AncestorTraverser {
    private AncestorOrSelfTraverser() { super(DTMDefaultBaseTraversers.this, null); }
    
    public int first(int param1Int) { return param1Int; }
    
    public int first(int param1Int1, int param1Int2) { return (DTMDefaultBaseTraversers.this.getExpandedTypeID(param1Int1) == param1Int2) ? param1Int1 : next(param1Int1, param1Int1, param1Int2); }
  }
  
  private class AncestorTraverser extends DTMAxisTraverser {
    private AncestorTraverser() {}
    
    public int next(int param1Int1, int param1Int2) { return DTMDefaultBaseTraversers.this.getParent(param1Int2); }
    
    public int next(int param1Int1, int param1Int2, int param1Int3) {
      param1Int2 = DTMDefaultBaseTraversers.this.makeNodeIdentity(param1Int2);
      while (-1 != (param1Int2 = DTMDefaultBaseTraversers.this.m_parent.elementAt(param1Int2))) {
        if (DTMDefaultBaseTraversers.this.m_exptype.elementAt(param1Int2) == param1Int3)
          return DTMDefaultBaseTraversers.this.makeNodeHandle(param1Int2); 
      } 
      return -1;
    }
  }
  
  private class AttributeTraverser extends DTMAxisTraverser {
    private AttributeTraverser() {}
    
    public int next(int param1Int1, int param1Int2) { return (param1Int1 == param1Int2) ? DTMDefaultBaseTraversers.this.getFirstAttribute(param1Int1) : DTMDefaultBaseTraversers.this.getNextAttribute(param1Int2); }
    
    public int next(int param1Int1, int param1Int2, int param1Int3) {
      param1Int2 = (param1Int1 == param1Int2) ? DTMDefaultBaseTraversers.this.getFirstAttribute(param1Int1) : DTMDefaultBaseTraversers.this.getNextAttribute(param1Int2);
      do {
        if (DTMDefaultBaseTraversers.this.getExpandedTypeID(param1Int2) == param1Int3)
          return param1Int2; 
      } while (-1 != (param1Int2 = DTMDefaultBaseTraversers.this.getNextAttribute(param1Int2)));
      return -1;
    }
  }
  
  private class ChildTraverser extends DTMAxisTraverser {
    private ChildTraverser() {}
    
    protected int getNextIndexed(int param1Int1, int param1Int2, int param1Int3) {
      int i = DTMDefaultBaseTraversers.this.m_expandedNameTable.getNamespaceID(param1Int3);
      int j = DTMDefaultBaseTraversers.this.m_expandedNameTable.getLocalNameID(param1Int3);
      while (true) {
        int k = DTMDefaultBaseTraversers.this.findElementFromIndex(i, j, param1Int2);
        if (-2 != k) {
          int m = DTMDefaultBaseTraversers.this.m_parent.elementAt(k);
          if (m == param1Int1)
            return k; 
          if (m < param1Int1)
            return -1; 
          do {
            m = DTMDefaultBaseTraversers.this.m_parent.elementAt(m);
            if (m < param1Int1)
              return -1; 
          } while (m > param1Int1);
          param1Int2 = k + 1;
          continue;
        } 
        DTMDefaultBaseTraversers.this.nextNode();
        if (DTMDefaultBaseTraversers.this.m_nextsib.elementAt(param1Int1) != -2)
          break; 
      } 
      return -1;
    }
    
    public int first(int param1Int) { return DTMDefaultBaseTraversers.this.getFirstChild(param1Int); }
    
    public int first(int param1Int1, int param1Int2) {
      int i = DTMDefaultBaseTraversers.this.makeNodeIdentity(param1Int1);
      int j = getNextIndexed(i, DTMDefaultBaseTraversers.this._firstch(i), param1Int2);
      return DTMDefaultBaseTraversers.this.makeNodeHandle(j);
    }
    
    public int next(int param1Int1, int param1Int2) { return DTMDefaultBaseTraversers.this.getNextSibling(param1Int2); }
    
    public int next(int param1Int1, int param1Int2, int param1Int3) {
      for (param1Int2 = DTMDefaultBaseTraversers.this._nextsib(DTMDefaultBaseTraversers.this.makeNodeIdentity(param1Int2)); -1 != param1Int2; param1Int2 = DTMDefaultBaseTraversers.this._nextsib(param1Int2)) {
        if (DTMDefaultBaseTraversers.this.m_exptype.elementAt(param1Int2) == param1Int3)
          return DTMDefaultBaseTraversers.this.makeNodeHandle(param1Int2); 
      } 
      return -1;
    }
  }
  
  private class DescendantFromRootTraverser extends DescendantTraverser {
    private DescendantFromRootTraverser() { super(DTMDefaultBaseTraversers.this, null); }
    
    protected int getFirstPotential(int param1Int) { return DTMDefaultBaseTraversers.this._firstch(0); }
    
    protected int getSubtreeRoot(int param1Int) { return 0; }
    
    public int first(int param1Int) { return DTMDefaultBaseTraversers.this.makeNodeHandle(DTMDefaultBaseTraversers.this._firstch(0)); }
    
    public int first(int param1Int1, int param1Int2) {
      if (isIndexed(param1Int2)) {
        byte b = 0;
        int j = getFirstPotential(b);
        return DTMDefaultBaseTraversers.this.makeNodeHandle(getNextIndexed(b, j, param1Int2));
      } 
      int i = DTMDefaultBaseTraversers.this.getDocumentRoot(param1Int1);
      return next(i, i, param1Int2);
    }
  }
  
  private class DescendantOrSelfFromRootTraverser extends DescendantTraverser {
    private DescendantOrSelfFromRootTraverser() { super(DTMDefaultBaseTraversers.this, null); }
    
    protected int getFirstPotential(int param1Int) { return param1Int; }
    
    protected int getSubtreeRoot(int param1Int) { return DTMDefaultBaseTraversers.this.makeNodeIdentity(DTMDefaultBaseTraversers.this.getDocument()); }
    
    public int first(int param1Int) { return DTMDefaultBaseTraversers.this.getDocumentRoot(param1Int); }
    
    public int first(int param1Int1, int param1Int2) {
      if (isIndexed(param1Int2)) {
        byte b = 0;
        int j = getFirstPotential(b);
        return DTMDefaultBaseTraversers.this.makeNodeHandle(getNextIndexed(b, j, param1Int2));
      } 
      int i = first(param1Int1);
      return next(i, i, param1Int2);
    }
  }
  
  private class DescendantOrSelfTraverser extends DescendantTraverser {
    private DescendantOrSelfTraverser() { super(DTMDefaultBaseTraversers.this, null); }
    
    protected int getFirstPotential(int param1Int) { return param1Int; }
    
    public int first(int param1Int) { return param1Int; }
  }
  
  private class DescendantTraverser extends IndexedDTMAxisTraverser {
    private DescendantTraverser() { super(DTMDefaultBaseTraversers.this, null); }
    
    protected int getFirstPotential(int param1Int) { return param1Int + 1; }
    
    protected boolean axisHasBeenProcessed(int param1Int) { return (DTMDefaultBaseTraversers.this.m_nextsib.elementAt(param1Int) != -2); }
    
    protected int getSubtreeRoot(int param1Int) { return DTMDefaultBaseTraversers.this.makeNodeIdentity(param1Int); }
    
    protected boolean isDescendant(int param1Int1, int param1Int2) { return (DTMDefaultBaseTraversers.this._parent(param1Int2) >= param1Int1); }
    
    protected boolean isAfterAxis(int param1Int1, int param1Int2) {
      do {
        if (param1Int2 == param1Int1)
          return false; 
        param1Int2 = DTMDefaultBaseTraversers.this.m_parent.elementAt(param1Int2);
      } while (param1Int2 >= param1Int1);
      return true;
    }
    
    public int first(int param1Int1, int param1Int2) {
      if (isIndexed(param1Int2)) {
        int i = getSubtreeRoot(param1Int1);
        int j = getFirstPotential(i);
        return DTMDefaultBaseTraversers.this.makeNodeHandle(getNextIndexed(i, j, param1Int2));
      } 
      return next(param1Int1, param1Int1, param1Int2);
    }
    
    public int next(int param1Int1, int param1Int2) {
      int i = getSubtreeRoot(param1Int1);
      param1Int2 = DTMDefaultBaseTraversers.this.makeNodeIdentity(param1Int2) + 1;
      while (true) {
        short s = DTMDefaultBaseTraversers.this._type(param1Int2);
        if (!isDescendant(i, param1Int2))
          return -1; 
        if (2 == s || 13 == s) {
          param1Int2++;
          continue;
        } 
        return DTMDefaultBaseTraversers.this.makeNodeHandle(param1Int2);
      } 
    }
    
    public int next(int param1Int1, int param1Int2, int param1Int3) {
      int i = getSubtreeRoot(param1Int1);
      param1Int2 = DTMDefaultBaseTraversers.this.makeNodeIdentity(param1Int2) + 1;
      if (isIndexed(param1Int3))
        return DTMDefaultBaseTraversers.this.makeNodeHandle(getNextIndexed(i, param1Int2, param1Int3)); 
      while (true) {
        int j = DTMDefaultBaseTraversers.this._exptype(param1Int2);
        if (!isDescendant(i, param1Int2))
          return -1; 
        if (j != param1Int3) {
          param1Int2++;
          continue;
        } 
        return DTMDefaultBaseTraversers.this.makeNodeHandle(param1Int2);
      } 
    }
  }
  
  private class FollowingSiblingTraverser extends DTMAxisTraverser {
    private FollowingSiblingTraverser() {}
    
    public int next(int param1Int1, int param1Int2) { return DTMDefaultBaseTraversers.this.getNextSibling(param1Int2); }
    
    public int next(int param1Int1, int param1Int2, int param1Int3) {
      while (-1 != (param1Int2 = DTMDefaultBaseTraversers.this.getNextSibling(param1Int2))) {
        if (DTMDefaultBaseTraversers.this.getExpandedTypeID(param1Int2) == param1Int3)
          return param1Int2; 
      } 
      return -1;
    }
  }
  
  private class FollowingTraverser extends DescendantTraverser {
    private FollowingTraverser() { super(DTMDefaultBaseTraversers.this, null); }
    
    public int first(int param1Int) {
      int i;
      param1Int = DTMDefaultBaseTraversers.this.makeNodeIdentity(param1Int);
      short s = DTMDefaultBaseTraversers.this._type(param1Int);
      if (2 == s || 13 == s) {
        param1Int = DTMDefaultBaseTraversers.this._parent(param1Int);
        i = DTMDefaultBaseTraversers.this._firstch(param1Int);
        if (-1 != i)
          return DTMDefaultBaseTraversers.this.makeNodeHandle(i); 
      } 
      do {
        i = DTMDefaultBaseTraversers.this._nextsib(param1Int);
        if (-1 != i)
          continue; 
        param1Int = DTMDefaultBaseTraversers.this._parent(param1Int);
      } while (-1 == i && -1 != param1Int);
      return DTMDefaultBaseTraversers.this.makeNodeHandle(i);
    }
    
    public int first(int param1Int1, int param1Int2) {
      int i;
      short s = DTMDefaultBaseTraversers.this.getNodeType(param1Int1);
      if (2 == s || 13 == s) {
        param1Int1 = DTMDefaultBaseTraversers.this.getParent(param1Int1);
        i = DTMDefaultBaseTraversers.this.getFirstChild(param1Int1);
        if (-1 != i)
          return (DTMDefaultBaseTraversers.this.getExpandedTypeID(i) == param1Int2) ? i : next(param1Int1, i, param1Int2); 
      } 
      do {
        i = DTMDefaultBaseTraversers.this.getNextSibling(param1Int1);
        if (-1 == i) {
          param1Int1 = DTMDefaultBaseTraversers.this.getParent(param1Int1);
        } else {
          return (DTMDefaultBaseTraversers.this.getExpandedTypeID(i) == param1Int2) ? i : next(param1Int1, i, param1Int2);
        } 
      } while (-1 == i && -1 != param1Int1);
      return i;
    }
    
    public int next(int param1Int1, int param1Int2) {
      param1Int2 = DTMDefaultBaseTraversers.this.makeNodeIdentity(param1Int2);
      while (true) {
        short s = DTMDefaultBaseTraversers.this._type(++param1Int2);
        if (-1 == s)
          return -1; 
        if (2 == s || 13 == s)
          continue; 
        break;
      } 
      return DTMDefaultBaseTraversers.this.makeNodeHandle(param1Int2);
    }
    
    public int next(int param1Int1, int param1Int2, int param1Int3) {
      param1Int2 = DTMDefaultBaseTraversers.this.makeNodeIdentity(param1Int2);
      while (true) {
        int i = DTMDefaultBaseTraversers.this._exptype(++param1Int2);
        if (-1 == i)
          return -1; 
        if (i != param1Int3)
          continue; 
        break;
      } 
      return DTMDefaultBaseTraversers.this.makeNodeHandle(param1Int2);
    }
  }
  
  private abstract class IndexedDTMAxisTraverser extends DTMAxisTraverser {
    private IndexedDTMAxisTraverser() {}
    
    protected final boolean isIndexed(int param1Int) { return (DTMDefaultBaseTraversers.this.m_indexing && 1 == DTMDefaultBaseTraversers.this.m_expandedNameTable.getType(param1Int)); }
    
    protected abstract boolean isAfterAxis(int param1Int1, int param1Int2);
    
    protected abstract boolean axisHasBeenProcessed(int param1Int);
    
    protected int getNextIndexed(int param1Int1, int param1Int2, int param1Int3) {
      int i = DTMDefaultBaseTraversers.this.m_expandedNameTable.getNamespaceID(param1Int3);
      int j = DTMDefaultBaseTraversers.this.m_expandedNameTable.getLocalNameID(param1Int3);
      while (true) {
        int k = DTMDefaultBaseTraversers.this.findElementFromIndex(i, j, param1Int2);
        if (-2 != k)
          return isAfterAxis(param1Int1, k) ? -1 : k; 
        if (axisHasBeenProcessed(param1Int1))
          break; 
        DTMDefaultBaseTraversers.this.nextNode();
      } 
      return -1;
    }
  }
  
  private class NamespaceDeclsTraverser extends DTMAxisTraverser {
    private NamespaceDeclsTraverser() {}
    
    public int next(int param1Int1, int param1Int2) { return (param1Int1 == param1Int2) ? DTMDefaultBaseTraversers.this.getFirstNamespaceNode(param1Int1, false) : DTMDefaultBaseTraversers.this.getNextNamespaceNode(param1Int1, param1Int2, false); }
    
    public int next(int param1Int1, int param1Int2, int param1Int3) {
      param1Int2 = (param1Int1 == param1Int2) ? DTMDefaultBaseTraversers.this.getFirstNamespaceNode(param1Int1, false) : DTMDefaultBaseTraversers.this.getNextNamespaceNode(param1Int1, param1Int2, false);
      do {
        if (DTMDefaultBaseTraversers.this.getExpandedTypeID(param1Int2) == param1Int3)
          return param1Int2; 
      } while (-1 != (param1Int2 = DTMDefaultBaseTraversers.this.getNextNamespaceNode(param1Int1, param1Int2, false)));
      return -1;
    }
  }
  
  private class NamespaceTraverser extends DTMAxisTraverser {
    private NamespaceTraverser() {}
    
    public int next(int param1Int1, int param1Int2) { return (param1Int1 == param1Int2) ? DTMDefaultBaseTraversers.this.getFirstNamespaceNode(param1Int1, true) : DTMDefaultBaseTraversers.this.getNextNamespaceNode(param1Int1, param1Int2, true); }
    
    public int next(int param1Int1, int param1Int2, int param1Int3) {
      param1Int2 = (param1Int1 == param1Int2) ? DTMDefaultBaseTraversers.this.getFirstNamespaceNode(param1Int1, true) : DTMDefaultBaseTraversers.this.getNextNamespaceNode(param1Int1, param1Int2, true);
      do {
        if (DTMDefaultBaseTraversers.this.getExpandedTypeID(param1Int2) == param1Int3)
          return param1Int2; 
      } while (-1 != (param1Int2 = DTMDefaultBaseTraversers.this.getNextNamespaceNode(param1Int1, param1Int2, true)));
      return -1;
    }
  }
  
  private class ParentTraverser extends DTMAxisTraverser {
    private ParentTraverser() {}
    
    public int first(int param1Int) { return DTMDefaultBaseTraversers.this.getParent(param1Int); }
    
    public int first(int param1Int1, int param1Int2) {
      param1Int1 = DTMDefaultBaseTraversers.this.makeNodeIdentity(param1Int1);
      while (-1 != (param1Int1 = DTMDefaultBaseTraversers.this.m_parent.elementAt(param1Int1))) {
        if (DTMDefaultBaseTraversers.this.m_exptype.elementAt(param1Int1) == param1Int2)
          return DTMDefaultBaseTraversers.this.makeNodeHandle(param1Int1); 
      } 
      return -1;
    }
    
    public int next(int param1Int1, int param1Int2) { return -1; }
    
    public int next(int param1Int1, int param1Int2, int param1Int3) { return -1; }
  }
  
  private class PrecedingAndAncestorTraverser extends DTMAxisTraverser {
    private PrecedingAndAncestorTraverser() {}
    
    public int next(int param1Int1, int param1Int2) {
      int i = DTMDefaultBaseTraversers.this.makeNodeIdentity(param1Int1);
      param1Int2 = DTMDefaultBaseTraversers.this.makeNodeIdentity(param1Int2) - 1;
      while (param1Int2 >= 0) {
        short s = DTMDefaultBaseTraversers.this._type(param1Int2);
        if (2 == s || 13 == s) {
          param1Int2--;
          continue;
        } 
        return DTMDefaultBaseTraversers.this.makeNodeHandle(param1Int2);
      } 
      return -1;
    }
    
    public int next(int param1Int1, int param1Int2, int param1Int3) {
      int i = DTMDefaultBaseTraversers.this.makeNodeIdentity(param1Int1);
      param1Int2 = DTMDefaultBaseTraversers.this.makeNodeIdentity(param1Int2) - 1;
      while (param1Int2 >= 0) {
        int j = DTMDefaultBaseTraversers.this.m_exptype.elementAt(param1Int2);
        if (j != param1Int3) {
          param1Int2--;
          continue;
        } 
        return DTMDefaultBaseTraversers.this.makeNodeHandle(param1Int2);
      } 
      return -1;
    }
  }
  
  private class PrecedingSiblingTraverser extends DTMAxisTraverser {
    private PrecedingSiblingTraverser() {}
    
    public int next(int param1Int1, int param1Int2) { return DTMDefaultBaseTraversers.this.getPreviousSibling(param1Int2); }
    
    public int next(int param1Int1, int param1Int2, int param1Int3) {
      while (-1 != (param1Int2 = DTMDefaultBaseTraversers.this.getPreviousSibling(param1Int2))) {
        if (DTMDefaultBaseTraversers.this.getExpandedTypeID(param1Int2) == param1Int3)
          return param1Int2; 
      } 
      return -1;
    }
  }
  
  private class PrecedingTraverser extends DTMAxisTraverser {
    private PrecedingTraverser() {}
    
    protected boolean isAncestor(int param1Int1, int param1Int2) {
      for (param1Int1 = DTMDefaultBaseTraversers.this.m_parent.elementAt(param1Int1); -1 != param1Int1; param1Int1 = DTMDefaultBaseTraversers.this.m_parent.elementAt(param1Int1)) {
        if (param1Int1 == param1Int2)
          return true; 
      } 
      return false;
    }
    
    public int next(int param1Int1, int param1Int2) {
      int i = DTMDefaultBaseTraversers.this.makeNodeIdentity(param1Int1);
      param1Int2 = DTMDefaultBaseTraversers.this.makeNodeIdentity(param1Int2) - 1;
      while (param1Int2 >= 0) {
        short s = DTMDefaultBaseTraversers.this._type(param1Int2);
        if (2 == s || 13 == s || isAncestor(i, param1Int2)) {
          param1Int2--;
          continue;
        } 
        return DTMDefaultBaseTraversers.this.makeNodeHandle(param1Int2);
      } 
      return -1;
    }
    
    public int next(int param1Int1, int param1Int2, int param1Int3) {
      int i = DTMDefaultBaseTraversers.this.makeNodeIdentity(param1Int1);
      param1Int2 = DTMDefaultBaseTraversers.this.makeNodeIdentity(param1Int2) - 1;
      while (param1Int2 >= 0) {
        int j = DTMDefaultBaseTraversers.this.m_exptype.elementAt(param1Int2);
        if (j != param1Int3 || isAncestor(i, param1Int2)) {
          param1Int2--;
          continue;
        } 
        return DTMDefaultBaseTraversers.this.makeNodeHandle(param1Int2);
      } 
      return -1;
    }
  }
  
  private class RootTraverser extends AllFromRootTraverser {
    private RootTraverser() { super(DTMDefaultBaseTraversers.this, null); }
    
    public int first(int param1Int1, int param1Int2) {
      int i = DTMDefaultBaseTraversers.this.getDocumentRoot(param1Int1);
      return (DTMDefaultBaseTraversers.this.getExpandedTypeID(i) == param1Int2) ? i : -1;
    }
    
    public int next(int param1Int1, int param1Int2) { return -1; }
    
    public int next(int param1Int1, int param1Int2, int param1Int3) { return -1; }
  }
  
  private class SelfTraverser extends DTMAxisTraverser {
    private SelfTraverser() {}
    
    public int first(int param1Int) { return param1Int; }
    
    public int first(int param1Int1, int param1Int2) { return (DTMDefaultBaseTraversers.this.getExpandedTypeID(param1Int1) == param1Int2) ? param1Int1 : -1; }
    
    public int next(int param1Int1, int param1Int2) { return -1; }
    
    public int next(int param1Int1, int param1Int2, int param1Int3) { return -1; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\dtm\ref\DTMDefaultBaseTraversers.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */