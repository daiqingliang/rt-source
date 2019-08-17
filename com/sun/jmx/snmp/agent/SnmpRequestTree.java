package com.sun.jmx.snmp.agent;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.snmp.SnmpEngine;
import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpPdu;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpVarBind;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Vector;
import java.util.logging.Level;

final class SnmpRequestTree {
  private Hashtable<Object, Handler> hashtable = null;
  
  private SnmpMibRequest request = null;
  
  private int version = 0;
  
  private boolean creationflag = false;
  
  private boolean getnextflag = false;
  
  private int type = 0;
  
  private boolean setreqflag = false;
  
  SnmpRequestTree(SnmpMibRequest paramSnmpMibRequest, boolean paramBoolean, int paramInt) {
    this.request = paramSnmpMibRequest;
    this.version = paramSnmpMibRequest.getVersion();
    this.creationflag = paramBoolean;
    this.hashtable = new Hashtable();
    setPduType(paramInt);
  }
  
  public static int mapSetException(int paramInt1, int paramInt2) throws SnmpStatusException {
    int i = paramInt1;
    if (paramInt2 == 0)
      return i; 
    int j = i;
    if (i == 225) {
      j = 17;
    } else if (i == 224) {
      j = 17;
    } 
    return j;
  }
  
  public static int mapGetException(int paramInt1, int paramInt2) throws SnmpStatusException {
    int i = paramInt1;
    if (paramInt2 == 0)
      return i; 
    int j = i;
    if (i == 225) {
      j = i;
    } else if (i == 224) {
      j = i;
    } else if (i == 6) {
      j = 224;
    } else if (i == 18) {
      j = 224;
    } else if (i >= 7 && i <= 12) {
      j = 224;
    } else if (i == 4) {
      j = 224;
    } else if (i != 16 && i != 5) {
      j = 225;
    } 
    return j;
  }
  
  public Object getUserData() { return this.request.getUserData(); }
  
  public boolean isCreationAllowed() { return this.creationflag; }
  
  public boolean isSetRequest() { return this.setreqflag; }
  
  public int getVersion() { return this.version; }
  
  public int getRequestPduVersion() { return this.request.getRequestPduVersion(); }
  
  public SnmpMibNode getMetaNode(Handler paramHandler) { return paramHandler.meta; }
  
  public int getOidDepth(Handler paramHandler) { return paramHandler.depth; }
  
  public Enumeration<SnmpMibSubRequest> getSubRequests(Handler paramHandler) { return new Enum(this, paramHandler); }
  
  public Enumeration<Handler> getHandlers() { return this.hashtable.elements(); }
  
  public void add(SnmpMibNode paramSnmpMibNode, int paramInt, SnmpVarBind paramSnmpVarBind) throws SnmpStatusException { registerNode(paramSnmpMibNode, paramInt, null, paramSnmpVarBind, false, null); }
  
  public void add(SnmpMibNode paramSnmpMibNode, int paramInt, SnmpOid paramSnmpOid, SnmpVarBind paramSnmpVarBind, boolean paramBoolean) throws SnmpStatusException { registerNode(paramSnmpMibNode, paramInt, paramSnmpOid, paramSnmpVarBind, paramBoolean, null); }
  
  public void add(SnmpMibNode paramSnmpMibNode, int paramInt, SnmpOid paramSnmpOid, SnmpVarBind paramSnmpVarBind1, boolean paramBoolean, SnmpVarBind paramSnmpVarBind2) throws SnmpStatusException { registerNode(paramSnmpMibNode, paramInt, paramSnmpOid, paramSnmpVarBind1, paramBoolean, paramSnmpVarBind2); }
  
  void setPduType(int paramInt) {
    this.type = paramInt;
    this.setreqflag = (paramInt == 253 || paramInt == 163);
  }
  
  void setGetNextFlag() { this.getnextflag = true; }
  
  void switchCreationFlag(boolean paramBoolean) { this.creationflag = paramBoolean; }
  
