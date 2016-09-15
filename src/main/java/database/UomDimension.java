/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Yurij
 */
@Entity
@Table(name = "uom_dimension")
@NamedQueries({
    @NamedQuery(name = "UomDimension.findAll", query = "SELECT u FROM UomDimension u"),
    @NamedQuery(name = "UomDimension.findByDimension", query = "SELECT u FROM UomDimension u WHERE u.dimension = :dimension")})
public class UomDimension implements Serializable {

    @Column(name = "tenant_id")
    private Integer tenantId;
    @Column(name = "client", length = 3)
    private String client;
    @Id
    @NotNull
    @Column(name = "dimension", length = 6)
    private String dimension;
    //@OneToMany(mappedBy = "dimension")
    //private Collection<UnitOfMeasure> unitOfMeasureCollection;

    public UomDimension() {
    }

    public UomDimension(String dimension) {
        this.dimension = dimension;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    /*@XmlTransient
    public Collection<UnitOfMeasure> getUnitOfMeasureCollection() {
        return unitOfMeasureCollection;
    }

    public void setUnitOfMeasureCollection(Collection<UnitOfMeasure> unitOfMeasureCollection) {
        this.unitOfMeasureCollection = unitOfMeasureCollection;
    }*/

    /*@Override
    public int hashCode() {
        int hash = 0;
        hash += (dimension != null ? dimension.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UomDimension)) {
            return false;
        }
        UomDimension other = (UomDimension) object;
        if ((this.dimension == null && other.dimension != null) || (this.dimension != null && !this.dimension.equals(other.dimension))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "database.UomDimension[ dimension=" + dimension + " ]";
    }*/

    public Integer getTenantId() {
        return tenantId;
    }

    public void setTenantId(Integer tenantId) {
        this.tenantId = tenantId;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }
    
}
