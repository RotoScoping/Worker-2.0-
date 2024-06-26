package org.example.model;


import org.example.validation.annotations.Length;

import java.io.Serializable;
import java.util.Objects;

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
 *
 * @author Mark Kriger
 * @since 15 /02/2024
 */
public class Organization implements Comparable<Organization>, Serializable {



    private int id;

    @Length(value = 762)
    private String fullName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Organization that = (Organization) o;
        return Objects.equals(fullName, that.fullName) && type == that.type && Objects.equals(postalAddress, that.postalAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullName, type, postalAddress);
    }

    private OrganizationType type;

    private Address postalAddress;

    private Organization() {}

    /**
     * Builder builder.
     *
     * @return the builder
     */
    public static Builder builder() {
        return new Organization().new Builder();
    }

    /**
     * The type Builder.
     */
    public class Builder {

        private Builder(){}

        /**
         * Full name builder.
         *
         * @param fullName the full name
         * @return the builder
         */
        public Builder fullName(String fullName) {
            Organization.this.fullName = fullName;
            return this;
        }

        public Builder id(int id) {
            Organization.this.id = id;
            return this;
        }

        /**
         * Type builder.
         *
         * @param type the type
         * @return the builder
         */
        public Builder type(OrganizationType type) {
            Organization.this.type = type;
            return this;
        }

        /**
         * Postal address builder.
         *
         * @param postalAddress the postal address
         * @return the builder
         */
        public Builder postalAddress(Address postalAddress) {
            Organization.this.postalAddress = postalAddress;
            return this;
        }

        /**
         * Build organization.
         *
         * @return the organization
         */
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

    /**
     * Gets full name.
     *
     * @return the full name
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public OrganizationType getType() {
        return type;
    }

    /**
     * Gets postal address.
     *
     * @return the postal address
     */
    public Address getPostalAddress() {
        return postalAddress;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
