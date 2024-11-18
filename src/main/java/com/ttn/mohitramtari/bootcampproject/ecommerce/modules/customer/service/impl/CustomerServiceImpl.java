package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.customer.service.impl;

import com.ttn.mohitramtari.bootcampproject.ecommerce.app.exception.AccountActivationException;
import com.ttn.mohitramtari.bootcampproject.ecommerce.app.exception.InvalidCredentialsException;
import com.ttn.mohitramtari.bootcampproject.ecommerce.app.exception.ResourcesNotFoundException;
import com.ttn.mohitramtari.bootcampproject.ecommerce.app.exception.UserAlreadyExistException;
import com.ttn.mohitramtari.bootcampproject.ecommerce.app.util.AddressEnum;
import com.ttn.mohitramtari.bootcampproject.ecommerce.app.util.EmailSenderService;
import com.ttn.mohitramtari.bootcampproject.ecommerce.app.util.NullAwareBeanUtilsBean;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.customer.dto.CustomerDetailsDto;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.customer.dto.CustomerProfileUpdateDto;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.customer.dto.CustomerRegistrationDto;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.customer.model.Customer;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.customer.repository.CustomerRepository;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.customer.service.CustomerService;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.address.dto.AddressDto;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.address.model.Address;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.address.repository.AddressRepository;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.dto.ForgetPasswordDto;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.dto.UpdatePasswordDto;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.dto.UserPasswordUpdateDto;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.imagestorage.service.ImageStorageService;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.model.ActivationToken;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.model.User;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.repository.ActivationTokenRepository;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.repository.UserRepository;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.role.repository.RoleRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ActivationTokenRepository activationTokenRepository;

    @Autowired
    EmailSenderService emailSenderService;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    NullAwareBeanUtilsBean nullAwareBeanUtilsBean;

    @Autowired
    ImageStorageService imageStorageService;

    @Autowired
    MessageSource messageSource;

    /**
     * Method to create the customer. Fields for customer creation is passed  in the form of
     * CustomerRegistrationDto. After saving the details, its role is being set and password is stored
     * in the encoded form and then it is saved in the database.
     *
     * @param customerRegistrationDto
     * @return Success message to check mail for further process
     * @throws InvalidCredentialsException if password and confirm password don't match.
     * @throws UserAlreadyExistException   if there is already a customer with same mail.
     */
    public ResponseEntity<String> createCustomer(CustomerRegistrationDto customerRegistrationDto) {
        if (!customerRegistrationDto.getUserPassword()
                .equals(customerRegistrationDto.getConfirmPassword())) {
            throw new InvalidCredentialsException(
                    messageSource.getMessage("password.not.match", null, LocaleContextHolder.getLocale()));
        }

        if (userRepository.existsByUserEmail(customerRegistrationDto.getUserEmail())) {
            throw new UserAlreadyExistException(messageSource.getMessage("user.already.exist.mail", null,
                    LocaleContextHolder.getLocale()));
        }

        if (customerRepository.existsByCustomerContact(customerRegistrationDto.getCustomerContact())) {
            throw new UserAlreadyExistException(
                    messageSource.getMessage("customer.already.exist.contact", null,
                            LocaleContextHolder.getLocale()));
        }

        Customer customer = modelMapper.map(customerRegistrationDto, Customer.class);
        customer.setRole(roleRepository.findByRoleAuthority("ROLE_CUSTOMER"));
        String encryptedPassword = passwordEncoder.encode(customer.getUserPassword());
        customer.setUserPassword(encryptedPassword);
        userRepository.save(customer);
        sendCustomerActivationMail(customer);
        return new ResponseEntity<>(
                messageSource.getMessage("success.mail.response", null, LocaleContextHolder.getLocale()),
                null, HttpStatus.CREATED);
    }

    /**
     * A helper method used by the createCustomer method to send Activation mail. Customer object is
     * being passed as an argument. Then a new activation token is created for that customer and then
     * it is saved in the database. And then a mail is being sent which contains the url to activate
     * customer and url contains the activation token.
     *
     * @param customer
     */
    public void sendCustomerActivationMail(Customer customer) {
        ActivationToken activationToken = new ActivationToken(customer);
        activationTokenRepository.save(activationToken);
        emailSenderService.sendEmail(
                emailSenderService.getCustomerActivationMail(customer.getUserEmail(),
                        activationToken.getActivationToken()));
    }

    /**
     * Method to validate the authentication token that was sent in the authentication mail, when the
     * customer did his registration. When customer clicks on that url, this method is called and then
     * the customer account gets activated and remove the authentication token from the database.
     *
     * @param activationToken
     * @return A string of Account verified or token has expired based on the conditions.
     */
    public ResponseEntity<String> validateRegistrationToken(String activationToken) {
        ActivationToken foundToken = activationTokenRepository.findByActivationToken(activationToken);
        if (foundToken != null) {
            if (foundToken.isActivationTokenExpired()) {
                Customer customer = (Customer) foundToken.getUser();
                activationTokenRepository.delete(
                        foundToken); //Deleting the old activation token from database
                sendCustomerActivationMail(customer);   //Sending the mail for a new activation token
                return new ResponseEntity<>(
                        messageSource.getMessage("token.expired", null, LocaleContextHolder.getLocale()), null,
                        HttpStatus.OK);
            }

            Customer customer = customerRepository.findByUserEmail(foundToken.getUser().getUserEmail());
            customer.setUserIsActive(true);
            customerRepository.save(customer);
            activationTokenRepository.delete(foundToken);
            return new ResponseEntity<>(
                    messageSource.getMessage("account.verified", null, LocaleContextHolder.getLocale()), null,
                    HttpStatus.OK);
        }
        return new ResponseEntity<>(
                messageSource.getMessage("token.invalid", null, LocaleContextHolder.getLocale()), null,
                HttpStatus.UNAUTHORIZED);
    }

    /**
     * Method to get the customer details. Customer's email is being passed as an argument. Then
     * Customer is being get using his email and converting it to dto format and returning it.
     *
     * @param email
     * @return Customer details in the format of DTO
     */
    public ResponseEntity<CustomerDetailsDto> getCustomerByEmail(String email) throws IOException {
        Customer customer = customerRepository.findByUserEmail(email);
        CustomerDetailsDto customerDetailsDto = modelMapper.map(customer, CustomerDetailsDto.class);
        customerDetailsDto.setImagePath(imageStorageService.getImagePath(email));

        if (customerDetailsDto.getImagePath() == null)
            customerDetailsDto.setImageUrl(null);
        else
            customerDetailsDto.setImageUrl("http://localhost:8080/download-image");
        return new ResponseEntity<>(customerDetailsDto, null, HttpStatus.OK);
    }

    /**
     * Method for the user to get the mail to reset his password. User has to enter his mail, then it
     * will be checked if this mail exists or not. If it exists, then a new mail is generated and sent
     * to the user.
     *
     * @param forgetPasswordDto which contains the field of email
     * @return Success message to check for mail for further steps
     * @throws UsernameNotFoundException if no user is there with the given mail
     * @throws BadCredentialsException
     */
    @Override
    public ResponseEntity<String> forgotPassword(ForgetPasswordDto forgetPasswordDto) {
        User user = userRepository.findByUserEmail(forgetPasswordDto.getEmail());
        if (userRepository.existsByUserEmail(forgetPasswordDto.getEmail())) {
            throw new UsernameNotFoundException(
                    messageSource.getMessage("no.user.found", null, LocaleContextHolder.getLocale()));
        } else if (!user.getUserIsActive()) {
            throw new AccountActivationException(
                    messageSource.getMessage("account.not.active", null, LocaleContextHolder.getLocale()));
        } else if (user.getUserIsDeleted()) {
            throw new AccountActivationException(
                    messageSource.getMessage("account.deleted", null, LocaleContextHolder.getLocale()));
        } else {
            ActivationToken activationToken = new ActivationToken(user);
            activationTokenRepository.save(activationToken);
            emailSenderService.sendEmail(emailSenderService.getUserForgotPasswordMail(user.getUserEmail(),
                    activationToken.getActivationToken()));
            return new ResponseEntity<>(
                    messageSource.getMessage("success.mail.response", null, LocaleContextHolder.getLocale()),
                    null, HttpStatus.OK);
        }
    }

    /**
     * When the user clicks on url provided in the mail, then this method will be called. It then
     * takes input of password and confirm password, if both matches then the password will be
     * updated.
     *
     * @param token
     * @param updatePasswordDto
     * @return Success message indicating that password has been updated.
     * @throws InvalidCredentialsException if confirm password doesn't match with current password
     */
    public ResponseEntity<String> validateForgotPassword(String token,
                                                         UpdatePasswordDto updatePasswordDto) {
        if (!updatePasswordDto.getPassword().equals(updatePasswordDto.getConfirmPassword())) {
            throw new InvalidCredentialsException(
                    messageSource.getMessage("password.not.match", null, LocaleContextHolder.getLocale()));
        }
        ActivationToken foundToken = activationTokenRepository.findByActivationToken(token);

        /**Checking if old token is still valid or not **/
        if (foundToken.isResetPasswordTokenExpired()) {
            Customer customer = (Customer) foundToken.getUser();
            activationTokenRepository.delete(foundToken); //Deleting the old activation token
            ActivationToken activationToken = new ActivationToken(
                    customer);  //Generating a new activation token
            activationTokenRepository.save(activationToken);
            emailSenderService.sendEmail(
                    emailSenderService.getUserForgotPasswordMail(customer.getUserEmail(),
                            activationToken.getActivationToken()));
        }

        User user = foundToken.getUser();
        user.setUserPassword(passwordEncoder.encode(updatePasswordDto.getPassword()));
        userRepository.save(user);
        return new ResponseEntity<>(
                messageSource.getMessage("password.updated", null, LocaleContextHolder.getLocale()), null,
                HttpStatus.OK);
    }

    /**
     * If the old activation token has been expired, then the user needs a new activation token. For
     * this, a new activation mail needs to be sent.
     *
     * @param forgetPasswordDto
     * @return Success message to check for mail for further steps
     * @throws UsernameNotFoundException  if no user is associated with the given mail
     * @throws AccountActivationException if account is already active
     */
    public ResponseEntity<String> resendActivationMail(ForgetPasswordDto forgetPasswordDto) {
        Customer customer = customerRepository.findByUserEmail(forgetPasswordDto.getEmail());
        if (customer == null) {
            throw new UsernameNotFoundException(
                    messageSource.getMessage("no.user.found", null, LocaleContextHolder.getLocale()));
        }

        if (Boolean.TRUE.equals(customer.getUserIsActive())) {
            throw new AccountActivationException(messageSource.getMessage("account.already.active", null,
                    LocaleContextHolder.getLocale()));
        }

        activationTokenRepository.deleteByUser(customer);
        sendCustomerActivationMail(customer);
        return new ResponseEntity<>(
                messageSource.getMessage("success.mail.response", null, LocaleContextHolder.getLocale()),
                null, HttpStatus.OK);
    }


    /**
     * Method to update the profile details of the customer
     *
     * @param email
     * @param customerProfileUpdateDto
     * @return Success message indicating that profile details have been updated
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws UserAlreadyExistException if there is already a user with same contact no.
     */
    @Override
    public ResponseEntity updateCustomerProfileDetails(String email,
                                                       CustomerProfileUpdateDto customerProfileUpdateDto)
            throws InvocationTargetException, IllegalAccessException {

        checkIfCustomerDetailsAreEmpty(customerProfileUpdateDto);
        Customer customer = customerRepository.findByUserEmail(email);
        if (customerProfileUpdateDto.getCustomerContact() != null) {
            if (customerRepository.existsByCustomerContact(
                    customerProfileUpdateDto.getCustomerContact())) {
                throw new UserAlreadyExistException(
                        messageSource.getMessage("customer.already.exist.contact", null,
                                LocaleContextHolder.getLocale()));
            } else {
                customer.setCustomerContact(customerProfileUpdateDto.getCustomerContact());
            }
        }
        Customer customerToBePatched = modelMapper.map(customerProfileUpdateDto, Customer.class);
        nullAwareBeanUtilsBean.copyProperties(customer, customerToBePatched);
        customerRepository.save(customer);
        return new ResponseEntity<>(
                messageSource.getMessage("profile.details.update", null, LocaleContextHolder.getLocale()),
                null, HttpStatus.OK);
    }

    /**
     * Method to update the password of the customer. First user needs to enter its current password.
     * It will be matched with the password stored in the database. And if they match, customer needs
     * to enter its new password and confirm password and they must be same.
     *
     * @param email
     * @param userPasswordUpdateDto
     * @return Success message indicating that password has been updated
     * @throws InvalidCredentialsException if Entered password doesn't match with current password
     * @throws InvocationTargetException   if new password and confirm password don't match
     */
    @Override
    public ResponseEntity updateCustomerPassword(String email,
                                                 UserPasswordUpdateDto userPasswordUpdateDto) {
        Customer customer = customerRepository.findByUserEmail(email);

        if (!passwordEncoder.matches(userPasswordUpdateDto.getCurrentPassword(),
                customer.getUserPassword())) {
            throw new InvalidCredentialsException(
                    messageSource.getMessage("entered.password.not.match", null,
                            LocaleContextHolder.getLocale()));
        }

        if (!userPasswordUpdateDto.getNewPassword()
                .equals(userPasswordUpdateDto.getConfirmPassword())) {
            throw new InvalidCredentialsException(
                    messageSource.getMessage("password.not.match", null, LocaleContextHolder.getLocale()));
        }

        customer.setUserPassword(passwordEncoder.encode(userPasswordUpdateDto.getNewPassword()));
        customerRepository.save(customer);
        emailSenderService.sendEmail(
                emailSenderService.getUserPasswordUpdatedMail(email, customer.getUserFirstName()));
        return new ResponseEntity<>(
                messageSource.getMessage("password.updated", null, LocaleContextHolder.getLocale()), null,
                HttpStatus.OK);
    }

    /**
     * Method for the customer to add a new address.
     *
     * @param email
     * @param addressDto
     * @return Success message indicating new address has been added
     */
    @Override
    public ResponseEntity addCustomerAddress(String email, AddressDto addressDto) {
        Customer customer = customerRepository.findByUserEmail(email);
        if (AddressEnum.findByLabel(addressDto.getUserAddressLabel()) == null)
            throw new ResourcesNotFoundException("You haven't provided correct value for the address label");
        Address address = modelMapper.map(addressDto, Address.class);
        address.setUserAddressLabel(addressDto.getUserAddressLabel().toUpperCase());
        customer.addAddress(address);
        customerRepository.save(customer);
        return new ResponseEntity<>(
                messageSource.getMessage("address.added", null, LocaleContextHolder.getLocale()), null,
                HttpStatus.OK);
    }

    /**
     * Method to delete an address. Address that will be deleted, its id is passed in the method.
     *
     * @param email
     * @param id
     * @return Success message indicating address has been deleted
     * @throws ResourcesNotFoundException if there is no address found for the given id
     */
    @Override
    public ResponseEntity deleteCustomerAddress(String email, Long id) {
        Customer customer = customerRepository.findByUserEmail(email);
        Set<Address> addressSet = customer.getAddressSet();
        for (Address address : addressSet) {
            if (address.getAddressId() == id) {
                address.setUserAddressIsDeleted(true);
                addressRepository.save(address);
                return new ResponseEntity<>(
                        messageSource.getMessage("address.deleted", null, LocaleContextHolder.getLocale()),
                        null, HttpStatus.OK);
            }
        }
        throw new ResourcesNotFoundException(
                messageSource.getMessage("no.address.found", null, LocaleContextHolder.getLocale()));
    }

    /**
     * Method to update the customer address. id of the address to be updated is passed in the method.
     * And the details of the address to be updated is passed in the AddressDto
     *
     * @param email
     * @param id
     * @param addressDto
     * @return Success message indicating address has been updated
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws ResourcesNotFoundException if no address is found for the given id
     */
    @Override
    public ResponseEntity updateCustomerAddress(String email, Long id, AddressDto addressDto)
            throws InvocationTargetException, IllegalAccessException {
        checkIfAddressFieldsAreEmpty(addressDto);
        Customer customer = customerRepository.findByUserEmail(email);
        Set<Address> addressSet = customer.getAddressSet();
        Address addressToBePatched = modelMapper.map(addressDto, Address.class);
        for (Address address : addressSet) {
            if (address.getAddressId() == id) {
                nullAwareBeanUtilsBean.copyProperties(address, addressToBePatched);
                addressRepository.save(address);
                return new ResponseEntity<>(
                        messageSource.getMessage("address.updated", null, LocaleContextHolder.getLocale()),
                        null, HttpStatus.OK);
            }
        }
        throw new ResourcesNotFoundException(
                messageSource.getMessage("no.address.found", null, LocaleContextHolder.getLocale()));
    }

    /**
     * Method to retrieve all the addresses of the currently logged in customer
     *
     * @param email
     * @return Set of all addresses associated with the given customer
     */
    @Override
    public ResponseEntity getAllAddresses(String email) {
        Customer customer = customerRepository.findByUserEmail(email);
        Set<Address> addressSet = customer.getAddressSet();
        Set<AddressDto> addressDtoSet = new HashSet<>();
        for (Address address : addressSet) {
            if (!address.getUserAddressIsDeleted())
                addressDtoSet.add(modelMapper.map(address, AddressDto.class));
        }
        return new ResponseEntity<>(addressDtoSet, null, HttpStatus.OK);
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

    private void checkIfCustomerDetailsAreEmpty(CustomerProfileUpdateDto customerProfileUpdateDto) {
        if (customerProfileUpdateDto.getUserFirstName() != null && customerProfileUpdateDto.getUserFirstName().length() == 0)
            throw new ResourcesNotFoundException("User's first name can't be empty");

        if (customerProfileUpdateDto.getUserLastName() != null && customerProfileUpdateDto.getUserLastName().length() == 0)
            throw new ResourcesNotFoundException("User's Last name can't be empty");
    }
}
