package uk.ac.ebi.utils.memory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * An helper that allows to release/reset resources when free memory goes beyond a given limit. This is needed in certain
 * cases, eg, when it's not clear why Hibernate keep filling up the memory and it has been proved that reinitialising 
 * the enity manager fixes the problem.
 *
 * <dl><dt>date</dt><dd>Jul 11, 2011</dd></dl>
 * @author brandizi
 *
 */
public class MemoryUtils
{
	@SuppressWarnings ( "unused" )
	private MemoryUtils () {}

	private final static Logger log = LoggerFactory.getLogger ( MemoryUtils.class );

	
	/**
	 * Invoke this when you think it's safe to invoke the action (eg, when you can get rid of
	 * all the objects and connections in an Hibernate entity manager). The method checks the amount of free memory 
	 * still available to the JVM ({@link Runtime#freeMemory()} / {@link Runtime#totalMemory()}) and, if this is &lt;
	 * minFreeMemory, executes the action and then invokes the {@link Runtime#gc() garbage collector}.
	 * The event triggering is also logged with trace level.
	 * 
	 * Note the method is synchronised, so that you can avoid that a number of threads trigger a memory reset almost
	 * simultaneously. 
	 */
	public static synchronized boolean checkMemory ( Runnable action, double minFreeMemory )
	{
		Runtime runtime = Runtime.getRuntime ();
		float freeMemRatio = ( 1.0f * runtime.freeMemory () ) / runtime.totalMemory ();
		if ( freeMemRatio < minFreeMemory )
		{
			if ( log.isTraceEnabled () )
			{
				log.trace ( String.format (  
					"Invoking memory cleaning to increase the quota of %.1f%% free memory", freeMemRatio * 100
				));
			}
			action.run ();
			runtime.gc ();
			return true;
		}
		return false;
	}

	/**
	 * Defaults to 0.2 (20%).
	 * 
	 */
	public static synchronized boolean checkMemory ( Runnable action ) {
		return checkMemory ( action, 0.2 );
	}

}
