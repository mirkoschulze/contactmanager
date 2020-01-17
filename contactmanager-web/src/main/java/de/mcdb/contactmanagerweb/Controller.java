package de.mcdb.contactmanagerweb;

import de.mcdb.contactmanagerapi.Dao;
import de.mcdb.contactmanagerapi.datamodel.Company;
import de.mcdb.contactmanagerapi.datamodel.Division;
import de.mcdb.contactmanagerapi.datamodel.Staffer;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Mirko Schulze
 */
@ManagedBean
@Named
@SessionScoped
@SuppressWarnings("unchecked")
public class Controller implements Serializable {

    private static final Logger L = LoggerFactory.getLogger(Controller.class);

    private Company company;
    private Division division;
    private Staffer staffer;

    private List<Company> companies = new ArrayList<>();
    private List<Division> divisions = new ArrayList<>();
    private List<Staffer> staffers = new ArrayList<>();

    @Inject
    private Dao dao;

    @PostConstruct
    public void init() {
        this.companies = dao.findAllFromCompany();
        this.divisions = dao.findAllFromDivision();
        this.staffers = dao.findAllFromStaffer();
    }

    //<editor-fold defaultstate="collapsed" desc="Getter / Setter">
    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Division getDivision() {
        return division;
    }

    public void setDivision(Division division) {
        this.division = division;
    }

    public Staffer getStaffer() {
        return staffer;
    }

    public void setStaffer(Staffer staffer) {
        this.staffer = staffer;
    }

    public List<Company> getCompanies() {
        return companies;
    }

    public void setCompanies(List<Company> companies) {
        this.companies = companies;
    }

    public List<Division> getDivisions() {
        return divisions;
    }

    public void setDivisions(List<Division> divisions) {
        this.divisions = divisions;
    }

    public List<Staffer> getStaffers() {
        return staffers;
    }

    public void setStaffers(List<Staffer> staffers) {
        this.staffers = staffers;
    }
    //</editor-fold>
}
