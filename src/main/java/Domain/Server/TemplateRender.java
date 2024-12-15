package Domain.Server;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import io.javalin.http.Context;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

public class TemplateRender {
    private static final Handlebars handlebars = new Handlebars();
    static {

        handlebars.registerHelper("includePartial", (context, options) -> {
            String partialName = options.param(0);
            try {
                Template partialTemplate = handlebars.compile("partials/" + partialName);
                StringWriter writer = new StringWriter();
                partialTemplate.apply(context, writer);
                return writer.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return "Error rendering partial.";
            }
        });
    }

    // This method renders a template and sends the result to the Javalin context
    public static void render(Context ctx, String templateName, Map<String, Object> model) {
        try {
            Template template = handlebars.compile(templateName.replace(".hbs", ""));
            StringWriter writer = new StringWriter();
            template.apply(model, writer);

            ctx.result(writer.toString()).contentType("text/html");
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).result("Error rendering template.");
        }
    }
}
