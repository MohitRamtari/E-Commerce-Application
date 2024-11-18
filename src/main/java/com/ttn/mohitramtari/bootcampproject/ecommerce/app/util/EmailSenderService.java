package com.ttn.mohitramtari.bootcampproject.ecommerce.app.util;

import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.product.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service("emailSenderService")
public class EmailSenderService {

    private final JavaMailSender javaMailSender;

    @Autowired
    public EmailSenderService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Async
    public void sendEmail(SimpleMailMessage email) {
        javaMailSender.send(email);
    }

    public SimpleMailMessage getCustomerActivationMail(String customerEmail, String activationToken) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(customerEmail);
        mailMessage.setSubject("Complete Registration");
        mailMessage.setFrom(GlobalVariables.ADMIN_EMAIL);
        mailMessage.setText("To complete your account registration, click here : "
                + "http://localhost:8080/register/confirm?token=" + activationToken);
        return mailMessage;
    }

    public SimpleMailMessage getSellerActivationMail(String email) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject("Awaiting Account Approval");
        mailMessage.setFrom(GlobalVariables.ADMIN_EMAIL);
        mailMessage.setText(
                "Dear seller, your account has been created and is now waiting for approval");
        return mailMessage;
    }

    public SimpleMailMessage getUserForgotPasswordMail(String email, String activationToken) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject("Forgot Password");
        simpleMailMessage.setFrom(GlobalVariables.ADMIN_EMAIL);
        simpleMailMessage.setText("Dear user, to reset your password click on this link : "
                + "http://localhost:8080/register/change-password?token=" + activationToken);
        return simpleMailMessage;
    }

    public SimpleMailMessage getCustomerAccountActivatedMail(String email) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject("Account Activated");
        mailMessage.setFrom(GlobalVariables.ADMIN_EMAIL);
        mailMessage.setText("Congratulations! Your account is now activated.");
        return mailMessage;
    }

    public SimpleMailMessage getCustomerAccountDeActivatedMail(String email) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject("Account DeActivated");
        mailMessage.setFrom(GlobalVariables.ADMIN_EMAIL);
        mailMessage.setText("Your account has been deactivated.");
        return mailMessage;
    }

    public SimpleMailMessage getSellerAccountActivatedMail(String email) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject("Seller Account Activated");
        mailMessage.setFrom(GlobalVariables.ADMIN_EMAIL);
        mailMessage.setText("Congratulations! Your Seller account has been activated.");
        return mailMessage;
    }

    public SimpleMailMessage getSellerAccountDeActivatedMail(String email) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject("Seller Account DeActivated");
        mailMessage.setFrom(GlobalVariables.ADMIN_EMAIL);
        mailMessage.setText("Your Seller account has been deactivated.");
        return mailMessage;
    }

    public SimpleMailMessage getUserPasswordUpdatedMail(String email, String userName) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject("Password Changed");
        mailMessage.setFrom(GlobalVariables.ADMIN_EMAIL);
        mailMessage.setText("Hello " + userName + ", you recently updated your password");
        return mailMessage;
    }

    public SimpleMailMessage sendSellerActivationMail() {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo("ramtarimohit2@gmail.com");
        mailMessage.setSubject("Seller Activation List");
        mailMessage.setFrom(GlobalVariables.ADMIN_EMAIL);
        mailMessage.setText("Check out the list of sellers that need to be activated : \n"
                + GlobalVariables.SELLER_ACTIVATION_URL);
        return mailMessage;
    }

    public SimpleMailMessage getSellerProductDeactivationMail(String sellerEmail,
                                                              String productDetails) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(sellerEmail);
        mailMessage.setSubject("Product Deactivated");
        mailMessage.setFrom(GlobalVariables.ADMIN_EMAIL);
        mailMessage.setText("This product has been deactivated by admin : \n" + productDetails);
        return mailMessage;
    }

    public SimpleMailMessage getSellerProductActivationMail(String sellerEmail,
                                                            String productDetails) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(sellerEmail);
        mailMessage.setSubject("Product Activated");
        mailMessage.setFrom(GlobalVariables.ADMIN_EMAIL);
        mailMessage.setText("This product has been activated by admin : \n" + productDetails);
        return mailMessage;
    }

    public SimpleMailMessage sendProductActivationListMail() {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo("ramtarimohit2@gmail.com");
        mailMessage.setSubject("Product Activation List");
        mailMessage.setFrom(GlobalVariables.ADMIN_EMAIL);
        mailMessage.setText("Check out the list of Products that need to be activated : \n"
                + GlobalVariables.SELLER_ACTIVATION_URL);
        return mailMessage;
    }

    public SimpleMailMessage sendProductActivationMail(Product newProduct) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(GlobalVariables.ADMIN_EMAIL);
        mailMessage.setSubject("Activate Product");
        mailMessage.setFrom("ramtarimohit2@gmail.com");
        mailMessage.setText(newProduct.getSeller().getUserFirstName() + ", has added a new product. Check out product details and activate the product : \n"
                + "Product Id : " + newProduct.getId() + "\n"
                + "Product Name : " + newProduct.getName() + "\n"
                + "Product Brand : " + newProduct.getBrand() + "\n"
                + "Seller Name : " + newProduct.getSeller().getUserFirstName() + " " + newProduct.getSeller().getUserLastName() + "\n"
                + "Seller GST No. : " + newProduct.getSeller().getSellerGstNo() + "\n"
                + "Activate Product URL : " + GlobalVariables.PRODUCT_ACTIVATION_URL + newProduct.getId()
        );
        return mailMessage;
    }
}
