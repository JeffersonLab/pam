package org.jlab.pam.persistence.entity;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author ryans
 */
@Entity
@Table(name = "WORKGROUP", schema = "PAM_OWNER")
public class Workgroup implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @SequenceGenerator(name = "WorkgroupId", sequenceName = "WORKGROUP_ID", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "WorkgroupId")        
    @Basic(optional = false)
    @NotNull
    @Column(name = "WORKGROUP_ID", nullable = false, precision = 22, scale = 0)
    private BigInteger workgroupId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(nullable = false, length = 32)
    private String name; 
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 8)
    @Column(name = "DESCRIPTION", nullable = false, length = 128)
    private String description;       
    @JoinTable(name = "WORKGROUP_MEMBERSHIP", joinColumns = {
        @JoinColumn(name = "WORKGROUP_ID", referencedColumnName = "WORKGROUP_ID", nullable = false)}, inverseJoinColumns = {
        @JoinColumn(name = "STAFF_ID", referencedColumnName = "STAFF_ID", nullable = false)})
    @ManyToMany
    private List<Staff> staffList;     
    
    public Workgroup() {
    }

    public Workgroup(BigInteger workgroupId) {
        this.workgroupId = workgroupId;
    }

    public BigInteger getWorkgroupId() {
        return workgroupId;
    }

    public void setWorkgroupId(BigInteger workgroupId) {
        this.workgroupId = workgroupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Staff> getStaffList() {
        return staffList;
    }

    public void setStaffList(List<Staff> staffList) {
        this.staffList = staffList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (workgroupId != null ? workgroupId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Workgroup)) {
            return false;
        }
        Workgroup other = (Workgroup) object;
        if ((this.workgroupId == null && other.workgroupId != null) || (this.workgroupId != null && !this.workgroupId.equals(other.workgroupId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.jlab.staff.persistence.entity.Workgroup[ workgroupId=" + workgroupId + " ]";
    }
    
}
