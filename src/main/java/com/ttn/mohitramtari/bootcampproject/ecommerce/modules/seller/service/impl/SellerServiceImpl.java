package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.seller.service.impl;

import com.ttn.mohitramtari.bootcampproject.ecommerce.app.exception.InvalidCredentialsException;
import com.ttn.mohitramtari.bootcampproject.ecommerce.app.exception.ResourceAlreadyExistException;
import com.ttn.mohitramtari.bootcampproject.ecommerce.app.exception.ResourcesNotFoundException;
import com.ttn.mohitramtari.bootcampproject.ecommerce.app.exception.UserAlreadyExistException;
import com.ttn.mohitramtari.bootcampproject.ecommerce.app.util.EmailSenderService;
import com.ttn.mohitramtari.bootcampproject.ecommerce.app.util.NullAwareBeanUtilsBean;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.seller.dto.SellerAddressDto;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.seller.dto.SellerDetailsDto;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.seller.dto.SellerProfileUpdateDto;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.seller.dto.SellerRegistrationDto;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.seller.model.Seller;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.seller.repository.SellerRepository;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.seller.service.SellerService;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.address.dto.AddressDto;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.address.model.Address;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.dto.UserPasswordUpdateDto;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.imagestorage.service.ImageStorageService;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.repository.UserRepository;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.role.repository.RoleRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

@Service
public class SellerServiceImpl implements SellerService {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SellerRepository sellerRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    EmailSenderService emailSenderService;

    @Autowired
    NullAwareBeanUtilsBean nullAwareBeanUtilsBean;

    @Autowired
    ImageStorageService imageStorageService;

    @Autowired
    MessageSource messageSource;

    /**
     * A method to create a seller and after saving the seller sending a message to his mail.
     *
     * @param sellerRegistrationDto
     * @return Success message to check for the mail for further instruction
     * @throws InvalidCredentialsException   if password and confirm password don't match
     * @throws UserAlreadyExistException     if there is already a user for the provided mail
     * @throws UserAlreadyExistException     if there is already a seller with the given gst number
     * @throws ResourceAlreadyExistException if there is already a user for the given mobile number
     * @throws ResourceAlreadyExistException if there is already a seller with the same company name.
     */
    public ResponseEntity<String> createSeller(SellerRegistrationDto sellerRegistrationDto) {
        if (!sellerRegistrationDto.getUserPassword()
                .equals(sellerRegistrationDto.getConfirmPassword())) {
            throw new InvalidCredentialsException(
                    messageSource.getMessage("password.not.match", null, LocaleContextHolder.getLocale()));
        }

        if (userRepository.existsByUserEmail(sellerRegistrationDto.getUserEmail())) {
            throw new UserAlreadyExistException(messageSource.getMessage("user.already.exist.mail", null,
                    LocaleContextHolder.getLocale()));
        }

        if (sellerRepository.existsBySellerGstNo(sellerRegistrationDto.getSellerGstNo())) {
            throw new UserAlreadyExistException(messageSource.getMessage("seller.already.exist.gst", null,
                    LocaleContextHolder.getLocale()));
        }

        if (sellerRepository.existsBySellerCompanyName(sellerRegistrationDto.getSellerCompanyName())) {
            throw new ResourceAlreadyExistException(
                    messageSource.getMessage("seller.already.exist.name", null,
                            LocaleContextHolder.getLocale()));
        }

        if (sellerRepository.existsBySellerCompanyContact(
                sellerRegistrationDto.getSellerCompanyContact())) {
            throw new ResourceAlreadyExistException(
                    messageSource.getMessage("seller.already.exist.contact", null,
                            LocaleContextHolder.getLocale()));
        }

        Seller seller = modelMapper.map(sellerRegistrationDto, Seller.class);
        seller.setRole(roleRepository.findByRoleAuthority("ROLE_SELLER"));

        Address address = sellerRegistrationDto.getAddress();
        address.setUser(seller);

        String encryptedPassword = passwordEncoder.encode(seller.getUserPassword());
        seller.setUserPassword(encryptedPassword);

        userRepository.save(seller);

        emailSenderService.sendEmail(emailSenderService.getSellerActivationMail(seller.getUserEmail()));
        return ResponseEntity.ok(messageSource.getMessage("success.mail.response", null, LocaleContextHolder.getLocale()));
    }


    /**
     * A method to retrieve a seller by its email.
     *
     * @param email
     * @return Seller details in the form of sellerDetailsDto
     */
    public ResponseEntity<SellerDetailsDto> getSellerByEmail(String email) throws IOException {
        Seller seller = sellerRepository.findByUserEmail(email);
        SellerDetailsDto sellerDetailsDto = modelMapper.map(seller, SellerDetailsDto.class);
        SellerAddressDto sellerAddressDto = modelMapper.map(seller.getAddress(),
                SellerAddressDto.class);
        sellerDetailsDto.setImagePath(imageStorageService.getImagePath(email));
        sellerDetailsDto.setSellerAddressDto(sellerAddressDto);
        return ResponseEntity.ok(sellerDetailsDto);
    }


