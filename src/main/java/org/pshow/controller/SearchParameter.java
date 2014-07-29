package org.pshow.controller;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

public class SearchParameter {
	private String name;
	private boolean isFolder;
	private String creator;
	private String lastModifiedBy;
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date created_start;
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date created_end;
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date lastModified_start;
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date lastModified_end;

	public SearchParameter() {
	}

	public SearchParameter(String name, boolean isFolder, String creator,
			String lastModifiedBy, Date created_start, Date created_end,
			Date lastModified_start, Date lastModified_end) {
		this.name = name;
		this.isFolder = isFolder;
		this.creator = creator;
		this.lastModifiedBy = lastModifiedBy;
		this.created_start = created_start;
		this.created_end = created_end;
		this.lastModified_start = lastModified_start;
		this.lastModified_end = lastModified_end;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isFolder() {
		return isFolder;
	}

	public void setFolder(boolean isFolder) {
		this.isFolder = isFolder;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public Date getCreated_start() {
		return created_start;
	}

	public void setCreated_start(Date created_start) {
		this.created_start = created_start;
	}

	public Date getCreated_end() {
		return created_end;
	}

	public void setCreated_end(Date created_end) {
		this.created_end = created_end;
	}

	public Date getLastModified_start() {
		return lastModified_start;
	}

	public void setLastModified_start(Date lastModified_start) {
		this.lastModified_start = lastModified_start;
	}

	public Date getLastModified_end() {
		return lastModified_end;
	}

	public void setLastModified_end(Date lastModified_end) {
		this.lastModified_end = lastModified_end;
	}

	public boolean isEmpty() {
		if (StringUtils.isNotBlank(this.name)
				|| StringUtils.isNotBlank(this.name)
				|| StringUtils.isNotBlank(this.creator)
				|| StringUtils.isNotBlank(this.lastModifiedBy)
				|| null != this.created_end || null != this.created_start
				|| null != this.lastModified_end
				|| null != this.lastModified_start) {
			return false;
		}
		return true;
	}
}