package io.phasetwo.service.auth.invitation;

import jakarta.ws.rs.core.MultivaluedMap;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.Config;
import org.keycloak.authentication.FormAction;
import org.keycloak.authentication.FormActionFactory;
import org.keycloak.authentication.FormContext;
import org.keycloak.authentication.ValidationContext;
import org.keycloak.authentication.forms.RegistrationPage;
import org.keycloak.events.Details;
import org.keycloak.events.Errors;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.*;
import org.keycloak.models.utils.FormMessage;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.services.messages.Messages;
import io.phasetwo.service.model.OrganizationProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class InvitationFormCheck implements FormAction, FormActionFactory {

    private static final String PROVIDER_ID = "invitation-check-action";
    private static AuthenticationExecutionModel.Requirement[] REQUIREMENT_CHOICES = {
            AuthenticationExecutionModel.Requirement.REQUIRED,
            AuthenticationExecutionModel.Requirement.DISABLED
    };


    @Override
    public String getHelpText() {
        return "Validates that given mail address has an active invitation.";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return null;
    }

    @Override
    public void validate(ValidationContext context) {
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        List<FormMessage> errors = new ArrayList<>();
        context.getEvent().detail(Details.REGISTER_METHOD, "form");
        if (StringUtils.isBlank(formData.getFirst(RegistrationPage.FIELD_EMAIL))) {
            errors.add(new FormMessage(RegistrationPage.FIELD_EMAIL, Messages.INVALID_EMAIL));
        } else {
            OrganizationProvider oProvider = context.getSession().getProvider(OrganizationProvider.class);
            long invCnt = oProvider.getUserInvitationsStream(
                    context.getRealm(),
                    new UserModelAdapter(){
                        @Override
                        public String getEmail() {
                            return formData.getFirst(RegistrationPage.FIELD_EMAIL);
                        }
                    }
            ).count();
            if(!(invCnt > 0)) {
                errors.add(new FormMessage(RegistrationPage.FIELD_EMAIL, "registration.restricted.invitation.only"));
            }
        }

        if (!errors.isEmpty()) {
            context.error(Errors.INVALID_REGISTRATION);
            formData.remove(RegistrationPage.FIELD_EMAIL);
            context.validationError(formData, errors);
        } else {

            context.success();
        }
    }

    @Override
    public void success(FormContext context) {
    }

    @Override
    public void buildPage(FormContext context, LoginFormsProvider form) {
    }

    @Override
    public boolean requiresUser() {
        return false;
    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {

    }

    @Override
    public boolean isUserSetupAllowed() {
        return false;
    }

    @Override
    public void close() {

    }

    @Override
    public String getDisplayType() {
        return "Invitation Check";
    }

    @Override
    public String getReferenceCategory() {
        return null;
    }

    @Override
    public boolean isConfigurable() {
        return false;
    }

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return REQUIREMENT_CHOICES;
    }

    @Override
    public FormAction create(KeycloakSession session) {
        return this;
    }

    @Override
    public void init(Config.Scope config) {

    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {

    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    abstract static class UserModelAdapter implements UserModel {
        @Override
        public boolean hasDirectRole(RoleModel role) {
            return UserModel.super.hasDirectRole(role);
        }

        @Override
        public Stream<RoleModel> getRealmRoleMappingsStream() {
            return Stream.empty();
        }

        @Override
        public Stream<RoleModel> getClientRoleMappingsStream(ClientModel clientModel) {
            return Stream.empty();
        }

        @Override
        public boolean hasRole(RoleModel roleModel) {
            return false;
        }

        @Override
        public void grantRole(RoleModel roleModel) {

        }

        @Override
        public Stream<RoleModel> getRoleMappingsStream() {
            return Stream.empty();
        }

        @Override
        public void deleteRoleMapping(RoleModel roleModel) {

        }

        @Override
        public void addRequiredAction(RequiredAction action) {
            UserModel.super.addRequiredAction(action);
        }

        @Override
        public void removeRequiredAction(RequiredAction action) {
            UserModel.super.removeRequiredAction(action);
        }

        @Override
        public Stream<GroupModel> getGroupsStream(String search, Integer first, Integer max) {
            return UserModel.super.getGroupsStream(search, first, max);
        }

        @Override
        public long getGroupsCount() {
            return UserModel.super.getGroupsCount();
        }

        @Override
        public long getGroupsCountByNameContaining(String search) {
            return UserModel.super.getGroupsCountByNameContaining(search);
        }

        @Override
        public void joinGroup(GroupModel group, MembershipMetadata metadata) {
            UserModel.super.joinGroup(group, metadata);
        }

        @Override
        public boolean isFederated() {
            return UserModel.super.isFederated();
        }

        @Override
        public String getId() {
            return "";
        }

        @Override
        public String getUsername() {
            return "";
        }

        @Override
        public void setUsername(String s) {

        }

        @Override
        public Long getCreatedTimestamp() {
            return 0L;
        }

        @Override
        public void setCreatedTimestamp(Long aLong) {

        }

        @Override
        public boolean isEnabled() {
            return false;
        }

        @Override
        public void setEnabled(boolean b) {

        }

        @Override
        public void setSingleAttribute(String s, String s1) {

        }

        @Override
        public void setAttribute(String s, List<String> list) {

        }

        @Override
        public void removeAttribute(String s) {

        }

        @Override
        public String getFirstAttribute(String s) {
            return "";
        }

        @Override
        public Stream<String> getAttributeStream(String s) {
            return Stream.empty();
        }

        @Override
        public Map<String, List<String>> getAttributes() {
            return Map.of();
        }

        @Override
        public Stream<String> getRequiredActionsStream() {
            return Stream.empty();
        }

        @Override
        public void addRequiredAction(String s) {

        }

        @Override
        public void removeRequiredAction(String s) {

        }

        @Override
        public String getFirstName() {
            return "";
        }

        @Override
        public void setFirstName(String s) {

        }

        @Override
        public String getLastName() {
            return "";
        }

        @Override
        public void setLastName(String s) {

        }

        @Override
        public String getEmail() {
            return "";
        }

        @Override
        public void setEmail(String s) {

        }

        @Override
        public boolean isEmailVerified() {
            return false;
        }

        @Override
        public void setEmailVerified(boolean b) {

        }

        @Override
        public Stream<GroupModel> getGroupsStream() {
            return Stream.empty();
        }

        @Override
        public void joinGroup(GroupModel groupModel) {

        }

        @Override
        public void leaveGroup(GroupModel groupModel) {

        }

        @Override
        public boolean isMemberOf(GroupModel groupModel) {
            return false;
        }

        @Override
        public String getFederationLink() {
            return "";
        }

        @Override
        public void setFederationLink(String s) {

        }

        @Override
        public String getServiceAccountClientLink() {
            return "";
        }

        @Override
        public void setServiceAccountClientLink(String s) {

        }

        @Override
        public SubjectCredentialManager credentialManager() {
            return null;
        }
    }

}
