/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.io.Serializable;
import java.util.Locale;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PostLoad;
import javax.persistence.PreRemove;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.exception.ODataApplicationException;
import org.apache.olingo.odata2.jpa.processor.api.exception.ODataJPARuntimeException;

/**
 *
 * @author Yurij
 */
@Entity
@Table(name = "unit_of_measure")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "UnitOfMeasure.findAll", query = "SELECT u FROM UnitOfMeasure u"),
    @NamedQuery(name = "UnitOfMeasure.findByTenantId", query = "SELECT u FROM UnitOfMeasure u WHERE u.unitOfMeasurePK.tenantId = :tenantId"),
    @NamedQuery(name = "UnitOfMeasure.findByClient", query = "SELECT u FROM UnitOfMeasure u WHERE u.unitOfMeasurePK.client = :client"),
    @NamedQuery(name = "UnitOfMeasure.findByUom", query = "SELECT u FROM UnitOfMeasure u WHERE u.unitOfMeasurePK.uom = :uom")})
public class UnitOfMeasure implements Serializable {
    @EmbeddedId
    protected UnitOfMeasurePK unitOfMeasurePK;
    
    @JoinColumn(name = "tenant_id", referencedColumnName = "tenant_id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Tenant tenant;
    
    /*@JoinColumn(name = "dimension", referencedColumnName = "dimension", insertable = true, updatable = true)
    @ManyToOne(optional = false)
    private UomDimension dimension;*/
    @Column(name = "dimension", length = 6)
    private String dimension;
    
    public UnitOfMeasure() {
    }
    
    @PreRemove 
    void onPreRemove() throws ODataApplicationException { 
        throw new RuntimeException("MotherFucker", new ODataApplicationException("С хуя ли вы удаляете сэр?", Locale.US, HttpStatusCodes.NOT_IMPLEMENTED, ""));
    }

    public UnitOfMeasure(UnitOfMeasurePK unitOfMeasurePK) {
        this.unitOfMeasurePK = unitOfMeasurePK;
    }

    public UnitOfMeasure(int tenantId, String client, String uom) {
        this.unitOfMeasurePK = new UnitOfMeasurePK(tenantId, client, uom);
    }

    public UnitOfMeasurePK getUnitOfMeasurePK() {
        return unitOfMeasurePK;
    }

    public void setUnitOfMeasurePK(UnitOfMeasurePK unitOfMeasurePK) {
        this.unitOfMeasurePK = unitOfMeasurePK;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    /*public UomDimension getDimension() {
        return dimension;
    }

    public void setDimension(UomDimension dimension) {
        this.dimension = dimension;
    }*/
    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (unitOfMeasurePK != null ? unitOfMeasurePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UnitOfMeasure)) {
            return false;
        }
        UnitOfMeasure other = (UnitOfMeasure) object;
        if ((this.unitOfMeasurePK == null && other.unitOfMeasurePK != null) || (this.unitOfMeasurePK != null && !this.unitOfMeasurePK.equals(other.unitOfMeasurePK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "database.UnitOfMeasure[ unitOfMeasurePK=" + unitOfMeasurePK + " ]";
    }
    
}
