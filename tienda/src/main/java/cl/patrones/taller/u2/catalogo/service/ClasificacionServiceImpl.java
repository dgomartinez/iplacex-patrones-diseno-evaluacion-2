package cl.patrones.taller.u2.catalogo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import cl.patrones.taller.u2.catalogo.domain.Categoria;
import cl.patrones.taller.u2.catalogo.domain.Clasificacion;
import cl.patrones.taller.u2.catalogo.repository.ClasificacionRepository;

@Service
public class ClasificacionServiceImpl implements ClasificacionService {
	
	private final ClasificacionRepository repository;

	public ClasificacionServiceImpl(ClasificacionRepository repository) {
		super();
		this.repository = repository;
	}

	@Override
	public Optional<Categoria> getCategoriaPorSku(String sku) {
		return repository.findFirstBySku(sku)
				.map(Clasificacion::getCategoria);
	}

	@Override
	public List<String> getSkusPorCategoria(Long categoriaId) {
		return repository.findByCategoriaId(categoriaId).stream()
				.map(Clasificacion::getSku)
				.distinct()
				.toList();
	}

}
