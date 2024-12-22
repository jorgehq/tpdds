package Domain.Server.Controlador;

import Domain.Colaborador.Colaborador;
import Domain.Heladera.Heladera;
import Domain.Repositorios.RepoColaboradores;
import Domain.Repositorios.RepoHeladera;
import Domain.Server.TemplateRender;
import io.javalin.http.Context;

import java.util.*;

public class HeladeraController {
    public void pantallaPrincipal(Context ctx) {

        String usuarioID = ctx.sessionAttribute("usuarioID");
        Map<String, Object> model = new HashMap<>();
        model.put("esAdmin", ctx.sessionAttribute("esAdmin"));
        Set<Heladera> filtradas;
        if (usuarioID == null) {
            ctx.redirect("/");
        } else {
            String filtro = ctx.queryParam("filtro");
            String dato = ctx.queryParam("dato");
            if (filtro == null) {

                TemplateRender.render(ctx, "/heladeras.html.hbs", model);
                return;
            }
            try {
                int filtroInt = Integer.parseInt(filtro);
                switch (filtroInt) {
                    case 0:
                        filtradas = RepoHeladera.getInstance().obtenerTodos();
                        model.put("heladeras", filtradas);
                        break;
                    case 1:
                        filtradas = RepoHeladera.getInstance().filtrarPorNombre(dato);
                        model.put("heladeras", filtradas);
                        break;
                    case 2:
                        filtradas = RepoHeladera.getInstance().filtrarPorLocalidad(dato);
                        model.put("heladeras", filtradas);
                        break;
                    case 3:
                        filtradas = RepoHeladera.getInstance().filtrarPorDireccion(dato);
                        model.put("heladeras", filtradas);
                        break;
                    default:
                        model.put("error", "El filtro seleccionado no es válido.");
                        break;
                }
            } catch (NumberFormatException e) {
                model.put("error", "El filtro debe ser un número válido.");
            } catch (IllegalArgumentException e) {
                model.put("error", "El dato ingresado no es válido.");
            } catch (Exception e) {
                model.put("error", "Ocurrió un error inesperado. Inténtalo nuevamente.");
            }
            TemplateRender.render(ctx, "/heladeras.html.hbs", model);
        }
    }
    public void suscribirseOreportar(Context ctx){

        List<String> heladeras = ctx.formParams("heladeraId");
        String action = ctx.formParam("action");
        Colaborador colaborador= RepoColaboradores.getInstance().buscarPorId(Long.parseLong(ctx.sessionAttribute("usuarioID")));

        if("suscripcion".equals(action)){

            for(String id:heladeras){
                Heladera h=RepoHeladera.getInstance().buscarPorId(Long.parseLong(id));
                boolean yaSuscrito = h.getInteresados().stream()
                        .anyMatch(c -> c.getId().equals(colaborador.getId()));
                if (!yaSuscrito) {
                    h.suscribirse(colaborador);
                    RepoHeladera.getInstance().guardar(h);
                    System.out.println("========================Comenzando suscripcion =========================");
                }
            }
            ctx.redirect("/heladeras");
        }else{
            if(heladeras.size()==0){
                ctx.redirect("/heladeras");
            }else{
                Heladera heladera= RepoHeladera.getInstance().buscarPorId(Long.parseLong(heladeras.get(0)));

                ctx.redirect("/falla?heladeraID="+heladera.getId());
            }

        }

    }

}
