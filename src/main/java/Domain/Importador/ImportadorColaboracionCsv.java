package Domain.Importador;

import Domain.Colaborador.Colaborador;
import Domain.Colaborador.FormaColaboracion;
import Domain.Colaborador.TipoDeColaboracion.DistribuirVianda;
import Domain.Colaborador.TipoDeColaboracion.DonarDinero;
import Domain.Colaborador.TipoDeColaboracion.DonarVianda;
import Domain.Colaborador.TipoDeColaboracion.TipoDeColaboracion;
import Domain.Colaborador.TipoDocumento.TipoDocumento;
import Domain.Colaborador.Transferencia.FrecuenciaDeDonacion;
import Domain.Converters.FormaColaboracionConverter;
import Domain.Heladera.Vianda;
import Domain.Repositorios.RepoColaboraciones;
import Domain.Repositorios.RepoColaboradores;
import Domain.Converters.TipoDocumentoConverter;
import Domain.Repositorios.RepoUsuario;
import Domain.Repositorios.RepoViandas;
import Domain.Usuarios.Usuario;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ImportadorColaboracionCsv {

  private List<TipoDeColaboracion> colaboraciones = new ArrayList<>();
  private TipoDocumentoConverter tipoDocumentoConverter = new TipoDocumentoConverter();
  private FormaColaboracionConverter formaColaboracionConverter = new FormaColaboracionConverter();

  public void importar(String nombreFicheroCSV) {
    try {
      String linea = "";
      BufferedReader br = new BufferedReader(new FileReader(nombreFicheroCSV));
      while ((linea = br.readLine()) != null) {
        String[] datoslinea = linea.split(";");
        String tipoDoc = datoslinea[0].trim();
        String documento = datoslinea[1].trim();
        String nombre = datoslinea[2].trim();
        String apellido = datoslinea[3].trim();
        String mail = datoslinea[4].trim();
        String fechaDeColaboracion = datoslinea[5].trim();
        String formaDeColaboracion = datoslinea[6].trim();
        String cantidad = datoslinea[7].trim();

        // conversion de datos
        TipoDocumento tipoDocumento = tipoDocumentoConverter.stringToTipoDocumento(tipoDoc);
        FormaColaboracion formaColaboracion = formaColaboracionConverter.stringToFormaColaboracion(formaDeColaboracion);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate fechaColaboracion = LocalDate.parse(fechaDeColaboracion, formatter);
        int cantidadI = Integer.parseInt(cantidad);
        long documentoL = Long.parseLong(documento);


        // verificar si tiene usuario
        // si no tiene, hay q crearlo
        Usuario usuario = RepoUsuario.getInstance().buscarPorNombreReal(nombre).get(0);

        List<TipoDeColaboracion> colaboracion = fabricaColaboraciones(fechaColaboracion, formaColaboracion, cantidadI,usuario.getAsignado());
        // agrego al repo de colaboraciones
        for(TipoDeColaboracion t:colaboracion){
          RepoColaboraciones.getInstance().guardar(t);
          colaboraciones.add(t);
        }

      }


      br.close();
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public List<TipoDeColaboracion> fabricaColaboraciones(LocalDate fechaColaboracion, FormaColaboracion formaColaboracion, int cantidadI,Colaborador co){
    List<TipoDeColaboracion> lista=new ArrayList<>();
    switch(formaColaboracion){
      case DINERO:
        lista.add(new DonarDinero(cantidadI,fechaColaboracion, FrecuenciaDeDonacion.UNICO));

      break;
      case DONACION_VIANDAS:
        for(int i=0;i<cantidadI;i++){
          Vianda v=new Vianda(fechaColaboracion.plusYears(1), fechaColaboracion, 1000, 4, true,co, null);
          lista.add(new DonarVianda(fechaColaboracion,
              v));
        }

      break;
      case REDISTRIBUCION_VIANDAS:
          lista.add(new DistribuirVianda(null,null,cantidadI,"",fechaColaboracion));
      break;

      default:
      break;
    }
    return lista;
  }

  public List<TipoDeColaboracion> getColaboraciones() {
    return colaboraciones;
  }
}
