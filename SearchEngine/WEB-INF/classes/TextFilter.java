import java.util.Map;
import java.util.HashMap;

public class TextFilter
{
	private Map<String, String> acronymMap;

	public TextFilter()
	{
		acronymMap = new HashMap<String, String>();
		acronymMap.put( "ai", "artificial intelligence" );
		acronymMap.put( "ml", "machine learning" );
		acronymMap.put( "cs", "computer science" );
		acronymMap.put( "ics", "information and computer science" );
		acronymMap.put( "bios", "basic input output system" );
		acronymMap.put( "kb", "kilobyte" );
		acronymMap.put( "mb", "megabyte" );
		acronymMap.put( "gb", "gigabyte" );
		acronymMap.put( "tb", "terabyte" );
		acronymMap.put( "cpu", "central processing unit" );
		acronymMap.put( "mac", "macintosh" );
		acronymMap.put( "os", "operating system" );
		acronymMap.put( "pc", "personal computer" );
		acronymMap.put( "ram", "random access memory" );
		acronymMap.put( "rom", "read only memory" );
		acronymMap.put( "ip", "internet protocol" );
		acronymMap.put( "isp", " internet service provider" );
		acronymMap.put( "usb", "universal serial bus" );
		acronymMap.put( "vr", "virtual reality" );
	}

	public String sift ( String in )
	{
		// Currently the filter:
		// 1) removes all non-alphanumeric characters
		// 2) removes capitalization
		// 3) expands acronyms
		// 4) removes anything less than 3 characters in length

		String[] out = in.replaceAll("[^a-zA-Z0-9_ ]", " ").toLowerCase().split("\\s+");

		StringBuilder accum = new StringBuilder();

		for ( String s : out )
		{
			if ( acronymMap.containsKey( s ) )
				accum.append( acronymMap.get(s) + " " );

			if ( s.length() >= 3 )
				accum.append( s + " " );
		}

		return accum.toString();
	}
}
