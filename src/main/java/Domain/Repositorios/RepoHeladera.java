package Domain.Repositorios;

import Domain.Heladera.Heladera;
import Domain.Tarjeta.Tarjeta;
import Domain.Ubicacion.Localidad;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RepoHeladera {
  private EntityManagerFactory emf;
  private EntityManager em;
  private static RepoHeladera instance;
  public RepoHeladera() {

    emf = Persistence.createEntityManagerFactory("simple-persistence-unit");
    em = emf.createEntityManager();
  }

  public static RepoHeladera getInstance() {
    if (instance == null) {
      instance = new RepoHeladera();
    }
    return instance;
  }


  public Heladera buscarPorId(Long id) {

    Heladera heladera = em.find(Heladera.class, id);
    heladera.getEstado().iniciarSensores();
    return heladera;
  }



  public Set<Heladera> obtenerTodos() {

    TypedQuery<Heladera> query = em.createQuery("SELECT u FROM Heladera u", Heladera.class);

    List<Heladera> listaHeladeras = query.getResultList();
    Set<Heladera> heladeras = new HashSet<>(listaHeladeras);

    heladeras.forEach(h -> h.getEstado().iniciarSensores());
    return heladeras;
  }
  public Set<Heladera> filtrarPorNombre(String nombre) {
    TypedQuery<Heladera> query = em.createQuery("SELECT h FROM Heladera h WHERE h.nombre LIKE :nombre", Heladera.class);
    query.setParameter("nombre", "%" + nombre + "%");
    List<Heladera> listaHeladeras = query.getResultList();
    Set<Heladera> heladeras = new HashSet<>(listaHeladeras);

    heladeras.forEach(h -> h.getEstado().iniciarSensores());
    return heladeras;
  }

  public Set<Heladera> filtrarPorLocalidad(String localidad) {
    TypedQuery<Heladera> query = em.createQuery("SELECT h FROM Heladera h WHERE h.direccion.localidad = :localidad", Heladera.class);
    query.setParameter("localidad", Localidad.valueOf(localidad.toUpperCase()));
    List<Heladera> listaHeladeras = query.getResultList();
    Set<Heladera> heladeras = new HashSet<>(listaHeladeras);

    heladeras.forEach(h -> h.getEstado().iniciarSensores());
    return heladeras;
  }

  public Set<Heladera> filtrarPorDireccion(String direccion) {
    TypedQuery<Heladera> query = em.createQuery("SELECT h FROM Heladera h WHERE h.direccion.direccion LIKE :direccion", Heladera.class);
    query.setParameter("direccion", "%" + direccion + "%");
    List<Heladera> listaHeladeras = query.getResultList();
    Set<Heladera> heladeras = new HashSet<>(listaHeladeras);

    heladeras.forEach(h -> h.getEstado().iniciarSensores());
    return heladeras;
  }
  public void guardar(Heladera heladera) {
    em.getTransaction().begin();
    em.persist(heladera);
    em.getTransaction().commit();

  }

  public void merge(Heladera heladera) {
    em.getTransaction().begin();
    em.merge(heladera);
    em.getTransaction().commit();

  }
  public void flush(){
    em.getTransaction().begin();
    em.flush();
    em.getTransaction().commit();
  }
}
