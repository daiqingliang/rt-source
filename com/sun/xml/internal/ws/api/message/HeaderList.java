package com.sun.xml.internal.ws.api.message;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.binding.SOAPBindingImpl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.xml.namespace.QName;

public class HeaderList extends ArrayList<Header> implements MessageHeaders {
  private static final long serialVersionUID = -6358045781349627237L;
  
  private int understoodBits;
  
  private BitSet moreUnderstoodBits = null;
  
  private SOAPVersion soapVersion;
  
  @Deprecated
  public HeaderList() {}
  
  public HeaderList(SOAPVersion paramSOAPVersion) { this.soapVersion = paramSOAPVersion; }
  
  public HeaderList(HeaderList paramHeaderList) {
    super(paramHeaderList);
    this.understoodBits = paramHeaderList.understoodBits;
    if (paramHeaderList.moreUnderstoodBits != null)
      this.moreUnderstoodBits = (BitSet)paramHeaderList.moreUnderstoodBits.clone(); 
  }
  
  public HeaderList(MessageHeaders paramMessageHeaders) {
    super(paramMessageHeaders.asList());
    if (paramMessageHeaders instanceof HeaderList) {
      HeaderList headerList = (HeaderList)paramMessageHeaders;
      this.understoodBits = headerList.understoodBits;
      if (headerList.moreUnderstoodBits != null)
        this.moreUnderstoodBits = (BitSet)headerList.moreUnderstoodBits.clone(); 
    } else {
      Set set = paramMessageHeaders.getUnderstoodHeaders();
      if (set != null)
        for (QName qName : set)
          understood(qName);  
    } 
  }
  
  public int size() { return super.size(); }
  
  public boolean hasHeaders() { return !isEmpty(); }
  
  @Deprecated
  public void addAll(Header... paramVarArgs) { addAll(Arrays.asList(paramVarArgs)); }
  
  public Header get(int paramInt) { return (Header)super.get(paramInt); }
  
  public void understood(int paramInt) {
    if (paramInt >= size())
      throw new ArrayIndexOutOfBoundsException(paramInt); 
    if (paramInt < 32) {
      this.understoodBits |= 1 << paramInt;
    } else {
      if (this.moreUnderstoodBits == null)
        this.moreUnderstoodBits = new BitSet(); 
      this.moreUnderstoodBits.set(paramInt - 32);
    } 
  }
  
  public boolean isUnderstood(int paramInt) {
    if (paramInt >= size())
      throw new ArrayIndexOutOfBoundsException(paramInt); 
    return (paramInt < 32) ? ((this.understoodBits == (this.understoodBits | 1 << paramInt))) : ((this.moreUnderstoodBits == null) ? false : this.moreUnderstoodBits.get(paramInt - 32));
  }
  
  public void understood(@NotNull Header paramHeader) {
    int i = size();
    for (byte b = 0; b < i; b++) {
      if (get(b) == paramHeader) {
        understood(b);
        return;
      } 
    } 
    throw new IllegalArgumentException();
  }
  
  @Nullable
  public Header get(@NotNull String paramString1, @NotNull String paramString2, boolean paramBoolean) {
    int i = size();
    for (byte b = 0; b < i; b++) {
      Header header = get(b);
      if (header.getLocalPart().equals(paramString2) && header.getNamespaceURI().equals(paramString1)) {
        if (paramBoolean)
          understood(b); 
        return header;
      } 
    } 
    return null;
  }
  
  public Header get(String paramString1, String paramString2) { return get(paramString1, paramString2, true); }
  
  @Nullable
  public Header get(@NotNull QName paramQName, boolean paramBoolean) { return get(paramQName.getNamespaceURI(), paramQName.getLocalPart(), paramBoolean); }
  
  @Nullable
  public Header get(@NotNull QName paramQName) { return get(paramQName, true); }
  
  public Iterator<Header> getHeaders(String paramString1, String paramString2) { return getHeaders(paramString1, paramString2, true); }
  
  @NotNull
  public Iterator<Header> getHeaders(@NotNull final String nsUri, @NotNull final String localName, final boolean markAsUnderstood) { return new Iterator<Header>() {
        int idx = 0;
        
        Header next;
        
        public boolean hasNext() {
          if (this.next == null)
            fetch(); 
          return (this.next != null);
        }
        
        public Header next() {
          if (this.next == null) {
            fetch();
            if (this.next == null)
              throw new NoSuchElementException(); 
          } 
          if (markAsUnderstood) {
            assert HeaderList.this.get(this.idx - true) == this.next;
            HeaderList.this.understood(this.idx - 1);
          } 
          Header header = this.next;
          this.next = null;
          return header;
        }
        
        private void fetch() {
          while (this.idx < HeaderList.this.size()) {
            Header header = HeaderList.this.get(this.idx++);
            if (header.getLocalPart().equals(localName) && header.getNamespaceURI().equals(nsUri)) {
              this.next = header;
              break;
            } 
          } 
        }
        
        public void remove() { throw new UnsupportedOperationException(); }
      }; }
  
  @NotNull
  public Iterator<Header> getHeaders(@NotNull QName paramQName, boolean paramBoolean) { return getHeaders(paramQName.getNamespaceURI(), paramQName.getLocalPart(), paramBoolean); }
  
  @NotNull
  public Iterator<Header> getHeaders(@NotNull String paramString) { return getHeaders(paramString, true); }
  
  @NotNull
  public Iterator<Header> getHeaders(@NotNull final String nsUri, final boolean markAsUnderstood) { return new Iterator<Header>() {
        int idx = 0;
        
        Header next;
        
        public boolean hasNext() {
          if (this.next == null)
            fetch(); 
          return (this.next != null);
        }
        
        public Header next() {
          if (this.next == null) {
            fetch();
            if (this.next == null)
              throw new NoSuchElementException(); 
          } 
          if (markAsUnderstood) {
            assert HeaderList.this.get(this.idx - true) == this.next;
            HeaderList.this.understood(this.idx - 1);
          } 
          Header header = this.next;
          this.next = null;
          return header;
        }
        
        private void fetch() {
          while (this.idx < HeaderList.this.size()) {
            Header header = HeaderList.this.get(this.idx++);
            if (header.getNamespaceURI().equals(nsUri)) {
              this.next = header;
              break;
            } 
          } 
        }
        
        public void remove() { throw new UnsupportedOperationException(); }
      }; }
  
  public String getTo(AddressingVersion paramAddressingVersion, SOAPVersion paramSOAPVersion) { return AddressingUtils.getTo(this, paramAddressingVersion, paramSOAPVersion); }
  
  public String getAction(@NotNull AddressingVersion paramAddressingVersion, @NotNull SOAPVersion paramSOAPVersion) { return AddressingUtils.getAction(this, paramAddressingVersion, paramSOAPVersion); }
  
  public WSEndpointReference getReplyTo(@NotNull AddressingVersion paramAddressingVersion, @NotNull SOAPVersion paramSOAPVersion) { return AddressingUtils.getReplyTo(this, paramAddressingVersion, paramSOAPVersion); }
  
  public WSEndpointReference getFaultTo(@NotNull AddressingVersion paramAddressingVersion, @NotNull SOAPVersion paramSOAPVersion) { return AddressingUtils.getFaultTo(this, paramAddressingVersion, paramSOAPVersion); }
  
  public String getMessageID(@NotNull AddressingVersion paramAddressingVersion, @NotNull SOAPVersion paramSOAPVersion) { return AddressingUtils.getMessageID(this, paramAddressingVersion, paramSOAPVersion); }
  
  public String getRelatesTo(@NotNull AddressingVersion paramAddressingVersion, @NotNull SOAPVersion paramSOAPVersion) { return AddressingUtils.getRelatesTo(this, paramAddressingVersion, paramSOAPVersion); }
  
