package [# th:text="${java_package}"/].security;

import be.c4j.ee.security.realm.AuthenticationInfoBuilder;
import be.c4j.ee.security.realm.SecurityDataProvider;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.subject.PrincipalCollection;

import javax.enterprise.context.ApplicationScoped;

/**
 *
 */
@ApplicationScoped
public class ApplicationSecurityData implements SecurityDataProvider {

    // Each user(principal) should have a unique id (type Serializable) and is used in the caching of the authorization data.
    // The value should be taken from the external resource where the information of the user is retrieved from.
    private int principalId = 0;

    @Override
    public AuthenticationInfo getAuthenticationInfo(AuthenticationToken authenticationToken) {
        if (authenticationToken instanceof UsernamePasswordToken) {
            UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) authenticationToken;

            // FIXME When using database as authenrtication resource, retrieve the iformation from it here.

            AuthenticationInfoBuilder authenticationInfoBuilder = new AuthenticationInfoBuilder();
            authenticationInfoBuilder.principalId(principalId++).name(authenticationToken.getPrincipal().toString());
            // FIXME: Change for production. Here we use username as password
            authenticationInfoBuilder.password(usernamePasswordToken.getUsername());
            //authenticationInfoBuilder.salt(byte[]); when using hashed password, set also 'algorithmName' in octopusConfig.properties file

            //authenticationInfoBuilder.addUserInfo(String key, Serializable value); When you want to store additional information for the user.
            return authenticationInfoBuilder.build();
        }
        return null;
    }

    @Override
    public AuthorizationInfo getAuthorizationInfo(PrincipalCollection principalCollection) {
        //AuthorizationInfoBuilder builder = new AuthorizationInfoBuilder();
        //UserPrincipal principal = (UserPrincipal) principalCollection.getPrimaryPrincipal();
        // Alternative UserPrincipal principal = principalCollection.oneByType(UserPrincipal.class);  can be used.
        // principal.getUserName() or principal.getId() are the preferred identifiers to lookup the authorization information of the user.
        return null;
        // return builder.build();
    }
}
