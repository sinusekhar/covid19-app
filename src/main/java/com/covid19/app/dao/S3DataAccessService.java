package com.covid19.app.dao;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;

public class S3DataAccessService {
    private static AmazonS3 s3Client = null;
    public S3DataAccessService(){
        s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.US_WEST_2)
                .build();
    }

    public void writeToS3(String bucketName, String fileName, String body) throws Exception{
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("application/json");
        s3Client.putObject(bucketName, fileName, new ByteArrayInputStream(body.getBytes()), metadata);
    }

}
