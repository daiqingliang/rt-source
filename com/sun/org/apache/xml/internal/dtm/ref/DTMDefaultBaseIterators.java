package com.sun.org.apache.xml.internal.dtm.ref;

import com.sun.org.apache.xml.internal.dtm.Axis;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.DTMAxisTraverser;
import com.sun.org.apache.xml.internal.dtm.DTMException;
import com.sun.org.apache.xml.internal.dtm.DTMManager;
import com.sun.org.apache.xml.internal.dtm.DTMWSFilter;
import com.sun.org.apache.xml.internal.res.XMLMessages;
import com.sun.org.apache.xml.internal.utils.NodeVector;
import com.sun.org.apache.xml.internal.utils.XMLStringFactory;
import javax.xml.transform.Source;

public abstract class DTMDefaultBaseIterators extends DTMDefaultBaseTraversers {
  public DTMDefaultBaseIterators(DTMManager paramDTMManager, Source paramSource, int paramInt, DTMWSFilter paramDTMWSFilter, XMLStringFactory paramXMLStringFactory, boolean paramBoolean) { super(paramDTMManager, paramSource, paramInt, paramDTMWSFilter, paramXMLStringFactory, paramBoolean); }
  
  public DTMDefaultBaseIterators(DTMManager paramDTMManager, Source paramSource, int paramInt1, DTMWSFilter paramDTMWSFilter, XMLStringFactory paramXMLStringFactory, boolean paramBoolean1, int paramInt2, boolean paramBoolean2, boolean paramBoolean3) { super(paramDTMManager, paramSource, paramInt1, paramDTMWSFilter, paramXMLStringFactory, paramBoolean1, paramInt2, paramBoolean2, paramBoolean3); }
  
  public DTMAxisIterator getTypedAxisIterator(int paramInt1, int paramInt2) {
    null = null;
    switch (paramInt1) {
      case 13:
        return new TypedSingletonIterator(paramInt2);
      case 3:
        return new TypedChildrenIterator(paramInt2);
      case 10:
        return (new ParentIterator()).setNodeType(paramInt2);
      case 0:
        return new TypedAncestorIterator(paramInt2);
      case 1:
        return (new TypedAncestorIterator(paramInt2)).includeSelf();
      case 2:
        return new TypedAttributeIterator(paramInt2);
      case 4:
        return new TypedDescendantIterator(paramInt2);
      case 5:
        return (new TypedDescendantIterator(paramInt2)).includeSelf();
      case 6:
        return new TypedFollowingIterator(paramInt2);
      case 11:
        return new TypedPrecedingIterator(paramInt2);
      case 7:
        return new TypedFollowingSiblingIterator(paramInt2);
      case 12:
        return new TypedPrecedingSiblingIterator(paramInt2);
      case 9:
        return new TypedNamespaceIterator(paramInt2);
      case 19:
        return new TypedRootIterator(paramInt2);
    } 
    throw new DTMException(XMLMessages.createXMLMessage("ER_TYPED_ITERATOR_AXIS_NOT_IMPLEMENTED", new Object[] { Axis.getNames(paramInt1) }));
  }
  
  public DTMAxisIterator getAxisIterator(int paramInt) {
    null = null;
    switch (paramInt) {
      case 13:
        return new SingletonIterator();
      case 3:
        return new ChildrenIterator();
      case 10:
        return new ParentIterator();
      case 0:
        return new AncestorIterator();
      case 1:
        return (new AncestorIterator()).includeSelf();
      case 2:
        return new AttributeIterator();
      case 4:
        return new DescendantIterator();
      case 5:
        return (new DescendantIterator()).includeSelf();
      case 6:
        return new FollowingIterator();
      case 11:
        return new PrecedingIterator();
      case 7:
        return new FollowingSiblingIterator();
      case 12:
        return new PrecedingSiblingIterator();
      case 9:
        return new NamespaceIterator();
      case 19:
        return new RootIterator();
    } 
    throw new DTMException(XMLMessages.createXMLMessage("ER_ITERATOR_AXIS_NOT_IMPLEMENTED", new Object[] { Axis.getNames(paramInt) }));
  }
  
  public class AncestorIterator extends InternalAxisIteratorBase {
    NodeVector m_ancestors = new NodeVector();
    
    int m_ancestorsPos;
    
    int m_markedPos;
    
    int m_realStartNode;
    
    public AncestorIterator() { super(DTMDefaultBaseIterators.this); }
    
