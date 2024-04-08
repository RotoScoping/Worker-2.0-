package core.model;

import core.validation.annotations.Max;
import core.validation.annotations.Min;
import core.validation.annotations.NotNull;

/**
 * Worker coordinates.
 * Location has the following fields:
 * <ul>
 *     <li> x - the x coordinate</li>
 *     <li> y - the y coordinate</li>
 * </ul>
 * The fields have the following constraints:
 * <ul>
 *     <li> x - not null, It should be greater than -977 </li>
 *     <li> y - not null, It should be less than 886  </li>
 * </ul>
 *
 * @author Mark Kriger
 * @since 15 /02/2024
 */
public class Coordinates {

    @NotNull
    @Min(-977)
    private Long x;

    @NotNull
    @Max(value = 886)
    private Double y;

    private Coordinates() {}

    /**
     * Builder builder.
     *
     * @return the builder
     */
    public static Builder builder() {
        return new Coordinates().new Builder();
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
        public Builder x(Long x) {
            Coordinates.this.x = x;
            return this;
        }

        /**
         * Y builder.
         *
         * @param y the y
         * @return the builder
         */
        public Builder y(Double y) {
            Coordinates.this.y = y;
            return this;
        }


        /**
         * Build coordinates.
         *
         * @return the coordinates
         */
        public Coordinates build() {
            return Coordinates.this;
        }
    }


    /**
     * Gets x.
     *
     * @return the x
     */
    public Long getX() {
        return x;
    }

    /**
     * Gets y.
     *
     * @return the y
     */
    public Double getY() {
        return y;
    }

    /**
     * Sets x.
     *
     * @param x the x
     */
    public void setX(Long x) {
        this.x = x;
    }

    /**
     * Sets y.
     *
     * @param y the y
     */
    public void setY(Double y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return String.format("(%d,%.1f)", x, y);
    }
}