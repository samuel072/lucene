package com.zero.lucene;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * 通过索引查询文件内容
 * @author hhr
 *
 */
public class Searcher {
	
	private IndexReader reader;
	private IndexSearcher searcher;
	
	/**
	 * 构造函数 实例化reader和searcher 对象
	 * @param indexDir
	 * @param q
	 * @throws Exception
	 */
	public Searcher(String indexDir) throws Exception {
		Directory directory = FSDirectory.open(Paths.get(indexDir));
		reader = DirectoryReader.open(directory);
		searcher = new IndexSearcher(reader);
	}
	
	/**
	 * 关闭读取流
	 * @throws IOException
	 */
	public void close() throws IOException {
		reader.close();
	}
	
	/**
	 * 根据条件查询出内容
	 * @param field			索引字段
	 * @param q				查询的条件
	 * @throws Exception
	 */
	public void query(String field, String q) throws Exception{
		// 标准的英文分词器
		Analyzer analyzer = new StandardAnalyzer();
		// 构造一个解析器
		QueryParser parser = new QueryParser(field, analyzer);
		// 根据条件解析
		Query query = parser.parse(q);
		// 拿到返回值 docment对象集合
		TopDocs topDocs = searcher.search(query, 10);
		// 拿到对象集合中的array 里面只有三个参数  docId, 
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		
		// 遍历
		for (ScoreDoc scoreDoc : scoreDocs) {
			int docId = scoreDoc.doc;
			// 拿到文档Id 然后根据文档Id读取信息
			Document document = reader.document(docId);
			String fileName = document.get("title");
			System.out.println("索引的文件名称是: " + fileName);
		}
	}
	
	public static void main(String[] args) {
		String indexDir = "D://lucene";
		String field = "contents";
		String query = "hhr360";
		Searcher searcher = null;
		try {
			searcher = new Searcher(indexDir);
			searcher.query(field, query);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				searcher.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
