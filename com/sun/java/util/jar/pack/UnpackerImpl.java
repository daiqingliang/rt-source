package com.sun.java.util.jar.pack;

import java.beans.PropertyChangeListener;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.SortedMap;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;

public class UnpackerImpl extends TLGlobals implements Pack200.Unpacker {
  Object _nunp;
  
  public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) { this.props.addListener(paramPropertyChangeListener); }
  
  public void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) { this.props.removeListener(paramPropertyChangeListener); }
  
  public SortedMap<String, String> properties() { return this.props; }
  
  public String toString() { return Utils.getVersionString(); }
  
  public void unpack(InputStream paramInputStream, JarOutputStream paramJarOutputStream) throws IOException {
    if (paramInputStream == null)
      throw new NullPointerException("null input"); 
    if (paramJarOutputStream == null)
      throw new NullPointerException("null output"); 
    assert Utils.currentInstance.get() == null;
    bool = !this.props.getBoolean("com.sun.java.util.jar.pack.default.timezone") ? 1 : 0;
    try {
      Utils.currentInstance.set(this);
      if (bool)
        Utils.changeDefaultTimeZoneToUtc(); 
      int i = this.props.getInteger("com.sun.java.util.jar.pack.verbose");
      BufferedInputStream bufferedInputStream = new BufferedInputStream(paramInputStream);
      if (Utils.isJarMagic(Utils.readMagic(bufferedInputStream))) {
        if (i > 0)
          Utils.log.info("Copying unpacked JAR file..."); 
        Utils.copyJarFile(new JarInputStream(bufferedInputStream), paramJarOutputStream);
      } else if (this.props.getBoolean("com.sun.java.util.jar.pack.disable.native")) {
        (new DoUnpack(null)).run(bufferedInputStream, paramJarOutputStream);
        bufferedInputStream.close();
        Utils.markJarFile(paramJarOutputStream);
      } else {
        try {
          (new NativeUnpack(this)).run(bufferedInputStream, paramJarOutputStream);
        } catch (UnsatisfiedLinkError|NoClassDefFoundError unsatisfiedLinkError) {
          (new DoUnpack(null)).run(bufferedInputStream, paramJarOutputStream);
        } 
        bufferedInputStream.close();
        Utils.markJarFile(paramJarOutputStream);
      } 
    } finally {
      this._nunp = null;
      Utils.currentInstance.set(null);
      if (bool)
        Utils.restoreDefaultTimeZone(); 
    } 
  }
  
  public void unpack(File paramFile, JarOutputStream paramJarOutputStream) throws IOException {
    if (paramFile == null)
      throw new NullPointerException("null input"); 
    if (paramJarOutputStream == null)
      throw new NullPointerException("null output"); 
    try (FileInputStream null = new FileInputStream(paramFile)) {
      unpack(fileInputStream, paramJarOutputStream);
    } 
    if (this.props.getBoolean("com.sun.java.util.jar.pack.unpack.remove.packfile"))
      paramFile.delete(); 
  }
  
  private class DoUnpack {
    final int verbose = UnpackerImpl.this.props.getInteger("com.sun.java.util.jar.pack.verbose");
    
    final Package pkg;
    
    final boolean keepModtime;
    
    final boolean keepDeflateHint;
    
    final int modtime;
    
    final boolean deflateHint;
    
    final CRC32 crc;
    
    final ByteArrayOutputStream bufOut;
    
    final OutputStream crcOut;
    
    private DoUnpack() {
      UnpackerImpl.this.props.setInteger("unpack.progress", 0);
      this.pkg = new Package();
      this.keepModtime = "keep".equals(UnpackerImpl.this.props.getProperty("com.sun.java.util.jar.pack.unpack.modification.time", "keep"));
      this.keepDeflateHint = "keep".equals(UnpackerImpl.this.props.getProperty("unpack.deflate.hint", "keep"));
      if (!this.keepModtime) {
        this.modtime = UnpackerImpl.this.props.getTime("com.sun.java.util.jar.pack.unpack.modification.time");
      } else {
        this.modtime = this.pkg.default_modtime;
      } 
      this.deflateHint = this.keepDeflateHint ? false : UnpackerImpl.this.props.getBoolean("unpack.deflate.hint");
      this.crc = new CRC32();
      this.bufOut = new ByteArrayOutputStream();
      this.crcOut = new CheckedOutputStream(this.bufOut, this.crc);
    }
    
    public void run(BufferedInputStream param1BufferedInputStream, JarOutputStream param1JarOutputStream) throws IOException {
      if (this.verbose > 0)
        UnpackerImpl.this.props.list(System.out); 
      for (byte b = 1;; b++) {
        unpackSegment(param1BufferedInputStream, param1JarOutputStream);
        if (!Utils.isPackMagic(Utils.readMagic(param1BufferedInputStream)))
          break; 
        if (this.verbose > 0)
          Utils.log.info("Finished segment #" + b); 
      } 
    }
    
    private void unpackSegment(InputStream param1InputStream, JarOutputStream param1JarOutputStream) throws IOException {
      UnpackerImpl.this.props.setProperty("unpack.progress", "0");
      (new PackageReader(this.pkg, param1InputStream)).read();
      if (UnpackerImpl.this.props.getBoolean("unpack.strip.debug"))
        this.pkg.stripAttributeKind("Debug"); 
      if (UnpackerImpl.this.props.getBoolean("unpack.strip.compile"))
        this.pkg.stripAttributeKind("Compile"); 
      UnpackerImpl.this.props.setProperty("unpack.progress", "50");
      this.pkg.ensureAllClassFiles();
      HashSet hashSet = new HashSet(this.pkg.getClasses());
      for (Package.File file : this.pkg.getFiles()) {
        String str = file.nameString;
        JarEntry jarEntry = new JarEntry(Utils.getJarEntryName(str));
        boolean bool1 = this.keepDeflateHint ? (((file.options & true) != 0 || (this.pkg.default_options & 0x20) != 0) ? 1 : 0) : this.deflateHint;
        boolean bool2 = !bool1 ? 1 : 0;
        if (bool2)
          this.crc.reset(); 
        this.bufOut.reset();
        if (file.isClassStub()) {
          Package.Class clazz = file.getStubClass();
          assert clazz != null;
          (new ClassWriter(clazz, bool2 ? this.crcOut : this.bufOut)).write();
          hashSet.remove(clazz);
        } else {
          file.writeTo(bool2 ? this.crcOut : this.bufOut);
        } 
        jarEntry.setMethod(bool1 ? 8 : 0);
        if (bool2) {
          if (this.verbose > 0)
            Utils.log.info("stored size=" + this.bufOut.size() + " and crc=" + this.crc.getValue()); 
          jarEntry.setMethod(0);
          jarEntry.setSize(this.bufOut.size());
          jarEntry.setCrc(this.crc.getValue());
        } 
        if (this.keepModtime) {
          jarEntry.setTime(file.modtime);
          jarEntry.setTime(file.modtime * 1000L);
        } else {
          jarEntry.setTime(this.modtime * 1000L);
        } 
        param1JarOutputStream.putNextEntry(jarEntry);
        this.bufOut.writeTo(param1JarOutputStream);
        param1JarOutputStream.closeEntry();
        if (this.verbose > 0)
          Utils.log.info("Writing " + Utils.zeString(jarEntry)); 
      } 
      assert hashSet.isEmpty();
      UnpackerImpl.this.props.setProperty("unpack.progress", "100");
      this.pkg.reset();
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jav\\util\jar\pack\UnpackerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */