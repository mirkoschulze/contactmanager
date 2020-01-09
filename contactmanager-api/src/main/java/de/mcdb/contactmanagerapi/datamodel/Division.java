package de.mcdb.contactmanagerapi.datamodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.OptimisticLocking;

/**
 * Representation of a specialized business branch.
 * <p>
 * A Division has a name and is part of a {@link Company}.
 * <p>
 * A Division consists of multiple {@link Staffer}<code>s</code>.
 * <p>
 * Contains an id.
 *
 * @author Mirko Schulze
 */
@Entity
@Table
@OptimisticLocking
public class Division implements Serializable {

    private static final String DEFAULT_NAME = "Druckerpatronenwechslerei";

    @Id
    @Column(name = "division_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "division_name")
    private String name;

    @ManyToOne
    private Company company;

    @OneToMany(targetEntity = Staffer.class, mappedBy = "division")
    private List<Staffer> staffers;

    public Division() {
        this.staffers = new ArrayList<>();
    }

    public Division(String name) {
        this();
        this.name = name;
    }

    /**
     * This constructor calls {@link #setCompany(Company)} to ensure
     * bidirectional mapping when creating a new instance.
     *
     * @param name this {@link Division}<code>s</code> name
     * @param company the {@link Company} this {@link Division} belongs to
     */
    public Division(String name, Company company) {
        this(name);
        this.setCompany(company);
    }

    /**
     * Sets the {@link Company} of this {@link Division} and ensures
     * bidirectional mapping in the following steps:
     * <ul><li>If the submitted Company already is the Company of this Division:
     * do nothing and return</li>
     * <li>Else:<ul><li>If this Divisions Company is not null: remove this
     * Division from the Company</li>
     * <li>If the submitted Company is not null: add this Division to the
     * submitted Company</li>
     * <li>Set this Divisions Company to the submitted
     * Company</li></ul></li></ul>
     *
     * @param company the referenced {@link Company}
     */
    public void setCompany(Company company) {
        if (this.company == company) {
            return;
        }
        if (this.company != null) {
            this.company.getDivisions().remove(this);
        }
        if (company != null) {
            company.getDivisions().add(this);
        }
        this.company = company;
    }

    /**
     * If {@link List}&lt;{@link Staffer}&gt; is not null: Calls
     * {@link #addStaffer(Staffer)} for each Staffer in List&lt;Staffer&gt; to
     * ensure bidirectional mapping.
     *
     * @param staffers the {@link List} with referenced
     * {@link Staffer}<code>s</code>
     */
    public void setStaffers(List<Staffer> staffers) {
        if (staffers != null) {
            for (Staffer staffer : staffers) {
                this.addStaffer(staffer);
            }
        }
    }

    /**
     * Adds the submitted {@link Staffer} to this {@link Division} and ensures
     * bidirectional mapping by calling {@link Staffer#setDivision(Division)},
     *
     * @param staffer the referenced {@link Staffer}
     */
    public void addStaffer(Staffer staffer) {
        if (staffer == null) {
            return;
        }
        staffer.setDivision(this);
    }

    /**
     * Removes the submitted {@link Staffer} from this {@link Division} and sets
     * the Division of the Staffer to null to ensure bidirectional mapping.
     *
     * @param staffer the referenced {@link Staffer}
     */
    public void removeStaffer(Staffer staffer) {
        this.getStaffers().remove(staffer);
        staffer.setDivision(null);
    }

    /**
     * Joins and returns a human-readable String with some data of this
     * {@link Division}.
     *
     * @return String - human-readable representation of this {@link Division}
     */
    public String toSimpleLine() {
        return this.getClass().getSimpleName() + " " + this.name;
    }

    /**
     * Joins and returns a human-readable String with all data of this
     * {@link Division}.
     *
     * @return String - human-readable representation of this {@link Division}
     */
    public String toEnhancedLine() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName());
        if (this.id != 0) {
            sb.append("(").append(this.id).append(")");
        }
        sb.append(": ");
        if (this.name != null && !this.name.isEmpty()) {
            sb.append(this.name);
        } else {
            sb.append(DEFAULT_NAME);
        }
        if (this.company != null) {
            sb.append(": ").append(this.company.toSimpleLine());
        }
        if (this.staffers != null) {
            if (!this.staffers.isEmpty()) {
                this.staffers.forEach(s -> {
                    sb.append(", ").append(s.toSimpleLine());
                });
            } else {
                sb.append("; staffers is empty");
            }

        } else {
            sb.append("; staffers is null");
        }
        return sb.toString();
    }

    /**
     * Returns a String representation of this {@link Division}.
     * <p>
     * Returns only the name of this Division to prevent a
     * {@link StackOverflowError}.
     *
     * @return String - a String representation of this {@link Division}
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

    public Company getCompany() {
        return company;
    }

    public List<Staffer> getStaffers() {
        return staffers;
    }

    //</editor-fold>
}
