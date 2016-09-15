/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Yurij
 */
@Embeddable
public class UnitOfMeasurePK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "tenant_id")
    private int tenantId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "client", length = 3)
    private String client;
    @Basic(optional = false)
    @NotNull
    @Column(name = "uom", length = 3)
    private String uom;

    public UnitOfMeasurePK() {
    }

    public UnitOfMeasurePK(int tenantId, String client, String uom) {
        this.tenantId = tenantId;
        this.client = client;
        this.uom = uom;
    }

    public int getTenantId() {
        return tenantId;
    }

    public void setTenantId(int tenantId) {
        this.tenantId = tenantId;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) tenantId;
        hash += (client != null ? client.hashCode() : 0);
        hash += (uom != null ? uom.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UnitOfMeasurePK)) {
            return false;
        }
        UnitOfMeasurePK other = (UnitOfMeasurePK) object;
        if (this.tenantId != other.tenantId) {
            return false;
        }
        if ((this.client == null && other.client != null) || (this.client != null && !this.client.equals(other.client))) {
            return false;
        }
        if ((this.uom == null && other.uom != null) || (this.uom != null && !this.uom.equals(other.uom))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "database.UnitOfMeasurePK[ tenantId=" + tenantId + ", client=" + client + ", uom=" + uom + " ]";
    }
    
}
