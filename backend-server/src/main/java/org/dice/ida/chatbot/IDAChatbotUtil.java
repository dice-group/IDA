package org.dice.ida.chatbot;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.Credentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.dialogflow.v2.SessionsSettings;
import org.dice.ida.constant.IDAConst;
import org.springframework.beans.factory.annotation.Value;
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

@Component
public class IDAChatbotUtil {

	private static Map<String, String> props;

	private static void readProperties() throws IOException {
		props = new HashMap<String, String>();
		// Read dsmap file
		Properties prop = new Properties();
		InputStream input = new FileInputStream(IDAChatbotUtil.class.getClassLoader().getResource("application.properties").getPath());
		prop.load(input);
		String keyStr;
		for (Object key : prop.keySet()) {
			keyStr = key.toString();
			props.put(keyStr, prop.getProperty(keyStr));
		}
	}

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

	public static SessionsSettings getSessionSettings() {
		try {
			readProperties();
			String projectId = props.get("dialogflow.project.id");
			Map<String, String> credentialsMap = readCredentials();
			String privateKeyId = credentialsMap.get("private_key_id");
			String pkcs8Pem = credentialsMap.get("private_key");
			String clientEmail = credentialsMap.get("client_email");
			String clientId = credentialsMap.get("client_id");
			String tokenServerUri = credentialsMap.get("token_uri");

			pkcs8Pem = pkcs8Pem.replace("-----BEGIN PRIVATE KEY-----", "");
			pkcs8Pem = pkcs8Pem.replace("-----END PRIVATE KEY-----", "");
			pkcs8Pem = pkcs8Pem.replaceAll("\\s+", "");
			byte[] pkcs8EncodedBytes = Base64.getDecoder().decode(pkcs8Pem);
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pkcs8EncodedBytes);
			KeyFactory kf = KeyFactory.getInstance("RSA");
			PrivateKey privKey = kf.generatePrivate(keySpec);
			Credentials idaCredentials = ServiceAccountCredentials.newBuilder().setProjectId(projectId)
					.setPrivateKeyId(privateKeyId).setPrivateKey(privKey)
					.setClientEmail(clientEmail).setClientId(clientId)
					.setTokenServerUri(URI.create(tokenServerUri)).build();
			return SessionsSettings.newBuilder()
					.setCredentialsProvider(FixedCredentialsProvider.create(idaCredentials)).build();
		} catch (InvalidKeySpecException | NoSuchAlgorithmException | IOException e) {
			throw new Error(e);
		}
	}
}
