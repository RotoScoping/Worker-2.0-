package org.example.model;



import org.example.validation.annotations.NotNull;

import java.io.Serializable;
import java.util.Objects;

/**
 * Organization address.
 * Location has the following fields:
 * <ul>
 *     <li> zipCode - mail code</li>
 *     <li> town - organization location</li>
 * </ul>
 * The fields have the following constraints:
 * <ul>
 *     <li> zipCode - not null</li>
 *     <li> town - not null</li>
 * </ul>
 *
 * @author Mark Kriger
 * @since 15 /02/2024
 */
public class Address implements Serializable {

    @NotNull
    private String zipCode;

    @NotNull
    private Location town;

    private Address() {}

    /**
     * Builder builder.
     *
     * @return the builder
     */
    public static Builder builder() {
        return new Address().new Builder();
    }

    /**
     * Gets zip code.
     *
     * @return the zip code
     */
    public String getZipCode() {
        return zipCode;
    }

    /**
     * Gets town.
     *
     * @return the town
     */
    public Location getTown() {
        return town;
    }

    /**
     * The type Builder.
     */
    public class Builder {

        private Builder(){}

        /**
         * Zip code builder.
         *
         * @param zipCode the zip code
         * @return the builder
         */
        public Builder zipCode(String zipCode) {
            Address.this.zipCode = zipCode;
            return this;
        }

        /**
         * Town builder.
         *
         * @param town the town
         * @return the builder
         */
        public Builder town(Location town) {
            Address.this.town = town;
            return this;
        }


        /**
         * Build address.
         *
         * @return the address
         */
        public Address build() {
            return Address.this;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(zipCode, address.zipCode) && Objects.equals(town, address.town);
    }

    @Override
    public int hashCode() {
        return Objects.hash(zipCode, town);
    }

    @Override
    public String toString() {
        return "\t\t\t" +
               "zipCode: " + zipCode +
               "\n\t\t\t town: " + town + "\n";
    }
}