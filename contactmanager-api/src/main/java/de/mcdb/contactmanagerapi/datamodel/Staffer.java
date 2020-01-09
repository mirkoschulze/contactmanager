package de.mcdb.contactmanagerapi.datamodel;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.OptimisticLocking;

/**
 * Representation of a person with a job.
 * <p>
 * A Staffer has a forename, a surname and is part of a {@link Division}.
 * <p>
 * Contains an id.
 *
 * @author Mirko Schulze
 */
@Entity
@Table
@OptimisticLocking
public class Staffer implements Serializable {

    private static final String DEFAULT_NAME = "Annika Sahneschnitte";

    @Id
    @Column(name = "staffer_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String foreName, surName;

    @ManyToOne
    private Division division;

    public Staffer() {

    }

    public Staffer(String foreName) {
        this.foreName = foreName;
    }

    public Staffer(String foreName, String surName) {
        this(foreName);
        this.surName = surName;
    }

    /**
     * This constructor calls {@link #setDivision(Division)} to ensure
     * bidirectional mapping when creating a new instance.
     *
     * @param foreName this {@link Staffer}<code>s</code> forename
     * @param surName this {@link Staffer}<code>s</code> surname
     * @param division the {@link Division} this {@link Staffer} belongs to
     */
    public Staffer(String foreName, String surName, Division division) {
        this(foreName, surName);
        this.setDivision(division);
    }

    /**
     * Sets the {@link Division} of this {@link Staffer} and ensures
     * bidirectional mapping in the following steps:
     * <ul><li>If the submitted Division already is the Division of this
     * Staffer: do nothing and return</li>
     * <li>Else:<ul><li>If this Staffers Division is not null: remove this
     * Staffer from the Division</li>
     * <li>If the submitted Division is not null: add this Staffer to the
     * submitted Division</li>
     * <li>Set this Staffers Division to the submitted
     * Division</li></ul></li></ul>
     *
     * @param division the referenced {@link Division}
     */
    public void setDivision(Division division) {
        if (this.division == division) {
            return;
        }
        if (this.division != null) {
            this.division.getStaffers().remove(this);
        }
        if (division != null) {
            division.getStaffers().add(this);
        }
        this.division = division;
    }

    /**
     * Joins and returns a human-readable String with some data of this
     * {@link Staffer}.
     *
     * @return String - human-readable representation of this {@link Staffer}
     */
    public String toSimpleLine() {
        return this.getClass().getSimpleName() + " " + this.foreName + " " + this.surName;
    }

    /**
     * Joins and returns a human-readable String with all data of this
     * {@link Staffer}.
     *
     * @return String - human-readable representation of this {@link Staffer}
     */
    public String toEnhancedLine() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName());
        if (this.id != 0) {
            sb.append(" (").append(this.id).append(")");
        }
        sb.append(": ");
        if (this.foreName != null && !this.foreName.isEmpty() && this.surName != null && !this.surName.isEmpty()) {
            sb.append(this.foreName).append(" ").append(this.surName);
        } else if (this.foreName != null && !this.foreName.isEmpty()) {
            sb.append(this.foreName);
        } else if (this.surName != null && !this.surName.isEmpty()) {
            sb.append(this.surName);
        } else {
            sb.append(DEFAULT_NAME);
        }
        if (this.division != null) {
            sb.append(": ").append(this.division.toSimpleLine());
            if (this.division.getCompany() != null) {
                sb.append(", ").append(this.getDivision().getCompany().toSimpleLine());
            }
        }
        return sb.toString();
    }

    //<editor-fold defaultstate="collapsed" desc="Getter/Setter, toString">
    public long getId() {
        return id;
    }

    public String getForeName() {
        return foreName;
    }

    public void setForeName(String foreName) {
        this.foreName = foreName;
    }

    public String getSurName() {
        return surName;
    }

    public void setSurName(String surName) {
        this.surName = surName;
    }

    public Division getDivision() {
        return division;
    }

    @Override
    public String toString() {
        return "Staffer{" + "id=" + id + ", foreName=" + foreName + ", surName=" + surName + ", division=" + division + '}';
    }
    //</editor-fold>

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (int) (this.id ^ (this.id >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Staffer other = (Staffer) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

}
