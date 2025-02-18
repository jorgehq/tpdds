package Domain.Server.Controlador;

import Domain.Colaborador.Colaborador;
import Domain.Colaborador.TipoDeColaboracion.DistribuirVianda;
import Domain.Colaborador.TipoDeColaboracion.DonarVianda;
import Domain.Colaborador.TipoDeColaboracion.TipoDeColaboracion;
import Domain.Heladera.Heladera;
import Domain.Incidentes.FallaTecnica;
import Domain.Repositorios.*;
import Domain.Server.Enums.Tiposreporte;
import Domain.Server.TemplateRender;
import Domain.Servicios.ServicioReporte;
import io.javalin.http.Context;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReporteController {


    public void pantallaPrincipal(Context ctx) {
        Map<String, Object> model = ctx.attribute("sharedData");;
        String eleccion = ctx.queryParam("eleccion");
        if (eleccion == null) {

            TemplateRender.render(ctx, "/Reporte.html.hbs", model);
        } else {
            switch (Tiposreporte.valueOf(eleccion)) {
                case FALLAS: //Fallas por heladera
                    model = ServicioReporte.generarReporteFallas(model);
                    break;
                case VIANDASRETIRADAS: //Viandas retiradas y colocadas
                    model = ServicioReporte.generarReporteViandasRetiradas(model);
                    break;
                case VIANDASPORCOLABORADOR: //Viandas por colaborador
                    model = ServicioReporte.generarReporteViandasPorColaborador(model);
                    break;
                case PUNTAJECOLABORADOR: //COlaboradores puntaje
                    model = ServicioReporte.generarReportePuntajeColaborador(model);
                    break;
                default:
                    break;
            }


            TemplateRender.render(ctx, "/Reporte.html.hbs", model);
        }

    }
    public void descargarReporte (Context ctx){
        Map<String, Object> model = new HashMap<>();
        model.put("esAdmin",ctx.sessionAttribute("esAdmin"));

        TemplateRender.render(ctx, "/Reporte.html.hbs", model);
    }
}