    public int getStartNode() { return this.m_realStartNode; }
    
    public final boolean isReverse() { return true; }
    
    public DTMAxisIterator cloneIterator() {
      this._isRestartable = false;
      try {
        AncestorIterator ancestorIterator = (AncestorIterator)clone();
        ancestorIterator._startNode = this._startNode;
        return ancestorIterator;
      } catch (CloneNotSupportedException cloneNotSupportedException) {
        throw new DTMException(XMLMessages.createXMLMessage("ER_ITERATOR_CLONE_NOT_SUPPORTED", null));
      } 
    }
    
    public DTMAxisIterator setStartNode(int param1Int) {
      if (param1Int == 0)
        param1Int = DTMDefaultBaseIterators.this.getDocument(); 
      this.m_realStartNode = param1Int;
      if (this._isRestartable) {
        int i = DTMDefaultBaseIterators.this.makeNodeIdentity(param1Int);
        if (!this._includeSelf && param1Int != -1) {
          i = DTMDefaultBaseIterators.this._parent(i);
          param1Int = DTMDefaultBaseIterators.this.makeNodeHandle(i);
        } 
        this._startNode = param1Int;
        while (i != -1) {
          this.m_ancestors.addElement(param1Int);
          i = DTMDefaultBaseIterators.this._parent(i);
          param1Int = DTMDefaultBaseIterators.this.makeNodeHandle(i);
        } 
        this.m_ancestorsPos = this.m_ancestors.size() - 1;
        this._currentNode = (this.m_ancestorsPos >= 0) ? this.m_ancestors.elementAt(this.m_ancestorsPos) : -1;
        return resetPosition();
      } 
      return this;
    }
    
    public DTMAxisIterator reset() {
      this.m_ancestorsPos = this.m_ancestors.size() - 1;
      this._currentNode = (this.m_ancestorsPos >= 0) ? this.m_ancestors.elementAt(this.m_ancestorsPos) : -1;
      return resetPosition();
    }
    
    public int next() {
      int i = this._currentNode;
      int j = --this.m_ancestorsPos;
      this._currentNode = (j >= 0) ? this.m_ancestors.elementAt(this.m_ancestorsPos) : -1;
      return returnNode(i);
    }
    
    public void setMark() { this.m_markedPos = this.m_ancestorsPos; }
    
    public void gotoMark() {
      this.m_ancestorsPos = this.m_markedPos;
      this._currentNode = (this.m_ancestorsPos >= 0) ? this.m_ancestors.elementAt(this.m_ancestorsPos) : -1;
    }
  }
  
  public final class AttributeIterator extends InternalAxisIteratorBase {
    public AttributeIterator() { super(DTMDefaultBaseIterators.this); }
    
    public DTMAxisIterator setStartNode(int param1Int) {
      if (param1Int == 0)
        param1Int = DTMDefaultBaseIterators.this.getDocument(); 
      if (this._isRestartable) {
        this._startNode = param1Int;
        this._currentNode = DTMDefaultBaseIterators.this.getFirstAttributeIdentity(DTMDefaultBaseIterators.this.makeNodeIdentity(param1Int));
        return resetPosition();
      } 
      return this;
    }
    
    public int next() {
      int i = this._currentNode;
      if (i != -1) {
        this._currentNode = DTMDefaultBaseIterators.this.getNextAttributeIdentity(i);
        return returnNode(DTMDefaultBaseIterators.this.makeNodeHandle(i));
      } 
      return -1;
    }
  }
  
  public final class ChildrenIterator extends InternalAxisIteratorBase {
    public ChildrenIterator() { super(DTMDefaultBaseIterators.this); }
    
    public DTMAxisIterator setStartNode(int param1Int) {
      if (param1Int == 0)
        param1Int = DTMDefaultBaseIterators.this.getDocument(); 
      if (this._isRestartable) {
        this._startNode = param1Int;
        this._currentNode = (param1Int == -1) ? -1 : DTMDefaultBaseIterators.this._firstch(DTMDefaultBaseIterators.this.makeNodeIdentity(param1Int));
        return resetPosition();
      } 
      return this;
    }
    
    public int next() {
      if (this._currentNode != -1) {
        int i = this._currentNode;
        this._currentNode = DTMDefaultBaseIterators.this._nextsib(i);
        return returnNode(DTMDefaultBaseIterators.this.makeNodeHandle(i));
      } 
      return -1;
    }
  }
  
  public class DescendantIterator extends InternalAxisIteratorBase {
    public DescendantIterator() { super(DTMDefaultBaseIterators.this); }
    
    public DTMAxisIterator setStartNode(int param1Int) {
      if (param1Int == 0)
        param1Int = DTMDefaultBaseIterators.this.getDocument(); 
      if (this._isRestartable) {
        param1Int = DTMDefaultBaseIterators.this.makeNodeIdentity(param1Int);
        this._startNode = param1Int;
        if (this._includeSelf)
          param1Int--; 
        this._currentNode = param1Int;
        return resetPosition();
      } 
      return this;
    }
    
    protected boolean isDescendant(int param1Int) { return (DTMDefaultBaseIterators.this._parent(param1Int) >= this._startNode || this._startNode == param1Int); }
    
    public int next() {
      short s;
      if (this._startNode == -1)
        return -1; 
      if (this._includeSelf && this._currentNode + 1 == this._startNode)
        return returnNode(DTMDefaultBaseIterators.this.makeNodeHandle(++this._currentNode)); 
      int i = this._currentNode;
      do {
        s = DTMDefaultBaseIterators.this._type(++i);
        if (-1 == s || !isDescendant(i)) {
          this._currentNode = -1;
          return -1;
        } 
      } while (2 == s || 3 == s || 13 == s);
      this._currentNode = i;
      return returnNode(DTMDefaultBaseIterators.this.makeNodeHandle(i));
    }
    
    public DTMAxisIterator reset() {
      boolean bool = this._isRestartable;
      this._isRestartable = true;
      setStartNode(DTMDefaultBaseIterators.this.makeNodeHandle(this._startNode));
      this._isRestartable = bool;
      return this;
    }
  }
  
  public class FollowingIterator extends InternalAxisIteratorBase {
    DTMAxisTraverser m_traverser;
    
    public FollowingIterator() {
      super(DTMDefaultBaseIterators.this);
      this.m_traverser = this$0.getAxisTraverser(6);
    }
    
    public DTMAxisIterator setStartNode(int param1Int) {
      if (param1Int == 0)
        param1Int = DTMDefaultBaseIterators.this.getDocument(); 
      if (this._isRestartable) {
        this._startNode = param1Int;
        this._currentNode = this.m_traverser.first(param1Int);
        return resetPosition();
      } 
      return this;
    }
    
    public int next() {
      int i = this._currentNode;
      this._currentNode = this.m_traverser.next(this._startNode, this._currentNode);
      return returnNode(i);
    }
  }
  
  public class FollowingSiblingIterator extends InternalAxisIteratorBase {
    public FollowingSiblingIterator() { super(DTMDefaultBaseIterators.this); }
    
    public DTMAxisIterator setStartNode(int param1Int) {
      if (param1Int == 0)
        param1Int = DTMDefaultBaseIterators.this.getDocument(); 
      if (this._isRestartable) {
        this._startNode = param1Int;
        this._currentNode = DTMDefaultBaseIterators.this.makeNodeIdentity(param1Int);
        return resetPosition();
      } 
      return this;
    }
    
    public int next() {
      this._currentNode = (this._currentNode == -1) ? -1 : DTMDefaultBaseIterators.this._nextsib(this._currentNode);
      return returnNode(DTMDefaultBaseIterators.this.makeNodeHandle(this._currentNode));
    }
  }
  
  public abstract class InternalAxisIteratorBase extends DTMAxisIteratorBase {
    protected int _currentNode;
    
    public void setMark() { this._markedNode = this._currentNode; }
    
    public void gotoMark() { this._currentNode = this._markedNode; }
  }
  
  public final class NamespaceAttributeIterator extends InternalAxisIteratorBase {
    private final int _nsType;
    
    public NamespaceAttributeIterator(int param1Int) {
      super(DTMDefaultBaseIterators.this);
      this._nsType = param1Int;
    }
    
    public DTMAxisIterator setStartNode(int param1Int) {
      if (param1Int == 0)
        param1Int = DTMDefaultBaseIterators.this.getDocument(); 
      if (this._isRestartable) {
        this._startNode = param1Int;
        this._currentNode = DTMDefaultBaseIterators.this.getFirstNamespaceNode(param1Int, false);
        return resetPosition();
      } 
      return this;
    }
    
    public int next() {
      int i = this._currentNode;
      if (-1 != i)
        this._currentNode = DTMDefaultBaseIterators.this.getNextNamespaceNode(this._startNode, i, false); 
      return returnNode(i);
    }
  }
  
