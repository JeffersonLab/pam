package org.jlab.pam.business.session;

import org.jlab.pam.persistence.entity.Staff;
import org.jlab.pam.persistence.entity.Workgroup;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

/**
 *
 * @author ryans
 */
@Stateless
@DeclareRoles({"pam-admin"})
public class LDAPFacade extends AbstractFacade<Workgroup> {

    @PersistenceContext(unitName = "pamPU")
    private EntityManager em;

    @EJB
    StaffFacade staffFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public LDAPFacade() {
        super(Workgroup.class);
    }

    private LdapContext connect() throws NamingException {
        final String ldapServer = System.getenv("LDAP_SERVER_URL");
        final String ldapUser = System.getenv("LDAP_USER");
        final String ldapPass = System.getenv("LDAP_PASS");

        Hashtable<String, Object> env = new Hashtable<String, Object>();
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, ldapServer);
        env.put(Context.SECURITY_PRINCIPAL, ldapUser);
        env.put(Context.SECURITY_CREDENTIALS, ldapPass);

        LdapContext ctx = new InitialLdapContext(env, null);

        return ctx;
    }

    @PermitAll
    public List<Workgroup> findOpsRoles() throws NamingException {
        List<Workgroup> roleList = new ArrayList<>();

        final String groupBase = System.getenv("LDAP_GROUP_PATH");
        final String opsManagedCn = System.getenv("LDAP_OPS_MANAGED_CN");

        LdapContext ctx = connect();

        String searchFilter = "memberOf=cn=" + opsManagedCn + "," + groupBase;

        SearchControls searchControls = new SearchControls();
        searchControls.setReturningAttributes(new String[]{"cn", "description"});
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

        NamingEnumeration<SearchResult> results = ctx.search(groupBase, searchFilter, searchControls);

        while(results.hasMoreElements()) {
            SearchResult result = results.nextElement();
            Attributes attrs = result.getAttributes();
            Attribute cn = attrs.get("cn");
            Attribute description = attrs.get("description");
            Workgroup workgroup = new Workgroup();
            workgroup.setName(cn.get().toString());
            workgroup.setDescription(description.get().toString());
            roleList.add(workgroup);
        }

        ctx.close();

        Collections.sort(roleList, new Comparator<Workgroup>() {
            @Override
            public int compare(Workgroup o1, Workgroup o2) {
                return o1.getDescription().compareTo(o2.getDescription());
            }
        });

        return roleList;
    }

    @PermitAll
    public Workgroup findGroupByCn(String cn) throws NamingException {
        Workgroup group = null;

        final String ldapBase = System.getenv("LDAP_GROUP_PATH");

        LdapContext ctx = connect();

        String searchFilter = "cn=" + cn;

        SearchControls searchControls = new SearchControls();
        searchControls.setReturningAttributes(new String[]{"description"});
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

        NamingEnumeration<SearchResult> results = ctx.search(ldapBase, searchFilter, searchControls);

        if(results.hasMoreElements()) {
            SearchResult result = results.nextElement();
            Attributes attrs = result.getAttributes();
            Attribute description = attrs.get("description");
            group = new Workgroup();
            group.setName(cn);
            group.setDescription(description.get().toString());
        }

        ctx.close();

        return group;
    }

    @PermitAll
    public List<Staff> findStaffByGroupCn(String cn) throws NamingException {
        List<Staff> staffList = new ArrayList<>();

        final String groupBase = System.getenv("LDAP_GROUP_PATH");
        final String userBase = System.getenv("LDAP_USER_PATH");

        LdapContext ctx = connect();

        String searchFilter = "memberOf=cn=" + cn + "," + groupBase;

        SearchControls searchControls = new SearchControls();
        searchControls.setReturningAttributes(new String[]{"givenName", "sn", "uid"});
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

        NamingEnumeration<SearchResult> results = ctx.search(userBase, searchFilter, searchControls);

        while(results.hasMoreElements()) {
            SearchResult result = results.nextElement();
            Attributes attrs = result.getAttributes();
            Attribute givenName = attrs.get("givenName");
            Attribute sn = attrs.get("sn");
            Attribute uid = attrs.get("uid");
            Staff staff = new Staff();
            staff.setUsername(uid.get().toString());
            staff.setFirstname(givenName.get().toString());
            staff.setLastname(sn.get().toString());
            staffList.add(staff);
        }

        ctx.close();

        Collections.sort(staffList, new Comparator<Staff>() {
            @Override
            public int compare(Staff o1, Staff o2) {
                return o1.getLastname().compareTo(o2.getLastname());
            }
        });


        return staffList;
    }
}