  SnmpMibSubRequest getSubRequest(Handler paramHandler) { return (paramHandler == null) ? null : new SnmpMibSubRequestImpl(this.request, paramHandler.getSubList(), null, false, this.getnextflag, null); }
  
  SnmpMibSubRequest getSubRequest(Handler paramHandler, SnmpOid paramSnmpOid) {
    if (paramHandler == null)
      return null; 
    int i = paramHandler.getEntryPos(paramSnmpOid);
    return (i == -1) ? null : new SnmpMibSubRequestImpl(this.request, paramHandler.getEntrySubList(i), paramHandler.getEntryOid(i), paramHandler.isNewEntry(i), this.getnextflag, paramHandler.getRowStatusVarBind(i));
  }
  
  SnmpMibSubRequest getSubRequest(Handler paramHandler, int paramInt) { return (paramHandler == null) ? null : new SnmpMibSubRequestImpl(this.request, paramHandler.getEntrySubList(paramInt), paramHandler.getEntryOid(paramInt), paramHandler.isNewEntry(paramInt), this.getnextflag, paramHandler.getRowStatusVarBind(paramInt)); }
  
  private void put(Object paramObject, Handler paramHandler) {
    if (paramHandler == null)
      return; 
    if (paramObject == null)
      return; 
    if (this.hashtable == null)
      this.hashtable = new Hashtable(); 
    this.hashtable.put(paramObject, paramHandler);
  }
  
  private Handler get(Object paramObject) { return (paramObject == null) ? null : ((this.hashtable == null) ? null : (Handler)this.hashtable.get(paramObject)); }
  
  private static int findOid(SnmpOid[] paramArrayOfSnmpOid, int paramInt, SnmpOid paramSnmpOid) {
    int i = paramInt;
    int j = 0;
    int k = i - 1;
    int m;
    for (m = j + (k - j) / 2; j <= k; m = j + (k - j) / 2) {
      SnmpOid snmpOid = paramArrayOfSnmpOid[m];
      int n = paramSnmpOid.compareTo(snmpOid);
      if (n == 0)
        return m; 
      if (paramSnmpOid.equals(snmpOid))
        return m; 
      if (n > 0) {
        j = m + 1;
      } else {
        k = m - 1;
      } 
    } 
    return -1;
  }
  
  private static int getInsertionPoint(SnmpOid[] paramArrayOfSnmpOid, int paramInt, SnmpOid paramSnmpOid) {
    SnmpOid[] arrayOfSnmpOid = paramArrayOfSnmpOid;
    int i = paramInt;
    int j = 0;
    int k = i - 1;
    int m;
    for (m = j + (k - j) / 2; j <= k; m = j + (k - j) / 2) {
      SnmpOid snmpOid = arrayOfSnmpOid[m];
      int n = paramSnmpOid.compareTo(snmpOid);
      if (n == 0)
        return m; 
      if (n > 0) {
        j = m + 1;
      } else {
        k = m - 1;
      } 
    } 
    return m;
  }
  