  public final class NamespaceChildrenIterator extends InternalAxisIteratorBase {
    private final int _nsType;
    
    public NamespaceChildrenIterator(int param1Int) {
      super(DTMDefaultBaseIterators.this);
      this._nsType = param1Int;
    }
    
    public DTMAxisIterator setStartNode(int param1Int) {
      if (param1Int == 0)
        param1Int = DTMDefaultBaseIterators.this.getDocument(); 
      if (this._isRestartable) {
        this._startNode = param1Int;
        this._currentNode = (param1Int == -1) ? -1 : -2;
        return resetPosition();
      } 
      return this;
    }
    
    public int next() {
      if (this._currentNode != -1)
        for (int i = (-2 == this._currentNode) ? DTMDefaultBaseIterators.this._firstch(DTMDefaultBaseIterators.this.makeNodeIdentity(this._startNode)) : DTMDefaultBaseIterators.this._nextsib(this._currentNode); i != -1; i = DTMDefaultBaseIterators.this._nextsib(i)) {
          if (DTMDefaultBaseIterators.this.m_expandedNameTable.getNamespaceID(DTMDefaultBaseIterators.this._exptype(i)) == this._nsType) {
            this._currentNode = i;
            return returnNode(i);
          } 
        }  
      return -1;
    }
  }
  
  public class NamespaceIterator extends InternalAxisIteratorBase {
    public NamespaceIterator() { super(DTMDefaultBaseIterators.this); }
    
    public DTMAxisIterator setStartNode(int param1Int) {
      if (param1Int == 0)
        param1Int = DTMDefaultBaseIterators.this.getDocument(); 
      if (this._isRestartable) {
        this._startNode = param1Int;
        this._currentNode = DTMDefaultBaseIterators.this.getFirstNamespaceNode(param1Int, true);
        return resetPosition();
      } 
      return this;
    }
    
    public int next() {
      int i = this._currentNode;
      if (-1 != i)
        this._currentNode = DTMDefaultBaseIterators.this.getNextNamespaceNode(this._startNode, i, true); 
      return returnNode(i);
    }
  }
  
  public class NthDescendantIterator extends DescendantIterator {
    int _pos;
    
    public NthDescendantIterator(int param1Int) {
      super(DTMDefaultBaseIterators.this);
      this._pos = param1Int;
    }
    
    public int next() {
      int i;
      while ((i = super.next()) != -1) {
        i = DTMDefaultBaseIterators.this.makeNodeIdentity(i);
        int j = DTMDefaultBaseIterators.this._parent(i);
        int k = DTMDefaultBaseIterators.this._firstch(j);
        byte b = 0;
        do {
          short s = DTMDefaultBaseIterators.this._type(k);
          if (1 != s)
            continue; 
          b++;
        } while (b < this._pos && (k = DTMDefaultBaseIterators.this._nextsib(k)) != -1);
        if (i == k)
          return i; 
      } 
      return -1;
    }
  }
  
  public final class ParentIterator extends InternalAxisIteratorBase {
    private int _nodeType = -1;
    
    public ParentIterator() { super(DTMDefaultBaseIterators.this); }
    
    public DTMAxisIterator setStartNode(int param1Int) {
      if (param1Int == 0)
        param1Int = DTMDefaultBaseIterators.this.getDocument(); 
      if (this._isRestartable) {
        this._startNode = param1Int;
        this._currentNode = DTMDefaultBaseIterators.this.getParent(param1Int);
        return resetPosition();
      } 
      return this;
    }
    
    public DTMAxisIterator setNodeType(int param1Int) {
      this._nodeType = param1Int;
      return this;
    }
    
    public int next() {
      int i = this._currentNode;
      if (this._nodeType >= 14) {
        if (this._nodeType != DTMDefaultBaseIterators.this.getExpandedTypeID(this._currentNode))
          i = -1; 
      } else if (this._nodeType != -1 && this._nodeType != DTMDefaultBaseIterators.this.getNodeType(this._currentNode)) {
        i = -1;
      } 
      this._currentNode = -1;
      return returnNode(i);
    }
  }
  
  public class PrecedingIterator extends InternalAxisIteratorBase {
    private final int _maxAncestors = 8;
    
    protected int[] _stack = new int[8];
    
    protected int _sp;
    
    protected int _oldsp;
    
    protected int _markedsp;
    
    protected int _markedNode;
    
    protected int _markedDescendant;
    
    public PrecedingIterator() { super(DTMDefaultBaseIterators.this); }
    
