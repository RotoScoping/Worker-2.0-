package core.model;


import core.validation.annotations.NotNull;

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
 *  @author Mark Kriger
 *  @since 15/02/2024
 */

public class Address {

    @NotNull
    private String zipCode;

    @NotNull
    private Location town;

    private Address() {}

    public static Builder builder() {
        return new Address().new Builder();
    }

    public String getZipCode() {
        return zipCode;
    }

    public Location getTown() {
        return town;
    }

    public class Builder {

        private Builder(){}

        public Builder zipCode(String zipCode) {
            Address.this.zipCode = zipCode;
            return this;
        }

        public Builder town(Location town) {
            Address.this.town = town;
            return this;
        }


        public Address build() {
            return Address.this;
        }
    }

    @Override
    public String toString() {
        return "\t\t\t" +
               "zipCode: " + zipCode +
               "\n\t\t\t town: " + town + "\n";
    }
}