package cl.duoc.mensajeria.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import cl.duoc.mensajeria.model.Mensajeria;
import cl.duoc.mensajeria.repository.MensajeriaRepository;

import java.util.List;
import java.util.Set;

@Service
public class MensajeriaService {

    private static final Set<String> ESTADOS_VALIDOS = Set.of("PENDIENTE", "RESPONDIDO");

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

    // Cambia el estado del mensaje (por ejemplo, de PENDIENTE a RESPONDIDO).
    public Mensajeria cambiarEstado(Long id, String estado) {
        if (!ESTADOS_VALIDOS.contains(estado)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estado no válido");
        }
        Mensajeria contacto = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mensaje no encontrado"));
        contacto.setEstado(estado);
        return repository.save(contacto);
    }
}