  public void fillRequestAddressingHeaders(Packet paramPacket, AddressingVersion paramAddressingVersion, SOAPVersion paramSOAPVersion, boolean paramBoolean1, String paramString, boolean paramBoolean2) { AddressingUtils.fillRequestAddressingHeaders(this, paramPacket, paramAddressingVersion, paramSOAPVersion, paramBoolean1, paramString, paramBoolean2); }
  
  public void fillRequestAddressingHeaders(Packet paramPacket, AddressingVersion paramAddressingVersion, SOAPVersion paramSOAPVersion, boolean paramBoolean, String paramString) { AddressingUtils.fillRequestAddressingHeaders(this, paramPacket, paramAddressingVersion, paramSOAPVersion, paramBoolean, paramString); }
  
  public void fillRequestAddressingHeaders(WSDLPort paramWSDLPort, @NotNull WSBinding paramWSBinding, Packet paramPacket) { AddressingUtils.fillRequestAddressingHeaders(this, paramWSDLPort, paramWSBinding, paramPacket); }
  
  public boolean add(Header paramHeader) { return super.add(paramHeader); }
  
  @Nullable
  public Header remove(@NotNull String paramString1, @NotNull String paramString2) {
    int i = size();
    for (byte b = 0; b < i; b++) {
      Header header = get(b);
      if (header.getLocalPart().equals(paramString2) && header.getNamespaceURI().equals(paramString1))
        return remove(b); 
    } 
    return null;
  }
  
  public boolean addOrReplace(Header paramHeader) {
    for (byte b = 0; b < size(); b++) {
      Header header = get(b);
      if (header.getNamespaceURI().equals(paramHeader.getNamespaceURI()) && header.getLocalPart().equals(paramHeader.getLocalPart())) {
        removeInternal(b);
        addInternal(b, paramHeader);
        return true;
      } 
    } 
    return add(paramHeader);
  }
  
  public void replace(Header paramHeader1, Header paramHeader2) {
    for (byte b = 0; b < size(); b++) {
      Header header = get(b);
      if (header.getNamespaceURI().equals(paramHeader2.getNamespaceURI()) && header.getLocalPart().equals(paramHeader2.getLocalPart())) {
        removeInternal(b);
        addInternal(b, paramHeader2);
        return;
      } 
    } 
    throw new IllegalArgumentException();
  }
  
  protected void addInternal(int paramInt, Header paramHeader) { add(paramInt, paramHeader); }
  
  protected Header removeInternal(int paramInt) { return (Header)super.remove(paramInt); }
  
  @Nullable
  public Header remove(@NotNull QName paramQName) { return remove(paramQName.getNamespaceURI(), paramQName.getLocalPart()); }
  
  public Header remove(int paramInt) {
    removeUnderstoodBit(paramInt);
    return (Header)super.remove(paramInt);
  }
  
  private void removeUnderstoodBit(int paramInt) {
    assert paramInt < size();
    if (paramInt < 32) {
      int i = this.understoodBits >>> -31 + paramInt << paramInt;
      int j = this.understoodBits << -paramInt >>> 31 - paramInt >>> 1;
      this.understoodBits = i | j;
      if (this.moreUnderstoodBits != null && this.moreUnderstoodBits.cardinality() > 0) {
        if (this.moreUnderstoodBits.get(0))
          this.understoodBits |= Integer.MIN_VALUE; 
        this.moreUnderstoodBits.clear(0);
        int k;
        for (k = this.moreUnderstoodBits.nextSetBit(1); k > 0; k = this.moreUnderstoodBits.nextSetBit(k + 1)) {
          this.moreUnderstoodBits.set(k - 1);
          this.moreUnderstoodBits.clear(k);
        } 
      } 
    } else if (this.moreUnderstoodBits != null && this.moreUnderstoodBits.cardinality() > 0) {
      paramInt -= 32;
      this.moreUnderstoodBits.clear(paramInt);
      for (int i = this.moreUnderstoodBits.nextSetBit(paramInt); i >= 1; i = this.moreUnderstoodBits.nextSetBit(i + 1)) {
        this.moreUnderstoodBits.set(i - 1);
        this.moreUnderstoodBits.clear(i);
      } 
    } 
    if (size() - 1 <= 33 && this.moreUnderstoodBits != null)
      this.moreUnderstoodBits = null; 
  }
  
