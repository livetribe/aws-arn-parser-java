/*
 * Copyright 2016 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.livetribe.aws.arn;

import static org.livetribe.aws.arn.Util.requireNonNull;


/**
 * @author LiveTribe
 */
public class IamArn extends Arn {
    private final String accountId;

    IamArn(String accountId) {
        super("aws", "iam");
        this.accountId = requireNonNull(accountId, "Region must not be null");
    }

    public String getAccountId() {
        return accountId;
    }

    static IamArn parseArn(ArnLexer lexer) throws ArnSyntaxException {

        lexer.colon();
        String accountId = lexer.scanToColon();
        lexer.colon();

        String kind = lexer.scan('/');
        if (kind.equals("root")) {
            if (!lexer.done()) {
                throw new ArnSyntaxException("Unrecognized content after root in IAM ARN");
            }
            return new IamRootArn(accountId);
        } else if (kind.equals("user")) {
            lexer.consume("/");
            String userName = lexer.scanToEOL();
            return new IamUserArn(accountId, userName);
        } else if (kind.equals("group")) {
            lexer.consume("/");
            String group = lexer.scanToEOL();
            return new IamGroupArn(accountId, group);
        } else if (kind.equals("role")) {
            lexer.consume("/");
            String role = lexer.scanToEOL();
            return new IamRoleArn(accountId, role);
        } else if (kind.equals("policy")) {
            lexer.consume("/");
            String policy = lexer.scanToEOL();
            return new IamPolicyArn(accountId, policy);
        } else if (kind.equals("instance-profile")) {
            lexer.consume("/");
            String instanceProfile = lexer.scanToEOL();
            return new IamInstanceProfileArn(accountId, instanceProfile);
        } else if (kind.equals("federated-user")) {
            lexer.consume("/");
            String federatedUser = lexer.scanToEOL();
            return new IamFederatedUserArn(accountId, federatedUser);
        } else if (kind.equals("assumed-role")) {
            lexer.consume("/");
            String role = lexer.scan('/');
            lexer.consume("/");
            String roleSession = lexer.scanToEOL();
            return new IamAssumedRoleArn(accountId, role, roleSession);
        } else if (kind.equals("mfa")) {
            lexer.consume("/");
            String virtualDevice = lexer.scanToEOL();
            return new IamMfaArn(accountId, virtualDevice);
        } else if (kind.equals("server-certificate")) {
            lexer.consume("/");
            String certificate = lexer.scanToEOL();
            return new IamServerCertificateArn(accountId, certificate);
        } else if (kind.equals("saml-provider")) {
            lexer.consume("/");
            String provider = lexer.scanToEOL();
            return new IamSamlProviderArn(accountId, provider);
        } else if (kind.equals("oidc-provider")) {
            lexer.consume("/");
            String provider = lexer.scanToEOL();
            return new IamOidcProviderArn(accountId, provider);
        }

        throw new ArnSyntaxException("Unrecognized ARN");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IamArn)) return false;
        if (!super.equals(o)) return false;

        IamArn that = (IamArn)o;

        return accountId.equals(that.accountId);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + accountId.hashCode();
        return result;
    }
}
