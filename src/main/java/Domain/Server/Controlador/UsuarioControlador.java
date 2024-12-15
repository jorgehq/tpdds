package Domain.Server.Controlador;

import Domain.Repositorios.RepoUsuario;
import Domain.Server.TemplateRender;
import Domain.Usuarios.Usuario;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import io.javalin.http.Context;
import io.javalin.http.staticfiles.Location;
import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import java.io.StringWriter;
import java.util.Map;


public class UsuarioControlador {



  public void login(Context ctx){
    if(ctx.formParam("usuarioID")!=null){
      ctx.redirect("/colaboracion");
    }else{
      TemplateRender.render(ctx, "login.html.hbs", Map.of());
    }

  }


  public void inicioSesion(Context ctx) {
    String username = ctx.formParam("correo");
    String password = ctx.formParam("contrasenia");

    Usuario o = RepoUsuario.getInstance().buscarPorNombre(username).get(0);

    if (o != null && o.getContrasenia().compareTo(password) == 0) {
      ctx.sessionAttribute("usuarioID", Long.toString(o.getAsignado().getId()));
      ctx.sessionAttribute("esAdmin", o.getAsignado().getEsAdmin());
      ctx.redirect("/colaboracion");
    } else {
      System.out.println("Credenciales incorrectas. Por favor, intenta de nuevo.");
      TemplateRender.render(ctx, "login.html.hbs", Map.of());
    }
  }


  public void cerrarSesion(Context ctx) {
    ctx.sessionAttribute("usuarioID", null);
    ctx.redirect("/");
  }
}
