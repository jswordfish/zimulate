package com.v2.competency.management.entities;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import com.poiji.annotation.ExcelCellName;
@MappedSuperclass
public class Base {
	 @Id
	    @GeneratedValue(strategy = GenerationType.AUTO)
	    protected Long id;
	 
	
	private String companyDescription;
	private Integer version;
	
	Date createDate;
	Date updateDate;
	
	String createdBy;
	
	String updatedBy;
	
	@Transient
	String formattedCreateDate;
	
	@Transient
	String formattedUpdateDate;
	
	String deletedBy;
	
	@ExcelCellName(value = "Company ID")
	String companyId;
	
	String companyName;
	
	
	
	public String getCompanyDescription() {
		return companyDescription;
	}
	public void setCompanyDescription(String companyDescription) {
		this.companyDescription = companyDescription;
	}
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
		this.formattedCreateDate = formatDate(createDate);
	}
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
		this.formattedUpdateDate = formatDate(updateDate);
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getUpdatedBy() {
		return updatedBy;
	}
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	
	
	public String getDeletedBy() {
		return deletedBy;
	}
	public void setDeletedBy(String deletedBy) {
		this.deletedBy = deletedBy;
	}
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Base other = (Base) obj;
		return Objects.equals(id, other.id);
	}
	public String getCompanyId() {
//		if(this.companyId == null && this.companyId.trim().length() == 0) {
//			return company();
//		}
		return companyId;
	}
	public void setCompanyId(String compamyId) {
		this.companyId = compamyId;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	
	
	private String company() {
		return "AB_123";
	}
	
	private String formatDate(Date date) {
        if (date == null) return null;
        
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return formatter.format(date);
    }
	public String getFormattedCreateDate() {
	    return formatDate(this.createDate);
	}
	public void setFormattedCreateDate(String formattedCreateDate) {
		this.formattedCreateDate = formattedCreateDate;
	}
	public String getFormattedUpdateDate() {
	    return formatDate(this.updateDate);
	}
	public void setFormattedUpdateDate(String formattedUpdateDate) {
		this.formattedUpdateDate = formattedUpdateDate;
	}
	
	
	

}
