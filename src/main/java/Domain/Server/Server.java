package Domain.Server;

import Domain.CronTasks.CronVerificandoIntegridadHeladeras;
import Domain.CronTasks.CronVerificarSolicitudes;
import Domain.Repositorios.RepoUsuario;
import Domain.Server.Controlador.*;
import io.javalin.Javalin;

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
    app.post("/registro/colaborador", registroController::eleccionColaborador);

    app.get("/registro/persona", registroController::pantallaPersonaHumana);
    app.post("/registro/persona", registroController::crearUsuarioPersonaHumana);

    app.get("/registro/negocio", registroController::pantallaPersonaJuridica);
    app.post("/registro/negocio", registroController::crearUsuarioPersonaJuridica);

    app.get("/colaboracion", colaboracionController::principal); // Generalizado
    app.post("/colaboracion/eleccion", colaboracionController::eleccionColaboracion);

    app.get("/colaboracion/dinero", colaboracionController::pantalla_donar_dinero); // Cambio a más específico
    app.post("/colaboracion/dinero", colaboracionController::donar_dinero);

    app.get("/colaboracion/vianda", colaboracionController::pantalla_donar_vianda);
    app.post("/colaboracion/vianda", colaboracionController::donar_vianda);

    app.get("/colaboracion/distribucion", colaboracionController::pantalla_distribuir_viandas); // Más claro
    app.post("/colaboracion/distribucion", colaboracionController::distribuir_viandas);

    app.get("/colaboracion/heladera", colaboracionController::pantalla_donar_heladera);
    app.post("/colaboracion/heladera", colaboracionController::donar_heladera);

    app.get("/colaboracion/persona-vulnerable", colaboracionController::pantalla_persona_vulnerable);
    app.post("/colaboracion/persona-vulnerable", colaboracionController::registro_persona_vulnerable);

    app.get("/heladeras", heladeraController::pantallaPrincipal);
    app.post("/heladeras", heladeraController::suscribirseOreportar);

    app.get("/falla", fallaTecnicaControler::pantalla_reportar_falla);
    app.post("/falla", fallaTecnicaControler::reportar_falla);

    app.get("/reporte", reporteController::pantallaPrincipal);
    app.post("/reporte/descargar", reporteController::descargarReporte);

    app.get("/colaboracion/archivo", cargaColaboracionController::pantalla_carga_datos);
    app.post("/colaboracion/archivo", cargaColaboracionController::pantalla_carga_datos);

    app.get("/notificaciones", notificacionController::pantallaNotificaciones);
    app.post("/notificaciones", notificacionController::aceptarNotificacion);

    app.get("/cuenta", cuentaController::pantalla_cuenta_principal);
    app.post("/cuenta", cuentaController::modificarCuenta);
    app.post("/Cerrar", cuentaController::cerrarCuenta);


    int port = Integer.parseInt(System.getenv("PORT"));
    app.start(port);

    iniciarBD();

  //  new CronVerificarSolicitudes().verificandoYmandarMensaje();
   // new CronVerificandoIntegridadHeladeras().verificandoYmandarMensaje();
  }



  public void iniciarBD(){
    RepoUsuario.getInstance().obtenerTodos();
  }

}
