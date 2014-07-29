package org.pshow.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity
@Table(name="ecm_role")
public class Role implements Serializable{
	private static final long serialVersionUID = 7928270441533321123L;
	@Id
	private Long id;
	@Column
	private String name;
	@Column
	private String description;
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "ecm_user_role", joinColumns = { @JoinColumn(name = "roleid", referencedColumnName = "id") }, inverseJoinColumns = { @JoinColumn(name = "userid", referencedColumnName = "id") })
	private List<User> users;
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "ecm_role_permission", joinColumns = { @JoinColumn(name = "roleid", referencedColumnName = "id") }, inverseJoinColumns = { @JoinColumn(name = "permissionid", referencedColumnName = "id") })
	private List<Permission> permissions;
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "ecm_group_role", joinColumns = { @JoinColumn(name = "roleid", referencedColumnName = "id") }, inverseJoinColumns = { @JoinColumn(name = "groupid", referencedColumnName = "id") })
	private List<UserGroup> groups;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public List<Permission> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<Permission> permissions) {
		this.permissions = permissions;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Role other = (Role) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public List<UserGroup> getGroups() {
		return groups;
	}

	public void setGroups(List<UserGroup> groups) {
		this.groups = groups;
	}
}
