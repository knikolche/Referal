package com.referal.models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;

@Entity
public class Material {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String title;
	private String type;
	private String subject;
	private String reference;
	@ManyToMany/*(cascade = CascadeType.PERSIST)*/
	@JoinTable(name = "materialstyle", joinColumns = { @JoinColumn(name = "MATERIAL_ID", referencedColumnName = "ID") }, inverseJoinColumns = { @JoinColumn(name = "STYLE_ID", referencedColumnName = "ID") })
	private List<LearningStyle> styles;

	@Lob
	@Column(length = 5 * 1024 * 1024)
	private String picture;
	
	public Material() {
		this.styles = new ArrayList<LearningStyle>();
	}

	public Material(String title, String type, String reference,
			List<LearningStyle> styles) {
		this.title = title;
		this.type = type;
		this.reference = reference;
		this.styles = styles;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public List<LearningStyle> getStyles() {
		return styles;
	}

	public void setStyles(List<LearningStyle> styles) {
		this.styles = styles;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public boolean containstyle(LearningStyle style){
		for(LearningStyle ls : styles){
			if(ls.getId()==style.getId()){
				return true;
			}
		}
		return false;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

}
