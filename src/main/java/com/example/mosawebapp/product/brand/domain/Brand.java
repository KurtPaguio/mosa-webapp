package com.example.mosawebapp.product.brand.domain;

import com.example.mosawebapp.product.threadtype.domain.ThreadType;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

@Entity
public class Brand {
  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private String id;
  @CreationTimestamp
  private Date dateCreated;
  @Column
  private String name;
  @Column
  private String imageUrl;
  @OneToMany(mappedBy = "brand", cascade = CascadeType.REMOVE, orphanRemoval = true)
  private Set<ThreadType> types = new HashSet<>();

  public Brand(){}
  public Brand(String name, String imageUrl) {
    this.name = name;
    this.imageUrl = imageUrl;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Date getDateCreated() {
    return dateCreated;
  }

  public void setDateCreated(Date dateCreated) {
    this.dateCreated = dateCreated;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public Set<ThreadType> getTypes() {
    return types;
  }

  public void setTypes(Set<ThreadType> types) {
    this.types = types;
  }
}
