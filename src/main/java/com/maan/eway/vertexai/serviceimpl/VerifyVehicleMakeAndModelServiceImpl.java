/**
 * @author : Ashok Kumar S 
 * @since  : 20-01-2025
 */
package com.maan.eway.vertexai.serviceimpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.cloud.vertexai.api.Content;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.api.Part;
import com.google.cloud.vertexai.generativeai.ContentMaker;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.PartMaker;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.document.req.FilePathReq;
import com.maan.eway.document.res.FilePathRes;
import com.maan.eway.document.service.impl.DocumentServiceImpl;
import com.maan.eway.error.Error;
import com.maan.eway.vertexai.request.VerifyVehicleMakeAndModelReq;
import com.maan.eway.vertexai.response.VerifyVehicleMakeAndModelRes;
import com.maan.eway.vertexai.service.VerifyVehicleMakeAndModelService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class VerifyVehicleMakeAndModelServiceImpl implements VerifyVehicleMakeAndModelService {
	private static final Logger log = LogManager.getLogger(VerifyVehicleMakeAndModelServiceImpl.class);
	private static final ModelMapper mapper = new ModelMapper();
	
	// The prefix for identifying Base64-encoded JPEG image data
	private static final String BASE64_PREFIX = "data:image/jpeg;base64,";
	// MIME type for JPEG images.
	private static final String MIME_TYPE = "image/jpeg";
	
	private static final String NOT_A_IMAGE_FILE = "NOT A IMAGE FILE";
	private static final String DISCLAIMER = "Gemini AI can make mistakes, so double-check it";
	

	//Key used to store or retrieve the "Type", "Make" and "Model" of the vehicle in the AI-generated response.
	private static final String TYPE_KEY = "TYPE";
	private static final String MAKE_KEY = "MAKE";
	private static final String MODEL_KEY = "MODEL";
	
	private final GenerativeModel generativeModel;
	private final DocumentServiceImpl documentService;
	private final EntityManager entityManager;
	
	@Autowired	
    public VerifyVehicleMakeAndModelServiceImpl(GenerativeModel generativeModel, DocumentServiceImpl documentService,
			EntityManager entityManager) {
		this.generativeModel = generativeModel;
		this.documentService = documentService;
		this.entityManager = entityManager;
	}

    // Validate incoming request
	public List<Error> validateVehicleMakeAndModelRequest (VerifyVehicleMakeAndModelReq req) {
		List<Error> errors = new ArrayList<>();
		if(req.getId() == null || req.getId().isBlank()) {
			errors.add(new Error ("1","Id","Id should not be blank"));
		}
		if(req.getQuoteNo() == null || req.getQuoteNo().isBlank()) {
			errors.add(new Error ("2","QuoteNo","Quote No. should not be blank"));
		}
		if(req.getUniqueId() == null) {
			errors.add(new Error ("3","UniqueId","Unique Id should not be null"));
		}
		if(req.getRequestReferenceNo() == null || req.getRequestReferenceNo().isBlank()) {
			errors.add(new Error ("4","RequestReferenceNo","Request Reference No. should not be blank"));
		}
		if(req.getRiskId() == null) {
			errors.add(new Error ("5","RiskId","Risk Id should not be null"));
		}
		return errors;
	}
	

	/**
	 * Checks and verifies the saved vehicle's make and model by analyzing the provided vehicle image using AI.
	 * This method retrieves the saved vehicle details from the database, extracts and validates the image, 
	 * and then uses Vertex AI to analyze the image and identify the vehicle's make and model. 
	 * Finally, it compares the AI-generated make and model with the saved details and returns the results.
	 * 
	 * @param req the request object containing the vehicle details and image information. This is used to 
	 *            fetch the saved vehicle details and the image file for analysis.
	 * @return an object containing the verification results. If the vehicle is not found in the database, 
	 *         or if there are any issues with the image file, an {@link Error} message is returned. 
	 *         Otherwise, the {@link VerifyVehicleMakeAndModelRes} object with the verification results is returned.
	 * @throws Exception if any unexpected errors occur during the process, including issues with image 
	 *         retrieval, AI analysis, or database queries. Errors are logged and handled by returning {@code null}.
	 */
	public Object checkSavedMakeAndModelWithAIImageAnalysis(VerifyVehicleMakeAndModelReq req) {
        try {     

            // Retrieve saved vehicle details based on request reference number and risk ID
            List<EserviceMotorDetails> savedVehicles = findSavedMotorDetailsInfo(req.getRequestReferenceNo(), req.getRiskId());
            if (savedVehicles.isEmpty()) {
                return new Error("1", "Message", "Vehicle info is not found in our database");
            }
            // Retrieve unique record of vehicle info (always expects only one result)
            EserviceMotorDetails motorDetails = savedVehicles.get(0);

            
            // Get the Base64 string of the image file
            String base64ImgRaw = getBase64StringOfImageFile(req);
            if (base64ImgRaw == null) {
                return new Error("2", "Message", "Exception Occurred While Getting File");
            }
            // Check if the file is not a valid image file
            if(NOT_A_IMAGE_FILE.equalsIgnoreCase(base64ImgRaw)) {
            	return new Error("3", "Message", "This is not a image file");
            }
            
            // Analyze the vehicle image with Vertex AI
            String aiGeneratedResponse = analyzeVehicleImageWithVertexAI(base64ImgRaw);            

            // Parse the AI-generated response to extract vehicle type, make, and model          
            Map<String, String> aiResultMap = parseAIGeneratedResponse(aiGeneratedResponse);
            
            // Create a response object and set the AI-generated results
            VerifyVehicleMakeAndModelRes verifyMakeAndModelRes = new VerifyVehicleMakeAndModelRes();
            verifyMakeAndModelRes.setObjectType(aiResultMap.get(TYPE_KEY));
            verifyMakeAndModelRes.setGeneratedVehicleMake(aiResultMap.get(MAKE_KEY));
            verifyMakeAndModelRes.setGeneratedVehicleModel(aiResultMap.get(MODEL_KEY));

            // Set provided vehicle make and model from the saved data         
            verifyMakeAndModelRes.setProvidedVehicleMake(motorDetails.getVehicleMakeDesc());
            verifyMakeAndModelRes.setProvidedVehicleModel(motorDetails.getVehcileModelDesc());
            
            // Check if the AI-generated make and model match the provided details
            verifyMakeAndModelRes.setIsVehicleMakeMatched(
            		verifyMakeAndModelRes.getProvidedVehicleMake().equalsIgnoreCase(verifyMakeAndModelRes.getGeneratedVehicleMake())  
            		);
            verifyMakeAndModelRes.setIsVehicleModelMatched(
            		verifyMakeAndModelRes.getProvidedVehicleModel().equalsIgnoreCase(verifyMakeAndModelRes.getGeneratedVehicleModel())
            		);
            verifyMakeAndModelRes.setDisclaimer(DISCLAIMER);
                        
            return verifyMakeAndModelRes;
        } catch (Exception e) {
            log.error("Error during AI analysis: {}", e.getMessage(), e);
            return null;
        }
    }
	

	/**
	 * Retrieves the Base64-encoded image string from the request object. The method fetches the image URL 
	 * using the provided request, validates that the URL is a Base64-encoded image, and then extracts and returns
	 * the Base64 string without the prefix. If the image is not Base64-encoded or if the image URL is invalid, 
	 * appropriate error handling is performed.
	 * 
	 * @param req the request object containing the details to retrieve the file path. 
	 *            The file path will be extracted from the request via the {@link FilePathReq} object.
	 * @return the Base64-encoded image string without the prefix if the image is valid; 
	 *         returns {@code NOT_A_IMAGE_FILE} if the file is not a valid Base64-encoded image 
	 *         {@code null} if the file path or image URL is invalid or missing.
	 */
    private String getBase64StringOfImageFile(VerifyVehicleMakeAndModelReq req) {
        FilePathReq filePathReq = mapper.map(req, FilePathReq.class);
        FilePathRes filePathRes = documentService.getFilePath(filePathReq);
        
        if(filePathRes == null || filePathRes.getImgurl() == null) {
        	return null;
        }
        
        if(! filePathRes.getImgurl().startsWith(BASE64_PREFIX)) {
        	return NOT_A_IMAGE_FILE;
        }
        //Remove prefix from base64 string
        return filePathRes.getImgurl().substring(BASE64_PREFIX.length());
    }
    

    /**
     * Retrieves a list of saved motor details from the database based on the given request reference number and risk ID.
     * This method queries the database using JPA Criteria API to filter the results.
     *
     * @param requestReferenceNo the reference number of the request to filter the motor details; must not be null or blank.
     * @param riskId the risk ID to filter the motor details; must not be null.
     * @return a list of {@link EserviceMotorDetails} objects that match the given filters. If no records are found, an empty list is returned.
     */
    private List<EserviceMotorDetails> findSavedMotorDetailsInfo(String requestReferenceNo, Integer riskId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<EserviceMotorDetails> query = cb.createQuery(EserviceMotorDetails.class);
        Root<EserviceMotorDetails> motorRoot = query.from(EserviceMotorDetails.class);

        Predicate requestRefFilter = cb.equal(motorRoot.get("requestReferenceNo"), requestReferenceNo);
        Predicate riskIdFilter = cb.equal(motorRoot.get("riskId"), riskId);

        query.select(motorRoot).where(cb.and(requestRefFilter, riskIdFilter));

        return entityManager.createQuery(query).getResultList();
    }


    /**
     * Analyzes the provided Base64-encoded image using Vertex AI to determine the type of object, 
     * and if it is a vehicle, identifies its make and model.
     * 
     * @param base64ImgRaw the Base64-encoded string representing the image file (without the prefix).
     * @return a formatted string response containing the identified object type, vehicle make, and vehicle model.
     *         The format of the response is:
     *         <pre>
     *         Type:<Object>
     *         Make:<Vehicle Make>
     *         Model:<Vehicle Model>
     *         </pre>
     *         If the make or model cannot be identified, the response will include 'Make:N/A' or 'Model:N/A'.
     * @throws IOException if an error occurs during Base64 decoding or communication with the Vertex AI service.
     */
    private String analyzeVehicleImageWithVertexAI(String base64ImgRaw) throws IOException{
        byte[] imageData = Base64.getDecoder().decode(base64ImgRaw);
        Part part = PartMaker.fromMimeTypeAndData(MIME_TYPE, imageData);
   	
    	String prompt = """
				Analyze the provided image and identify the following details:	
				Determine the type of object in the image and respond with TYPE:<OBJECT>.
				If the object is a vehicle, identify its make (e.g., Toyota). If the object is not a vehicle or make cannot be identified, respond with MAKE:N/A.
				If the object is a vehicle, identify its model (e.g., Civic, F Pace). If the object is not a vehicle or model cannot be identified, respond with MODEL:N/A.				
				Please format your response in all capital letters exactly as follows:
				TYPE:<OBJECT>
				MAKE:<VEHICLE MAKE>
				MODEL:<VEHICLE MODEL>
    		    """;
    	
        Content content = ContentMaker.fromMultiModalData(part, prompt);
        GenerateContentResponse contentResponse = generativeModel.generateContent(content);
        
        log.info("Vertex AI Result : \n" + ResponseHandler.getText(contentResponse));
        return ResponseHandler.getText(contentResponse);    	
    }
	
    /**
     * Parses the AI-generated response and extracts key-value pairs from it. 
     * The response is expected to have each key-value pair formatted as "Key:Value" on separate lines.
     * 
     * @param aiResponse the AI-generated response as a string. Each line should be in the format "Key:Value".
     * @return a map containing the extracted key-value pairs. Keys and values are trimmed of leading/trailing whitespace.
     *         If the response is malformed or empty, the map may be incomplete or empty.
     */
    private Map<String, String> parseAIGeneratedResponse(String aiResponse){
    	Map<String, String> details = new HashMap<>();

    	// Split the response by newline
    	String[] lines = aiResponse.split("\n");

		for(String line : lines) {
			String[] parts = line.split(":");
			
			details.put(parts[0], parts[1]);
		}
    	    	
    	return details;    	
    }
    
    
    
}
