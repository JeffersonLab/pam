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
import org.jlab.pam.business.session.WorkgroupFacade;
import org.jlab.pam.persistence.entity.Workgroup;

/**
 *
 * @author ryans
 */
@Path("groups")
public class Groups {

    private WorkgroupFacade lookupWorkgroupFacade() {
        try {
            InitialContext ic = new InitialContext();
            return (WorkgroupFacade) ic.lookup("java:global/staff/WorkgroupFacade");
        } catch (NamingException e) {
            throw new RuntimeException("Unable to obtain EJB", e);
        }
    }

    @GET
    @Produces("application/json")
    public Response getGroupsJson(
            @QueryParam("name") final String name,
            @QueryParam("group-id") final BigInteger groupId,
            @QueryParam("user-id") final BigInteger userId,
            @QueryParam("offset") @DefaultValue("0") final int offset,
            @QueryParam("max") @DefaultValue("" + Integer.MAX_VALUE) final int max
    ) {
        StreamingOutput stream = new StreamingOutput() {
            @Override
            public void write(OutputStream out) {
                try (JsonGenerator gen = Json.createGenerator(out)) {
                    WorkgroupFacade workgroupFacade = lookupWorkgroupFacade();

                    List<Workgroup> groupList = workgroupFacade.filterList(name, groupId, userId, offset, max);

                    gen.writeStartArray();
                    for (Workgroup group : groupList) {
                        gen.writeStartObject()
                                .write("id", group.getWorkgroupId())
                                .write("name", group.getName())
                                .write("description", group.getDescription()).writeEnd();
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
    public Response getGroupJson(@PathParam("id") final BigInteger id) {
        StreamingOutput stream = new StreamingOutput() {
            @Override
            public void write(OutputStream out) {
                try (JsonGenerator gen = Json.createGenerator(out)) {
                    WorkgroupFacade groupFacade = lookupWorkgroupFacade();

                    Workgroup group = groupFacade.find(id);

                    if (group == null) {
                        throw new JsonWebApplicationException(Response.Status.NOT_FOUND, "Group not found");
                    }

                    gen.writeStartObject()
                            .write("id", group.getWorkgroupId())
                            .write("name", group.getName())
                            .write("description", group.getDescription()).writeEnd();
                }
            }
        };
        return Response.ok(stream).build();
    }

    @GET
    @Produces("application/javascript")
    public Response getGroupsJsonp(
            @QueryParam("name") final String name,
            @QueryParam("group-id") final BigInteger groupId,
            @QueryParam("user-id") final BigInteger userId,
            @QueryParam("offset") @DefaultValue("0") final int offset,
            @QueryParam("max") @DefaultValue("" + Integer.MAX_VALUE) final int max,
            @QueryParam("callback") @DefaultValue("callback") final String callback
    ) {

        WorkgroupFacade workgroupFacade = lookupWorkgroupFacade();

        List<Workgroup> groupList = workgroupFacade.filterList(name, groupId, userId, offset, max);

        JsonArrayBuilder builder = Json.createArrayBuilder();
        for (Workgroup group : groupList) {
            builder.add(Json.createObjectBuilder()
                    .add("id", group.getWorkgroupId())
                    .add("name", group.getName())
                    .add("description", group.getDescription()));
        }

        return Response.ok(callback + "(" + builder.build().toString() + ");").build();
    }

    @GET
    @Path("{id}")
    @Produces("application/javascript")
    public Response getGroupJsonp(
            @PathParam("id") final BigInteger id,
            @QueryParam("callback") @DefaultValue("callback") final String callback
    ) {
        WorkgroupFacade groupFacade = lookupWorkgroupFacade();

        Workgroup group = groupFacade.find(id);

        if (group == null) {
            throw new JsonpWebApplicationException(Response.Status.NOT_FOUND, "Group not found", callback);
        }

        JsonObjectBuilder builder = Json.createObjectBuilder();

        builder.add("id", group.getWorkgroupId())
                .add("name", group.getName())
                .add("description", group.getDescription());

        return Response.ok(callback + "(" + builder.build().toString() + ");").build();
    }
}
