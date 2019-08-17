package jdk.internal.org.objectweb.asm.commons;

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.FieldVisitor;
import jdk.internal.org.objectweb.asm.MethodVisitor;

public class SerialVersionUIDAdder extends ClassVisitor {
  private boolean computeSVUID;
  
  private boolean hasSVUID;
  
  private int access;
  
  private String name;
  
  private String[] interfaces;
  
  private Collection<Item> svuidFields = new ArrayList();
  
  private boolean hasStaticInitializer;
  
  private Collection<Item> svuidConstructors = new ArrayList();
  
  private Collection<Item> svuidMethods = new ArrayList();
  
  public SerialVersionUIDAdder(ClassVisitor paramClassVisitor) {
    this(327680, paramClassVisitor);
    if (getClass() != SerialVersionUIDAdder.class)
      throw new IllegalStateException(); 
  }
  
  protected SerialVersionUIDAdder(int paramInt, ClassVisitor paramClassVisitor) { super(paramInt, paramClassVisitor); }
  
  public void visit(int paramInt1, int paramInt2, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString) {
    this.computeSVUID = ((paramInt2 & 0x200) == 0);
    if (this.computeSVUID) {
      this.name = paramString1;
      this.access = paramInt2;
      this.interfaces = new String[paramArrayOfString.length];
      System.arraycopy(paramArrayOfString, 0, this.interfaces, 0, paramArrayOfString.length);
    } 
    super.visit(paramInt1, paramInt2, paramString1, paramString2, paramString3, paramArrayOfString);
  }
  
  public MethodVisitor visitMethod(int paramInt, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString) {
    if (this.computeSVUID) {
      if ("<clinit>".equals(paramString1))
        this.hasStaticInitializer = true; 
      int i = paramInt & 0xD3F;
      if ((paramInt & 0x2) == 0)
        if ("<init>".equals(paramString1)) {
          this.svuidConstructors.add(new Item(paramString1, i, paramString2));
        } else if (!"<clinit>".equals(paramString1)) {
          this.svuidMethods.add(new Item(paramString1, i, paramString2));
        }  
    } 
    return super.visitMethod(paramInt, paramString1, paramString2, paramString3, paramArrayOfString);
  }
  
  public FieldVisitor visitField(int paramInt, String paramString1, String paramString2, String paramString3, Object paramObject) {
    if (this.computeSVUID) {
      if ("serialVersionUID".equals(paramString1)) {
        this.computeSVUID = false;
        this.hasSVUID = true;
      } 
      if ((paramInt & 0x2) == 0 || (paramInt & 0x88) == 0) {
        int i = paramInt & 0xDF;
        this.svuidFields.add(new Item(paramString1, i, paramString2));
      } 
    } 
    return super.visitField(paramInt, paramString1, paramString2, paramString3, paramObject);
  }
  
  public void visitInnerClass(String paramString1, String paramString2, String paramString3, int paramInt) {
    if (this.name != null && this.name.equals(paramString1))
      this.access = paramInt; 
    super.visitInnerClass(paramString1, paramString2, paramString3, paramInt);
  }
  
  public void visitEnd() {
    if (this.computeSVUID && !this.hasSVUID)
      try {
        addSVUID(computeSVUID());
      } catch (Throwable throwable) {
        throw new RuntimeException("Error while computing SVUID for " + this.name, throwable);
      }  
    super.visitEnd();
  }
  
  public boolean hasSVUID() { return this.hasSVUID; }
  
  protected void addSVUID(long paramLong) {
    FieldVisitor fieldVisitor = super.visitField(24, "serialVersionUID", "J", null, Long.valueOf(paramLong));
    if (fieldVisitor != null)
      fieldVisitor.visitEnd(); 
  }
  
  protected long computeSVUID() throws IOException {
    dataOutputStream = null;
    long l = 0L;
    try {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      dataOutputStream = new DataOutputStream(byteArrayOutputStream);
      dataOutputStream.writeUTF(this.name.replace('/', '.'));
      dataOutputStream.writeInt(this.access & 0x611);
      Arrays.sort(this.interfaces);
      for (byte b = 0; b < this.interfaces.length; b++)
        dataOutputStream.writeUTF(this.interfaces[b].replace('/', '.')); 
      writeItems(this.svuidFields, dataOutputStream, false);
      if (this.hasStaticInitializer) {
        dataOutputStream.writeUTF("<clinit>");
        dataOutputStream.writeInt(8);
        dataOutputStream.writeUTF("()V");
      } 
      writeItems(this.svuidConstructors, dataOutputStream, true);
      writeItems(this.svuidMethods, dataOutputStream, true);
      dataOutputStream.flush();
      byte[] arrayOfByte = computeSHAdigest(byteArrayOutputStream.toByteArray());
      for (int i = Math.min(arrayOfByte.length, 8) - 1; i >= 0; i--)
        l = l << 8 | (arrayOfByte[i] & 0xFF); 
    } finally {
      if (dataOutputStream != null)
        dataOutputStream.close(); 
    } 
    return l;
  }
  
  protected byte[] computeSHAdigest(byte[] paramArrayOfByte) {
    try {
      return MessageDigest.getInstance("SHA").digest(paramArrayOfByte);
    } catch (Exception exception) {
      throw new UnsupportedOperationException(exception.toString());
    } 
  }
  
  private static void writeItems(Collection<Item> paramCollection, DataOutput paramDataOutput, boolean paramBoolean) throws IOException {
    int i = paramCollection.size();
    Item[] arrayOfItem = (Item[])paramCollection.toArray(new Item[i]);
    Arrays.sort(arrayOfItem);
    for (byte b = 0; b < i; b++) {
      paramDataOutput.writeUTF((arrayOfItem[b]).name);
      paramDataOutput.writeInt((arrayOfItem[b]).access);
      paramDataOutput.writeUTF(paramBoolean ? (arrayOfItem[b]).desc.replace('/', '.') : (arrayOfItem[b]).desc);
    } 
  }
  
  private static class Item extends Object implements Comparable<Item> {
    final String name;
    
    final int access;
    
    final String desc;
    
    Item(String param1String1, int param1Int, String param1String2) {
      this.name = param1String1;
      this.access = param1Int;
      this.desc = param1String2;
    }
    
    public int compareTo(Item param1Item) {
      int i = this.name.compareTo(param1Item.name);
      if (i == 0)
        i = this.desc.compareTo(param1Item.desc); 
      return i;
    }
    
    public boolean equals(Object param1Object) { return (param1Object instanceof Item) ? ((compareTo((Item)param1Object) == 0)) : false; }
    
    public int hashCode() { return (this.name + this.desc).hashCode(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\org\objectweb\asm\commons\SerialVersionUIDAdder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */