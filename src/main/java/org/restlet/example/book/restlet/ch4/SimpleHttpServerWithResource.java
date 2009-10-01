package org.restlet.example.book.restlet.ch4;
import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.Router;
import org.restlet.data.MediaType;
import org.restlet.data.Protocol;
import org.restlet.data.Request;
import org.restlet.data.Response;

public class SimpleHttpServerWithResource {
    public static void main(String[] args) {

        Application application = new Application() {

            @Override
            public synchronized Restlet createRoot() {
                // Tiens le router r�cup�re le contexte de l'application
                Router router = new Router(getContext());
                
                // Creates a Restlet whose response to each request is "Hello,
                // world".
                Restlet restlet = new Restlet() {
                    @Override
                    public void handle(Request request, Response response) {
                        response
                                .setEntity("hello, world", MediaType.TEXT_PLAIN);
                    }
                };

                return router;
            }
        };

        Component component = new Component();
        component.getServers().add(Protocol.HTTP);
        component.getDefaultHost().attach(application);
        try {
            component.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
