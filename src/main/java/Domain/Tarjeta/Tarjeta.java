package Domain.Tarjeta;

import Domain.Exception.CodigoInvalidoException;
import Domain.Exception.SinUsosDisponiblesException;
import Domain.Heladera.Heladera;
import Domain.Persona.PersonaVulnerable;
import Domain.Repositorios.RepoTarjetas;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
@Entity
public class Tarjeta {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @OneToMany
  @JoinColumn(name = "tarjeta_id")
  private List<MovimientoTarjeta> movimientos = new ArrayList<>();
  @Column
  private String codigo; // Podriamos hacer la validacion en el formulario
  @Column
  private LocalDate fechaRegistro;



  public Tarjeta() {
  }

  public Tarjeta(String codigo, LocalDate fechaRegistro) {
    if (codigo.length() == 11) {
      this.codigo = codigo;
    }
    else {
      throw new CodigoInvalidoException("El codigo debe tener 11 caracteres");
    }
    this.fechaRegistro = fechaRegistro;

  }

  public void utilizarTarjeta(Integer cantidadDeMenoresACargo, Heladera heladera) {
    System.out.println("Movimientos "+todayUses());
    if (todayUses() < 4 + cantidadDeMenoresACargo * 2) {

      heladera.quitarVianda();

      this.registrarMovimiento(new MovimientoTarjeta(heladera));
    }

  }

  private Integer todayUses() {
    return this.movimientos.stream()
        .filter(movimiento -> movimiento.fecha.equals(LocalDate.now()))
        .toList().size();
  }

  private void registrarMovimiento(MovimientoTarjeta movimientoTarjeta) {
    this.movimientos.add(movimientoTarjeta);
    RepoTarjetas.getInstance().guardar(this);
  }

  public String getCodigo() {
    return this.codigo;
  }

  public LocalDate getFechaRegistro() {
    return this.fechaRegistro;
  }


  public int getIntentos() {

    return this.movimientos.size();
  }

  public Long getId() {
    return id;
  }
}
