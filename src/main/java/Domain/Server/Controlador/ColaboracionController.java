package Domain.Server.Controlador;

import Domain.Colaborador.Colaborador;
import Domain.Colaborador.PersonaJuridica;
import Domain.Colaborador.TipoDeColaboracion.*;
import Domain.Colaborador.Transferencia.FrecuenciaDeDonacion;
import Domain.Heladera.Heladera;
import Domain.Heladera.Vianda;
import Domain.Persona.PersonaVulnerable;
import Domain.Repositorios.*;
import Domain.Server.TemplateRender;
import Domain.Solicitudes.SolicitudColaboracion;
import Domain.Tarjeta.Tarjeta;
import Domain.Ubicacion.Direccion;
import Domain.Ubicacion.Localidad;
import Domain.Ubicacion.Provincia;
import Domain.Usuarios.Usuario;
import io.javalin.http.Context;
import net.bytebuddy.asm.Advice;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public class ColaboracionController {
    public void principal(Context ctx) {
        String usuarioID = ctx.sessionAttribute("usuarioID");
        Map<String, Object> model = new HashMap<>();
        model.put("esAdmin", ctx.sessionAttribute("esAdmin"));
        String dato = ctx.queryParam("eleccion");
        Set<Heladera> filtradas;
        if (usuarioID == null) {
            ctx.redirect("/");
        } else {
            String filtro = ctx.queryParam("filtro");

            if (filtro == null) {

                TemplateRender.render(ctx, "/colaboraciones.html.hbs", model);
            } else {
                int filtroInt = Integer.parseInt(filtro);
                try {
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

                }catch (NumberFormatException e) {
                    model.put("error", "El filtro debe ser un número válido.");
                } catch (IllegalArgumentException e) {
                    model.put("error", "El dato ingresado no es válido.");
                } catch (Exception e) {
                    model.put("error", "Ocurrió un error inesperado. Inténtalo nuevamente.");
                }
                TemplateRender.render(ctx, "/colaboraciones.html.hbs", model);

            }

        }
    }
    public void eleccionColaboracion(Context ctx){
        String eleccion = ctx.formParam("eleccion");
        if(eleccion==null){
            TemplateRender.render(ctx, "/colaboraciones.html.hbs", Map.of());
        }else{
            switch(Integer.valueOf(eleccion)){
                case 0: //Donacion de dinero
                    ctx.redirect("/colaboracion/dinero");
                    break;
                case 1: //DonarVianda
                    List<String> heladeraIds = ctx.formParams("heladeraId"); // Obtener lista de IDs de heladeras
                    if(heladeraIds.size()==0){
                        ctx.redirect("/colaboracion");
                    }else {
                        Heladera heladera = RepoHeladera.getInstance().buscarPorId(Long.parseLong(heladeraIds.get(0))); // Consultar el repositorio

                        if (heladera != null) {

                            ctx.redirect("/colaboracion/vianda?heladeraID=" + heladera.getId());

                        } else {
                            ctx.status(404).result("Heladera no encontrada");
                        }
                    }
                    break;
                case 2: //Distribuir Vianda
                   
                    List<String> heladeraIds2 = ctx.formParams("heladeraId"); 

                    if(heladeraIds2.size()<2){
                        ctx.redirect("/colaboracion");
                    }else {
                        Heladera heladera1 = RepoHeladera.getInstance().buscarPorId(Long.parseLong(heladeraIds2.get(0))); // Consultar el repositorio
                        Heladera heladera2 = RepoHeladera.getInstance().buscarPorId(Long.parseLong(heladeraIds2.get(1)));


                        ctx.redirect( "/colaboracion/distribucion?heladeraId=" + heladera1.getId() + "&heladeraId2=" + heladera2.getId());

                    }
                    break;
                case 3 : //Donar Heladera
                    Colaborador colaborador= RepoColaboradores.getInstance().buscarPorId(Long.parseLong(ctx.sessionAttribute("usuarioID")));
                    if(colaborador instanceof  PersonaJuridica){
                        ctx.redirect("/colaboracion/heladera");
                    }else {
                        ctx.redirect("/colaboracion");
                    }

                    break;

            }
        }

    }
    public void pantalla_donar_dinero(Context ctx){
        Map<String, Object> model = new HashMap<>();
        model.put("frecuencias",FrecuenciaDeDonacion.values());
        model.put("esAdmin",ctx.sessionAttribute("esAdmin"));
        TemplateRender.render(ctx, "/donarDinero.html.hbs", model);
    }
    public void donar_dinero(Context ctx){

        String monto = ctx.formParam("monto");
        String tarjeta = ctx.formParam("tarjeta");
        String frecuencia = ctx.formParam("frecuencia");
        if(monto==null || tarjeta==null || frecuencia ==null){
            Map<String, Object> model = new HashMap<>();
            model.put("frecuencias",FrecuenciaDeDonacion.values());
            model.put("esAdmin",ctx.sessionAttribute("esAdmin"));
            TemplateRender.render(ctx, "/donarDinero.html.hbs", model);
        }else{

            TipoDeColaboracion t=new DonarDinero(Integer.valueOf(monto), LocalDate.now(), FrecuenciaDeDonacion.valueOf(frecuencia));

            Colaborador colaborador= RepoColaboradores.getInstance().buscarPorId(Long.parseLong(ctx.sessionAttribute("usuarioID")));

            RepoColaboraciones.getInstance().guardar(t);
            colaborador.realizarColaboracion(t);
            RepoColaboradores.getInstance().guardar(colaborador);

            ctx.redirect("/colaboracion");
        }


    }

    public void pantalla_donar_vianda(Context ctx){
        String h = ctx.queryParam("heladeraID");

        Map<String,Object> model=new HashMap<>();
        model.put("esAdmin",ctx.sessionAttribute("esAdmin"));
        Heladera heladera1 = RepoHeladera.getInstance().buscarPorId(Long.parseLong(h));

        model.put("heladera", heladera1);
        model.put("idHeladera1", heladera1.getId());
        model.put("direccionHeladera1", heladera1.getDireccion().getDireccion());

        TemplateRender.render(ctx, "/donarVianda.html.hbs", model);
    }

    public void donar_vianda(Context ctx){
        String fechaCaducidad = ctx.formParam("fecha");
        String peso = ctx.formParam("peso");
        String calorias = ctx.formParam("calorias");
        String heladeraid = ctx.formParam("heladera");

        Colaborador colaborador=RepoColaboradores.getInstance().buscarPorId(Long.parseLong(ctx.sessionAttribute("usuarioID")));
        Heladera heladera= RepoHeladera.getInstance().buscarPorId(Long.parseLong(heladeraid));

        TipoDeColaboracion t=new DonarVianda();

        Map<String,String> datos=new HashMap<>();


        datos.put("fechaCaducidad", fechaCaducidad);
        datos.put("peso", peso);
        datos.put("calorias", calorias);

        colaborador.getTarjeta().agregarSolicitud(t,heladera,datos,1);


        ctx.redirect("/colaboracion");

    }
    public void pantalla_distribuir_viandas(Context ctx){

        String h = ctx.queryParam("heladeraId");
        String h2 = ctx.queryParam("heladeraId2");

        Map<String,Object> model=new HashMap<>();
        model.put("esAdmin",ctx.sessionAttribute("esAdmin"));
        Heladera heladera1 = RepoHeladera.getInstance().buscarPorId(Long.parseLong(h)); // Consultar el repositorio
        Heladera heladera2 = RepoHeladera.getInstance().buscarPorId(Long.parseLong(h2));

        model.put("heladera1", heladera1);
        model.put("heladera2", heladera2);


        TemplateRender.render(ctx, "/distribuirVianda.html.hbs", model);
    }

    public void distribuir_viandas(Context ctx){
        String motivo = ctx.formParam("motivo");
        String cantidad = ctx.formParam("cantidad");
        String heladeraid = ctx.formParam("origen");
        String heladeraid2 = ctx.formParam("destino");

        if(heladeraid.equals(heladeraid2)){
            ctx.redirect("/colaboracion");
            return;
        }


        Colaborador colaborador=RepoColaboradores.getInstance().buscarPorId(Long.parseLong(ctx.sessionAttribute("usuarioID")));
        Heladera heladera= RepoHeladera.getInstance().buscarPorId(Long.parseLong(heladeraid));

        if(heladera.getEstado().getHeladeraAveriada()){
            TipoDeColaboracion t=new DistribuirVianda();


            Map<String,String> datos=new HashMap<>();
            datos.put("motivo", motivo);
            datos.put("cantidad", cantidad);
            datos.put("origen", heladeraid);
            datos.put("destino", heladeraid2);

            RepoColaboraciones.getInstance().guardar(t);

            colaborador.getColaboraciones().add(t);

            RepoColaboradores.getInstance().guardar(colaborador);

            colaborador.getTarjeta().agregarSolicitud(t,heladera,datos,Integer.valueOf(cantidad));
        }


        ctx.redirect("/colaboracion");

    }
    public void pantalla_donar_heladera(Context ctx){

        Map<String,Object> model=new HashMap<>();
        model.put("esAdmin",ctx.sessionAttribute("esAdmin"));
        model.put("provincias", Provincia.values());
        model.put("localidades", Localidad.values());
        TemplateRender.render(ctx, "/donarHeladera.html.hbs", model);
    }

    public void donar_heladera(Context ctx){

        String nombre = ctx.formParam("nombre");
        String capacidad = ctx.formParam("capacidad");
        String latitud = ctx.formParam("latitud");
        String longitud = ctx.formParam("longitud");
        String provincia = ctx.formParam("provincia");
        String localidad = ctx.formParam("localidad");
        String direccion = ctx.formParam("direccion");
        String tminimo = ctx.formParam("tminimo");
        String tmaximo = ctx.formParam("tmaximo");


        Colaborador colaborador=RepoColaboradores.getInstance().buscarPorId(Long.parseLong(ctx.sessionAttribute("usuarioID")));

        Heladera heladera=new Heladera(nombre,Integer.valueOf(capacidad),latitud,longitud,
            new Direccion(Provincia.valueOf(provincia), Localidad.valueOf(localidad), direccion)
            ,LocalDate.now(), BigDecimal.valueOf(Integer.valueOf(tmaximo)),BigDecimal.valueOf(Integer.valueOf(tminimo)));


        TipoDeColaboracion t=new HacerseCargoDeHeladera(new Heladera(nombre,Integer.valueOf(capacidad),latitud,longitud,
                new Direccion(Provincia.valueOf(provincia), Localidad.valueOf(localidad), direccion)
                ,LocalDate.now(), BigDecimal.valueOf(Integer.valueOf(tmaximo)),BigDecimal.valueOf(Integer.valueOf(tminimo))));


        PersonaJuridica j=(PersonaJuridica) colaborador;

        j.agregarHeladera(heladera);

        RepoHeladera.getInstance().guardar(heladera);
        RepoColaboraciones.getInstance().guardar(t);
        colaborador.realizarColaboracion(t);
        RepoColaboradores.getInstance().guardar(colaborador);

        ctx.redirect("/colaboracion");
    }

    public void pantalla_persona_vulnerable(Context ctx){
        Map<String,Object> model=new HashMap<>();
        model.put("esAdmin",ctx.sessionAttribute("esAdmin"));
        model.put("provincias", Provincia.values());
        model.put("localidades", Localidad.values());
        TemplateRender.render(ctx, "/RegistrarPersonaVulnerable.html.hbs", model);
    }

    public void registro_persona_vulnerable(Context ctx){
        String nombre = ctx.formParam("nombre");
        String fechaNacimiento = ctx.formParam("fecha");
        String provincia = ctx.formParam("provincia");
        String localidad = ctx.formParam("localidad");
        String domicilio = ctx.formParam("direccion");
        String dni = ctx.formParam("dni");
        String cantidadHijos = ctx.formParam("cantidadHijos");
        String tarjeta = ctx.formParam("tarjeta");


        Colaborador co = RepoColaboradores.getInstance().buscarPorId(Long.parseLong(ctx.sessionAttribute("usuarioID")));

        Tarjeta tj= RepoTarjetas.getInstance().buscarPorNumeroTarjeta(tarjeta);

        PersonaVulnerable p=new PersonaVulnerable(nombre, LocalDate.parse(fechaNacimiento),LocalDate.now(),
                Optional.of(new Direccion(Provincia.valueOf(provincia), Localidad.valueOf(localidad),domicilio)),
                Optional.of(dni),Integer.valueOf(cantidadHijos));

        p.asignarTarjeta(tj);

        RepositorioPersonasVulnerables.getInstance().guardar(p);

        RegistrarUnaPersonaVulnerable r=new RegistrarUnaPersonaVulnerable(p,tj);

        RepoColaboraciones.getInstance().guardar(r);

        co.realizarColaboracion(r);

        RepoColaboradores.getInstance().guardar(co);


        ctx.redirect("/colaboracion");
    }
}