    public boolean isReverse() { return true; }
    
    public DTMAxisIterator cloneIterator() {
      this._isRestartable = false;
      try {
        PrecedingIterator precedingIterator = (PrecedingIterator)clone();
        int[] arrayOfInt = new int[this._stack.length];
        System.arraycopy(this._stack, 0, arrayOfInt, 0, this._stack.length);
        precedingIterator._stack = arrayOfInt;
        return precedingIterator;
      } catch (CloneNotSupportedException cloneNotSupportedException) {
        throw new DTMException(XMLMessages.createXMLMessage("ER_ITERATOR_CLONE_NOT_SUPPORTED", null));
      } 
    }
    
    public DTMAxisIterator setStartNode(int param1Int) {
      if (param1Int == 0)
        param1Int = DTMDefaultBaseIterators.this.getDocument(); 
      if (this._isRestartable) {
        param1Int = DTMDefaultBaseIterators.this.makeNodeIdentity(param1Int);
        if (DTMDefaultBaseIterators.this._type(param1Int) == 2)
          param1Int = DTMDefaultBaseIterators.this._parent(param1Int); 
        this._startNode = param1Int;
        byte b;
        this._stack[b = 0] = param1Int;
        int i = param1Int;
        while ((i = DTMDefaultBaseIterators.this._parent(i)) != -1) {
          if (++b == this._stack.length) {
            int[] arrayOfInt = new int[b + 4];
            System.arraycopy(this._stack, 0, arrayOfInt, 0, b);
            this._stack = arrayOfInt;
          } 
          this._stack[b] = i;
        } 
        if (b > 0)
          b--; 
        this._currentNode = this._stack[b];
        this._oldsp = this._sp = b;
        return resetPosition();
      } 
      return this;
    }
    
    public int next() {
      this._currentNode++;
      while (this._sp >= 0) {
        if (this._currentNode < this._stack[this._sp]) {
          if (DTMDefaultBaseIterators.this._type(this._currentNode) != 2 && DTMDefaultBaseIterators.this._type(this._currentNode) != 13)
            return returnNode(DTMDefaultBaseIterators.this.makeNodeHandle(this._currentNode)); 
        } else {
          this._sp--;
        } 
        this._currentNode++;
      } 
      return -1;
    }
    
    public DTMAxisIterator reset() {
      this._sp = this._oldsp;
      return resetPosition();
    }
    
    public void setMark() {
      this._markedsp = this._sp;
      this._markedNode = this._currentNode;
      this._markedDescendant = this._stack[0];
    }
    
    public void gotoMark() {
      this._sp = this._markedsp;
      this._currentNode = this._markedNode;
    }
  }
  
  public class PrecedingSiblingIterator extends InternalAxisIteratorBase {
    protected int _startNodeID;
    
    public PrecedingSiblingIterator() { super(DTMDefaultBaseIterators.this); }
    
    public boolean isReverse() { return true; }
    
    public DTMAxisIterator setStartNode(int param1Int) {
      if (param1Int == 0)
        param1Int = DTMDefaultBaseIterators.this.getDocument(); 
      if (this._isRestartable) {
        this._startNode = param1Int;
        param1Int = this._startNodeID = DTMDefaultBaseIterators.this.makeNodeIdentity(param1Int);
        if (param1Int == -1) {
          this._currentNode = param1Int;
          return resetPosition();
        } 
        short s = DTMDefaultBaseIterators.this.m_expandedNameTable.getType(DTMDefaultBaseIterators.this._exptype(param1Int));
        if (2 == s || 13 == s) {
          this._currentNode = param1Int;
        } else {
          this._currentNode = DTMDefaultBaseIterators.this._parent(param1Int);
          if (-1 != this._currentNode) {
            this._currentNode = DTMDefaultBaseIterators.this._firstch(this._currentNode);
          } else {
            this._currentNode = param1Int;
          } 
        } 
        return resetPosition();
      } 
      return this;
    }
    
    public int next() {
      if (this._currentNode == this._startNodeID || this._currentNode == -1)
        return -1; 
      int i = this._currentNode;
      this._currentNode = DTMDefaultBaseIterators.this._nextsib(i);
      return returnNode(DTMDefaultBaseIterators.this.makeNodeHandle(i));
    }
  }
  
  public class RootIterator extends InternalAxisIteratorBase {
    public RootIterator() { super(DTMDefaultBaseIterators.this); }
    
    public DTMAxisIterator setStartNode(int param1Int) {
      if (this._isRestartable) {
        this._startNode = DTMDefaultBaseIterators.this.getDocumentRoot(param1Int);
        this._currentNode = -1;
        return resetPosition();
      } 
      return this;
    }
    
    public int next() {
      if (this._startNode == this._currentNode)
        return -1; 
      this._currentNode = this._startNode;
      return returnNode(this._startNode);
    }
  }
  
  public class SingletonIterator extends InternalAxisIteratorBase {
    private boolean _isConstant;
    
    public SingletonIterator(DTMDefaultBaseIterators this$0) { this(-2147483648, false); }
    
    public SingletonIterator(DTMDefaultBaseIterators this$0, int param1Int) { this(param1Int, false); }
    
    public SingletonIterator(int param1Int, boolean param1Boolean) {
      super(DTMDefaultBaseIterators.this);
      this._currentNode = this._startNode = param1Int;
      this._isConstant = param1Boolean;
    }
    
    public DTMAxisIterator setStartNode(int param1Int) {
      if (param1Int == 0)
        param1Int = DTMDefaultBaseIterators.this.getDocument(); 
      if (this._isConstant) {
        this._currentNode = this._startNode;
        return resetPosition();
      } 
      if (this._isRestartable) {
        this._currentNode = this._startNode = param1Int;
        return resetPosition();
      } 
      return this;
    }
    
    public DTMAxisIterator reset() {
      if (this._isConstant) {
        this._currentNode = this._startNode;
        return resetPosition();
      } 
      boolean bool = this._isRestartable;
      this._isRestartable = true;
      setStartNode(this._startNode);
      this._isRestartable = bool;
      return this;
    }
    
    public int next() {
      int i = this._currentNode;
      this._currentNode = -1;
      return returnNode(i);
    }
  }
  
  public final class TypedAncestorIterator extends AncestorIterator {
    private final int _nodeType;
    
    public TypedAncestorIterator(int param1Int) {
      super(DTMDefaultBaseIterators.this);
      this._nodeType = param1Int;
    }
    
    public DTMAxisIterator setStartNode(int param1Int) {
      if (param1Int == 0)
        param1Int = DTMDefaultBaseIterators.this.getDocument(); 
      this.m_realStartNode = param1Int;
      if (this._isRestartable) {
        int i = DTMDefaultBaseIterators.this.makeNodeIdentity(param1Int);
        int j = this._nodeType;
        if (!this._includeSelf && param1Int != -1)
          i = DTMDefaultBaseIterators.this._parent(i); 
        this._startNode = param1Int;
        if (j >= 14) {
          while (i != -1) {
            int k = DTMDefaultBaseIterators.this._exptype(i);
            if (k == j)
              this.m_ancestors.addElement(DTMDefaultBaseIterators.this.makeNodeHandle(i)); 
            i = DTMDefaultBaseIterators.this._parent(i);
          } 
        } else {
          while (i != -1) {
            int k = DTMDefaultBaseIterators.this._exptype(i);
            if ((k >= 14 && DTMDefaultBaseIterators.this.m_expandedNameTable.getType(k) == j) || (k < 14 && k == j))
              this.m_ancestors.addElement(DTMDefaultBaseIterators.this.makeNodeHandle(i)); 
            i = DTMDefaultBaseIterators.this._parent(i);
          } 
        } 
        this.m_ancestorsPos = this.m_ancestors.size() - 1;
        this._currentNode = (this.m_ancestorsPos >= 0) ? this.m_ancestors.elementAt(this.m_ancestorsPos) : -1;
        return resetPosition();
      } 
      return this;
    }
  }
  
  public final class TypedAttributeIterator extends InternalAxisIteratorBase {
    private final int _nodeType;
    
    public TypedAttributeIterator(int param1Int) {
      super(DTMDefaultBaseIterators.this);
      this._nodeType = param1Int;
    }
    
    public DTMAxisIterator setStartNode(int param1Int) {
      if (this._isRestartable) {
        this._startNode = param1Int;
        this._currentNode = DTMDefaultBaseIterators.this.getTypedAttribute(param1Int, this._nodeType);
        return resetPosition();
      } 
      return this;
    }
    
    public int next() {
      int i = this._currentNode;
      this._currentNode = -1;
      return returnNode(i);
    }
  }
  
  public final class TypedChildrenIterator extends InternalAxisIteratorBase {
    private final int _nodeType;
    
    public TypedChildrenIterator(int param1Int) {
      super(DTMDefaultBaseIterators.this);
      this._nodeType = param1Int;
    }
    
