package uk.ac.ebi.utils.string;

import org.apache.commons.lang.StringUtils;


public class StringSearchUtils
{
	private StringSearchUtils () {}
	
	/**
	 * Tells if the string contains one of the matches. This is often used to detect which type of material/data one has.
	 */
  public static boolean containsOneOf ( String target, String... matches ) 
  {
  	if ( target == null ) throw new IllegalArgumentException ( 
  		"StringSearchUtils.containsOne(): target is null!" 
  	);
  	if ( matches == null || matches.length == 0 ) throw new IllegalArgumentException ( 
  		"containsOne(): no match to check!" 
  	);
  	for ( String match: matches )
  		if ( target.contains ( match ) ) return true;
    return false;
  }

  /**
	 * Tells if the string contains one of the matches. This is often used to detect which type of material/data one has.
	 */
  public static boolean containsOneOfIgnoreCase ( String target, String... matches ) 
  {
  	if ( target == null ) throw new IllegalArgumentException ( 
  		"StringSearchUtils.containsOne(): target is null!" 
  	);
  	if ( matches == null || matches.length == 0 ) throw new IllegalArgumentException ( 
    	"containsOne(): no match to check!" 
  	);
  	for ( String match: matches )
  		if ( StringUtils.containsIgnoreCase ( target, match ) ) return true;
    return false;
  }

}
