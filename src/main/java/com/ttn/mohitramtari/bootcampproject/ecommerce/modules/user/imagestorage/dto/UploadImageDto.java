package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.imagestorage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UploadImageDto {

    private String fileName;
    private String fileDownloadUri;
    private String fileType;
    private long size;
}
