package com.zero.lucene;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Before;
import org.junit.Test;

/**
 * 对索引的增删改
 * @author hhr
 *
 */
public class IndexTest {

	private IndexWriter writer ;
	
	/**
	 * 实例化IndexWriter对象
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		String dir = "D:\\lucene";
		Directory directory = FSDirectory.open(Paths.get(dir));
		Analyzer analyzer = new StandardAnalyzer();
		IndexWriterConfig conf = new IndexWriterConfig(analyzer);
		writer = new IndexWriter(directory, conf);
	}
	
	/**
	 * 获取 IndexReader对象
	 * @return
	 * @throws IOException
	 */
	private IndexReader getIndexReader() throws IOException {
		Directory dir = FSDirectory.open(Paths.get("D:\\lucene"));
		IndexReader indexReader = DirectoryReader.open(dir);
		return indexReader;
	}

	/**
	 * 获取 IndexSearcher对象
	 * @return
	 * @throws IOException
	 */
	private IndexSearcher getIndexSearcher() throws IOException {
		IndexReader indexReader = getIndexReader();
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		return indexSearcher;
	}
	
	// add delete update search
	
	/**
	 * 创建索引
	 * @throws IOException
	 */
	@Test
	public void createIndex() throws IOException {
		// 需要索引的文件夹
		File[] files = new File("D:\\lucene\\lucene").listFiles();
		for (File file : files) {
			Document doc = new Document();
			doc.add(new StringField("title", file.getName(), Field.Store.YES));
			doc.add(new TextField("contents", new FileReader(file)));
			writer.addDocument(doc);
		}
		System.out.println(writer.numDocs());
		writer.close();
	}
	
	/**
	 * 标记删除 只是一个标记状态 没有真的删除
	 * @throws Exception
	 */
	@Test
	public void delBeforeMerge() throws Exception{
		writer.deleteDocuments(new Term("title", "pwd.txt"));
		writer.commit();
		IndexReader indexReader = getIndexReader();
		int numDocs = indexReader.numDocs();
		System.out.println("除去标记的文件数据: " + numDocs);
		System.out.println("加上标记的所有文件数量" + indexReader.maxDoc());
		writer.close();
		indexReader.close();
	}
	
	/**
	 * 强制删除(合并索引) 一边删除一边合并合并
	 * 非常的消耗IO流
	 * @throws Exception
	 */
	@Test
	public void delAfterFocus() throws Exception {
		writer.deleteDocuments(new Term("title", "pwd.txt"));
		writer.forceMergeDeletes(); // 强制删除
		writer.commit();
		IndexReader indexReader = getIndexReader();
		int numDocs = indexReader.numDocs();
		System.out.println("除去标记的文件数据: " + numDocs);
		System.out.println("加上标记的所有文件数量" + indexReader.maxDoc());
		writer.close();
		indexReader.close();
	}
	
	/**
	 * 修改索引 (应该是先删除 后添加  如果没有删除的 就直接添加 修改之前的分词也可以用)
	 * @throws Exception
	 */
	@Test
	public void updateIndex() throws Exception {
		search("pwd.txt");
		Document doc = new Document();
		doc.add(new StringField("title", "密码.txt", Field.Store.YES));
		writer.updateDocument(new Term("title", "pwd.txt"), doc);
		writer.commit();
		writer.close();
		search("密码");
	}
	
	private void search(String q) throws IOException, ParseException {
		IndexSearcher indexSearcher = getIndexSearcher();
		Analyzer analyer = new StandardAnalyzer();
		QueryParser parser = new QueryParser("title", analyer);
		Query query = parser.parse(q);
		TopDocs topDocs = indexSearcher.search(query, 10);
		IndexReader indexReader = null;
		for(ScoreDoc scoreDoc : topDocs.scoreDocs) {
			indexReader = getIndexReader();
			Document document = indexReader.document(scoreDoc.doc);
			System.out.println("fileName: " + document.get("title"));
		}
		
		if(null != indexReader) {
			indexReader.close();
		}
	}
	
	@Test
	public void getSearch() throws IOException, ParseException {
		search("密码.txt");
	}
	
}
