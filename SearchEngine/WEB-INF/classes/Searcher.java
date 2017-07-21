
import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;

public class Searcher
{
	private IndexSearcher indexSearcher;
	private TextFilter textFilter;

	public Searcher ( IndexReader indexReader )
	{
		indexSearcher = new IndexSearcher( indexReader );
		textFilter = new TextFilter();
	}

	public String search ( String query )
	{
		String result = null;
		try
		{
			result = displayResults( getResults( query ) );
		}
		catch ( Exception e )
		{
			result = "[ERROR] Exception raised searching for: " + query ;
		}
		
		return result;
	}

	public TopDocs getResults ( String query ) throws IOException
	{
		BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();

		for ( String s : textFilter.sift( query ).split("\\s+") )
		{
			queryBuilder.add( new TermQuery( new Term( "FileTitle", s ) ), BooleanClause.Occur.SHOULD ); 
			queryBuilder.add( new TermQuery( new Term( "FileHeader", s ) ), BooleanClause.Occur.SHOULD ); 
			queryBuilder.add( new TermQuery( new Term( "FileBody", s ) ), BooleanClause.Occur.SHOULD ); 
			queryBuilder.add( new TermQuery( new Term( "FileBold", s ) ), BooleanClause.Occur.SHOULD ); 
			queryBuilder.add( new TermQuery( new Term( "FileMeta", s ) ), BooleanClause.Occur.SHOULD ); 
		}

		BooleanQuery boolQuery = queryBuilder.build();

		return indexSearcher.search( boolQuery, 10 );
	}

	public String displayResults ( TopDocs docs ) throws IOException
	{
		StringBuilder accum = new StringBuilder();
		ScoreDoc[] hits = docs.scoreDocs;

		for ( int i = 0; i < hits.length; ++i )
		{
			int docId = hits[i].doc;
			Document d = indexSearcher.doc(docId);
			accum.append(d.get("FileURL") + ":::::" );
		}

		return accum.toString();
	}

}
