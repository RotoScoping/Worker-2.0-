package core.model;

import core.validation.annotations.Length;

/**
 * The entity of the organization in which the worker works.
 * Organization has the following fields:
 * <ul>
 *     <li> fullName - name of organization</li>
 *     <li> type -  type of organization.</li>
 *     <li> postalAddress - address </li>
 * </ul>
 * The fields have the following constraints:
 * <ul>
 *     <li> fullName - name should not be longer 762</li>
 * </ul>
 *  @author Mark Kriger
 *  @since 15/02/2024
 */
public class Organization implements Comparable<Organization> {



    @Length(value = 762)
    private String fullName;

    private OrganizationType type;

    private Address postalAddress;

    private Organization() {}

    public static Builder builder() {
        return new Organization().new Builder();
    }

    public class Builder {

        private Builder(){}

        public Builder fullName(String fullName) {
            Organization.this.fullName = fullName;
            return this;
        }

        public Builder type(OrganizationType type) {
            Organization.this.type = type;
            return this;
        }

        public Builder postalAddress(Address postalAddress) {
            Organization.this.postalAddress = postalAddress;
            return this;
        }

        public Organization build() {
            return Organization.this;
        }
    }


    @Override
    public String toString() {
        return
                "\t\tfullName: " + fullName +
                "\n\t\ttype: " + type +
                "\n\t\tpostalAddress:\n " + postalAddress;
    }

    @Override
    public int compareTo(Organization o) {
        return o.fullName.compareTo(this.fullName);
    }

    public String getFullName() {
        return fullName;
    }

    public OrganizationType getType() {
        return type;
    }

    public Address getPostalAddress() {
        return postalAddress;
    }
}
