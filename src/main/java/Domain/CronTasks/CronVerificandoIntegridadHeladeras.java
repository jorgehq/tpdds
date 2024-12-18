package Domain.CronTasks;

import Domain.Heladera.Heladera;
import Domain.Notificaciones.NotificacionFaltanViandas;
import Domain.Repositorios.RepoHeladera;
import Domain.Repositorios.RepoSolicitudColaboracion;
import Domain.Solicitudes.SolicitudColaboracion;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

      scheduler.scheduleAtFixedRate(this::verificarHeladeras, 0, 100, TimeUnit.SECONDS);
    }, 30, TimeUnit.SECONDS);
  }

  public void verificarHeladeras(){
    this.mostrar();
    Set<Heladera> lista= RepoHeladera.getInstance().obtenerTodos();
    System.out.println("==============================Lista: "+lista.size());
    for(Heladera s:lista){
      if(s.getViandasEnHeladera().size()<20){
        s.notificarInteresados(new NotificacionFaltanViandas(s.getCapacidadDeViandas()-s.getViandasEnHeladera().size(),s));

      }
    }
  }
}
