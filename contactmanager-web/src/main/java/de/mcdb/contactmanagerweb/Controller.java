package de.mcdb.contactmanagerweb;

import de.mcdb.contactmanagerapi.datamodel.Company;
import de.mcdb.contactmanagerapi.datamodel.Division;
import de.mcdb.contactmanagerapi.datamodel.Staffer;
import de.mcdb.contactmanagerweb.dataaccess.CompanyDao;
import de.mcdb.contactmanagerweb.dataaccess.DivisionDao;
import de.mcdb.contactmanagerweb.dataaccess.StafferDao;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author Mirko Schulze
 */
@ManagedBean
@Named
@SessionScoped
@SuppressWarnings("unchecked")
public class Controller implements Serializable {

    private Company company;
    private Division division;
    private Staffer staffer;

    private List<Company> companies = new ArrayList<>();
    private List<Division> divisions = new ArrayList<>();
    private List<Staffer> staffers = new ArrayList<>();

    @Inject
    private StafferDao stafferDao;

    @Inject
    private DivisionDao divisionDao;

    @Inject
    private CompanyDao companyDao;

    @PostConstruct
    public void init() {
        this.companies = this.companyDao.findAll();
        this.divisions = this.divisionDao.findAll();
        this.staffers = this.stafferDao.findAll();
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
