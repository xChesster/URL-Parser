// Paul Lanham
// G00974343
// CS 330-002
import java.util.regex.*;
import java.io.*;
import java.util.*;
import java.net.*;

public class URLLexer
	{
	// These are the 7 tokens in our simplified URL definition
	public static final int PROTOCOL = 0;
	public static final int NUMERICAL_ADDRESS = 1;
	public static final int NON_NUMERICAL_ADDRESS = 2;
	public static final int PORT = 3;
	public static final int FILE = 4;
	public static final int FRAGMENT = 5;
	public static final int QUERY = 6;
	
   // private variables
   private String[] tokens;
   private int tokenIndex;
   private String url;
   private int matchingIndex;
   private int position;
   private boolean done;
   
	// Here you place regular expressions, one per token.  Each is a string.
	public static final String[] REGULAR_EXPRESSION = 
		new String[]
			{
			"\\p{Alpha}*://",  // protocol
			"(?:[0-2][0-5][0-5]|[0-9][0-9]|[0-9])[.](?:[0-2][0-5][0-5]|[0-9][0-9]|[0-9])[.](?:[0-2][0-5][0-5]|[0-9][0-9]|[0-9])[.](?:[0-2][0-5][0-5]|[0-9][0-9]|[0-9])", // numerical address
			"((?:\\p{Alnum}|[-])+[.])+((?:\\p{Alnum}|[-])+)", // non-numerical address, (upper/lowercase letters,#'s,hyphens)
			":\\p{Digit}+", // port
			"/[^#?]*", // file
			"[#].*", // fragment
			"[?].*", // query
			};

	// This is an array of names for each of the tokens, which might be convenient for you to
	// use to print out stuff.
	public static final String[] NAME = new String[] { "protocol", "numerical address", "non-numerical address",
													   "port", "file", "fragment", "query" };
	
	/** Creates a Blank URLLexer set up to do pattern-matching on the given regular expressions. */
	public URLLexer() {
		// IMPLEMENT ME (ABOUT 5 LINES)
      this.tokens = new String[7];
      this.tokenIndex = 0;
      this.url = "";
      this.matchingIndex = 0;
      this.position = 0;
      this.done = false;
	}
	
	// resets the tokenizer to the beginning of string, 
   // and sets up any other variables you may need to 
   // keep track of, such as current position in the input, the
   // matching index for the token, etc
	public void reset(String input) {
		// IMPLEMENT ME (ABOUT 3 LINES)
      this.url = input;
      this.tokenIndex = 0;
      this.tokens = new String[7];
      this.position = 0;
      this.matchingIndex = 0;
      this.done = false;
   }
	
   // returns the index into the array of regular expression strings
   // which matched the token which had recently been returned by nextToken()
	public int getMatchingIndex() {
		// IMPLEMENT ME (ABOUT 1 LINE)
      return this.matchingIndex;
	}
	
   // returns current position in the token stream 
   // where the next token will be extracted.
	public int getPosition() { 
		// IMPLEMENT ME (ABOUT 1 LINE)
      return this.position;   
	}
	
   // provides the next token, else null if no more tokens, 
   // or when it encounters text in the string which can’t be 
   // tokenized by any of the regular expressions provided.
	public String nextToken() {
		// IMPLEMENT ME (ABOUT 10 LINES)
      Pattern regex;
      Matcher matcher;
      // checking if end of url has been reached
      if(url.length() == 0) {
         done = true;
         return "";
      }
      int tempPos = 0;
      int start = 0;
      // loop until match is found
      for(int i = 0; i < 7; i++) {
         regex = Pattern.compile(REGULAR_EXPRESSION[i], Pattern.DOTALL);
         matcher = regex.matcher(this.url);
         
         // checking if there is a match starting from the current position
         // in input string
         if(matcher.find(0)) {
            // updating the position
            tempPos = matcher.end();
            // checking for duplicate entry in tokens[]
            if(this.tokens[i] != null) {
               continue;
            }
            // storing subsequence in token array at the index of the regex
            // used to match it
            start = matcher.start();
            this.position = tempPos;
            this.url = url.substring(0, start) + url.substring(position, url.length());
            this.tokens[i] = matcher.group();
            // updating matchingIndex value so that the token can be printed in MAIN()
            this.matchingIndex = i;
            // returning matching subsequence
            return this.tokens[i];
         }
      }
      return "invalid";
	}
   
   // private helper method
   private void printToken(int i) {
      switch(i) {
         case 0:
            System.out.println("PROTOCOL: " + this.tokens[PROTOCOL]);
            break;
         case 1:
            System.out.println("NUMERICAL_ADDRESS: " + this.tokens[NUMERICAL_ADDRESS]);
            break;
         case 2:
            System.out.println("NON_NUMERICAL_ADDRESS: " + this.tokens[NON_NUMERICAL_ADDRESS]);
            break;
         case 3:
            System.out.println("PORT: " + this.tokens[PORT]);
            break;
         case 4:
            System.out.println("FILE: " + this.tokens[FILE]);
            break;
         case 5:
            System.out.println("FRAGMENT: " + this.tokens[FRAGMENT]);
            break;
         case 6:
            System.out.println("QUERY: " + this.tokens[QUERY]);
            break;
      }
   }
   
   // private helper method
   private boolean urlParsed() {
      return done;
   }
   
   // private helper method
   private String getToken(int index) {
      return this.tokens[index];
   }

	public static void main(String[] args) throws IOException {
		// IMPLEMENT ME.
		//
		// You will repeatedly request a URL by printing "URL: ".  Once the user has provided
		// a URL, you will trim it of whitespace, then tokenize it.  As you tokenize it you 
		// will print out the tokens one
		// by one, including their token types.  If you find a duplicate token type, you will
		// FAIL.  You will also FAIL if the tokenizer cannot recognize any further tokens but
		// you still have characters left to tokenize.  If you manage to finish tokenizing
		// a URL, you will pass the tokens to the fetch(...) function provided below.  Whenever
		// a failure occurs, you will indicate it, then loop again to request another URL.
      
      URLLexer lexer = new URLLexer();
      Scanner sc = new Scanner(System.in);
      System.out.print("URL: ");
      // looping until there are no more inputs
      while(sc.hasNext()) {
         // getting url line
         lexer.reset(sc.nextLine().trim());
         // tokenize url
         while(!(lexer.nextToken().equals("invalid"))) {
            // checking if url has been fully parsed successfully
            if(lexer.urlParsed())
               break;
            // printing token and token type each time a token is found
            lexer.printToken(lexer.getMatchingIndex());
         }
         // checking if url has been parsed successfully and calling
         // fetch if it has
         if(lexer.urlParsed()) {
            fetch(lexer.getToken(PROTOCOL), lexer.getToken(NUMERICAL_ADDRESS),
                  lexer.getToken(NON_NUMERICAL_ADDRESS), lexer.getToken(PORT),
                  lexer.getToken(FILE), lexer.getToken(QUERY), lexer.getToken(FRAGMENT));
         }
         // printing message notifying user of invalid url
         else {
            System.out.println("Entered URL is invalid.");
         }
         // requesting new URL
         System.out.print("URL: ");
      }
	}

	// perhaps this function might come in use.
	// It takes various tokenized values, checks them for validity, then fetches the data
	// from a URL formed by them and prints it to the screen.
	public static void fetch(String protocol, String numericalAddress, String nonNumericalAddress,
							String port, String file, String query, String fragment) {
		String address = numericalAddress;
		int iport = 80;

		// verify the URL
		if (protocol == null || !protocol.equals("http://"))
			{
			System.out.println("ERROR. I don't know how to use protocol " + protocol);
			}
		else if (query != null)
			{
			System.out.println("ERROR. I'm not smart enough to issue queries, like " + query); 
			}
		else if (numericalAddress == null && nonNumericalAddress == null)
			{
			System.out.println("ERROR. No address was provided.");
			}
		else if (numericalAddress != null && nonNumericalAddress != null)
			{
			System.out.println("ERROR. Both types of addresses were provided.");
			}
		else
			{
			if (address == null)
				{
				address = nonNumericalAddress;
				}
			if (fragment != null)
				{
				System.out.println("NOTE. Fragment provided: I will not use it.");
				}
			if (port != null)
				{
				iport = Integer.parseInt(port.substring(1));  // strip off the ":"
				}
			else
				{
				System.out.println("NOTE. No port provided, defaulting to port 80.");
				}
			if (file == null)
				{
				System.out.println("NOTE. No file was provided.  Assuming it's just /");
				file = "/";
				}
			
			System.out.println("Downloading ADDRESS: " + address + " PORT: " + iport + " FILE: " + file);
			System.out.println("\n=======================================");
	
			java.io.InputStream stream = null;
			try
				{				
				java.net.URL url = new java.net.URL("http", address, iport, file);
				java.net.URLConnection connection = url.openConnection();
				connection.connect();
				stream = connection.getInputStream();
				final int BUFLEN = 1024;
				byte[] buffer = new byte[BUFLEN];
				while(true)
					{
					int len = stream.read(buffer, 0, BUFLEN);
					if (len <= 0) break;
					System.out.write(buffer, 0, len);
					}
				}
			catch (java.io.IOException e)
				{
				System.out.println("Error fetching data.");
				}
			try
				{
				if (stream != null) stream.close();
				}
			catch (java.io.IOException e) { }
			System.out.println("\n=======================================");
			}
		}
	}