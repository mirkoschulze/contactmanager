package de.mcdb.contactmanagerdesktop.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.OptimisticLocking;

/**
 * Representation of a business organisation.
 * <p>
 * A Company has a name and consists of multiple {@link Division}<code>s</code>.
 * <p>
 * Contains an id.
 *
 * @author Mirko Schulze
 */
@Entity
@Table
@OptimisticLocking
public class Company implements Serializable {

    private static final String DEFAULT_NAME = "Die Lappen AG";

    @Id
    @Column(name = "company_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "company_name")
    private String name;

    @OneToMany(targetEntity = Division.class, mappedBy = "company")
    private List<Division> divisions;

    public Company() {
        this.divisions = new ArrayList<>();
    }

    public Company(String name) {
        this();
        this.name = name;
    }

    /**
     * If {@link List}&lt;{@link Division}&gt; is not null: Calls
     * {@link #addDivision(Division)} for each Division in List&lt;Division&gt;
     * to ensure bidirectional mapping.
     *
     * @param divisions the {@link List} with referenced
     * {@link Division}<code>s</code>
     */
    public void setDivisions(List<Division> divisions) {
        if (divisions != null) {
            for (Division division : divisions) {
                this.addDivision(division);
            }
        }
    }

    /**
     * Adds the submitted {@link Division} to this {@link Company} and ensures
     * bidirectional mapping by calling {@link Division#setCompany(Company)},
     *
     * @param division the referenced {@link Division}
     */
    public void addDivision(Division division) {
        if (division == null) {
            return;
        }
        division.setCompany(this);
    }

    /**
     * Removes the referenced {@link Division} from this {@link Company} and
     * sets the Company of the Division to null to ensure bidirectional mapping.
     *
     * @param division the referenced {@link Division}
     */
    public void removeDivision(Division division) {
        this.getDivisions().remove(division);
        division.setCompany(null);
    }

    /**
     * Joins and returns a human-readable String with some data of this
     * {@link Company}.
     *
     * @return String - human-readable representation of this {@link Company}
     */
    public String toSimpleLine() {
        return this.getClass().getSimpleName() + " " + this.name;
    }

    /**
     * Joins and returns a human-readable String with all data of this
     * {@link Company}.
     *
     * @return String - human-readable representation of this {@link Company}
     */
    public String toEnhancedLine() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName());
        if (this.id != 0) {
            sb.append(" (").append(this.id).append(')');
        }
        sb.append(": ");
        if (this.name != null && !this.name.isEmpty()) {
            sb.append(this.name);
        } else {
            sb.append(DEFAULT_NAME);
        }
        if (this.divisions != null && !this.divisions.isEmpty()) {
            this.divisions.forEach(d -> {
                sb.append(", ").append(d.toSimpleLine());
            });
        }
        return sb.toString();
    }

    /**
     * Returns a String representation of this {@link Company}.
     * <p>
     * Returns only the name of this Company to prevent a
     * {@link StackOverflowError}.
     *
     * @return String - a String representation of this {@link Company}
     */
    @Override
    public String toString() {
        return this.name;
    }

    //<editor-fold defaultstate="collapsed" desc="Getter/Setter">
    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Division> getDivisions() {
        return divisions;
    }

    //</editor-fold>
}
