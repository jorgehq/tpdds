package Domain.Repositorios;

import Domain.Colaborador.Colaborador;
import Domain.Solicitudes.SolicitudColaboracion;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class RepoSolicitudColaboracion {
    private EntityManagerFactory emf;
    private EntityManager em;
    private static RepoSolicitudColaboracion instance;


    public RepoSolicitudColaboracion() {

        emf = Persistence.createEntityManagerFactory("simple-persistence-unit");
        em = emf.createEntityManager();
    }

    public static RepoSolicitudColaboracion getInstance() {
        if (instance == null) {
            instance = new RepoSolicitudColaboracion();
        }
        return instance;
    }


    public SolicitudColaboracion buscarPorId(Long id) {

        SolicitudColaboracion colaborador = em.find(SolicitudColaboracion.class, id);

        return colaborador;
    }


    public List<SolicitudColaboracion> obtenerTodos() {

        TypedQuery<SolicitudColaboracion> query = em.createQuery("SELECT u FROM SolicitudColaboracion u", SolicitudColaboracion.class);
        List<SolicitudColaboracion> colaboradores = query.getResultList();
        return colaboradores;
    }

    public void guardar(SolicitudColaboracion c) {
        em.getTransaction().begin();
        em.persist(c);
        em.getTransaction().commit();
    }
    public void merge(SolicitudColaboracion c) {
        em.getTransaction().begin();
        em.merge(c);
        em.getTransaction().commit();
    }
    public void remove(SolicitudColaboracion c) {
        em.getTransaction().begin();
        em.remove(c);
        em.getTransaction().commit();
    }
}
