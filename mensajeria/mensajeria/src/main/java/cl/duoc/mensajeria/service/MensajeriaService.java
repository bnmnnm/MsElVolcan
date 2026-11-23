package cl.duoc.mensajeria.service;

import org.springframework.stereotype.Service;

import cl.duoc.mensajeria.model.Mensajeria;
import cl.duoc.mensajeria.repository.MensajeriaRepository;

import java.util.List;

@Service
public class MensajeriaService {

    private final MensajeriaRepository repository;

    public MensajeriaService(MensajeriaRepository repository) {
        this.repository = repository;
    }

    public Mensajeria guardar(Mensajeria contacto) {
        contacto.setId(null);
        contacto.setEstado("PENDIENTE");
        return repository.save(contacto);
    }

    public List<Mensajeria> listar() {
        return repository.findAll();
    }
}
