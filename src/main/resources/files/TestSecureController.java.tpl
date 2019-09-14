package [# th:text="${java_package}"/].secure;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.UUID;

@Path("/secured")
@ApplicationScoped
public class TestSecureController {

    private PrivateKey key;

    @PostConstruct
    public void init() {
        try {
            key = readPrivateKey();
        } catch (IOException e) {
            ; //
        }
    }

    @GET
    @Path("/test")
    public String testSecureCall() {
        if (key == null) {
            throw new WebApplicationException("Unable to read privateKey.pem", 500);
        }

        String jwt = generateJWT(key);

        // any method to send a REST request with an appropriate header will work of course.
        WebTarget target = ClientBuilder.newClient().target("http://localhost:[# th:text="${port_service_b}"/]/data/protected");
        Response response = target.request().header("authorization", "Bearer " + jwt).buildGet().invoke();

        return String.format("Claim value within JWT of 'custom-value' : %s", response.readEntity(String.class));
    }

    private static String generateJWT(PrivateKey key) {
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256)
                .type(JOSEObjectType.JWT)
                .keyID("theKeyId")
                .build();

        MPJWTToken token = new MPJWTToken();
        token.setAud("targetService");
        token.setIss("https://server.example.com");  // Must match the expected issues configuration values
        token.setJti(UUID.randomUUID().toString());

        token.setSub("Jessie");  // Sub is required for WildFly Swarm
        token.setUpn("Jessie");

        token.setIat(System.currentTimeMillis());
        token.setExp(System.currentTimeMillis() + 30000); // 30 Seconds expiration!

        token.addAdditionalClaims("custom-value", "Jessie specific value");

        token.setGroups(Arrays.asList("user", "protected"));

        JWSObject jwsObject = new JWSObject(header, new Payload(token.toJSONString()));

        // Apply the Signing protection
        JWSSigner signer = new RSASSASigner(key);

        try {
            jwsObject.sign(signer);
        } catch (JOSEException e) {
            e.printStackTrace();
        }

        return jwsObject.serialize();
    }

    private PrivateKey readPrivateKey() throws IOException {

        InputStream inputStream = TestSecureController.class.getResourceAsStream("/privateKey.pem");

        PEMParser pemParser = new PEMParser(new InputStreamReader(inputStream));
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider(new BouncyCastleProvider());
        Object object = pemParser.readObject();
        KeyPair kp = converter.getKeyPair((PEMKeyPair) object);
        return kp.getPrivate();
    }
}