  public boolean remove(Object paramObject) {
    if (paramObject != null)
      for (byte b = 0; b < size(); b++) {
        if (paramObject.equals(get(b))) {
          remove(b);
          return true;
        } 
      }  
    return false;
  }
  
  public Header remove(Header paramHeader) { return remove(paramHeader) ? paramHeader : null; }
  
  public static HeaderList copy(MessageHeaders paramMessageHeaders) { return (paramMessageHeaders == null) ? null : new HeaderList(paramMessageHeaders); }
  
  public static HeaderList copy(HeaderList paramHeaderList) { return copy(paramHeaderList); }
  
  public void readResponseAddressingHeaders(WSDLPort paramWSDLPort, WSBinding paramWSBinding) {}
  
  public void understood(QName paramQName) { get(paramQName, true); }
  
  public void understood(String paramString1, String paramString2) { get(paramString1, paramString2, true); }
  
  public Set<QName> getUnderstoodHeaders() {
    HashSet hashSet = new HashSet();
    for (byte b = 0; b < size(); b++) {
      if (isUnderstood(b)) {
        Header header = get(b);
        hashSet.add(new QName(header.getNamespaceURI(), header.getLocalPart()));
      } 
    } 
    return hashSet;
  }
  
  public boolean isUnderstood(Header paramHeader) { return isUnderstood(paramHeader.getNamespaceURI(), paramHeader.getLocalPart()); }
  
  public boolean isUnderstood(String paramString1, String paramString2) {
    for (byte b = 0; b < size(); b++) {
      Header header = get(b);
      if (header.getLocalPart().equals(paramString2) && header.getNamespaceURI().equals(paramString1))
        return isUnderstood(b); 
    } 
    return false;
  }
  
  public boolean isUnderstood(QName paramQName) { return isUnderstood(paramQName.getNamespaceURI(), paramQName.getLocalPart()); }
  
  public Set<QName> getNotUnderstoodHeaders(Set<String> paramSet1, Set<QName> paramSet2, WSBinding paramWSBinding) {
    HashSet hashSet = null;
    if (paramSet1 == null)
      paramSet1 = new HashSet<String>(); 
    SOAPVersion sOAPVersion = getEffectiveSOAPVersion(paramWSBinding);
    paramSet1.add(sOAPVersion.implicitRole);
    for (byte b = 0; b < size(); b++) {
      if (!isUnderstood(b)) {
        Header header = get(b);
        if (!header.isIgnorable(sOAPVersion, paramSet1)) {
          QName qName = new QName(header.getNamespaceURI(), header.getLocalPart());
          if (paramWSBinding == null) {
            if (hashSet == null)
              hashSet = new HashSet(); 
            hashSet.add(qName);
          } else if (paramWSBinding instanceof SOAPBindingImpl && !((SOAPBindingImpl)paramWSBinding).understandsHeader(qName) && !paramSet2.contains(qName)) {
            if (hashSet == null)
              hashSet = new HashSet(); 
            hashSet.add(qName);
          } 
        } 
      } 
    } 
    return hashSet;
  }
  
  private SOAPVersion getEffectiveSOAPVersion(WSBinding paramWSBinding) {
    SOAPVersion sOAPVersion = (this.soapVersion != null) ? this.soapVersion : paramWSBinding.getSOAPVersion();
    if (sOAPVersion == null)
      sOAPVersion = SOAPVersion.SOAP_11; 
    return sOAPVersion;
  }
  
  public void setSoapVersion(SOAPVersion paramSOAPVersion) { this.soapVersion = paramSOAPVersion; }
  
  public Iterator<Header> getHeaders() { return iterator(); }
  
  public List<Header> asList() { return this; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\message\HeaderList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */