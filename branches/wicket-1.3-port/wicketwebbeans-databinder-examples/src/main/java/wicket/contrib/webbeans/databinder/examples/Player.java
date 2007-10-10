package wicket.contrib.webbeans.databinder.examples;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

@Entity
public class Player implements Serializable
{
    private Long id;
    private String firstName;
    private String lastName;
    private Date finalGame;
    private City city;
    
    @Id @GeneratedValue(strategy=GenerationType.AUTO)
    public Long getId()
    {
        return this.id;
    }
    public void setId(Long id)
    {
        this.id = id;
    }
    public String getFirstName()
    {
        return this.firstName;
    }
    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }
    public String getLastName()
    {
        return this.lastName;
    }
    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }
    public Date getFinalGame()
    {
        return this.finalGame;
    }
    public void setFinalGame(Date finalGame)
    {
        this.finalGame = finalGame;
    }
    public City getCity()
    {
        return this.city;
    }
    public void setCity(City city)
    {
        this.city = city;
    }
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.city == null) ? 0 : this.city.hashCode());
        result = prime * result + ((this.finalGame == null) ? 0 : this.finalGame.hashCode());
        result = prime * result + ((this.firstName == null) ? 0 : this.firstName.hashCode());
        result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
        result = prime * result + ((this.lastName == null) ? 0 : this.lastName.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Player))
            return false;
        final Player other = (Player) obj;
        if (this.city == null)
        {
            if (other.city != null)
                return false;
        }
        else if (!this.city.equals(other.city))
            return false;
        if (this.finalGame == null)
        {
            if (other.finalGame != null)
                return false;
        }
        else if (!this.finalGame.equals(other.finalGame))
            return false;
        if (this.firstName == null)
        {
            if (other.firstName != null)
                return false;
        }
        else if (!this.firstName.equals(other.firstName))
            return false;
        if (this.id == null)
        {
            if (other.id != null)
                return false;
        }
        else if (!this.id.equals(other.id))
            return false;
        if (this.lastName == null)
        {
            if (other.lastName != null)
                return false;
        }
        else if (!this.lastName.equals(other.lastName))
            return false;
        return true;
    }
}