package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.customer.mapper;

import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.admin.controller.AdminController;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.customer.dto.CustomerDetailsInAdminDto;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.customer.dto.CustomerModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class CustomerModelAssembler extends
        RepresentationModelAssemblerSupport<CustomerDetailsInAdminDto, CustomerModel> {

    public CustomerModelAssembler() {
        super(AdminController.class, CustomerModel.class);
    }

    @Override
    public CustomerModel toModel(CustomerDetailsInAdminDto entity) {
        CustomerModel customerModel = new CustomerModel();
        BeanUtils.copyProperties(entity, customerModel);
        customerModel.setUserFullName(
                entity.getUserFirstName() + " " + StringUtils.defaultString(entity.getUserMiddleName())
                        + entity.getUserLastName());
        return customerModel;
    }
}
