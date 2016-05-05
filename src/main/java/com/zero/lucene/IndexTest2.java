package com.zero.lucene;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

/**
 * 对文档进行加权
 * 使用Term做搜索
 * Term term = new Term(Field, q);
 * Query query = new TermQuery(term);
 * @author hhr
 */
public class IndexTest2 {

	/**
	 * 建立索引
	 * @Note 注意事项:  StringField 这种是不会分词的   (this is my lucene) 是一个整体
	 * @throws Exception
	 */
	@Test
	public void index() throws Exception {
		String dir = "D:\\lucene";
		Directory directory = FSDirectory.open(Paths.get(dir));
		Analyzer analyzer = new StandardAnalyzer();
		IndexWriterConfig conf = new IndexWriterConfig(analyzer);
		
		IndexWriter indexWriter = new IndexWriter(directory, conf);
		File[] files = new File("D:\\lucene\\lucene").listFiles();
		
		for (File file : files) {
			Document document = new Document();
			document.add(new StringField("title", file.getName(), Store.YES));
			TextField contentField = new TextField("contents", new FileReader(file));
			// 加权 默认都是1
			if ("pwd.txt".equals(document.get("pwd.txt"))) {
				contentField.setBoost(2f);
			}
			document.add(contentField);
			indexWriter.addDocument(document);
		}
		indexWriter.close();
	}
	
	@Test
	public void search() throws Exception {
		String dir = "D:\\lucene";
		Directory directory = FSDirectory.open(Paths.get(dir));
		IndexReader indexReader = DirectoryReader.open(directory);
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		
		Term term = new Term("title", "pwd.txt");
		Query query = new TermQuery(term);
		
//		Analyzer analyzer = new StandardAnalyzer();
//		QueryParser parser = new QueryParser("title", analyzer);
//		Query query = parser.parse("pwd.txt");
		
		TopDocs topDocs = indexSearcher.search(query, 10);
		
		for(ScoreDoc scoreDoc : topDocs.scoreDocs) {
			Document document = indexReader.document(scoreDoc.doc);
			System.out.println(document.get("title"));
		}
		
		indexReader.close();
	}

}
