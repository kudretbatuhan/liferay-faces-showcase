/**
 * Copyright (c) 2000-2015 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.faces.showcase.model;

import java.io.Serializable;
import java.util.List;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import com.liferay.faces.showcase.comparator.CustomerComparator;
import com.liferay.faces.showcase.dto.Customer;
import com.liferay.faces.showcase.service.CustomerService;
import com.liferay.faces.util.model.OnDemandDataModel;
import com.liferay.faces.util.model.SortCriterion;


/**
 * @author  Neil Griffin
 */
public class CustomerOnDemandDataModel extends OnDemandDataModel<Customer> implements Serializable {

	// serialVersionUID
	private static final long serialVersionUID = 1715725381634521866L;

	// Transient Data members
	private transient CustomerService customerService;
	private int rowsPerPage;

	public CustomerOnDemandDataModel(int rowsPerPage) {
		this.rowsPerPage = rowsPerPage;
	}

	@Override
	public int countRows() {
		return getCustomerService().getCustomerCount();
	}

	@Override
	public List<Customer> findRows(int startRow, int finishRow, List<SortCriterion> sortCriteria) {

		CustomerComparator customerComparator = new CustomerComparator(sortCriteria);
		List<Customer> customers = getCustomerService().getCustomers(startRow, finishRow, customerComparator);

		FacesMessage facesMessage = new FacesMessage("OnDemandDataModel: Fetched row index range " + startRow + "-" +
				finishRow);
		FacesContext facesContext = FacesContext.getCurrentInstance();
		facesContext.addMessage(null, facesMessage);

		return customers;
	}

	protected CustomerService getCustomerService() {

		if (customerService == null) {

			// In order to accommodate clustered environments, the CustomerService is transient and therefore must be
			// self-injected.
			FacesContext facesContext = FacesContext.getCurrentInstance();
			Application application = facesContext.getApplication();
			ELResolver elResolver = application.getELResolver();
			ELContext elContext = facesContext.getELContext();
			customerService = (CustomerService) elResolver.getValue(elContext, null, "customerService");
		}

		return customerService;
	}

	@Override
	public int getRowsPerPage() {
		return rowsPerPage;
	}
}