    /**
     * A method to update profile details of seller.
     *
     * @param email
     * @param sellerProfileUpdateDto
     * @return Success message indicating that the details have been updated
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws ResourceAlreadyExistException if there is already a company with same name
     * @throws ResourceAlreadyExistException if there is already a seller with the same contact
     *                                       number
     */
    @Override
    public ResponseEntity updateSellerProfileDetails(String email,
                                                     SellerProfileUpdateDto sellerProfileUpdateDto)
            throws InvocationTargetException, IllegalAccessException {

        checkIfSellerDetailsAreEmpty(sellerProfileUpdateDto);
        if (sellerRepository.existsBySellerCompanyName(sellerProfileUpdateDto.getSellerCompanyName())) {
            throw new ResourceAlreadyExistException(
                    messageSource.getMessage("seller.already.exist.name", null,
                            LocaleContextHolder.getLocale()));
        }

        if (sellerRepository.existsBySellerCompanyContact(
                sellerProfileUpdateDto.getSellerCompanyContact())) {
            throw new ResourceAlreadyExistException(
                    messageSource.getMessage("seller.already.exist.contact", null,
                            LocaleContextHolder.getLocale()));
        }

        Seller seller = sellerRepository.findByUserEmail(email);
        Seller sellerToBePatched = modelMapper.map(sellerProfileUpdateDto, Seller.class);

        nullAwareBeanUtilsBean.copyProperties(seller, sellerToBePatched);
        sellerRepository.save(seller);
        return ResponseEntity.ok(messageSource.getMessage("profile.details.update", null, LocaleContextHolder.getLocale()));
    }

    /**
     * A method to update the password of seller. First it matches the entered password with your
     * current password. Then it matches your new entered password with the confirm password.
     *
     * @param email
     * @param userPasswordUpdateDto
     * @return Success message indicating that user's password has been updated
     * @throws InvalidCredentialsException if entered password doesn't match with entered confirm
     *                                     password
     * @throws InvalidCredentialsException if new password and confirm password don't match
     */
    @Override
    public ResponseEntity updateSellerPassword(String email,
                                               UserPasswordUpdateDto userPasswordUpdateDto) {
        Seller seller = sellerRepository.findByUserEmail(email);

        if (!passwordEncoder.matches(userPasswordUpdateDto.getCurrentPassword(),
                seller.getUserPassword())) {
            throw new InvalidCredentialsException(
                    messageSource.getMessage("entered.password.not.match", null,
                            LocaleContextHolder.getLocale()));
        }
        if (!userPasswordUpdateDto.getNewPassword()
                .equals(userPasswordUpdateDto.getConfirmPassword())) {
            throw new InvalidCredentialsException(
                    messageSource.getMessage("password.not.match", null, LocaleContextHolder.getLocale()));
        }
        seller.setUserPassword(passwordEncoder.encode(userPasswordUpdateDto.getNewPassword()));
        sellerRepository.save(seller);
        emailSenderService.sendEmail(
                emailSenderService.getUserPasswordUpdatedMail(email, seller.getUserFirstName()));
        return ResponseEntity.ok(messageSource.getMessage("password.updated", null, LocaleContextHolder.getLocale()));
    }


    /**
     * A method to update the address of the seller.
     *
     * @param email
     * @param addressDto
     * @return Success message indicating that user's address has been updated
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @Override
    public ResponseEntity updateSellerAddress(String email, AddressDto addressDto)
            throws InvocationTargetException, IllegalAccessException {
        checkIfAddressFieldsAreEmpty(addressDto);
        Seller seller = sellerRepository.findByUserEmail(email);
        Address address = seller.getAddress();
        Address addressToBePatched = modelMapper.map(addressDto, Address.class);
        nullAwareBeanUtilsBean.copyProperties(address, addressToBePatched);
        seller.setAddress(address);
        sellerRepository.save(seller);
        return ResponseEntity.ok(
                messageSource.getMessage("address.updated", null, LocaleContextHolder.getLocale()));
    }

    private void checkIfSellerDetailsAreEmpty(SellerProfileUpdateDto sellerProfileUpdateDto) {
        if (sellerProfileUpdateDto.getUserFirstName() != null && sellerProfileUpdateDto.getUserFirstName().length() == 0)
            throw new ResourcesNotFoundException("Seller's first name can't be empty");

        if (sellerProfileUpdateDto.getUserLastName() != null && sellerProfileUpdateDto.getUserLastName().length() == 0)
            throw new ResourcesNotFoundException("Seller's last name can't be empty");

        if (sellerProfileUpdateDto.getSellerCompanyName() != null && sellerProfileUpdateDto.getSellerCompanyName().length() == 0)
            throw new ResourcesNotFoundException("Seller's company name can't be empty");
    }

    private void checkIfAddressFieldsAreEmpty(AddressDto addressDto) {
        if (addressDto.getUserAddressLine() == null && addressDto.getUserStreet() == null && addressDto.getUserCity() == null && addressDto.getUserState() == null && addressDto.getUserCountry() == null && addressDto.getUserAddressZipCode() == null && addressDto.getUserAddressLabel() == null)
            throw new ResourcesNotFoundException("Please provide at least one field value to update address");

        if (addressDto.getUserAddressLine() != null && addressDto.getUserAddressLine().length() == 0)
            throw new ResourcesNotFoundException("Address Line can't be empty");

        if (addressDto.getUserStreet() != null && addressDto.getUserStreet().length() == 0)
            throw new ResourcesNotFoundException("Address Street can't be empty");

        if (addressDto.getUserCity() != null && addressDto.getUserCity().length() == 0)
            throw new ResourcesNotFoundException("Address City can't be empty");

        if (addressDto.getUserState() != null && addressDto.getUserState().length() == 0)
            throw new ResourcesNotFoundException("Address State can't be empty");

        if (addressDto.getUserCountry() != null && addressDto.getUserCountry().length() == 0)
            throw new ResourcesNotFoundException("Address Country can't be empty");

        if (addressDto.getUserAddressZipCode() != null && addressDto.getUserAddressZipCode().length() == 0)
            throw new ResourcesNotFoundException("Address Zip Code can't be empty");

        if (addressDto.getUserAddressLabel() != null && addressDto.getUserAddressLabel().length() == 0)
            throw new ResourcesNotFoundException("Address Label can't be empty");
    }
}
