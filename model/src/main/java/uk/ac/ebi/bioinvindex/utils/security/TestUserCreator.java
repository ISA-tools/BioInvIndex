package uk.ac.ebi.bioinvindex.utils.security;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;


import uk.ac.ebi.bioinvindex.dao.UserDAO;
import uk.ac.ebi.bioinvindex.dao.ejb3.DaoFactory;
import uk.ac.ebi.bioinvindex.model.security.Person;
import uk.ac.ebi.bioinvindex.model.security.UserRole;
import uk.ac.ebi.bioinvindex.utils.StringEncryption;

public class TestUserCreator
{
	public static void main ( String args[] )
	{
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory ( "BIIEntityManager" );
		EntityManager entityManager = entityManagerFactory.createEntityManager ();

		Person user = new Person ();
		user.setUserName ( "brandizi" );
		user.setEmail ( "brandizi@ebi.ac.uk" );
		user.setAddress ( "EBI" );
		user.setAffiliation ( "EBI" );
		user.setFirstName ( "Marco" );
		user.setLastName ( "Brandizi" );
		user.setRole ( UserRole.CURATOR );
		user.setJoinDate ( new Date() );
		user.setPassword ( StringEncryption.getInstance().encrypt( "lunchbox" ) );

		DaoFactory daoFactory = DaoFactory.getInstance ( entityManager );
		UserDAO userDAO = daoFactory.getUserDAO ();
		EntityTransaction tns = entityManager.getTransaction ();
		tns.begin ();
		Long id = userDAO.save ( user );
		tns.commit ();

		System.out.println ( "User saved, id #" + id );
	}
}
