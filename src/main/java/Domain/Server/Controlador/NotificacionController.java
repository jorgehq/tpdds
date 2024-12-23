package Domain.Server.Controlador;

import Domain.Colaborador.Colaborador;
import Domain.Heladera.Heladera;
import Domain.Notificaciones.Notificacion;
import Domain.Notificaciones.NotificacionFaltanViandas;
import Domain.Notificaciones.NotificacionIncidente;
import Domain.Repositorios.RepoColaboradores;
import Domain.Repositorios.RepoHeladera;
import Domain.Repositorios.RepoNotificaciones;
import Domain.Server.TemplateRender;
import io.javalin.http.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NotificacionController {

    public void pantallaNotificaciones(Context ctx) {
        String usuarioID = ctx.sessionAttribute("usuarioID");
        Map<String, Object> model = new HashMap<>();
        model.put("esAdmin",ctx.sessionAttribute("esAdmin"));
        List<Notificacion> filtradas ;
        if (usuarioID == null) {
            ctx.redirect("/");
        } else {
            String filtro = ctx.queryParam("filtro");
            Colaborador colaborador=RepoColaboradores.getInstance().buscarPorId(Long.parseLong(ctx.sessionAttribute("usuarioID")));

                filtradas = new ArrayList<>(colaborador.getNotificaciones());
                if(filtradas==null){
                    TemplateRender.render(ctx, "/Notificaciones.html.hbs", model);
                }else{
                    filtradas.sort((n1, n2) -> n2.getFecha().compareTo(n1.getFecha()));
                    List<Integer> cantidadActual = new ArrayList<>();
                    List<Integer> cantidadFaltante = new ArrayList<>();
                    List<Integer> cantidadDonada = new ArrayList<>();
                    List<String> tiposNotificacion=new ArrayList<>();
                    List<String> enlaces=new ArrayList<>();

                    for (Notificacion n : filtradas) {
                        System.out.println("Notificación: " + n.getId() + ", Heladera: " + n.getHeladera());
                    }

                    for (Notificacion n : filtradas) {

                        Heladera h= RepoHeladera.getInstance().buscarPorId(n.getHeladera().getId());

                        cantidadActual.add(h.cantidadViandas());

                        if (n instanceof NotificacionFaltanViandas) {
                            cantidadFaltante.add(n.getHeladera().getCapacidadDeViandas() - n.getHeladera().cantidadViandas());
                            cantidadDonada.add(n.getHeladera().cantidadSolicitudesVianda());
                            tiposNotificacion.add("FaltanViandas");
                            enlaces.add("/donar_vianda?heladeraID="+n.getHeladera().getId());

                        } else if (n instanceof NotificacionIncidente) {
                            cantidadFaltante.add(n.getHeladera().cantidadViandas());
                            cantidadDonada.add(n.getHeladera().cantidadSolicitudesDistribucion());
                            tiposNotificacion.add("Incidente");
                            enlaces.add("/distribuir_vianda?heladeraId="+n.getHeladera().getId()+"&heladeraId2="+((NotificacionIncidente) n).getSugerencia().getHeladera().getId());
                        } else {
                            tiposNotificacion.add("Otro");
                        }

                    }


                    model.put("Indices", IntStream.range(0, filtradas.size()).boxed().collect(Collectors.toList()));
                    model.put("Notificaciones", filtradas);
                    model.put("TiposNotificacion", tiposNotificacion);
                    model.put("CantidadViandas", cantidadActual);
                    model.put("Faltantes", cantidadFaltante);
                    model.put("EnCamino", cantidadDonada);
                    model.put("Enlaces",enlaces);

                    TemplateRender.render(ctx, "/Notificaciones.html.hbs", model);
                }




        }
    }
    public void aceptarNotificacion(Context ctx){
        String notidicacionId=ctx.formParam("tipo");

        Notificacion n= RepoNotificaciones.getInstance().buscarPorId(Long.parseLong(ctx.formParam(notidicacionId)));

        if(n instanceof NotificacionFaltanViandas){
            //FAltan viandas
            NotificacionFaltanViandas faltanViandas=(NotificacionFaltanViandas)n;

            ctx.redirect( "/donar_vianda?heladeraId=" + faltanViandas.getHeladera().getId());
        }else{
            NotificacionIncidente incidente=(NotificacionIncidente)n;
            ctx.redirect( "/donar_vianda?heladeraId=" + incidente.getHeladera().getId() + "&heladeraId2=" + incidente.getSugerencia().getHeladera().getId());
        }
    }
}
