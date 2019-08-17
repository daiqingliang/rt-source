package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.xml.internal.stream.XMLBufferListener;

public class XMLAttributesImpl implements XMLAttributes, XMLBufferListener {
  protected static final int TABLE_SIZE = 101;
  
  protected static final int MAX_HASH_COLLISIONS = 40;
  
  protected static final int MULTIPLIERS_SIZE = 32;
  
  protected static final int MULTIPLIERS_MASK = 31;
  
  protected static final int SIZE_LIMIT = 20;
  
  protected boolean fNamespaces = true;
  
  protected int fLargeCount = 1;
  
  protected int fLength;
  
  protected Attribute[] fAttributes = new Attribute[4];
  
  protected Attribute[] fAttributeTableView;
  
  protected int[] fAttributeTableViewChainState;
  
  protected int fTableViewBuckets;
  
  protected boolean fIsTableViewConsistent;
  
  protected int[] fHashMultipliers;
  
  public XMLAttributesImpl() { this(101); }
  
  public XMLAttributesImpl(int paramInt) {
    this.fTableViewBuckets = paramInt;
    for (byte b = 0; b < this.fAttributes.length; b++)
      this.fAttributes[b] = new Attribute(); 
  }
  
  public void setNamespaces(boolean paramBoolean) { this.fNamespaces = paramBoolean; }
  
  public int addAttribute(QName paramQName, String paramString1, String paramString2) { return addAttribute(paramQName, paramString1, paramString2, null); }
  
  public int addAttribute(QName paramQName, String paramString1, String paramString2, XMLString paramXMLString) {
    int i;
    if (this.fLength < 20) {
      i = (paramQName.uri != null && !paramQName.uri.equals("")) ? getIndexFast(paramQName.uri, paramQName.localpart) : getIndexFast(paramQName.rawname);
      i = this.fLength;
      if (i == -1 && this.fLength++ == this.fAttributes.length) {
        Attribute[] arrayOfAttribute = new Attribute[this.fAttributes.length + 4];
        System.arraycopy(this.fAttributes, 0, arrayOfAttribute, 0, this.fAttributes.length);
        for (int j = this.fAttributes.length; j < arrayOfAttribute.length; j++)
          arrayOfAttribute[j] = new Attribute(); 
        this.fAttributes = arrayOfAttribute;
      } 
    } else if (paramQName.uri == null || paramQName.uri.length() == 0 || (i = getIndexFast(paramQName.uri, paramQName.localpart)) == -1) {
      if (!this.fIsTableViewConsistent || this.fLength == 20 || (this.fLength > 20 && this.fLength > this.fTableViewBuckets)) {
        prepareAndPopulateTableView();
        this.fIsTableViewConsistent = true;
      } 
      int j = getTableViewBucket(paramQName.rawname);
      if (this.fAttributeTableViewChainState[j] != this.fLargeCount) {
        i = this.fLength;
        if (this.fLength++ == this.fAttributes.length) {
          Attribute[] arrayOfAttribute = new Attribute[this.fAttributes.length << 1];
          System.arraycopy(this.fAttributes, 0, arrayOfAttribute, 0, this.fAttributes.length);
          for (int k = this.fAttributes.length; k < arrayOfAttribute.length; k++)
            arrayOfAttribute[k] = new Attribute(); 
          this.fAttributes = arrayOfAttribute;
        } 
        this.fAttributeTableViewChainState[j] = this.fLargeCount;
        (this.fAttributes[i]).next = null;
        this.fAttributeTableView[j] = this.fAttributes[i];
      } else {
        byte b = 0;
        Attribute attribute1 = this.fAttributeTableView[j];
        while (attribute1 != null && attribute1.name.rawname != paramQName.rawname) {
          attribute1 = attribute1.next;
          b++;
        } 
        if (attribute1 == null) {
          i = this.fLength;
          if (this.fLength++ == this.fAttributes.length) {
            Attribute[] arrayOfAttribute = new Attribute[this.fAttributes.length << 1];
            System.arraycopy(this.fAttributes, 0, arrayOfAttribute, 0, this.fAttributes.length);
            for (int k = this.fAttributes.length; k < arrayOfAttribute.length; k++)
              arrayOfAttribute[k] = new Attribute(); 
            this.fAttributes = arrayOfAttribute;
          } 
          if (b >= 40) {
            (this.fAttributes[i]).name.setValues(paramQName);
            rebalanceTableView(this.fLength);
          } else {
            (this.fAttributes[i]).next = this.fAttributeTableView[j];
            this.fAttributeTableView[j] = this.fAttributes[i];
          } 
        } else {
          i = getIndexFast(paramQName.rawname);
        } 
      } 
    } 
    Attribute attribute = this.fAttributes[i];
    attribute.name.setValues(paramQName);
    attribute.type = paramString1;
    attribute.value = paramString2;
    attribute.xmlValue = paramXMLString;
    attribute.nonNormalizedValue = paramString2;
    attribute.specified = false;
    if (attribute.augs != null)
      attribute.augs.removeAllItems(); 
    return i;
  }
  
  public void removeAllAttributes() { this.fLength = 0; }
  
  public void removeAttributeAt(int paramInt) {
    this.fIsTableViewConsistent = false;
    if (paramInt < this.fLength - 1) {
      Attribute attribute = this.fAttributes[paramInt];
      System.arraycopy(this.fAttributes, paramInt + 1, this.fAttributes, paramInt, this.fLength - paramInt - 1);
      this.fAttributes[this.fLength - 1] = attribute;
    } 
    this.fLength--;
  }
  
  public void setName(int paramInt, QName paramQName) { (this.fAttributes[paramInt]).name.setValues(paramQName); }
  
  public void getName(int paramInt, QName paramQName) { paramQName.setValues((this.fAttributes[paramInt]).name); }
  
  public void setType(int paramInt, String paramString) { (this.fAttributes[paramInt]).type = paramString; }
  
  public void setValue(int paramInt, String paramString) { setValue(paramInt, paramString, null); }
  
  public void setValue(int paramInt, String paramString, XMLString paramXMLString) {
    Attribute attribute = this.fAttributes[paramInt];
    attribute.value = paramString;
    attribute.nonNormalizedValue = paramString;
    attribute.xmlValue = paramXMLString;
  }
  
  public void setNonNormalizedValue(int paramInt, String paramString) {
    if (paramString == null)
      paramString = (this.fAttributes[paramInt]).value; 
    (this.fAttributes[paramInt]).nonNormalizedValue = paramString;
  }
  
  public String getNonNormalizedValue(int paramInt) { return (this.fAttributes[paramInt]).nonNormalizedValue; }
  
  public void setSpecified(int paramInt, boolean paramBoolean) { (this.fAttributes[paramInt]).specified = paramBoolean; }
  
  public boolean isSpecified(int paramInt) { return (this.fAttributes[paramInt]).specified; }
  
  public int getLength() { return this.fLength; }
  
  public String getType(int paramInt) { return (paramInt < 0 || paramInt >= this.fLength) ? null : getReportableType((this.fAttributes[paramInt]).type); }
  
  public String getType(String paramString) {
    int i = getIndex(paramString);
    return (i != -1) ? getReportableType((this.fAttributes[i]).type) : null;
  }
  
  public String getValue(int paramInt) {
    if (paramInt < 0 || paramInt >= this.fLength)
      return null; 
    if ((this.fAttributes[paramInt]).value == null && (this.fAttributes[paramInt]).xmlValue != null)
      (this.fAttributes[paramInt]).value = (this.fAttributes[paramInt]).xmlValue.toString(); 
    return (this.fAttributes[paramInt]).value;
  }
  
  public String getValue(String paramString) {
    int i = getIndex(paramString);
    if (i == -1)
      return null; 
    if ((this.fAttributes[i]).value == null)
      (this.fAttributes[i]).value = (this.fAttributes[i]).xmlValue.toString(); 
    return (this.fAttributes[i]).value;
  }
  
  public String getName(int paramInt) { return (paramInt < 0 || paramInt >= this.fLength) ? null : (this.fAttributes[paramInt]).name.rawname; }
  
  public int getIndex(String paramString) {
    for (byte b = 0; b < this.fLength; b++) {
      Attribute attribute = this.fAttributes[b];
      if (attribute.name.rawname != null && attribute.name.rawname.equals(paramString))
        return b; 
    } 
    return -1;
  }
  
  public int getIndex(String paramString1, String paramString2) {
    for (byte b = 0; b < this.fLength; b++) {
      Attribute attribute = this.fAttributes[b];
      if (attribute.name.localpart != null && attribute.name.localpart.equals(paramString2) && (paramString1 == attribute.name.uri || (paramString1 != null && attribute.name.uri != null && attribute.name.uri.equals(paramString1))))
        return b; 
    } 
    return -1;
  }
  
  public int getIndexByLocalName(String paramString) {
    for (byte b = 0; b < this.fLength; b++) {
      Attribute attribute = this.fAttributes[b];
      if (attribute.name.localpart != null && attribute.name.localpart.equals(paramString))
        return b; 
    } 
    return -1;
  }
  
  public String getLocalName(int paramInt) { return !this.fNamespaces ? "" : ((paramInt < 0 || paramInt >= this.fLength) ? null : (this.fAttributes[paramInt]).name.localpart); }
  
  public String getQName(int paramInt) {
    if (paramInt < 0 || paramInt >= this.fLength)
      return null; 
    String str = (this.fAttributes[paramInt]).name.rawname;
    return (str != null) ? str : "";
  }
  
  public QName getQualifiedName(int paramInt) { return (paramInt < 0 || paramInt >= this.fLength) ? null : (this.fAttributes[paramInt]).name; }
  
  public String getType(String paramString1, String paramString2) {
    if (!this.fNamespaces)
      return null; 
    int i = getIndex(paramString1, paramString2);
    return (i != -1) ? getType(i) : null;
  }
  
  public int getIndexFast(String paramString) {
    for (byte b = 0; b < this.fLength; b++) {
      Attribute attribute = this.fAttributes[b];
      if (attribute.name.rawname == paramString)
        return b; 
    } 
    return -1;
  }
  
  public void addAttributeNS(QName paramQName, String paramString1, String paramString2) {
    int i = this.fLength;
    if (this.fLength++ == this.fAttributes.length) {
      Attribute[] arrayOfAttribute;
      if (this.fLength < 20) {
        arrayOfAttribute = new Attribute[this.fAttributes.length + 4];
      } else {
        arrayOfAttribute = new Attribute[this.fAttributes.length << 1];
      } 
      System.arraycopy(this.fAttributes, 0, arrayOfAttribute, 0, this.fAttributes.length);
      for (int j = this.fAttributes.length; j < arrayOfAttribute.length; j++)
        arrayOfAttribute[j] = new Attribute(); 
      this.fAttributes = arrayOfAttribute;
    } 
    Attribute attribute = this.fAttributes[i];
    attribute.name.setValues(paramQName);
    attribute.type = paramString1;
    attribute.value = paramString2;
    attribute.nonNormalizedValue = paramString2;
    attribute.specified = false;
    attribute.augs.removeAllItems();
  }
  
  public QName checkDuplicatesNS() {
    int i = this.fLength;
    if (i <= 20) {
      Attribute[] arrayOfAttribute = this.fAttributes;
      for (byte b = 0; b < i - 1; b++) {
        Attribute attribute = arrayOfAttribute[b];
        for (byte b1 = b + true; b1 < i; b1++) {
          Attribute attribute1 = arrayOfAttribute[b1];
          if (attribute.name.localpart == attribute1.name.localpart && attribute.name.uri == attribute1.name.uri)
            return attribute1.name; 
        } 
      } 
      return null;
    } 
    return checkManyDuplicatesNS();
  }
  
  private QName checkManyDuplicatesNS() {
    this.fIsTableViewConsistent = false;
    prepareTableView();
    int i = this.fLength;
    Attribute[] arrayOfAttribute1 = this.fAttributes;
    Attribute[] arrayOfAttribute2 = this.fAttributeTableView;
    int[] arrayOfInt = this.fAttributeTableViewChainState;
    int j = this.fLargeCount;
    for (byte b = 0; b < i; b++) {
      Attribute attribute = arrayOfAttribute1[b];
      int k = getTableViewBucket(attribute.name.localpart, attribute.name.uri);
      if (arrayOfInt[k] != j) {
        arrayOfInt[k] = j;
        attribute.next = null;
        arrayOfAttribute2[k] = attribute;
      } else {
        byte b1 = 0;
        Attribute attribute1 = arrayOfAttribute2[k];
        while (attribute1 != null) {
          if (attribute1.name.localpart == attribute.name.localpart && attribute1.name.uri == attribute.name.uri)
            return attribute.name; 
          attribute1 = attribute1.next;
          b1++;
        } 
        if (b1 >= 40) {
          rebalanceTableViewNS(b + true);
          j = this.fLargeCount;
        } else {
          attribute.next = arrayOfAttribute2[k];
          arrayOfAttribute2[k] = attribute;
        } 
      } 
    } 
    return null;
  }
  
  public int getIndexFast(String paramString1, String paramString2) {
    for (byte b = 0; b < this.fLength; b++) {
      Attribute attribute = this.fAttributes[b];
      if (attribute.name.localpart == paramString2 && attribute.name.uri == paramString1)
        return b; 
    } 
    return -1;
  }
  
  private String getReportableType(String paramString) { return (paramString.charAt(0) == '(') ? "NMTOKEN" : paramString; }
  
  protected int getTableViewBucket(String paramString) { return (hash(paramString) & 0x7FFFFFFF) % this.fTableViewBuckets; }
  
  protected int getTableViewBucket(String paramString1, String paramString2) { return (paramString2 == null) ? ((hash(paramString1) & 0x7FFFFFFF) % this.fTableViewBuckets) : ((hash(paramString1, paramString2) & 0x7FFFFFFF) % this.fTableViewBuckets); }
  
  private int hash(String paramString) { return (this.fHashMultipliers == null) ? paramString.hashCode() : hash0(paramString); }
  
  private int hash(String paramString1, String paramString2) { return (this.fHashMultipliers == null) ? (paramString1.hashCode() + paramString2.hashCode() * 31) : (hash0(paramString1) + hash0(paramString2) * this.fHashMultipliers[32]); }
  
  private int hash0(String paramString) {
    int i = 0;
    int j = paramString.length();
    int[] arrayOfInt = this.fHashMultipliers;
    for (byte b = 0; b < j; b++)
      i = i * arrayOfInt[b & 0x1F] + paramString.charAt(b); 
    return i;
  }
  
  protected void cleanTableView() {
    if (++this.fLargeCount < 0) {
      if (this.fAttributeTableViewChainState != null)
        for (int i = this.fTableViewBuckets - 1; i >= 0; i--)
          this.fAttributeTableViewChainState[i] = 0;  
      this.fLargeCount = 1;
    } 
  }
  
  private void growTableView() {
    int i = this.fLength;
    int j = this.fTableViewBuckets;
    do {
      j = (j << 1) + 1;
      if (j < 0) {
        j = Integer.MAX_VALUE;
        break;
      } 
    } while (i > j);
    this.fTableViewBuckets = j;
    this.fAttributeTableView = null;
    this.fLargeCount = 1;
  }
  
  protected void prepareTableView() {
    if (this.fLength > this.fTableViewBuckets)
      growTableView(); 
    if (this.fAttributeTableView == null) {
      this.fAttributeTableView = new Attribute[this.fTableViewBuckets];
      this.fAttributeTableViewChainState = new int[this.fTableViewBuckets];
    } else {
      cleanTableView();
    } 
  }
  
  protected void prepareAndPopulateTableView() { prepareAndPopulateTableView(this.fLength); }
  
  private void prepareAndPopulateTableView(int paramInt) {
    prepareTableView();
    for (byte b = 0; b < paramInt; b++) {
      Attribute attribute = this.fAttributes[b];
      int i = getTableViewBucket(attribute.name.rawname);
      if (this.fAttributeTableViewChainState[i] != this.fLargeCount) {
        this.fAttributeTableViewChainState[i] = this.fLargeCount;
        attribute.next = null;
        this.fAttributeTableView[i] = attribute;
      } else {
        attribute.next = this.fAttributeTableView[i];
        this.fAttributeTableView[i] = attribute;
      } 
    } 
  }
  
  public String getPrefix(int paramInt) {
    if (paramInt < 0 || paramInt >= this.fLength)
      return null; 
    String str = (this.fAttributes[paramInt]).name.prefix;
    return (str != null) ? str : "";
  }
  
  public String getURI(int paramInt) { return (paramInt < 0 || paramInt >= this.fLength) ? null : (this.fAttributes[paramInt]).name.uri; }
  
  public String getValue(String paramString1, String paramString2) {
    int i = getIndex(paramString1, paramString2);
    return (i != -1) ? getValue(i) : null;
  }
  
  public Augmentations getAugmentations(String paramString1, String paramString2) {
    int i = getIndex(paramString1, paramString2);
    return (i != -1) ? (this.fAttributes[i]).augs : null;
  }
  
  public Augmentations getAugmentations(String paramString) {
    int i = getIndex(paramString);
    return (i != -1) ? (this.fAttributes[i]).augs : null;
  }
  
  public Augmentations getAugmentations(int paramInt) { return (paramInt < 0 || paramInt >= this.fLength) ? null : (this.fAttributes[paramInt]).augs; }
  
  public void setAugmentations(int paramInt, Augmentations paramAugmentations) { (this.fAttributes[paramInt]).augs = paramAugmentations; }
  
  public void setURI(int paramInt, String paramString) { (this.fAttributes[paramInt]).name.uri = paramString; }
  
  public void setSchemaId(int paramInt, boolean paramBoolean) { (this.fAttributes[paramInt]).schemaId = paramBoolean; }
  
  public boolean getSchemaId(int paramInt) { return (paramInt < 0 || paramInt >= this.fLength) ? false : (this.fAttributes[paramInt]).schemaId; }
  
  public boolean getSchemaId(String paramString) {
    int i = getIndex(paramString);
    return (i != -1) ? (this.fAttributes[i]).schemaId : 0;
  }
  
  public boolean getSchemaId(String paramString1, String paramString2) {
    if (!this.fNamespaces)
      return false; 
    int i = getIndex(paramString1, paramString2);
    return (i != -1) ? (this.fAttributes[i]).schemaId : 0;
  }
  
  public void refresh() {
    if (this.fLength > 0)
      for (byte b = 0; b < this.fLength; b++)
        getValue(b);  
  }
  
  public void refresh(int paramInt) {}
  
  private void prepareAndPopulateTableViewNS(int paramInt) {
    prepareTableView();
    for (byte b = 0; b < paramInt; b++) {
      Attribute attribute = this.fAttributes[b];
      int i = getTableViewBucket(attribute.name.localpart, attribute.name.uri);
      if (this.fAttributeTableViewChainState[i] != this.fLargeCount) {
        this.fAttributeTableViewChainState[i] = this.fLargeCount;
        attribute.next = null;
        this.fAttributeTableView[i] = attribute;
      } else {
        attribute.next = this.fAttributeTableView[i];
        this.fAttributeTableView[i] = attribute;
      } 
    } 
  }
  
  private void rebalanceTableView(int paramInt) {
    if (this.fHashMultipliers == null)
      this.fHashMultipliers = new int[33]; 
    PrimeNumberSequenceGenerator.generateSequence(this.fHashMultipliers);
    prepareAndPopulateTableView(paramInt);
  }
  
  private void rebalanceTableViewNS(int paramInt) {
    if (this.fHashMultipliers == null)
      this.fHashMultipliers = new int[33]; 
    PrimeNumberSequenceGenerator.generateSequence(this.fHashMultipliers);
    prepareAndPopulateTableViewNS(paramInt);
  }
  
  static class Attribute {
    public QName name = new QName();
    
    public String type;
    
    public String value;
    
    public XMLString xmlValue;
    
    public String nonNormalizedValue;
    
    public boolean specified;
    
    public boolean schemaId;
    
    public Augmentations augs = new AugmentationsImpl();
    
    public Attribute next;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\interna\\util\XMLAttributesImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */