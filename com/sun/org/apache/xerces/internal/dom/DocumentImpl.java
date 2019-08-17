package com.sun.org.apache.xerces.internal.dom;

import com.sun.org.apache.xerces.internal.dom.events.EventImpl;
import com.sun.org.apache.xerces.internal.dom.events.MutationEventImpl;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.events.DocumentEvent;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventException;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.ranges.DocumentRange;
import org.w3c.dom.ranges.Range;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;
import org.w3c.dom.traversal.TreeWalker;

public class DocumentImpl extends CoreDocumentImpl implements DocumentTraversal, DocumentEvent, DocumentRange {
  static final long serialVersionUID = 515687835542616694L;
  
  protected List<NodeIterator> iterators;
  
  protected List<Range> ranges;
  
  protected Map<NodeImpl, List<LEntry>> eventListeners;
  
  protected boolean mutationEvents = false;
  
  private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("iterators", Vector.class), new ObjectStreamField("ranges", Vector.class), new ObjectStreamField("eventListeners", Hashtable.class), new ObjectStreamField("mutationEvents", boolean.class) };
  
  EnclosingAttr savedEnclosingAttr;
  
  public DocumentImpl() {}
  
  public DocumentImpl(boolean paramBoolean) { super(paramBoolean); }
  
  public DocumentImpl(DocumentType paramDocumentType) { super(paramDocumentType); }
  
  public DocumentImpl(DocumentType paramDocumentType, boolean paramBoolean) { super(paramDocumentType, paramBoolean); }
  
  public Node cloneNode(boolean paramBoolean) {
    DocumentImpl documentImpl = new DocumentImpl();
    callUserDataHandlers(this, documentImpl, (short)1);
    cloneNode(documentImpl, paramBoolean);
    documentImpl.mutationEvents = this.mutationEvents;
    return documentImpl;
  }
  
  public DOMImplementation getImplementation() { return DOMImplementationImpl.getDOMImplementation(); }
  
  public NodeIterator createNodeIterator(Node paramNode, short paramShort, NodeFilter paramNodeFilter) { return createNodeIterator(paramNode, paramShort, paramNodeFilter, true); }
  
  public NodeIterator createNodeIterator(Node paramNode, int paramInt, NodeFilter paramNodeFilter, boolean paramBoolean) {
    if (paramNode == null) {
      String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null);
      throw new DOMException((short)9, str);
    } 
    NodeIteratorImpl nodeIteratorImpl = new NodeIteratorImpl(this, paramNode, paramInt, paramNodeFilter, paramBoolean);
    if (this.iterators == null)
      this.iterators = new ArrayList(); 
    this.iterators.add(nodeIteratorImpl);
    return nodeIteratorImpl;
  }
  
  public TreeWalker createTreeWalker(Node paramNode, short paramShort, NodeFilter paramNodeFilter) { return createTreeWalker(paramNode, paramShort, paramNodeFilter, true); }
  
  public TreeWalker createTreeWalker(Node paramNode, int paramInt, NodeFilter paramNodeFilter, boolean paramBoolean) {
    if (paramNode == null) {
      String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null);
      throw new DOMException((short)9, str);
    } 
    return new TreeWalkerImpl(paramNode, paramInt, paramNodeFilter, paramBoolean);
  }
  
  void removeNodeIterator(NodeIterator paramNodeIterator) {
    if (paramNodeIterator == null)
      return; 
    if (this.iterators == null)
      return; 
    this.iterators.remove(paramNodeIterator);
  }
  
  public Range createRange() {
    if (this.ranges == null)
      this.ranges = new ArrayList(); 
    RangeImpl rangeImpl = new RangeImpl(this);
    this.ranges.add(rangeImpl);
    return rangeImpl;
  }
  
  void removeRange(Range paramRange) {
    if (paramRange == null)
      return; 
    if (this.ranges == null)
      return; 
    this.ranges.remove(paramRange);
  }
  
  void replacedText(NodeImpl paramNodeImpl) {
    if (this.ranges != null) {
      int i = this.ranges.size();
      for (byte b = 0; b != i; b++)
        ((RangeImpl)this.ranges.get(b)).receiveReplacedText(paramNodeImpl); 
    } 
  }
  
  void deletedText(NodeImpl paramNodeImpl, int paramInt1, int paramInt2) {
    if (this.ranges != null) {
      int i = this.ranges.size();
      for (byte b = 0; b != i; b++)
        ((RangeImpl)this.ranges.get(b)).receiveDeletedText(paramNodeImpl, paramInt1, paramInt2); 
    } 
  }
  
  void insertedText(NodeImpl paramNodeImpl, int paramInt1, int paramInt2) {
    if (this.ranges != null) {
      int i = this.ranges.size();
      for (byte b = 0; b != i; b++)
        ((RangeImpl)this.ranges.get(b)).receiveInsertedText(paramNodeImpl, paramInt1, paramInt2); 
    } 
  }
  
  void splitData(Node paramNode1, Node paramNode2, int paramInt) {
    if (this.ranges != null) {
      int i = this.ranges.size();
      for (byte b = 0; b != i; b++)
        ((RangeImpl)this.ranges.get(b)).receiveSplitData(paramNode1, paramNode2, paramInt); 
    } 
  }
  
  public Event createEvent(String paramString) throws DOMException {
    if (paramString.equalsIgnoreCase("Events") || "Event".equals(paramString))
      return new EventImpl(); 
    if (paramString.equalsIgnoreCase("MutationEvents") || "MutationEvent".equals(paramString))
      return new MutationEventImpl(); 
    String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null);
    throw new DOMException((short)9, str);
  }
  
  void setMutationEvents(boolean paramBoolean) { this.mutationEvents = paramBoolean; }
  
  boolean getMutationEvents() { return this.mutationEvents; }
  
  private void setEventListeners(NodeImpl paramNodeImpl, List<LEntry> paramList) {
    if (this.eventListeners == null)
      this.eventListeners = new HashMap(); 
    if (paramList == null) {
      this.eventListeners.remove(paramNodeImpl);
      if (this.eventListeners.isEmpty())
        this.mutationEvents = false; 
    } else {
      this.eventListeners.put(paramNodeImpl, paramList);
      this.mutationEvents = true;
    } 
  }
  
  private List<LEntry> getEventListeners(NodeImpl paramNodeImpl) { return (this.eventListeners == null) ? null : (List)this.eventListeners.get(paramNodeImpl); }
  
  protected void addEventListener(NodeImpl paramNodeImpl, String paramString, EventListener paramEventListener, boolean paramBoolean) {
    if (paramString == null || paramString.equals("") || paramEventListener == null)
      return; 
    removeEventListener(paramNodeImpl, paramString, paramEventListener, paramBoolean);
    List list = getEventListeners(paramNodeImpl);
    if (list == null) {
      list = new ArrayList();
      setEventListeners(paramNodeImpl, list);
    } 
    list.add(new LEntry(paramString, paramEventListener, paramBoolean));
    LCount lCount = LCount.lookup(paramString);
    if (paramBoolean) {
      lCount.captures++;
      lCount.total++;
    } else {
      lCount.bubbles++;
      lCount.total++;
    } 
  }
  
  protected void removeEventListener(NodeImpl paramNodeImpl, String paramString, EventListener paramEventListener, boolean paramBoolean) {
    if (paramString == null || paramString.equals("") || paramEventListener == null)
      return; 
    List list = getEventListeners(paramNodeImpl);
    if (list == null)
      return; 
    for (int i = list.size() - 1; i >= 0; i--) {
      LEntry lEntry = (LEntry)list.get(i);
      if (lEntry.useCapture == paramBoolean && lEntry.listener == paramEventListener && lEntry.type.equals(paramString)) {
        list.remove(i);
        if (list.isEmpty())
          setEventListeners(paramNodeImpl, null); 
        LCount lCount = LCount.lookup(paramString);
        if (paramBoolean) {
          lCount.captures--;
          lCount.total--;
          break;
        } 
        lCount.bubbles--;
        lCount.total--;
        break;
      } 
    } 
  }
  
  protected void copyEventListeners(NodeImpl paramNodeImpl1, NodeImpl paramNodeImpl2) {
    List list = getEventListeners(paramNodeImpl1);
    if (list == null)
      return; 
    setEventListeners(paramNodeImpl2, new ArrayList(list));
  }
  
  protected boolean dispatchEvent(NodeImpl paramNodeImpl, Event paramEvent) {
    if (paramEvent == null)
      return false; 
    EventImpl eventImpl = (EventImpl)paramEvent;
    if (!eventImpl.initialized || eventImpl.type == null || eventImpl.type.equals("")) {
      String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "UNSPECIFIED_EVENT_TYPE_ERR", null);
      throw new EventException((short)0, str);
    } 
    LCount lCount = LCount.lookup(eventImpl.getType());
    if (lCount.total == 0)
      return eventImpl.preventDefault; 
    eventImpl.target = paramNodeImpl;
    eventImpl.stopPropagation = false;
    eventImpl.preventDefault = false;
    ArrayList arrayList = new ArrayList(10);
    NodeImpl nodeImpl = paramNodeImpl;
    for (Node node = nodeImpl.getParentNode(); node != null; node = node.getParentNode()) {
      arrayList.add(node);
      Node node1 = node;
    } 
    if (lCount.captures > 0) {
      eventImpl.eventPhase = 1;
      for (int i = arrayList.size() - 1; i >= 0 && !eventImpl.stopPropagation; i--) {
        NodeImpl nodeImpl1 = (NodeImpl)arrayList.get(i);
        eventImpl.currentTarget = nodeImpl1;
        List list = getEventListeners(nodeImpl1);
        if (list != null) {
          List list1 = (List)((ArrayList)list).clone();
          int j = list1.size();
          for (byte b = 0; b < j; b++) {
            LEntry lEntry = (LEntry)list1.get(b);
            if (lEntry.useCapture && lEntry.type.equals(eventImpl.type) && list.contains(lEntry))
              try {
                lEntry.listener.handleEvent(eventImpl);
              } catch (Exception exception) {} 
          } 
        } 
      } 
    } 
    if (lCount.bubbles > 0) {
      eventImpl.eventPhase = 2;
      eventImpl.currentTarget = paramNodeImpl;
      List list = getEventListeners(paramNodeImpl);
      if (!eventImpl.stopPropagation && list != null) {
        List list1 = (List)((ArrayList)list).clone();
        int i = list1.size();
        for (byte b = 0; b < i; b++) {
          LEntry lEntry = (LEntry)list1.get(b);
          if (!lEntry.useCapture && lEntry.type.equals(eventImpl.type) && list.contains(lEntry))
            try {
              lEntry.listener.handleEvent(eventImpl);
            } catch (Exception exception) {} 
        } 
      } 
      if (eventImpl.bubbles) {
        eventImpl.eventPhase = 3;
        int i = arrayList.size();
        for (byte b = 0; b < i && !eventImpl.stopPropagation; b++) {
          NodeImpl nodeImpl1 = (NodeImpl)arrayList.get(b);
          eventImpl.currentTarget = nodeImpl1;
          list = getEventListeners(nodeImpl1);
          if (list != null) {
            List list1 = (List)((ArrayList)list).clone();
            int j = list1.size();
            for (byte b1 = 0; b1 < j; b1++) {
              LEntry lEntry = (LEntry)list1.get(b1);
              if (!lEntry.useCapture && lEntry.type.equals(eventImpl.type) && list.contains(lEntry))
                try {
                  lEntry.listener.handleEvent(eventImpl);
                } catch (Exception exception) {} 
            } 
          } 
        } 
      } 
    } 
    if (lCount.defaults <= 0 || !eventImpl.cancelable || !eventImpl.preventDefault);
    return eventImpl.preventDefault;
  }
  
  protected void dispatchEventToSubtree(Node paramNode, Event paramEvent) {
    ((NodeImpl)paramNode).dispatchEvent(paramEvent);
    if (paramNode.getNodeType() == 1) {
      NamedNodeMap namedNodeMap = paramNode.getAttributes();
      for (int i = namedNodeMap.getLength() - 1; i >= 0; i--)
        dispatchingEventToSubtree(namedNodeMap.item(i), paramEvent); 
    } 
    dispatchingEventToSubtree(paramNode.getFirstChild(), paramEvent);
  }
  
  protected void dispatchingEventToSubtree(Node paramNode, Event paramEvent) {
    if (paramNode == null)
      return; 
    ((NodeImpl)paramNode).dispatchEvent(paramEvent);
    if (paramNode.getNodeType() == 1) {
      NamedNodeMap namedNodeMap = paramNode.getAttributes();
      for (int i = namedNodeMap.getLength() - 1; i >= 0; i--)
        dispatchingEventToSubtree(namedNodeMap.item(i), paramEvent); 
    } 
    dispatchingEventToSubtree(paramNode.getFirstChild(), paramEvent);
    dispatchingEventToSubtree(paramNode.getNextSibling(), paramEvent);
  }
  
  protected void dispatchAggregateEvents(NodeImpl paramNodeImpl, EnclosingAttr paramEnclosingAttr) {
    if (paramEnclosingAttr != null) {
      dispatchAggregateEvents(paramNodeImpl, paramEnclosingAttr.node, paramEnclosingAttr.oldvalue, (short)1);
    } else {
      dispatchAggregateEvents(paramNodeImpl, null, null, (short)0);
    } 
  }
  
  protected void dispatchAggregateEvents(NodeImpl paramNodeImpl, AttrImpl paramAttrImpl, String paramString, short paramShort) {
    NodeImpl nodeImpl = null;
    if (paramAttrImpl != null) {
      LCount lCount1 = LCount.lookup("DOMAttrModified");
      nodeImpl = (NodeImpl)paramAttrImpl.getOwnerElement();
      if (lCount1.total > 0 && nodeImpl != null) {
        MutationEventImpl mutationEventImpl = new MutationEventImpl();
        mutationEventImpl.initMutationEvent("DOMAttrModified", true, false, paramAttrImpl, paramString, paramAttrImpl.getNodeValue(), paramAttrImpl.getNodeName(), paramShort);
        nodeImpl.dispatchEvent(mutationEventImpl);
      } 
    } 
    LCount lCount = LCount.lookup("DOMSubtreeModified");
    if (lCount.total > 0) {
      MutationEventImpl mutationEventImpl = new MutationEventImpl();
      mutationEventImpl.initMutationEvent("DOMSubtreeModified", true, false, null, null, null, null, (short)0);
      if (paramAttrImpl != null) {
        dispatchEvent(paramAttrImpl, mutationEventImpl);
        if (nodeImpl != null)
          dispatchEvent(nodeImpl, mutationEventImpl); 
      } else {
        dispatchEvent(paramNodeImpl, mutationEventImpl);
      } 
    } 
  }
  
  protected void saveEnclosingAttr(NodeImpl paramNodeImpl) {
    this.savedEnclosingAttr = null;
    LCount lCount = LCount.lookup("DOMAttrModified");
    if (lCount.total > 0) {
      NodeImpl nodeImpl = paramNodeImpl;
      while (true) {
        if (nodeImpl == null)
          return; 
        short s = nodeImpl.getNodeType();
        if (s == 2) {
          EnclosingAttr enclosingAttr = new EnclosingAttr();
          enclosingAttr.node = (AttrImpl)nodeImpl;
          enclosingAttr.oldvalue = enclosingAttr.node.getNodeValue();
          this.savedEnclosingAttr = enclosingAttr;
          return;
        } 
        if (s == 5) {
          nodeImpl = nodeImpl.parentNode();
          continue;
        } 
        if (s == 3) {
          nodeImpl = nodeImpl.parentNode();
          continue;
        } 
        break;
      } 
      return;
    } 
  }
  
  void modifyingCharacterData(NodeImpl paramNodeImpl, boolean paramBoolean) {
    if (this.mutationEvents && !paramBoolean)
      saveEnclosingAttr(paramNodeImpl); 
  }
  
  void modifiedCharacterData(NodeImpl paramNodeImpl, String paramString1, String paramString2, boolean paramBoolean) {
    if (this.mutationEvents && !paramBoolean) {
      LCount lCount = LCount.lookup("DOMCharacterDataModified");
      if (lCount.total > 0) {
        MutationEventImpl mutationEventImpl = new MutationEventImpl();
        mutationEventImpl.initMutationEvent("DOMCharacterDataModified", true, false, null, paramString1, paramString2, null, (short)0);
        dispatchEvent(paramNodeImpl, mutationEventImpl);
      } 
      dispatchAggregateEvents(paramNodeImpl, this.savedEnclosingAttr);
    } 
  }
  
  void replacedCharacterData(NodeImpl paramNodeImpl, String paramString1, String paramString2) { modifiedCharacterData(paramNodeImpl, paramString1, paramString2, false); }
  
  void insertingNode(NodeImpl paramNodeImpl, boolean paramBoolean) {
    if (this.mutationEvents && !paramBoolean)
      saveEnclosingAttr(paramNodeImpl); 
  }
  
  void insertedNode(NodeImpl paramNodeImpl1, NodeImpl paramNodeImpl2, boolean paramBoolean) {
    if (this.mutationEvents) {
      LCount lCount = LCount.lookup("DOMNodeInserted");
      if (lCount.total > 0) {
        MutationEventImpl mutationEventImpl = new MutationEventImpl();
        mutationEventImpl.initMutationEvent("DOMNodeInserted", true, false, paramNodeImpl1, null, null, null, (short)0);
        dispatchEvent(paramNodeImpl2, mutationEventImpl);
      } 
      lCount = LCount.lookup("DOMNodeInsertedIntoDocument");
      if (lCount.total > 0) {
        NodeImpl nodeImpl = paramNodeImpl1;
        if (this.savedEnclosingAttr != null)
          nodeImpl = (NodeImpl)this.savedEnclosingAttr.node.getOwnerElement(); 
        if (nodeImpl != null) {
          for (NodeImpl nodeImpl1 = nodeImpl; nodeImpl1 != null; nodeImpl1 = nodeImpl1.parentNode()) {
            nodeImpl = nodeImpl1;
            if (nodeImpl1.getNodeType() == 2) {
              nodeImpl1 = (NodeImpl)((AttrImpl)nodeImpl1).getOwnerElement();
              continue;
            } 
          } 
          if (nodeImpl.getNodeType() == 9) {
            MutationEventImpl mutationEventImpl = new MutationEventImpl();
            mutationEventImpl.initMutationEvent("DOMNodeInsertedIntoDocument", false, false, null, null, null, null, (short)0);
            dispatchEventToSubtree(paramNodeImpl2, mutationEventImpl);
          } 
        } 
      } 
      if (!paramBoolean)
        dispatchAggregateEvents(paramNodeImpl1, this.savedEnclosingAttr); 
    } 
    if (this.ranges != null) {
      int i = this.ranges.size();
      for (byte b = 0; b != i; b++)
        ((RangeImpl)this.ranges.get(b)).insertedNodeFromDOM(paramNodeImpl2); 
    } 
  }
  
  void removingNode(NodeImpl paramNodeImpl1, NodeImpl paramNodeImpl2, boolean paramBoolean) {
    if (this.iterators != null) {
      int i = this.iterators.size();
      for (byte b = 0; b != i; b++)
        ((NodeIteratorImpl)this.iterators.get(b)).removeNode(paramNodeImpl2); 
    } 
    if (this.ranges != null) {
      int i = this.ranges.size();
      for (byte b = 0; b != i; b++)
        ((RangeImpl)this.ranges.get(b)).removeNode(paramNodeImpl2); 
    } 
    if (this.mutationEvents) {
      if (!paramBoolean)
        saveEnclosingAttr(paramNodeImpl1); 
      LCount lCount = LCount.lookup("DOMNodeRemoved");
      if (lCount.total > 0) {
        MutationEventImpl mutationEventImpl = new MutationEventImpl();
        mutationEventImpl.initMutationEvent("DOMNodeRemoved", true, false, paramNodeImpl1, null, null, null, (short)0);
        dispatchEvent(paramNodeImpl2, mutationEventImpl);
      } 
      lCount = LCount.lookup("DOMNodeRemovedFromDocument");
      if (lCount.total > 0) {
        NodeImpl nodeImpl = this;
        if (this.savedEnclosingAttr != null)
          nodeImpl = (NodeImpl)this.savedEnclosingAttr.node.getOwnerElement(); 
        if (nodeImpl != null) {
          for (NodeImpl nodeImpl1 = nodeImpl.parentNode(); nodeImpl1 != null; nodeImpl1 = nodeImpl1.parentNode())
            nodeImpl = nodeImpl1; 
          if (nodeImpl.getNodeType() == 9) {
            MutationEventImpl mutationEventImpl = new MutationEventImpl();
            mutationEventImpl.initMutationEvent("DOMNodeRemovedFromDocument", false, false, null, null, null, null, (short)0);
            dispatchEventToSubtree(paramNodeImpl2, mutationEventImpl);
          } 
        } 
      } 
    } 
  }
  
  void removedNode(NodeImpl paramNodeImpl, boolean paramBoolean) {
    if (this.mutationEvents && !paramBoolean)
      dispatchAggregateEvents(paramNodeImpl, this.savedEnclosingAttr); 
  }
  
  void replacingNode(NodeImpl paramNodeImpl) {
    if (this.mutationEvents)
      saveEnclosingAttr(paramNodeImpl); 
  }
  
  void replacingData(NodeImpl paramNodeImpl) {
    if (this.mutationEvents)
      saveEnclosingAttr(paramNodeImpl); 
  }
  
  void replacedNode(NodeImpl paramNodeImpl) {
    if (this.mutationEvents)
      dispatchAggregateEvents(paramNodeImpl, this.savedEnclosingAttr); 
  }
  
  void modifiedAttrValue(AttrImpl paramAttrImpl, String paramString) {
    if (this.mutationEvents)
      dispatchAggregateEvents(paramAttrImpl, paramAttrImpl, paramString, (short)1); 
  }
  
  void setAttrNode(AttrImpl paramAttrImpl1, AttrImpl paramAttrImpl2) {
    if (this.mutationEvents)
      if (paramAttrImpl2 == null) {
        dispatchAggregateEvents(paramAttrImpl1.ownerNode, paramAttrImpl1, null, (short)2);
      } else {
        dispatchAggregateEvents(paramAttrImpl1.ownerNode, paramAttrImpl1, paramAttrImpl2.getNodeValue(), (short)1);
      }  
  }
  
  void removedAttrNode(AttrImpl paramAttrImpl, NodeImpl paramNodeImpl, String paramString) {
    if (this.mutationEvents) {
      LCount lCount = LCount.lookup("DOMAttrModified");
      if (lCount.total > 0) {
        MutationEventImpl mutationEventImpl = new MutationEventImpl();
        mutationEventImpl.initMutationEvent("DOMAttrModified", true, false, paramAttrImpl, paramAttrImpl.getNodeValue(), null, paramString, (short)3);
        dispatchEvent(paramNodeImpl, mutationEventImpl);
      } 
      dispatchAggregateEvents(paramNodeImpl, null, null, (short)0);
    } 
  }
  
  void renamedAttrNode(Attr paramAttr1, Attr paramAttr2) {}
  
  void renamedElement(Element paramElement1, Element paramElement2) {}
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    Vector vector1 = (this.iterators == null) ? null : new Vector(this.iterators);
    Vector vector2 = (this.ranges == null) ? null : new Vector(this.ranges);
    Hashtable hashtable = null;
    if (this.eventListeners != null) {
      hashtable = new Hashtable();
      for (Map.Entry entry : this.eventListeners.entrySet())
        hashtable.put(entry.getKey(), new Vector((Collection)entry.getValue())); 
    } 
    ObjectOutputStream.PutField putField = paramObjectOutputStream.putFields();
    putField.put("iterators", vector1);
    putField.put("ranges", vector2);
    putField.put("eventListeners", hashtable);
    putField.put("mutationEvents", this.mutationEvents);
    paramObjectOutputStream.writeFields();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    ObjectInputStream.GetField getField = paramObjectInputStream.readFields();
    Vector vector1 = (Vector)getField.get("iterators", null);
    Vector vector2 = (Vector)getField.get("ranges", null);
    Hashtable hashtable = (Hashtable)getField.get("eventListeners", null);
    this.mutationEvents = getField.get("mutationEvents", false);
    if (vector1 != null)
      this.iterators = new ArrayList(vector1); 
    if (vector2 != null)
      this.ranges = new ArrayList(vector2); 
    if (hashtable != null) {
      this.eventListeners = new HashMap();
      for (Map.Entry entry : hashtable.entrySet())
        this.eventListeners.put(entry.getKey(), new ArrayList((Collection)entry.getValue())); 
    } 
  }
  
  class EnclosingAttr implements Serializable {
    private static final long serialVersionUID = 5208387723391647216L;
    
    AttrImpl node;
    
    String oldvalue;
  }
  
  class LEntry implements Serializable {
    private static final long serialVersionUID = -8426757059492421631L;
    
    String type;
    
    EventListener listener;
    
    boolean useCapture;
    
    LEntry(String param1String, EventListener param1EventListener, boolean param1Boolean) {
      this.type = param1String;
      this.listener = param1EventListener;
      this.useCapture = param1Boolean;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\dom\DocumentImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */