package Domain.Server.Controlador;

import Domain.Colaborador.PersonaHumana;
import Domain.Colaborador.PersonaJuridica;
import Domain.Heladera.Heladera;
import Domain.Repositorios.RepoUsuario;
import Domain.Server.TemplateRender;
import Domain.Usuarios.Usuario;
import io.javalin.http.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CuentaController {
    public void pantalla_cuenta_principal(Context ctx) {
        String sesion = ctx.sessionAttribute("usuarioID");
        Map<String, Object> model = new HashMap<>();
        List<Heladera> filtradas=new ArrayList<>();
        if(sesion==null){
            ctx.redirect("/");
        }
        Usuario u = RepoUsuario.getInstance().buscarPorIdColaborador(Long.parseLong(sesion));

        if(u.getAsignado() instanceof  PersonaHumana){
            PersonaHumana ph=(PersonaHumana) u.getAsignado();

            model.put("nombre", u.getNombre());
            model.put("email",ph.getMediosDeContacto().get(0).getContacto());
            model.put("celular", ph.getMediosDeContacto().get(1).getContacto());
            model.put("nombreUsuario", u.getNombreUsuario());
            model.put("contrasenia", u.getContrasenia());
            model.put("direccion", ph.getDireccion().getDireccion());
            model.put("dni", ph.getDocumento());

            TemplateRender.render(ctx, "/Cuenta.html.hbs", model);
        }else{
            PersonaJuridica ph=(PersonaJuridica) u.getAsignado();

            model.put("nombre", u.getNombre());
            model.put("razonsocial",ph.getRazonSocial());
            model.put("email",ph.getMediosDeContacto().get(0).getContacto());
            model.put("celular", ph.getMediosDeContacto().get(1).getContacto());
            model.put("nombreUsuario", u.getNombreUsuario());
            model.put("contrasenia", u.getContrasenia());
            model.put("direccion", ph.getDireccion().getDireccion());
            model.put("dni", ph.getDocumento());
            TemplateRender.render(ctx, "/CuentaJuridica.html.hbs", model);
        }




    }
    public void modificarCuenta(Context ctx){
        String email = ctx.formParam("correo");
        String celular = ctx.formParam("celular");
        String nombre = ctx.formParam("nombre");
        String apellido = ctx.formParam("apellido");
        String contrasenia = ctx.formParam("contrasenia");
        String contraseniaR = ctx.formParam("repetirContrasenia");
        String nombreusuario = ctx.formParam("nombreUsuario");
        String direccion = ctx.formParam("direccion");
        String dni = ctx.formParam("dni");

        String sesion = ctx.sessionAttribute("usuarioID");
        if (!contrasenia.equals(contraseniaR)) {
           ctx.redirect("/cuenta");
        }

        Usuario o = RepoUsuario.getInstance().buscarPorIdColaborador(Long.parseLong(sesion));
        System.out.println(nombre + " " + apellido + " " + email + " " + celular);

        o.setContrasenia(contrasenia);
        o.setNombreUsuario(nombreusuario);
        o.setNombre(nombre);
        o.getAsignado().getMediosDeContacto().get(0).setContacto(email);
        o.getAsignado().getDireccion().setDireccion(direccion);

        o.getAsignado().getMediosDeContacto().get(1).setContacto(celular);
        RepoUsuario.getInstance().guardar(o);

        ctx.redirect("/cuenta");
    }

    public void cerrarCuenta(Context ctx){
        ctx.sessionAttribute("usuarioID",null);
        ctx.sessionAttribute("esAdmin",null);
        ctx.redirect("/");
    }
}
