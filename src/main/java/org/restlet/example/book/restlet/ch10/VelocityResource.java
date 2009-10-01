package org.restlet.example.book.restlet.ch10;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.ext.velocity.TemplateRepresentation;
import org.restlet.resource.Representation;
import org.restlet.resource.Resource;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;

public class VelocityResource extends Resource {

    // List of items
    private List<String> items;

    public VelocityResource(Context context, Request request, Response response) {
        super(context, request, response);
        // This resource is able to generate one kind of representations, then
        // turn off content negotiation.
        this.setNegotiateContent(false);
        getVariants().add(new Variant(MediaType.TEXT_PLAIN));

        // Collect data
        this.items = new ArrayList<String>();
        this.items.add("item 1");
        this.items.add("item 2");
        this.items.add("item 3");
    }

    @Override
    public Representation represent(Variant variant) throws ResourceException {
        TemplateRepresentation representation = null;

        try {
            Map<String, Object> map = new TreeMap<String, Object>();
            map.put("items", items);
            representation = new TemplateRepresentation("items.vtl", map,
                    MediaType.TEXT_PLAIN);
            representation
                    .getEngine()
                    .addProperty(
                            "file.resource.loader.path",
                            "D:\\alaska\\forge\\build\\swc\\nre\\trunk\\books\\apress\\manuscript\\sample\\");
        } catch (Exception e) {
            getResponse()
                    .setStatus(new Status(Status.SERVER_ERROR_INTERNAL, e));
        }

        return representation;
    }
}
