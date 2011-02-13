package uk.ac.ebi.bioinvindex.wsmodel.study;

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

/**
 * This class represents a contact with all the attributes required.
 * This is a lighter version from the Contact class.
 * @author: Manon DELAHAYE [manon@ebi.ac.uk]
 * Date: 31-Mar-2009
 */
public class ContactWS {

        private String firstName;
        private String lastName;
        private String midInitials;
        private String email;
        private String phone;
        private String fax;
        private String address;
        private String affiliation;
        private String url;

        public ContactWS() {
        }


        public ContactWS(String firstName, String midInitials, String lastName, String email)
        {
            this.setFirstName(firstName);
            this.setMidInitials(midInitials);
            this.setLastName(lastName);
            this.setEmail(email);
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getMidInitials() {
            return midInitials;
        }

        public void setMidInitials(String midInitials) {
            this.midInitials = midInitials;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getFax() {
            return fax;
        }

        public void setFax(String fax) {
            this.fax = fax;
        }

        public String getAddress () {
            return address;
        }

        public void setAddress ( String address ) {
            this.address = address;
        }

        public String getAffiliation() {
            return affiliation;
        }

        public void setAffiliation(String affiliation) {
            this.affiliation = affiliation;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        /*public Collection<ContactRole> getRoles () {
            return Collections.unmodifiableCollection ( roles );
        }

        protected void setRoles ( List<ContactRole> roles ) {
            this.roles = roles;
        }


        public boolean removeRole ( ContactRole role )
        {
            boolean rval = false;
            for ( ContactRole rolei: new ArrayList<ContactRole> ( roles ) ) {
                if ( rolei.equals ( role ) ) rval |= roles.remove ( rolei );
            }
            return rval;
        }

        public void addRole ( ContactRole role ) {
            roles.add ( role );
        }*/


        /** Gets Name+Middle+Surname */
        public String getFullName ()
        {
            StringBuilder sb = new StringBuilder();
            sb.append(firstName);
            if (midInitials != null && midInitials.length() > 0)
            {
                sb.append(" ");
                sb.append(midInitials);
            }
            sb.append(" ");
            sb.append(lastName);

            return sb.toString();
        }

        public String toString()
        {
            String roleS = "", sep = "";
           /* if ( roles != null ) for ( ContactRole role: roles ) {
                roleS += DataType.format ( "%s{ '%s' ( '%s' ) }", sep, role.getAcc(), role.getName() );
                sep = ", ";
            }*/
            String owner = "";
            return String.format(
                        "Contact{'%s' ('%s') '%s' <%s>\n  Roles: %s\n  Phone: '%s', Fax: '%s'\n  Affiliation: '%s', " +
                        "URL: <%s>, owner: %s }", getFirstName(), getMidInitials(), getLastName(), getEmail(), roleS, getPhone(), getFax(), 
              getAffiliation(), getUrl(), owner
            );
        }

        @Override
        public boolean equals(Object o)
        {
            /*if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;*/

            ContactWS contact = (ContactWS) o;

            if (email != null ? !email.equals(contact.email) : contact.email != null) return false;
            if (firstName != null ? !firstName.equals(contact.firstName) : contact.firstName != null) return false;
            if (lastName != null ? !lastName.equals(contact.lastName) : contact.lastName != null) return false;
            if (midInitials != null ? !midInitials.equals(contact.midInitials) : contact.midInitials != null) return false;

            return true;
        }

        @Override
        public int hashCode()
        {
            int result = super.hashCode();
            result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
            result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
            result = 31 * result + (midInitials != null ? midInitials.hashCode() : 0);
            result = 31 * result + (email != null ? email.hashCode() : 0);
            return result;
        }
}
