package com.ttn.mohitramtari.bootcampproject.ecommerce;

import com.ttn.mohitramtari.bootcampproject.ecommerce.app.util.AddressEnum;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class EcommerceApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    public void listFilesUsingDirectoryStream() throws IOException {
        String dir = "/home/mohit/Pictures/users/sellers";
        Set<String> fileSet = new HashSet<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(dir))) {
            for (Path path : stream) {
                if (!Files.isDirectory(path)) {
                    fileSet.add(path.getFileName()
                            .toString());
                }
            }
        }
        System.out.println(fileSet);
    }
}

