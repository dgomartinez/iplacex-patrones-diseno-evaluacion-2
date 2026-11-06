package cl.patrones.taller.u2.tienda.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.patrones.taller.u2.bodegaje.domain.Bodega;
import cl.patrones.taller.u2.bodegaje.domain.Producto;
import cl.patrones.taller.u2.bodegaje.domain.Stock;
import cl.patrones.taller.u2.bodegaje.service.BodegajeService;
import cl.patrones.taller.u2.catalogo.domain.Aviso;
import cl.patrones.taller.u2.catalogo.domain.Categoria;
import cl.patrones.taller.u2.catalogo.service.CategoriaService;
import cl.patrones.taller.u2.catalogo.service.ClasificacionService;

@ExtendWith(MockitoExtension.class)
class AvisoServiceImplTest {

	@Mock
	private BodegajeService bodegajeService;

	@Mock
	private ClasificacionService clasificacionService;

	@Mock
	private CategoriaService categoriaService;

	@InjectMocks
	private AvisoServiceImpl avisoService;

	@Test
	void getAvisosDevuelveLosSeisProductosClasificados() {
		Categoria comedor = categoria(7L, "Comedor");
		Categoria dormitorio = categoria(8L, "Dormitorio");
		Categoria modernas = categoria(4L, "Modernas");
		Categoria clasicas = categoria(3L, "Clásicas");

		List<Producto> productos = List.of(
				producto(1L, "lamp-001", "Lámpara LumiGlow", 20000L, "pendant-lamp-light.jpg", 10, 5, 3),
				producto(2L, "lamp-002", "Lámpara NordicBeam", 50000L, "carrie-table-lamp2.jpg", 20, 10, 30),
				producto(3L, "lamp-003", "Lámpara Nomad Light", 45000L, "carrie-table-lamp.jpg", 2, 4, 3),
				producto(4L, "lamp-004", "Lámpara HaloTouch", 15000L, "table-lamp-14x29cm.jpg", 0, 1, 1),
				producto(5L, "sill-001", "Silla Lounge", 25000L, "wegner-lounge-chair.jpg", 3),
				producto(6L, "sill-002", "Silla Clásica Moderna", 20000L, "modern-classic-chairs.jpg", 1));

		when(bodegajeService.getProductos()).thenReturn(productos);
		when(clasificacionService.getCategoriaPorSku("lamp-001")).thenReturn(Optional.of(comedor));
		when(clasificacionService.getCategoriaPorSku("lamp-002")).thenReturn(Optional.of(comedor));
		when(clasificacionService.getCategoriaPorSku("lamp-003")).thenReturn(Optional.of(comedor));
		when(clasificacionService.getCategoriaPorSku("lamp-004")).thenReturn(Optional.of(dormitorio));
		when(clasificacionService.getCategoriaPorSku("sill-001")).thenReturn(Optional.of(modernas));
		when(clasificacionService.getCategoriaPorSku("sill-002")).thenReturn(Optional.of(clasicas));

		List<Aviso> avisos = avisoService.getAvisos();

		assertThat(avisos).hasSize(6);
		assertThat(avisos).extracting(Aviso::getSku)
				.containsExactly("lamp-001", "lamp-002", "lamp-003", "lamp-004", "sill-001", "sill-002");
	}

	@Test
	void calculaElPrecioConTreintaPorCientoDeUtilidad() {
		Producto producto = producto(1L, "lamp-001", "Lámpara LumiGlow", 20000L, "pendant-lamp-light.jpg", 10);
		when(bodegajeService.getProductos()).thenReturn(List.of(producto));
		when(clasificacionService.getCategoriaPorSku("lamp-001"))
				.thenReturn(Optional.of(categoria(7L, "Comedor")));

		List<Aviso> avisos = avisoService.getAvisos();

		assertThat(avisos.get(0).getPrecio()).isEqualTo(26000L);
	}

	@Test
	void redondeaElPrecioAlEnteroMasCercano() {
		Producto producto = producto(1L, "lamp-001", "Lámpara LumiGlow", 19999L, "pendant-lamp-light.jpg", 1);
		when(bodegajeService.getProductos()).thenReturn(List.of(producto));
		when(clasificacionService.getCategoriaPorSku("lamp-001"))
				.thenReturn(Optional.of(categoria(7L, "Comedor")));

		List<Aviso> avisos = avisoService.getAvisos();

		assertThat(avisos.get(0).getPrecio()).isEqualTo(25999L);
	}

	@Test
	void sumaElStockDeTodasLasBodegas() {
		Producto producto = producto(1L, "lamp-001", "Lámpara LumiGlow", 20000L, "pendant-lamp-light.jpg", 10, 5, 3);
		when(bodegajeService.getProductos()).thenReturn(List.of(producto));
		when(clasificacionService.getCategoriaPorSku("lamp-001"))
				.thenReturn(Optional.of(categoria(7L, "Comedor")));

		List<Aviso> avisos = avisoService.getAvisos();

		assertThat(avisos.get(0).getStock()).isEqualTo(18);
	}

