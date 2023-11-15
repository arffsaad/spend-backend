package cyou.arfsd.spendbackend.Models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Instalments {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;
    
    private Integer userid;
    private String name;
    private Integer amountLeft;
    private Integer amountDue;
    private Integer months;
    private Integer monthly;
    private Integer dueDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id){
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name=name;
    }

    public Integer getAmountLeft(){
        return amountLeft;
    }

    public void setAmountLeft(Integer amountLeft){
        this.amountLeft=amountLeft;
    }

    public Integer getAmountDue(){
        return amountDue;
    }

    public void setAmountDue(Integer amountDue){
        this.amountDue=amountDue;
    }

    public Integer getDueDate(){
        return dueDate;
    }

    public void setDueDate(Integer dueDate){
        this.dueDate=dueDate;
    }

    public Integer getUserid(){
        return userid;
    }

    public void setUserid(Integer userid){
        this.userid=userid;
    }

    public Integer getMonths(){
        return months;
    }

    public void setMonths(Integer months){
        this.months=months;
    }

    public Integer getMonthly(){
        return monthly;
    }

    public void setMonthly(Integer monthly){
        this.monthly=monthly;
    }
}
