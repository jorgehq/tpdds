package Domain.Server.Controlador;

import Domain.Colaborador.Colaborador;
import Domain.Heladera.Heladera;
import Domain.Incidentes.FallaTecnica;
import Domain.Notificaciones.NotificacionIncidente;
import Domain.Notificaciones.Sugerencias.Sugerencia;
import Domain.Notificaciones.Sugerencias.SugerenciaAlAzar;
import Domain.Repositorios.RepoColaboradores;
import Domain.Repositorios.RepoFallaTecnica;
import Domain.Repositorios.RepoHeladera;
import Domain.Server.TemplateRender;
import io.javalin.http.Context;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FallaTecnicaControler {
    public void pantalla_reportar_falla(Context ctx){
        List<String> heladeraIds = ctx.queryParams("heladeraID");
        Map<String,Object> model=new HashMap<>();

        Heladera h= RepoHeladera.getInstance().buscarPorId(Long.parseLong(heladeraIds.get(0)));

        model.put("heladera", h);

        TemplateRender.render(ctx, "/ReportarFalla.html.hbs", model);
    }

    public void reportar_falla(Context ctx){
        String heladeraId=ctx.formParam("heladera");
        String descripcion=ctx.formParam("descripcion");
        String foto=ctx.formParam("url");


        Heladera h= RepoHeladera.getInstance().buscarPorId(Long.parseLong(heladeraId));
        h.getEstado().marcarComoInactiva();
        Colaborador c= RepoColaboradores.getInstance().buscarPorId(Long.parseLong(ctx.sessionAttribute("usuarioID")));

        FallaTecnica falla=new FallaTecnica(LocalDateTime.now(),h,descripcion,foto,c);
        RepoFallaTecnica.getInstance().guardar(falla);

        Sugerencia s= new SugerenciaAlAzar();
        s.sugerencias(h);
        h.notificarInteresados(new NotificacionIncidente(h,s,falla.getDescripcion()));
        RepoHeladera.getInstance().guardar(h);

        ctx.redirect("/heladeras");
    }
}
