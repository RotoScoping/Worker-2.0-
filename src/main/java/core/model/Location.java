package core.model;


import core.validation.annotations.NotNull;

/**
 * Organization location.
 * Location has the following fields:
 * <ul>
 *     <li> x - the x coordinate</li>
 *     <li> y -  the y coordinate</li>
 *     <li> z - the z coordinate</li>
 *     <li> name - name of location</li>
 * </ul>
 * The fields have the following constraints:
 * <ul>
 *     <li> x - not null </li>
 *     <li> y - not null </li>
 *     <li> z - not null </li>
 *     <li> name - not blank, not null </li>
 * </ul>
 *
 * @author Mark Kriger
 * @since 15 /02/2024
 */
public class Location {

    @NotNull
    private Double x;
    @NotNull
    private Integer y;


    @NotNull
    private Double z;

    @NotNull
    private String name;

    private Location() {}

    /**
     * Builder builder.
     *
     * @return the builder
     */
    public static Builder builder() {
        return new Location().new Builder();
    }

    /**
     * The type Builder.
     */
    public class Builder {

        private Builder(){}

        /**
         * X builder.
         *
         * @param x the x
         * @return the builder
         */
        public Builder x(Double x) {
            Location.this.x = x;
            return this;
        }

        /**
         * Y builder.
         *
         * @param y the y
         * @return the builder
         */
        public Builder y(Integer y) {
            Location.this.y = y;
            return this;
        }

        /**
         * Z builder.
         *
         * @param z the z
         * @return the builder
         */
        public Builder z(Double z) {
            Location.this.z = z;
            return this;
        }

        /**
         * Name builder.
         *
         * @param name the name
         * @return the builder
         */
        public Builder name(String name) {
            Location.this.name = name;
            return this;
        }

        /**
         * Build location.
         *
         * @return the location
         */
        public Location build() {
            return Location.this;
        }
    }
    @Override
    public String toString() {
        return "(" +
               " name: " + name +
               ", x: " + x +
               ", y: " + y +
               ", z: " + z +
               " )";
    }

    /**
     * Gets x.
     *
     * @return the x
     */
    public Double getX() {
        return x;
    }

    /**
     * Gets y.
     *
     * @return the y
     */
    public Integer getY() {
        return y;
    }

    /**
     * Gets z.
     *
     * @return the z
     */
    public Double getZ() {
        return z;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

}