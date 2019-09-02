import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 * @author xin
 * @date 2019-08-28 21:26
 */
public class Main {
  private static final File INDEX_DIR = new File("d:/tmp/lucene-index");

  public static void main(String[] args) throws Exception {

    Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);

    // Store the index in memory:
    Directory directory = new RAMDirectory();
    // To store an index on disk, use this instead:
    //Directory directory = FSDirectory.open("/tmp/testindex");
    IndexWriter iwriter = new IndexWriter(directory, analyzer, true,
        new IndexWriter.MaxFieldLength(25000));
    // Write 3 docs into index
    Document doc = new Document();
    String text = "This is the text to be indexed.";
    doc.add(new Field("fieldname", text, Field.Store.YES,
        Field.Index.ANALYZED));
    iwriter.addDocument(doc);

    doc = new Document();
    doc.add(new Field("fieldname", "steven red universe", Field.Store.YES,
        Field.Index.ANALYZED));
    iwriter.addDocument(doc);

    doc = new Document();
    doc.add(new Field("fieldname", "My girl like apple", Field.Store.YES,
        Field.Index.ANALYZED));
    iwriter.addDocument(doc);
    iwriter.close();

    // Now search the index:
    IndexSearcher isearcher = new IndexSearcher(directory, true); // read-only=true
    // Parse a simple query that searches for "text":
    QueryParser parser = new QueryParser("fieldname", analyzer);
    Query query = parser.parse("text");
    ScoreDoc[] hits = isearcher.search(query, null, 1000).scoreDocs;
    assertEquals(1, hits.length);
    // Iterate through the results:
    for (int i = 0; i < hits.length; i++) {
      Document hitDoc = isearcher.doc(hits[i].doc);
      assertEquals("This is the text to be indexed.", hitDoc.get("fieldname"));
    }
    isearcher.close();
    directory.close();
  }
}
