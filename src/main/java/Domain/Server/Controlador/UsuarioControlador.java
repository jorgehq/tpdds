package Domain.Server.Controlador;

import Domain.Exception.Errorpopups;
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
import java.util.HashMap;
import java.util.Map;


public class UsuarioControlador {



  public void login(Context ctx){
    String error = ctx.sessionAttribute("error");
    Map<String, Object> model=new HashMap<>();
    if(ctx.formParam("usuarioID")!=null){
      ctx.redirect("/colaboracion");
    }else{
      if (error != null) {
        model.put("error", error);
        ctx.sessionAttribute("error", null);
      }
      TemplateRender.render(ctx, "login.html.hbs", model);
    }

  }


  public void inicioSesion(Context ctx) {
    String usuarioID = ctx.sessionAttribute("usuarioID");
    String username = ctx.formParam("correo");
    String password = ctx.formParam("contrasenia");
    String error = ctx.sessionAttribute("error");
    Map<String, Object> model=new HashMap<>();

    Usuario o = RepoUsuario.getInstance().buscarPorNombre(username).get(0);
    if (usuarioID != null) {
      ctx.redirect("/col");
    }
    if (o != null && o.getContrasenia().compareTo(password) == 0) {

      ctx.sessionAttribute("usuarioID", Long.toString(o.getAsignado().getId()));
      ctx.sessionAttribute("esAdmin", o.getAsignado().getEsAdmin());
      ctx.redirect("/colaboracion");
    } else {
      Errorpopups.erroresyredireccion(ctx,"⚠️ Credenciales incorrectas. Por favor, intenta de nuevo."
              ,"/");
    }
  }


  public void cerrarSesion(Context ctx) {
    ctx.sessionAttribute("usuarioID", null);
    ctx.redirect("/");
  }
}
