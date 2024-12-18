package Domain.CronTasks;

import Domain.Repositorios.RepoSolicitudColaboracion;
import Domain.Solicitudes.SolicitudColaboracion;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CronVerificarSolicitudes {
     public CronVerificarSolicitudes() {
    }
    public void mostrar(){
      System.out.println("=======================================================");
      System.out.println("Cron Buscando incidentes cercanos y notificar");
      System.out.println("=======================================================");
    }
    public void verificandoYmandarMensaje(){

      ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
      scheduler.scheduleAtFixedRate(this::mostrar, 0, 100, TimeUnit.SECONDS);
    }

    public void verificarSolicitudes(){
      List<SolicitudColaboracion> lista=RepoSolicitudColaboracion.getInstance().obtenerTodos();
      for(SolicitudColaboracion s:lista){
        if(s.fechaExpiracion.isBefore(LocalDate.now())){
          s.setExpired(true);
          RepoSolicitudColaboracion.getInstance().guardar(s);
        }
      }
     }
  }

