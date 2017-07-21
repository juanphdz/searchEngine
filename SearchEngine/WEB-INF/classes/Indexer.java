import java.io.File;
import java.io.IOException;
import java.io.FileReader;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import org.jsoup.Jsoup;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Indexer
{
	private IndexWriterConfig  indexConfig;
	private StandardAnalyzer   analyzer;
	private IndexWriter        indexWriter;
	private Directory          indexDirectory;
	private TextFilter         filter;
	private JSONObject         urlDatabase;

	public Indexer ( String indexPath )
	{
		try
		{
			File indexFile = new File ( indexPath );
			indexDirectory = FSDirectory.open( indexFile.toPath() );
			analyzer       = new StandardAnalyzer();
			indexConfig    = new IndexWriterConfig ( analyzer );
			indexWriter    = new IndexWriter ( indexDirectory, indexConfig );
			filter         = new TextFilter();
			urlDatabase    = new JSONObject();
		}
		catch ( Exception e )
		{
			System.out.println( "[FATAL] Error opening Indexer." );
			System.exit(-1);
		}
	}
	public void close()
	{
		try
		{
			indexWriter.close();
		}
		catch ( Exception e )
		{
			System.out.println( "[FATAL] Error closing Indexer." );
			System.exit(-1);
		}
	}

	public void addURLDatabase ( File urlFile )
	{
		JSONParser urlParser = new JSONParser();
		try
		{
			urlDatabase = (JSONObject) urlParser.parse( new FileReader( urlFile ) );
		}
		catch ( Exception e )
		{
			System.out.println( "[FATAL] Error reading URL Database." );
			System.exit(-1);
		}
	}


	public ParsedDocument parseFile ( File file ) throws Exception
	{
		org.jsoup.nodes.Document doc = Jsoup.parse( file, "UTF-8", "" );
		ParsedDocument parsedDoc = new ParsedDocument();

		parsedDoc.appendTitle( filter.sift( doc.select( "title, h1" ).text() ) );
		parsedDoc.appendHeader( filter.sift( doc.select( "h2, h3, h4, h5, h6" ).text() ) );
		parsedDoc.appendBody( filter.sift( doc.select( "p, ol, td, tl, li, ul" ).text() ) );
		parsedDoc.appendEmphasized( filter.sift( doc.select( "b, em, strong, u" ).text() ) );
		parsedDoc.appendMeta( filter.sift( doc.select( "meta" ).text() ) );

		return parsedDoc;
	}

	public void indexFile ( File file )
	{
		try
		{
			Document doc   = new Document();

			// Set Options
			FieldType type = new FieldType();
			type.setStoreTermVectors( true );
			type.setIndexOptions( IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS );
			type.setStored( true );

			// Record file path and URL
			String filePath = file.toPath().toString().replaceAll( "\\\\", "/" ).replaceFirst( "/", "~~" ).replaceAll( ".*~~", "" );
			String fileURL  = (String) urlDatabase.get( filePath );

			doc.add( new Field ( "FilePath", filePath, type ) );
			doc.add( new Field ( "FileURL",  fileURL,  type ) );


			// Parse file into 5 sections
			ParsedDocument parsedDocument;
			try
			{
				parsedDocument = parseFile( file );
			}
			catch ( Exception e )
			{
				parsedDocument = new ParsedDocument();
			}

			Field titleField  = new Field ( "FileTitle", parsedDocument.getTitleText() + " " + filter.sift( fileURL ), type );
			Field headerField = new Field ( "FileHeader", parsedDocument.getHeaderText(), type );
			Field bodyField   = new Field ( "FileBody", parsedDocument.getBodyText(), type );
			Field boldField   = new Field ( "FileBold", parsedDocument.getEmphasizedText(), type );
			Field metaField   = new Field ( "FileMeta", parsedDocument.getMetaText(), type );

			// Set Field Weights
			titleField.setBoost(2f);
			headerField.setBoost(1.4f);
			bodyField.setBoost(1f);
			boldField.setBoost(1.1f);
			metaField.setBoost(0.4f);

			// Create a lucene document
			doc.add( titleField );
			doc.add( headerField );
			doc.add( bodyField );
			doc.add( boldField );
			doc.add( metaField );

			// Index it
			indexWriter.addDocument( doc );
		}
		catch ( Exception e )
		{
			//e.printStackTrace();
			System.out.println( "[ERROR] Unable to index file: " + file.toPath().toString() );
		}
	}

	public void indexFiles ( File files[] )
	{
		for ( File file : files )
		{
			if ( !file.exists() || !file.canRead() || file.isHidden() )
				continue;

			if ( file.isDirectory() )
			{
				indexFiles ( file.listFiles() );
				continue;
			}

			indexFile( file );
		}
	}
}