    public DTMAxisIterator setStartNode(int param1Int) {
      if (param1Int == 0)
        param1Int = DTMDefaultBaseIterators.this.getDocument(); 
      if (this._isRestartable) {
        this._startNode = param1Int;
        this._currentNode = (param1Int == -1) ? -1 : DTMDefaultBaseIterators.this._firstch(DTMDefaultBaseIterators.this.makeNodeIdentity(this._startNode));
        return resetPosition();
      } 
      return this;
    }
    
    public int next() {
      int i = this._currentNode;
      int j = this._nodeType;
      if (j >= 14) {
        while (i != -1 && DTMDefaultBaseIterators.this._exptype(i) != j)
          i = DTMDefaultBaseIterators.this._nextsib(i); 
      } else {
        while (i != -1) {
          int k = DTMDefaultBaseIterators.this._exptype(i);
          if ((k < 14) ? (k == j) : (DTMDefaultBaseIterators.this.m_expandedNameTable.getType(k) == j))
            break; 
          i = DTMDefaultBaseIterators.this._nextsib(i);
        } 
      } 
      if (i == -1) {
        this._currentNode = -1;
        return -1;
      } 
      this._currentNode = DTMDefaultBaseIterators.this._nextsib(i);
      return returnNode(DTMDefaultBaseIterators.this.makeNodeHandle(i));
    }
  }
  
  public final class TypedDescendantIterator extends DescendantIterator {
    private final int _nodeType;
    
    public TypedDescendantIterator(int param1Int) {
      super(DTMDefaultBaseIterators.this);
      this._nodeType = param1Int;
    }
    
    public int next() {
      short s;
      if (this._startNode == -1)
        return -1; 
      int i = this._currentNode;
      do {
        s = DTMDefaultBaseIterators.this._type(++i);
        if (-1 == s || !isDescendant(i)) {
          this._currentNode = -1;
          return -1;
        } 
      } while (s != this._nodeType && DTMDefaultBaseIterators.this._exptype(i) != this._nodeType);
      this._currentNode = i;
      return returnNode(DTMDefaultBaseIterators.this.makeNodeHandle(i));
    }
  }
  
  public final class TypedFollowingIterator extends FollowingIterator {
    private final int _nodeType;
    
    public TypedFollowingIterator(int param1Int) {
      super(DTMDefaultBaseIterators.this);
      this._nodeType = param1Int;
    }
    
    public int next() {
      int i;
      do {
        i = this._currentNode;
        this._currentNode = this.m_traverser.next(this._startNode, this._currentNode);
      } while (i != -1 && DTMDefaultBaseIterators.this.getExpandedTypeID(i) != this._nodeType && DTMDefaultBaseIterators.this.getNodeType(i) != this._nodeType);
      return (i == -1) ? -1 : returnNode(i);
    }
  }
  
  public final class TypedFollowingSiblingIterator extends FollowingSiblingIterator {
    private final int _nodeType;
    
    public TypedFollowingSiblingIterator(int param1Int) {
      super(DTMDefaultBaseIterators.this);
      this._nodeType = param1Int;
    }
    
    public int next() {
      if (this._currentNode == -1)
        return -1; 
      int i = this._currentNode;
      int j = this._nodeType;
      if (j >= 14) {
        do {
          i = DTMDefaultBaseIterators.this._nextsib(i);
        } while (i != -1 && DTMDefaultBaseIterators.this._exptype(i) != j);
      } else {
        while ((i = DTMDefaultBaseIterators.this._nextsib(i)) != -1) {
          int k = DTMDefaultBaseIterators.this._exptype(i);
          if ((k < 14) ? (k == j) : (DTMDefaultBaseIterators.this.m_expandedNameTable.getType(k) == j))
            break; 
        } 
      } 
      this._currentNode = i;
      return (this._currentNode == -1) ? -1 : returnNode(DTMDefaultBaseIterators.this.makeNodeHandle(this._currentNode));
    }
  }
  
  public class TypedNamespaceIterator extends NamespaceIterator {
    private final int _nodeType;
    
    public TypedNamespaceIterator(int param1Int) {
      super(DTMDefaultBaseIterators.this);
      this._nodeType = param1Int;
    }
    
