package org.metplus.curriculum.database.domain;

import com.mongodb.BasicDBObject;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import org.bson.BSONCallback;
import org.bson.BSONDecoder;
import org.bson.BSONObject;
import org.metplus.curriculum.database.config.SpringMongoConfig;
import org.metplus.curriculum.database.exceptions.ResumeNotFound;
import org.metplus.curriculum.database.exceptions.ResumeReadException;
import org.metplus.curriculum.exceptions.CurriculumException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Joao Pereira on 19/09/2015.
 */
@Document
public class Resume extends AbstractDocument {
    private String filename;
    private String fileType;
    private String userId;


    @Autowired
    SpringMongoConfig dbConfig;

    public ByteArrayOutputStream getResume() throws ResumeNotFound, ResumeReadException {
        try {
            BasicDBObject query = new BasicDBObject();
            query.put("_id", userId);

            GridFS fileStore = new GridFS(
                    dbConfig.mongoTemplate().getDb(), "filestore");
            GridFSDBFile gridFile = fileStore.findOne(query);

            InputStream in = gridFile.getInputStream();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int data = in.read();
            while (data >= 0) {
                out.write((char) data);
                data = in.read();
            }
            out.flush();

            return out;
        } catch (java.io.IOException e) {
            throw new ResumeReadException(e.getMessage());
        } catch (Exception e) {
            throw new ResumeNotFound(e.getMessage());
        }
    }

    public void setResume(InputStream fileInputStream, String filename) throws CurriculumException {
        try {
            GridFS fileStore = new GridFS(dbConfig.mongoTemplate().getDb(), "filestore");
            GridFSInputFile inputFile = fileStore.createFile(fileInputStream);
            inputFile.setId(userId);
            inputFile.setFilename(filename);
            inputFile.save();
        } catch (Exception e) {
            throw new CurriculumException("Error while saving the resume to the database: " + e.getMessage());
        }
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
