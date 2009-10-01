/*
 * Copyright 2005-2008 Noelios Consulting.
 * 
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the "License"). You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the license at
 * http://www.opensource.org/licenses/cddl1.txt See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and
 * include the License file at http://www.opensource.org/licenses/cddl1.txt If
 * applicable, add the following below this CDDL HEADER, with the fields
 * enclosed by brackets "[]" replaced with your own identifying information:
 * Portions Copyright [yyyy] [name of copyright owner]
 */

package org.restlet.example.book.restlet.ch8;

import java.io.File;

import org.restlet.Component;
import org.restlet.Context;
import org.restlet.Directory;
import org.restlet.Restlet;
import org.restlet.Router;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.LocalReference;
import org.restlet.data.Protocol;
import org.restlet.example.book.restlet.ch8.data.db4o.Db4oFacade;
import org.restlet.example.book.restlet.ch8.objects.ObjectsFacade;
import org.restlet.example.book.restlet.ch8.resources.ContactResource;
import org.restlet.example.book.restlet.ch8.resources.ContactsResource;
import org.restlet.example.book.restlet.ch8.resources.FeedResource;
import org.restlet.example.book.restlet.ch8.resources.FeedsResource;
import org.restlet.example.book.restlet.ch8.resources.MailResource;
import org.restlet.example.book.restlet.ch8.resources.MailRootResource;
import org.restlet.example.book.restlet.ch8.resources.MailboxResource;
import org.restlet.example.book.restlet.ch8.resources.MailboxesResource;
import org.restlet.example.book.restlet.ch8.resources.MailsResource;
import org.restlet.example.book.restlet.ch8.resources.UserResource;
import org.restlet.example.book.restlet.ch8.resources.UsersResource;

/**
 * The main Web application.
 */
public class Application extends org.restlet.Application {

    public static void main(String... args) throws Exception {
        // Create a component with an HTTP server connector
        Component component = new Component();
        component.getServers().add(Protocol.HTTP, 8585);
        component.getClients().add(Protocol.FILE);
        component.getClients().add(Protocol.HTTP);
        // Attach the application to the default host and start it
        component.getDefaultHost().attach("/rmep",
                new Application(component.getContext()));
        component.start();
    }

    /** Facade object for all access to data. */
    private ObjectsFacade dataFacade;

    /** Freemarker configuration object. */
    private freemarker.template.Configuration fmc;

    public Application(Context context) {
        super(context);
        // List of protocols required by the application.
        this.getConnectorService().getClientProtocols().add(Protocol.FILE);
        this.getConnectorService().getClientProtocols().add(Protocol.HTTP);

        /** Create and chain the Objects and Data facades. */
        dataFacade = new ObjectsFacade(new Db4oFacade(System
                .getProperty("user.home")
                + File.separator + "rmep.dbo"));
        // Check that at least one administrator exists in the database.
        this.dataFacade.initAdmin();

        try {
            File templateDir = new File("src/main/java/org/restlet/example/book/restlet/ch8/web/tmpl");
            fmc = new freemarker.template.Configuration();
            fmc.setDirectoryForTemplateLoading(templateDir);
        } catch (Exception e) {
            getLogger().severe("Erreur config FreeMarker");
            e.printStackTrace();
        }
    }

    @Override
    public Restlet createRoot() {
        Router router = new Router(getContext());

        RmepGuard guard = new RmepGuard(getContext(),
                ChallengeScheme.HTTP_BASIC, "rmep", dataFacade);

        // Secure the root of the application and only this resource. It allows
        // anonymous access to all other resources and authentication at the top
        // of our application's hierarchy of URIs. It also makes the assumption
        // that common Internet browsers preemptively authenticate future
        // requests made to "sub" URIs.
        guard.setNext(MailRootResource.class);

        // Add a route for the MailRoot resource
        router.attachDefault(guard);

        Directory imgDirectory = new Directory(
                getContext(),
                LocalReference
                        .createFileReference("src/main/java/org/restlet/example/book/restlet/ch8/web/images"));
        // Add a route for the image resources
        router.attach("/images", imgDirectory);

        Directory cssDirectory = new Directory(
                getContext(),
                LocalReference
                        .createFileReference("src/main/java/org/restlet/example/book/restlet/ch8/web/stylesheets"));
        // Add a route for the CSS resources
        router.attach("/stylesheets", cssDirectory);

        // Add a route for a Users resource
        router.attach("/users", UsersResource.class);

        // Add a route for a User resource
        router.attach("/users/{userId}", UserResource.class);

        // Add a route for a Mailboxes resource
        router.attach("/mailboxes", MailboxesResource.class);

        // Add a router for access to mailbox
        Router mailboxRouter = new Router(getContext());

        // Add a route for a Mailbox resource
        mailboxRouter.attachDefault(MailboxResource.class);

        // Add a route for a Contacts resource
        mailboxRouter.attach("/contacts", ContactsResource.class);

        // Add a route for a Contact resource
        mailboxRouter.attach("/contacts/{contactId}", ContactResource.class);

        // Add a route for a Mails resource
        mailboxRouter.attach("/mails", MailsResource.class);

        // Add a route for a Mail resource
        mailboxRouter.attach("/mails/{mailId}", MailResource.class);

        // Add a route for a Feeds resource
        mailboxRouter.attach("/feeds", FeedsResource.class);

        // Add a route for a Feed resource
        mailboxRouter.attach("/feeds/{feedId}", FeedResource.class);

        // Add a route for a Mailbox resource
        router.attach("/mailboxes/{mailboxId}", mailboxRouter);

        return router;
    }

    /**
     * Returns the data facade.
     * 
     * @return the data facade.
     */
    public ObjectsFacade getObjectsFacade() {
        return this.dataFacade;
    }

    /**
     * Returns the freemarker configuration object.
     * 
     * @return the freemarker configuration object.
     */
    public freemarker.template.Configuration getFmc() {
        return this.fmc;
    }

}