    public int next() {
      for (int i = this._currentNode; i != -1; i = DTMDefaultBaseIterators.this.getNextNamespaceNode(this._startNode, i, true)) {
        if (DTMDefaultBaseIterators.this.getExpandedTypeID(i) == this._nodeType || DTMDefaultBaseIterators.this.getNodeType(i) == this._nodeType || DTMDefaultBaseIterators.this.getNamespaceType(i) == this._nodeType) {
          this._currentNode = i;
          return returnNode(i);
        } 
      } 
      return this._currentNode = -1;
    }
  }
  
  public final class TypedPrecedingIterator extends PrecedingIterator {
    private final int _nodeType;
    
    public TypedPrecedingIterator(int param1Int) {
      super(DTMDefaultBaseIterators.this);
      this._nodeType = param1Int;
    }
    
    public int next() {
      int i = this._currentNode;
      int j = this._nodeType;
      if (j >= 14) {
        while (true) {
          i++;
          if (this._sp < 0) {
            i = -1;
            break;
          } 
          if (i >= this._stack[this._sp]) {
            if (--this._sp < 0) {
              i = -1;
              break;
            } 
            continue;
          } 
          if (DTMDefaultBaseIterators.this._exptype(i) == j)
            break; 
        } 
      } else {
        while (true) {
          i++;
          if (this._sp < 0) {
            i = -1;
            break;
          } 
          if (i >= this._stack[this._sp]) {
            if (--this._sp < 0) {
              i = -1;
              break;
            } 
            continue;
          } 
          int k = DTMDefaultBaseIterators.this._exptype(i);
          if ((k < 14) ? (k == j) : (DTMDefaultBaseIterators.this.m_expandedNameTable.getType(k) == j))
            break; 
        } 
      } 
      this._currentNode = i;
      return (i == -1) ? -1 : returnNode(DTMDefaultBaseIterators.this.makeNodeHandle(i));
    }
  }
  
  public final class TypedPrecedingSiblingIterator extends PrecedingSiblingIterator {
    private final int _nodeType;
    
    public TypedPrecedingSiblingIterator(int param1Int) {
      super(DTMDefaultBaseIterators.this);
      this._nodeType = param1Int;
    }
    
    public int next() {
      int i = this._currentNode;
      int j = this._nodeType;
      int k = this._startNodeID;
      if (j >= 14) {
        while (i != -1 && i != k && DTMDefaultBaseIterators.this._exptype(i) != j)
          i = DTMDefaultBaseIterators.this._nextsib(i); 
      } else {
        while (i != -1 && i != k) {
          int m = DTMDefaultBaseIterators.this._exptype(i);
          if ((m < 14) ? (m == j) : (DTMDefaultBaseIterators.this.m_expandedNameTable.getType(m) == j))
            break; 
          i = DTMDefaultBaseIterators.this._nextsib(i);
        } 
      } 
      if (i == -1 || i == this._startNodeID) {
        this._currentNode = -1;
        return -1;
      } 
      this._currentNode = DTMDefaultBaseIterators.this._nextsib(i);
      return returnNode(DTMDefaultBaseIterators.this.makeNodeHandle(i));
    }
  }
  
  public class TypedRootIterator extends RootIterator {
    private final int _nodeType;
    
    public TypedRootIterator(int param1Int) {
      super(DTMDefaultBaseIterators.this);
      this._nodeType = param1Int;
    }
    
    public int next() {
      if (this._startNode == this._currentNode)
        return -1; 
      int i = this._nodeType;
      int j = this._startNode;
      int k = DTMDefaultBaseIterators.this.getExpandedTypeID(j);
      this._currentNode = j;
      if (i >= 14) {
        if (i == k)
          return returnNode(j); 
      } else if (k < 14) {
        if (k == i)
          return returnNode(j); 
      } else if (DTMDefaultBaseIterators.this.m_expandedNameTable.getType(k) == i) {
        return returnNode(j);
      } 
      return -1;
    }
  }
  
  public final class TypedSingletonIterator extends SingletonIterator {
    private final int _nodeType;
    
    public TypedSingletonIterator(int param1Int) {
      super(DTMDefaultBaseIterators.this);
      this._nodeType = param1Int;
    }
    
    public int next() {
      int i = this._currentNode;
      int j = this._nodeType;
      this._currentNode = -1;
      if (j >= 14) {
        if (DTMDefaultBaseIterators.this.getExpandedTypeID(i) == j)
          return returnNode(i); 
      } else if (DTMDefaultBaseIterators.this.getNodeType(i) == j) {
        return returnNode(i);
      } 
      return -1;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\dtm\ref\DTMDefaultBaseIterators.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */