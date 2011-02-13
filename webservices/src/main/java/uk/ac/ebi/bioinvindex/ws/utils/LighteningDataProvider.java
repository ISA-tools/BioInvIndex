package uk.ac.ebi.bioinvindex.ws.utils;

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

import uk.ac.ebi.bioinvindex.wsmodel.data.PropertyValuesWS;
import uk.ac.ebi.bioinvindex.wsmodel.data.DataWS;
import uk.ac.ebi.bioinvindex.wsmodel.data.AssayTypeWS;
import uk.ac.ebi.bioinvindex.wsmodel.enumerations.PropertyType;
import uk.ac.ebi.bioinvindex.model.AssayResult;
import uk.ac.ebi.bioinvindex.model.processing.Assay;
import uk.ac.ebi.bioinvindex.model.term.Characteristic;
import uk.ac.ebi.bioinvindex.model.term.Factor;
import uk.ac.ebi.bioinvindex.model.term.Parameter;
import uk.ac.ebi.bioinvindex.model.term.PropertyValue;

import java.util.List;
import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * This component is used to lighten the model objects into
 * web services oriented objects (data objects). In fact web services clients
 * don't need to know everything but a lighter part of the
 * objects.
 * @author: Manon DELAHAYE [manon@ebi.ac.uk]
 * Date: 08-Apr-2009
 */
public class LighteningDataProvider {

    /* This is a Singleton instance */
    private static LighteningDataProvider instance;

    public static LighteningDataProvider getInstance()
    {
       if (null == instance)
       {
           instance = new LighteningDataProvider();
       }
       return instance;
    }

    private LighteningDataProvider() {
    }

    /**
     * This method lightens a propertyValue object
     * @param from The initial property value from the model
     * @param to The lighter property value used for web services
     */
    public void lightenProperty(PropertyValue from, PropertyValuesWS to)
    {
        to.setType(getPropertyTypeFromClass(from.getType().getClass()));
        to.setName(from.getType().getValue());
        to.setValue(from.getValue());
        if(from.getUnit() != null)
            to.setUnit(from.getUnit().getValue());
    }

    /**
     * Get the property type (enumeration) from the type of class given (which is a PropertyValue)
     * @param cl The class of the type
     * @return The property type
     */
    public PropertyType getPropertyTypeFromClass(Class cl)
    {
        PropertyType type = null;
        if(cl == Factor.class)
            type = PropertyType.FACTOR;
        else if(cl == Characteristic.class)
            type = PropertyType.CHARACTERISTIC;
        else if(cl == Parameter.class)
            type = PropertyType.PROTOCOL;

        return type;
    }

    /**
     * This method lightens a list of property values
     * @param from The initial list of property values made with property values from the model
     * @param to The lighter list of property values made with the property values used for web services
     */
    public void lightenPropertyList(Collection<PropertyValue> from, List<PropertyValuesWS> to)
    {
        Iterator<PropertyValue> it = from.iterator();

        while(it.hasNext())
        {
            PropertyValuesWS property = new PropertyValuesWS();
            lightenProperty(it.next(), property);
            to.add(property);
        }
    }

    /**
     * This method lightens a assayImpl object
     * @param from The initial assayImpl from the model
     * @param to The lighter assayType used for web services
     */
    public void lightenAssayType(Assay from, AssayTypeWS to)
    {
        to.setMeasurement(from.getMeasurement().getName());
        to.setTechnology(from.getTechnologyName());
        to.setPlatform(from.getAssayPlatform());
    }

    /**
     * TODO we shouldn't need this one
     * This method lightens a list of assay types
     * @param from The initial list of Assays made with assays form the model
     * @param to The lighter list of assay types with the assay types used for web services
     */
    public void lightenAssayTypeList(Collection<Assay> from, List<AssayTypeWS> to)
    {
        Iterator<Assay> it = from.iterator();

        while(it.hasNext())
        {
            AssayTypeWS assayType = new AssayTypeWS();
            lightenAssayType(it.next(), assayType);
            to.add(assayType);
        }
    }

    /**
     * This method turns an assay result into a DataWS object.
     * @param from The initial assayresult containing all the information we need
     * @param to The dataWS made with the information stored in the assayresult object
     */
    public void createDataWSFromAssayResult(AssayResult from, DataWS to)
    {
	    if (from == null) return;

        to.setStudyAcc(from.getStudy().getAcc());
        to.setDataURL(from.getData().getUrl());
        to.setType(from.getData().getType().getName());
        
        // Setting the list of properties
        List<PropertyValuesWS> properties = new ArrayList<PropertyValuesWS>();
        lightenPropertyList(from.getCascadedPropertyValues(), properties);
        to.setProperties(properties);

        // Setting the list of AssayType
        List<AssayTypeWS> assayTypes = new ArrayList<AssayTypeWS>();
        lightenAssayTypeList(from.getAssays(), assayTypes);
        to.setAssayTypes(assayTypes);
    }

    /**
     * This method turns a list of AssayResult objects into a list of DataWS objects
     * @param from The list of AssayResult objects
     * @param to The list of DataWS objects created
     */
    public void createDataWSListFromAssayResultList(List<AssayResult> from, List<DataWS> to)
    {
        Iterator<AssayResult> it = from.iterator();

        while(it.hasNext())
        {
            DataWS data = new DataWS();
            createDataWSFromAssayResult(it.next(), data);
            to.add(data);
        }
    }


}