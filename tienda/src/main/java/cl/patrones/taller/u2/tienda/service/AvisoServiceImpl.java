package cl.patrones.taller.u2.tienda.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.patrones.taller.u2.bodegaje.domain.Producto;
import cl.patrones.taller.u2.bodegaje.domain.Stock;
import cl.patrones.taller.u2.bodegaje.service.BodegajeService;
import cl.patrones.taller.u2.catalogo.domain.Aviso;
import cl.patrones.taller.u2.catalogo.domain.Categoria;
import cl.patrones.taller.u2.catalogo.service.CategoriaService;
import cl.patrones.taller.u2.catalogo.service.ClasificacionService;

@Service
public class AvisoServiceImpl implements AvisoService {

	private final BodegajeService bodegajeService;
	private final ClasificacionService clasificacionService;
	private final CategoriaService categoriaService;

	public AvisoServiceImpl(
			BodegajeService bodegajeService,
			ClasificacionService clasificacionService,
			CategoriaService categoriaService) {
		super();
		this.bodegajeService = bodegajeService;
		this.clasificacionService = clasificacionService;
		this.categoriaService = categoriaService;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Aviso> getAvisos() {
		return bodegajeService.getProductos().stream()
				.map(this::toAviso)
				.flatMap(Optional::stream)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<Aviso> getAvisosPorCategoria(Long categoriaId) {
		List<String> skus = clasificacionService.getSkusPorCategoria(categoriaId);
		if (skus.isEmpty()) {
			return List.of();
		}

		return categoriaService.getCategoriaPorId(categoriaId)
				.map(categoria -> bodegajeService
						.getProductosBySku(skus.toArray(new String[0])).stream()
						.map(producto -> toAviso(producto, categoria))
						.toList())
				.orElse(List.of());
	}

	private Optional<Aviso> toAviso(Producto producto) {
		return clasificacionService.getCategoriaPorSku(producto.getSku())
				.map(categoria -> toAviso(producto, categoria));
	}

	private Aviso toAviso(Producto producto, Categoria categoria) {
		int stock = producto.getStocks().stream()
				.mapToInt(Stock::getCantidad)
				.sum();

		Long precio = Math.round(producto.getCosto() * 1.30);

		return new Aviso(
				producto.getId(),
				producto.getSku(),
				producto.getNombre(),
				precio,
				producto.getImagen(),
				stock,
				categoria);
	}

}
