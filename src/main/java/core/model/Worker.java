package core.model;
import core.validation.annotations.NotNull;
import core.validation.annotations.Positive;

import java.time.LocalDate;
import java.util.Date;

/**
 * The main entity in the business logic of the service.
 * Worker has the following fields:
 * <ul>
 *     <li> id - is the user's ID. The numbering starts from 1 and is generated automatically.</li>
 *     <li> name -  the name of the worker.</li>
 *     <li> coordinates - the location of the worker.</li>
 *     <li> сreationDate - the date the worker was added to the storage, generated automatically.</li>
 *     <li> startDate - the start date of the worker's work in the organization.</li>
 *     <li> position - the vacancy for which the worker was accepted.</li>
 *     <li> status - the status of the worker in the company.</li>
 *     <li> organization - the organization in which worker works.</li>
 * </ul>
 * The fields have the following constraints:
 * <ul>
 *     <li> id - The field value must be greater than 0, unique </li>
 *     <li> name - Not null, not empty </li>
 *     <li> coordinates - Not null </li>
 *     <li> creationDate - Not null </li>
 *     <li> salary - positive number </li>
 *     <li> startDate - Not null </li>
 *     <li> position - Not null </li>
 *     <li> status - Not null </li>
 * </ul>
 * @author Mark Kriger
 * @since 15/02/2024
 */

public class Worker implements Comparable<Worker> {


    private int id;

    @NotNull
    private String name;
    @NotNull
    private Coordinates coordinates;


    private LocalDate creationDate;

    @Positive
    private float salary;

    @NotNull
    private Date startDate;
    @NotNull
    private Position position;

    @NotNull
    private Status status;

    private Organization organization;


    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    private Worker(){}

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public float getSalary() {
        return salary;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Position getPosition() {
        return position;
    }

    public Status getStatus() {
        return status;
    }

    public Organization getOrganization() {
        return organization;
    }

    public static Builder builder() {
        return new Worker().new Builder();
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public class Builder {


        private Builder() {
            // private constructor
        }

        public Builder name(String name) {
            Worker.this.name = name;
            return this;
        }

        public Builder coordinates(Coordinates coordinates) {
            Worker.this.coordinates = coordinates;
            return this;
        }
        public Builder salary(float salary) {
            Worker.this.salary = salary;
            return this;
        }

        public Builder startDate(Date startDate) {
            Worker.this.startDate = startDate;
            return this;
        }

        public Builder position(Position position) {
            Worker.this.position = position;
            return this;
        }
        public Builder status(Status status) {
            Worker.this.status = status;
            return this;
        }
        public Builder organization(Organization organization) {
            Worker.this.organization = organization;
            return this;
        }

        public Worker build() {
            return Worker.this;
        }

    }



    /**
     * @return Worker's fields as formatted string value
     */
    @Override
    public String toString() {
        return String.format("""
                
                Worker (id = %d):
                \tname: %s
                \tcoordinates: %s
                \tcreationDate: %tF
                \tsalary: %f
                \tstartDate: %tF
                \tposition: %s
                \tstatus: %s
                \torganization:
                %s""", id, name, coordinates, creationDate, salary, startDate, position, status, organization);
    }


    /**
     * Default comparison by id value
     * @param o the worker to be compared.
     * @return comparison result
     */
    @Override
    public int compareTo(Worker o) {
        return Integer.compare(this.id, o.id);
    }


}





