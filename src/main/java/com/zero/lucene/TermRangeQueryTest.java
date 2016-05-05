package com.zero.lucene;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

/**
 * 范围查询
 * @author hhr
 *
 */
public class TermRangeQueryTest {
	
	private IndexReader indexReader;
	private IndexSearcher indexSearcher;
	/**
	 * 获取 IndexReader对象
	 * @return
	 * @throws IOException
	 */
	private IndexReader getIndexReader() throws IOException {
		Directory dir = FSDirectory.open(Paths.get("D:\\lucene"));
		indexReader = DirectoryReader.open(dir);
		return indexReader;
	}

	/**
	 * 获取 IndexSearcher对象
	 * @return
	 * @throws IOException
	 */
	private IndexSearcher getIndexSearcher() throws IOException {
		IndexReader indexReader = getIndexReader();
		indexSearcher = new IndexSearcher(indexReader);
		return indexSearcher;
	}
	
	private void close() throws IOException {
		indexReader.close();
	}
	
	public void testTermRangeQuery() throws IOException {
		Query query = new TermRangeQuery("title", new BytesRef("a"), new BytesRef("a"), true, false);
		indexSearcher = getIndexSearcher();
		TopDocs topDocs = indexSearcher.search(query, 100);
		for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
			System.out.println(scoreDoc.doc);
		}
		close();
	}
}
