/**
 * @author : Ashok Kumar S 
 * @since  : 20-01-2025
 */
package com.maan.eway.vertexai.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerationConfig;
import com.google.cloud.vertexai.api.HarmCategory;
import com.google.cloud.vertexai.api.SafetySetting;
import com.google.cloud.vertexai.generativeai.GenerativeModel;

@Configuration
public class VertexAIGenerativeModelConfig {

	@Value("${spring.ai.vertex.ai.gemini.project-id}")
	private String projectId;
	@Value("${spring.ai.vertex.ai.gemini.location}")
	private String location;
	@Value("${spring.ai.vertex.ai.gemini.chat.options.model}")
	private String modelName;

//Properties related to Generation Config	
	private static final int maxOutputTokens = 2500;
	private static final String responseMimeType = "text/plain";
	private static final float temperature = 1F;
	private static final float topP = 0.95F;
	
	
	@Bean
	public GenerativeModel createGenerativeModel1() {
		//Vertex Ai
		VertexAI vertexAi = new VertexAI(projectId, location);
		
		
		//Generation Config
		GenerationConfig generationConfig = GenerationConfig.newBuilder()
	              .setMaxOutputTokens(maxOutputTokens)
	              .setResponseMimeType(responseMimeType)
	              .setTemperature(temperature)
	              .setTopP(topP)
	              .build();

		
		//Safety filter settings
		List<SafetySetting> safetySettings = new ArrayList<>();

		safetySettings.add(SafetySetting.newBuilder()
	            .setCategory(HarmCategory.HARM_CATEGORY_HATE_SPEECH)
	            .setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_MEDIUM_AND_ABOVE)
	            .build());
		
		safetySettings.add(SafetySetting.newBuilder()
	            .setCategory(HarmCategory.HARM_CATEGORY_DANGEROUS_CONTENT)
	            .setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_MEDIUM_AND_ABOVE)
	            .build());
		
		safetySettings.add(SafetySetting.newBuilder()
	            .setCategory(HarmCategory.HARM_CATEGORY_SEXUALLY_EXPLICIT)
	            .setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_MEDIUM_AND_ABOVE)
	            .build());
		
		safetySettings.add(SafetySetting.newBuilder()
	            .setCategory(HarmCategory.HARM_CATEGORY_HARASSMENT)
	            .setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_MEDIUM_AND_ABOVE)
	            .build());
		
		
		//Generative Model
		GenerativeModel generativeModel = new GenerativeModel.Builder()
		          .setModelName(modelName)
		          .setVertexAi(vertexAi)
		          .setGenerationConfig(generationConfig)
		          .setSafetySettings(safetySettings)
		          .build();
		
		return generativeModel;		
	}

}
