package org.example.model;


import org.example.validation.annotations.Max;
import org.example.validation.annotations.Min;
import org.example.validation.annotations.NotNull;

import java.io.Serializable;
import java.util.Objects;

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
public class Coordinates implements Serializable {



    private int id;
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

        public Builder id(int id) {
            Coordinates.this.id = id;
            return this;
        }

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return String.format("(%d,%.1f)", x, y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinates that = (Coordinates) o;
        return Objects.equals(x, that.x) && Objects.equals(y, that.y);
    }



    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}