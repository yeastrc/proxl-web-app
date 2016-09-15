package org.yeastrc.xlink.dto;

import org.yeastrc.xlink.enum_classes.FilterDirectionType;

/**
 * table annotation_type_filterable
 *
 */
public class AnnotationTypeFilterableDTO {


	private int annotationTypeId;

	/**
	 * enum FilterDirectionType
	 */
	private FilterDirectionType filterDirectionType;

	private boolean defaultFilter;
	private Double defaultFilterValue;
	private String defaultFilterValueString;

	private boolean defaultFilterAtDatabaseLoad;
	private Double defaultFilterValueAtDatabaseLoad;
	private String defaultFilterValueStringAtDatabaseLoad;

	private Integer sortOrder;


	public int getAnnotationTypeId() {
		return annotationTypeId;
	}


	public void setAnnotationTypeId(int annotationTypeId) {
		this.annotationTypeId = annotationTypeId;
	}


	public FilterDirectionType getFilterDirectionType() {
		return filterDirectionType;
	}


	public void setFilterDirectionType(FilterDirectionType filterDirectionType) {
		this.filterDirectionType = filterDirectionType;
	}


	public boolean isDefaultFilter() {
		return defaultFilter;
	}


	public void setDefaultFilter(boolean defaultFilter) {
		this.defaultFilter = defaultFilter;
	}


	public Double getDefaultFilterValue() {
		return defaultFilterValue;
	}


	public void setDefaultFilterValue(Double defaultFilterValue) {
		this.defaultFilterValue = defaultFilterValue;
	}


	public String getDefaultFilterValueString() {
		return defaultFilterValueString;
	}


	public void setDefaultFilterValueString(String defaultFilterValueString) {
		this.defaultFilterValueString = defaultFilterValueString;
	}


	public Integer getSortOrder() {
		return sortOrder;
	}


	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}


	public boolean isDefaultFilterAtDatabaseLoad() {
		return defaultFilterAtDatabaseLoad;
	}


	public void setDefaultFilterAtDatabaseLoad(boolean defaultFilterAtDatabaseLoad) {
		this.defaultFilterAtDatabaseLoad = defaultFilterAtDatabaseLoad;
	}


	public Double getDefaultFilterValueAtDatabaseLoad() {
		return defaultFilterValueAtDatabaseLoad;
	}


	public void setDefaultFilterValueAtDatabaseLoad(
			Double defaultFilterValueAtDatabaseLoad) {
		this.defaultFilterValueAtDatabaseLoad = defaultFilterValueAtDatabaseLoad;
	}


	public String getDefaultFilterValueStringAtDatabaseLoad() {
		return defaultFilterValueStringAtDatabaseLoad;
	}


	public void setDefaultFilterValueStringAtDatabaseLoad(
			String defaultFilterValueStringAtDatabaseLoad) {
		this.defaultFilterValueStringAtDatabaseLoad = defaultFilterValueStringAtDatabaseLoad;
	}
	
}
