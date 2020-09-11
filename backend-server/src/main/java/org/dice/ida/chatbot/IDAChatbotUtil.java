package org.dice.ida.chatbot;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.Credentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.dialogflow.v2.SessionsSettings;
import org.dice.ida.constant.IDAConst;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
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
 * Chatbot utility class
 *
 * @author Nandeesh Patel
 */
@Component
public class IDAChatbotUtil {

	private static Map<String, String> props;

	/**
	 * Method to read the application properties file and store it in a map as class member.
	 * Cannot use Value annotation since we need it as static member
	 * @throws IOException when file does not exist
	 */
	private static void readProperties() throws IOException {
		props = new HashMap<String, String>();
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
	 * @return map of dialogflow authentication credentials
	 */
	private static Map<String, String> readCredentials() {
		ObjectMapper objectMapper = new ObjectMapper();
		Map<String, String> credentials;
		String credentialsFilePath = props.get("dialogflow.credentials.path");
		try {
			String jsonString = new String(Files.readAllBytes(Paths.get(IDAChatbotUtil.class.getClassLoader().getResource(credentialsFilePath).getPath())));
			credentials = objectMapper.readValue(jsonString, new TypeReference<Map<String, String>>() {
			});
			return credentials;
		} catch (IOException ex) {
			throw new Error(ex);
		}
	}

	/**
	 * Method to create a session settings object from the dialogflow auth credentials
	 * @return Dialogflow session settings to create the session
	 */
	public static SessionsSettings getSessionSettings() {
		try {
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

			Credentials idaCredentials = ServiceAccountCredentials.newBuilder()
					.setProjectId(props.get(IDAConst.CRED_PATH_KEY))
					.setPrivateKeyId(credentialsMap.get(IDAConst.CRED_PRIVATE_KEY_ID))
					.setPrivateKey(rsaPrivateKey)
					.setClientEmail(credentialsMap.get(IDAConst.CRED_CLIENT_EMAIL))
					.setClientId(credentialsMap.get(IDAConst.CRED_CLIENT_ID))
					.setTokenServerUri(URI.create(credentialsMap.get(IDAConst.CRED_TOKEN_URI))).build();
			return SessionsSettings.newBuilder()
					.setCredentialsProvider(FixedCredentialsProvider.create(idaCredentials)).build();
		} catch (InvalidKeySpecException | NoSuchAlgorithmException | IOException e) {
			throw new Error(e);
		}
	}
}
