

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.util.Scanner;

/**
 * Servlet implementation class search
 */
@WebServlet("/search")
public class search extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public search() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    

	public static void index ( String directory, String indexPath )
	{
		try
		{
			File testFile = new File ( directory );
			Indexer indexer = new Indexer( indexPath );
			indexer.indexFiles(testFile.listFiles());
			indexer.close();
		}
		catch ( Exception e )
		{
			System.out.println( "[FATAL] failure indexing directory: " + directory );
			System.out.println( "[FATAL] Possible corrupted index: " + indexPath );
			System.exit(-1);
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("text/html"); 
		PrintWriter out = response.getWriter();
		// index ( "data", "C:/eclipse/indexs"  );
		//Reader reader = new Reader( "C:/eclipse/index" );
		Reader reader = new Reader( "/home/ubuntu/cs121/index" );
		Searcher searcher = new Searcher( reader.getReader() );
		String query = request.getParameter("query");
		
		String result = searcher.search(query);
		String [] results = result.split(":::::");
		
		for(String r : results){
			out.println("<p><a href=http://"+r+ ">" + r + "</a></p>");
		}
		



		//reader.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//doGet(request, response);
	}

}
