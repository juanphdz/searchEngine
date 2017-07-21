
import java.io.File;
import java.util.Set;
import java.util.HashSet;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.document.Document;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

public class Reader
{
	private IndexReader indexReader;
	private Directory   indexDirectory;

	public Reader ( String indexPath )
	{
		try
		{
			File indexFile = new File ( indexPath );
			indexDirectory = FSDirectory.open( indexFile.toPath() );
			indexReader    = DirectoryReader.open( indexDirectory );
		}
		catch ( Exception e )
		{
			System.out.println( "[FATAL] Error opening reader." );
			System.exit(-1);
		}
	}

	public void close()
	{
		try
		{
			indexReader.close();
		}
		catch ( Exception e )
		{
			System.out.println( "[FATAL] Error closing reader." );
			System.exit(-1);
		}
	}

	public IndexReader getReader()
	{
		return indexReader;
	}

	public int numDocs()
	{
		return indexReader.numDocs();
	}

	public long numUniqueTerms()
	{
		Set<String> termSet = new HashSet<String>();
		try
		{
			Fields fields = MultiFields.getFields( indexReader );

			for ( String field : fields )
			{
				try
				{
					Terms terms = fields.terms(field);
					TermsEnum termsEnum = terms.iterator();
					while (  true )
					{
						try
						{
							BytesRef term = termsEnum.next();
							if ( term == null ) break;
							termSet.add( term.utf8ToString() );
						}
						catch ( Exception e )
						{
							continue;
						}
					}
				}
				catch ( Exception e )
				{
					continue;
				}
			}
		}
		catch ( Exception e )
		{
			System.exit(-1);
		}
		return termSet.size();
	}
}
