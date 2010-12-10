/*
 * IOUtils.java
 *
 * Created on July 29, 2007, 1:03 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package uk.ac.ebi.utils.io;

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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Miscellanea of small IO utilities 
 *
 * @author brandizi
 */
public class IOUtils 
{
	private IOUtils () {}
	
	/** Reads the full content of a Reader and puts it into a String */
	public static String readInputFully ( Reader rdr ) throws IOException
	{
		if ( rdr == null ) return null; 
		StringBuilder rval = new StringBuilder ( 1024 );
		int c; 
		
		while ( ( c = rdr.read () ) != -1 )
			rval.append ( (char) c );
		
		return rval.toString ();
	}
	
	/** Reads a resource from the class loader, i.e. using clazz.getResourceAsStream (),
	 *  and puts all the content in a string 
	 */
	public static String readResource ( Class clazz, String path ) throws IOException
	{
		Reader rdr = new BufferedReader ( new InputStreamReader ( clazz.getResourceAsStream ( path ) ) );
		return readInputFully ( rdr );
	}

	/**
	 * Reads the input stream and returns amn MD5 has for it
	 */
	public static String getMD5 ( InputStream is ) throws IOException, NoSuchAlgorithmException 
	{
		MessageDigest md = MessageDigest.getInstance ( "MD5" );
	  byte buffer[] = new byte [ 1024 ];
	  
	  try {
	  	for ( int read = is.read ( buffer ); read != -1; read = is.read ( buffer ) )
			 if ( read > 0 ) md.update ( buffer, 0, read );
		} 
	  finally {
			is.close();
		}
	  byte[] digest = md.digest();
	  if ( digest == null ) return null;
	  StringBuilder strDigest = new StringBuilder ();
	  for ( int i = 0; i < digest.length; i++ )
	    strDigest.append ( Integer.toString ( ( digest[i] & 0xff ) + 0x100, 16).substring ( 1 ) );
	  return strDigest.toString ();	
	}

	/**
	 * Reads the input stream and returns amn MD5 has for it
	 */
	public static String getMD5 ( File f ) throws IOException, NoSuchAlgorithmException 
	{
		return getMD5 (  new FileInputStream ( f ) );
	}
	
}
