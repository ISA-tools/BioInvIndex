package uk.ac.ebi.utils.regex;

import java.util.regex.Pattern;

import static java.lang.System.out;
import static org.junit.Assert.*;

import org.junit.Test;

public class RegExTest
{
	@Test
	public void testMatchesAny ()
	{
		out.println ( "\n\n\n________ Testing RegEx.testMatchesAny() ______" );
		assertTrue ( "Wrong result for matchesAny()!", 
			RegEx.matchesAny ( 
				"A test String", 
				Pattern.compile ( "foo" ), 
				Pattern.compile ( "^.*TEST.*$", Pattern.CASE_INSENSITIVE ) )
		);
		out.println ( "\n\n___________ /end: Testing RegEx.testMatchesAny() _________" );
	}
}
