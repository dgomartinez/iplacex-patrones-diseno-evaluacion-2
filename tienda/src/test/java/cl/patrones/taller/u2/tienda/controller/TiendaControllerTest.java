package cl.patrones.taller.u2.tienda.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.context.annotation.Import;
import cl.patrones.taller.u2.SecurityConfig;

import cl.patrones.taller.u2.catalogo.domain.Aviso;
import cl.patrones.taller.u2.catalogo.domain.Categoria;
import cl.patrones.taller.u2.catalogo.service.CategoriaService;
import cl.patrones.taller.u2.tienda.menu.service.MenuService;
import cl.patrones.taller.u2.tienda.service.AvisoService;

@WebMvcTest(TiendaController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
class TiendaControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private AvisoService avisoService;

	@MockitoBean
	private CategoriaService categoriaService;

	@MockitoBean
	private MenuService menuService;

	@Test
	void inicioAgregaLosAvisosAlModelo() throws Exception {
		Categoria comedor = categoria(7L, "Comedor");
		when(avisoService.getAvisos()).thenReturn(List.of(
				aviso(1L, "lamp-001", comedor),
				aviso(2L, "lamp-002", comedor)));

		mockMvc.perform(get("/"))
				.andExpect(status().isOk())
				.andExpect(view().name("inicio"))
				.andExpect(model().attribute("avisos", hasSize(2)));
	}

	@Test
	void categoriaConAvisosAgregaCategoriaYAvisosAlModelo() throws Exception {
		Categoria comedor = categoria(7L, "Comedor");
		when(categoriaService.getCategoriaPorId(7L)).thenReturn(Optional.of(comedor));
		when(avisoService.getAvisosPorCategoria(7L)).thenReturn(List.of(
				aviso(1L, "lamp-001", comedor),
				aviso(2L, "lamp-002", comedor),
				aviso(3L, "lamp-003", comedor)));

		mockMvc.perform(get("/categoria/7/comedor"))
				.andExpect(status().isOk())
				.andExpect(view().name("categoria"))
				.andExpect(model().attribute("categoria", comedor))
				.andExpect(model().attribute("avisos", hasSize(3)));

		verify(avisoService).getAvisosPorCategoria(7L);
	}

	@Test
	void categoriaSinAvisosDevuelveVistaConListaVacia() throws Exception {
		Categoria lamparas = categoria(1L, "Lámparas");
		when(categoriaService.getCategoriaPorId(1L)).thenReturn(Optional.of(lamparas));
		when(avisoService.getAvisosPorCategoria(1L)).thenReturn(List.of());

		mockMvc.perform(get("/categoria/1/lamparas"))
				.andExpect(status().isOk())
				.andExpect(view().name("categoria"))
				.andExpect(model().attribute("categoria", lamparas))
				.andExpect(model().attribute("avisos", hasSize(0)));
	}

	@Test
	void validaElSlugNormalizandoTildes() throws Exception {
		Categoria clasicas = categoria(3L, "Clásicas");
		when(categoriaService.getCategoriaPorId(3L)).thenReturn(Optional.of(clasicas));
		when(avisoService.getAvisosPorCategoria(3L)).thenReturn(List.of(aviso(6L, "sill-002", clasicas)));

		mockMvc.perform(get("/categoria/3/clasicas"))
				.andExpect(status().isOk())
				.andExpect(view().name("categoria"))
				.andExpect(model().attribute("categoria", clasicas));
	}

	@Test
	void slugIncorrectoDevuelve404() throws Exception {
		Categoria comedor = categoria(7L, "Comedor");
		when(categoriaService.getCategoriaPorId(7L)).thenReturn(Optional.of(comedor));

		mockMvc.perform(get("/categoria/7/otro"))
				.andExpect(status().isNotFound());

		verify(avisoService, never()).getAvisosPorCategoria(anyLong());
	}

	@Test
	void categoriaInexistenteDevuelve404() throws Exception {
		when(categoriaService.getCategoriaPorId(999L)).thenReturn(Optional.empty());

		mockMvc.perform(get("/categoria/999/xxx"))
				.andExpect(status().isNotFound());

		verify(avisoService, never()).getAvisosPorCategoria(anyLong());
	}

	private Categoria categoria(Long id, String nombre) {
		Categoria categoria = new Categoria();
		categoria.setId(id);
		categoria.setNombre(nombre);
		return categoria;
	}

	private Aviso aviso(Long id, String sku, Categoria categoria) {
		return new Aviso(id, sku, "Producto " + sku, 26000L, "imagen.jpg", 10, categoria);
	}

}
