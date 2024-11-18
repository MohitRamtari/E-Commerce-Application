package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.imagestorage.service;

import com.ttn.mohitramtari.bootcampproject.ecommerce.app.exception.FileStorageException;
import com.ttn.mohitramtari.bootcampproject.ecommerce.app.exception.ResourcesNotFoundException;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.imagestorage.dto.ImageStorageDto;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.model.User;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ImageStorageService {

    @Autowired
    UserRepository userRepository;
    private Path fileStorageLocation;

    @Autowired
    public ImageStorageService(ImageStorageDto imageStorageDto) {
        this.fileStorageLocation = Paths.get(imageStorageDto.getUploadDir()).toAbsolutePath()
                .normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException(
                    "Unable to create the directory where the uploaded files will be stored.", ex);
        }
    }


    public String storeFile(String email, MultipartFile file) {
        User user = userRepository.findByUserEmail(email);
        String fileName =
                user.getUserId().toString() + "." + file.getContentType().replaceAll("image/", "");
        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new FileStorageException(
                        "Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!",
                    ex);
        }
    }

    public Boolean storeProductImage(Long id, MultipartFile file) {
        if (file.isEmpty())
            return false;
        String fileName =
                id.toString() + "." + file.getContentType().replaceAll("image/", "");
        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new FileStorageException(
                        "Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = Path.of("/home/mohit/Pictures/products/" + fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return true;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!",
                    ex);
        }
    }

    public Boolean storeProductVariationImage(String id, MultipartFile file) {
        if (file.isEmpty())
            return false;
        String fileName =
                id + "." + file.getContentType().replaceAll("image/", "");
        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new FileStorageException(
                        "Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = Path.of("/home/mohit/Pictures/products/variations/" + fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return true;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!",
                    ex);
        }
    }


    public Resource loadFileAsResource(String email) {
        String userId = userRepository.findByUserEmail(email).getUserId().toString();
        String fileName = "";
        try {
            Set<String> fileList = listFilesUsingDirectoryStream();
            for (String file : fileList) {
                if (file.startsWith(userId)) {
                    fileName = file;
                    break;
                }
            }
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (fileName.length() != 0) {
                return resource;
            } else {
                throw new ResourcesNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new ResourcesNotFoundException("File not found " + fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Resource getProductImage(Long id) {
        String userId = id.toString();
        String fileName = "";
        try {
            Set<String> fileList = listProductImagesUsingDirectoryStream();
            for (String file : fileList) {
                if (file.startsWith(userId)) {
                    fileName = file;
                    break;
                }
            }
            this.fileStorageLocation = Path.of("/home/mohit/Pictures/products");
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new ResourcesNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new ResourcesNotFoundException("File not found " + fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getProductVariationImagesId(String id) throws IOException {
        String fileName = "";
        List<String> variationList = new ArrayList<>();
        Set<String> fileList = listProductVariationImagesUsingDirectoryStream();
        for (String file : fileList) {
            if (file.startsWith(id)) {
                variationList.add(file);
            }
        }
        return variationList;
    }

    public Resource getProductVariationImage(String id) {
        String userId = id;
        String fileName = "";
        try {
            Set<String> fileList = listProductVariationImagesUsingDirectoryStream();
            for (String file : fileList) {
                if (file.startsWith(userId)) {
                    fileName = file;
                    break;
                }
            }
            this.fileStorageLocation = Path.of("/home/mohit/Pictures/products/variations");
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new ResourcesNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new ResourcesNotFoundException("File not found " + fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Path getImagePath(String email) throws IOException {
        String userId = userRepository.findByUserEmail(email).getUserId().toString();
        String fileName = "";
        try {
            Set<String> fileList = listFilesUsingDirectoryStream();
            for (String file : fileList) {
                if (file.startsWith(userId)) {
                    fileName = file;
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Path filePath;
        if (fileName.equals("")) {
            filePath = null;
        } else {
            filePath = this.fileStorageLocation.resolve(fileName).normalize();
        }
        return filePath;
    }

    public Set<String> listFilesUsingDirectoryStream() throws IOException {
        String dir = "/home/mohit/Pictures/users";
        Set<String> fileSet = new HashSet<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(dir))) {
            for (Path path : stream) {
                if (!Files.isDirectory(path)) {
                    fileSet.add(path.getFileName().toString());
                }
            }
        }
        return fileSet;
    }

    public Set<String> listProductImagesUsingDirectoryStream() throws IOException {
        String dir = "/home/mohit/Pictures/products";
        Set<String> fileSet = new HashSet<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(dir))) {
            for (Path path : stream) {
                if (!Files.isDirectory(path)) {
                    fileSet.add(path.getFileName().toString());
                }
            }
        }
        return fileSet;
    }

    public Set<String> listProductVariationImagesUsingDirectoryStream() throws IOException {
        String dir = "/home/mohit/Pictures/products/variations";
        Set<String> fileSet = new HashSet<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(dir))) {
            for (Path path : stream) {
                if (!Files.isDirectory(path)) {
                    fileSet.add(path.getFileName().toString());
                }
            }
        }
        return fileSet;
    }
}
