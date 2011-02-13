//package uk.ac.ebi.bioinvindex.services;
//
//import static org.jboss.seam.ScopeType.STATELESS;
//import org.jboss.seam.annotations.Factory;
//import org.jboss.seam.annotations.In;
//import org.jboss.seam.annotations.Name;
//import org.jboss.seam.annotations.Scope;
//import org.jboss.seam.annotations.web.RequestParameter;
//import uk.ac.ebi.bioinvindex.dao.ejb3.StudyEJB3DAO;
//import uk.ac.ebi.bioinvindex.model.impl.Study;
//
///**
// * User: Nataliya Sklyar (nsklyar@ebi.ac.uk)
// * Date: Nov 5, 2007
// */
//@Name("studyBeanAction")
//@Scope(STATELESS)
//public class StudyBeanAction {
//
//	@In
//	private StudyEJB3DAO studyEJB3DAO;
//
//	@In
//	private SourceURLResolver sourceURLResolver;
//
////	@RequestParameter
//	private String studyId;
//
//	@Factory("studyBean")
//	public StudyBean createStudyBean() {
//		Study study = studyEJB3DAO.getByAcc(studyId);
//		if (study == null) {
//			throw new BIIException("Study with id = " + studyId + " is not available");
//		}
//
//		StudyBeanImpl studyBean = new StudyBeanImpl(study);
//		studyBean.setStudyDao(studyEJB3DAO);
//		studyBean.setSourceURLResolver(sourceURLResolver);
//
//		return studyBean;
//	}
//
//	public String getStudyId() {
//		System.out.println("StudyBeanAction.getStudyId = " + studyId);
//		return studyId;
//	}
//
//	public void setStudyId(String studyId) {
//		System.out.println("StudyBeanAction.setStudyId = " + studyId);
//		this.studyId = studyId;
//	}
//}
