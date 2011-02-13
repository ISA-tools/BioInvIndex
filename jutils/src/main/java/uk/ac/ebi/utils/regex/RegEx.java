package uk.ac.ebi.utils.regex;

/*
 * __________
 * CREDITS
 * __________
 *
 * Team page: http://isatab.sf.net/
 * - Marco Brandizi (software engineer: ISAvalidator, ISAconverter, BII data management utility, BII model)
 * - Eamonn Maguire (software engineer: ISAcreator, ISAcreator configurator, ISAvalidator, ISAconverter,  BII data management utility, BII web)
 * - Nataliya Sklyar (software engineer: BII web application, BII model,  BII data management utility)
 * - Philippe Rocca-Serra (technical coordinator: user requirements and standards compliance for ISA software, ISA-tab format specification, BII model, ISAcreator wizard, ontology)
 * - Susanna-Assunta Sansone (coordinator: ISA infrastructure design, standards compliance, ISA-tab format specification, BII model, funds raising)
 *
 * Contributors:
 * - Manon Delahaye (ISA team trainee:  BII web services)
 * - Richard Evans (ISA team trainee: rISAtab)
 *
 *
 * ______________________
 * Contacts and Feedback:
 * ______________________
 *
 * Project overview: http://isatab.sourceforge.net/
 *
 * To follow general discussion: isatab-devel@list.sourceforge.net
 * To contact the developers: isatools@googlegroups.com
 *
 * To report bugs: http://sourceforge.net/tracker/?group_id=215183&atid=1032649
 * To request enhancements:  http://sourceforge.net/tracker/?group_id=215183&atid=1032652
 *
 *
 * __________
 * License:
 * __________
 *
 * This work is licenced under the Creative Commons Attribution-Share Alike 2.0 UK: England & Wales License. To view a copy of this licence, visit http://creativecommons.org/licenses/by-sa/2.0/uk/ or send a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco, California 94105, USA.
 *
 * __________
 * Sponsors
 * __________
 * This work has been funded mainly by the EU Carcinogenomics (http://www.carcinogenomics.eu) [PL 037712] and in part by the
 * EU NuGO [NoE 503630](http://www.nugo.org/everyone) projects and in part by EMBL-EBI.
 */

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An helper class for the regex library. Easier calls to the RE functions, plus 
 * caching of the pattern, which may speed up your code. 
 *
 * @author brandizi, copied from the library org.brandizi.jutils.
 * 
 */
public class RegEx 
{
  private Pattern pattern;
	
	/** Creates and store a new Pattern, calling {@link Pattern#compile(String)} */
	public RegEx ( String pattern ) {
	  this.pattern = Pattern.compile ( pattern ); 
	}	

	/** Creates and store a new Pattern, calling {@link Pattern#compile(String, int)} */
	public RegEx ( String pattern, int flags ) {
	  this.pattern = Pattern.compile ( pattern, flags ); 
	}	

	
	
	/** Gets a new matcher for the input sequence */
	public Matcher matcher ( CharSequence input ) {
		return pattern.matcher ( input );
	}


	/** Returns all the groups in the input, see Java documentation for details */
	public String[] groups ( CharSequence input ) 
	{
		Matcher matcher = matcher ( input );
		if ( !matcher.matches () ) 
			return null;

		int n = matcher.groupCount (); 
		String[] result = new String [ n + 1 ];
		for ( int i = 0; i <= n; i++ )
			result [ i ] = matcher.group ( i );

		return result;
	}  
	
	
	/** Calls {@link #matcher(CharSequence) matcher(input)}.{@link Matcher#matches()} */
	public boolean matches ( CharSequence input ) {
		return matcher ( input ).matches ();
	}

	public String getPattern () {
		return pattern.toString ();
	}


	/** Matches the string against any of the patterns, returns true at the first pattern that matches */
	public static boolean matchesAny ( String target, Pattern... patterns )
	{
		for ( Pattern pattern: patterns ) 
			if ( pattern.matcher ( target ).matches () ) return true;
		return false;
	}
}
