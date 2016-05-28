/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uma.ecplusproject.rs;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author gabriel
 */
@XmlRootElement
public class Hash {
    private String hash;
    
    public Hash()
    {}
    
    public Hash(String hash)
    {
        this.hash = hash;
    }
    
    public String getHash()
    {
        return hash;
    }
    
    public void setHash(String hash)
    {
        this.hash = hash;
    }
    
        @Override
    public int hashCode() {
        int c = 0;
        c += (hash != null ? hash.hashCode() : 0);
        return c;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Hash)) {
            return false;
        }
        Hash other = (Hash) object;
        if ((this.hash == null && other.hash != null) || (this.hash != null && !this.hash.equals(other.hash))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "es.uma.ecplusproject.rs.hash[ hash=" + hash + " ]";
    }
}
