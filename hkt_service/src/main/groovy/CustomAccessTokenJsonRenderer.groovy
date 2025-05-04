import com.tcb.system.security.Role
import com.tcb.system.security.RoleGroup
import com.tcb.system.security.User
import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.rest.token.AccessToken
import grails.plugin.springsecurity.rest.token.rendering.AccessTokenJsonRenderer
import groovy.json.JsonBuilder
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority

@Transactional
class CustomAccessTokenJsonRenderer implements AccessTokenJsonRenderer {
    @Override
    String generateJson(AccessToken accessToken) {
        def listRoleGroup = null
        def roles = accessToken.authorities.collect { GrantedAuthority role -> role.authority }
        if (!roles) {
            User user = User.findByUsername(accessToken.principal.username)
            Collection<GrantedAuthority> authority = user.getAuthorities().collect { new SimpleGrantedAuthority(it.authority) }
            roles = authority.collect { GrantedAuthority role -> role.authority }
        }
        def listRole = Role.findAllByAuthorityInList(roles)
        if (listRole) {
            listRoleGroup = RoleGroup.findAllByIdInList(listRole*.roleGroupId)
        }
        // Add extra custom parameters if you want in this map to be rendered in login response
        Map response = [
                username    : accessToken.principal.username,
                access_token: accessToken.accessToken,
                token_type  : "Bearer",
                expires_in  : accessToken.expiration,
                roles       : roles,
                roleGroups  : listRoleGroup ? listRoleGroup*.code : []
        ]

        return new JsonBuilder(response).toPrettyString()
    }
}
