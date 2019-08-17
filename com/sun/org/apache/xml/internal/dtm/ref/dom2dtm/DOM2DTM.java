package com.sun.org.apache.xml.internal.dtm.ref.dom2dtm;

import com.sun.org.apache.xml.internal.dtm.DTMManager;
import com.sun.org.apache.xml.internal.dtm.DTMWSFilter;
import com.sun.org.apache.xml.internal.dtm.ref.DTMDefaultBaseIterators;
import com.sun.org.apache.xml.internal.dtm.ref.IncrementalSAXSource;
import com.sun.org.apache.xml.internal.utils.FastStringBuffer;
import com.sun.org.apache.xml.internal.utils.QName;
import com.sun.org.apache.xml.internal.utils.StringBufferPool;
import com.sun.org.apache.xml.internal.utils.TreeWalker;
import com.sun.org.apache.xml.internal.utils.XMLCharacterRecognizer;
import com.sun.org.apache.xml.internal.utils.XMLString;
import com.sun.org.apache.xml.internal.utils.XMLStringFactory;
import java.util.Vector;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.dom.DOMSource;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Entity;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;

public class DOM2DTM extends DTMDefaultBaseIterators {
  static final boolean JJK_DEBUG = false;
  
  static final boolean JJK_NEWCODE = true;
  
  static final String NAMESPACE_DECL_NS = "http://www.w3.org/XML/1998/namespace";
  
  private Node m_pos;
  
  private int m_last_parent = 0;
  
  private int m_last_kid = -1;
  
  private Node m_root;
  
  boolean m_processedFirstElement = false;
  
  private boolean m_nodesAreProcessed;
  
  protected Vector m_nodes = new Vector();
  
  TreeWalker m_walker = new TreeWalker(null);
  
  public DOM2DTM(DTMManager paramDTMManager, DOMSource paramDOMSource, int paramInt, DTMWSFilter paramDTMWSFilter, XMLStringFactory paramXMLStringFactory, boolean paramBoolean) {
    super(paramDTMManager, paramDOMSource, paramInt, paramDTMWSFilter, paramXMLStringFactory, paramBoolean);
    this.m_pos = this.m_root = paramDOMSource.getNode();
    this.m_last_parent = this.m_last_kid = -1;
    this.m_last_kid = addNode(this.m_root, this.m_last_parent, this.m_last_kid, -1);
    if (1 == this.m_root.getNodeType()) {
      NamedNodeMap namedNodeMap = this.m_root.getAttributes();
      boolean bool = (namedNodeMap == null) ? 0 : namedNodeMap.getLength();
      if (bool) {
        int i = -1;
        for (byte b = 0; b < bool; b++) {
          i = addNode(namedNodeMap.item(b), 0, i, -1);
          this.m_firstch.setElementAt(-1, i);
        } 
        this.m_nextsib.setElementAt(-1, i);
      } 
    } 
    this.m_nodesAreProcessed = false;
  }
  
  protected int addNode(Node paramNode, int paramInt1, int paramInt2, int paramInt3) { // Byte code:
    //   0: aload_0
    //   1: getfield m_nodes : Ljava/util/Vector;
    //   4: invokevirtual size : ()I
    //   7: istore #5
    //   9: aload_0
    //   10: getfield m_dtmIdent : Lcom/sun/org/apache/xml/internal/utils/SuballocatedIntVector;
    //   13: invokevirtual size : ()I
    //   16: iload #5
    //   18: bipush #16
    //   20: iushr
    //   21: if_icmpne -> 92
    //   24: aload_0
    //   25: getfield m_mgr : Lcom/sun/org/apache/xml/internal/dtm/DTMManager;
    //   28: ifnonnull -> 39
    //   31: new java/lang/ClassCastException
    //   34: dup
    //   35: invokespecial <init> : ()V
    //   38: athrow
    //   39: aload_0
    //   40: getfield m_mgr : Lcom/sun/org/apache/xml/internal/dtm/DTMManager;
    //   43: checkcast com/sun/org/apache/xml/internal/dtm/ref/DTMManagerDefault
    //   46: astore #6
    //   48: aload #6
    //   50: invokevirtual getFirstFreeDTMID : ()I
    //   53: istore #7
    //   55: aload #6
    //   57: aload_0
    //   58: iload #7
    //   60: iload #5
    //   62: invokevirtual addDTM : (Lcom/sun/org/apache/xml/internal/dtm/DTM;II)V
    //   65: aload_0
    //   66: getfield m_dtmIdent : Lcom/sun/org/apache/xml/internal/utils/SuballocatedIntVector;
    //   69: iload #7
    //   71: bipush #16
    //   73: ishl
    //   74: invokevirtual addElement : (I)V
    //   77: goto -> 92
    //   80: astore #6
    //   82: aload_0
    //   83: ldc 'ER_NO_DTMIDS_AVAIL'
    //   85: aconst_null
    //   86: invokestatic createXMLMessage : (Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   89: invokevirtual error : (Ljava/lang/String;)V
    //   92: aload_0
    //   93: dup
    //   94: getfield m_size : I
    //   97: iconst_1
    //   98: iadd
    //   99: putfield m_size : I
    //   102: iconst_m1
    //   103: iload #4
    //   105: if_icmpne -> 119
    //   108: aload_1
    //   109: invokeinterface getNodeType : ()S
    //   114: istore #6
    //   116: goto -> 123
    //   119: iload #4
    //   121: istore #6
    //   123: iconst_2
    //   124: iload #6
    //   126: if_icmpne -> 161
    //   129: aload_1
    //   130: invokeinterface getNodeName : ()Ljava/lang/String;
    //   135: astore #7
    //   137: aload #7
    //   139: ldc 'xmlns:'
    //   141: invokevirtual startsWith : (Ljava/lang/String;)Z
    //   144: ifne -> 157
    //   147: aload #7
    //   149: ldc 'xmlns'
    //   151: invokevirtual equals : (Ljava/lang/Object;)Z
    //   154: ifeq -> 161
    //   157: bipush #13
    //   159: istore #6
    //   161: aload_0
    //   162: getfield m_nodes : Ljava/util/Vector;
    //   165: aload_1
    //   166: invokevirtual addElement : (Ljava/lang/Object;)V
    //   169: aload_0
    //   170: getfield m_firstch : Lcom/sun/org/apache/xml/internal/utils/SuballocatedIntVector;
    //   173: bipush #-2
    //   175: iload #5
    //   177: invokevirtual setElementAt : (II)V
    //   180: aload_0
    //   181: getfield m_nextsib : Lcom/sun/org/apache/xml/internal/utils/SuballocatedIntVector;
    //   184: bipush #-2
    //   186: iload #5
    //   188: invokevirtual setElementAt : (II)V
    //   191: aload_0
    //   192: getfield m_prevsib : Lcom/sun/org/apache/xml/internal/utils/SuballocatedIntVector;
    //   195: iload_3
    //   196: iload #5
    //   198: invokevirtual setElementAt : (II)V
    //   201: aload_0
    //   202: getfield m_parent : Lcom/sun/org/apache/xml/internal/utils/SuballocatedIntVector;
    //   205: iload_2
    //   206: iload #5
    //   208: invokevirtual setElementAt : (II)V
    //   211: iconst_m1
    //   212: iload_2
    //   213: if_icmpeq -> 252
    //   216: iload #6
    //   218: iconst_2
    //   219: if_icmpeq -> 252
    //   222: iload #6
    //   224: bipush #13
    //   226: if_icmpeq -> 252
    //   229: bipush #-2
    //   231: aload_0
    //   232: getfield m_firstch : Lcom/sun/org/apache/xml/internal/utils/SuballocatedIntVector;
    //   235: iload_2
    //   236: invokevirtual elementAt : (I)I
    //   239: if_icmpne -> 252
    //   242: aload_0
    //   243: getfield m_firstch : Lcom/sun/org/apache/xml/internal/utils/SuballocatedIntVector;
    //   246: iload #5
    //   248: iload_2
    //   249: invokevirtual setElementAt : (II)V
    //   252: aload_1
    //   253: invokeinterface getNamespaceURI : ()Ljava/lang/String;
    //   258: astore #7
    //   260: iload #6
    //   262: bipush #7
    //   264: if_icmpne -> 276
    //   267: aload_1
    //   268: invokeinterface getNodeName : ()Ljava/lang/String;
    //   273: goto -> 282
    //   276: aload_1
    //   277: invokeinterface getLocalName : ()Ljava/lang/String;
    //   282: astore #8
    //   284: iload #6
    //   286: iconst_1
    //   287: if_icmpeq -> 296
    //   290: iload #6
    //   292: iconst_2
    //   293: if_icmpne -> 310
    //   296: aconst_null
    //   297: aload #8
    //   299: if_acmpne -> 310
    //   302: aload_1
    //   303: invokeinterface getNodeName : ()Ljava/lang/String;
    //   308: astore #8
    //   310: aload_0
    //   311: getfield m_expandedNameTable : Lcom/sun/org/apache/xml/internal/dtm/ref/ExpandedNameTable;
    //   314: astore #9
    //   316: aload_1
    //   317: invokeinterface getLocalName : ()Ljava/lang/String;
    //   322: ifnonnull -> 337
    //   325: iload #6
    //   327: iconst_1
    //   328: if_icmpeq -> 337
    //   331: iload #6
    //   333: iconst_2
    //   334: if_icmpne -> 337
    //   337: aconst_null
    //   338: aload #8
    //   340: if_acmpeq -> 357
    //   343: aload #9
    //   345: aload #7
    //   347: aload #8
    //   349: iload #6
    //   351: invokevirtual getExpandedTypeID : (Ljava/lang/String;Ljava/lang/String;I)I
    //   354: goto -> 364
    //   357: aload #9
    //   359: iload #6
    //   361: invokevirtual getExpandedTypeID : (I)I
    //   364: istore #10
    //   366: aload_0
    //   367: getfield m_exptype : Lcom/sun/org/apache/xml/internal/utils/SuballocatedIntVector;
    //   370: iload #10
    //   372: iload #5
    //   374: invokevirtual setElementAt : (II)V
    //   377: aload_0
    //   378: iload #10
    //   380: iload #5
    //   382: invokevirtual indexNode : (II)V
    //   385: iconst_m1
    //   386: iload_3
    //   387: if_icmpeq -> 400
    //   390: aload_0
    //   391: getfield m_nextsib : Lcom/sun/org/apache/xml/internal/utils/SuballocatedIntVector;
    //   394: iload #5
    //   396: iload_3
    //   397: invokevirtual setElementAt : (II)V
    //   400: iload #6
    //   402: bipush #13
    //   404: if_icmpne -> 414
    //   407: aload_0
    //   408: iload_2
    //   409: iload #5
    //   411: invokevirtual declareNamespaceInContext : (II)V
    //   414: iload #5
    //   416: ireturn
    // Exception table:
    //   from	to	target	type
    //   24	77	80	java/lang/ClassCastException }
  
  public int getNumberOfNodes() { return this.m_nodes.size(); }
  
  protected boolean nextNode() { // Byte code:
    //   0: aload_0
    //   1: getfield m_nodesAreProcessed : Z
    //   4: ifeq -> 9
    //   7: iconst_0
    //   8: ireturn
    //   9: aload_0
    //   10: getfield m_pos : Lorg/w3c/dom/Node;
    //   13: astore_1
    //   14: aconst_null
    //   15: astore_2
    //   16: iconst_m1
    //   17: istore_3
    //   18: aload_1
    //   19: invokeinterface hasChildNodes : ()Z
    //   24: ifeq -> 142
    //   27: aload_1
    //   28: invokeinterface getFirstChild : ()Lorg/w3c/dom/Node;
    //   33: astore_2
    //   34: aload_2
    //   35: ifnull -> 56
    //   38: bipush #10
    //   40: aload_2
    //   41: invokeinterface getNodeType : ()S
    //   46: if_icmpne -> 56
    //   49: aload_2
    //   50: invokeinterface getNextSibling : ()Lorg/w3c/dom/Node;
    //   55: astore_2
    //   56: iconst_5
    //   57: aload_1
    //   58: invokeinterface getNodeType : ()S
    //   63: if_icmpeq -> 322
    //   66: aload_0
    //   67: aload_0
    //   68: getfield m_last_kid : I
    //   71: putfield m_last_parent : I
    //   74: aload_0
    //   75: iconst_m1
    //   76: putfield m_last_kid : I
    //   79: aconst_null
    //   80: aload_0
    //   81: getfield m_wsfilter : Lcom/sun/org/apache/xml/internal/dtm/DTMWSFilter;
    //   84: if_acmpeq -> 322
    //   87: aload_0
    //   88: getfield m_wsfilter : Lcom/sun/org/apache/xml/internal/dtm/DTMWSFilter;
    //   91: aload_0
    //   92: aload_0
    //   93: getfield m_last_parent : I
    //   96: invokevirtual makeNodeHandle : (I)I
    //   99: aload_0
    //   100: invokeinterface getShouldStripSpace : (ILcom/sun/org/apache/xml/internal/dtm/DTM;)S
    //   105: istore #4
    //   107: iconst_3
    //   108: iload #4
    //   110: if_icmpne -> 120
    //   113: aload_0
    //   114: invokevirtual getShouldStripWhitespace : ()Z
    //   117: goto -> 131
    //   120: iconst_2
    //   121: iload #4
    //   123: if_icmpne -> 130
    //   126: iconst_1
    //   127: goto -> 131
    //   130: iconst_0
    //   131: istore #5
    //   133: aload_0
    //   134: iload #5
    //   136: invokevirtual pushShouldStripWhitespace : (Z)V
    //   139: goto -> 322
    //   142: aload_0
    //   143: getfield m_last_kid : I
    //   146: iconst_m1
    //   147: if_icmpeq -> 178
    //   150: aload_0
    //   151: getfield m_firstch : Lcom/sun/org/apache/xml/internal/utils/SuballocatedIntVector;
    //   154: aload_0
    //   155: getfield m_last_kid : I
    //   158: invokevirtual elementAt : (I)I
    //   161: bipush #-2
    //   163: if_icmpne -> 178
    //   166: aload_0
    //   167: getfield m_firstch : Lcom/sun/org/apache/xml/internal/utils/SuballocatedIntVector;
    //   170: iconst_m1
    //   171: aload_0
    //   172: getfield m_last_kid : I
    //   175: invokevirtual setElementAt : (II)V
    //   178: aload_0
    //   179: getfield m_last_parent : I
    //   182: iconst_m1
    //   183: if_icmpeq -> 312
    //   186: aload_1
    //   187: invokeinterface getNextSibling : ()Lorg/w3c/dom/Node;
    //   192: astore_2
    //   193: aload_2
    //   194: ifnull -> 215
    //   197: bipush #10
    //   199: aload_2
    //   200: invokeinterface getNodeType : ()S
    //   205: if_icmpne -> 215
    //   208: aload_2
    //   209: invokeinterface getNextSibling : ()Lorg/w3c/dom/Node;
    //   214: astore_2
    //   215: aload_2
    //   216: ifnull -> 222
    //   219: goto -> 312
    //   222: aload_1
    //   223: invokeinterface getParentNode : ()Lorg/w3c/dom/Node;
    //   228: astore_1
    //   229: aload_1
    //   230: ifnonnull -> 233
    //   233: aload_1
    //   234: ifnull -> 250
    //   237: iconst_5
    //   238: aload_1
    //   239: invokeinterface getNodeType : ()S
    //   244: if_icmpne -> 250
    //   247: goto -> 178
    //   250: aload_0
    //   251: invokevirtual popShouldStripWhitespace : ()V
    //   254: aload_0
    //   255: getfield m_last_kid : I
    //   258: iconst_m1
    //   259: if_icmpne -> 277
    //   262: aload_0
    //   263: getfield m_firstch : Lcom/sun/org/apache/xml/internal/utils/SuballocatedIntVector;
    //   266: iconst_m1
    //   267: aload_0
    //   268: getfield m_last_parent : I
    //   271: invokevirtual setElementAt : (II)V
    //   274: goto -> 289
    //   277: aload_0
    //   278: getfield m_nextsib : Lcom/sun/org/apache/xml/internal/utils/SuballocatedIntVector;
    //   281: iconst_m1
    //   282: aload_0
    //   283: getfield m_last_kid : I
    //   286: invokevirtual setElementAt : (II)V
    //   289: aload_0
    //   290: aload_0
    //   291: getfield m_parent : Lcom/sun/org/apache/xml/internal/utils/SuballocatedIntVector;
    //   294: aload_0
    //   295: aload_0
    //   296: getfield m_last_parent : I
    //   299: dup_x1
    //   300: putfield m_last_kid : I
    //   303: invokevirtual elementAt : (I)I
    //   306: putfield m_last_parent : I
    //   309: goto -> 178
    //   312: aload_0
    //   313: getfield m_last_parent : I
    //   316: iconst_m1
    //   317: if_icmpne -> 322
    //   320: aconst_null
    //   321: astore_2
    //   322: aload_2
    //   323: ifnull -> 333
    //   326: aload_2
    //   327: invokeinterface getNodeType : ()S
    //   332: istore_3
    //   333: iconst_5
    //   334: iload_3
    //   335: if_icmpne -> 340
    //   338: aload_2
    //   339: astore_1
    //   340: iconst_5
    //   341: iload_3
    //   342: if_icmpeq -> 18
    //   345: aload_2
    //   346: ifnonnull -> 370
    //   349: aload_0
    //   350: getfield m_nextsib : Lcom/sun/org/apache/xml/internal/utils/SuballocatedIntVector;
    //   353: iconst_m1
    //   354: iconst_0
    //   355: invokevirtual setElementAt : (II)V
    //   358: aload_0
    //   359: iconst_1
    //   360: putfield m_nodesAreProcessed : Z
    //   363: aload_0
    //   364: aconst_null
    //   365: putfield m_pos : Lorg/w3c/dom/Node;
    //   368: iconst_0
    //   369: ireturn
    //   370: iconst_0
    //   371: istore #4
    //   373: aconst_null
    //   374: astore #5
    //   376: aload_2
    //   377: invokeinterface getNodeType : ()S
    //   382: istore_3
    //   383: iconst_3
    //   384: iload_3
    //   385: if_icmpeq -> 393
    //   388: iconst_4
    //   389: iload_3
    //   390: if_icmpne -> 469
    //   393: aconst_null
    //   394: aload_0
    //   395: getfield m_wsfilter : Lcom/sun/org/apache/xml/internal/dtm/DTMWSFilter;
    //   398: if_acmpeq -> 412
    //   401: aload_0
    //   402: invokevirtual getShouldStripWhitespace : ()Z
    //   405: ifeq -> 412
    //   408: iconst_1
    //   409: goto -> 413
    //   412: iconst_0
    //   413: istore #4
    //   415: aload_2
    //   416: astore #6
    //   418: aload #6
    //   420: ifnull -> 466
    //   423: aload #6
    //   425: astore #5
    //   427: iconst_3
    //   428: aload #6
    //   430: invokeinterface getNodeType : ()S
    //   435: if_icmpne -> 440
    //   438: iconst_3
    //   439: istore_3
    //   440: iload #4
    //   442: aload #6
    //   444: invokeinterface getNodeValue : ()Ljava/lang/String;
    //   449: invokestatic isWhiteSpace : (Ljava/lang/String;)Z
    //   452: iand
    //   453: istore #4
    //   455: aload_0
    //   456: aload #6
    //   458: invokespecial logicalNextDOMTextNode : (Lorg/w3c/dom/Node;)Lorg/w3c/dom/Node;
    //   461: astore #6
    //   463: goto -> 418
    //   466: goto -> 491
    //   469: bipush #7
    //   471: iload_3
    //   472: if_icmpne -> 491
    //   475: aload_1
    //   476: invokeinterface getNodeName : ()Ljava/lang/String;
    //   481: invokevirtual toLowerCase : ()Ljava/lang/String;
    //   484: ldc 'xml'
    //   486: invokevirtual equals : (Ljava/lang/Object;)Z
    //   489: istore #4
    //   491: iload #4
    //   493: ifne -> 720
    //   496: aload_0
    //   497: aload_2
    //   498: aload_0
    //   499: getfield m_last_parent : I
    //   502: aload_0
    //   503: getfield m_last_kid : I
    //   506: iload_3
    //   507: invokevirtual addNode : (Lorg/w3c/dom/Node;III)I
    //   510: istore #6
    //   512: aload_0
    //   513: iload #6
    //   515: putfield m_last_kid : I
    //   518: iconst_1
    //   519: iload_3
    //   520: if_icmpne -> 720
    //   523: iconst_m1
    //   524: istore #7
    //   526: aload_2
    //   527: invokeinterface getAttributes : ()Lorg/w3c/dom/NamedNodeMap;
    //   532: astore #8
    //   534: aload #8
    //   536: ifnonnull -> 543
    //   539: iconst_0
    //   540: goto -> 550
    //   543: aload #8
    //   545: invokeinterface getLength : ()I
    //   550: istore #9
    //   552: iload #9
    //   554: ifle -> 637
    //   557: iconst_0
    //   558: istore #10
    //   560: iload #10
    //   562: iload #9
    //   564: if_icmpge -> 637
    //   567: aload_0
    //   568: aload #8
    //   570: iload #10
    //   572: invokeinterface item : (I)Lorg/w3c/dom/Node;
    //   577: iload #6
    //   579: iload #7
    //   581: iconst_m1
    //   582: invokevirtual addNode : (Lorg/w3c/dom/Node;III)I
    //   585: istore #7
    //   587: aload_0
    //   588: getfield m_firstch : Lcom/sun/org/apache/xml/internal/utils/SuballocatedIntVector;
    //   591: iconst_m1
    //   592: iload #7
    //   594: invokevirtual setElementAt : (II)V
    //   597: aload_0
    //   598: getfield m_processedFirstElement : Z
    //   601: ifne -> 631
    //   604: ldc 'xmlns:xml'
    //   606: aload #8
    //   608: iload #10
    //   610: invokeinterface item : (I)Lorg/w3c/dom/Node;
    //   615: invokeinterface getNodeName : ()Ljava/lang/String;
    //   620: invokevirtual equals : (Ljava/lang/Object;)Z
    //   623: ifeq -> 631
    //   626: aload_0
    //   627: iconst_1
    //   628: putfield m_processedFirstElement : Z
    //   631: iinc #10, 1
    //   634: goto -> 560
    //   637: aload_0
    //   638: getfield m_processedFirstElement : Z
    //   641: ifne -> 704
    //   644: aload_0
    //   645: new com/sun/org/apache/xml/internal/dtm/ref/dom2dtm/DOM2DTMdefaultNamespaceDeclarationNode
    //   648: dup
    //   649: aload_2
    //   650: checkcast org/w3c/dom/Element
    //   653: ldc 'xml'
    //   655: ldc 'http://www.w3.org/XML/1998/namespace'
    //   657: aload_0
    //   658: iload #7
    //   660: iconst_m1
    //   661: if_icmpne -> 669
    //   664: iload #6
    //   666: goto -> 671
    //   669: iload #7
    //   671: iconst_1
    //   672: iadd
    //   673: invokevirtual makeNodeHandle : (I)I
    //   676: invokespecial <init> : (Lorg/w3c/dom/Element;Ljava/lang/String;Ljava/lang/String;I)V
    //   679: iload #6
    //   681: iload #7
    //   683: iconst_m1
    //   684: invokevirtual addNode : (Lorg/w3c/dom/Node;III)I
    //   687: istore #7
    //   689: aload_0
    //   690: getfield m_firstch : Lcom/sun/org/apache/xml/internal/utils/SuballocatedIntVector;
    //   693: iconst_m1
    //   694: iload #7
    //   696: invokevirtual setElementAt : (II)V
    //   699: aload_0
    //   700: iconst_1
    //   701: putfield m_processedFirstElement : Z
    //   704: iload #7
    //   706: iconst_m1
    //   707: if_icmpeq -> 720
    //   710: aload_0
    //   711: getfield m_nextsib : Lcom/sun/org/apache/xml/internal/utils/SuballocatedIntVector;
    //   714: iconst_m1
    //   715: iload #7
    //   717: invokevirtual setElementAt : (II)V
    //   720: iconst_3
    //   721: iload_3
    //   722: if_icmpeq -> 730
    //   725: iconst_4
    //   726: iload_3
    //   727: if_icmpne -> 733
    //   730: aload #5
    //   732: astore_2
    //   733: aload_0
    //   734: aload_2
    //   735: putfield m_pos : Lorg/w3c/dom/Node;
    //   738: iconst_1
    //   739: ireturn }
  
  public Node getNode(int paramInt) {
    int i = makeNodeIdentity(paramInt);
    return (Node)this.m_nodes.elementAt(i);
  }
  
  protected Node lookupNode(int paramInt) { return (Node)this.m_nodes.elementAt(paramInt); }
  
  protected int getNextNodeIdentity(int paramInt) {
    if (++paramInt >= this.m_nodes.size() && !nextNode())
      paramInt = -1; 
    return paramInt;
  }
  
  private int getHandleFromNode(Node paramNode) {
    if (null != paramNode) {
      boolean bool;
      int i = this.m_nodes.size();
      byte b = 0;
      do {
        while (b < i) {
          if (this.m_nodes.elementAt(b) == paramNode)
            return makeNodeHandle(b); 
          b++;
        } 
        bool = nextNode();
        i = this.m_nodes.size();
      } while (bool || b < i);
    } 
    return -1;
  }
  
  public int getHandleOfNode(Node paramNode) {
    if (null != paramNode && (this.m_root == paramNode || (this.m_root.getNodeType() == 9 && this.m_root == paramNode.getOwnerDocument()) || (this.m_root.getNodeType() != 9 && this.m_root.getOwnerDocument() == paramNode.getOwnerDocument())))
      for (Node node = paramNode; node != null; node = (node.getNodeType() != 2) ? node.getParentNode() : ((Attr)node).getOwnerElement()) {
        if (node == this.m_root)
          return getHandleFromNode(paramNode); 
      }  
    return -1;
  }
  
  public int getAttributeNode(int paramInt, String paramString1, String paramString2) {
    if (null == paramString1)
      paramString1 = ""; 
    short s = getNodeType(paramInt);
    if (1 == s) {
      int i = makeNodeIdentity(paramInt);
      while (-1 != (i = getNextNodeIdentity(i))) {
        s = _type(i);
        if (s == 2 || s == 13) {
          Node node = lookupNode(i);
          String str1 = node.getNamespaceURI();
          if (null == str1)
            str1 = ""; 
          String str2 = node.getLocalName();
          if (str1.equals(paramString1) && paramString2.equals(str2))
            return makeNodeHandle(i); 
        } 
      } 
    } 
    return -1;
  }
  
  public XMLString getStringValue(int paramInt) {
    short s = getNodeType(paramInt);
    Node node = getNode(paramInt);
    if (1 == s || 9 == s || 11 == s) {
      String str;
      fastStringBuffer = StringBufferPool.get();
      try {
        getNodeData(node, fastStringBuffer);
        str = (fastStringBuffer.length() > 0) ? fastStringBuffer.toString() : "";
      } finally {
        StringBufferPool.free(fastStringBuffer);
      } 
      return this.m_xstrf.newstr(str);
    } 
    if (3 == s || 4 == s) {
      FastStringBuffer fastStringBuffer = StringBufferPool.get();
      while (node != null) {
        fastStringBuffer.append(node.getNodeValue());
        node = logicalNextDOMTextNode(node);
      } 
      String str = (fastStringBuffer.length() > 0) ? fastStringBuffer.toString() : "";
      StringBufferPool.free(fastStringBuffer);
      return this.m_xstrf.newstr(str);
    } 
    return this.m_xstrf.newstr(node.getNodeValue());
  }
  
  public boolean isWhitespace(int paramInt) {
    short s = getNodeType(paramInt);
    Node node = getNode(paramInt);
    if (3 == s || 4 == s) {
      FastStringBuffer fastStringBuffer = StringBufferPool.get();
      while (node != null) {
        fastStringBuffer.append(node.getNodeValue());
        node = logicalNextDOMTextNode(node);
      } 
      boolean bool = fastStringBuffer.isWhitespace(0, fastStringBuffer.length());
      StringBufferPool.free(fastStringBuffer);
      return bool;
    } 
    return false;
  }
  
  protected static void getNodeData(Node paramNode, FastStringBuffer paramFastStringBuffer) {
    Node node;
    switch (paramNode.getNodeType()) {
      case 1:
      case 9:
      case 11:
        for (node = paramNode.getFirstChild(); null != node; node = node.getNextSibling())
          getNodeData(node, paramFastStringBuffer); 
        break;
      case 2:
      case 3:
      case 4:
        paramFastStringBuffer.append(paramNode.getNodeValue());
        break;
    } 
  }
  
  public String getNodeName(int paramInt) {
    Node node = getNode(paramInt);
    return node.getNodeName();
  }
  
  public String getNodeNameX(int paramInt) {
    Node node;
    short s = getNodeType(paramInt);
    switch (s) {
      case 13:
        node = getNode(paramInt);
        null = node.getNodeName();
        if (null.startsWith("xmlns:")) {
          null = QName.getLocalPart(null);
        } else if (null.equals("xmlns")) {
          null = "";
        } 
        return null;
      case 1:
      case 2:
      case 5:
      case 7:
        node = getNode(paramInt);
        return node.getNodeName();
    } 
    return "";
  }
  
  public String getLocalName(int paramInt) {
    int i = makeNodeIdentity(paramInt);
    if (-1 == i)
      return null; 
    Node node = (Node)this.m_nodes.elementAt(i);
    String str = node.getLocalName();
    if (null == str) {
      String str1 = node.getNodeName();
      if ('#' == str1.charAt(0)) {
        str = "";
      } else {
        int j = str1.indexOf(':');
        str = (j < 0) ? str1 : str1.substring(j + 1);
      } 
    } 
    return str;
  }
  
  public String getPrefix(int paramInt) {
    int i;
    String str;
    Node node;
    short s = getNodeType(paramInt);
    switch (s) {
      case 13:
        node = getNode(paramInt);
        str = node.getNodeName();
        i = str.indexOf(':');
        return (i < 0) ? "" : str.substring(i + 1);
      case 1:
      case 2:
        node = getNode(paramInt);
        str = node.getNodeName();
        i = str.indexOf(':');
        return (i < 0) ? "" : str.substring(0, i);
    } 
    return "";
  }
  
  public String getNamespaceURI(int paramInt) {
    int i = makeNodeIdentity(paramInt);
    if (i == -1)
      return null; 
    Node node = (Node)this.m_nodes.elementAt(i);
    return node.getNamespaceURI();
  }
  
  private Node logicalNextDOMTextNode(Node paramNode) {
    Node node = paramNode.getNextSibling();
    if (node == null)
      for (paramNode = paramNode.getParentNode(); paramNode != null && 5 == paramNode.getNodeType(); paramNode = paramNode.getParentNode()) {
        node = paramNode.getNextSibling();
        if (node != null)
          break; 
      }  
    for (paramNode = node; paramNode != null && 5 == paramNode.getNodeType(); paramNode = paramNode.getNextSibling()) {
      if (paramNode.hasChildNodes()) {
        paramNode = paramNode.getFirstChild();
        continue;
      } 
    } 
    if (paramNode != null) {
      short s = paramNode.getNodeType();
      if (3 != s && 4 != s)
        paramNode = null; 
    } 
    return paramNode;
  }
  
  public String getNodeValue(int paramInt) {
    int i = _exptype(makeNodeIdentity(paramInt));
    i = (-1 != i) ? getNodeType(paramInt) : -1;
    if (3 != i && 4 != i)
      return getNode(paramInt).getNodeValue(); 
    Node node1 = getNode(paramInt);
    Node node2 = logicalNextDOMTextNode(node1);
    if (node2 == null)
      return node1.getNodeValue(); 
    FastStringBuffer fastStringBuffer = StringBufferPool.get();
    fastStringBuffer.append(node1.getNodeValue());
    while (node2 != null) {
      fastStringBuffer.append(node2.getNodeValue());
      node2 = logicalNextDOMTextNode(node2);
    } 
    String str = (fastStringBuffer.length() > 0) ? fastStringBuffer.toString() : "";
    StringBufferPool.free(fastStringBuffer);
    return str;
  }
  
  public String getDocumentTypeDeclarationSystemIdentifier() {
    Document document;
    if (this.m_root.getNodeType() == 9) {
      document = (Document)this.m_root;
    } else {
      document = this.m_root.getOwnerDocument();
    } 
    if (null != document) {
      DocumentType documentType = document.getDoctype();
      if (null != documentType)
        return documentType.getSystemId(); 
    } 
    return null;
  }
  
  public String getDocumentTypeDeclarationPublicIdentifier() {
    Document document;
    if (this.m_root.getNodeType() == 9) {
      document = (Document)this.m_root;
    } else {
      document = this.m_root.getOwnerDocument();
    } 
    if (null != document) {
      DocumentType documentType = document.getDoctype();
      if (null != documentType)
        return documentType.getPublicId(); 
    } 
    return null;
  }
  
  public int getElementById(String paramString) {
    Document document = (this.m_root.getNodeType() == 9) ? (Document)this.m_root : this.m_root.getOwnerDocument();
    if (null != document) {
      Element element = document.getElementById(paramString);
      if (null != element) {
        int i = getHandleFromNode(element);
        if (-1 == i) {
          int j = this.m_nodes.size() - 1;
          while (-1 != (j = getNextNodeIdentity(j))) {
            Node node = getNode(j);
            if (node == element) {
              i = getHandleFromNode(element);
              break;
            } 
          } 
        } 
        return i;
      } 
    } 
    return -1;
  }
  
  public String getUnparsedEntityURI(String paramString) {
    String str = "";
    Document document = (this.m_root.getNodeType() == 9) ? (Document)this.m_root : this.m_root.getOwnerDocument();
    if (null != document) {
      DocumentType documentType = document.getDoctype();
      if (null != documentType) {
        NamedNodeMap namedNodeMap = documentType.getEntities();
        if (null == namedNodeMap)
          return str; 
        Entity entity = (Entity)namedNodeMap.getNamedItem(paramString);
        if (null == entity)
          return str; 
        String str1 = entity.getNotationName();
        if (null != str1) {
          str = entity.getSystemId();
          if (null == str)
            str = entity.getPublicId(); 
        } 
      } 
    } 
    return str;
  }
  
  public boolean isAttributeSpecified(int paramInt) {
    short s = getNodeType(paramInt);
    if (2 == s) {
      Attr attr = (Attr)getNode(paramInt);
      return attr.getSpecified();
    } 
    return false;
  }
  
  public void setIncrementalSAXSource(IncrementalSAXSource paramIncrementalSAXSource) {}
  
  public ContentHandler getContentHandler() { return null; }
  
  public LexicalHandler getLexicalHandler() { return null; }
  
  public EntityResolver getEntityResolver() { return null; }
  
  public DTDHandler getDTDHandler() { return null; }
  
  public ErrorHandler getErrorHandler() { return null; }
  
  public DeclHandler getDeclHandler() { return null; }
  
  public boolean needsTwoThreads() { return false; }
  
  private static boolean isSpace(char paramChar) { return XMLCharacterRecognizer.isWhiteSpace(paramChar); }
  
  public void dispatchCharactersEvents(int paramInt, ContentHandler paramContentHandler, boolean paramBoolean) throws SAXException {
    if (paramBoolean) {
      XMLString xMLString = getStringValue(paramInt);
      xMLString = xMLString.fixWhiteSpace(true, true, false);
      xMLString.dispatchCharactersEvents(paramContentHandler);
    } else {
      short s = getNodeType(paramInt);
      Node node = getNode(paramInt);
      dispatchNodeData(node, paramContentHandler, 0);
      if (3 == s || 4 == s)
        while (null != (node = logicalNextDOMTextNode(node)))
          dispatchNodeData(node, paramContentHandler, 0);  
    } 
  }
  
  protected static void dispatchNodeData(Node paramNode, ContentHandler paramContentHandler, int paramInt) throws SAXException {
    String str;
    Node node;
    switch (paramNode.getNodeType()) {
      case 1:
      case 9:
      case 11:
        for (node = paramNode.getFirstChild(); null != node; node = node.getNextSibling())
          dispatchNodeData(node, paramContentHandler, paramInt + 1); 
        break;
      case 7:
      case 8:
        if (0 != paramInt)
          break; 
      case 2:
      case 3:
      case 4:
        str = paramNode.getNodeValue();
        if (paramContentHandler instanceof CharacterNodeHandler) {
          ((CharacterNodeHandler)paramContentHandler).characters(paramNode);
          break;
        } 
        paramContentHandler.characters(str.toCharArray(), 0, str.length());
        break;
    } 
  }
  
  public void dispatchToEvents(int paramInt, ContentHandler paramContentHandler) throws SAXException {
    treeWalker = this.m_walker;
    ContentHandler contentHandler = treeWalker.getContentHandler();
    if (null != contentHandler)
      treeWalker = new TreeWalker(null); 
    treeWalker.setContentHandler(paramContentHandler);
    try {
      Node node = getNode(paramInt);
      treeWalker.traverseFragment(node);
    } finally {
      treeWalker.setContentHandler(null);
    } 
  }
  
  public void setProperty(String paramString, Object paramObject) {}
  
  public SourceLocator getSourceLocatorFor(int paramInt) { return null; }
  
  public static interface CharacterNodeHandler {
    void characters(Node param1Node) throws SAXException;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\dtm\ref\dom2dtm\DOM2DTM.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */