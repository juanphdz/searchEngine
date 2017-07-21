public class ParsedDocument
{
	private String        docType    = new String();
	private StringBuilder title      = new StringBuilder();
	private StringBuilder header     = new StringBuilder();
	private StringBuilder body       = new StringBuilder();
	private StringBuilder emphasized = new StringBuilder();
	private StringBuilder comment    = new StringBuilder();
	private StringBuilder meta       = new StringBuilder();

	public String getDocType()
	{
		return docType;
	}

	public String getTitleText()
	{
		return title.toString();
	}

	public String getHeaderText()
	{
		return header.toString();
	}
	
	public String getBodyText()
	{
		return body.toString();
	}
	
	public String getEmphasizedText()
	{
		return emphasized.toString();
	}

	public String getCommentText()
	{
		return comment.toString();
	}

	public String getMetaText()
	{
		return meta.toString();
	}

	public void setDocType ( String text )
	{
		docType = text;
	}

	public void appendTitle ( String text )
	{
		title.append( text );
	}

	public void appendHeader ( String text )
	{
		header.append( text );
	}

	public void appendBody ( String text )
	{
		body.append( text );
	}

	public void appendEmphasized ( String text )
	{
		emphasized.append( text );
	}

	public void appendComment ( String text )
	{
		comment.append( text );
	}

	public void appendMeta ( String text )
	{
		meta.append( text );
	}

	@Override
	public String toString()
	{
		return title.toString()
				+ " " + header.toString()
				+ " " + body.toString()
				+ " " + emphasized.toString();
	}
}
