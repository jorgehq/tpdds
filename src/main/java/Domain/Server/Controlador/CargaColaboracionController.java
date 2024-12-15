package Domain.Server.Controlador;

import Domain.Importador.ImportadorColaboracionCsv;
import Domain.Server.TemplateRender;
import io.javalin.http.Context;

import java.util.HashMap;
import java.util.Map;

public class CargaColaboracionController {
    public void pantalla_carga_datos(Context ctx){
        Map<String, Object> model = new HashMap<>();
        model.put("esAdmin",ctx.sessionAttribute("esAdmin"));
        TemplateRender.render(ctx, "/cargarColaboraciones.html.hbs", model);
    }

    public void cargar_archivo(Context ctx){
        String archivoCSV = "migracion.csv";
        ImportadorColaboracionCsv importador = new ImportadorColaboracionCsv();

        importador.importar(archivoCSV);

        ctx.redirect("/colaboracion");
    }
}
