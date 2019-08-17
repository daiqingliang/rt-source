package com.sun.org.apache.xalan.internal.xsltc.runtime;

import java.util.ListResourceBundle;

public class ErrorMessages_ja extends ListResourceBundle {
  public Object[][] getContents() { return new Object[][] { 
        { "RUN_TIME_INTERNAL_ERR", "''{0}''内のランタイム内部エラーです" }, { "RUN_TIME_COPY_ERR", "<xsl:copy>の実行中のランタイム・エラーです。" }, { "DATA_CONVERSION_ERR", "''{0}''から''{1}''への変換が無効です。" }, { "EXTERNAL_FUNC_ERR", "外部関数''{0}''はXSLTCによってサポートされていません。" }, { "EQUALITY_EXPR_ERR", "等価式に不明な引数タイプがあります。" }, { "INVALID_ARGUMENT_ERR", "''{1}''の呼出しの引数タイプ''{0}''が無効です" }, { "FORMAT_NUMBER_ERR", "パターン''{1}''を使用して数値''{0}''をフォーマットしようとしました。" }, { "ITERATOR_CLONE_ERR", "イテレータ''{0}''のクローンを作成できません。" }, { "AXIS_SUPPORT_ERR", "軸''{0}''のイテレータはサポートされていません。" }, { "TYPED_AXIS_SUPPORT_ERR", "型指定された軸''{0}''のイテレータはサポートされていません。" }, 
        { "STRAY_ATTRIBUTE_ERR", "属性''{0}''が要素の外側にあります。" }, { "STRAY_NAMESPACE_ERR", "ネームスペース宣言''{0}''=''{1}''が要素の外側にあります。" }, { "NAMESPACE_PREFIX_ERR", "接頭辞''{0}''のネームスペースが宣言されていません。" }, { "DOM_ADAPTER_INIT_ERR", "DOMAdapterが間違ったタイプのソースDOMを使用して作成されました。" }, { "PARSER_DTD_SUPPORT_ERR", "使用中のSAXパーサーはDTD宣言イベントを処理しません。" }, { "NAMESPACES_SUPPORT_ERR", "使用中のSAXパーサーにはXMLネームスペースのサポートがありません。" }, { "CANT_RESOLVE_RELATIVE_URI_ERR", "URI参照''{0}''を解決できませんでした。" }, { "UNSUPPORTED_XSL_ERR", "サポートされていないXSL要素''{0}''" }, { "UNSUPPORTED_EXT_ERR", "認識されないXSLTC拡張''{0}''" }, { "UNKNOWN_TRANSLET_VERSION_ERR", "指定されたtransletの''{0}''は、使用中のXSLTCランタイムのバージョンよりも新しいXSLTCのバージョンを使用して作成されたものです。このtransletを実行するには、スタイルシートを再コンパイルするか、より新しいバージョンのXSLTCを使用する必要があります。" }, 
        { "INVALID_QNAME_ERR", "値がQNameであることが必要な属性の値が''{0}''でした" }, { "INVALID_NCNAME_ERR", "値がNCNameであることが必要な属性の値が''{0}''でした" }, { "UNALLOWED_EXTENSION_FUNCTION_ERR", "セキュア処理機能がtrueに設定されているとき、拡張関数''{0}''の使用は許可されません。" }, { "UNALLOWED_EXTENSION_ELEMENT_ERR", "セキュア処理機能がtrueに設定されているとき、拡張要素''{0}''の使用は許可されません。" } }; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\runtime\ErrorMessages_ja.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */