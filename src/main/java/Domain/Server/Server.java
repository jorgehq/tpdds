package Domain.Server;

import Domain.CronTasks.CronVerificandoIntegridadHeladeras;
import Domain.CronTasks.CronVerificarSolicitudes;
import Domain.Repositorios.RepoUsuario;
import Domain.Server.Controlador.*;
import io.javalin.Javalin;

import java.util.HashMap;
import java.util.Map;

public class Server {
  public void start() {
    Javalin app = Javalin.create(config -> {
      config.staticFiles.add(staticFileConfig -> {
        staticFileConfig.hostedPath = "/assets";
        staticFileConfig.directory = "/assets";
      });
    });
    UsuarioControlador usuarioControlador = new UsuarioControlador();
    RegistroControlador registroController = new RegistroControlador();
    ColaboracionController colaboracionController = new ColaboracionController();
    HeladeraController heladeraController = new HeladeraController();
    ReporteController reporteController = new ReporteController();
    CargaColaboracionController cargaColaboracionController = new CargaColaboracionController();
    NotificacionController notificacionController = new NotificacionController();
    FallaTecnicaControler fallaTecnicaControler = new FallaTecnicaControler();
    CuentaController cuentaController = new CuentaController();

    app.get("/", usuarioControlador::login);
    app.post("/login", usuarioControlador::inicioSesion);

    app.get("/registro", registroController::eleccion);
    app.post("/colaboradores", registroController::eleccionColaborador);

    app.get("/registro/persona", registroController::pantallaPersonaHumana);
    app.post("/personas", registroController::crearUsuarioPersonaHumana);

    app.get("/registro/negocio", registroController::pantallaPersonaJuridica);
    app.post("/negocios", registroController::crearUsuarioPersonaJuridica);

    app.get("/colaboracion", colaboracionController::principal);
    app.post("/elecciones-colaboraciones", colaboracionController::eleccionColaboracion);

    app.get("/colaboracion/dinero", colaboracionController::pantalla_donar_dinero);
    app.post("/colaboraciones-dinero", colaboracionController::donar_dinero);

    app.get("/colaboracion/vianda", colaboracionController::pantalla_donar_vianda);
    app.post("/viandas", colaboracionController::donar_vianda);

    app.get("/colaboracion/distribucion", colaboracionController::pantalla_distribuir_viandas);
    app.post("/distribuciones", colaboracionController::distribuir_viandas);

    app.get("/colaboracion/heladera", colaboracionController::pantalla_donar_heladera);
    app.post("/colaboraciones-heladeras", colaboracionController::donar_heladera); // Porq heladeras ya existe

    app.get("/colaboracion/persona-vulnerable", colaboracionController::pantalla_persona_vulnerable);
    app.post("/personas-vulnerables", colaboracionController::registro_persona_vulnerable);

    app.post("/tarjetas", colaboracionController::solicitarTarjetas);

    app.get("/heladeras", heladeraController::pantallaPrincipal);
    app.post("/heladeras", heladeraController::suscribirseOreportar);
    ///heladeras/{id_heladera}

    app.get("/falla", fallaTecnicaControler::pantalla_reportar_falla);
    app.post("/fallas", fallaTecnicaControler::reportar_falla);

    app.get("/reporte", reporteController::pantallaPrincipal);
    app.get("/descargas", reporteController::descargarReporte); //Cambiar a get

    app.get("/colaboracion/archivo", cargaColaboracionController::pantalla_carga_datos);
    app.post("/colaboraciones/csv", cargaColaboracionController::cargar_archivo);


    app.get("/notificaciones", notificacionController::pantallaNotificaciones);
    app.get("/notificaciones/eliminar", notificacionController::eliminarNotificacionColaborador);

    app.get("/cuenta", cuentaController::pantalla_cuenta_principal);
    app.post("/cuentas", cuentaController::modificarCuenta);

    app.get("/cuenta/cerrar", cuentaController::cerrarCuenta);
    app.get("/colaboraciones", cuentaController::AceptarTodasColaboraciones);


    iniciarBD();
   // int port = Integer.parseInt(System.getenv("PORT"));
    app.before(ctx -> {
      Map<String, Object> sharedData = new HashMap<>();
      sharedData.put("esAdmin", ctx.sessionAttribute("esAdmin") != null ? ctx.sessionAttribute("esAdmin") : false);
      sharedData.put("usuarioID", ctx.sessionAttribute("usuarioID") != null ? ctx.sessionAttribute("usuarioID") : "");
      ctx.attribute("sharedData", sharedData);
    });
    app.start(7000);

    new CronVerificarSolicitudes().verificandoYmandarMensaje();
   new CronVerificandoIntegridadHeladeras().verificandoYmandarMensaje();
  }



  public void iniciarBD(){
    RepoUsuario.getInstance().obtenerTodos();
  }

}
