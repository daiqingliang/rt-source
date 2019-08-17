package com.sun.org.apache.xml.internal.serializer.utils;

import java.util.ListResourceBundle;

public class SerializerMessages_ja extends ListResourceBundle {
  public Object[][] getContents() { return new Object[][] { 
        { "BAD_MSGKEY", "メッセージ・キー''{0}''は、メッセージ・クラス''{1}''ではありません" }, { "BAD_MSGFORMAT", "メッセージ・クラス''{1}''のメッセージ''{0}''のフォーマットが失敗しました。" }, { "ER_SERIALIZER_NOT_CONTENTHANDLER", "シリアライザ・クラス''{0}''はorg.xml.sax.ContentHandlerを実装しません。" }, { "ER_RESOURCE_COULD_NOT_FIND", "リソース[ {0} ]は見つかりませんでした。\n {1}" }, { "ER_RESOURCE_COULD_NOT_LOAD", "リソース[ {0} ]をロードできませんでした: {1} \n {2} \t {3}" }, { "ER_BUFFER_SIZE_LESSTHAN_ZERO", "バッファ・サイズ<=0" }, { "ER_INVALID_UTF16_SURROGATE", "無効なUTF-16サロゲートが検出されました: {0}。" }, { "ER_OIERROR", "IOエラー" }, { "ER_ILLEGAL_ATTRIBUTE_POSITION", "子ノードの後または要素が生成される前に属性{0}を追加できません。属性は無視されます。" }, { "ER_NAMESPACE_PREFIX", "接頭辞''{0}''のネームスペースが宣言されていません。" }, 
        { "ER_STRAY_ATTRIBUTE", "属性''{0}''が要素の外側にあります。" }, { "ER_STRAY_NAMESPACE", "ネームスペース宣言''{0}''=''{1}''が要素の外側にあります。" }, { "ER_COULD_NOT_LOAD_RESOURCE", "''{0}''をロードできませんでした(CLASSPATHを確認してください)。現在は単にデフォルトを使用しています" }, { "ER_ILLEGAL_CHARACTER", "{1}の指定された出力エンコーディングで示されない整数値{0}の文字を出力しようとしました。" }, { "ER_COULD_NOT_LOAD_METHOD_PROPERTY", "出力メソッド''{1}''のプロパティ・ファイル''{0}''をロードできませんでした(CLASSPATHを確認してください)" }, { "ER_INVALID_PORT", "無効なポート番号" }, { "ER_PORT_WHEN_HOST_NULL", "ホストがnullの場合はポートを設定できません" }, { "ER_HOST_ADDRESS_NOT_WELLFORMED", "ホストは整形式のアドレスではありません" }, { "ER_SCHEME_NOT_CONFORMANT", "スキームが整合していません。" }, { "ER_SCHEME_FROM_NULL_STRING", "null文字列からはスキームを設定できません" }, 
        { "ER_PATH_CONTAINS_INVALID_ESCAPE_SEQUENCE", "パスに無効なエスケープ・シーケンスが含まれています" }, { "ER_PATH_INVALID_CHAR", "パスに無効な文字が含まれています: {0}" }, { "ER_FRAG_INVALID_CHAR", "フラグメントに無効文字が含まれています" }, { "ER_FRAG_WHEN_PATH_NULL", "パスがnullの場合はフラグメントを設定できません" }, { "ER_FRAG_FOR_GENERIC_URI", "汎用URIのフラグメントのみ設定できます" }, { "ER_NO_SCHEME_IN_URI", "スキームがURIに見つかりません" }, { "ER_CANNOT_INIT_URI_EMPTY_PARMS", "URIは空のパラメータを使用して初期化できません" }, { "ER_NO_FRAGMENT_STRING_IN_PATH", "フラグメントはパスとフラグメントの両方に指定できません" }, { "ER_NO_QUERY_STRING_IN_PATH", "問合せ文字列はパスおよび問合せ文字列内に指定できません" }, { "ER_NO_PORT_IF_NO_HOST", "ホストが指定されていない場合はポートを指定できません" }, 
        { "ER_NO_USERINFO_IF_NO_HOST", "ホストが指定されていない場合はUserinfoを指定できません" }, { "ER_XML_VERSION_NOT_SUPPORTED", "警告: 出力ドキュメントのバージョンは、''{0}''であることがリクエストされています。XMLのこのバージョンはサポートされていません。出力ドキュメントのバージョンは、''1.0''になります。" }, { "ER_SCHEME_REQUIRED", "スキームが必要です。" }, { "ER_FACTORY_PROPERTY_MISSING", "SerializerFactoryに渡されるプロパティ・オブジェクトに、''{0}''プロパティがありません。" }, { "ER_ENCODING_NOT_SUPPORTED", "警告:  エンコーディング''{0}''は、Javaランタイムでサポートされていません。" } }; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\serialize\\utils\SerializerMessages_ja.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */