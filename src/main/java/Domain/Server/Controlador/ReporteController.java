package Domain.Server.Controlador;

import Domain.Colaborador.Colaborador;
import Domain.Colaborador.TipoDeColaboracion.DistribuirVianda;
import Domain.Colaborador.TipoDeColaboracion.DonarVianda;
import Domain.Colaborador.TipoDeColaboracion.TipoDeColaboracion;
import Domain.Heladera.Heladera;
import Domain.Incidentes.FallaTecnica;
import Domain.Repositorios.*;
import Domain.Server.TemplateRender;
import io.javalin.http.Context;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReporteController {


    public void pantallaPrincipal(Context ctx) {
        Map<String, Object> model = new HashMap<>();
        model.put("esAdmin",ctx.sessionAttribute("esAdmin"));
        String eleccion = ctx.queryParam("eleccion");
        if (eleccion == null) {

            TemplateRender.render(ctx, "/Reporte.html.hbs", model);
        } else {
            switch (Integer.valueOf(eleccion)) {
                case 0: //Fallas por heladera
                    Map<Heladera, Integer> reporteFallasPorHeladera = new HashMap<>();
                    List<Heladera> heladeras = RepoHeladera.getInstance().obtenerTodos().stream().toList();

                    for (Heladera h : heladeras) {
                        int cantidad = 0;
                        List<FallaTecnica>fallas=RepoFallaTecnica.getInstance().obtenerTodos().stream().toList();
                        if(fallas.size()==0){
                            System.out.println("===============No hay fallas en esta heladera==============");
                            cantidad=0;
                        }else{
                            cantidad += fallas.stream().filter(f -> f.getHeladera().getId() == h.getId()).toList().size();

                        }
                        reporteFallasPorHeladera.put(h, cantidad);
                    }
                    // Obtener fallas del repositorio
                    model.put("heladeras", reporteFallasPorHeladera);
                    break;
                case 1: //Viandas retiradas y colocadas
                    Map<Heladera, Integer> reporteEntradaSalida = new HashMap<>();
                    List<Heladera> heladeras2 = RepoHeladera.getInstance().obtenerTodos().stream().toList();
                    List<TipoDeColaboracion> colaboraciones = RepoColaboraciones.getInstance().obtenerTodos();
                    List<DonarVianda> donaciones = new ArrayList<>();

                    for (TipoDeColaboracion t : colaboraciones) {
                        if (t instanceof DonarVianda) {
                            donaciones.add((DonarVianda) t);
                        }
                    }
                    int cantidad = 0;
                    for (Heladera h : heladeras2) {

                        cantidad += donaciones.stream()
                            .filter(d -> d.getVianda().getHeladera() != null) // Verifica que la heladera no sea nula
                            .filter(d -> d.getVianda().getHeladera().getId().equals(h.getId())) // Coincide con la heladera actual
                            .toList().size();

                        // Filtrar movimientos de tarjeta donde la heladera no sea nula y coincida con la heladera actual
                        cantidad += RepoMovimientoTarjeta.getInstance().obtenerTodos().stream()
                            .filter(m -> m.heladera != null) // Verifica que la heladera no sea nula
                            .filter(m -> m.heladera.getId().equals(h.getId())) // Coincide con la heladera actual
                            .toList().size();

                        reporteEntradaSalida.put(h, cantidad);
                        cantidad = 0;
                    }

                    model.put("heladeras", reporteEntradaSalida);
                    break;
                case 2: //Viandas por colaborador
                    Map<Colaborador, Integer> reporteViandasPorColaborador = new HashMap<>();

                    List<Colaborador> colaboradores = RepoColaboradores.getInstance().obtenerTodos();
                    List<TipoDeColaboracion> colaboraciones2 = RepoColaboraciones.getInstance().obtenerTodos();
                    List<DonarVianda> donaciones2 = new ArrayList<>();

                    for (TipoDeColaboracion t : colaboraciones2) {
                        if (t instanceof DonarVianda) {
                            donaciones2.add((DonarVianda) t);
                        }
                    }
                    for (Colaborador c : colaboradores) {
                        int cantidad3 = 0;

                        cantidad3 += donaciones2.stream().filter(d -> d.getVianda().getHeladera() != null)
                            .filter(d -> d.getVianda().getColaborador().getId() == c.getId()).toList().size();
                        reporteViandasPorColaborador.put(c, cantidad3);
                    }
                    model.put("colaboradores", reporteViandasPorColaborador);
                    break;
                case 3: //COlaboradores puntaje
                    Map<Colaborador, Double> reportePuntajeColaborador = new HashMap<>();
                    List<Colaborador> colaboradores3 = RepoColaboradores.getInstance().obtenerTodos();
                    for (Colaborador c : colaboradores3) {
                        double cantidad4 = 0;
                        cantidad4 = c.calcularPuntajeColaboraciones(LocalDate.now().minusDays(7), LocalDate.now());
                        reportePuntajeColaborador.put(c, cantidad4);
                    }
                    model.put("colaboradores", reportePuntajeColaborador);
                    break;
                default:
                    break;
            }


            TemplateRender.render(ctx, "/reporte.html.hbs", model);
        }

    }
    public void descargarReporte (Context ctx){
        Map<String, Object> model = new HashMap<>();
        model.put("esAdmin",ctx.sessionAttribute("esAdmin"));

        TemplateRender.render(ctx, "/Reporte.html.hbs", model);
    }
}