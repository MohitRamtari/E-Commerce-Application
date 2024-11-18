package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.seller.mapper;

import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.admin.controller.AdminController;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.seller.dto.SellerDetailsInAdminDto;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.seller.dto.SellerModel;
import org.springframework.beans.BeanUtils;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class SellerModelAssembler extends
        RepresentationModelAssemblerSupport<SellerDetailsInAdminDto, SellerModel> {

    public SellerModelAssembler() {
        super(AdminController.class, SellerModel.class);
    }

    @Override
    public SellerModel toModel(SellerDetailsInAdminDto entity) {
        SellerModel sellerModel = new SellerModel();
        BeanUtils.copyProperties(entity, sellerModel);
        return sellerModel;
    }
}
