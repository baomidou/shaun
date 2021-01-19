package shaun.test.support;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.boot.web.server.WebServer;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

/**
 * @author miemie
 * @since 2020-12-04
 */
@Slf4j
public class StarterWebInfo implements ApplicationListener<WebServerInitializedEvent> {

    protected int port;

    @Override
    public void onApplicationEvent(@NonNull WebServerInitializedEvent event) {
        WebServer server = event.getWebServer();
        port = server.getPort();
    }

    protected void logUrl(String domain, String path) {
        if (!StringUtils.hasText(domain)) {
            domain = "localhost";
        }
        if (!StringUtils.hasText(path)) {
            path = "";
        }
        log.info("http://{}:{}{}", domain, port, path);
    }
}
