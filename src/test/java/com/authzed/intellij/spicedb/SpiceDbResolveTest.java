package com.authzed.intellij.spicedb;

import com.authzed.intellij.spicedb.psi.SpiceDbCaveatDef;
import com.authzed.intellij.spicedb.psi.SpiceDbNamedElement;
import com.authzed.intellij.spicedb.psi.SpiceDbObjectDef;
import com.authzed.intellij.spicedb.psi.SpiceDbPartialDef;
import com.authzed.intellij.spicedb.psi.SpiceDbPermissionDecl;
import com.authzed.intellij.spicedb.psi.SpiceDbRelationDecl;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveResult;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * Reference resolution tests across multiple .zed files, mirroring the
 * compose/<domain>/ layout of a real spicedb-schemas repo.
 */
public class SpiceDbResolveTest extends BasePlatformTestCase {

    private void addComposeFiles() {
        myFixture.addFileToProject("schema/compose/iam/iam.zed", """
                definition login {
                  relation platform: platform
                  permission read_profile = platform->org_admin
                }

                definition role {
                  relation member: login
                  permission read = member
                }
                """);

        myFixture.addFileToProject("schema/compose/iam/platform.zed", """
                partial org_platform {
                  relation org_administrator: login | role#member
                  permission org_admin = org_administrator
                }
                """);

        myFixture.addFileToProject("schema/compose/platform.zed", """
                definition platform {
                  ...org_platform
                  relation administrator: login
                  permission super_admin = administrator
                }
                """);

        myFixture.addFileToProject("schema/schema.zed", """
                use import
                import "./compose/platform.zed";

                caveat not_expired(current_time timestamp, expiration_time timestamp) {
                    current_time <= expiration_time
                }

                definition account {
                  relation platform: platform
                  relation holder: login | role#member
                  relation session: login with not_expired

                  permission read = holder + platform->org_admin
                  permission manage = platform->super_admin
                }
                """);
    }

    private List<PsiElement> resolveAtCaret(String fileName, String text) {
        myFixture.configureByText(fileName, text);
        PsiReference ref = myFixture.getFile()
                .findReferenceAt(myFixture.getCaretOffset());
        assertNotNull("No reference at caret", ref);
        List<PsiElement> targets = new ArrayList<>();
        if (ref instanceof PsiPolyVariantReference poly) {
            for (ResolveResult result : poly.multiResolve(false)) {
                targets.add(result.getElement());
            }
        } else {
            PsiElement resolved = ref.resolve();
            if (resolved != null) {
                targets.add(resolved);
            }
        }
        return targets;
    }

    public void testTypeRefResolvesToDefinitionAcrossFiles() {
        addComposeFiles();
        List<PsiElement> targets = resolveAtCaret("extra.zed", """
                definition widget {
                  relation owner: lo<caret>gin
                }
                """);
        assertSize(1, targets);
        assertInstanceOf(targets.get(0), SpiceDbObjectDef.class);
        assertEquals("login", ((SpiceDbObjectDef) targets.get(0)).getName());
    }

    public void testSubjectRelationResolvesToRelationInType() {
        addComposeFiles();
        List<PsiElement> targets = resolveAtCaret("extra.zed", """
                definition widget {
                  relation owner: role#mem<caret>ber
                }
                """);
        assertSize(1, targets);
        assertInstanceOf(targets.get(0), SpiceDbRelationDecl.class);
        assertEquals("member", ((SpiceDbRelationDecl) targets.get(0)).getName());
    }

    public void testLocalRefResolvesToRelation() {
        addComposeFiles();
        List<PsiElement> targets = resolveAtCaret("extra.zed", """
                definition widget {
                  relation owner: login
                  permission read = own<caret>er
                }
                """);
        assertSize(1, targets);
        assertInstanceOf(targets.get(0), SpiceDbRelationDecl.class);
    }

    public void testArrowTargetResolvesInRelationTargetType() {
        addComposeFiles();
        List<PsiElement> targets = resolveAtCaret("extra.zed", """
                definition widget {
                  relation platform: platform
                  permission manage = platform->super_a<caret>dmin
                }
                """);
        assertSize(1, targets);
        assertInstanceOf(targets.get(0), SpiceDbPermissionDecl.class);
        assertEquals("super_admin", ((SpiceDbPermissionDecl) targets.get(0)).getName());
    }

    public void testArrowTargetResolvesThroughPartialSplat() {
        addComposeFiles();
        // org_admin is defined in the org_platform partial, splatted into platform
        List<PsiElement> targets = resolveAtCaret("extra.zed", """
                definition widget {
                  relation platform: platform
                  permission read = platform->org_ad<caret>min
                }
                """);
        assertSize(1, targets);
        assertInstanceOf(targets.get(0), SpiceDbPermissionDecl.class);
        assertEquals("org_admin", ((SpiceDbPermissionDecl) targets.get(0)).getName());
    }

    public void testCaveatRefResolvesToCaveat() {
        addComposeFiles();
        List<PsiElement> targets = resolveAtCaret("extra.zed", """
                definition widget {
                  relation session: login with not_ex<caret>pired
                }
                """);
        assertSize(1, targets);
        assertInstanceOf(targets.get(0), SpiceDbCaveatDef.class);
    }

    public void testPartialSplatResolvesToPartial() {
        addComposeFiles();
        List<PsiElement> targets = resolveAtCaret("extra.zed", """
                definition widget {
                  ...org_plat<caret>form
                }
                """);
        assertSize(1, targets);
        assertInstanceOf(targets.get(0), SpiceDbPartialDef.class);
    }

    public void testBuiltinExpirationDoesNotResolveButIsSoft() {
        addComposeFiles();
        myFixture.configureByText("extra.zed", """
                use expiration
                definition widget {
                  relation session: login with expira<caret>tion
                }
                """);
        PsiReference ref = myFixture.getFile().findReferenceAt(myFixture.getCaretOffset());
        assertNotNull(ref);
        assertTrue("expiration reference should be soft", ref.isSoft());
        assertNull(ref.resolve());
    }

    public void testGeneratedFolderIsExcludedFromResolution() {
        addComposeFiles();
        // composed output and the packaged resource copy duplicate the login
        // definition; both must be ignored
        myFixture.addFileToProject("schema/generated/schema.zed", """
                definition login {
                  relation platform: platform
                  permission read_profile = platform->org_admin
                }
                """);
        myFixture.addFileToProject("src/main/resources/schema.zed", """
                definition login {
                  relation platform: platform
                  permission read_profile = platform->org_admin
                }
                """);
        myFixture.addFileToProject("target/classes/schema.zed", """
                definition login {
                  relation platform: platform
                  permission read_profile = platform->org_admin
                }
                """);
        List<PsiElement> targets = resolveAtCaret("extra.zed", """
                definition widget {
                  relation owner: lo<caret>gin
                }
                """);
        assertSize(1, targets);
        assertEquals("iam.zed", targets.get(0).getContainingFile().getName());
    }

    public void testTypeRefResolvesWhenRelationNameShadowsTypeName() {
        // Regression for issue #1: relation named identically to its type
        List<PsiElement> targets = resolveAtCaret("shadow.zed", """
                definition platform {
                }

                definition api_client {
                  relation platform: plat<caret>form
                }
                """);
        assertSize(1, targets);
        assertInstanceOf(targets.get(0), SpiceDbObjectDef.class);
        assertEquals("platform", ((SpiceDbObjectDef) targets.get(0)).getName());
    }

    public void testFindUsagesOfRelation() {
        addComposeFiles();
        myFixture.configureByText("extra.zed", """
                definition widget {
                  relation own<caret>er: login
                  permission read = owner
                  permission manage = owner
                }
                """);
        PsiElement declaration = myFixture.getElementAtCaret();
        assertInstanceOf(declaration, SpiceDbNamedElement.class);
        assertSize(2, myFixture.findUsages(declaration));
    }

    public void testFindUsagesSkipsExcludedCopies() {
        addComposeFiles();
        // duplicate copies referencing super_admin must not appear as usages
        myFixture.addFileToProject("schema/generated/schema.zed", """
                definition account2 {
                  relation platform: platform
                  permission manage = platform->super_admin
                }
                """);
        myFixture.addFileToProject("target/classes/schema.zed", """
                definition account2 {
                  relation platform: platform
                  permission manage = platform->super_admin
                }
                """);
        myFixture.configureByFile("schema/compose/platform.zed");
        // schema.zed has one real usage: platform->super_admin in account.manage
        PsiElement declaration = null;
        for (PsiElement candidate : com.intellij.psi.util.PsiTreeUtil
                .findChildrenOfType(myFixture.getFile(), SpiceDbPermissionDecl.class)) {
            if ("super_admin".equals(((SpiceDbPermissionDecl) candidate).getName())) {
                declaration = candidate;
            }
        }
        assertNotNull(declaration);
        assertSize(1, myFixture.findUsages(declaration));
    }
}
