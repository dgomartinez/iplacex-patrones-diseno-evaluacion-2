package cl.patrones.taller.u2.catalogo.service;

import java.util.List;
import java.util.Optional;

import cl.patrones.taller.u2.catalogo.domain.Categoria;

public interface ClasificacionService {

	public Optional<Categoria> getCategoriaPorSku(String sku);
	public List<String> getSkusPorCategoria(Long categoriaId);
}
