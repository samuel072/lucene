package com.zero.lucene;

import java.nio.file.Paths;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

/**
 * 分页查询
 * @author hhr
 *
 */
public class SearcherTest {

	private Integer currPage;
	public static final  Integer PAGE_SIZE = 10;
	@Test
	public void searcher() throws Exception {
		Directory directory = FSDirectory.open(Paths.get("D:\\lucene"));
		IndexReader indexReader = DirectoryReader.open(directory);
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		QueryParser parser = new QueryParser("title", new StandardAnalyzer());
		Query query = parser.parse("pwd.txt");
		TopDocs topDocs = indexSearcher.search(query, 100);
		int i = currPage * PAGE_SIZE;
		for (int j = i; j < (j+PAGE_SIZE); j++) {
			Document doc = indexReader.document(topDocs.scoreDocs[j].doc);
			System.out.println(doc);
		}
	}
}
