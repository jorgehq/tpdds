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
      System.out.println("Cron Buscando Solicitudes caducadas ");
      System.out.println("=======================================================");
    }
    public void verificandoYmandarMensaje(){
      ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
      scheduler.schedule(() -> {

        scheduler.scheduleAtFixedRate(this::verificarSolicitudes, 0, 180, TimeUnit.SECONDS);
      }, 30, TimeUnit.SECONDS);
    }

    public void verificarSolicitudes(){
       this.mostrar();
      List<SolicitudColaboracion> lista=RepoSolicitudColaboracion.getInstance().obtenerTodos();
      System.out.println("==============================Cantidad Solicitudes en Lista: "+lista.size()+" ===========================================");
      for(SolicitudColaboracion s:lista){
          System.out.println("=======================================================");
          System.out.println(s.fechaExpiracion);
          System.out.println("esta expirado??? "+s.fechaExpiracion.isBefore(LocalDate.now()));
          System.out.println("Ya se notifico??? "+s.isExpired());
          System.out.println("=======================================================");

        if(s.fechaExpiracion.isBefore(LocalDate.now()) && !s.isExpired()){
          s.setExpired(true);
          RepoSolicitudColaboracion.getInstance().guardar(s);
            System.out.println("=======================================================");
            System.out.println("Se encontro una solicitud expirada");
            System.out.println("=======================================================");

        }else{
            System.out.println("=======================================================");
            System.out.println("Solicitud "+s.getId()+" no esta expirada");
            System.out.println("=======================================================");
        }
      }
     }
  }

