package com.sun.xml.internal.stream.buffer;

public class AbstractCreator extends AbstractCreatorProcessor {
  protected MutableXMLStreamBuffer _buffer;
  
  public void setXMLStreamBuffer(MutableXMLStreamBuffer paramMutableXMLStreamBuffer) {
    if (paramMutableXMLStreamBuffer == null)
      throw new NullPointerException("buffer cannot be null"); 
    setBuffer(paramMutableXMLStreamBuffer);
  }
  
  public MutableXMLStreamBuffer getXMLStreamBuffer() { return this._buffer; }
  
  protected final void createBuffer() { setBuffer(new MutableXMLStreamBuffer()); }
  
  protected final void increaseTreeCount() { this._buffer.treeCount++; }
  
  protected final void setBuffer(MutableXMLStreamBuffer paramMutableXMLStreamBuffer) {
    this._buffer = paramMutableXMLStreamBuffer;
    this._currentStructureFragment = this._buffer.getStructure();
    this._structure = (byte[])this._currentStructureFragment.getArray();
    this._structurePtr = 0;
    this._currentStructureStringFragment = this._buffer.getStructureStrings();
    this._structureStrings = (String[])this._currentStructureStringFragment.getArray();
    this._structureStringsPtr = 0;
    this._currentContentCharactersBufferFragment = this._buffer.getContentCharactersBuffer();
    this._contentCharactersBuffer = (char[])this._currentContentCharactersBufferFragment.getArray();
    this._contentCharactersBufferPtr = 0;
    this._currentContentObjectFragment = this._buffer.getContentObjects();
    this._contentObjects = (Object[])this._currentContentObjectFragment.getArray();
    this._contentObjectsPtr = 0;
  }
  
  protected final void setHasInternedStrings(boolean paramBoolean) { this._buffer.setHasInternedStrings(paramBoolean); }
  
  protected final void storeStructure(int paramInt) {
    this._structure[this._structurePtr++] = (byte)paramInt;
    if (this._structurePtr == this._structure.length)
      resizeStructure(); 
  }
  
  protected final void resizeStructure() {
    this._structurePtr = 0;
    if (this._currentStructureFragment.getNext() != null) {
      this._currentStructureFragment = this._currentStructureFragment.getNext();
      this._structure = (byte[])this._currentStructureFragment.getArray();
    } else {
      this._structure = new byte[this._structure.length];
      this._currentStructureFragment = new FragmentedArray(this._structure, this._currentStructureFragment);
    } 
  }
  
  protected final void storeStructureString(String paramString) {
    this._structureStrings[this._structureStringsPtr++] = paramString;
    if (this._structureStringsPtr == this._structureStrings.length)
      resizeStructureStrings(); 
  }
  
  protected final void resizeStructureStrings() {
    this._structureStringsPtr = 0;
    if (this._currentStructureStringFragment.getNext() != null) {
      this._currentStructureStringFragment = this._currentStructureStringFragment.getNext();
      this._structureStrings = (String[])this._currentStructureStringFragment.getArray();
    } else {
      this._structureStrings = new String[this._structureStrings.length];
      this._currentStructureStringFragment = new FragmentedArray(this._structureStrings, this._currentStructureStringFragment);
    } 
  }
  
  protected final void storeContentString(String paramString) { storeContentObject(paramString); }
  
  protected final void storeContentCharacters(int paramInt1, char[] paramArrayOfChar, int paramInt2, int paramInt3) {
    if (this._contentCharactersBufferPtr + paramInt3 >= this._contentCharactersBuffer.length) {
      if (paramInt3 >= 512) {
        storeStructure(paramInt1 | 0x4);
        storeContentCharactersCopy(paramArrayOfChar, paramInt2, paramInt3);
        return;
      } 
      resizeContentCharacters();
    } 
    if (paramInt3 < 256) {
      storeStructure(paramInt1);
      storeStructure(paramInt3);
      System.arraycopy(paramArrayOfChar, paramInt2, this._contentCharactersBuffer, this._contentCharactersBufferPtr, paramInt3);
      this._contentCharactersBufferPtr += paramInt3;
    } else if (paramInt3 < 65536) {
      storeStructure(paramInt1 | true);
      storeStructure(paramInt3 >> 8);
      storeStructure(paramInt3 & 0xFF);
      System.arraycopy(paramArrayOfChar, paramInt2, this._contentCharactersBuffer, this._contentCharactersBufferPtr, paramInt3);
      this._contentCharactersBufferPtr += paramInt3;
    } else {
      storeStructure(paramInt1 | 0x4);
      storeContentCharactersCopy(paramArrayOfChar, paramInt2, paramInt3);
    } 
  }
  
  protected final void resizeContentCharacters() {
    this._contentCharactersBufferPtr = 0;
    if (this._currentContentCharactersBufferFragment.getNext() != null) {
      this._currentContentCharactersBufferFragment = this._currentContentCharactersBufferFragment.getNext();
      this._contentCharactersBuffer = (char[])this._currentContentCharactersBufferFragment.getArray();
    } else {
      this._contentCharactersBuffer = new char[this._contentCharactersBuffer.length];
      this._currentContentCharactersBufferFragment = new FragmentedArray(this._contentCharactersBuffer, this._currentContentCharactersBufferFragment);
    } 
  }
  
  protected final void storeContentCharactersCopy(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    char[] arrayOfChar = new char[paramInt2];
    System.arraycopy(paramArrayOfChar, paramInt1, arrayOfChar, 0, paramInt2);
    storeContentObject(arrayOfChar);
  }
  
  protected final Object peekAtContentObject() { return this._contentObjects[this._contentObjectsPtr]; }
  
  protected final void storeContentObject(Object paramObject) {
    this._contentObjects[this._contentObjectsPtr++] = paramObject;
    if (this._contentObjectsPtr == this._contentObjects.length)
      resizeContentObjects(); 
  }
  
  protected final void resizeContentObjects() {
    this._contentObjectsPtr = 0;
    if (this._currentContentObjectFragment.getNext() != null) {
      this._currentContentObjectFragment = this._currentContentObjectFragment.getNext();
      this._contentObjects = (Object[])this._currentContentObjectFragment.getArray();
    } else {
      this._contentObjects = new Object[this._contentObjects.length];
      this._currentContentObjectFragment = new FragmentedArray(this._contentObjects, this._currentContentObjectFragment);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\stream\buffer\AbstractCreator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */