package org.metplus.curriculum.database.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mongodb.BasicDBObject;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import org.metplus.curriculum.database.config.SpringMongoConfig;
import org.metplus.curriculum.database.exceptions.ResumeNotFound;
import org.metplus.curriculum.database.exceptions.ResumeReadException;
import org.metplus.curriculum.exceptions.CurriculumException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.gridfs.GridFsOperations;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Class that will store the information of the resume
 */
@Document
public class Resume extends AbstractDocument {
    private String filename;
    private String fileType;
    private String userId;

    private Map<String, MetaData> metaData;

    @Autowired
    private GridFsOperations gridOperation;

    public Resume(String userId) {
        super();
        this.userId = userId;
    }

    /**
     * Retrieve the resume file
     * @return Stream of the file
     * @throws ResumeNotFound When the resume is not found
     * @throws ResumeReadException When a error occur while reading file from disk
     */
    public ByteArrayOutputStream getResume(SpringMongoConfig dbConfig) throws ResumeNotFound, ResumeReadException {
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

    /**
     * Save the file to disk
     * @apiNote First removes all files for the user only after will try to save a new one, this might cause problem
     * @param fileInputStream File content
     * @throws CurriculumException When an error is raised while saving
     */
    public void setResume(InputStream fileInputStream, SpringMongoConfig dbConfig) throws CurriculumException {
        try {
            GridFS fileStore = new GridFS(dbConfig.mongoTemplate().getDb(), "filestore");
            BasicDBObject query = new BasicDBObject();
            query.put("_id", userId);

            fileStore.remove(query);

            GridFSInputFile inputFile = fileStore.createFile(fileInputStream);
            inputFile.setId(userId);
            inputFile.setFilename(filename);
            inputFile.save();
        } catch (Exception e) {
            e.printStackTrace();
            throw new CurriculumException("Error while saving the resume to the database: " + e.getMessage());
        }
    }

    /**
     * Retrieve the filename
     * @return Filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Save the filename
     * @param filename New File name
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * Retrieve the file type
     * @return File type
     */
    public String getFileType() {
        return fileType;
    }

    /**
     * Change the file type
     * @param fileType String with the file type
     */
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    /**
     * Retrieve the User Identifier
     * @return User Identifier
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Retrieve all the meta data of the specific resume
     * @return Structure with all the meta data
     */
    public Map<String, MetaData> getMetaData() {
        return metaData;
    }

    /**
     * Over write all the meta data of this resume
     * @param metaData New meta data
     */
    public void setMetaData(Map<String, MetaData> metaData) {
        this.metaData = metaData;
    }

    /**
     * Check if a cruncher already have processed this resume
     * @param cruncherName Name of the cruncher
     * @return True if meta data is present, false otherwise
     */
    @JsonIgnore
    public boolean isCruncherDataAvailable(String cruncherName) {
        return metaData.containsKey(cruncherName);
    }

    /**
     * Retrieve meta data from a specific cruncher
     * @param cruncherName Name of the cruncher
     * @return Cruncher meta data
     */
    public MetaData getCruncherData(String cruncherName) {
        return metaData.get(cruncherName);
    }
}
