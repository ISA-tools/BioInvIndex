package uk.ac.ebi.bioinvindex.model;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 02/02/2012
 *         Time: 16:08
 */
@Entity
@Table(name = "BioEntities")
@Indexed(index = "bii")
public class BioEntity extends Identifiable {

    @Field(name = "DESCRIPTION")
    private String description;

    @Field(name = "IDENTIFIER")
    private String identifier;

    public BioEntity(String description, String identifier) {

        this.description = description;
        this.identifier = identifier;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public String toString() {
        return "BioEntity{" +
                "description='" + description + '\'' +
                ", identifier='" + identifier + '\'' +
                '}';
    }
}
