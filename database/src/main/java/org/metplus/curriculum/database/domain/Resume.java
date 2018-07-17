package org.metplus.curriculum.database.domain;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import org.metplus.curriculum.database.config.SpringMongoConfig;
import org.metplus.curriculum.database.exceptions.ResumeNotFound;
import org.metplus.curriculum.database.exceptions.ResumeReadException;
import org.metplus.curriculum.exceptions.CurriculumException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Class that will store the information of the resume
 */
@Document
public class Resume extends DocumentWithMetaData {
    @Field
    private String filename;
    @Field
    private String fileType;
    @Field
    private String userId;

    @Transient
    private double starRating;

    @Autowired
    private GridFsOperations gridOperation;

    public Resume() {
        super();
    }

    public Resume(String userId) {
        super();
        this.userId = userId;
    }

    public Resume(String userId, double starRating) {
        super();
        this.userId = userId;
        this.starRating = starRating;
    }

    /**
     * Retrieve the resume file
     *
     * @return Stream of the file
     * @throws ResumeNotFound      When the resume is not found
     * @throws ResumeReadException When a error occur while reading file from disk
     */
    public ByteArrayOutputStream getResume(SpringMongoConfig dbConfig) throws ResumeNotFound, ResumeReadException {
        try {
            Query query = generateQueryToFindFile();
            GridFsTemplate fileStore = new GridFsTemplate(
                    dbConfig.mongoDbFactory(), dbConfig.mappingMongoConverter(), "filestore");
            GridFSFile gridFile = fileStore.findOne(query);

            GridFSBucket bucket = GridFSBuckets.create(dbConfig.mongoDbFactory().getDb(), "files");
            InputStream in = new GridFsResource(gridFile, bucket.openDownloadStream(gridFile.getObjectId())).getInputStream();
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

    /**
     * Save the file to disk
     *
     * @param fileInputStream File content
     * @throws CurriculumException When an error is raised while saving
     * @apiNote First removes all files for the user only after will try to save a new one, this might cause problem
     */
    public void setResume(InputStream fileInputStream, SpringMongoConfig dbConfig) throws CurriculumException {
        try {

            GridFsTemplate fileStore = new GridFsTemplate(
                    dbConfig.mongoDbFactory(), dbConfig.mappingMongoConverter(), "filestore");

            Query query = generateQueryToFindFile();
            fileStore.delete(query);

            DBObject metaData = new BasicDBObject();
            metaData.put("userid", userId);
            fileStore.store(fileInputStream, metaData);

//            GridFSInputFile inputFile = fileStore.createFile(fileInputStream);
//            inputFile.setId(userId);
//            inputFile.setFilename(filename);
//            inputFile.save();
        } catch (Exception e) {
            e.printStackTrace();
            throw new CurriculumException("Error while saving the resume to the database: " + e.getMessage());
        }
    }

    private Query generateQueryToFindFile() {
        return new Query(Criteria.where("_id").is(userId).orOperator(Criteria.where("metadata.userid").is(userId)));
    }

    /**
     * Retrieve the filename
     *
     * @return Filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Save the filename
     *
     * @param filename New File name
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * Retrieve the file type
     *
     * @return File type
     */
    public String getFileType() {
        return fileType;
    }

    /**
     * Change the file type
     *
     * @param fileType String with the file type
     */
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    /**
     * Retrieve the User Identifier
     *
     * @return User Identifier
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Retrieve the starRating of the resume
     *
     * @return Start ratting of the resume
     */
    public double getStarRating() {
        return starRating;
    }

    /**
     * Set the start ratting of the resume
     *
     * @param starRating
     */
    public void setStarRating(double starRating) {
        this.starRating = starRating;
    }
}
