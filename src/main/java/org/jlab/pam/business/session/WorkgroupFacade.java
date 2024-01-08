package org.jlab.pam.business.session;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.jlab.smoothness.business.exception.UserFriendlyException;
import org.jlab.pam.persistence.entity.Staff;
import org.jlab.pam.persistence.entity.Workgroup;

/**
 *
 * @author ryans
 */
@Stateless
@DeclareRoles({"pam-admin"})
public class WorkgroupFacade extends AbstractFacade<Workgroup> {

    @PersistenceContext(unitName = "pamPU")
    private EntityManager em;

    @EJB
    StaffFacade staffFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public WorkgroupFacade() {
        super(Workgroup.class);
    }

    @PermitAll
    public List<Workgroup> filterList(String name, BigInteger groupId, BigInteger staffId,
            int offset, int max) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Workgroup> cq = cb.createQuery(Workgroup.class);
        Root<Workgroup> root = cq.from(Workgroup.class);
        cq.select(root);

        List<Predicate> filters = new ArrayList<>();

        if (name != null && !name.isEmpty()) {
            filters.add(cb.like(cb.lower(root.<String>get("name")), name.toLowerCase()));
        }

        if (groupId != null) {
            filters.add(cb.equal(root.get("workgroupId"), groupId));
        }

        if (staffId != null) {
            Subquery<BigInteger> subquery = cq.subquery(BigInteger.class);
            Root<Workgroup> subroot = subquery.from(Workgroup.class);
            subquery.select(subroot.<BigInteger>get("workgroupId"));
            Join<Workgroup, Staff> staffList = subroot.join("staffList");
            subquery.where(cb.equal(staffList.get("staffId"), staffId));
            filters.add(cb.in(root.get("workgroupId")).value(subquery));
        }

        if (!filters.isEmpty()) {
            cq.where(cb.and(filters.toArray(new Predicate[]{})));
        }

        List<Order> orders = new ArrayList<>();
        Path p0 = root.get("name");
        Order o0 = cb.asc(p0);
        orders.add(o0);
        cq.orderBy(orders);
        return getEntityManager().createQuery(cq).setFirstResult(offset).setMaxResults(max).getResultList();
    }

    @PermitAll
    public long countList(String name, BigInteger groupId, BigInteger staffId, int offset, int max) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Workgroup> root = cq.from(Workgroup.class);

        List<Predicate> filters = new ArrayList<>();

        if (name != null && !name.isEmpty()) {
            filters.add(cb.like(cb.lower(root.<String>get("name")), name.toLowerCase()));
        }

        if (groupId != null) {
            filters.add(cb.equal(root.get("workgroupId"), groupId));
        }

        if (staffId != null) {
            Subquery<BigInteger> subquery = cq.subquery(BigInteger.class);
            Root<Workgroup> subroot = subquery.from(Workgroup.class);
            subquery.select(subroot.<BigInteger>get("workgroupId"));
            Join<Workgroup, Staff> staffList = subroot.join("staffList");
            subquery.where(cb.equal(staffList.get("staffId"), staffId));
            filters.add(cb.in(root.get("workgroupId")).value(subquery));
        }

        if (!filters.isEmpty()) {
            cq.where(cb.and(filters.toArray(new Predicate[]{})));
        }

        cq.select(cb.count(root));
        TypedQuery<Long> q = getEntityManager().createQuery(cq);
        return q.getSingleResult();
    }

    @PermitAll
    public Workgroup find(BigInteger groupId) {
        return super.find(groupId);
    }

    @RolesAllowed("pam-admin")
    public void addUserToGroup(BigInteger groupId, String username) throws UserFriendlyException {
        if (groupId == null) {
            throw new UserFriendlyException("groupId must not be null");
        }

        if (username == null || username.isEmpty()) {
            throw new UserFriendlyException("username must not be empty");
        }

        Workgroup group = find(groupId);

        if (group == null) {
            throw new UserFriendlyException("Unable to find group with ID: " + groupId);
        }

        Staff staff = staffFacade.findByUsername(username);

        if (staff == null) {
            throw new UserFriendlyException("Unable to find staff with username: " + username);
        }

        if (group.getStaffList().contains(staff)) {
            throw new UserFriendlyException("User " + username + " is already in group " + group.getName());
        } else {
            group.getStaffList().add(staff);
        }
    }

    @RolesAllowed("pam-admin")
    public void removeUserFromGroup(BigInteger groupId, BigInteger staffId) throws UserFriendlyException {
        if (groupId == null) {
            throw new UserFriendlyException("groupId must not be null");
        }

        if (staffId == null) {
            throw new UserFriendlyException("staffId must not be null");
        }

        Workgroup group = find(groupId);

        if (group == null) {
            throw new UserFriendlyException("Unable to find group with ID: " + groupId);
        }

        Staff staff = staffFacade.find(staffId);

        if (staff == null) {
            throw new UserFriendlyException("Unable to find staff with id: " + staffId);
        }

        group.getStaffList().remove(staff);
    }

    @Override
    @PermitAll
    public List<Workgroup> findAll() {
        TypedQuery<Workgroup> q = em.createQuery("select w from Workgroup w", Workgroup.class);

        return q.getResultList();
    }

    @RolesAllowed("pam-admin")
    public void batchEditMembership(BigInteger groupId, String usernames) throws UserFriendlyException {
        if (groupId == null) {
            throw new UserFriendlyException("groupId must not be null");
        }

        Workgroup group = find(groupId);

        if (group == null) {
            throw new UserFriendlyException("Unable to find group with ID: " + groupId);
        }

        List<Staff> members = new ArrayList<>();

        if (usernames != null && !usernames.trim().isEmpty()) {
            String[] tokens = usernames.split(",|\\s");

            for (String username : tokens) {
                if (username != null && !username.isEmpty()) {
                    Staff staff = staffFacade.findByUsername(username);

                    if (staff == null) {
                        throw new UserFriendlyException("Unable to find staff with username: " + username);
                    }

                    members.add(staff);
                }
            }
        }

        group.setStaffList(members);
    }

    private boolean isExistingName(String name) {
        Query q = em.createNativeQuery("select count(*) from pam_owner.workgroup where name = :name");

        q.setParameter("name", name);

        Long count = ((Number) q.getSingleResult()).longValue();

        return count > 0;
    }

    private boolean containsWhiteSpace(final String testCode) {
        if (testCode != null) {
            for (int i = 0; i < testCode.length(); i++) {
                if (Character.isWhitespace(testCode.charAt(i))) {
                    return true;
                }
            }
        }
        return false;
    }

    @RolesAllowed("pam-admin")
    public void addGroup(String name, String description) throws UserFriendlyException {
        if (name == null || name.trim().isEmpty()) {
            throw new UserFriendlyException("Name must not be empty");
        }

        if (description == null || description.trim().isEmpty()) {
            throw new UserFriendlyException("Description must not be empty");
        }

        if (name.length() > 8) {
            throw new UserFriendlyException("Name must be no more than 8 characters");
        }

        if (containsWhiteSpace(name)) {
            throw new UserFriendlyException("Name must not contain any whitespace");
        }

        name = name.toLowerCase();

        if (isExistingName(name)) {
            throw new UserFriendlyException("Name already in use");
        }

        Workgroup group = new Workgroup();

        group.setName(name);
        group.setDescription(description);

        create(group);
    }

    @RolesAllowed("pam-admin")
    public void deleteGroup(BigInteger groupId) throws UserFriendlyException {
        if (groupId == null) {
            throw new UserFriendlyException("groupId must not be null");
        }

        Workgroup group = find(groupId);

        if (group == null) {
            throw new UserFriendlyException("Unable to find group with ID: " + groupId);
        }

        remove(group);
    }

    @PermitAll
    public List<Date> findGroupMembershipHistories(String name) {
        Query q = em.createNativeQuery("select unique(version_date) from workgroup_membership_history where workgroup_id = (select workgroup_id from workgroup where name = '" + name + "')");

        List<Date> dateList = q.getResultList();

        return dateList;
    }

    @PermitAll
    public List<Staff> findGroupMembershipHistory(String name, Date versionDate) {
        Query q = em.createNativeQuery("select staff_id, username, firstname, lastname from workgroup_membership_history inner join staff using(staff_id) where version_date = :versionDate and workgroup_id = (select workgroup_id from workgroup where name = :groupName) order by lastname asc");

        q.setParameter("versionDate", versionDate);
        q.setParameter("groupName", name);

        List<Staff> staffList = new ArrayList<>();

        List<Object[]> recordList = q.getResultList();

        for(Object[] row: recordList) {
            Staff s = new Staff();

            Number staffId = (Number)row[0];
            String username = (String)row[1];
            String firstname = (String)row[2];
            String lastname = (String)row[3];

            s.setStaffId(BigInteger.valueOf(staffId.longValue()));
            s.setUsername(username);
            s.setFirstname(firstname);
            s.setLastname(lastname);

            staffList.add(s);
        }

        return staffList;
    }

    @PermitAll
    public Workgroup findByName(String name) {
        TypedQuery<Workgroup> q = em.createQuery("select w from Workgroup w where name = :name", Workgroup.class);

        q.setParameter("name", name);

        List<Workgroup> workgroupList = q.getResultList();

        Workgroup group = null;

        if(workgroupList != null && !workgroupList.isEmpty()) {
            group = workgroupList.get(0);
        }

        return group;
    }
}
