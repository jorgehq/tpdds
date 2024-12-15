package Domain.Server;

import Domain.Server.Controlador.*;
import io.javalin.Javalin;

public class App {
    public static void main(String[] args) {


        Javalin app = Javalin.create(config -> {
            config.staticFiles.add(staticFileConfig -> {
                staticFileConfig.hostedPath="/assets";
                staticFileConfig.directory="/assets";
            });
        });
        UsuarioControlador usuarioControlador=new UsuarioControlador();
        RegistroControlador registroController = new RegistroControlador();
        ColaboracionController colaboracionController=new ColaboracionController();
        HeladeraController heladeraController=new HeladeraController();
        ReporteController reporteController=new ReporteController();
        CargaColaboracionController cargaColaboracionController=new CargaColaboracionController();
        NotificacionController notificacionController=new NotificacionController();
        FallaTecnicaControler fallaTecnicaControler=new FallaTecnicaControler();
        CuentaController cuentaController=new CuentaController();

        app.get("/", usuarioControlador::login);
        app.post("/", usuarioControlador::inicioSesion);

        app.get("/elegir_registro", registroController::eleccion);
        app.post("/elegir_registro", registroController::eleccionColaborador);

        app.get("/registro_persona", registroController::pantallaPersonaHumana);
        app.post("/registro_persona", registroController::crearUsuarioPersonaHumana);

        app.get("/registro_negocio", registroController::pantallaPersonaJuridica);
        app.post("/registro_negocio", registroController::crearUsuarioPersonaJuridica);

        app.get("/colaboracion", colaboracionController::principal);
        app.post("/colaboracion",colaboracionController::eleccionColaboracion);

        app.get("/donar_dinero",colaboracionController::pantalla_donar_dinero);
        app.post("/donar_dinero",colaboracionController::donar_dinero);

        app.get("/donar_vianda",colaboracionController::pantalla_donar_vianda);
        app.post("/donar_vianda",colaboracionController::donar_vianda);

        app.get("/distribuir_vianda",colaboracionController::pantalla_distribuir_viandas);
        app.post("/distribuir_vianda",colaboracionController::distribuir_viandas);

        app.get("/donar_heladera",colaboracionController::pantalla_donar_heladera);
        app.post("/donar_heladera",colaboracionController::donar_heladera);

        app.get("/registro_persona_vulnerable",colaboracionController::pantalla_persona_vulnerable);
        app.post("/registro_persona_vulnerable",colaboracionController::registro_persona_vulnerable);

        app.get("/heladeras",heladeraController::pantallaPrincipal);
        app.post("/heladeras",heladeraController::suscribirseOreportar);

        app.get("/reportar_falla",fallaTecnicaControler::pantalla_reportar_falla);
        app.post("/reportar_falla",fallaTecnicaControler::reportar_falla);


        app.get("/reporte",reporteController::pantallaPrincipal);
        app.post("/reporte",reporteController::descargarReporte);

        app.get("/cargar_archivo_colaboracion", cargaColaboracionController::pantalla_carga_datos);
        app.post("/cargar_archivo_colaboracion", cargaColaboracionController::pantalla_carga_datos);

        app.get("/notificacion", notificacionController::pantallaNotificaciones);
        app.post("/notificacion", notificacionController::aceptarNotificacion);

        app.get("/cuenta", cuentaController::pantalla_cuenta_principal);
        app.post("/cuenta", cuentaController::modificarCuenta);
        app.post("/Cerrar", cuentaController::modificarCuenta);





        int port = Integer.parseInt(System.getenv("PORT"));
        app.start(port);
    }



}

