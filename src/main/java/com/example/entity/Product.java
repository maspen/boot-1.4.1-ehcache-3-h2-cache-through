package com.example.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "product")
public class Product implements Serializable {
	private static final long serialVersionUID = -7451098259792280698L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@NotNull
	private Long id;
	
	@NotNull
	private Long cacheKey;
	
	@NotNull
	private String name;
	
	@NotNull
    private int price;

	public void setProduct(Product product) {
		this.id = product.getId();
		this.cacheKey = product.cacheKey;
		this.name = product.getName();
		this.price = product.getPrice();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getCacheKey() {
		return cacheKey;
	}

	public void setCacheKey(Long cacheKey) {
		this.cacheKey = cacheKey;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cacheKey == null) ? 0 : cacheKey.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + price;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Product other = (Product) obj;
		if (cacheKey == null) {
			if (other.cacheKey != null)
				return false;
		} else if (!cacheKey.equals(other.cacheKey))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (price != other.price)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Product [id=" + id + ", cacheKey=" + cacheKey + ", name=" + name + ", price=" + price + "]";
	}
}
