package com.company.sitovetrina.security;

import com.company.sitovetrina.entity.Configsitovetrina;
import io.jmix.security.model.EntityPolicyAction;
import io.jmix.security.role.annotation.EntityPolicy;
import io.jmix.security.role.annotation.ResourceRole;
import io.jmix.security.role.annotation.SpecificPolicy;
import io.jmix.securityflowui.role.annotation.ViewPolicy;

@ResourceRole(name = "AnonymousRole", code = AnonymousRole.CODE, scope = "UI")
public interface AnonymousRole {
    String CODE = "anonymous-role";
    @EntityPolicy(entityClass = Configsitovetrina.class, actions = EntityPolicyAction.READ)
    void configsitovetrina();

    @ViewPolicy(viewIds = {"Home", "LoginView"})
    void screens();

    @SpecificPolicy(resources = "ui.loginToUi")
    void specific();
}