  private void registerNode(SnmpMibNode paramSnmpMibNode, int paramInt, SnmpOid paramSnmpOid, SnmpVarBind paramSnmpVarBind1, boolean paramBoolean, SnmpVarBind paramSnmpVarBind2) throws SnmpStatusException {
    if (paramSnmpMibNode == null) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpRequestTree.class.getName(), "registerNode", "meta-node is null!");
      return;
    } 
    if (paramSnmpVarBind1 == null) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpRequestTree.class.getName(), "registerNode", "varbind is null!");
      return;
    } 
    SnmpMibNode snmpMibNode = paramSnmpMibNode;
    Handler handler = get(snmpMibNode);
    if (handler == null) {
      handler = new Handler(this.type);
      handler.meta = paramSnmpMibNode;
      handler.depth = paramInt;
      put(snmpMibNode, handler);
    } 
    if (paramSnmpOid == null) {
      handler.addVarbind(paramSnmpVarBind1);
    } else {
      handler.addVarbind(paramSnmpVarBind1, paramSnmpOid, paramBoolean, paramSnmpVarBind2);
    } 
  }
  
  static final class Enum extends Object implements Enumeration<SnmpMibSubRequest> {
    private final SnmpRequestTree.Handler handler;
    
    private final SnmpRequestTree hlist;
    
    private int entry = 0;
    
    private int iter = 0;
    
    private int size = 0;
    
    Enum(SnmpRequestTree param1SnmpRequestTree, SnmpRequestTree.Handler param1Handler) {
      this.handler = param1Handler;
      this.hlist = param1SnmpRequestTree;
      this.size = param1Handler.getSubReqCount();
    }
    
    public boolean hasMoreElements() { return (this.iter < this.size); }
    
    public SnmpMibSubRequest nextElement() throws NoSuchElementException {
      if (this.iter == 0 && this.handler.sublist != null) {
        this.iter++;
        return this.hlist.getSubRequest(this.handler);
      } 
      this.iter++;
      if (this.iter > this.size)
        throw new NoSuchElementException(); 
      SnmpMibSubRequest snmpMibSubRequest = this.hlist.getSubRequest(this.handler, this.entry);
      this.entry++;
      return snmpMibSubRequest;
    }
  }
  
  static final class Handler {
    SnmpMibNode meta;
    
    int depth;
    
    Vector<SnmpVarBind> sublist;
    
    SnmpOid[] entryoids = null;
    
    Vector<SnmpVarBind>[] entrylists = null;
    
    boolean[] isentrynew = null;
    
    SnmpVarBind[] rowstatus = null;
    
    int entrycount = 0;
    
    int entrysize = 0;
    
    final int type;
    
    private static final int Delta = 10;
    
    public Handler(int param1Int) { this.type = param1Int; }
    
    public void addVarbind(SnmpVarBind param1SnmpVarBind) {
      if (this.sublist == null)
        this.sublist = new Vector(); 
      this.sublist.addElement(param1SnmpVarBind);
    }
    
    void add(int param1Int, SnmpOid param1SnmpOid, Vector<SnmpVarBind> param1Vector, boolean param1Boolean, SnmpVarBind param1SnmpVarBind) {
      if (this.entryoids == null) {
        this.entryoids = new SnmpOid[10];
        this.entrylists = (Vector[])new Vector[10];
        this.isentrynew = new boolean[10];
        this.rowstatus = new SnmpVarBind[10];
        this.entrysize = 10;
        param1Int = 0;
      } else if (param1Int >= this.entrysize || this.entrycount == this.entrysize) {
        SnmpOid[] arrayOfSnmpOid = this.entryoids;
        Vector[] arrayOfVector = this.entrylists;
        boolean[] arrayOfBoolean = this.isentrynew;
        SnmpVarBind[] arrayOfSnmpVarBind = this.rowstatus;
        this.entrysize += 10;
        this.entryoids = new SnmpOid[this.entrysize];
        this.entrylists = (Vector[])new Vector[this.entrysize];
        this.isentrynew = new boolean[this.entrysize];
        this.rowstatus = new SnmpVarBind[this.entrysize];
        if (param1Int > this.entrycount)
          param1Int = this.entrycount; 
        if (param1Int < 0)
          param1Int = 0; 
        int i = param1Int;
        int j = this.entrycount - param1Int;
        if (i > 0) {
          System.arraycopy(arrayOfSnmpOid, 0, this.entryoids, 0, i);
          System.arraycopy(arrayOfVector, 0, this.entrylists, 0, i);
          System.arraycopy(arrayOfBoolean, 0, this.isentrynew, 0, i);
          System.arraycopy(arrayOfSnmpVarBind, 0, this.rowstatus, 0, i);
        } 
        if (j > 0) {
          int k = i + 1;
          System.arraycopy(arrayOfSnmpOid, i, this.entryoids, k, j);
          System.arraycopy(arrayOfVector, i, this.entrylists, k, j);
          System.arraycopy(arrayOfBoolean, i, this.isentrynew, k, j);
          System.arraycopy(arrayOfSnmpVarBind, i, this.rowstatus, k, j);
        } 
      } else if (param1Int < this.entrycount) {
        int i = param1Int + 1;
        int j = this.entrycount - param1Int;
        System.arraycopy(this.entryoids, param1Int, this.entryoids, i, j);
        System.arraycopy(this.entrylists, param1Int, this.entrylists, i, j);
        System.arraycopy(this.isentrynew, param1Int, this.isentrynew, i, j);
        System.arraycopy(this.rowstatus, param1Int, this.rowstatus, i, j);
      } 
      this.entryoids[param1Int] = param1SnmpOid;
      this.entrylists[param1Int] = param1Vector;
      this.isentrynew[param1Int] = param1Boolean;
      this.rowstatus[param1Int] = param1SnmpVarBind;
      this.entrycount++;
    }
    
    public void addVarbind(SnmpVarBind param1SnmpVarBind1, SnmpOid param1SnmpOid, boolean param1Boolean, SnmpVarBind param1SnmpVarBind2) throws SnmpStatusException {
      Vector vector = null;
      SnmpVarBind snmpVarBind = param1SnmpVarBind2;
      if (this.entryoids == null) {
        vector = new Vector();
        add(0, param1SnmpOid, vector, param1Boolean, snmpVarBind);
      } else {
        int i = SnmpRequestTree.getInsertionPoint(this.entryoids, this.entrycount, param1SnmpOid);
        if (i > -1 && i < this.entrycount && param1SnmpOid.compareTo(this.entryoids[i]) == 0) {
          vector = this.entrylists[i];
          snmpVarBind = this.rowstatus[i];
        } else {
          vector = new Vector();
          add(i, param1SnmpOid, vector, param1Boolean, snmpVarBind);
        } 
        if (param1SnmpVarBind2 != null) {
          if (snmpVarBind != null && snmpVarBind != param1SnmpVarBind2 && (this.type == 253 || this.type == 163))
            throw new SnmpStatusException(12); 
          this.rowstatus[i] = param1SnmpVarBind2;
        } 
      } 
      if (param1SnmpVarBind2 != param1SnmpVarBind1)
        vector.addElement(param1SnmpVarBind1); 
    }
    
    public int getSubReqCount() {
      int i = 0;
      if (this.sublist != null)
        i++; 
      if (this.entryoids != null)
        i += this.entrycount; 
      return i;
    }
    
    public Vector<SnmpVarBind> getSubList() { return this.sublist; }
    
    public int getEntryPos(SnmpOid param1SnmpOid) { return SnmpRequestTree.findOid(this.entryoids, this.entrycount, param1SnmpOid); }
    
    public SnmpOid getEntryOid(int param1Int) { return (this.entryoids == null) ? null : ((param1Int == -1 || param1Int >= this.entrycount) ? null : this.entryoids[param1Int]); }
    
    public boolean isNewEntry(int param1Int) { return (this.entryoids == null) ? false : ((param1Int == -1 || param1Int >= this.entrycount) ? false : this.isentrynew[param1Int]); }
    
    public SnmpVarBind getRowStatusVarBind(int param1Int) { return (this.entryoids == null) ? null : ((param1Int == -1 || param1Int >= this.entrycount) ? null : this.rowstatus[param1Int]); }
    
    public Vector<SnmpVarBind> getEntrySubList(int param1Int) { return (this.entrylists == null) ? null : ((param1Int == -1 || param1Int >= this.entrycount) ? null : this.entrylists[param1Int]); }
    
    public Iterator<SnmpOid> getEntryOids() { return (this.entryoids == null) ? null : Arrays.asList(this.entryoids).iterator(); }
    
    public int getEntryCount() { return (this.entryoids == null) ? 0 : this.entrycount; }
  }
  
  static final class SnmpMibSubRequestImpl implements SnmpMibSubRequest {
    private final Vector<SnmpVarBind> varbinds;
    
    private final SnmpMibRequest global;
    
    private final int version;
    
    private final boolean isnew;
    
    private final SnmpOid entryoid;
    
    private final boolean getnextflag;
    
    private final SnmpVarBind statusvb;
    
    SnmpMibSubRequestImpl(SnmpMibRequest param1SnmpMibRequest, Vector<SnmpVarBind> param1Vector, SnmpOid param1SnmpOid, boolean param1Boolean1, boolean param1Boolean2, SnmpVarBind param1SnmpVarBind) {
      this.global = param1SnmpMibRequest;
      this.varbinds = param1Vector;
      this.version = param1SnmpMibRequest.getVersion();
      this.entryoid = param1SnmpOid;
      this.isnew = param1Boolean1;
      this.getnextflag = param1Boolean2;
      this.statusvb = param1SnmpVarBind;
    }
    
    public Enumeration<SnmpVarBind> getElements() { return this.varbinds.elements(); }
    
    public Vector<SnmpVarBind> getSubList() { return this.varbinds; }
    
    public final int getSize() { return (this.varbinds == null) ? 0 : this.varbinds.size(); }
    
    public void addVarBind(SnmpVarBind param1SnmpVarBind) {
      this.varbinds.addElement(param1SnmpVarBind);
      this.global.addVarBind(param1SnmpVarBind);
    }
    
    public boolean isNewEntry() { return this.isnew; }
    
    public SnmpOid getEntryOid() { return this.entryoid; }
    
    public int getVarIndex(SnmpVarBind param1SnmpVarBind) { return (param1SnmpVarBind == null) ? 0 : this.global.getVarIndex(param1SnmpVarBind); }
    
    public Object getUserData() { return this.global.getUserData(); }
    
    public void registerGetException(SnmpVarBind param1SnmpVarBind, SnmpStatusException param1SnmpStatusException) throws SnmpStatusException {
      if (this.version == 0)
        throw new SnmpStatusException(param1SnmpStatusException, getVarIndex(param1SnmpVarBind) + 1); 
      if (param1SnmpVarBind == null)
        throw param1SnmpStatusException; 
      if (this.getnextflag) {
        param1SnmpVarBind.value = SnmpVarBind.endOfMibView;
        return;
      } 
      int i = SnmpRequestTree.mapGetException(param1SnmpStatusException.getStatus(), this.version);
      if (i == 225) {
        param1SnmpVarBind.value = SnmpVarBind.noSuchObject;
      } else if (i == 224) {
        param1SnmpVarBind.value = SnmpVarBind.noSuchInstance;
      } else {
        throw new SnmpStatusException(i, getVarIndex(param1SnmpVarBind) + 1);
      } 
    }
    
    public void registerSetException(SnmpVarBind param1SnmpVarBind, SnmpStatusException param1SnmpStatusException) throws SnmpStatusException {
      if (this.version == 0)
        throw new SnmpStatusException(param1SnmpStatusException, getVarIndex(param1SnmpVarBind) + 1); 
      throw new SnmpStatusException(15, getVarIndex(param1SnmpVarBind) + 1);
    }
    
    public void registerCheckException(SnmpVarBind param1SnmpVarBind, SnmpStatusException param1SnmpStatusException) throws SnmpStatusException {
      int i = param1SnmpStatusException.getStatus();
      int j = SnmpRequestTree.mapSetException(i, this.version);
      if (i != j)
        throw new SnmpStatusException(j, getVarIndex(param1SnmpVarBind) + 1); 
      throw new SnmpStatusException(param1SnmpStatusException, getVarIndex(param1SnmpVarBind) + 1);
    }
    
    public int getVersion() { return this.version; }
    
    public SnmpVarBind getRowStatusVarBind() { return this.statusvb; }
    
    public SnmpPdu getPdu() { return this.global.getPdu(); }
    
    public int getRequestPduVersion() { return this.global.getRequestPduVersion(); }
    
    public SnmpEngine getEngine() { return this.global.getEngine(); }
    
    public String getPrincipal() { return this.global.getPrincipal(); }
    
    public int getSecurityLevel() { return this.global.getSecurityLevel(); }
    
    public int getSecurityModel() { return this.global.getSecurityModel(); }
    
    public byte[] getContextName() { return this.global.getContextName(); }
    
    public byte[] getAccessContextName() { return this.global.getAccessContextName(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\agent\SnmpRequestTree.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */