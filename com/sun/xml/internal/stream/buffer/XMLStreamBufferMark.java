package com.sun.xml.internal.stream.buffer;

import java.util.Map;

public class XMLStreamBufferMark extends XMLStreamBuffer {
  public XMLStreamBufferMark(Map<String, String> paramMap, AbstractCreatorProcessor paramAbstractCreatorProcessor) {
    if (paramMap != null)
      this._inscopeNamespaces = paramMap; 
    this._structure = paramAbstractCreatorProcessor._currentStructureFragment;
    this._structurePtr = paramAbstractCreatorProcessor._structurePtr;
    this._structureStrings = paramAbstractCreatorProcessor._currentStructureStringFragment;
    this._structureStringsPtr = paramAbstractCreatorProcessor._structureStringsPtr;
    this._contentCharactersBuffer = paramAbstractCreatorProcessor._currentContentCharactersBufferFragment;
    this._contentCharactersBufferPtr = paramAbstractCreatorProcessor._contentCharactersBufferPtr;
    this._contentObjects = paramAbstractCreatorProcessor._currentContentObjectFragment;
    this._contentObjectsPtr = paramAbstractCreatorProcessor._contentObjectsPtr;
    this.treeCount = 1;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\stream\buffer\XMLStreamBufferMark.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */