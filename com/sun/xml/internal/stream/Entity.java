package com.sun.xml.internal.stream;

import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.xml.internal.stream.util.BufferAllocator;
import com.sun.xml.internal.stream.util.ThreadLocalBufferAllocator;
import java.io.InputStream;
import java.io.Reader;

public abstract class Entity {
  public String name;
  
  public boolean inExternalSubset;
  
  public Entity() { clear(); }
  
  public Entity(String paramString, boolean paramBoolean) {
    this.name = paramString;
    this.inExternalSubset = paramBoolean;
  }
  
  public boolean isEntityDeclInExternalSubset() { return this.inExternalSubset; }
  
  public abstract boolean isExternal();
  
  public abstract boolean isUnparsed();
  
  public void clear() {
    this.name = null;
    this.inExternalSubset = false;
  }
  
  public void setValues(Entity paramEntity) {
    this.name = paramEntity.name;
    this.inExternalSubset = paramEntity.inExternalSubset;
  }
  
  public static class ExternalEntity extends Entity {
    public XMLResourceIdentifier entityLocation;
    
    public String notation;
    
    public ExternalEntity() { clear(); }
    
    public ExternalEntity(String param1String1, XMLResourceIdentifier param1XMLResourceIdentifier, String param1String2, boolean param1Boolean) {
      super(param1String1, param1Boolean);
      this.entityLocation = param1XMLResourceIdentifier;
      this.notation = param1String2;
    }
    
    public final boolean isExternal() { return true; }
    
    public final boolean isUnparsed() { return (this.notation != null); }
    
    public void clear() {
      super.clear();
      this.entityLocation = null;
      this.notation = null;
    }
    
    public void setValues(Entity param1Entity) {
      super.setValues(param1Entity);
      this.entityLocation = null;
      this.notation = null;
    }
    
    public void setValues(ExternalEntity param1ExternalEntity) {
      super.setValues(param1ExternalEntity);
      this.entityLocation = param1ExternalEntity.entityLocation;
      this.notation = param1ExternalEntity.notation;
    }
  }
  
  public static class InternalEntity extends Entity {
    public String text;
    
    public InternalEntity() { clear(); }
    
    public InternalEntity(String param1String1, String param1String2, boolean param1Boolean) {
      super(param1String1, param1Boolean);
      this.text = param1String2;
    }
    
    public final boolean isExternal() { return false; }
    
    public final boolean isUnparsed() { return false; }
    
    public void clear() {
      super.clear();
      this.text = null;
    }
    
    public void setValues(Entity param1Entity) {
      super.setValues(param1Entity);
      this.text = null;
    }
    
    public void setValues(InternalEntity param1InternalEntity) {
      super.setValues(param1InternalEntity);
      this.text = param1InternalEntity.text;
    }
  }
  
  public static class ScannedEntity extends Entity {
    public static final int DEFAULT_BUFFER_SIZE = 8192;
    
    public int fBufferSize = 8192;
    
    public static final int DEFAULT_XMLDECL_BUFFER_SIZE = 28;
    
    public static final int DEFAULT_INTERNAL_BUFFER_SIZE = 1024;
    
    public InputStream stream;
    
    public Reader reader;
    
    public XMLResourceIdentifier entityLocation;
    
    public String encoding;
    
    public boolean literal;
    
    public boolean isExternal;
    
    public String version;
    
    public char[] ch = null;
    
    public int position;
    
    public int count;
    
    public int lineNumber = 1;
    
    public int columnNumber = 1;
    
    boolean declaredEncoding = false;
    
    boolean externallySpecifiedEncoding = false;
    
    public String xmlVersion = "1.0";
    
    public int fTotalCountTillLastLoad;
    
    public int fLastCount;
    
    public int baseCharOffset;
    
    public int startPosition;
    
    public boolean mayReadChunks;
    
    public boolean xmlDeclChunkRead = false;
    
    public boolean isGE = false;
    
    public String getEncodingName() { return this.encoding; }
    
    public String getEntityVersion() { return this.version; }
    
    public void setEntityVersion(String param1String) { this.version = param1String; }
    
    public Reader getEntityReader() { return this.reader; }
    
    public InputStream getEntityInputStream() { return this.stream; }
    
    public ScannedEntity(boolean param1Boolean1, String param1String1, XMLResourceIdentifier param1XMLResourceIdentifier, InputStream param1InputStream, Reader param1Reader, String param1String2, boolean param1Boolean2, boolean param1Boolean3, boolean param1Boolean4) {
      this.isGE = param1Boolean1;
      this.name = param1String1;
      this.entityLocation = param1XMLResourceIdentifier;
      this.stream = param1InputStream;
      this.reader = param1Reader;
      this.encoding = param1String2;
      this.literal = param1Boolean2;
      this.mayReadChunks = param1Boolean3;
      this.isExternal = param1Boolean4;
      int i = param1Boolean4 ? this.fBufferSize : 1024;
      BufferAllocator bufferAllocator = ThreadLocalBufferAllocator.getBufferAllocator();
      this.ch = bufferAllocator.getCharBuffer(i);
      if (this.ch == null)
        this.ch = new char[i]; 
    }
    
    public void close() {
      BufferAllocator bufferAllocator = ThreadLocalBufferAllocator.getBufferAllocator();
      bufferAllocator.returnCharBuffer(this.ch);
      this.ch = null;
      this.reader.close();
    }
    
    public boolean isEncodingExternallySpecified() { return this.externallySpecifiedEncoding; }
    
    public void setEncodingExternallySpecified(boolean param1Boolean) { this.externallySpecifiedEncoding = param1Boolean; }
    
    public boolean isDeclaredEncoding() { return this.declaredEncoding; }
    
    public void setDeclaredEncoding(boolean param1Boolean) { this.declaredEncoding = param1Boolean; }
    
    public final boolean isExternal() { return this.isExternal; }
    
    public final boolean isUnparsed() { return false; }
    
    public String toString() {
      StringBuffer stringBuffer = new StringBuffer();
      stringBuffer.append("name=\"" + this.name + '"');
      stringBuffer.append(",ch=" + new String(this.ch));
      stringBuffer.append(",position=" + this.position);
      stringBuffer.append(",count=" + this.count);
      return stringBuffer.toString();
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\stream\Entity.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */