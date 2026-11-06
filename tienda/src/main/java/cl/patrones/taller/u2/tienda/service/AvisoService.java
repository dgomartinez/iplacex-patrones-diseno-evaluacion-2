package cl.patrones.taller.u2.tienda.service;

import java.util.List;

import cl.patrones.taller.u2.catalogo.domain.Aviso;

public interface AvisoService {

	public List<Aviso> getAvisos();
	public List<Aviso> getAvisosPorCategoria(Long categoriaId);
}
