package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.imagestorage.controller;

import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.imagestorage.dto.UploadImageDto;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.imagestorage.service.ImageStorageService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.Principal;

@RestController
public class ImageStorageController {

    @Autowired
    private ImageStorageService imageStorageService;

    @Operation(summary = "Upload Profile Image", description = "Api for the user to upload their profile image. After storing the file it returns file name, uri to download file, file type, fiel size", method = "POST")
    @PostMapping("/upload/profile-image")
    public UploadImageDto uploadSingleFile(Principal principal,
                                           @RequestParam("image") MultipartFile image) {
        String fileName = imageStorageService.storeFile(principal.getName(), image);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/download-image/").toUriString();

        return new UploadImageDto(fileName, fileDownloadUri, image.getContentType(), image.getSize());
    }

//    @Operation(summary = "Api for the seller to upload the images for his product")
//    @PostMapping("/seller/upload-product-image/{id}")
//    public UploadImageDto uploadProductImage(@PathVariable("id") Long productVariationId,
//                                             @RequestParam("image") MultipartFile image) {
//        String fileName = String.valueOf(
//                imageStorageService.storeProductImage(productVariationId, image));
//
//        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
//                .path("/download-image/").toUriString();
//
//        return new UploadImageDto(fileName, fileDownloadUri, image.getContentType(), image.getSize());
//    }

    @Operation(summary = "Download profile image", description = "API for the user to download his image", method = "GET")
    @GetMapping("/download/profile-image")
    public ResponseEntity<Resource> downloadFile(Principal principal, HttpServletRequest request) {
        // Load file as Resource
        Resource resource = imageStorageService.loadFileAsResource(principal.getName());

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            System.out.print("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"").body(resource);
    }

    @Operation(summary = "Download product's primary image", description = "User can get the primary image of the object by passing the id of the product", method = "GET")
    @GetMapping("/download/product/primary-image/{id}")
    public ResponseEntity<Resource> getProductPrimaryImage(@PathVariable("id") Long id, HttpServletRequest request)
            throws IOException {
        // Load file as Resource
        Resource resource = imageStorageService.getProductImage(id);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            System.out.print("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"").body(resource);
    }

    @Operation(summary = "Download secondary image of the product", description = "Download secondary image of  the product by passing secondary image's id", method = "GET")
    @GetMapping("/download/product/secondary-image/{id}")
    public ResponseEntity<Resource> getProductSecondaryImage(@PathVariable("id") String id, HttpServletRequest request)
            throws IOException {
        // Load file as Resource
        Resource resource = imageStorageService.getProductVariationImage(id);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            System.out.print("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"").body(resource);
    }
}
