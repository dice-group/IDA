package org.dice.ida.chatbot;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.Credentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.dialogflow.v2beta1.EntityTypesSettings;
import org.dice.ida.constant.IDAConst;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Class containing utility functions for Dialoglow admin client
 *
 * @author Nandeesh Patel
 */
@Component
public class IDAChatbotAdminUtil {
	private static Map<String, String> props;
	private static Credentials idaCredentials;

	/**
	 * Method to read the application properties file and store it in a map as class member.
	 * Cannot use Value annotation since we need it as static member
	 *
	 * @throws IOException when file does not exist
	 */
	private static void readProperties() throws IOException {
		props = new HashMap<>();
		Properties prop = new Properties();
		InputStream input = new FileInputStream(IDAChatbotUtil.class.getClassLoader().getResource("application.properties").getPath());
		prop.load(input);
		String keyStr;
		for (Object key : prop.keySet()) {
			keyStr = key.toString();
			props.put(keyStr, prop.getProperty(keyStr));
		}
	}

	/**
	 * Method to read the dialogflow credentials from the json file and return it as a map
	 *
	 * @return map of dialogflow authentication credentials
	 */
	private static Map<String, String> readCredentials() throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		Map<String, String> credentials;
		String credentialsFilePath = props.get("dialogflow.credentials.admin.path");
		InputStream input = new FileInputStream(IDAChatbotUtil.class.getClassLoader().getResource(credentialsFilePath).getPath());
		String jsonString = new String(input.readAllBytes());
		credentials = objectMapper.readValue(jsonString, new TypeReference<>() {
		});
		return credentials;
	}

	/**
	 * Method to create Dialogflow credential object
	 *
	 * @throws IOException - when credential file does not exist
	 * @throws NoSuchAlgorithmException - wrong encryption algorithm
	 * @throws InvalidKeySpecException - wrong credential key
	 */
	private static void createCredentials() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		readProperties();
		Map<String, String> credentialsMap = readCredentials();
			/*
			 Convert the base64 private key to RSA Private key
			 */
		String privateKey = credentialsMap.get(IDAConst.CRED_PRIVATE_KEY);
		privateKey = privateKey.replace(IDAConst.CRED_PRIVATE_KEY_BEGIN, "");
		privateKey = privateKey.replace(IDAConst.CRED_PRIVATE_KEY_END, "");
		privateKey = privateKey.replaceAll("\\s+", "");
		byte[] pkcs8EncodedBytes = Base64.getDecoder().decode(privateKey);
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pkcs8EncodedBytes);
		KeyFactory kf = KeyFactory.getInstance(IDAConst.CRED_PRIVATE_KEY_TYPE);
		PrivateKey rsaPrivateKey = kf.generatePrivate(keySpec);

		idaCredentials = ServiceAccountCredentials.newBuilder()
				.setProjectId(props.get(IDAConst.CRED_PATH_KEY))
				.setPrivateKeyId(credentialsMap.get(IDAConst.CRED_PRIVATE_KEY_ID))
				.setPrivateKey(rsaPrivateKey)
				.setClientEmail(credentialsMap.get(IDAConst.CRED_CLIENT_EMAIL))
				.setClientId(credentialsMap.get(IDAConst.CRED_CLIENT_ID))
				.setTokenServerUri(URI.create(credentialsMap.get(IDAConst.CRED_TOKEN_URI))).build();
	}

	/**
	 * Method to get dialogflow entity type management settings
	 *
	 * @return entity type management settings object
	 * @throws InvalidKeySpecException - invalid credential key
	 * @throws NoSuchAlgorithmException - invalid encoding algorithm
	 * @throws IOException - credential file does not exist
	 */
	public static EntityTypesSettings getEntityTypeSettings() throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {
		createCredentials();
		return EntityTypesSettings.newBuilder()
				.setCredentialsProvider(FixedCredentialsProvider.create(idaCredentials)).build();
	}
}
