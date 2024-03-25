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
 *  @author Mark Kriger
 *  @since 15/02/2024
 */

public class Coordinates {

    @NotNull
    @Min(-977)
    private Long x;

    @NotNull
    @Max(value = 886)
    private Double y;

    private Coordinates() {}

    public static Builder builder() {
        return new Coordinates().new Builder();
    }

    public class Builder {

        private Builder(){}

        public Builder x(Long x) {
            Coordinates.this.x = x;
            return this;
        }

        public Builder y(Double y) {
            Coordinates.this.y = y;
            return this;
        }


        public Coordinates build() {
            return Coordinates.this;
        }
    }




    public Long getX() {
        return x;
    }

    public Double getY() {
        return y;
    }

    public void setX(Long x) {
        this.x = x;
    }

    public void setY(Double y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return String.format("(%d %.1f)", x, y);
    }
}