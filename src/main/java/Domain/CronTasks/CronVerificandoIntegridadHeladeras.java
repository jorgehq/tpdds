package Domain.CronTasks;

import Domain.Heladera.Heladera;
import Domain.Incidentes.FallaTecnica;
import Domain.Notificaciones.Notificacion;
import Domain.Notificaciones.NotificacionFaltanViandas;
import Domain.Notificaciones.NotificacionIncidente;
import Domain.Repositorios.RepoFallaTecnica;
import Domain.Repositorios.RepoHeladera;
import Domain.Repositorios.RepoNotificaciones;
import Domain.Repositorios.RepoSolicitudColaboracion;
import Domain.Solicitudes.SolicitudColaboracion;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CronVerificandoIntegridadHeladeras {
  public CronVerificandoIntegridadHeladeras() {
  }

  public void mostrar(){

    System.out.println("=======================================================");
    System.out.println("Cron Buscando Problemas en heladeras caducadas ");
    System.out.println("=======================================================");
  }
  public void verificandoYmandarMensaje(){
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    scheduler.schedule(() -> {

      scheduler.scheduleAtFixedRate(this::verificarHeladeras, 0, 180, TimeUnit.SECONDS);
    }, 30, TimeUnit.SECONDS);
  }

  public void verificarHeladeras() {
    this.mostrar();

    // Obtener todas las heladeras en buen estado
    Set<Heladera> lista = RepoHeladera.getInstance()
            .obtenerTodos()
            .stream()
            .collect(Collectors.toSet());

    System.out.println("=======================================================");
    System.out.println("Hay " + lista.size() + " heladeras en buen estado en la lista total");
    System.out.println("=======================================================");

    // Obtener todas las notificaciones existentes
    List<Notificacion> notificaciones = RepoNotificaciones.getInstance().obtenerTodos();

    for (Heladera heladera : lista) {

      if (heladera.getEstadoHeladera().getHeladeraAveriada()) {
        System.out.println("=======================================================");
        System.out.println("SE detectó incidente en la heladera " + heladera.getNombre() + ". Verificando notificación.");
        System.out.println("=======================================================");

        // Buscar la notificación de incidente para esta heladera
        Notificacion notificacionExistente = null;
        for (Notificacion notificacion : notificaciones) {
          if (notificacion.getHeladera().equals(heladera) && notificacion instanceof NotificacionIncidente) {
            notificacionExistente = notificacion;
            break; // Salir del bucle al encontrar la notificación
          }
        }

        if (notificacionExistente != null) {
          // Notificar a los interesados si la notificación existe
          heladera.notificarInteresados(notificacionExistente);
        } else {
          System.out.println("No se encontró una notificación de incidente para la heladera " + heladera.getNombre());
        }
      } else if (heladera.getViandasEnHeladera().size() < 20) {
        System.out.println("=======================================================");
        System.out.println("SE detectó falta de viandas en la heladera " + heladera.getNombre() + ". Mandando notificaciones faltantes.");
        System.out.println("=======================================================");


        // Verificar si ya existe una notificación de falta de viandas para esta heladera
        boolean notificacionExistente = false;
        for (Notificacion notificacion : notificaciones) {
          System.out.println("Notificaciones data: "+notificacion.getId()+" Heladera "+notificacion.getHeladera().getNombre()
                  +" tipo: "+notificacion.getTipoNotificacion());
          if (notificacion.getHeladera().equals(heladera) && notificacion instanceof NotificacionFaltanViandas) {
            notificacionExistente = true;
            break; // Salir del bucle al encontrar la notificación
          }
        }
        System.out.println("??????????????????????????????La notificacion ya existe?"+ notificacionExistente);
        if (!notificacionExistente) {
          System.out.println("===========================Creando una nueva notificaicona para viandas faltantres");
          NotificacionFaltanViandas notificacion = new NotificacionFaltanViandas(
                  heladera.getCapacidadDeViandas() - heladera.getViandasEnHeladera().size(),
                  heladera
          );
          RepoNotificaciones.getInstance().guardar(notificacion);
          heladera.notificarInteresados(notificacion);
        }
      }
    }
  }
}
