package com.authzed.intellij.spicedb;

import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

import java.util.Collection;

/**
 * Smoke tests: real-world schema shapes must parse without error elements.
 */
public class SpiceDbParsingSmokeTest extends BasePlatformTestCase {

    public void testFullSchemaShapesParseWithoutErrors() {
        PsiFile file = myFixture.configureByText("schema.zed", """
                use import
                use partial
                import "./compose/platform.zed";

                // a caveat with CEL body
                caveat not_expired(current_time timestamp, expiration_time timestamp) {
                    current_time <= expiration_time
                }

                caveat allowed_ips(user_ip ipaddress, allowed_range string) {
                    user_ip.in_cidr(allowed_range)
                }

                partial billing_platform {
                  relation billing_admin: login
                  permission manage_billing = billing_admin
                }

                definition platform {
                  ...billing_platform
                  relation administrator: login | role#member
                  permission super_admin = administrator
                }

                definition token {
                  relation owner: login | api_client | service_account#identifier
                  relation parent: platform
                  relation session: session with not_expired
                  relation temp: login with not_expired and expiration
                  relation anon: login:*

                  permission read = owner + parent->super_admin
                  permission complex = (owner + parent->super_admin) & owner - anon
                  permission chained = parent->manage_billing
                  permission any_owner = parent.any(administrator)
                  permission all_owner = parent.all(administrator)
                  permission nothing = nil
                }
                """);

        Collection<PsiErrorElement> errors =
                PsiTreeUtil.findChildrenOfType(file, PsiErrorElement.class);
        assertEmpty("Parse errors: " + errors, errors);
    }

    public void testExpirationUseFlagParses() {
        PsiFile file = myFixture.configureByText("schema.zed", """
                use expiration

                definition doc {
                  relation viewer: login with expiration
                  permission view = viewer
                }
                """);
        assertEmpty(PsiTreeUtil.findChildrenOfType(file, PsiErrorElement.class));
    }
}