	@Test
	void devuelveStockCeroCuandoNoHayBodegas() {
		Producto producto = producto(1L, "lamp-001", "Lámpara LumiGlow", 20000L, "pendant-lamp-light.jpg");
		when(bodegajeService.getProductos()).thenReturn(List.of(producto));
		when(clasificacionService.getCategoriaPorSku("lamp-001"))
				.thenReturn(Optional.of(categoria(7L, "Comedor")));

		List<Aviso> avisos = avisoService.getAvisos();

		assertThat(avisos.get(0).getStock()).isZero();
	}

	@Test
	void excluyeProductosSinClasificacion() {
		Producto clasificado = producto(1L, "lamp-001", "Lámpara LumiGlow", 20000L, "pendant-lamp-light.jpg", 10);
		Producto sinClasificar = producto(7L, "sill-003", "Silla Sin Clasificar", 10000L, "silla.jpg", 5);

		when(bodegajeService.getProductos()).thenReturn(List.of(clasificado, sinClasificar));
		when(clasificacionService.getCategoriaPorSku("lamp-001"))
				.thenReturn(Optional.of(categoria(7L, "Comedor")));
		when(clasificacionService.getCategoriaPorSku("sill-003")).thenReturn(Optional.empty());

		List<Aviso> avisos = avisoService.getAvisos();

		assertThat(avisos).hasSize(1);
		assertThat(avisos.get(0).getSku()).isEqualTo("lamp-001");
	}

	@Test
	void getAvisosPorCategoriaDevuelveLosProductosDeLaCategoria() {
		Categoria comedor = categoria(7L, "Comedor");
		List<Producto> productos = List.of(
				producto(1L, "lamp-001", "Lámpara LumiGlow", 20000L, "pendant-lamp-light.jpg", 10),
				producto(2L, "lamp-002", "Lámpara NordicBeam", 50000L, "carrie-table-lamp2.jpg", 20),
				producto(3L, "lamp-003", "Lámpara Nomad Light", 45000L, "carrie-table-lamp.jpg", 2));

		when(clasificacionService.getSkusPorCategoria(7L))
				.thenReturn(List.of("lamp-001", "lamp-002", "lamp-003"));
		when(categoriaService.getCategoriaPorId(7L)).thenReturn(Optional.of(comedor));
		when(bodegajeService.getProductosBySku(any(String[].class))).thenReturn(productos);

		List<Aviso> avisos = avisoService.getAvisosPorCategoria(7L);

		assertThat(avisos).hasSize(3);
		assertThat(avisos).allMatch(aviso -> aviso.getCategoria() == comedor);
		assertThat(avisos).extracting(Aviso::getSku)
				.containsExactly("lamp-001", "lamp-002", "lamp-003");
	}

	@Test
	void getAvisosPorCategoriaReutilizaLaCategoriaSinConsultarPorSku() {
		Categoria comedor = categoria(7L, "Comedor");
		List<Producto> productos = List.of(
				producto(1L, "lamp-001", "Lámpara LumiGlow", 20000L, "pendant-lamp-light.jpg", 10),
				producto(2L, "lamp-002", "Lámpara NordicBeam", 50000L, "carrie-table-lamp2.jpg", 20));

		when(clasificacionService.getSkusPorCategoria(7L))
				.thenReturn(List.of("lamp-001", "lamp-002"));
		when(categoriaService.getCategoriaPorId(7L)).thenReturn(Optional.of(comedor));
		when(bodegajeService.getProductosBySku(any(String[].class))).thenReturn(productos);

		avisoService.getAvisosPorCategoria(7L);

		verify(clasificacionService, times(1)).getSkusPorCategoria(7L);
		verify(categoriaService, times(1)).getCategoriaPorId(7L);
		verify(bodegajeService, times(1)).getProductosBySku(any(String[].class));
		verify(clasificacionService, never()).getCategoriaPorSku(anyString());
	}

	@Test
	void getAvisosPorCategoriaSinSkusNoConsultaBodegaje() {
		when(clasificacionService.getSkusPorCategoria(1L)).thenReturn(List.of());

		List<Aviso> avisos = avisoService.getAvisosPorCategoria(1L);

		assertThat(avisos).isEmpty();
		verifyNoInteractions(bodegajeService);
		verifyNoInteractions(categoriaService);
	}

	@Test
	void getAvisosPorCategoriaInexistenteDevuelveListaVacia() {
		when(clasificacionService.getSkusPorCategoria(99L)).thenReturn(List.of("lamp-001"));
		when(categoriaService.getCategoriaPorId(99L)).thenReturn(Optional.empty());

		List<Aviso> avisos = avisoService.getAvisosPorCategoria(99L);

		assertThat(avisos).isEmpty();
		verifyNoInteractions(bodegajeService);
	}

	private Categoria categoria(Long id, String nombre) {
		Categoria categoria = new Categoria();
		categoria.setId(id);
		categoria.setNombre(nombre);
		return categoria;
	}

	private Producto producto(Long id, String sku, String nombre, Long costo, String imagen, int... cantidades) {
		Producto producto = new Producto(sku, nombre, costo, imagen);
		producto.setId(id);

		List<Stock> stocks = new ArrayList<>();
		for (int cantidad : cantidades) {
			stocks.add(new Stock(new Bodega("Bodega", "Dirección"), producto, cantidad));
		}
		producto.setStocks(stocks);

		return producto;
	}

}
