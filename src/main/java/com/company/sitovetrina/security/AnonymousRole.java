package com.company.sitovetrina.security;

import com.company.sitovetrina.entity.Configsitovetrina;
import com.company.sitovetrina.entity.Newsletter;
import com.company.sitovetrina.entity.Servizio;
import io.jmix.security.model.EntityPolicyAction;
import io.jmix.security.role.annotation.EntityPolicy;
import io.jmix.security.role.annotation.ResourceRole;
import io.jmix.security.role.annotation.SpecificPolicy;
import io.jmix.securityflowui.role.annotation.ViewPolicy;

@ResourceRole(name = "AnonymousRole", code = AnonymousRole.CODE, scope = "UI")
public interface AnonymousRole {
    String CODE = "anonymous-role";

    @ViewPolicy(viewIds = {"Home", "LoginView", "Servizi"})
    void screens();

    @SpecificPolicy(resources = "ui.loginToUi")
    void specific();

    @EntityPolicy(entityClass = Configsitovetrina.class, actions = EntityPolicyAction.READ)
    void configsitovetrina();

    @EntityPolicy(entityClass = Servizio.class, actions = EntityPolicyAction.READ)
    void servizio();

    @EntityPolicy(entityClass = Newsletter.class, actions = {EntityPolicyAction.CREATE, EntityPolicyAction.READ})
    void newsletter();
}
