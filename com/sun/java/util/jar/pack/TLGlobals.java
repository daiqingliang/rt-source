package com.sun.java.util.jar.pack;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

class TLGlobals {
  final PropMap props = new PropMap();
  
  private final Map<String, ConstantPool.Utf8Entry> utf8Entries = new HashMap();
  
  private final Map<String, ConstantPool.ClassEntry> classEntries = new HashMap();
  
  private final Map<Object, ConstantPool.LiteralEntry> literalEntries = new HashMap();
  
  private final Map<String, ConstantPool.SignatureEntry> signatureEntries = new HashMap();
  
  private final Map<String, ConstantPool.DescriptorEntry> descriptorEntries = new HashMap();
  
  private final Map<String, ConstantPool.MemberEntry> memberEntries = new HashMap();
  
  private final Map<String, ConstantPool.MethodHandleEntry> methodHandleEntries = new HashMap();
  
  private final Map<String, ConstantPool.MethodTypeEntry> methodTypeEntries = new HashMap();
  
  private final Map<String, ConstantPool.InvokeDynamicEntry> invokeDynamicEntries = new HashMap();
  
  private final Map<String, ConstantPool.BootstrapMethodEntry> bootstrapMethodEntries = new HashMap();
  
  SortedMap<String, String> getPropMap() { return this.props; }
  
  Map<String, ConstantPool.Utf8Entry> getUtf8Entries() { return this.utf8Entries; }
  
  Map<String, ConstantPool.ClassEntry> getClassEntries() { return this.classEntries; }
  
  Map<Object, ConstantPool.LiteralEntry> getLiteralEntries() { return this.literalEntries; }
  
  Map<String, ConstantPool.DescriptorEntry> getDescriptorEntries() { return this.descriptorEntries; }
  
  Map<String, ConstantPool.SignatureEntry> getSignatureEntries() { return this.signatureEntries; }
  
  Map<String, ConstantPool.MemberEntry> getMemberEntries() { return this.memberEntries; }
  
  Map<String, ConstantPool.MethodHandleEntry> getMethodHandleEntries() { return this.methodHandleEntries; }
  
  Map<String, ConstantPool.MethodTypeEntry> getMethodTypeEntries() { return this.methodTypeEntries; }
  
  Map<String, ConstantPool.InvokeDynamicEntry> getInvokeDynamicEntries() { return this.invokeDynamicEntries; }
  
  Map<String, ConstantPool.BootstrapMethodEntry> getBootstrapMethodEntries() { return this.bootstrapMethodEntries; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jav\\util\jar\pack\TLGlobals.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */