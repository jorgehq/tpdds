package Domain.Repositorios;

import Domain.Incidentes.FallaTecnica;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class RepoFallaTecnica {
  private EntityManagerFactory emf;
  private EntityManager em;
  private static RepoFallaTecnica instance;
  public RepoFallaTecnica() {

    emf = Persistence.createEntityManagerFactory("simple-persistence-unit");
    em = emf.createEntityManager();
  }

  public static RepoFallaTecnica getInstance() {
    if (instance == null) {
      instance = new RepoFallaTecnica();
    }
    return instance;
  }


  public FallaTecnica buscarPorId(Long id) {

    FallaTecnica falla = em.find(FallaTecnica.class, id);

    return falla;
  }



  public List<FallaTecnica> obtenerTodos() {

    TypedQuery<FallaTecnica> query = em.createQuery("SELECT u FROM FallaTecnica u", FallaTecnica.class);

    List<FallaTecnica> fallas = query.getResultList();
    return fallas;
  }

  public void guardar(FallaTecnica falla) {
    em.getTransaction().begin();
    em.persist(falla);
    em.getTransaction().commit();

  }

}
