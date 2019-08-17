package sun.tools.jar.resources;

import java.util.ListResourceBundle;

public final class jar_ja extends ListResourceBundle {
  protected final Object[][] getContents() { return new Object[][] { 
        { "error.bad.cflag", "フラグ'c'ではマニフェストまたは入力ファイルの指定が必要です。" }, { "error.bad.eflag", "'e'フラグと'Main-Class'属性を持つマニフェストは同時に\n指定できません。" }, { "error.bad.option", "オプション-{ctxu}のうちの1つを指定する必要があります。" }, { "error.bad.uflag", "フラグ'u'ではマニフェストか'e'フラグ、または入力ファイルの指定が必要です。" }, { "error.cant.open", "{0}を開くことができません " }, { "error.create.dir", "ディレクトリ{0}を作成できませんでした" }, { "error.create.tempfile", "一時ファイルを作成できませんでした" }, { "error.illegal.option", "不正なオプション: {0}" }, { "error.incorrect.length", "{0}の処理中に不正な長さがありました" }, { "error.nosuch.fileordir", "{0}というファイルまたはディレクトリはありません" }, 
        { "error.write.file", "既存jarファイルの書込み中にエラーが発生しました" }, { "out.added.manifest", "マニフェストが追加されました" }, { "out.adding", "{0}を追加中です" }, { "out.create", "  {0}が作成されました" }, { "out.deflated", "({0}%収縮されました)" }, { "out.extracted", "{0}が抽出されました" }, { "out.ignore.entry", "エントリ{0}を無視します" }, { "out.inflated", " {0}が展開されました" }, { "out.size", "(入={0})(出={1})" }, { "out.stored", "(0%格納されました)" }, 
        { "out.update.manifest", "マニフェストが更新されました" }, { "usage", "使用方法: jar {ctxui}[vfmn0PMe] [jar-file] [manifest-file] [entry-point] [-C dir] files ...\nオプション:\n    -c  アーカイブを新規作成する\n    -t  アーカイブの内容を一覧表示する\n    -x  指定の(またはすべての)ファイルをアーカイブから抽出する\n    -u  既存アーカイブを更新する\n    -v  標準出力に詳細な出力を生成する\n    -f  アーカイブ・ファイル名を指定する\n    -m  指定のマニフェスト・ファイルからマニフェスト情報を取り込む\n    -n  新規アーカイブの作成後にPack200正規化を実行する\n    -e  実行可能jarファイルにバンドルされたスタンドアロン・\n        アプリケーションのエントリ・ポイントを指定する\n    -0  格納のみ。ZIP圧縮を使用しない\n    -P  ファイル名の先頭の'/' (絶対パス)および\\\"..\\\" (親ディレクトリ)コンポーネントを保持する\n    -M  エントリのマニフェスト・ファイルを作成しない\n    -i  指定のjarファイルの索引情報を生成する\n    -C  指定のディレクトリに変更し、次のファイルを取り込む\nファイルがディレクトリの場合は再帰的に処理されます。\nマニフェスト・ファイル名、アーカイブ・ファイル名およびエントリ・ポイント名は、\nフラグ'm'、'f'、'e'の指定と同じ順番で指定する必要があります。\n\n例1: 2つのクラス・ファイルをアーカイブclasses.jarに保存する: \n       jar cvf classes.jar Foo.class Bar.class \n例2: 既存のマニフェスト・ファイル'mymanifest'を使用し、foo/ディレクトリの\n           全ファイルを'classes.jar'にアーカイブする: \n       jar cvfm classes.jar mymanifest -C foo/ .\n" } }; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\tools\jar\resources\jar_ja.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */