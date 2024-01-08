package org.jlab.pam.business.session;

import java.math.BigInteger;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
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
import javax.sql.DataSource;
import org.jlab.pam.persistence.entity.Staff;
import org.jlab.pam.persistence.entity.Workgroup;

/**
 *
 * @author ryans
 */
@Stateless
public class StaffFacade extends AbstractFacade<Staff> {

    @PersistenceContext(unitName = "pamPU")
    private EntityManager em;
    @Resource(mappedName = "jdbc/pam")
    DataSource ds;
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public StaffFacade() {
        super(Staff.class);
    }

    public Staff findByUsername(String username) {
        TypedQuery<Staff> q = em.createQuery("select s from Staff s where username = :username", Staff.class);

        q.setParameter("username", username);

        Staff staff = null;

        List<Staff> resultList = q.getResultList();

        if (resultList != null && !resultList.isEmpty()) {
            staff = resultList.get(0);
        }

        return staff;
    }

    public List<Staff> filterList(String username, String firstname, String lastname, BigInteger userId, BigInteger groupId, int offset, int max) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Staff> cq = cb.createQuery(Staff.class);
        Root<Staff> root = cq.from(Staff.class);
        cq.select(root);

        List<Predicate> filters = new ArrayList<>();

        if (username != null && !username.isEmpty()) {
            filters.add(cb.like(cb.lower(root.<String>get("username")), username.toLowerCase()));
        }

        if (firstname != null && !firstname.isEmpty()) {
            filters.add(cb.like(cb.lower(root.<String>get("firstname")), firstname.toLowerCase()));
        }

        if (lastname != null && !lastname.isEmpty()) {
            filters.add(cb.like(cb.lower(root.<String>get("lastname")), lastname.toLowerCase()));
        }

        if (userId != null) {
            filters.add(cb.equal(root.get("staffId"), userId));
        }

        if (groupId != null) {
            Subquery<BigInteger> subquery = cq.subquery(BigInteger.class);
            Root<Staff> subroot = subquery.from(Staff.class);
            subquery.select(subroot.<BigInteger>get("staffId"));
            Join<Staff, Workgroup> groupList = subroot.join("groupList");
            subquery.where(cb.equal(groupList.get("workgroupId"), groupId));
            filters.add(cb.in(root.get("staffId")).value(subquery));
        }

        if (!filters.isEmpty()) {
            cq.where(cb.and(filters.toArray(new Predicate[]{})));
        }

        List<Order> orders = new ArrayList<>();
        Path p0 = root.get("lastname");
        Order o0 = cb.asc(p0);
        orders.add(o0);
        Path p1 = root.get("staffId");
        Order o1 = cb.asc(p1);
        orders.add(o1);        
        cq.orderBy(orders);
        return getEntityManager().createQuery(cq).setFirstResult(offset).setMaxResults(max).getResultList();
    }

    public long countList(String username, String firstname, String lastname, BigInteger userId, BigInteger groupId, int offset, int max) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Staff> root = cq.from(Staff.class);

        List<Predicate> filters = new ArrayList<>();

        if (username != null && !username.isEmpty()) {
            filters.add(cb.like(cb.lower(root.<String>get("username")), username.toLowerCase()));
        }

        if (firstname != null && !firstname.isEmpty()) {
            filters.add(cb.like(cb.lower(root.<String>get("firstname")), firstname.toLowerCase()));
        }

        if (lastname != null && !lastname.isEmpty()) {
            filters.add(cb.like(cb.lower(root.<String>get("lastname")), lastname.toLowerCase()));
        }

        if (userId != null) {
            filters.add(cb.equal(root.get("staffId"), userId));
        }

        if (groupId != null) {
            Subquery<BigInteger> subquery = cq.subquery(BigInteger.class);
            Root<Staff> subroot = subquery.from(Staff.class);
            subquery.select(subroot.<BigInteger>get("staffId"));
            Join<Staff, Workgroup> groupList = subroot.join("groupList");
            subquery.where(cb.equal(groupList.get("workgroupId"), groupId));
            filters.add(cb.in(root.get("staffId")).value(subquery));
        }

        if (!filters.isEmpty()) {
            cq.where(cb.and(filters.toArray(new Predicate[]{})));
        }

        cq.select(cb.count(root));
        TypedQuery<Long> q = getEntityManager().createQuery(cq);
        return q.getSingleResult();
    }

    public Staff find(BigInteger staffId) {
        return super.find(staffId);
    }

    @SuppressWarnings("unchecked")    
    public void setLocalPassword(BigInteger staffId, String password) throws SQLException {
        Query q = em.createNativeQuery("select count(*) from local_password where staff_id = " + staffId);
        
        List<Object> resultList = q.getResultList();
        
        boolean insertRow = true;
        
        if(resultList != null && !resultList.isEmpty()) {
            Number result = (Number)resultList.get(0);
            
            if(result != null && result.longValue() > 0) {
                    insertRow = false;
            }
        }
        
        final String insertProcedure = "app_user_security.add_password";
        final String updateProcedure = "app_user_security.set_password";
        
        String procedure;
        
        if(insertRow) {
            procedure = insertProcedure;
        } else {
            procedure = updateProcedure;
        }
        
        Connection con = null;
        try {
            con = ds.getConnection();
            
            String query = "{call " + procedure + "(?, ?)}";
            CallableStatement cstmt = con.prepareCall(query);
            cstmt.setInt(1, staffId.intValue());
            cstmt.setString(2, password);
            cstmt.execute();

            cstmt.close();            
        } finally {
            if(con != null) {
                con.close();
            }
        }
    }
}
