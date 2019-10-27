package com.jimistore.boot.nemo.dao.api.request;

import java.io.Serializable;

import com.jimistore.boot.nemo.dao.api.enums.AndOr;
import com.jimistore.boot.nemo.dao.api.enums.Compare;

public class Filter {
	
	private static final String COLUMN_ID="id";
	
	private AndOr andOr;
	
	private AndOr childAndOr=AndOr.and;
	
	private FilterEntry[] filterEntrys;
	
	private Filter next;
	
	private Filter(){
		
	}

	public static Filter id(Serializable id){
		return new Filter().setAndOr(AndOr.where).setFilterEntrys(FilterEntry.create(COLUMN_ID, Compare.eq, id));
	}
	
	public static Filter where(FilterEntry... entries){
		return new Filter().setAndOr(AndOr.where).setFilterEntrys(entries);
	}
	
	public Filter or(FilterEntry... entries){
		return this.setNext(new Filter().setAndOr(AndOr.or).setFilterEntrys(entries).setNext(this.getNext()));
	}
	
	public Filter or(AndOr andOr, FilterEntry... entries){
		return this.setNext(new Filter().setAndOr(AndOr.or).setChildAndOr(andOr).setFilterEntrys(entries).setNext(this.getNext()));
	}
	
	public Filter and(AndOr andOr, FilterEntry... entries){
		return this.setNext(new Filter().setAndOr(AndOr.and).setChildAndOr(andOr).setFilterEntrys(entries).setNext(this.getNext()));
	}
	
	public Filter and(FilterEntry... entries){
		return this.setNext(new Filter().setAndOr(AndOr.and).setFilterEntrys(entries).setNext(this.getNext()));
	}

	public AndOr getAndOr() {
		return andOr;
	}

	public Filter setAndOr(AndOr andOr) {
		this.andOr = andOr;
		return this;
	}

	public FilterEntry[] getFilterEntrys() {
		return filterEntrys;
	}

	public Filter setFilterEntrys(FilterEntry... filterEntrys) {
		this.filterEntrys = filterEntrys;
		return this;
	}

	public Filter getNext() {
		return next;
	}
	
	public boolean hasNext(){
		return next!=null;
	}

	public Filter setNext(Filter next) {
		this.next = next;
		return this;
	}

	public AndOr getChildAndOr() {
		return childAndOr;
	}

	public Filter setChildAndOr(AndOr childAndOr) {
		this.childAndOr = childAndOr;
		return this;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	
}
