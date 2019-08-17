package com.sun.org.apache.xml.internal.dtm.ref.sax2dtm;

import com.sun.org.apache.xml.internal.dtm.DTMManager;
import com.sun.org.apache.xml.internal.dtm.DTMWSFilter;
import com.sun.org.apache.xml.internal.utils.IntStack;
import com.sun.org.apache.xml.internal.utils.XMLStringFactory;
import java.util.Vector;
import javax.xml.transform.Source;
import org.xml.sax.SAXException;

public class SAX2RTFDTM extends SAX2DTM {
  private static final boolean DEBUG = false;
  
  private int m_currentDocumentNode = -1;
  
  IntStack mark_size = new IntStack();
  
  IntStack mark_data_size = new IntStack();
  
  IntStack mark_char_size = new IntStack();
  
  IntStack mark_doq_size = new IntStack();
  
  IntStack mark_nsdeclset_size = new IntStack();
  
  IntStack mark_nsdeclelem_size = new IntStack();
  
  int m_emptyNodeCount = this.m_size;
  
  int m_emptyNSDeclSetCount = (this.m_namespaceDeclSets == null) ? 0 : this.m_namespaceDeclSets.size();
  
  int m_emptyNSDeclSetElemsCount = (this.m_namespaceDeclSetElements == null) ? 0 : this.m_namespaceDeclSetElements.size();
  
  int m_emptyDataCount = this.m_data.size();
  
  int m_emptyCharsCount = this.m_chars.size();
  
  int m_emptyDataQNCount = this.m_dataOrQName.size();
  
  public SAX2RTFDTM(DTMManager paramDTMManager, Source paramSource, int paramInt, DTMWSFilter paramDTMWSFilter, XMLStringFactory paramXMLStringFactory, boolean paramBoolean) { super(paramDTMManager, paramSource, paramInt, paramDTMWSFilter, paramXMLStringFactory, paramBoolean); }
  
  public int getDocument() { return makeNodeHandle(this.m_currentDocumentNode); }
  
  public int getDocumentRoot(int paramInt) {
    for (int i = makeNodeIdentity(paramInt); i != -1; i = _parent(i)) {
      if (_type(i) == 9)
        return makeNodeHandle(i); 
    } 
    return -1;
  }
  
  protected int _documentRoot(int paramInt) {
    if (paramInt == -1)
      return -1; 
    for (int i = _parent(paramInt); i != -1; i = _parent(paramInt))
      paramInt = i; 
    return paramInt;
  }
  
  public void startDocument() throws SAXException {
    this.m_endDocumentOccured = false;
    this.m_prefixMappings = new Vector();
    this.m_contextIndexes = new IntStack();
    this.m_parents = new IntStack();
    this.m_currentDocumentNode = this.m_size;
    super.startDocument();
  }
  
  public void endDocument() throws SAXException {
    charactersFlush();
    this.m_nextsib.setElementAt(-1, this.m_currentDocumentNode);
    if (this.m_firstch.elementAt(this.m_currentDocumentNode) == -2)
      this.m_firstch.setElementAt(-1, this.m_currentDocumentNode); 
    if (-1 != this.m_previous)
      this.m_nextsib.setElementAt(-1, this.m_previous); 
    this.m_parents = null;
    this.m_prefixMappings = null;
    this.m_contextIndexes = null;
    this.m_currentDocumentNode = -1;
    this.m_endDocumentOccured = true;
  }
  
  public void pushRewindMark() throws SAXException {
    if (this.m_indexing || this.m_elemIndexes != null)
      throw new NullPointerException("Coding error; Don't try to mark/rewind an indexed DTM"); 
    this.mark_size.push(this.m_size);
    this.mark_nsdeclset_size.push((this.m_namespaceDeclSets == null) ? 0 : this.m_namespaceDeclSets.size());
    this.mark_nsdeclelem_size.push((this.m_namespaceDeclSetElements == null) ? 0 : this.m_namespaceDeclSetElements.size());
    this.mark_data_size.push(this.m_data.size());
    this.mark_char_size.push(this.m_chars.size());
    this.mark_doq_size.push(this.m_dataOrQName.size());
  }
  
  public boolean popRewindMark() {
    boolean bool = this.mark_size.empty();
    this.m_size = bool ? this.m_emptyNodeCount : this.mark_size.pop();
    this.m_exptype.setSize(this.m_size);
    this.m_firstch.setSize(this.m_size);
    this.m_nextsib.setSize(this.m_size);
    this.m_prevsib.setSize(this.m_size);
    this.m_parent.setSize(this.m_size);
    this.m_elemIndexes = (int[][][])null;
    int i = bool ? this.m_emptyNSDeclSetCount : this.mark_nsdeclset_size.pop();
    if (this.m_namespaceDeclSets != null)
      this.m_namespaceDeclSets.setSize(i); 
    int j = bool ? this.m_emptyNSDeclSetElemsCount : this.mark_nsdeclelem_size.pop();
    if (this.m_namespaceDeclSetElements != null)
      this.m_namespaceDeclSetElements.setSize(j); 
    this.m_data.setSize(bool ? this.m_emptyDataCount : this.mark_data_size.pop());
    this.m_chars.setLength(bool ? this.m_emptyCharsCount : this.mark_char_size.pop());
    this.m_dataOrQName.setSize(bool ? this.m_emptyDataQNCount : this.mark_doq_size.pop());
    return (this.m_size == 0);
  }
  
  public boolean isTreeIncomplete() { return !this.m_endDocumentOccured; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\dtm\ref\sax2dtm\SAX2RTFDTM.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */