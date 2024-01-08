package org.jlab.pam.presentation.rest;

import java.io.OutputStream;
import java.math.BigInteger;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.stream.JsonGenerator;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.jlab.smoothness.business.util.ObjectUtil;
import org.jlab.pam.business.session.StaffFacade;
import org.jlab.pam.persistence.entity.Staff;

/**
 *
 * @author ryans
 */
@Path("users")
public class Users {

    private StaffFacade lookupStaffFacade() {
        try {
            InitialContext ic = new InitialContext();
            return (StaffFacade) ic.lookup("java:global/staff/StaffFacade");
        } catch (NamingException e) {
            throw new RuntimeException("Unable to obtain EJB", e);
        }
    }

    @GET
    @Produces("application/json")
    public Response getUsersJson(
            @QueryParam("username") final String username,
            @QueryParam("firstname") final String firstname,
            @QueryParam("lastname") final String lastname,
            @QueryParam("user-id") final BigInteger userId,
            @QueryParam("group-id") final BigInteger groupId,
            @QueryParam("offset") @DefaultValue("0") final int offset,
            @QueryParam("max") @DefaultValue("" + Integer.MAX_VALUE) final int max) {
        StreamingOutput stream = new StreamingOutput() {
            @Override
            public void write(OutputStream out) {
                try (JsonGenerator gen = Json.createGenerator(out)) {
                    StaffFacade staffFacade = lookupStaffFacade();

                    List<Staff> staffList = staffFacade.filterList(username, firstname, lastname, userId, groupId, offset, max);

                    gen.writeStartArray();
                    for (Staff staff : staffList) {
                        gen.writeStartObject()
                                .write("id", staff.getStaffId())
                                .write("firstname", ObjectUtil.coalesce(staff.getFirstname(), ""))
                                .write("lastname", ObjectUtil.coalesce(staff.getLastname(), ""))
                                .write("username", ObjectUtil.coalesce(staff.getUsername(), "")).writeEnd();
                    }
                    gen.writeEnd();
                }
            }
        };
        return Response.ok(stream).build();
    }

    @GET
    @Path("{id}")
    @Produces("application/json")
    public Response getUserJson(@PathParam("id") final BigInteger id) {
        StreamingOutput stream = new StreamingOutput() {
            @Override
            public void write(OutputStream out) {
                try (JsonGenerator gen = Json.createGenerator(out)) {
                    StaffFacade staffFacade = lookupStaffFacade();

                    Staff staff = staffFacade.find(id);

                    if (staff == null) {
                        throw new JsonWebApplicationException(Response.Status.NOT_FOUND, "User not found");
                    }

                    gen.writeStartObject()
                            .write("id", staff.getStaffId())
                            .write("firstname", ObjectUtil.coalesce(staff.getFirstname(), ""))
                            .write("lastname", ObjectUtil.coalesce(staff.getLastname(), ""))
                            .write("username", ObjectUtil.coalesce(staff.getUsername(), "")).writeEnd();
                }
            }
        };
        return Response.ok(stream).build();
    }

    @GET
    @Produces("application/javascript")
    public Response getUsersJsonp(
            @QueryParam("username") final String username,
            @QueryParam("firstname") final String firstname,
            @QueryParam("lastname") final String lastname,
            @QueryParam("user-id") final BigInteger userId,
            @QueryParam("group-id") final BigInteger groupId,
            @QueryParam("offset") @DefaultValue("0") final int offset,
            @QueryParam("max") @DefaultValue("" + Integer.MAX_VALUE) final int max,
            @QueryParam("callback") @DefaultValue("callback") final String callback
    ) {

        StaffFacade staffFacade = lookupStaffFacade();

        List<Staff> staffList = staffFacade.filterList(username, firstname, lastname, userId, groupId, offset, max);

        JsonArrayBuilder builder = Json.createArrayBuilder();
        for (Staff staff : staffList) {
            builder.add(Json.createObjectBuilder().add("id", staff.getStaffId())
                    .add("firstname", ObjectUtil.coalesce(staff.getFirstname(), ""))
                    .add("lastname", ObjectUtil.coalesce(staff.getLastname(), ""))
                    .add("username", ObjectUtil.coalesce(staff.getUsername(), "")));
        }

        return Response.ok(callback + "(" + builder.build().toString() + ");").build();
    }

    @GET
    @Path("{id}")
    @Produces("application/javascript")
    public Response getUserJsonp(
            @PathParam("id") final BigInteger id,
            @QueryParam("callback") @DefaultValue("callback") final String callback
    ) {
        StaffFacade staffFacade = lookupStaffFacade();

        Staff staff = staffFacade.find(id);

        if (staff == null) {
            throw new JsonpWebApplicationException(Response.Status.NOT_FOUND, "User not found", callback);
        }

        JsonObjectBuilder builder = Json.createObjectBuilder();

        builder.add("id", staff.getStaffId())
                .add("firstname", ObjectUtil.coalesce(staff.getFirstname(), ""))
                .add("lastname", ObjectUtil.coalesce(staff.getLastname(), ""))
                .add("username", ObjectUtil.coalesce(staff.getUsername(), ""));

        return Response.ok(callback + "(" + builder.build().toString() + ");").build();
    }
}
